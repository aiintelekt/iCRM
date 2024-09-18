/**
 * 
 */
package org.groupfio.custom.field.writer;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.groupfio.custom.field.ResponseCodes;
import org.groupfio.custom.field.constants.CustomFieldConstants;
import org.groupfio.custom.field.constants.CustomFieldConstants.ReaderType;
import org.groupfio.custom.field.reader.Reader;
import org.groupfio.custom.field.reader.ReaderFactory;
import org.groupfio.custom.field.util.DataUtil;
import org.groupfio.custom.field.util.ParamUtil;
import org.groupfio.custom.field.util.ResponseUtils;
import org.groupfio.custom.field.util.XmlUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Sharif
 *
 */
public class EntityWriter extends Writer {
	
	private static String MODULE = Writer.class.getName();
	
	private static EntityWriter instance;
	
	public static synchronized EntityWriter getInstance(){
        if(instance == null) {
            instance = new EntityWriter();
        }
        return instance;
    }

	@Override
	protected Map<String, Object> doWrite(Map<String, Object> context) throws Exception {

		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			
			context.put("searchEntityName", "CustomFieldSeg${groupId}");
			context.put("sqlTemplateKey", "dynamic.entity.model.seg.sql.template");
			writeEntity(context);
			
			String historicalCapture = ParamUtil.getString(context, "historicalCapture");
			if (UtilValidate.isNotEmpty(historicalCapture) && historicalCapture.equals("Y")) {
				context.put("searchEntityName", "CustomFieldSegTrk${groupId}");
				context.put("sqlTemplateKey", "dynamic.entity.model.seg.trk.sql.template");
				writeEntity(context);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
			
			response.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(CustomFieldConstants.RESPONSE_MESSAGE, e.getMessage());
			
			return response;
			
		}
		
		response.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;

	}
	
	private void writeEntity (Map<String, Object> context) throws Exception {
			
		String entityFilePath = UtilProperties.getPropertyValue(CustomFieldConstants.configResource, "dynamic.entity.path");
		String entityModelFilePath = UtilProperties.getPropertyValue(CustomFieldConstants.configResource, "dynamic.entity.model.template.path");
		
		Map<String, Object> readerContext = new HashMap<String, Object>();
		
		readerContext.put("entityModelFilePath", entityModelFilePath);
		readerContext.put("searchEntityName", context.get("searchEntityName"));
		
		Reader reader = ReaderFactory.getReader(ReaderType.ENTITY);
		
		Map<String, Object> readerResult = reader.read(readerContext);
		
		if (ResponseUtils.isSuccess(readerResult)) {
			
			String entityXml = (String) readerResult.get("entityXml");
			
			if(UtilValidate.isNotEmpty(entityXml)) {
				
				String groupId = DataUtil.getFormatedValue(context.get("groupId").toString());
				
				entityXml = entityXml.replace("${groupId}", groupId);
				
				String dynamicEntityModelPath = UtilProperties.getPropertyValue(CustomFieldConstants.configResource, "dynamic.entity.model.path");
				
				if (UtilValidate.isNotEmpty(dynamicEntityModelPath)) {
					
					Document document = UtilXml.readXmlDocument(new FileInputStream(dynamicEntityModelPath), dynamicEntityModelPath);
					
					Element rootElt = document.getDocumentElement();
					
					rootElt.appendChild(document.createCDATASection(entityXml));
					String entityModelXml = XmlUtil.toXml(rootElt);
					
					entityModelXml = entityModelXml.replace("<![CDATA[", "");
					entityModelXml = entityModelXml.replace("]]>", "");
					
					FileUtil.writeString(entityFilePath, "dynamic-entitymodel.xml", entityModelXml);
					
					// Sql script writing [start]
					
					String modelSqlPath = UtilProperties.getPropertyValue(CustomFieldConstants.configResource, "dynamic.entity.sql.path");
					
					//String entityModelSql = UtilProperties.getPropertyValue(CustomFieldConstants.configResource, "dynamic.entity.model.seg.sql.template");
					String entityModelSql = UtilProperties.getPropertyValue(CustomFieldConstants.configResource, context.get("sqlTemplateKey").toString());
					
					entityModelSql = entityModelSql.replace("${groupId}", groupId.toLowerCase());
					
					entityModelSql = FileUtil.readString("UTF-8", new File(modelSqlPath+"dynamic-entitymodel.sql")) + "\r\t" + entityModelSql; 
					
					System.out.println(entityModelSql);
					
					FileUtil.writeString(modelSqlPath, "dynamic-entitymodel.sql", entityModelSql);
					
					// Sql script writing [end]
					
				}
				
			}
			
		}
		
	}

}
