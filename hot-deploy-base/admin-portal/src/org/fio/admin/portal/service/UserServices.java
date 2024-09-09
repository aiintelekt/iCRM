	package org.fio.admin.portal.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.admin.portal.constant.AdminPortalConstant.UserCreatePermission;
import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.login.LoginServices;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * 
 * @author Mahendran Thanasekaran
 * @since 10-08-2019
 *
 */
public class UserServices {
	private UserServices() {}
	private static final String MODULE = UserServices.class.getName();
	public static final String RESOURCE = "AdminPortalUiLabels";
	
	public static Map<String, Object> createEmployee(DispatchContext dctx, Map<String, Object> context) {
		return context;
	}
	
	@SuppressWarnings("deprecation")
	public static Map<String, Object> createUser(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String location=(String)context.get("location");
		Map<String, Object> results = ServiceUtil.returnSuccess();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			List<String> erroList = new ArrayList<String>();
			Map<String, Object> userLoginContext = new HashMap<String, Object>();	
			//valid the username if username exists in the request parameter
			String userName = (String) context.get("userName");
			GenericValue newUserLogin = null;
			if(UtilValidate.isNotEmpty(userName)) {
				userLoginContext.put("userLoginId", userName);
				if(UtilValidate.isEmpty(context.get("password")))
					context.put("password",userName);
				userLoginContext.put("currentPassword", (String) context.get("password"));
				userLoginContext.put("currentPasswordVerify", context.get("confirmPassword") !=null ? (String) context.get("confirmPassword") : context.get("password"));
				userLoginContext.put("passwordHint", (String) context.get("passwordHint"));
				GenericValue userLogin1 = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId",userName), false);
				if(UtilValidate.isNotEmpty(userLogin1)) {
					return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "PartyUserNameInUse", locale));
				}
				newUserLogin = delegator.makeValue("UserLogin");
				newUserLogin.setPKFields(userLoginContext);
				newUserLogin.setNonPKFields(userLoginContext);
				
				//valid the password
				String password = (String) userLoginContext.get("currentPassword");
                String confirmPassword = (String) userLoginContext.get("currentPasswordVerify");
                String passwordHint = (String) userLoginContext.get("passwordHint");
                LoginServices.checkNewPassword(newUserLogin, null, password, confirmPassword, passwordHint, erroList, true, locale);
                if(erroList!=null && erroList.size() > 0) {
                	return ServiceUtil.returnError(erroList);
                }
			}
			
			//create new person
			// automatically set the parameters
            ModelService createPersonService = dctx.getModelService("createPerson");
            Map<String, Object> personContext = createPersonService.makeValid(context, ModelService.IN_PARAM);
            personContext.put("nationality", context.get("nationality"));
            personContext.put("occupation", context.get("occupationalGroup"));
            if(UtilValidate.isNotEmpty(context.get("birthDate")))
            	personContext.put("birthDate", DataUtil.convertDateTimestamp((String) context.get("birthDate"), df, DateTimeTypeConstant.DATE, DateTimeTypeConstant.SQL_DATE));
            	//personContext.put("birthDate",context.get("birthDate"));
            	Map<String, Object> createPersonResult = dispatcher.runSync("createPerson", personContext);
            if (ServiceUtil.isError(createPersonResult) || ServiceUtil.isFailure(createPersonResult)) {
                return createPersonResult;
            }
			String partyId = (String) createPersonResult.get("partyId");
			
			// update location
			if (UtilValidate.isNotEmpty(partyId)) {
				String partyIdentification=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, location, "LOCATION");
			}
			
			//create user login and invoke createUserLoginPasswordHistory to track 
			if(UtilValidate.isNotEmpty(newUserLogin)) {
				String checkLoginUserPermission = EntityUtilProperties.getPropertyValue("admin-portal.properties", "check.user.login.permission", delegator);
				if("true".equals(checkLoginUserPermission)) {
					if(!security.hasPermission(UserCreatePermission.CREATE, userLogin)) {
						return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "UserDoNotHavePermission", locale));
					}
				}
				newUserLogin.set("partyId", partyId);
				 boolean useEncryption = "true".equals(EntityUtilProperties.getPropertyValue("security", "password.encrypt", delegator));
	                if (useEncryption) { newUserLogin.set("currentPassword", org.ofbiz.base.crypto.HashCrypt.getDigestHash((String) newUserLogin.get("currentPassword"))); }
				newUserLogin.set("isLdapUser", (String) context.get("isLdapUser"));
				newUserLogin.set("enabled", (String) context.get("userStatus"));
				newUserLogin.create();
			}
			
			Map<String, Object>  partyRoleContext = new HashMap<String, Object>();
			partyRoleContext.put("partyId", partyId);
			partyRoleContext.put("roleTypeId", (String) context.get("roleTypeId"));
			partyRoleContext.put("userLogin", userLogin);
			Map<String, Object> partyRoleResult =  dispatcher.runSync("createPartyRole", partyRoleContext);
			if (ServiceUtil.isError(partyRoleResult) || ServiceUtil.isFailure(partyRoleResult)) {
                return partyRoleResult;
            }
			//userLogin = newUserLogin;
			//create contact info about the user
			if(UtilValidate.isNotEmpty(context.get("address1")) || UtilValidate.isNotEmpty(context.get("address2")) || UtilValidate.isNotEmpty(context.get("address3")) || UtilValidate.isNotEmpty(context.get("city"))) {
				ModelService createAddressService = dctx.getModelService("createPartyPostalAddress");
	            Map<String, Object> addressContext = createAddressService.makeValid(context, ModelService.IN_PARAM);
	            String fullName = context.get("firstName") !=null ? (String) context.get("firstName") :"";
	            fullName = fullName + (context.get("middleName") !=null ? " "+(String) context.get("middleName") :"");
	            fullName = fullName + (context.get("lastName") !=null ? " "+(String) context.get("lastName") :"");
	            String addressSolicitation = context.get("addressSolicitation") != null ?(String) context.get("addressSolicitation") : "Y";
	            String contactMechPurposeTypeId = context.get("addressContactTypeId") != null ? (String) context.get("addressContactTypeId") : "PRIMARY_LOCATION";
	            addressContext.put("toName", fullName);
	            addressContext.put("partyId", partyId);
	            addressContext.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
	            addressContext.put("allowSolicitation", addressSolicitation);
	            addressContext.put("userLogin", userLogin);
	            Map<String, Object> createAddressResult = dispatcher.runSync("createPartyPostalAddress", addressContext);
	            if (ServiceUtil.isError(createAddressResult) || ServiceUtil.isFailure(createAddressResult)) {
	                return createAddressResult;
	            }
	          //Create purpose type
	           /*String contactMechId = (String) createAddressResult.get("contactMechId");
	            Map<String, Object> addressPurposeContext = new HashMap<String, Object>();
	            addressPurposeContext.put("partyId", partyId);
	            addressPurposeContext.put("contactMechPurposeTypeId", "HOME_LOCATION");
	            addressPurposeContext.put("contactMechId", contactMechId);
	            addressPurposeContext.put("userLogin", userLogin);
	            createAddressResult = dispatcher.runSync("createPartyContactMechPurpose", addressPurposeContext);
	            if (ServiceUtil.isError(createAddressResult) || ServiceUtil.isFailure(createAddressResult)) {
	                return createAddressResult;
	            } 
	            */
			}
			
            //create phone conact
			if(UtilValidate.isNotEmpty(context.get("contactNumber"))) {
				String contactMechPurposeTypeId = context.get("phoneContactTypeId") != null ?(String) context.get("phoneContactTypeId") : "PRIMARY_PHONE";
				String phoneSolicitation = context.get("phoneSolicitation") != null ?(String) context.get("phoneSolicitation") : "Y";
				ModelService createPhoneService = dctx.getModelService("createPartyTelecomNumber");
	            Map<String, Object> phoneContext = createPhoneService.makeValid(context, ModelService.IN_PARAM);
	            phoneContext.put("allowSolicitation", phoneSolicitation);
	            phoneContext.put("userLogin", userLogin);
	            phoneContext.put("partyId", partyId);
	            Map<String, Object> createPhoneResult = dispatcher.runSync("createPartyTelecomNumber", phoneContext);
	            if (ServiceUtil.isError(createPhoneResult) || ServiceUtil.isFailure(createPhoneResult)) {
	                return createPhoneResult;
	            }
	            //Create purpose type
	            String contactMechId = (String) createPhoneResult.get("contactMechId");
	            Map<String, Object> phonePurposeContext = new HashMap<String, Object>();
	            phonePurposeContext.put("partyId", partyId);
	            phonePurposeContext.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
	            phonePurposeContext.put("contactMechId", contactMechId);
	            phonePurposeContext.put("userLogin", userLogin);
	            createPhoneResult = dispatcher.runSync("createPartyContactMechPurpose", phonePurposeContext);
	            if (ServiceUtil.isError(createPhoneResult) || ServiceUtil.isFailure(createPhoneResult)) {
	                return createPhoneResult;
	            }
			}
			
			//creaet email
			if(UtilValidate.isNotEmpty(context.get("emailAddress"))) {
				String contactMechPurposeTypeId = context.get("emailContactTypeId") != null ? (String) context.get("emailContactTypeId"):"PRIMARY_EMAIL";
				String emailId = (String) context.get("emailAddress");
				String emailSolicitation = context.get("emailSolicitation") != null ?(String) context.get("emailSolicitation") : "Y";
	            Map<String, Object> emailContext = new HashMap<String, Object>();
	            emailContext.put("emailAddress", emailId);
	            emailContext.put("allowSolicitation", emailSolicitation);
	            emailContext.put("userLogin", userLogin);
	            emailContext.put("partyId", partyId);
	            
	            Map<String, Object> createEmailResult = dispatcher.runSync("createPartyEmailAddress", emailContext);
	            if (ServiceUtil.isError(createEmailResult) || ServiceUtil.isFailure(createEmailResult)) {
	                return createEmailResult;
	            }
	          //Create purpose type
	            String contactMechId = (String) createEmailResult.get("contactMechId");
	            Map<String, Object> emailPurposeContext = new HashMap<String, Object>();
	            emailPurposeContext.put("partyId", partyId);
	            emailPurposeContext.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
	            emailPurposeContext.put("contactMechId", contactMechId);
	            emailPurposeContext.put("userLogin", userLogin);
	            createEmailResult = dispatcher.runSync("createPartyContactMechPurpose", emailPurposeContext);
	            if (ServiceUtil.isError(createEmailResult) || ServiceUtil.isFailure(createEmailResult)) {
	                return createEmailResult;
	            }
			}
			/*
			Map<String, Object> createSecGrpMap = new HashMap<String, Object>();
			createSecGrpMap.put("userLoginId", userName);
			createSecGrpMap.put("groupId", context.get("securityGroupId"));
			createSecGrpMap.put("fromDate", UtilDateTime.nowTimestamp());
			createSecGrpMap.put("userLogin", userLogin);

			Map<String, Object> createSecGrpResult;
			try {
				createSecGrpResult = dispatcher.runSync("addUserLoginToSecurityGroup", createSecGrpMap);
			} catch (GenericServiceException e) {
				Debug.logError(e.getMessage(), MODULE);
			} */
			
			results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "UserCreatedSuccessfully", locale));
			results.put("partyId", partyId);
			results.put("userLoginId", userName);
		} catch (Exception e) {
			e.printStackTrace();
			results = ServiceUtil.returnError("Error : "+e.getMessage());
		}
		return results;
	}
	
	public static Map<String, Object> addPartyRole(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> results = new HashMap<String, Object>();
    	String partyId = (String) context.get("partyId");
    	String userLoginId = (String) context.get("userLoginId");
    	String parentTypeId = (String) context.get("parentTypeId");
    	String selectedRows = (String) context.get("selectedRowsUser");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	try {
    		if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(selectedRows)) {
    			List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
    			if(UtilValidate.isNotEmpty(selectedRows))
    				requestMapList = DataUtil.convertToListMap(selectedRows);
    			if(UtilValidate.isNotEmpty(requestMapList)) {
    				if(UtilValidate.isEmpty(parentTypeId)) {
	                    parentTypeId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
	                }
	                List<GenericValue> roleList = DataUtil.getPartyRoles(delegator, partyId, parentTypeId);
	                if(UtilValidate.isNotEmpty(roleList)) {
	                   delegator.removeAll(roleList);
	                }
    				for(Map<String, Object> requestMap : requestMapList) {
    					String roleTypeId = (String) requestMap.get("roleTypeId");
    					parentTypeId = (String) requestMap.get("parentTypeId");
    					context.put("roleTypeId", roleTypeId);
    					context.put("createdOn", UtilDateTime.nowTimestamp());
    					context.put("createdBy", userLogin.getString("userLoginId"));
    					
    	    			results = dispatcher.runSync("createPartyRole", context);
    	    			
    	    			if(ServiceUtil.isSuccess(results)) {
    	    				GenericValue partyGv =  EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryFirst();
    	    				if(UtilValidate.isNotEmpty(partyGv)) {
    	    					partyGv.set("roleTypeId", roleTypeId);
    	    					partyGv.store();
    	    				}
    	    			}
    				}
    			}
    			
    			if(ServiceUtil.isSuccess(results))
    				results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "PartyRoleSuccessfullyCreated", locale));
    			else
    				results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "PartyRoleCreationFailed", locale));
    		}
    		Debug.log("Results : "+results, MODULE);
    	} catch (Exception e) {
    		e.printStackTrace();
    		results = ServiceUtil.returnError("Error : "+e.getMessage());
    	}
    	results.put("userLoginId", userLoginId);
    	return results;
    }
	
	public static Map<String, Object> removePartyRole(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> results = new HashMap<String, Object>();
    	String partyId = (String) context.get("partyId");
    	String roleTypeId = (String) context.get("roleTypeId");
    	try {
    		if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeId)) {
    			results = dispatcher.runSync("deletePartyRole", context);
    			if(ServiceUtil.isSuccess(results))
    				results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "PartyRoleSuccessfullyRemoved", locale));
    			else
    				results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "PartyRoleDeletionFailed", locale));
    		}
    		Debug.log("Results : "+results, MODULE);
    	} catch (Exception e) {
    		e.printStackTrace();
    		results = ServiceUtil.returnError("Error : "+e.getMessage());
    	}
    	return results;
    }
	
	public static String addPartyRoles(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String requestData = DataUtil.getJsonStrBody(request);
		String userLoginId = request.getParameter("userLoginId");
		String partyId = request.getParameter("partyId");
		String selectedRows = request.getParameter("selecteddRows");
    	
    	try {
    		if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(selectedRows)) {
    			List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
    			if(UtilValidate.isNotEmpty(selectedRows))
    				requestMapList = DataUtil.convertToListMap(selectedRows);
    			if(UtilValidate.isNotEmpty(requestMapList)) {
    				for(Map<String, Object> requestMap : requestMapList) {
    					Map<String,Object> inputMap = FastMap.newInstance();
    					String roleTypeId = (String) requestMap.get("roleTypeId");
    					inputMap.put("partyId", partyId);
    					inputMap.put("roleTypeId", roleTypeId);
    					inputMap.put("createdOn", UtilDateTime.nowTimestamp());
    					inputMap.put("createdBy", userLogin.getString("userLoginId"));
    					inputMap.put("userLogin", userLogin);
    					
    	    			results = dispatcher.runSync("createPartyRole", inputMap);
    	    			
    	    			if(ServiceUtil.isSuccess(results)){
    	    				GenericValue securityGroupRoleTypeAssoc = EntityUtil.getFirst(EntityQuery.use(delegator).select("groupId").from("SecurityGroupRoleTypeAssoc").where("roleTypeId", roleTypeId).queryList());
    	    				if (UtilValidate.isNotEmpty(securityGroupRoleTypeAssoc) && UtilValidate.isNotEmpty(securityGroupRoleTypeAssoc.getString("groupId"))) {
    	    					String groupId = securityGroupRoleTypeAssoc.getString("groupId");
    	    					GenericValue userLoginSecurityGroup = EntityUtil.getFirst(EntityQuery.use(delegator).from("UserLoginSecurityGroup").where("userLoginId", userLoginId, "groupId", groupId).queryList());
        	    				if (UtilValidate.isEmpty(userLoginSecurityGroup)) {
        	    					
        	    					userLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup");
        							userLoginSecurityGroup.put("userLoginId", userLoginId);
        							userLoginSecurityGroup.put("groupId", groupId);
        							userLoginSecurityGroup.put("fromDate", UtilDateTime.nowTimestamp());
        							delegator.create(userLoginSecurityGroup);

        	    				}
    	    				}
    	    			}
    				}
    				request.setAttribute("userLoginId", userLoginId);
					request.setAttribute("_EVENT_MESSAGE_", "Roles Added Successfully to User : "+userLoginId);
    			}
    		}
    		Debug.log("Results : "+results, MODULE);
    	} catch (Exception e) {
    		String errMsg = "Problem While Adding Roles to User " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
    	}
    	return "success";
	}
	
	public static String resetPassword(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		String username = request.getParameter("USERNAME");

		try {

			if(UtilValidate.isNotEmpty(username)) {

				GenericValue userLoginCheck = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", username).queryOne();

				if(UtilValidate.isNotEmpty(userLoginCheck)) {
					request.setAttribute("userLoginId", username);
					userLoginCheck.set("requirePasswordChange", "Y");
					userLoginCheck.store();
				} else {
					request.setAttribute("userLoginId", username);
					request.setAttribute("_ERROR_MESSAGE_", "Please Enter Valid User Name : "+username);
					return "error";
				}
			}
			else {
				
				request.setAttribute("_ERROR_MESSAGE_", "Please Enter User Login Id  "+username);
				return "error";
			}

		} catch (Exception e) {
			String errMsg = "Problem While Fetching User Login " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}
	
	public static String updatePassword(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		String username = request.getParameter("userLoginId");
		String newPassword = request.getParameter("newPassword");
		String newPasswordVerify = request.getParameter("newPasswordVerify");
		String oneTimePassword = request.getParameter("oneTimePassword");
		GenericValue userLogin = null;
		Locale locale = UtilHttp.getLocale(request);
		boolean checkPwd = true;
		try {
			//check otp entered with the db 
			if(UtilValidate.isNotEmpty(oneTimePassword)){
				boolean useEncryption = "true".equals(EntityUtilProperties.getPropertyValue("security", "password.encrypt", delegator));	
				boolean isServiceAuth = false;
				userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", username).cache(isServiceAuth).queryOne();
				
				//check if require password change
				String requirePasswordChange = userLogin.getString("requirePasswordChange");
				if(UtilValidate.isNotEmpty(requirePasswordChange)){
					if(requirePasswordChange.equals("N") || UtilValidate.isEmpty(requirePasswordChange)){
						request.setAttribute("userLoginId", username);
						request.setAttribute("_ERROR_MESSAGE_", "Reset Password is Already Done: "+username);
						return "error";
					}
				}
				
				checkPwd = checkPassword(userLogin.getString("currentPassword"), useEncryption, oneTimePassword);
				if(!checkPwd) {
					String errMsg = "";
					Debug.logInfo("[LoginServices.userLogin] : Password Incorrect", MODULE);
					// password invalid...
					// errMsg = UtilProperties.getMessage(RESOURCE,"loginservices.password_incorrect", locale);
					request.setAttribute("userLoginId", username);
					request.setAttribute("_ERROR_MESSAGE_", "Temp Password is Incorrect: "+username);
					return "error";
				}
			}

			if(UtilValidate.isNotEmpty(newPassword) && UtilValidate.isNotEmpty(newPasswordVerify) && checkPwd){
				if(newPassword.equals(newPasswordVerify)){
					if(UtilValidate.isNotEmpty(username)) {
						GenericValue userLoginCheck = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", username).queryOne();
						if(UtilValidate.isNotEmpty(userLoginCheck)) {
							userLoginCheck.set("currentPassword", org.ofbiz.base.crypto.HashCrypt.getDigestHash(newPassword));
							userLoginCheck.set("enabled", "Y");
							userLoginCheck.set("requirePasswordChange", "N");
							userLoginCheck.set("hasLoggedOut", "Y");
							userLoginCheck.set("disabledDateTime", null);
							userLoginCheck.store();
						}
						request.setAttribute("_EVENT_MESSAGE_", "Password Updated Successfully for User : "+username);
					}
				}else{
					request.setAttribute("userLoginId", username);
					request.setAttribute("_ERROR_MESSAGE_", "New Password and Confirm Password Must be Same : "+username);
					return "error";
				}
			}

		} catch (Exception e) {
			String errMsg = "Problem While Fetching User Login " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}
	private static boolean checkPassword(String oldPassword, boolean useEncryption, String currentPassword) {
		boolean passwordMatches = false;
		if (oldPassword != null) {
			if (useEncryption) {
				passwordMatches = HashCrypt.comparePassword(oldPassword,
						org.ofbiz.common.login.LoginServices.getHashType(), currentPassword);
			} else {
				passwordMatches = oldPassword.equals(currentPassword);
			}
		}
		if (!passwordMatches
				&& "true".equals(UtilProperties.getPropertyValue("security", "password.accept.encrypted.and.plain"))) {
			passwordMatches = currentPassword.equals(oldPassword);
		}
		return passwordMatches;
	}
	public static Map < String, Object > updateUser(DispatchContext dctx, Map < String, ? > context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map < String, Object > serviceResults = null;
		Map < String, Object > results = ServiceUtil.returnSuccess();
		Map<String, Object> res = null;
		Map<String, Object> result = new HashMap<String, Object>();
		String userName = (String) context.get("userName");
		String contactMechId = (String) context.get("contactMechId");
		String partyId = (String) context.get("userPartyId");
		String city = (String) context.get("generalCity");
		String stateProvinceGeoId = (String) context.get("stateGeoId");
		String address1 = (String) context.get("generalAddress1");
		String address2 = (String) context.get("generalAddress2");
		String countryGeoId = (String) context.get("generalCountryGeoId"); 
		String postalCode = (String) context.get("generalPostalCode");
		String postalCodeExt = (String) context.get("generalPostalCodeExt");
		String attnName = (String) context.get("generalAttnName");
		String phoneNumber=(String)context.get("primaryPhoneNumber");
		String extension = (String)context.get("extension");
		String email=(String)context.get("primaryEmail");
		String emailMechId=(String)context.get("emailContactMechId");
		String telePhoneMechId=(String)context.get("contactNumberContactMechId");
		String userStatus = (String)context.get("userStatus");
		String location=(String)context.get("location");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			ModelService createPersonService = dctx.getModelService("updatePerson");
			Map<String, Object> personContext = createPersonService.makeValid(context, ModelService.IN_PARAM);
			personContext.put("partyId", partyId);
			personContext.put("occupation", context.get("occupationalGroup"));
			if(UtilValidate.isNotEmpty(context.get("bDate"))) {
			personContext.put("birthDate", DataUtil.convertDateTimestamp((String) context.get("bDate"), df, DateTimeTypeConstant.DATE, DateTimeTypeConstant.SQL_DATE));
			}
			Map<String, Object> createPersonResult = dispatcher.runSync("updatePerson", personContext);
			if (ServiceUtil.isError(createPersonResult) || ServiceUtil.isFailure(createPersonResult)) {
				return createPersonResult;
			}
			// create PartySupplementalData
			GenericValue partyData = EntityQuery.use(delegator).from("PartySupplementalData").where("partyId", partyId).queryOne();
			if(UtilValidate.isNotEmpty(partyData)) {
				partyData.setNonPKFields(context);
				partyData.store();
			}
			//update User login Status
			GenericValue uLogin = EntityQuery.use(delegator).from("UserLogin")
					.where("userLoginId", userName).queryFirst();
			if(UtilValidate.isNotEmpty(uLogin)){
				
				if(UtilValidate.isNotEmpty(userStatus) && userStatus.equals("Y")) {
					uLogin.put("enabled", "Y");
					uLogin.put("disabledDateTime",null);
					uLogin.store();
				}
				if(UtilValidate.isNotEmpty(userStatus) && userStatus.equals("N")) {
					Timestamp disabledDateTime = UtilDateTime.nowTimestamp();
					uLogin.put("enabled", "N");
					uLogin.put("disabledDateTime",disabledDateTime);
					uLogin.store();
				}
			}
			
			// update location
			if (UtilValidate.isNotEmpty(partyId)) {
				String partyIdentification=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, location, "LOCATION");
			}
			//update postal address
			if(UtilValidate.isNotEmpty(contactMechId)) {
				Map<String, Object> postalDetails = null;
				postalDetails = dispatcher.runSync("updatePostalAddressData", UtilMisc.toMap("partyId", partyId,
						"contactMechId", contactMechId, "userLogin", userLogin, "locale", locale, 
						"city", city,"address1", address1,"address2", address2,"postalCode", postalCode,"postalCodeExt", postalCodeExt,"countryGeoId", countryGeoId,"stateProvinceGeoId",stateProvinceGeoId
						));
				if (ServiceUtil.isError(postalDetails)) {
					return postalDetails;
				}	 
			} else {
				if(UtilValidate.isNotEmpty(address1) || UtilValidate.isNotEmpty(address2)  || UtilValidate.isNotEmpty(city)) {
					ModelService createAddressService = dctx.getModelService("createPartyPostalAddress");
					Map<String, Object> addressContext = createAddressService.makeValid(context, ModelService.IN_PARAM);
					String fullName = context.get("firstName") !=null ? (String) context.get("firstName") :"";
					fullName = fullName + (context.get("middleName") !=null ? " "+(String) context.get("middleName") :"");
					fullName = fullName + (context.get("lastName") !=null ? " "+(String) context.get("lastName") :"");
					String contactMechPurposeTypeId = context.get("addressContactTypeId") != null ? (String) context.get("addressContactTypeId") : "PRIMARY_LOCATION";
					addressContext.put("toName", fullName);
					addressContext.put("attnName", attnName);
					addressContext.put("partyId", partyId);
					addressContext.put("address1", address1);
					addressContext.put("address2", address2);
					addressContext.put("city", city);
					addressContext.put("postalCodeExt", postalCodeExt);
					addressContext.put("countryGeoId", countryGeoId);
					addressContext.put("stateProvinceGeoId", stateProvinceGeoId);
					addressContext.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
					addressContext.put("allowSolicitation", "Y");
					addressContext.put("userLogin", userLogin);
					Map<String, Object> createAddressResult = dispatcher.runSync("createPartyPostalAddress", addressContext);
					if (ServiceUtil.isError(createAddressResult) || ServiceUtil.isFailure(createAddressResult)) {
						return createAddressResult;
					}
				}
			}
			//Update email
			if(UtilValidate.isNotEmpty(emailMechId) && UtilValidate.isNotEmpty(email)){
				Map<String, Object> resEmailMap = new HashMap<String, Object>();
				Map<String, Object> emailUpdateMap = new HashMap<String, Object>();
				emailUpdateMap.put("partyId", partyId);
				emailUpdateMap.put("contactMechId", emailMechId);
				emailUpdateMap.put("emailAddress", email);
				emailUpdateMap.put("userLogin", userLogin);
				emailUpdateMap.put("allowSolicitation", "Y");
				resEmailMap = dispatcher.runSync("updatePartyEmailAddress", emailUpdateMap);
				if (ServiceUtil.isError(resEmailMap)) {
					return resEmailMap;
				}
			}else{
				if(UtilValidate.isNotEmpty(email)){

					Map<String, Object> resultVal = null;
					Map<String, Object> input = UtilMisc.toMap("partyId", partyId, "userLogin", userLogin,
							"contactMechPurposeTypeId", "PRIMARY_EMAIL", "allowSolicitation", "Y");
					input.put("emailAddress", email);
					input.put("emailValidInd", "Y");
					resultVal = dispatcher.runSync("createPartyEmailAddress", input);
					if (ServiceUtil.isError(resultVal)) {
						return resultVal;
					}
				}
			}
			//Updating phone number
			if(UtilValidate.isNotEmpty(telePhoneMechId)){
				Map < String, Object > input = UtilMisc. < String, Object > toMap();
				input.put("partyId", partyId);
				input.put("contactNumber", phoneNumber);
				input.put("extension", extension);
				input.put("userLogin", userLogin);
				input.put("contactMechId", telePhoneMechId);
				input.put("allowSolicitation", "Y");
				Map < String, Object > teleserviceResults = null;
				teleserviceResults = dispatcher.runSync("updatePartyTelecomNumber", input);
				if (ServiceUtil.isError(teleserviceResults)) {
					return teleserviceResults;
				}
			}else{
				if(UtilValidate.isNotEmpty(phoneNumber)){
					Map < String, Object > inputPhone = UtilMisc.toMap("userLogin", userLogin, "contactNumber", phoneNumber, "extension", extension, "partyId", partyId, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PRIMARY_PHONE", "allowSolicitation", "Y");
					Map < String, Object > serviceResultsPhone = dispatcher.runSync("createPartyTelecomNumber", inputPhone);
					if (ServiceUtil.isError(serviceResultsPhone)) {
						return serviceResultsPhone;
					}
				}
			}
			results = ServiceUtil.returnSuccess("User Updated Successfully : "+userName+".");
			//results.put("partyId", partyId);
			results.put("userLoginId", userName);

		}catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		//results.put("partyId", partyId);
		results.put("userLoginId", userName);
		return results;
	}
	
	public static String createTechnicianRate(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String partyId = (String) context.get("partyId");
		String rateTypeId = (String) context.get("rateTypeId");
		String currencyUomId = (String) context.get("currencyUomId");
		String defaultRate = (String) context.get("defaultRate");
		BigDecimal rateAmount = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(context.get("rate"))) {
			rateAmount = new BigDecimal((String)context.get("rate"));
		}
		
		String fromDate = (String) context.get("fromDate");
		String thruDate = (String) context.get("thruDate");
		
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		
		try {
			
			Timestamp fromDateTime = null;
			Timestamp thruDateTime = null;

			if (UtilValidate.isNotEmpty(fromDate)) {
				fromDate = df1.format(df2.parse(fromDate));
				fromDateTime = UtilDateTime.getDayStart(Timestamp.valueOf(fromDate));
			}
			if (UtilValidate.isNotEmpty(thruDate)) {
				thruDate = df1.format(df2.parse(thruDate));
				thruDateTime = UtilDateTime.getDayEnd(Timestamp.valueOf(thruDate));
			}
			
			if(UtilValidate.isNotEmpty(partyId)){
				GenericValue partyRate = delegator.makeValue("PartyRate");
				partyRate.set("partyId", partyId);
				partyRate.set("rateTypeId", rateTypeId);
				partyRate.set("currencyUomId", currencyUomId);
				partyRate.set("defaultRate", defaultRate);
				partyRate.set("rate", rateAmount);
				partyRate.set("fromDate", fromDateTime);
				partyRate.set("thruDate", thruDateTime);
				partyRate.create();
				request.setAttribute("partyId",partyId);
				request.setAttribute("_EVENT_MESSAGE_", "Technician Rate Added Successfully");
			}
			
		}catch (Exception e) {
			String errMsg = "Problem While Creating Party Rate " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}
	
	public static String updateTechnicianRate(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String partyId = (String) context.get("partyId");
		String rateTypeId = (String) context.get("rateTypeId");
		String currencyUomId = (String) context.get("currencyUomId");
		String defaultRate = (String) context.get("defaultRate");
		BigDecimal rateAmount = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(context.get("rate"))) {
			rateAmount = new BigDecimal((String)context.get("rate"));
		}
		
		String fromDate = (String) context.get("fromDate");
		String thruDate = (String) context.get("thruDate");
		
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		
		try {

			Timestamp fromDateTime = null;
			Timestamp thruDateTime = null;

			if (UtilValidate.isNotEmpty(fromDate)) {
				String fromDateStr = df1.format(df2.parse(fromDate));
				fromDateTime = UtilDateTime.getDayStart(Timestamp.valueOf(fromDateStr));
			}
			if (UtilValidate.isNotEmpty(thruDate)) {
				String thruDateStr = df1.format(df2.parse(thruDate));
				thruDateTime = UtilDateTime.getDayEnd(Timestamp.valueOf(thruDateStr));
			}
			
			if(UtilValidate.isNotEmpty(partyId)){
				GenericValue partyRate = EntityQuery.use(delegator).from("PartyRate").where("partyId", partyId, "rateTypeId", rateTypeId, "currencyUomId", currencyUomId, "fromDate", fromDateTime).queryOne();
				
				if(UtilValidate.isNotEmpty(partyRate)) {
					partyRate.set("thruDate", thruDateTime);
					partyRate.store();
					request.setAttribute("partyId",partyId);
					request.setAttribute("rateTypeId",rateTypeId);
					request.setAttribute("uomId",currencyUomId);
					request.setAttribute("fromDate",fromDate);
					request.setAttribute("_EVENT_MESSAGE_", "Technician Rate Updated Successfully");
				}
			}

		}catch (Exception e) {
			String errMsg = "Problem While Updating Party Rate " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}
	
	public static String updateUserPassword(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		String username = request.getParameter("userLoginId");
		String newPassword = request.getParameter("password");
		String confirmpassword = request.getParameter("confirmpassword");
		String oldPassword = request.getParameter("oldPassword");
		GenericValue userLogin = null;
		Locale locale = UtilHttp.getLocale(request);
		boolean checkPwd = true;
		try {
			//check otp entered with the db 
			if(UtilValidate.isNotEmpty(oldPassword)){
				boolean useEncryption = "true".equals(EntityUtilProperties.getPropertyValue("security", "password.encrypt", delegator));	
				boolean isServiceAuth = false;
				userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", username).cache(isServiceAuth).queryOne();
				
				//check if require password change
				/*String requirePasswordChange = userLogin.getString("requirePasswordChange");
				if(UtilValidate.isNotEmpty(requirePasswordChange)){
					if(requirePasswordChange.equals("N") || UtilValidate.isEmpty(requirePasswordChange)){
						request.setAttribute("userLoginId", username);
						request.setAttribute("_ERROR_MESSAGE_", "Reset Password is Already Done: "+username);
						return "error";
					}
				}*/
				
				checkPwd = checkPassword(userLogin.getString("currentPassword"), useEncryption, oldPassword);
				if(!checkPwd) {
					Debug.logInfo("[LoginServices.userLogin] : Password Incorrect", MODULE);
					request.setAttribute("userLoginId", username);
					request.setAttribute("_ERROR_MESSAGE_", "Current Password is Incorrect: "+username);
					return "error";
				}
			} else {
				Debug.logInfo("[LoginServices.userLogin] : Password Incorrect", MODULE);
				request.setAttribute("userLoginId", username);
				request.setAttribute("_ERROR_MESSAGE_", "Current Password is empty: "+username);
				return "error";
			}

			if(UtilValidate.isNotEmpty(newPassword) && UtilValidate.isNotEmpty(confirmpassword) && checkPwd){
				if(newPassword.equals(confirmpassword)){
					if(UtilValidate.isNotEmpty(username)) {
						GenericValue userLoginCheck = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", username).queryOne();
						if(UtilValidate.isNotEmpty(userLoginCheck)) {
							userLoginCheck.set("currentPassword", org.ofbiz.base.crypto.HashCrypt.getDigestHash(newPassword));
							userLoginCheck.set("requirePasswordChange", "N");
							userLoginCheck.set("hasLoggedOut", "Y");
							userLoginCheck.set("enabled", "Y");
							userLoginCheck.set("disabledDateTime", null);
							userLoginCheck.store();
						}
						request.setAttribute("_EVENT_MESSAGE_", "Password Updated Successfully for User : "+username);
					}
				}else{
					request.setAttribute("userLoginId", username);
					request.setAttribute("_ERROR_MESSAGE_", "New Password and Confirm Password Must be Same : "+username);
					return "error";
				}
			}

		} catch (Exception e) {
			String errMsg = "Problem While Fetching User Login " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}
}
