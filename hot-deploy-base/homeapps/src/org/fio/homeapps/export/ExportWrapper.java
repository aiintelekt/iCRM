package org.fio.homeapps.export;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;

/**
 * @author Sharif
 *
 */
public class ExportWrapper {

    public static Map<String, Object> getData(List<String> selectedColumns, String propName, String propValue, Map<String, Object> exportRow) {
    	
        if ((UtilValidate.isNotEmpty(selectedColumns) && selectedColumns.contains(propName)) || UtilValidate.isEmpty(selectedColumns)) {
            exportRow.put(propName, propValue);
        }
        
        return exportRow;
    }
    
    public static Map<String, Map<String, Object>> getData(List<String> selectedColumns, String propName, String propValue, int fromIndex, int toIndex, Map<String, Map<String, Object>> exportRow) {
    	
        if ((UtilValidate.isNotEmpty(selectedColumns) && selectedColumns.contains(propName)) || UtilValidate.isEmpty(selectedColumns)) {
            Map<String, Object> value = new LinkedHashMap<String, Object>();
            value.put("propValue", propValue);
            value.put("fromIndex", fromIndex);
            value.put("toIndex", toIndex);
        	exportRow.put(propName, value);
        }
        
        return exportRow;
    }
    
}
