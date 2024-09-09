package org.groupfio.etl.process.wrapper;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;

/**
 * @author Sharif
 *
 */
public class ExportWrapper {

    public static Map<String, Object> getData(List<String> selectedColumns , String propName , String propValue , Map<String, Object> exportRow) {
        if (UtilValidate.isNotEmpty(selectedColumns) && selectedColumns.contains(propName)) {
            exportRow.put(propName, propValue);
        }
        return exportRow;
    }
}
