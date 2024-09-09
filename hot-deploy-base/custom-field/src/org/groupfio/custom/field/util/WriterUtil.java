/**
 * 
 */
package org.groupfio.custom.field.util;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class WriterUtil {

	private static String MODULE = WriterUtil.class.getName();
	
	public static boolean writeLog(LocalDispatcher dispatcher, String taskName, String logMsg, String tableName, String modelName) {
		
		try {
			
			Map<String, Object> reqContext = FastMap.newInstance();
        	reqContext.put("logMessage", logMsg);
        	reqContext.put("taskName", taskName);
        	reqContext.put("etlTableName", tableName);
        	reqContext.put("modelName", modelName);
        	
        	Map<String, Object> result = dispatcher.runSync("writeEtlErrorLog", reqContext);
        	if (!ServiceUtil.isError(result)) {
        		return true;
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError("Error write log>>"+e.getMessage(), MODULE);
		}
		
		return false;
	}	
	
}
