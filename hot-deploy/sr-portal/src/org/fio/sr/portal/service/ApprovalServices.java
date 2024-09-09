/**
 * 
 */
package org.fio.sr.portal.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.util.UtilAttribute;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class ApprovalServices {

	private static final String MODULE = ApprovalServices.class.getName();
	
	public static Map complete3PLInvApproval(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map requestContext = (Map) context.get("responseContext");
		
		String approvalCategoryId = (String) requestContext.get("approvalCategoryId");
		
		String domainEntityType = (String) requestContext.get("domainEntityType");
		String domainEntityId = (String) requestContext.get("domainEntityId");
		
		String parentWorkEffortId = (String) requestContext.get("parentWorkEffortId");
		String parentDecisionStatusId = (String) requestContext.get("parentDecisionStatusId");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			if (domainEntityType.equals(DomainEntityType.SERVICE_REQUEST)) {
				GenericValue attrEntityAssoc = EntityUtil.getFirst( delegator.findByAnd("AttrEntityAssoc", UtilMisc.toMap("domainEntityType", "ACTIVITY"), null, true) );
				
				String invStatus = null;
				if (UtilValidate.isNotEmpty(parentDecisionStatusId) && parentDecisionStatusId.equals("DECISION_APPROVED")) {
					invStatus = "APPROVED";
				} else if (UtilValidate.isNotEmpty(parentDecisionStatusId) && parentDecisionStatusId.equals("DECISION_REJECTED")) {
					invStatus = "DECLINED";
				}
				
				if (UtilValidate.isNotEmpty(invStatus)) {
					UtilAttribute.storeAttribute(delegator, UtilMisc.toMap("attrEntityAssoc", attrEntityAssoc, 
							"customFieldId", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APV3PL_INV_STS"), "domainEntityId", parentWorkEffortId, "domainEntityType", "ACTIVITY", "value", invStatus));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.putAll(ServiceUtil.returnSuccess("Completed 3PL Invoice Approval Successfully.."));
		return result;
	}
	
}
