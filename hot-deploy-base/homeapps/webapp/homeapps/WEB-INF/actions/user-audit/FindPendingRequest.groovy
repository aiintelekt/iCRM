import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("homeappsUiLabels", locale);

userLogin = session.getAttribute("userLogin");
context.put("userLoginId", userLogin.getString("userLoginId"));

filterContext = new HashMap();
context.put("filterContext", filterContext);

statusList = UtilMisc.toMap("PENDING", uiLabelMap.get("PENDING"), "APPROVED", uiLabelMap.get("APPROVED"), "EXPIRED", uiLabelMap.get("EXPIRED"));
context.put("statusList", statusList);

actionList = UtilMisc.toMap("CREATE", uiLabelMap.get("CREATE"), "UPDATE", uiLabelMap.get("UPDATE"));
context.put("actionList", actionList);

requestTypeList = delegator.findByAnd("UserAuditPref", null, java.util.Arrays.asList("sequenceNumber ASC"), false);
context.put("requestTypeList", org.fio.homeapps.util.DataHelper.getDropDownOptions(requestTypeList, "userAuditPrefId", "description"));

makerList = org.fio.homeapps.util.UtilUserAudit.getUserLoginList(delegator, "DBS_ADMPR_MAKER");
context.put("makerList", org.fio.homeapps.util.DataHelper.getDropDownOptions(makerList, "userLoginId", "userLoginId"));

chekerList = org.fio.homeapps.util.UtilUserAudit.getUserLoginList(delegator, "DBS_ADMPR_CHEKER");
context.put("chekerList", org.fio.homeapps.util.DataHelper.getDropDownOptions(chekerList, "userLoginId", "userLoginId"));
