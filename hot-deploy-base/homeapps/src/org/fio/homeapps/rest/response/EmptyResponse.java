/**
 * 
 */
package org.fio.homeapps.rest.response;

import java.util.Map;

import org.fio.homeapps.rest.response.Response;

/**
 * @author Sharif
 *
 */
public class EmptyResponse extends Response {
	
	@Override
	protected void doBuild(Map<String, Object> context) throws Exception {
		
		prepareContext(context);

	}

}
