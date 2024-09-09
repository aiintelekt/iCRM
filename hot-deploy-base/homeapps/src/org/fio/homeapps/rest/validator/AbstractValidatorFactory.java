/**
 * 
 */
package org.fio.homeapps.rest.validator;

/**
 * @author Sharif
 *
 */
public abstract class AbstractValidatorFactory {

	public abstract Validator getValidator(String type);
	
}
