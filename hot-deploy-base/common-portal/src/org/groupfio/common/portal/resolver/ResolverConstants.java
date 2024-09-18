/**
 * 
 */
package org.groupfio.common.portal.resolver;

/**
 * @author Sharif
 *
 */
public class ResolverConstants {

	public enum ResolverType {
		ESCALATION_RESOLVER("ESCALATION_RESOLVER"),
		OPPORTUNITY_ESCALATION_RESOLVER("OPPORTUNITY_ESCALATION_RESOLVER")
		;
		
		protected String value;
		
		private ResolverType(String value) {
			this.value = value;
		}
	}
	
}
