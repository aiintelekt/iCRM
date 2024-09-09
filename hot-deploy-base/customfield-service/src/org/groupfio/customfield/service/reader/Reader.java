/**
 * 
 */
package org.groupfio.customfield.service.reader;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;

/**
 * @author Sharif
 *
 */
public abstract class Reader {

	private static String MODULE = Reader.class.getName();
	
	private Map<String, Object> context;

	protected abstract Map<String, Object> doRead(Map<String, Object> context) throws Exception;

	public Map<String, Object> read(Map<String, Object> context){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = doRead(context);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return result;
	}
	
}
