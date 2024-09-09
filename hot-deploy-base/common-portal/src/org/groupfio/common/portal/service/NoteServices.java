/**
 * 
 */
package org.groupfio.common.portal.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;


import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class NoteServices {

	private static final String MODULE = NoteServices.class.getName();

	public static Map<String, Object> createNoteData(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		Map<String, Object> result = new HashMap<String, Object>();

		Map requestContext = (Map) context.get("requestContext");

		String noteString = (String) requestContext.get("note");
		String noteId = (String) requestContext.get("noteId");
		String noteName = (String) requestContext.get("noteName");
		String isImportant = (String) requestContext.get("isImportant");
		String campaignNoteId = (String) requestContext.get("campaignNoteId");
		String noteType = (String) requestContext.get("noteType");
		String callBackDate = (String) requestContext.get("callBackDate");
		String subProduct = (String) requestContext.get("subProduct");

		String partyId = (String) requestContext.get("partyId");

		String domainEntityType = (String) requestContext.get("domainEntityType");
		String domainEntityId = (String) requestContext.get("domainEntityId");
		String salesOpportunityId = (String) requestContext.get("salesOpportunityId");
		String custRequestId = (String) requestContext.get("custRequestId");
		String workEffortId = (String) requestContext.get("workEffortId");
		String loyaltyPointsId = (String) requestContext.get("loyaltyPointsId");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastContactDate = sdf.format(new Date());

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			Map<String, Object> noteRes = null;
			try {
				if (UtilValidate.isNotEmpty(domainEntityType) && "OPPORTUNITY".equals(domainEntityType)) {
					GenericValue salesOpportunityData = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId", salesOpportunityId).queryOne();
					if (UtilValidate.isNotEmpty(salesOpportunityData) && UtilValidate.isNotEmpty(salesOpportunityData.getString("partyId"))) {
						partyId = salesOpportunityData.getString("partyId");
					}
				} else if (UtilValidate.isNotEmpty(domainEntityType) && "SERVICE_REQUEST".equals(domainEntityType)) {
					GenericValue custRequestData = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryOne();
					if (UtilValidate.isNotEmpty(custRequestData) && UtilValidate.isNotEmpty(custRequestData.getString("fromPartyId"))) {
						partyId = custRequestData.getString("fromPartyId");
					}
				} else if (UtilValidate.isNotEmpty(domainEntityType) && ("SUBSCRIPTION".equals(domainEntityType))) {
					GenericValue subscription = EntityQuery.use(delegator).from("Subscription").where("subscriptionId", domainEntityId).queryOne();
					if (UtilValidate.isNotEmpty(subscription) && UtilValidate.isNotEmpty(subscription.getString("partyId"))) {
						partyId = subscription.getString("partyId");
					}
				} else if (UtilValidate.isNotEmpty(domainEntityType) && ("REBATE".equals(domainEntityType))) {
					GenericValue entity = EntityQuery.use(delegator).from("Agreement").where("agreementId", domainEntityId).queryOne();
					if (UtilValidate.isNotEmpty(entity) && UtilValidate.isNotEmpty(entity.getString("partyIdTo"))) {
						partyId = entity.getString("partyIdTo");
					}
				}

				if (UtilValidate.isEmpty(partyId)) {
					partyId = null;
				}

				noteRes = dispatcher.runSync("createNote", UtilMisc.toMap("partyId", partyId, "note", noteString,
						"userLogin", userLogin, "locale", locale, "noteName", noteName
						// "createdByUserLogin", userLogin.getString("userLoginId")
						));
			} catch (GenericServiceException e) {
				Debug.logError(e, e.getMessage(), MODULE);
				result.putAll(ServiceUtil.returnError(UtilProperties.getMessage("PartyErrorUiLabels",
						"PartyNoteCreationError", locale)));
				return result;
			}

			noteId = (String) noteRes.get("noteId");
			if (UtilValidate.isEmpty(noteId)) {
				result.putAll(ServiceUtil.returnError(UtilProperties.getMessage("PartyErrorUiLabels",
						"partyservices.problem_creating_note_no_noteId_returned", locale)));
				return result;
			} else {
				GenericValue noteData = null;
				try {
					noteData = delegator.findOne("NoteData", false, UtilMisc.toMap("noteId", noteId));
					if (UtilValidate.isNotEmpty(noteType)) {
						noteData.put("noteType", noteType);
					}
					if (UtilValidate.isNotEmpty(callBackDate)) {
						try {
							Date callBackDate1 = new SimpleDateFormat("yyyy-MM-dd").parse(callBackDate);
							callBackDate = sdf.format(callBackDate1);
							noteData.put("callBackDate", java.sql.Date.valueOf(callBackDate));
						} catch (ParseException pe) {
						}
					}
					if (UtilValidate.isNotEmpty(subProduct)) {
						noteData.put("subProduct", subProduct);
					}
					noteData.put("createdByUserLogin", userLogin.getString("userLoginId"));
					noteData.put("createdByUserLoginRoleTypeId",
							PartyHelper.getPartyRoleTypeId(userLogin.getString("partyId"), delegator));
					if(UtilValidate.isNotEmpty(loyaltyPointsId))
						noteData.put("loyaltyPointsId", loyaltyPointsId);
					noteData.store();

					if (UtilValidate.isNotEmpty(partyId) && (UtilValidate.isNotEmpty(domainEntityType)
							&& CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType))) {
						GenericValue partySupplData = delegator.findOne("PartySupplementalData",
								UtilMisc.toMap("partyId", partyId), false);
						if (UtilValidate.isNotEmpty(partySupplData)) {
							if (UtilValidate.isNotEmpty(callBackDate)) {
								partySupplData.set("lastCallBackDate", java.sql.Date.valueOf(callBackDate));
								partySupplData.put("lastContactDate", java.sql.Date.valueOf(lastContactDate));
								partySupplData.store();
							}
						}

						Map<String, String> fields = UtilMisc.toMap("partyId", partyId, "noteId", noteId, "isImportant",
								isImportant, "campaignId", campaignNoteId, "domainEntityId", domainEntityId,
								"domainEntityType", domainEntityType);
						GenericValue v = delegator.makeValue("PartyNote", fields);

						delegator.create(v);
					}

					if (UtilValidate.isNotEmpty(domainEntityType)
							&& domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
						Map<String, String> fields = UtilMisc.toMap("salesOpportunityId", domainEntityId, "noteId",
								noteId, "isImportant", isImportant, "domainEntityId", domainEntityId,
								"domainEntityType", domainEntityType);
						GenericValue v = delegator.makeValue("SalesOpportunityNote", fields);

						delegator.create(v);
					} else if (UtilValidate.isNotEmpty(domainEntityType)
							&& (domainEntityType.equals(DomainEntityType.SUBSCRIPTION)
									|| domainEntityType.equals(DomainEntityType.SUBS_PRODUCT)
									|| domainEntityType.equals(DomainEntityType.REBATE)
									|| domainEntityType.equals(DomainEntityType.APV_TPL)
									)
							) {
						Map<String, String> fields = UtilMisc.toMap("domainEntityId", domainEntityId,
								"domainEntityType", domainEntityType, "noteId", noteId, "isImportant", isImportant);
						GenericValue v = delegator.makeValue("CommonNote", fields);

						delegator.create(v);
					}
					if (UtilValidate.isNotEmpty(domainEntityType)
							&& CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
						Map<String, String> fields = UtilMisc.toMap("custRequestId", domainEntityId, "noteId", noteId,
								"isImportant", isImportant, "domainEntityId", domainEntityId, "domainEntityType",
								domainEntityType);
						GenericValue v = delegator.makeValue("CustRequestNote", fields);

						delegator.create(v);

						if (UtilValidate.isNotEmpty(workEffortId)) {
							Map<String, String> wenFields = UtilMisc.toMap("workEffortId", workEffortId, "noteId",
									noteId);
							GenericValue workEffortNote = delegator.makeValue("WorkEffortNote", wenFields);

							delegator.create(workEffortNote);

							GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryFirst();
							if(UtilValidate.isNotEmpty(workEffort)) {
								workEffort.set("lastModifiedDate", UtilDateTime.nowTimestamp());
								workEffort.store();
							}

						}

						if(UtilValidate.isNotEmpty(domainEntityId) || UtilValidate.isNotEmpty(custRequestId)) {
							String custReqId = UtilValidate.isNotEmpty(domainEntityId) ? domainEntityId : custRequestId;
							GenericValue custReq = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custReqId).queryFirst();
							if(UtilValidate.isNotEmpty(custReq)) {
								custReq.set("lastModifiedDate", UtilDateTime.nowTimestamp());
								custReq.store();
							}
						}
					}
					//product promo code note table
					try {
						if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.PRODUCT_PROMO_CODE)) {
							Map<String, String> productNote = UtilMisc.toMap("productPromoCodeId", domainEntityId, "noteId",noteId);
							GenericValue productPromoCodeNote = delegator.makeValue("ProductPromoCodeNote",productNote);
							delegator.create(productPromoCodeNote);
						}
					}catch(Exception e) {
						result.putAll(ServiceUtil.returnError("product promo code Id is not in product promo code table"));
						return result;
					}
				} catch (Exception e) {
					e.printStackTrace();
					result.putAll(ServiceUtil.returnError(e.getMessage()));
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.putAll(ServiceUtil.returnSuccess("Successfully created note data"));
		return result;
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public static Map<String, Object> updatePartyNotePriority(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> result = new HashMap<String, Object>();
		String partyId = (String) context.get("partyId");
		String noteId = (String) context.get("noteId");
		String domainEntityType = (String) context.get("domainEntityType");
		java.sql.Timestamp now = UtilDateTime.nowTimestamp();
		SQLProcessor sqlProcessor = null;
		try {
			sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
			String updateQuery ="";
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("UPDATE PARTY_NOTE pn ");
			sbQuery.append("INNER JOIN NOTE_DATA nd ON pn.note_id = nd.note_id ");
			sbQuery.append("SET pn.IS_IMPORTANT = 'N' ");
			sbQuery.append("WHERE pn.IS_IMPORTANT = 'Y' ");
			sbQuery.append("AND (nd.EXPIRED_DATE IS NOT NULL AND nd.EXPIRED_DATE <= NOW()) ");

			if (UtilValidate.isNotEmpty(partyId)) {
				sbQuery.append("AND pn.PARTY_ID = '"+partyId+"' ");
			}
			if (UtilValidate.isNotEmpty(noteId)) {
				sbQuery.append("AND pn.NOTE_ID = '"+noteId+"' ");
			}
			if (UtilValidate.isNotEmpty(domainEntityType)) {
				sbQuery.append("AND pn.DOMAIN_ENTITY_TYPE = '"+domainEntityType+"' ");
			}

			updateQuery = sbQuery.toString();
			Debug.log("----Quety Sql --"+updateQuery);
			Debug.logInfo("Update SQL Update Start: "+UtilDateTime.nowTimestamp(), MODULE);

			sqlProcessor.prepareStatement(updateQuery);
			int resCount = sqlProcessor.executeUpdate();

			Debug.logInfo("Records updated count "+resCount,MODULE);
			Debug.logInfo("Update SQL Update END: "+UtilDateTime.nowTimestamp(), MODULE);

		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}finally {
			try {
				sqlProcessor.close();
			} catch (GenericDataSourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		result.putAll(ServiceUtil.returnSuccess("Successfully updated note data priority"));
		return result;
	}
}
