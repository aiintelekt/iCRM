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

parentTemplateCategoryId = request.getParameter("parentTemplateCategoryId");
templateCategoryId = request.getParameter("templateCategoryId");


println("parentTemplateCategoryId---------->"+parentTemplateCategoryId +"templateCategoryId==="+templateCategoryId);


if(UtilValidate.isNotEmpty(parentTemplateCategoryId) && UtilValidate.isNotEmpty(UtilValidate.isNotEmpty(templateCategoryId))){

		templateCategory = EntityUtil.getFirst(delegator.findByAnd("TemplateCategory",
									UtilMisc.toMap("parentTemplateCategoryId", parentTemplateCategoryId,"templateCategoryId",templateCategoryId), UtilMisc.toList("sequenceId"),
									false));
	    inputContext.put("parentTemplateCategoryId", parentTemplateCategoryId);
		inputContext.put("templateCategoryId", templateCategoryId);
		inputContext.put("templateCategoryName", templateCategory.get("templateCategoryName"));
		inputContext.put("sequence", templateCategory.get("sequenceId"));
		inputContext.put("isEnable", templateCategory.get("isEnabled"));
		
		inputContext.put("createdStamp", templateCategory.get("createdStamp"));
		inputContext.put("lastUpdatedStamp", templateCategory.get("lastUpdatedStamp"));
		
		// fillup administration info
		inputContext.put("createdOn", UtilValidate.isNotEmpty(templateCategory.get("createdStamp")) ? UtilDateTime.timeStampToString(templateCategory.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		inputContext.put("modifiedOn", UtilValidate.isNotEmpty(templateCategory.get("lastUpdatedStamp")) ? UtilDateTime.timeStampToString(templateCategory.getTimestamp("lastUpdatedStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		//inputContext.put("createdBy", templateCategory.get("createdByUserLogin"));
		//inputContext.put("modifiedBy", templateCategory.get("lastModifiedByUserLogin"));
	}

	
context.put("inputContext", inputContext);
