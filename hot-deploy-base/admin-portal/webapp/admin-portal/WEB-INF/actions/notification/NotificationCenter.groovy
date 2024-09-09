import org.fio.homeapps.util.DataUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil

Delegator delegator = request.getAttribute("delegator");

String userLoginId = request.getParameter("userLoginId");

List<Map<String, Object>> notificationSubscribeList = new LinkedList<Map<String, Object>>();

if(UtilValidate.isNotEmpty(userLoginId)) {
	String userLoginPartyId = DataUtil.getPartyIdByUserLoginId(delegator, userLoginId);
	
	EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
		EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS,"Y"),
		EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS,""),
		EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS,null)
	);
	
	List<GenericValue> notificationEventTypeList = EntityQuery.use(delegator).from("NotificationEventType").where(condition).queryList();
	if(UtilValidate.isNotEmpty(notificationEventTypeList)) {
		for(GenericValue notificationEventType : notificationEventTypeList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(notificationEventType));
			
			String customFieldId = notificationEventType.getString("customFieldId");
			
			String isSubscribed = "N";
			if(UtilValidate.isNotEmpty(userLoginPartyId)) {
				EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, customFieldId),
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginPartyId)
				);
				GenericValue customFieldValue = EntityQuery.use(delegator).from("CustomFieldValue").where(condition1).queryFirst();
				if(UtilValidate.isNotEmpty(customFieldValue))
					isSubscribed = "Y";
					
				context.put("userLoginPartyId", userLoginPartyId);
			}
			
			data.put("isSubscribed", isSubscribed);
			notificationSubscribeList.add(data);
		}
	}
}
println ("notificationSubscribeList----->"+notificationSubscribeList);
context.put("notificationSubscribeList", notificationSubscribeList);

