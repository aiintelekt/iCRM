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

emplTeamId = null;
buId = null;
statusId = null;
emplTeamId=request.getParameter("emplTeamId");
inputContext = new LinkedHashMap<String, Object>();
GenericValue viewTeam = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId", emplTeamId).queryOne();
if (UtilValidate.isNotEmpty(viewTeam))
{
	context.put("emplTeamId",viewTeam.getString("emplTeamId"));
	inputContext.put("emplTeamId",viewTeam.getString("emplTeamId"));
	inputContext.put("teamName",viewTeam.getString("teamName"));
	//context.put("isActive",viewTeam.getString("isActive"));
	statusId=viewTeam.getString("isActive");
	if (UtilValidate.isNotEmpty(statusId))
	{
		GenericValue getEnumeration = EntityQuery.use(delegator).from("Enumeration").where("enumTypeId", "STATUS_ID", "enumCode", statusId).queryOne();
		if (UtilValidate.isNotEmpty(getEnumeration))
		{
			inputContext.put("status",getEnumeration.getString("description"));
		}
	}
	buId=viewTeam.getString("businessUnit");
	if (UtilValidate.isNotEmpty(buId))
	{
		GenericValue getBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", buId).queryOne();
		if (UtilValidate.isNotEmpty(getBu))
		{
			inputContext.put("businessUnitName",getBu.getString("productStoreGroupName"));
		}
	}
}
context.put("inputContext",inputContext);