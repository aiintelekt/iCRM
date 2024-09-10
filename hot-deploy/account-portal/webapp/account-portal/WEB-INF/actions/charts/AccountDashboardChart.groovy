import org.ofbiz.base.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.ResultSet;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;

delegator = request.getAttribute("delegator");

String role = request.getParameter("roleTypeId");
String segId = request.getParameter("segmentCode");
segmentationValues = delegator.findList("IcrmSegmentationStats", EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT"), null, ["-timeComputed"], null, false);

Set segmentCodesSet = new HashSet(EntityUtil.getFieldListFromEntityList(segmentationValues,"segmentCode", false));
List segmentCodeIds = new ArrayList(segmentCodesSet);
segmentDropDownList=[];
for(int i=0;i<segmentCodeIds.size();i++){
	Map tempMap= new HashMap();
	tempMap.put("code",segmentCodeIds.get(i));
	segmentDesc = delegator.findOne("CustomFieldGroup", UtilMisc.toMap("groupId",segmentCodeIds.get(i)), false);
    if(UtilValidate.isNotEmpty(segmentDesc)){
    	tempMap.put("value",segmentDesc.get("groupName"));
    }else{
    	tempMap.put("value",segmentCodeIds.get(i));
    }
	segmentDropDownList.add(tempMap);
}
context.put("segmentDropDownList",segmentDropDownList);
JSONArray pieChartData = new JSONArray();
JSONArray namesList = new JSONArray();
segmentCode=segId;
if(UtilValidate.isNotEmpty(segmentCode)){
	segmentationValues = delegator.findList("IcrmSegmentationStats", EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, role), null, ["-timeComputed"], null, false);
	List segmentsCodeWiseList = EntityUtil.filterByCondition(segmentationValues, EntityCondition.makeCondition("segmentCode",EntityOperator.EQUALS,segmentCode));
	duplicates=[];
	for(segGenericVal in segmentsCodeWiseList ){
		if(!duplicates.contains(segGenericVal.getString("segmentValue"))){
			if(UtilValidate.isNotEmpty(segGenericVal.get("segmentValueTotalRows"))){
				JSONObject segJsonVal=new JSONObject();
				segJsonVal.put("value",segGenericVal.get("segmentValueTotalRows"));
				customFieldName = delegator.findOne("CustomField", UtilMisc.toMap("customFieldId",segGenericVal.getString("segmentValue")), false);
				 name=segGenericVal.getString("segmentValue");
				if(UtilValidate.isNotEmpty(customFieldName)){
					name= customFieldName.get("customFieldName");
			    }
				if(name.length()>61){					
					name=name.substring(0,61);
				}
				segJsonVal.put("name",name);
			    namesList.add(name);		
				pieChartData.add(segJsonVal);
				duplicates.add(segGenericVal.getString("segmentValue"));
			}
		}
	}
	
}

context.put("pieChartData", pieChartData.toString());
request.setAttribute("pieChartVal", pieChartData.toString());
request.setAttribute("namesList",namesList.toString());
request.setAttribute("namesSize",namesList.size());
context.put("namesList",namesList.toString());
return "success";