/**
 * 
 */
package org.fio.homeapps.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fio.homeapps.export.ExportConstants.ExportType;
import org.fio.homeapps.export.ExportConstants.ExporterType;
import org.fio.homeapps.util.UtilHttp;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

/**
 * @author Sharif
 *
 */
public class ExporterFacade {

	public static void exportReport(Map<String, Object> exportContext) {
		
		String exportType = (String) exportContext.get("exportType");
		Exporter exporter = null;
		
		switch (exportType){
		case ExportType.EXPORT_TYPE_CSV: 
			
			exporter = ExporterFactory.getExporter(ExporterType.CSV);
			
			exporter.exporter(exportContext);
			
			break;
		case ExportType.EXPORT_TYPE_EXCEL:
			
			exporter = ExporterFactory.getExporter(ExporterType.EXCEL);
			
			exporter.exporter(exportContext);
			
			break;
		}
		
	}
	
	public static String downloadReport(HttpServletRequest request, HttpServletResponse response, String filePath, String exportType) {
		FileInputStream fis = null;
		try {
			if (UtilValidate.isNotEmpty(filePath)) {
				String rootPath = filePath; 
				File file = new File(filePath);
				String fileName = file.getName();
				if (UtilValidate.isEmpty(file) || !file.exists()) {
					return "error";
				}

				if (file.getCanonicalPath().startsWith(new File(rootPath).getCanonicalPath())) {
					fis = new FileInputStream(file);
					// System.out.println("file inputtttt "+fis);
					byte b[];
					int x = fis.available();
					b = new byte[x];
					fis.read(b);

					response.setContentType(ExportUtil.getContentType(exportType));
					
					response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
					OutputStream os = response.getOutputStream();
					os.write(b);
					os.flush();
					os.close();
					fis.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logInfo("Eception is ::" + e.getMessage(), "");
			return "error";
		}
		return "success";
	}
	
}
