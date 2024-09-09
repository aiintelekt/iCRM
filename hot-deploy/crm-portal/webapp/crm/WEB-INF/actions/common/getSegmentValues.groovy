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
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.fio.crm.util.DataHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.ofbiz.base.conversion.JSONConverters.ListToJSON;
import org.ofbiz.base.lang.JSON;

partyId = parameters.get("partyId");

currentRole = request.getRequestURI();

if (currentRole.contains("viewLead")) {
	roleType="LEAD";
} else if (currentRole.contains("viewAccount")) {
	roleType="ACCOUNT";
} else {
	roleType="CONTACT";
}

context.put("roleTypeId", roleType);

expirationCond = EntityCondition.makeCondition(UtilMisc.toList(
EntityCondition.makeCondition("roleTypeId", roleType),
EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, null)

));

fieldRoleConfig = delegator.findList("CustomFieldRoleConfig", expirationCond, null, null, null, false);
List customFieldListsToView=new ArrayList();
List groupNameLists=new ArrayList();
segmentCode = new LinkedHashMap();

if( fieldRoleConfig != null && fieldRoleConfig.size() > 0 ){
	for(GenericValue fieldRoleConfigLists : fieldRoleConfig){
		groupId = fieldRoleConfigLists.getString("groupId");
		if(UtilValidate.isNotEmpty(groupId)){
			customField = from("CustomFieldGroup").where("groupId" , groupId, "groupType","SEGMENTATION").orderBy("sequence").queryList();
			
			if( customField !=null && customField.size()>0 ){
				for(GenericValue cf : customField){
					groupName = cf.getString("groupName");
					groupId = cf.getString("groupId");
					groupNameLists.add(groupId);
					segmentCode.put(groupId, groupName);
				}
			}
		}	
		
	}
}

context.put("segmentCodeList", segmentCode);
context.put("partyId", partyId);
/*		
partyClassMap = new LinkedHashMap();
List partyClassList=new ArrayList();
partyClassificationList = from('CustomFieldPartyClassification').where('partyId', partyId).queryList()
*/
//partyClassificationList = delegator.findAll("CustomFieldPartyClassification", false);
/*if( partyClassificationList != null && partyClassificationList.size()>0 ){
	for(GenericValue partyClassification : partyClassificationList){
	partyClassGroupId = partyClassification.getString("groupId");	
	partyClassCustomFieldId = partyClassification.getString("customFieldId");
	partyClassDate = partyClassification.getString("inceptionDate");
    partyClassgroupName = select("groupName").from('CustomFieldGroup').where('groupId', partyClassGroupId).queryOne()
    partycustomFieldName = select("customFieldName").from('CustomField').where('customFieldId', partyClassCustomFieldId).queryOne()
	
partyClassMap.put("segmentGroup",partyClassgroupName);
partyClassMap.put("segmentValue",partycustomFieldName);
partyClassMap.put("campaignName",campaignName);
partyClassMap.put("startDate",partyClassDate);
partyClassList.add(partyClassMap);
	}
}*/

condition = UtilMisc.toMap("groupType", "SEGMENTATION"); 
cond = EntityCondition.makeCondition(condition);
groupingCodeList = delegator.findList("CustomFieldGroupingCode", cond, null, ["sequenceNumber"], null, false);
context.put("groupingCodeList", DataHelper.getDropDownOptions(groupingCodeList, "groupingCode", "groupingCode"));	

groupingCode = request.getParameter("segment_groupingCode");
segmentCodeId = request.getParameter("segment_segmentCodeId");
	
segmentCode = new HashMap();

segmentCode.put("segment_groupingCode", groupingCode);
segmentCode.put("segment_segmentCodeId", segmentCodeId);
println("segmentCode> "+segmentCode);
context.put("segmentSegmentCode", segmentCode);

groupingCode = request.getParameter("filter_groupingCode");
segmentCodeId = request.getParameter("filter_segmentCodeId");
	
segmentCode = new HashMap();

segmentCode.put("filter_groupingCode", groupingCode);
segmentCode.put("filter_segmentCodeId", segmentCodeId);

context.put("filterSegmentCode", segmentCode);

condition = UtilMisc.toMap("partyId", partyId);
/*
if (UtilValidate.isNotEmpty(groupingCode)) {
	condition.put("groupingCode", groupingCode);
}
*/
if (UtilValidate.isNotEmpty(segmentCodeId)) {
	condition.put("groupId", segmentCodeId);
}

cond = EntityCondition.makeCondition(condition);
partyClassificationList = delegator.findList("CustomFieldPartyClassification", cond, null, ["-inceptionDate"], null, false);	
JSONArray results = new JSONArray();
partyClassificationList.each{pc ->

	JSONObject result = new JSONObject();
	result.putAll(pc);
	
	group = pc.getRelatedOne("CustomFieldGroup", false);
	if (UtilValidate.isNotEmpty(group)) {
		result.put("groupName", group.getString("groupName"));
		
		code = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroupingCode",UtilMisc.toMap("customFieldGroupingCodeId", group.groupingCode), null, false) );
		if (UtilValidate.isNotEmpty(code)) {
			result.put("groupingCodeName", code.getString("groupingCode"));
		} 
	}
	
	if (UtilValidate.isNotEmpty(groupingCode) && UtilValidate.isNotEmpty(code) 
		&& !code.getString("groupingCode").equals(groupingCode)
		) {
		return;
	}
	
	field = pc.getRelatedOne("CustomField", false);
	if (UtilValidate.isNotEmpty(field)) {
		result.put("customFieldName", field.getString("customFieldName"));
	}
	
	results.add(result);
}
	
context.put("partyClassificationList", results);
ListToJSON listToJSON = new ListToJSON();
JSON json = listToJSON.convert(results);
context.put("partyClassificationListStr", json.toString());