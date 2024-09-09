/**
 * 
 */
package org.groupfio.common.portal.resolver;

import org.groupfio.common.portal.resolver.ResolverConstants.ResolverType;

/**
 * @author Sharif
 * @author Mahendran Thanasekaran
 */
public class ResolverFactory {

	public static Resolver getResolver(ResolverType type) {
		
		Resolver resolver = null;
		
		switch (type) {
			case ESCALATION_RESOLVER:
				resolver = EscalationResolver.getInstance();
				break;
			case OPPORTUNITY_ESCALATION_RESOLVER:
				resolver = OpportunityEscalationResolver.getInstance();
				break;
			default:
				break;
		}
		
		return resolver;
	}
	
}
