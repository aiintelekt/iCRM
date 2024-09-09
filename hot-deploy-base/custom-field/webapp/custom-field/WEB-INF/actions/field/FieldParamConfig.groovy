import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.groupfio.custom.field.util.DataHelper;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.groupfio.custom.field.util.DataUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

groupId = request.getParameter("groupId"); 
customFieldId = request.getParameter("customFieldId"); 

context.put("groupId", groupId);
context.put("customFieldId", customFieldId);

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CF_PARM_TYPE"));
conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
fieldParamTypes = delegator.findList("Enumeration", mainConditons, null, null, null, false);
context.put("fieldParamTypes", fieldParamTypes);

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CF_PARM_VAL_TYPE"));
conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
fieldParamValueTypes = delegator.findList("Enumeration", mainConditons, null, null, null, false);
context.put("fieldParamValueTypes", fieldParamValueTypes);

println("customFieldId>> "+customFieldId);
if (UtilValidate.isNotEmpty(customFieldId)) {

	customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId, "groupId", groupId), null, false) );
	if (UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("paramData"))) {
		String paramData = customField.getString("paramData");
		List<Object> paramDataList = org.fio.homeapps.util.ParamUtil.jsonToList(paramData);
		println('paramDataList> '+paramDataList);
		
		context.put("paramDataList", paramDataList);
	}
	
}


