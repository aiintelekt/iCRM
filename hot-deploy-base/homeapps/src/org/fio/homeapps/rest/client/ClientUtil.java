package org.fio.homeapps.rest.client;

import org.fio.homeapps.rest.client.ClientConstants.ChannelAccessType;
import org.fio.homeapps.rest.util.ConfigurationParam;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class ClientUtil {
	
	public static RestClient getRestClient(Delegator delegator, ConfigurationParam configurationParam, String channelAccessId) {
    	
    	try {
    		if (UtilValidate.isNotEmpty(channelAccessId)) {
    			GenericValue channelAccess = EntityUtil.getFirst( delegator.findByAnd("ChannelAccess", UtilMisc.toMap("channelAccessId", channelAccessId), null, false) );
    			if (UtilValidate.isNotEmpty(channelAccess)) {
    				RestClient client = new RestClient(delegator, configurationParam, channelAccess, ChannelAccessType.ACCESS_TYPE_REST);
        			return client;
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		return null;
		
    }
	
	public static TokenClient getTokenClient(Delegator delegator, ConfigurationParam configurationParam, String channelAccessId) {
    	
    	try {
    		if (UtilValidate.isNotEmpty(channelAccessId)) {
    			GenericValue channelAccess = EntityUtil.getFirst( delegator.findByAnd("ChannelAccess", UtilMisc.toMap("channelAccessId", channelAccessId), null, false) );
    			if (UtilValidate.isNotEmpty(channelAccess)) {
    				TokenClient client = new TokenClient(delegator, configurationParam, channelAccess, ChannelAccessType.ACCESS_TYPE_REST);
        			return client;
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		return null;
		
    }
	
}
