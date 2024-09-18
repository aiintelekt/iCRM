/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.*;
import java.math.BigDecimal;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.payment.*;
import java.text.DateFormat;
import java.text.*;
import java.text.NumberFormat;
import org.ofbiz.accounting.invoice.InvoiceWorker;

// @param GenericValue invoice - The Invoice entity to find payment applications for
String invoiceId = request.getParameter("invoiceId");

inputContext = new LinkedHashMap<String, Object>();

if (UtilValidate.isNotEmpty(invoiceId)) {
	
	invoice = from("Invoice").where("invoiceId", invoiceId).queryOne();
}

invoiceApplications = [];  // to pass back to the screen with payment applications added
// retrieve related applications with null itemnumber
invoiceAppls = invoice.getRelated("PaymentApplication", [invoiceItemSeqId : null], null, false);
invoiceAppls.each { invoiceAppl ->
	itemmap = [:];
	itemmap.invoiceId = invoiceAppl.invoiceId;
	itemmap.invoiceItemSeqId = invoiceAppl.invoiceItemSeqId;
	itemmap.total = InvoiceWorker.getInvoiceTotal(invoice);
	itemmap.paymentApplicationId = invoiceAppl.paymentApplicationId;
	itemmap.paymentId = invoiceAppl.paymentId;
	itemmap.billingAccountId = invoiceAppl.billingAccountId;
	itemmap.taxAuthGeoId = invoiceAppl.taxAuthGeoId;
	itemmap.amountToApply = invoiceAppl.amountApplied;
	itemmap.amountApplied = invoiceAppl.amountApplied;
	invoiceApplications.add(itemmap);
}


// retrieve related applications with an existing itemnumber
invoice.getRelated("InvoiceItem", null, null, false).each { item ->
	BigDecimal itemTotal = null;
	if (item.amount != null) {
		  if (!item.quantity) {
			  itemTotal = item.getBigDecimal("amount");
		  } else {
			  itemTotal = item.getBigDecimal("amount").multiply(item.getBigDecimal("quantity"));
		  }
	}
	// get relation payment applications for every item(can be more than 1 per item number)
	item.getRelated("PaymentApplication", null, null, false).each { paymentApplication ->
		itemmap = [:];
		itemmap.putAll(item);
		//itemmap.total = NumberFormat.getInstance(locale).format(itemTotal);
		itemmap.total = paymentApplication.getBigDecimal("itemTotal")?.setScale(decimals,rounding);
		itemmap.paymentApplicationId = paymentApplication.paymentApplicationId;
		itemmap.paymentId = paymentApplication.paymentId;
		itemmap.toPaymentId = paymentApplication.toPaymentId;
		itemmap.amountApplied  = paymentApplication.getBigDecimal("amountApplied")?.setScale(decimals,rounding);
		//itemmap.amountApplied = (paymentApplication.getBigDecimal("amountApplied"));
		itemmap.amountToApply = paymentApplication.getBigDecimal("amountApplied");
		itemmap.billingAccountId = paymentApplication.billingAccountId;
		itemmap.taxAuthGeoId = paymentApplication.taxAuthGeoId;
		invoiceApplications.add(itemmap);
	}
}
if (invoiceApplications) context.invoiceApplications = invoiceApplications;

BigDecimal invoiceTotal = BigDecimal.ZERO;
BigDecimal appliedAmount = BigDecimal.ZERO;
BigDecimal notAppliedAmount = BigDecimal.ZERO;

invoiceTotal = org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoiceId);
//appliedAmount  org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceApplied(delegator,invoiceId);
notAppliedAmount = org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceNotApplied(delegator,invoiceId);

context.put("invoiceTotal",invoiceTotal.setScale(2, BigDecimal.ROUND_CEILING).toString() );
context.put("appliedAmount", appliedAmount.setScale(2, BigDecimal.ROUND_CEILING).toString());
context.put("notAppliedAmount", notAppliedAmount.setScale(2, BigDecimal.ROUND_CEILING).toString());
