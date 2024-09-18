import javax.swing.DebugGraphics

import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.admin.portal.util.DataHelper
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.base.util.*

Set < String > fieldsToSelect = new TreeSet < String > ();
try {
	
	fieldsToSelect.add("parameterId");
	fieldsToSelect.add("description");
	fieldsToSelect.add("value");
	fieldsToSelect.add("storeId");
	fieldsToSelect.add("comments");

List < GenericValue > generalValues = EntityQuery.use(delegator).select(fieldsToSelect).from("PretailLoyaltyGlobalParameters").where("storeId","GENERAL_PARAM").orderBy("-lastUpdatedTxStamp").queryList();
Debug.logInfo("generalValues"+generalValues,"");
context.put("generalValues", generalValues);

List < GenericValue > workingHours = EntityQuery.use(delegator).select(fieldsToSelect).from("PretailLoyaltyGlobalParameters").where("storeId","WORKING_HOUR_PARAM").orderBy("-lastUpdatedTxStamp").queryList();
Debug.logInfo("workingHours"+workingHours,"");
context.put("workingHours", workingHours);

List < GenericValue > serviceReqs = EntityQuery.use(delegator).select(fieldsToSelect).from("PretailLoyaltyGlobalParameters").where("storeId","SERVICE_REQ_PARAM").orderBy("-lastUpdatedTxStamp").queryList();
Debug.logInfo("serviceReqs"+serviceReqs,"");
context.put("serviceReqs", serviceReqs);

List < GenericValue > dataMigs = EntityQuery.use(delegator).select(fieldsToSelect).from("PretailLoyaltyGlobalParameters").where("storeId","DMG_PARAM").orderBy("-lastUpdatedTxStamp").queryList();
Debug.logInfo("dataMigs"+dataMigs,"");
context.put("dataMigs", dataMigs);

}


catch(Exception e)
{
	e.printStackTrace();
}

List<List<GenericValue>> sectionSeperated =  new ArrayList<List<GenericValue>>();
List<GenericValue> sectionDetails = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", AdminPortalConstant.GlobalParameter.GLOBAL_PARAMS), UtilMisc.toList("enumSequenceId ASC"), false);
context.put("sectionId", DataHelper.getDropDownOptions(sectionDetails, "enumId", "description"));
context.put("sectionDetails", sectionDetails);

if(UtilValidate.isNotEmpty(sectionDetails))
	{
	
		for(GenericValue row:sectionDetails)
		{
			
			List < GenericValue> sectionDet = EntityQuery.use(delegator).select(fieldsToSelect).from("PretailLoyaltyGlobalParameters").where("storeId",row.getString("enumId")).orderBy("-lastUpdatedTxStamp").queryList();
			sectionSeperated.add( sectionDet);
		}
	}
	
	Debug.logInfo("sectionSeperated::"+sectionSeperated,"");
	context.put("sectionSeperated",sectionSeperated);
	
imageTypes = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "IMAGE_TYPE"),null,false);
context.put("imageType", DataHelper.getDropDownOptions(imageTypes, "enumCode", "description"));

