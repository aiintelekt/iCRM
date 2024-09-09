/**
 * 
 */
package org.fio.homeapps.util;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * @author Sharif
 *
 */
public class UtilFreemarker {
	
	private static final Logger log = LoggerFactory.getLogger(UtilFreemarker.class);
	
	private static Configuration stringLoaderConf = new Configuration(Configuration.VERSION_2_3_22);
	private static StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
	static {
		stringLoaderConf = new Configuration(Configuration.VERSION_2_3_22);
		stringLoaderConf.setDefaultEncoding("UTF-8");
		stringLoaderConf.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		stringLoaderConf.setTemplateLoader(stringTemplateLoader);
    }
	
	public static String renderPreview(String template, Map<String, Object> context) {
		Map<String, Object> dataContext = (Map<String, Object>) context.get("dataContext");
		dataContext.put("nodeId", "9999999999999");
		String previewName = UUID.randomUUID().toString();
		stringTemplateLoader.putTemplate(previewName, template);
		String result = renderTemplate(previewName, dataContext);
		//System.out.println("result> "+result);
		//stringTemplateLoader.removeTemplate(previewName);
		
		/*try {
			templateConfiguration.removeTemplateFromCache(previewName);
		} catch (IOException e) {
			LOG.debug("Couldn't remove temporary template from cache: " + e.getMessage());
		}*/
		return result;
	}
	
	public static String renderTemplate (String id, Map<String, Object> context) {
		try {
			Template temp = stringLoaderConf.getTemplate(id);
			Writer writer = new StringWriter();
			temp.process(context, writer);
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
