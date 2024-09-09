/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fio.dataimport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.UtilGenerator;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class ItmDataDecoder implements ImportDecoder {
	
    private static final String module = ItmDataDecoder.class.getName();
    
    protected GenericValue userLogin;
    
    public ItmDataDecoder(Map<String, ?> context) throws GeneralException {
        this.userLogin = (GenericValue) context.get("userLogin");
    }

    public List<GenericValue> decode(GenericValue entry, Timestamp importTimestamp, Delegator delegator, LocalDispatcher dispatcher, Object... args) throws Exception {
    	List<GenericValue> toBeStored = FastList.newInstance();
    	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    	GenericValue itm = null;
		conditions.add(EntityCondition.makeCondition("transactionNumber", EntityOperator.EQUALS, entry.getString("invoiceId")));
		conditions.add(EntityCondition.makeCondition("invoiceSequenceNumber", EntityOperator.EQUALS, entry.getString("invoiceSequenceNumber")));
    	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
    	itm = EntityQuery.use(delegator).from("InvoiceTransactionMaster").where(mainConditon).queryFirst();
		if (UtilValidate.isNotEmpty(itm)) {
			prepareItmData(itm, entry, true);
		} else {
			itm = delegator.makeValue("InvoiceTransactionMaster", UtilMisc.toMap("transactionNumber",
					entry.getString("invoiceId"), "invoiceId", UtilGenerator.getNextSeqId(), "invoiceSequenceNumber", entry.getString("invoiceSequenceNumber")));
			prepareItmData(itm, entry, false);
		}
		toBeStored.add(itm);
		return toBeStored;
    }
    
    private static void prepareItmData(GenericValue itm, GenericValue entry, boolean isCreate) {
    	BigDecimal emptyDecimal = new BigDecimal(0).setScale(2, BigDecimal.ROUND_CEILING);
    	
    	itm.put("invoiceType", entry.getString("invoiceType"));
		itm.put("invoiceExtRefNumber", entry.getString("invoiceExtRefNumber"));
		itm.put("storeNumber", entry.getString("storeNumber"));
		itm.put("invoiceDate", entry.getTimestamp("invoiceDate"));
		itm.put("totalSalesAmount", entry.getBigDecimal("totalSalesAmount"));
		itm.put("invoiceCurrency", entry.getString("invoiceCurrency"));
		itm.put("totalTenderAmount_1", entry.getBigDecimal("totalTenderAmount_1"));
		itm.put("totalTenderAmount_2", entry.getBigDecimal("totalTenderAmount_2"));
		itm.put("totalTenderType_1", entry.getString("totalTenderType_1"));
		itm.put("totalTenderType_2", entry.getString("totalTenderType_2"));
		itm.put("totalTaxAmount", entry.getBigDecimal("totalTaxAmount"));
		itm.put("billToPartyId", entry.getString("billToPartyId"));
		//itm.put("billToPartyName", entry.getString("billToPartyName"));
		itm.put("billToPartyRefId", entry.getString("billToPartyRefId"));
		itm.put("skuNumber", entry.getString("skuNumber"));
		itm.put("skuDescription", entry.getString("skuDescription"));
		itm.put("unitRetail", entry.getBigDecimal("unitRetail"));
		itm.put("unitCost", entry.getBigDecimal("unitCost"));
		itm.put("quantitySold", entry.getBigDecimal("quantitySold"));
		itm.put("itemNetSales", entry.getBigDecimal("itemNetSales"));
		itm.put("extendedDiscount", entry.getBigDecimal("extendedDiscount"));
		itm.put("totalLineTaxAmount", entry.getBigDecimal("totalLineTaxAmount"));
		itm.put("discountAmount", entry.getBigDecimal("discountAmount"));
		itm.put("isReturn", entry.getString("isReturn"));
		itm.put("numberOfReturns", entry.getBigDecimal("numberOfReturns"));
		itm.put("totalReturnedAmount", entry.getBigDecimal("totalReturnedAmount"));
		itm.put("totalAdjustmentNonTaxAndDiscount", entry.getBigDecimal("totalAdjustmentNonTaxAndDiscount"));
		itm.put("isGuestPurchase", entry.getString("isGuestPurchase"));
		itm.put("primaryBrand", entry.getString("primaryBrand"));
		itm.put("returnedDate", entry.getTimestamp("returnedDate"));
		itm.put("productCategoryId1", entry.getString("productCategoryId1"));
		itm.put("productCategoryName1", entry.getString("productCategoryName1"));
		itm.put("productCategoryId2", entry.getString("productCategoryId2"));
		itm.put("productCategoryName2", entry.getString("productCategoryName2"));
		itm.put("productCategoryId3", entry.getString("productCategoryId3"));
		itm.put("productCategoryName3", entry.getString("productCategoryName3"));
		itm.put("shipmentAddress1", entry.getString("shipmentAddress1"));
		itm.put("shipmentAddress2", entry.getString("shipmentAddress2"));
		itm.put("shipmentCity", entry.getString("shipmentCity"));
		itm.put("shipmentStateProvince", entry.getString("shipmentStateProvince"));
		itm.put("shipmentPostalCode", entry.getString("shipmentPostalCode"));
		itm.put("shipmentCountry", entry.getString("shipmentCountry"));
		itm.put("shipmentPhone", entry.getString("shipmentPhone"));
		
		itm.put("billAddress1", entry.getString("billAddress1"));
		itm.put("billAddress2", entry.getString("billAddress2"));
		itm.put("billCity", entry.getString("billCity"));
		itm.put("billPostalCode", entry.getString("billPostalCode"));
		itm.put("billCountry", entry.getString("billCountry"));
		itm.put("billStateProvince", entry.getString("billStateProvince"));
		itm.put("billingPhone", entry.getString("billingPhone"));
		//itm.put("billingEmail", entry.getString("billingEmail"));
		
		itm.put("numberOfReturns", emptyDecimal);
		itm.put("totalReturnedAmount", emptyDecimal);
		
		if (isCreate) {
			itm.put("chainRegularPrice", emptyDecimal);
			itm.put("unitRegularPrice", emptyDecimal);
			//itm.put("unitCost", emptyDecimal);
			//itm.put("extendedDiscount", emptyDecimal);
			//itm.put("totalLineTaxAmount", emptyDecimal);
			//itm.put("discountAmount", emptyDecimal);
			itm.put("discountAmountReward", emptyDecimal);
			itm.put("discountAmountMkt1", emptyDecimal);
			itm.put("discountAmountMkt2", emptyDecimal);
			itm.put("discountAmountMkt3", emptyDecimal);
			itm.put("carrierFee", emptyDecimal);
			itm.put("numberOfPackages", emptyDecimal);
			itm.put("balanceAmount", emptyDecimal);
			itm.put("numberOfItemsShipped", emptyDecimal);
			itm.put("numberOfItemsCancelled", emptyDecimal);
			itm.put("numberOfItemsApproved", emptyDecimal);
			itm.put("salesCommissionAmount", emptyDecimal);
			itm.put("actualShipCost", emptyDecimal);
			itm.put("totalInvoiceChannelFee", emptyDecimal);
			itm.put("rebateAmount", emptyDecimal);
			itm.put("earnedAmount", emptyDecimal);
		}
		
		if (UtilValidate.isNotEmpty(entry.getString("invoiceType")) && (entry.getString("invoiceType").equals("Sale") || entry.getString("invoiceType").equals("Return")) ) {
			itm.put("itemNetSales", emptyDecimal);
			itm.put("invoiceType", "Return");
			itm.put("numberOfReturns", itm.getBigDecimal("quantitySold").negate());
			itm.put("totalReturnedAmount", itm.getBigDecimal("quantitySold").multiply(itm.getBigDecimal("unitRetail")).negate().setScale(2, BigDecimal.ROUND_CEILING));
			itm.put("quantitySold", emptyDecimal);
			itm.put("itemStatus", "RETURN");
			itm.put("transactionType", "04");
			itm.put("isReturn", "Y");
		}
		
    }
}
