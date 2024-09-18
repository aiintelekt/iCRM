package org.fio.crm.export;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;

/**
 * @author Sharif
 *
 */
public abstract class Exporter {

	private static String MODULE = Exporter.class.getName();
	
	private Map<String, Object> context;

	protected abstract Map<String, Object> doExporter(Map<String, Object> context) throws Exception;

	public Map<String, Object> exporter(Map<String, Object> context){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = doExporter(context);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return result;
	}
	
}
