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
loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", userLogin.getString("userLoginId"));

noteUserRoleList = new LinkedHashMap<String, Object>();

noteUserRoleList.put("TECHNICIAN", "Technician");
noteUserRoleList.put("CUST_SERVICE_REP", "Customer Service Rep");
noteUserRoleList.put("DEALER", "DEALER");
noteUserRoleList.put("SALES_REP", "Sales Representative");

context.put("noteUserRoleList", noteUserRoleList);


