package org.groupfio.activity.portal;

/**
 * @author Sharif Ul Islam
 *
 */
public class ActivityPortalConstants {

	// Resource bundles	
    public static final String configResource = "account-portal";
    public static final String uiLabelMap = "account-portalUiLabels";
    
	public static final class SourceInvoked {
        private SourceInvoked() { }
        public static final String API = "API";
        public static final String PORTAL = "PORTAL";
        public static final String UNKNOWN = "UNKNOWN";
    }
	
	public static final class LookupType {
        private LookupType() { }
        public static final String STATIC_DATA = "STATIC_DATA";
        public static final String PICKER_DATA = "PICKER_DATA";
        public static final String DYNAMIC_DATA = "DYNAMIC_DATA";
    }
	
	public static final class FieldType {
        private FieldType() { }
        public static final String TEXT = "TEXT";
        public static final String DATE = "DATE";
        public static final String DROPDOWN = "DROPDOWN";
        public static final String PICKER = "PICKER";
        public static final String NUMBER = "NUMBER";
        public static final String DATE_TIME = "DATE_TIME";
        public static final String RADIO = "RADIO";
        public static final String TEXT_AREA = "TEXT_AREA";
    }
	
	public static final class LayoutType {
        private LayoutType() { }
        public static final String ONE_COLUMN = "1C";
        public static final String TWO_COLUMN = "2C";
        public static final String THREE_COLUMN = "3C";
    }
	
	public static final class ActivitySearchType {
        private ActivitySearchType() { }
        public static final String MY_ACTIVITY = "my-activity";
        public static final String MY_OPEN_ACTIVITY = "my-open-activity";
        public static final String MY_COMPLETED_ACTIVITY = "my-completed-activity";
    }
}
