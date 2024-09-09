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
uiLabelMap = UtilProperties.getResourceBundleMap("homeappsUiLabels", locale);

profileSecurityConfig = new HashMap();

context.put("profileSecurityConfig", profileSecurityConfig);

conditionsList = FastList.newInstance();

conditionsList.add(EntityCondition.makeCondition("customSecurityGroupType", EntityOperator.EQUALS, "Y"));

securityGroupList = EntityQuery.use(delegator).from("SecurityGroup").where(conditionsList)
		.orderBy("description").queryList();
context.put("securityGroupList", org.fio.homeapps.util.DataHelper.getDropDownOptions(securityGroupList, "groupId", "groupId", "description", 0, false));

securityPermission = EntityQuery.use(delegator).from("SecurityPermission").where("actionType", "PARENT", "securityResourceType", "SCREEN")
						.queryList();

if(securityPermission != null && securityPermission.size() > 0) {

	conditionsList = FastList.newInstance();

	conditionsList.add(EntityCondition.makeCondition("permissionId", EntityOperator.IN, 
			EntityUtil.getFieldListFromEntityList(securityPermission, "permissionId", true)));
	
	EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
	
	ofbizTabSecurityShortcut = EntityQuery.use(delegator).from("OfbizTabSecurityShortcut")
			.where(mainConditons).queryList();
	if(ofbizTabSecurityShortcut != null && ofbizTabSecurityShortcut.size() > 0) {
		context.put("screenProfileList", org.fio.homeapps.util.DataHelper.getDropDownOptions(ofbizTabSecurityShortcut, "permissionId", "uiLabels", 0, false));
	}
	
}
