import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.util.TimeZone

delegator = request.getAttribute("delegator");



presentDate = UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "dd/MM/YYYY", TimeZone.getDefault(), null);

context.put("presentDate", presentDate);

