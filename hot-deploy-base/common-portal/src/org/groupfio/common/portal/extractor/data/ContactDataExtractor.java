/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.common.portal.extractor.constants.DataConstants;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

/**
 * @author Sharif
 *
 */
public class ContactDataExtractor extends DataExtractor {

	private static String MODULE = ContactDataExtractor.class.getName();
	
	public ContactDataExtractor(Data extractedData) {
		super(extractedData);
	}
	
	@Override
	public Map<String, Object> retrieve(Map<String, Object> context) {
		if (UtilValidate.isNotEmpty(extractedData)) {
			extractedData.retrieve(context);
		}
		return retrieveData(context);
	}

	private Map<String, Object> retrieveData(Map<String, Object> context) {
		System.out.println("Start retrieve Contact Info");
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request"); 
				response = (Map<String, Object>) context.get("response"); 
				Map<String, Object> contactInfoData = new LinkedHashMap<String, Object>();
				
				String partyId = ParamUtil.getString(request, "partyId");
				//Debug.logInfo("Contact Data extractor context : "+context, MODULE);
				if (UtilValidate.isNotEmpty(partyId)) {
					Map<String, String> contactInfo = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, partyId);
					if (UtilValidate.isNotEmpty(contactInfo)) {
						contactInfoData.put(DataConstants.CONTACT_INFO_TAG.get("PHONE"), Objects.toString(contactInfo.get("PrimaryPhone"), ""));
						contactInfoData.put(DataConstants.CONTACT_INFO_TAG.get("MOBILE_PHONE"), Objects.toString(contactInfo.get("MobilePhone"), ""));
						contactInfoData.put(DataConstants.CONTACT_INFO_TAG.get("SECONDARY_PHONE"), Objects.toString(contactInfo.get("SecondaryPhone"), ""));
						contactInfoData.put(DataConstants.CONTACT_INFO_TAG.get("EMAIL_ADDRESS"), Objects.toString(contactInfo.get("EmailAddress"), ""));
						contactInfoData.put(DataConstants.CONTACT_INFO_TAG.get("SKYPE_ID"), Objects.toString(contactInfo.get("SkypeId"), ""));
						contactInfoData.put(DataConstants.CONTACT_INFO_TAG.get("WEB_URL"), Objects.toString(contactInfo.get("webURL"), ""));
						
						response.put("contactInfoData", contactInfoData);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
		}
		
		return response;
	}
}
