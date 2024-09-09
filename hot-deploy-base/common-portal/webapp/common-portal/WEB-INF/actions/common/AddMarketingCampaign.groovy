import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.base.util.StringUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);

String partyId = request.getParameter("partyId");
partyRoleTypeId = context.get("partyRoleTypeId");
inputContext = context.get("inputContext");

if (UtilValidate.isNotEmpty(partyId)) {
	
	campaignRoles = delegator.findByAnd("MarketingCampaignRole",UtilMisc.toMap("partyId", partyId, "roleTypeId", partyRoleTypeId),null,false);
    campaigns = EntityUtil.getRelated("MarketingCampaign", campaignRoles);
    context.put("marketingCampaigns", campaigns);
    if ((campaignsList = EntityUtil.getFieldListFromEntityList(campaigns, "campaignName", false)) != null) {
        marketingCampaignNames = StringUtil.join(campaignsList, ", ");
        if (UtilValidate.isNotEmpty(marketingCampaignNames)) {
        	context.put("marketingCampaignNames", marketingCampaignNames);
        	inputContext.put("marketingCampaignNames", marketingCampaignNames);
        }
    }
	
}

context.put("inputContext", inputContext);
