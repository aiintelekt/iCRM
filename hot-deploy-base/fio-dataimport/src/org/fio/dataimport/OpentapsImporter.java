/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fio.dataimport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

/**
 * Encapsulates the process of importing data from flat entities into the opentaps model.  Each row of data is imported
 * in its own transaction, allowing for later analysis and loading of failed imports.  The row is transformed into
 * a set of opentaps entities using an ImportDecoder.
 *
 * The flat entities must define the following fields,
 *
 * <ul>
 * <li>importStatusId: Not Processed (DATAIMP_NOT_PROC or null), Imported (DATAIMP_IMPORTED), Failed (DATAIMP_FAILED)</li>
 * <li>processedTimestamp:  date-time field to store when it was processed.</li>
 * <li>importError: very-long field to store exception trace for failed imports.</li>
 * </ul>
 *
 * TODO: configure a timeout for each row
 */
public class OpentapsImporter {

    private static String MODULE = OpentapsImporter.class.getName();

    protected String entityName = null;
    protected ImportDecoder decoder = null;
    protected Delegator delegator = null;
    protected LocalDispatcher dispatcher = null;
    protected EntityCondition conditions = null;
    protected Object[] args;
    protected List<String> orderBy = null;
    protected int maxToImport = -1;
    
    public List<GenericValue> importedDataList = new ArrayList<GenericValue>();

    // a positive failure threshold means we're counting, otherwise we're not
    protected int failureThreshold = 0;

    /** Frequently used EntityFindOption for distinct read-only select. */
    public static final EntityFindOptions DISTINCT_READ_OPTIONS = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
    /**
     * Create a basic importer for the given flat entity that will only check the processedTimestamp field
     * to see if an entry qualifies for importing.
     */
    public OpentapsImporter(String entityName, DispatchContext dctx, ImportDecoder decoder, Object... args) {
        this(entityName, dctx, decoder, null, args);
    }

    /**
     * Create an importer for the given flat entity that uses the given EntityCondition as well as the standard
     * processedTimestamp condition to check for entities to process.  Note that you do not have to specify
     * the condition for processedTimestamp, because this class handles that condition.
     */
    public OpentapsImporter(String entityName, DispatchContext dctx, ImportDecoder decoder, EntityCondition conditions, Object... args) {
        this.entityName = entityName;
        this.decoder = decoder;
        this.delegator = dctx.getDelegator();
        this.dispatcher = dctx.getDispatcher();
        this.conditions = conditions;
        this.args = args;
    }

    /** Set a number of consecutive failures before the import should be stopped.  Useful in development and trial runs. */
    public void setMaxConsecutiveFailures(Integer threshold) {
        this.failureThreshold = threshold == null ? 0 : threshold;
    }

    /**
     * Ignore consecutive failures.  Note that by default, the importer ignores consecutive failures,
     * so this method only makes sense when using setMaxConsecutiveFailures().
     */
    public void setIgnoreConsecutiveFailures() {
        this.failureThreshold = 0;
    }

    /** Specify the ordering of the import records.  Used in cases where there are interdependencies in the data that make the import sensitive to order. */
    public void setOrderBy(List<String> orderBy) {
        this.orderBy = orderBy;
    }

    /** As above, but specify only one field ordering for convenience. */
    public void setOrderBy(String orderBy) {
        this.orderBy = UtilMisc.toList(orderBy);
    }

    /** Set a maximum number of imports to process.  Useful for development or trial runs on large imports. */
    public void setMaxToImport(int max) {
        this.maxToImport = max;
    }

    /**
     * Remove the max number of imports constraint.  All records will be imported.  Note that by default,
     * the importer will attempt to import everything, so this method only makes sense when using setMaxToImport().
     */
    public void unsetMaxToImport() {
        this.maxToImport = -1;
    }

    /**
     * Configure the importer using parameters maxToImport, maxConsecutiveFailures.  Thier values should be Numbers.
     */
    public void configure(Map<String, ?> context) {
        Object maxToImport = context.get("maxToImport");
        Object maxConsecutiveFailures = context.get("maxConsecutiveFailures");
        if (maxToImport != null && maxToImport instanceof Number) {
            setMaxToImport(((Number) maxToImport).intValue());
        }
        if (maxConsecutiveFailures != null && maxConsecutiveFailures instanceof Number) {
            setMaxConsecutiveFailures(((Number) maxConsecutiveFailures).intValue());
        }
    }

    /**
     * Runs the import process for each unprocessed or failed entry.
     *
     * If there is a problem with the transaction or iterator, this method will throw a GenericEntityException.
     * Otherwise, the imports are wrapped in their own individual transactions and any exceptions that occur during
     * the actual import are caught and logged, then the import process continues to the next entry.
     */
    public int runImport(Map<String, ?> context) throws GenericEntityException {
    	
    	List<GenericValue> importDatas = (List) context.get("importDatas");

    	Boolean isTrackImportStatus = (Boolean) context.get("isTrackImportStatus");
    	
    	if (importDatas == null) {
    		EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.OR,
                    EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_PROC"),
                    EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED"),
                    EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_APPROVED"),
                    //EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_ERROR"),
                    EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null)
                    );
           
            EntityCondition statusCond =  null;
            if(UtilValidate.isNotEmpty(context.get("batchId"))){
            	String batchId = (String) context.get("batchId");
            	statusCond = EntityCondition.makeCondition(EntityOperator.AND,
                         EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId),
                         conditions );
            	
            }
            
            TransactionUtil.begin();
            
            if(UtilValidate.isNotEmpty(context.get("batchId")))
            	importDatas = delegator.findList(entityName, statusCond, null,null,null,false);
            else
            	importDatas = delegator.findList(entityName, conditions, null,null,null,false);
            TransactionUtil.commit();
    	}
    	
        Timestamp now = UtilDateTime.nowTimestamp();
        int imported = 0;
        int consecutiveFailures = 0;
        Debug.logInfo("=== Running Import of " + entityName + " ===", MODULE);
        if (maxToImport > 0) {
            Debug.logInfo("Importing a maximum of " + maxToImport + " records.", MODULE);
        }

        GenericValue flatEntity = null;
       // while ((flatEntity = iterator.next()) != null) {
        for (int count = 0; count < importDatas.size(); count++) {
        	flatEntity = (GenericValue) importDatas.get(count);
            try {
            	
                // begin the transaction for this row
                TransactionUtil.begin();

                // decode the flat entity into a set of normalized opentaps entities
                List<GenericValue> toStore = decoder.decode(flatEntity, now, delegator, dispatcher, args);
                if (toStore == null || toStore.size() == 0) {
                    continue;
                }

                // store the entities in a transaction
                delegator.storeAll(toStore);

                if (UtilValidate.isEmpty(isTrackImportStatus) || isTrackImportStatus) {
                	// also mark the flat entity as processed
                    flatEntity.set("importStatusId", "DATAIMP_IMPORTED");
                    flatEntity.set("processedTimestamp", UtilDateTime.nowTimestamp());
                    flatEntity.set("importError", null); // clear this out in case it had an exception originally
                }
                
                flatEntity.store();

                // we're done, so commit
                TransactionUtil.commit();

                Debug.logInfo("Successfully imported " + entityName + " " + makePkString(flatEntity), MODULE);
                imported += 1;
                consecutiveFailures = 0;
                importedDataList.add(flatEntity);
            } catch (Exception e) {
                String message = "Failed to import " + entityName + " " + makePkString(flatEntity) + ": " + e.getMessage();
                Debug.logError(e, message, MODULE);

                // roll back the decoding and the storing (if this fails then the import ends)
                TransactionUtil.rollback();

                // store the exception and mark as failed (if this errors then the whole thing crashes, which it should anyway since there's a big problem)
                flatEntity.set("importStatusId","DATAIMP_FAILED");
                flatEntity.set("processedTimestamp", UtilDateTime.nowTimestamp());
                flatEntity.set("importError", message);
                flatEntity.store();

                consecutiveFailures += 1;
            }

            if (failureThreshold > 0 && consecutiveFailures >= failureThreshold) {
                Debug.logInfo("Aborting Import:  " + consecutiveFailures + " consecutive import failures occured.", MODULE);
                break;
            }

            if (maxToImport > 0 && imported == maxToImport) {
                Debug.logInfo("Stopping import: " + imported + " records have been imported as specified.", MODULE);
                break;
            }
        }
        //iterator.close();

        Debug.logInfo("Imported " + imported + " Entries", MODULE);
        Debug.logInfo("=== Finished Import " + entityName + " ===", MODULE);

        return imported;
    }

    // TODO: this should really be in a util class, it came from our test framework
    public String makePkString(GenericValue value) {
    	StringBuilder buff = new StringBuilder("[");
        ModelEntity model = value.getModelEntity();
        for (Iterator<ModelField> iter = model.getPksIterator(); iter.hasNext();) {
            ModelField field = iter.next();
            buff.append(value.get(field.getName()));
            if (iter.hasNext()) {
                buff.append(", ");
            }
        }
        buff.append("]");
        return buff.toString();
    }
    
    
}
