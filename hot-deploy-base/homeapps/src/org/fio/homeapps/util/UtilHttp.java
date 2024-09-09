/**
 * 
 */
package org.fio.homeapps.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

/**
 * @author Sharif
 *
 */
public class UtilHttp extends org.ofbiz.base.util.UtilHttp {
	
	public static final String module = UtilHttp.class.getName();

	public static Map<String, Object> getCombinedMap(HttpServletRequest request) {
		Map<String, Object> combinedMap = new HashMap<String, Object>();
		combinedMap = getCombinedMap(request, null);
		combinedMap.putAll(getMultiplartParameterMap(request));
        return combinedMap;
    }
	
	public static Map<String, Object> getMultiplartParameterMap(HttpServletRequest request) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Map<String, Object> multiPartMap = new HashMap<String, Object>();
        try {
			if(ServletFileUpload.isMultipartContent(request)){
				List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				int count = 0;
				for (FileItem item : multiparts) {
					if (item.isFormField()) {
						String fName = item.getFieldName();
						//String fValue = item.getString();
						String fValue = item.getString("UTF-8");
						paramMap.put(fName, fValue);
					} else if (!item.isFormField()) {
						multiPartMap.put("uploadedFile_"+(count++), item);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        paramMap.put("multiPartMap", multiPartMap);
        return paramMap;
    }
	
	public static String getSafeFileName(String fileName) {
		try {
			if (UtilValidate.isNotEmpty(fileName)) {
				fileName = fileName.replaceAll("[\r\n]", "");
				return URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), module);
		}
        return fileName;
    }
	
}
