package org.fio.homeapps.rest.validator;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sharif
 *
 */
public abstract class Validator {

	private static final Logger log = LoggerFactory.getLogger(Validator.class);
	
	private Map<String, Object> context;

	protected abstract Map<String, Object> doValidate(Map<String, Object> context) throws Exception;

	public Map<String, Object> validate(Map<String, Object> context){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = doValidate(context);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}
	
}
