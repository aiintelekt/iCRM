


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
import org.ofbiz.base.util.Debug;

String invoiceId = request.getParameter("invoiceId");
String invoiceItemSeqId = request.getParameter("invoiceItemSeqId");

inputContext = new LinkedHashMap<String, Object>();

if (UtilValidate.isNotEmpty(invoiceId)) {

	Invoice = from("InvoiceItem").where("invoiceId", invoiceId,"invoiceItemSeqId", invoiceItemSeqId).queryOne();

	if (UtilValidate.isNotEmpty(Invoice)) {
		invoiceItemTypeId = Invoice.getString("invoiceItemTypeId");
		invoiceItemType = EntityQuery.use(delegator).from("InvoiceItemType")
				.where("invoiceItemTypeId",invoiceItemTypeId)
				.queryFirst();
		inputContext.put("invoiceId", invoiceId);
		inputContext.put("invoiceItemTypeId", invoiceItemType.getString("description"));
		inputContext.put("description", Invoice.getString("description"));
		inputContext.put("amount", Invoice.getString("amount"));
		inputContext.put("quantity", Invoice.getString("quantity"));
		
		if (UtilValidate.isNotEmpty(Invoice.getString("productId"))) {
			inputContext.put("productId", Invoice.getString("description"));
		}
	}

}
	
	context.put("inputContext", inputContext);




