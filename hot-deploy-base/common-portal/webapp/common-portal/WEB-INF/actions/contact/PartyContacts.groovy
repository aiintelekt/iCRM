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

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);

String partyId = request.getParameter("partyId");

partyId = parameters.get("partyId");
firstName = parameters.get("firstName");
lastName = parameters.get("lastName");
emailAddress = parameters.get("emailAddress");
contactNumber = parameters.get("contactNumber");

delegator = request.getAttribute("delegator");
if(UtilValidate.isNotEmpty(partyId))
	context.put("partyId", partyId);

context.put("firstName", firstName);
context.put("lastName", lastName);
context.put("emailAddress", emailAddress);
context.put("contactNumber", contactNumber);

List custFLst = [];
List custFAndExprs = [];
custFAndExprs.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "FINDLISTGROUP"));
custFAndExprs.add(EntityCondition.makeCondition("hide", EntityOperator.NOT_EQUAL, "Y"));

List custF = from("CustomField").where(custFAndExprs).orderBy("sequenceNumber").queryList()
for(GenericValue gp :custF){
	Map partyDetails = new HashMap();
	
	customFieldName = gp.getString("customFieldName");
	customFieldId = gp.getString("customFieldId");
	partyDetails.put("customFieldName", customFieldName);
	partyDetails.put("customFieldId", customFieldId);
	custFLst.add(partyDetails);

}

context.put("groupList",custFLst);


