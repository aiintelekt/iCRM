import org.ofbiz.party.contact.ContactMechWorker;
import org.fio.crm.party.PartyContactMechValueMapsSorter
import org.ofbiz.base.util.UtilMisc;

partyId = request.getParameter("partyId");
if (partyId != null) {
    //primaryContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId);
    //context.put("primaryContactInformation",primaryContactInformation);
}

displayContactMechs = request.getAttribute("displayContactMechs") ? request.getAttribute("displayContactMechs") : context.displayContactMechs;
println("````````displayContactMechs```````" + displayContactMechs);
if ((displayContactMechs != null) && (displayContactMechs.equals("Y"))) {
    partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
    Collections.sort(partyContactMechValueMaps, new PartyContactMechValueMapsSorter());

    List userLogins = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId), null, true);
    println("partyId :" + partyId);
    println("partyContactMechValueMaps : " + partyContactMechValueMaps);
    context.put("contactMeches", partyContactMechValueMaps);
    context.put("userLogins", userLogins);
}

context.put("isActUspsAddrVal", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT", "N"));
context.put("tabIdForCurrentTab", request.getParameter("tabId"));