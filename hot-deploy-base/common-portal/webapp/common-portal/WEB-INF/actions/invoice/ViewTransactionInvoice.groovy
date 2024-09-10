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
invoiceId = parameters.get("invoiceId");
context.put("invoiceId",invoiceId);

String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
String currencyUomId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CURRENCY_UOM_ID");
context.put("currencyUomId",UtilValidate.isNotEmpty(currencyUomId)?currencyUomId:"");

if (UtilValidate.isNotEmpty(invoiceId)){
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));

	BigDecimal	adjustment;
	totalAdjustments=0.00;
	totalAmount=0.00;
	billToPartyId=null;
	totalTaxAmount = 0.00;
	totalItemAmount=0.00;
	invoiceTotal=0.00;
	totalSaleAmount = 0.00;
	List InvoiceByIdViewList=new ArrayList();
	String requestURI = request.getRequestURI();
	//listOrders = delegator.findList("RmsTransactionMaster", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId), null, UtilMisc.toList("sequenceNumber"), null, false);
	//for(orderLine in listOrders){
	
	String srSql = "SELECT * FROM Invoice_Transaction_Master WHERE invoice_Id='"+invoiceId+"' ORDER BY INVOICE_SEQUENCE_NUMBER ASC";
	rs = sqlProcessor.executeQuery(srSql);
	
	while (rs.next()) {
			
		Map mp = new LinkedHashMap();
		//String isReturn=rs.getString("is_Return");
		billToPartyId=rs.getString("bill_To_Party_Id");
		mp.put("invoiceId",rs.getString("invoice_id"));
		mp.put("billToPartyId",billToPartyId);
		mp.put("billToPartyName",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, billToPartyId, false));
		
		String sequenceNumber =rs.getString("INVOICE_SEQUENCE_NUMBER");
		mp.put("sequenceNumber",sequenceNumber);
		
		String invoiceType = rs.getString("invoice_Type");
		invoiceTypeDesc = EntityUtil.getFirst(delegator.findByAnd("InvoiceType", UtilMisc.toMap("invoiceTypeId", invoiceType),null, false));
		invoiceTypeDesc = UtilValidate.isNotEmpty(invoiceTypeDesc)?invoiceTypeDesc.get("description"):invoiceType;
		mp.put("invoiceType",invoiceTypeDesc);
		
		mp.put("invoiceDate", UtilDateTime.toDateString(rs.getDate("invoice_Date"), globalDateFormat));
		
		String status=rs.getString("invoice_Status");
		statusItem = EntityUtil.getFirst(delegator.findByAnd("StatusItem", UtilMisc.toMap("statusId", status),null, false));
		if (UtilValidate.isNotEmpty(statusItem)) {
			status = statusItem.getString("description");
		}
		mp.put("statusDesc",status);
		mp.put("skuNumber",rs.getString("sku_Number"));
		mp.put("skuDescription",rs.getString("sku_description"));
		mp.put("currencyUom",rs.getString("INVOICE_CURRENCY"));
		
		totalSaleAmount = rs.getBigDecimal("TOTAL_SALES_AMOUNT");
		mp.put("totalSaleAmount",UtilValidate.isNotEmpty(totalSaleAmount)?totalSaleAmount.setScale(2, BigDecimal.ROUND_CEILING):BigDecimal.ZERO);
		discountAmt = rs.getBigDecimal("DISCOUNT_AMOUNT");
		discountAmt = UtilValidate.isNotEmpty(discountAmt)?discountAmt.setScale(2, BigDecimal.ROUND_CEILING):BigDecimal.ZERO;
		
		itemNetSales = rs.getBigDecimal("ITEM_NET_SALES");
		itemNetSales = UtilValidate.isNotEmpty(itemNetSales)?itemNetSales.setScale(2, BigDecimal.ROUND_CEILING):BigDecimal.ZERO; 
		mp.put("itemNetSales",itemNetSales);
		
		if (UtilValidate.isEmpty(totalSaleAmount) || (UtilValidate.isNotEmpty(totalSaleAmount) && totalSaleAmount.compareTo(BigDecimal.ZERO)<=0)) {
			invoiceTotal = invoiceTotal.add(itemNetSales);
		}
		
		mp.put("discountAmount",discountAmt);
		
		mp.put("marginCost",UtilValidate.isNotEmpty(rs.getBigDecimal("margin_Cost"))?rs.getBigDecimal("margin_Cost").setScale(2, BigDecimal.ROUND_CEILING):BigDecimal.ZERO);
		mp.put("unitCost",UtilValidate.isNotEmpty(rs.getBigDecimal("unit_Cost"))?rs.getBigDecimal("unit_Cost").setScale(2, BigDecimal.ROUND_CEILING):BigDecimal.ZERO);
		mp.put("unitRetail",UtilValidate.isNotEmpty(rs.getBigDecimal("unit_Retail"))?rs.getBigDecimal("unit_Retail").setScale(2, BigDecimal.ROUND_CEILING):BigDecimal.ZERO);
		mp.put("quantitySold",UtilValidate.isNotEmpty(rs.getBigDecimal("quantity_Sold"))?rs.getBigDecimal("quantity_Sold").setScale(2, BigDecimal.ROUND_CEILING):BigDecimal.ZERO);
		mp.put("extendedDiscount",UtilValidate.isNotEmpty(rs.getBigDecimal("extended_Discount"))?rs.getBigDecimal("extended_Discount").setScale(2, BigDecimal.ROUND_CEILING):BigDecimal.ZERO);
		mp.put("externalId", UtilValidate.isNotEmpty(rs.getString("transaction_Number"))?rs.getString("transaction_Number"):"");
		
		totalAdjustments = totalAdjustments.add(discountAmt);
		totalTaxAmount = rs.getBigDecimal("TOTAL_TAX_AMOUNT");
		totalItemAmount = totalItemAmount.add(itemNetSales);
		context.put("InvoiceList", mp);
		InvoiceByIdViewList.add(mp);
		
	}
	if (UtilValidate.isNotEmpty(totalSaleAmount) && totalSaleAmount.compareTo(BigDecimal.ZERO)>0) {
		invoiceTotal = totalSaleAmount;
	}
	totalItemAmount = UtilValidate.isNotEmpty(totalItemAmount)?totalItemAmount.setScale(2, BigDecimal.ROUND_CEILING):"0.00"; 
	totalTaxAmount = UtilValidate.isNotEmpty(totalTaxAmount)?totalTaxAmount.setScale(2, BigDecimal.ROUND_CEILING):"0.00";
	totalAdjustments = UtilValidate.isNotEmpty(totalAdjustments)?totalAdjustments.setScale(2, BigDecimal.ROUND_CEILING):"0.00";
	invoiceTotal = UtilValidate.isNotEmpty(invoiceTotal)?invoiceTotal.setScale(2, BigDecimal.ROUND_CEILING):"0.00";
	
	context.put("totalItemAmount",totalItemAmount);
	context.put("totalTaxAmount",totalTaxAmount);
	context.put("totalAdjustments",totalAdjustments);
	context.put("invoiceTotal", invoiceTotal);
	context.put("InvoiceByIdViewList", InvoiceByIdViewList);
	
	
	//Contact Information
	partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, billToPartyId, false);
	context.put("partyIdVal", billToPartyId);
	context.put("partyName", partyName);
	 
	 if(billToPartyId!=null){
		 primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,billToPartyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
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