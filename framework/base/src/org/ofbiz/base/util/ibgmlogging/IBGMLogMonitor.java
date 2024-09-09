package org.ofbiz.base.util.ibgmlogging;

public class IBGMLogMonitor {
	
	private static final I3beLogService LOGGER = LogFactory.getLog(IBGMLogMonitor.class);
	
	public static void printIBGMLogging (IBGMLogVo ibgmLogVo) {
		LOGGER.debug (JsonUtils.convertRequestJson (ibgmLogVo));
	}

}