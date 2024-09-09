/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.fio.homeapps.util.ParamUtil;
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
public class Group implements Data {
	
	private static String MODULE = Group.class.getName();

	@Override
	public Map<String, Object> retrieve(Map<String, Object> context) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator");
				Map<String, Object> request = (Map<String, Object>) context.get("request");
				response = (Map<String, Object>) context.get("response");
				Map<String, Object> groupData = new LinkedHashMap<String, Object>();
				
				String partyId = ParamUtil.getString(request, "partyId");
				if (UtilValidate.isNotEmpty(partyId)) {
					GenericValue person = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
					if (UtilValidate.isNotEmpty(person)) {
						groupData.put(DataConstants.GROUP_TAG.get("GROUP_NAME"), Objects.toString(person.get("groupName"), ""));
						groupData.put(DataConstants.GROUP_TAG.get("GROUP_NAME_LOCAL"), Objects.toString(person.get("groupNameLocal"), ""));
						groupData.put(DataConstants.GROUP_TAG.get("GROUP_ID"), Objects.toString(partyId, ""));
						response.put("groupData", groupData);
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
