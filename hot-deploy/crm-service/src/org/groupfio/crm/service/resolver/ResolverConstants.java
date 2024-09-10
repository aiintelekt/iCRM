/**
 * 
 */
package org.groupfio.crm.service.resolver;

/**
 * @author Sharif
 *
 */
public class ResolverConstants {

	public enum ResolverType {
		ESCALATION_RESOLVER("ESCALATION_RESOLVER"),
		TAT_RESOLVER("TAT_RESOLVER"),
		ACTIVITY_ESCALATION_RESOLVER("ACTIVITY_ESCALATION_RESOLVER"),
		ACTIVITY_TAT_RESOLVER("ACTIVITY_TAT_RESOLVER"),
		SLA_TAT_RESOLVER("SLA_TAT_RESOLVER"),
		;
		
		protected String value;
		
		private ResolverType(String value) {
			this.value = value;
		}
	}
	
}
