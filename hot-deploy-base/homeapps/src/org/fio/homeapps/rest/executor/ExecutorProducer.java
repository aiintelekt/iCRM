/**
 * 
 */
package org.fio.homeapps.rest.executor;

import org.fio.homeapps.rest.executor.ExecutorConstants.ExecutorType;

/**
 * @author Sharif
 *
 */
public class ExecutorProducer {

	public static Executor getExecutor(ExecutorType type) {
		Executor executor = null;
		
		switch (type) {
			case SERVICE_EXECUTOR:
				executor = ServiceExecutor.getInstance();
				break;
				
			default:
				break;
		}
		
		return executor;
	}

}
