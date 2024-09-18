/**
 * 
 */
package org.fio.crm.resolver;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;

/**
 * @author Sharif
 *
 */
public abstract class Resolver {

	private static String MODULE = Resolver.class.getName();
	
	private Map<String, Object> context;

	protected abstract Map<String, Object> doResolve(Map<String, Object> context) throws Exception;

	public Map<String, Object> resolve(Map<String, Object> context) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = doResolve(context);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return result;
	}
	
}
