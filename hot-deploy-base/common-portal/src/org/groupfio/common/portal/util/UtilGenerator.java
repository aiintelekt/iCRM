/**
 * 
 */
package org.groupfio.common.portal.util;

import org.ofbiz.base.util.UtilValidate;

/**
 * @author Sharif
 *
 */
public class UtilGenerator {

	private static String MODULE = UtilGenerator.class.getName();
	
	public static String getFormattedCode(String prefix, String sequenceNumber) {
		String formattedCode = "";
		if (UtilValidate.isNotEmpty(prefix)) {
			formattedCode = prefix;
		}

		if (UtilValidate.isNotEmpty(sequenceNumber)) {
			int length = sequenceNumber.length();
			if (length == 1) {
				formattedCode += "0000" + (sequenceNumber);
			} else if (length == 2) {
				formattedCode += "000" + (sequenceNumber);
			} else if (length == 3) {
				formattedCode += "00" + (sequenceNumber);
			} else if (length == 4) {
				formattedCode += "0" + (sequenceNumber);
			} else {
				formattedCode += (sequenceNumber);
			}
		}
		return formattedCode;
	}
	
}
