/**
 * 
 */
package org.fio.admin.portal.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.UtilAttribute;
import org.groupfio.token.app.util.UtilCommon;
import org.groupfio.token.app.util.UtilDecoder;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
//import org.ofbiz.security.authz.Authorization;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.webapp.stats.VisitHandler;

import javolution.util.FastList;
import net.sf.json.JSONObject;


/**
 * @author sharif
 *
 */
public class LoginWorker {

	public final static String MODULE = LoginWorker.class.getName();
	public final static String LOCKED_USER_KEY = "_LOCKED_USER_";
	
	public static String checkLogin(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
        
        String isLockedUser = (String) session.getAttribute(LOCKED_USER_KEY);
        if (UtilValidate.isNotEmpty(isLockedUser) && isLockedUser.equals("TRUE")) {
        	return "locked";
        }
        
        try {
			String token = (String) context.get("token");
			if (UtilValidate.isNotEmpty(token) && UtilValidate.isEmpty(userLogin)) {
				String apiKey = null;
			    String siteKey = null;
			    
			    GenericValue tokenLog = EntityQuery.use(delegator).select("subscriptionId").from("TokenLog").where("tokenKey", token).cache(false).queryFirst();
		    	if (UtilValidate.isNotEmpty(tokenLog)) {
		    		String subscriptionId = tokenLog.getString("subscriptionId");
				    if (UtilValidate.isNotEmpty(subscriptionId)) {
				    	siteKey = UtilAttribute.getSubscriptionAttrValue(delegator, subscriptionId, "SITE_KEY");
				    	apiKey = UtilAttribute.getSubscriptionAttrValue(delegator, subscriptionId, "API_KEY");
				    }
		    	}
			    
			    if (UtilValidate.isEmpty(siteKey) || UtilValidate.isEmpty(apiKey)) {
			    	apiKey = DataUtil.getTenantPropertiesValue(delegator, "security", "common.api.key");
				    siteKey = DataUtil.getTenantPropertiesValue(delegator, "security", "common.site.key");
			    }
			    
			    if (UtilValidate.isEmpty(siteKey) || UtilValidate.isEmpty(apiKey)) {
			    	Debug.logInfo("Site key OR API key not configured", MODULE);
	                org.ofbiz.webapp.control.LoginWorker.doBasicLogout(userLogin, request, response);
	                userLogin = null;
	                session.setAttribute(LOCKED_USER_KEY, null);
	                request.setAttribute("_ERROR_MESSAGE_", "Site key OR API key not configured!");
	                return "error";
			    }
			    
			    JSONObject docodedData = UtilDecoder.decodedJson(siteKey, apiKey, token);
			    if (UtilCommon.isValidToken(docodedData)) {
			    	String userLoginId = docodedData.getString("user_login_id");
			    	GenericValue externalUserLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userLoginId, "enabled", "Y").cache(false).queryFirst();
			    	if (UtilValidate.isNotEmpty(externalUserLogin)) {
			    		boolean shoudLoggedOut = false;
			    		List<String> errorList = new ArrayList<>();
			    		if (!hasBasePermission(externalUserLogin, request)) {
			    			shoudLoggedOut = true;
			    			String errorMess = "User does not have permission";
			    			errorList.add(errorMess);
			                Debug.logInfo(errorMess, MODULE);
			            }
			    		if (isFlaggedLoggedOut(externalUserLogin, externalUserLogin.getDelegator())) {
			    			shoudLoggedOut = true;
			    			String errorMess = "User is flagged as logged out";
			    			errorList.add(errorMess);
			                Debug.logInfo(errorMess, MODULE);
			            }
			    		
			    		if (shoudLoggedOut) {
			    			//org.ofbiz.webapp.control.LoginWorker.doBasicLogout(externalUserLogin, request, response);
			                //userLogin = null;
			    			session.setAttribute(LOCKED_USER_KEY, null);
			                request.setAttribute("_ERROR_MESSAGE_", StringUtil.join(errorList, " and "));
			                return "error";
			    		}
			    		
			    		//externalUserLogin.put("hasLoggedOut", null);
			    		//externalUserLogin.store();
			    		
			    		org.ofbiz.webapp.control.LoginWorker.doBasicLogin(externalUserLogin, request);
			    		
			    		session = request.getSession();
			    		String externalRefererUrl = request.getHeader("referer");
					    session.setAttribute("externalRefererUrl", externalRefererUrl);
					    
			    	} else {
				    	request.setAttribute("_ERROR_MESSAGE_", "User login not valid OR active!");
		                return "error";
				    }
			    } else {
			    	request.setAttribute("_ERROR_MESSAGE_", "Invalid token!");
	                return "error";
			    }
			} else if (UtilValidate.isNotEmpty(userLogin) && isFlaggedLoggedOut(userLogin, delegator)) {
                Debug.logInfo("User is flagged as logged out", MODULE);
                org.ofbiz.webapp.control.LoginWorker.doBasicLogout(userLogin, request, response);
                userLogin = null;
                session.setAttribute(LOCKED_USER_KEY, null);
                request.setAttribute("_ERROR_MESSAGE_", "User is flagged as logged out!");
                return "error";
            }
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
        
        String checkLoginRes = org.ofbiz.webapp.control.LoginWorker.checkLogin(request, response);
        return checkLoginRes;
    }
	
	public static String lock(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String isLockedUser = (String) session.getAttribute(LOCKED_USER_KEY);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        
        String password = request.getParameter("PASSWORD");
        
        if (UtilValidate.isEmpty(userLogin)) {
        	return "requireLogin";
        }
        
        // user is logged in; check to see if they have globally logged out if not
        // check if they have permission for this login attempt; if not log them out
        if (userLogin != null) {
            if (!hasBasePermission(userLogin, request) || org.ofbiz.webapp.control.LoginWorker.isFlaggedLoggedOut(userLogin, userLogin.getDelegator())) {
                Debug.logInfo("User does not have permission or is flagged as logged out", MODULE);
                org.ofbiz.webapp.control.LoginWorker.doBasicLogout(userLogin, request, response);
                userLogin = null;

                // have to reget this because the old session object will be invalid
                session = request.getSession();
                session.setAttribute(LOCKED_USER_KEY, null);
                
                return "requireLogin";
            }
        }
        
        List<String> unpwErrMsgList = FastList.newInstance();
        
        if (UtilValidate.isNotEmpty(isLockedUser) && isLockedUser.equals("TRUE")) {
        	
        	String tryUnlock = request.getParameter("TRY_UNLOCK");
        	if (UtilValidate.isEmpty(tryUnlock)) {
        		return "error";
        	}
        	
        	if (UtilValidate.isEmpty(password)) {
        		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(org.ofbiz.webapp.control.LoginWorker.resourceWebapp, "loginevents.password_was_empty_reenter", UtilHttp.getLocale(request)));
        		return "error";
            }
            
            Map<String, Object> result = null;
            try {
                // get the visit id to pass to the userLogin for history
                String visitId = VisitHandler.getVisitId(session);
                result = dispatcher.runSync("userLogin", UtilMisc.toMap("login.username", userLogin.getString("userLoginId"), "login.password", password, "visitId", visitId, "locale", UtilHttp.getLocale(request)));
            } catch (GenericServiceException e) {
                Debug.logError(e, "Error calling userLogin service", MODULE);
                Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
                String errMsg = UtilProperties.getMessage(org.ofbiz.webapp.control.LoginWorker.resourceWebapp, "loginevents.following_error_occurred_during_login", messageMap, UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
            
            if (ModelService.RESPOND_SUCCESS.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
            	session.setAttribute(LOCKED_USER_KEY, null);
            	request.setAttribute("_SUCCESS_MESSAGE_", "Successfully Unlocked!");
            	return "success";
            }
        	
            request.setAttribute("_ERROR_MESSAGE_", result.get(ModelService.ERROR_MESSAGE));
        }
        
        session.setAttribute(LOCKED_USER_KEY, "TRUE");
        
        return "error";
	}
	
	public static String logout(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
        
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        
        session.setAttribute(LOCKED_USER_KEY, null);
        
        String checkLogoutRes = org.ofbiz.webapp.control.LoginWorker.logout(request, response);
		
		return checkLogoutRes;
	}
	
	protected static boolean hasBasePermission(GenericValue userLogin, HttpServletRequest request) {
        ServletContext context = (ServletContext) request.getAttribute("servletContext");
        //Authorization authz = (Authorization) request.getAttribute("authz");
        Security security = (Security) request.getAttribute("security");

        String serverId = (String) context.getAttribute("_serverId");
        String contextPath = request.getContextPath();

        ComponentConfig.WebappInfo info = ComponentConfig.getWebAppInfo(serverId, contextPath);
        if (security != null) {
            if (info != null) {
                for (String permission: info.getBasePermission()) {
                	if (!"NONE".equals(permission) && !security.hasEntityPermission(permission, "_VIEW", userLogin)) {
                        return false;
                    }
                    /*if (!"NONE".equals(permission) && !security.hasEntityPermission(permission, "_VIEW", userLogin) &&
                            !authz.hasPermission(userLogin.getString("userLoginId"), permission, null)) {
                        return false;
                    }*/
                }
            } else {
                Debug.logInfo("No webapp configuration found for : " + serverId + " / " + contextPath, MODULE);
            }
        } else {
            Debug.logWarning("Received a null Security object from HttpServletRequest", MODULE);
        }

        return true;
    }
	
	public static boolean isFlaggedLoggedOut(GenericValue userLogin, Delegator delegator) {
        if ("true".equalsIgnoreCase(EntityUtilProperties.getPropertyValue("security", "login.disable.global.logout", delegator))) {
            return false;
        }
        if (userLogin == null || userLogin.get("userLoginId") == null) {
            return true;
        }
        
        try {
			userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userLogin.getString("userLoginId")).cache(false).queryFirst();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
        
        return (userLogin.get("hasLoggedOut") != null ?
                "Y".equalsIgnoreCase(userLogin.getString("hasLoggedOut")) : false);
    }
}
