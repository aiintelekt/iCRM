/**
 * 
 */
package org.fio.crm.resolver;

import org.fio.crm.resolver.ResolverConstants.ResolverType;

/**
 * @author Sharif
 *
 */
public class ResolverFactory {

	public static Resolver getResolver(ResolverType type) {
		
		Resolver resolver = null;
		
		switch (type) {
			case EMPL_POSITION:
				resolver = EmplPositionResolver.getInstance();
				break;
			default:
				break;
		}
		
		return resolver;
	}
	
}
