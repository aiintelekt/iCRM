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

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);

String partyId = request.getParameter("partyId");

String isLoyaltyEnable = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_LOYALTY_ENABLE");
context.put("isLoyaltyEnable", isLoyaltyEnable);
