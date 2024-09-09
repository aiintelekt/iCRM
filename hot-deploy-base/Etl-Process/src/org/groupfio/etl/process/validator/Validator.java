/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.util.Map;

/**
 * @author Group Fio
 *
 */
public interface Validator {

	public Map<String, Object> validate(Map<String, Object> context);
	
}
