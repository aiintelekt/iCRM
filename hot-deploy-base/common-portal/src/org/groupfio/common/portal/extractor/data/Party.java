/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.groupfio.common.portal.extractor.constants.DataConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

/**
 * @author Sharif
 *
 */
public class Party implements Data {
	
	private static String MODULE = Party.class.getName();

	@Override
	public Map<String, Object> retrieve(Map<String, Object> context) {
		
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator");
				Map<String, Object> request = (Map<String, Object>) context.get("request");
				response = (Map<String, Object>) context.get("response");
				
				String partyId = ParamUtil.getString(request, "partyId");
				if (UtilValidate.isNotEmpty(partyId)) {
					String roleTypeId = ParamUtil.getString(request, "roleTypeId");
					if (UtilValidate.isEmpty(roleTypeId)) {
						roleTypeId = DataUtil.getPartyRoleTypeId(delegator, partyId);
					}
					
					if (UtilValidate.isNotEmpty(roleTypeId) && (roleTypeId.equals("ACCOUNT") || roleTypeId.equals("LEAD"))) {
						response = new Group().retrieve(context);
						Map<String, Object> primaryContact = org.groupfio.common.portal.util.DataUtil.getPrimaryContact(delegator, partyId, roleTypeId);
						if (UtilValidate.isNotEmpty(primaryContact)) {
							String primaryContactId = (String) primaryContact.get("contactId");
							if (UtilValidate.isNotEmpty(primaryContactId)) {
								Map<String, Object> pcReqContext = new LinkedHashMap<String, Object>();
								pcReqContext.putAll(request);
								pcReqContext.put("partyId", primaryContactId);
								
								Map<String, Object> tempContext = new LinkedHashMap<String, Object>();
								tempContext.putAll(context);
								tempContext.put("request", pcReqContext);
								
								response.putAll(new Person().retrieve(tempContext));
							}
						}
					} else if (UtilValidate.isNotEmpty(roleTypeId) 
							&& (roleTypeId.equals("CONTACT") || roleTypeId.equals("TECHNICIAN") || roleTypeId.equals("CUSTOMER"))
							) {
						response = new Person().retrieve(context);
					} else {
						Map<String, Object> personData = new LinkedHashMap<String, Object>();
						String name = PartyHelper.getPartyName(delegator, partyId, false);
						
						personData.put(DataConstants.PERSON_TAG.get("FULL_NAME"), Objects.toString(name, ""));
						personData.put(DataConstants.PERSON_TAG.get("PARTY_ID"), Objects.toString(partyId, ""));
						response.put("personData", personData);
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
