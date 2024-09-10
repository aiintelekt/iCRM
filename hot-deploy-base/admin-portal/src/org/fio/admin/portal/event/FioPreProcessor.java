package org.fio.admin.portal.event;

import static org.ofbiz.base.util.UtilGenerics.checkMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.admin.portal.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilObject;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.webapp.control.LoginWorker;
import org.ofbiz.webapp.website.WebSiteWorker;

public class FioPreProcessor {
	
	private static final String MODULE = FioPreProcessor.class.getName();
	private FioPreProcessor() {}

	@SuppressWarnings("unchecked")
	public static String setUserLoginSecurityGroup(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		ServletContext servletContext = request.getSession().getServletContext();
    	List<String> securityGroups = new LinkedList<>();
    	List<String> roles = new LinkedList<>();
    	try {
    		if(session==null || !request.isRequestedSessionIdValid()) return "success";
    		if(UtilValidate.isEmpty(userLogin)) userLogin = (GenericValue) request.getAttribute("userLogin");
    		String externalLoginKey = (String) request.getAttribute("externalLoginKey");
    		String url = request.getRequestURL().toString();
    		if(UtilValidate.isEmpty(externalLoginKey))
    			externalLoginKey = (String) session.getAttribute("externalLoginKey");
    		if(UtilValidate.isNotEmpty(userLogin)) {
    			request.setAttribute("userLogin", userLogin);
    			if(UtilValidate.isEmpty(externalLoginKey))
        			externalLoginKey = (String) LoginWorker.getExternalLoginKey(request);
        		request.setAttribute("externalLoginKey", externalLoginKey);
        		
    			securityGroups = UtilValidate.isNotEmpty(session.getAttribute("userLoginSecurityGroups")) ? (List<String>) session.getAttribute("userLoginSecurityGroups") : UtilValidate.isNotEmpty(request.getAttribute("userLoginSecurityGroups")) ? (List<String>) request.getAttribute("userLoginSecurityGroups") : new LinkedList<>();
    			roles = UtilValidate.isNotEmpty(session.getAttribute("userLoginRoles")) ? (List<String>) session.getAttribute("userLoginRoles") : UtilValidate.isNotEmpty(request.getAttribute("userLoginRoles")) ? (List<String>) request.getAttribute("userLoginRoles") : new LinkedList<>();
    			
    			if(UtilValidate.isEmpty(securityGroups) || UtilValidate.isEmpty(roles)) {
    				String userLoginPartyId = userLogin.getString("partyId");
                	Map<String, Object> result = org.fio.homeapps.util.DataHelper.getUserRoleGroup(delegator, userLoginPartyId);
                	if(UtilValidate.isNotEmpty(result)) {
                		request.setAttribute("userLoginSecurityGroups", result.get("userLoginSecurityGroups"));
                    	request.setAttribute("userLoginRoles", result.get("roles"));
                        session.setAttribute("userLoginSecurityGroups", result.get("userLoginSecurityGroups"));
                        session.setAttribute("userLoginRoles", result.get("roles"));
                	}
    			}
    			String landingRedirect = UtilValidate.isNotEmpty(session.getAttribute("landingRedirect")) ? (String) session.getAttribute("landingRedirect") : UtilValidate.isNotEmpty(request.getAttribute("landingRedirect")) ? (String) request.getAttribute("landingRedirect") : "N";
            	if(!"Y".equals(landingRedirect)) {
            		//String endpoint = getDynamicEndpoint(delegator, userLogin);
                	Map<String, Object>  result = getDynamicEndPointData(delegator, userLogin);
                	String endpoint = "";
                	if(UtilValidate.isNotEmpty(result)) {
                		endpoint = (String) result.get("endPoint");
                		List<String> components = (List<String>) result.get("components");
                		String contextPath = "";
                		if (request.getContextPath().length() > 1) {
                			contextPath = request.getContextPath().substring(1);
                        }
                		if(components.contains(contextPath)) {
                			endpoint ="";
                		}
                	}
                	if(UtilValidate.isNotEmpty(endpoint)) {

        	        	HttpServletRequest httpRequest = (HttpServletRequest) request;
        	            HttpServletResponse httpResponse = (HttpServletResponse) response;

        	            // TODO may be need to remove this when it will run in https protocal
        	            httpResponse.setHeader("SET-COOKIE", "JSESSIONID=" + httpRequest.getSession().getId() + "; HttpOnly");
        	            
        	            // Debug.logInfo("Running ContextFilter.doFilter", module);

        	            // ----- Servlet Object Setup -----

        	            // set the webSiteId in the session
        	            if (UtilValidate.isEmpty(httpRequest.getSession().getAttribute("webSiteId"))){
        	                httpRequest.getSession().setAttribute("webSiteId", WebSiteWorker.getWebSiteId(httpRequest));
        	            }

        	            // set the filesystem path of context root.
        	            httpRequest.setAttribute("_CONTEXT_ROOT_", servletContext.getRealPath("/"));

        	            // set the server root url
        	            httpRequest.setAttribute("_SERVER_ROOT_URL_", UtilHttp.getServerRootUrl(httpRequest));

        	            // request attributes from redirect call
        	            String reqAttrMapHex = (String) httpRequest.getSession().getAttribute("_REQ_ATTR_MAP_");
        	            if (UtilValidate.isNotEmpty(reqAttrMapHex)) {
        	                byte[] reqAttrMapBytes = StringUtil.fromHexString(reqAttrMapHex);
        	                Map<String, Object> reqAttrMap = checkMap(UtilObject.getObject(reqAttrMapBytes), String.class, Object.class);
        	                if (reqAttrMap != null) {
        	                    for (Map.Entry<String, Object> entry: reqAttrMap.entrySet()) {
        	                        httpRequest.setAttribute(entry.getKey(), entry.getValue());
        	                    }
        	                }
        	                httpRequest.getSession().removeAttribute("_REQ_ATTR_MAP_");
        	            }
        	            
        	            if (!endpoint.toLowerCase().startsWith("http")) {
	                    } else{
	                    	endpoint ="";
	                    }
        	           
        	            
        	            // set the ServletContext in the request for future use
        	            httpRequest.setAttribute("servletContext", servletContext);
        	            
        	            if (UtilValidate.isEmpty(httpRequest.getSession().getAttribute("userLoginSecurityGroups"))){
        	            	httpRequest.getSession().setAttribute("userLoginSecurityGroups", securityGroups);
        	            }
        	            if (UtilValidate.isEmpty(httpRequest.getSession().getAttribute("userLoginRoles"))){
        	            	httpRequest.getSession().setAttribute("userLoginRoles", roles);
        	            }
                        
                        if(UtilValidate.isNotEmpty(endpoint)) {
        	            	if (UtilValidate.isNotEmpty(externalLoginKey)) {
        	            		String userLoginPartyIdParam = userLogin.getString("partyId");
        	    				String enableViewCustEndpoint = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_VIEW_CUST_ENDPOINT","N");
        	            		if(UtilValidate.isNotEmpty(userLoginPartyIdParam) && endpoint.contains("?partyId") && UtilValidate.isNotEmpty(enableViewCustEndpoint) && enableViewCustEndpoint.equals("Y") ) {
        	            			endpoint = endpoint + userLoginPartyIdParam +"&externalLoginKey="+externalLoginKey;
        	            		}else if(endpoint.contains("?")) {
        	            			endpoint = endpoint + "&externalLoginKey="+externalLoginKey;
        	            		}else {
        	                    	endpoint = endpoint + "?externalLoginKey="+ externalLoginKey;
        	                    }
        	                }
        	            	
        	            	httpRequest.setAttribute("landingRedirect", "Y");
        	            	httpRequest.getSession().setAttribute("landingRedirect", "Y");
        	            	httpResponse.sendRedirect(endpoint);
        	            }
                	}
            	}
			}
    	} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
		}
        return "success";
	}
	
	public static String getDynamicEndpoint(Delegator delegator, GenericValue userLogin) {
		String userLoginPartyId = userLogin.getString("partyId");
		String endPoint = null;
		try {
			String parentTypeId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
	    	if(UtilValidate.isNotEmpty(parentTypeId)) {
	    		List<GenericValue> partyRoleList = DataUtil.getPartyRoles(delegator, userLoginPartyId, parentTypeId);
	        	String securityRole = UtilValidate.isNotEmpty(partyRoleList) ? partyRoleList.get(0).getString("roleTypeId") : "";
	        	
	        	GenericValue securityRoleEndpoint = EntityQuery.use(delegator).from("SecurityRoleEndpoints").where("roleTypeId", securityRole).queryFirst();
	        	String userOverride = null;
	        	if(UtilValidate.isNotEmpty(securityRoleEndpoint)) {
	        		userOverride = securityRoleEndpoint.getString("userOverride");
	        		endPoint = securityRoleEndpoint.getString("endPoint");
	        	}
	        	if("Y".equals(userOverride)) {
	        		GenericValue userPreference = EntityQuery.use(delegator).from("UserPreference").where("userLoginId", userLogin.getString("userLoginId"),"userPrefTypeId","LANDING_PAGE","userPrefGroupTypeId","GLOBAL_PREFERENCES").queryFirst();
	        		endPoint = UtilValidate.isNotEmpty(userPreference) && UtilValidate.isNotEmpty(userPreference.getString("userPrefValue")) ? userPreference.getString("userPrefValue") : endPoint ;
	        	}
	    	}
		} catch (Exception e) {
		}  
		return endPoint;
	}
	
	private static Map<String, Object> getDynamicEndPointData(Delegator delegator, GenericValue userLogin) {
		String userLoginPartyId = userLogin.getString("partyId");
		String endPoint = null;
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String parentTypeId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
	    	if(UtilValidate.isNotEmpty(parentTypeId)) {
	    		List<GenericValue> partyRoleList = DataUtil.getPartyRoles(delegator, userLoginPartyId, parentTypeId);
	        	String securityRole = UtilValidate.isNotEmpty(partyRoleList) ? partyRoleList.get(0).getString("roleTypeId") : "";
	        	
	        	GenericValue securityRoleEndpoint = EntityQuery.use(delegator).from("SecurityRoleEndpoints").where("roleTypeId", securityRole).queryFirst();
	        	String userOverride = null;
	        	if(UtilValidate.isNotEmpty(securityRoleEndpoint)) {
	        		userOverride = securityRoleEndpoint.getString("userOverride");
	        		endPoint = securityRoleEndpoint.getString("endPoint");
	        		String components = securityRoleEndpoint.getString("accessibleComponents");
	        		result.put("components", DataUtil.stringToList(components, ","));
	        		result.put("endPoint", endPoint);
	        		
	        	}
	        	if("Y".equals(userOverride)) {
	        		GenericValue userPreference = EntityQuery.use(delegator).from("UserPreference").where("userLoginId", userLogin.getString("userLoginId"),"userPrefTypeId","LANDING_PAGE","userPrefGroupTypeId","GLOBAL_PREFERENCES").queryFirst();
	        		endPoint = UtilValidate.isNotEmpty(userPreference) && UtilValidate.isNotEmpty(userPreference.getString("userPrefValue")) ? userPreference.getString("userPrefValue") : endPoint ;
	        	}
	    	}
		} catch (Exception e) {
		}  
		return result;
	}
}
