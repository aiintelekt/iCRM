/**
 * 
 */
package org.groupfio.etl.process.processor;

import java.util.Map;

/**
 * @author Group Fio
 *
 */
public abstract class AbstractProcessor {

	protected abstract Map<String, Object> doProcess(Map<String, Object> context) throws Exception;
	
	public Map<String, Object> process(Map<String, Object> context) throws Exception {
		return doProcess(context);
	}
	
}
