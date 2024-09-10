package org.fio.homeapps.rest.executor;

/**
 * @author Sharif
 *
 */
public class ExecutorConstants {

	public enum ExecutorType {
		SERVICE_EXECUTOR("SERVICE_EXECUTOR"),
		;
		
		public String value;
		
		private ExecutorType(String value) {
			this.value = value;
		}
	}
	
}
