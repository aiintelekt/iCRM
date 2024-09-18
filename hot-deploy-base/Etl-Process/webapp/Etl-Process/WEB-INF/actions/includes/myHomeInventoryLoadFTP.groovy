/**
 * @author Group Fio
 * @since Apr 28, 2011
 *
 */
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.opentaps.base.constants.StatusItemConstants;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.opentaps.data.migration.constants.DMConstants.DataimportStatus;

invProcessed = 0;
invNotProcessed = 0;

/*
GET PROCESSED
*/
/*
searchConditions = FastList.newInstance();
searchConditions.add(new EntityExpr("importStatus", EntityOperator.EQUALS,"SUCCESS"));
allConditions = new EntityConditionList(searchConditions, EntityOperator.OR);
invProcessed = delegator.findCountByCondition("DmgInventoryLoad", allConditions, null);*/


allConditions = new EntityExpr("importStatus", EntityOperator.EQUALS,"SUCCESS");
invProcessed1 = delegator.findByCondition("DmgInventoryLoad", allConditions, null, null);
invProcessed=invProcessed1.size();



/*
GET NOT-PROCESSED
*/

EntityCondition statusCond = EntityCondition.makeCondition(EntityOperator.OR,
				   EntityCondition.makeCondition("importStatus", EntityOperator.EQUALS, "ERROR"),
				   EntityCondition.makeCondition("importStatus", EntityOperator.EQUALS, "SKIPPED"),
				   EntityCondition.makeCondition("importStatus", EntityOperator.EQUALS, null));



invNotProcessed = delegator.findCountByCondition("DmgInventoryLoad", statusCond, null);
AllRecords1= delegator.findAll("DmgFacilityLocation");
AllRecordsSize=AllRecords1.size();
condition1 = new EntityExpr("isProcessed", EntityOperator.EQUALS,"Y");
	ConditionRecordsSize = delegator.findCountByCondition("DmgFacilityLocation", condition1, null, null);
	
	
	AllRecords2= delegator.findAll("DmgProductFacilityLocation");
	AllRecordsSizePFL=AllRecords2.size();
	
	condition2= new EntityExpr("isProcessed", EntityOperator.EQUALS,"Y");
		ConditionRecordsSizePFL = delegator.findCountByCondition("DmgProductFacilityLocation", condition2, null, null);
		
		
		context.put("invProcessed", invProcessed);
		context.put("invNotProcessed", invNotProcessed);
		
		context.put("ConditionRecordsSizePFL", ConditionRecordsSizePFL);
		context.put("AllRecordsSizePFL", AllRecordsSizePFL);
		
	context.put("ConditionRecordsSize", ConditionRecordsSize);
	context.put("AllRecordsSize", AllRecordsSize);





orderFulProcessed = 0;
orderFulNotProcessed = 0;

/*
GET PROCESSED
*/
searchConditions = FastList.newInstance();
searchConditions.add(new EntityExpr("importStatusId", EntityOperator.EQUALS, "SUCCESS"));
searchConditions.add(new EntityExpr("isprocessed", EntityOperator.EQUALS, "Y"));
searchConditions.add(new EntityExpr("accessType", EntityOperator.EQUALS, "FTP"));
allConditions = new EntityConditionList(searchConditions, EntityOperator.AND);

orderFulProcessed = delegator.findCountByCondition("EtlOrderFulfillment", allConditions, null);

/*
GET NOT-PROCESSED
*/

searchConditions1 = FastList.newInstance();
searchConditions1.add(new EntityExpr("isprocessed", EntityOperator.EQUALS, "N"));
searchConditions1.add(new EntityExpr("accessType", EntityOperator.EQUALS, "FTP"));
allConditions1 = new EntityConditionList(searchConditions1, EntityOperator.AND);
orderFulNotProcessed =delegator.findCountByCondition("EtlOrderFulfillment", allConditions1, null);

context.put("orderCompleteProcessed", orderFulProcessed);
context.put("orderNotCompleteProcessed", orderFulNotProcessed);



