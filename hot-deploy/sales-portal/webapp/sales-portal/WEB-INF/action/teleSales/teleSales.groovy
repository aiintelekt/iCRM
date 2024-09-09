import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import java.util.LinkedHashMap;

import javax.swing.DebugGraphics;

import org.fio.admin.portal.util.DataHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


String campaignName = request.getParameter("campaignName");


exprList = [];
exprList.add(EntityCondition.makeCondition('campaignTypeId', EntityOperator.IN, ["PHONE_CALL","TELESALES"]));
exprList.add(EntityCondition.makeCondition('marketingCampaignId', EntityOperator.NOT_EQUAL, null));
List marketingCampaignList = from("MarketingCampaign").where(exprList).orderBy("createdStamp").queryList();


context.marketingCampaignList=marketingCampaignList;

userLogin = context.get("userLogin");
userLoginId = userLogin.userLoginId;


Map totalCallsByCamp = new LinkedHashMap();
totalCallsByCamp.put("1","1");
totalCallsByCamp.put("2","2");
totalCallsByCamp.put("3","3");
totalCallsByCamp.put("4","4");
totalCallsByCamp.put("5","5");


context.put("totalCallsByCamp",totalCallsByCamp);












