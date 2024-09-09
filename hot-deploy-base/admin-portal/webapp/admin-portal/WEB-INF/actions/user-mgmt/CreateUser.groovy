import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import javolution.util.FastList;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.GenericValue;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("AdminPortalUiLabels", locale);
	
//chargeTypeList = delegator.findByAnd("SecurityGroup", UtilMisc.toMap("customSecurityGroupType", "Y"), java.util.Arrays.asList("description"), false);
securityGroupList = delegator.findByAnd("SecurityGroup", null, java.util.Arrays.asList("description"), false);
context.put("securityGroupList", org.fio.homeapps.util.DataHelper.getDropDownOptions(securityGroupList, "groupId", "groupId", "description"));