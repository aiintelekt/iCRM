package org.fio.crm.export;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;

public class ExportWrapper {

    public static Map<String, Object> getData(List<String> selectedColumns , String propName , String propValue , Map<String, Object> exportRow) {
    	
        if ((UtilValidate.isNotEmpty(selectedColumns) && selectedColumns.contains(propName)) || UtilValidate.isEmpty(selectedColumns)) {
            exportRow.put(propName, propValue);
        }
        
        return exportRow;
    }
    
}
