/**
 * 
 */
package org.fio.homeapps.export;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.export.ExportConstants.ExportType;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class ExportUtil {

	public static String getFileExtension(String exportType) {
		String fileExtension = "";
		if (UtilValidate.isNotEmpty(exportType)) {
			
			switch (exportType) {
			case ExportType.EXPORT_TYPE_CSV:
				fileExtension = ".csv";
				break;

			case ExportType.EXPORT_TYPE_EXCEL:
				fileExtension = ".xls";
				break;
			}
		}
		return fileExtension;
	}
	
	public static String getContentType(String exportType) {
		String contentType = "";
		if (UtilValidate.isNotEmpty(exportType)) {
			
			switch (exportType) {
			case ExportType.EXPORT_TYPE_CSV:
				contentType = "text/csv";
				break;

			case ExportType.EXPORT_TYPE_EXCEL:
				contentType = "application/vnd.ms-excel";
				break;
			case ExportType.EXPORT_TYPE_XML:
				contentType = "text/xml, application/xml";
				break;

			}
		}
		return contentType;
	}
	
	public static Map<String, Object> populateExportData(Delegator delegator, Map<String, Object> context) {
		String expFileTemplateId = (String) context.get("expFileTemplateId");
		String delimiter = (String) context.get("delimiter");
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) context.get("dataList");
		List<Map<String, Object>> resultList = FastList.newInstance();
		List<String> headers = new ArrayList<>();
		
		Map<String, Object> response = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(expFileTemplateId)) {
				GenericValue fileTpl = EntityQuery.use(delegator).from("FileTemplate")
						.where("fileTemplateId", expFileTemplateId).queryFirst();
				if (UtilValidate.isNotEmpty(fileTpl)) {
					delimiter = UtilValidate.isNotEmpty(fileTpl.getString("delimeter")) ? fileTpl.getString("delimeter") : delimiter;
					List<GenericValue> fieldList = EntityQuery.use(delegator).from("FileTemplateField")
							.where("fileTemplateId", expFileTemplateId, "isHide", "N").orderBy("sequenceNumber").queryList();
					if (UtilValidate.isNotEmpty(fieldList)) {
						for (GenericValue field : fieldList) {
							headers.add(field.getString("fieldName"));
						}
						
						for (Map<String, Object> data : dataList) {
							Map<String, Object> result = new LinkedHashMap<String,Object>();
							
							for (GenericValue field : fieldList) {
								Object value = data.get(field.getString("entityFieldName"));
								value = UtilValidate.isNotEmpty(value) ? value : field.getString("defaultValue");
								result.put(field.getString("fieldName"), value);
							}
							resultList.add(result);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.put("resultList", resultList);
		response.put("headers", headers);
		response.put("delimiter", delimiter);
		return response;
	}
	
}
