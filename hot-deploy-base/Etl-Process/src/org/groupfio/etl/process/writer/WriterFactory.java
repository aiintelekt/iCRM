/**
 * 
 */
package org.groupfio.etl.process.writer;

/**
 * @author Group Fio
 *
 */
public final class WriterFactory {

	private static final LogWriter LOG_WRITER = new LogWriter();
	private static final ValidationAuditWriter VALIDATION_AUDIT_WRITER = new ValidationAuditWriter();
	
	public static LogWriter getLogWriter () {
		return LOG_WRITER;
	}
	
	public static ValidationAuditWriter getValidationAuditWriter () {
		return VALIDATION_AUDIT_WRITER;
	}
	
}
