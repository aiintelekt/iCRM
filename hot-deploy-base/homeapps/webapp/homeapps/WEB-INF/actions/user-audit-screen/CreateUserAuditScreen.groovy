import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import java.util.LinkedHashMap;

delegator = request.getAttribute("delegator");

// user audit [start]

auditTrackList = new LinkedHashMap<String, Object>();
isReviewScreen = false;

userAuditRequestId = request.getParameter("userAuditRequestId");
context.put("userAuditRequestId" , userAuditRequestId);

if (UtilValidate.isNotEmpty(userAuditRequestId)) {
	isReviewScreen = true;
	inputContext = new LinkedHashMap<String, Object>();
	
	auditRequest = EntityUtil.getFirst( delegator.findByAnd("UserAuditRequest", UtilMisc.toMap("userAuditRequestId", userAuditRequestId), null, false) );

	inputContext = org.fio.homeapps.util.UtilUserAudit.prepareInputContext(auditRequest);
	context.put("inputContext", inputContext);
	
	valueCompare = org.fio.homeapps.util.UtilUserAudit.prepareValueCompare(delegator, auditRequest.oldContextMap, auditRequest.contextMap, auditRequest.serviceRequestType);
	context.put("totalChanged", valueCompare.totalChanged);
	
	auditEntityConfig = EntityQuery.use(delegator).from("UserAuditPref")
						.where("userAuditPrefId", auditRequest.serviceRequestType).queryOne();
	if(UtilValidate.isNotEmpty(auditEntityConfig)) {
		context.put("auditServiceTypeDesc", auditEntityConfig.getString("description"));
	}
}

context.put("isReviewScreen" , isReviewScreen);
context.put("auditTrackList", auditTrackList);



// user audit [end]

