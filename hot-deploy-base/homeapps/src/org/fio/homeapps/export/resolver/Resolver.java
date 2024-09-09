/**
 * 
 */
package org.fio.homeapps.export.resolver;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sharif
 *
 */
public abstract class Resolver {

	private static final Logger log = LoggerFactory.getLogger(Resolver.class);
	
	private Map<String, Object> context;

	protected abstract Map<String, Object> doResolve(Map<String, Object> context) throws Exception;

	public Map<String, Object> resolve(Map<String, Object> context){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = doResolve(context);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}
	
}
