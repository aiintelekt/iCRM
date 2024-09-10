import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant
import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

import java.text.SimpleDateFormat
import java.util.function.Function
import java.util.stream.Collectors


delegator = request.getAttribute("delegator");

inputContext = new LinkedHashMap<String, Object>();
context.put("inputContext", inputContext);