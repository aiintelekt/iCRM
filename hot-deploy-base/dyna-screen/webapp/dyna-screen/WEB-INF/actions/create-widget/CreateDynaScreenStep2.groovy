import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;
import org.ofbiz.webtools.labelmanager.*;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("dyna-screenUiLabels", locale);

String dynaConfigId = request.getParameter("dynaConfigId");

//layoutTypeList = new LinkedHashMap<String, Object>();
//layoutTypeList.put("1C", uiLabelMap.get("oneColumn"));
//layoutTypeList.put("2C", uiLabelMap.get("twoColumn"));
//layoutTypeList.put("3C", uiLabelMap.get("threeColumn"));
//context.put("layoutTypeList", layoutTypeList);
//
//colSizeList = new LinkedHashMap<String, Object>();
//colSizeList.put("", "Please Select");
//colSizeList.put("col-sm-1", "col-sm-1");
//colSizeList.put("col-sm-2", "col-sm-2");
//colSizeList.put("col-sm-3", "col-sm-3");
//colSizeList.put("col-sm-4", "col-sm-4");
//colSizeList.put("col-sm-5", "col-sm-5");
//colSizeList.put("col-sm-6", "col-sm-6");
//colSizeList.put("col-sm-7", "col-sm-7");
//colSizeList.put("col-sm-8", "col-sm-8");
//colSizeList.put("col-sm-9", "col-sm-9");
//colSizeList.put("col-sm-10", "col-sm-10");
//colSizeList.put("col-sm-11", "col-sm-11");
//context.put("colSizeList", colSizeList);
//
//yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
//context.put("yesNoOptions", yesNoOptions);
//
//conditionsList = [];
//conditionsList.add(EntityCondition.makeCondition("isHide", EntityOperator.EQUALS, "N"));
//mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
//componentList = delegator.findList("OfbizComponentAccess", mainConditons, null, null, null, false);
//context.put("componentList", org.fio.homeapps.util.DataHelper.getDropDownOptions(componentList, "componentName", "uiLabels"));
//
//conditionsList = [];
////conditionsList.add(EntityCondition.makeCondition("customSecurityGroupType", EntityOperator.EQUALS, "Y"));
//securityGroupList = EntityQuery.use(delegator).from("SecurityGroup").where(conditionsList)
//		.orderBy("description").queryList();
//context.put("securityGroupList", org.fio.homeapps.util.DataHelper.getDropDownOptions(securityGroupList, "groupId", "groupId", "description", 0, false));

inputContext = new LinkedHashMap<String, Object>();

inputContext.put("dynaConfigId", dynaConfigId);

if (UtilValidate.isNotEmpty(dynaConfigId)) {
	dynaScreenConfig = EntityUtil.getFirst( delegator.findByAnd("DynaScreenConfig", UtilMisc.toMap("dynaConfigId", dynaConfigId), null, false) );
	
	inputContext.put("componentMountPoint", dynaScreenConfig.get("componentMountPoint"));
	inputContext.put("screenDisplayName", dynaScreenConfig.get("screenDisplayName"));
	inputContext.put("layoutType", dynaScreenConfig.get("layoutType"));
	inputContext.put("isPrimary", dynaScreenConfig.get("isPrimary"));
	inputContext.put("isDisabled", dynaScreenConfig.get("isDisabled"));
	inputContext.put("securityGroupId", dynaScreenConfig.get("securityGroupId"));
	inputContext.put("defaultMessage", dynaScreenConfig.get("defaultMessage"));
		
	inputContext.put("fromDate", UtilValidate.isNotEmpty(dynaScreenConfig.get("fromDate")) ? UtilDateTime.timeStampToString(dynaScreenConfig.getTimestamp("fromDate"), "dd/MM/yyyy", TimeZone.getDefault(), null) : "");
	inputContext.put("thruDate", UtilValidate.isNotEmpty(dynaScreenConfig.get("thruDate")) ? UtilDateTime.timeStampToString(dynaScreenConfig.getTimestamp("thruDate"), "dd/MM/yyyy", TimeZone.getDefault(), null) : "");
					
}

curDispatchContext = dispatcher.getDispatchContext();
serviceNames = curDispatchContext.getAllServiceNames();
Map<String, Object> serviceNameList = new LinkedHashMap<String, Object>();
serviceNames.each { serviceName ->
	serviceNameList.put(serviceName, serviceName);
}
context.put("serviceNameList", serviceNameList);

/*
LabelManagerFactory factory = LabelManagerFactory.getInstance();
context.filesFound = factory.getFilesFound();
context.componentNamesFound = factory.getComponentNamesFound();

context.put("componentList", org.fio.homeapps.util.DataHelper.getDropDownOptions(factory.getComponentNamesFound(), "groupId", "groupId"));
*/


context.put("inputContext", inputContext);