/**
 * 
 */
package org.groupfio.custom.field.reader;

import org.groupfio.custom.field.constants.CustomFieldConstants.ReaderType;

/**
 * @author Sharif
 *
 */
public class ReaderFactory {

	public static Reader getReader(ReaderType type) {
		
		Reader reader = null;
		
		switch (type) {
			case ENTITY:
				reader = new EntityReader();
				break;
			default:
				break;
		}
		
		return reader;
	}
	
}
