/**
 * 
 */
package org.fio.homeapps.export.resolver;

import org.fio.homeapps.export.resolver.ResolverConstants.ResolverType;

/**
 * @author Sharif
 */
public class ResolverFactory {

	public static Resolver getResolver(ResolverType type) {
		
		Resolver resolver = null;
		
		switch (type) {
			case EXP_DATA_RESOLVER:
				resolver = DataResolver.getInstance();
				break;
			default:
				break;
		}
		
		return resolver;
	}
	
}
