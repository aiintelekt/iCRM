import org.ofbiz.party.contact.ContactMechWorker;
import org.fio.crm.party.PartyContactMechValueMapsSorter
import org.ofbiz.base.util.UtilMisc;
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
import org.ofbiz.accounting.invoice.InvoiceWorker;


invoiceId = request.getParameter("invoiceId");
if (UtilValidate.isNotEmpty(invoiceId)) {
	
	Invoice = from("Invoice").where("invoiceId", invoiceId).queryOne();
	partyId = Invoice.get("partyId");
	if(partyId!=null){
		//primaryContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId);
		//context.put("primaryContactInformation",primaryContactInformation);
	}
	
		partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
		Collections.sort(partyContactMechValueMaps, new PartyContactMechValueMapsSorter());
	
		List userLogins = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId),null,true);
	println("partyId :"+partyId);
	println("partyContactMechValueMaps : "+partyContactMechValueMaps);
		context.put("contactMeches", partyContactMechValueMaps);
		context.put("userLogins", userLogins);
	
	context.put("isActUspsAddrVal", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT", "N"));
}

