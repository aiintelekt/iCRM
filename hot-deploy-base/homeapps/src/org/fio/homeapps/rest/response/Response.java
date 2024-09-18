/**
 * 
 */
package org.fio.homeapps.rest.response;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.fio.homeapps.util.ParamUtil;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public abstract class Response {

	protected String response_code;
	protected String response_code_desc;
	private String response_ref_id;
	
	protected abstract void doBuild(Map<String, Object> context) throws Exception;
	
	public void build(Map<String, Object> context) throws Exception {
		doBuild(context);
	}
	
	public void prepareContext(Map<String, Object> context) throws Exception {
		
		Delegator delegator = (Delegator) context.get("delegator");
		
		setResponse_code( ParamUtil.getString(context, "responseCode") );
		setResponse_code_desc( ParamUtil.getString(context, "responseCodeDesc") );
		setResponse_ref_id( ParamUtil.getString(context, "responseRefId") );
		
		Map<String, Object> validationMessage = (Map<String, Object>) context.get("validationMessage");
		if (UtilValidate.isNotEmpty(validationMessage)) {
			response_code = "";
			response_code_desc = "";
			
			Set<String> codeList = new LinkedHashSet<String>();
			
			for (String prop : validationMessage.keySet()) {
				String code = (String) validationMessage.get(prop);
				//responseCode += code + " | ";
				codeList.add(code);
				
				GenericValue errorCode = EntityUtil.getFirst( delegator.findByAnd("OfbizApiErrorCode", UtilMisc.toMap("code", code), null, false) );
				if (UtilValidate.isNotEmpty(errorCode) && UtilValidate.isNotEmpty(errorCode.getString("solutionDescription"))) {
					String solutionDescription = errorCode.getString("solutionDescription");
					if (UtilValidate.isNotEmpty(solutionDescription)) {
						solutionDescription = solutionDescription.replace("{mess}", prop);
					}
					response_code_desc += "["+code+"] "+solutionDescription + " | ";
				}
			}
			
			response_code = StringUtil.join(codeList, " | ");
			
			if (UtilValidate.isNotEmpty(response_code_desc)) {
				response_code_desc = response_code_desc.substring(0, response_code_desc.length() - 2);
			}
		} else if (UtilValidate.isNotEmpty(response_code)) {
			response_code_desc = UtilValidate.isNotEmpty(response_code_desc) ? response_code_desc : "";
			GenericValue errorCode = EntityUtil.getFirst( delegator.findByAnd("OfbizApiErrorCode", UtilMisc.toMap("code", response_code), null, false) );
			if (UtilValidate.isNotEmpty(errorCode) && UtilValidate.isNotEmpty(errorCode.getString("solutionDescription"))) {
				String solutionDescription = errorCode.getString("solutionDescription");
				if (UtilValidate.isNotEmpty(solutionDescription)) {
					solutionDescription = solutionDescription.replace("{mess}", response_code_desc);
				}
				response_code_desc = "["+response_code+"] "+solutionDescription;
			}
			
		}
		
	}

	public String getResponse_code() {
		return response_code;
	}

	public void setResponse_code(String responseCode) {
		this.response_code = responseCode;
	}

	public String getResponse_ref_id() {
		return response_ref_id;
	}

	public void setResponse_ref_id(String responseRefId) {
		this.response_ref_id = responseRefId;
	}

	public String getResponse_code_desc() {
		return response_code_desc;
	}

	public void setResponse_code_desc(String responseCodeDesc) {
		this.response_code_desc = responseCodeDesc;
	}
	
}
