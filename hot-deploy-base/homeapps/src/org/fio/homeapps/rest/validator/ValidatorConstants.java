package org.fio.homeapps.rest.validator;

/**
 * @author Sharif
 *
 */
public class ValidatorConstants {

	public enum ValidatorType {
		ACCESS_TOKEN_VALIDATOR("ACCESS_TOKEN_VALIDATOR"),
		SERVICE_EXECUTOR_VALIDATOR("SERVICE_EXECUTOR_VALIDATOR"),
		;
		
		public String value;
		
		private ValidatorType(String value) {
			this.value = value;
		}
	}
	
}
