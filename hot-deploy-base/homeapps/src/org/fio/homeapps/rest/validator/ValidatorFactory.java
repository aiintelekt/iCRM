package org.fio.homeapps.rest.validator;

import org.fio.homeapps.rest.validator.ValidatorConstants.ValidatorType;

/**
 * @author Sharif
 *
 */
public class ValidatorFactory {

	public static Validator getValidator(ValidatorType type) {
		
		Validator validator = null;
		
		switch (type) {
			case ACCESS_TOKEN_VALIDATOR:
				validator = AccessTokenValidator.getInstance();
				break;
			case SERVICE_EXECUTOR_VALIDATOR:
				validator = ServiceExecutorDataValidator.getInstance();
				break;
				
			default:
				break;
		}
		
		return validator;
	}
	
}
