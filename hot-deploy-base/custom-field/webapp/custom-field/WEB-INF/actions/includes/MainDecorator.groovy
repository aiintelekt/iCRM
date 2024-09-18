/**
 * @author Sharif Ul Islam
 * @since June 16, 2015
 * 
 */
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.*;
import java.util.*;
import org.ofbiz.entity.*;

/*
// set an infrastructure object
infrastructure = new Infrastructure(dispatcher);
globalContext.put("infrastructure", infrastructure);

user = null;
if (userLogin != null) {
    user = new User(userLogin, delegator);
    globalContext.put("user", user);
}

// Instead of using screens to define the uiLabelMap, grab it from UtilMessage
uiLabelMap = UtilMessage.getUiLabels(locale);

// Add any uiLabels defined upstream to the top of the map
existingUiLabelMap = globalContext.get("uiLabelMap");
if (existingUiLabelMap != null) uiLabelMap.pushResourceBundle(existingUiLabelMap.getInitialResourceBundle());

globalContext.put("uiLabelMap", uiLabelMap); 

// Place the opentapsErrors map in the global context
opentapsErrors = UtilMessage.getOpentapsErrors(request);
globalContext.put("opentapsErrors", opentapsErrors);

// Add the import and include transforms
// These will enable Freemarker include loading and global macros from a remote file.
// Note that globalContext will allow access from all sections of the screen widget
loader = Thread.currentThread().getContextClassLoader();
globalContext.put("import", loader.loadClass("org.opentaps.common.template.freemarker.transform.ImportTransform").newInstance());
globalContext.put("include", loader.loadClass("org.opentaps.common.template.freemarker.transform.IncludeTransform").newInstance());
globalContext.put("paginateTransform", loader.loadClass("org.opentaps.common.webapp.transform.PaginateTransform").newInstance());

//TODO: oandreyev. This should not be here
globalContext.put("timeZone", UtilCommon.getTimeZone(request));

if (userLogin != null) {

	

}

*/
// Restriction based on permission 
List<GenericValue> userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup")
                    .where(EntityCondition.makeCondition("groupId",EntityOperator.LIKE,"DBS_%"), 
                    EntityCondition.makeCondition("userLoginId", userLogin.userLoginId))
                    .cache().filterByDate().queryList();
println("userLoginSecurityGroup:::::"+userLogin.partyId);                    
if(UtilValidate.isNotEmpty(userLoginSecurityGroup)){
   List<GenericValue> securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        .where(EntityCondition.makeCondition("groupId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(userLoginSecurityGroup, "groupId", true)))
        .queryList();
   List<String> permissionIds = EntityUtil.getFieldListFromEntityList(securityGroupPermission, "permissionId", true);
   if(UtilValidate.isNotEmpty(permissionIds)){
      List<GenericValue> componentAccess = EntityQuery.use(delegator).from("OfbizComponentAccess")
        .where(EntityCondition.makeCondition("permissionId", EntityOperator.IN, permissionIds))
        .queryList();
      println("componentAccess123:::::"+componentAccess);
      if(UtilValidate.isNotEmpty(componentAccess)){
          context.put("componentAccess",componentAccess);
      }else{
        context.put("componentAccess","");
      }
   }
}