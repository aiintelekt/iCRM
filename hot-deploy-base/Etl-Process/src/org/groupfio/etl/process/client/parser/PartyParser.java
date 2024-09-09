/**
 * 
 */
package org.groupfio.etl.process.client.parser;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.client.response.Party;
import org.json.simple.JSONObject;
import org.ofbiz.base.util.UtilValidate;

/**
 * @author Group Fio
 *
 */
public class PartyParser {

	public static Party parseParty(JSONObject response) {
		
		Party party = new Party();
		
		try {
		
			if (UtilValidate.isEmpty(response)) {
				return party;
			}
		
			String partyStatus = ParamUtil.getString(response, "partyStatus");
			String externalAppPartyRef = ParamUtil.getString(response, "externalAppPartyRef");
			String partyId = ParamUtil.getString(response, "partyId");
			
			String responseCode = ParamUtil.getString(response, "responseCode");
			String responseRefId = ParamUtil.getString(response, "responseRefId");
			
			party.setPartyStatus(partyStatus);
			party.setExternalAppPartyRef(externalAppPartyRef);
			party.setPartyId(partyId);
			
			party.setResponseCode(responseCode);
			party.setResponseRefId(responseRefId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return party;
		
	}
	
}
