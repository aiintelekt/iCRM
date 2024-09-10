package org.groupfio.crm.service.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Raja
 * @author Sharif
 *
 */
public class SecurityCodeServiceImpl {

	private static final String MODULE = SecurityCodeServiceImpl.class.getName();
    
	public static Map createSecurityCode(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String groupId = (String) context.get("groupId");
		String description = (String) context.get("description");
		String type = (String) context.get("type");
		Map<String, Object> result = new HashMap<String, Object>();

		if(UtilValidate.isNotEmpty(groupId)) {
			try {
				GenericValue securityGroup = EntityQuery.use(delegator).from("SecurityGroup")
						.where("groupId", groupId).queryOne();
				if(securityGroup != null && securityGroup.size() > 0) {
					result.putAll(ServiceUtil.returnError("Security ID already exists!"));
					return result;
				}

				securityGroup = delegator.makeValue("SecurityGroup", UtilMisc.toMap("groupId", groupId));
				securityGroup.put("description", description);
				securityGroup.put("customSecurityGroupType", "Y");
				securityGroup.put("customSecurityType", type);
				securityGroup.create();

				result.putAll(ServiceUtil.returnSuccess("Security Code created successfully!"));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		result.put("groupId", groupId);
		return result;
	}
	
	public static Map updateSecurityCode(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String groupId = (String) context.get("groupId");
		String description = (String) context.get("description");
		String type = (String) context.get("type");
		Map<String, Object> result = new HashMap<String, Object>();

		if(UtilValidate.isNotEmpty(groupId)) {
			try {
				GenericValue securityGroup = EntityQuery.use(delegator).from("SecurityGroup")
						.where("groupId", groupId).queryOne();
				if(securityGroup != null && securityGroup.size() > 0) {
					securityGroup.put("description", description);
					securityGroup.put("customSecurityGroupType", "Y");
					securityGroup.put("customSecurityType", type);
					securityGroup.store();
				} else {
					result.putAll(ServiceUtil.returnError("Invalid Security ID!"));
					return result;
				}
				result.putAll(ServiceUtil.returnSuccess("Security Code updated successfully!"));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		result.put("groupId", groupId);
		return result;
	}
}
