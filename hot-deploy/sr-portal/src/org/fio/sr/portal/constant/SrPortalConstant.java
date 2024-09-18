package org.fio.sr.portal.constant;

public class SrPortalConstant {
	private static final String MODULE = SrPortalConstant.class.getName();
	private SrPortalConstant() {}
	public static final String PRIMARY_PARTY_ROLE = "PRIMARY_PARTY_ROLE";
	
	public static final class DateTimeTypeConstant {
		private DateTimeTypeConstant() {}
		public static final String TIMESTAMP = "timestamp";
		public static final String DATE = "date";
		public static final String SQL_DATE = "sqlDate";
		public static final String UTIL_DATE = "utilDate";
		public static final String STRING = "string";
	}
	
	public static final class UserCreatePermission {
		private UserCreatePermission() {}
		public static final String CREATE = "USER_CREATE_ADMIN";
		public static final String DELETE = "USER_DELETE_ADMIN";
		public static final String UPDATE = "USER_UPDATE_ADMIN";
	}
	public static final class ApiRequestConstants {
		private ApiRequestConstants() {}
		public static final String CREATE_IA = "createInteractiveActivity";
		public static final String FIND_IA = "findInteractiveActivity";
		public static final String UPDATE_IA = "updateInteractiveActivity";
		public static final String GET_DETAIL_IA = "getDetailInteractiveActivity";
	}
	public static final class CustRequestAssocConstants {
		private CustRequestAssocConstants() {}
		public static final String SR_TYPE = "SRTYPE";
		public static final String SR_Category = "SRCategory";
		public static final String SR_SubCategory = "SRSubCategory";
	}
	public static final class SecurityType{
		private SecurityType() {}
		public static final String OPS_SECURITY = "OPS_SECURITY";
		public static final String UI_SECURITY = "UI_SECURITY";
	}
	public static final class EntityOperations{
		private EntityOperations() {}
		public static final String CREATE = "Create";
		public static final String READ = "Read";
		public static final String WRITE = "Write";
		public static final String DELETE = "Delete";
		public static final String APPEND = "Append";
		public static final String APPEND_TO= "Append To";
		public static final String ASSIGN = "Assign";
		public static final String SHARE = "Share";
	}
	public static final class AccessLevel{
		private AccessLevel() {}
		public static final String LEVEL1 = "L1";
		public static final String LEVEL2 = "L2";
		public static final String LEVEL3 = "L3";
		public static final String LEVEL4 = "L4";
		public static final String LEVEL5 = "L5";
		public static final String LEVEL6 = "L6";	
		public static final String YES = "Y";
		public static final String NO = "N";
		public static final String ALL = "A";
	}
	public static final class HolidayCreatePermission {
		private HolidayCreatePermission() {}
		public static final String CREATE = "HOLIDAY_CREATE_ADMIN";
		public static final String DELETE = "HOLIDAY_DELETE_ADMIN";
		public static final String UPDATE = "HOLIDAY_UPDATE_ADMIN";
	}
	public static final class ParamUnitConstant { 
		private ParamUnitConstant() {} 
		public static final String SR_TYPE = "SRTYPE";
		public static final String SR_CATEGORY = "SRCategory";
		public static final String SR_SUB_CATEGORY = "SRSubCategory";
		public static final String IA_TYPE = "IA_TYPE";
		public static final String Activity = "Activity";
		public static final String RELATED_TO = "RelatedTo";
		public static final String STATUS_ID = "STATUS_ID";
		public static final String TYPE = "Type";
		public static final String SUB_TYPE = "SubType";
		public static final String ACTIVE = "Active";
		

	}
	public static final class GlobalParameter { 
		private GlobalParameter() {} 
		
		public static final String GLOBAL_PARAMS = "GLOBAL_PARAMS";
		public static final String IS_SECURITY_MATRIX_ENABLE = "SECURITY_MODE_OPERATION_ACCESS";
		public static final String COMMON_TEAMS = "COMMON_TEAMS";
		
	}
	public static final class BusinessUnitConstant { 
		private BusinessUnitConstant() {} 
		public static final String STATUS_ID = "STATUS_ID";
		public static final String ACTIVE = "ACTIVE";
	}
	public static final class AlertCategoryConstant { 
		private AlertCategoryConstant() {} 
		public static final String STATUS_ID = "STATUS_ID";
		public static final String PRIORITY = "PRIORITY_LEVEL";
	}
	
	public static final class SrResolutionConstant{
		private SrResolutionConstant() {}
		public static final String REASON_CODE = "REASON_CODE";
		public static final String CAUSE_CATEGORY = "CAUSE_CATEGORY";
	}
}
