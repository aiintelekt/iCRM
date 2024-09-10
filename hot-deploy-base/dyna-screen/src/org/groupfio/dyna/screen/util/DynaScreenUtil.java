/**
 * 
 */
package org.groupfio.dyna.screen.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.dyna.screen.DynaScreenConstants.LayoutType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class DynaScreenUtil {
	
	private static final String MODULE = DynaScreenUtil.class.getName();
	
	public static Map<String, Object> screenComputation (Delegator delegator, int fieldCount, String layoutType) {
		Map<String, Object> screenComputation = new LinkedHashMap<String, Object>();
		try {
			
			int layoutColumn = 1;
    		if (UtilValidate.isNotEmpty(layoutType)) {
    			if (layoutType.equals(LayoutType.ONE_COLUMN)) {
    				layoutColumn = 1;
    			} else if (layoutType.equals(LayoutType.TWO_COLUMN)) {
    				layoutColumn = 2;
    			} else if (layoutType.equals(LayoutType.THREE_COLUMN)) {
    				layoutColumn = 3;
    			}
    		}
    		
    		int colFieldRemainCount = (fieldCount%layoutColumn);
    		int colFieldCount = (fieldCount/layoutColumn);
    		
    		screenComputation.put("layoutColumn", layoutColumn);
    		screenComputation.put("fieldCount", fieldCount);
    		screenComputation.put("colFieldRemainCount", colFieldRemainCount);
    		screenComputation.put("colFieldCount", colFieldCount);
			
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return screenComputation;
	}
	
}
