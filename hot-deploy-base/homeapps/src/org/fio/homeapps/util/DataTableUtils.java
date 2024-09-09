/**
 * 
 */
package org.fio.homeapps.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;

/**
 * @author sharif
 *
 */
public class DataTableUtils {
	
	public static String getOrderBy (HttpServletRequest request) {
    	
    	Map paramMap = UtilHttp.getCombinedMap(request);
    	String orderBy = "";
    	String dir = "";
    		
		for (int i = 0 ; i < getDataColumnLength(request) ; i++) {
			orderBy = (String) paramMap.get("order["+i+"][column]");
			if (UtilValidate.isNotEmpty(orderBy)) {
				orderBy = (String) paramMap.get("columns["+i+"][data]");
				dir = (String) paramMap.get("order["+i+"][dir]");
				break;
			}
		}
    	
    	return orderBy + " " + dir.toUpperCase();
    }
	
	public static int getDataColumnLength(HttpServletRequest request) {
		Map paramMap = UtilHttp.getCombinedMap(request);
		String column = "";
		int length = 0;
		while (column != null) {
			column = (String) paramMap.get("columns["+length+"][data]");
			length++;
		}
		return length-1;
	}

}
