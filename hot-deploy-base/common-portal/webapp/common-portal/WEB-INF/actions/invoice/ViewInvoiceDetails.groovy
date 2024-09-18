import java.math.BigDecimal
import java.sql.Timestamp;

import org.fio.crm.party.PartyHelper;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.EnumUtil
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil
import org.ofbiz.base.util.Debug;
import org.ofbiz.accounting.invoice.InvoiceWorker;

String invoiceId = request.getParameter("invoiceId");
String srNumber = request.getParameter("srNumber");
String canDoPayment = "Y";
String canSetToPaid = "N";
inputContext = new LinkedHashMap < String, Object > ();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

if (UtilValidate.isNotEmpty(userLogin)) {
    userLoginId = userLogin.getString("userLoginId");
    inputContext.put("userLoginId", userLoginId);
}
Debug.log("invoiceId=====invoiceId " + invoiceId);
if (UtilValidate.isNotEmpty(invoiceId)) {

    Invoice = from("Invoice").where("invoiceId", invoiceId).queryFirst();
    List InvoiceStatuses = delegator.findByAnd("InvoiceStatus", UtilMisc.toMap("invoiceId", invoiceId), null, true);
    Debug.log("InvoiceStatuses=====groovy " + InvoiceStatuses);
    ArrayList invoiceStatusesList = new ArrayList();
    invoiceStatusesList = EntityUtil.getFieldListFromEntityList(InvoiceStatuses, "statusId", true);
    String sentStatus = "INVOICE_SENT";
    if (invoiceStatusesList.contains(sentStatus)) {
        canSetToPaid = "Y";
    }
    inputContext.put("srNumber", srNumber);
    inputContext.put("InvoiceStatuses", InvoiceStatuses);
    inputContext.put("canSetToPaid", canSetToPaid);
    if (UtilValidate.isNotEmpty(Invoice)) {
        invoiceType = Invoice.getString("invoiceTypeId");
        statusId = Invoice.getString("statusId");
        invoiceStatus = Invoice.getString("statusId");
        String statusItemDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, invoiceStatus, "INVOICE_STATUS");
        //Debug.log("statusItemDesc"+statusItemDesc);
        refNumber = Invoice.getString("referenceNumber");
        invoiceDate = Invoice.getString("invoiceDate");
        partyId = Invoice.get("partyId");
        name = org.fio.homeapps.util.DataUtil.getPartyName(delegator, partyId);
        String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
        String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

        inputContext.put("invoiceId", invoiceId);
        inputContext.put("invoiceTypeId", invoiceType);
        inputContext.put("invoiceStatus", statusItemDesc);
        inputContext.put("statusId", statusId);
        inputContext.put("referenceNumber", refNumber);
        inputContext.put("partyId", partyId);
        inputContext.put("partyNameDesc", name + " (" + partyId + ")");
        inputContext.put("partyName", name);
        inputContext.put("partyId_desc", name);
        inputContext.put("invoiceMessage", Invoice.getString("invoiceMessage"));
        inputContext.put("invoiceDateUpdate", UtilValidate.isNotEmpty(Invoice.get("invoiceDate")) ?
            UtilDateTime.timeStampToString(Invoice.getTimestamp("invoiceDate"),
                globalDateFormat, TimeZone.getDefault(), null) : "");
        inputContext.put("invoiceDateView", UtilValidate.isNotEmpty(Invoice.get("invoiceDate")) ?
            UtilDateTime.timeStampToString(Invoice.getTimestamp("invoiceDate"),
                globalDateFormat, TimeZone.getDefault(), null) : "");
        inputContext.put("dueDate", UtilValidate.isNotEmpty(Invoice.get("dueDate")) ?
            UtilDateTime.timeStampToString(Invoice.getTimestamp("dueDate"),
                globalDateFormat, TimeZone.getDefault(), null) : "");
        invoiceTotal = org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
        outstandingAmount = org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceNotApplied(delegator, invoiceId);
        inputContext.put("invoiceTotal", invoiceTotal);
        inputContext.put("outstandingAmount", outstandingAmount);
        CCDetails = from("PaymentMethodAndCreditCard").where("partyId", partyId).queryOne();
        if (UtilValidate.isNotEmpty(CCDetails)) {
            inputContext.put("paymentMethodId", CCDetails.getString("paymentMethodId"));
            inputContext.put("paymentMethodTypeId", CCDetails.getString("paymentMethodTypeId"));
            if (UtilValidate.isNotEmpty(CCDetails.getString("cardNumber"))) {
                inputContext.put("ccNumber", org.fio.homeapps.util.DataUtil.maskCardNumber(CCDetails.getString("cardNumber"), "xxxx-xxxx-xxxx-####"));
            } else {
                inputContext.put("ccNumber", "NO CARD DETAILS AVAILABLE");
            }

            inputContext.put("paymentDate", CCDetails.getString("fromDate"));
            cardType = CCDetails.getString("cardType")
            if (cardType.equals("CCT_VISA")) {
                inputContext.put("cardType", "VISA");
            }
            inputContext.put("expireDate", CCDetails.getString("expireDate"));
        } else {
            inputContext.put("CCDetails", CCDetails);
        }
        if (outstandingAmount.compareTo(BigDecimal.ZERO) == 0) {
            canDoPayment = "N";
        }
        inputContext.put("canDoPayment", canDoPayment);
    }
    activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
    context.put("activeTab", activeTab);

    println("activeTab>>> " + activeTab);
    context.put("inputContext", inputContext);
}