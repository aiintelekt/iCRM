import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.fio.crm.party.PartyHelper;
import org.groupfio.account.portal.util.DataHelper;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityFindOptions
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.Debug;
import javolution.util.FastList;
import org.fio.homeapps.util.UtilActivity;
import org.fio.homeapps.util.EnumUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("account-portalUiLabels", locale);

String partyId = request.getParameter("partyId");
String isView = context.get("isView");
Map<String, Object> contactAcctMap = new HashMap<String, Object>();
contactAcctMap.put("partyIdTo", partyId);
contactAcctMap.put("partyRoleTypeId", "ACCOUNT");

inputContext = context.get("inputContext");

Map<String, Object> result = dispatcher.runSync("common.getContactAndPartyAssocUL", contactAcctMap);
if(ServiceUtil.isSuccess(result)){
	context.partyContactAssocList = result.partyContactAssoc;
	
	if (UtilValidate.isNotEmpty(result)){
		List primaryContactsList = new ArrayList();
		primaryContactsList = result.partyContactAssoc;
		inputContext.put("primaryContactsList", primaryContactsList);
		String primaryContactName = "";
		String contactId = "";String primaryCId = "";
		for(int i=0;i<primaryContactsList.size();i++){
			Map < String, Object > partyContactMap = new HashMap < String, Object > ();
			partyContactMap = (Map<String, Object>) primaryContactsList.get(i);
			if(i==0){
				primaryContactName = (String) partyContactMap.get("name");
				contactId = (String) partyContactMap.get("contactId");
			}
			/*String primaryContactStatusId =  partyContactMap.get("statusId");
			if("PARTY_DEFAULT".equals(primaryContactStatusId)){
				primaryContactName = (String) partyContactMap.get("name");
				contactId = (String) partyContactMap.get("contactId");
				primaryCId = (String) partyContactMap.get("contactId");
			}*/
		}
		
	}
}