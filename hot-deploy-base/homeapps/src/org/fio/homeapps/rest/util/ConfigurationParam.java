package org.fio.homeapps.rest.util;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class ConfigurationParam {
	
	private static final String MODULE = ConfigurationParam.class.getName();

	private Delegator delegator;
	
	private String channelAccessId;
	
	public String getValue(String attrName) {
		
		String value = null;
		
		try {
			if (UtilValidate.isNotEmpty(attrName)) {
				
				GenericValue configuration = EntityUtil.getFirst( delegator.findByAnd("OfbizApiConfiguration", UtilMisc.toMap("attrName", attrName, "channelAccessId", channelAccessId), null, false) );
				
				if (UtilValidate.isNotEmpty(configuration)) {
					value = configuration.getString("attrValue");
				}
				
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			//e.printStackTrace();
		}
		
		return value;
	}

	public Delegator getDelegator() {
		return delegator;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	public String getChannelAccessId() {
		return channelAccessId;
	}

	public void setChannelAccessId(String channelAccessId) {
		this.channelAccessId = channelAccessId;
	}
	
}
