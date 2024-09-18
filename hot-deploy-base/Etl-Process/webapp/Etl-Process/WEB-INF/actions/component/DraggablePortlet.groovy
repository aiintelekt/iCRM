import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.opentaps.common.util.UtilCommon;

portletScreens = UtilMisc.toMap(
	  "portlet_1", UtilMisc.toMap("portletName", "Portlet 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen1")
	, "portlet_2", UtilMisc.toMap("portletName", "Portlet 2", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen2")
	, "portlet_3", UtilMisc.toMap("portletName", "Portlet 3", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen3")
	, "portlet_4", UtilMisc.toMap("portletName", "Portlet 4", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen4")
	, "portlet_5", UtilMisc.toMap("portletName", "Portlet 5", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen5")
	, "portlet_6", UtilMisc.toMap("portletName", "Portlet 6", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen6")
	, "portlet_7", UtilMisc.toMap("portletName", "Portlet 7", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen7")
	, "portlet_8", UtilMisc.toMap("portletName", "Portlet 8", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen8")
	, "portlet_9", UtilMisc.toMap("portletName", "Portlet 9", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen9")
	, "portlet_10", UtilMisc.toMap("portletName", "Portlet 10", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen9")
	, "portlet_11", UtilMisc.toMap("portletName", "Portlet 11", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen9")
	, "portlet_12", UtilMisc.toMap("portletName", "Portlet 12", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen9")
	);
context.put("portletScreens", portletScreens);