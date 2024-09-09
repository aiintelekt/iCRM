/**
 * 
 */
package org.groupfio.etl.process.uploader;

import java.util.Map;

/**
 * @author Group Fio
 *
 */
public interface FileUploader {

	public Map<String, Object> upload (Map<String, Object> context);
	
}
