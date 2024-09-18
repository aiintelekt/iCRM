package org.groupfio.crm.service.constants;

/**
 * @author Manjesh
 * @author Sharif Ul Islam
 *
 */
public class MakerCheckerConstants {

    public static final String configResource = "prcingPortal";
    public static final String uiLabelMap = "pricingPortalUiLabels";
    
    public static final String RESPONSE_CODE = "code";
	public static final String RESPONSE_MESSAGE = "message";
	

    /**
     * MAKERCHECKER constant values.
     */
    
    public static final class ApprovalStatus {
        private ApprovalStatus() { }
        public static final String PENDING = "PENDING";
        public static final String APPROVED = "APPROVED";
        public static final String REJECTED = "REJECTED";
        public static final String IGNORED = "IGNORED";
		public static final String EXPIRED = "EXPIRED";
    }
    
    public static final class ModeOfAction {
        private ModeOfAction() { }
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String IGNORED = "IGNORED";
    }
}
