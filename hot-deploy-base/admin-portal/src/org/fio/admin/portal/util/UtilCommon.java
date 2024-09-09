package org.fio.admin.portal.util;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import net.sf.json.JSONObject;

import java.util.Locale;
import java.util.TimeZone;

import org.groupfio.token.app.util.UtilEncoder;

/**
 * @author Sharif
 *
 */
public class UtilCommon {
	
	private static final String MODULE = UtilCommon.class.getName();
	
	public static String getSSOToken(Delegator delegator, GenericValue userLogin) {
		try {
			if (UtilValidate.isNotEmpty(delegator) && UtilValidate.isNotEmpty(userLogin)) {
				String siteKey=null;
				String apiKey=null;
				String tenantId = null;
				int tokenExpiryTime = 5; 
				GenericValue tenantProp = EntityQuery.use(delegator).from("TenantProperties").where("resourceName","security","propertyName","common.site.key").queryFirst();
				if(UtilValidate.isNotEmpty(tenantProp)) {
			  		siteKey = tenantProp.getString("propertyValue");
			  	}
			  	tenantProp = EntityQuery.use(delegator).from("TenantProperties").where("resourceName","security","propertyName","common.api.key").queryFirst();
			  	if(UtilValidate.isNotEmpty(tenantProp)) {
			  		apiKey = tenantProp.getString("propertyValue");
			  	}
			  	tenantProp = EntityQuery.use(delegator).from("TenantProperties").where("resourceName","security","propertyName","common.token.expiry.min").queryFirst();
			  	if(UtilValidate.isNotEmpty(tenantProp) && UtilValidate.isNotEmpty(tenantProp.getString("propertyValue"))) {
			  		tokenExpiryTime = Integer.parseInt(tenantProp.getString("propertyValue"));
			  	}
			  	tenantProp = EntityQuery.use(delegator).from("TenantProperties").where("resourceName","security","propertyName","common.site.tenantId").queryFirst();
			  	if(UtilValidate.isNotEmpty(tenantProp)) {
			  		tenantId = tenantProp.getString("propertyValue");
			  	}
				
				//System.out.println("siteKey> "+siteKey+", apiKey> "+apiKey+", tokenExpiryTime> "+tokenExpiryTime+", tenantId> "+tenantId);
				
				if(UtilValidate.isNotEmpty(siteKey) && UtilValidate.isNotEmpty(apiKey)) {
					JSONObject data = new JSONObject();
					data.put("user_login_id", userLogin.getString("userLoginId"));
					data.put("version", "1.0");
					data.put("tenant_id", tenantId);
					data.put("expires", org.ofbiz.base.util.UtilDateTime.timeStampToString(org.groupfio.token.app.util.UtilDateTime.addMinutesToTimestamp(org.ofbiz.base.util.UtilDateTime.nowTimestamp(), tokenExpiryTime), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault()));
					
					String token = UtilEncoder.encodedString(siteKey, apiKey, data);
					//System.out.println("token> "+token);
					return token;
				}
			}
		} catch (Exception e) {			
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);	
		}
		return null;
	}

}
