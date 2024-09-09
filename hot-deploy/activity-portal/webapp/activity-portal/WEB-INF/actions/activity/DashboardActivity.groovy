import java.sql.ResultSet;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.jdbc.SQLProcessor;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("activity-portalUiLabels", locale);
String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);

presentDate = UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), globalDateFormat, TimeZone.getDefault(), null);
context.put("presentDate", presentDate);