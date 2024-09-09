package org.fio.crm.config;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;



public class TaxExcemption {
	
	public static Map<String, Object> removeTaxExcemption(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String taxAuthGeoId = (String)context.get("taxAuthGeoId");
		String taxAuthPartyId = (String)context.get("taxAuthPartyId");
		try {
			GenericValue TaxExcemptionGv = EntityQuery.use(delegator).from("TaxExcemption").where("taxAuthGeoId", taxAuthGeoId,"taxAuthPartyId",taxAuthPartyId).queryOne();
			//GenericValue TaxExcemptionGv = delegator.findByPrimaryKey("TaxExcemption",UtilMisc.toMap("taxAuthGeoId",taxAuthGeoId,"taxAuthPartyId",taxAuthPartyId));
			if(UtilValidate.isNotEmpty(TaxExcemptionGv)){
				TaxExcemptionGv.remove();
				EntityCondition thrudateCon1=EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
				EntityCondition thrudateCon2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp());
				EntityCondition dateCondition = EntityCondition.makeCondition(thrudateCon1,EntityOperator.OR,thrudateCon2);
				List<EntityCondition> conList = FastList.newInstance();
				conList.add(dateCondition);
				conList.add(EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, taxAuthPartyId));
				conList.add(EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.EQUALS, taxAuthGeoId));
				List<GenericValue> PartyTaxAuthInfoList = delegator.findList("PartyTaxAuthInfo", EntityCondition.makeCondition(conList,EntityOperator.AND),null,null,null,false);
				delegator.removeAll(PartyTaxAuthInfoList);
				
			}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess("Tax Exemption removed successfully");
    }
	public static Map<String, Object> addTaxExcemption(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String taxAuthGeoId = (String)context.get("taxAuthGeoId");
		String taxAuthPartyId = (String)context.get("taxAuthPartyId");
		try {
			GenericValue TaxExcemptionGv = EntityQuery.use(delegator).from("TaxExcemption").where("taxAuthGeoId", taxAuthGeoId,"taxAuthPartyId",taxAuthPartyId).queryOne();
			//GenericValue TaxExcemptionGv = delegator.findByPrimaryKey("TaxExcemption",UtilMisc.toMap("taxAuthGeoId",taxAuthGeoId,"taxAuthPartyId",taxAuthPartyId));
			if(UtilValidate.isNotEmpty(TaxExcemptionGv)){
				return ServiceUtil.returnError("Tax Exemption already added");
			}else{
				TaxExcemptionGv =  delegator.makeValue("TaxExcemption",UtilMisc.toMap("taxAuthGeoId",taxAuthGeoId,"taxAuthPartyId",taxAuthPartyId));
				TaxExcemptionGv.create();
			}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.toString());
		}
		return ServiceUtil.returnSuccess("Tax Excemption added successfully");
    }
	public static Map<String, Object> createPartyTaxExcemption(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String)context.get("partyId");
		String isExempt = (String)context.get("isExempt");
		String partyTaxId = (String)context.get("partyTaxId");
		try {
			if((isExempt != "") && ("Y".equalsIgnoreCase(isExempt))){
				List<GenericValue> TaxExcemptionList = delegator.findAll("TaxExcemption",true);
				for(GenericValue TaxExcemption : TaxExcemptionList){
					String taxAuthGeoId = (String)TaxExcemption.get("taxAuthGeoId");
					String taxAuthPartyId = (String)TaxExcemption.get("taxAuthPartyId");
					Map input = UtilMisc.toMap("partyId",partyId,"isExempt",isExempt,"partyTaxId",partyTaxId,"taxAuthGeoId",taxAuthGeoId,"taxAuthPartyId",taxAuthPartyId,"fromDate",UtilDateTime.nowTimestamp());
					if(UtilValidate.isNotEmpty(taxAuthGeoId) && UtilValidate.isNotEmpty(taxAuthPartyId)){
						GenericValue PartyTaxAuthInfo = delegator.makeValue("PartyTaxAuthInfo",input);
						try{
							PartyTaxAuthInfo.create();
						}catch (GenericEntityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.toString());
		}
		return ServiceUtil.returnSuccess("Tax Excemption created successfully");
    }
	
	public static Map<String, Object> updatePartyTaxExcemption(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String)context.get("partyId");
		String isExempt = (String)context.get("isExempt");
		String partyTaxId = (String)context.get("partyTaxId");
		try {
			if((isExempt !="") && ("Y".equalsIgnoreCase(isExempt))){
				EntityCondition thrudateCon1=EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
				EntityCondition thrudateCon2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp());
				EntityCondition dateCondition = EntityCondition.makeCondition(thrudateCon1,EntityOperator.OR,thrudateCon2);
				List<EntityCondition> conList = FastList.newInstance();
				conList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conList.add(dateCondition);

				List<GenericValue> PartyTaxAuthInfoList = delegator.findList("PartyTaxAuthInfo",EntityCondition.makeCondition(conList,EntityOperator.AND),null,null,null,false);
				if(UtilValidate.isEmpty(PartyTaxAuthInfoList)){
					Map inputMap = UtilMisc.toMap("partyTaxId",partyTaxId,"isExempt",isExempt,"partyId",partyId,"userLogin",userLogin);
		            try {
						dispatcher.runSync("createPartyTaxExcemption",inputMap);
					} catch (GenericServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					List<GenericValue> TaxExcemptionList = delegator.findAll("TaxExcemption",true);
					for(GenericValue TaxExcemption : TaxExcemptionList){
						String taxAuthGeoId = TaxExcemption.getString("taxAuthGeoId");
						String taxAuthPartyId = TaxExcemption.getString("taxAuthPartyId");
						if((taxAuthGeoId != null) && (taxAuthPartyId != null)){
							List<EntityCondition> conList1 = FastList.newInstance();
							conList1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
							conList1.add(dateCondition);
							conList1.add(EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, taxAuthPartyId));
							conList1.add(EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.EQUALS, taxAuthGeoId));
							List<GenericValue> PartyTaxAuthInfo = delegator.findList("PartyTaxAuthInfo",EntityCondition.makeCondition(conList1,EntityOperator.AND),null,null,null,false);
							if((PartyTaxAuthInfo != null) && (PartyTaxAuthInfo.size()>0)){
								GenericValue PartyTaxAuth = PartyTaxAuthInfo.get(PartyTaxAuthInfo.size()-1);
								PartyTaxAuth.set("isExempt","Y");
								PartyTaxAuth.set("partyTaxId",partyTaxId);
								PartyTaxAuth.store();
							}else{
								GenericValue PartyTaxAuth = delegator.makeValue("PartyTaxAuthInfo",UtilMisc.toMap("taxAuthPartyId",taxAuthPartyId,"taxAuthGeoId",taxAuthGeoId,"partyId",partyId,"fromDate",UtilDateTime.nowTimestamp(),"isExempt","Y","partyTaxId",partyTaxId));
								PartyTaxAuth.create();
							}
						}
					}
				}
				
			}else if((isExempt != null) && ("N".equalsIgnoreCase(isExempt))){
				List<GenericValue> PartyTaxAuthInfoList = delegator.findByAnd("PartyTaxAuthInfo",UtilMisc.toMap("partyId",partyId),null,false);
				if((PartyTaxAuthInfoList != null) && (PartyTaxAuthInfoList.size()>0)){
					for(GenericValue PartyTaxAuthInfo : PartyTaxAuthInfoList){
						PartyTaxAuthInfo.set("thruDate", UtilDateTime.nowTimestamp());
						PartyTaxAuthInfo.store();
					}
				}
				
			}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnSuccess(e.toString());
		}
		return ServiceUtil.returnSuccess("Tax Excemption added successfully");
    }
}
