/**
 * 
 */
package org.groupfio.dyna.screen.resolver;

/**
 * @author Sharif
 *
 */
public class ResolverConstants {

	public enum ResolverType {
		ESCALATION_RESOLVER("ESCALATION_RESOLVER"),
		;
		
		public String value;
		
		private ResolverType(String value) {
			this.value = value;
		}
	}
	
}
