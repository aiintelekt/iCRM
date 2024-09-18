/**
 * 
 */
package org.fio.homeapps.util;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Sharif
 *
 */
public class UtilXml extends org.ofbiz.base.util.UtilXml {

	public static final String MODULE = UtilXml.class.getName();
	
	public static List<GenericValue> getEntityList(Delegator delegator, Document doc, String entityName) {
		List<GenericValue> entityList = new ArrayList<GenericValue>();
		
		try {
			
			NodeList nList = doc.getElementsByTagName(entityName);
			
			for (int index = 0; index < nList.getLength(); index++) {
				Node nNode = nList.item(index);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if (UtilValidate.isNotEmpty(eElement)) {
						GenericValue entity = delegator.makeValue(eElement);
						entityList.add(entity);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
		}
		
		return entityList;
	}
	
}
