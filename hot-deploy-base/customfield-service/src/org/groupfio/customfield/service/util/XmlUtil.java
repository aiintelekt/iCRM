/**
 * 
 */
package org.groupfio.customfield.service.util;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

/**
 * @author Sharif
 *
 */
public class XmlUtil {

	public static String toXml (Element element) {
		
		try {
			DOMSource domSource = new DOMSource(element);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty("omit-xml-declaration", "yes");
			StringWriter sw = new StringWriter();
			StreamResult sr = new StreamResult(sw);
			transformer.transform(domSource, sr);
			
			return sw.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
