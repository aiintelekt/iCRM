package org.fio.crm.common;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class CrmSegmentServices {
	
	public static final String MODULE = CrmSegmentServices.class.getName();

	public static Map<String, Object>  createSegmentToParty(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String segmentValue = (String) context.get("segmentValue");
        String groupId = (String) context.get("groupId");
        String partyId = (String) context.get("partyId");
        Timestamp now = UtilDateTime.nowTimestamp();
        try {
		 if(UtilValidate.isNotEmpty(segmentValue) && UtilValidate.isNotEmpty(groupId) && UtilValidate.isNotEmpty(partyId)){
         	List<GenericValue> chekSegment=delegator.findByAnd("CustomFieldPartyClassification",UtilMisc.toMap("partyId", partyId,"groupId", groupId,"customFieldId",segmentValue), null, false);
         	if(UtilValidate.isEmpty(chekSegment)){
			 GenericValue storeSegmentValue=delegator.makeValue("CustomFieldPartyClassification");
			storeSegmentValue.set("customFieldId", segmentValue);
			storeSegmentValue.set("groupId", groupId);
			storeSegmentValue.set("partyId", partyId);
			storeSegmentValue.set("inceptionDate", now);
			storeSegmentValue.create();
          }
		 }
        } catch (GeneralException e) {
        	Debug.logInfo("===========ERROR==========="+e.toString(), "");
        }
        
        Map<String,Object> result = new HashMap<String, Object>();
		//result.put("partyId",partyId);
        result.putAll(ServiceUtil.returnSuccess("Segment Value Added Successfully."));
	
	    return result;
    }
	
    public static Map removeSegmentValuetoParty(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	String segmentValue = (String) context.get("segmentValue");
        String groupId = (String) context.get("groupId");
        String partyId = (String) context.get("partyId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue valueParty = EntityUtil.getFirst( delegator.findByAnd("CustomFieldPartyClassification",UtilMisc.toMap("partyId", partyId,"groupId", groupId,"customFieldId",segmentValue), null, false) );
    		
    		if (UtilValidate.isEmpty(valueParty)) {
    			result.putAll(ServiceUtil.returnError("Segment Value not exists!"));
    			return result;
    		}
    		
    		valueParty.remove();
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Segment Value Deleted Successfully."));
    	
    	return result;
    	
    }
}
