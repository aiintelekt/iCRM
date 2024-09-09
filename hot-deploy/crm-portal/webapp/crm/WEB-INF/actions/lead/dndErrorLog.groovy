import java.text.SimpleDateFormat

import org.fio.campaign.util.ExportFileUtil
import org.fio.crm.contactmech.PartyPrimaryContactMechWorker
import org.ofbiz.base.component.ComponentConfig
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityFindOptions
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil

import groovy.json.StringEscapeUtils
import javolution.util.FastList
import javolution.util.FastMap;

importId = parameters.get("importId");
SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
Date now = new Date();
try {
	if(UtilValidate.isNotEmpty(importId)){
		List contactList = FastList.newInstance();
		EntityFindOptions efo = new EntityFindOptions();
		efo.setDistinct(true);
		efo.setLimit(1000);
		contactName = "";
		//GenericValue contactListName = select("contactListName") .from("ContactList").where("contactListId", contactListId).queryOne();
		//if (contactListName != null && contactListName.size() > 0) {
		//	contactName = contactListName.getString("contactListName");
		//}
		
		List <GenericValue> dndErrorList = delegator.findList("DbsDndErrorLog", EntityCondition.makeCondition("importId", EntityOperator.EQUALS,importId), null, null, null, false);
		for(GenericValue dndError: dndErrorList) {
			errorId = dndError.getString("errorId");
			dndNumber = dndError.getString("dndNumber");
			dndIndicator = dndError.getString("dndIndicator");
			//if('E140'.equals(checkErrorLog)){
					errorDescription = "";
					Map <String, Object> dataMap = new LinkedHashMap();
					errorDescriptionList = select('codeDescription') .from("ErrorCode").where("errorCodeId", errorId).queryOne();
					if (errorDescriptionList != null && errorDescriptionList.size() > 0) {
						errorDescription = errorDescriptionList.getString("codeDescription");
					}
					dataMap.put("importId", importId);
					dataMap.put("errorDescription", errorDescription);
					dataMap.put("dndNumber", dndNumber);
					dataMap.put("dndIndicator", dndIndicator);
					//dataMap.put("errorDescription", errorDescription);
					contactList.add(dataMap);
				}
			//}	
		//}
		// contruct the file
		if(contactList != null && contactList.size() > 0) {
			String fileName = "DNDErrorLog"+importId+".csv";
			String location = ComponentConfig.getRootLocation("campaign")+"/webapp/campaign-resource/files";
			String delimiter = ",";
			List<String> headers = new ArrayList<String>();
			headers.add("Process Id");
			headers.add("Log Id");
			headers.add("DND Number");	
			headers.add("DND Indicator");
			ExportFileUtil.constructFileFromList(delegator, contactList, headers, fileName, location, delimiter, true);
			Thread.sleep(1000);
			ExportFileUtil.downloadFile(request,response,location+File.separatorChar+fileName);
			boolean isdelete = false;
			if(isdelete) {
				File file = new File(location+File.separatorChar+fileName);
				file.delete();
			}
		}
	}
	return "success";
} catch(Exception e) {
	return "error";
}


