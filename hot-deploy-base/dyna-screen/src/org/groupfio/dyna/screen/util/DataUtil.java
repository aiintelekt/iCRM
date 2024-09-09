/**
 * 
 */
package org.groupfio.dyna.screen.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.LinkedMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class DataUtil {
	
	private static String MODULE = DataUtil.class.getName();
	
	/*public static final Map<String, String> BASED_ON_CONFIG =
			Collections.unmodifiableMap(new HashMap<String, String>() {
				{
					put("TRANS_VOL", "RA_ON_1001");
					put("GIRO_TRANS_VOL", "RA_ON_1002");
					put("TRANS_AMT_LCL_CURR", "RA_ON_1000");
					put("TRANS_AMT_REMIT_CURR", "RA_ON_1003");
					put("COPY_CREATE", "RA_ON_1004");
				}
			});*/
	
	
	
}
