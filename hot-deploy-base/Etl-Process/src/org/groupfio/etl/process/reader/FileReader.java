/**
 * 
 */
package org.groupfio.etl.process.reader;

import java.util.List;
import java.util.Map;

/**
 * @author Group Fio
 *
 */
public interface FileReader {

	public List<Map<String, Object>> read(Map<String, Object> context);
	
}
