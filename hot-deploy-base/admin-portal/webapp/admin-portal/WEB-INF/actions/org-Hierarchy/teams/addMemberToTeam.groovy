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

emplTeamId=request.getParameter("emplTeamId");
GenericValue viewTeam = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId", emplTeamId).queryOne();
if (UtilValidate.isNotEmpty(viewTeam))
{
	context.put("emplTeamId",viewTeam.getString("emplTeamId"));
	Debug.log("empl ID====="+viewTeam.getString("emplTeamId"));
}