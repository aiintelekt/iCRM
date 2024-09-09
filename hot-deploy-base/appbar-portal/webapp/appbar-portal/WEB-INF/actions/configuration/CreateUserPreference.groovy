import org.fio.appbar.portal.util.DataUtil
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;


String userLoginId = userLogin.getString("userLoginId");
String appBarId = request.getParameter("appBarId");
String appBarTypeId = request.getParameter("appBarTypeId");

if(UtilValidate.isNotEmpty(appBarId) && UtilValidate.isNotEmpty(appBarTypeId)) {
	context.put("appBarId",appBarId);
	context.put("appBarTypeId",appBarTypeId);
	List<EntityCondition> commonConditions = new ArrayList<EntityCondition>();
	
	commonConditions.add(EntityCondition.makeCondition("appBarId",EntityOperator.EQUALS,appBarId));
	commonConditions.add(EntityCondition.makeCondition("appBarTypeId",EntityOperator.EQUALS,appBarTypeId));
	
	List<EntityCondition> appBarElementCond = new ArrayList<EntityCondition>();
	
	appBarElementCond.addAll(commonConditions);
	
	appBarElementCond.add(EntityCondition.makeCondition(EntityOperator.OR,
			EntityCondition.makeCondition("appBarElementActive",EntityOperator.EQUALS,null),
			EntityCondition.makeCondition("appBarElementActive",EntityOperator.EQUALS,"Y")
			));
	
	List<GenericValue> appBarElementList = EntityQuery.use(delegator)
												.select("appBarId","appBarTypeId","appBarElementId","appBarElementSeqNum","appBarElementName","appBarElementUilabel","appBarElementPosition","lastUpdatedTxStamp")
												.from("AppBarElements")
												.where(EntityCondition.makeCondition(appBarElementCond,EntityOperator.AND)).orderBy("appBarElementSeqNum ASC").distinct().queryList();	
	if(UtilValidate.isNotEmpty(appBarElementList)) {
		List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
		List<EntityCondition> userPrefAppBarCond = new ArrayList<EntityCondition>();
		userPrefAppBarCond.addAll(commonConditions);
		userPrefAppBarCond.add(EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId));
		List<GenericValue> appBarUserPreferenceList = EntityQuery.use(delegator).from("AppBarUserPreference").
															where(EntityCondition.makeCondition(userPrefAppBarCond,EntityOperator.AND)).orderBy("appBarElementSeqNum ASC").distinct().queryList();
		if(UtilValidate.isNotEmpty(appBarUserPreferenceList)) {
			for(GenericValue appBarElement : appBarElementList) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.putAll(DataUtil.convertGenericValueToMap(delegator, appBarElement));
				data.put("appBarElementSeqNum", "L");
				String elementId = appBarElement.getString("appBarElementId");
				for(GenericValue appBarUserPreference : appBarUserPreferenceList) {
					String appBarElementId = appBarUserPreference.getString("appBarElementId");
					if(elementId.equals(appBarElementId)) {
						data.put("appBarElementSeqNum", appBarUserPreference.getInteger("appBarElementSeqNum"));
						data.put("isActive", "Y");
					}
				}
				resultList.add(data);
				Collections.sort(resultList, new java.util.Comparator<Object>() {
				    public int compare(Object obj1, Object obj2) {
				    	Map<String,Object> map1 = (Map<String,Object>) obj1;
				    	Map<String,Object> map2 = (Map<String,Object>) obj2;
				    	String value1 = (String) map1.get("appBarElementSeqNum");
				    	String value2 = (String) map2.get("appBarElementSeqNum");
						if(DataUtil.isInteger(value1) && DataUtil.isInteger(value2)) {
							int v1 = Integer.parseInt(value1);
							int v2 = Integer.parseInt(value2);
							return (v1 == v2) ? 0 : (v1 == null ? 1 : (v2 == null ? -1 : v1.compareTo(v2))) ;
						} else {
							return (value1.equals(value2)) ? 0 : (value1 == null ? 1 : (value2 == null ? -1 : value1.compareTo(value2))) ;
						}
				    }
				});
			}
			context.put("appBarElementList",resultList);
		} else {
			for(GenericValue appBarElement : appBarElementList) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.putAll(DataUtil.convertGenericValueToMap(delegator, appBarElement));
				data.put("isActive", "N");
				resultList.add(data);
			}
			context.put("appBarElementList",resultList);
		}
	}		
}