/**
 * 
 */
package org.fio.homeapps.export.resolver;

/**
 * @author Sharif
 *
 */
public class ResolverConstants {

	public enum ResolverType {
		EXP_DATA_RESOLVER("EXP_DATA_RESOLVER"),
		;
		
		protected String value;
		
		private ResolverType(String value) {
			this.value = value;
		}
	}
	
}
