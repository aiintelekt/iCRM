package org.fio.homeapps.export;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

/**
 * @author Mahendran T
 * @author Sharif
 * @desc Construct the file
 * */
public class CsvExporter extends Exporter {
	
	private final static String MODULE = CsvExporter.class.getName();
	
	public CsvExporter() {
		
	}
	
	private static CsvExporter instance;
	
	public static synchronized CsvExporter getInstance(){
        if(instance == null) {
            instance = new CsvExporter();
        }
        return instance;
    }
	
	@Override
	protected Map<String, Object> doExporter(Map<String, Object> context) throws Exception {
		
		Delegator delegator = (Delegator) context.get("delegator");
		List<Map<String, Object>> rows = (List<Map<String, Object>>) context.get("rows");
		List<String> headers = (List<String>) context.get("headers");
		String fileName = (String) context.get("fileName");
		String location = (String) context.get("location"); 
		String delimiter = (String) context.get("delimiter"); 
		Boolean isHeaderRequird = (Boolean) context.get("isHeaderRequird");
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			
			if(UtilValidate.isNotEmpty(rows)) {
				String data = "";
				List<String> headerFields = new ArrayList<String>();
				Set<String> fieldSet = rows.get(0).keySet();
				if(isHeaderRequird) {

					if(headers != null && headers.size() > 0) {
						headerFields.addAll(headers);
					} else {
						// get the header data from the generic value
						fieldSet.forEach(e->{
							headerFields.add(e);
						});
					}
					data = fileHeaders(headerFields, delimiter);
				}
				int noOfRows = 0;
				for(Map<String, Object> row : rows) {
					noOfRows = noOfRows+1;
					String columnValues = "";
					int i=0;
					for(String key : fieldSet) {
						String value = UtilValidate.isNotEmpty(row.get(key)) ? row.get(key).toString() : "";
						value = UtilValidate.isNotEmpty(value) && !value.equals("null") ? value : "";
						if(i == 0) {
							columnValues = columnValues.concat(value);
						} else {
							columnValues = columnValues.concat(delimiter.concat(value));
						}
						i++;
					}
					data = data.concat(columnValues) + "\n";
				}
				if(!"".equals(data) && noOfRows > 0) {
					String filePath = location+File.separatorChar+fileName;
					File file = new File(filePath);
					FileUtils.writeStringToFile(file, data, "UTF-8");
					Debug.logInfo("File Exported with "+ noOfRows +" rows", MODULE);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			
			return response;
		}
		
		response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;
	}
	
	public String fileHeaders(List<String> headers, String delimiter) {
		String headerString = "";
		if(headers != null && headers.size() > 0) {
			try {
				for(int i =0;i<headers.size();i++) {
					String header = UtilValidate.isNotEmpty((String)headers.get(i)) ? headers.get(i) : "";
					if(i == 0) {
						headerString = header;
					} else {
						headerString = headerString.concat(delimiter.concat(header));
					}
				}
				headerString = headerString.concat("\n");
			} catch (Exception e) {
				Debug.logError("Error : "+e.getMessage(), MODULE);
			}
		}
		return headerString;
	}
	
}
