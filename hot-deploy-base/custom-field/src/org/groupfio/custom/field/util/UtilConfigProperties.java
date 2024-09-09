package org.groupfio.custom.field.util;

import org.groupfio.custom.field.constants.CustomFieldConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;


/**
 * @author Sharif Ul Islam
 *
 */
public class UtilConfigProperties {

	private static String MODULE = UtilConfigProperties.class.getName();
	
	public static String getPropertyValue(Delegator delegator, String name) {
		
		String propertyValue = null;
		
		try {
			
			boolean configPropertyLoadFromFile = true;
			
			GenericValue configurationParameters = EntityUtil.getFirst( delegator.findByAnd("LockboxConfigurationParameters", UtilMisc.toMap("parameterId", "config_property_load_from_file"), null, false) );
			if (UtilValidate.isNotEmpty(configurationParameters)){
				configPropertyLoadFromFile = configurationParameters.getBoolean("value");
			}
			
			if(configPropertyLoadFromFile){
				propertyValue = org.ofbiz.base.util.UtilProperties.getPropertyValue(CustomFieldConstants.configResource, name);
			}
			else{				
				configurationParameters = EntityUtil.getFirst( delegator.findByAnd("LockboxConfigurationParameters", UtilMisc.toMap("parameterId", name), null, false) );
				if (UtilValidate.isNotEmpty(configurationParameters)){
					propertyValue = configurationParameters.getString("value");
				}
			}
			
		} catch (Exception e) {			
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);	
		}
		
		return propertyValue;
	}
	
}
