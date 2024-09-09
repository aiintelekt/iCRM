/**
 * 
 */
package org.groupfio.customfield.service.writer;

import org.groupfio.customfield.service.CustomfieldServiceConstants.WriterType;

/**
 * @author Sharif
 *
 */
public class WriterFactory {

	public static Writer getWriter(WriterType type) {
		
		Writer writer = null;
		
		switch (type) {
			case ENTITY:
				writer = new EntityWriter();
				break;
			default:
				break;
		}
		
		return writer;
	}
	
}
