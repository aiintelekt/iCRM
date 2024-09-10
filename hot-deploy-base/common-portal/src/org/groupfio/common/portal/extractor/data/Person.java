/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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
public class Person implements Data {
	
	private static String MODULE = Person.class.getName();

	@Override
	public Map<String, Object> retrieve(Map<String, Object> context) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator");
				Map<String, Object> request = (Map<String, Object>) context.get("request");
				response = (Map<String, Object>) context.get("response");
				Map<String, Object> personData = new LinkedHashMap<String, Object>();
				
				String partyId = ParamUtil.getString(request, "partyId");
				if (UtilValidate.isNotEmpty(partyId)) {
					GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
					personData.put(DataConstants.PERSON_TAG.get("PARTY_ID"), Objects.toString(partyId, ""));
					if (UtilValidate.isNotEmpty(person)) {
						personData.put(DataConstants.PERSON_TAG.get("BIRTHDAY"), Objects.toString(person.get("birthDate"), ""));
						personData.put(DataConstants.PERSON_TAG.get("FIRST_NAME"), Objects.toString(person.get("firstName"), ""));
						personData.put(DataConstants.PERSON_TAG.get("LAST_NAME"), Objects.toString(person.get("lastName"), ""));
						personData.put(DataConstants.PERSON_TAG.get("TITLE"), Objects.toString(person.get("personalTitle"), ""));
						
						String name = person.getString("firstName");
						if (UtilValidate.isNotEmpty(person.getString("lastName"))) {
							if (UtilValidate.isNotEmpty(name)) {
								name = name + " " + person.getString("lastName");
							} else {
								name = person.getString("lastName");
							}
						}
						personData.put(DataConstants.PERSON_TAG.get("FULL_NAME"), Objects.toString(name, ""));
						String designation = PartyHelper.getPartyDesignation(partyId, delegator);
						personData.put(DataConstants.PERSON_TAG.get("DESIGNATION"), Objects.toString(designation, ""));
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
