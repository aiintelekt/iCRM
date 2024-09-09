/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.util.Map;

/**
 * @author Sharif
 *
 */
public interface Data {

	public Map<String, Object> retrieve(Map<String, Object> context);
	
}
