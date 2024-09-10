/**
 * 
 */
package org.fio.crm.writer;

/**
 * @author Sharif
 *
 */
public final class WriterFactory {
	
	private static final ValidationAuditWriter VALIDATION_AUDIT_WRITER = new ValidationAuditWriter();
	
	public static ValidationAuditWriter getValidationAuditWriter () {
		return VALIDATION_AUDIT_WRITER;
	}
	
}
