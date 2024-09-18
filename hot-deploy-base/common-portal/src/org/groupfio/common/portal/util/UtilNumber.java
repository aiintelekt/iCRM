/**
 * 
 */
package org.groupfio.common.portal.util;

import java.math.BigDecimal;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;

/**
 * @author Sharif
 *
 */
public class UtilNumber {
	
	private static final String MODULE = UtilCommon.class.getName();
	
	public static boolean isLong (String value) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				return UtilValidate.isNotEmpty(Long.valueOf(""+value));
			}
		} catch (NumberFormatException e) {
		}
		return false;
	}
	
	public static boolean isBigDecimal (String value) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				return UtilValidate.isNotEmpty(BigDecimal.valueOf(Double.valueOf(""+value)));
			}
		} catch (NumberFormatException e) {
		}
		return false;
	}
	
	public static boolean isDouble (String value) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				return UtilValidate.isNotEmpty(Double.valueOf(Double.valueOf(""+value)));
			}
		} catch (NumberFormatException e) {
		}
		return false;
	}

}
