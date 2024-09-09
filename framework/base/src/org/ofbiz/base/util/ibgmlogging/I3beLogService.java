package org.ofbiz.base.util.ibgmlogging;

public interface I3beLogService {

	boolean isInfoEnabled ();

	public void info (String loggerMessage);

	public void info (String loggerMessage, String loggerValue);

	public void info (String loggerMessage, Object loggerValue);

	public void info (String userId, String mode, String transactionId, String loggerMessage, String loggerValue, Exception e);

	public void debug (String loggerMessage);

	public void debug (String loggerMessage, String loggerValue);

	public void debug (String userId, String mode, String transactionId, String loggerMessage, String loggerValue, Exception e);

	public void error (String loggerMessage);

	public void error (String loggerMessage, String loggerValue);

	public void error (String userId, String mode, String transactionId, String loggerMessage, String loggerValue, Exception e);

	public void error (String loggerMessage, Exception e);

	public void error (Exception exception);
}
