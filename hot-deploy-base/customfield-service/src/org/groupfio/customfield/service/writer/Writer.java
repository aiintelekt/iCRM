/**
 * 
 */
package org.groupfio.customfield.service.writer;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;

/**
 * @author Sharif
 *
 */
public abstract class Writer {

	private static String MODULE = Writer.class.getName();
	
	private Map<String, Object> context;

	protected abstract Map<String, Object> doWrite(Map<String, Object> context) throws Exception;

	public Map<String, Object> write(Map<String, Object> context){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = doWrite(context);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return result;
	}
	
}
