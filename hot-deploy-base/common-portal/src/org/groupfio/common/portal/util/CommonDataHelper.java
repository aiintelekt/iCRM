/**
 * 
 */
package org.groupfio.common.portal.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class CommonDataHelper {

	private static String MODULE = CommonDataHelper.class.getName();
	
	public static Map<String, Object> getCampaignNames(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> campaignIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(campaignIds)) {
					results = campaignIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
							x -> {
								String campaignName = UtilCampaign.getCampaignName(delegator, x);
								return UtilValidate.isNotEmpty(campaignName) ? campaignName : "";
							},
							(attr1, attr2) -> {
								return attr2;
							}));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getPartyLTDValues(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(partyIds)) {
					results = partyIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
							x -> {
								String ltdValue = "";
								try {
									String clvSql = null;
									ResultSet rs = null;
									SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
							        clvSql = "SELECT `PROPERTY_VALUE` as 'clv' FROM `party_metric_indicator` WHERE `CUSTOM_FIELD_ID`='TOTAL_PURCHASED' AND `PARTY_ID`='" + x + "'";

									rs = sqlProcessor.executeQuery(clvSql);
									if (rs != null) {
										while (rs.next()) {
											ltdValue= rs.getString("clv");
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								return UtilValidate.isNotEmpty(ltdValue) ? ltdValue : "";
							},
							(attr1, attr2) -> {
								return attr2;
							}));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getPartyPostals(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(partyIds)) {
					results = partyIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
							x -> {
								GenericValue postal = UtilContactMech.getPartyPostal(delegator, x, null, true);
								return UtilValidate.isNotEmpty(postal) ? postal : "";
							},
							(attr1, attr2) -> {
								return attr2;
							}));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getPartyPhones(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(partyIds)) {
					results = partyIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
							x -> {
								String phoneNumber = UtilContactMech.getPartyPhone(delegator, x, null, true);
								return UtilValidate.isNotEmpty(phoneNumber) ? phoneNumber : "";
							},
							(attr1, attr2) -> {
								return attr2;
							}));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getPersonList(Delegator delegator, List<GenericValue> dataList, String fieldId, Set<String> fieldToSelect) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(partyIds)) {
					results = partyIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
							x -> {
								GenericValue person = null;
								try {
									person = EntityQuery.use(delegator).select(fieldToSelect).from("Person").where("partyId", x).queryOne();
								} catch (GenericEntityException e) {
									e.printStackTrace();
								}
								
								return UtilValidate.isNotEmpty(person) ? person : "";
							},
							(attr1, attr2) -> {
								return attr2;
							}));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getPartyImportantNoteCount(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(partyIds)) {
					results = partyIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
							x -> {
								long count = 0;
								try {
									count = EntityQuery.use(delegator).select("noteId").from("PartyNoteView").where("targetPartyId", x, "isImportant", "Y").queryCount();
								} catch (GenericEntityException e) {
									e.printStackTrace();
								}
								
								return count;
							},
							(attr1, attr2) -> {
								return attr2;
							}));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getCurrentTimeForTimezones(Map<String, Object> timeZones) {
		Map<String, Object> currentTimeForTimezones = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(timeZones)) {
				for (String timeZoneKey : timeZones.keySet()) {
					String timeZoneVal = (String) timeZones.get(timeZoneKey);
					if (UtilValidate.isNotEmpty(timeZoneVal)) {
						SimpleDateFormat df = new SimpleDateFormat("HH:mm");
						Calendar cal = Calendar.getInstance((TimeZone.getTimeZone(timeZoneVal)));
						df.setTimeZone(cal.getTimeZone());
						currentTimeForTimezones.put(timeZoneKey, df.format(cal.getTime()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentTimeForTimezones;
	}
	
}
