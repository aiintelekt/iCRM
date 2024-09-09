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
import org.groupfio.custom.field.util.DataUtil;
import java.util.ArrayList;
import org.ofbiz.base.util.StringUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

campaignConfig = UtilMisc.toMap("configType", "BATCH");
context.put("campaignConfig", campaignConfig);

groupId = request.getParameter("groupId"); 
customFieldId = request.getParameter("customFieldId"); 

marketingCampaignId = request.getParameter("marketingCampaignId");

context.put("groupId", groupId);
context.put("customFieldId", customFieldId);
context.put("marketingCampaignId", marketingCampaignId);

campaignFieldDet= EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );

if(UtilValidate.isNotEmpty(campaignFieldDet)){
context.put("campaignFieldDet", campaignFieldDet);
}
marketingCampaignDet= EntityUtil.getFirst( delegator.findByAnd("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", marketingCampaignId), null, false) );
if(UtilValidate.isNotEmpty(marketingCampaignDet)){
	context.put("marketingCampaignDet", marketingCampaignDet);
	}
	
println("customFieldId>> "+customFieldId);
if (UtilValidate.isNotEmpty(customFieldId)) {

	campaignConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldCampaignConfig", UtilMisc.toMap("customFieldId", customFieldId, "groupId", groupId), null, false) );
	if (UtilValidate.isNotEmpty(campaignConfig)) {
		context.put("campaignConfig", campaignConfig);
	}
	
	condition = UtilMisc.toMap("customFieldId", customFieldId);

	cond = EntityCondition.makeCondition(condition);
	campaignConfigAssocList = delegator.findList("CustomFieldCampaignConfigAssoc", cond, null, ["sequenceNumber"], null, false);
	context.put("campaignConfigAssocList", campaignConfigAssocList);
	println("campaignConfigAssocList>> "+campaignConfigAssocList);
	
	isDripProcessed = "N";
	List<String> processedCampaignIds = new ArrayList();
	for (GenericValue ccal : campaignConfigAssocList) {
		if (UtilValidate.isNotEmpty(ccal.getString("isProcessed")) && ccal.getString("isProcessed").equals("Y")) {
			isDripProcessed = "Y";
			processedCampaignIds.add(ccal.getString("marketingCampaignId"));
		}
	}
	context.put("isDripProcessed", isDripProcessed);
	if (UtilValidate.isNotEmpty(processedCampaignIds)) {
		context.put("processedCampaignIds", org.ofbiz.base.util.StringUtil.join(processedCampaignIds, ","));
	}
	
	parentCampaign = DataUtil.getCampaign(delegator, groupId);
	context.put("parentCampaign", UtilValidate.isNotEmpty(parentCampaign) ? parentCampaign : UtilMisc.toMap());
}

String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
context.put("globalDateFormat",globalDateFormat);

