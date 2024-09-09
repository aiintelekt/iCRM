/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.common.portal.extractor.SegmentValueFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ValueOverrideType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 *
 */
public class SegmentationDataExtractor extends DataExtractor {

	private static String MODULE = SegmentationDataExtractor.class.getName();
	
	public SegmentationDataExtractor(Data extractedData) {
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
		System.out.println("Start retrieve Segmentation");
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request"); 
				response = (Map<String, Object>) context.get("response"); 
				Map<String, Object> segmentationData = new LinkedHashMap<String, Object>();
				
				String partyId = ParamUtil.getString(request, "partyId");
				//Debug.logInfo("Segmentation Data extractor context : "+context, MODULE);
				if (UtilValidate.isNotEmpty(partyId)) {
					
					List<GenericValue> segmentationTags = (List<GenericValue>) request.get("segmentationTags");
					if (UtilValidate.isNotEmpty(segmentationTags)) {
						for (GenericValue segmentationTag : segmentationTags) {
							String value = null;
							String customFieldGroupId = segmentationTag.getString("customFieldGroupId");
							GenericValue group = EntityQuery.use(delegator).select("valueOverrideType","valueCapture").from("CustomFieldGroup").where("groupId", customFieldGroupId).queryFirst();
							if (UtilValidate.isNotEmpty(group)) {
								String valueOverrideType = group.getString("valueOverrideType");
								String valueCapture = group.getString("valueCapture");
								if (UtilValidate.isNotEmpty(valueOverrideType) && valueOverrideType.equals(ValueOverrideType.GLOBAL_OVERRIDE)) {
									Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
									extractContext.put("delegator", delegator);
									extractContext.put("valueOverrideType", ValueOverrideType.GLOBAL_OVERRIDE);
									extractContext.put("valueCapture", valueCapture);
			            			extractContext.put("customFieldGroupId", customFieldGroupId);
			            			
			            			Map<String, Object> extractResultContext = SegmentValueFacade.extractData(extractContext);
			            			value = (String) extractResultContext.get("value");
								} else if (UtilValidate.isNotEmpty(valueOverrideType) && valueOverrideType.equals(ValueOverrideType.PARTY_OVERRIDE)) {
									Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
									extractContext.put("delegator", delegator);
									extractContext.put("valueOverrideType", ValueOverrideType.PARTY_OVERRIDE);
									extractContext.put("valueCapture", valueCapture);
			            			extractContext.put("customFieldGroupId", customFieldGroupId);
			            			extractContext.put("partyId", partyId);
			            			extractContext.put("isCaptureDefault", "Y");
			            			
			            			Map<String, Object> extractResultContext = SegmentValueFacade.extractData(extractContext);
			            			value = (String) extractResultContext.get("value");
								} else if (UtilValidate.isNotEmpty(valueOverrideType) && valueOverrideType.equals(ValueOverrideType.NO_OVERRIDE)) {
									Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
									extractContext.put("delegator", delegator);
									extractContext.put("valueOverrideType", ValueOverrideType.NO_OVERRIDE);
									extractContext.put("valueCapture", valueCapture);
			            			extractContext.put("customFieldGroupId", customFieldGroupId);
			            			extractContext.put("partyId", partyId);
			            			
			            			Map<String, Object> extractResultContext = SegmentValueFacade.extractData(extractContext);
			            			value = (String) extractResultContext.get("value");
								}
							}
							
							segmentationData.put(segmentationTag.getString("tagId"), Objects.toString(value, ""));
						}
					}
					//Debug.logInfo("segmentationData> "+segmentationData, MODULE);
					response.put("segmentationData", segmentationData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
		}
		
		return response;
	}
}
