import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.Debug;
import java.util.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.fio.admin.portal.util.DataHelper;
import org.ofbiz.base.util.UtilValidate;
import java.util.List;
import org.fio.admin.portal.util.DataHelper;
import org.fio.admin.portal.constant.AdminPortalConstant;

inputContext = new LinkedHashMap<String, Object>();
String buId=null;
emplTeamId = null;
emplTeamId=request.getParameter("emplTeamId");
List<GenericValue> buDetails = delegator.findByAnd("ProductStoreGroup",null, null, false);
context.put("buIdList", DataHelper.getDropDownOptions(buDetails, "productStoreGroupId", "productStoreGroupName"));
List<GenericValue> statusDetails = delegator.findByAnd("Enumeration",  UtilMisc.toMap("enumTypeId", AdminPortalConstant.BusinessUnitConstant.STATUS_ID), null, false);
context.put("statusIdList", DataHelper.getDropDownOptions(statusDetails, "enumCode", "description"));
GenericValue viewTeam = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId", emplTeamId).queryOne();
if (UtilValidate.isNotEmpty(viewTeam))
{
	inputContext.put("emplTeamId",viewTeam.getString("emplTeamId"));
	inputContext.put("teamName",viewTeam.getString("teamName"));
	inputContext.put("status",viewTeam.getString("isActive"));
	context.put("emplTeamId",viewTeam.getString("emplTeamId"));
	buId=viewTeam.getString("businessUnit");
	if (UtilValidate.isNotEmpty(buId)){
	GenericValue getBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", buId).queryOne();
		if (UtilValidate.isNotEmpty(getBu)){
			inputContext.put("buName",getBu.getString("productStoreGroupName"));
		}
	}
	context.put("inputContext", inputContext)
}