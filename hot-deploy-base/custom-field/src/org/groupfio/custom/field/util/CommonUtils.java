/**
 * 
 */
package org.groupfio.custom.field.util;

import org.apache.commons.lang.RandomStringUtils;

/**
 * @author sharif
 *
 */
public class CommonUtils {

	public static String getRandomString (int length) {
		//String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$&-_";
		String pwd = RandomStringUtils.random( length, characters );
		
		return pwd;
	}
	
}
