package org.fio.homeapps.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sharif
 *
 */
public class UserAuditConstants {

    public static final String configResource = "homeapps-config";
    public static final String uiLabelMap = "HomeappsUiLabels";
    
    public static final String RESPONSE_CODE = "code";
	public static final String RESPONSE_MESSAGE = "message";
	
	public static final Map<String, String> MANDATORY_FIELD_CONFIG =
			Collections.unmodifiableMap(new HashMap<String, String>() {
				{
					put("uomId", "Currency Code");
					put("uomIdTo", "To Currency");
					
					put("ratesViewdate", "Date");
					put("location", "Location");
					put("benchmarkName", "Benchmark");
					put("rscurrency", "Currency");
					
					put("currency", "Currency");
					put("effectiveDate", "Effective Date");
					put("productId", "Product Id");
					put("customerId", "Customer Id");
					put("counterCustomerId", "Counter Customer Id");
					put("customerName", "Customer Name");
					put("counterCustomerName", "Counter Customer Name");
					
				}
			});

    public static final class ApprovalStatus {
        private ApprovalStatus() { }
        public static final String PENDING = "PENDING";
        public static final String APPROVED = "APPROVED";
        public static final String REJECTED = "REJECTED";
        public static final String IGNORED = "IGNORED";
		public static final String EXPIRED = "EXPIRED";
    }
    
}
