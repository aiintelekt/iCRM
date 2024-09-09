/**
 * 
 */
package org.groupfio.customfield.service.services;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.groupfio.customfield.service.CustomfieldServiceConstants.GroupType;
import org.groupfio.customfield.service.util.DataUtil;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Sharif
 *
 */
public class EconomicValueServices {

	private static final String MODULE = EconomicValueServices.class.getName();
	public static final String resource = "CustomFieldUiLabels";
	
    public static Map createEconomicValue(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	
    	String customFieldId = (String) context.get("customFieldId");
    	String customFieldName = (String) context.get("customFieldName");
    	String sequenceNumber = (String) context.get("sequenceNumber");
    	String customFieldType = (String) context.get("customFieldType");
    	String isEnabled = (String) context.get("isEnabled");
    	
    	String valueCapture = (String) context.get("valueCapture");
    	String valueMin = (String) context.get("valueMin");
    	String valueMax = (String) context.get("valueMax");
    	String valueData = (String) context.get("valueData");
    	
    	String productPromoCodeGroupId = (String) context.get("productPromoCodeGroupId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupId", groupId);
    	
    	try {
        	
    		Map<String, Object> conds = UtilMisc.toMap("customFieldName", customFieldName);
    		if (UtilValidate.isNotEmpty(groupId)) {
    			conds.put("groupId", groupId);
    		}
    		
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap(conds), null, false) );
    		
    		if (UtilValidate.isNotEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Economic Metric already exists!"));
    			return result;
    		}
    		
    		customField = delegator.makeValue("CustomField");
    		
    		//String customFieldId = delegator.getNextSeqId("CustomField");
    		
    		customField.put("customFieldId", customFieldId);
    		
    		customField.put("groupType", GroupType.SEGMENTATION);
    		
    		customField.put("isEnabled", UtilValidate.isNotEmpty(isEnabled) ? isEnabled : "Y");
    		customField.put("customFieldName", customFieldName);
    		customField.put("customFieldType", customFieldType);
    		customField.put("productPromoCodeGroupId", productPromoCodeGroupId);
    		
    		customField.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1));
    		
    		if (UtilValidate.isNotEmpty(groupId)) {
    			GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    			if (UtilValidate.isNotEmpty(group)) {
    				customField.put("groupName", group.getString("groupName"));
    				customField.put("groupId", groupId);
    				customField.put("groupType", group.getString("groupType"));
    				valueCapture = group.getString("valueCapture");
    			}
    		}
    		
    		customField.create();
    		
    		// store value configuration [start]
    		if ( UtilValidate.isNotEmpty(groupId) && (UtilValidate.isNotEmpty(valueCapture) && !valueCapture.equals("MULTIPLE")) ) {
    			
	    		String valueSeqNum = "1";
	    		Map<String, Object> valueConfigContext = new HashMap<String, Object>();
	    		
	    		valueConfigContext.put("groupId", groupId);
	    		valueConfigContext.put("customFieldId", customFieldId);
	    		valueConfigContext.put("valueCapture", valueCapture);
	    		valueConfigContext.put("valueSeqNum", valueSeqNum);
	    		valueConfigContext.put("valueMin", valueMin);
	    		valueConfigContext.put("valueMax", valueMax);
	    		valueConfigContext.put("valueData", valueData);
	    		valueConfigContext.put("userLogin", userLogin);
	    		
	    		Map<String, Object> valueConfigResult = dispatcher.runSync("segment.createValueConfig", valueConfigContext);
	    		
	    		if (ServiceUtil.isSuccess(valueConfigResult)) {
	    			Debug.log("Successfully created Economic Metric configuration : " + valueConfigContext);
	    		}
	    		
    		}
    		// store value configuration [end]
    		
    		result.put("customFieldId", customFieldId);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Economic Metric .."));
    	
    	return result;
    	
    }
    
    public static Map updateEconomicValue(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String customFieldId = (String) context.get("customFieldId");
    	
    	String groupId = (String) context.get("groupId");
    	
    	String customFieldName = (String) context.get("customFieldName");
    	String sequenceNumber = (String) context.get("sequenceNumber");
    	String customFieldType = (String) context.get("customFieldType");
    	String isEnabled = (String) context.get("isEnabled");
    	
    	String valueCapture = (String) context.get("valueCapture");
    	String valueMin = (String) context.get("valueMin");
    	String valueMax = (String) context.get("valueMax");
    	String valueData = (String) context.get("valueData");
    	
    	String productPromoCodeGroupId = (String) context.get("productPromoCodeGroupId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupId", groupId);
		result.put("customFieldId", customFieldId);
    	
    	try {
        	
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
    		
    		if (UtilValidate.isEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Economic Metric not exists!"));
    			return result;
    		}
    		
    		EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId),
					EntityCondition.makeCondition("customFieldName", EntityOperator.EQUALS, customFieldName),
					EntityCondition.makeCondition("customFieldId", EntityOperator.NOT_EQUAL, customFieldId)
					);
			
			List<GenericValue> customFields = delegator.findList("CustomField", conditions, null, null, null, false);
			if (UtilValidate.isNotEmpty(customFields)) {
				result.putAll(ServiceUtil.returnError("Economic Metric already exists!"));
    			return result;
			}
			
    		customField.put("isEnabled", UtilValidate.isNotEmpty(isEnabled) ? isEnabled : "Y");
    		customField.put("customFieldName", customFieldName);
    		customField.put("customFieldType", customFieldType);
    		customField.put("productPromoCodeGroupId", productPromoCodeGroupId);
    		
    		customField.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1));
    		
    		if (UtilValidate.isNotEmpty(groupId)) {
    			GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    			if (UtilValidate.isNotEmpty(group)) {
    				customField.put("groupName", group.getString("groupName"));
    				customField.put("groupId", groupId);
    				customField.put("groupType", group.getString("groupType"));
    				valueCapture = group.getString("valueCapture");
    			}
    		}
    		
    		customField.store();
    		
    		// store value configuration [start]
    		
    		if (UtilValidate.isNotEmpty(groupId)) {
    		
	    		String valueSeqNum = "1";
	    		Map<String, Object> valueConfigContext = new HashMap<String, Object>();
	    		
	    		valueConfigContext.put("groupId", groupId);
	    		valueConfigContext.put("customFieldId", customFieldId);
	    		valueConfigContext.put("valueCapture", valueCapture);
	    		valueConfigContext.put("valueSeqNum", valueSeqNum);
	    		valueConfigContext.put("valueMin", valueMin);
	    		valueConfigContext.put("valueMax", valueMax);
	    		valueConfigContext.put("valueData", valueData);
	    		valueConfigContext.put("userLogin", userLogin);
	    		
	    		GenericValue valueConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldValueConfig", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "valueCapture", valueCapture, "valueSeqNum", Long.parseLong(valueSeqNum)), null, false) );
	    		String valueConfigService = "segment.updateValueConfig";
	    		if (UtilValidate.isEmpty(valueConfig)) {
	    			valueConfigService = "segment.createValueConfig";
	    		}
	    		
	    		Map<String, Object> valueConfigResult = dispatcher.runSync(valueConfigService, valueConfigContext);
	    		
	    		if (ServiceUtil.isSuccess(valueConfigResult)) {
	    			Debug.log("Successfully "+valueConfigService+" Economic value configuration : " + valueConfigContext);
	    		}
    		
    		}
    		
    		// store value configuration [end]
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Economic Metric .."));
    	
    	return result;
    	
    }
    
    public static Map deleteEconomicValue(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String customFieldId = (String) context.get("customFieldId");
    	
    	String valueCapture = (String) context.get("valueCapture");
    	String valueSeqNum = (String) context.get("valueSeqNum");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
    		
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField",UtilMisc.toMap("customFieldId", customFieldId), null, false) );
    		if (UtilValidate.isEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Economic Metric not exists!"));
    			return result;
    		}
    		String groupId = customField.getString("groupId");
    		
    		if (UtilValidate.isNotEmpty(groupId)) {
    			GenericValue customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
        		valueCapture = customFieldGroup.getString("valueCapture");
        		valueSeqNum = "1";
        		
        		//delegator.removeAll( delegator.findByAnd("CustomFieldValue",UtilMisc.toMap("customFieldId", customFieldId), null, false) );
        		
    			Map<String, Object> valueConfigContext = new HashMap<String, Object>();
        		valueConfigContext.put("groupId", groupId);
        		valueConfigContext.put("customFieldId", customFieldId);
        		
        		valueConfigContext.put("valueCapture", valueCapture);
        		valueConfigContext.put("valueSeqNum", valueSeqNum);
        		
        		valueConfigContext.put("userLogin", userLogin);
        		
    			Map<String, Object> valueConfigResult = dispatcher.runSync("segment.deleteValueConfig", valueConfigContext);
        		if (ServiceUtil.isSuccess(valueConfigResult)) {
        			Debug.log("Successfully deleted Economic value configuration : " + valueConfigContext);
        		}
    		}
    		
    		delegator.removeAll( delegator.findByAnd("CustomFieldRoleConfig", UtilMisc.toMap("groupId", groupId), null, false) );
    		
    		String segmentationValueAssociatedEntityName = DataUtil.getSegmentationValueAssociatedEntityName(delegator, groupId);
    		delegator.removeAll( delegator.findByAnd(segmentationValueAssociatedEntityName, UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId), null, false) );
    		
    		customField.remove();
    		
    		result.put("groupId", groupId);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted Economic Metric.."));
    	
    	return result;
    	
    }
    
    public static String uploadEconomicMetricsService(HttpServletRequest request, HttpServletResponse response) throws ComponentException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        String filePath = ComponentConfig.getRootLocation("custom-field") + "webapp/importFiles/";
        File store = new File(filePath);
        String groupId = request.getParameter("groupId");
        String activeTab = request.getParameter("activeTab");
        Locale locale = UtilHttp.getLocale(request);
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileItemFactory.setRepository(store);
        String csvFile = "";
        CSVReader reader = null;
        int successCount = 0;
        int errorCount = 0;
        int customFieldCount = 1;
        int invalidCodeId = 0;
        int totalCount = 0;
        /*List < String > invalidPartyId = new LinkedList < String > ();
        List < String > invalidMetricId = new LinkedList < String > ();
        List < String > invalidMetricValue = new LinkedList < String > ();
        List < String > invalidEconomicCodeId = new LinkedList < String > ();
        List < String > partyIdNotFound = new LinkedList < String > ();
        List < String > economicCodeIdNotFound = new LinkedList < String > ();
        List < String > metricIdNotFound = new LinkedList < String > ();*/
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHMMSS");
        String requestId = dateFormat.format(new Date());
        try {
            String name = "";
            if (ServletFileUpload.isMultipartContent(request)) {
                @SuppressWarnings("unchecked")
                List < FileItem > multiparts = new ServletFileUpload(
                    new DiskFileItemFactory()).parseRequest(request);

                for (FileItem item: multiparts) {
                    if (!item.isFormField()) {
                        name = new File(item.getName()).getName();
                        item.write(new File(filePath + File.separator + name));
                    }
                    if (item.isFormField()) {
                        String fName = item.getFieldName();
                        String fValue = item.getString();
                        if ("groupId".equals(fName)) {
                            groupId = fValue;
                        } else if ("activeTab".equals(fName)) {
                            activeTab = fValue;
                        }
                    }
                }
                //File uploaded successfully

                if (UtilValidate.isNotEmpty(requestId) && UtilValidate.isNotEmpty(groupId)) {

                    TransactionUtil.begin();
                    GenericValue customFieldFileUpload = delegator.makeValue("CustomFieldFileUpload", UtilMisc.toMap("requestId", requestId,
                        "customFieldType", "ECONOMIC_METRIC", "customFieldGroupId", groupId, "status", "FILE_UPLOADED",
                        "originalFileName", name, "systemFileName", name, "folderLocation", filePath, "uploadedBy", userLogin.getString("partyId")));
                    customFieldFileUpload.create();
                    TransactionUtil.commit();

                    Debug.logInfo("==============" + "File Uploaded Successfully, Request Id==" + requestId, MODULE);
                    csvFile = filePath + name;
                    reader = new CSVReader(new FileReader(csvFile), ',', '"', 1);
                    List < String[] > records = reader.readAll();
                    String[] nextLine;
                    String importValidation = "^[a-zA-Z0-9_-]*$";
                    if (records != null && records.size() > 0) {
                        totalCount = records.size();
                        
                        TransactionUtil.begin();
                        GenericValue CFFUInProgress = EntityQuery.use(delegator).from("CustomFieldFileUpload").where("customFieldGroupId", groupId,
                            "requestId", requestId, "customFieldType", "ECONOMIC_METRIC").queryFirst();
                        if (CFFUInProgress != null && CFFUInProgress.size() > 0) {
                            CFFUInProgress.put("status", "IN_PROGRESS");
                            CFFUInProgress.put("noOfRecordsUploaded", Long.valueOf(totalCount));
                            CFFUInProgress.store();
                        }
                        TransactionUtil.commit();
                        
                        for (String[] record: records) {
                            if (record.length > 0) {
                                String partyId = record[0];
                                String codeId = record[1];
                                String customFieldId = record[2];
                                String value = record[3];
                                TransactionUtil.begin();
                                if (UtilValidate.isNotEmpty(codeId) && UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(customFieldId)) {
                                    codeId = codeId.trim();
                                    partyId = partyId.trim();
                                    customFieldId = customFieldId.trim();
                                    GenericValue customFieldGroup = EntityUtil.getFirst(delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", codeId, "groupType", "ECONOMIC_METRIC"), null, false));
                                    if (customFieldGroup == null || customFieldGroup.size() < 1) {
                                        /*invalidEconomicCodeId.add(codeId);
                                        invalidCodeId++;*/
                                        errorCount++;
                                        GenericValue CFSTCodeId = delegator.makeValue("CustomFieldSegmentImportTemp", UtilMisc.toMap("requestId", requestId,
                                            "customFieldType", "ECONOMIC_METRIC", "customFieldGroupId", codeId, "segmentValueId", customFieldId,
                                            "partyId", partyId, "metricValue", value, "status", "ERROR", "message", "Invalid Economic Code Id"));
                                        delegator.createOrStore(CFSTCodeId);
                                    } else {
                                        boolean invalidCustomFieldId = false;
                                        boolean invalidPropertyValue = false;
                                        List < EntityCondition > conditions = new ArrayList < EntityCondition > ();
                                        conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
                                            EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
                                            EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)));

                                        conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
                                            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                                            EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD"))));
                                        EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                                        List < GenericValue > partyToSummartyByRole = delegator.findList("PartyToSummaryByRole", mainConditons, null, null, null, false);
                                        if (partyToSummartyByRole != null && partyToSummartyByRole.size() > 0) {

                                            if (UtilValidate.isNotEmpty(value)) {
                                                String metricValueVal = value.replaceAll(",", "");
                                                if (UtilValidate.isNotEmpty(metricValueVal) && UtilValidate.isSignedDouble(metricValueVal)) {
                                                    value = metricValueVal;
                                                } else {
                                                    invalidPropertyValue = true;
                                                    /*invalidMetricValue.add(value);
                                                    invalidCodeId++;*/
                                                    errorCount++;
                                                    GenericValue CFSTMetricValue = delegator.makeValue("CustomFieldSegmentImportTemp", UtilMisc.toMap("requestId", requestId,
                                                        "customFieldType", "ECONOMIC_METRIC", "customFieldGroupId", codeId, "segmentValueId", customFieldId,
                                                        "partyId", partyId, "metricValue", value, "status", "ERROR", "message", "Metric Value format is invalid"));
                                                    delegator.createOrStore(CFSTMetricValue);
                                                }
                                            }
                                            if (!invalidPropertyValue) {
                                                GenericValue customField = delegator.findOne("CustomField", UtilMisc.toMap("customFieldId", customFieldId), false);
                                                if (customField == null || customField.size() < 1) {
                                                    if (customFieldId.matches(importValidation)) {
                                                        customField = delegator.makeValue("CustomField");
                                                        customField.put("customFieldId", customFieldId);
                                                        customField.put("groupId", codeId);
                                                        customField.put("groupType", "ECONOMIC_METRIC");
                                                        if (UtilValidate.isNotEmpty(customFieldGroup.getString("groupName"))) {
                                                            customField.put("groupName", customFieldGroup.getString("groupName"));
                                                        }
                                                        customField.put("customFieldName", customFieldId);
                                                        customField.put("sequenceNumber", new Long(customFieldCount));
                                                        customField.put("isEnabled", "Y");
                                                        customField.create();
                                                        customFieldCount++;
                                                    } else {
                                                        invalidCustomFieldId = true;
                                                        errorCount++;
                                                        /*invalidMetricId.add(customFieldId);
                                                        invalidCodeId++;*/
                                                        GenericValue CFSTMetricId = delegator.makeValue("CustomFieldSegmentImportTemp", UtilMisc.toMap("requestId", requestId,
                                                            "customFieldType", "ECONOMIC_METRIC", "customFieldGroupId", codeId, "segmentValueId", customFieldId,
                                                            "partyId", partyId, "metricValue", value, "status", "ERROR", "message", "Metric Id format is invalid"));
                                                        delegator.createOrStore(CFSTMetricId);
                                                    }
                                                } else if (!(codeId.equals(customField.getString("groupId")))) {
                                                    invalidCustomFieldId = true;
                                                    /*invalidMetricId.add(customFieldId);
                                                    invalidCodeId++;*/
                                                    errorCount++;
                                                    GenericValue CFSTMetricId = delegator.makeValue("CustomFieldSegmentImportTemp", UtilMisc.toMap("requestId", requestId,
                                                        "customFieldType", "ECONOMIC_METRIC", "customFieldGroupId", codeId, "segmentValueId", customFieldId,
                                                        "partyId", partyId, "metricValue", value, "status", "ERROR", "message", "Code Id and Metric Id not associated"));
                                                    delegator.createOrStore(CFSTMetricId);
                                                }
                                                Debug.log("Results===" + codeId.equals(customField.getString("groupId")));
                                                if (!invalidCustomFieldId) {
                                                    GenericValue associatedEntity = EntityUtil.getFirst(delegator.findByAnd("PartyMetricIndicator", UtilMisc.toMap("groupId", codeId, "customFieldId", customFieldId, "partyId", partyId), null, false));
                                                    if (associatedEntity == null || associatedEntity.size() < 1) {
                                                        associatedEntity = delegator.makeValue("PartyMetricIndicator");
                                                        associatedEntity.put("groupId", codeId);
                                                        associatedEntity.put("customFieldId", customFieldId);
                                                        associatedEntity.put("partyId", partyId);

                                                        if (GroupType.ECONOMIC_METRIC.equals(customFieldGroup.getString("groupType"))) {
                                                            associatedEntity.put("propertyName", codeId + "." + customFieldId);
                                                            if (UtilValidate.isNotEmpty(customFieldGroup.getString("groupingCode"))) {
                                                                GenericValue groupingCode = customFieldGroup.getRelatedOne("CustomFieldGroupingCode", false);
                                                                if (UtilValidate.isNotEmpty(groupingCode)) {
                                                                    associatedEntity.put("groupingCode", groupingCode.getString("groupingCode"));
                                                                }
                                                            }
                                                        }
                                                        if (customFieldGroup.getString("groupType").equals(GroupType.SEGMENTATION)) {
                                                            associatedEntity.put("inceptionDate", UtilDateTime.nowTimestamp());
                                                        }
                                                        associatedEntity.put("sequenceNumber", new Long(successCount + 1));
                                                        associatedEntity.put("propertyValue", value);
                                                        associatedEntity.create();
                                                    } else {
                                                        //alreadyExistsCount++;
                                                        associatedEntity.put("propertyValue", value);
                                                        associatedEntity.store();
                                                    }
                                                    successCount++;
                                                }
                                            }
                                        } else {
                                            /*invalidPartyId.add(partyId);
                                            invalidCodeId++;*/
                                            errorCount++;
                                            GenericValue CFSTPartyId = delegator.makeValue("CustomFieldSegmentImportTemp", UtilMisc.toMap("requestId", requestId,
                                                "customFieldType", "ECONOMIC_METRIC", "customFieldGroupId", codeId, "segmentValueId", customFieldId,
                                                "partyId", partyId, "metricValue", value, "status", "ERROR", "message", "Invalid Party Id"));
                                            delegator.createOrStore(CFSTPartyId);
                                        }
                                    }
                                } /*else {
                                    invalidCodeId++;
                                    if (UtilValidate.isEmpty(partyId)) {
                                        partyIdNotFound.add(String.valueOf(invalidCodeId + 1));
                                    }
                                    if (UtilValidate.isEmpty(codeId)) {
                                        economicCodeIdNotFound.add(String.valueOf(invalidCodeId + 1));
                                    }
                                    if (UtilValidate.isEmpty(customFieldId)) {
                                        metricIdNotFound.add(String.valueOf(invalidCodeId + 1));
                                    }
                                }*/
                            }
                            TransactionUtil.commit();
                        }
                    } else {
                        TransactionUtil.begin();
                        GenericValue CFFUEmptyFile = EntityQuery.use(delegator).from("CustomFieldFileUpload").where("customFieldGroupId", groupId,
                            "requestId", requestId, "customFieldType", "ECONOMIC_METRIC").queryFirst();
                        if (CFFUEmptyFile != null && CFFUEmptyFile.size() > 0) {
                            CFFUEmptyFile.put("status", "COMPLETED");
                            CFFUEmptyFile.put("noOfRecordsProcessed", Long.valueOf(successCount));
                            CFFUEmptyFile.put("message", "No data available in the file");
                            CFFUEmptyFile.store();
                        }
                        TransactionUtil.commit();
                        request.setAttribute("groupId", groupId);
                        request.setAttribute("activeTab", activeTab);
                        request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource, "emptyFile", locale));
                        return "error";
                    }
                }
            }
        } catch (Exception e1) {
            try {
                TransactionUtil.begin();
                GenericValue customFieldFileUploadException = EntityQuery.use(delegator).from("CustomFieldFileUpload").where("customFieldGroupId", groupId,
                    "requestId", requestId, "customFieldType", "ECONOMIC_METRIC").queryFirst();
                if (customFieldFileUploadException != null && customFieldFileUploadException.size() > 0) {
                    customFieldFileUploadException.put("status", "FAILED");
                    customFieldFileUploadException.put("noOfRecordsProcessed", Long.valueOf(successCount));
                    customFieldFileUploadException.put("message", "Exception in Upload Economic Metrics Service " + e1.getMessage());
                    customFieldFileUploadException.store();
                }
                TransactionUtil.commit();
            } catch (Exception e) {
                Debug.log("Exception in customFieldFileUploadException" + e.getMessage());
            }

            Debug.log("Exception in Upload Economic Metrics Service" + e1.getMessage());
            request.setAttribute("groupId", groupId);
            request.setAttribute("activeTab", activeTab);
            request.setAttribute("_ERROR_MESSAGE_", e1.getMessage());
            return "error";
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                Debug.log("Exception in csv reader and bufferreader closing in Upload Economic Metrics Service" + e.getMessage());
            }
        }

        String msg = "Results: Total Count: " + totalCount + ", Success Count: " + successCount +", Error Count: " + errorCount;
        /*if (invalidPartyId != null && invalidPartyId.size() > 0) {
            msg = msg + ", Invalid Party Id: " + invalidPartyId;
        }
        if (invalidEconomicCodeId != null && invalidEconomicCodeId.size() > 0) {
            msg = msg + ", Invalid Economic Code Id: " + invalidEconomicCodeId;
        }
        if (partyIdNotFound != null && partyIdNotFound.size() > 0) {
            msg = msg + ", Party Id not found in the row no: " + partyIdNotFound;
        }
        if (economicCodeIdNotFound != null && economicCodeIdNotFound.size() > 0) {
            msg = msg + ", Economic Code Id not found in the row no: " + economicCodeIdNotFound;
        }
        if (metricIdNotFound != null && metricIdNotFound.size() > 0) {
            msg = msg + ", Metric Id not found in the row no: " + metricIdNotFound;
        }
        if (invalidMetricId != null && invalidMetricId.size() > 0) {
            msg = msg + ", Metric Value format is invalid " + invalidMetricId;
        }
        if (invalidMetricValue != null && invalidMetricValue.size() > 0) {
            msg = msg + ", Metric Value format is invalid " + invalidMetricValue;
        }*/
        try {
            TransactionUtil.begin();
            GenericValue customFieldFileUploadSuccess = EntityQuery.use(delegator).from("CustomFieldFileUpload").where("customFieldGroupId", groupId,
                "requestId", requestId, "customFieldType", "ECONOMIC_METRIC").queryFirst();
            if (customFieldFileUploadSuccess != null && customFieldFileUploadSuccess.size() > 0) {
                customFieldFileUploadSuccess.put("status", "COMPLETED");
                customFieldFileUploadSuccess.put("noOfRecordsProcessed", Long.valueOf(successCount));
                customFieldFileUploadSuccess.put("message", msg);
                customFieldFileUploadSuccess.store();
            }
            TransactionUtil.commit();
        } catch (Exception e) {
            Debug.log("Exception in customFieldFileUploadSuccess" + e.getMessage());
        }
        String returnMsg = "Customers imported into the Economic Metric Sucessfully";
        if (successCount < 1) {
            returnMsg = "Economic Metric data import failed due to invalid data's";
        }
        request.setAttribute("groupId", groupId);
        request.setAttribute("activeTab", activeTab);
        request.setAttribute("_EVENT_MESSAGE_", returnMsg);
        return "success";
    }
}
