import java.text.SimpleDateFormat
import java.util.function.Function
import java.util.stream.Collectors

import org.fio.admin.portal.constant.AdminPortalConstant.BusinessUnitConstant
import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant
import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil
import org.fio.admin.portal.util.DataHelper;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;



List<GenericValue> getUserNames = delegator.findByAnd("UserLoginPerson",null,null,false);
//println('==getUserNames==='+getUserNames);
context.put("userNamesList", getUserNames);

searchCriteria = request.getParameter("searchCriteria");
println('==searchCriteria==='+searchCriteria);
if(UtilValidate.isEmpty(searchCriteria)) {
	List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
	Set < String > fieldToSelect = new TreeSet < String > ();
	fieldToSelect.add("partyId");
	fieldToSelect.add("userLoginId");
	fieldToSelect.add("enabled");
	fieldToSelect.add("firstName");
	fieldToSelect.add("lastName");
	fieldToSelect.add("businessUnit");
	List < GenericValue > userList = EntityQuery.use(delegator).select(fieldToSelect).from("UserLoginPerson").queryList();
	for (GenericValue user: userList) {
		Map<String, Object> data = new HashMap<String, Object>();
		String userPartyId = user.getString("partyId");
		String enabled = user.getString("enabled");
		String firstName = user.getString("firstName");
		String lastName = user.getString("lastName");
		String userName = firstName + (UtilValidate.isNotEmpty(lastName) ? " " + lastName : "");
		data.put("partyId", userPartyId);
		data.put("userName", userName);
		data.put("oneBankId", UtilValidate.isNotEmpty(user.getString("userLoginId")) ? user.getString("userLoginId") : "");
		data.put("businessUnit", UtilValidate.isNotEmpty(user.getString("businessUnit")) ? user.getString("businessUnit") : "");
		data.put("userStatus", enabled == "N" ? "Inactive" : "Active");
		results.add(data);
		}
}
else {
	context.put("searchCriteria", searchCriteria);
}

roleTypes = from("RoleType").queryList();
context.put("roleTypes", org.fio.homeapps.util.DataHelper.getDropDownOptions(roleTypes, "roleTypeId", "description"));



List < EntityCondition > conditions = new ArrayList<EntityCondition>();

List<String> skipRoleTypeIds = new ArrayList<>();
String skipRoleTypeId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SKIP_ROLE_TYPE_ID");
if(UtilValidate.isNotEmpty(skipRoleTypeId) && skipRoleTypeId.contains(",")) {
	skipRoleTypeIds = org.fio.admin.portal.util.DataUtil.stringToList(skipRoleTypeId, ",");
} else if(UtilValidate.isNotEmpty(skipRoleTypeId)) {
	skipRoleTypeIds.add(skipRoleTypeId);
}
if(UtilValidate.isNotEmpty(skipRoleTypeIds)) {
	conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, null),
		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, ""),
		EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_IN, skipRoleTypeIds)
		));
}

EntityCondition condition1 = EntityCondition.makeCondition(conditions, EntityOperator.AND);
Set < String > fieldToSelect1 = new TreeSet < String > ();
fieldToSelect1.add("partyId");
fieldToSelect1.add("userLoginId");
fieldToSelect1.add("enabled");
fieldToSelect1.add("firstName");
fieldToSelect1.add("lastName");
List < GenericValue > userList = EntityQuery.use(delegator).select(fieldToSelect1).from("UserLoginPerson").where(condition1).queryList();
if(UtilValidate.isNotEmpty(userList)) {
	context.put("userNameList", DataHelper.getDropDownOptionsFromMultiDesField(userList, "userLoginId", UtilMisc.toList("firstName","lastName")));
}
