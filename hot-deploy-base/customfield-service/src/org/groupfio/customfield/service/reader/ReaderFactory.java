/**
 * 
 */
package org.groupfio.customfield.service.reader;

import org.groupfio.customfield.service.CustomfieldServiceConstants.ReaderType;

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
