/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.common.portal.extractor.constants.DataConstants;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

/**
 * @author Sharif
 *
 */
public class PostalDataExtractor extends DataExtractor {

	private static String MODULE = PostalDataExtractor.class.getName();
	
	public PostalDataExtractor(Data extractedData) {
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
		System.out.println("Start retrieve Postal Address");
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request"); 
				response = (Map<String, Object>) context.get("response"); 
				Map<String, Object> postalData = new LinkedHashMap<String, Object>();
				
				String partyId = ParamUtil.getString(request, "partyId");
				//Debug.logInfo("Postal Data extractor context : "+context, MODULE);
				if (UtilValidate.isNotEmpty(partyId)) {
					GenericValue postalAddress = PartyPrimaryContactMechWorker.getPartyPrimaryPostal(delegator, partyId);
					if (UtilValidate.isNotEmpty(postalAddress)) {
						String address1 = (String)postalAddress.get("address1");
						String address2 = (String)postalAddress.get("address2");
						String city = (String)postalAddress.get("city");
						String zip = (String)postalAddress.get("postalCode");
						String zip_ext = (String)postalAddress.get("postalCodeExt");
						
						if (UtilValidate.isNotEmpty(zip) && UtilValidate.isNotEmpty(zip_ext)) {
							zip_ext = "-"+zip_ext;
						}
						address1 = DataHelper.convertToLabel(address1);
						address2 = DataHelper.convertToLabel(address2);
						city = DataHelper.convertToLabel(city);
						
						postalData.put(DataConstants.POSTAL_TAG.get("ADDRESS_1"), Objects.toString(address1, ""));
						postalData.put(DataConstants.POSTAL_TAG.get("ADDRESS_2"), Objects.toString(address2, ""));
						postalData.put(DataConstants.POSTAL_TAG.get("CITY"), Objects.toString(city, ""));
						postalData.put(DataConstants.POSTAL_TAG.get("ZIP"), Objects.toString(zip, ""));
						postalData.put(DataConstants.POSTAL_TAG.get("ZIP_EXT"), Objects.toString(zip_ext, ""));
						
						String state = postalAddress.getString("stateProvinceGeoId");
						String country = postalAddress.getString("countryGeoId");
						if (UtilValidate.isNotEmpty(state)) {
							state = DataUtil.getGeoName(delegator, state, "STATE,PROVINCE");
						}
						if (UtilValidate.isNotEmpty(country)) {
							country = DataUtil.getGeoName(delegator, country, "COUNTRY");
						}
						
						postalData.put(DataConstants.POSTAL_TAG.get("COUNTRY"), Objects.toString(country, ""));
						postalData.put(DataConstants.POSTAL_TAG.get("STATE"), Objects.toString(state, ""));
						String location = DataUtil.getPartyAttrValue(delegator, partyId, "LOCATION");
						postalData.put(DataConstants.POSTAL_TAG.get("LOCATION"), Objects.toString(location, ""));
						response.put("postalData", postalData);
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
