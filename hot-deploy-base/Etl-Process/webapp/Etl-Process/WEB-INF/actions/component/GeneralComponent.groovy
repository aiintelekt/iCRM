import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.opentaps.common.util.UtilCommon;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.util.EntityUtil;

dropdownOptions = UtilMisc.toMap("option_1", "Option 1", "option_2", "Option 2", "option_3", "Option 3", "option_4", "Option 4");
context.put("dropdownOptions", dropdownOptions);

checkboxOptions = UtilMisc.toMap("checkbox_1", "Option 1", "checkbox_2", "Option 2", "checkbox_3", "Option 3", "checkbox_4", "Option 4");
context.put("checkboxOptions", checkboxOptions);

tabOptions = UtilMisc.toMap("tab_1", UtilMisc.toMap("tabName", "Section 1", "tabDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#tabSampleScreen1"), "tab_2", UtilMisc.toMap("tabName", "Section 2", "tabDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#tabSampleScreen2"), "tab_3", UtilMisc.toMap("tabName", "Section 3", "tabDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#tabSampleScreen3"));
context.put("tabOptions", tabOptions);

accordionOptions = UtilMisc.toMap("accordion_1", UtilMisc.toMap("accordionName", "Accordion 1", "accordionDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#tabSampleScreen1"), "accordion_2", UtilMisc.toMap("accordionName", "Accordion 2", "accordionDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#tabSampleScreen2"), "accordion_3", UtilMisc.toMap("accordionName", "Accordion 3", "accordionDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#tabSampleScreen3"));
context.put("accordionOptions", accordionOptions);

datatableColumns = UtilMisc.toMap("partyId", "Party Id", "partyTypeId", "Type", "createdByUserLogin", "Created By UserLogin");
context.put("datatableColumns", datatableColumns);

datatableParams = UtilMisc.toMap("param_01", "Param_1", "param_02", "Param_2", "param_03", "Param_3");
context.put("datatableParams", datatableParams);

portletScreens = UtilMisc.toMap(
	  "portlet_1", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen1")
	, "portlet_2", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen2")
	, "portlet_3", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen3")
	, "portlet_4", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen4")
	, "portlet_5", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen5")
	, "portlet_6", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen6")
	, "portlet_7", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen7")
	, "portlet_8", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen8")
	, "portlet_9", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen9")
	, "portlet_10", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen9")
	, "portlet_11", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen9")
	, "portlet_12", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen9")
	, "portlet_13", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen9")
	, "portlet_14", UtilMisc.toMap("portletName", "Accordion 1", "portletDetailScreen", "component://fio-responsive-template/webapp/widget/fio-responsive-template/screens/common/GeneralScreens.xml#portletScreen9")
	);
context.put("portletScreens", portletScreens);