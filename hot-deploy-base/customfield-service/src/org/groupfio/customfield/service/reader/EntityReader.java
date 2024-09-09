/**
 * 
 */
package org.groupfio.customfield.service.reader;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.customfield.service.util.XmlUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Sharif
 *
 */
public class EntityReader extends Reader {
	
	private static String MODULE = Reader.class.getName();
	
	private static EntityReader instance;
	
	public static synchronized EntityReader getInstance(){
        if(instance == null) {
            instance = new EntityReader();
        }
        return instance;
    }

	@Override
	protected Map<String, Object> doRead(Map<String, Object> context) throws Exception {

		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			
			String entityModelFilePath = ParamUtil.getString(context, "entityModelFilePath");
			String searchEntityName = ParamUtil.getString(context, "searchEntityName");
			
			if (UtilValidate.isNotEmpty(entityModelFilePath) && UtilValidate.isNotEmpty(searchEntityName)) {
				
				File entityXmlFile = new File(entityModelFilePath);
				
				if (entityXmlFile.exists()) {
					
					Document document = UtilXml.readXmlDocument(new FileInputStream(entityModelFilePath), entityModelFilePath);
					
					Element rootElt = document.getDocumentElement();
					
					List<? extends Element> messageElementList = UtilXml.childElementList(rootElt, "entity");
			        if (UtilValidate.isNotEmpty(messageElementList)) {
			            for (Iterator<? extends Element> i = messageElementList.iterator(); i.hasNext();) {
			                Element entityElement = i.next();
			                	
		                	String entityName = UtilXml.elementAttribute(entityElement, "entity-name", null);
		                	
		                	if (UtilValidate.isNotEmpty(entityName) && searchEntityName.equals(entityName)) {
		                		
		                		String entityXml = XmlUtil.toXml(entityElement);
		                		
			                    response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			                    
			                    response.put("entityXml", entityXml);
		                		
			                    return response;
		                	}
		                	
			            }
			        }
					
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
			
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			
			return response;
			
		}
		
		response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;

	}

}
