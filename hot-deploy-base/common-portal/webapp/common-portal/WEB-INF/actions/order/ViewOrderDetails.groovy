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
import org.ofbiz.entity.jdbc.SQLProcessor;

delegator = request.getAttribute("delegator");
orderId = parameters.get("orderId");
context.put("orderId",orderId);

String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

if (UtilValidate.isNotEmpty(orderId)){		
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));

	BigDecimal	adjustment;
	totalAdjustments=0.00;
	totalAmount=0.00;
	billToPartyId=null;	
 	List OrderByIdViewListN=new ArrayList();
	String requestURI = request.getRequestURI();
	//listOrders = delegator.findList("RmsTransactionMaster", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, UtilMisc.toList("sequenceNumber"), null, false);
	//for(orderLine in listOrders){
	
	String srSql = "SELECT * FROM `Rms_Transaction_Master` WHERE `order_Id`='"+orderId+"' ORDER BY convert(`sequence_Number`, decimal)";
	rs = sqlProcessor.executeQuery(srSql);
	
	while (rs.next()) {
			
		Map mp = new LinkedHashMap();
		String isReturn=rs.getString("is_Return");
		billToPartyId=rs.getString("bill_To_Party_Id");
		//String couponNumber =rs.getString("couponNumber");
		String discTransactionType = rs.getString("disc_Transaction_Type");
		//mp.put("orderName",rs.getString("orderName"));
		mp.put("orderDate", UtilDateTime.toDateString(rs.getDate("order_Date"), globalDateFormat));	
	 	mp.put("transactionNumber",rs.getString("transaction_Number"));
	 	String status=rs.getString("header_Status");
	 	statusItem = EntityUtil.getFirst(delegator.findByAnd("StatusItem", UtilMisc.toMap("statusId", status),null, false));
		if (UtilValidate.isNotEmpty(statusItem)) {
			status = statusItem.getString("description");
		}
		mp.put("statusDesc",status);
		String salesChannel=rs.getString("sales_Channel_Enum_Id");
	 	enumerationDes = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", salesChannel),null, false));
		if (UtilValidate.isNotEmpty(enumerationDes)) {
			salesChannel = enumerationDes.getString("description");
		}
		mp.put("salesChannel",salesChannel);					
		mp.put("skuNumber",rs.getString("sku_Number"));
		ProductId=rs.getString("sku_Number");	
	 	mp.put("discTransactionType",discTransactionType);
	 	mp.put("productName",rs.getString("sku_Description"));	
						
		mp.put("totalAdjustmentNonTaxAndDiscount",rs.getString("discount_Amount"));
		mp.put("numberOfItemsCancelled",rs.getString("number_Of_Items_Cancelled"));	
		mp.put("quantitySold",rs.getBigDecimal("quantity_Sold"));
		mp.put("numberOfReturns",rs.getBigDecimal("number_Of_Returns"));
		mp.put("netQa",rs.getString("quantity_Sold"));										
		
		mp.put("totalReturnedAmount",rs.getString("total_Returned_Amount"));								
		mp.put("numberOfItemsShipped",rs.getString("number_Of_Items_Shipped"));		   		 	
		mp.put("transactionType",rs.getString("transaction_Type"));		   		 	
	 	mp.put("cashierNumber",rs.getString("cashier_Number"));
	   	mp.put("registerNumber",rs.getString("register_Number"));
	   	
	 	sold = rs.getBigDecimal("quantity_Sold");
		price = rs.getBigDecimal("unit_Retail");
		BigDecimal bi = BigDecimal.valueOf(sold.intValue());				
		unit = price.multiply(sold);
		
		BigDecimal unitRetail = rs.getBigDecimal("unit_Retail");
		mp.put("unitRetail", unitRetail);
		
		BigDecimal unitCostActual = rs.getBigDecimal("unit_cost");
		mp.put("unitCostActual", unitCostActual);
		
		adjustment=rs.getBigDecimal("extended_Discount");			
		if (UtilValidate.isNotEmpty(adjustment)) {
			subTotal = adjustment.add(unit);	//add as adjustments are always negative values	
		} else {
			subTotal= unit;
		}
					
		mp.put("unitCost",price);
		if (UtilValidate.isNotEmpty(adjustment)){	
			totalAdjustments+=adjustment;
		}				
		// totalAdjustments+=adjustment;		
		mp.put("subTotal",subTotal);
		totalAmount+=subTotal;		
		mp.put("totalTaxAmount",rs.getString("total_Tax_Amount"));
		totalTaxAmount=rs.getString("total_Tax_Amount");
		context.put("totalTaxAmount", totalTaxAmount);
		orderTotal=rs.getString("total_Sales_Amount");
		context.put("orderTotal", orderTotal);
		
		mp.put("purchaseOrder", rs.getString("purchase_Order"));
		mp.put("deliveryDate", UtilValidate.isNotEmpty(rs.getTimestamp("estimated_Delivery_Date")) ? UtilDateTime.timeStampToString(rs.getTimestamp("estimated_Delivery_Date"), globalDateFormat, TimeZone.getDefault(), null) : "");
		
		if(requestURI.contains("client-portal")) {
			mp.put("srNumber", org.groupfio.common.portal.util.SrUtil.getSrNumberNonLink(delegator, orderId, rs.getString("sequence_Number")));
		} else {
			mp.put("srNumber", org.groupfio.common.portal.util.SrUtil.getSrNumber(delegator, orderId, rs.getString("sequence_Number")));
		}
		
		mp.put("sequenceNumber", rs.getString("sequence_Number"));
		
		context.put("OrderByIdViewList", mp);
		
		context.put("externalId", UtilValidate.isNotEmpty(rs.getString("transaction_Number")) ? rs.getString("transaction_Number") : orderId);	
		
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
 		primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,billToPartyId,UtilMisc.toMap("isRetriveEmail", true),true);
 		if(UtilValidate.isNotEmpty(primaryContactInformation)) {
 			context.put("partyEmail",primaryContactInformation.get("EmailAddress"));
 			context.put("primaryPhoneNumber", org.groupfio.common.portal.util.DataHelper.preparePhoneNumber(delegator, primaryContactInformation.get("PrimaryPhone")));
 		}
 		
 		String partyRoleTypeId = org.fio.homeapps.util.PartyHelper.getPartyRoleTypeId(billToPartyId, delegator);
 		Map<String, Object> primaryContact = org.groupfio.common.portal.util.DataUtil.getPrimaryContact(delegator, billToPartyId, partyRoleTypeId);
 		if (UtilValidate.isNotEmpty(primaryContact)) {
			primaryContactId = (String) primaryContact.get("contactId");
			primaryContactName = (String) primaryContact.get("contactName");
			context.put("primaryContactId", primaryContactId);
			context.put("primaryContactName", primaryContactName);
			//primaryContactEmail = PartyHelper.getEmailAddress(delegator, primaryContactId, "PRIMARY_EMAIL");
			//primaryContactPhone = PartyHelper.getContactNumber(delegator, primaryContactId, "PRIMARY_PHONE");
		}
 		
 	}
	
}	