/**
 * 
 */
package org.groupfio.etl.process.service;

import org.groupfio.etl.process.service.impl.CommonImportServiceImpl;

/**
 * @author Group Fio
 *
 */
public class ServiceFactory {

	public static final CommonImportServiceImpl COMMON_IMPORT_SERVICE_IMPL = new CommonImportServiceImpl();
	
	public static CommonImportServiceImpl getCommonImportService () {
		return COMMON_IMPORT_SERVICE_IMPL;
	}
	
}
