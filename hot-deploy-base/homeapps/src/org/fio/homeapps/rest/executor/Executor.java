/**
 * 
 */
package org.fio.homeapps.rest.executor;

import java.util.Map;

import org.fio.homeapps.rest.response.Response;

/**
 * @author Sharif
 *
 */
public abstract class Executor {

	protected abstract Response doExecute(Map<String, Object> context) throws Exception;
	
	public Response execute(Map<String, Object> context) throws Exception {
		return doExecute(context);
	}
	
}
