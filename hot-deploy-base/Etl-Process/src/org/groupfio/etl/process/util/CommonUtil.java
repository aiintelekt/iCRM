/**
 * 
 */
package org.groupfio.etl.process.util;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Group Fio
 *
 */
public class CommonUtil {
	private static String MODULE = CommonUtil.class.getName();

	public static String getFileExtension(String fileName) {
		if (UtilValidate.isNotEmpty(fileName)) {
			return fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
		}
		return null;
	}
	
	public static String getAbsoulateFileName(String fileName) {
		if (UtilValidate.isNotEmpty(fileName)) {
			return fileName.substring(0, fileName.lastIndexOf("."));
		}
		return null;
	}
	
	public static String getEtlProcessTableName (Delegator delegator, String processId) {
		
		try {
			GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess",UtilMisc.toMap("processId", processId),null,false));
			if (UtilValidate.isNotEmpty(checkProcess)) {
				return checkProcess.getString("tableName");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			Debug.logError(e, MODULE);
		}
		
		return null;
	}
	
	public static boolean validateRange (Map<Long, Long> rangeList, Long counter) {
		
		if (UtilValidate.isEmpty(rangeList)) {
			return true;
		}
			
		for (Long start : rangeList.keySet()) {
			Long end = rangeList.get(start);
			
			if (counter >= start-1 && counter <= end-1) {
				return true;
			} 
			
		}
		
		return false;
	}
	
}
