package org.fio.admin.portal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.fio.admin.portal.constant.AdminPortalConstant.AccessLevel;
import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.admin.portal.constant.AdminPortalConstant.JavaDataType;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.FreemarkerUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ModelServiceReader;
import org.ofbiz.service.ServiceUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * 
 * @author Mahendran
 * @since 30-07-2019
 * 
 */
public class DataUtil {

    private static String MODULE = DataUtil.class.getName();
    public static final String RESOURCE = "AdminPortalUiLabels";

    @SuppressWarnings("unchecked")
    public static < T > T convertDateTimestamp(String value, SimpleDateFormat sdf, String type, String returnType) {
        Date date = null;
        try {
            List < SimpleDateFormat > dateFormatList = getDateFormats(type);
            if (dateFormatList.size() > 0 && UtilValidate.isNotEmpty(value)) {
                for (SimpleDateFormat format: dateFormatList) {
                    try {
                        format.setLenient(false);
                        date = format.parse(value);
                    } catch (ParseException e) {}
                    if (date != null) {
                        if (returnType.equalsIgnoreCase(DateTimeTypeConstant.SQL_DATE)) {
                            return (T) new java.sql.Date(date.getTime());
                        } else if (returnType.equalsIgnoreCase(DateTimeTypeConstant.TIMESTAMP)) {
                            String stamp = sdf.format(date.getTime());
                            return (T) Timestamp.valueOf(stamp);
                        } else if (returnType.equalsIgnoreCase(DateTimeTypeConstant.UTIL_DATE)) {
                            return (T) new Date(date.getTime());
                        } else if (returnType.equalsIgnoreCase(DateTimeTypeConstant.STRING)) {
                            String dateStr = sdf.format(date.getTime());
                            return (T) dateStr;
                        }

                        break;
                    }
                }
            }
        } catch (Exception e) {
            Debug.logError("Date Time Conversion Error : " + e.getMessage(), MODULE);
        }
        return null;
    }

    public static String getFileExtension(String fileName) {
        if (fileName != null && fileName.length() != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        }
        return null;
    }

    public static List < SimpleDateFormat > getDateFormats(String formatType) {
        List < SimpleDateFormat > dateFormats = null;
        try {
            if (DateTimeTypeConstant.DATE.equalsIgnoreCase(formatType)) {
                dateFormats = new ArrayList < SimpleDateFormat > () {
                    /**
                     * 
                     */
                    private static final long serialVersionUID = 8458284727816509794L;

                    {
                        add(new SimpleDateFormat("MM/dd/yy"));
                        add(new SimpleDateFormat("dd/MM/yy"));
                        add(new SimpleDateFormat("MM/dd/yyyy"));
                        add(new SimpleDateFormat("dd/MM/yyyy"));
                        add(new SimpleDateFormat("M/dd/yyyy"));
                        add(new SimpleDateFormat("dd.M.yyyy"));
                        add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
                        add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
                        add(new SimpleDateFormat("dd.MMM.yyyy"));
                        add(new SimpleDateFormat("dd-MMM-yyyy"));
                        add(new SimpleDateFormat("dd-MM-yyyy"));
                        add(new SimpleDateFormat("dd MMM yyyy"));
                        add(new SimpleDateFormat("yyyy-MM-dd"));
                        add(new SimpleDateFormat("yyyyMMdd"));
                    }
                };
            } else if (DateTimeTypeConstant.TIMESTAMP.equalsIgnoreCase(formatType)) {
                dateFormats = new ArrayList < SimpleDateFormat > () {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = -659231124932530282L;

                    {
                        add(new SimpleDateFormat("MM/dd/yy HH:mm:ss"));
                        add(new SimpleDateFormat("dd/MM/yy HH:mm:ss"));
                        add(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("M/dd/yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd.M.yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd.MMM.yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd MMM yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd MM yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                        add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
                        add(new SimpleDateFormat("yyyyMMdd HH:mm:ss"));
                        add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                    }
                };
            }  else if (DateTimeTypeConstant.DATE_TIMESTAMP.equalsIgnoreCase(formatType)) {
            	dateFormats = new ArrayList < SimpleDateFormat > () {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = -659231124932530282L;

                    {
                    	add(new SimpleDateFormat("MM/dd/yy"));
                        add(new SimpleDateFormat("dd/MM/yy"));
                        add(new SimpleDateFormat("MM/dd/yyyy"));
                        add(new SimpleDateFormat("dd/MM/yyyy"));
                        add(new SimpleDateFormat("M/dd/yyyy"));
                        add(new SimpleDateFormat("dd.M.yyyy"));
                        add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
                        add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
                        add(new SimpleDateFormat("dd.MMM.yyyy"));
                        add(new SimpleDateFormat("dd-MMM-yyyy"));
                        add(new SimpleDateFormat("dd-MM-yyyy"));
                        add(new SimpleDateFormat("dd MMM yyyy"));
                        add(new SimpleDateFormat("yyyy-MM-dd"));
                        add(new SimpleDateFormat("yyyyMMdd"));
                        add(new SimpleDateFormat("MM/dd/yy HH:mm:ss"));
                        add(new SimpleDateFormat("dd/MM/yy HH:mm:ss"));
                        add(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("M/dd/yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd.M.yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd.MMM.yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd MMM yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd MM yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                        add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
                        add(new SimpleDateFormat("yyyyMMdd HH:mm:ss"));
                        add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                    }
                };
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormats;
    }

    public static File[] getFileFromDirectory(File dir) {
        File[] files = null;
        try {
            if (dir.listFiles() != null && dir.isDirectory()) {
                files = dir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        if (file.isFile() && !file.isHidden()) {
                            return true;
                        }
                        return false;
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public static boolean isDigits(String str) {
        boolean isDigits = false;
        try {
            if (str.matches("[0-9]+") && str.length() > 2) {
                isDigits = true;
            }
        } catch (Exception e) {
            isDigits = false;
        }
        return isDigits;
    }


    public static < T > List < T > getFieldListFromMapList(List < Map < String, Object >> genericValueList, String fieldName, boolean distinct) {
        if (genericValueList == null || fieldName == null) {
            return null;
        }
        List < T > fieldList = new LinkedList < T > ();
        Set < T > distinctSet = null;
        if (distinct) {
            distinctSet = new HashSet < T > ();
        }

        for (Map < String, Object > value: genericValueList) {
            T fieldValue = UtilGenerics. < T > cast(value.get(fieldName));
            if (fieldValue != null) {
                if (distinct) {
                    if (!distinctSet.contains(fieldValue)) {
                        fieldList.add(fieldValue);
                        distinctSet.add(fieldValue);
                    }
                } else {
                    fieldList.add(fieldValue);
                }
            }
        }

        return fieldList;
    }
    public static String convertToDate(String dateReceivedFromUser) {

        DateFormat userDateFormat = new SimpleDateFormat("yyyy-mm-dd");
        DateFormat dateFormatNeeded = new SimpleDateFormat("dd-mm-yyyy");
        Date date;
        String convertedDate = null;
        try {
            date = userDateFormat.parse(dateReceivedFromUser);
            convertedDate = dateFormatNeeded.format(date);
            //System.out.println("Converted Date is " + convertedDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return convertedDate;
    }
    //Author : Arshiya S, Description :  Convert Timestamp to date format (dd-MM-yyyy)
    public static String timeStampToDate(Timestamp timestampReceived) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat changeDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormatNeeded = new SimpleDateFormat("dd-MM-yyyy");
        String timestampStr = null;
        if (UtilValidate.isNotEmpty(timestampReceived)) {
            timestampStr = dateFormat.format(timestampReceived);
        }
        Date date;
        String convertedDate = null;
        try {
            if (UtilValidate.isNotEmpty(timestampStr)) {
                date = dateFormat.parse(timestampStr);
                convertedDate = changeDateFormat.format(date);
                Date date1 = changeDateFormat.parse(convertedDate);
                convertedDate = dateFormatNeeded.format(date1);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static Map < String, Object > convertGenericValueToMap(Delegator delegator, GenericValue genericValue) {
        Map < String, Object > returnMap = new HashMap < String, Object > ();
        try {
            if (UtilValidate.isNotEmpty(genericValue)) {
                Set < String > keys = genericValue.keySet();
                for (String key: keys) {
                    returnMap.put(key, UtilValidate.isNotEmpty(genericValue.get(key)) ? genericValue.get(key) : "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;
    }
    
    public static Map < String, String > convertGenericToMap(GenericValue genericValue) {
        Map < String, String > returnMap = new HashMap < String, String > ();
        try {
            if (UtilValidate.isNotEmpty(genericValue)) {
                Set < String > keys = genericValue.keySet();
                for (String key: keys) {
                    returnMap.put(key, UtilValidate.isNotEmpty(genericValue.get(key)) ? genericValue.get(key).toString() : "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;
    }

    public static String convertGenericValueToJson(Delegator delegator, GenericValue genericValue) {
        try {
            Map < String, Object > inputMap = convertGenericValueToMap(delegator, genericValue);
            ObjectMapper mapperObj = new ObjectMapper();
            String jsonResp = mapperObj.writeValueAsString(inputMap);
            return jsonResp;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    
    public static String convertToJson(Collection<?> collection) {
        try {
            ObjectMapper mapperObj = new ObjectMapper();
            String jsonResp = mapperObj.writeValueAsString(collection);
            return jsonResp;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    public static String convertToJson(Map<String, Object> dataMap) {
        try {
            ObjectMapper mapperObj = new ObjectMapper();
            String jsonResp = mapperObj.writeValueAsString(dataMap);
            return jsonResp;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static List < GenericValue > getPartyRoles(Delegator delegator, String partyId) {
        return getPartyRoles(delegator, partyId, null);
    }
    public static List < GenericValue > getPartyRoles(Delegator delegator, String partyId, String parentRoleType) {
        List < GenericValue > results = new ArrayList < GenericValue > ();
        try {
            if (UtilValidate.isNotEmpty(partyId)) {

                List < GenericValue > partyRoleList = EntityQuery.use(delegator).from("PartyRole").where("partyId", partyId).orderBy("-lastUpdatedTxStamp").queryList();
                for (GenericValue partyRole: partyRoleList) {
                    String roleTypeId = partyRole.getString("roleTypeId");
                    List < EntityCondition > conditions = new ArrayList < EntityCondition > ();
                    conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
                    if (UtilValidate.isNotEmpty(parentRoleType))
                        conditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parentRoleType));

                    EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                    GenericValue roleType = EntityQuery.use(delegator).select("roleTypeId").from("RoleType").where(condition).orderBy("-lastUpdatedTxStamp").queryFirst();
                    if (UtilValidate.isNotEmpty(roleType)) {
                        results.add(partyRole);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static List < String > getListFromMap(List < Map < String, Object >> listMap, String fieldToSelect) {
        List < String > list = new ArrayList < String > ();
        try {
            if (UtilValidate.isNotEmpty(listMap)) {
                for (Map < String, Object > map: listMap) {
                    if (map.containsKey(fieldToSelect))
                        list.add((String) map.get(fieldToSelect));
                    else
                        Debug.logInfo("Key not found", MODULE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public static Map < String, Object > getMapFromGeneric(List < GenericValue > list, String keyField, String valueField, boolean keyDescrtion) {
        Map < String, Object > dataMap = new LinkedHashMap < String, Object > ();
        if (UtilValidate.isNotEmpty(list)) {
            if (keyDescrtion)
                dataMap = list.stream().collect(Collectors.toMap(s -> s.getString(keyField), s -> s.getString(valueField) != null ? s.getString(valueField)+ "(" + s.getString(keyField) + ")" : ""+ "(" + s.getString(keyField) + ")", (oldValue, newValue) -> newValue, LinkedHashMap::new));
            else
                dataMap = list.stream().collect(Collectors.toMap(s -> s.getString(keyField), s -> s.getString(valueField) != null ? s.getString(valueField) : ""+ s.getString(keyField), (oldValue, newValue) -> newValue, LinkedHashMap::new));
        }
        return dataMap;
    }
    
    @SuppressWarnings("unchecked")
	public static <V, V1 extends V> Map<String, V> toLinkedMap(String...  data) {
    	Map<String, V> map = new LinkedHashMap<String, V>();
    	for (int i = 0; i < data.length;) {
            map.put((String) data[i++], (V) data[i++]);
        }
        return map;
    }
    
    public static boolean isInteger(String s) {
        boolean isValidInteger = false;
        try {
            Integer.parseInt(s.trim());
            isValidInteger = true;
        } catch (NumberFormatException ex) {}
        return isValidInteger;
    }
    public static boolean isValidUserOld(Delegator delegator, Map<String, Object> context) {
        String userLoginId = (String) context.get("userLoginId");
        String businessUnit = (String) context.get("businessUnit");
        String teamId = (String) context.get("teamId");
        boolean isVaild = false;
        String partyId = "";
        try {
            EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
                EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),
                EntityCondition.makeCondition(EntityOperator.OR,
                      EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"PARTY_ENABLED"),
                      EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,null)),
                EntityCondition.makeCondition(EntityOperator.OR,
                      EntityCondition.makeCondition("enabled",EntityOperator.EQUALS,"Y"),
                      EntityCondition.makeCondition("enabled",EntityOperator.EQUALS,null)));
            GenericValue userLogin = EntityQuery.use(delegator).select(userLoginId).from("UserLoginPerson").where(condition).queryFirst();
            if(UtilValidate.isNotEmpty(userLogin)) {
                isVaild = true;
                partyId = userLogin.getString("partyId");
            }
            if(isVaild) {
                //Check given bu and team assoc exists or not
                List<String> teamIds = new ArrayList<String>();
                if(UtilValidate.isNotEmpty(businessUnit) && UtilValidate.isNotEmpty(teamId)) {
                    List<GenericValue> emplTeam = EntityQuery.use(delegator).select("emplTeamId").from("EmplTeam").where("emplTeamId",teamId,"businessUnit",businessUnit).queryList();
                    if(UtilValidate.isEmpty(emplTeam)) {
                        isVaild = false;
                    }
                } else if(UtilValidate.isNotEmpty(businessUnit) && UtilValidate.isEmpty(teamId)) {
                    List<GenericValue> emplTeam = EntityQuery.use(delegator).select("emplTeamId").from("EmplTeam").where("emplTeamId",teamId,"businessUnit",businessUnit).queryList();
                    if(UtilValidate.isEmpty(emplTeam)) {
                        isVaild = false;
                    } else {
                    	teamIds.addAll(EntityUtil.getFieldListFromEntityList(emplTeam, "emplTeamId", true));
                    }
                } else if(UtilValidate.isNotEmpty(teamId)) {
                    teamIds.add(teamId);
                }
                if(isVaild) {
                    List<EntityCondition> conditions = new ArrayList<EntityCondition>();
                    if(UtilValidate.isNotEmpty(partyId))
                        conditions.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,partyId));
                    if(UtilValidate.isNotEmpty(teamIds))
                        conditions.add(EntityCondition.makeCondition("emplTeamId",EntityOperator.IN,teamIds));
                    
                    EntityCondition mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                    List<GenericValue> emplFulfillment = EntityQuery.use(delegator).select("partyId").from("EmplPositionFulfillment").where(mainCond).filterByDate().queryList();
                    if(UtilValidate.isEmpty(emplFulfillment)) {
                        isVaild = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return isVaild;
    }
    public static Map<String, Object> isValidUser(Delegator delegator, Map<String, Object> context) {
        String userLoginId = (String) context.get("userLoginId");
        Map<String, Object> results = new HashMap<String, Object>();
        String partyId = "";
        try {
            EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
                EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),
                EntityCondition.makeCondition(EntityOperator.OR,
                      EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"PARTY_ENABLED"),
                      EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,null)),
                EntityCondition.makeCondition(EntityOperator.OR,
                      EntityCondition.makeCondition("enabled",EntityOperator.EQUALS,"Y"),
                      EntityCondition.makeCondition("enabled",EntityOperator.EQUALS,null)));
            GenericValue userLogin = EntityQuery.use(delegator).select("userLoginId","partyId").from("UserLoginPerson").where(condition).queryFirst();
            if(UtilValidate.isNotEmpty(userLogin)) {
                partyId = userLogin.getString("partyId");
                results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                results.put("partyId", partyId);
                results.put(ModelService.SUCCESS_MESSAGE, "ValidUser");
            } else {
                results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                results.put(ModelService.ERROR_MESSAGE, "AMInvalidUser");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError("Error : "+e.getMessage());
        }
        return results;
    }
    public static Map<String, Object> validateBuAssociate(Delegator delegator, String businessUnit, String teamId, String partyId, String oplevel){
        Map<String, Object> results = new HashMap<String, Object>();
        List<Map<String, Object>> buInfo = new LinkedList<Map<String,Object>>();
        try {
            GenericValue securityGlobal = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId",GlobalConstants.COMMON_TEAMS).queryFirst();
            List<String> commonTeamList = new ArrayList<String>();
            if(UtilValidate.isNotEmpty(securityGlobal)) {
                String commonTeams = securityGlobal.getString("value");
                if(UtilValidate.isNotEmpty(commonTeams)) {
                    commonTeamList = Stream.of(commonTeams.split(",")).map(e -> new String(e)).collect(Collectors.toList());
                }
            }
            List<EntityCondition> conditions = new ArrayList<EntityCondition>();
            if(UtilValidate.isNotEmpty(teamId))
                conditions.add(EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,teamId));
            if(UtilValidate.isNotEmpty(businessUnit))
                conditions.add(EntityCondition.makeCondition("businessUnit",EntityOperator.EQUALS,businessUnit));
            conditions.add(EntityCondition.makeCondition(EntityOperator.OR,EntityCondition.makeCondition("isActive",EntityOperator.EQUALS,null),EntityCondition.makeCondition("isActive",EntityOperator.EQUALS,"Y")));
            EntityCondition condition = EntityCondition.makeCondition(conditions,EntityOperator.AND);
            List<GenericValue> emplTeams = EntityQuery.use(delegator).select("emplTeamId","businessUnit").from("EmplTeam").where(condition).queryList();
            List<String> teams = new ArrayList<String>();
            
            if(UtilValidate.isNotEmpty(emplTeams)) {
                Map<String, Object> buMap = new LinkedHashMap<String, Object>();
                teams = EntityUtil.getFieldListFromEntityList(emplTeams, "emplTeamId", true);
                businessUnit = emplTeams.get(0).getString("businessUnit");
                buMap.put("bu", businessUnit);
                buMap.put("team_list", teams);
                if(!AccessLevel.LEVEL2 .equals(oplevel))
                    buInfo.add(buMap);
                
                if(UtilValidate.isEmpty(teamId) && UtilValidate.isNotEmpty(commonTeamList)) {
                    for(String commonTeam : commonTeamList) {
                        teams.remove(commonTeam);
                    }
                }
            } 
            if(AccessLevel.LEVEL2 .equals(oplevel) || AccessLevel.LEVEL3.equals(oplevel)) {
                List<EntityCondition> conditions1 = new ArrayList<EntityCondition>();
                if(UtilValidate.isNotEmpty(partyId))
                    conditions1.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
                if(UtilValidate.isNotEmpty(teams))
                    conditions1.add(EntityCondition.makeCondition("emplTeamId",EntityOperator.IN,teams));
                EntityCondition mainCond = EntityCondition.makeCondition(conditions1, EntityOperator.AND);
                List<GenericValue> emplFulfillment = EntityQuery.use(delegator).select("partyId","emplTeamId").from("EmplPositionFulfillment").where(mainCond).filterByDate().queryList();
                if(UtilValidate.isEmpty(emplFulfillment)) {
                    results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    results.put(ModelService.ERROR_MESSAGE, "AccessDenied");
                    return results;
                } else if(AccessLevel.LEVEL2 .equals(oplevel)) {
                    Map<String, Object> buMap = new LinkedHashMap<String, Object>();
                    buMap.put("bu", businessUnit);
                    String teamId1 = emplFulfillment.get(0).getString("emplTeamId");
                    buMap.put("team_list", UtilMisc.toList(teamId1));
                    buInfo.add(buMap);
                }
            } else if(AccessLevel.LEVEL5.equals(oplevel)) {
                //List<GenericValue> productStoreGroup = EntityQuery.use(delegator).select("productStoreGroupId").from("ProductStoreGroup").where("primaryParentGroupId",businessUnit).queryList();
                List<String> childBuList = new ArrayList<String>();
                List<String> childBusinessUnits = getHierarchyBu(delegator, UtilMisc.toList(businessUnit), childBuList);
                if(UtilValidate.isNotEmpty(childBusinessUnits)) {
                    EntityCondition activeCondition = EntityCondition.makeCondition(EntityOperator.OR,EntityCondition.makeCondition("isActive",EntityOperator.EQUALS,null),EntityCondition.makeCondition("isActive",EntityOperator.EQUALS,"Y"));
                    for(String childBu : childBusinessUnits) {
                        EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,activeCondition,EntityCondition.makeCondition("businessUnit",EntityOperator.EQUALS,childBu));
                        List<GenericValue> emplTeams1 = EntityQuery.use(delegator).select("emplTeamId","businessUnit").from("EmplTeam").where(condition1).queryList();
                        if(UtilValidate.isNotEmpty(emplTeams1)) {
                            Map<String, Object> buMap = new LinkedHashMap<String, Object>();
                            teams = EntityUtil.getFieldListFromEntityList(emplTeams1, "emplTeamId", true);
                            if(UtilValidate.isEmpty(teamId) && UtilValidate.isNotEmpty(commonTeamList)) {
                                for(String commonTeam : commonTeamList) {
                                    teams.remove(commonTeam);
                                }
                            }
                            String bUnit = emplTeams1.get(0).getString("businessUnit");
                            buMap.put("bu", bUnit);
                            buMap.put("team_list", teams);
                            buInfo.add(buMap);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            results.put(ModelService.ERROR_MESSAGE, "Error : "+e.getMessage());
            return results;
        }
        results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        results.put(ModelService.SUCCESS_MESSAGE, "ValidUser");
        results.put("buInfo", buInfo);
        return results;
    }
    
    public static List<String> getHierarchyBu(Delegator delegator, List<String> parentBu, List<String> buList) {
        try {
            List<EntityCondition> conditions = new ArrayList<EntityCondition>();
            if(UtilValidate.isNotEmpty(parentBu))
                conditions.add(EntityCondition.makeCondition("primaryParentGroupId",EntityOperator.IN,parentBu));
            EntityCondition condition = EntityCondition.makeCondition(conditions,EntityOperator.AND);
            List<String> childBu = new ArrayList<String>();
            List<GenericValue> productStoreGroupList = EntityQuery.use(delegator).select("productStoreGroupId").from("ProductStoreGroup").where(condition).orderBy("-lastUpdatedTxStamp").filterByDate().queryList();
            if(UtilValidate.isNotEmpty(productStoreGroupList)) {
                childBu = EntityUtil.getFieldListFromEntityList(productStoreGroupList, "productStoreGroupId", true);
                buList.addAll(childBu);
                if(UtilValidate.isNotEmpty(childBu))
                    getHierarchyBu(delegator, childBu, buList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buList;
    }
    public static String getBusinessUnitName(Delegator delegator, String productStoreGroupId) {
		
    	String buName = null;
		try {
			GenericValue producStoreGroup = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId",productStoreGroupId).queryOne();
			if(UtilValidate.isNotEmpty(producStoreGroup)){
				buName = producStoreGroup.getString("productStoreGroupName");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return buName;
	}
    
    public static List<String> retainAll(List<String> list, List<String> list1) {
		try {
			if(UtilValidate.isNotEmpty(list1)) {
				if(UtilValidate.isEmpty(list)) return list1;
				list.retainAll(list1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
    
    public static final ObjectMapper mapper = new ObjectMapper();
	
	public static List jsonArrayStrToMapList(String jsonBodyStr) {
		TypeFactory factory = mapper.getTypeFactory();
		CollectionType listType = 
			    factory.constructCollectionType(List.class, Map.class);

		List<Map<String, Object>> result = null;
		try {
			result = mapper.readValue(jsonBodyStr, listType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}
	
	public static Map<String,Object> jsonObjStrToMap(String jsonBodyStr) {
		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
		Map<String, Object> result = null;
		try {
			result = mapper.readValue(jsonBodyStr, typeRef);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}
	
	public static String convertToJsonStr(Map<String, Object> jsonMap) {
		String jsonString = null;
		try {
			Gson gson = new Gson();
			if(UtilValidate.isNotEmpty(jsonMap)) {
				jsonString = gson.toJson(jsonMap);
			} else {
				//jsonString = gson.toJson("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public static <T> String convertToJsonStr(T object) {
		String jsonString = null;
		try {
			Gson gson = new Gson();
			if(UtilValidate.isNotEmpty(object)) {
				jsonString = gson.toJson(object);
			} else {
				//jsonString = gson.toJson("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public static Map<String, Object> convertToMap(String jsonString){
		Map<String, Object> jsonEleMap = new LinkedHashMap<String, Object>();
		try {
			if(UtilValidate.isNotEmpty(jsonString)) {
				Gson gson = new Gson();
				Type type = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();	
				jsonEleMap = gson.fromJson(jsonString, type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonEleMap;
	}
	public static List<Map<String, Object>> convertJsonStrToList(String jsonString){
		List<Map<String, Object>> jsonEleMap = new LinkedList<Map<String, Object>>();
		try {
			if(UtilValidate.isNotEmpty(jsonString)) {
				Gson gson = new Gson();
				Type type = new TypeToken<LinkedList<Map<String, Object>>>() {}.getType();	
				jsonEleMap = gson.fromJson(jsonString, type);
			}
		} catch (Exception e) {
			jsonEleMap = new LinkedList<Map<String, Object>>();
			Debug.logError("Error : "+e.getMessage(), MODULE);
		}
		return jsonEleMap;
	}
	public static Map<String, String> convertToMapString(String jsonString){
		Map<String, String> jsonEleMap = new LinkedHashMap<String, String>();
		try {
			if(UtilValidate.isNotEmpty(jsonString)) {
				Gson gson = new Gson();
				Type type = new TypeToken<LinkedHashMap<String, String>>() {}.getType();	
				jsonEleMap = gson.fromJson(jsonString, type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonEleMap;
	}
	
	public static Map<String, Object> convertToMapIgnore(String jsonString){
		Map<String, Object> jsonEleMap = new LinkedHashMap<String, Object>();
		try {
			if(UtilValidate.isNotEmpty(jsonString)) {
				jsonString = StringEscapeUtils.unescapeHtml(jsonString);
				
				Gson gson = new Gson();
				Type type = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();	
				jsonEleMap = gson.fromJson(jsonString, type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonEleMap;
	}
	
	public static List<Map<String, Object>> convertToListMap(String jsonString){
		List<Map<String, Object>> jsonEleMap = new ArrayList<Map<String, Object>>();
		try {
			if(UtilValidate.isNotEmpty(jsonString)) {
				Gson gson = new Gson();
				Type type = new TypeToken<List<LinkedHashMap<String, Object>>>() {}.getType();	
				jsonEleMap = gson.fromJson(jsonString, type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonEleMap;
	}
	
	public static String getJsonStrBody(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
	    try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			try {
			    String line;
			    while ((line = reader.readLine()) != null) {
			        sb.append(line).append('\n');
			    }
			} finally {
			    reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return sb.toString();
	}
	
	public static void prepareGenericData(String key, String value,String javaType, GenericValue genericValue) {
		try {
			if (JavaDataType.STRING.equalsIgnoreCase(javaType) || JavaDataType.STRING_HELPER.equalsIgnoreCase(javaType)) {
				genericValue.put(key, (String) value);
			}
			if (JavaDataType.TIMESTAMP.equalsIgnoreCase(javaType) || JavaDataType.TIMESTAMP_HELPER.equalsIgnoreCase(javaType)) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Timestamp stamp = convertDateTimestamp(value, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.TIMESTAMP);
				genericValue.put(key, stamp);
			}
			if (JavaDataType.BIGDECIMAL.equalsIgnoreCase(javaType) || JavaDataType.BIGDECIMAL_HELPER.equalsIgnoreCase(javaType)) {
				// Debug.log(" javaType " + javaType + "value" +value);
				if (UtilValidate.isNotEmpty(value) && value.contains(",")) {
					value = value.replace(",", "");
					genericValue.put(key, new BigDecimal(value));

				}else if(UtilValidate.isNotEmpty(value) && value.contains("$")){
					value = value.replace("$", "");
					genericValue.put(key, new BigDecimal(value));

				}else if (UtilValidate.isNotEmpty(value)){
					genericValue.put(key, new BigDecimal(value));
				}
			}
			if (JavaDataType.LONG.equalsIgnoreCase(javaType) || JavaDataType.LONG_HELPER.equalsIgnoreCase(javaType)) {
				if (UtilValidate.isNotEmpty(value))
					genericValue.put(key, Long.valueOf(value));

			}
			if (JavaDataType.DOUBLE.equalsIgnoreCase(javaType) || JavaDataType.DOUBLE_HELPER.equalsIgnoreCase(javaType)) {
				// Debug.log(" javaType " + javaType + "value" +value);
				if (UtilValidate.isNotEmpty(value))
					genericValue.put(key, Double.valueOf(value));

			}

			if (JavaDataType.DATE.equalsIgnoreCase(javaType) || JavaDataType.DATE_HELPER.equalsIgnoreCase(javaType)) {
				if (UtilValidate.isNotEmpty(value)) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					java.sql.Date sqlDate = convertDateTimestamp(value, df, DateTimeTypeConstant.DATE, DateTimeTypeConstant.SQL_DATE);
					genericValue.put(key, sqlDate);
				}

			}
		} catch (Exception e) {
			Debug.logError("Field Mapping Error : "+e.getMessage(), MODULE);
		}

	} 
	
	public static String listToString(List<String> list) {
		return listToString(list, null);
	}
	public static String listToString(List<String> list, String separator) {
		String value = "";
		try {
			separator = UtilValidate.isNotEmpty(separator) ? separator : ", ";
			if(UtilValidate.isNotEmpty(list))
				value = list.stream().filter(e-> e!=null && !e.isEmpty()).map(String::trim).distinct().collect(Collectors.joining(separator));
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(),MODULE);
		}
		return value;
	}
	
	public static List<String> stringToList(String str, String separator){
		List<String> list = new LinkedList<String>();
		try {
			separator = UtilValidate.isNotEmpty(separator) ? separator : ", ";
			list = Stream.of(str.trim().split(separator)).map(String::trim).distinct().collect(Collectors.toList());
		} catch (Exception e) {
		}
		return list;
	}
	
	public static String getGeoName(Delegator delegator, String value) {
		return getGeoName(delegator, value, null);
	}
	
	public static String getGeoName(Delegator delegator, String value, String geoTypeId) {

        try {
        	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
            if (UtilValidate.isNotEmpty(value)) {
            	conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoId")), EntityOperator.EQUALS, value.toString().toUpperCase()));
                
            	if(UtilValidate.isNotEmpty(geoTypeId)) {
            		if(geoTypeId.contains("/")) {
            			List<String> geoTypes = Stream.of(geoTypeId.trim().split("/")).collect(Collectors.toList());
            			conditions.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.IN, geoTypes ));
            		} else
            		conditions.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, geoTypeId));
            	}
                GenericValue geo = EntityQuery.use(delegator).select("geoName").from("Geo").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).queryFirst();
                if (UtilValidate.isNotEmpty(geo)) {
                    return geo.getString("geoName");
                }
            }
        } catch (Exception e) {}

        return "";
    }

	public static String getPartySecurityRole(Delegator delegator, String partyId) {
		String securityRole = "";
		try {
			String parentRoleId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
	    	if(UtilValidate.isNotEmpty(parentRoleId)) {
	    		List<GenericValue> partyRoleList = getPartyRoles(delegator, partyId, parentRoleId);
	        	securityRole = UtilValidate.isNotEmpty(partyRoleList) ? partyRoleList.get(0).getString("roleTypeId") : "";
	    	}
		} catch (Exception e) {
		}
		return securityRole;
	}
	
	public static String formatPhoneNumber(String phoneNumber) {
		try {
			if(UtilValidate.isNotEmpty(phoneNumber))
				phoneNumber = phoneNumber.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1)-$2-$3");
		} catch (Exception e) {
		}
		return phoneNumber;
	}
	
	public static String getRateType(Delegator delegator, String rateTypeId) {
		
		try {
			if(UtilValidate.isNotEmpty(rateTypeId)) {
				GenericValue rateType = EntityQuery.use(delegator).select("rateTypeId","description").from("RateType").where("rateTypeId",rateTypeId).queryOne();
                if (UtilValidate.isNotEmpty(rateType)) {
                    return rateType.getString("description");
                }
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String toList(List<String> list,String prefix){
		String str ="";
		
		if(list.size() > 0){
			for(int i=0;i<list.size();i++){
				if(i == 0){
					if(UtilValidate.isNotEmpty(prefix)){
						str= str+"'"+prefix+list.get(i)+"'";
					}else{
						str= str+"'"+list.get(i)+"'";
					}
				}
				else{
					if(UtilValidate.isNotEmpty(prefix)){
						str = str+",'"+prefix+list.get(i)+"'";
					}else{
						str = str+",'"+list.get(i)+"'";
					}
				}
			}
		}
		
		return str;
	}
	public static boolean isJSONValid(String jsonString) {
	    try {
	        new JSONObject(jsonString);
	    } catch (JSONException ex) {
	        try {
	            new JSONArray(jsonString);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public static boolean isValidJson(String json) {
	    try {
	        new JsonParser().parse(json);
	        return true;
	    } catch (JsonSyntaxException jse) {
	        return false;
	    }
	}
	
	public static Map<String, Object> getGeoNameList(Delegator delegator, String geoTypeId) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();

			if(UtilValidate.isNotEmpty(geoTypeId)) {
				if(geoTypeId.contains("/")) {
					List<String> geoTypes = Stream.of(geoTypeId.trim().split("/")).collect(Collectors.toList());
					conditions.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.IN, geoTypes ));
				} else
					conditions.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, geoTypeId));
			}
			List<GenericValue> geoList = EntityQuery.use(delegator).select("geoId", "geoName").from("Geo").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).cache(true).queryList();
			if (UtilValidate.isNotEmpty(geoList)) {
				data.putAll(DataUtil.getMapFromGeneric(geoList, "geoId", "geoName", false));
			}
		} catch (Exception e) {}

		return data;
	}
	public static Map<String, Object> getUomDescriptionList(Delegator delegator, String uomTypeId) {
		Map<String, Object> data = new HashMap<String, Object>();
        try {
        	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        	if (UtilValidate.isNotEmpty(uomTypeId)) {
				conditions.add(EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, uomTypeId));
			}
        	EntityCondition condition = EntityCondition.makeCondition(conditions,EntityOperator.AND);
        	List<GenericValue> uomList = EntityQuery.use(delegator).select("uomId","description").from("Uom").where(condition).cache(true).queryList();
        	if(UtilValidate.isNotEmpty(uomList)) {
        		data.putAll(DataUtil.getMapFromGeneric(uomList, "uomId", "description", false));
        	}
        } catch (Exception e) {e.printStackTrace();}

        return data;
    }
	
	public static <T> boolean validateFieldsNotEmpty(List<T> listOfFields) {
		boolean isNotEmpty = true;
		try {
			if(UtilValidate.isNotEmpty(listOfFields)) {
				for(Object obj : listOfFields) {
					if(obj instanceof Object && UtilValidate.isEmpty(obj)) {
						isNotEmpty = false;
						break;
					} else if (obj instanceof String && UtilValidate.isEmpty(obj)) {
						isNotEmpty = false;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isNotEmpty;
	}
	
	public static boolean is3rdPartyTechnician(Delegator delegator, String partyId) {
        try {
        	if (UtilValidate.isNotEmpty(partyId)) {
        		EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
        				EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
        				EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "TECHNICIAN"),
        				EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTRACT_TYPE")
        				);
        		long count = EntityQuery.use(delegator).from("PartyRelationship").where(condition).queryCount();
    			if (count > 0) {
        			return true;
        		}
        	} else
        		return false;
        } catch (Exception e) {
        	Debug.logError(e, MODULE);
        }
        return false;
    }
	
	public static boolean isDate(String dateStr, String type) {

        List < SimpleDateFormat > dateFormatList = getDateFormats(type);
        if (dateFormatList.size() > 0 && UtilValidate.isNotEmpty(dateStr)) {
            for (SimpleDateFormat format: dateFormatList) {
                try {
                    format.setLenient(false);
                    format.parse(dateStr);
                    return true;
                } catch (ParseException e) {
                }
            }
        }
      
        return false;
	}
	
	public static String formatPhoneNumber(String phoneNumber, String pattern) {
		try {
			if(UtilValidate.isEmpty(pattern)) pattern ="($1)-$2-$3";
			if(UtilValidate.isNotEmpty(phoneNumber))
				phoneNumber = phoneNumber.replaceFirst("(\\d{3})(\\d{3})(\\d+)", pattern);
		} catch (Exception e) {
		}
		return phoneNumber;
	}
	
	public static boolean isDecimal(String data, String regExp) {
		boolean isDecimal = false;
		try {
			if(UtilValidate.isEmpty(regExp)) regExp = "[0-9]+([.][0-9]{1,2})?";
			final Pattern pattern = Pattern.compile(regExp);
			Matcher matcher = pattern.matcher(data); 
			if(matcher.matches())
				isDecimal = true;
			else
				isDecimal = false;
		} catch (Exception e) {
		}
		return isDecimal;
	}
	public static String getFormattedNumValue(Delegator delegator, String value) {
		if (UtilValidate.isNotEmpty(value)) {
			return getFormattedNumValue(delegator, value, null);
		}
		return value;
	}
	
	public static String getFormattedNumValue(Delegator delegator, String value, String format) {
		if (UtilValidate.isNotEmpty(value)) {
			String defaultNumFormat = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "NUM_FORMAT", "###,###.##"); 
			if (UtilValidate.isNotEmpty(format)) {
				defaultNumFormat = format;
			}
			
			DecimalFormat myFormatter = new DecimalFormat(defaultNumFormat);
			try {
				value = myFormatter.format(new BigDecimal(value));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return value;
		}
		return value;
	}
	
	public static String extractContentWithTag(Map<String, Object> context) {
		String mergedContent = "";
		try {
			Map<String, Object> mergeContext = new LinkedHashMap<String, Object>();
			Writer wr = new StringWriter();
			if(UtilValidate.isNotEmpty(context)) {
				String content = (String) context.get("content");
				mergeContext.putAll(context);
				FreemarkerUtil.renderTemplateWithTags("MergeTxet", content, mergeContext, wr, false, true);
				mergedContent = wr.toString();
			}
			
		} catch (Exception e) {
		}
		return mergedContent;
	}
	
	public static String getDataSourceDescription(Delegator delegator, String dataSourceId) {
		String description = "";
		try {
			if (UtilValidate.isNotEmpty(dataSourceId)) {
            	GenericValue dataSource = EntityQuery.use(delegator).from("DataSource").where("dataSourceId", dataSourceId).queryFirst();
            	if (UtilValidate.isNotEmpty(dataSource)) {
            		description = dataSource.getString("description");
            	}
            }
		} catch (Exception e) {
		}
		return description;
	}
	
	public static boolean bigDecimalBetweenCompareIt(BigDecimal value, BigDecimal value1, BigDecimal value2) {
		boolean isValid = false;
		try {
			if(UtilValidate.isNotEmpty(value) && UtilValidate.isNotEmpty(value1) && UtilValidate.isNotEmpty(value2)) {
				if((value.compareTo(value1) == 1 || value.compareTo(value1) == 0) && (value.compareTo(value2) == -1 || value.compareTo(value) == 0)) {
					isValid=true;
				}
			}
			if(UtilValidate.isNotEmpty(value) && UtilValidate.isNotEmpty(value1)) {
				if(value.compareTo(value1) == 1 || value.compareTo(value1) == 0) {
					isValid=true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isValid;
	}
	
	public static String getGlobalMysqlDateFormat(Delegator delegator) {
		try {
			String globalDateFormat = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "MYSQL_GLOBAL_DATE_FORMAT", "%m/%d/%Y");
			return globalDateFormat;
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}

	public static String getGlobalMysqlDateTimeFormat(Delegator delegator) {

		try {
			String globalFormat = getGlobalMysqlDateFormat(delegator);
			String globalTimeFormat = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "MYSQL_GLOBAL_TIME_FORMAT", "%H:%i");
			globalFormat = globalFormat + (UtilValidate.isNotEmpty(globalTimeFormat) ?  " "+ globalTimeFormat :"");
			return globalFormat;
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	public static boolean validateDate(Timestamp reqFromDate, Timestamp reqThruDate, Timestamp fromDate, Timestamp thruDate) {
		boolean isValid = false;
		try {
			if(reqFromDate == null || reqThruDate == null || fromDate == null || thruDate == null)
				isValid = false;

			if(((reqFromDate.after(fromDate) || reqFromDate.equals(fromDate))  && reqFromDate.before(thruDate)) && ((reqThruDate.before(thruDate) || reqThruDate.equals(thruDate)) && reqThruDate.after(fromDate)) ) {
				isValid = true;
			}

		} catch (Exception e) {
			isValid = false;
		}

		return isValid;
	}
	public static Map<String, Object> getOfbizServiceList(Delegator delegator, String componentId){
		return getOfbizServiceList(delegator, componentId, null);
	}
	public static Map<String, Object> getOfbizServiceList(Delegator delegator, String componentId, String prefix){
		Map<String, Object> services = new LinkedHashMap<String, Object>();
		try {
			Map<String, ModelService> serviceMap = new HashMap<String, ModelService>();
			if(UtilValidate.isNotEmpty(componentId)) {
				for (ComponentConfig.ServiceResourceInfo componentResourceInfo: ComponentConfig.getAllServiceResourceInfos("model",componentId)) {
					serviceMap.putAll(ModelServiceReader.getModelServiceMap(componentResourceInfo.createResourceHandler(), delegator));
				}
			} else {
				for (ComponentConfig.ServiceResourceInfo componentResourceInfo: ComponentConfig.getAllServiceResourceInfos("model")) {
					serviceMap.putAll(ModelServiceReader.getModelServiceMap(componentResourceInfo.createResourceHandler(), delegator));
				}
			}
			/*
			Set<String> serviceNames = new TreeSet<String>();
			if (serviceMap != null) {
				serviceNames.addAll(serviceMap.keySet());
			}
			*/
			for(String service : serviceMap.keySet()) {
				if(UtilValidate.isNotEmpty(prefix) && service.startsWith(prefix))
					services.put(service, service);
				else if(UtilValidate.isEmpty(prefix))
					services.put(service, service);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return services;
		
	}
	
	public static String convertCamelCaseToWords(String str) {
		return convertCamelCaseToWords(str, null);
	}
	public static String convertCamelCaseToWords(String str, String separator) {
		String words = "";
		try {
			separator = UtilValidate.isNotEmpty(separator) ? separator : StringUtils.SPACE;
			if(UtilValidate.isNotEmpty(str)) {
				words = StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(str), separator));
			}
		} catch (Exception e) {
			words = "";
		}
		return words;
	}
	
	public static String resourceValueAppender(String str, Object[] arguments) {
		String value ="";
		try {
			value = MessageFormat.format(str, arguments);
		} catch (Exception e) {
		}
		return value;
		
	}
	
	public static String removeHtmlTags(String input) {
        // Regular expression to match HTML tags
        String regex = "<[^>]*>";
        
        // Compile the regular expression pattern
        Pattern pattern = Pattern.compile(regex);
        
        // Replace all occurrences of HTML tags with an empty string
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("");
    }
	
	public static String getPrimaryPhone(Delegator delegator, String partyId) {
		String phoneNo = "";
		try {
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PCM", "PartyContactMech");
			dynamicView.addAlias("PCM", "partyId");
			dynamicView.addAlias("PCM", "contactMechId");
			dynamicView.addAlias("PCM", "fromDate");
			dynamicView.addAlias("PCM", "thruDate");
			dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
			dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
			dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			dynamicView.addMemberEntity("TN", "TelecomNumber");
			dynamicView.addAlias("TN", "contactNumber");
			dynamicView.addViewLink("PCM", "TN", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE")
					);
			
			GenericValue telecomNumber = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(telecomNumber))
				phoneNo = telecomNumber.getString("contactNumber");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return phoneNo;
	}
	
	public static String getPartyIdByPrmaryPhone(Delegator delegator, String phoneNumber) {
		String partyId = "";
		try {
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PCM", "PartyContactMech");
			dynamicView.addAlias("PCM", "partyId");
			dynamicView.addAlias("PCM", "contactMechId");
			dynamicView.addAlias("PCM", "fromDate");
			dynamicView.addAlias("PCM", "thruDate");
			dynamicView.addAlias("PCM", "lastUpdatedTxStamp");
			dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
			dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
			dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			dynamicView.addMemberEntity("TN", "TelecomNumber");
			dynamicView.addAlias("TN", "countryCode");
			dynamicView.addAlias("TN", "areaCode");
			dynamicView.addAlias("TN", "contactNumber");
			dynamicView.addViewLink("PCM", "TN", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			
			Map<String, Object> splittedPhoneNumMap = phoneNumberSplitter(phoneNumber);
			if(UtilValidate.isNotEmpty(splittedPhoneNumMap)) {
				String countryCode = (String) splittedPhoneNumMap.get("countryCode");
				String nationalNumber = (String) splittedPhoneNumMap.get("nationalNumber");
				String areaCode = (String) splittedPhoneNumMap.get("areaCode");
				String contactNumber = (String) splittedPhoneNumMap.get("contactNumber");
				
				List<EntityCondition> conditions = new LinkedList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("countryCode", EntityOperator.EQUALS, countryCode),
						EntityCondition.makeCondition("countryCode", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("countryCode", EntityOperator.EQUALS, "")
						));
				if(UtilValidate.isNotEmpty(areaCode)) {
					conditions.add(EntityCondition.makeCondition("areaCode", EntityOperator.EQUALS, areaCode));
					
					if(UtilValidate.isNotEmpty(contactNumber))
						conditions.add(EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, contactNumber));
				}
				
				EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				
				GenericValue telecomNumber = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().orderBy("lastUpdatedTxStamp DESC").queryFirst();
				if(UtilValidate.isNotEmpty(telecomNumber))
					partyId = telecomNumber.getString("partyId");
				else {
					if(UtilValidate.isNotEmpty(nationalNumber)) {
						conditions = new LinkedList<EntityCondition>();
						conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("countryCode", EntityOperator.EQUALS, countryCode),
								EntityCondition.makeCondition("countryCode", EntityOperator.EQUALS, null),
								EntityCondition.makeCondition("countryCode", EntityOperator.EQUALS, "")
								));
						conditions.add(EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, nationalNumber));
						
						condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
						
						telecomNumber = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().orderBy("lastUpdatedTxStamp DESC").queryFirst();
						if(UtilValidate.isNotEmpty(telecomNumber))
							partyId = telecomNumber.getString("partyId");	
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return partyId;
	}
	
	public static String getPartyIdByPrmaryEmail(Delegator delegator, String emailId) {
		String partyId = "";
		try {
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PCM", "PartyContactMech");
			dynamicView.addAlias("PCM", "partyId");
			dynamicView.addAlias("PCM", "contactMechId");
			dynamicView.addAlias("PCM", "fromDate");
			dynamicView.addAlias("PCM", "thruDate");
			dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
			dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
			dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			dynamicView.addMemberEntity("CM", "ContactMech");
			dynamicView.addAlias("CM", "infoString");
			dynamicView.addViewLink("PCM", "CM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, emailId),
					EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL")
					);
			
			GenericValue emailAddress = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(emailAddress))
				partyId = emailAddress.getString("partyId");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return partyId;
	}
	
	public static String getPrimaryEmailByPartyId(Delegator delegator, String partyId) {
		String emailId = "";
		try {
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PCM", "PartyContactMech");
			dynamicView.addAlias("PCM", "partyId");
			dynamicView.addAlias("PCM", "contactMechId");
			dynamicView.addAlias("PCM", "fromDate");
			dynamicView.addAlias("PCM", "thruDate");
			dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
			dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
			dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			dynamicView.addMemberEntity("CM", "ContactMech");
			dynamicView.addAlias("CM", "infoString");
			dynamicView.addViewLink("PCM", "CM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL")
					);
			
			GenericValue emailAddress = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(emailAddress))
				emailId = emailAddress.getString("infoString");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return emailId;
	}
	
	public static Map<String, Object> phoneNumberSplitter(String phoneNo) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Map<String, Object> result = new HashMap<String, Object>();
        try {
        	PhoneNumber phoneNumber = phoneNumberUtil.parse(phoneNo, null);
            
            int countryCode = phoneNumber.getCountryCode();
            long nationalNumber = phoneNumber.getNationalNumber();
            result.put("countryCode", countryCode+"");
            result.put("nationalNumber", nationalNumber+"");
            
            
            // Assuming you want to split the national number into area code and contact number
            int areaCodeLength = phoneNumberUtil.getLengthOfGeographicalAreaCode(phoneNumber);
            String contactNumber = String.valueOf(nationalNumber);
            
            if (areaCodeLength > 0) {
                String areaCode = contactNumber.substring(0, areaCodeLength);
                contactNumber = contactNumber.substring(areaCodeLength);
                result.put("areaCode", areaCode+"");
                //System.out.println("Country Code: " + countryCode);
                //System.out.println("Area Code: " + areaCode);
            }
            result.put("contactNumber", contactNumber+"");
            //System.out.println("Contact Number: " + contactNumber);
            
        } catch (NumberParseException e) {
        }
		return result;
    }
	
	public static int getDefaultAutoCompleteMaxRows(Delegator delegator) {
		try {
			String globalParameter =org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_AUTO_COMPLETE_MAX_ROWS","20");
			if (UtilValidate.isNotEmpty(globalParameter)) {
				return Integer.parseInt(globalParameter);
			}
		} catch (Exception e) {
		}
		return 20;
	}
	
	public static int getDefaultMaxRowsCount(Delegator delegator) {
		try {
			String globalParameter =org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_MAX_ROWS_COUNT","100");
			if (UtilValidate.isNotEmpty(globalParameter)) {
				return Integer.parseInt(globalParameter);
			}
		} catch (Exception e) {
		}
		return 100;
	}
	
}