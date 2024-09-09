/**
 * 
 */
package org.groupfio.common.portal.extractor.constants;

/**
 * @author Sharif
 *
 */
public class ExtractorConstants {

	public static final class ExtractType {
		public ExtractType() { }
        
        public static final String EXTRACT_EMAIL_DATA = "EMAIL_DATA";
        public static final String EXTRACT_CAMPAIGN_DATA = "CAMPAIGN_DATA";
        public static final String EXTRACT_CMP_EVENT_DATA = "CMP_EVENT_DATA";
    }
	
	public static final class ValueOverrideType {
		public ValueOverrideType() { }
        
        public static final String GLOBAL_OVERRIDE = "CFVO_GLOBAL";
        public static final String PARTY_OVERRIDE = "CFVO_PARTY";
        public static final String NO_OVERRIDE = "CFVO_NO";
    }
	
}
