import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.*;
import java.util.*;
import org.fio.crm.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.Debug;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;

delegator = request.getAttribute("delegator");
orderId = parameters.get("orderId");
context.put("orderId",orderId);
if (UtilValidate.isNotEmpty(orderId)){		
	BigDecimal	adjustment;
	totalAdjustments=0.00;
	totalAmount=0.00;	
 	List OrderByIdViewListN=new ArrayList();
	
	listOrders = delegator.findList("RmsTransactionMaster", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	billToPartyId=null;
	for(conditionFind in listOrders){
		Map mp = new HashMap();
		String isReturn=conditionFind.get("isReturn");
		billToPartyId=conditionFind.get("billToPartyId");
		//String couponNumber =conditionFind.get("couponNumber");
		String discTransactionType = conditionFind.get("discTransactionType");
		//mp.put("orderName",conditionFind.get("orderName"));
		mp.put("orderDate", UtilDateTime.toDateString(conditionFind.get("orderDate"),"dd/MM/yyyy"));	
	 	mp.put("transactionNumber",conditionFind.get("transactionNumber"));
	 	String status=conditionFind.get("headerStatus");
	 	statusItem = EntityUtil.getFirst(delegator.findByAnd("StatusItem", UtilMisc.toMap("statusId", status),null, false));
		if (UtilValidate.isNotEmpty(statusItem)) {
			status = statusItem.getString("description");
		}
		mp.put("statusDesc",status);
		String salesChannel=conditionFind.get("salesChannelEnumId");
	 	enumerationDes = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", salesChannel),null, false));
		if (UtilValidate.isNotEmpty(enumerationDes)) {
			salesChannel = enumerationDes.getString("description");
		}
		mp.put("salesChannel",salesChannel);					
		mp.put("skuNumber",conditionFind.get("skuNumber"));
		ProductId=conditionFind.get("skuNumber");	
	 	mp.put("discTransactionType",discTransactionType);
	 	mp.put("productName",conditionFind.get("skuNumber"));	
						
		mp.put("totalAdjustmentNonTaxAndDiscount",conditionFind.get("discountAmount"));
		mp.put("numberOfItemsCancelled",conditionFind.get("numberOfItemsCancelled"));	
		mp.put("quantitySold",conditionFind.get("quantitySold"));
		mp.put("numberOfReturns",conditionFind.get("numberOfReturns"));
		mp.put("netQa",conditionFind.get("quantitySold"));										
		
		mp.put("totalReturnedAmount",conditionFind.get("totalReturnedAmount"));								
		mp.put("numberOfItemsShipped",conditionFind.get("numberOfItemsShipped"));		   		 	
		mp.put("transactionType",conditionFind.get("transactionType"));		   		 	
	 	mp.put("cashierNumber",conditionFind.get("cashierNumber"));
	   	mp.put("registerNumber",conditionFind.get("registerNumber"));
	 	sold=conditionFind.get("quantitySold");
		price=conditionFind.get("unitRetail");
		BigDecimal	bi = BigDecimal.valueOf(sold.intValue());				
		unit= price.multiply(sold);
		mp.put("unitRetail",unit);
		adjustment=conditionFind.get("extendedDiscount");			
		if (UtilValidate.isNotEmpty(adjustment)){
			subTotal = adjustment.add(unit);	//add as adjustments are always negative values	
		}
		else{
			subTotal= unit;
		}
					
		mp.put("unitCost",price);
		if (UtilValidate.isNotEmpty(adjustment)){	
			totalAdjustments+=adjustment;
		}				
		// totalAdjustments+=adjustment;		
		mp.put("subTotal",subTotal);
		totalAmount+=subTotal;		
		mp.put("totalTaxAmount",conditionFind.get("totalTaxAmount"));
		totalTaxAmount=conditionFind.get("totalTaxAmount");
		context.put("totalTaxAmount", totalTaxAmount);
		orderTotal=conditionFind.get("totalSalesAmount");
		context.put("orderTotal", orderTotal);
		
		mp.put("purchaseOrder", conditionFind.get("purchaseOrder"));
		//mp.put("srNumber", org.groupfio.common.portal.util.DataUtil.getSrNumber(delegator, conditionFind.getString("custRequestId")));
		mp.put("deliveryDate", UtilValidate.isNotEmpty(conditionFind.get("estimatedDeliveryDate")) ? UtilDateTime.timeStampToString(conditionFind.getTimestamp("estimatedDeliveryDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		
		context.put("OrderByIdViewList", mp);
		OrderByIdViewListN.add(mp); 						
						
	}
	context.put("totalAdjustments", totalAdjustments);		
	context.put("totalAmount", totalAmount);
	context.put("OrderByIdViewList12", OrderByIdViewListN);			
	
	context.put("OrderByIdViewListN", OrderByIdViewListN);  	
	
	//Contact Information
	partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, billToPartyId, false);
	context.put("partyIdVal", billToPartyId);
	context.put("partyName", partyName); 	
 	
 	if(billToPartyId!=null){
 		primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,billToPartyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
 		if(UtilValidate.isNotEmpty(primaryContactInformation)) {
 			context.put("partyEmail",primaryContactInformation.get("EmailAddress"));
 			context.put("primaryPhoneNumber",primaryContactInformation.get("PrimaryPhone"));
 			
 		}
 	}
 	
	
}	