package org.ofbiz.base.util.ibgmlogging;

public class LogFactory {

	public static I3beLogService getLog (Class<?> clazz) {
		return new LogWrapperServiceImpl (clazz);
	}
}
