/**
 * 
 */
package org.fio.crm.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;

import org.fio.crm.resolver.Resolver;
import org.fio.crm.resolver.ResolverFactory;
import org.fio.crm.resolver.ResolverConstants.ResolverType;
import org.fio.crm.util.ResponseUtils;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
//import org.ofbiz.security.authz.Authorization;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.webapp.stats.VisitHandler;


/**
 * @author sharif
 *
 */
public class LoginWorker {

	public final static String module = LoginWorker.class.getName();
	public final static String LOCKED_USER_KEY = "_LOCKED_USER_";
	
	public static String checkLogin(HttpServletRequest request, HttpServletResponse response) {
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        
        String isLockedUser = (String) session.getAttribute(LOCKED_USER_KEY);
        
        if (UtilValidate.isNotEmpty(isLockedUser) && isLockedUser.equals("TRUE")) {
        	return "locked";
        }
        
        String checkLoginRes = org.ofbiz.webapp.control.LoginWorker.checkLogin(request, response);
        
        if (UtilValidate.isEmpty(session.getAttribute("dataSecurityMetaInfo")) && (UtilValidate.isNotEmpty(checkLoginRes) && checkLoginRes.equals("success"))) {
        	
        	Resolver emplPositionResolver = ResolverFactory.getResolver(ResolverType.EMPL_POSITION);
            
            Map<String, Object> resolverContext = new HashMap<String, Object>();
            resolverContext.put("delegator", delegator);
            resolverContext.put("dispatcher", dispatcher);
            resolverContext.put("userLogin", session.getAttribute("userLogin"));
            
            Map<String, Object> resolverResult = emplPositionResolver.resolve(resolverContext);
            if (ResponseUtils.isSuccess(resolverResult)) {
            	session.setAttribute("dataSecurityMetaInfo", resolverResult);
            }
        	
        }
        
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
                Debug.logInfo("User does not have permission or is flagged as logged out", module);
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
                Debug.logError(e, "Error calling userLogin service", module);
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
                Debug.logInfo("No webapp configuration found for : " + serverId + " / " + contextPath, module);
            }
        } else {
            Debug.logWarning("Received a null Security object from HttpServletRequest", module);
        }

        return true;
    }
	
}
