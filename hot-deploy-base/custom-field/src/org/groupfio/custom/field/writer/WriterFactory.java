/**
 * 
 */
package org.groupfio.custom.field.writer;

import org.groupfio.custom.field.constants.CustomFieldConstants.WriterType;

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
