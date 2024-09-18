import org.apache.commons.collections4.map.LinkedMap
import org.fio.admin.portal.util.DataHelper
import org.fio.admin.portal.util.DataUtil
import org.fio.admin.portal.util.EnumUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

List<String> orderBy = new ArrayList<String>();
orderBy.add("entityName");
orderBy.add("seqId");
List<GenericValue> configuredEntityList = EntityQuery.use(delegator).select("entityName","entityAliasName","operationName","isEnabled","entityType").from("EntityOperationConfig").orderBy(orderBy).queryList();
if(configuredEntityList != null && configuredEntityList.size() > 0) {
	List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
	Map<String, Object> entityMap = new LinkedMap<String, Object>();
	for(GenericValue configuredEntity : configuredEntityList) {
		Map<String, Object> configuredDetails = new HashMap<String, Object>();
		List<Map<String, Object>> details = new LinkedList<Map<String, Object>>();
		String entityType = configuredEntity.getString("entityType");
		String entityName = configuredEntity.getString("entityName");
		if("PARTY_ENTITY".equals(entityType)) {
			entityName = configuredEntity.getString("entityAliasName");
		}
		configuredDetails.put("operationName", configuredEntity.getString("operationName"));
		configuredDetails.put("isEnabled", configuredEntity.getString("isEnabled"));
		if(entityMap.containsKey(entityName)) {
			details = entityMap.get(entityName);
			details.add(configuredDetails);
		} else {
			details.add(configuredDetails);
		}
		entityMap.put(entityName, details);
	}
	//println ("entityConfigureMap--->"+entityMap)
	context.put("entityConfigureMap", entityMap);
}

String roleTypeId = request.getParameter("roleTypeId");
if(UtilValidate.isNotEmpty(roleTypeId)) {
	Set<String> fieldsToSelect = new TreeSet<String>();
	fieldsToSelect.add("securityGroupId");
	fieldsToSelect.add("permissionId");
	fieldsToSelect.add("entityName");
	fieldsToSelect.add("operationName");
	fieldsToSelect.add("oplevel");
    List<GenericValue> securityOperationLevels = EntityQuery.use(delegator).select(fieldsToSelect).from("SecurityGroupOpLevel").where("roleTypeId",roleTypeId).queryList();
	if(UtilValidate.isNotEmpty(securityOperationLevels)) {
		Map<String, Object> securityLevelMap = new LinkedMap<String, Object>();
		for(GenericValue securityOperationLevel : securityOperationLevels) {
			Map<String, Object> levelDetails = new HashMap<String, Object>();
			List<Map<String, Object>> details1 = new LinkedList<Map<String, Object>>();
			
			String entityName1 = securityOperationLevel.getString("entityName");
			String operationName = securityOperationLevel.getString("operationName");
			String oplevel = securityOperationLevel.getString("oplevel");
			String levelStr = oplevel.replace("L", "");
			int imgValue = 0;
			if(DataUtil.isInteger(levelStr))
				imgValue = Integer.parseInt(levelStr) == 6 ? 0 : Integer.parseInt(levelStr);
			levelDetails.put("oplevel", oplevel);
			levelDetails.put("imgValue", imgValue);
			levelDetails.put("operationName", operationName);
			if(securityLevelMap.containsKey(entityName1)) {
				details1 = securityLevelMap.get(entityName1);
				details1.add(levelDetails);
			} else {
				details1.add(levelDetails);
			}
			securityLevelMap.put(entityName1, details1);
		}
		context.put("securityLevelMap", securityLevelMap);
		//println ("securityLevelMap--->"+securityLevelMap);
	}
}

List<String> levelImage = new LinkedList<String>();
levelImage.add("/bootstrap/images/type-6-red.png");
levelImage.add("/bootstrap/images/type-1-red.png");
levelImage.add("/bootstrap/images/type-2-yellow.png");
levelImage.add("/bootstrap/images/type-3-yellow.png");
levelImage.add("/bootstrap/images/type-4-green.png");
levelImage.add("/bootstrap/images/type-5-green.png");
context.put("levelImage",levelImage);
//println ("levelImage--->"+levelImage)
/*
Map<String, String> levelOptions = new LinkedHashMap<String, String>();
levelOptions.put("L1", "No");
levelOptions.put("L2", "L2 - User");
levelOptions.put("L3", "L3 - Team");
levelOptions.put("L4", "L4 - BU");
levelOptions.put("L5", "L5  -P-C-BU");
levelOptions.put("L6", "L6 - Org");
context.put("levelOptions",levelOptions); */

List<GenericValue> opsLevelEnum = EnumUtil.getEnums(delegator, "OPERATION_LEVELS");
context.put("levelOptions",DataHelper.getDropDownOptions(opsLevelEnum, "enumId", "enumId"));
