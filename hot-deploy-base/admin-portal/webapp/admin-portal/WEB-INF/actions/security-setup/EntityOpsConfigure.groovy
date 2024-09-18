import org.fio.admin.portal.util.DataUtil;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityQuery;


Map<String, ModelEntity> modelEntities;
try {
	modelEntities = delegator.getModelEntityMapByGroup("org.ofbiz");
} catch (GenericEntityException e) {
	throw new ContainerException(e.getMessage(), e);
}
TreeSet<String> modelEntityNames = new TreeSet<String>(modelEntities.keySet());



List<String> entities = new LinkedList<String>();
Map<String, String> data = new HashMap<String,String>();
for (String entityName : modelEntityNames) {
	data.put(entityName, entityName)
}
context.put("entities", data);

String entityName = request.getParameter("entityName");
String operationName = request.getParameter("operationName");
String roleTypeId = request.getParameter("roleTypeId");
List<EntityCondition> conditions = new ArrayList<EntityCondition>();
if(UtilValidate.isNotEmpty(entityName)) {
	conditions.add(EntityCondition.makeCondition("entityName",EntityOperator.EQUALS,entityName));
}
if(UtilValidate.isNotEmpty(operationName)) {
	conditions.add(EntityCondition.makeCondition("operationName",EntityOperator.EQUALS,operationName));
}
if(UtilValidate.isNotEmpty(roleTypeId) && !"null".equals(roleTypeId)) {
	conditions.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,roleTypeId));
}
if(conditions != null && conditions.size() > 0) {
	EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	entityOpsConfigList = EntityQuery.use(delegator).from("EntityOperationConfig").where(condition).orderBy("-lastUpdatedTxStamp").queryList();
	if(entityOpsConfigList != null && entityOpsConfigList.size() > 0) {
		Map<String, Object> entityConfig = new HashMap<String, Object>();
		entityConfig.put("entityName", entityName);
		entityConfig.put("operationName", operationName);
		entityConfig.put("entityType", entityOpsConfigList.get(0).getString("entityType"));
		entityConfig.put("roleTypeId", entityOpsConfigList.get(0).getString("roleTypeId"));
		entityConfig.put("entityAliasName", entityOpsConfigList.get(0).getString("entityAliasName"));
		List<String> operations = new ArrayList<String>();
		for(GenericValue entityOpsConfigGv : entityOpsConfigList) {
			operations.add(entityOpsConfigGv.getString("operationName").toUpperCase());
		}
		println ("operations--->"+operations);
		entityConfig.put("operations", operations);
		context.put("entityOpsConfig", entityConfig);
	}
}

Map<String, Object> entityType = new HashMap<>();
entityType.put("PARTY_ENTITY", "Party Entity");
entityType.put("NON_PARTY_ENTITY", "Non Party Entity");
context.put("entityTypeList",entityType);

List<GenericValue> roles = EntityQuery.use(delegator).select("roleTypeId","description").from("RoleType").where("parentTypeId","SECURITY_ROLE").queryList();
if(UtilValidate.isNotEmpty(roles)) {
	context.put("roleTypeList",DataUtil.getMapFromGeneric(roles, "roleTypeId", "description", false));
}

