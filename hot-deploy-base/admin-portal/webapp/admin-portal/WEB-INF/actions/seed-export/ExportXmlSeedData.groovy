import org.ofbiz.base.container.ContainerException;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelReader;

Delegator delegator = request.getAttribute("delegator");
ModelReader reader = delegator.getModelReader();
/*
TreeSet<String> modelEntityNames = new TreeSet<String>();
try {
	ModelReader reader = delegator.getModelReader();
	modelEntityNames = reader.getEntityCache().values() as TreeSet; //new TreeSet<String>(modelEntities.keySet());
} catch (GenericEntityException e) {
	throw new ContainerException(e.getMessage(), e);
}



List<String> entities = new LinkedList<String>();
Map<String, String> data = new HashMap<String,String>();
for(ModelEntity modelEntity : modelEntityNames) {
	String entityName = modelEntity.getEntityName();
	data.put(entityName, entityName)
}
context.put("entities", data);

*/
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


exportBy = new LinkedHashMap<String, Object>();

exportBy.put("BY_ENTITY", "Entity");
exportBy.put("BY_SQL_SCRIPT", "SQL Script");

context.put("exportBy", exportBy);