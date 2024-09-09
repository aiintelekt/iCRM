/**
 * 
 */
package org.groupfio.etl.process.writer;

import java.util.Map;

/**
 * @author Group Fio
 *
 */
public interface Writer {

	public Map<String, Object> write(Map<String, Object> context);
	
}
