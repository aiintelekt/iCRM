import java.text.SimpleDateFormat


import org.ofbiz.base.util.UtilMisc;
import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityFieldValue
import org.ofbiz.entity.condition.EntityFunction
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil
import javax.servlet.http.HttpSession;
import org.fio.admin.portal.util.DataHelper




SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
HttpSession session = request.getSession();
userLogin = request.getAttribute("userLogin");
String userLoginId = userLogin.get("partyId");
println("userLoginId==-=-=-=-=--18=-=-=-=-=-"+userLoginId);


//List componentDetails = EntityQuery.use(delegator).from("OfbizComponentAccess").where("componentId","CAMPAIGN").queryOne();
//println("componentData-=-=-=-=-=23-=-=-=-="+componentDetails);


EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
	EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("componentId")), EntityOperator.NOT_EQUAL, null),
);

 componentDetails = delegator.findList("OfbizComponentAccess", condition, null, UtilMisc.toList("-createdStamp"), null, false);
 println("componentData-=-=-=-=-=23-=-=-=-="+componentDetails);
context.put("componentData", DataHelper.getDropDownOptions(componentDetails, "componentId", "componentName"));



EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.OR,
	EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumTypeId")), EntityOperator.EQUALS, "SCROLL_MSG"),
);

 messageTypeDetails = delegator.findList("Enumeration", condition1, null, null, null, false);
 context.put("msgTypeData", DataHelper.getDropDownOptions(messageTypeDetails, "enumId", "description"));
 
