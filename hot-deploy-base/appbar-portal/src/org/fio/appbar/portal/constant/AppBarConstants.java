package org.fio.appbar.portal.constant;


public class AppBarConstants {
	private static final String MODULE = AppBarConstants.class.getName();
	private AppBarConstants() {}
	
	public static final class AppBarAccessLevel {
		private AppBarAccessLevel() {}
		public static final String SYSTEM_LEVEL = "SYSTEM_LEVEL";
		public static final String USER_LEVEL = "USER_LEVEL";
	}
	public static final class AppBarDataLevel {
		private AppBarDataLevel() {}
		public static final String GLOBAL_LEVEL = "GLOBAL";
		public static final String BU_LEVEL = "BU";
		public static final String TEAM_LEVEL = "TEAM";
		public static final String USER_LEVEL = "USER";
		
	}
}
