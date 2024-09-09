import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

String instanceId = request.getParameter("instanceId");
String groupId = request.getParameter("groupId");

if(UtilValidate.isNotEmpty(instanceId) && UtilValidate.isNotEmpty(groupId)) {
	Map<String, Object> agGridAccessConfig = new HashMap<String, Object>();
	agGridAccessConfig.put("instanceId",instanceId);
	agGridAccessConfig.put("groupId",groupId);
	GenericValue agGridAccess = EntityQuery.use(delegator).from("AgGridAccess").where("instanceId",instanceId,"groupId",groupId).queryOne();
	if(UtilValidate.isNotEmpty(agGridAccess)) {
		String optionsJson = agGridAccess.getString("optionsJson");
		if(UtilValidate.isNotEmpty(optionsJson)) {
			println ("optionsJson---------->"+optionsJson);
			Map<String, Object> optionMap = DataUtil.convertToMap(optionsJson);
			List<String> keys = new ArrayList<String>();
			for(String key : optionMap.keySet()) {
				if("Y".equals(optionMap.get(key)))
					keys.add(key);
			}
			agGridAccessConfig.put("options", keys);
		}
	}
	context.put("agGridAccessConfig",agGridAccessConfig);
}
List<GenericValue> securityGroupList = EntityQuery.use(delegator).from("SecurityGroup").queryList();
if(UtilValidate.isNotEmpty(securityGroupList)) {
	context.put("groupList",DataUtil.getMapFromGeneric(securityGroupList, "groupId", "description", false));
}

List<GenericValue> gridInstanceList = EntityQuery.use(delegator).select("instanceId","name").from("GridUserPreferences").where("userId","admin","role","ADMIN").queryList();
if(UtilValidate.isNotEmpty(gridInstanceList)) {
	context.put("gridInstanceList",DataUtil.getMapFromGeneric(gridInstanceList, "instanceId", "name", false));
}
