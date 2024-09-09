import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.util.TimeZone

delegator = request.getAttribute("delegator");

String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);

presentDate = UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), globalDateFormat, TimeZone.getDefault(), null);

context.put("presentDate", presentDate);

