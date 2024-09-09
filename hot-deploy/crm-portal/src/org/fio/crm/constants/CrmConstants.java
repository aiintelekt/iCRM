package org.fio.crm.constants;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;

public class CrmConstants {

    public static final String configResource = "crm";
    public static final String uiLabelMap = "crmUiLabels";
    
    public static final String RESPONSE_CODE = "code";
	public static final String RESPONSE_MESSAGE = "message";
	
	public static final List<String> RM_ROLES = UtilMisc.toList("DBS_RM", "DBS_PM", "DBS_LBRM", "DBS_ARM", "DBS_CL");
    
    /**
     * PartyRelationshipType constant values.
     */
    public static final class PartyRelationshipTypeConstants {
        private PartyRelationshipTypeConstants() { }
        /** Relationship from an original to a duplicated party. */
        public static final String DUPLICATED = "DUPLICATED";
        public static final String LEAD_OWNER = "LEAD_OWNER";
        /** An account or contact owner is someone who is responsible for that account. */
        public static final String RESPONSIBLE_FOR = "RESPONSIBLE_FOR";
        public static final String CONTACT_REL_INV = "CONTACT_REL_INV";
    }
    
    /**
     * RoleType constant values.
     */
    public static final class RoleTypeConstants {
        private RoleTypeConstants() { }
        /** Lead. */
        public static final String LEAD = "LEAD";
        /** Owner. */
        public static final String OWNER = "OWNER";
        /** Contact. */
        public static final String CONTACT = "CONTACT";
        /** Account. */
        public static final String ACCOUNT = "ACCOUNT";
        /** Account Team. */
        public static final String ACCOUNT_TEAM = "ACCOUNT_TEAM";
    }
    
    /**
     * SecurityGroup constant values.
     */
    public static final class SecurityGroupConstants {
        private SecurityGroupConstants() { }
        /** Permissions granted to lead owners. */
        public static final String LEAD_OWNER = "LEAD_OWNER";
        /** Permissions granted to contact owners. */
        public static final String CONTACT_OWNER = "CONTACT_OWNER";
    }
    
    /**
     * StatusItem constant values.
     */
    public static final class PartyLeadStatus {
        private PartyLeadStatus() { }
        /** Assigned. */
        public static final String LEAD_ASSIGNED = "LEAD_ASSIGNED";
        /** Qualified. */
        public static final String LEAD_QUALIFIED = "LEAD_QUALIFIED";
        /** Converted. */
        public static final String LEAD_CONVERTED = "LEAD_CONVERTED";
        /** Active. */
        public static final String LEAD_ACTIVE = "LEAD_ACTIVE";
        /** Booking. */
        public static final String LEAD_BOOKING = "LEAD_BOOKING";
        /** Scheduled. */
        public static final String LEAD_SCHEDULED = "LEAD_SCHEDULED";
    }
    
    /**
     * SecurityPermissionConstants constant values.
     */
    public static final class SecurityPermissionConstants {
         private SecurityPermissionConstants() { }
         
         /** Create a new Lead. */
         public static final String CRMSFA_LEAD_CREATE = "CRMSFA_LEAD_CREATE";
    }
    
    public static final class SecurityPermissions{
    	private SecurityPermissions(){}
    	public static final String CRMSFA_ACCOUNTS_VIEW = "CRMSFA_ACCOUNTS_VIEW"; 
    	public static final String CRMSFA_ACCOUNT_VIEW = "CRMSFA_ACCOUNT_VIEW"; 
    	public static final String CRMSFA_ACCOUNT_CREATE = "CRMSFA_ACCOUNT_CREATE"; 
    	public static final String CRMSFA_ACCOUNT_UPDATE = "CRMSFA_ACCOUNT_UPDATE"; 
    	public static final String CRMSFA_ACCOUNT_DEACTIVATE = "CRMSFA_ACCOUNT_DEACTIVATE"; 
    	public static final String CRMSFA_ACCOUNT_REASSIGN = "CRMSFA_ACCOUNT_REASSIGN"; 
    	public static final String CRMSFA_ACCOUNT_CUST_VIEW = "CRMSFA_ACCOUNT_CUST_VIEW"; 
    	public static final String CRMSFA_ACCOUNT_CUST_CREATE = "CRMSFA_ACCOUNT_CUST_CREATE"; 
    	public static final String CRMSFA_ACCOUNT_CUST_UPDATE = "CRMSFA_ACCOUNT_CUST_UPDATE"; 
    	public static final String CRMSFA_ACCOUNT_CUST_DELETE = "CRMSFA_ACCOUNT_CUST_DELETE"; 
    }
    
    public static final class PartyStatus {
        private PartyStatus() { }
        /** Disabled. */
        public static final String PARTY_DISABLED = "PARTY_DISABLED";
        /** Enabled. */
        public static final String PARTY_ENABLED = "PARTY_ENABLED";
    }
    
    public static final class ValidationAuditType {
        private ValidationAuditType() { }
        public static final String VAT_LEAD_IMPORT = "VAT_LEAD_IMPORT";
        public static final String VAT_RM_REASSIGN = "VAT_RM_REASSIGN";
        public static final String VAT_LEAD_DEDUP = "VAT_LEAD_DEDUP";
    }
}
