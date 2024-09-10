/**
 * 
 */
package org.groupfio.custom.field.validator;

import java.util.Map;

/**
 * @author Sharif
 *
 */
public interface Validator {

	public Map<String, Object> validate(Map<String, Object> context);
	
}
