/**
 * 
 */
package org.fio.homeapps.rest.client.constants;

/**
 * @author Sharif
 *
 */
public class RestClientConstants {

	public static class OrderMethod {
		
        private OrderMethod() { }
        
        public static final String METHOD_TYPE_RECEIVED = "received";
        public static final String METHOD_TYPE_SHIPPED = "reef_order_confirmation_shipped";
        public static final String METHOD_TYPE_CANCELED = "reef_order_confirmation_canceled";
        public static final String METHOD_TYPE_REJECTED = "reef_order_confirmation_rejected";
        
    }
	
}
