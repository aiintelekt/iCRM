/**
 * 
 */
package org.groupfio.dyna.screen.resolver;

import org.groupfio.dyna.screen.resolver.ResolverConstants.ResolverType;

/**
 * @author Sharif
 *
 */
public class ResolverFactory {

	public static Resolver getResolver(ResolverType type) {
		
		Resolver resolver = null;
		
		/*switch (type) {
			case ESCALATION_RESOLVER:
				resolver = EscalationResolver.getInstance();
				break;
			default:
				break;
		}*/
		
		return resolver;
	}
	
}
