/**
 * 
 */
package org.groupfio.crm.service.resolver;

import org.groupfio.crm.service.resolver.ResolverConstants.ResolverType;

/**
 * @author Sharif
 *
 */
public class ResolverFactory {

	public static Resolver getResolver(ResolverType type) {
		
		Resolver resolver = null;
		
		switch (type) {
			case ESCALATION_RESOLVER:
				resolver = EscalationResolver.getInstance();
				break;
			case TAT_RESOLVER:
				resolver = TatResolver.getInstance();
				break;
			case ACTIVITY_ESCALATION_RESOLVER:
				resolver = ActivityEscalationResolver.getInstance();
				break;
			case ACTIVITY_TAT_RESOLVER:
				resolver = ActivityTatResolver.getInstance();
				break;
			case SLA_TAT_RESOLVER:
				resolver = SlaTatResolver.getInstance();
				break;
			default:
				break;
		}
		
		return resolver;
	}
	
}
