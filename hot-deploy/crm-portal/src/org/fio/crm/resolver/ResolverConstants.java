/**
 * 
 */
package org.fio.crm.resolver;

/**
 * @author Sharif
 *
 */
public class ResolverConstants {

	public enum ResolverType {
		EMPL_POSITION("EMPL_POSITION"),
		;

		protected String value;

		private ResolverType(String value) {
			this.value = value;
		}
		
	}
	
}
