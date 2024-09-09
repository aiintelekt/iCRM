package org.groupfio.common.portal.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.content.ContentEvents;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class UtilCommon {
	
	public static final EntityFindOptions DISTINCT_READ_OPTIONS = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);

	public static final EntityFindOptions READ_ONLY_OPTIONS = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
	private static final String MODULE = UtilCommon.class.getName();

	public static GenericValue getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (GenericValue) session.getAttribute("userLogin");
	}
	public static Map<String, ?> makeHistoryEntry(String text, String view, List<String> override) {
		Map<String, Object> entry = FastMap.<String, Object>newInstance();
		if (text == null) {
			throw new IllegalArgumentException("Argument \"text\" can't be null");
		}
		entry.put("text", text);
		if (view != null) {
			entry.put("view", view);
		}
		if (override != null) {
			entry.put("override", override);
		}
		return entry;
	}

	/**
	 * Prepares an History entry for later processing.
	 * @param text the text that should be displayed as the label of the entry
	 * @param view the view name
	 * @return the history entry <code>Map</code>
	 */
	public static Map<String, ?> makeHistoryEntry(String text, String view) {
		return makeHistoryEntry(text, view, null);
	}

	/**
	 * Prepares an History entry for later processing.
	 * @param text the text that should be displayed as the label of the entry
	 * @return the history entry <code>Map</code>
	 */
	public static Map<String, ?> makeHistoryEntry(String text) {
		return makeHistoryEntry(text, null, null);
	}

	public static Locale getLocale(HttpServletRequest request) {
		return UtilHttp.getLocale(request);
	}

	/**
	 * This method will read the donePage parameter and return it to the
	 * controller as the result. It is called from controller.xml using <event
	 * type="java" path="org.opentaps.common.event.CommonEvents"
	 * invoke="donePageRequestHelper"/> Then it can be used with <response
	 * name="${returnValue}" .../> to determine what to do next.
	 * 
	 * @param request
	 *            a <code>HttpServletRequest</code> value
	 * @param response
	 *            a <code>HttpServletResponse</code> value
	 * @return The donePage or DONE_PAGE parameter if it exists, otherwise
	 *         "error"
	 */
	public static String donePageRequestHelper(HttpServletRequest request,
			HttpServletResponse response) {
		
		Map<String, Object> parameters = UtilHttp.getParameterMap(request);
		String donePage = (String) parameters.get("donePage");
		if (donePage == null) {
			donePage = (String) parameters.get("DONE_PAGE");
		}
		if (donePage == null) {
			// special case after service-multi
			Set<String> keys = parameters.keySet();
			for (String current : keys) {
				if (current.startsWith("donePage")) {
					donePage = (String) parameters.get(current);
					break;
				}
			}
		}
		if (donePage == null) {
			donePage = "error";
		}
		
		request.setAttribute("activeTab", parameters.get("activeTab"));
		
		String errorPage = (String) parameters.get("errorPage");
		if (errorPage != null && UtilCommon.hasError(request)) {
			//Debug.logInfo("donePageRequestHelper: goto errorPage [" + errorPage
			//		+ "]", MODULE);
			return errorPage;
		}

		//Debug.logInfo(
				///"donePageRequestHelper: goto donePage [" + donePage + "]",
				//MODULE);
		return donePage;
	}
	
    /**
     * Checks if a request has an error set.
     * @param request a <code>HttpServletRequest</code> value
     * @return a <code>Boolean</code> value
     */
    @SuppressWarnings("unchecked")
    public static Boolean hasError(HttpServletRequest request) {
        Enumeration<String> attrs = request.getAttributeNames();
        while (attrs.hasMoreElements()) {
            String a = attrs.nextElement();
            if ("_ERROR_MESSAGE_LIST_".equals(a) || "_ERROR_MESSAGE_".equals(a)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Fetches a <code>List</code> of Enumerations by enumTypeId from the cache.
     * @param enumTypeId the type of enumeration to fetch
     * @param delegator a <code>Delegator</code> value
     * @return the <code>List</code> of enumeration <code>GenericValue</code>
     * @exception GenericEntityException if an error occurs
     */
    public static List<GenericValue> getEnumerations(String enumTypeId, Delegator delegator) throws GenericEntityException {
        return delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", enumTypeId), UtilMisc.toList("sequenceId"),false);
    }
    /**
     * Gets a list of states in the given country.
     * @param delegator a <code>Delegator</code> value
     * @param countryGeoId the country for which to return the list of states
     * @return a <code>List</code> of states Geo <code>GenericValue</code>
     * @exception GenericEntityException if an error occurs
     */
    public static List<GenericValue> getStates(Delegator delegator, String countryGeoId) throws GenericEntityException {
        return delegator.findByAnd("GeoAssocAndGeoTo", UtilMisc.toMap("geoIdFrom", countryGeoId, "geoAssocTypeId", "REGIONS"), UtilMisc.toList("geoName"),false);
    }
    
    /**
     * Parse a comma-delimited string of email addresses and validate each.
     * @param emailAddressString comma-delimited string of email addresses
     * @return <code>Set</code> of valid email addresses
     */
    public static Set<String> getValidEmailAddressesFromString(String emailAddressString) {
        return getValidEmailAddressesFromString(emailAddressString, false);
    }

    /**
     * Parse a comma-delimited string of email addresses and validate each.
     * @param emailAddressString comma-delimited string of email addresses
     * @param requireDot if a dot is required in the email address to consider it valid
     * @return <code>Set</code> of valid email addresses
     */
    public static Set<String> getValidEmailAddressesFromString(String emailAddressString, boolean requireDot) {
        Set<String> emailAddresses = new TreeSet<String>();
        if (UtilValidate.isNotEmpty(emailAddressString)) {
            String[] emails = emailAddressString.split(",");
            for (int x = 0; x < emails.length; x++) {
                if (!UtilValidate.isEmail(emails[x])) {
                    Debug.log("Ignoring invalid email address: " + emails[x]);
                    continue;
                }
                emailAddresses.add(UtilValidate.stripWhitespace(emails[x]));
            }
        }
        return emailAddresses;
    }
    
    /**
     * Gets a <code>ByteWrapper</code> object for the given parameters.
     * @param delegator a <code>Delegator</code> value
     * @param dataResourceId a <code>String</code> value
     * @param https a <code>String</code> value
     * @param webSiteId a <code>String</code> value
     * @param locale a <code>Locale</code> value
     * @param rootDir a <code>String</code> value
     * @return the <code>ByteWrapper</code>
     * @exception IOException if an error occurs
     * @exception GeneralException if an error occurs
     * @deprecated for upgrade ofbiz to new version only, refactor the code later, ofbiz no longer uses ByteWrapper, instead use byte[] directly.
     */
    public static ByteWrapper getContentAsByteWrapper(Delegator delegator, String dataResourceId, String https, String webSiteId, Locale locale, String rootDir) throws IOException, GeneralException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ContentEvents.streamDataResource(baos, delegator, dataResourceId, https, webSiteId, locale, rootDir);
        ByteWrapper byteWrapper = new ByteWrapper(baos.toByteArray());
        return byteWrapper;
    }
    
    /**
     * Get a <code>TimeZone</code> from a context <code>Map</code>, or if not set return the default <code>TimeZone</code>.
     *
     * @param context a context <code>Map</code> value
     * @return a <code>TimeZone</code> value
     */
    public static TimeZone getTimeZone(Map<String, ?> context) {
        TimeZone tz = (TimeZone) context.get("timeZone");
        if (tz == null) {
            tz = TimeZone.getDefault();
        }
        return tz;
    }
    
    /**
     * This method takes the date/time/duration form input and transforms it into an end timestamp.
     * It uses Java Date formatting capabilities to transform the duration input into an interval.
     *
     * @param start Full date, hour, minute and second of the starting time
     * @param duration The user input for hour such as "1:00"
     * @param timeZone a <code>TimeZone</code> value
     * @param locale a <code>Locale</code> value
     * @return the end <code>Timestamp</code>
     * @throws IllegalArgumentException If the duration input is unparseable or negative
     */
    public static Timestamp getEndTimestamp(Timestamp start, String duration, Locale locale, TimeZone timeZone) throws IllegalArgumentException {

        // return the start timestamp if no duration specified (i.e., duration = 0)
        if (duration == null || duration.length() == 0) {
            return start;
        }

        Calendar cal = Calendar.getInstance(timeZone, locale);

        // Turn the duraiton into a date and time with the hour being the duration (note this is input from user, which we require to be in HH:mm form)
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        try {
            cal.setTime(df.parse(duration));
        } catch (ParseException e) {
            throw new IllegalArgumentException(String.format("Duration input must be in %1$s format."));
        }

        // extract the days, hours and minutes
        int days = cal.get(Calendar.DAY_OF_YEAR) - 1;
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);

        // set to the start time and add the hours and minutes
        cal.setTime(start);
        cal.set(Calendar.DAY_OF_YEAR, days + cal.get(Calendar.DAY_OF_YEAR));
        cal.set(Calendar.HOUR_OF_DAY, hours + cal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, minutes + cal.get(Calendar.MINUTE));

        // create the end timestamp
        Timestamp end = new Timestamp(cal.getTimeInMillis());

        // make sure it's after the start timestamp
        if (end.before(start)) {
            throw new IllegalArgumentException("Cannot set a negative duration.");
        }

        // return our result as a Timestamp
        return end;
    }
    public static List < String > getArrayToList(String data) {
        List < String > convertedList = null;
        if (UtilValidate.isNotEmpty(data)) {
            convertedList = new LinkedList < String > ();
            Debug.logInfo("Converted before input " + data, MODULE);
            if (data.contains(",")) {
                String records[] = data.split(",");
                for (String r: records) {
                    convertedList.add(r.trim());
                }
            } else {
                convertedList.add(data);
            }

            Debug.logInfo("Input : " + convertedList, MODULE);
            return convertedList;
        }
        return null;
    }
    
    public static java.sql.Date parseToDate(String dateString, String dateFormat) {
    	try {
    		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date callBackDate1 = new SimpleDateFormat(dateFormat).parse(dateString);
			String callBackDate = sdf.format(callBackDate1);
			return java.sql.Date.valueOf(callBackDate);
		} catch (ParseException pe) {
			Debug.log("====ParseException===" + pe.getMessage());
		}
    	return null;
    }
    
    public static List<String> removeFromList(String input, String item) {
    	List<String> result = new ArrayList<>();
    	try {
    		if (UtilValidate.isNotEmpty(input) && UtilValidate.isNotEmpty(item)) {
    			result = new ArrayList<>(Arrays.asList(input.split(",")));
    			result.remove(item);
    			return result;
    		}
		} catch (Exception pe) {
			pe.printStackTrace();
			Debug.log("====removeFromList===" + pe.getMessage());
		}
    	return result;
    }
    
    public static String formatPhoneNumber(String phoneNumber) {
		try {
			if(UtilValidate.isNotEmpty(phoneNumber))
				phoneNumber = phoneNumber.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1)-$2-$3");
		} catch (Exception e) {
		}
		return phoneNumber;
	}
    
    public static GenericValue getEntityByDomain(Delegator delegator, String domainEntityId, String domainEntityType) {
    	GenericValue entity = null;
		try {
			if (UtilValidate.isNotEmpty(domainEntityId) && UtilValidate.isNotEmpty(domainEntityType)) {
				if (CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
					entity = EntityQuery.use(delegator).from("PartyRole").where("partyId", domainEntityId, "roleTypeId", domainEntityType).queryFirst();
				} else if (domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
					entity = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId", domainEntityId).queryFirst();
				} else if (CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
					entity = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", domainEntityId).queryFirst();
				} else if (domainEntityType.equals(DomainEntityType.ACTIVITY)) {
					entity = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", domainEntityId).queryFirst();
				} else if (domainEntityType.equals(DomainEntityType.REBATE)) {
					entity = EntityQuery.use(delegator).from("Agreement").where("agreementId", domainEntityId).queryFirst();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
		return entity;
	}
    
    public static String getDomainName(Delegator delegator, Map<String, Object> context) {
		String approvalName = "";
		try {
			String domainEntityType = (String) context.get("domainEntityType");
			String domainEntityId = (String) context.get("domainEntityId");
			String programTypeDes = (String) context.get("programTypeDes");
			String domainIndicatorDes = (String) context.get("domainIndicatorDes");
			
			if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.REBATE)) {
				GenericValue agreement = EntityQuery.use(delegator).select("description").from("Agreement").where("agreementId", domainEntityId).queryFirst();
				if (UtilValidate.isNotEmpty(agreement)) {
					approvalName += ", Agreement#"+agreement.getString("description")+" ("+domainEntityId+")";
				}
				if (UtilValidate.isNotEmpty(programTypeDes)) {
					approvalName += ", Program Type#"+programTypeDes;
				}
			} else if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
				GenericValue agreement = EntityQuery.use(delegator).select("custRequestName").from("CustRequest").where("custRequestId", domainEntityId).queryFirst();
				if (UtilValidate.isNotEmpty(agreement)) {
					if (UtilValidate.isEmpty(domainIndicatorDes)) {
						domainIndicatorDes = "FSR#";
					}
					approvalName += ", "+domainIndicatorDes+agreement.getString("custRequestName")+" ("+domainEntityId+")";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return approvalName;
	}
    
    public static Map<String,Object> connect(String requestString, String requestUri,String apiUser,String apiKey){
    	Map<String,Object> responseMap = FastMap.newInstance();
    	try {
    		if(UtilValidate.isNotEmpty(requestUri)) {
    			URL url = new URL(requestUri);
    			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    			conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("charset", "utf-8");
				conn.setRequestProperty("apiUser", apiUser);
				conn.setRequestProperty("apiKey", apiKey);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setInstanceFollowRedirects(false);
				DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
				outStream.writeBytes(requestString);
				outStream.flush();
				outStream.close();
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String responseStr = "";
				String line = "";
				StringBuffer buffer = new StringBuffer();
				while ((line = br.readLine()) != null) {
					buffer.append(line);
					buffer.append('\n');
				}
				br.close();
				responseStr = buffer.toString().trim();
				Debug.logInfo("ww response json : "+responseStr, MODULE);
				responseMap.put("responseCode",conn.getResponseCode());
				responseMap.put("responseString",responseStr);
				conn.disconnect();
    		}
    	}catch(MalformedURLException e) {
    		e.printStackTrace();
			responseMap.put("responseString",e.getMessage());
    	}catch(IOException e) {
    		e.printStackTrace();
			responseMap.put("responseString",e.getMessage());
    	}
    	return responseMap;
    }
    
    public static boolean isRegistrationDoneOrNotNeeded(Delegator delegator) {
    	String freemiumEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_FREEMIUM_ENABLED","N");
    	String customerDetailUpdated = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_CUST_DETL_UPD","N");
    	String customerOtpAuthenticated = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_CUST_DETL_VFD","N");
    	if(freemiumEnabled.equals("N"))
    		return true;
    	/*if(UtilValidate.isEmpty(customerDetailUpdated) || UtilValidate.isEmpty(customerOtpAuthenticated))
    		return true;*/
    	if(customerDetailUpdated.equals("Y") && customerOtpAuthenticated.equals("Y"))
    		return true;
    	return false;
    }
    
    public static boolean cleanUpFreemiumDataConfigs(Delegator delegator) {
    	List<GenericValue> toRemove = FastList.newInstance();
    	try {
    		List<String> params = new ArrayList<String>();
    		params.addAll(UtilMisc.toSet("IS_FREEMIUM_ENABLED","IS_CUST_DETL_UPD", "IS_CUST_DETL_VFD", "LEAD_APP_URL", "LEAD_APP_UPD_URL", "LEAD_APP_AUTH_URL", "LEAD_APP_USER", "LEAD_APP_KEY"));
    		
    		for(String param: params) {
    			GenericValue paramGv = org.fio.homeapps.util.DataUtil.pretailLoyaltyGlobalParameters(delegator, param);
    			if(UtilValidate.isNotEmpty(paramGv))
    				toRemove.add(paramGv);
    		}
    		if(UtilValidate.isNotEmpty(toRemove))
    			delegator.removeAll(toRemove);
    	}catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    public static boolean isDealerOnly(Delegator delegator, String partyId) {
        try {
            if (UtilValidate.isNotEmpty(partyId)) {
            	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
    			conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DEALER"));
    			conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_IN, Arrays.asList("PARENT_ACCOUNT","SUB_PARENT_ACCOUNT")));
            	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
            	GenericValue partyRole = EntityQuery.use(delegator).from("PartyRole").where(mainConditon).queryFirst();
				if (UtilValidate.isNotEmpty(partyRole)) {
					return true;
				}
            }
        } catch (Exception e) {}
        return false;
    }
    
    public static String isComponentEnabled(Delegator delegator, String componentId) {
        try {
            if (UtilValidate.isNotEmpty(componentId)) {
            	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    			conditions.add(EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId));
    			conditions.add(EntityCondition.makeCondition("isHide", EntityOperator.EQUALS, "N"));
            	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
            	GenericValue component = EntityQuery.use(delegator).from("OfbizComponentAccess").where(mainConditon).queryFirst();
				if (UtilValidate.isNotEmpty(component)) {
					return "Y";
				}
            }
        } catch (Exception e) {}
        return "N";
    }
    
}
