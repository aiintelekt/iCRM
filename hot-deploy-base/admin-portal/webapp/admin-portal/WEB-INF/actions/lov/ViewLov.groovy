import java.sql.Timestamp
import java.text.SimpleDateFormat

import org.fio.crm.party.PartyHelper;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.Debug;

import javolution.util.FastList;
import java.text.DecimalFormat;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("AdminPortalUiLabels", locale);

String isView = context.get("isView");

context.put("haveDataPermission", "Y");

inputContext = new LinkedHashMap<String, Object>();

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

println("activeTab>>> "+activeTab);

yesNoMap = ["Y":"Yes", "N":"No"];
context.put("yesNoOptions", yesNoMap);

context.put("isSubscriptionExpired", false);

lovId = request.getParameter("lovId");
lovTypeId = request.getParameter("lovTypeId");

inputContext.put("lovId", lovId);
inputContext.put("lovTypeId", lovTypeId);
println("lov---------->"+lovId +"lovTypeId==="+UtilValidate.isNotEmpty(lovId));

lovEntityAssoc = from("LovEntityAssoc").where("lovEntityTypeId", lovTypeId).queryOne();

println("lovEntityAssoc------------>"+lovEntityAssoc);
if(UtilValidate.isNotEmpty(lovId) && UtilValidate.isNotEmpty(UtilValidate.isNotEmpty(lovId))){

		lovEntity = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
									UtilMisc.toMap("enumId", lovId,"enumTypeId",lovTypeId), UtilMisc.toList("sequenceId"),
									false));
		inputContext.put("lovId", lovEntity.get("enumId"));
		inputContext.put("name", lovEntity.get("name"));
		inputContext.put("description", lovEntity.get("description"));
		inputContext.put("sequence", lovEntity.get("sequenceId"));
		inputContext.put("isEnable", lovEntity.get("isEnabled"));
		
		inputContext.put("createdStamp", lovEntity.get("createdStamp"));
		inputContext.put("lastUpdatedStamp", lovEntity.get("lastUpdatedStamp"));
		
		// fillup administration info
		inputContext.put("createdOn", UtilValidate.isNotEmpty(lovEntity.get("createdStamp")) ? UtilDateTime.timeStampToString(lovEntity.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		inputContext.put("modifiedOn", UtilValidate.isNotEmpty(lovEntity.get("lastUpdatedStamp")) ? UtilDateTime.timeStampToString(lovEntity.getTimestamp("lastUpdatedStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		inputContext.put("createdBy", lovEntity.get("createdByUserLogin"));
		inputContext.put("modifiedBy", lovEntity.get("lastModifiedByUserLogin"));
	}
/*if(UtilValidate.isNotEmpty(lovEntityAssoc)){
	context.put("lovEntityAssoc", lovEntityAssoc);
	
	String entityName = lovEntityAssoc.getString("entityName");
	String idColumn = lovEntityAssoc.getString("idColumn");
	String nameColumn = lovEntityAssoc.getString("nameColumn");
	String descColumn = lovEntityAssoc.getString("descColumn");
	String sequenceColumn = lovEntityAssoc.getString("seqluenceColumn");
	String enableColumn = lovEntityAssoc.getString("enableColumn");
	
	List<EntityCondition> conditionList = FastList.newInstance();
	Set<String> fieldToSelect = new HashSet<String>();
	fieldToSelect.add(idColumn);
	fieldToSelect.add(nameColumn);
	fieldToSelect.add(descColumn);
	fieldToSelect.add(sequenceColumn);
	fieldToSelect.add(enableColumn);
	fieldToSelect.add("createdStamp");
	fieldToSelect.add("lastUpdatedStamp");
	fieldToSelect.add("createdByUserLogin");
	fieldToSelect.add("lastModifiedByUserLogin");
	
	conditionList.add(EntityCondition.makeCondition(idColumn, EntityOperator.EQUALS, lovId));
	
	GenericValue lovEntity = EntityUtil.getFirst( delegator.findList(entityName, EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldToSelect, UtilMisc.toList(sequenceColumn), null, false) );
	if(UtilValidate.isNotEmpty(lovEntity)){
		inputContext.put("lovId", lovEntity.get(idColumn));
		inputContext.put("name", lovEntity.get(nameColumn));
		inputContext.put("description", lovEntity.get(descColumn));
		inputContext.put("sequence", lovEntity.get(sequenceColumn));
		inputContext.put("isEnable", lovEntity.get(enableColumn));
		
		inputContext.put("createdStamp", lovEntity.get("createdStamp"));
		inputContext.put("lastUpdatedStamp", lovEntity.get("lastUpdatedStamp"));
		
		// fillup administration info
		inputContext.put("createdOn", UtilValidate.isNotEmpty(lovEntity.get("createdStamp")) ? UtilDateTime.timeStampToString(lovEntity.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		inputContext.put("modifiedOn", UtilValidate.isNotEmpty(lovEntity.get("lastUpdatedStamp")) ? UtilDateTime.timeStampToString(lovEntity.getTimestamp("lastUpdatedStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		inputContext.put("createdBy", lovEntity.get("createdByUserLogin"));
		inputContext.put("modifiedBy", lovEntity.get("lastModifiedByUserLogin"));
	}		
		
}*/

context.put("inputContext", inputContext);
