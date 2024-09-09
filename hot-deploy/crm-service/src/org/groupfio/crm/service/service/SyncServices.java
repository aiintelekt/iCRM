/**
 * 
 */
package org.groupfio.crm.service.service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.fio.homeapps.util.SrDataHelper;
import org.groupfio.crm.service.resolver.Resolver;
import org.groupfio.crm.service.resolver.ResolverConstants.ResolverType;
import org.groupfio.crm.service.resolver.ResolverFactory;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class SyncServices {
	
	private static final String MODULE = SyncServices.class.getName();

	public static Map<String, Object> syncSrStatusEsc(DispatchContext dctx, Map context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	
    	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		NumberFormat nf = NumberFormat.getInstance(locale);
		
    	Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
    	
    	//String partyId = (String) requestContext.get("partyId");
				
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			
			List<GenericValue> emailConfigList = EntityQuery.use(delegator).from("CustRequestEmailConfig").orderBy("sequenceId").queryList();
			if (UtilValidate.isNotEmpty(emailConfigList)) {
				Map<String, Object> emailTemplates = SrDataHelper.getSrStatusEmailTemplates(delegator, emailConfigList);
				
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				
				dynamicView.addMemberEntity("CR", "CustRequest");
				dynamicView.addAlias("CR", "custRequestId", null, null, null, true, null);
				//dynamicView.addAlias("CR", "custRequestId");
				dynamicView.addAlias("CR", "custRequestName");
				dynamicView.addAlias("CR", "statusId");
				dynamicView.addAlias("CR", "createdDate");
				
				dynamicView.addMemberEntity("CRS", "CustRequestSupplementory");
				dynamicView.addAlias("CRS", "statusEscTime");
				dynamicView.addAlias("CRS", "statusClosedEscTime");
				
				dynamicView.addViewLink("CR", "CRS", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
				
				String orderBy = "createdDate DESC";
				
				Set<String> fieldsToSelect = new LinkedHashSet<String>();
				
				fieldsToSelect.add("custRequestId");fieldsToSelect.add("custRequestName");fieldsToSelect.add("statusId");fieldsToSelect.add("createdDate");
				fieldsToSelect.add("statusEscTime");fieldsToSelect.add("statusClosedEscTime");
				
				List<EntityCondition> conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("statusEscTime", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("statusClosedEscTime", EntityOperator.EQUALS, null)
						));
				
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_WRK_COMPL")));
				
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				Debug.logInfo("syncSrStatusEsc condition: "+condition, MODULE);
				
				EntityListIterator pli = EntityQuery.use(delegator)
                		.select(fieldsToSelect)
                        .from(dynamicView)
                        .where(condition)
                        .orderBy(orderBy)
                        .cursorScrollInsensitive()
                        //.fetchSize(highIndex)
                        //.distinct()
                        //.cache(true)
                        .queryIterator();
                // get the partial list for this page
				List<GenericValue> resultList = pli.getCompleteList();
				Debug.logInfo("syncSrStatusEsc resultList size: "+resultList.size(), MODULE);
				if (UtilValidate.isNotEmpty(resultList)) {
					String escalationLevel = "1";
					for (GenericValue sr : resultList) {
						Debug.logInfo("syncSrStatusEsc SR_Number: "+sr.getString("custRequestId"), MODULE);
						
						GenericValue supplementory = EntityQuery.use(delegator).from("CustRequestSupplementory").where("custRequestId", sr.getString("custRequestId")).queryFirst();
						if (UtilValidate.isNotEmpty(supplementory)) {
							Map<String, Object> escalationContext = new LinkedHashMap<String, Object>();
							escalationContext.put("delegator", delegator);
							escalationContext.put("escalationLevel", escalationLevel);
							escalationContext.put("createdDate", sr.getTimestamp("createdDate"));
							escalationContext.put("statusId", sr.getString("statusId"));
							escalationContext.put("statusClosedEscTime", sr.get("statusClosedEscTime"));
							escalationContext.put("isCalculateCommitDate", "N");
							Resolver resolver = ResolverFactory.getResolver(ResolverType.ESCALATION_RESOLVER);
							Map<String, Object> escalationResult = resolver.resolve(escalationContext);
							
							supplementory.put("statusEscTime", escalationResult.get("statusEscTime"));
							supplementory.put("statusClosedEscTime", escalationResult.get("statusClosedEscTime"));
							
							supplementory.store();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			return result;
		}
		
		result.putAll(ServiceUtil.returnSuccess("Successfully sync sr status esc"));
		return result;
	}
	
}
