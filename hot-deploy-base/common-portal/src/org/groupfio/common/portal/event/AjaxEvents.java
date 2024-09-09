/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.groupfio.common.portal.event;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.fio.admin.portal.constant.AdminPortalConstant.GlobalParameter;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.constants.GlobalConstants.Channels;
import org.fio.homeapps.constants.GlobalConstants.DateTimeTypeConstant;
import org.fio.homeapps.constants.GlobalConstants.EnumDisplayType;
import org.fio.homeapps.util.ActivityDataHelper;
import org.fio.homeapps.util.AgreementDataHelper;
import org.fio.homeapps.util.CacheUtil;
import org.fio.homeapps.util.CommonDataHelper;
import org.fio.homeapps.util.CommonUtils;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.QueryUtil;
import org.fio.homeapps.util.ResponseUtils;
import org.fio.homeapps.util.SrDataHelper;
import org.fio.homeapps.util.StatusUtil;
import org.fio.homeapps.util.UtilActivity;
import org.fio.homeapps.util.UtilDateTime;
import org.fio.homeapps.util.UtilMessage;
import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.CommonPortalConstants.EventResponse;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.common.portal.util.CommonPortalUtil;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.DataUtil;
import org.groupfio.common.portal.util.LoginFilterUtil;
import org.groupfio.common.portal.util.OrderDataHelper;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.groupfio.common.portal.util.ReportUtil;
import org.groupfio.common.portal.util.ResAvailUtil;
import org.groupfio.common.portal.util.SrUtil;
import org.groupfio.common.portal.util.UtilAttribute;
import org.groupfio.common.portal.util.UtilCampaign;
import org.groupfio.common.portal.util.UtilCommon;
import org.groupfio.common.portal.util.UtilContactMech;
import org.groupfio.common.portal.util.UtilOrder;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelUtil;
import org.ofbiz.entity.model.ModelViewEntity.ModelViewLink;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceContainer;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Sharif Ul Islam
 * 
 */
public final class AjaxEvents {

	public AjaxEvents() {
	}

	private static final String MODULE = AjaxEvents.class.getName();
	public static final String resource = "PartyUiLabels";
	public static final String resourceError = "PartyErrorUiLabels";

	public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
		return doJSONResponse(response, jsonObject.toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
		return doJSONResponse(response, JSONArray.fromObject(collection).toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Map map) {
		return doJSONResponse(response, JSONObject.fromObject(map));
	}

	public static String doJSONResponse(HttpServletResponse response, String jsonString) {
		String result = "success";

		response.setContentType("application/x-json");
		try {
			response.setContentLength(jsonString.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			Debug.logWarning(
					"Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(),
					MODULE);
			response.setContentLength(jsonString.length());
		}

		Writer out;
		try {
			out = response.getWriter();
			out.write(jsonString);
			out.flush();
		} catch (IOException e) {
			Debug.logError(e, "Failed to get response writer", MODULE);
			result = "error";
		}
		return result;
	}

	public static GenericValue getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (GenericValue) session.getAttribute("userLogin");
	}

	/*************************************************************************/
	/**                                                                     **/
	/** Common JSON Requests **/
	/**                                                                     **/
	/*************************************************************************/

	public static String searchServiceRequests(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String) context.get("partyId");

		String cin = (String) context.get("cin");
		String srNo = (String) context.get("srNo");
		String srType = (String) context.get("srType");
		String srStatus = (String) context.get("srStatus");
		String email = (String) context.get("email");
		String phone = (String) context.get("phone");

		String owner = (String) context.get("owner");
		String srArea = (String) context.get("srArea");
		String srSubStatus = (String) context.get("srSubStatus");
		String createdBy = (String) context.get("createdBy");
		String srSubArea = (String) context.get("srSubArea");
		String open = (String) context.get("open");
		String slaAtRisk = (String) context.get("slaAtRisk");
		String slaExpired = (String) context.get("slaExpired");
		String closed = (String) context.get("closed");
		String unAssigned = (String) context.get("unAssigned");
		// String startDate = (String) context.get("startDate");
		// String endDate = (String) context.get("endDate");
		String startDate = (String) context.get("srDateRange_from");
		String endDate = (String) context.get("srDateRange_to");

		String priority = (String) context.get("priority");
		String orderId = (String) context.get("orderId");
		String srName = (String) context.get("srName");
		String srPrimaryContactId = (String) context.get("srPrimaryContactId");
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalKey = (String) context.get("externalLoginKey");
		Debug.log("externalLoginKey***********" + externalKey);
		Debug.log("domainEntityType***********" + domainEntityType);
		String searchType = request.getParameter("searchType");

		Timestamp systemTime = UtilDateTime.nowTimestamp();
		ArrayList<String> statuses = new ArrayList<String>();

		String clientPortal = (String) context.get("clientPortal");
		Debug.log("clientPortal========" + clientPortal);

		try {

			String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

			// Integrate security matrix logic start
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String businessUnit = null;

			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId) && UtilValidate.isEmpty(clientPortal)) {
				String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Read");
				accessMatrixMap.put("entityName", "CustRequest");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
			}
			// Integrate security matrix logic end
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {

				List<EntityCondition> conditionlist = FastList.newInstance();
				// check with ownerId
				if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					conditionlist.add(EntityCondition.makeCondition("owner", EntityOperator.IN, ownerIds));
				}

				// check with emplTeamId
				if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					conditionlist.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
				}

				//				if (UtilValidate.isNotEmpty(partyId)) {
				//					conditionlist.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, partyId));
				//				}

				if (UtilValidate.isNotEmpty(partyId)) {
					String partySecurityRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, partyId);
					conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("assocPartyId", EntityOperator.EQUALS, partyId),
							EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, partySecurityRole)
							));
				}

				if (UtilValidate.isNotEmpty(startDate)) {
					startDate = df1.format(df2.parse(startDate));
					conditionlist.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO,
							UtilDateTime.getDayStart(Timestamp.valueOf(startDate))));
				}

				if (UtilValidate.isNotEmpty(endDate)) {
					endDate = df1.format(df2.parse(endDate));
					conditionlist.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO,
							UtilDateTime.getDayEnd(Timestamp.valueOf(endDate))));
				}

				if (UtilValidate.isNotEmpty(cin)) {
					conditionlist.add(EntityCondition.makeCondition("attrValue", EntityOperator.LIKE, "" + cin + "%"));
					conditionlist
					.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "CIF_REFERENCE"));
				}
				if (UtilValidate.isNotEmpty(srNo)) {
					conditionlist.add(EntityCondition.makeCondition("srNumber", EntityOperator.LIKE, "" + srNo + "%"));
				}
				if (UtilValidate.isNotEmpty(srType)) {
					conditionlist.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, srType));
				}
				if (UtilValidate.isNotEmpty(srArea)) {
					conditionlist.add(EntityCondition.makeCondition("category", EntityOperator.EQUALS, srArea));
				}
				if (UtilValidate.isNotEmpty(srSubArea)) {
					conditionlist.add(EntityCondition.makeCondition("subCategory", EntityOperator.EQUALS, srSubArea));
				}

				if (UtilValidate.isNotEmpty(email)) {
					conditionlist.add(EntityCondition.makeCondition("email", EntityOperator.LIKE, "" + email + "%"));
				}
				if (UtilValidate.isNotEmpty(owner)) {
					conditionlist.add(EntityCondition.makeCondition("owner", EntityOperator.EQUALS, owner));
				}
				if (UtilValidate.isNotEmpty(srSubStatus)) {
					conditionlist.add(EntityCondition.makeCondition("subStatus", EntityOperator.EQUALS, srSubStatus));
				}
				if (UtilValidate.isNotEmpty(phone)) {
					conditionlist.add(
							EntityCondition.makeCondition("contactNumber", EntityOperator.LIKE, "" + phone + "%"));
				}

				if (UtilValidate.isNotEmpty(createdBy)) {
					conditionlist.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.LIKE,
							"" + createdBy + "%"));
				}
				if (UtilValidate.isNotEmpty(priority)) {
					conditionlist.add(
							EntityCondition.makeCondition("priority", EntityOperator.EQUALS, Long.parseLong(priority)));
				}
				if (UtilValidate.isNotEmpty(srName)) {
					conditionlist.add(EntityCondition.makeCondition("srName", EntityOperator.LIKE, "%" + srName + "%"));
				}
				if (UtilValidate.isNotEmpty(orderId)) {
					conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				}
				if (UtilValidate.isNotEmpty(srPrimaryContactId)) {
					conditionlist.add(EntityCondition.makeCondition("primaryContactId", EntityOperator.EQUALS,
							srPrimaryContactId));
				}

				if (UtilValidate.isNotEmpty(open)) {
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_ASSIGNED);
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_OPEN);
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_IN_PROGRESS);
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_PENDING);
				}
				if (UtilValidate.isNotEmpty(closed)) {
					statuses.add(CommonPortalConstants.srClosedStatuses.SR_CLOSED);
					statuses.add(CommonPortalConstants.srClosedStatuses.SR_CANCELLED);
				}
				if (UtilValidate.isNotEmpty(srStatus)) {
					statuses.add(srStatus);
				}
				/*
				 * if(UtilValidate.isNotEmpty(slaExpired) ||
				 * UtilValidate.isNotEmpty(slaAtRisk)) {
				 * statuses.add(CommonPortalConstants.srOpenStatuses.SR_OPEN);
				 * statuses.add(CommonPortalConstants.srOpenStatuses.SR_PENDING)
				 * ; statuses.add(CommonPortalConstants.srOpenStatuses.
				 * SR_INFO_PROV);
				 * statuses.add(CommonPortalConstants.srOpenStatuses.SR_ASSIGNED
				 * );
				 * statuses.add(CommonPortalConstants.srOpenStatuses.SR_OVER_DUE
				 * ); statuses.add(CommonPortalConstants.srOpenStatuses.
				 * SR_ESCALATED);
				 * statuses.add(CommonPortalConstants.srOpenStatuses.
				 * SR_RESEARCHING); }
				 */
				if (UtilValidate.isNotEmpty(unAssigned) || UtilValidate.isNotEmpty(searchType)
						&& searchType.equals(CommonPortalConstants.SrSearchType.UN_ASSIGNED_SRS)) {
					List conditionUnassignList = FastList.newInstance();

					conditionUnassignList.add(EntityCondition.makeCondition(
							UtilMisc.toList(
									EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, null),
									EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, "")),
							EntityOperator.OR));
					EntityCondition mainUnassigneConditons = EntityCondition.makeCondition(conditionUnassignList,
							EntityOperator.AND);

					List<GenericValue> custRequestUnAssignedList = delegator.findList("CustRequest",
							mainUnassigneConditons, UtilMisc.toSet("custRequestId"), null, null, false);

					List<String> unAssignedcustRequestIds = EntityUtil
							.getFieldListFromEntityList(custRequestUnAssignedList, "custRequestId", true);
					if (UtilValidate.isNotEmpty(unAssignedcustRequestIds)) {
						conditionlist.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN,
								unAssignedcustRequestIds));
					}

				}
				if (UtilValidate.isNotEmpty(searchType)
						&& searchType.equals(CommonPortalConstants.SrSearchType.MY_SRS)) {
					conditionlist.add(EntityCondition.makeCondition("owner", EntityOperator.EQUALS, userLoginId));
				}
				if (UtilValidate.isNotEmpty(searchType)
						&& searchType.equals(CommonPortalConstants.SrSearchType.MY_OPEN_SRS)) {
					conditionlist.add(EntityCondition.makeCondition("owner", EntityOperator.EQUALS, userLoginId));
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_ASSIGNED);
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_OPEN);
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_IN_PROGRESS);
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_PENDING);
				}
				if (UtilValidate.isNotEmpty(searchType)
						&& searchType.equals(CommonPortalConstants.SrSearchType.MY_CLOSED_SRS)) {
					conditionlist.add(EntityCondition.makeCondition("owner", EntityOperator.EQUALS, userLoginId));
					statuses.add(CommonPortalConstants.srClosedStatuses.SR_CLOSED);
					statuses.add(CommonPortalConstants.srClosedStatuses.SR_CANCELLED);
				}
				List<EntityCondition> flagConditions = new ArrayList<EntityCondition>();
				if (UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal)) {
					statuses.clear();
					if (UtilValidate.isNotEmpty(open)) {
						// excluding closing
						statuses.add(CommonPortalConstants.srClosedStatuses.SR_CLOSED);
						statuses.add(CommonPortalConstants.srClosedStatuses.SR_CANCELLED);

					}
					if (UtilValidate.isNotEmpty(closed)) {
						statuses.add(CommonPortalConstants.srClosedStatuses.SR_CLOSED);
						statuses.add(CommonPortalConstants.srClosedStatuses.SR_CANCELLED);
					}
					if (UtilValidate.isNotEmpty(open) && UtilValidate.isNotEmpty(closed)) {
						statuses.clear();
						/*
						 * statuses.add(CommonPortalConstants.srClosedStatuses.
						 * SR_CLOSED);
						 * statuses.add(CommonPortalConstants.srClosedStatuses.
						 * SR_CANCELLED);
						 */
					}
					if (UtilValidate.isNotEmpty(srStatus)) {
						statuses.add(srStatus);
					}
					if (UtilValidate.isNotEmpty(searchType) && "client-all-srs".equals(searchType)) {
						/*
						 * statuses.add(CommonPortalConstants.srOpenStatuses.
						 * SR_ASSIGNED);
						 * statuses.add(CommonPortalConstants.srOpenStatuses.
						 * SR_OPEN);
						 * statuses.add(CommonPortalConstants.srOpenStatuses.
						 * SR_IN_PROGRESS);
						 * statuses.add(CommonPortalConstants.srOpenStatuses.
						 * SR_PENDING); statuses.add("SR_FEED_PROVIDED");
						 */
					} else if (UtilValidate.isNotEmpty(searchType) && "client-open-srs".equals(searchType)) {
						/*
						 * statuses.add(CommonPortalConstants.srOpenStatuses.
						 * SR_ASSIGNED);
						 * statuses.add(CommonPortalConstants.srOpenStatuses.
						 * SR_OPEN);
						 * statuses.add(CommonPortalConstants.srOpenStatuses.
						 * SR_IN_PROGRESS); statuses.add("SR_FEED_PROVIDED");
						 */
						statuses.add(CommonPortalConstants.srClosedStatuses.SR_CLOSED);
						statuses.add(CommonPortalConstants.srClosedStatuses.SR_CANCELLED);
					}
					if (UtilValidate.isNotEmpty(searchType) && "client-feedback-srs".equals(searchType)) {
						statuses.add(CommonPortalConstants.srOpenStatuses.SR_PENDING);
					}
				}

				if (UtilValidate.isNotEmpty(slaAtRisk) && "Y".equalsIgnoreCase(slaAtRisk)) {
					Timestamp now = UtilDateTime.nowTimestamp();
					flagConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("preEscalationDate", EntityOperator.LESS_THAN, now),
							EntityCondition.makeCondition("dateDue", EntityOperator.GREATER_THAN, now),
							EntityCondition.makeCondition("status", EntityOperator.NOT_IN,
									UtilMisc.toList(CommonPortalConstants.srClosedStatuses.SR_CLOSED,
											CommonPortalConstants.srClosedStatuses.SR_CANCELLED))));
				}
				if (UtilValidate.isNotEmpty(slaExpired) && "Y".equalsIgnoreCase(slaExpired)) {
					Timestamp now = UtilDateTime.nowTimestamp();
					flagConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("dateDue", EntityOperator.LESS_THAN, now),
							EntityCondition.makeCondition("status", EntityOperator.NOT_IN,
									UtilMisc.toList(CommonPortalConstants.srClosedStatuses.SR_CLOSED,
											CommonPortalConstants.srClosedStatuses.SR_CANCELLED))));
				}
				if (UtilValidate.isNotEmpty(statuses)) {
					if ((UtilValidate.isNotEmpty(searchType) && "client-open-srs".equals(searchType))
							|| ((UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal))
									&& (UtilValidate.isNotEmpty(open)
											|| (UtilValidate.isNotEmpty(open) && UtilValidate.isNotEmpty(closed))))) {
						flagConditions.add(EntityCondition.makeCondition("status", EntityOperator.NOT_IN, statuses));
					} else if (UtilValidate.isNotEmpty(searchType) && "client-all-srs".equals(searchType)) {
					} else {
						flagConditions.add(EntityCondition.makeCondition("status", EntityOperator.IN, statuses));
					}
				}

				if (UtilValidate.isNotEmpty(flagConditions)) {
					EntityCondition conditions = EntityCondition.makeCondition(flagConditions, EntityOperator.OR);
					conditionlist.add(conditions);
				}

				/*
				 * if(UtilValidate.isNotEmpty(slaExpired)) {
				 * conditionlist.add(EntityCondition.makeCondition("dateDue",
				 * EntityOperator.LESS_THAN_EQUAL_TO,
				 * UtilDateTime.nowTimestamp())); }
				 * if(UtilValidate.isNotEmpty(slaAtRisk)) { Timestamp
				 * slaAtRiskDate = ReportUtil.getPrevBusinessDateBeforeSystem(
				 * CommonPortalConstants.SLA_AT_RISK); slaAtRiskDate =
				 * ReportUtil.isDeclaredHoliday(delegator,slaAtRiskDate);
				 * conditionlist.add(EntityCondition.makeCondition("dateDue",
				 * EntityOperator.BETWEEN,
				 * UtilMisc.toList(slaAtRiskDate,UtilDateTime.nowTimestamp())));
				 * }
				 */
				// EntityCondition condition =
				// EntityCondition.makeCondition(conditionlist,
				// EntityOperator.AND);

				EntityCondition condition = null;
				if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals("CONTACT")) {
					List conditionsList = FastList.newInstance();

					conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					conditionsList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
					conditionsList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
					List<GenericValue> custRequestListList = delegator.findList("CustRequestContact", mainConditons,
							UtilMisc.toSet("custRequestId"), null, null, false);
					if (UtilValidate.isNotEmpty(custRequestListList)) {
						List<String> custRequestIds = EntityUtil.getFieldListFromEntityList(custRequestListList,
								"custRequestId", true);
						conditionlist.clear();
						conditionlist
						.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestIds));
						EntityCondition conditions = EntityCondition.makeCondition(flagConditions, EntityOperator.OR);
						conditionlist.add(conditions);
						condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
					}

				} else {
					condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				}
				Debug.log("condition=========" + condition);
				EntityFindOptions efo = new EntityFindOptions();
				efo.setOffset(0);
				efo.setLimit(1000);
				if (UtilValidate.isNotEmpty(condition)) {
					List<GenericValue> serviceRequests = delegator.findList("CustRequestSummary2", condition, null,
							UtilMisc.toList("dateDue"), efo, false);

					List custRequestIds = EntityUtil.getFieldListFromEntityList(serviceRequests, "custRequestId", true);
					List<EntityCondition> conditionList = FastList.newInstance();
					conditionList
					.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestIds));
					conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
					conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SALES_REP"));
					EntityCondition custRequestPartyCondition = EntityCondition.makeCondition(conditionList,
							EntityOperator.AND);

					List<GenericValue> custRequestPartyList = EntityQuery.use(delegator)
							.select("custRequestId", "partyId").from("CustRequestParty")
							.where(custRequestPartyCondition).queryList();
					List custPartyIds = EntityUtil.getFieldListFromEntityList(custRequestPartyList, "partyId", true);
					List<GenericValue> salesRepUserLoginList = EntityQuery.use(delegator)
							.select("userLoginId", "partyId").from("UserLogin")
							.where(EntityCondition.makeCondition("partyId", EntityOperator.IN, custPartyIds))
							.queryList();

					if (UtilValidate.isNotEmpty(serviceRequests)) {
						
						String servicePortalName = SrUtil.getServicePortalName(delegator);

						for (GenericValue serviceRequest : serviceRequests) {

							Map<String, Object> data = new HashMap<String, Object>();

							String custRequestId = serviceRequest.getString("custRequestId");

							String salePersonName = "";
							List<GenericValue> custPartyData = EntityUtil.filterByCondition(custRequestPartyList,
									EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,
											custRequestId));
							if (UtilValidate.isNotEmpty(custPartyData)) {
								GenericValue custPartyDataVal = EntityUtil.getFirst(custPartyData);
								List<GenericValue> custPartyUserLoginData = EntityUtil.filterByCondition(
										salesRepUserLoginList, EntityCondition.makeCondition("partyId",
												EntityOperator.EQUALS, custPartyDataVal.getString("partyId")));
								if (UtilValidate.isNotEmpty(custPartyUserLoginData)) {
									salePersonName = PartyHelper.getUserLoginName(delegator,
											custPartyUserLoginData.get(0).getString("userLoginId"), false);
								}
							}
							data.put("salePersonName", salePersonName);

							data.put("srNumber", serviceRequest.getString("srNumber"));
							data.put("nationalId", serviceRequest.getString("custReqNatId"));
							data.put("vplusNumber", serviceRequest.getString("custReqVplusId"));
							data.put("urgency", "");
							//data.put("supervisorReview", serviceRequest.getString("supervisorReviewBy"));
							data.put("purchaseOrder", serviceRequest.getString("purchaseOrder"));

							String status = serviceRequest.getString("status");
							if (UtilValidate.isNotEmpty(status)) {
								data.put("srStatus",
										org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, status));
							}

							if (UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal)) {
								Set<String> clientOpenStatus = new HashSet<String>();
								clientOpenStatus.add("SR_OPEN");
								clientOpenStatus.add("SR_ASSIGNED");
								clientOpenStatus.add("SR_IN_PROGRESS");
								clientOpenStatus.add("SR_FEED_PROVIDED");

								if (clientOpenStatus.contains(status)) {
									data.put("srStatus",
											org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, "SR_OPEN"));
								}
							}

							String atRisk = "No";
							if (!UtilMisc.toList("SR_CLOSED", "SR_CANCELLED").contains(status)) {
								Timestamp dueDateTimeStamp = serviceRequest.getTimestamp("dateDue");
								Timestamp preEscalationTimeStamp = serviceRequest.getTimestamp("preEscalationDate");
								Timestamp now = UtilDateTime.nowTimestamp();
								if (UtilValidate.isNotEmpty(preEscalationTimeStamp)
										&& UtilValidate.isNotEmpty(dueDateTimeStamp)
										&& now.after(preEscalationTimeStamp) && now.before(dueDateTimeStamp)) {
									atRisk = "Yes";
								}
							}
							data.put("slaRisk", atRisk);

							String overDue = "No";
							if (!UtilMisc.toList("SR_CLOSED", "SR_CANCELLED").contains(status)) {
								Timestamp dueDateTimeStamp = serviceRequest.getTimestamp("dateDue");
								Timestamp now = UtilDateTime.nowTimestamp();
								if (UtilValidate.isNotEmpty(dueDateTimeStamp) && now.after(dueDateTimeStamp)) {
									overDue = "Yes";
								}
							}
							data.put("overDue", overDue);

							String subStatus = serviceRequest.getString("subStatus");
							if ("SR_ACTIVE".equals(serviceRequest.getString("subStatus"))) {
								subStatus = "Active";
							} else if ("SR_COMPLETED".equals(serviceRequest.getString("subStatus"))) {
								subStatus = "Completed";
							} else if ("SR_FAILED".equals(serviceRequest.getString("subStatus"))) {
								subStatus = "Failed";
							} else if ("SR_INPROGRESS".equals(serviceRequest.getString("subStatus"))) {
								subStatus = "In Progress";
							} else if ("SR_PARTIALLY_EXECUTED".equals(serviceRequest.getString("subStatus"))) {
								subStatus = "Partially Executed";
							} else if ("SR_REJECTED".equals(serviceRequest.getString("subStatus"))) {
								subStatus = "Rejected";
							} else if ("SR_RESOLVED".equals(serviceRequest.getString("subStatus"))) {
								subStatus = "Resolved";
							} else if ("SR_RETURNED".equals(serviceRequest.getString("subStatus"))) {
								subStatus = "Returned";
							} else if ("SR_SUB_CANCELLED".equals(serviceRequest.getString("subStatus"))) {
								subStatus = "Cancelled";
							} else if ("61297".equals(serviceRequest.getString("subStatus"))
									|| "SR_PENDING_CANCELLATION".equals(serviceRequest.getString("subStatus"))) {
								subStatus = "Pending Cancellation";
							}
							data.put("srSubStatus", subStatus);

							// build cache [start]

							String srSource = "";
							String ownerBU = "";

							if (UtilValidate.isNotEmpty(serviceRequest.getString("source"))) {
								String srSourceCacheKey = "ENUM_DESC_" + serviceRequest.getString("source") + "_"
										+ "CASE_ORIGIN_CODE";
								if (CacheUtil.getInstance().notContains(srSourceCacheKey)) {
									CacheUtil.getInstance().put(srSourceCacheKey, EnumUtil.getEnumDescription(delegator,
											serviceRequest.getString("source"), "CASE_ORIGIN_CODE"));
								}
								srSource = (String) CacheUtil.getInstance().get(srSourceCacheKey);
							}

							if (UtilValidate.isNotEmpty(serviceRequest.getString("ownerBu"))) {
								String ownerBUCacheKey = "OWNER_BU_" + serviceRequest.getString("ownerBu");
								if (CacheUtil.getInstance().notContains(ownerBUCacheKey)) {
									CacheUtil.getInstance().put(ownerBUCacheKey, DataUtil.getBusinessUnitName(delegator,
											serviceRequest.getString("ownerBu")));
								}

								ownerBU = (String) CacheUtil.getInstance().get(ownerBUCacheKey);
							}

							// build cache [end]

							data.put("srSource", srSource);
							data.put("responseMethod", "");
							data.put("refNumber", "");
							data.put("pwebMktgId", "");
							data.put("ownerBU", ownerBU);
							data.put("owner", serviceRequest.getString("owner"));
							if (UtilValidate.isNotEmpty(serviceRequest.getString("owner"))) {
								data.put("ownerName", PartyHelper.getUserLoginName(delegator,
										serviceRequest.getString("owner"), false));
							}
							data.put("opp", "");

							String modifiedOn = "";
							if (UtilValidate.isNotEmpty(serviceRequest.getString("lastModifiedDate"))) {
								modifiedOn = DataUtil.convertDateTimestamp(serviceRequest.getString("lastModifiedDate"),
										new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP,
										DateTimeTypeConstant.STRING);
							}

							data.put("modifiedOn", modifiedOn);
							data.put("modifiedBy", serviceRequest.getString("lastModifiedByUserLogin"));
							if (UtilValidate.isNotEmpty(serviceRequest.getString("lastModifiedByUserLogin"))) {
								data.put("modifiedByName", PartyHelper.getUserLoginName(delegator,
										serviceRequest.getString("lastModifiedByUserLogin"), false));
							}
							String lastUserAssignedDate = "";
							if (UtilValidate.isNotEmpty(custRequestId)) {
								GenericValue lastAssigned = EntityQuery.use(delegator).select("changedDate")
										.from("EntityAuditLog")
										.where("pkCombinedValueText", custRequestId, "changedEntityName", "CustRequest",
												"changedFieldName", "responsiblePerson")
										.orderBy("changedDate DESC").queryFirst();
								if (UtilValidate.isNotEmpty(lastAssigned)) {
									lastUserAssignedDate = DataUtil.convertDateTimestamp(
											lastAssigned.getString("changedDate"),
											new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP,
											DateTimeTypeConstant.STRING);
								}
							}
							data.put("lastUserAssignedDate", lastUserAssignedDate); // data.put("lastUserAssignedDate",
							// "2019/02/08");
							data.put("lastUpdatedByFromIserve", "");
							data.put("instructionComment", serviceRequest.getString("instruction"));
							data.put("followUpNeeded", serviceRequest.getString("customerRequestAction"));

							// int diffInDays
							// =ReportUtil.getWorkingDaysBetweenDates(delegator,
							// serviceRequest.getTimestamp("createdDate"),systemTime);
							String diffInDays = "3 Days"; // Default
							GenericValue slaSetupConfig = EntityQuery.use(delegator)
									.select("slaPeriodLvl", "srPeriodUnit", "slaConfigId", "srPriority")
									.from("SrSlaConfig")
									.where("srTypeId", serviceRequest.getString("type"), "srCategoryId",
											serviceRequest.getString("category"), "srSubCategoryId",
											serviceRequest.getString("subCategory"), "status", "ACTIVE")
									.cache(true).queryFirst();
							if (UtilValidate.isNotEmpty(slaSetupConfig)
									&& UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))) {
								if (UtilValidate.isNotEmpty(slaSetupConfig.getString("srPriority"))
										&& UtilValidate.isNotEmpty(serviceRequest.getString("priority"))
										&& serviceRequest.getString("priority")
										.equals(slaSetupConfig.getString("srPriority"))) {
									diffInDays = UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))
											? slaSetupConfig.getString("srPeriodUnit") : "";
											diffInDays = diffInDays + " " + slaSetupConfig.getString("slaPeriodLvl");
								} else {
									// Debug.log("slaConfigId>>>>
									// "+slaSetupConfig.getString("slaConfigId"));
									if (UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))) {
										diffInDays = UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))
												? slaSetupConfig.getString("srPeriodUnit") : "";
												diffInDays = diffInDays + " " + slaSetupConfig.getString("slaPeriodLvl");
									}
								}
							} else {
								slaSetupConfig = EntityQuery.use(delegator)
										.select("slaPeriodLvl", "srPeriodUnit", "slaConfigId").from("SrSlaConfig")
										.where("status", "ACTIVE", "srTypeId", serviceRequest.getString("type"))
										.cache(true).queryFirst();
								if (UtilValidate.isNotEmpty(slaSetupConfig)
										&& UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))) {
									diffInDays = UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))
											? slaSetupConfig.getString("srPeriodUnit") : "";
											diffInDays = diffInDays + " " + slaSetupConfig.getString("slaPeriodLvl");
								}
							}

							data.put("duration", diffInDays);

							String dateDue = "";
							if (UtilValidate.isNotEmpty(serviceRequest.getString("dateDue"))) {
								dateDue = DataUtil.convertDateTimestamp(serviceRequest.getString("dateDue"),
										new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP,
										DateTimeTypeConstant.STRING);
							}
							data.put("dueDate", dateDue);

							data.put("documentsNeeded", serviceRequest.getString("documentNeeded"));

							int daysOverDue = 0;
							if (UtilValidate.isNotEmpty(dateDue)
									&& systemTime.after(serviceRequest.getTimestamp("dateDue"))) {
								if (UtilValidate.isNotEmpty(serviceRequest.getTimestamp("closedByDate"))
										&& "SR_CLOSED".equals(serviceRequest.getString("status"))
										&& (serviceRequest.getTimestamp("closedByDate")
												.compareTo(serviceRequest.getTimestamp("dateDue")) == 1)) {
									daysOverDue = ReportUtil.getWorkingDaysBetweenDates(delegator,
											serviceRequest.getTimestamp("dateDue"),
											serviceRequest.getTimestamp("closedByDate"));
								} else if (!"SR_CLOSED".equals(serviceRequest.getString("status"))
										&& serviceRequest.getTimestamp("dateDue").before(systemTime)) {
									daysOverDue = ReportUtil.getWorkingDaysBetweenDates(delegator,
											serviceRequest.getTimestamp("dateDue"), systemTime);
								}
							}
							data.put("daysOverdue", daysOverDue);

							String closedByDate = "";
							if (UtilValidate.isNotEmpty(serviceRequest.getString("closedByDate"))) {
								closedByDate = DataUtil.convertDateTimestamp(serviceRequest.getString("closedByDate"),
										new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP,
										DateTimeTypeConstant.STRING);
							}

							data.put("dateClosed", closedByDate);
							data.put("closedBy", serviceRequest.getString("closedBy"));
							String createdDate = "";
							if (UtilValidate.isNotEmpty(serviceRequest.getString("createdDate"))) {
								createdDate = DataUtil.convertDateTimestamp(serviceRequest.getString("createdDate"),
										new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP,
										DateTimeTypeConstant.STRING);
							}
							if (UtilValidate.isNotEmpty(serviceRequest.getString("closedBy"))) {
								data.put("closedByName", PartyHelper.getUserLoginName(delegator,
										serviceRequest.getString("closedBy"), false));
							}

							String openDate = "";
							if (UtilValidate.isNotEmpty(serviceRequest.getString("openDateTime"))) {
								openDate = DataUtil.convertDateTimestamp(serviceRequest.getString("openDateTime"),
										new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP,
										DateTimeTypeConstant.STRING);
							}

							data.put("openDate", createdDate);
							data.put("createdOn", createdDate);
							data.put("createdByFromIserve", "");
							data.put("createdBy", serviceRequest.getString("createdByUserLogin"));
							data.put("act", "");
							/*data.put("accountName", serviceRequest.getString("accountName"));
							data.put("account", serviceRequest.getString("accountNumber"));*/
							data.put("custRequestId", custRequestId);
							data.put("partyId", serviceRequest.getString("customerId"));
							data.put("orderId", serviceRequest.getString("orderId"));
							data.put("srName", serviceRequest.getString("srName"));

							if (UtilValidate.isNotEmpty(serviceRequest.getString("onceAndDone"))) {
								if ("Y".equals(serviceRequest.getString("onceAndDone")))
									data.put("onceAndDone", "Yes");
								if ("N".equals(serviceRequest.getString("onceAndDone")))
									data.put("onceAndDone", "No");
							}

							if (UtilValidate.isNotEmpty(serviceRequest.getString("createdByUserLogin"))) {
								data.put("createdByName", PartyHelper.getUserLoginName(delegator,
										serviceRequest.getString("createdByUserLogin"), false));
							}

							if (UtilValidate.isNotEmpty(serviceRequest.getString("cif"))) {
								data.put("cif", serviceRequest.getString("cif"));
							} else {
								String cifCacheKey = "CRA_CIF_" + custRequestId;
								if (CacheUtil.getInstance().notContains(cifCacheKey)) {
									CacheUtil.getInstance().put(cifCacheKey,
											EntityQuery.use(delegator).select("attrValue").from("CustRequestAttribute")
											.where("custRequestId", custRequestId, "attrName", "CIF_REFERENCE")
											.queryFirst());
								}

								GenericValue cifGv = (GenericValue) CacheUtil.getInstance().get(cifCacheKey);
								if (UtilValidate.isNotEmpty(cifGv)
										&& UtilValidate.isNotEmpty(cifGv.getString("attrValue"))) {
									data.put("cif", cifGv.getString("attrValue"));
								}
							}

							if (UtilValidate.isNotEmpty(serviceRequest.getString("customerRelatedType"))) {
								String relatedType = GlobalConstants.ROLE_TYPE_BY_EXTERNALID
										.get(serviceRequest.getString("customerRelatedType"));
								data.put("custType",
										org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator, relatedType));
							}

							data.put("product", EnumUtil.getEnumDescriptionByEnumId(delegator,
									serviceRequest.getString("accountType")));
							data.put("srType", org.fio.homeapps.util.DataUtil.getCustRequestTypeDesc(delegator,
									serviceRequest.getString("type")));
							data.put("srCategory", org.fio.homeapps.util.DataUtil.getCustRequestCategoryDesc(delegator,
									serviceRequest.getString("category")));
							data.put("srSubCategory", org.fio.homeapps.util.DataUtil
									.getCustRequestCategoryDesc(delegator, serviceRequest.getString("subCategory")));
							data.put("srPriority", EnumUtil.getEnumDescriptionByEnumId(delegator,
									serviceRequest.getString("priority")));
							data.put("customerName",
									PartyHelper.getPartyName(delegator, serviceRequest.getString("customerId"), false));
							data.put("partyType", org.ofbiz.party.party.PartyHelper
									.getFirstPartyRoleTypeId(serviceRequest.getString("customerId"), delegator));

							String primaryContactId = org.fio.homeapps.util.DataUtil.getSrPrimaryContact(delegator,
									custRequestId);
							if (UtilValidate.isNotEmpty(primaryContactId)) {
								data.put("primaryContactName",
										PartyHelper.getPartyName(delegator, primaryContactId, false));
							}
							// load dyna field [start]

							List<GenericValue> dynaList = delegator.findByAnd("CustRequestAttribute",
									UtilMisc.toMap("custRequestId", custRequestId), UtilMisc.toList("sequenceNumber"),
									false);
							if (UtilValidate.isNotEmpty(dynaList)) {
								int count = 1;
								for (GenericValue dyna : dynaList) {

									data.put("dynaField" + count, dyna.getString("attrName"));
									data.put("dynaValue" + count, dyna.getString("attrValue"));

									count++;
								}
							}
							data.put("externalKey", externalKey);
							// load dyna field [end]
							
							data.put("servicePortalName", servicePortalName);

							results.add(data);

						}
					}
				}

			} else {
				Debug.log("error==");
				Map<String, Object> data = new HashMap<String, Object>();
				if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					data.put("errorMessage", accessMatrixRes.get("errorMessage").toString());
				} else {
					data.put("errorMessage", "Access Denied");
				}
				results.add(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
			// return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String searchActivities(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		//List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalLoginKey = request.getParameter("externalLoginKey");

		// String partyId = (String) context.get("partyId");
		String owner = UtilValidate.isNotEmpty(context.get("owner")) ? (String) context.get("owner")
				: (String) context.get("partyId");

		String activityNo = (String) context.get("activityNo");
		String activityType = (String) context.get("activityType");

		String activitySubType = (String) context.get("activitySubType");
		String createdBy = (String) context.get("createdBy");
		String statusId = (String) context.get("statusId");
		String open = (String) context.get("open");
		String closed = (String) context.get("closed");
		String scheduled = (String) context.get("scheduled");
		String workEffortTypeIdNotIn = request.getParameter("workEffortTypeIdNotIn");
		String isChecklistActivity = request.getParameter("isChecklistActivity");

		Timestamp systemTime = UtilDateTime.nowTimestamp();

		String startDate = (String) context.get("startDate");
		String endDate = (String) context.get("endDate");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		
		ArrayList<String> statuses = new ArrayList<String>();
		List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);

			// if(UtilValidate.isNotEmpty(owner)) {
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String opLevel = "L1";
			String businessUnit = null;
			List<Map<String, Object>> buInfo = new ArrayList<Map<String, Object>>();
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Read");
				accessMatrixMap.put("entityName", "WorkEffort");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
			}

			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				List<EntityCondition> conditionList = FastList.newInstance();

				// check with ownerId
				if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					conditionList.add(EntityCondition.makeCondition("ownerId", EntityOperator.IN, ownerIds));
				}

				// check with emplTeamId
				if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
				}

				if (UtilValidate.isNotEmpty(domainEntityType)
						&& domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
					List conditionsList = FastList.newInstance();

					conditionsList.add(
							EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, domainEntityId));

					EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

					List<GenericValue> opportunityRoleList = delegator.findList("SalesOpportunityWorkEffort",
							mainConditons, UtilMisc.toSet("workEffortId"), null, null, false);
					if (UtilValidate.isNotEmpty(opportunityRoleList)) {
						List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(opportunityRoleList,
								"workEffortId", true);

						conditionList
						.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
					} else {
						conditionList.add(
								EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, "999888999888"));
					}
					owner = null;
				}

				if (UtilValidate.isNotEmpty(domainEntityType)
						&& CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {

					owner = null;

					List conditionsList = FastList.newInstance();

					conditionsList
					.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, domainEntityId));

					EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

					List<GenericValue> custRequestWorkEffortList = delegator.findList("CustRequestWorkEffort",
							mainConditons, UtilMisc.toSet("workEffortId"), null, null, false);
					if (UtilValidate.isNotEmpty(custRequestWorkEffortList)) {
						List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList,
								"workEffortId", true);

						conditionList
						.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
					} else {
						conditionList.add(
								EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, "999888999888"));
					}
				}

				if (UtilValidate.isNotEmpty(owner) && UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.CLIENT_SERVICE_REQUEST)) {
					conditionList = FastList.newInstance();

					List<GenericValue> custRequestWorkEffortList = EntityQuery.use(delegator).select("workEffortId").from("CustRequestWorkEffort").where("custRequestId", domainEntityId).distinct().queryList();
					if (UtilValidate.isNotEmpty(custRequestWorkEffortList)) {
						List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList, "workEffortId", true);
						conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
					}
					if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.CONTACT)) {
						conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, owner),
								EntityCondition.makeCondition("contactPartyId", EntityOperator.EQUALS, owner)
								)); 
					} else {
						conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, owner));
					}
				} else if (UtilValidate.isNotEmpty(owner)) {
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, owner));
				}


				if (UtilValidate.isNotEmpty(activityNo)) {
					conditionList.add(
							EntityCondition.makeCondition("workEffortId", EntityOperator.LIKE, "" + activityNo + "%"));
				}
				if (UtilValidate.isNotEmpty(activityType)) {
					conditionList.add(EntityCondition.makeCondition("workEffortServiceType", EntityOperator.LIKE,
							"" + activityType + "%"));
				}
				if (UtilValidate.isNotEmpty(activitySubType)) {
					conditionList.add(EntityCondition.makeCondition("workEffortSubServiceType", EntityOperator.EQUALS,
							activitySubType));
				}

				if (UtilValidate.isNotEmpty(createdBy)) {
					conditionList.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.LIKE,
							"" + createdBy + "%"));
				}
				if (UtilValidate.isNotEmpty(startDate)) {
					startDate = df1.format(df2.parse(startDate));
					EntityConditionList<EntityExpr> dateCondition = EntityCondition
							.makeCondition(
									UtilMisc.toList(
											EntityCondition.makeCondition("createdDate",
													EntityOperator.GREATER_THAN_EQUAL_TO,
													UtilDateTime.getDayStart(Timestamp.valueOf(startDate))),
											EntityCondition.makeCondition("lastModifiedDate",
													EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime
													.getDayStart(Timestamp.valueOf(startDate)))),
									EntityOperator.OR);
					conditionList.add(dateCondition);
				}

				if (UtilValidate.isNotEmpty(endDate)) {
					endDate = df1.format(df2.parse(endDate));
					EntityConditionList<EntityExpr> dateCondition = EntityCondition
							.makeCondition(
									UtilMisc.toList(
											EntityCondition.makeCondition("createdDate",
													EntityOperator.LESS_THAN_EQUAL_TO,
													UtilDateTime.getDayEnd(Timestamp.valueOf(endDate))),
											EntityCondition.makeCondition("lastModifiedDate",
													EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime
													.getDayEnd(Timestamp.valueOf(endDate)))),
									EntityOperator.OR);
					conditionList.add(dateCondition);
				}
				if (UtilValidate.isNotEmpty(open)) {
					statuses.add(CommonPortalConstants.activityOpenStatuses.IA_OPEN);
					statuses.add(CommonPortalConstants.activityOpenStatuses.IA_MIN_PROGRESS);
					statuses.add(CommonPortalConstants.activityOpenStatuses.IA_MSCHEDULED);
				}
				if (UtilValidate.isNotEmpty(closed)) {
					statuses.add(closed);
				}
				if (UtilValidate.isNotEmpty(scheduled)) {
					statuses.add(CommonPortalConstants.activityOpenStatuses.IA_MSCHEDULED);
				}
				
				if (UtilValidate.isNotEmpty(statusId)) {
					statuses.add(statusId);
				}
				if (UtilValidate.isNotEmpty(statuses)) {
					conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.IN, statuses));
				}

				if (UtilValidate.isNotEmpty(workEffortTypeIdNotIn)) {
					List<String> workEffortTypeIdsNotIn = Arrays.asList(workEffortTypeIdNotIn.split(","));
					conditionList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.NOT_IN, workEffortTypeIdsNotIn));
				}

				if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.REBATE)) {
					conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
					conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
				}
				
				EntityCondition condition = null;
				if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.CONTACT)) {
					List conditionsList = FastList.newInstance();

					conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, owner));
					conditionsList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
					conditionsList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

					List<GenericValue> workEffortRoleList = delegator.findList("WorkEffortContact", mainConditons,
							UtilMisc.toSet("workEffortId"), null, null, false);
					if (UtilValidate.isNotEmpty(workEffortRoleList)) {
						List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(workEffortRoleList,
								"workEffortId", true);
						conditionList.clear();
						conditionList
						.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
						if (UtilValidate.isNotEmpty(statuses)) {
							conditionList
							.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.IN, statuses));
						}
					}
				}
				
				//check and adding skip sms activity
				String hideSmsActivity = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_HIDE_SMS", "N");
				boolean hideSMSActivity = false;
				if(UtilValidate.isNotEmpty(hideSmsActivity) && "Y".equals(hideSmsActivity)) {
					hideSMSActivity = true;
				}
				if(hideSMSActivity) {
					conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.EQUALS, null),
							EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.NOT_IN, UtilMisc.toList("SMS"))
							));
				}

				
				EntityFindOptions efo = new EntityFindOptions();
				efo.setOffset(0);
				efo.setLimit(1000);

				Set<String> fieldsToSelect = new TreeSet<String>();
				fieldsToSelect.add("workEffortId");
				fieldsToSelect.add("workEffortServiceType");
				fieldsToSelect.add("workEffortSubServiceType");
				fieldsToSelect.add("workEffortPurposeTypeId");
				fieldsToSelect.add("phoneNumber");
				fieldsToSelect.add("workEffortName");
				fieldsToSelect.add("estimatedCompletionDate");
				fieldsToSelect.add("estimatedStartDate");
				fieldsToSelect.add("duration");
				fieldsToSelect.add("actualCompletionDate");
				fieldsToSelect.add("wfOnceDone");
				fieldsToSelect.add("sourceReferenceId");
				fieldsToSelect.add("description");
				fieldsToSelect.add("currentStatusId");
				fieldsToSelect.add("actualStartDate");
				fieldsToSelect.add("workEffortTypeId");
				fieldsToSelect.add("completedBy");
				fieldsToSelect.add("closedDateTime");
				fieldsToSelect.add("closedByUserLogin");
				fieldsToSelect.add("lastModifiedDate");
				fieldsToSelect.add("lastModifiedByUserLogin");
				fieldsToSelect.add("createdByUserLogin");

				fieldsToSelect.add("partyId");
				fieldsToSelect.add("createdDate");
				fieldsToSelect.add("wfNationalId");
				fieldsToSelect.add("scopeEnumId");
				fieldsToSelect.add("wfVplusId");
				fieldsToSelect.add("lastUpdatedStamp");

				fieldsToSelect.add("primOwnerId");
				fieldsToSelect.add("businessUnitName");
				fieldsToSelect.add("priority");
				fieldsToSelect.add("direction");
				fieldsToSelect.add("ownerPartyId");
				fieldsToSelect.add("domainEntityType");
				fieldsToSelect.add("domainEntityId");
				fieldsToSelect.add("emplTeamId");
				fieldsToSelect.add("ownerId");
				
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				
				dynamicView.addMemberEntity("WE", "WorkEffort");
				dynamicView.addAlias("WE", "workEffortId","workEffortId", null, Boolean.FALSE, Boolean.TRUE, null);
				dynamicView.addAlias("WE", "externalId");
				dynamicView.addAlias("WE", "workEffortName");
				dynamicView.addAlias("WE", "workEffortTypeId");
				dynamicView.addAlias("WE", "workEffortPurposeTypeId");
				dynamicView.addAlias("WE", "workEffortServiceType");
				dynamicView.addAlias("WE", "workEffortSubServiceType");
				dynamicView.addAlias("WE", "scopeEnumId");
				dynamicView.addAlias("WE", "currentStatusId");
				dynamicView.addAlias("WE", "currentSubStatusId");
				dynamicView.addAlias("WE", "workEffortName");
				dynamicView.addAlias("WE", "description");
				dynamicView.addAlias("WE", "phoneNumber");
				dynamicView.addAlias("WE", "wfNationalId");
				dynamicView.addAlias("WE", "wfVplusId");
				dynamicView.addAlias("WE", "estimatedStartDate");
				dynamicView.addAlias("WE", "estimatedCompletionDate");
				dynamicView.addAlias("WE", "actualStartDate");
				dynamicView.addAlias("WE", "actualCompletionDate");
				dynamicView.addAlias("WE", "duration");
				dynamicView.addAlias("WE", "lastModifiedByUserLogin");
				dynamicView.addAlias("WE", "channelId");
				dynamicView.addAlias("WE", "wfOnceDone");
				dynamicView.addAlias("WE", "lastUpdatedStamp");
				dynamicView.addAlias("WE", "closedByUserLogin");
				dynamicView.addAlias("WE", "closedDateTime");
				dynamicView.addAlias("WE", "primOwnerId");
				dynamicView.addAlias("WE", "createdDate");
				dynamicView.addAlias("WE", "createdByUserLogin");
				dynamicView.addAlias("WE", "lastModifiedDate");
				dynamicView.addAlias("WE", "sourceReferenceId");
				dynamicView.addAlias("WE", "cif");
				dynamicView.addAlias("WE", "businessUnitName");
				dynamicView.addAlias("WE", "priority");
				dynamicView.addAlias("WE", "direction");
				dynamicView.addAlias("WE", "ownerPartyId");
				dynamicView.addAlias("WE", "locationDesc");
				dynamicView.addAlias("WE", "completedBy");
				dynamicView.addAlias("WE", "domainEntityType");
				dynamicView.addAlias("WE", "domainEntityId");
				dynamicView.addAlias("WE", "createdStamp");
				dynamicView.addAlias("WE", "lastUpdatedStamp");
				
				dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
				dynamicView.addAlias("WEPA", "partyId");
				dynamicView.addAlias("WEPA", "roleTypeId");
				dynamicView.addAlias("WEPA", "fromDate");
				dynamicView.addAlias("WEPA", "thruDate");
				dynamicView.addAlias("WEPA", "statusId");
				dynamicView.addAlias("WEPA", "statusDateTime");
				dynamicView.addAlias("WEPA", "callOutCome");
				dynamicView.addAlias("WEPA", "assignedByUserLoginId");
				dynamicView.addAlias("WEPA", "partyAssignFacilityId","facilityId",null, false,false,null);
				dynamicView.addAlias("WEPA", "ownerId");
				dynamicView.addAlias("WEPA", "emplTeamId");
				dynamicView.addAlias("WEPA", "businessUnit");
				dynamicView.addViewLink("WE", "WEPA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
				
				if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.CONTACT)) {
					dynamicView.addMemberEntity("WEC", "WorkEffortContact");
					dynamicView.addAlias("WEC", "contactPartyId","partyId",null, false,false,null);
					dynamicView.addViewLink("WE", "WEC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
				}
				
				if(UtilValidate.isNotEmpty(isChecklistActivity)) {
					if (isChecklistActivity.equals("Y")) {
						conditionList.add(EntityCondition.makeCondition("channelId", EntityOperator.EQUALS, Channels.PROGRAM));
					} else {
						conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("channelId", EntityOperator.EQUALS, null),
								EntityCondition.makeCondition("channelId", EntityOperator.NOT_EQUAL, Channels.PROGRAM)
								));
					}
				}
				
				condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				Debug.log("searchActivities condition======" + condition);
				
				String isApprovalEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPROVAL_ENABLED");

				if (UtilValidate.isNotEmpty(condition)) {
					
					List<GenericValue> activityList = EntityQuery.use(delegator).select(fieldsToSelect).from(dynamicView).where(condition).orderBy("createdDate DESC").queryList();
					
					List<String> workTypeIds = EntityUtil.getFieldListFromEntityList(activityList,
							"workEffortPurposeTypeId", true);
					List<GenericValue> workTypeList = delegator.findList("WorkEffortPurposeType",
							EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.IN, workTypeIds),
							null, null, null, false);
					if (UtilValidate.isNotEmpty(activityList)) {
						String partyName = "";

						String activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
						Map<String, Object> activityTypes = ActivityDataHelper.getWorkEffortTypes(delegator, activityList);

						for (GenericValue activity : activityList) {

							Map<String, Object> data = new HashMap<String, Object>();

							String workEffortId = activity.getString("workEffortId");
							String typeDesc = EnumUtil.getEnumDescription(delegator, activity.getString("workEffortServiceType"), "IA_TYPE");
							String subTypeDesc = EnumUtil.getEnumDescription(delegator, activity.getString("workEffortSubServiceType"), "IA_TYPE");

							String actPartyId = activity.getString("partyId");
							String workEffortTypeId = activity.getString("workEffortTypeId");
							data.put("workEffortTypeId", activity.getString("workEffortTypeId"));
							data.put("partyId", actPartyId);
							if (UtilValidate.isNotEmpty(actPartyId)) {
								partyName = org.fio.homeapps.util.DataUtil.getUserLoginName(delegator, actPartyId);
							}
							data.put("partyName", partyName);

							List<GenericValue> workEffortPurposeType = EntityUtil.filterByCondition(workTypeList,
									EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.EQUALS,activity.getString("workEffortPurposeTypeId")));
							if (UtilValidate.isNotEmpty(workEffortPurposeType)) {
								data.put("workType", workEffortPurposeType.get(0).get("description"));
							}

							data.put("iaNumber", activity.getString("workEffortId"));
							data.put("partyId", actPartyId);
							data.put("priority", EnumUtil.getEnumDescription(delegator, activity.getString("priority"), "PRIORITY_LEVEL"));
							data.put("direction", EnumUtil.getEnumDescription(delegator, activity.getString("direction"), "PH_DIRECTIONCODE"));

							if (UtilValidate.isEmpty(typeDesc)) {
								typeDesc = (String) activityTypes.get(activity.getString("workEffortTypeId"));
							}
							data.put("activityType", typeDesc);

							data.put("activitySubType", subTypeDesc);
							data.put("businessUnit", UtilValidate.isNotEmpty(activity.getString("businessUnitName")) ? activity.getString("businessUnitName") : "");

							data.put("ownerName", UtilActivity.getActivityOwnerName(delegator, activityOwnerRole, workEffortId, true));
							data.put("contactName", org.fio.homeapps.util.UtilActivity.getActivityContactName(delegator, workEffortId));

							data.put("phone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(activity.getString("phoneNumber")));
							String status = Objects.toString(org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, activity.getString("currentStatusId")), "IA_STATUS_ID");
							data.put("status", status);
							data.put("subject", activity.getString("workEffortName"));
							data.put("accountNo", "");
							data.put("businessUnit", "");

							String dateDue = "";
							if (UtilValidate.isNotEmpty(activity.getString("estimatedCompletionDate"))) {
								dateDue = DataUtil.convertDateTimestamp(activity.getString("estimatedCompletionDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
							}
							data.put("plannedDueDate", dateDue);
							String plannedStartDate = "";
							if (UtilValidate.isNotEmpty(activity.getString("estimatedStartDate"))) {
								plannedStartDate = DataUtil.convertDateTimestamp(activity.getString("estimatedStartDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
							}
							data.put("plannedStartDate", plannedStartDate);
							String actualCompletion = "";
							if (UtilValidate.isNotEmpty(activity.getString("actualCompletionDate"))) {
								actualCompletion = DataUtil.convertDateTimestamp(activity.getString("actualCompletionDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
							}
							data.put("actualCompletion", actualCompletion);
							if(UtilValidate.isNotEmpty(activity.getString("wfOnceDone")) && "Y".equals(activity.getString("wfOnceDone"))){
								data.put("onceDone", activity.getString("wfOnceDone"));
							}else{
								data.put("onceDone", "");
							}
							data.put("productName", "");

							data.put("comments", activity.getString("description"));
							String actualStart = "";
							if (UtilValidate.isNotEmpty(activity.getString("actualStartDate"))) {
								actualStart = DataUtil.convertDateTimestamp(activity.getString("actualStartDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
							}
							data.put("actualStart", actualStart);
							if (UtilValidate.isNotEmpty(activity.getString("duration"))) {
								data.put("duration", activity.getString("duration") + "Min");
							}
							String overDue = "";
							if (UtilValidate.isNotEmpty(activity.getTimestamp("estimatedCompletionDate"))
									&& systemTime.after(activity.getTimestamp("estimatedCompletionDate"))) {
								overDue = "Y";
							}
							String plannedEndDate = "";
							if(UtilValidate.isNotEmpty(activity.getTimestamp("estimatedCompletionDate"))){
								plannedEndDate = DataUtil.convertDateTimestamp(activity.getString("estimatedCompletionDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
							}
							data.put("plannedEndDate", plannedEndDate);
							data.put("overDue", overDue);

							String closedDate = "";
							if (UtilValidate.isNotEmpty(activity.getString("closedDateTime"))) {
								closedDate = UtilDateTime.timeStampToString(activity.getTimestamp("closedDateTime"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
							}
							data.put("closedDate", closedDate);
							data.put("closedByName", org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, activity.getString("closedByUserLogin"), false));

							String createdDate = "";
							if (UtilValidate.isNotEmpty(activity.getString("createdDate"))) {
								createdDate = UtilDateTime.timeStampToString(activity.getTimestamp("createdDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
							}
							data.put("createdOn", createdDate);
							data.put("createdByName", org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, activity.getString("createdByUserLogin"), false));

							String modifiedDate = "";
							if (UtilValidate.isNotEmpty(activity.getString("lastModifiedDate"))) {
								modifiedDate = UtilDateTime.timeStampToString(activity.getTimestamp("lastModifiedDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
							}
							data.put("modifiedOn", modifiedDate);
							data.put("modifiedByName", org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, activity.getString("lastModifiedByUserLogin"), false));

							data.put("completedByName", org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, activity.getString("completedBy"), false));

							if (UtilValidate.isNotEmpty(workEffortId) && UtilValidate.isNotEmpty(domainEntityType)
									&& domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
								GenericValue salesOpportunityWorkEffort = EntityQuery.use(delegator)
										.select("salesOpportunityId")
										.from("SalesOpportunityWorkEffort")
										.where("workEffortId", workEffortId)
										.queryFirst();
								if (UtilValidate.isNotEmpty(salesOpportunityWorkEffort)) {
									String salesOpportunityId = salesOpportunityWorkEffort.getString("salesOpportunityId");
									if (UtilValidate.isNotEmpty(salesOpportunityId)) {
										data.put("salesOpportunityId", salesOpportunityId);
									}
								}
							}

							GenericValue workEffortAttr = EntityQuery.use(delegator).from("WorkEffortAttribute").where("workEffortId",workEffortId,"attrName","TECH_ARRIVAL_WINDOW").queryFirst();
							data.put("arrivalWindow", UtilValidate.isNotEmpty(workEffortAttr) && UtilValidate.isNotEmpty(workEffortAttr.getString("attrValue")) ? workEffortAttr.getString("attrValue")+"hr" : "" );

							GenericValue workEffortAttr1 = EntityQuery.use(delegator).from("WorkEffortAttribute").where("workEffortId",workEffortId,"attrName","IS_SCHEDULING_REQUIRED").queryFirst();
							data.put("isScheduled", UtilValidate.isNotEmpty(workEffortAttr1) && UtilValidate.isNotEmpty(workEffortAttr1.getString("attrValue")) ? "N".equals(workEffortAttr1.getString("attrValue")) ? "No" : "Y".equals(workEffortAttr1.getString("attrValue")) ? "Yes" :"" : "");

							GenericValue callRecordMasterGv = EntityQuery.use(delegator).from("CallRecordMaster").where("workEffortId",workEffortId,"workEffortTypeId", workEffortTypeId).queryFirst();
							String callDateTime = "";
							if (UtilValidate.isNotEmpty(callRecordMasterGv) && UtilValidate.isNotEmpty(callRecordMasterGv.getString("startDate"))) {
								callDateTime = DataUtil.convertDateTimestamp(callRecordMasterGv.getString("startDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
							}
							data.put("callDateTime", callDateTime);

							if(UtilValidate.isNotEmpty(activity.getString("duration"))){
								String durationUntiType = "hr";
								if (Float.parseFloat(activity.getString("duration")) > 1) {
									durationUntiType = "hrs";
								}
								data.put("wftMsdduration", activity.getString("duration")+" "+durationUntiType);
							}

							// approval data [start]
							if(UtilValidate.isEmpty(isApprovalEnabled) || isApprovalEnabled.equals("Y")){
								if (UtilValidate.isNotEmpty(activity.getString("workEffortTypeId")) && activity.getString("workEffortTypeId").equals("TASK")) {
									GenericValue approval = EntityQuery.use(delegator).from("WorkEffortApproval").where("workEffortId", activity.getString("workEffortId")).queryFirst();
									if (UtilValidate.isNotEmpty(approval)) {
										data.put("approvalCategoryId", approval.getString("approvalCategoryId"));
									}
								}
							}
							// approval data [end]

							data.put("sourceIdLink", org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom((String)activity.get("domainEntityId"), (String)activity.get("domainEntityType"), externalLoginKey));
							data.put("sourceComponent", org.groupfio.common.portal.util.DataHelper.convertToLabel((String)activity.get("domainEntityType")));

							data.put("domainEntityId", activity.getString("domainEntityId"));
							data.put("domainEntityType", activity.getString("domainEntityType"));
							data.put("domainEntityTypeDesc", DataHelper.convertToLabel( activity.getString("domainEntityType") ));
							data.put("externalLoginKey", externalLoginKey);

							dataList.add(data);
						}
					}
				}
			} else {
				String errorMessage = "";
				if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errorMessage = accessMatrixRes.get("errorMessage").toString();
				} else {
					errorMessage = "Access Denied";
				}
				result.put("list", new ArrayList<Map<String, Object>>());
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, errorMessage);
				return AjaxEvents.doJSONResponse(response, result);
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put("list",dataList);
		return doJSONResponse(response, result);
	}

	public static String getTeamMembers(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String draw = request.getParameter("draw");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String rmPartyId = request.getParameter("rmPartyId");
		String roleTypeId = request.getParameter("roleTypeId");
		String gridSearchText = request.getParameter("search[value]");
		Map<String, Object> returnMap = FastMap.newInstance();
		List<Object> partyRoleList = FastList.newInstance();
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();

			// construct role conditions
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			} else {
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SALES_REP"));
			}

			// construct search conditions
			if (UtilValidate.isNotEmpty(lastName)) {
				conditions.add(EntityCondition.makeCondition("lastName", EntityOperator.LIKE,
						EntityFunction.UPPER("" + lastName + "%")));
			}
			if (UtilValidate.isNotEmpty(firstName)) {
				conditions.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE,
						EntityFunction.UPPER("" + firstName + "%")));
			}

			if (UtilValidate.isNotEmpty(gridSearchText)) {
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("lastName", EntityOperator.LIKE,
								EntityFunction.UPPER("%" + gridSearchText + "%")),
						EntityCondition.makeCondition("firstName", EntityOperator.LIKE,
								EntityFunction.UPPER("%" + gridSearchText + "%"))));
			}

			if (UtilValidate.isNotEmpty(rmPartyId)) {
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, rmPartyId));
			}
			// remove disabled parties
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)));

			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
			int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0;
			efo.setOffset(startInx);
			efo.setLimit(endInx);

			long count = 0;
			EntityFindOptions efoNum = new EntityFindOptions();
			efoNum.setDistinct(true);
			efoNum.getDistinct();
			efoNum.setFetchSize(1000);

			count = delegator.findCountByCondition("PartyToSummaryByRole", mainConditons, null,
					UtilMisc.toSet("partyId"), efoNum);

			/*
			 * List < GenericValue > partyToSummartyByRoleCount =
			 * delegator.findList("PartyToSummaryByRole", mainConditons, null,
			 * null, null, false); int count =
			 * partyToSummartyByRoleCount.size();
			 */

			long recordsFiltered = count;
			long recordsTotal = count;
			List<GenericValue> partyToSummartyByRole = delegator.findList("PartyToSummaryByRole", mainConditons, null,
					null, null, false);
			if (partyToSummartyByRole != null && partyToSummartyByRole.size() > 0) {
				int id = 1;
				for (GenericValue roles : partyToSummartyByRole) {
					Map<String, Object> partyToSummartyByRoleMap = FastMap.newInstance();
					id = id + 1;
					partyToSummartyByRoleMap.put("id", id + "");
					partyToSummartyByRoleMap.put("name",
							roles.getString("firstName") + " " + roles.getString("lastName"));
					partyToSummartyByRoleMap.put("partyId", roles.getString("partyId"));
					partyRoleList.add(partyToSummartyByRoleMap);
				}
				returnMap.put("data", partyRoleList);
				returnMap.put("draw", draw);
				returnMap.put("recordsTotal", recordsTotal);
				returnMap.put("recordsFiltered", recordsFiltered);
			} else {
				returnMap.put("data", partyRoleList);
				returnMap.put("draw", draw);
				returnMap.put("recordsTotal", 0);
				returnMap.put("recordsFiltered", 0);
				return AjaxEvents.doJSONResponse(response, returnMap);
			}
		} catch (Exception e) {
			Debug.logError("Exception in Get Team Member" + e.getMessage(), MODULE);
		}
		return AjaxEvents.doJSONResponse(response, returnMap);
	}

	// update Note
	public static String updatePartyNote(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String noteId = request.getParameter("noteId");
		String text = request.getParameter("note");
		// a note may be for a Party or a Case
		String partyId = request.getParameter("partyId");
		String campaignListId = request.getParameter("campaignListId");
		Locale locale = UtilHttp.getLocale(request);
		String returnMsg = null;
		String subProduct = (String) request.getParameter("subProduct");
		String noteType = request.getParameter("noteType");
		String callBackDate = request.getParameter("callBackDate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastContactDate = sdf.format(new Date());
		try {
			if (UtilValidate.isNotEmpty(noteId)) {
				GenericValue note = EntityQuery.use(delegator).from("NoteData").where("noteId", noteId).queryOne();
				// update the note
				if (note != null && note.size() > 0) {
					note.setString("noteInfo", text);
					if (UtilValidate.isNotEmpty(noteType)) {
						note.put("noteType", noteType);
					}
					if (UtilValidate.isNotEmpty(callBackDate)) {
						/*
						 * String pattern = "dd-MM-yyyy"; SimpleDateFormat
						 * simpleDateFormat = new SimpleDateFormat(pattern);
						 * 
						 * Date callBkDate = null; try { callBkDate =
						 * simpleDateFormat.parse(callBackDate); } catch
						 * (ParseException e) { // TODO Auto-generated catch
						 * block e.printStackTrace(); } note.put("callBackDate",
						 * new Timestamp(callBkDate.getTime()));
						 */
						try {
							Date callBackDate1 = new SimpleDateFormat("dd-MM-yyyy").parse(callBackDate);
							callBackDate = sdf.format(callBackDate1);
							note.put("callBackDate", java.sql.Date.valueOf(callBackDate));
						} catch (ParseException pe) {
							Debug.log("====ParseException===" + pe.getMessage());
						}
					}

					if (UtilValidate.isNotEmpty(subProduct)) {
						note.put("subProduct", subProduct);
					}
					note.store();
					GenericValue partySupplData = delegator.findOne("PartySupplementalData",
							UtilMisc.toMap("partyId", partyId), false);
					if (UtilValidate.isNotEmpty(partySupplData)) {
						if (UtilValidate.isNotEmpty(callBackDate)) {
							partySupplData.set("lastCallBackDate", java.sql.Date.valueOf(callBackDate));
							partySupplData.put("lastContactDate", java.sql.Date.valueOf(lastContactDate));
							partySupplData.store();

						}

					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError("Exception in update customer note" + e.getMessage(), MODULE);
		}
		// returnMsg = UtilProperties.getMessage(crmResource,
		// "noteUpdatedSuccess", locale);
		returnMsg = "Note update success";
		request.setAttribute("_EVENT_MESSAGE_", returnMsg);
		return "success";

	}

	@SuppressWarnings("unchecked")
	public static String searchOrders(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String partyId = request.getParameter("orderPartyId");

		String soNumber = request.getParameter("soNumber");
		String poNumber = request.getParameter("poNumber");
		String location = request.getParameter("location");
		String orderDate = request.getParameter("orderDate");
		String isOrderCompleted = request.getParameter("isOrderCompleted");

		String searchText = request.getParameter("searchText");
		String pickerLocation = request.getParameter("pickerLocation");
		String orderByColumn = request.getParameter("orderByColumn");

		String externalLoginKey = request.getParameter("externalLoginKey");
		if(UtilValidate.isEmpty(partyId))
			partyId = request.getParameter("partyId");

		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			
			if (UtilValidate.isEmpty(location)) {
				location = pickerLocation;
			}
			
			if (UtilValidate.isEmpty(partyId) && UtilValidate.isEmpty(soNumber) && UtilValidate.isEmpty(poNumber) && UtilValidate.isEmpty(location) && UtilValidate.isEmpty(orderDate) && UtilValidate.isEmpty(searchText)) {
				result.put("data", dataList);
				return AjaxEvents.doJSONResponse(response, result);
			}

			List conditionList = FastList.newInstance();

			if (UtilValidate.isNotEmpty(partyId)) {
				conditionList.add(EntityCondition.makeCondition("billToPartyId", EntityOperator.EQUALS, partyId));
			}

			if (UtilValidate.isNotEmpty(soNumber)) {
				//conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, soNumber));
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, soNumber),
						EntityCondition.makeCondition("transactionNumber", EntityOperator.EQUALS, soNumber)
						));
			}

			if (UtilValidate.isNotEmpty(poNumber)) {
				conditionList.add(EntityCondition.makeCondition("purchaseOrder", EntityOperator.EQUALS, poNumber));
			}

			if (UtilValidate.isNotEmpty(searchText)) {
				conditionList.add(EntityCondition.makeCondition("transactionNumber", EntityOperator.LIKE, searchText + "%"));
			}

			if (UtilValidate.isNotEmpty(location)) {
				conditionList.add(EntityCondition.makeCondition("storeNumber", EntityOperator.EQUALS, location));
			}

			if (UtilValidate.isNotEmpty(orderDate)) {
				Timestamp od = UtilDateTime.stringToTimeStamp(orderDate, "MM/dd/yyyy", TimeZone.getDefault(), Locale.getDefault());
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND, 
						EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(od)),
						EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(od))
						));
			}
			if(UtilValidate.isNotEmpty(isOrderCompleted)){
				if("Y".equals(isOrderCompleted))
					conditionList.add(EntityCondition.makeCondition("orderStatus",EntityOperator.EQUALS, "ORDER_COMPLETED" ));
				else if("N".equals(isOrderCompleted))
					conditionList.add(EntityCondition.makeCondition("orderStatus",EntityOperator.NOT_EQUAL,"ORDER_COMPLETED"));
			}
			int searchLimit = 1000;
			if (UtilValidate.isNotEmpty(searchText)) {
				searchLimit = 20;
			}
			
			String orderBy = null;
            if (UtilValidate.isNotEmpty(orderByColumn)) {
            	orderBy = orderByColumn;
            }
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			Debug.log("searchOrders conditions: "+mainConditons);
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("orderId");
			fieldsToSelect.add("billToPartyId");
			fieldsToSelect.add("totalSalesAmount");
			fieldsToSelect.add("totalTaxAmount");
			fieldsToSelect.add("quantitySold");
			fieldsToSelect.add("orderStatus");
			fieldsToSelect.add("sequenceNumber");
			fieldsToSelect.add("subSequenceNumber");
			fieldsToSelect.add("skuDescription");
			fieldsToSelect.add("itemStatus");
			fieldsToSelect.add("purchaseOrder");
			fieldsToSelect.add("transactionNumber");
			fieldsToSelect.add("orderTypeId");
			fieldsToSelect.add("storeNumber");
			fieldsToSelect.add("unitCost");
			fieldsToSelect.add("orderDate");
			fieldsToSelect.add("shipDate");
			fieldsToSelect.add("estimatedDeliveryDate");
			fieldsToSelect.add("originalTransactionDate");
			fieldsToSelect.add("skuNumber");
			//fieldsToSelect.add("correspondingPoid");
			
			EntityQuery query = EntityQuery.use(delegator).select(fieldsToSelect).from("RmsTransactionMaster").where(mainConditons).fetchSize(searchLimit);
			if (UtilValidate.isNotEmpty(orderBy)) {
				query.orderBy(orderBy);
			}
			
			List<GenericValue> orderList = query.queryList();
			
			if (UtilValidate.isNotEmpty(orderList)) {

				Map<String, Object> storeNames = SrDataHelper.getProductStoreNames(delegator);
				Map<String, Object> orderTypes = SrDataHelper.getOrderTypes(delegator);

				Set orderIdsSet = new LinkedHashSet(EntityUtil.getFieldListFromEntityList(orderList, "orderId", false));
				List orderIds = new ArrayList(orderIdsSet);
				for (int i = 0; i < orderIds.size(); i++) {
					Map<String, Object> data = new HashMap<String, Object>();

					String orderId = (String) orderIds.get(i);
					long receiptCount = delegator.findCountByCondition("EreceiptTransaction", EntityCondition.makeCondition(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId)), null, UtilMisc.toSet("orderId"), null);
					data.put("containsReceipt", receiptCount > 0 ? "Y":"N");
					data.put("orderId", orderId);
					data.put("partyId", partyId);

					List<GenericValue> ordersRelatedList = EntityUtil.filterByCondition(orderList, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
					if (UtilValidate.isNotEmpty(ordersRelatedList)) {
						GenericValue orderGenericVal = ordersRelatedList.get(0);
						BigDecimal totalValue = orderGenericVal.getBigDecimal("totalSalesAmount");
						String totalSalesValue = orderGenericVal.getString("totalSalesAmount");
						BigDecimal totalTaxValue = orderGenericVal.getBigDecimal("totalTaxAmount");
						if (UtilValidate.isNotEmpty(totalSalesValue)) {
							data.put("orderTotal", totalSalesValue);
						}
						if (UtilValidate.isNotEmpty(totalTaxValue)) {
							data.put("totalTaxValue", Integer.valueOf(totalTaxValue.intValue()));
						}
						data.put("itemCount", ordersRelatedList.size());
						List<BigDecimal> quantitySoldList = EntityUtil.getFieldListFromEntityList(ordersRelatedList,
								"quantitySold", false);
						if (UtilValidate.isNotEmpty(quantitySoldList)) {
							BigDecimal qtySoldSum = quantitySoldList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
							data.put("quantitySold", qtySoldSum.doubleValue());
						}

						data.put("orderStatus", orderGenericVal.getString("orderStatus"));
						data.put("sequenceNumber", orderGenericVal.getString("sequenceNumber"));
						//data.put("subSequenceNumber", orderGenericVal.getString("subSequenceNumber"));
						data.put("subSequenceNumber", UtilValidate.isNotEmpty(orderGenericVal.getString("subSequenceNumber")) ? Integer.parseInt(orderGenericVal.getString("subSequenceNumber")) : "");
						data.put("skuDescription", orderGenericVal.getString("skuDescription"));
						data.put("productId", orderGenericVal.getString("skuNumber"));
						data.put("itemStatus", orderGenericVal.getString("itemStatus"));
						data.put("purchaseOrder", orderGenericVal.getString("purchaseOrder"));
						data.put("externalId", UtilValidate.isNotEmpty(orderGenericVal.getString("transactionNumber")) ? orderGenericVal.getString("transactionNumber") : orderId );
						data.put("orderType", orderTypes.get(orderGenericVal.getString("orderTypeId")));
						//data.put("customerPo", orderGenericVal.getString("correspondingPoid"));
						
						data.put("storeNumber", orderGenericVal.getString("storeNumber"));
						data.put("storeName", storeNames.get(orderGenericVal.getString("storeNumber")));

						BigDecimal extendedCost = new BigDecimal(0);
						BigDecimal unitCost = orderGenericVal.getBigDecimal("unitCost");
						BigDecimal actualQty = orderGenericVal.getBigDecimal("quantitySold");
						if(UtilValidate.isEmpty(actualQty))
							actualQty = BigDecimal.ZERO;

						if (UtilValidate.isNotEmpty(unitCost)) {
							unitCost = unitCost.setScale(2, BigDecimal.ROUND_HALF_EVEN);
							extendedCost = BigDecimal.valueOf(unitCost.doubleValue() * actualQty.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_EVEN);
						}

						data.put("actualQty", actualQty.doubleValue());
						data.put("unitCost", unitCost);
						data.put("extendedCost", extendedCost);

						data.put("orderDate",
								UtilValidate.isNotEmpty(orderGenericVal.get("orderDate"))
								? UtilDateTime.timeStampToString(orderGenericVal.getTimestamp("orderDate"),
										globalDateFormat, TimeZone.getDefault(), null) : "");
						data.put("shipDate",
								UtilValidate.isNotEmpty(orderGenericVal.get("shipDate"))
								? UtilDateTime.timeStampToString(orderGenericVal.getTimestamp("shipDate"),
										globalDateFormat, TimeZone.getDefault(), null) : "");
						data.put("estimatedDeliveryDate",
								UtilValidate.isNotEmpty(orderGenericVal.get("estimatedDeliveryDate")) ? UtilDateTime
										.timeStampToString(orderGenericVal.getTimestamp("estimatedDeliveryDate"),
												globalDateTimeFormat, TimeZone.getDefault(), null) : "");
						data.put("orderCompleteDate",
								UtilValidate.isNotEmpty(orderGenericVal.get("originalTransactionDate")) ? UtilDateTime
										.timeStampToString(orderGenericVal.getTimestamp("originalTransactionDate"),
												globalDateTimeFormat, TimeZone.getDefault(), null) : "");

						data.put("externalLoginKey", externalLoginKey);
						data.put("seqNo", i+"");
					}

					dataList.add(data);
				}

			}

			result.put("data", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String searchMainOrders(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String partyId = request.getParameter("orderPartyId");
		String orderDate = request.getParameter("orderDate");
		String salesOpportunityId = request.getParameter("salesOpportunityId");
		
		String searchText = request.getParameter("searchText");
		String externalLoginKey = request.getParameter("externalLoginKey");
		String orderByColumn = request.getParameter("orderByColumn");
		
		if(UtilValidate.isEmpty(partyId)) {
			partyId = request.getParameter("partyId");
		}
		
		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			String isEnableIUCInt = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IUC_INT_ENABLED");
			String iucUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IUC_URL");
			
			if (UtilValidate.isEmpty(partyId) && UtilValidate.isEmpty(orderDate) && UtilValidate.isEmpty(searchText)) {
				result.put("data", dataList);
				return AjaxEvents.doJSONResponse(response, result);
			}

			List conditionList = FastList.newInstance();

			if (UtilValidate.isNotEmpty(partyId)) {
				conditionList.add(EntityCondition.makeCondition("billToPartyId", EntityOperator.EQUALS, partyId));
			}
			
			if (UtilValidate.isNotEmpty(searchText)) {
				conditionList.add(EntityCondition.makeCondition("orderName", EntityOperator.LIKE, searchText + "%"));
			}
			
			if (UtilValidate.isNotEmpty(orderDate)) {
				Timestamp od = UtilDateTime.stringToTimeStamp(orderDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND, 
						EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(od)),
						EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(od))
						));
			}
			
			if(UtilValidate.isNotEmpty(salesOpportunityId)) {
				conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "OPPO_ID"));
				conditionList.add(EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, salesOpportunityId));
			}
			
			int searchLimit = 1000;
			if (UtilValidate.isNotEmpty(searchText)) {
				searchLimit = 20;
			}
			
			String orderBy = null;
            if (UtilValidate.isNotEmpty(orderByColumn)) {
            	orderBy = orderByColumn;
            }
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			Debug.log("searchOrders conditions: "+mainConditons);
			
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("OH", "OrderHeader");
			dynamicView.addAlias("OH", "orderId", null, null, null, true, null);
			
			dynamicView.addAlias("OH", "externalId");
			dynamicView.addAlias("OH", "orderDate");
			dynamicView.addAlias("OH", "orderName");
			dynamicView.addAlias("OH", "productStoreId");
			dynamicView.addAlias("OH", "statusId");
			dynamicView.addAlias("OH", "billToPartyId");
			dynamicView.addAlias("OH", "currencyUom");
			dynamicView.addAlias("OH", "grandTotal");
			dynamicView.addAlias("OH", "salesChannelEnumId");
			dynamicView.addAlias("OH", "entryDate");
			dynamicView.addAlias("OH", "createdBy");
			dynamicView.addAlias("OH", "billFromPartyId");
			
			dynamicView.addAlias("OH", "createdStamp");
			dynamicView.addAlias("OH", "lastUpdatedStamp");
			
			dynamicView.addMemberEntity("OI", "OrderItem");
			dynamicView.addAlias("OI", "correspondingPoId");
			dynamicView.addAlias("OI", "productId");
			dynamicView.addAlias("OI", "orderItemSeqId");
			
			dynamicView.addViewLink("OH", "OI", Boolean.TRUE, ModelKeyMap.makeKeyMapList("orderId"));
			
			if(UtilValidate.isNotEmpty(salesOpportunityId)) {
				dynamicView.addMemberEntity("OA", "OrderAttribute");
				dynamicView.addAlias("OA", "attrName");
				dynamicView.addAlias("OA", "attrValue");
				dynamicView.addViewLink("OH", "OA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("orderId"));
			}
			
			EntityQuery query = EntityQuery.use(delegator).from(dynamicView).where(mainConditons).fetchSize(searchLimit);
			if (UtilValidate.isNotEmpty(orderBy)) {
				query.orderBy(orderBy);
			}
			
			List<GenericValue> orderList = query.queryList();
			if (UtilValidate.isNotEmpty(orderList)) {

				Map<String, Object> storeNames = SrDataHelper.getProductStoreNames(delegator);
				Map<String, Object> orderTypes = OrderDataHelper.getMainOrderTypes(delegator);
				
				Map<String, Object> partyNames = new HashMap<>();
				PartyHelper.getPartyNameByPartyIds(delegator, partyNames, orderList, "billToPartyId");
				
				for (GenericValue order : orderList) {
					Map<String, Object> data = new HashMap<String, Object>();
					
					data.put("orderDate",
							UtilValidate.isNotEmpty(order.get("orderDate"))
							? UtilDateTime.timeStampToString(order.getTimestamp("orderDate"),
									globalDateFormat, TimeZone.getDefault(), null) : "");
					data.put("orderName", order.getString("orderName"));
					data.put("storeNumber", order.getString("productStoreId"));
					data.put("storeName", storeNames.get(order.getString("productStoreId")));
					data.put("orderStatus", StatusUtil.getStatusDescription(delegator, order.getString("statusId")));
					data.put("billToPartyId", order.getString("billToPartyId"));
					data.put("custName", partyNames.get(order.getString("billToPartyId")));
					
					data.put("currencyUom", order.getString("currencyUom"));
					data.put("customerPo", order.getString("correspondingPoId"));
					
					String orderIdAndName = "";
					if (UtilValidate.isNotEmpty(order.getString("orderName"))){
						orderIdAndName = order.getString("orderName")+"("+order.getString("orderId")+")";
                	} else{
                		orderIdAndName = order.getString("orderId");
                	}
					data.put("orderIdAndName", orderIdAndName);
					
					BigDecimal totalCost = order.getBigDecimal("grandTotal");
					if (UtilValidate.isNotEmpty(totalCost)) {
						totalCost = totalCost.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					}
					data.put("totalCost", totalCost);
					
					Timestamp shipByDate = UtilOrder.getEarliestShipByDate(delegator, order.getString("orderId"));
					data.put("shipBeforeDate",
							UtilValidate.isNotEmpty(shipByDate)
							? UtilDateTime.timeStampToString(shipByDate,
									globalDateFormat, TimeZone.getDefault(), null) : "");
					
					String orderDetailUrl = "/common-portal/control/redirectOrders?orderId="+order.getString("orderId")+"&partyId="+order.getString("billToPartyId")+"&externalLoginKey="+externalLoginKey;
					if (UtilValidate.isNotEmpty(isEnableIUCInt) && isEnableIUCInt.equals("Y")) {
						orderDetailUrl = iucUrl + "sales/control/orderview?orderId="+order.getString("orderId");
					}
					data.put("orderDetailUrl", orderDetailUrl);
					
					data.put("externalLoginKey", externalLoginKey);
					
					dataList.add(data);
				}

			}

			result.put("data", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	@SuppressWarnings("unchecked")
	public static String searchSegmentations(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String start = request.getParameter("start");
		String length = request.getParameter("length");

		String partyId = request.getParameter("partyId");

		String groupingCode = request.getParameter("groupingCode");
		String groupId = request.getParameter("groupId");
		String groupType = request.getParameter("groupType");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		
		String filterGroupCode = request.getParameter("filterGroupCode");
		
		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {

			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

			List conditionList = FastList.newInstance();
			
			List<String> notIncludeGroupList = new ArrayList<String>();
			if (UtilValidate.isNotEmpty(filterGroupCode) && filterGroupCode.equalsIgnoreCase("Y")) {
				filterGroupCode = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator,"SEGMENTATION_FILTER_GROUPS");
				if (UtilValidate.isNotEmpty(filterGroupCode)) {
					notIncludeGroupList = Arrays.asList(filterGroupCode.split(","));
				}
				
			}
			
			if (UtilValidate.isNotEmpty(domainEntityType)
					&& CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			} else {
				conditionList
				.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
				conditionList.add(
						EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
			}

			if (UtilValidate.isNotEmpty(groupingCode)) {
				conditionList.add(EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, groupingCode));
			}else if (UtilValidate.isNotEmpty(notIncludeGroupList)) {
				conditionList.add(EntityCondition.makeCondition("groupingCode", EntityOperator.NOT_IN, notIncludeGroupList));
			}
			if (UtilValidate.isNotEmpty(groupId)) {
				conditionList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
			}

			if (UtilValidate.isNotEmpty(groupType)) {
				conditionList.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
			}

			List<String> orderBy = UtilMisc.toList("inceptionDate DESC");
			String entityName = "PartyClassificationSummary";

			if (UtilValidate.isNotEmpty(domainEntityType)
					&& !CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
				entityName = "SegmentationSummary";
			}

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> entryList = delegator.findList(entityName, mainConditons, null, orderBy, null, false);
			if (UtilValidate.isNotEmpty(entryList)) {
				long rowId = 1000;
				for (GenericValue entry : entryList) {
					Map<String, Object> data = new HashMap<String, Object>();

					data.putAll(entry);

					data.put("domainEntityId", domainEntityId);
					data.put("domainEntityType", domainEntityType);

					if (UtilValidate.isNotEmpty(entry.getString("groupingCode"))) {
						GenericValue group = EntityUtil.getFirst(delegator.findByAnd("CustomFieldGroupingCode",
								UtilMisc.toMap("customFieldGroupingCodeId", entry.getString("groupingCode")), null,
								false));
						if (UtilValidate.isNotEmpty(group)) {
							data.put("groupingCodeName", group.getString("groupingCode"));
						}
					}

					GenericValue value = EntityUtil.getFirst(delegator.findByAnd("CustomField",
							UtilMisc.toMap("customFieldId", entry.getString("customFieldId")), null, false));
					if (UtilValidate.isNotEmpty(value)) {
						data.put("customFieldName", value.getString("customFieldName"));
						data.put("groupName", value.getString("groupName"));
					}

					data.put("inceptionDate",
							UtilValidate.isNotEmpty(entry.get("inceptionDate"))
							? UtilDateTime.timeStampToString(entry.getTimestamp("inceptionDate"),
									globalDateTimeFormat, TimeZone.getDefault(), null)
									: "");

					data.put("rowId", (rowId++));

					dataList.add(data);
				}

			}

			result.put("data", dataList);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);

	}

	public static String searchEconomicMetrics(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String start = request.getParameter("start");
		String length = request.getParameter("length");

		String partyId = request.getParameter("partyId");

		String groupingCode = request.getParameter("groupingCode");
		String groupId = request.getParameter("groupId");
		String groupType = request.getParameter("groupType");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");

		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {

			List conditionList = FastList.newInstance();

			if (UtilValidate.isNotEmpty(domainEntityType)
					&& CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			} else {
				conditionList
				.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
				conditionList.add(
						EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
			}

			if (UtilValidate.isNotEmpty(groupingCode)) {
				conditionList.add(EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, groupingCode));
			}

			if (UtilValidate.isNotEmpty(groupId)) {
				conditionList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
			}

			if (UtilValidate.isNotEmpty(groupType)) {
				conditionList.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
			}

			List<String> orderBy = UtilMisc.toList("sequenceNumber ASC");
			String entityName = "PartyMetricSummary";

			if (UtilValidate.isNotEmpty(domainEntityType)
					&& !CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
				entityName = "EconomicMetricSummary";
			}

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> entryList = delegator.findList(entityName, mainConditons, null, orderBy, null, false);
			if (UtilValidate.isNotEmpty(entryList)) {

				for (GenericValue entry : entryList) {
					Map<String, Object> data = new HashMap<String, Object>();

					data.putAll(entry);

					data.put("domainEntityId", domainEntityId);
					data.put("domainEntityType", domainEntityType);

					if (UtilValidate.isNotEmpty(entry.getString("groupingCode"))) {
						GenericValue group = EntityUtil.getFirst(delegator.findByAnd("CustomFieldGroupingCode",
								UtilMisc.toMap("customFieldGroupingCodeId", entry.getString("groupingCode")), null,
								false));
						if (UtilValidate.isNotEmpty(group)) {
							data.put("groupingCodeName", group.getString("groupingCode"));
						}
					}

					GenericValue value = EntityUtil.getFirst(delegator.findByAnd("CustomField",
							UtilMisc.toMap("customFieldId", entry.getString("customFieldId")), null, false));
					if (UtilValidate.isNotEmpty(value)) {
						data.put("customFieldName", value.getString("customFieldName"));
						data.put("groupName", value.getString("groupName"));
					}

					dataList.add(data);
				}

			}

			result.put("data", dataList);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);

	}

	public static String getProductCatalogList(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();

		List<GenericValue> catalogList = null;
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			catalogList = EntityQuery.use(delegator).from("ProdCatalog").queryList();
			if (UtilValidate.isNotEmpty(catalogList)) {
				for (GenericValue catalog : catalogList) {
					Map<String, Object> data = new HashMap<String, Object>();

					data.put("prodCatalogId", catalog.getString("prodCatalogId"));
					data.put("catalogName", catalog.getString("catalogName"));

					dataList.add(data);
				}
			} 

			result.put("dataList", dataList);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (GenericEntityException e) {
			result.put(ModelService.ERROR_MESSAGE, ModelService.RESPOND_ERROR);
			return doJSONResponse(response, result);
		}

		return doJSONResponse(response, result);
	}

	public static String getProductCategoryList(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String prodCatalogId = (String) context.get("prodCatalogId");

		Map<String, Object> result = FastMap.newInstance();

		try {
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();

			if (UtilValidate.isNotEmpty(prodCatalogId)) {
				List<Map<String, Object>> dataList = DataUtil.getProductCategoryList(delegator, prodCatalogId);
				result.put("dataList", dataList);
			}
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getProductSubCategoryList(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String prodCatalogId = (String) context.get("prodCatalogId");
		String productCategoryId = (String) context.get("productCategoryId");
		Map<String, Object> result = FastMap.newInstance();

		try {
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();

			if (UtilValidate.isNotEmpty(prodCatalogId) || UtilValidate.isNotEmpty(productCategoryId)) {
				List<Map<String, Object>> dataList = DataUtil.getProductSubCategoryList(delegator, prodCatalogId, productCategoryId);
				result.put("dataList", dataList);
			}
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getProductList(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String prodCatalogId = (String) context.get("prodCatalogId");
		String productCategoryId = (String) context.get("productCategoryId");

		Map<String, Object> result = FastMap.newInstance();

		try {

			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();

			if (UtilValidate.isNotEmpty(prodCatalogId) || UtilValidate.isNotEmpty(productCategoryId)) {

				List<String> productCategoryIds = new ArrayList<String>();
				List<String> productIds = new ArrayList<String>();

				List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
				List<EntityCondition> conditionlist = FastList.newInstance();

				if (UtilValidate.isEmpty(productCategoryId)) {
					conditionlist
					.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, prodCatalogId));
					conditionlist.add(EntityUtil.getFilterByDateExpr());
					EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
					List<GenericValue> productCatalogList = EntityQuery.use(delegator)
							.select("prodCatalogId", "productCategoryId").from("ProdCatalogCategory").where(condition)
							.orderBy("sequenceNum").queryList();

					if (UtilValidate.isNotEmpty(productCatalogList)) {
						productCategoryIds = EntityUtil.getFieldListFromEntityList(productCatalogList,
								"productCategoryId", true);
					}
				}

				if (UtilValidate.isNotEmpty(productCategoryId)) {
					productCategoryIds.add(productCategoryId);
				}

				if (UtilValidate.isNotEmpty(productCategoryIds)) {
					for (String categoryId : productCategoryIds) {
						conditionlist.clear();
						conditionlist.add(
								EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryId));
						conditionlist.add(EntityUtil.getFilterByDateExpr());

						EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
						List<GenericValue> productCategoryMemberList = EntityQuery.use(delegator)
								.select("productId", "productCategoryId").from("ProductCategoryMember").where(condition)
								.orderBy("sequenceNum").queryList();
						if (UtilValidate.isNotEmpty(productCategoryMemberList)) {
							productIds.addAll(EntityUtil.getFieldListFromEntityList(productCategoryMemberList,
									"productId", true));
						}
					}
				}

				if (UtilValidate.isNotEmpty(productIds)) {
					for (String productId : productIds) {
						Map<String, Object> data = new HashMap<String, Object>();

						GenericValue productCategory = EntityQuery.use(delegator).from("ProductContent")
								.where("productId", productId).queryFirst();
						if (UtilValidate.isNotEmpty(productCategory)) {
							GenericValue content = EntityQuery.use(delegator).from("Content")
									.where("contentId", productCategory.getString("contentId")).queryFirst();
							if (UtilValidate.isNotEmpty(content)) {
								GenericValue contentResource = EntityQuery.use(delegator).from("ElectronicText")
										.where("dataResourceId", content.getString("dataResourceId")).queryFirst();
								if (UtilValidate.isNotEmpty(contentResource)) {
									data.put("productId", productId);
									data.put("productName", contentResource.get("textData"));
									dataList.add(data);
								}
							}

						}

					}
				}

				result.put("dataList", dataList);
			}

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);
	}

	@SuppressWarnings("unchecked")
	public static String getDataSourceDetails(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		String dataSourceId = request.getParameter("dataSourceId");
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("dataSourceId", dataSourceId);
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getDataSourceDetails", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			} else {
				String errMsg = "Problem While Fetching Owner Related Users/Team Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String searchPartys(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String start = request.getParameter("start");
		String length = request.getParameter("length");

		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String partyId = request.getParameter("partyId");
		String roleTypeId = request.getParameter("roleTypeId");
		String partyLevel = request.getParameter("partyLevel");
		String supplementalPartyTypeId = request.getParameter("supplementalPartyTypeId");
		String localName = request.getParameter("localName");
		String externalId = request.getParameter("externalId");

		String searchType = request.getParameter("searchType");
		String domainEntityType = request.getParameter("domainEntityType");
		String searchText = request.getParameter("searchText");
		String generalState =  request.getParameter("generalState");
		String generalCity =  request.getParameter("generalCity");

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {
			String applicationType = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_TYPE");
			List conditionList = FastList.newInstance();

			if (UtilValidate.isNotEmpty(roleTypeId) && (roleTypeId.equals("ACCOUNT") || roleTypeId.equals("LEAD") || roleTypeId.equals("CUSTOMER"))) {
				EntityCondition roleTypeCondition = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,
						roleTypeId);
				conditionList.add(roleTypeCondition);
			} else {

				if (UtilValidate.isNotEmpty(domainEntityType) && (domainEntityType.equals(DomainEntityType.OPPORTUNITY)
						|| CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType))) {

					EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT"),
							EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "LEAD"),
									EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,
											"LEAD_QUALIFIED")));
					if (UtilValidate.isEmpty(applicationType) || applicationType.equals("B2C") || applicationType.equals("BOTH")) {
						roleTypeCondition = EntityCondition.makeCondition(EntityOperator.OR,
								roleTypeCondition,
								EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER"));
					}
					conditionList.add(roleTypeCondition);
				} else {
					EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT"),
							EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "LEAD"));
					if (UtilValidate.isEmpty(applicationType) || applicationType.equals("B2C") || applicationType.equals("BOTH")) {
						roleTypeCondition = EntityCondition.makeCondition(EntityOperator.OR,
								roleTypeCondition,
								EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER"));
					}
					conditionList.add(roleTypeCondition);
				}
			}

			EntityCondition partyStatusCondition = EntityCondition
					.makeCondition(
							UtilMisc.toList(
									EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,
											"PARTY_DISABLED"),
									EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)),
							EntityOperator.OR);
			conditionList.add(partyStatusCondition);

			// conditionList.add(EntityUtil.getFilterByDateExpr());

			if (UtilValidate.isNotEmpty(domainEntityType) && (domainEntityType.equals(DomainEntityType.OPPORTUNITY)
					|| CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType))) {
				if (UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equals("LEAD")) {
					conditionList
					.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "LEAD_QUALIFIED"));
				}
			}

			if (UtilValidate.isNotEmpty(partyLevel)) {
				conditionList.add(EntityCondition.makeCondition("partyRoleTypeId", EntityOperator.EQUALS, partyLevel));
			}

			if (UtilValidate.isNotEmpty(localName)) {
				conditionList.add(
						EntityCondition.makeCondition("groupNameLocal", EntityOperator.LIKE, "%" + localName + "%"));
			}

			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.LIKE, "%" +partyId + "%");
				conditionList.add(partyCondition);
			}
			
			if (UtilValidate.isNotEmpty(externalId)) {
				conditionList.add(EntityCondition.makeCondition("externalId", EntityOperator.LIKE, "%" + externalId + "%"));
			}

			if (UtilValidate.isNotEmpty(name)) {
				EntityCondition nameCondition = EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + name + "%");
				if (UtilValidate.isNotEmpty(roleTypeId) && (roleTypeId.equals("ALL") || roleTypeId.equals("CUSTOMER"))) {
					nameCondition = EntityCondition.makeCondition(EntityOperator.OR,
							nameCondition,
							EntityCondition.makeCondition("firstName", EntityOperator.LIKE, "%" + name + "%"),
							EntityCondition.makeCondition("middleName", EntityOperator.LIKE, "%" + name + "%"),
							EntityCondition.makeCondition("lastName", EntityOperator.LIKE, "%" + name + "%")
							);
				}
				conditionList.add(nameCondition);
			}

			if (UtilValidate.isNotEmpty(searchText)) {
				EntityCondition nameCondition = EntityCondition
						.makeCondition(
								UtilMisc.toList(
										EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + searchText + "%"),
										EntityCondition.makeCondition("groupNameLocal", EntityOperator.LIKE, "%" + searchText + "%")
										),
								EntityOperator.OR);
				if (UtilValidate.isNotEmpty(roleTypeId) && (roleTypeId.equals("ALL") || roleTypeId.equals("CUSTOMER"))) {
					List<String> names = Arrays.asList(searchText.split(" "));
					String firstName = names.get(0);
					String middleName = names.size() >= 3 ? names.get(1) : null;
					String lastName = null;
					if (names.size()==2) {
						//middleName = names.get(1);	
						lastName = names.get(1);
					} else if (names.size()>=3) {
						lastName = StringUtil.join(names.subList(2, names.size()-1), " ");
					}
					
					List conditions = FastList.newInstance();
					if (UtilValidate.isNotEmpty(firstName)) {
						conditions.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, firstName + "%"));
					}
					if (UtilValidate.isNotEmpty(middleName)) {
						conditions.add(EntityCondition.makeCondition("middleName", EntityOperator.LIKE, middleName + "%"));
					}
					if (UtilValidate.isNotEmpty(lastName)) {
						conditions.add(EntityCondition.makeCondition("lastName", EntityOperator.LIKE, lastName + "%"));
					}
					
					nameCondition = EntityCondition.makeCondition(EntityOperator.OR,
							nameCondition,
							EntityCondition.makeCondition(conditions, EntityOperator.AND)
							);
				}
				conditionList.add(nameCondition);
			}

			List<EntityCondition> eventExprs = new LinkedList<EntityCondition>();
			if (UtilValidate.isNotEmpty(email) || UtilValidate.isNotEmpty(phone)) {

				if (UtilValidate.isNotEmpty(email)) {
					eventExprs.add(EntityCondition.makeCondition("primaryEmail", EntityOperator.LIKE, "%" + email + "%"));
				}

				if (UtilValidate.isNotEmpty(phone)) {
					eventExprs.add(EntityCondition.makeCondition("primaryContactNumber", EntityOperator.LIKE, "%" + phone + "%"));
				}

				conditionList.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
			}

			if (UtilValidate.isNotEmpty(searchType) && searchType.equals("my-active-account")) {
				EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
						// EntityCondition.makeCondition("partyIdFrom",
						// EntityOperator.EQUALS, partyId),
						EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS,
								userLogin.getString("partyId")),
						EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,
								"RESPONSIBLE_FOR"),
						EntityUtil.getFilterByDateExpr()), EntityOperator.AND);

				conditionList.add(conditionPR);
			}

			if (UtilValidate.isNotEmpty(supplementalPartyTypeId)) {
				conditionList.add(EntityCondition.makeCondition("supplementalPartyTypeId", EntityOperator.EQUALS,
						supplementalPartyTypeId));
			}

			if(UtilValidate.isNotEmpty(generalState)) {
				conditionList.add(EntityCondition.makeCondition("primaryStateProvinceGeoId", EntityOperator.EQUALS, generalState));
			}
			if(UtilValidate.isNotEmpty(generalCity)) {
				conditionList.add(EntityCondition.makeCondition("primaryCity", EntityOperator.EQUALS, generalCity));
			}
			
			int resultLimit = 1000;
			if (UtilValidate.isNotEmpty(searchText)) {
				resultLimit = 20;
			}
			
			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
					EntityFindOptions.CONCUR_READ_ONLY, false);
			efo.setOffset(0);
			efo.setLimit(resultLimit);

			String viewName = "AccountSummaryView";
			if (UtilValidate.isNotEmpty(partyLevel)) {
				viewName = "AccountSummaryView2";
			}

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			Debug.log("searchPartys mainConditons: "+mainConditons);
			List<GenericValue> partySummaryList = delegator.findList(viewName, mainConditons, null,
					UtilMisc.toList("-partyId"), efo, false);

			if (UtilValidate.isNotEmpty(partySummaryList)) {
				
				Map<String, Object> partyNames = new HashMap<>();
				PartyHelper.getPartyNameByPartyIds(delegator, partyNames, partySummaryList, "partyId");

				for (GenericValue partySummary : partySummaryList) {

					Map<String, Object> data = new HashMap<String, Object>();

					partyId = partySummary.getString("partyId");
					roleTypeId = partySummary.getString("roleTypeId");
					externalId = partySummary.getString("externalId");

					String groupName = partySummary.getString("groupName");
					String statusId = partySummary.getString("statusId");
					String statusItemDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId, "PARTY_STATUS");

					String phoneNumber = partySummary.getString("primaryContactNumber");
					String infoString = partySummary.getString("primaryEmail");
					String city = partySummary.getString("primaryCity");
					String state = partySummary.getString("primaryStateProvinceGeoId");

					String country = partySummary.getString("primaryCountryGeoId");
					String postalCode = partySummary.getString("primaryPostalCode");
					String address1 = partySummary.getString("primaryAddress1");
					String address2 = partySummary.getString("primaryAddress2");

					if (UtilValidate.isNotEmpty(state)) {
						state = org.fio.admin.portal.util.DataUtil.getGeoName(delegator, state, "STATE/PROVINCE");
					}
					if (UtilValidate.isNotEmpty(country)) {
						country = org.fio.homeapps.util.DataUtil.getGeoName(delegator, country, "COUNTRY");
					}
					
					String partyExternalId = org.fio.homeapps.util.DataUtil.getPartyIdentificationPartyId(delegator, partyId, "ALT_DEAL_CUST_ID");
					//partyExternalId = UtilValidate.isNotEmpty(partyExternalId) ? partyExternalId : partyId;
					
					data.put("partyId", partyId);
					data.put("externalId", partyExternalId);
					data.put("roleTypeId", roleTypeId);
					if (UtilValidate.isNotEmpty(groupName)) {
						groupName = groupName.replaceAll("'", "`");
					}
					data.put("externalId", externalId);
					data.put("groupName", groupName);
					String partyName = (String) partyNames.get(partyId);
					if (UtilValidate.isNotEmpty(partyName)) {
						partyName = partyName.replaceAll("'", "`").replaceAll("\"", "``");
					}
					data.put("partyName", partyName);
					data.put("statusDescription", statusItemDesc);
					data.put("contactNumber", DataHelper.preparePhoneNumber(delegator, phoneNumber));
					data.put("infoString", infoString);
					data.put("city", city);
					data.put("state", state);
					data.put("country", country);
					data.put("postalCode", postalCode);
					data.put("address1", address1);
					data.put("address2", address2);

					data.put("localName", partySummary.getString("groupNameLocal"));

					dataList.add(data);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, dataList);

	}

	public static String searchContacts(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<GenericValue> resultList = null;
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");

		String partyId = request.getParameter("partyId");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String middleName = request.getParameter("middleName");
		String emailAddress = request.getParameter("emailAddress");
		String contactNumber = request.getParameter("contactNumber");

		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();

			// construct role conditions
			EntityCondition roleTypeCondition = EntityCondition
					.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"));
			conditions.add(roleTypeCondition);

			conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN,
					UtilMisc.toList("ACCOUNT", "LEAD")));
			conditions.add(
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"));

			EntityCondition partyStatusCondition = EntityCondition
					.makeCondition(
							UtilMisc.toList(
									EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,
											"PARTY_DISABLED"),
									EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)),
							EntityOperator.OR);
			conditions.add(partyStatusCondition);

			if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
				List conditionsList = FastList.newInstance();

				conditionsList.add(
						EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, domainEntityId));

				EntityCondition roleCondition = EntityCondition
						.makeCondition(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));

				conditionsList.add(roleCondition);
				conditionsList.add(EntityUtil.getFilterByDateExpr());

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

				List<GenericValue> opportunityRoleList = delegator.findList("SalesOpportunityRole", mainConditons,
						UtilMisc.toSet("partyId"), null, null, false);
				if (UtilValidate.isNotEmpty(opportunityRoleList)) {
					List<String> contactIds = EntityUtil.getFieldListFromEntityList(opportunityRoleList, "partyId",
							true);

					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, contactIds));
				} else {
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "999888999888"));
				}
			}

			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.LIKE,"%" +partyId + "%");
				conditions.add(partyCondition);
			}

			if (UtilValidate.isNotEmpty(firstName)) {
				EntityCondition firstNameCondition = EntityCondition.makeCondition("firstName", EntityOperator.LIKE,
						"%" + firstName + "%");
				conditions.add(firstNameCondition);
			}
			if (UtilValidate.isNotEmpty(lastName)) {
				EntityCondition lastNameCondition = EntityCondition.makeCondition("lastName", EntityOperator.LIKE,
						"%" + lastName + "%");
				conditions.add(lastNameCondition);
			}
			if (UtilValidate.isNotEmpty(middleName)) {
				EntityCondition middleNameCondition = EntityCondition.makeCondition("middleName", EntityOperator.LIKE,
						"%" + middleName + "%");
				conditions.add(middleNameCondition);
			}

			List<EntityCondition> eventExprs = new LinkedList<EntityCondition>();
			if (UtilValidate.isNotEmpty(emailAddress) || UtilValidate.isNotEmpty(contactNumber)) {

				if (UtilValidate.isNotEmpty(emailAddress)) {
					eventExprs.add(EntityCondition.makeCondition("primaryEmail", EntityOperator.LIKE,"%" + emailAddress + "%"));
				}

				if (UtilValidate.isNotEmpty(contactNumber)) {
					eventExprs.add(EntityCondition.makeCondition("primaryContactNumber", EntityOperator.LIKE,"%" +contactNumber + "%"));
				}

				conditions.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
			}

			// Login Based contact Filter
			String userLoginId = userLogin.getString("partyId");
			if (UtilValidate.isNotEmpty(userLoginId) && LoginFilterUtil.checkEmployeePosition(delegator, userLoginId)) {

				Map<String, Object> dataSecurityMetaInfo = (Map<String, Object>) session
						.getAttribute("dataSecurityMetaInfo");
				if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {

					List<String> lowerPositionPartyIds = (List<String>) dataSecurityMetaInfo
							.get("lowerPositionPartyIds");
					if (UtilValidate.isNotEmpty(lowerPositionPartyIds)) {

						List<EntityCondition> accountConditions = new ArrayList<EntityCondition>();
						EntityCondition accountRoleTypeCondition = EntityCondition
								.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN,
										UtilMisc.toList("ACCOUNT", "LEAD")));
						accountConditions.add(accountRoleTypeCondition);

						EntityCondition accountPartyStatusCondition = EntityCondition.makeCondition(
								UtilMisc.toList(
										EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,
												"PARTY_DISABLED"),
										EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)),
								EntityOperator.OR);

						accountConditions.add(accountPartyStatusCondition);
						accountConditions.add(EntityUtil.getFilterByDateExpr());

						EntityCondition securityConditions = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, lowerPositionPartyIds),
								EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,
										"RESPONSIBLE_FOR"),
								EntityUtil.getFilterByDateExpr());

						if (UtilValidate.isNotEmpty(userLogin)) {
							securityConditions = EntityCondition
									.makeCondition(UtilMisc.toList(
											EntityCondition.makeCondition("uploadedByUserLoginId",
													EntityOperator.EQUALS, userLogin.getString("userLoginId")),
											securityConditions), EntityOperator.OR);
						}

						accountConditions.add(securityConditions);

						EntityCondition mainConditons = EntityCondition.makeCondition(accountConditions,
								EntityOperator.AND);

						EntityFindOptions efo = new EntityFindOptions();
						efo.setDistinct(true);
						efo.getDistinct();

						Debug.logInfo("count 1 start: " + UtilDateTime.nowTimestamp(), MODULE);
						List<GenericValue> accounts = delegator.findList("PartyCommonView", mainConditons,
								UtilMisc.toSet("partyId"), UtilMisc.toList("partyId" + " " + "ASC"), efo, false);
						Debug.logInfo("count 2 start: " + UtilDateTime.nowTimestamp(), MODULE);

						List<String> accountPartyIds = EntityUtil.getFieldListFromEntityList(accounts, "partyId", true);

						EntityCondition partyIdToCondition = EntityCondition.makeCondition(UtilMisc.toList(
								EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, accountPartyIds),
								EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, accountPartyIds)),
								EntityOperator.OR);
						conditions.add(partyIdToCondition);
					}

					Debug.log("lowerPositionPartyIds> " + lowerPositionPartyIds);

				}
			}

			conditions.add(EntityUtil.getFilterByDateExpr());
			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
					EntityFindOptions.CONCUR_READ_ONLY, false);
			efo.setOffset(0);
			efo.setLimit(100);

			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);

			Debug.logInfo("mainConditons: " + mainConditons, MODULE);

			Debug.logInfo("list 1 start: " + UtilDateTime.nowTimestamp(), MODULE);

			Set<String> fieldsToSelect = new LinkedHashSet<String>();

			fieldsToSelect.add("partyId");
			fieldsToSelect.add("callBackDate");
			fieldsToSelect.add("pgPartyId");
			fieldsToSelect.add("statusId");
			fieldsToSelect.add("groupName");
			fieldsToSelect.add("personalTitle");
			fieldsToSelect.add("firstName");
			fieldsToSelect.add("lastName");
			fieldsToSelect.add("personalTitle");
			fieldsToSelect.add("primaryCity");
			fieldsToSelect.add("primaryPostalCode");
			fieldsToSelect.add("primaryCountryGeoId");
			fieldsToSelect.add("primaryStateProvinceGeoId");
			fieldsToSelect.add("primaryContactNumber");
			fieldsToSelect.add("primaryEmail");
			fieldsToSelect.add("primaryAddress1");
			fieldsToSelect.add("primaryAddress2");
			fieldsToSelect.add("primaryEmail");
			fieldsToSelect.add("departmentName");
			fieldsToSelect.add("designation");
			fieldsToSelect.add("birthDate");

			GenericValue systemProperty = EntityQuery.use(delegator).select("systemPropertyValue")
					.from("SystemProperty")
					.where("systemResourceId", "general", "systemPropertyId", "fio.grid.fetch.limit").queryFirst();
			// set the page parameters
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}

			int fioGridFetch = UtilValidate.isNotEmpty(systemProperty)
					&& UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue"))
					? Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;

					int viewSize = fioGridFetch;
					try {
						viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
					} catch (Exception e) {
						viewSize = fioGridFetch;
					}

					int highIndex = 0;
					int lowIndex = 0;
					// get the indexes for the partial list
					lowIndex = viewIndex * viewSize + 1;
					highIndex = (viewIndex + 1) * viewSize;

					// set distinct on so we only get one row per
					// using list iterator
					EntityListIterator pli = EntityQuery.use(delegator).select(fieldsToSelect).from("PartySummaryView")
							.where(mainConditons).orderBy("-partyId").cursorScrollInsensitive().fetchSize(highIndex).distinct()
							.cache(true).queryIterator();
					// get the partial list for this page
					resultList = pli.getPartialList(lowIndex, viewSize);

					// close the list iterator
					pli.close();

					// List < GenericValue > partySummaryList =
					// delegator.findList("PartySummaryView", mainConditons, null, null,
					// efo, false);
					Debug.logInfo("list 2 end: " + UtilDateTime.nowTimestamp(), MODULE);
					if (UtilValidate.isNotEmpty(resultList)) {
						for (GenericValue partySummary : resultList) {

							String contactId = partySummary.getString("partyId");

							Map<String, Object> data = new HashMap<String, Object>();
							String callBackDate = partySummary.getString("callBackDate");
							String companyName = partySummary.getString("groupName");
							String companyId = UtilValidate.isNotEmpty(partySummary.getString("pgPartyId"))
									? partySummary.getString("pgPartyId") : "";
									String statusId = partySummary.getString("statusId");
									String generalProfTitle = partySummary.getString("personalTitle");
									String statusItemDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId,
											"PARTY_STATUS");

									String name = partySummary.getString("firstName");
									if (UtilValidate.isNotEmpty(partySummary.getString("lastName"))) {
										if (UtilValidate.isNotEmpty(name)) {
											name = name + " " + partySummary.getString("lastName");
										} else {
											name = partySummary.getString("lastName");
										}
									}

									String phoneNumber = partySummary.getString("primaryContactNumber");
									String infoString = partySummary.getString("primaryEmail");
									String city = partySummary.getString("primaryCity");
									String state = partySummary.getString("primaryStateProvinceGeoId");

									String country = partySummary.getString("primaryCountryGeoId");
									String postalCode = partySummary.getString("primaryPostalCode");
									String address1 = partySummary.getString("primaryAddress1");
									String address2 = partySummary.getString("primaryAddress2");

									if (UtilValidate.isNotEmpty(state)) {
										state = org.fio.homeapps.util.DataUtil.getGeoName(delegator, state);
									}
									if (UtilValidate.isNotEmpty(country)) {
										country = org.fio.homeapps.util.DataUtil.getGeoName(delegator, country, "COUNTRY");
									}

									String departmentName = partySummary.getString("departmentName");
									String designation = EnumUtil.getEnumDescription(delegator, partySummary.getString("designation"),
											"DBS_LD_DESIGNATION");
									String birthDate = partySummary.getString("birthDate");

									data.put("partyId", contactId);
									data.put("name", name);
									data.put("generalProfTitle", generalProfTitle);
									data.put("callBackDate", callBackDate);
									data.put("statusDescription", statusItemDesc);
									data.put("contactNumber", phoneNumber);
									data.put("infoString", infoString);
									data.put("city", city);
									data.put("state", state);
									data.put("groupName", companyName);
									data.put("partyIdTo", companyId);

									data.put("departmentName", departmentName);
									data.put("designation", designation);
									data.put("country", country);
									data.put("postalCode", postalCode);
									data.put("address1", address1);
									data.put("address2", address2);
									data.put("birthDate", birthDate);

									dataList.add(data);
						}
					}

		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("errorMessage", e.getMessage());
			data.put("errorResult", new ArrayList<Map<String, Object>>());
			dataList.add(data);
		}
		return AjaxEvents.doJSONResponse(response, dataList);
	}

	public static String searchOpportunitys(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String start = request.getParameter("start");
		String length = request.getParameter("length");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String salesEmailAddress = (String) context.get("salesEmailAddress");
		String opportunityName = (String) context.get("opportunityName");
		String salesPhone = (String) context.get("salesPhone");
		String statusOpen = (String) context.get("statusOpen");
		String statusClosed = (String) context.get("statusClosed");
		String statusWon = (String) context.get("statusWon");
		String statusNew = (String) context.get("statusNew");
		String statusLost = (String) context.get("statusLost");
		String statusProgress = (String) context.get("statusProgress");
		String statusContact = (String) context.get("statusContact");
		String statusNotContact = (String) context.get("statusNotContact");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String responseTypeId = (String) context.get("responseTypeId");
		String callOutCome = (String) context.get("callOutCome");
		String salesChannelId = (String) context.get("salesChannelId");
		String responseType = (String) context.get("responseTypeId");
		String externalKey = (String) context.get("externalLoginKey");

		String partyId = request.getParameter("partyId");
		String roleTypeId = request.getParameter("roleTypeId");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");

		String estimatedClosedDays = (String) context.get("estimatedClosedDays");
		
		String salesOpportunityTypeId = (String) context.get("salesOpportunityTypeId");
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String dataSourceDesc = "";
		String partyroleTypeId = "";
		String salesOpppartyId = "";
		Map<String, Object> result = new HashMap<String, Object>();
		List<GenericValue> resultList = null;
		long start1 = System.currentTimeMillis();
		try {

			List conditionList = FastList.newInstance();
			List<String> statusIdsList = FastList.newInstance();
			List<String> oppoStatusList = FastList.newInstance();

			if (UtilValidate.isNotEmpty(salesOpportunityId)) {
				EntityCondition tempIdCondition = EntityCondition.makeCondition("salesOpportunityId",
						EntityOperator.LIKE, salesOpportunityId);
				conditionList.add(tempIdCondition);
			}
			/*
			if (UtilValidate.isNotEmpty(salesEmailAddress)) {
				EntityCondition tempNameCondition = EntityCondition.makeCondition("salesEmailAddress",
						EntityOperator.LIKE, salesEmailAddress);
				conditionList.add(tempNameCondition);
			} */
			if (UtilValidate.isNotEmpty(opportunityName)) {
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("opportunityName",
						EntityOperator.LIKE, opportunityName);
				conditionList.add(tempTypeCondition);
			}
			if (UtilValidate.isNotEmpty(marketingCampaignId)) {
				conditionList.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.EQUALS,
						marketingCampaignId));
			}
			if (UtilValidate.isNotEmpty(responseType)) {
				conditionList.add(EntityCondition.makeCondition("responseType", EntityOperator.EQUALS, responseType));
			}
			if (UtilValidate.isNotEmpty(callOutCome)) {
				conditionList.add(EntityCondition.makeCondition("callOutCome", EntityOperator.EQUALS, callOutCome));
			}
			/*
			if (UtilValidate.isNotEmpty(salesChannelId)) {
				conditionList
						.add(EntityCondition.makeCondition("salesChannelId", EntityOperator.EQUALS, salesChannelId));
			}

			if (UtilValidate.isNotEmpty(salesPhone)) {
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("salesPhone", EntityOperator.LIKE,
						salesPhone);
				conditionList.add(tempTypeCondition);
			}
			 */

			if (UtilValidate.isNotEmpty(domainEntityType)
					&& domainEntityType.equals(DomainEntityType.RELATED_OPPORTUNITY)) {

				// conditionList.add(EntityCondition.makeCondition("assocSalesOpportunityId",
				// EntityOperator.EQUALS, salesOpportunityId));
				// conditionList.add(EntityCondition.makeCondition("assocSalesOpportunityId",
				// EntityOperator.EQUALS, salesOpportunityId));

				List conditionsList = FastList.newInstance();

				conditionsList.add(
						EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, domainEntityId));
				conditionsList.add(EntityCondition.makeCondition("salesOpportunityAssocTypeId", EntityOperator.EQUALS,
						"OPPO_RELATED"));

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

				List<GenericValue> opportunityAssocList = delegator.findList("SalesOpportunityAssoc", mainConditons,
						UtilMisc.toSet("salesOpportunityIdTo"), null, null, false);
				if (UtilValidate.isNotEmpty(opportunityAssocList)) {
					List<String> assocOppIds = EntityUtil.getFieldListFromEntityList(opportunityAssocList,
							"salesOpportunityIdTo", true);

					conditionList
					.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.IN, assocOppIds));
				} else {
					conditionList.add(
							EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, "999888999888"));
				}
			}

			if (UtilValidate.isNotEmpty(domainEntityType)
					&& domainEntityType.equals(DomainEntityType.ADD_RELATED_OPPORTUNITY)) {

				List<String> assocOppIds = new ArrayList<String>();

				assocOppIds.add(domainEntityId);

				List conditionsList = FastList.newInstance();

				conditionsList.add(
						EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, domainEntityId));
				conditionsList.add(EntityCondition.makeCondition("salesOpportunityAssocTypeId", EntityOperator.EQUALS,
						"OPPO_RELATED"));

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

				List<GenericValue> opportunityAssocList = delegator.findList("SalesOpportunityAssoc", mainConditons,
						UtilMisc.toSet("salesOpportunityIdTo"), null, null, false);
				if (UtilValidate.isNotEmpty(opportunityAssocList)) {
					assocOppIds.addAll(
							EntityUtil.getFieldListFromEntityList(opportunityAssocList, "salesOpportunityIdTo", true));
				}

				conditionList
				.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.NOT_IN, assocOppIds));
			}

			if (UtilValidate.isNotEmpty(partyId)) {
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			}
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			}

			if (UtilValidate.isNotEmpty(statusOpen)) {
				oppoStatusList.add(statusOpen);
			}
			if (UtilValidate.isNotEmpty(statusClosed)) {
				oppoStatusList.add(statusClosed);
			}
			if (UtilValidate.isNotEmpty(statusWon)) {
				statusIdsList.add(statusWon);
			}
			if (UtilValidate.isNotEmpty(statusNew)) {
				statusIdsList.add(statusNew);
			}
			if (UtilValidate.isNotEmpty(statusLost)) {
				statusIdsList.add(statusLost);
			}
			if (UtilValidate.isNotEmpty(statusProgress)) {
				statusIdsList.add(statusProgress);
			}
			if (UtilValidate.isNotEmpty(statusContact)) {
				statusIdsList.add(statusContact);
			}
			if (UtilValidate.isNotEmpty(statusNotContact)) {
				statusIdsList.add(statusNotContact);
			}
			if (UtilValidate.isNotEmpty(statusIdsList)) {
				conditionList.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.IN, statusIdsList));
			}
			if (UtilValidate.isNotEmpty(oppoStatusList)) {
				conditionList.add(EntityCondition.makeCondition("opportunityStatusId", EntityOperator.IN, oppoStatusList));
			}

			if (UtilValidate.isNotEmpty(estimatedClosedDays)) {
				Timestamp estimatedClosedDate = UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), Integer.parseInt(estimatedClosedDays));
				conditionList.add(EntityCondition.makeCondition("estimatedCloseDate", EntityOperator.EQUALS, UtilDateTime.getDayStart(estimatedClosedDate)));
			}
			
			if (UtilValidate.isNotEmpty(salesOpportunityTypeId)) {
				conditionList.add(EntityCondition.makeCondition("salesOpportunityTypeId", EntityOperator.EQUALS,salesOpportunityTypeId));
			}
			
			
			GenericValue systemProperty = EntityQuery.use(delegator)
					.select("systemPropertyValue")
					.from("SystemProperty")
					.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
					.queryFirst();

			// set the page parameters
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}
			result.put("viewIndex", Integer.valueOf(viewIndex));

			int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;

			int viewSize = fioGridFetch;
			try {
				viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
			} catch (Exception e) {
				viewSize = fioGridFetch;
			}
			result.put("viewSize", Integer.valueOf(viewSize));

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

			Debug.logInfo("mainConditons : "+mainConditons, MODULE);
			//List<GenericValue> salesOpportunityList = delegator.findList("SalesOpportunitySummary", mainConditons, null, UtilMisc.toList("-createdStamp"), efo, false);
			int highIndex = 0;
			int lowIndex = 0;
			int resultListSize = 0;
			// get the indexes for the partial list
			try {
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				dynamicView.addMemberEntity("SO", "SalesOpportunity");
				dynamicView.addAliasAll("SO", null, null);
				dynamicView.addAlias("SO", "salesOpportunityId");
				dynamicView.addAlias("SO", "createdTxStamp");
				dynamicView.addAlias("SO", "lastUpdatedTxStamp");
				dynamicView.addAlias("SO", "soPartyId", "partyId", "", null, null, null);

				dynamicView.addMemberEntity("SOR", "SalesOpportunityRole");
				dynamicView.addAlias("SOR", "roleTypeId");
				dynamicView.addAlias("SOR", "sorPartyId", "partyId", "", false, false, null);
				dynamicView.addViewLink("SO", "SOR", Boolean.FALSE, ModelKeyMap.makeKeyMapList("salesOpportunityId"));

				lowIndex = viewIndex * viewSize + 1;
				highIndex = (viewIndex + 1) * viewSize;

				// set distinct on so we only get one row per 
				// using list iterator
				EntityListIterator pli = EntityQuery.use(delegator)
						.from(dynamicView)
						.where(mainConditons)
						.orderBy("-createdTxStamp")
						.cursorScrollInsensitive()
						.fetchSize(highIndex)
						.distinct()
						.cache(true)
						.queryIterator();
				// get the partial list for this page
				resultList = pli.getPartialList(lowIndex, viewSize);

				resultListSize = pli.getResultsSizeAfterPartialList();
				pli.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (UtilValidate.isNotEmpty(resultList)) {
				String isCampaignEnabled = org.groupfio.common.portal.util.UtilCommon.isComponentEnabled(delegator, org.fio.homeapps.constants.GlobalConstants.MODULE_COMP_ID.get(org.fio.homeapps.constants.GlobalConstants.ModuleName.CAMPAIGN));
				
				List<GenericValue> statusList = EntityQuery.use(delegator).from("StatusItem").where(EntityCondition.makeCondition("statusTypeId", "OPPORTUNITY_STATUS")).cache(true).queryList(); 
				Map < String, Object > statusMap = org.fio.homeapps.util.DataUtil.getMapFromGeneric(statusList, "statusId", "description", false);
				
				Map<String, Object> campaignNameMap = null;
				Map<String, Object> campaignChannelMap = null;
				Map<String, Object> campaignStatusMap = null;
				Map<String, Object> campaignTypeMap = null;
				if (UtilValidate.isNotEmpty(isCampaignEnabled) && isCampaignEnabled.equals("Y")) {
					List<String> marketingCampaignIds = EntityUtil.getFieldListFromEntityList(resultList, "marketingCampaignId", true);
					List<GenericValue> marketingCampaignList = EntityQuery.use(delegator).from("MarketingCampaign").where(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.IN, marketingCampaignIds)).cache(true).queryList();
					campaignNameMap = org.fio.homeapps.util.DataUtil.getMapFromGeneric(marketingCampaignList, "marketingCampaignId", "campaignName", false);
					campaignChannelMap = org.fio.homeapps.util.DataUtil.getMapFromGeneric(marketingCampaignList, "marketingCampaignId", "campaignTypeId", false);
					campaignStatusMap = org.fio.homeapps.util.DataUtil.getMapFromGeneric(marketingCampaignList, "marketingCampaignId", "statusId", false);
					List<GenericValue> campaignTypeList = EntityQuery.use(delegator).from("CampaignType").cache(true).queryList();
					campaignTypeMap = org.fio.homeapps.util.DataUtil.getMapFromGeneric(campaignTypeList, "campaignTypeId", "description", false);
				}

				List<String> salesOppourtunityIds = EntityUtil.getFieldListFromEntityList(resultList, "salesOpportunityId", true);
				EntityCondition oppoRoleCondition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("salesOpportunityId", EntityOperator.IN,salesOppourtunityIds),
						EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"CONTACT"));
				List<GenericValue> salesOppPrimConList = EntityQuery.use(delegator).from("SalesOpportunityRole").where(oppoRoleCondition).filterByDate().distinct().cache(true).queryList();
				Map<String, Object> saleOppoPrimConMap = org.fio.homeapps.util.DataUtil.getMapFromGeneric(salesOppPrimConList, "salesOpportunityId", "partyId", false);

				List<GenericValue> dataSourceList = EntityQuery.use(delegator).from("DataSource").cache(true).queryList();
				Map<String, Object> dataSourceMap = org.fio.homeapps.util.DataUtil.getMapFromGeneric(dataSourceList, "dataSourceId", "description", false);
				
				//List<String> prodIds = EntityUtil.getFieldListFromEntityList(resultList, "product", true);
				List<GenericValue> prodList = EntityQuery.use(delegator).from("Product").cache(true).queryList();
				Map<String, Object> productMap = org.fio.homeapps.util.DataUtil.getMapFromGeneric(prodList, "productId", "productName", false);

				//List<String> prodCategoryIds = EntityUtil.getFieldListFromEntityList(resultList, "productCategoryId", true);
				List<GenericValue> prodCategoryList = EntityQuery.use(delegator).from("ProductCategory").cache(true).queryList();
				Map<String, Object> productCategoryMap =  org.fio.homeapps.util.DataUtil.getMapFromGeneric(prodCategoryList, "productCategoryId", "categoryName", false);

				List<GenericValue> opportunityStageList = EntityQuery.use(delegator).from("SalesOpportunityStage").queryList();
				Map<String, Object> opportunityStageMap = org.fio.homeapps.util.DataUtil.getMapFromGeneric(opportunityStageList, "opportunityStageId", "description", false);

				List<GenericValue> opportunityTypesList = EntityQuery.use(delegator).from("Enumeration").where(EntityCondition.makeCondition("enumTypeId", "OPPO_CATEGORIES")).queryList(); 
				
				Map<String, Object> opportunityTypesMap = org.fio.homeapps.util.DataUtil.getMapFromGeneric(opportunityTypesList, "enumId", "description", false);
				
				Map<String, Object> partyNames = new HashMap<>();
				PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, resultList, "ownerId");
				PartyHelper.getPartyNameByPartyIds(delegator, partyNames, resultList, "sorPartyId");
				Map<String, Object> responsiblePartyMap = PartyHelper.getResponsibleParty(delegator, resultList, "partyId", "");

				long start2 = System.currentTimeMillis();
				for (GenericValue salesOpportunity : resultList) {
					String salesOppoId = salesOpportunity.getString("salesOpportunityId");

					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(salesOpportunity));

					data.put("oppoStageDesc", opportunityStageMap.get(salesOpportunity.getString("opportunityStageId")));
					//data.put("estimatedAmount", UtilValidate.isNotEmpty(salesOpportunity.getString("estimatedAmount"))? salesOpportunity.getString("estimatedAmount"): "");
					String estimatedAmount = org.groupfio.common.portal.util.DataHelper.getFormattedNumValue(delegator, salesOpportunity.getString("estimatedAmount"));
					data.put("oppoStatus",statusMap.get(salesOpportunity.getString("opportunityStatusId")));
					data.put("estimatedAmount",estimatedAmount);

					data.put("ownerName", partyNames.get(salesOpportunity.getString("ownerId")));

					data.put("estimatedCloseDate", UtilValidate.isNotEmpty(salesOpportunity.getString("estimatedCloseDate"))?UtilDateTime.timeStampToString(salesOpportunity.getTimestamp("estimatedCloseDate"), "MM-dd-yyyy", TimeZone.getDefault(), null) : "");
					data.put("description", UtilValidate.isNotEmpty(salesOpportunity.getString("remarks"))? salesOpportunity.getString("remarks"): "");

					data.put("nextCallDate", UtilValidate.isNotEmpty(salesOpportunity.getString("callBackDate"))?UtilDateTime.timeStampToString(UtilDateTime.stringToTimeStamp(salesOpportunity.getString("callBackDate"), "yyyy-MM-dd", TimeZone.getDefault(), null ), "MM-dd-yyyy", TimeZone.getDefault(), null): "");

					data.put("partyName", partyNames.get(salesOpportunity.getString("sorPartyId")));
					data.put("primaryContact", UtilValidate.isNotEmpty(saleOppoPrimConMap) && UtilValidate.isNotEmpty(saleOppoPrimConMap.get(salesOppoId)) ? org.fio.homeapps.util.DataUtil.getPartyName(delegator, (String)saleOppoPrimConMap.get(salesOppoId)) : "");

					data.put("productId", salesOpportunity.getString("product"));
					data.put("productName", UtilValidate.isNotEmpty(salesOpportunity.getString("product")) ? productMap.get(salesOpportunity.getString("product")) : "");

					data.put("productCategoryId", salesOpportunity.getString("productCategoryId"));
					data.put("productCategoryIdDesc", UtilValidate.isNotEmpty(salesOpportunity.getString("productCategoryId")) ? productCategoryMap.get(salesOpportunity.getString("productCategoryId")) :"");

					data.put("productSubCategoryId", salesOpportunity.getString("productSubCategoryId"));
					data.put("productSubCategoryIdDesc", UtilValidate.isNotEmpty(salesOpportunity.getString("productSubCategoryId")) ? productCategoryMap.get(salesOpportunity.getString("productSubCategoryId")) :"");

					if (UtilValidate.isNotEmpty(isCampaignEnabled) && isCampaignEnabled.equals("Y")) {
						data.put("campaignName", UtilValidate.isNotEmpty(salesOpportunity.getString("marketingCampaignId")) ? campaignNameMap.get(salesOpportunity.getString("marketingCampaignId")) : "");
						if(UtilValidate.isNotEmpty(salesOpportunity.getString("marketingCampaignId"))) {
							String campaignTypeId = (String) campaignChannelMap.get(salesOpportunity.getString("marketingCampaignId"));
							data.put("deliveryChannel", UtilValidate.isNotEmpty(campaignTypeId) ? campaignTypeMap.get(campaignTypeId)  : "");
						}
						String campaignStatusId = UtilValidate.isNotEmpty(salesOpportunity.getString("marketingCampaignId")) ? (String) campaignStatusMap.get(salesOpportunity.getString("marketingCampaignId")) : "";
						String dataSourceId = UtilValidate.isNotEmpty(salesOpportunity.getString("dataSourceId")) ? salesOpportunity.getString("dataSourceId") : "";
						if(dataSourceId.equals("CAMPAIGN") && UtilValidate.isNotEmpty(campaignStatusId) && "MKTG_CAMP_PUBLISHED".equals(campaignStatusId)) {
							continue;
						} else {
							data.put("source", UtilValidate.isNotEmpty(dataSourceMap) && UtilValidate.isNotEmpty(dataSourceMap.get(dataSourceId)) ? dataSourceMap.get(dataSourceId) : "");
						}
					}
					
					String bidType = "";
					GenericValue salesOppoAttr = EntityQuery.use(delegator).from("SalesOpportunityAttribute").where("salesOpportunityId",salesOppoId,"AttrName","BID_TYPE").queryFirst();
					bidType = UtilValidate.isNotEmpty(salesOppoAttr)?salesOppoAttr.getString("AttrValue"):"";
					data.put("bidType", bidType);
					String createdOn = "";
					if (UtilValidate.isNotEmpty(salesOpportunity.get("createdDate"))) {
						createdOn = UtilValidate.isNotEmpty(salesOpportunity.get("createdDate")) ? UtilDateTime.timeStampToString(salesOpportunity.getTimestamp("createdDate"), "MM-dd-yyyy", TimeZone.getDefault(), null) : "";
					}
					String modifiedOn = "";
					if (UtilValidate.isNotEmpty(salesOpportunity.get("lastModifiedDate"))) {
						modifiedOn = UtilValidate.isNotEmpty(salesOpportunity.get("lastModifiedDate")) ? UtilDateTime.timeStampToString(salesOpportunity.getTimestamp("lastModifiedDate"), "MM-dd-yyyy", TimeZone.getDefault(), null) : "";
					}
					
					String salesOppoTypeIdDesc = salesOpportunity.getString("salesOpportunityTypeId");
					if (UtilValidate.isNotEmpty(opportunityTypesMap) && UtilValidate.isNotEmpty(salesOpportunity.getString("salesOpportunityTypeId"))) {
						salesOppoTypeIdDesc = (String) opportunityTypesMap.get(salesOpportunity.getString("salesOpportunityTypeId"));
					}
					data.put("salesOppoTypeIdDesc", salesOppoTypeIdDesc);
					
					data.put("createdOn", createdOn);
					data.put("modifiedOn", modifiedOn);
					if("OPPO_CLOSED".equals(salesOpportunity.getString("opportunityStatusId"))) {
						data.put("closedOn", modifiedOn);
						data.put("closedBy", salesOpportunity.getString("lastModifiedByUserLogin"));
					}
					String sorPartyId = salesOpportunity.getString("sorPartyId");
					
					data.put("relationshipManager", UtilValidate.isNotEmpty(responsiblePartyMap) ? responsiblePartyMap.get(sorPartyId) : "");
					
					data.put("externalLoginKey", externalKey);
					dataList.add(data);

				}
				long end2 = System.currentTimeMillis();
				Debug.logInfo("timeElapsed for construction --->"+(end2-start2) / 1000f, MODULE);
				result.put("highIndex", Integer.valueOf(highIndex));
				result.put("lowIndex", Integer.valueOf(lowIndex));
			}

			result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			result.put("totalRecords", nf.format(resultListSize));
			result.put("recordCount", resultListSize);
			result.put("chunkSize", viewSize);   
			Debug.logInfo("data ready: "+UtilDateTime.nowTimestamp(), MODULE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}

		long end1 = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end1-start1) / 1000f, MODULE);
		result.put("timeTaken", (end1-start1) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);

	}

	public static String relateOpportunity(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String targetSalesOpportunityId = request.getParameter("targetSalesOpportunityId");
		String selectedSalesOpportunityIds = request.getParameter("selectedSalesOpportunityIds");

		Map<String, Object> result = FastMap.newInstance();

		try {

			if (UtilValidate.isNotEmpty(selectedSalesOpportunityIds)) {
				List<String> alreadyRelatedOppList = new ArrayList<String>();
				List<String> newRelatedOppList = new ArrayList<String>();
				StringTokenizer st = new StringTokenizer(selectedSalesOpportunityIds, ",");

				while (st.hasMoreTokens()) {
					String salesOpportunityId = st.nextToken();

					List conditionsList = FastList.newInstance();

					conditionsList.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS,
							targetSalesOpportunityId));
					conditionsList.add(EntityCondition.makeCondition("salesOpportunityAssocTypeId",
							EntityOperator.EQUALS, "OPPO_RELATED"));
					conditionsList.add(EntityCondition.makeCondition("salesOpportunityIdTo", EntityOperator.EQUALS,
							salesOpportunityId));

					EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

					GenericValue opportunityAssoc = EntityUtil.getFirst(
							delegator.findList("SalesOpportunityAssoc", mainConditons, null, null, null, false));
					if (UtilValidate.isNotEmpty(opportunityAssoc)) {
						alreadyRelatedOppList.add(salesOpportunityId);
						result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.CONFLICT_CODE);
						result.put(GlobalConstants.RESPONSE_MESSAGE,
								"Already related opportunity# " + salesOpportunityId);
					} else {

						opportunityAssoc = delegator.makeValue("SalesOpportunityAssoc");

						opportunityAssoc.put("salesOpportunityId", targetSalesOpportunityId);
						opportunityAssoc.put("salesOpportunityAssocTypeId", "OPPO_RELATED");
						opportunityAssoc.put("salesOpportunityIdTo", salesOpportunityId);
						opportunityAssoc.put("fromDate", UtilDateTime.nowTimestamp());

						opportunityAssoc.create();

						newRelatedOppList.add(salesOpportunityId);
					}

				}

				String message = "";
				if (UtilValidate.isNotEmpty(alreadyRelatedOppList)) {
					message += "Already related opportunity# " + alreadyRelatedOppList.toString();
				}
				if (UtilValidate.isNotEmpty(newRelatedOppList)) {
					message += "Successfully related opportunity# " + newRelatedOppList.toString();
				}

				result.put(GlobalConstants.RESPONSE_MESSAGE, message);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);

	}

	public static String searchNotes(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		String isImportant =  request.getParameter("isImportant");
		String contactListId =  request.getParameter("contactListId");
		String marketingCampaignId =  request.getParameter("marketingCampaignId");

		String partyId = request.getParameter("partyId");
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");

		String noteType = request.getParameter("noteCat");
		String noteUserRoleTypeId = request.getParameter("noteUserRoleTypeId");
		String noteUserLoginId = request.getParameter("noteUserLoginId");

		String srStatusId = request.getParameter("srStatusId");
		String workEffortId = request.getParameter("workEffortId");

		String contextPath = request.getParameter("contextPath");
		if(UtilValidate.isNotEmpty(contextPath)) contextPath = contextPath.substring(1);

		String userLoginId = userLogin.getString("userLoginId");

		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String partyIdStatus = "";
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);

			List conditionList = FastList.newInstance();

			boolean isAccount = false;

 			if (UtilValidate.isNotEmpty(partyId) && !partyId.equals("N/A")) {
				GenericValue partySummary = delegator.findOne("PartySummaryDetailsView",
						UtilMisc.toMap("partyId", partyId), false);
				partyIdStatus = (String) partySummary.get("statusId");
			}
			if (UtilValidate.isNotEmpty(partyId) && (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType))) {
				
				/*GenericValue partyRole = delegator.findOne("PartyRole",
						UtilMisc.toMap("partyId", partyId, "roleTypeId", "ACCOUNT"), false);
				if (UtilValidate.isNotEmpty(partyRole)) {
					isAccount = true;
				}
				if (isAccount) {
					
					EntityCondition roleCondition = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
							EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"),
							EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV")),
							EntityOperator.AND);

					conditionList.add(roleCondition);
					
					EntityCondition statusCondition = EntityCondition.makeCondition(
							UtilMisc.toList(
									EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
									EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)),
							EntityOperator.OR);
					conditionList.add(statusCondition);
					conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
					conditionList.add(EntityUtil.getFilterByDateExpr());

					EntityFindOptions efo1 = new EntityFindOptions();
					efo1.setDistinct(true);
					List partyFromRelnListNote = delegator.findList(
							"PartyFromByRelnAndContactInfoAndPartyClassification",
							EntityCondition.makeCondition(conditionList, EntityOperator.AND), null,
							UtilMisc.toList("createdDate"), efo1, false);
					conditionList = FastList.newInstance();
					if (partyFromRelnListNote != null && partyFromRelnListNote.size() > 0) {
						List partyFromRelnNote = EntityUtil.getFieldListFromEntityList(partyFromRelnListNote,
								"partyIdFrom", true);
						if (partyFromRelnNote != null && partyFromRelnNote.size() > 0) {
							conditionList.add(EntityCondition.makeCondition("targetPartyId", EntityOperator.EQUALS, partyId));

							// conditionList.add(EntityCondition.makeCondition("targetPartyId",
							// EntityOperator.IN, partyFromRelnNote));
						}
					}
				}*/
				
				if (UtilValidate.isNotEmpty(partyId) && !partyId.equals("N/A")) {
					conditionList.add(EntityCondition.makeCondition("targetPartyId", EntityOperator.EQUALS, partyId));
				}
			}

			String entityName = "PartyNoteView";
			if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
				entityName = "OpportunityNoteView";
				conditionList.add(
						EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, domainEntityId));
			} else if (UtilValidate.isNotEmpty(domainEntityType) 
					&& (domainEntityType.equals(DomainEntityType.SUBSCRIPTION) || domainEntityType.equals(DomainEntityType.SUBS_PRODUCT) || domainEntityType.equals(DomainEntityType.REBATE) || domainEntityType.equals(DomainEntityType.APV_TPL) || domainEntityType.equals(DomainEntityType.SQL_GRP))) {
				entityName = "CommonNoteView";
				conditionList
				.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
				conditionList.add(
						EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
			} else if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
				entityName = "CustRequestNoteView";

				List<String> noteIds = new ArrayList<String>();
				if (UtilValidate.isEmpty(workEffortId)) {
					conditionList
					.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, domainEntityId));
				}
			}else if (UtilValidate.isNotEmpty(domainEntityType)
					&& domainEntityType.equals(DomainEntityType.PRODUCT_PROMO_CODE)) {
				if(UtilValidate.isNotEmpty(partyId) && partyId.equals(userLogin.getString("partyId"))) {
					conditionList.add(EntityCondition.makeCondition("noteParty", EntityOperator.EQUALS, userLogin.getString("partyId")));
				}
					conditionList.add(EntityCondition.makeCondition("productPromoCodeId", EntityOperator.EQUALS, domainEntityId));
				entityName = "ProductPromoCodeNoteView";
			}

			List<String> noteIds = new ArrayList<String>();
			if (UtilValidate.isNotEmpty(workEffortId)) {
				List<GenericValue> workEffortNotes = EntityQuery.use(delegator).from("WorkEffortNote")
						.where("workEffortId", workEffortId).queryList();
				noteIds = UtilValidate.isNotEmpty(workEffortNotes)
						? EntityUtil.getFieldListFromEntityList(workEffortNotes, "noteId", true)
								: new ArrayList<>(Arrays.asList("898559887789"));
						if (UtilValidate.isNotEmpty(noteIds)) {
							conditionList.add(EntityCondition.makeCondition("noteId", EntityOperator.IN, noteIds));
						}
						domainEntityType = DomainEntityType.ACTIVITY;
						domainEntityId = workEffortId;
			}

			if (UtilValidate.isNotEmpty(noteType)) {
				conditionList.add(EntityCondition.makeCondition("noteType", EntityOperator.EQUALS, noteType));
			}
			if (UtilValidate.isNotEmpty(noteUserRoleTypeId)) {
				conditionList.add(EntityCondition.makeCondition("createdByUserLoginRoleTypeId", EntityOperator.EQUALS,
						noteUserRoleTypeId));
			}
			if (UtilValidate.isNotEmpty(noteUserLoginId)) {
				conditionList.add(
						EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, noteUserLoginId));
			}
			
			if(UtilValidate.isNotEmpty(isImportant)) {
				conditionList.add(EntityCondition.makeCondition("isImportant", EntityOperator.EQUALS, isImportant));
			}
			
			if (UtilValidate.isNotEmpty(conditionList)) {
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> noteList = delegator.findList(entityName, mainConditons, null,
						UtilMisc.toList("noteDateTime DESC"), null, false);
				if (UtilValidate.isNotEmpty(noteList)) {
					long importantNotesCount = delegator.findCountByCondition("PartyNoteView", EntityCondition.makeCondition(UtilMisc.toMap("targetPartyId", partyId,"isImportant","Y")), null, null);
					for (GenericValue entry : noteList) {
						Map<String, Object> data = new HashMap<String, Object>();
						data = org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, entry);
						data.put("noteDesc", org.fio.admin.portal.util.DataUtil.removeHtmlTags(entry.getString("noteInfo")));
						String noteId = entry.getString("noteId");

						//data.put("noteId", entry.getString("noteId"));
						//data.put("noteInfo", entry.getString("noteInfo"));
						//data.put("noteParty", entry.getString("noteParty"));
						data.put("domainEntityId", domainEntityId);
						data.put("domainEntityType", domainEntityType);
						//data.put("isImportant", entry.getString("isImportant"));
						//data.put("domainEntityId", entry.getString("domainEntityId"));
						//data.put("domainEntityType", entry.getString("domainEntityType"));
						data.put("domainEntityTypeDesc", org.groupfio.common.portal.util.DataHelper.convertToLabel(domainEntityType));

						//data.put("noteName", entry.getString("noteName"));
						if (UtilValidate.isNotEmpty(partyId) && (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType))) {
							data.put("partyId", partyId);
						} else {
							data.put("partyId", domainEntityId);
						}
						data.put("noteDateTime",
								UtilValidate.isNotEmpty(entry.get("noteDateTime"))
								? UtilDateTime.timeStampToString(entry.getTimestamp("noteDateTime"),
										globalDateFormat, TimeZone.getDefault(), null)
										: "");

						data.put("expiredDate",
								UtilValidate.isNotEmpty(entry.get("expiredDate"))
								? UtilDateTime.timeStampToString(entry.getTimestamp("expiredDate"),
										globalDateFormat, TimeZone.getDefault(), null)
										: "");
						data.put("noteTypeId", entry.getString("noteType"));
						data.put("noteType", EnumUtil.getEnumDescription(delegator, entry.getString("noteType")));
						data.put("createdByName",
								PartyHelper.getPartyName(delegator, org.fio.homeapps.util.DataUtil
										.getPartyIdByUserLoginId(delegator, entry.getString("createdByUserLogin")),
										false));
						data.put("createdByRole", org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator,
								entry.getString("createdByUserLoginRoleTypeId")));

						if (UtilValidate.isNotEmpty(entry.getString("noteParty"))) {
							data.put("notePartyName",
									PartyHelper.getPartyName(delegator, entry.getString("noteParty"), false));
						}
						String domainEntityIdName ="";
						if(UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.PRODUCT_PROMO_CODE)) {
							domainEntityIdName = PartyHelper.getPartyName(delegator, entry.getString("noteParty"), false);
						} else if (UtilValidate.isNotEmpty(entityName) && entityName.equals("PartyNoteView") && UtilValidate.isNotEmpty(entry.getString("targetPartyId"))) {
							domainEntityIdName = PartyHelper.getPartyName(delegator, entry.getString("targetPartyId"), false);
						}
						data.put("domainEntityIdName", domainEntityIdName);

						if (UtilValidate.isNotEmpty(srStatusId)) {
							data.put("srStatusId", srStatusId);
						}
						if (UtilValidate.isNotEmpty(partyIdStatus)) {
							data.put("partyIdStatus", partyIdStatus);
						}
						if (UtilValidate.isNotEmpty(marketingCampaignId)) {
							data.put("marketingCampaignId", marketingCampaignId);
						}
						if (UtilValidate.isNotEmpty(contactListId)) {
							data.put("contactListId", contactListId);
						}
						data.put("contextPath", contextPath);
						data.put("loginUser", userLoginId);
						data.put("edit", noteId);
						data.put("remove", noteId);
						dataList.add(data);
					}
					result.put("importantNotesCount", importantNotesCount);
				}
			}
			result.put("data", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);

	}

	public static String createNoteData(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String noteString = (String) context.get("note");
		String noteId = (String) context.get("noteId");
		String noteName = (String) context.get("noteName");
		String isImportant = (String) context.get("isImportant");
		String campaignNoteId = (String) context.get("campaignNoteId");
		Locale locale = (Locale) context.get("locale");
		if(UtilValidate.isEmpty(locale))
			locale = UtilHttp.getLocale(request);
		String noteType = (String) context.get("noteType");
		String callBackDate = (String) context.get("callBackDate");
		String subProduct = (String) context.get("subProduct");
		String noteTypeId = (String) context.get("noteTypeId");

		String partyId = (String) context.get("partyId");

		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String custRequestId = (String) context.get("custRequestId");
		String workEffortId = (String) context.get("workEffortId");
		String expiredDate = (String) context.get("expiredDate");
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastContactDate = sdf.format(new Date());

		Timestamp expiredDateTs = null;
		if(UtilValidate.isNotEmpty(expiredDate)) {
			try {
				expiredDateTs = UtilDateTime.getDayEnd(UtilDateTime.stringToTimeStamp(expiredDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
			}catch (Exception pe) {
				pe.printStackTrace();
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		String callListNoteType = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_CALL_LIST_NOTE_TYPE", "N");
		if(UtilValidate.isNotEmpty(callListNoteType) && callListNoteType.equals("Y")) {
			String callListNoteTypeVal = (String) context.get("callListNoteType");
			noteType = callListNoteTypeVal;
		}
		
		try {

			Map<String, Object> noteRes = null;
			try {
				if (UtilValidate.isNotEmpty(domainEntityType) && "OPPORTUNITY".equals(domainEntityType)) {
					GenericValue salesOpportunityData = EntityQuery.use(delegator).from("SalesOpportunity")
							.where("salesOpportunityId", salesOpportunityId).queryOne();
					if (UtilValidate.isNotEmpty(salesOpportunityData)
							&& UtilValidate.isNotEmpty(salesOpportunityData.getString("partyId"))) {
						partyId = salesOpportunityData.getString("partyId");
					}
				} else if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
					GenericValue custRequestData = EntityQuery.use(delegator).from("CustRequest")
							.where("custRequestId", custRequestId).queryOne();
					if (UtilValidate.isNotEmpty(custRequestData)
							&& UtilValidate.isNotEmpty(custRequestData.getString("fromPartyId"))) {
						partyId = custRequestData.getString("fromPartyId");
					}
				} else if (UtilValidate.isNotEmpty(domainEntityType) && ("SUBSCRIPTION".equals(domainEntityType))) {
					GenericValue subscription = EntityQuery.use(delegator).from("Subscription")
							.where("subscriptionId", domainEntityId).queryOne();
					if (UtilValidate.isNotEmpty(subscription)
							&& UtilValidate.isNotEmpty(subscription.getString("partyId"))) {
						partyId = subscription.getString("partyId");
					}
				} else if (UtilValidate.isNotEmpty(domainEntityType) && ("REBATE".equals(domainEntityType))) {
					GenericValue entity = EntityQuery.use(delegator).from("Agreement")
							.where("agreementId", domainEntityId).queryOne();
					if (UtilValidate.isNotEmpty(entity)
							&& UtilValidate.isNotEmpty(entity.getString("partyIdTo"))) {
						partyId = entity.getString("partyIdTo");
					}
				}

				if (UtilValidate.isEmpty(partyId)) {
					partyId = null;
				}
				if(UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.PRODUCT_PROMO_CODE) && UtilValidate.isNotEmpty(partyId) && partyId.equals("N/A")) {
					partyId=userLogin.getString("partyId");
				}
				noteRes = dispatcher.runSync("createNote", UtilMisc.toMap("partyId", partyId, "note", noteString,
						"userLogin", userLogin, "locale", locale, "noteName", noteName,"expiredDate",expiredDateTs
						// "createdByUserLogin", userLogin.getString("userLoginId")
						));
				if(ServiceUtil.isError(noteRes) || ServiceUtil.isFailure(noteRes)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(noteRes));
					return AjaxEvents.doJSONResponse(response, result);
				}
			} catch (GenericServiceException e) {
				Debug.logError(e, e.getMessage(), MODULE);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, UtilProperties.getMessage("PartyErrorUiLabels",
						"PartyNoteCreationError", UtilMisc.toMap("errorString", e.getMessage()), locale));
				return AjaxEvents.doJSONResponse(response, result);
			}

			noteId = (String) noteRes.get("noteId");
			if (UtilValidate.isEmpty(noteId)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, UtilProperties.getMessage("PartyErrorUiLabels",
						"partyservices.problem_creating_note_no_noteId_returned", locale));
				return AjaxEvents.doJSONResponse(response, result);
			} else {
				GenericValue noteData = null;
				try {
					noteData = delegator.findOne("NoteData", false, UtilMisc.toMap("noteId", noteId));
					if (UtilValidate.isNotEmpty(noteType)) {
						noteData.put("noteType", noteType);
					}
					
					if(UtilValidate.isNotEmpty(expiredDate)) {
							/*
							Date expireDate1 = new SimpleDateFormat("yyyy-MM-dd").parse(expiredDate);
							expiredDate = sdf.format(expireDate1);
							noteData.put("expiredDate", java.sql.Date.valueOf(expiredDate));
							*/
							noteData.put("expiredDate", expiredDateTs);
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
					noteData.store();

					String userLoginPartyId = UtilValidate.isNotEmpty(userLogin) ? userLogin.getString("partyId") : "";
					String userLoginName = org.fio.homeapps.util.DataUtil.getUserLoginName(delegator, userLoginPartyId);
					
					String eventName = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "NOTE_CREATE_EVENT_NAME", "Create Note");
					Map<String, Object> eventMap = new HashMap<String, Object>();
					String eventDescription = "";
					String viewUrl = "";
					
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
									|| domainEntityType.equals(DomainEntityType.SQL_GRP)
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
						
						String donePage = request.getParameter("donePage");

						if(UtilValidate.isNotEmpty(domainEntityId) || UtilValidate.isNotEmpty(custRequestId)) {
							String custReqId = UtilValidate.isNotEmpty(domainEntityId) ? domainEntityId : custRequestId;
							GenericValue custReq = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custReqId).queryFirst();
							if(UtilValidate.isNotEmpty(custReq)) {
								custReq.set("lastModifiedDate", UtilDateTime.nowTimestamp());
								custReq.store();
							}
						}
						
						//call the event registry to store sr note event information
						//for notification
						eventDescription = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_NOTE_CREATE_EVENT_DESC", "New note added by {1} for this Service({0})");
						String eventUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_NOTE_EVENT_URL", "");
						if(eventDescription.contains("{0}") || eventDescription.contains("{1}")) {
							eventDescription = MessageFormat.format(eventDescription, new Object[] { custRequestId , userLoginName});
						}
						
						if(UtilValidate.isNotEmpty(eventUrl)) {
							if(eventUrl.contains("{0}")) {
								eventUrl = MessageFormat.format(eventUrl, new Object[] { custRequestId });
							}
							viewUrl = eventUrl;
						} else {
							String serverRootUrl = UtilValidate.isNotEmpty(request) ? UtilHttp.getServerRootUrl(request) : "";
							String contextPath =  UtilValidate.isNotEmpty(request) ? request.getContextPath() : "";
							
							if(UtilValidate.isNotEmpty(serverRootUrl) && UtilValidate.isNotEmpty(donePage)) {
								viewUrl = serverRootUrl+donePage+"?srNumber="+custRequestId;
							}
						}
						
						eventMap.put("entityId", custRequestId);
						eventMap.put("entityName", "CustRequestNote");
						eventMap.put("eventType", "NOTES");
						
						eventMap.put("eventDate", org.ofbiz.base.util.UtilDateTime.nowTimestamp());
						eventMap.put("eventName", eventName);
						eventMap.put("eventDescription", eventDescription);
						eventMap.put("eventUrl", viewUrl);
						eventMap.put("domainEntityId", UtilValidate.isNotEmpty(domainEntityId) ? domainEntityId : custRequestId);
						eventMap.put("domainEntityType", domainEntityType);
						eventMap.put("userLogin", userLogin);
						dispatcher.runSync("notificationEventRegister", eventMap);
						
						
					}

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
						//call the event registry to store activity note event information
						//for notification
						String donePage = request.getParameter("donePage");
						
						eventDescription = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_NOTE_CREATE_EVENT_DESC", "New note added by {1} for this Service({0})");
						String eventUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_NOTE_EVENT_URL", "");
						if(eventDescription.contains("{0}") || eventDescription.contains("{1}")) {
							eventDescription = MessageFormat.format(eventDescription, new Object[] { custRequestId , userLoginName});
						}
						if(UtilValidate.isNotEmpty(eventUrl)) {
							if(eventUrl.contains("{0}")) {
								eventUrl = MessageFormat.format(eventUrl, new Object[] { custRequestId });
							}
							viewUrl = eventUrl;
						} else {
							String serverRootUrl = UtilValidate.isNotEmpty(request) ? UtilHttp.getServerRootUrl(request) : "";
							String contextPath =  UtilValidate.isNotEmpty(request) ? request.getContextPath() : "";
							
							if(UtilValidate.isNotEmpty(serverRootUrl) && UtilValidate.isNotEmpty(donePage)) {
								viewUrl = serverRootUrl+donePage+"?workEffortId="+workEffortId;
							}
						}
						
						eventMap.put("entityId", workEffortId);
						eventMap.put("entityName", "WorkEffortNote");
						eventMap.put("eventType", "NOTES");
						
						eventMap.put("eventDate", org.ofbiz.base.util.UtilDateTime.nowTimestamp());
						eventMap.put("eventName", eventName);
						eventMap.put("eventDescription", eventDescription);
						eventMap.put("eventUrl", viewUrl);
						eventMap.put("domainEntityId", domainEntityId);
						eventMap.put("domainEntityType", domainEntityType);
						eventMap.put("userLogin", userLogin);
						dispatcher.runSync("notificationEventRegister", eventMap);
						
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
						return AjaxEvents.doJSONResponse(response, result);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
					return AjaxEvents.doJSONResponse(response, result);
				}

			}

			result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully created note");
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);

	}

	public static String updateNoteData(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String noteString = (String) context.get("note");
		String noteId = (String) context.get("noteId");
		String noteName = (String) context.get("noteName");
		String isImportant = (String) context.get("isImportant");
		String campaignNoteId = (String) context.get("campaignNoteId");
		Locale locale = (Locale) context.get("locale");

		String noteType = (String) context.get("noteType");
		String callBackDate = (String) context.get("callBackDate");
		String subProduct = (String) context.get("subProduct");
		String noteTypeId = (String) context.get("noteTypeId");

		String partyId = (String) context.get("partyId");

		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");

		String expiredDate = (String) context.get("expiredDate");
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		Map<String, Object> result = FastMap.newInstance();

		try {

			GenericValue noteData = EntityUtil
					.getFirst(delegator.findByAnd("NoteData", UtilMisc.toMap("noteId", noteId), null, false));
			if (UtilValidate.isEmpty(noteData)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.NOT_FOUND_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Note not exists!");
				return AjaxEvents.doJSONResponse(response, result);
			}

			Map<String, Object> noteRes = null;
			Map<String, Object> noteReq = UtilMisc.toMap("noteId", noteId, "noteParty", partyId, "noteInfo", noteString,
					"userLogin", userLogin, "locale", locale, "noteName", noteName, "subProduct", subProduct);

			if (UtilValidate.isNotEmpty(callBackDate)) {
				noteReq.put("callBackDate", UtilCommon.parseToDate(callBackDate, "yyyy-MM-dd"));
			}
			if (UtilValidate.isNotEmpty(noteType)) {
				noteReq.put("noteType", noteType);
			}
			if(UtilValidate.isNotEmpty(expiredDate)) {
				try {
					Timestamp expiredDateTs = UtilDateTime.getDayEnd(UtilDateTime.stringToTimeStamp(expiredDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
					/*
					Date expireDate1 = new SimpleDateFormat("yyyy-MM-dd").parse(expiredDate);
					expiredDate = sdf.format(expireDate1);
					noteData.put("expiredDate", java.sql.Date.valueOf(expiredDate));
					*/
					noteReq.put("expiredDate", expiredDateTs);
				} catch (Exception pe) {
					pe.printStackTrace();
				}
			}

			try {
				noteRes = dispatcher.runSync("updateNote", noteReq
						// "createdByUserLogin", userLogin.getString("userLoginId")
						);
			} catch (GenericServiceException e) {
				Debug.logError(e, e.getMessage(), MODULE);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, UtilProperties.getMessage("PartyErrorUiLabels",
						"PartyNoteCreationError", UtilMisc.toMap("errorString", e.getMessage()), locale));
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			String userLoginPartyId = UtilValidate.isNotEmpty(userLogin) ? userLogin.getString("partyId") : "";
			String userLoginName = org.fio.homeapps.util.DataUtil.getUserLoginName(delegator, userLoginPartyId);
			
			String eventName = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "NOTE_UPDATE_EVENT_NAME", "Update Note");
			Map<String, Object> eventMap = new HashMap<String, Object>();
			String eventDescription = "";
			String viewUrl = "";
			String donePage = request.getParameter("donePage");

			GenericValue partySupplData = delegator.findOne("PartySupplementalData", UtilMisc.toMap("partyId", partyId),
					false);
			if (UtilValidate.isNotEmpty(partySupplData)) {
				if (UtilValidate.isNotEmpty(callBackDate)) {
					partySupplData.set("lastCallBackDate", UtilCommon.parseToDate(callBackDate, "yyyy-MM-dd"));
					partySupplData.store();
				}
			}

			if (UtilValidate.isNotEmpty(domainEntityType) && (UtilValidate.isNotEmpty(domainEntityType)
					&& CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType))) {
				GenericValue assoc = EntityUtil.getFirst(delegator.findByAnd("PartyNote",
						UtilMisc.toMap("partyId", domainEntityId, "noteId", noteId), null, false));
				if (UtilValidate.isNotEmpty(assoc)) {
					assoc.put("isImportant", isImportant);
					assoc.store();
				}
			}

			if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
				GenericValue assoc = EntityUtil.getFirst(delegator.findByAnd("SalesOpportunityNote",
						UtilMisc.toMap("salesOpportunityId", domainEntityId, "noteId", noteId), null, false));
				if (UtilValidate.isNotEmpty(assoc)) {
					assoc.put("isImportant", isImportant);
					assoc.store();
				}
			} else if (UtilValidate.isNotEmpty(domainEntityType)
					&& (domainEntityType.equals(DomainEntityType.SUBSCRIPTION)
							|| domainEntityType.equals(DomainEntityType.SUBS_PRODUCT)
							|| domainEntityType.equals(DomainEntityType.REBATE)
							|| domainEntityType.equals(DomainEntityType.APV_TPL)
							|| domainEntityType.equals(DomainEntityType.SQL_GRP)
							)
					) {
				GenericValue assoc = EntityUtil
						.getFirst(delegator.findByAnd("CommonNote", UtilMisc.toMap("domainEntityId", domainEntityId,
								"domainEntityType", domainEntityType, "noteId", noteId), null, false));
				if (UtilValidate.isNotEmpty(assoc)) {
					assoc.put("isImportant", isImportant);
					assoc.store();
				}
			} else if (UtilValidate.isNotEmpty(domainEntityType)
					&& CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
				GenericValue assoc = EntityUtil.getFirst(delegator.findByAnd("CustRequestNote",
						UtilMisc.toMap("custRequestId", domainEntityId, "noteId", noteId), null, false));
				if (UtilValidate.isNotEmpty(assoc)) {
					assoc.put("isImportant", isImportant);
					assoc.store();
				}
				
				
				//call the event registry to store sr note event information
				//for notification
				eventDescription = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_NOTE_UPDATE_EVENT_DESC", "Note Updated by {1} for this Service({0})");
				String eventUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_NOTE_EVENT_URL", "");
				if(eventDescription.contains("{0}") || eventDescription.contains("{1}")) {
					eventDescription = MessageFormat.format(eventDescription, new Object[] { domainEntityId , userLoginName});
				}
				
				if(UtilValidate.isNotEmpty(eventUrl)) {
					if(eventUrl.contains("{0}")) {
						eventUrl = MessageFormat.format(eventUrl, new Object[] { domainEntityId });
					}
					viewUrl = eventUrl;
				} else {
					String serverRootUrl = UtilValidate.isNotEmpty(request) ? UtilHttp.getServerRootUrl(request) : "";
					String contextPath =  UtilValidate.isNotEmpty(request) ? request.getContextPath() : "";
					
					if(UtilValidate.isNotEmpty(serverRootUrl) && UtilValidate.isNotEmpty(donePage)) {
						viewUrl = serverRootUrl+donePage+"?srNumber="+domainEntityId;
						//viewUrl = serverRootUrl+contextPath+"/control/viewServiceRequest?srNumber="+domainEntityId;
					}
				}
				
				eventMap.put("entityId", domainEntityId);
				eventMap.put("entityName", "CustRequestNote");
				eventMap.put("eventType", "NOTES");
				
				eventMap.put("eventDate", org.ofbiz.base.util.UtilDateTime.nowTimestamp());
				eventMap.put("eventName", eventName);
				eventMap.put("eventDescription", eventDescription);
				eventMap.put("eventUrl", viewUrl);
				eventMap.put("domainEntityId", domainEntityId);
				eventMap.put("domainEntityType", domainEntityType);
				eventMap.put("userLogin", userLogin);
				dispatcher.runSync("notificationEventRegister", eventMap);
				
			}

			result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully updated note");
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);

	}

	public static String removeNoteData(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String selectedNoteIds = request.getParameter("selectedNoteIds");

		Map<String, Object> result = FastMap.newInstance();

		try {

			if (UtilValidate.isNotEmpty(selectedNoteIds)) {

				for (String noteId : selectedNoteIds.split(",")) {

					delegator.removeByAnd("PartyNote", UtilMisc.toMap("noteId", noteId));
					delegator.removeByAnd("SalesOpportunityNote", UtilMisc.toMap("noteId", noteId));
					delegator.removeByAnd("CustRequestNote", UtilMisc.toMap("noteId", noteId));
					delegator.removeByAnd("WorkEffortNote", UtilMisc.toMap("noteId", noteId));
					delegator.removeByAnd("CommonNote", UtilMisc.toMap("noteId", noteId));
					delegator.removeByAnd("ProductPromoCodeNote", UtilMisc.toMap("noteId", noteId));

					delegator.removeByAnd("NoteData", UtilMisc.toMap("noteId", noteId));

				}

				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(ModelService.SUCCESS_MESSAGE, "Note removed successfully.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getSubProductList(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String productId = (String) context.get("productId");

		Map<String, Object> result = FastMap.newInstance();

		try {

			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();

			if (UtilValidate.isNotEmpty(productId)) {

				List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
				List<EntityCondition> conditionlist = FastList.newInstance();

				conditionlist.add(EntityCondition.makeCondition("parentEnumId", EntityOperator.EQUALS, productId));
				conditionlist.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "SUB_PRODUCT"));

				EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> subProductList = EntityQuery.use(delegator).select("enumId", "description")
						.from("Enumeration").where(condition).orderBy("sequenceId").queryList();

				if (UtilValidate.isNotEmpty(subProductList)) {

					for (GenericValue subProduct : subProductList) {
						Map<String, Object> data = new HashMap<String, Object>();

						data.put("productId", subProduct.getString("enumId"));
						data.put("productName", subProduct.getString("description"));

						dataList.add(data);
					}

				}

				result.put("dataList", dataList);
			}

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getNoteData(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String noteId = request.getParameter("noteId");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");

		String externalLoginKey = request.getParameter("externalLoginKey");

		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

			List conditionList = FastList.newInstance();

			conditionList.add(EntityCondition.makeCondition("noteId", EntityOperator.EQUALS, noteId));

			String entityName = "PartyNoteView";
			if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
				entityName = "OpportunityNoteView";
			} else if (UtilValidate.isNotEmpty(domainEntityType)
					&& CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
				entityName = "CustRequestNoteView";
			} else if (UtilValidate.isNotEmpty(domainEntityType)
					&& CommonPortalConstants.COMMON_NOTE_ENTITY_TYPE.containsKey(domainEntityType)) {
				entityName = "CommonNoteView";
			}else if (UtilValidate.isNotEmpty(domainEntityType)
					&& domainEntityType.equals(DomainEntityType.PRODUCT_PROMO_CODE)) {
				entityName = "ProductPromoCodeNoteView";
			}

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.OR);
			GenericValue entry = EntityUtil.getFirst(delegator.findList(entityName, mainConditons, null,
					UtilMisc.toList("noteDateTime DESC"), null, false));
			if (UtilValidate.isNotEmpty(entry)) {

				data.put("noteId", entry.getString("noteId"));
				data.put("noteName", entry.getString("noteName"));
				data.put("noteInfo", entry.getString("noteInfo"));
				data.put("note", entry.getString("noteInfo"));
				data.put("noteParty", entry.getString("noteParty"));
				data.put("domainEntityId", domainEntityId);
				data.put("domainEntityType", domainEntityType);
				data.put("isImportant", UtilValidate.isNotEmpty(entry.getString("isImportant")) ? entry.getString("isImportant") : "");
				data.put("createdBy", entry.getString("createdByUserLogin"));
				data.put("createdByName",  org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, entry.getString("createdByUserLogin"), false));
				data.put("notePartyName", PartyHelper.getPartyName(delegator, entry.getString("noteParty"), false));

				data.put("domainEntityId", UtilValidate.isNotEmpty(entry.getString("domainEntityId")) ? entry.getString("domainEntityId") : "");
				data.put("domainEntityType", UtilValidate.isNotEmpty(entry.getString("domainEntityType")) ? entry.getString("domainEntityType")  : "");
				data.put("domainEntityTypeDesc", UtilValidate.isNotEmpty(entry.getString("domainEntityType")) ? DataHelper.convertToLabel(entry.getString("domainEntityType")) : "");
				data.put("domainEntityLink", DataHelper.prepareLinkedFrom(entry.getString("domainEntityId"), entry.getString("domainEntityType"), externalLoginKey));

				data.put("createdStamp", UtilValidate.isNotEmpty(entry.get("createdStamp")) ? UtilDateTime.timeStampToString(entry.getTimestamp("createdStamp"), globalDateFormat, TimeZone.getDefault(), null) : "");
				data.put("noteDateTime", UtilValidate.isNotEmpty(entry.get("noteDateTime")) ? UtilDateTime.timeStampToString(entry.getTimestamp("noteDateTime"), globalDateFormat, TimeZone.getDefault(), null) : "");

				GenericValue partyNoteData = EntityUtil
						.getFirst(delegator.findByAnd("NoteData", UtilMisc.toMap("noteId", noteId), null, false));
				if (UtilValidate.isNotEmpty(partyNoteData)) {
					data.put("noteType", partyNoteData.getString("noteType"));
					data.put("subProduct", partyNoteData.getString("subProduct"));
					data.put("callBackDate", UtilValidate.isNotEmpty(partyNoteData.getString("callBackDate"))
							? new SimpleDateFormat(globalDateFormat).format(partyNoteData.getDate("callBackDate")) : "");
					data.put("expiredDate", org.fio.admin.portal.util.DataUtil.convertDateTimestamp(partyNoteData.getString("expiredDate"), new SimpleDateFormat(globalDateFormat), "timestamp", "string"));
				}
			}

			result.put("data", data);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", data);
			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String createAttachmentData(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String activeTab = (String) context.get("activeTab");
		String attachmentTitle = (String) context.get("attachmentTitle");
		String attchmentFIle = (String) context.get("attchmentFIle");
		String partyId = (String) context.get("partyId");
		String path = (String) context.get("path");
		String url = (String) context.get("url");
		String classificationEnumId = (String) context.get("classificationEnumId");
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String custRequestId = (String) context.get("custRequestId");
		String workEffortId = (String) context.get("workEffortId");
		String attachmentDescription = (String) context.get("attachmentDescription");
		String globalPathName = (String) context.get("globalPathName");
		String linkedFrom = (String) context.get("linkedFrom");
		String publicOrPrivate = (String) context.get("publicOrPrivate");
		// String helpfulLink =(String)context.get("helpfulLink");

		String filePath = null;
		int targetWidth =  UtilValidate.isNotEmpty(context.get("imgTargetWidth")) && org.fio.admin.portal.util.DataUtil.isDigits((String) context.get("imgTargetWidth")) ? Integer.parseInt((String) context.get("imgTargetWidth")) : 1024;
		int targetHeight =  UtilValidate.isNotEmpty(context.get("imgTargetHeight")) && org.fio.admin.portal.util.DataUtil.isDigits((String) context.get("imgTargetHeight")) ? Integer.parseInt((String) context.get("imgTargetHeight")) : 786;

		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");

		Map<String, Object> result = FastMap.newInstance();
		String filePathnew = request.getParameter("path");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastContactDate = sdf.format(new Date());
		//String name = "";

		try {
			
			String userLoginPartyId = UtilValidate.isNotEmpty(userLogin) ? userLogin.getString("partyId") : "";
			String userLoginName = org.fio.homeapps.util.DataUtil.getUserLoginName(delegator, userLoginPartyId);
			
			String eventName = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ATTACHMENT_CREATE_EVENT_NAME", "Create Attachment");
			Map<String, Object> eventMap = new HashMap<String, Object>();
			String eventDescription = "";
			String viewUrl = "";

			
			if (ServletFileUpload.isMultipartContent(request)) {
				try {

					globalPathName = UtilValidate.isNotEmpty(globalPathName) ? globalPathName : "UPLOAD_LOC";
					if (UtilValidate.isNotEmpty(path) || UtilValidate.isNotEmpty(globalPathName)) {
						filePath = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, globalPathName);
						if (UtilValidate.isNotEmpty(filePath)) {
							File dir = new File(filePath);
							if (!dir.exists()) {
								dir.mkdirs();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Debug.logError(e, MODULE);
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
					return AjaxEvents.doJSONResponse(response, result);
				}

				//String dataResourceId = delegator.getNextSeqId("DataResource");

				String fileRelativePath = "";
				//String fileName = "";
				String extension = "";
				Map<String, Object> passedParams = new HashMap<String, Object>();
				Debug.logInfo("before file read/write................."+UtilDateTime.nowTimestamp(), MODULE);
				List<Map<String, Object>> imageList = new LinkedList<Map<String,Object>>();
				//List<FileItem> fileItems = new ServletFileUpload(new DiskFileItemFactory(10240, FileUtil.getFile("runtime/tmp"))).parseRequest(request);
				List<FileItem> fileItems = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				for (FileItem item : fileItems) {
					if (!item.isFormField()) {
						Map<String, Object> imageFileSizeMap = new HashMap<String, Object>();
				    	String dataResourceId = delegator.getNextSeqId("DataResource");
				    	String fileName = new File(item.getName()).getName();
						extension = org.fio.admin.portal.util.DataUtil.getFileExtension(fileName);
						fileRelativePath = filePath + File.separator + dataResourceId+"."+extension;
						try (BufferedInputStream bufferedInputStream = new BufferedInputStream(item.getInputStream());
								OutputStream outputStream = new FileOutputStream(fileRelativePath);
								) {
							byte[] buffer = new byte[4 * 1024];
							int read;
							while ((read = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) {
								outputStream.write(buffer, 0, read);
							}
						}
						long fileSize = 0l;
						fileSize = (item.getSize()/1024) / 1024;
						Debug.logInfo("fileSize------------>>>>>"+fileSize, MODULE);

						imageFileSizeMap.put("fileName", fileName);
				    	imageFileSizeMap.put("dataResourceId", dataResourceId);
				    	imageFileSizeMap.put("fileSize", fileSize);
				    	imageList.add(imageFileSizeMap);
				    	
						/*
						 * File outputFile = new File(fileRelativePath); item.write(outputFile);
						 */
					} else if(item.isFormField()) {
						String fieldName = item.getFieldName();
						//String fieldStr = item.asString(inputStream);
						String fieldValue = item.getString();
						//passedParams.put(fieldName, fieldStr);
						if("partyId".equals(fieldName)) 
							partyId = fieldValue;
						else if("path".equals(fieldName))
							path = fieldValue;
						else if("attachmentDescription".equals(fieldName))
							attachmentDescription = fieldValue;
						else if("classificationEnumId".equals(fieldName))
							classificationEnumId = fieldValue;
						else if("salesOpportunityId".equals(fieldName))
							salesOpportunityId = fieldValue;
						else if("custRequestId".equals(fieldName))
							custRequestId = fieldValue;
						else if("domainEntityType".equals(fieldName))
							domainEntityType = fieldValue;
						else if("domainEntityId".equals(fieldName))
							domainEntityId = fieldValue;
						else if("workEffortId".equals(fieldName))
							workEffortId = fieldValue;
						else if("globalPathName".equals(fieldName))
							globalPathName = fieldValue;
						else if("linkedFrom".equals(fieldName))
							linkedFrom = fieldValue;
						else if("publicOrPrivate".equals(fieldName))
							publicOrPrivate = fieldValue;
						else if("imgTargetWidth".equals(fieldName))
							targetWidth = UtilValidate.isNotEmpty(fieldValue) && org.fio.admin.portal.util.DataUtil.isDigits((String) fieldValue) ? Integer.parseInt(fieldValue) : 1024;
							else if("imgTargetHeight".equals(fieldName))
								targetHeight = UtilValidate.isNotEmpty(fieldValue) && org.fio.admin.portal.util.DataUtil.isDigits((String) fieldValue) ? Integer.parseInt(fieldValue) : 786;
					}
				}
				Debug.logInfo("after file read/write................."+UtilDateTime.nowTimestamp(), MODULE);
				/*
				while (iterStream.hasNext()) {
				    FileItemStream item = iterStream.next();
				    InputStream inputStream = item.openStream();

				    if (!item.isFormField()) {
				    	fileName = new File(item.getName()).getName();
				        // Process the InputStream
				    	extension = org.fio.admin.portal.util.DataUtil.getFileExtension(fileName);
				    	originalImage = ImageIO.read(inputStream);
				    	/*
				    	fileRelativePath = filePath + File.separator + dataResourceId+"."+extension;
                        File outputFile = new File(fileRelativePath);
				    	FileUtils.copyInputStreamToFile(inputStream, outputFile);
				    	inputStream.close();
				    	fileSize = Files.size(outputFile.toPath());
				 *
				    } else if(item.isFormField()) {
				    	String fieldName = item.getFieldName();
				    	String fieldStr = Streams.asString(inputStream);
	                    passedParams.put(fieldName, fieldStr);
				    }

				}
				 */
				List<String> nonImageFormat = new ArrayList<>();
				nonImageFormat.add("wmv");
				nonImageFormat.add("webm");
				nonImageFormat.add("mp4");
				nonImageFormat.add("mpg");
				nonImageFormat.add("mpeg");
				nonImageFormat.add("m4v");
				nonImageFormat.add("mov");
				nonImageFormat.add("3gp");
				nonImageFormat.add("3gpp");
				nonImageFormat.add("pdf");
				nonImageFormat.add("xlsx");
				nonImageFormat.add("docx");
				nonImageFormat.add("xls");
				nonImageFormat.add("doc");

				nonImageFormat.add("tiff");
				nonImageFormat.add("tif");
				nonImageFormat.add("heic");
				nonImageFormat.add("heif");

				
				if(UtilValidate.isNotEmpty(imageList)) {
					int attachCount = 0;
					for(Map<String, Object> imageMap : imageList) {
						String fileName =  (String) imageMap.get("fileName"); 
						String dataResourceId = (String) imageMap.get("dataResourceId");
						long fileSize = UtilValidate.isNotEmpty(imageMap.get("dataResourceId")) ? (long) imageMap.get("fileSize") : 0l;
						
						if( UtilValidate.isNotEmpty(fileName) && !nonImageFormat.contains(extension)) {
							Debug.logInfo("before file resize................."+UtilDateTime.nowTimestamp(), MODULE);
							try {
								fileRelativePath = filePath + File.separator + dataResourceId+"."+extension;
								File outputFile = new File(fileRelativePath);
								int uploadImgSize = Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "UPLOAD_IMG_SIZE", "3"));

								if(fileSize >uploadImgSize) {
									BufferedImage originalImage = ImageIO.read(outputFile);
									/*
		    						BufferedImage resizedImage = Scalr.resize(originalImage, 
		    								Scalr.Method.SPEED,
		    								Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, 
		    								Scalr.OP_ANTIALIAS);
									 */
									//Image tmp = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
									BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
									Graphics2D graphics2D = resizedImage.createGraphics();
									graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
									graphics2D.dispose();
									ImageIO.write(resizedImage, extension, outputFile);
								}

							}catch (Exception e) {
								e.printStackTrace();
							}
							Debug.logInfo("after file resize................."+UtilDateTime.nowTimestamp(), MODULE);
						}

						String mimeTypeId = new MimetypesFileTypeMap().getContentType(fileName);

						GenericValue dataResource = delegator.makeValue("DataResource");

						dataResource.set("dataResourceId", dataResourceId);
						dataResource.set("dataResourceName", fileName);
						dataResource.set("dataResourceTypeId", "LOCAL_FILE");
						dataResource.set("statusId", "CTNT_PUBLISHED");
						dataResource.set("mimeTypeId", mimeTypeId);
						// dataResource.set("objectInfo", filePath+"/"+name + "_"
						// +partyId);
						dataResource.set("objectInfo", fileRelativePath);
						dataResource.create();
						GenericValue content = delegator.makeValue("Content");
						String contentId = delegator.getNextSeqId("Content");
						// added prefix for attachment Id
						contentId = "AT-" + contentId;
						// ended
						content.set("contentId", contentId);
						content.set("dataResourceId", dataResourceId);
						content.set("classificationEnumId", classificationEnumId);
						content.set("contentName", fileName);
						content.set("contentTypeId", "ATTACHMENT");
						content.set("description", attachmentDescription);
						content.set("domainEntityId", domainEntityId);
						content.set("domainEntityType", domainEntityType);
						content.set("linkedFrom", linkedFrom);
						content.set("createdDate", UtilDateTime.nowTimestamp());
						content.set("mimeTypeId", mimeTypeId);
						content.set("createdByUserLogin", userLogin.getString("userLoginId"));
						content.create();

						String isPublic = UtilValidate.isNotEmpty(publicOrPrivate) && "PUBLIC".equals(publicOrPrivate) ? "Y" : "N";

						DataHelper.contentAssociate(delegator,
								UtilMisc.toMap("contentId", contentId, "partyId", partyId, "salesOpportunityId",
										salesOpportunityId, "custRequestId", custRequestId, "workEffortId", workEffortId,
										"publicOrPrivate", isPublic, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType));

						result.put("contentId", contentId);
						attachCount +=1;
					}
					if(UtilValidate.isNotEmpty(workEffortId)){
						eventDescription = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_ATTACHEMENT_CREATE_EVENT_DESC", "New attachement added by {1} for this activity ({0})");
						String eventUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_ATTACHEMENT_EVENT_URL", "");
						
						Object[] obj =new Object[] { workEffortId , userLoginName, attachCount};
						if(eventDescription.contains("{0}") || eventDescription.contains("{1}")) {
							eventDescription = MessageFormat.format(eventDescription, obj);
						}
						if(UtilValidate.isNotEmpty(eventUrl)) {
							if(eventUrl.contains("{0}")) {
								eventUrl = MessageFormat.format(eventUrl, new Object[] { custRequestId });
							}
							viewUrl = eventUrl;
						} else {
							String serverRootUrl = UtilValidate.isNotEmpty(request) ? UtilHttp.getServerRootUrl(request) : "";
							String contextPath =  UtilValidate.isNotEmpty(request) ? request.getContextPath() : "";
							
							if(UtilValidate.isNotEmpty(serverRootUrl) && UtilValidate.isNotEmpty(contextPath)) {
								viewUrl = serverRootUrl+"/" +path+"/control/viewActivity?workEffortId="+workEffortId;
							}
						}
						
						eventMap.put("entityId", workEffortId);
						eventMap.put("entityName", "WorkEffortContent");
						eventMap.put("eventType", "ATTACHMENTS");
						
						eventMap.put("eventDate", org.ofbiz.base.util.UtilDateTime.nowTimestamp());
						eventMap.put("eventName", eventName);
						eventMap.put("eventDescription", eventDescription);
						eventMap.put("eventUrl", viewUrl);
						eventMap.put("domainEntityId", domainEntityId);
						eventMap.put("domainEntityType", domainEntityType);
						eventMap.put("userLogin", userLogin);
						dispatcher.runSync("notificationEventRegister", eventMap);
					} else if(UtilValidate.isNotEmpty(custRequestId)){
						
						eventDescription = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_ATTACHEMENT_CREATE_EVENT_DESC", "New attachement added by {1} for this service ({0})");
						String eventUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_ATTACHEMENT_EVENT_URL", "");
						Object[] obj =new Object[] { custRequestId , userLoginName, attachCount};
						if(eventDescription.contains("{0}") || eventDescription.contains("{1}")) {
							eventDescription = MessageFormat.format(eventDescription, obj);
						}
						if(UtilValidate.isNotEmpty(eventUrl)) {
							if(eventUrl.contains("{0}")) {
								eventUrl = MessageFormat.format(eventUrl, new Object[] { custRequestId });
							}
							viewUrl = eventUrl;
						} else {
							String serverRootUrl = UtilValidate.isNotEmpty(request) ? UtilHttp.getServerRootUrl(request) : "";
							String contextPath =  UtilValidate.isNotEmpty(request) ? request.getContextPath() : "";
							
							if(UtilValidate.isNotEmpty(serverRootUrl) && UtilValidate.isNotEmpty(contextPath)) {
								viewUrl = serverRootUrl+"/" +path+"/control/viewServiceRequest?srNumber="+custRequestId;
							}
						}
						
						eventMap.put("entityId", custRequestId);
						eventMap.put("entityName", "CustRequestContent");
						eventMap.put("eventType", "ATTACHMENTS");
						
						eventMap.put("eventDate", org.ofbiz.base.util.UtilDateTime.nowTimestamp());
						eventMap.put("eventName", eventName);
						eventMap.put("eventDescription", eventDescription);
						eventMap.put("eventUrl", viewUrl);
						eventMap.put("domainEntityId", domainEntityId);
						eventMap.put("domainEntityType", domainEntityType);
						eventMap.put("userLogin", userLogin);
						dispatcher.runSync("notificationEventRegister", eventMap);
					}
				}
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully created Attachment");
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			} else {
				GenericValue DataResource = delegator.makeValue("DataResource");
				String dataResourceId = delegator.getNextSeqId("DataResource");
				DataResource.set("dataResourceId", dataResourceId);
				DataResource.set("dataResourceName", url);
				DataResource.set("dataResourceTypeId", "URL_RESOURCE");
				DataResource.set("statusId", "CTNT_PUBLISHED");
				DataResource.set("mimeTypeId", "text/plain");
				DataResource.set("objectInfo", url);
				DataResource.create();
				GenericValue content = delegator.makeValue("Content");
				String contentId = delegator.getNextSeqId("Content");
				// added prefix for attachment Id
				contentId = "AT-" + contentId;
				// ended
				content.set("contentId", contentId);
				content.set("dataResourceId", dataResourceId);
				content.set("classificationEnumId", classificationEnumId);
				content.set("contentName", url);
				content.set("contentTypeId", "HYPERLINK");
				content.set("description", attachmentDescription);
				content.set("domainEntityId", domainEntityId);
				content.set("domainEntityType", domainEntityType);
				content.set("linkedFrom", linkedFrom);
				content.set("createdDate", UtilDateTime.nowTimestamp());
				content.set("createdByUserLogin", userLogin.getString("userLoginId"));
				content.create();

				if (UtilValidate.isNotEmpty(partyId)) {
					GenericValue partyContent = delegator.makeValue("PartyContent");
					partyContent.set("contentId", contentId);
					partyContent.set("partyId", partyId);
					partyContent.set("partyContentTypeId", "USERDEF");
					partyContent.set("fromDate", UtilDateTime.nowTimestamp());
					partyContent.create();
				}
				if (UtilValidate.isNotEmpty(salesOpportunityId)) {
					GenericValue opporContent = delegator.makeValue("OpportunityContent");
					opporContent.set("contentId", contentId);
					opporContent.set("salesOpportunityId", salesOpportunityId);
					opporContent.set("contentTypeId", "HYPERLINK");
					opporContent.set("fromDate", UtilDateTime.nowTimestamp());
					opporContent.create();
				}
				if (UtilValidate.isNotEmpty(custRequestId)) {
					GenericValue srContent = delegator.makeValue("CustRequestContent");
					srContent.set("contentId", contentId);
					srContent.set("custRequestId", custRequestId);
					srContent.set("contentTypeId", "HYPERLINK");
					srContent.set("fromDate", UtilDateTime.nowTimestamp());
					srContent.create();
					
					eventDescription = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_BOOKMARK_CREATE_EVENT_DESC", "New bookmark added by {1} for this service ({0})");
					String eventUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_ATTACHEMENT_EVENT_URL", "");
					if(eventDescription.contains("{0}") || eventDescription.contains("{1}")) {
						eventDescription = MessageFormat.format(eventDescription, new Object[] { custRequestId , userLoginName});
					}
					if(UtilValidate.isNotEmpty(eventUrl)) {
						if(eventUrl.contains("{0}")) {
							eventUrl = MessageFormat.format(eventUrl, new Object[] { custRequestId });
						}
						viewUrl = eventUrl;
					} else {
						String serverRootUrl = UtilValidate.isNotEmpty(request) ? UtilHttp.getServerRootUrl(request) : "";
						String contextPath =  UtilValidate.isNotEmpty(request) ? request.getContextPath() : "";
						
						if(UtilValidate.isNotEmpty(serverRootUrl) && UtilValidate.isNotEmpty(contextPath)) {
							viewUrl = serverRootUrl+"/" +path+"/control/viewServiceRequest?srNumber="+custRequestId;
						}
					}
					
					eventMap.put("entityId", custRequestId);
					eventMap.put("entityName", "CustRequestContent");
					eventMap.put("eventType", "ATTACHMENTS");
					
					eventMap.put("eventDate", org.ofbiz.base.util.UtilDateTime.nowTimestamp());
					eventMap.put("eventName", eventName);
					eventMap.put("eventDescription", eventDescription);
					eventMap.put("eventUrl", viewUrl);
					eventMap.put("domainEntityId", domainEntityId);
					eventMap.put("domainEntityType", domainEntityType);
					eventMap.put("userLogin", userLogin);
					dispatcher.runSync("notificationEventRegister", eventMap);
					
				}
				if (UtilValidate.isNotEmpty(workEffortId)) {
					GenericValue activityContent = delegator.makeValue("WorkEffortContent");
					activityContent.set("contentId", contentId);
					activityContent.set("workEffortId", workEffortId);
					activityContent.set("workEffortContentTypeId", "ACTIVITY_HYPERLINK");
					activityContent.set("fromDate", UtilDateTime.nowTimestamp());
					// activityContent.set("helpfulLink", helpfulLink);
					activityContent.create();
					
					
					eventDescription = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_BOOKMARK_CREATE_EVENT_DESC", "New bookmark added by {1} for this activity ({0})");
					String eventUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_ATTACHEMENT_EVENT_URL", "");
					if(eventDescription.contains("{0}") || eventDescription.contains("{1}")) {
						eventDescription = MessageFormat.format(eventDescription, new Object[] { workEffortId , userLoginName});
					}
					if(UtilValidate.isNotEmpty(eventUrl)) {
						if(eventUrl.contains("{0}")) {
							eventUrl = MessageFormat.format(eventUrl, new Object[] { custRequestId });
						}
						viewUrl = eventUrl;
					} else {
						String serverRootUrl = UtilValidate.isNotEmpty(request) ? UtilHttp.getServerRootUrl(request) : "";
						String contextPath =  UtilValidate.isNotEmpty(request) ? request.getContextPath() : "";
						
						if(UtilValidate.isNotEmpty(serverRootUrl) && UtilValidate.isNotEmpty(contextPath)) {
							viewUrl = serverRootUrl+"/" +path+"/control/viewActivity?workEffortId="+workEffortId;
						}
					}
					
					eventMap.put("entityId", workEffortId);
					eventMap.put("entityName", "WorkEffortContent");
					eventMap.put("eventType", "ATTACHMENTS");
					
					eventMap.put("eventDate", org.ofbiz.base.util.UtilDateTime.nowTimestamp());
					eventMap.put("eventName", eventName);
					eventMap.put("eventDescription", eventDescription);
					eventMap.put("eventUrl", viewUrl);
					eventMap.put("domainEntityId", domainEntityId);
					eventMap.put("domainEntityType", domainEntityType);
					eventMap.put("userLogin", userLogin);
					dispatcher.runSync("notificationEventRegister", eventMap);
				}

				if (UtilValidate.isNotEmpty(domainEntityId) && UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.COMMON_ATTACHMENT_ENTITY_TYPE.containsKey(domainEntityType)) {
					GenericValue commonContent = delegator.makeValue("CommonContent");
					commonContent.set("contentId", contentId);
					commonContent.set("domainEntityId", domainEntityId);
					commonContent.set("domainEntityType", domainEntityType);
					commonContent.set("contentTypeId", "HYPERLINK");
					commonContent.set("fromDate", UtilDateTime.nowTimestamp());
					commonContent.create();
				}
				
				result.put("contentId", contentId);

				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully created BookMarkUrl");
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}

			if(UtilValidate.isNotEmpty(domainEntityId) || UtilValidate.isNotEmpty(custRequestId)) {
				String custReqId = UtilValidate.isNotEmpty(domainEntityId) ? domainEntityId : custRequestId;
				GenericValue custReq = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custReqId).queryFirst();
				if(UtilValidate.isNotEmpty(custReq)) {
					custReq.set("lastModifiedDate", UtilDateTime.nowTimestamp());
					custReq.store();
				}
			}
			if(UtilValidate.isNotEmpty(workEffortId)) {
				GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryFirst();
				if(UtilValidate.isNotEmpty(workEffort)) {
					workEffort.set("lastModifiedDate", UtilDateTime.nowTimestamp());
					workEffort.store();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String createContentAssociation(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String contentId = request.getParameter("contentId");
		
		String isThirdPartyAttachment = request.getParameter("isThirdPartyAttachment");
		String classificationEnumId = request.getParameter("classificationEnumId");
		String invoiceAmount = request.getParameter("invoiceAmount");

		Map<String, Object> result = FastMap.newInstance();

		try {
			if (UtilValidate.isNotEmpty(isThirdPartyAttachment) && isThirdPartyAttachment.equals("Y")) {
				UtilAttribute.storeContentAttrValue(delegator, contentId, "INV_AMT", invoiceAmount);
			}
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			result.put(ModelService.SUCCESS_MESSAGE, "Content association done successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String searchAttachments(HttpServletRequest request, HttpServletResponse response) {
		Debug.log("searchAttachments=====searchAttachments====");

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		
		String partyId = request.getParameter("partyId");
		String salesOpportunityId = request.getParameter("salesOpportunityId");
		String custRequestId = request.getParameter("custRequestId");
		String classificationEnumTypes = request.getParameter("classificationEnumTypes");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");

		String workEffortId = request.getParameter("workEffortId");
		String userLoginId = userLogin.getString("userLoginId");

		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> classifications = FastMap.newInstance();
		Map<String, Object> contTypeMap = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);

			List<EntityCondition> conditionlist = FastList.newInstance();
			List<String> contentIds = new ArrayList<String>();
			Debug.log("partyId================search=================" + partyId);
			if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
				conditionlist.add(
						EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, domainEntityId));
				conditionlist.add(EntityCondition.makeCondition("contentTypeId", EntityOperator.IN,
						UtilMisc.toList("PARTY_ATTACHMENT_DATA", "HYPERLINK", "EMAIL_ATTACHMENT_DATA")));
				conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

				EntityCondition oppoCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> oppoContentDataList = delegator.findList("OpportunityContent", oppoCondition, null,
						null, null, false);

				if (UtilValidate.isNotEmpty(oppoContentDataList)) {
					contentIds = EntityUtil.getFieldListFromEntityList(oppoContentDataList, "contentId", true);
				}
				
				// get activity attachment
				List<GenericValue> workEfforts = EntityQuery.use(delegator).from("WorkEffort")
						.where("domainEntityType", domainEntityType, "domainEntityId", domainEntityId).queryList();
				List<String> workEffortIds = UtilValidate.isNotEmpty(workEfforts)
						? EntityUtil.getFieldListFromEntityList(workEfforts, "workEffortId", true)
								: new ArrayList<String>();
						if (UtilValidate.isNotEmpty(workEffortIds)) {
							conditionlist.clear();
							conditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
							conditionlist.add(EntityCondition.makeCondition("workEffortContentTypeId", EntityOperator.IN,
									UtilMisc.toList("ACTIVITY_ATTACHMENT_DATA", "ACTIVITY_HYPERLINK", "EMAIL_ATTACHMENT_DATA")));
							conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

							EntityCondition workEffortCondition = EntityCondition.makeCondition(conditionlist,
									EntityOperator.AND);
							List<GenericValue> activityContentDataList = delegator.findList("WorkEffortContent",
									workEffortCondition, null, null, null, false);

							if (UtilValidate.isNotEmpty(activityContentDataList)) {
								List<String> activityContentIds = EntityUtil.getFieldListFromEntityList(activityContentDataList,
										"contentId", true);
								if (UtilValidate.isNotEmpty(activityContentIds))
									contentIds.addAll(activityContentIds);
							}
						}

			} else if (UtilValidate.isNotEmpty(domainEntityType)
					&& CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
				conditionlist
				.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, domainEntityId));
				conditionlist.add(EntityCondition.makeCondition("contentTypeId", EntityOperator.IN,
						UtilMisc.toList("SR_ATTACHMENT_DATA", "HYPERLINK", "EMAIL_ATTACHMENT_DATA")));
				conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

				EntityCondition custReqCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> srContentDataList = delegator.findList("CustRequestContent", custReqCondition, null,
						null, null, false);

				if (UtilValidate.isNotEmpty(srContentDataList)) {
					contentIds = EntityUtil.getFieldListFromEntityList(srContentDataList, "contentId", true);
				}

				// get activity attachment
				List<GenericValue> workEfforts = EntityQuery.use(delegator).from("WorkEffort")
						.where("domainEntityType", domainEntityType, "domainEntityId", domainEntityId).queryList();
				List<String> workEffortIds = UtilValidate.isNotEmpty(workEfforts)
						? EntityUtil.getFieldListFromEntityList(workEfforts, "workEffortId", true)
								: new ArrayList<String>();
						if (UtilValidate.isNotEmpty(workEffortIds)) {
							conditionlist.clear();
							conditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
							conditionlist.add(EntityCondition.makeCondition("workEffortContentTypeId", EntityOperator.IN,
									UtilMisc.toList("ACTIVITY_ATTACHMENT_DATA", "ACTIVITY_HYPERLINK", "EMAIL_ATTACHMENT_DATA")));
							conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

							EntityCondition workEffortCondition = EntityCondition.makeCondition(conditionlist,
									EntityOperator.AND);
							List<GenericValue> activityContentDataList = delegator.findList("WorkEffortContent",
									workEffortCondition, null, null, null, false);

							if (UtilValidate.isNotEmpty(activityContentDataList)) {
								List<String> activityContentIds = EntityUtil.getFieldListFromEntityList(activityContentDataList,
										"contentId", true);
								if (UtilValidate.isNotEmpty(activityContentIds))
									contentIds.addAll(activityContentIds);
							}
						}

			} else if (UtilValidate.isNotEmpty(domainEntityType)
					&& domainEntityType.equals(DomainEntityType.CLIENT_SERVICE_REQUEST)) {
				conditionlist
				.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, domainEntityId));
				conditionlist.add(EntityCondition.makeCondition("contentTypeId", EntityOperator.IN,
						UtilMisc.toList("SR_ATTACHMENT_DATA", "HYPERLINK","EMAIL_ATTACHMENT_DATA")));
				conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

				EntityCondition custReqCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> srContentDataList = delegator.findList("CustRequestContent", custReqCondition, null,
						null, null, false);

				if (UtilValidate.isNotEmpty(srContentDataList)) {
					contentIds = EntityUtil.getFieldListFromEntityList(srContentDataList, "contentId", true);
				}

			} else if (UtilValidate.isNotEmpty(workEffortId) && UtilValidate.isNotEmpty(domainEntityType)
					&& domainEntityType.equals(DomainEntityType.ACTIVITY)) {
				conditionlist.clear();
				conditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
				conditionlist.add(EntityCondition.makeCondition("workEffortContentTypeId", EntityOperator.IN,
						UtilMisc.toList("ACTIVITY_ATTACHMENT_DATA", "ACTIVITY_HYPERLINK", "EMAIL_ATTACHMENT_DATA")));
				conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

				EntityCondition workEffortCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> activityContentDataList = delegator.findList("WorkEffortContent",
						workEffortCondition, null, null, null, false);

				if (UtilValidate.isNotEmpty(activityContentDataList)) {
					contentIds = EntityUtil.getFieldListFromEntityList(activityContentDataList, "contentId", true);
				}

			} else if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
				conditionlist.clear();
				conditionlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionlist.add(EntityCondition.makeCondition("partyContentTypeId", EntityOperator.IN,
						UtilMisc.toList("PARTY_ATTACHMENT_DATA", "USERDEF","EMAIL_ATTACHMENT_DATA")));
				conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				
				EntityCondition PartyContcondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> PartyContentDataList = delegator.findList("PartyContent", PartyContcondition, null,
						null, null, false);
				
				if (UtilValidate.isNotEmpty(PartyContentDataList)) {
					contentIds = EntityUtil.getFieldListFromEntityList(PartyContentDataList, "contentId", true);
				}
			} else if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.COMMON_ATTACHMENT_ENTITY_TYPE.containsKey(domainEntityType)) {
				conditionlist.clear();
				conditionlist.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
				conditionlist.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
				conditionlist.add(EntityCondition.makeCondition("contentTypeId", EntityOperator.IN,
						UtilMisc.toList("ATTACHMENT", "HYPERLINK", "EMAIL_ATTACHMENT_DATA")));
				conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

				EntityCondition contentContcondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> contentDataList = delegator.findList("CommonContent", contentContcondition, null,
						null, null, false);

				if (UtilValidate.isNotEmpty(contentDataList)) {
					contentIds = EntityUtil.getFieldListFromEntityList(contentDataList, "contentId", true);
				}
			}

			if (UtilValidate.isNotEmpty(contentIds)) {
				
				conditionlist.clear();
				conditionlist.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CONTENT_CLASS"));
				EntityCondition enumCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				
				conditionlist.clear();
				EntityCondition contTypeCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> contTypeList = delegator.findList("ContentType", enumCondition, null, null, null, false);
				if (UtilValidate.isNotEmpty(contTypeList)) {
					for (GenericValue entry : contTypeList) {
						contTypeMap.put(entry.getString("contentTypeId"), entry.getString("description"));
					}
				}
				
				conditionlist.clear();
				conditionlist.add(EntityCondition.makeCondition("contentTypeId", EntityOperator.IN,
						UtilMisc.toList("ATTACHMENT", "HYPERLINK","MAIL_ATTACHMENT")));
				conditionlist.add(EntityCondition.makeCondition("contentId", EntityOperator.IN, contentIds));
				
				if (UtilValidate.isNotEmpty(classificationEnumTypes)) {
					List<GenericValue> classificationEnums = new ArrayList<>();
					for (String enumTypeId : classificationEnumTypes.split(",")) {
						classificationEnums.addAll(EnumUtil.getEnableEnums(delegator, enumTypeId));
					}
					List<String> classificationEnumIds = EntityUtil.getFieldListFromEntityList(classificationEnums, "enumId", true);
					conditionlist.add(EntityCondition.makeCondition("classificationEnumId", EntityOperator.IN, classificationEnumIds));
				}
				
				List<String> orderBy = UtilMisc.toList("createdTxStamp DESC");
				EntityCondition Contentcondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);

				List<GenericValue> contentDataList = delegator.findList("Content", Contentcondition, null, orderBy, null, false);
				
				Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
				
				classifications.putAll(EnumUtil.getEnumList(delegator, contentDataList, "classificationEnumId", "CONTENT_CLASS"));
				Map<String, Object> thirdPartyClassifications = EnumUtil.getEnumList(delegator, contentDataList, "classificationEnumId", "THIRDPTY_CONTENT_CLASS");
				thirdPartyClassifications.entrySet().removeIf(entry -> UtilValidate.isEmpty(entry.getValue()));
				if(UtilValidate.isNotEmpty(thirdPartyClassifications)) {
					classifications.putAll(thirdPartyClassifications);
				}
				//classifications.putAll(EnumUtil.getEnumList(delegator, contentDataList, "classificationEnumId", "THIRDPTY_CONTENT_CLASS"));
				
				Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);

				if (UtilValidate.isNotEmpty(contentDataList)) {
					List<String> nonImageFormat = new ArrayList<>();
					nonImageFormat.add("wmv");
					nonImageFormat.add("webm");
					nonImageFormat.add("mp4");
					nonImageFormat.add("mpg");
					nonImageFormat.add("mpeg");
					nonImageFormat.add("m4v");
					nonImageFormat.add("mov");
					nonImageFormat.add("3gp");
					nonImageFormat.add("3gpp");
					nonImageFormat.add("pdf");
					nonImageFormat.add("xlsx");
					nonImageFormat.add("docx");
					nonImageFormat.add("xls");
					nonImageFormat.add("doc");
					nonImageFormat.add("csv");

					nonImageFormat.add("tiff");
					nonImageFormat.add("tif");
					nonImageFormat.add("heic");
					nonImageFormat.add("heif");
					String mountPointLoc = "/common-portal-resource/images/temp/" + userLoginId + "/";
					String bootstrapImageLoc = "/bootstrap/images/";
					String commonPortalImageTempLoc = ComponentConfig.getRootLocation("common-portal") + "webapp" + mountPointLoc;

					File dir = new File(commonPortalImageTempLoc);
					try {
						dir.setExecutable(true);
						dir.setWritable(true);
						dir.setReadable(true);
						if (!dir.exists()){
							dir.mkdirs();
						}
						FileUtils.cleanDirectory(dir);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} 
					boolean isReadOnly = org.fio.homeapps.util.DataUtil.hasPermissionWoFullPerm(request, "READ_ONLY_PERM");
					int seq = 1;
					for (GenericValue entry : contentDataList) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("contentId", entry.getString("contentId"));
						data.put("contentType", entry.getString("contentTypeId"));
						data.put("contentTypeId",
								UtilValidate.isNotEmpty(contTypeMap.get(entry.getString("contentTypeId")))
								? contTypeMap.get(entry.getString("contentTypeId"))
										: entry.getString("contentTypeId"));
						data.put("contentName", entry.getString("contentName"));
						if (UtilValidate.isNotEmpty(entry.getString("classificationEnumId"))) {
							data.put("classificationDescription", classifications.get(entry.getString("classificationEnumId")));
						} else {
							data.put("classificationDescription", "");
						}
						data.put("description", entry.getString("description"));

						GenericValue PartyContent = EntityUtil.getFirst(delegator.findByAnd("PartyContent",
								UtilMisc.toMap("contentId", entry.getString("contentId")), null, false));

						if (UtilValidate.isNotEmpty(PartyContent)) {
							data.put("partyId", PartyContent.getString("partyId"));
						}

						data.put("domainEntityId", entry.getString("domainEntityId"));
						data.put("domainEntityType", entry.getString("domainEntityType"));
						data.put("domainEntityTypeDesc", org.groupfio.common.portal.util.DataHelper
								.convertToLabel(entry.getString("domainEntityType")));
						data.put("linkedFrom", entry.getString("linkedFrom"));
						data.put("isReadOnly", isReadOnly ? "Y" : "N");
						String dataResourceId = entry.getString("dataResourceId");
						if (UtilValidate.isNotEmpty(dataResourceId)) {

							GenericValue dataResource = EntityQuery.use(delegator).from("DataResource")
									.where("dataResourceId", dataResourceId).queryFirst();
							if (UtilValidate.isNotEmpty(dataResource)) {
								String filePath = dataResource.getString("objectInfo");
								File image = new File(filePath);
								String fileName = image.isFile() ? image.getName() : "";
								String fileExtension = org.fio.admin.portal.util.DataUtil.getFileExtension(fileName);
								if (nonImageFormat.contains(fileExtension)) {
									//image = new File(bootstrapImageLoc + fileExtension + ".png");
									data.put("imageUrl", bootstrapImageLoc + fileExtension + ".png");
								} else if (image.exists()) {
									
									String fileRelativePath = commonPortalImageTempLoc + dataResourceId+"."+fileExtension;
									File desImage = new File(fileRelativePath);
									FileUtils.copyFile(image, desImage);
									
									
									data.put("imageUrl", mountPointLoc+ dataResourceId+"."+fileExtension);
									/*
									byte[] fileContent = Files.readAllBytes(image.toPath());
									String encodedImage = java.util.Base64.getEncoder().encodeToString(fileContent);
									data.put("imageEncoded", "data:image/png;base64," + encodedImage); */
								} else {
									data.put("imageUrl", bootstrapImageLoc + "default-product-img.png");
								}
							}
						}

						data.put("createdDate",
								UtilValidate.isNotEmpty(entry.get("createdDate"))
								? UtilDateTime.timeStampToString(entry.getTimestamp("createdDate"),
										globalDateFormat, TimeZone.getDefault(), null)
										: "");
						data.put("createdByName",
								PartyHelper.getPartyName(delegator, org.fio.homeapps.util.DataUtil
										.getPartyIdByUserLoginId(delegator, entry.getString("createdByUserLogin")),
										false));

						String attrValue = DataUtil.getContentAttribute(delegator, entry.getString("contentId"), "IS_PUBLIC");
						if (UtilValidate.isNotEmpty(attrValue)) {
							if ("N".equals(attrValue))
								data.put("attachmentType", "Private");
							if ("Y".equals(attrValue))
								data.put("attachmentType", "Public");
						}
						
						String invoiceAmount = DataUtil.getContentAttribute(delegator, entry.getString("contentId"), "INV_AMT");
						data.put("invoiceAmount", invoiceAmount);
						
						data.put("downloadCol", "downloadCol_"+seq);

						data.put("imageCol", "imageCol_"+seq);
						
						if (UtilValidate.isNotEmpty(attrValue) && "N".equals(attrValue)) {
							String createdBy = entry.getString("createdByUserLogin");
							List<String> attachmentRolesList = new ArrayList<>();
							String attachmentRoles = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator,
									"PRIVATE_ATTACHMENT_ROLES");
							if (UtilValidate.isNotEmpty(attachmentRoles)) {
								if (UtilValidate.isNotEmpty(attachmentRoles) && attachmentRoles.contains(",")) {
									attachmentRolesList = org.fio.admin.portal.util.DataUtil
											.stringToList(attachmentRoles, ",");
								} else
									attachmentRolesList.add(attachmentRoles);
							}
							Map<String, Object> userLoginInfo = org.fio.homeapps.util.DataUtil
									.getUserLoginInfo(delegator, userLoginId);
							String userLoginRoleTypeId = (String) userLoginInfo.get("roleTypeId");
							if ((UtilValidate.isNotEmpty(userLoginRoleTypeId)
									&& attachmentRolesList.contains(userLoginRoleTypeId)) ||createdBy.equals(userLoginId)) {
								dataList.add(data);
							}
						} else {
							dataList.add(data);
						}

						seq +=1;
					}

				}

				result.put("data", dataList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);

	}

	public static String removeAttachmentData(HttpServletRequest request, HttpServletResponse response) {
		Debug.log("removeAttachmentData================");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		String requestData = org.fio.admin.portal.util.DataUtil.getJsonStrBody(request);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		// String selectedattachmentIds =
		// request.getParameter("selectedattachmentIds");

		Map<String, Object> result = FastMap.newInstance();
		String contentId = request.getParameter("contentId");
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		try {
			List<Map<String, Object>> dataList = org.fio.admin.portal.util.DataUtil.convertToListMap(requestData);
			if(UtilValidate.isEmpty(dataList)) {
				Map<String, Object> results = new HashMap<>();
				if(UtilValidate.isEmpty(contentId)) {
					result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					result.put(ModelService.ERROR_MESSAGE, "content Id is empty");
					return AjaxEvents.doJSONResponse(response, result);
				}
				results.put("domainEntityType", domainEntityType);
				results.put("contentId", contentId);
				results.put("domainEntityId", domainEntityId);
				
				dataList.add(results);
			}
			//List<GenericValue> toBeRemove = new ArrayList<GenericValue>();
			List<EntityCondition> conditionlist = FastList.newInstance();
			for (Map<String, Object> data : dataList) {
				contentId = (String) data.get("contentId");
				domainEntityType = (String) data.get("domainEntityType");
				domainEntityId = (String) data.get("domainEntityId");

				GenericValue partyContent = EntityUtil.getFirst(
						delegator.findByAnd("PartyContent", UtilMisc.toMap("contentId", contentId), null, false));
				if (UtilValidate.isNotEmpty(partyContent)) {
					partyContent.set("thruDate", UtilDateTime.nowTimestamp());
					delegator.createOrStore(partyContent);
				}
				GenericValue custReqContent = EntityUtil.getFirst(
						delegator.findByAnd("CustRequestContent", UtilMisc.toMap("contentId", contentId), null, false));
				if (UtilValidate.isNotEmpty(custReqContent)) {
					custReqContent.set("thruDate", UtilDateTime.nowTimestamp());
					delegator.createOrStore(custReqContent);
				}
				GenericValue OppoContent = EntityUtil.getFirst(
						delegator.findByAnd("OpportunityContent", UtilMisc.toMap("contentId", contentId), null, false));
				if (UtilValidate.isNotEmpty(OppoContent)) {
					OppoContent.set("thruDate", UtilDateTime.nowTimestamp());
					delegator.createOrStore(OppoContent);
				}
				GenericValue workEffortContent = EntityUtil.getFirst(
						delegator.findByAnd("WorkEffortContent", UtilMisc.toMap("contentId", contentId), null, false));
				if (UtilValidate.isNotEmpty(workEffortContent)) {
					workEffortContent.set("thruDate", UtilDateTime.nowTimestamp());
					delegator.createOrStore(workEffortContent);
				}

				if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.COMMON_ATTACHMENT_ENTITY_TYPE.containsKey(domainEntityType)) {
					conditionlist.clear();
					conditionlist.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
					conditionlist.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
					conditionlist.add(EntityCondition.makeCondition("contentTypeId", EntityOperator.IN,
							UtilMisc.toList("ATTACHMENT", "HYPERLINK")));
					conditionlist.add(EntityCondition.makeCondition("contentId", EntityOperator.EQUALS, contentId));

					EntityCondition contentContcondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
					GenericValue contentData = EntityQuery.use(delegator).from("CommonContent").where(contentContcondition).queryFirst();
					if (UtilValidate.isNotEmpty(contentData)) {
						contentData.set("thruDate", UtilDateTime.nowTimestamp());
						delegator.createOrStore(contentData);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Attachment successfully removed");
		return AjaxEvents.doJSONResponse(response, result);

	}

	public static String getUsersList(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String isIncludeLoggedInUser = (String) context.get("isIncludeLoggedInUser");
		String isIncludeInactiveUser = (String) context.get("isIncludeInactiveUser");
		String activeTeamMember = (String) context.get("activeTeamMember");
		String removeDuplicateUsers = (String) context.get("removeDuplicateUsers") != null ? (String) context.get("removeDuplicateUsers") : "N";
		Set<String> addedUserLoginIds = new HashSet<>();
		try {
			Set<String> fieldToSelect = new TreeSet<String>();
			fieldToSelect.add("partyId");
			fieldToSelect.add("userLoginId");
			fieldToSelect.add("firstName");
			fieldToSelect.add("lastName");
			fieldToSelect.add("businessUnit");
			fieldToSelect.add("roleTypeId");
			fieldToSelect.add("roleDescription");
			String roleTypeId = UtilValidate.isNotEmpty(context.get("roleTypeId")) ? (String) context.get("roleTypeId")
					: "SALES_REP";
			List<String> roles = new ArrayList<>();
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				String globalConfig = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, roleTypeId);
				if (UtilValidate.isNotEmpty(globalConfig) && globalConfig.contains(",")) {
					roles = org.fio.admin.portal.util.DataUtil.stringToList(globalConfig, ",");
				} else if (UtilValidate.isNotEmpty(globalConfig)) {
					roles.add(globalConfig);
				} else {
					if (roleTypeId.contains(",")) {
						roles = org.fio.admin.portal.util.DataUtil.stringToList(roleTypeId, ",");
					} else
						roles.add(roleTypeId);
				}
			}

			/*List<GenericValue> partyRoleList = EntityQuery.use(delegator).from("PartyRole")
					.where(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roles)).queryList();*/

			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("PR", "PartyRole");
			dynamicViewEntity.addAlias("PR", "partyId");
			dynamicViewEntity.addAlias("PR", "roleTypeId");
			
			dynamicViewEntity.addMemberEntity("P", "Person");
			dynamicViewEntity.addAlias("P", "firstName");
			dynamicViewEntity.addAlias("P", "lastName");
			dynamicViewEntity.addAlias("P", "partyId");
			dynamicViewEntity.addAlias("P", "businessUnit");
			dynamicViewEntity.addViewLink("PR", "P", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
			
			dynamicViewEntity.addMemberEntity("UL", "UserLogin");
			dynamicViewEntity.addAlias("UL", "userLoginId");
			dynamicViewEntity.addAlias("UL", "enabled");
			dynamicViewEntity.addAlias("UL", "partyId");
			dynamicViewEntity.addViewLink("PR", "UL", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
			
			dynamicViewEntity.addMemberEntity("RT", "RoleType");
			dynamicViewEntity.addAlias("RT", "roleTypeId");
			dynamicViewEntity.addAlias("RT", "roleDescription", "description",null,Boolean.FALSE,Boolean.FALSE,null);
			dynamicViewEntity.addViewLink("PR", "RT", Boolean.TRUE, ModelKeyMap.makeKeyMapList("roleTypeId"));
			
			List conditions = FastList.newInstance();
			if (UtilValidate.isEmpty(isIncludeLoggedInUser)) {
				conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.NOT_EQUAL, userLogin.getString("userLoginId")));
			}
			conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roles));
			if (UtilValidate.isNotEmpty(isIncludeInactiveUser) && isIncludeInactiveUser.equals("Y")) {
				
			}else {
				conditions.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
			}
			
			if(UtilValidate.isNotEmpty(activeTeamMember) && "Y".equals(activeTeamMember)) {
				List<String> activeTeamMembers = UtilCampaign.getCsrList(delegator, null, true);
				if(UtilValidate.isNotEmpty(activeTeamMembers)) {
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, activeTeamMembers));
				}
			}
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			List<GenericValue> partyRoleList = EntityQuery.use(delegator).select(fieldToSelect).from(dynamicViewEntity).where(condition).queryList();
				if (UtilValidate.isNotEmpty(partyRoleList)) {
					partyRoleList.stream()
					.filter(partyRole -> !"Y".equals(removeDuplicateUsers) || addedUserLoginIds.add(partyRole.getString("userLoginId")))
					.map(partyRole -> {
						String partyId = partyRole.getString("partyId");
						String partyRoleTypeId = partyRole.getString("roleTypeId");
						String userLoginId = partyRole.getString("userLoginId");
						String firstName = partyRole.getString("firstName");
						String lastName = partyRole.getString("lastName");
						String roleDesc = partyRole.getString("roleDescription");
						roleDesc = UtilValidate.isNotEmpty(roleDesc) ? roleDesc : partyRoleTypeId;
						String userName = firstName + (UtilValidate.isNotEmpty(lastName) ? " " + lastName : "");
						Map<String, Object> data = new HashMap<>();
						data.put("userLoginId", userLoginId);
						data.put("partyId", partyId);
						data.put("userName", userName);
						data.put("roleDesc", roleDesc);
						String emailAddress = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, partyId, "PRIMARY_EMAIL");
						data.put("emailAddress", UtilValidate.isNotEmpty(emailAddress) ? emailAddress : "");
						return data;
					})
					.forEach(results::add);
				}
			
			//Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getStateDataJSON(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String countryGeoId = request.getParameter("countryGeoId");

		try {
			Collection<GenericValue> states = CommonWorkers.getAssociatedStateList(delegator, countryGeoId);
			return doJSONResponse(response, states);
		} catch (Exception e) {
			return doJSONResponse(response, FastList.newInstance());
		}
	}

	@SuppressWarnings("unchecked")
	public static String getPartyDataSource(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String partyId = request.getParameter("partyId");
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				GenericValue partyDataSource = EntityUtil.getFirst(delegator.findByAnd("PartyDataSource",
						UtilMisc.toMap("partyId", partyId), UtilMisc.toList("fromDate DESC"), false));
				if (partyDataSource != null && partyDataSource.size() > 0) {
					String dataSourceId = partyDataSource.getString("dataSourceId");
					if (UtilValidate.isNotEmpty(dataSourceId)) {
						GenericValue dataSource = delegator.findOne("DataSource",
								UtilMisc.toMap("dataSourceId", dataSourceId), false);
						if (dataSource != null && dataSource.size() > 0) {
							Map<String, Object> inputMap = new HashMap<String, Object>();
							inputMap.put("dataSourceId", dataSourceId);
							inputMap.put("selected", true);
							inputMap.put("description", dataSource.getString("description"));
							results.add(inputMap);
						}
					}
				}
			}
			List<GenericValue> sourceList = EntityQuery.use(delegator).select("dataSourceId", "description")
					.from("DataSource").queryList();
			for (GenericValue each : sourceList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String dataSourceId = each.getString("dataSourceId");
				data.put("dataSourceId", dataSourceId);
				data.put("selected", false);
				data.put("description", each.getString("description"));
				results.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		result.put("partyDataSource", results);
		return doJSONResponse(response, result);
	}

	public static String getPartyCurrencyUom(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> contactMapList = new ArrayList<Map<String, Object>>();
		String partyId = request.getParameter("partyId");
		Locale locale = UtilHttp.getLocale(request);

		try {
			List<GenericValue> partySummaryDetailsViewGv = delegator.findByAnd("PartySummaryDetailsView",
					UtilMisc.toMap("partyId", partyId), UtilMisc.toList("createdDate DESC"), false);
			if (UtilValidate.isNotEmpty(partySummaryDetailsViewGv)) {
				GenericValue partySummaryDetails = EntityUtil.getFirst((partySummaryDetailsViewGv));
				if (UtilValidate.isNotEmpty(partySummaryDetails)) {
					GenericValue uom = delegator.findOne("Uom",
							UtilMisc.toMap("uomId", partySummaryDetails.getString("preferredCurrencyUomId")), false);
					if (uom != null && uom.size() > 0) {
						Map<String, Object> inputMap = new HashMap<String, Object>();
						inputMap.put("uomId", partySummaryDetails.getString("preferredCurrencyUomId"));
						inputMap.put("selected", true);
						inputMap.put("description", uom.getString("description"));
						results.add(inputMap);
					}
				}
			}
			List<GenericValue> sourceList = EntityQuery.use(delegator).select("uomId", "description").from("Uom")
					.where("uomTypeId", "CURRENCY_MEASURE").queryList();
			for (GenericValue each : sourceList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String dataSourceId = each.getString("uomId");
				data.put("uomId", dataSourceId);
				data.put("selected", false);
				data.put("description", each.getString("description"));
				results.add(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		result.put("partyUomSource", results);
		return doJSONResponse(response, result);
	}

	public static String getPrimaryContacts(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		List<Map<String, String>> toMapList = new ArrayList<Map<String, String>>();
		Map<String, Object> result = ServiceUtil.returnSuccess();

		List<Map<String, Object>> contactMapList = new ArrayList<Map<String, Object>>();
		String partyId = request.getParameter("partyId");
		String toEmailDD = request.getParameter("toEmailDD");
		String isLoadEmail = request.getParameter("isLoadEmail");
		String salesOpportunityId = request.getParameter("salesOpportunityId");
		String isIncludeMainParty = request.getParameter("isIncludeMainParty");
		
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String validRoleTypeId = "";
		try {
			validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("ACCOUNT"), delegator);
			if (UtilValidate.isEmpty(validRoleTypeId))
				validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("LEAD"), delegator);
			if (UtilValidate.isEmpty(validRoleTypeId))
				validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("CONTACT"), delegator);
			if (UtilValidate.isNotEmpty(validRoleTypeId)) {
				Map<String, Object> contactAcctMap = new HashMap<String, Object>();
				contactAcctMap.put("partyIdTo", partyId);
				contactAcctMap.put("partyRoleTypeId", validRoleTypeId);
				Map<String, Object> serResult = dispatcher.runSync("common.getContactAndPartyAssoc", contactAcctMap);

				if (ServiceUtil.isSuccess(serResult)) {
					if (UtilValidate.isNotEmpty(serResult)) {
						List<Object> primaryContactList = FastList.newInstance();
						String selectedPartyId = "";
						primaryContactList = (List<Object>) serResult.get("partyContactAssoc");
						if (UtilValidate.isNotEmpty(salesOpportunityId)) {
							GenericValue salesOppo = EntityUtil
									.getFirst(EntityQuery.use(delegator).select("partyId").from("SalesOpportunityRole")
											.where("salesOpportunityId", salesOpportunityId, "roleTypeId", "CONTACT")
											.queryList());
							if (salesOppo != null && salesOppo.size() > 0) {
								selectedPartyId = salesOppo.getString("partyId");
							}
						}
						for (int i = 0; i < primaryContactList.size(); i++) {
							Map<String, Object> partyContactMap = new HashMap<String, Object>();
							partyContactMap = (Map<String, Object>) primaryContactList.get(i);
							contactMapList.add(partyContactMap);
							Map<String, Object> data = new HashMap<String, Object>();
							String primaryContactStatusId = (String) partyContactMap.get("statusId");
							String primaryContactId = (String) partyContactMap.get("contactId");
							if (UtilValidate.isNotEmpty(selectedPartyId)) {
								if (selectedPartyId.equals(primaryContactId)) {
									partyContactMap.put("selected", true);
								}
							} else {
								if ("PARTY_DEFAULT".equals(primaryContactStatusId)) {
									partyContactMap.put("selected", true);
								}
							}

							if (UtilValidate.isNotEmpty(isLoadEmail) && isLoadEmail.equals("Y")) {
								partyContactMap.put("email", org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, partyId, "PRIMARY_EMAIL"));
							}

							results.add(partyContactMap);

							if (UtilValidate.isNotEmpty(toEmailDD)) {
								if (primaryContactId != null) {
									Map<String, String> primaryContactInformation = PartyPrimaryContactMechWorker
											.getPartyPrimaryContactMechValueMaps(delegator, primaryContactId);
									if (UtilValidate.isNotEmpty(primaryContactInformation)) {
										if ("PARTY_DEFAULT".equals(primaryContactStatusId)) {
											primaryContactInformation.put("selected", "true");
											primaryContactInformation.put("primaryContactId", primaryContactId);
											primaryContactInformation.put("partyName",
													PartyHelper.getPartyName(delegator, primaryContactId, false));
											toMapList.add(primaryContactInformation);
										} else {
											primaryContactInformation.put("primaryContactId", primaryContactId);
											primaryContactInformation.put("partyName",
													PartyHelper.getPartyName(delegator, primaryContactId, false));
											toMapList.add(primaryContactInformation);
										}
									}
								}
							}

						}

					}
					if (UtilValidate.isNotEmpty(toEmailDD)) {
						if (UtilValidate.isNotEmpty(partyId)) {
							if (partyId != null) {
								Map<String, String> primaryContactInformation = PartyPrimaryContactMechWorker
										.getPartyPrimaryContactMechValueMaps(delegator, partyId);
								if (UtilValidate.isNotEmpty(primaryContactInformation)) {
									primaryContactInformation.put("primaryContactId", partyId);
									primaryContactInformation.put("partyName",
											PartyHelper.getPartyName(delegator, partyId, false));
									toMapList.add(primaryContactInformation);
								}
							}
						}
					}

				}
			}
			
			if (UtilValidate.isNotEmpty(isIncludeMainParty) && isIncludeMainParty.equals("Y")) {
				Map<String, Object> partyContactMap = new HashMap<String, Object>();
				partyContactMap.put("contactId", partyId);
				partyContactMap.put("name", PartyHelper.getPartyName(delegator, partyId, false));
				results.add(partyContactMap);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		result.put("partyRelContacts", results);
		if (UtilValidate.isNotEmpty(toEmailDD)) {
			result.put("toMapList", toMapList);
		}
		return doJSONResponse(response, result);
	}

	public static String getCcContactEmailIds(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = ServiceUtil.returnSuccess();

		List<Map<String, Object>> contactMapList = new ArrayList<Map<String, Object>>();
		String partyId = request.getParameter("partyId");
		String ccEmailD = request.getParameter("ccEmailD");
		String salesOpportunityId = request.getParameter("salesOpportunityId");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String validRoleTypeId = "";

		try {
			validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("ACCOUNT"), delegator);
			if (UtilValidate.isEmpty(validRoleTypeId))
				validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("LEAD"), delegator);

			if (UtilValidate.isNotEmpty(validRoleTypeId)) {

				List<GenericValue> partyAndContactMechDataList = null;

				EntityCondition conditionPr = EntityCondition.makeCondition(
						UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
								EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, validRoleTypeId),
								EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)),
						EntityOperator.AND);

				List<GenericValue> relatedPartyiesDataList = EntityQuery.use(delegator).from("PartyRelationship")
						.where(conditionPr).queryList();
				EntityCondition conditionPr1 = EntityCondition.makeCondition(
						UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
								EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)),
						EntityOperator.AND);

				List<GenericValue> rmPartyIdDataList = EntityQuery.use(delegator).from("PartyRelationship")
						.where(conditionPr1).queryList();
				List<String> rmPartyIds = EntityUtil.getFieldListFromEntityList(rmPartyIdDataList, "partyIdTo", true);
				List<String> contactPartyIds = EntityUtil.getFieldListFromEntityList(relatedPartyiesDataList,
						"partyIdFrom", true);

				contactPartyIds.addAll(rmPartyIds);

				List<GenericValue> allRmPartyIdDataList = EntityQuery.use(delegator).from("PartyRole")
						.where(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SALES_REP"))
						.queryList();
				List<String> allRmPartyIds = EntityUtil.getFieldListFromEntityList(allRmPartyIdDataList, "partyId",
						true);

				contactPartyIds.addAll(allRmPartyIds);

				if (UtilValidate.isNotEmpty(relatedPartyiesDataList)) {
					EntityCondition conditionPartyCtMech = EntityCondition.makeCondition(
							UtilMisc.toList(
									EntityCondition.makeCondition("partyId", EntityOperator.IN, contactPartyIds),
									EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS,
											"EMAIL_ADDRESS"),
									EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)),
							EntityOperator.AND);

					List<String> cCEmailIds = FastList.newInstance();

					partyAndContactMechDataList = EntityQuery.use(delegator).from("PartyAndContactMech")
							.where(conditionPartyCtMech).queryList();

					for (int i = 0; i < partyAndContactMechDataList.size(); i++) {
						Map<String, Object> partyContactMap = new HashMap<String, Object>();
						GenericValue partyContactGv = partyAndContactMechDataList.get(i);
						Map<String, Object> data = new HashMap<String, Object>();

						if (UtilValidate.isNotEmpty(partyContactGv)) {
							String contactPartyId = (String) partyContactGv.get("partyId");
							String emailId = (String) partyContactGv.get("infoString");
							if (UtilValidate.isEmpty(cCEmailIds)) {
								cCEmailIds.add(emailId);
							}

							if (UtilValidate.isNotEmpty(emailId) && !cCEmailIds.contains(emailId)) {

								cCEmailIds.add(emailId);

								partyContactMap.put("contactPartyId", contactPartyId);
								partyContactMap.put("emailId", emailId);

								results.add(partyContactMap);
							}
						}

					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		result.put("partyRelContacts", results);
		return doJSONResponse(response, result);
	}

	public static String getPartyRM(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> contactMapList = new ArrayList<Map<String, Object>>();
		String partyIdFrom = request.getParameter("partyIdFrom");
		String partyIdTo = request.getParameter("partyIdTo");
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();

			EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RELATIONSHIP_FOR"),
					EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER")));
			conditions.add(roleTypeCondition);
			EntityCondition partyStatusCondition = EntityCondition
					.makeCondition(
							UtilMisc.toList(
									EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,
											"PARTY_DISABLED"),
									EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)),
							EntityOperator.OR);
			conditions.add(partyStatusCondition);
			if (UtilValidate.isNotEmpty(partyIdTo)) {
				conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo));
			}
			if (UtilValidate.isNotEmpty(partyIdFrom)) {
				conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
			}
			conditions.add(EntityUtil.getFilterByDateExpr());
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> partyFromReln = delegator.findList("PartyFromRelnAndParty", mainConditons, null,
					null, null, false);
			if (partyFromReln != null && partyFromReln.size() > 0) {
				List<String> partyRelnId = null;
				if (UtilValidate.isNotEmpty(partyIdFrom)) {
					partyRelnId = EntityUtil.getFieldListFromEntityList(partyFromReln, "partyIdTo", true);
				} else if (UtilValidate.isNotEmpty(partyIdTo)) {
					partyRelnId = EntityUtil.getFieldListFromEntityList(partyFromReln, "partyIdFrom", true);
				} else {
					partyRelnId = EntityUtil.getFieldListFromEntityList(partyFromReln, "partyIdFrom", true);
				}

				if (partyRelnId != null && partyRelnId.size() > 0) {
					for (String partyId : partyRelnId) {
						String contactId = "";
						String assocPartyId = "";
						String name = "";
						String companyName = "";
						GenericValue partySummaryDetailsViewGv = delegator.findOne("PartySummaryDetailsView",
								UtilMisc.toMap("partyId", partyId), false);
						if (UtilValidate.isNotEmpty(partyIdFrom)) {
							contactId = partyIdFrom; // to be validated
							assocPartyId = partyId;
							companyName = partySummaryDetailsViewGv.getString("companyName");
						} else if (UtilValidate.isNotEmpty(partyIdTo)) {
							contactId = partyId;
							assocPartyId = partyIdTo;
							name = partySummaryDetailsViewGv.getString("groupName");
							if (UtilValidate.isNotEmpty(partySummaryDetailsViewGv.getString("lastName"))) {
								name = partySummaryDetailsViewGv.getString("firstName") + " "
										+ partySummaryDetailsViewGv.getString("lastName");
							}
						} else {
							contactId = partyId;
							name = partySummaryDetailsViewGv.getString("groupName");
							if (UtilValidate.isNotEmpty(partySummaryDetailsViewGv.getString("lastName"))) {
								name = partySummaryDetailsViewGv.getString("firstName") + " "
										+ partySummaryDetailsViewGv.getString("lastName");
							}
						}
						GenericValue partyContactViewGv = delegator.findOne("Person",
								UtilMisc.toMap("partyId", contactId), false);
						if (UtilValidate.isNotEmpty(partyContactViewGv)
								&& UtilValidate.isNotEmpty(partyContactViewGv.getString("firstName"))) {
							if (UtilValidate.isNotEmpty(partyContactViewGv.getString("lastName"))) {
								name = partyContactViewGv.getString("firstName") + " "
										+ partyContactViewGv.getString("lastName");
							} else {
								name = partyContactViewGv.getString("firstName");
							}
						}

						Map<String, Object> partyContactMap = new HashMap<String, Object>();

						partyContactMap.put("contactId", contactId);
						partyContactMap.put("partyId", contactId);
						partyContactMap.put("name", name);
						partyContactMap.put("companyName", companyName);
						results.add(partyContactMap);
					}
				}
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		result.put("partyRM", results);
		return doJSONResponse(response, result);
	}

	public static String getPartyOppoOwner(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyIdFrom = (String) context.get("partyIdFrom");
		String partyIdTo = (String) context.get("partyIdTo");
		String partyRoleTypeId = (String) context.get("partyRoleTypeId");

		List<Object> partyContactList = FastList.newInstance();
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();

			EntityCondition roleTypeCondition = EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y")));
			conditions.add(roleTypeCondition);
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> UserLoginPerson = delegator.findList("UserLoginPerson", mainConditons, null, null, null,
					false);
			for (GenericValue eachUser : UserLoginPerson) {

				String userLoginId = eachUser.getString("userLoginId");
				String name = eachUser.getString("firstName") + " " + eachUser.getString("lastName");

				Map<String, Object> partyContactMap = new HashMap<String, Object>();
				partyContactMap.put("partyId", userLoginId);
				partyContactMap.put("name", name);
				partyContactList.add(partyContactMap);

			}
		} catch (Exception e) {
			Debug.logInfo("==========================ERROR======================" + e.toString(), "");
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		result.put("partyContactAssoc", partyContactList);
		return doJSONResponse(response, result);
	}

	public static String getRMUsersList(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		try {
			List<GenericValue> rmPartyIdsList = delegator.findList("PartyRole",
					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_MANAGER"), null, null,
					null, false);
			if (UtilValidate.isNotEmpty(rmPartyIdsList)) {
				List<String> rmPartyIds = EntityUtil.getFieldListFromEntityList(rmPartyIdsList, "partyId", true);
				if (UtilValidate.isNotEmpty(rmPartyIds)) {
					List<GenericValue> personDetailsList = delegator.findList("Person",
							EntityCondition.makeCondition("partyId", EntityOperator.IN, rmPartyIds), null, null, null,
							false);
					if (UtilValidate.isNotEmpty(personDetailsList)) {
						for (GenericValue eachEntry : personDetailsList) {
							Map<String, Object> data = new HashMap<String, Object>();
							data.put("partyId", eachEntry.getString("partyId"));
							data.put("partyName",
									eachEntry.getString("firstName") + " " + eachEntry.getString("lastName"));
							results.add(data);
						}
					}
				}
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getProductData(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String entityId = request.getParameter("entityId");
		String entityIdVal = request.getParameter("entityIdVal");
		String entityName = request.getParameter("entityName");
		String productCatalogId = (String) context.get("prodCatalogId");
		List<String> productIds = FastList.newInstance();
		Map<String, Object> result = FastMap.newInstance();

		try {

			List<Map<String, Object>> pcDataList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> pscDataList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			String selproductCategoryId = "";
			String selproductSubCategoryId = "";
			String selproductId = "";
			GenericValue productData = null;
			if (UtilValidate.isNotEmpty(entityIdVal) && UtilValidate.isNotEmpty(entityId)
					&& UtilValidate.isNotEmpty(entityName)) {
				productData = EntityQuery.use(delegator).select("productCategoryId", "productSubCategoryId", "product")
						.from(entityName).where(entityId, entityIdVal).queryOne();
			}
			if (UtilValidate.isNotEmpty(productCatalogId)) {

				List<EntityCondition> conditionlist = FastList.newInstance();

				conditionlist
				.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, productCatalogId));
				conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> productCatalogList = EntityQuery.use(delegator)
						.select("prodCatalogId", "productCategoryId").from("ProdCatalogCategory").where(condition)
						.orderBy("sequenceNum").queryList();
				if (UtilValidate.isNotEmpty(productData)) {
					if (UtilValidate.isNotEmpty(productData.get("productCategoryId"))) {
						selproductCategoryId = productData.getString("productCategoryId");
					}
				}
				if (UtilValidate.isNotEmpty(productCatalogList)) {
					List<String> productCategoryIds = EntityUtil.getFieldListFromEntityList(productCatalogList,
							"productCategoryId", true);

					for (String prodCategoryId : productCategoryIds) {
						Map<String, Object> data = new HashMap<String, Object>();

						GenericValue category = EntityQuery.use(delegator).select("categoryName")
								.from("ProductCategory").where("productCategoryId", prodCategoryId).queryOne();
						if (UtilValidate.isNotEmpty(category)) {
							if (prodCategoryId.equals(selproductCategoryId)) {
								data.put("productCategoryId", prodCategoryId);
								data.put("categoryName", category.getString("categoryName"));
								data.put("selected", true);
							} else {
								data.put("productCategoryId", prodCategoryId);
								data.put("categoryName", category.getString("categoryName"));
								data.put("selected", false);

							}
						}
						pcDataList.add(data);
					}

				}

				result.put("productCat", pcDataList);
			}
			if (UtilValidate.isNotEmpty(productData.get("productSubCategoryId"))) {
				selproductSubCategoryId = productData.getString("productSubCategoryId");
			}
			if (UtilValidate.isNotEmpty(selproductCategoryId)) {
				List<GenericValue> subCategoryList = EntityQuery.use(delegator)
						.select("productCategoryId", "categoryName").from("ProductCategory")
						.where("primaryParentCategoryId", selproductCategoryId).queryList();
				if (UtilValidate.isNotEmpty(subCategoryList)) {
					for (GenericValue each : subCategoryList) {
						Map<String, Object> data = new HashMap<String, Object>();
						if (selproductSubCategoryId.equals(each.get("productCategoryId"))) {
							data.put("productSubCategoryId", each.get("productCategoryId"));
							data.put("categoryName", each.getString("categoryName"));
							data.put("selected", true);
						} else {
							data.put("productSubCategoryId", each.get("productCategoryId"));
							data.put("categoryName", each.getString("categoryName"));
							data.put("selected", false);
						}
						pscDataList.add(data);
					}
				}
				result.put("productSubCat", pscDataList);
			}
			if (UtilValidate.isNotEmpty(productData.get("product"))) {
				selproductId = productData.getString("product");
			}
			if (UtilValidate.isNotEmpty(selproductSubCategoryId)) {

				List<EntityCondition> conditionlist = FastList.newInstance();
				conditionlist.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS,
						selproductSubCategoryId));
				conditionlist.add(EntityUtil.getFilterByDateExpr());

				EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> productCategoryMemberList = EntityQuery.use(delegator)
						.select("productId", "productCategoryId").from("ProductCategoryMember").where(condition)
						.orderBy("sequenceNum").queryList();
				if (UtilValidate.isNotEmpty(productCategoryMemberList)) {
					productIds.addAll(
							EntityUtil.getFieldListFromEntityList(productCategoryMemberList, "productId", true));
				}
			}
			if (UtilValidate.isNotEmpty(productIds)) {
				for (String productId : productIds) {
					Map<String, Object> data = new HashMap<String, Object>();

					GenericValue product = EntityQuery.use(delegator).select("productName").from("Product")
							.where("productId", productId).queryOne();
					if (UtilValidate.isNotEmpty(product)) {
						if (selproductId.equals(productId)) {
							data.put("productId", productId);
							data.put("productName", product.getString("productName"));
							data.put("selected", true);
						} else {
							data.put("productId", productId);
							data.put("productName", product.getString("productName"));
							data.put("selected", false);
						}
					}
					dataList.add(data);
				}
			}

			result.put("product", dataList);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getPartyTelecomNumbers(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = request.getParameter("partyId");

		try {
			EntityCondition conditionPCM = EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
							EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)),
					EntityOperator.AND);

			List<GenericValue> partyContactMechList = EntityQuery.use(delegator).from("PartyContactMech")
					.where(conditionPCM).queryList();

			if (UtilValidate.isNotEmpty(partyContactMechList)) {
				List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(partyContactMechList,
						"contactMechId", true);
				if (UtilValidate.isNotEmpty(contactMechIds)) {

					String primaryContactMechId = "";
					List<EntityCondition> contactMechConditionList = FastList.newInstance();
					contactMechConditionList
					.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds));
					contactMechConditionList.add(EntityCondition.makeCondition("contactMechTypeId",
							EntityOperator.EQUALS, "TELECOM_NUMBER"));
					EntityCondition contactMechCondition = EntityCondition.makeCondition(contactMechConditionList,
							EntityOperator.AND);
					List<GenericValue> contactMechList = EntityQuery.use(delegator).from("ContactMech")
							.where(contactMechCondition).queryList();

					if (UtilValidate.isNotEmpty(contactMechList)) {
						List<String> telecomContactMechIds = EntityUtil.getFieldListFromEntityList(contactMechList,
								"contactMechId", true);

						if (UtilValidate.isNotEmpty(telecomContactMechIds)) {
							List<EntityCondition> partyContactMechPurposeConditionList = FastList.newInstance();
							partyContactMechPurposeConditionList.add(EntityCondition.makeCondition("contactMechId",
									EntityOperator.IN, telecomContactMechIds));
							partyContactMechPurposeConditionList.add(EntityCondition
									.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE"));
							EntityCondition partyContactMechPurposeCondition = EntityCondition
									.makeCondition(partyContactMechPurposeConditionList, EntityOperator.AND);
							GenericValue partyContactMechPurpose = EntityUtil.getFirst(
									EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose")
									.where(partyContactMechPurposeCondition).queryList());
							if (UtilValidate.isNotEmpty(partyContactMechPurpose)
									&& UtilValidate.isNotEmpty(partyContactMechPurpose.getString("contactMechId"))) {
								primaryContactMechId = partyContactMechPurpose.getString("contactMechId");
								GenericValue primaryTelecomNumber = EntityQuery.use(delegator).select("contactNumber","countryCode","areaCode")
										.from("TelecomNumber").where("contactMechId", primaryContactMechId).queryOne();
								if (UtilValidate.isNotEmpty(primaryTelecomNumber)
										&& UtilValidate.isNotEmpty(primaryTelecomNumber.getString("contactNumber"))) {
									String primaryPhone = primaryTelecomNumber.getString("contactNumber");
									String areaCodeNum = primaryTelecomNumber.getString("areaCode");
									if (UtilValidate.isNotEmpty(primaryPhone)) {
										Map<String, Object> data = new HashMap<String, Object>();
										data.put("isPrimary", "Y");
										data.put("contactNumber", primaryPhone);
										data.put("purposeTypeId", "PRIMARY_PHONE");
										data.put("contactMechId", primaryContactMechId);
										data.put("areaCode", areaCodeNum);
										data.put("countryCode", primaryTelecomNumber.getString("countryCode"));
										results.add(data);
									}
								}
							}

							List<GenericValue> partyContactMechPurposeList = EntityQuery.use(delegator)
									.select("contactMechId", "contactMechPurposeTypeId").from("PartyContactMechPurpose")
									.where(EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("contactMechId", EntityOperator.IN,
													telecomContactMechIds),
											EntityUtil.getFilterByDateExpr()))
									.queryList();

							Map<String, Object> telecomContactMechPurposeMap = new HashMap<String, Object>();

							if (UtilValidate.isNotEmpty(partyContactMechPurposeList)) {
								for (GenericValue eachEntry : partyContactMechPurposeList) {
									String contactMechId = eachEntry.getString("contactMechId");
									String contactMechPurposeTypeId = eachEntry.getString("contactMechPurposeTypeId");
									if(contactMechId.equals(primaryContactMechId)) continue;
									String phoneNumber = "";
									String areaCode = "";
									String countryCode = "";
									GenericValue telecomNumber = null;
									if (telecomContactMechPurposeMap.containsKey(contactMechId)) {
										phoneNumber = (String) telecomContactMechPurposeMap.get(contactMechId);
										telecomNumber = EntityQuery.use(delegator).select("contactMechId", "contactNumber","countryCode","areaCode").from("TelecomNumber")
												.where(EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS, contactMechId)).queryFirst();
									} else {
										telecomNumber = EntityQuery.use(delegator)
												.select("contactMechId", "contactNumber","countryCode","areaCode").from("TelecomNumber")
												.where(EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS, contactMechId)).queryFirst();
										if (UtilValidate.isNotEmpty(telecomNumber)) {
											phoneNumber = telecomNumber.getString("contactNumber");
										}
									}
									if (UtilValidate.isNotEmpty(telecomNumber)) {
										areaCode = telecomNumber.getString("areaCode");
										countryCode = telecomNumber.getString("countryCode");
									}
									telecomContactMechPurposeMap.put(contactMechId, phoneNumber);

									Map<String, Object> phoneNumberWithPurposeTypeMap = new HashMap<String, Object>();
									phoneNumberWithPurposeTypeMap.put("purposeTypeId", contactMechPurposeTypeId);
									phoneNumberWithPurposeTypeMap.put("contactNumber", phoneNumber);
									phoneNumberWithPurposeTypeMap.put("areaCode", areaCode);
									phoneNumberWithPurposeTypeMap.put("countryCode", countryCode);
									phoneNumberWithPurposeTypeMap.put("isPrimary", "N");
									phoneNumberWithPurposeTypeMap.put("contactMechId", contactMechId);
									results.add(phoneNumberWithPurposeTypeMap);

								}
							}
						}
					}
				}
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getAttendeeList(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> contactMapList = new ArrayList<Map<String, Object>>();
		String partyId = request.getParameter("partyId");
		String workEffortId = request.getParameter("workEffortId");
		try {
			List requireAttendeeslist = new ArrayList();
			List optionalAttendeeslist = new ArrayList();
			List<String> reqAttParties = new ArrayList();
			List<String> optionalAttParties = new ArrayList();
			if (UtilValidate.isNotEmpty(workEffortId)) {
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
				conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				conditionList.add(EntityCondition.makeCondition("expectationEnumId", EntityOperator.IN,
						UtilMisc.toList("WEE_REQUIRE", "WEE_REQUEST")));
				EntityCondition conditionsWorkEff = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> workEffortPartyAss = EntityQuery.use(delegator)
						.select("partyId", "expectationEnumId").from("WorkEffortPartyAssignment")
						.where(conditionsWorkEff).queryList();
				requireAttendeeslist = EntityUtil.filterByCondition(workEffortPartyAss,
						EntityCondition.makeCondition("expectationEnumId", EntityOperator.EQUALS, "WEE_REQUIRE"));
				optionalAttendeeslist = EntityUtil.filterByCondition(workEffortPartyAss,
						EntityCondition.makeCondition("expectationEnumId", EntityOperator.EQUALS, "WEE_REQUEST"));
				reqAttParties = EntityUtil.getFieldListFromEntityList(requireAttendeeslist, "partyId", true);
				optionalAttParties = EntityUtil.getFieldListFromEntityList(optionalAttendeeslist, "partyId", true);
			}
			
			if (UtilValidate.isNotEmpty(reqAttParties)) {
				for (String ptyId : reqAttParties) {
					Map<String, Object> partyContactMap = new HashMap<String, Object>();
					partyContactMap.put("partyId", ptyId);
					partyContactMap.put("userName", PartyHelper.getPartyName(delegator, ptyId, false));
					partyContactMap.put("selected", "req");
					results.add(partyContactMap);
				}
			}

			if (UtilValidate.isNotEmpty(partyId)) {
				String validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("ACCOUNT"),
						delegator);
				if (UtilValidate.isEmpty(validRoleTypeId))
					validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("LEAD"), delegator);

				if (UtilValidate.isNotEmpty(validRoleTypeId)) {
					Map<String, Object> contactAcctMap = new HashMap<String, Object>();
					contactAcctMap.put("partyIdTo", partyId);
					contactAcctMap.put("partyRoleTypeId", validRoleTypeId);
					Map<String, Object> serResult = dispatcher.runSync("common.getContactAndPartyAssoc",
							contactAcctMap);

					if (ServiceUtil.isSuccess(serResult)) {
						List<Object> primaryContactList = FastList.newInstance();
						primaryContactList = (List<Object>) serResult.get("partyContactAssoc");
						for (int i = 0; i < primaryContactList.size(); i++) {
							Map<String, Object> partyContactMap = new HashMap<String, Object>();
							Map<String, Object> dataMap = new HashMap<String, Object>();
							partyContactMap = (Map<String, Object>) primaryContactList.get(i);
							
							if (UtilValidate.isNotEmpty(reqAttParties)
									&& reqAttParties.contains(partyContactMap.get("contactId"))) {
								continue;
							}
							
							dataMap.put("partyId", partyContactMap.get("contactId"));
							dataMap.put("userName", partyContactMap.get("name"));
							dataMap.put("selected", "no");
							if (UtilValidate.isNotEmpty(optionalAttParties)
									&& optionalAttParties.contains(partyContactMap.get("contactId"))) {
								dataMap.put("selected", "opt");
							}
							if (UtilValidate.isNotEmpty(reqAttParties)
									&& reqAttParties.contains(partyContactMap.get("contactId"))) {
								dataMap.put("selected", "req");
							}
							results.add(dataMap);
						}
					}
				}
			}
			Set<String> fieldToSelect = new TreeSet<String>();
			fieldToSelect.add("partyId");
			fieldToSelect.add("userLoginId");
			fieldToSelect.add("firstName");
			fieldToSelect.add("lastName");
			EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
			List<GenericValue> UserLoginPersonList = EntityQuery.use(delegator).select(fieldToSelect)
					.from("UserLoginPerson").where(conditions).queryList();

			if (UserLoginPersonList != null && UserLoginPersonList.size() > 0) {
				for (int i = 0; i < UserLoginPersonList.size(); i++) {
					if (UtilValidate.isNotEmpty(reqAttParties)
							&& reqAttParties.contains(UserLoginPersonList.get(i).getString("partyId"))) {
						continue;
					}
					
					Map<String, Object> partyContactMap = new HashMap<String, Object>();
					partyContactMap.put("partyId", UserLoginPersonList.get(i).getString("partyId"));
					String name = "";
					name = UtilValidate.isNotEmpty(UserLoginPersonList.get(i).getString("firstName"))
							? UserLoginPersonList.get(i).getString("firstName") : "";
							if (UtilValidate.isNotEmpty(UserLoginPersonList.get(i).getString("lastName"))) {
								name += " " + UserLoginPersonList.get(i).getString("lastName");
							}
							partyContactMap.put("userName", name);
							partyContactMap.put("selected", "no");
							if (UtilValidate.isNotEmpty(optionalAttParties)
									&& optionalAttParties.contains(UserLoginPersonList.get(i).getString("partyId"))) {
								partyContactMap.put("selected", "opt");
							}
							if (UtilValidate.isNotEmpty(reqAttParties)
									&& reqAttParties.contains(UserLoginPersonList.get(i).getString("partyId"))) {
								partyContactMap.put("selected", "req");
							}
							contactMapList.add(partyContactMap);
				}
				results.addAll(contactMapList);
			}
			// Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		result.put("attendeesList", results);
		return doJSONResponse(response, result);
	}

	public static String searchCampaigns(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String requestUri = request.getParameter("requestUri");

		String start = request.getParameter("start");
		String length = request.getParameter("length");

		String marketingCampaignId = request.getParameter("campaignId");
		String campaignName = request.getParameter("campaignName");
		String campaignTypeId = request.getParameter("campaignType");
		String statusId = request.getParameter("campaignStatus");

		String domainEntityType = request.getParameter("domainEntityType");

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {

			List conditionList = FastList.newInstance();

			if (UtilValidate.isNotEmpty(marketingCampaignId)) {
				EntityCondition campaignIdCondition = EntityCondition.makeCondition("marketingCampaignId",EntityOperator.LIKE, "%" +  marketingCampaignId + "%");
				conditionList.add(campaignIdCondition);
			}
			if (UtilValidate.isNotEmpty(campaignName)) {
				EntityCondition campaignNameCondition = EntityCondition.makeCondition("campaignName",
						EntityOperator.LIKE, "%" + campaignName + "%");
				conditionList.add(campaignNameCondition);
			}
			if (UtilValidate.isNotEmpty(campaignTypeId)) {
				EntityCondition campaignTypeCondition = EntityCondition.makeCondition("campaignTypeId",
						EntityOperator.EQUALS, campaignTypeId);
				conditionList.add(campaignTypeCondition);
			}
			if (UtilValidate.isNotEmpty(statusId)) {
				EntityCondition campaignStatusCondition = EntityCondition.makeCondition("statusId",
						EntityOperator.EQUALS, statusId);
				conditionList.add(campaignStatusCondition);
			}

			if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
				EntityCondition condition = EntityCondition.makeCondition("statusId", EntityOperator.IN,
						UtilMisc.toList("MKTG_CAMP_INPROGRESS", "MKTG_CAMP_PUBLISHED", "MKTG_CAMP_SCHEDULED"));
				conditionList.add(condition);
			}

			if (UtilValidate.isEmpty(conditionList)) {
				EntityCondition defaultCondition = EntityCondition.makeCondition("statusId", EntityOperator.IN,
						UtilMisc.toList("MKTG_CAMP_PUBLISHED", "MKTG_CAMP_SCHEDULED"));
				conditionList.add(defaultCondition);
			}

			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
					EntityFindOptions.CONCUR_READ_ONLY, false);
			efo.setOffset(0);
			efo.setLimit(1000);

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> marketingCampaignList = delegator.findList("MarketingCampaign", mainConditons, null,
					UtilMisc.toList("-marketingCampaignId"), efo, false);

			if (UtilValidate.isNotEmpty(marketingCampaignList)) {
				for (GenericValue marketingCampaign : marketingCampaignList) {
					Map<String, Object> data = new HashMap<String, Object>();
					marketingCampaignId = marketingCampaign.getString("marketingCampaignId");
					campaignName = marketingCampaign.getString("campaignName");
					campaignTypeId = marketingCampaign.getString("campaignTypeId");
					statusId = marketingCampaign.getString("statusId");

					String statusItemDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);
					String campaignTypeDesc = org.fio.homeapps.util.DataUtil.getCampaignChannelType(delegator,
							campaignTypeId);

					data.put("marketingCampaignId", marketingCampaignId);
					data.put("campaignName", campaignName);
					data.put("campaignType", campaignTypeDesc);
					data.put("status", statusItemDesc);
					dataList.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, dataList);
	}

	public static String getCustomerContactInfo(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String partyId = (String) context.get("partyId");

		Map<String, Object> result = FastMap.newInstance();
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				Map<String, String> primaryContactInformation = PartyPrimaryContactMechWorker
						.getPartyPrimaryContactMechValueMaps(delegator, partyId);
				Map postalAddress = PartyPrimaryContactMechWorker.getPartyPrimaryPostalAddressList(delegator, partyId);
				result.put("primaryContactInformation", primaryContactInformation);
				result.put("postal", postalAddress);

			}
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getPrimaryContactInfoByUserLoginId(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String userLoginId = (String) context.get("userLoginId");

		Map<String, Object> result = FastMap.newInstance();

		try {

			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();

			if (UtilValidate.isNotEmpty(userLoginId)) {

				GenericValue selectedUserLogin = delegator.findOne("UserLogin",
						UtilMisc.toMap("userLoginId", userLoginId), false);
				if (UtilValidate.isEmpty(selectedUserLogin)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Invalid data!");
					return AjaxEvents.doJSONResponse(response, result);
				}

				String partyId = selectedUserLogin.getString("partyId");

				Map<String, String> primaryContactInformation = PartyPrimaryContactMechWorker
						.getPartyPrimaryContactMechValueMaps(delegator, partyId);

				result.put("primaryContactInformation", primaryContactInformation);

			}

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getDuplicatePartyList(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> data = FastMap.newInstance();
		String requestUri = request.getParameter("requestUri");
		String primaryPhoneNumber = request.getParameter("primaryPhoneNumber");
		String primaryEmail = request.getParameter("primaryEmail");
		String generalPostalCode = request.getParameter("generalPostalCode");
		String name = request.getParameter("name");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String accType = request.getParameter("accType");
		String generalAddress1 =request.getParameter("generalAddress1");
		String generalCity =request.getParameter("generalCity");
		String loyaltyEnabledVal = request.getParameter("loyaltyEnabledVal");
		String generalCountryGeoId = request.getParameter("generalCountryGeoId");
		String generalStateProvinceGeoId = request.getParameter("generalStateProvinceGeoId");
		List<String> emptyFieldList = new ArrayList<String>();
		Map<String,String> reqFields = null;

		Integer dupCount = 0;
		Integer matchCount = 0;
		List<GenericValue> matchRecodsGv = null;
		List<GenericValue> partyNameAndZipcode = null;
		GenericValue partyPhoneContactMechTo = null;
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		primaryPhoneNumber = primaryPhoneNumber.trim();
		String groupName = "";
		String link = "";
		String pNameUrl = "";
		String gName = "";
		String linkSecond = "";
		String nameUrl = "";
		GenericValue pCMGV = null;
		GenericValue pCMGV1 = null;
		String SnameUrl = "";

		try {
			// validation for Email Id
			EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("primaryEmail", EntityOperator.IN, UtilMisc.toList(primaryEmail)));
			List<GenericValue> partyContactMechToList = delegator.findList("PartySummaryDetailsView", conditions, null,
					UtilMisc.toList("createdDate DESC"), null, false);
			dupCount = partyContactMechToList.size();
			// GenericValue partyContactMechTo =
			// EntityUtil.getFirst(EntityUtil.filterByDate(delegator.findList("PartyAndContactMech",
			// conditions, null, UtilMisc.toList("fromDate DESC"), null,
			// false)));
			if (UtilValidate.isNotEmpty(partyContactMechToList)) {
				pCMGV1 = partyContactMechToList.get(0);
				if (UtilValidate.isNotEmpty(pCMGV1)) {
					groupName = pCMGV1.getString("groupName");
					String roleTypeId = pCMGV1.getString("roleTypeId");
					String pId = pCMGV1.getString("partyId");
					if (!UtilValidate.isNotEmpty(groupName) && UtilValidate.isNotEmpty(roleTypeId)) {
						String firstRName = pCMGV1.getString("firstName");
						String lastRName = pCMGV1.getString("lastName");
						if (roleTypeId.equals("CONTACT")) {
							link = "/contact-portal/control/viewContact?partyId=" + pId;
							roleTypeId = "Contact";
						}
						if (roleTypeId.equals("CUSTOMER")) {
							link = "/customer-portal/control/viewCustomer?partyId=" + pId;
							roleTypeId = "Customer";
						}

						String personName = firstRName + " " + lastRName + " " + "-" + " " + roleTypeId;
						pNameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
					} else {
						if (roleTypeId.equals("LEAD")) {
							link = "/lead-portal/control/viewLead?partyId=" + pId;
							roleTypeId = "Lead";
						}
						if (roleTypeId.equals("ACCOUNT")) {
							link = "/account-portal/control/viewAccount?partyId=" + pId;
							roleTypeId = "Account";
						}
						String personName = groupName + " " + "-" + " " + roleTypeId;
						pNameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
					}
				}
				if (partyContactMechToList.size() > 1) {
					pCMGV = partyContactMechToList.get(1);
				}
				if (UtilValidate.isNotEmpty(pCMGV)) {
					gName = pCMGV.getString("groupName");
					String roleTypeId = pCMGV.getString("roleTypeId");
					String pId = pCMGV.getString("partyId");
					if (!UtilValidate.isNotEmpty(gName) && UtilValidate.isNotEmpty(roleTypeId)) {
						String firstRName = pCMGV.getString("firstName");
						String lastRName = pCMGV.getString("lastName");

						if (roleTypeId.equals("CONTACT")) {
							link = "/contact-portal/control/viewContact?partyId=" + pId;
							roleTypeId = "Contact";
						}
						if (roleTypeId.equals("CUSTOMER")) {
							link = "/customer-portal/control/viewCustomer?partyId=" + pId;
							roleTypeId = "Customer";
						}
						String personName = firstRName + " " + lastRName + " " + "-" + " " + roleTypeId;
						nameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
					} else {

						if (roleTypeId.equals("LEAD")) {
							link = "/lead-portal/control/viewLead?partyId=" + pId;
							roleTypeId = "Lead";
						}
						if (roleTypeId.equals("ACCOUNT")) {
							link = "/account-portal/control/viewAccount?partyId=" + pId;
							roleTypeId = "Account";
						}
						String personName = gName + " " + "-" + " " + roleTypeId;
						nameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
					}
				}

				if (dupCount > 1) {
					data.put("Error_Message", "Email Id already exists for below party:</br> " + "1. " + pNameUrl
							+ "</br>" + "2. " + nameUrl + "</br>Still want to continue with create?");
				} else {
					data.put("Error_Message", "Email Id exists for below party:</br>" + "1. " + pNameUrl
							+ "</br>Still want to continue with create?");
				}
				// }

			}

			// validation for Lead name and account name
			if (accType.equals("LEAD") || accType.equals("ACCOUNT")) {
				List<GenericValue> partyAndZipcode = delegator.findByAnd("PartySummaryDetailsView",
						UtilMisc.toMap("primaryPostalCode", generalPostalCode, "groupName", name),
						UtilMisc.toList("createdDate DESC"), false);
				if (UtilValidate.isNotEmpty(partyAndZipcode)) {
					matchCount = matchCount + 1;
					GenericValue partyAndZipcodeExp = EntityUtil.getFirst((partyAndZipcode));
					pCMGV = partyAndZipcode.get(0);
					if (UtilValidate.isNotEmpty(pCMGV)) {
						groupName = pCMGV.getString("groupName");
						String roleTypeId = pCMGV.getString("roleTypeId");
						String pId = pCMGV.getString("partyId");
						if (roleTypeId.equals("LEAD")) {
							link = "/lead-portal/control/viewLead?partyId=" + pId;
							roleTypeId = "Lead";
						}
						if (roleTypeId.equals("ACCOUNT")) {
							link = "/account-portal/control/viewAccount?partyId=" + pId;
							roleTypeId = "Account";
						}
						String personName = groupName + " " + "-" + " " + roleTypeId;
						nameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";

					}

					// get second record
					if (partyAndZipcode.size() > 1) {
						pCMGV1 = partyAndZipcode.get(1);
					}
					if (UtilValidate.isNotEmpty(pCMGV1)) {
						gName = pCMGV1.getString("groupName");
						String roleTypeId = pCMGV1.getString("roleTypeId");
						String pId = pCMGV1.getString("partyId");
						if (!UtilValidate.isNotEmpty(gName) && UtilValidate.isNotEmpty(roleTypeId)) {

							if (roleTypeId.equals("LEAD")) {
								link = "/lead-portal/control/viewLead?partyId=" + pId;
								roleTypeId = "Lead";
							}
							if (roleTypeId.equals("ACCOUNT")) {
								link = "/account-portal/control/viewAccount?partyId=" + pId;
								roleTypeId = "Account";
							}
							String personName = gName + " " + "-" + " " + roleTypeId;
							SnameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
						}
					}

					if (partyAndZipcode.size() > 1) {
						data.put("Error_Message", "ZIP Code and Name match exists for below parties:</br> " + "1. "
								+ nameUrl + "</br>" + "2. " + SnameUrl + "</br>Still want to continue with create?");
					} else {
						data.put("Error_Message", "ZIP Code and Name match exists for below parties:</br> " + "1. "
								+ nameUrl + "</br>" + "Still want to continue with create?");
					}

				}
			}
			// validation for Contact Name
			else {
				List<GenericValue> partyAndZipcode = delegator
						.findByAnd(
								"PartySummaryDetailsView", UtilMisc.toMap("primaryPostalCode", generalPostalCode,
										"firstName", firstName, "lastName", lastName),
								UtilMisc.toList("createdDate DESC"), false);
				if (UtilValidate.isNotEmpty(partyAndZipcode)) {
					GenericValue partyAndZipcodeExp = EntityUtil.getFirst((partyAndZipcode));
					matchCount = matchCount + 1;
					pCMGV = partyAndZipcode.get(0);
					if (UtilValidate.isNotEmpty(pCMGV)) {
						firstName = pCMGV.getString("firstName");
						lastName = pCMGV.getString("lastName");
						String roleTypeId = pCMGV.getString("roleTypeId");
						String pId = pCMGV.getString("partyId");
						if (roleTypeId.equals("CUSTOMER")) {
							link = "/customer-portal/control/viewCustomer?partyId=" + pId;
							roleTypeId = "Customer";
						}
						if (roleTypeId.equals("CONTACT")) {
							link = "/contact-portal/control/viewContact?partyId=" + pId;
							roleTypeId = "Contact";
						}
						String personName = firstName + "" + lastName + " " + "-" + " " + roleTypeId;
						nameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
					}

					if (partyAndZipcode.size() > 1) {
						pCMGV1 = partyAndZipcode.get(1);
					}
					if (UtilValidate.isNotEmpty(pCMGV1)) {
						firstName = pCMGV1.getString("firstName");
						lastName = pCMGV1.getString("lastName");
						String roleTypeId = pCMGV1.getString("roleTypeId");
						String pId = pCMGV1.getString("partyId");
						if (roleTypeId.equals("CUSTOMER")) {
							link = "/customer-portal/control/viewCustomer?partyId=" + pId;
							roleTypeId = "Customer";
						}
						if (roleTypeId.equals("CONTACT")) {
							link = "/contact-portal/control/viewContact?partyId=" + pId;
							roleTypeId = "Contact";
						}
						String personName = firstName + "" + lastName + " " + "-" + " " + roleTypeId;
						SnameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
					}

					if (partyAndZipcode.size() > 1) {
						data.put("Error_Message", "ZIP Code and Name match exists for below parties:</br> " + "1. "
								+ nameUrl + "</br>" + "2. " + SnameUrl + "</br>Still want to continue with create?");
					} else {
						data.put("Error_Message", "ZIP Code and Name match exists for below parties:</br> " + "1. "
								+ nameUrl + "</br>" + "Still want to continue with create ?");
					}
				}
			}
			String loyaltyCustomerPermission = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOYALTY_CUSTOMER_VALIDATION");
			if (UtilValidate.isNotEmpty(loyaltyCustomerPermission) && loyaltyCustomerPermission.equals("Y")) {
				reqFields = UtilMisc.toMap("First Name",firstName, "last Name",lastName,"Email",primaryEmail);
			}

			/*String loyaltyCustAddressValidation = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOYALTY_CUST_POSTAL_ADDR_VALIDATION");
			if (UtilValidate.isNotEmpty(loyaltyCustAddressValidation) && loyaltyCustAddressValidation.equals("Y")) {
				reqFields = UtilMisc.toMap("Zip",generalPostalCode, "Address Line 1",generalAddress1,"City" , generalCity,"Country",generalCountryGeoId,"State",generalStateProvinceGeoId);
			}*/

			for(String reqField : reqFields.keySet()) {
				if(UtilValidate.isEmpty(reqFields.get(reqField)))
					emptyFieldList.add(reqField);
			}

			if (loyaltyEnabledVal.equals("Y") && UtilValidate.isNotEmpty(emptyFieldList))
				data.put("Error_Message", "Customer is not eligible to enable loyalty number : Required field(s) "+emptyFieldList+" missing. " +"</br>"+" Still want to continue with non-loyalty customer ?");

			// if all three matches
			/*
			 * if(matchCount == 2) {
			 * if(UtilValidate.isNotEmpty(partyContactMechTo)){
			 * data.put("Error_Message",
			 * "PartyId- "+partyPhoneContactMechTo.getString("partyId")
			 * +" matching with all validations. Do you want to create ?"); }
			 * 
			 * }
			 */
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		if (data.isEmpty()) {
			data.put("Error_Message", "NO_RECORDS");
			return doJSONResponse(response, data);
		}
		return doJSONResponse(response, data);
	}

	// get duplicate email list
	public static String getDuplicateEmailList(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> data = FastMap.newInstance();
		String requestUri = request.getParameter("requestUri");
		String primaryEmail = request.getParameter("primaryEmail");
		String partyId = request.getParameter("partyId");
		String accType = request.getParameter("accType");
		String screenType = request.getParameter("screenType");
		Integer dupCount = 0;
		Integer matchCount = 0;
		List<GenericValue> matchRecodsGv = null;
		List<GenericValue> partyNameAndZipcode = null;
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String groupName = "";
		String link = "";
		String pNameUrl = "";
		String gName = "";
		String linkSecond = "";
		String nameUrl = "";
		GenericValue pCMGV = null;
		String message = "";
		try {
			if (screenType.equals("CREATE")) {
				message = "Still want to continue with the create?";
			} else {
				message = "Still want to continue with the update?";
			}
			// validation for Email Address

			// get current party email address
			List<GenericValue> currentPrimaryEmailList = EntityQuery.use(delegator).from("PartySummaryDetailsView")
					.where("partyId", partyId).orderBy("-createdDate").queryList();
			GenericValue currentPartyContactMech = currentPrimaryEmailList.get(0);
			String oldInfoString = currentPartyContactMech.getString("primaryEmail");

			if (!primaryEmail.equals(oldInfoString)) {
				EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND, EntityCondition
						.makeCondition("primaryEmail", EntityOperator.IN, UtilMisc.toList(primaryEmail)));
				List<GenericValue> partyContactMechToList = EntityQuery.use(delegator).from("PartySummaryDetailsView")
						.where("primaryEmail", primaryEmail).orderBy("-createdDate").queryList();
				dupCount = partyContactMechToList.size();
				if (UtilValidate.isNotEmpty(partyContactMechToList)) {
					GenericValue pCMGV1 = partyContactMechToList.get(0);
					if (UtilValidate.isNotEmpty(pCMGV1)) {
						groupName = pCMGV1.getString("groupName");
						String roleTypeId = pCMGV1.getString("roleTypeId");
						String pId = pCMGV1.getString("partyId");
						if (!UtilValidate.isNotEmpty(groupName) && UtilValidate.isNotEmpty(roleTypeId)) {
							String firstName = pCMGV1.getString("firstName");
							String lastName = pCMGV1.getString("lastName");
							if (roleTypeId.equals("CUSTOMER")) {
								link = "/customer-portal/control/viewCustomer?partyId=" + pId;
								roleTypeId = "Customer";
							}
							if (roleTypeId.equals("CONTACT")) {
								link = "/contact-portal/control/viewContact?partyId=" + pId;
								roleTypeId = "Contact";
							}
							String personName = firstName + "" + lastName + " " + "-" + " " + roleTypeId;
							pNameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
						} else {
							if (roleTypeId.equals("LEAD")) {
								link = "/lead-portal/control/viewLead?partyId=" + pId;
								roleTypeId = "Lead";
							}
							if (roleTypeId.equals("ACCOUNT")) {
								link = "/account-portal/control/viewAccount?partyId=" + pId;
								roleTypeId = "Account";
							}
							String personName = groupName + " " + "-" + " " + roleTypeId;
							pNameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
						}
					}
					if (partyContactMechToList.size() > 1) {
						pCMGV = partyContactMechToList.get(1);
					}
					if (UtilValidate.isNotEmpty(pCMGV)) {
						gName = pCMGV.getString("groupName");
						String roleTypeId = pCMGV.getString("roleTypeId");
						Debug.log("pCMGV======" + pCMGV);
						String pId = pCMGV.getString("partyId");
						if (!UtilValidate.isNotEmpty(gName) && UtilValidate.isNotEmpty(roleTypeId)) {
							String firstName = pCMGV.getString("firstName");
							String lastName = pCMGV.getString("lastName");
							if (roleTypeId.equals("CUSTOMER")) {
								link = "/customer-portal/control/viewCustomer?partyId=" + pId;
								roleTypeId = "Customer";
							}
							if (roleTypeId.equals("CONTACT")) {
								link = "/contact-portal/control/viewContact?partyId=" + pId;
								roleTypeId = "Contact";
							}
							String personName = firstName + "" + lastName + " " + "-" + " " + roleTypeId;
							nameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
						} else {

							if (UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equals("LEAD")) {
								link = "/lead-portal/control/viewLead?partyId=" + pId;
								roleTypeId = "Lead";
							}
							if (UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equals("ACCOUNT")) {
								link = "/account-portal/control/viewAccount?partyId=" + pId;
								roleTypeId = "Account";
							}
							String personName = gName + " " + "-" + " " + roleTypeId;
							nameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
						}
					}

					if (dupCount > 1) {
						data.put("Error_Message", "Email Id already exists for below parties:</br> " + "1. " + pNameUrl
								+ "</br>" + "2. " + nameUrl + "</br>" + message);
					} else {
						data.put("Error_Message",
								"Email Id already exists for below party:</br>" + "1. " + pNameUrl + "</br>" + message);
					}
					// }
				} else {
					// data.put("Error_Message", "Email Id already exists" +".Do
					// you want update ?");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		if (data.isEmpty()) {
			data.put("Error_Message", "NO_RECORDS");
			return doJSONResponse(response, data);
		}
		return doJSONResponse(response, data);
	}
	// get duplicate address with name

	public static String getDuplicateAddress(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> data = FastMap.newInstance();
		String requestUri = request.getParameter("requestUri");
		String generalPostalCode = request.getParameter("postalCode");
		String name = request.getParameter("groupName");
		String partyId = request.getParameter("partyId");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String accType = request.getParameter("accType");
		String screenType = request.getParameter("screenType");
		Integer dupCount = 0;
		Integer matchCount = 0;
		List<GenericValue> matchRecodsGv = null;
		String link = null;
		List<GenericValue> partyNameAndZipcode = null;
		String groupName = null;
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String nameUrl = "";
		GenericValue pCMGV1 = null;
		String gName = "";
		String SnameUrl = "";
		String cGroupName = "";
		String pCode = "";
		String fName = "";
		String lName = "";
		String message = "";
		if (screenType.equals("CREATE")) {
			message = "Still want to create?";
		} else {
			message = "Still want to update?";
		}
		try {
			List<GenericValue> partyAndZipcodeCurrent = delegator.findByAnd("PartySummaryDetailsView",
					UtilMisc.toMap("partyId", partyId), null, false);
			if (UtilValidate.isNotEmpty(partyAndZipcodeCurrent)) {
				GenericValue partyAndZipcodeExp = EntityUtil.getFirst((partyAndZipcodeCurrent));
				cGroupName = partyAndZipcodeExp.getString("groupName");
				pCode = partyAndZipcodeExp.getString("primaryPostalCode");
				fName = partyAndZipcodeExp.getString("firstName");
				lName = partyAndZipcodeExp.getString("lastName");
			}
			// validation for Lead name and account name
			if (accType.equals("LEAD") || accType.equals("ACCOUNT")) {
				if (!name.equals(cGroupName) || !generalPostalCode.equals(pCode)) {
					List<GenericValue> partyAndZipcode = delegator.findByAnd("PartySummaryDetailsView",
							UtilMisc.toMap("primaryPostalCode", generalPostalCode, "groupName", name),
							UtilMisc.toList("createdDate DESC"), false);
					if (UtilValidate.isNotEmpty(partyAndZipcode)) {
						GenericValue partyAndZipcodeExp = EntityUtil.getFirst((partyAndZipcode));
						String latestPartyId = partyAndZipcodeExp.getString("partyId");

						GenericValue pCMGV = partyAndZipcode.get(0);
						if (UtilValidate.isNotEmpty(pCMGV)) {
							groupName = pCMGV.getString("groupName");
							String roleTypeId = pCMGV.getString("roleTypeId");
							String pId = pCMGV.getString("partyId");
							if (roleTypeId.equals("LEAD")) {
								link = "/lead-portal/control/viewLead?partyId=" + pId;
								roleTypeId = "Lead";
							}
							if (roleTypeId.equals("ACCOUNT")) {
								link = "/account-portal/control/viewAccount?partyId=" + pId;
								roleTypeId = "Account";
							}
							String personName = groupName + " " + "-" + " " + roleTypeId;
							nameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";

						}

						// get second record
						if (partyAndZipcode.size() > 1) {
							pCMGV1 = partyAndZipcode.get(1);
						}
						if (UtilValidate.isNotEmpty(pCMGV1)) {
							gName = pCMGV1.getString("groupName");
							String roleTypeId = pCMGV1.getString("roleTypeId");
							String pId = pCMGV1.getString("partyId");
							if (!UtilValidate.isNotEmpty(gName) && UtilValidate.isNotEmpty(roleTypeId)) {

								if (roleTypeId.equals("LEAD")) {
									link = "/lead-portal/control/viewLead?partyId=" + pId;
									roleTypeId = "Lead";
								}
								if (roleTypeId.equals("ACCOUNT")) {
									link = "/account-portal/control/viewAccount?partyId=" + pId;
									roleTypeId = "Account";
								}
								String personName = gName + " " + "-" + " " + roleTypeId;
								SnameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
							}
						}

						if (partyAndZipcode.size() > 1) {
							data.put("Error_Message", "ZIP Code and Name match exists for below parties:</br> " + "1. "
									+ nameUrl + "</br>" + "2. " + SnameUrl + "</br>" + message);
						} else {
							data.put("Error_Message", "ZIP Code and Name match exists for below parties:</br> " + "1. "
									+ nameUrl + "</br>" + message);
						}
					} else {
						// data.put("Error_Message", "ZIP Code and Name match
						// already exists in system. Do you want to update ?");
					}
				}

			}
			// validation for Contact Name
			else {
				if ((!fName.equals(firstName) && !lName.equals(lastName)) || !generalPostalCode.equals(pCode)) {
					List<GenericValue> partyAndZipcode = delegator.findByAnd(
							"PartySummaryDetailsView", UtilMisc.toMap("primaryPostalCode", generalPostalCode,
									"firstName", firstName, "lastName", lastName),
							UtilMisc.toList("createdDate DESC"), false);
					if (UtilValidate.isNotEmpty(partyAndZipcode)) {

						GenericValue partyAndZipcodeExp = EntityUtil.getFirst((partyAndZipcode));
						matchCount = matchCount + 1;
						String latestPartyId = partyAndZipcodeExp.getString("partyId");
						// Debug.log("latestPartyId==="+latestPartyId);
						GenericValue pCMGV = partyAndZipcode.get(0);
						if (UtilValidate.isNotEmpty(pCMGV)) {
							firstName = pCMGV.getString("firstName");
							lastName = pCMGV.getString("lastName");
							String roleTypeId = pCMGV.getString("roleTypeId");
							String pId = pCMGV.getString("partyId");
							if (roleTypeId.equals("CUSTOMER")) {
								link = "/customer-portal/control/viewCustomer?partyId=" + pId;
								roleTypeId = "Customer";
							}
							if (roleTypeId.equals("CONTACT")) {
								link = "/contact-portal/control/viewContact?partyId=" + pId;
								roleTypeId = "Contact";
							}
							String personName = firstName + "" + lastName + " " + "-" + " " + roleTypeId;
							nameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
						}

						if (partyAndZipcode.size() > 1) {
							pCMGV1 = partyAndZipcode.get(1);
						}
						if (UtilValidate.isNotEmpty(pCMGV1)) {
							firstName = pCMGV1.getString("firstName");
							lastName = pCMGV1.getString("lastName");
							String roleTypeId = pCMGV1.getString("roleTypeId");
							String pId = pCMGV1.getString("partyId");
							if (roleTypeId.equals("CUSTOMER")) {
								link = "/customer-portal/control/viewCustomer?partyId=" + pId;
								roleTypeId = "Customer";
							}
							if (roleTypeId.equals("CONTACT")) {
								link = "/contact-portal/control/viewContact?partyId=" + pId;
								roleTypeId = "Contact";
							}
							String personName = firstName + "" + lastName + " " + "-" + " " + roleTypeId;
							SnameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";
						}

						if (partyAndZipcode.size() > 1) {
							data.put("Error_Message", "ZIP Code and Name match exists for below parties:</br> " + "1. "
									+ nameUrl + "</br>" + "2. " + SnameUrl + "</br>" + message);
						} else {
							data.put("Error_Message", "ZIP Code and Name match exists for below parties:</br> " + "1. "
									+ nameUrl + "</br>" + message);
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		if (data.isEmpty()) {
			data.put("Error_Message", "NO_RECORDS");
			return doJSONResponse(response, data);
		}
		return doJSONResponse(response, data);
	}

	// get DuplicatePhoneNumber

	public static String getDuplicatePhoneNumber(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> data = FastMap.newInstance();
		String requestUri = request.getParameter("requestUri");
		String primaryPhoneNumber = request.getParameter("primaryPhoneNumber");
		String accType = request.getParameter("accType");
		String partyId = request.getParameter("partyId");
		String screenType = request.getParameter("screenType");
		Integer dupCount = 0;
		Integer matchCount = 0;
		List<GenericValue> matchRecodsGv = null;
		List<GenericValue> partyNameAndZipcode = null;
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String gName = "";
		String link = "";
		String SnameUrl = "";
		String firstName = "";
		String lastName = "";
		String nameUrl = "";
		GenericValue pCM = null;
		String CurrentcontactNum = null;
		String cMnumber = "Y";
		String toValidate = "N";
		try {

			// check current value
			GenericValue Currentcontact = EntityQuery.use(delegator)
					.from("PartyContactDetailByPurpose").where("contactMechTypeId", "TELECOM_NUMBER",
							"contactMechPurposeTypeId", "PHONE_MOBILE", "partyId", partyId, "purposeThruDate", null)
					.orderBy("-fromDate").queryOne();
			if (UtilValidate.isNotEmpty(Currentcontact)) {
				CurrentcontactNum = Currentcontact.getString("contactNumber");
				cMnumber = "N";
				if (!primaryPhoneNumber.equals(CurrentcontactNum)) {
					toValidate = "Y";
				}
			}
			if (cMnumber.equals("Y")) {
				toValidate = "Y";
			}

			// validation for phone number
			if (toValidate.equals("Y")) {
				List<GenericValue> contact = EntityQuery.use(delegator)
						.from("PartyContactDetailByPurpose").where("contactNumber", primaryPhoneNumber,
								"contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PHONE_MOBILE")
						.orderBy("-fromDate").queryList();
				if (UtilValidate.isNotEmpty(contact)) {
					// get first record
					GenericValue pCMGV = contact.get(0);
					if (UtilValidate.isNotEmpty(pCMGV)) {
						String pId = pCMGV.getString("partyId");
						GenericValue pCMGV1 = EntityQuery.use(delegator).from("PartySummaryDetailsView")
								.where("partyId", pId).orderBy("-createdDate").queryFirst();
						// Check existing email
						gName = pCMGV1.getString("groupName");
						String roleTypeId = pCMGV1.getString("roleTypeId");
						if (UtilValidate.isNotEmpty(roleTypeId)) {

							if (roleTypeId.equals("LEAD")) {
								link = "/lead-portal/control/viewLead?partyId=" + pId;
								roleTypeId = "Lead";
							}
							if (roleTypeId.equals("ACCOUNT")) {
								link = "/account-portal/control/viewAccount?partyId=" + pId;
								roleTypeId = "Account";
							}
							String personName = gName + " " + "-" + " " + roleTypeId;
							nameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";

							if (roleTypeId.equals("CONTACT")) {
								/*
								 * firstName = pCMGV.getString("firstName");
								 * lastName = pCMGV.getString("lastName");
								 * String personCName =
								 * firstName+""+lastName+" "+"-"+" "+"Contact";
								 */
								String name = PartyHelper.getPartyName(delegator, pId, false);
								String personCName = name + " " + "-" + " " + "Contact";

								link = "/contact-portal/control/viewContact?partyId=" + pId;
								nameUrl = "<a href='" + link + "' target='_blank'>" + personCName + "</a>";
							}
							if (roleTypeId.equals("CUSTOMER")) {
								String name = PartyHelper.getPartyName(delegator, pId, false);
								String personCName = name + " " + "-" + " " + "Customer";
								link = "/customer-portal/control/viewCustomer?partyId=" + pId;
								nameUrl = "<a href='" + link + "' target='_blank'>" + personCName + "</a>";
							}

						}

					}

					if (contact.size() > 1) {
						pCM = contact.get(1);
					}
					if (UtilValidate.isNotEmpty(pCM)) {
						String pId = pCM.getString("partyId");
						GenericValue pCMGV1 = EntityQuery.use(delegator).from("PartySummaryDetailsView")
								.where("partyId", pId).orderBy("-createdDate").queryFirst();
						// Check existing email
						gName = pCMGV1.getString("groupName");
						String roleTypeId = pCMGV1.getString("roleTypeId");
						if (UtilValidate.isNotEmpty(roleTypeId)) {

							if (roleTypeId.equals("LEAD")) {
								link = "/lead-portal/control/viewLead?partyId=" + pId;
								roleTypeId = "Lead";
							}
							if (roleTypeId.equals("ACCOUNT")) {
								link = "/account-portal/control/viewAccount?partyId=" + pId;
								roleTypeId = "Account";
							}
							String personName = gName + " " + "-" + " " + roleTypeId;
							SnameUrl = "<a href='" + link + "' target='_blank'>" + personName + "</a>";

							if (roleTypeId.equals("CONTACT")) {
								/*
								 * firstName = pCMGV.getString("firstName");
								 * lastName = pCMGV.getString("lastName");
								 * String personCName =
								 * firstName+""+lastName+" "+"-"+" "+"Contact";
								 */
								String name = PartyHelper.getPartyName(delegator, pId, false);
								String personCName = name + " " + "-" + " " + "Contact";

								link = "/contact-portal/control/viewContact?partyId=" + pId;
								SnameUrl = "<a href='" + link + "' target='_blank'>" + personCName + "</a>";
							}
							if (roleTypeId.equals("CUSTOMER")) {
								String name = PartyHelper.getPartyName(delegator, pId, false);
								String personCName = name + " " + "-" + " " + "Customer";
								link = "/customer-portal/control/viewCustomer?partyId=" + pId;
								SnameUrl = "<a href='" + link + "' target='_blank'>" + personCName + "</a>";
							}
						}
					}
					if (screenType.equals("CREATE")) {
						if (contact.size() > 1) {
							data.put("Error_Message",
									"Phone Number already exists for below parties:</br> " + "1. " + nameUrl + "</br>"
											+ "2. " + SnameUrl + "</br>Still want to continue with the create?");
						} else {
							data.put("Error_Message", "Phone Number already exists for below party:</br> " + "1. "
									+ nameUrl + "</br>" + "Still want to continue with the create?");
						}
					} else {
						if (contact.size() > 1) {
							data.put("Error_Message",
									"Phone Number already exists for below parties:</br> " + "1. " + nameUrl + "</br>"
											+ "2. " + SnameUrl + "</br>Still want to continue with the update?");
						} else {
							data.put("Error_Message", "Phone Number already exists for below party:</br> " + "1. "
									+ nameUrl + "</br>" + "Still want to continue with the update?");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		if (data.isEmpty()) {
			data.put("Error_Message", "NO_RECORDS");
			return doJSONResponse(response, data);
		}
		return doJSONResponse(response, data);
	}

	@SuppressWarnings("unchecked")
	public static String getTemplatesData(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();

		String templateType = request.getParameter("templateType");
		String tempalateName = request.getParameter("tempalateName");
		String templateCategories = request.getParameter("templateCategories");
		String emailEngine = request.getParameter("emailEngine");
		String searchKeyword = request.getParameter("searchText");
		String marketingCampaignId = request.getParameter("marketingCampaignId");
		
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("templateType", templateType);
			inputMap.put("tempalateName", tempalateName);
			inputMap.put("templateCategories", templateCategories);
			inputMap.put("emailEngine", emailEngine);
			inputMap.put("searchKeyword", searchKeyword);
			inputMap.put("marketingCampaignId", marketingCampaignId);
			
			Map<String, Object> requestContext = new HashMap<String, Object>();
			requestContext.putAll(context);
			
			inputMap.put("requestContext", requestContext);
			inputMap.put("userLogin", userLogin);

			Map<String, Object> res = dispatcher.runSync("common.getTemplatesData", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
				result.put("data", results);
			} else {
				String errMsg = (String) res.get("errorMsg");
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				result.put("data", new ArrayList<Map<String, Object>>());
				return doJSONResponse(response, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log("error--"+e.getMessage());
			result.put("data", new ArrayList<Map<String, Object>>());
			return doJSONResponse(response, result);
		}
		return doJSONResponse(response, result);
	}

	@SuppressWarnings("unchecked")
	public static String getCampaignDetails(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		String profilePartyId = request.getParameter("profilePartyId");
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			Map<String, Object> campaignMap = new HashMap<String, Object>();
			Map<String, Object> campaignContactListMap = new HashMap<String, Object>();
			
			List<EntityCondition> exprListForParametersAL = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(profilePartyId)) {
				
				exprListForParametersAL
				.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, profilePartyId));
				
				/*String validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(profilePartyId, UtilMisc.toList("ACCOUNT"),
						delegator);
				if (UtilValidate.isEmpty(validRoleTypeId)) {
					validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(profilePartyId, UtilMisc.toList("LEAD"),
							delegator);
				}
				if (UtilValidate.isEmpty(validRoleTypeId)) {
					validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(profilePartyId,
							UtilMisc.toList("CONTACT"), delegator);
				}
				if (UtilValidate.isEmpty(validRoleTypeId)) {
					validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(profilePartyId,
							UtilMisc.toList("CUSTOMER"), delegator);
				}
				
				if (UtilValidate.isNotEmpty(validRoleTypeId) 
						&& (validRoleTypeId.equals("CONTACT") || validRoleTypeId.equals("CUSTOMER"))) {
					exprListForParametersAL
					.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, profilePartyId));
				} else {
					exprListForParametersAL
					.add(EntityCondition.makeCondition("acctPartyId", EntityOperator.EQUALS, profilePartyId));
				}*/
			}
			
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			Debug.log("exprListForParametersAL===START=======" + exprListForParametersAL);
			List<GenericValue> campaignCLPAL = delegator.findList("CampaignContactListPartyAndContact",
					EntityCondition.makeCondition(exprListForParametersAL, EntityOperator.AND), null, null, efo, false);
			List<String> contactListIds = EntityUtil.getFieldListFromEntityList(campaignCLPAL, "contactListId", true);
			Debug.log("contactListIds==========" + contactListIds);
			exprListForParametersAL.clear();
			if (UtilValidate.isNotEmpty(contactListIds)) {
				exprListForParametersAL.add(EntityCondition.makeCondition("contactListId", EntityOperator.IN, contactListIds));
				
				Set<String> fieldToSelect = new HashSet<String>();
				fieldToSelect.add("startDate");
				fieldToSelect.add("endDate");
				fieldToSelect.add("marketingCampaignId");
				fieldToSelect.add("campaignName");
				fieldToSelect.add("campaignCode");
				Debug.log("exprListForParametersAL====campaign and contact======" + exprListForParametersAL);
				List<GenericValue> campaignCLL = delegator.findList("MarketingCampaignContactList",
						EntityCondition.makeCondition(exprListForParametersAL, EntityOperator.AND), null, null, efo,
						false);
				List<String> campaignIds = EntityUtil.getFieldListFromEntityList(campaignCLL, "marketingCampaignId", true);
				Debug.log("campaignIds=====campaingIds====" + campaignIds);
				exprListForParametersAL.clear();
				if (UtilValidate.isNotEmpty(campaignIds))
					exprListForParametersAL
					.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.IN, campaignIds));
				List<GenericValue> CampaignDetails = delegator.findList("MarketingCampaign",
						EntityCondition.makeCondition(exprListForParametersAL, EntityOperator.AND), fieldToSelect, null, efo, false);
				if (UtilValidate.isNotEmpty(CampaignDetails)) {
					for (GenericValue entry : CampaignDetails) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("campaignName", entry.getString("campaignName"));
						data.put("campaignCode", entry.getString("campaignCode"));
						data.put("startDate", entry.getString("startDate"));
						data.put("endDate", entry.getString("endDate"));
						campaignMap.put(entry.getString("marketingCampaignId"), data);
					}
				}
				if (UtilValidate.isNotEmpty(campaignCLL)) {
					for (GenericValue entry : campaignCLL) {
						campaignContactListMap.put(entry.getString("contactListId"),
								entry.getString("marketingCampaignId"));
					}
				}
			}
			Debug.log("campaignContactListMap==========" + campaignContactListMap);
			if (UtilValidate.isNotEmpty(campaignCLPAL)) {
				for (GenericValue entry : campaignCLPAL) {
					Map<String, Object> data = new HashMap<String, Object>();
					
					Map<String, Object> mrktDetailMap = new HashMap<String, Object>();
					
					if (UtilValidate.isNotEmpty(campaignContactListMap.get(entry.getString("contactListId")))
							&& UtilValidate.isNotEmpty(campaignMap.get(campaignContactListMap.get(entry.getString("contactListId"))))
							) {
						mrktDetailMap = (Map<String, Object>) campaignMap.get(campaignContactListMap.get(entry.getString("contactListId")));
					}
					Debug.log("mrktDetailMap==========" + mrktDetailMap);
					
					if (UtilValidate.isNotEmpty(mrktDetailMap)) {
						data.put("campaignCode", mrktDetailMap.get("campaignCode"));
						data.put("startDate", mrktDetailMap.get("startDate"));
						data.put("endDate", mrktDetailMap.get("endDate"));
						data.put("campaignName", mrktDetailMap.get("campaignName"));
					}
					
					if (UtilValidate.isNotEmpty(campaignContactListMap.get(entry.getString("contactListId")))) {
						data.put("marketingCampaignId", campaignContactListMap.get(entry.getString("contactListId")));
					}
					
					data.put("opened", UtilValidate.isNotEmpty(entry.getString("opened")) ? entry.getString("opened") : "0");
					data.put("clicked", UtilValidate.isNotEmpty(entry.getString("clicked")) ? entry.getString("clicked") : "0");
					data.put("notOpened", UtilValidate.isNotEmpty(entry.getString("notOpen")) ? entry.getString("notOpen") : "0");
					data.put("bounced", UtilValidate.isNotEmpty(entry.getString("bounced")) ? entry.getString("bounced") : "0");
					data.put("subscribed", UtilValidate.isNotEmpty(entry.getString("subscribed")) ? entry.getString("subscribed") : "0");
					data.put("unSubscribed", UtilValidate.isNotEmpty(entry.getString("unsubscribed")) ? entry.getString("unsubscribed") : "0");
					data.put("sent", UtilValidate.isNotEmpty(entry.getString("sent")) ? entry.getString("sent") : "");
					
					String name = PartyHelper.getPartyName(delegator, entry.getString("partyId"), false);
					/*name += entry.getString("firstName");
					if (UtilValidate.isNotEmpty(entry.getString("lastName")))
						name += " " + entry.getString("lastName");*/
					data.put("contactName", name);
					
					resultsList.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, resultsList);
	}

	public static String createPartyContactMechPurpose(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		// Debug.logInfo(new Exception(), "In createPartyContactMechPurpose
		// context: " + context, module);
		Map<String, Object> result = new HashMap<String, Object>();
		// required parameters
		String partyId = request.getParameter("partyId");
		String contactMechId = request.getParameter("contactMechId");
		String contactMechPurposeTypeId = request.getParameter("contactMechPurposeTypeId");
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		String errMsg = null;
		GenericValue tempVal = null;
		try {
			tempVal = EntityQuery.use(delegator).from("PartyContactWithPurpose")
					.where("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId",
							contactMechPurposeTypeId)
					.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
					.queryFirst();
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), MODULE);
			tempVal = null;
		}

		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.nowTimestamp();
		}

		if (tempVal != null) {
			// exists already with valid date, show warning
			errMsg = UtilProperties.getMessage(resourceError,
					"contactmechservices.could_not_create_new_purpose_already_exists", locale);
			// errMsg += ": " + tempVal.getPrimaryKey().toString();
			return errMsg;
		} else {
			// no entry with a valid date range exists, create new with open
			// thruDate
			GenericValue newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId",
							contactMechPurposeTypeId, "contactMechId", contactMechId, "fromDate", fromDate));
			try {
				delegator.create(newPartyContactMechPurpose);
			} catch (GenericEntityException e) {
				Debug.logWarning(e.getMessage(), MODULE);
				return "errMessage";
			}
		}
		result.put("fromDate", fromDate);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String searchCustomers(HttpServletRequest request, HttpServletResponse response) {
	    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	    LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
	    List < Map < String, Object >> dataList = new ArrayList < Map < String, Object >> ();
	    Map<String, Object> result = new HashMap<String, Object>();
	    
	    Map < String, Object > context = UtilHttp.getCombinedMap(request);

	    String domainEntityType = request.getParameter("domainEntityType");
	    String domainEntityId = request.getParameter("domainEntityId");
	    String partyId = request.getParameter("customerId");
	    String firstName = request.getParameter("firstName");
	    String lastName = request.getParameter("lastName");
	    String emailAddress = request.getParameter("emailAddress");
	    String contactNumber = request.getParameter("contactNumber");
	    String stateProvinceGeoId = request.getParameter("generalStateProvinceGeoId");
	    String countryGeoId = request.getParameter("generalCountryGeoId");
	    String dataSourceId = request.getParameter("dataSourceId");
	    String toName = request.getParameter("toName");
	    String address1 = request.getParameter("address1");
	    String city = request.getParameter("city");
	    String postalCode = request.getParameter("postalCode");
	    String searchType = request.getParameter("searchType");
	    String loginPartyId = request.getParameter("loginPartyId");
	    //String userLoginId = request.getParameter("userLoginId");
	    String roleTypeId = request.getParameter("roleTypeId");
	    String isContractor = request.getParameter("isContractor");
	    String isHomeOwner = request.getParameter("isHomeOwner");
	    String loyaltyId = request.getParameter("loyaltyId");
	    String searchText = request.getParameter("searchText");
	    String externalId = (String) context.get("externalId");
	    String externalLoginKey = (String) context.get("externalLoginKey");
	    String ignoreOrderCustomerBy = (String) context.get("ignoreOrderBy");
	    String personResponsibleFor = (String) context.get("personResponsibleFor");
	    String callBackDate = (String) context.get("callBackDate");
	    String productStoreId = (String) context.get("productStoreId");
	    String crossRefName = (String) context.get("attrName");
	    String crossRefValue = (String) context.get("attrValue");
	    String isExportAction = (String) request.getAttribute("isExportAction");
	    
		String showCallListStateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SHOW_CALL_LIST_STATE_ID", "N");
		String showCallListEnumCode = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SHOW_CALL_LIST_ENUM_CODE", "N");
		
		if(UtilValidate.isNotEmpty(userLogin)) {
			userLogin = (GenericValue) context.get("userLogin");
		}
		
		String userLoginId = UtilValidate.isNotEmpty(userLogin) ? userLogin.getString("userLoginId") : request.getParameter("userLoginId");
		boolean hasFullAccess = org.fio.homeapps.util.DataUtil.hasFullPermission(delegator, userLoginId);
		
		boolean ignoreOrderBy = false;
		if(UtilValidate.isNotEmpty(ignoreOrderCustomerBy) && "Y".equals(ignoreOrderCustomerBy)) {
			ignoreOrderBy = true;
		}
		
	    //Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	    List < GenericValue > resultList = null;
	    long start = System.currentTimeMillis();
	    int viewIndex = 0;
		int highIndex = 0;
		int lowIndex = 0;
		int resultListSize = 0;
		int viewSize = 0;
	    try {
	        // Integrate security matrix logic start
	        // String userLoginId = userLogin.getString("userLoginId");
	        String accessLevel = "Y";
	        String businessUnit = null;
	        Debug.log("userLoginId==== :" + userLoginId);
	        Map < String, Object > accessMatrixRes = new HashMap < String, Object > ();
	        if (UtilValidate.isNotEmpty(userLoginId) && !hasFullAccess) {

	            String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
	            businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
	            Map < String, Object > accessMatrixMap = new LinkedHashMap < String, Object > ();
	            accessMatrixMap.put("delegator", delegator);
	            accessMatrixMap.put("dispatcher", dispatcher);
	            accessMatrixMap.put("businessUnit", businessUnit);
	            accessMatrixMap.put("modeOfOp", "Read");
	            accessMatrixMap.put("entityName", "Customer");
	            accessMatrixMap.put("userLoginId", userLoginId);
	            Debug.log("accessMatrixMap==== :" + accessMatrixMap);
	            accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
	            Debug.log("accessMatrixRes==== :" + accessMatrixRes);
	            if (UtilValidate.isNotEmpty(accessMatrixRes)) {
	                accessLevel = (String) accessMatrixRes.get("accessLevel");
	            } else {
	                accessLevel = null;
	            }
	        }
	        if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
	            List < EntityCondition > conditions = new ArrayList < EntityCondition > ();
	            if (UtilValidate.isEmpty(roleTypeId)) {
	                roleTypeId = "CUSTOMER";
	            }
	            
	            List < String > partyIds = null;
	            
	            // construct role conditions
	            EntityCondition roleTypeCondition = EntityCondition
	                .makeCondition(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
	            conditions.add(roleTypeCondition);

	            // check with ownerId
	            if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
	                @SuppressWarnings("unchecked")
	                List < String > ownerIds = (List < String > ) accessMatrixRes.get("ownerId");
	                conditions.add(EntityCondition.makeCondition("ownerId", EntityOperator.IN, ownerIds));
	            }

	            // check with emplTeamId
	            if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
	                @SuppressWarnings("unchecked")
	                List < String > emplTeamIds = (List < String > ) accessMatrixRes.get("emplTeamId");
	                conditions.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
	            }

	            /*EntityCondition partyStatusCondition = EntityCondition
	                .makeCondition(
	                    UtilMisc.toList(
	                        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,
	                            "PARTY_DISABLED"),
	                        EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)),
	                    EntityOperator.OR);*/
	            conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));

	            if (UtilValidate.isNotEmpty(partyId)) {
	                EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.LIKE, partyId + "%");
	                conditions.add(partyCondition);
	            }

	            if (UtilValidate.isNotEmpty(firstName)) {
	                EntityCondition firstNameCondition = EntityCondition.makeCondition("firstName", EntityOperator.LIKE,
	                    firstName + "%");
	                conditions.add(firstNameCondition);
	            }
	            if (UtilValidate.isNotEmpty(lastName)) {
	                EntityCondition lastNameCondition = EntityCondition.makeCondition("lastName", EntityOperator.LIKE,
	                    lastName + "%");
	                conditions.add(lastNameCondition);
	            }
	            if (UtilValidate.isNotEmpty(searchText)) {
	                EntityCondition nameCondition = EntityCondition
	                    .makeCondition(
	                        UtilMisc.toList(
	                            EntityCondition.makeCondition("firstName", EntityOperator.LIKE, searchText + "%"),
	                            EntityCondition.makeCondition("lastName", EntityOperator.LIKE, searchText + "%")
	                        ),
	                        EntityOperator.OR);
	                conditions.add(nameCondition);
	            }
	            
	            if (UtilValidate.isNotEmpty(callBackDate)) {
	            	Date callBackDate1 = new SimpleDateFormat("MM/dd/yyyy").parse(callBackDate);
	            	callBackDate = sdf.format(callBackDate1);
	            	conditions.add(EntityCondition.makeCondition("callBackDate", EntityOperator.EQUALS,
	            			java.sql.Date.valueOf(callBackDate)));
	            }

	            if (UtilValidate.isNotEmpty(dataSourceId)) {
	                EntityCondition lastNameCondition = EntityCondition.makeCondition("dataSourceId",
	                    EntityOperator.EQUALS, dataSourceId);
	                conditions.add(lastNameCondition);
	            }
	            if (UtilValidate.isNotEmpty(toName)) {
	                EntityCondition toNameCondition = EntityCondition.makeCondition("toName",
	                    EntityOperator.LIKE, toName + "%");
	                conditions.add(toNameCondition);
	            }
	            if (UtilValidate.isNotEmpty(address1)) {
	                EntityCondition address1Condition = EntityCondition.makeCondition("address1",
	                    EntityOperator.LIKE, address1 + "%");
	                conditions.add(address1Condition);
	            }
	            if (UtilValidate.isNotEmpty(countryGeoId)) {
	                EntityCondition countryCondition = EntityCondition.makeCondition("countryGeoId",
	                    EntityOperator.EQUALS, countryGeoId);
	                conditions.add(countryCondition);
	            }
	            if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
	                EntityCondition stateCondition = EntityCondition.makeCondition("stateProvinceGeoId",
	                    EntityOperator.EQUALS, stateProvinceGeoId);
	                conditions.add(stateCondition);
	            }
	            if (UtilValidate.isNotEmpty(postalCode)) {
	                EntityCondition postalCodeCondition = EntityCondition.makeCondition("postalCode",
	                    EntityOperator.LIKE, "%" + postalCode + "%");
	                conditions.add(postalCodeCondition);
	            }
	            if (UtilValidate.isNotEmpty(city)) {
	                EntityCondition cityCondition = EntityCondition.makeCondition("city", EntityOperator.LIKE, city + "%");
	                conditions.add(cityCondition);
	            }
	            if (UtilValidate.isNotEmpty(loyaltyId)) {
	                EntityCondition loyaltyCondition = EntityCondition.makeCondition("loyaltyId", EntityOperator.LIKE, loyaltyId + "%");
	                conditions.add(loyaltyCondition);
	            }
	            if (UtilValidate.isNotEmpty(externalId)) {
	                conditions.add(EntityCondition.makeCondition("externalId", EntityOperator.LIKE, externalId + "%"));
	            }
	            
	            List < EntityCondition > eventExprs = new LinkedList < EntityCondition > ();
	            if (UtilValidate.isNotEmpty(emailAddress) || UtilValidate.isNotEmpty(contactNumber)) {
	                if (UtilValidate.isNotEmpty(emailAddress)) {
	                    eventExprs.add(
	                        EntityCondition.makeCondition("infoString", EntityOperator.LIKE, emailAddress + "%"));
	                }

	                if (UtilValidate.isNotEmpty(contactNumber)) {
	                    eventExprs.add(EntityCondition.makeCondition("contactNumber", EntityOperator.LIKE, contactNumber + "%"));
	                }
	                conditions.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
	            }
	            
	            String userLoginParty = loginPartyId;
	            if (UtilValidate.isNotEmpty(userLogin)) {
	                userLoginParty = userLogin.getString("partyId");
	            }
	            
	            if ((UtilValidate.isNotEmpty(searchType) && searchType.equals("my-active-customer")) || UtilValidate.isNotEmpty(personResponsibleFor)) {
	            	if (UtilValidate.isEmpty(personResponsibleFor)) {
	            		personResponsibleFor = userLoginParty;
	            	}
	            	
	            	List<EntityCondition> partyRelationshipConditionList = FastList.newInstance();
	            	if(!hasFullAccess) {
	            		partyRelationshipConditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
	            				EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, personResponsibleFor),
								EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeId),
			                    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
			                    EntityUtil.getFilterByDateExpr()
								));
						
						EntityCondition partyRelationshipCondition = EntityCondition.makeCondition(partyRelationshipConditionList, EntityOperator.AND);
						
						Set<String> fieldToSelect = new LinkedHashSet<>();
						fieldToSelect.add("partyIdFrom");
						
						List<GenericValue> partyRelationshipList = delegator.findList("PartyRelationship", partyRelationshipCondition, fieldToSelect, null, null, false);
						partyIds = UtilValidate.isNotEmpty(partyRelationshipList) ? EntityUtil.getFieldListFromEntityList(partyRelationshipList, "partyIdFrom", true) : new ArrayList<>();
	            	}
	            } else if (UtilValidate.isNotEmpty(searchType) && searchType.equals("my-team-customer")) {
	                List teamCondList = FastList.newInstance();

	                EntityCondition teamLeadCondition = EntityCondition.makeCondition(
	                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginParty));
	                teamCondList.add(teamLeadCondition);
	                EntityCondition teamConditons = EntityCondition.makeCondition(teamCondList, EntityOperator.AND);
	                List < GenericValue > teamList = delegator.findList("EmplPositionFulfillment", teamConditons, null,
	                    null, null, false);
	                partyIds = null;
	                if (UtilValidate.isNotEmpty(teamList)) {
	                    GenericValue emplPosition = teamList.get(0);
	                    String isTeamLead = emplPosition.getString("isTeamLead");
	                    if (UtilValidate.isNotEmpty(isTeamLead) && "Y".equals(isTeamLead)) {
	                        List teamMemberCondList = FastList.newInstance();
	                        EntityCondition teamMemberCondition = EntityCondition.makeCondition(
	                            EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLoginParty));
	                        teamMemberCondList.add(teamMemberCondition);
	                        EntityCondition teamMemberConditons = EntityCondition.makeCondition(teamMemberCondList,
	                            EntityOperator.AND);
	                        List < GenericValue > teamMemberList = delegator.findList("EmplPositionFulfillment",
	                            teamMemberConditons, null, null, null, false);
	                        partyIds = EntityUtil.getFieldListFromEntityList(teamMemberList, "partyId", true);
	                    }
	                }
	                if (UtilValidate.isEmpty(partyIds)) {
	                    conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, null));
	                }
	            }
	            
	            if (UtilValidate.isNotEmpty(partyIds)) {
	            	conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
	            }

	            if (UtilValidate.isNotEmpty(isContractor)) {
	                if (isContractor.equals("Y")) {
	                    conditions.add(EntityCondition.makeCondition("supplementalPartyTypeId", EntityOperator.EQUALS,
	                        "CONTRACTOR"));
	                } else if (isContractor.equals("N")) {
	                    conditions.add(
	                        EntityCondition.makeCondition("supplementalPartyTypeId", EntityOperator.EQUALS, null));
	                }
	            }
	            if (UtilValidate.isNotEmpty(isHomeOwner) && isHomeOwner.equals("Y")) {
	                conditions
	                    .add(EntityCondition.makeCondition("supplementalPartyTypeId", EntityOperator.EQUALS, null));
	            }
	            
	            boolean isAttrSearch = false;
	            if(UtilValidate.isNotEmpty(crossRefName) && UtilValidate.isNotEmpty(crossRefValue)) {
	            	isAttrSearch = true;
	            	conditions.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, crossRefName));
	            	conditions.add(EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, crossRefValue));
	            }
	            
	            if(UtilValidate.isNotEmpty(productStoreId)){
	            	conditions.add(EntityCondition.makeCondition(UtilMisc.toList(
			            				EntityCondition.makeCondition("assignedStore",productStoreId),
			            				EntityCondition.makeCondition("loyaltyStoreId",productStoreId),
			            				EntityCondition.makeCondition("localStorePreference",productStoreId)),
	            			EntityOperator.OR));
	            }
	            
	            EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	            Debug.logInfo("searchCustomers mainConditons: " + mainConditons, MODULE);

	            if (UtilValidate.isNotEmpty(mainConditons)) {
	            	
	                boolean isPostalSearch = false;
	                if (UtilValidate.isNotEmpty(address1) || UtilValidate.isNotEmpty(toName) || UtilValidate.isNotEmpty(countryGeoId) || UtilValidate.isNotEmpty(stateProvinceGeoId) || UtilValidate.isNotEmpty(postalCode) || UtilValidate.isNotEmpty(city)) {
	                	isPostalSearch = true;
	                }
	                
	                DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
		            dynamicViewEntity.addMemberEntity("P", "Party");
		            dynamicViewEntity.addAlias("P", "partyId", "partyId", null, false, true, null);
		            dynamicViewEntity.addAlias("P", "statusId");
		            dynamicViewEntity.addAlias("P", "roleTypeId");
		            dynamicViewEntity.addAlias("P", "preferredCurrencyUomId");
		            dynamicViewEntity.addAlias("P", "timeZoneDesc");
		            dynamicViewEntity.addAlias("P", "emplTeamId");
		            dynamicViewEntity.addAlias("P", "ownerId");
		            dynamicViewEntity.addAlias("P", "externalId");
		            dynamicViewEntity.addAlias("P", "dataSourceId");
		            dynamicViewEntity.addAlias("P", "createdStamp");
		            dynamicViewEntity.addAlias("P", "createdTxStamp");
		            dynamicViewEntity.addAlias("P", "createdDate");
		            
		            //if (UtilValidate.isNotEmpty(loyaltyId)) {
			            dynamicViewEntity.addMemberEntity("PER", "Person");
			            dynamicViewEntity.addAlias("PER", "firstName");
			            dynamicViewEntity.addAlias("PER", "middleName");
			            dynamicViewEntity.addAlias("PER", "lastName");
			            dynamicViewEntity.addAlias("PER", "callBackDate");
			            dynamicViewEntity.addAlias("PER", "personalTitle");
			            dynamicViewEntity.addAlias("PER", "designation");
			            dynamicViewEntity.addAlias("PER", "birthDate");
			            dynamicViewEntity.addAlias("PER", "loyaltyId");
			            dynamicViewEntity.addAlias("PER", "assignedStore");
			            dynamicViewEntity.addAlias("PER", "loyaltyStoreId");
			            dynamicViewEntity.addAlias("PER", "localStorePreference");
			            dynamicViewEntity.addViewLink("P", "PER", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
		            //}
		            
		            dynamicViewEntity.addMemberEntity("PSD", "PartySupplementalData");
		            dynamicViewEntity.addAlias("PSD", "uploadedByUserLoginId");
		            dynamicViewEntity.addAlias("PSD", "departmentName");
		            dynamicViewEntity.addAlias("PSD", "ownershipEnumId");
		            dynamicViewEntity.addAlias("PSD", "industryEnumId");
		            dynamicViewEntity.addAlias("PSD", "annualRevenue");
		            dynamicViewEntity.addAlias("PSD", "sicCode");
		            dynamicViewEntity.addAlias("PSD", "numberEmployees");
		            dynamicViewEntity.addAlias("PSD", "supplementalPartyTypeId");
		            dynamicViewEntity.addAlias("PSD", "primaryPostalAddressId");
		            dynamicViewEntity.addAlias("PSD", "primaryTelecomNumberId");
		            dynamicViewEntity.addAlias("PSD", "primaryEmailId");
		            dynamicViewEntity.addViewLink("P", "PSD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
		            
		            if (isPostalSearch) {
			            dynamicViewEntity.addMemberEntity("PA", "PostalAddress");
			            dynamicViewEntity.addAlias("PA", "toName"); 
			            dynamicViewEntity.addAlias("PA", "attnName");
			            dynamicViewEntity.addAlias("PA", "address1");
			            dynamicViewEntity.addAlias("PA", "address2");
			            dynamicViewEntity.addAlias("PA", "directions");
			            dynamicViewEntity.addAlias("PA", "city");
			            dynamicViewEntity.addAlias("PA", "postalCode");
			            dynamicViewEntity.addAlias("PA", "postalCodeExt");
			            dynamicViewEntity.addAlias("PA", "countryGeoId");
			            dynamicViewEntity.addAlias("PA", "stateProvinceGeoId");
			            dynamicViewEntity.addAlias("PA", "countyGeoId");
			            dynamicViewEntity.addAlias("PA", "postalCodeGeoId");
			            dynamicViewEntity.addViewLink("PSD", "PA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryPostalAddressId", "contactMechId"));
		            }
		            
		            if (UtilValidate.isNotEmpty(contactNumber)) {
			            dynamicViewEntity.addMemberEntity("TN", "TelecomNumber");
			            dynamicViewEntity.addAlias("TN", "countryCode");
			            dynamicViewEntity.addAlias("TN", "areaCode");
			            dynamicViewEntity.addAlias("TN", "contactNumber");
			            dynamicViewEntity.addAlias("TN", "askForName");
			            dynamicViewEntity.addViewLink("PSD", "TN", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryTelecomNumberId", "contactMechId"));
		            }
		            
		            if (UtilValidate.isNotEmpty(emailAddress)) {
		            	dynamicViewEntity.addMemberEntity("CM", "ContactMech");
			            dynamicViewEntity.addAlias("CM", "infoString");
			            dynamicViewEntity.addViewLink("PSD", "CM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryEmailId", "contactMechId"));
		            }
		            
		            if(isAttrSearch) {
		            	dynamicViewEntity.addMemberEntity("PTA", "PartyAttribute");
		            	dynamicViewEntity.addAlias("PTA", "attrName");
		            	dynamicViewEntity.addAlias("PTA", "attrValue");
		            	dynamicViewEntity.addViewLink("P", "PTA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
		            }
		            
	                // set the page parameters
		            GenericValue systemProperty = EntityQuery.use(delegator).select("systemPropertyValue")
		                    .from("SystemProperty")
		                    .where("systemResourceId", "general", "systemPropertyId", "fio.grid.fetch.limit")
		                    .queryFirst();
		            
	                try {
	                    viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
	                } catch (Exception e) {
	                    viewIndex = 0;
	                }

	                int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) &&
	                    UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?
	                    Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;

	                try {
	                    viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
	                } catch (Exception e) {
	                    viewSize = fioGridFetch;
	                }
	                
	                // get the indexes for the partial list
	                lowIndex = viewIndex * viewSize;
	                highIndex = (viewIndex + 1) * viewSize;
	                
	                Debug.logInfo("prepare resultList start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
	                TransactionUtil.begin(20000);
	                if (UtilValidate.isNotEmpty(isExportAction) && isExportAction.equals("Y")) {
	                	resultList = EntityQuery.use(delegator).from(dynamicViewEntity).where(mainConditons).cache(true).queryList();
	                } else {
		                if(ignoreOrderBy) {
		                	resultList = EntityQuery.use(delegator).limit(viewSize).offset(lowIndex).from(dynamicViewEntity).where(mainConditons).cache(true).queryList();
		                } else {
		                	resultList = EntityQuery.use(delegator).limit(viewSize).offset(lowIndex).from(dynamicViewEntity).where(mainConditons).orderBy("-partyId").cache(true).queryList();
		                }
	                }
	                TransactionUtil.commit();
	                Debug.logInfo("prepare resultList end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
	                
	                // TODO temporary to show only the main search count max 1000
	                resultListSize = resultList.size();
	                
	                /*Debug.logInfo("prepare resultListSize start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
	                resultListSize = QueryUtil.findCountByCondition(delegator, dynamicViewEntity, mainConditons, null, null, null, UtilMisc.toMap("isIncludeGroupBy", "N"));
	                Debug.logInfo("prepare resultListSize end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);*/
	                
	                if (UtilValidate.isNotEmpty(resultList)) {
	                	
	                	Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
	                	partyIds = EntityUtil.getFieldListFromEntityList(resultList, "partyId", true);
	                	Map<String, Object> timeZones = EnumUtil.getEnumList(delegator, resultList, "timeZoneDesc", "TIME_ZONE", EnumDisplayType.CODE);
	                	
	                	Map<String, Object> currentTimeForTimezones = org.groupfio.common.portal.util.CommonDataHelper.getCurrentTimeForTimezones(timeZones);;
	                	
	                	//Map<String, Object> stateMap = CommonDataHelper.getGeoNameList(delegator, resultList, "stateProvinceGeoId", "STATE/PROVINCE");
						//Map<String, Object> countryMap = CommonDataHelper.getGeoNameList(delegator, resultList, "primaryCountryGeoId", "COUNTRY");
	                	
						/*Map<String, Object> partyNames = new HashMap<>();
						PartyHelper.getPartyNameByPartyIds(delegator, partyNames, resultList, "partyId");*/
						
						Map<String, Object> statusMap = StatusUtil.getStatusList(delegator, resultList, "statusId", "PARTY_STATUS");
						
						Map<String, Object> patientIdList = CommonDataHelper.getPartyIdentificationValues(delegator, resultList, "partyId", "PATIENT_ID");
						Map<String, Object> custAltIdList = CommonDataHelper.getPartyIdentificationValues(delegator, resultList, "partyId", "CUST_ALT_ID");
						Map<String, Object> companyNameList = CommonDataHelper.getPartyIdentificationValues(delegator, resultList, "partyId", "COMPANY_NAME");
						Map<String, Object> partyDataSourceList = CommonDataHelper.getPartyDataSourceList(delegator, resultList, "partyId");
						Map<String, Object> responsiblePartyNames = PartyHelper.getResponsiblePartyByPartyIds(delegator, partyIds, "CUSTOMER");
						
						Map<String, Object> emailAddressList = new LinkedHashMap<>();
						if (UtilValidate.isEmpty(emailAddress)) {
							emailAddressList = CommonDataHelper.getEmailAddressList(delegator, resultList, "primaryEmailId");
						}
						
						Map<String, Object> contactNumberList = new LinkedHashMap<>();
						if (UtilValidate.isEmpty(contactNumber)) {
							contactNumberList = CommonDataHelper.getContactNumberList(delegator, resultList, "primaryTelecomNumberId");
						}
						
						Map<String, Object> postalAddressList = new LinkedHashMap<>();
						if (!isPostalSearch) {
							postalAddressList = CommonDataHelper.getPostalAddressList(delegator, resultList, "primaryPostalAddressId");
						}
						
						Map<String, Object> stateMap = new LinkedHashMap<>();
						Map<String, Object> countryMap = new LinkedHashMap<>();
						if (UtilValidate.isNotEmpty(postalAddressList)) {
							List<String> geoIds = postalAddressList.values().stream().map(x->((GenericValue) x).getString("stateProvinceGeoId")).collect(Collectors.toList());
							stateMap = CommonDataHelper.getGeoNameList(delegator, geoIds, "STATE/PROVINCE");
							
							geoIds = postalAddressList.values().stream().map(x->((GenericValue) x).getString("countryGeoId")).collect(Collectors.toList());
							countryMap = CommonDataHelper.getGeoNameList(delegator, geoIds, "COUNTRY");
						} else {
							stateMap = CommonDataHelper.getGeoNameList(delegator, resultList, "stateProvinceGeoId", "STATE/PROVINCE");
							countryMap = CommonDataHelper.getGeoNameList(delegator, resultList, "countryGeoId", "COUNTRY");
						}
						
						Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						
						Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
	                    for (GenericValue partySummary: resultList) {
	                        String contactId = partySummary.getString("partyId");
	                        GenericValue postalAddress = null;
	                        String supplementalPartyTypeId = partySummary.getString("supplementalPartyTypeId");
	                        Map < String, Object > data = new HashMap < String, Object > ();
	                        String statusId = partySummary.getString("statusId");
	                        String generalProfTitle = partySummary.getString("personalTitle");
	                        
	                        String primaryPostalAddressId = (String) partySummary.get("primaryPostalAddressId");
							String primaryEmailId = (String) partySummary.get("primaryEmailId");
							String primaryTelecomNumberId = (String) partySummary.get("primaryTelecomNumberId");
	                        
	                        String statusItemDesc = (String) statusMap.get(statusId);
	                        	
	                        String name = partySummary.getString("firstName");
                            if (UtilValidate.isNotEmpty(partySummary.getString("lastName"))) {
                                if (UtilValidate.isNotEmpty(name)) {
                                    name = name + " " + partySummary.getString("lastName");
                                } else {
                                    name = partySummary.getString("lastName");
                                }
                            }

                            String infoString = "";
                            if (UtilValidate.isNotEmpty(emailAddress)) {
                            	infoString = partySummary.getString("infoString");
                            } else {
                            	infoString = (String) emailAddressList.get(primaryEmailId);
                            }

                            String phoneNumber = "";
                            if (UtilValidate.isNotEmpty(contactNumber)) {
                            	phoneNumber = partySummary.getString("contactNumber");
                            } else {
                            	phoneNumber = (String) contactNumberList.get(primaryTelecomNumberId);
                            }
    						Map<String, String> contactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, contactId, UtilMisc.toMap("isRetrivePhone", true), true);
    						phoneNumber =(String) contactInformation.get("PrimaryPhone");
    						phoneNumber = DataHelper.preparePhoneNumber(delegator, phoneNumber);
    						
    						Map<String,String> primaryPhone = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMapsExt(delegator, contactId);
    						phoneNumber = (String) primaryPhone.get("PrimaryPhone");

                            String state = "";
                            String country = "";
                            if (!isPostalSearch) {
                            	postalAddress = (GenericValue) postalAddressList.get(primaryPostalAddressId);
                            	if (UtilValidate.isNotEmpty(postalAddress)) {
                            		address1 = postalAddress.getString("address1");
                            		city = postalAddress.getString("city");
                                	state = (String) stateMap.get(postalAddress.getString("stateProvinceGeoId"));
                                	country = (String) countryMap.get(postalAddress.getString("countryGeoId"));
                                	postalCode = postalAddress.getString("postalCode");
                            	}
                            } else {
                            	address1 = partySummary.getString("address1");
                            	city = partySummary.getString("city");
                            	state = (String) stateMap.get(partySummary.getString("stateProvinceGeoId"));
                            	country = (String) countryMap.get(partySummary.getString("countryGeoId"));
                            	postalCode = partySummary.getString("postalCode");
                            }
                            
	                        String preferredCurrencyUomId = partySummary.getString("preferredCurrencyUomId");
	                        
	                        String timeZoneDesc = (String) timeZones.get(partySummary.getString("timeZoneDesc"));
	                        data.put("currentTimeForTimezone", currentTimeForTimezones.get(partySummary.getString("timeZoneDesc")));
	                        
	                        String relationshipManager = (String) responsiblePartyNames.get(contactId);
	                        
	                        String dataSourceDesc = (String) partyDataSourceList.get(contactId);
	                        
	                        String ecometryId = UtilAttribute.getAttrValue(delegator, "PartyAttribute", "partyId", partySummary.getString("partyId"), "ECOMETRY_ID", true);
	                        //String ecometryId = partySummary.getString("ecometryIdValue");
	                        
	                        data.put("name", name);
	                        data.put("statusDescription", statusItemDesc);
	                        data.put("loyaltyId", UtilValidate.isNotEmpty(partySummary.getString("loyaltyId")) ? partySummary.getString("loyaltyId") : "");
	                        
							data.put("timeZoneDesc", timeZoneDesc);
	                        data.put("infoString", infoString);
	                        data.put("contactNumber", phoneNumber);
	                        data.put("city", city);
	                        data.put("address1", address1);
	                        data.put("assignedStore", partySummary.getString("assignedStore"));
	                        data.put("loyaltyStoreId", partySummary.getString("loyaltyStoreId"));
	                        data.put("localStorePreference", partySummary.getString("localStorePreference"));
				            
	                        if(UtilValidate.isNotEmpty(showCallListStateId) && showCallListStateId.equals("Y") && !isPostalSearch && UtilValidate.isNotEmpty(postalAddress)) {
	                        	state = UtilValidate.isNotEmpty(postalAddress.getString("stateProvinceGeoId"))?postalAddress.getString("stateProvinceGeoId"):"";
	                        } else if(UtilValidate.isNotEmpty(showCallListStateId) && showCallListStateId.equals("Y") && isPostalSearch) {
	                        	state = UtilValidate.isNotEmpty(partySummary.getString("stateProvinceGeoId"))?partySummary.getString("stateProvinceGeoId"):"";
	                        }
	                        data.put("state", state);
	                        data.put("country", country);
	                        data.put("postalCode", postalCode);
	                        data.put("dataSourceDesc", dataSourceDesc);
	                        data.put("relationshipManager", relationshipManager);
	                        data.put("preferredCurrencyUomId", preferredCurrencyUomId);
	                        data.put("partyId", contactId);
	                        data.put("generalProfTitle", generalProfTitle);
	                        data.put("companyName", companyNameList.get(contactId));

	                        //get PATIENT_ID
	                        String patientId = (String) patientIdList.get(contactId);
	                        data.put("patientId", UtilValidate.isNotEmpty(patientId) ? patientId : "");
	                        //get CUST_ALT_ID
	                        String custAltId = (String) custAltIdList.get(contactId);
	                        data.put("custAltId", UtilValidate.isNotEmpty(custAltId) ? custAltId : "");
	                        
	                        if (UtilValidate.isNotEmpty(supplementalPartyTypeId) &&
	                            supplementalPartyTypeId.equals("CONTRACTOR")) {
	                            data.put("isContractor", "Yes");
	                        } else {
	                            data.put("isContractor", "No");
	                        }
	                        
	                        data.put("domainEntityId", contactId);
	                        data.put("domainEntityType", DomainEntityType.CUSTOMER);
	                        data.put("domainEntityTypeDesc", DataHelper.convertToLabel(DomainEntityType.CUSTOMER));
	                        data.put("externalId", partySummary.getString("externalId"));
	                        data.put("externalLoginKey", externalLoginKey);
	                        data.put("ecometryId", UtilValidate.isNotEmpty(ecometryId) ? ecometryId : "");

	                        dataList.add(data);
	                    }
	                    Debug.logInfo("prepare actual data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
	                }
	                
	            }
	        } else {
	            Map < String, Object > data = new HashMap < String, Object > ();
	            if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
	                data.put("errorMessage", accessMatrixRes.get("errorMessage").toString());
	            } else {
	                data.put("errorMessage", "Access Denied");
	            }
	            // dataList.add(data);
	        }
	        // Debug.log("Results : " + dataList, MODULE);
	    } catch (Exception e) {
	        e.printStackTrace();
	        result.put("errorMessage", UtilMessage.getPrintStackTrace(e));
	    }
	    long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);
		
		result.put("highIndex", Integer.valueOf(highIndex));
		result.put("lowIndex", Integer.valueOf(lowIndex));

		result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
		result.put("totalRecords", nf.format(resultListSize));
		result.put("recordCount", resultListSize);
		result.put("chunkSize", viewSize);

		result.put("viewSize", viewSize);
		result.put("viewIndex", viewIndex);
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		
		if (UtilValidate.isNotEmpty(isExportAction) && isExportAction.equals("Y")) {
			return JSONObject.fromObject(result).toString();
		}
	    return AjaxEvents.doJSONResponse(response, result);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String redirectOrders(HttpServletRequest request, HttpServletResponse response)
			throws GenericServiceException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String requestUri = request.getParameter("requestUri");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String partyId = (String) context.get("partyId");
		String orderId = (String) context.get("orderId");
		String externalKey = (String) context.get("externalLoginKey");
		if(UtilValidate.isEmpty(partyId))
			partyId = (String) context.get("ptyId");

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String redirectUrl = null;

		try {

			if (UtilValidate.isNotEmpty(orderId)) {
				GenericValue partyDetails = EntityQuery.use(delegator).from("Party").where("partyId", partyId)
						.queryOne();
				if (UtilValidate.isNotEmpty(partyDetails)) {
					String roleTypeId = partyDetails.getString("roleTypeId");
					if (UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equals("ACCOUNT")) {
						redirectUrl = "/account-portal/control/viewOrder?orderId=" + orderId + "&externalLoginKey="
								+ externalKey;
						// redirect
						Debug.logInfo("redirectUrl = " + redirectUrl, MODULE);
						response.sendRedirect(redirectUrl);
					} else if (UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equals("CUSTOMER")) {
						redirectUrl = "/customer-portal/control/viewOrder?orderId=" + orderId + "&externalLoginKey="
								+ externalKey;
						// redirect
						Debug.logInfo("redirectUrl = " + redirectUrl, MODULE);
						response.sendRedirect(redirectUrl);
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return redirectUrl;
	}

	public static String findServiceRequests(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String externalLoginKey = (String) context.get("externalLoginKey");

		HttpSession session = request.getSession();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();


		Map<String, Object> result = new HashMap<String, Object>();
		List<GenericValue> resultList = null;
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		long start = System.currentTimeMillis();

		Timestamp systemTime = UtilDateTime.nowTimestamp();
		ArrayList<String> statuses = new ArrayList<String>();

		int viewIndex = 0;
		int highIndex = 0;
		int lowIndex = 0;
		int resultListSize = 0;
		int viewSize = 0;

		try {
			List<String> userLoginRoles = UtilValidate.isNotEmpty(session.getAttribute("userLoginRoles"))
					? (List<String>) session.getAttribute("userLoginRoles")
							: UtilValidate.isNotEmpty(request.getAttribute("userLoginRoles"))
							? (ArrayList<String>) request.getAttribute("userLoginRoles") : new LinkedList<>();
							if (UtilValidate.isEmpty(userLoginRoles)) {
								String userLoginPartyId = userLogin.getString("partyId");
								Map<String, Object> userData = org.fio.homeapps.util.DataHelper.getUserRoleGroup(delegator,
										userLoginPartyId);
								userLoginRoles = UtilValidate.isNotEmpty(userData) ? (List<String>) userData.get("userLoginRoles")
										: new LinkedList<>();
								Debug.logInfo("userLoginRoles--->" + userLoginRoles, MODULE);
							}
							// Integrate security matrix logic start
							String userLoginId = userLogin.getString("userLoginId");
							String accessLevel = "Y";
							String businessUnit = null;
							boolean isCSR = false;
							String csrRolesList = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CSR_ROLES",
									"CUST_SERVICE_REP");
							if (UtilValidate.isNotEmpty(csrRolesList) && UtilValidate.isNotEmpty(userLoginRoles)) {
								List<String> csrRoles = new ArrayList<>();
								if (UtilValidate.isNotEmpty(csrRolesList) && csrRolesList.contains(",")) {
									csrRoles = org.fio.admin.portal.util.DataUtil.stringToList(csrRolesList, ",");
								} else
									csrRoles.add(csrRolesList);

								if (UtilValidate.isNotEmpty(csrRoles)) {
									for (String csrRole : csrRoles) {
										if (userLoginRoles.contains(csrRole)) {
											isCSR = true;
											break;
										}
									}
								}
							}
							Debug.logInfo("csrRolesList--->" + csrRolesList + "---isCSR--->" + isCSR, MODULE);
							Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
							if (UtilValidate.isNotEmpty(userLoginId) && isCSR) {
								String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
								businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
								Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
								accessMatrixMap.put("delegator", delegator);
								accessMatrixMap.put("dispatcher", dispatcher);
								accessMatrixMap.put("businessUnit", businessUnit);
								accessMatrixMap.put("modeOfOp", "Read");
								accessMatrixMap.put("entityName", "CustRequest");
								accessMatrixMap.put("userLoginId", userLoginId);
								accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
								if (UtilValidate.isNotEmpty(accessMatrixRes)) {
									accessLevel = (String) accessMatrixRes.get("accessLevel");
								} else {
									accessLevel = null;
								}
							}
							// Integrate security matrix logic end
							if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {

								Map<String, Object> callCtxt = FastMap.newInstance();
								Map<String, Object> callResult = FastMap.newInstance();

								Map<String, Object> requestContext = new LinkedHashMap<>();
								requestContext.putAll(context);

								requestContext.put("scheduledStartDate", request.getParameter("scheduledDate_from"));
								requestContext.put("scheduledEndDate", request.getParameter("scheduledDate_to"));
								requestContext.put("activityOwner", context.get("technician"));

								requestContext.put("totalGridFetch",
										DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));
								requestContext.put("isPostalCodeRequired", "N");
								requestContext.put("isCSR", isCSR);

								// check with ownerId
								if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
									@SuppressWarnings("unchecked")
									List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
									requestContext.put("ownerIds", ownerIds);
								}
								// check with emplTeamId
								if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
									@SuppressWarnings("unchecked")
									List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
									requestContext.put("emplTeamIds", emplTeamIds);
								}

								callCtxt.put("requestContext", requestContext);

								callCtxt.put("userLogin", userLogin);

								Debug.logInfo("findServiceRequest service start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
								callResult = dispatcher.runSync("common.findServiceRequest", callCtxt);
								Debug.logInfo("findServiceRequest service end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);

								if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("srList"))) {

									resultList = (List<GenericValue>) callResult.get("srList");

									Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
									Map<String, Object> srSourceList = EnumUtil.getEnumList(delegator, resultList, "custReqSrSource", "CASE_ORIGIN_CODE");
									Map<String, Object> priorityList = EnumUtil.getEnumList(delegator, resultList, "priority", "PRIORITY_LEVEL");
									Map<String, Object> statusList = StatusUtil.getStatusList(delegator, resultList, "statusId", "SR_STATUS_ID");

									Map<String, Object> partyNames = new HashMap<>();
									PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, resultList, "responsiblePerson");
									PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, resultList, "createdByUserLogin");
									PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, resultList, "lastModifiedByUserLogin");
									PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, resultList, "closedByUserLogin");
									PartyHelper.getPartyNameByPartyIds(delegator, partyNames, resultList, "fromPartyId");
									
									Map<String, Object> stateMap = org.fio.admin.portal.util.DataUtil.getGeoNameList(delegator, "STATE/PROVINCE");

									Map<String, Object> srTypeNames = SrDataHelper.getSrTypeNames(delegator, resultList, "custRequestTypeId");
									Map<String, Object> srCategoryNames = SrDataHelper.getSrCategoryNames(delegator, resultList, "custRequestCategoryId");
									Map<String, Object> srSubCategoryNames = SrDataHelper.getSrCategoryNames(delegator, resultList, "custRequestSubCategoryId");

									Map<String, Object> businessUnitNames = CommonDataHelper.getBusinessUnitNames(delegator, resultList, "ownerBu");
									Map<String, Object> customFieldGroupNames = CommonDataHelper.getCustomFieldGroupNames(delegator, UtilMisc.toMap("groupType","SEGMENTATION"));

									Map<String, Object> storeNames = SrDataHelper.getProductStoreNames(delegator);

									Map<String, Object> workOrderSchList = SrDataHelper.getScheduledDate(delegator, resultList, "custRequestId");

									//String locationCustomFieldId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOC_CF_ID");
									String locationCustomFieldId = (String) context.get("locationCustomFieldId");
									if (UtilValidate.isEmpty(locationCustomFieldId)) {
										locationCustomFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
									}

									String soldByLocation = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SOLD_BY_LOCATION", "Sold By Location");
									String sblCustomFieldId = DataHelper.getCustomFieldId(delegator, "CUSTOMER_GRP", soldByLocation);

									List custRequestIds = EntityUtil.getFieldListFromEntityList(resultList, "custRequestId", true);

									List conditionList = FastList.newInstance();
									conditionList.add(EntityCondition.makeCondition("custRequestId",EntityOperator.IN,custRequestIds));
									conditionList.add(EntityCondition.makeCondition("custRequestTypeId","REASON_CODE"));
									EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
									List<GenericValue> custRequestResolution = delegator.findList("CustRequestResolution", cond,
											UtilMisc.toSet("custRequestId","custRequestTypeId","description"), null, null, false);

									Map <String, Object> reasonCodeMap= org.fio.homeapps.util.DataUtil.getMapFromGeneric(custRequestResolution, "custRequestId", "description", false);

									conditionList.clear();
									conditionList.add(EntityCondition.makeCondition("custRequestId",EntityOperator.IN,custRequestIds));
									conditionList.add(EntityCondition.makeCondition("custRequestTypeId","CAUSE_CATEGORY"));
									cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
									custRequestResolution = delegator.findList("CustRequestResolution", cond,
											UtilMisc.toSet("custRequestId","custRequestTypeId","description"), null, null, false);

									Map <String, Object> causeCaategoryMap= org.fio.homeapps.util.DataUtil.getMapFromGeneric(custRequestResolution, "custRequestId", "description", false);

									Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);

									viewIndex = (int) callResult.get("viewIndex");
									highIndex = (int) callResult.get("highIndex");
									lowIndex = (int) callResult.get("lowIndex");
									resultListSize = (int) callResult.get("resultListSize");
									viewSize = (int) callResult.get("viewSize");

									String cacheKey = "";
									String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
									String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
									// int count = 0;
									Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
									String primaryTechCustomFieldId = DataHelper.getCustomFieldId(delegator, "ANCHOR_ROLES", "ANR_TECHNICIAN");
									
									String isEnabledStaTat = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_ENABLE","N");
									String slaTatStopStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_STOP_STATUS");
									String slaTatPauseStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_PAUSE_STATUS");
									
									List holidays = FastList.newInstance();
									conditionList = FastList.newInstance();
									conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
													EntityCondition.makeCondition("status", EntityOperator.EQUALS, null),
													EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE"))
							                	);
									List<GenericValue> holidayConfigList = EntityQuery.use(delegator).from("TechDataHolidayConfig").where(EntityCondition.makeCondition(conditionList, EntityOperator.AND)).distinct(true).cache(true).queryList();
							    	if(UtilValidate.isNotEmpty(holidayConfigList)) {
							    		for(GenericValue holidayConfig : holidayConfigList) {
							    			java.sql.Date holidayDate = holidayConfig.getDate("holidayDate");
							    			holidays.add(new Timestamp(holidayDate.getTime()));
							    		}
							    	}
							    	
							    	String activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
							    	
									for (GenericValue serviceRequest : resultList) {
										// Debug.logInfo("row "+(++count), MODULE);
										Map<String, Object> data = new HashMap<String, Object>();

										String custRequestId = serviceRequest.getString("custRequestId");

										//Map<String, Object> assocPartyNames = SrDataHelper.getSrAssocPartyNames(delegator, custRequestId);
										Map<String, Map<String, Object>> assocPartys = SrDataHelper.getSrAssocPartys(delegator, custRequestId);
										String contractorPersonName = UtilValidate.isNotEmpty(assocPartys.get("CONTRACTOR")) ? (String) assocPartys.get("CONTRACTOR").get("partyName") : ""; 
										String salePersonName = UtilValidate.isNotEmpty(assocPartys.get("SALES_REP")) ? (String) assocPartys.get("SALES_REP").get("partyName") : ""; 
										String primaryContactName = UtilValidate.isNotEmpty(assocPartys.get("CONTACT")) ? (String) assocPartys.get("CONTACT").get("partyName") : ""; 
										String homeOwnerName = UtilValidate.isNotEmpty(assocPartys.get("CUSTOMER")) ? (String) assocPartys.get("CUSTOMER").get("partyName") : ""; 

										data.put("contractorPersonName", contractorPersonName);
										data.put("salePersonName", salePersonName);
										data.put("primaryPerson", DataUtil.getCustRequestAttribute(delegator, custRequestId, "PRIMARY"));
										data.put("srNumber", serviceRequest.getString("externalId"));
										data.put("sourceDocumentId",
												UtilValidate.isNotEmpty(serviceRequest.getString("custReqDocumentNum"))
												? serviceRequest.getString("custReqDocumentNum") : custRequestId);

										String status = serviceRequest.getString("statusId");
										data.put("srStatus", statusList.get(serviceRequest.getString("statusId")));

										String atRisk = "No";
										if (!UtilMisc.toList("SR_CLOSED", "SR_CANCELLED").contains(status)) {
											Timestamp dueDateTimeStamp = serviceRequest.getTimestamp("commitDate");
											Timestamp preEscalationTimeStamp = serviceRequest.getTimestamp("preEscalationDate");
											Timestamp now = UtilDateTime.nowTimestamp();
											if (UtilValidate.isNotEmpty(preEscalationTimeStamp)
													&& UtilValidate.isNotEmpty(dueDateTimeStamp) && now.after(preEscalationTimeStamp)
													&& now.before(dueDateTimeStamp)) {
												atRisk = "Yes";
											}
										}
										data.put("slaRisk", atRisk);

										String overDue = "No";
										if (!UtilMisc.toList("SR_CLOSED", "SR_CANCELLED").contains(status)) {
											Timestamp dueDateTimeStamp = serviceRequest.getTimestamp("commitDate");
											Timestamp now = UtilDateTime.nowTimestamp();
											if (UtilValidate.isNotEmpty(dueDateTimeStamp) && now.after(dueDateTimeStamp)) {
												overDue = "Yes";
											}
										}
										data.put("overDue", overDue);

										data.put("srSource", srSourceList.get(serviceRequest.getString("custReqSrSource")));
										data.put("ownerBU", businessUnitNames.get(serviceRequest.getString("ownerBu")));
										data.put("owner", serviceRequest.getString("responsiblePerson"));
										data.put("ownerName", partyNames.get(serviceRequest.getString("responsiblePerson")));

										String modifiedOn = "";
										if (UtilValidate.isNotEmpty(serviceRequest.getString("lastModifiedDate"))) {
											modifiedOn = org.fio.homeapps.util.UtilDateTime.timeStampToString(
													serviceRequest.getTimestamp("lastModifiedDate"), globalDateTimeFormat,
													TimeZone.getDefault(), null);
										}

										data.put("modifiedOn", modifiedOn);
										data.put("modifiedBy", serviceRequest.getString("lastModifiedByUserLogin"));
										data.put("modifiedByName", partyNames.get(serviceRequest.getString("lastModifiedByUserLogin")));

										String dateDue = "";
										if (UtilValidate.isNotEmpty(serviceRequest.getString("commitDate"))) {
											dateDue = org.fio.homeapps.util.UtilDateTime.timeStampToString(
													serviceRequest.getTimestamp("commitDate"), globalDateTimeFormat, TimeZone.getDefault(),
													null);
										}
										data.put("dueDate", dateDue);

										String closedByDate = "";
										if (UtilValidate.isNotEmpty(serviceRequest.getString("closedByDate"))) {
											closedByDate = org.fio.homeapps.util.UtilDateTime.timeStampToString(
													serviceRequest.getTimestamp("closedByDate"), globalDateTimeFormat,
													TimeZone.getDefault(), null);
										}

										data.put("dateClosed", closedByDate);
										data.put("closedBy", serviceRequest.getString("closedByUserLogin"));
										data.put("closedByName", partyNames.get(serviceRequest.getString("closedByUserLogin")));

										data.put("createdBy", serviceRequest.getString("createdByUserLogin"));
										data.put("createdByName", partyNames.get(serviceRequest.getString("createdByUserLogin")));
										String createdDate = "";
										if (UtilValidate.isNotEmpty(serviceRequest.getString("createdDate"))) {
											createdDate = org.fio.homeapps.util.UtilDateTime.timeStampToString(
													serviceRequest.getTimestamp("createdDate"), globalDateTimeFormat, TimeZone.getDefault(),
													null);
										}
										data.put("openDate", createdDate);
										data.put("createdOn", createdDate);

										String openDate = "";
										if (UtilValidate.isNotEmpty(serviceRequest.getString("openDateTime"))) {
											openDate = org.fio.homeapps.util.UtilDateTime.timeStampToString(
													serviceRequest.getTimestamp("openDateTime"), globalDateTimeFormat,
													TimeZone.getDefault(), null);
										}

										data.put("createdByFromIserve", "");
										data.put("custRequestId", custRequestId);
										data.put("partyId", serviceRequest.getString("fromPartyId"));
										data.put("orderId", serviceRequest.getString("custOrderId"));
										data.put("srName", serviceRequest.getString("custRequestName"));
										data.put("purchaseOrder", UtilValidate.isNotEmpty(serviceRequest.getString("purchaseOrder")) ? SrUtil.getSrOrderIds(delegator, serviceRequest.getString("purchaseOrder"), "ORIGINAL") : "");

										if (UtilValidate.isNotEmpty(serviceRequest.getString("custReqOnceDone"))) {
											if ("Y".equals(serviceRequest.getString("custReqOnceDone")))
												data.put("onceAndDone", "Yes");
											if ("N".equals(serviceRequest.getString("custReqOnceDone")))
												data.put("onceAndDone", "No");
										}

										data.put("srType", srTypeNames.get(serviceRequest.getString("custRequestTypeId")));
										data.put("srCategory", srCategoryNames.get(serviceRequest.getString("custRequestCategoryId")));
										data.put("srSubCategory", srSubCategoryNames.get(serviceRequest.getString("custRequestSubCategoryId")));
										data.put("srPriority", priorityList.get(serviceRequest.getString("priority")));

										data.put("customerName", partyNames.get(serviceRequest.getString("fromPartyId")));
										data.put("contractorEmail", serviceRequest.getString("contractorEmail"));

										data.put("primaryContactName", primaryContactName);
										data.put("homeOwnerName", homeOwnerName);

										data.put("activityOwnerName", org.fio.homeapps.util.DataUtil.getSrActivityOwnersName(delegator, custRequestId, activityOwnerRole));

										data.put("homePhoneNumber", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("homePhoneNumber")));
										data.put("offPhoneNumber", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("offPhoneNumber")));
										data.put("mobileNumber", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("mobileNumber")));
										data.put("contractorOffPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("contractorOffPhone")));
										data.put("contractorMobilePhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("contractorMobilePhone")));
										data.put("contractorHomePhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("contractorHomePhone")));

										data.put("homeOwnerPartyId", UtilValidate.isNotEmpty(assocPartys.get("CUSTOMER")) ? (String) assocPartys.get("CUSTOMER").get("partyId") : "");
										data.put("contractorPartyId", UtilValidate.isNotEmpty(assocPartys.get("CONTRACTOR")) ? (String) assocPartys.get("CONTRACTOR").get("partyId") : "");
										data.put("salePersonPartyId", UtilValidate.isNotEmpty(assocPartys.get("SALES_REP")) ? (String) assocPartys.get("SALES_REP").get("partyId") : "");
										data.put("primaryContactPartyId", UtilValidate.isNotEmpty(assocPartys.get("CONTACT")) ? (String) assocPartys.get("CONTACT").get("partyId") : "");

										data.put("dealerRefNo", SrUtil.getCustRequestAttrValue(delegator, "DEALER_REF_NO", custRequestId));
										String locationId = SrUtil.getCustRequestAttrValue(delegator, locationCustomFieldId, custRequestId);
										if (UtilValidate.isNotEmpty(locationId)) {
											data.put("location", storeNames.get(locationId));
										}

										String soldByLocationId = SrUtil.getCustRequestAttrValue(delegator, sblCustomFieldId, custRequestId);
										if (UtilValidate.isNotEmpty(soldByLocationId)) {
											data.put("soldBy", storeNames.get(soldByLocationId));
										}

										String finishType = SrUtil.getCustRequestAttrValue(delegator, "FSR_FINISH_TYPE", custRequestId);
										if (UtilValidate.isNotEmpty(finishType)) {
											data.put("finishType", customFieldGroupNames.get(finishType));
										}
										String srAmount = SrUtil.getCustRequestAttrValue(delegator, "SR_AMOUNT", custRequestId);
										data.put("srAmount", UtilValidate.isNotEmpty(srAmount) ? srAmount : "");
										String city = serviceRequest.getString("pstlPostalCity");
										String state = serviceRequest.getString("pstlStateProvinceGeoId");
										String country = serviceRequest.getString("pstlCountryGeoId");
										String postalCode = serviceRequest.getString("pstlPostalCode");
										String address1 = serviceRequest.getString("pstlAddress1");
										String address2 = serviceRequest.getString("pstlAddress2");

										if (UtilValidate.isNotEmpty(state)) {
											//state = org.fio.homeapps.util.DataUtil.getGeoName(delegator, state, "STATE/PROVINCE");
											state = (String) stateMap.get(state);
										}					

										data.put("city", city);
										data.put("state", state);
										data.put("country", country);
										data.put("postalCode", postalCode);
										data.put("address1", address1);
										data.put("address2", address2);
										
										String primAttrValue = SrUtil.getCustRequestAttrValue(delegator, primaryTechCustomFieldId, custRequestId);
										String primAttrValueName = partyNames.containsKey(primAttrValue) ? (String) partyNames.get(primAttrValue) : PartyHelper.getPersonName(delegator, primAttrValue, false);
										primAttrValueName = UtilValidate.isNotEmpty(primAttrValueName) ? primAttrValueName : primAttrValue;
										data.put("primaryTechnicain", primAttrValueName);

										data.put("domainEntityId", custRequestId);
										data.put("domainEntityType", DomainEntityType.SERVICE_REQUEST);
										data.put("domainEntityTypeDesc", DataHelper.convertToLabel( DomainEntityType.SERVICE_REQUEST ));
										data.put("externalLoginKey", externalLoginKey);	
										data.put("scheduledDate", workOrderSchList.get(custRequestId));

										data.put("reasonCode", UtilValidate.isNotEmpty(reasonCodeMap)?reasonCodeMap.get(custRequestId):"");
										data.put("causeCategory", UtilValidate.isNotEmpty(causeCaategoryMap)?causeCaategoryMap.get(custRequestId):"");
										
										int slaTat = 0;
										if (isEnabledStaTat.equals("Y")) {
											
											/*List slaTatStopStatusList = org.fio.homeapps.util.DataUtil.stringToList(slaTatStopStatus, ",");
											if (UtilValidate.isNotEmpty(serviceRequest.getString("statusId")) && slaTatStopStatusList.contains(serviceRequest.getString("statusId"))) {
												GenericValue slaTatInfo = EntityQuery.use(delegator).from("CustRequestAttribute").where("attrName", "SLA_TAT", "custRequestId", custRequestId).queryFirst();
												if (UtilValidate.isNotEmpty(slaTatInfo) && UtilValidate.isNotEmpty(slaTatInfo.getString("attrValue")) && UtilValidate.isNotEmpty(slaTatInfo.getString("attrValue").trim())) {
													slaTat = (int)Double.parseDouble(slaTatInfo.getString("attrValue").trim());
												}
											}else {
												Map inpCxt = UtilMisc.toMap("delegator",delegator,"closedDate",org.fio.homeapps.util.UtilDateTime.nowTimestamp(),"custRequestId", custRequestId,"srStatuId",serviceRequest.getString("statusId"));
												inpCxt.put("slaTatPauseStatus", slaTatPauseStatus);
												inpCxt.put("slaTatStopStatus", slaTatStopStatus);
												inpCxt.put("holidays", holidays);
												int tatDays = DataHelper.prepareTatToDate(inpCxt);
												slaTat = tatDays;
											}*/
											
											String srTatCount = org.groupfio.common.portal.util.DataHelper.getSrTatCount(delegator, custRequestId, holidays);
											if (UtilValidate.isNotEmpty(srTatCount)) {
												slaTat = Integer.parseInt(srTatCount);
											}
										}
										
										data.put("slaTat", slaTat);

										dataList.add(data);
									}
									Debug.logInfo("prepare actual data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);

								}

							} else {
								String errorMessage = "";
								if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
									errorMessage = accessMatrixRes.get("errorMessage").toString();
								} else {
									errorMessage = "Access Denied";
								}
								result.put("list", new ArrayList<Map<String, Object>>());
								result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
								result.put(ModelService.ERROR_MESSAGE, errorMessage);
								return AjaxEvents.doJSONResponse(response, result);
							}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);

		result.put("highIndex", Integer.valueOf(highIndex));
		result.put("lowIndex", Integer.valueOf(lowIndex));

		result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
		result.put("totalRecords", nf.format(resultListSize));
		result.put("recordCount", resultListSize);
		result.put("chunkSize", viewSize);

		result.put("viewSize", viewSize);
		result.put("viewIndex", viewIndex);

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}

	public static String findServiceRequests_old(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		HttpSession session = request.getSession();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		List<GenericValue> resultList = null;
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		long start = System.currentTimeMillis();

		String partyId = (String) context.get("partyId");

		String cin = (String) context.get("cin");
		String srNo = (String) context.get("srNo");

		String email = (String) context.get("email");
		String phone = (String) context.get("phone");

		String srArea = (String) context.get("srArea");
		String srSubStatus = (String) context.get("srSubStatus");
		String createdBy = (String) context.get("createdBy");
		String srSubArea = (String) context.get("srSubArea");
		String open = (String) context.get("open");
		String slaAtRisk = (String) context.get("slaAtRisk");
		String slaExpired = (String) context.get("slaExpired");
		String unAssigned = (String) context.get("unAssigned");
		String closed = (String) context.get("closed");

		String startDate = (String) context.get("srDateRange_from");
		String endDate = (String) context.get("srDateRange_to");

		String orderId = (String) context.get("orderId");
		String srName = (String) context.get("srName");
		String srPrimaryContactId = (String) context.get("srPrimaryContactId");
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalKey = (String) context.get("externalLoginKey");
		Debug.log("externalLoginKey***********" + externalKey);
		String searchType = request.getParameter("searchType");

		Object owner = context.get("owner");
		Object srType = context.get("srType");
		Object srStatus = context.get("srStatus");
		Object priority = context.get("priority");

		Timestamp systemTime = UtilDateTime.nowTimestamp();
		ArrayList<String> statuses = new ArrayList<String>();

		try {
			List<String> userLoginRoles = UtilValidate.isNotEmpty(session.getAttribute("userLoginRoles"))
					? (List<String>) session.getAttribute("userLoginRoles")
							: UtilValidate.isNotEmpty(request.getAttribute("userLoginRoles"))
							? (ArrayList<String>) request.getAttribute("userLoginRoles") : new ArrayList<>();
							// Integrate security matrix logic start
							String userLoginId = userLogin.getString("userLoginId");
							String accessLevel = "Y";
							String businessUnit = null;
							boolean isCSR = false;
							String csrRolesList = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CSR_ROLES",
									"CUST_SERVICE_REP");
							if (UtilValidate.isNotEmpty(csrRolesList) && UtilValidate.isNotEmpty(userLoginRoles)) {
								List<String> csrRoles = new ArrayList<>();
								if (UtilValidate.isNotEmpty(csrRolesList) && csrRolesList.contains(",")) {
									csrRoles = org.fio.admin.portal.util.DataUtil.stringToList(csrRolesList, ",");
								} else
									csrRoles.add(csrRolesList);

								if (UtilValidate.isNotEmpty(csrRoles)) {
									for (String csrRole : csrRoles) {
										if (userLoginRoles.contains(csrRole)) {
											isCSR = true;
											break;
										}
									}
								}
							}
							Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
							if (UtilValidate.isNotEmpty(userLoginId) && isCSR) {
								String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
								businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
								Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
								accessMatrixMap.put("delegator", delegator);
								accessMatrixMap.put("dispatcher", dispatcher);
								accessMatrixMap.put("businessUnit", businessUnit);
								accessMatrixMap.put("modeOfOp", "Read");
								accessMatrixMap.put("entityName", "CustRequest");
								accessMatrixMap.put("userLoginId", userLoginId);
								accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
								if (UtilValidate.isNotEmpty(accessMatrixRes)) {
									accessLevel = (String) accessMatrixRes.get("accessLevel");
								} else {
									accessLevel = null;
								}
							}
							// Integrate security matrix logic end
							if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
								String isSecurityEnable = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator,
										GlobalParameter.IS_SECURITY_MATRIX_ENABLE, "N");
								// get the default general grid fetch limit
								GenericValue systemProperty = EntityQuery.use(delegator).select("systemPropertyValue")
										.from("SystemProperty")
										.where("systemResourceId", "general", "systemPropertyId", "fio.grid.fetch.limit").queryFirst();
								// set the page parameters
								int viewIndex = 0;
								try {
									viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
								} catch (Exception e) {
									viewIndex = 0;
								}
								result.put("viewIndex", Integer.valueOf(viewIndex));

								int fioGridFetch = UtilValidate.isNotEmpty(systemProperty)
										&& UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue"))
										? Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;

										int viewSize = fioGridFetch;
										try {
											viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
										} catch (Exception e) {
											viewSize = fioGridFetch;
										}
										result.put("viewSize", Integer.valueOf(viewSize));

										List<EntityCondition> conditionlist = FastList.newInstance();
										// check with ownerId
										if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
											@SuppressWarnings("unchecked")
											List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
											conditionlist.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.IN, ownerIds));
										}

										// check with emplTeamId
										if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
											@SuppressWarnings("unchecked")
											List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
											conditionlist.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
										}

										if (UtilValidate.isNotEmpty(partyId)) {
											conditionlist.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId));
										}

										if (UtilValidate.isNotEmpty(startDate)) {
											startDate = df1.format(df2.parse(startDate));
											conditionlist.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO,
													UtilDateTime.getDayStart(Timestamp.valueOf(startDate))));
										}

										if (UtilValidate.isNotEmpty(endDate)) {
											endDate = df1.format(df2.parse(endDate));
											conditionlist.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO,
													UtilDateTime.getDayEnd(Timestamp.valueOf(endDate))));
										}

										if (UtilValidate.isNotEmpty(srNo)) {
											conditionlist
											.add(EntityCondition.makeCondition("externalId", EntityOperator.LIKE, "" + srNo + "%"));
										}
										if (UtilValidate.isNotEmpty(srType)) {
											if (!(srType instanceof List))
												srType = UtilMisc.toList("" + srType);
											conditionlist.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.IN, srType));
										}
										if (UtilValidate.isNotEmpty(srArea)) {
											conditionlist
											.add(EntityCondition.makeCondition("custRequestCategoryId", EntityOperator.EQUALS, srArea));
										}
										if (UtilValidate.isNotEmpty(srSubArea)) {
											conditionlist.add(EntityCondition.makeCondition("custRequestSubCategoryId", EntityOperator.EQUALS,
													srSubArea));
										}

										if (UtilValidate.isNotEmpty(owner)) {
											if (!(owner instanceof List))
												owner = UtilMisc.toList("" + owner);
											conditionlist.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.IN, owner));
										}

										if (UtilValidate.isNotEmpty(createdBy)) {
											conditionlist.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.LIKE,
													"" + createdBy + "%"));
										}
										if (UtilValidate.isNotEmpty(priority)) {
											if (!(priority instanceof List))
												priority = UtilMisc.toList("" + priority);
											conditionlist.add(EntityCondition.makeCondition("priority", EntityOperator.IN, priority));
										}
										if (UtilValidate.isNotEmpty(srName)) {
											conditionlist.add(
													EntityCondition.makeCondition("custRequestName", EntityOperator.LIKE, "%" + srName + "%"));
										}
										if (UtilValidate.isNotEmpty(orderId)) {
											conditionlist.add(EntityCondition.makeCondition("custOrderId", EntityOperator.EQUALS, orderId));
										}

										if (UtilValidate.isNotEmpty(open)) {
											statuses.add(CommonPortalConstants.srOpenStatuses.SR_ASSIGNED);
											statuses.add(CommonPortalConstants.srOpenStatuses.SR_OPEN);
											statuses.add(CommonPortalConstants.srOpenStatuses.SR_IN_PROGRESS);
											statuses.add(CommonPortalConstants.srOpenStatuses.SR_PENDING);
											statuses.add(CommonPortalConstants.srOpenStatuses.SR_FEED_PROVIDED);
										}
										if (UtilValidate.isNotEmpty(closed)) {
											statuses.add(CommonPortalConstants.srClosedStatuses.SR_CLOSED);
											statuses.add(CommonPortalConstants.srClosedStatuses.SR_CANCELLED);
										}
										if (UtilValidate.isNotEmpty(srStatus)) {
											if (srStatus instanceof List)
												statuses.addAll((List) srStatus);
											else
												statuses.add("" + srStatus);
										}

										if (UtilValidate.isNotEmpty(unAssigned) || UtilValidate.isNotEmpty(searchType)
												&& searchType.equals(CommonPortalConstants.SrSearchType.UN_ASSIGNED_SRS)) {

											conditionlist.add(EntityCondition.makeCondition(
													UtilMisc.toList(
															EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, null),
															EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, "")),
													EntityOperator.OR));

										}
										if (UtilValidate.isNotEmpty(searchType)
												&& searchType.equals(CommonPortalConstants.SrSearchType.MY_SRS)) {
											conditionlist.add(
													EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, userLoginId));
										}
										if (UtilValidate.isNotEmpty(searchType)
												&& searchType.equals(CommonPortalConstants.SrSearchType.MY_OPEN_SRS)) {
											conditionlist.add(
													EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, userLoginId));
											statuses.add(CommonPortalConstants.srOpenStatuses.SR_ASSIGNED);
											statuses.add(CommonPortalConstants.srOpenStatuses.SR_OPEN);
											statuses.add(CommonPortalConstants.srOpenStatuses.SR_IN_PROGRESS);
											statuses.add(CommonPortalConstants.srOpenStatuses.SR_PENDING);
										}
										if (UtilValidate.isNotEmpty(searchType)
												&& searchType.equals(CommonPortalConstants.SrSearchType.MY_CLOSED_SRS)) {
											conditionlist.add(
													EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, userLoginId));
											statuses.add(CommonPortalConstants.srClosedStatuses.SR_CLOSED);
											statuses.add(CommonPortalConstants.srClosedStatuses.SR_CANCELLED);
										}

										List<EntityCondition> flagConditions = new ArrayList<EntityCondition>();
										if (UtilValidate.isNotEmpty(slaAtRisk) && "Y".equalsIgnoreCase(slaAtRisk)) {
											Timestamp now = UtilDateTime.nowTimestamp();
											flagConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
													EntityCondition.makeCondition("preEscalationDate", EntityOperator.LESS_THAN, now),
													EntityCondition.makeCondition("commitDate", EntityOperator.GREATER_THAN, now),
													EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN,
															UtilMisc.toList(CommonPortalConstants.srClosedStatuses.SR_CLOSED,
																	CommonPortalConstants.srClosedStatuses.SR_CANCELLED))));
										}
										if (UtilValidate.isNotEmpty(slaExpired) && "Y".equalsIgnoreCase(slaExpired)) {
											Timestamp now = UtilDateTime.nowTimestamp();
											flagConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
													EntityCondition.makeCondition("commitDate", EntityOperator.LESS_THAN, now),
													EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN,
															UtilMisc.toList(CommonPortalConstants.srClosedStatuses.SR_CLOSED,
																	CommonPortalConstants.srClosedStatuses.SR_CANCELLED))));
										}

										if (UtilValidate.isNotEmpty(statuses)) {
											flagConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, statuses));
										}
										if (UtilValidate.isNotEmpty(flagConditions)) {
											EntityCondition conditions = EntityCondition.makeCondition(flagConditions, EntityOperator.OR);
											conditionlist.add(conditions);
										}
										Set<String> fieldsToSelect = new LinkedHashSet<String>();
										fieldsToSelect.add("custRequestId");
										fieldsToSelect.add("custRequestName");
										fieldsToSelect.add("fromPartyId");
										fieldsToSelect.add("statusId");
										fieldsToSelect.add("custRequestTypeId");
										fieldsToSelect.add("custRequestCategoryId");
										fieldsToSelect.add("custRequestSubCategoryId");
										fieldsToSelect.add("createdDate");
										fieldsToSelect.add("responsiblePerson");
										fieldsToSelect.add("emplTeamId");
										fieldsToSelect.add("openDateTime");
										fieldsToSelect.add("priority");
										fieldsToSelect.add("ownerBu");
										fieldsToSelect.add("custOrderId");
										fieldsToSelect.add("custReqSrSource");
										fieldsToSelect.add("custReqOnceDone");
										fieldsToSelect.add("externalId");
										fieldsToSelect.add("createdByUserLogin");
										fieldsToSelect.add("lastModifiedDate");
										fieldsToSelect.add("lastModifiedByUserLogin");
										fieldsToSelect.add("closedByDate");
										fieldsToSelect.add("closedByUserLogin");
										fieldsToSelect.add("commitDate");
										fieldsToSelect.add("preEscalationDate");

										EntityFindOptions efo = new EntityFindOptions();
										efo.setOffset(0);
										efo.setLimit(1000);

										DynamicViewEntity dynamicView = new DynamicViewEntity();

										dynamicView.addMemberEntity("CR", "CustRequest");
										dynamicView.addAlias("CR", "custRequestId");
										dynamicView.addAlias("CR", "custRequestName");
										dynamicView.addAlias("CR", "fromPartyId");
										dynamicView.addAlias("CR", "statusId");
										dynamicView.addAlias("CR", "custRequestTypeId");
										dynamicView.addAlias("CR", "custRequestCategoryId");
										dynamicView.addAlias("CR", "custRequestSubCategoryId");
										dynamicView.addAlias("CR", "openDateTime");
										dynamicView.addAlias("CR", "createdDate");
										dynamicView.addAlias("CR", "responsiblePerson");
										dynamicView.addAlias("CR", "emplTeamId");
										dynamicView.addAlias("CR", "priority");
										dynamicView.addAlias("CR", "ownerBu");
										dynamicView.addAlias("CR", "custOrderId");
										dynamicView.addAlias("CR", "custReqSrSource");
										dynamicView.addAlias("CR", "custReqOnceDone");
										dynamicView.addAlias("CR", "externalId");
										dynamicView.addAlias("CR", "createdByUserLogin");
										dynamicView.addAlias("CR", "lastModifiedDate");
										dynamicView.addAlias("CR", "lastModifiedByUserLogin");
										dynamicView.addAlias("CR", "closedByDate");
										dynamicView.addAlias("CR", "closedByUserLogin");

										dynamicView.addMemberEntity("CRS", "CustRequestSupplementory");
										dynamicView.addAlias("CRS", "commitDate");
										dynamicView.addAlias("CRS", "preEscalationDate");

										dynamicView.addViewLink("CR", "CRS", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

										if (UtilValidate.isNotEmpty(srPrimaryContactId)) {
											fieldsToSelect.add("partyId");
											fieldsToSelect.add("fromDate");
											fieldsToSelect.add("thruDate");
											fieldsToSelect.add("isPrimary");
											dynamicView.addMemberEntity("CRC", "CustRequestContact");
											dynamicView.addAlias("CRC", "partyId");
											dynamicView.addAlias("CRC", "fromDate");
											dynamicView.addAlias("CRC", "thruDate");
											dynamicView.addAlias("CRC", "isPrimary");

											dynamicView.addViewLink("CR", "CRC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

											conditionlist
											.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, srPrimaryContactId));
											conditionlist.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));

										}
										if (!isCSR && "Y".equals(isSecurityEnable)) {
											fieldsToSelect.add("crpPartyId");
											fieldsToSelect.add("roleTypeId");
											fieldsToSelect.add("crpThruDate");
											dynamicView.addMemberEntity("CRP", "CustRequestParty");
											dynamicView.addAlias("CRP", "crpPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
											dynamicView.addAlias("CRP", "roleTypeId");
											dynamicView.addAlias("CRP", "crpFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
											dynamicView.addAlias("CRP", "crpThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
											dynamicView.addViewLink("CR", "CRP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

											String userLoginPartyId = userLogin.getString("partyId");
											String securityRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator,
													userLoginPartyId);

											conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
													EntityCondition.makeCondition("crpPartyId", EntityOperator.EQUALS, userLoginPartyId),
													EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, securityRole),
													EntityCondition.makeCondition("crpThruDate", EntityOperator.EQUALS, null)));
										}

										EntityCondition condition = null;
										if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.CONTACT)) {
											List conditionsList = FastList.newInstance();

											conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
											conditionsList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
											conditionsList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
											EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

											List<GenericValue> custRequestListList = delegator.findList("CustRequestContact", mainConditons,
													UtilMisc.toSet("custRequestId"), null, null, false);
											if (UtilValidate.isNotEmpty(custRequestListList)) {
												List<String> custRequestIds = EntityUtil.getFieldListFromEntityList(custRequestListList,
														"custRequestId", true);
												conditionlist.clear();
												conditionlist
												.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestIds));
												condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
											}
										} else {
											condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
										}
										Debug.log("condition=======" + condition);

										int highIndex = 0;
										int lowIndex = 0;
										int resultListSize = 0;
										try {
											// get the indexes for the partial list
											lowIndex = viewIndex * viewSize + 1;
											highIndex = (viewIndex + 1) * viewSize;

											// set distinct on so we only get one row per order
											// using list iterator
											EntityListIterator pli = EntityQuery.use(delegator).select(fieldsToSelect).from(dynamicView)
													.where(condition).orderBy("commitDate").cursorScrollInsensitive().fetchSize(highIndex)
													.distinct().cache(true).queryIterator();
											// get the partial list for this page
											resultList = pli.getPartialList(lowIndex, viewSize);

											// attempt to get the full size
											resultListSize = pli.getResultsSizeAfterPartialList();
											// close the list iterator
											pli.close();
										} catch (GenericEntityException e) {
											String errMsg = "Error: " + e.toString();
											Debug.logError(e, errMsg, MODULE);
										}

										if (UtilValidate.isNotEmpty(resultList)) {

											String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);

											for (GenericValue serviceRequest : resultList) {

												Map<String, Object> data = new HashMap<String, Object>();

												String custRequestId = serviceRequest.getString("custRequestId");

												data.put("srNumber", serviceRequest.getString("externalId"));

												String status = serviceRequest.getString("statusId");
												if (UtilValidate.isNotEmpty(status)) {
													data.put("srStatus",
															org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, status));
												}

												String atRisk = "No";
												if (!UtilMisc.toList("SR_CLOSED", "SR_CANCELLED").contains(status)) {
													Timestamp dueDateTimeStamp = serviceRequest.getTimestamp("commitDate");
													Timestamp preEscalationTimeStamp = serviceRequest.getTimestamp("preEscalationDate");
													Timestamp now = UtilDateTime.nowTimestamp();
													if (UtilValidate.isNotEmpty(preEscalationTimeStamp)
															&& UtilValidate.isNotEmpty(dueDateTimeStamp) && now.after(preEscalationTimeStamp)
															&& now.before(dueDateTimeStamp)) {
														atRisk = "Yes";
													}
												}
												data.put("slaRisk", atRisk);

												String overDue = "No";
												if (!UtilMisc.toList("SR_CLOSED", "SR_CANCELLED").contains(status)) {
													Timestamp dueDateTimeStamp = serviceRequest.getTimestamp("commitDate");
													Timestamp now = UtilDateTime.nowTimestamp();
													if (UtilValidate.isNotEmpty(dueDateTimeStamp) && now.after(dueDateTimeStamp)) {
														overDue = "Yes";
													}
												}
												data.put("overDue", overDue);

												// build cache [start]

												String srSource = "";
												String ownerBU = "";

												if (UtilValidate.isNotEmpty(serviceRequest.getString("custReqSrSource"))) {
													String srSourceCacheKey = "ENUM_DESC_" + serviceRequest.getString("custReqSrSource") + "_"
															+ "CASE_ORIGIN_CODE";
													if (CacheUtil.getInstance().notContains(srSourceCacheKey)) {
														CacheUtil.getInstance().put(srSourceCacheKey, EnumUtil.getEnumDescription(delegator,
																serviceRequest.getString("custReqSrSource"), "CASE_ORIGIN_CODE"));
													}
													srSource = (String) CacheUtil.getInstance().get(srSourceCacheKey);
												}

												if (UtilValidate.isNotEmpty(serviceRequest.getString("ownerBu"))) {
													String ownerBUCacheKey = "OWNER_BU_" + serviceRequest.getString("ownerBu");
													if (CacheUtil.getInstance().notContains(ownerBUCacheKey)) {
														CacheUtil.getInstance().put(ownerBUCacheKey,
																DataUtil.getBusinessUnitName(delegator, serviceRequest.getString("ownerBu")));
													}

													ownerBU = (String) CacheUtil.getInstance().get(ownerBUCacheKey);
												}

												// build cache [end]

												data.put("srSource", srSource);
												data.put("ownerBU", ownerBU);
												data.put("owner", serviceRequest.getString("responsiblePerson"));
												if (UtilValidate.isNotEmpty(serviceRequest.getString("responsiblePerson"))) {
													data.put("ownerName", PartyHelper.getUserLoginName(delegator,
															serviceRequest.getString("responsiblePerson"), false));
												}

												String modifiedOn = "";
												if (UtilValidate.isNotEmpty(serviceRequest.getString("lastModifiedDate"))) {
													modifiedOn = DataUtil.convertDateTimestamp(serviceRequest.getString("lastModifiedDate"),
															new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP,
															DateTimeTypeConstant.STRING);
												}

												data.put("modifiedOn", modifiedOn);
												data.put("modifiedBy", serviceRequest.getString("lastModifiedByUserLogin"));
												if (UtilValidate.isNotEmpty(serviceRequest.getString("lastModifiedByUserLogin"))) {
													data.put("modifiedByName", PartyHelper.getUserLoginName(delegator,
															serviceRequest.getString("lastModifiedByUserLogin"), false));
												}

												// int diffInDays
												// =ReportUtil.getWorkingDaysBetweenDates(delegator,
												// serviceRequest.getTimestamp("createdDate"),systemTime);
												String diffInDays = "3 Days"; // Default
												GenericValue slaSetupConfig = EntityQuery.use(delegator)
														.select("slaPeriodLvl", "srPeriodUnit", "slaConfigId", "srPriority").from("SrSlaConfig")
														.where("srTypeId", serviceRequest.getString("custRequestTypeId"), "srCategoryId",
																serviceRequest.getString("custRequestCategoryId"), "srSubCategoryId",
																serviceRequest.getString("custRequestSubCategoryId"), "status", "ACTIVE")
														.cache(true).queryFirst();
												if (UtilValidate.isNotEmpty(slaSetupConfig)
														&& UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))) {
													if (UtilValidate.isNotEmpty(slaSetupConfig.getString("srPriority"))
															&& UtilValidate.isNotEmpty(serviceRequest.getString("priority")) && serviceRequest
															.getString("priority").equals(slaSetupConfig.getString("srPriority"))) {
														diffInDays = UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))
																? slaSetupConfig.getString("srPeriodUnit") : "";
																diffInDays = diffInDays + " " + slaSetupConfig.getString("slaPeriodLvl");
													} else {
														// Debug.log("slaConfigId>>>>
														// "+slaSetupConfig.getString("slaConfigId"));
														if (UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))) {
															diffInDays = UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))
																	? slaSetupConfig.getString("srPeriodUnit") : "";
																	diffInDays = diffInDays + " " + slaSetupConfig.getString("slaPeriodLvl");
														}
													}
												} else {
													slaSetupConfig = EntityQuery.use(delegator)
															.select("slaPeriodLvl", "srPeriodUnit", "slaConfigId").from("SrSlaConfig")
															.where("status", "ACTIVE", "srTypeId",
																	serviceRequest.getString("custRequestTypeId"))
															.cache(true).queryFirst();
													if (UtilValidate.isNotEmpty(slaSetupConfig)
															&& UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))) {
														diffInDays = UtilValidate.isNotEmpty(slaSetupConfig.getString("srPeriodUnit"))
																? slaSetupConfig.getString("srPeriodUnit") : "";
																diffInDays = diffInDays + " " + slaSetupConfig.getString("slaPeriodLvl");
													}
												}

												data.put("duration", diffInDays);

												String dateDue = "";
												if (UtilValidate.isNotEmpty(serviceRequest.getString("commitDate"))) {
													dateDue = DataUtil.convertDateTimestamp(serviceRequest.getString("commitDate"),
															new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP,
															DateTimeTypeConstant.STRING);
												}
												data.put("dueDate", dateDue);

												int daysOverDue = 0;
												if (UtilValidate.isNotEmpty(dateDue)
														&& systemTime.after(serviceRequest.getTimestamp("commitDate"))) {
													if (UtilValidate.isNotEmpty(serviceRequest.getTimestamp("closedByDate"))
															&& "SR_CLOSED".equals(serviceRequest.getString("statusId"))
															&& (serviceRequest.getTimestamp("closedByDate")
																	.compareTo(serviceRequest.getTimestamp("commitDate")) == 1)) {
														daysOverDue = ReportUtil.getWorkingDaysBetweenDates(delegator,
																serviceRequest.getTimestamp("commitDate"),
																serviceRequest.getTimestamp("closedByDate"));
													} else if (!"SR_CLOSED".equals(serviceRequest.getString("statusId"))
															&& serviceRequest.getTimestamp("commitDate").before(systemTime)) {
														daysOverDue = ReportUtil.getWorkingDaysBetweenDates(delegator,
																serviceRequest.getTimestamp("commitDate"), systemTime);
													}
												}
												data.put("daysOverdue", daysOverDue);

												String closedByDate = "";
												if (UtilValidate.isNotEmpty(serviceRequest.getString("closedByDate"))) {
													closedByDate = DataUtil.convertDateTimestamp(serviceRequest.getString("closedByDate"),
															new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP,
															DateTimeTypeConstant.STRING);
												}

												data.put("dateClosed", closedByDate);
												data.put("closedBy", serviceRequest.getString("closedByUserLogin"));
												String createdDate = "";
												if (UtilValidate.isNotEmpty(serviceRequest.getString("createdDate"))) {
													createdDate = DataUtil.convertDateTimestamp(serviceRequest.getString("createdDate"),
															new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP,
															DateTimeTypeConstant.STRING);
												}
												if (UtilValidate.isNotEmpty(serviceRequest.getString("closedByUserLogin"))) {
													data.put("closedByName", PartyHelper.getUserLoginName(delegator,
															serviceRequest.getString("closedByUserLogin"), false));
												}

												String openDate = "";
												if (UtilValidate.isNotEmpty(serviceRequest.getString("openDateTime"))) {
													openDate = DataUtil.convertDateTimestamp(serviceRequest.getString("openDateTime"),
															new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP,
															DateTimeTypeConstant.STRING);
												}

												data.put("openDate", createdDate);
												data.put("createdOn", createdDate);
												data.put("createdByFromIserve", "");
												data.put("createdBy", serviceRequest.getString("createdByUserLogin"));
												data.put("custRequestId", custRequestId);
												data.put("partyId", serviceRequest.getString("fromPartyId"));
												data.put("orderId", serviceRequest.getString("custOrderId"));
												data.put("srName", serviceRequest.getString("custRequestName"));

												if (UtilValidate.isNotEmpty(serviceRequest.getString("custReqOnceDone"))) {
													if ("Y".equals(serviceRequest.getString("custReqOnceDone")))
														data.put("onceAndDone", "Yes");
													if ("N".equals(serviceRequest.getString("custReqOnceDone")))
														data.put("onceAndDone", "No");
												}

												if (UtilValidate.isNotEmpty(serviceRequest.getString("createdByUserLogin"))) {
													data.put("createdByName", PartyHelper.getUserLoginName(delegator,
															serviceRequest.getString("createdByUserLogin"), false));
												}

												data.put("srType", org.fio.homeapps.util.DataUtil.getCustRequestTypeDesc(delegator,
														serviceRequest.getString("custRequestTypeId")));
												data.put("srCategory", org.fio.homeapps.util.DataUtil.getCustRequestCategoryDesc(delegator,
														serviceRequest.getString("custRequestCategoryId")));
												data.put("srSubCategory", org.fio.homeapps.util.DataUtil.getCustRequestCategoryDesc(delegator,
														serviceRequest.getString("custRequestSubCategoryId")));
												data.put("srPriority",
														EnumUtil.getEnumDescriptionByEnumId(delegator, serviceRequest.getString("priority")));
												data.put("customerName",
														PartyHelper.getPartyName(delegator, serviceRequest.getString("fromPartyId"), false));
												data.put("partyType", org.ofbiz.party.party.PartyHelper
														.getFirstPartyRoleTypeId(serviceRequest.getString("fromPartyId"), delegator));

												String primaryContactId = org.fio.homeapps.util.DataUtil.getSrPrimaryContact(delegator,
														custRequestId);
												if (UtilValidate.isNotEmpty(primaryContactId)) {
													data.put("primaryContactName",
															PartyHelper.getPartyName(delegator, primaryContactId, false));
												}

												dataList.add(data);
											}

											result.put("highIndex", Integer.valueOf(highIndex));
											result.put("lowIndex", Integer.valueOf(lowIndex));
										}
										result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
										result.put("totalRecords", nf.format(resultListSize));
										result.put("recordCount", resultListSize);
										result.put("chunkSize", viewSize);

							} else {
								String errorMessage = "";
								if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
									errorMessage = accessMatrixRes.get("errorMessage").toString();
								} else {
									errorMessage = "Access Denied";
								}
								result.put("list", new ArrayList<Map<String, Object>>());
								result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
								result.put(ModelService.ERROR_MESSAGE, errorMessage);
								return AjaxEvents.doJSONResponse(response, result);
							}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}

	// createUserLoginForContactAndSendEmail
	public static String createUserLoginForContactAndSendEmail(HttpServletRequest request,
			HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> data = FastMap.newInstance();
		Locale locale = UtilHttp.getLocale(request);
		String requestUri = request.getParameter("requestUri");
		String partyId = request.getParameter("partyId");
		String primaryContactId = request.getParameter("primaryContactId");
		Map<String, Object> serviceResults = null;
		String userName = "";
		GenericValue primaryContactMailGv = null;
		String errMsg = "";
		String contactMechIdTo = "";
		try {
			primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
					.where("partyId", primaryContactId, "contactMechPurposeTypeId", "PRIMARY_EMAIL",
							"contactMechTypeId", "EMAIL_ADDRESS")
					.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
					.queryFirst();

			if (UtilValidate.isEmpty(primaryContactMailGv)) {
				primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
						.where("partyId", primaryContactId, "contactMechTypeId", "EMAIL_ADDRESS")
						.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
						.queryFirst();
			}

			if (UtilValidate.isNotEmpty(primaryContactMailGv)) {
				userName = primaryContactMailGv.getString("infoString");
				contactMechIdTo = primaryContactMailGv.getString("contactMechId");

			} else {
				data.put("errMsg", "Invited Contact Already in Use " + userName);
				return doJSONResponse(response, data);
			}
			GenericValue userLoginForMailCheck = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userName),
					false);
			if (UtilValidate.isNotEmpty(userLoginForMailCheck)) {
				errMsg = "Already in use";
				data.put("errMsg", userLoginForMailCheck.getString("userLoginId") + errMsg);
				return doJSONResponse(response, data);

			}

			String clientDefaultPassword = CommonUtils.getRandomString(6);
			Map<String, Object> userLoginContext = new HashMap<String, Object>();
			GenericValue userLoginAtt = null;
			if (UtilValidate.isNotEmpty(userName)) {
				userLoginContext.put("userLoginId", userName);
				userLoginContext.put("currentPassword", clientDefaultPassword);
				userLoginContext.put("currentPasswordVerify", clientDefaultPassword);
				userLoginContext.put("requirePasswordChange", "N");
				userLoginContext.put("partyId", primaryContactId);
				userLoginContext.put("isClientPortal", "Y");
				GenericValue userLoginForMailId = delegator.findOne("UserLogin",
						UtilMisc.toMap("userLoginId", userName), false);
				if (UtilValidate.isNotEmpty(userLoginForMailId)) {
					errMsg = UtilProperties.getMessage(resource, "PartyUserNameInUse", locale);
					data.put("errMsg", errMsg);
					return doJSONResponse(response, data);

				}
				try {
					serviceResults = dispatcher.runSync("createUserLogin", userLoginContext);
					// add to UL Attribute
					userLoginAtt = delegator.makeValue("UserLoginAttribute");
					userLoginAtt.set("userLoginId", userName);
					userLoginAtt.set("attrName", "OTP_PWD");
					userLoginAtt.set("attrValue", clientDefaultPassword);
					userLoginAtt.create();
					// assign OWNER role to invite userlogin partyId
					Map<String, Object> callResult = FastMap.newInstance();
					GenericValue partyRole = EntityQuery.use(delegator).select("partyId", "roleTypeId")
							.from("PartyRole").where("partyId", partyId, "roleTypeId", "OWNER").queryFirst();
					if (UtilValidate.isEmpty(partyRole)) {
						dispatcher.runAsync("createPartyRole", UtilMisc.toMap("partyId", primaryContactId, "roleTypeId",
								"OWNER", "userLogin", userLogin));
					}
					// assign security group
					Map<String, Object> updateSecurityContext = new HashMap<String, Object>();
					List selectedGroupIds = new ArrayList();
					updateSecurityContext.put("userLogin", userLogin);
					updateSecurityContext.put("userLoginId", userName);
					selectedGroupIds.add("ACCOUNT_OWNER");
					selectedGroupIds.add("CLIENT_PORTAL_OWNER");
					updateSecurityContext.put("selectedGroupIds", selectedGroupIds);
					dispatcher.runAsync("ap.addCustomSecurityGroupForInviteUsers", updateSecurityContext);

				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					Debug.logError("createUserLogin  failed: ", MODULE);
				}

				// send Email to customer
				if (ServiceUtil.isSuccess(serviceResults)) {
					GenericValue inviteUserTemplateGv = delegator.findOne("PretailLoyaltyGlobalParameters",
							UtilMisc.toMap("parameterId", "CLIENT_EMAIL_INVITE"), false);
					if (UtilValidate.isNotEmpty(inviteUserTemplateGv)) {
						String templateId = inviteUserTemplateGv.getString("value");
						if (UtilValidate.isNotEmpty(templateId)) {
							sendEmailUsingTemplateId(delegator, primaryContactId, partyId, templateId, userLogin,
									dispatcher);
						}
					}

					else {
						data.put("errMsg", "No Template Added for Invite User");
						return doJSONResponse(response, data);
					}
				}
			} else {
				data.put("errMsg", "No UserName Error");
				return doJSONResponse(response, data);
			}

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			String errMsg1 = "Problem Creating In UserLogin";
			Debug.logError(errMsg1, MODULE);
		}
		if (data.isEmpty()) {
			data.put("errMsg", "UserLogin Successfully Created");
			return doJSONResponse(response, data);
		}

		return doJSONResponse(response, data);
	}

	public static String searchInviteUsers(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String domainEntityType = request.getParameter("domainEntityType");
		String partyId = request.getParameter("partyId");
		String custRequestId = request.getParameter("custRequestId");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String partyIdStatus = "";
		String name = "";
		EntityCondition condition = null;
		try {

			List<String> optionalAttParties = new ArrayList();

			if (UtilValidate.isNotEmpty(custRequestId)) {
				List optAttendeesConditionList = FastList.newInstance();
				optAttendeesConditionList
				.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				optAttendeesConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				optAttendeesConditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "N"));
				EntityCondition optAttendeesCondition = EntityCondition.makeCondition(optAttendeesConditionList,
						EntityOperator.AND);
				List<GenericValue> custRequestOptAttendees = EntityQuery.use(delegator).select("partyId")
						.from("CustRequestContact").where(optAttendeesCondition).queryList();
				optionalAttParties = EntityUtil.getFieldListFromEntityList(custRequestOptAttendees, "partyId", true);
			}

			List conditionList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(partyId)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				if (UtilValidate.isNotEmpty(partyId)) {
					conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
				}
				// construct role conditions
				EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,
								"CONTACT_REL_INV")

						));
				conditions.add(roleTypeCondition);
				conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"));
				EntityCondition partyStatusCondition = EntityCondition
						.makeCondition(
								UtilMisc.toList(
										EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,
												"PARTY_DISABLED"),
										EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)),
								EntityOperator.OR);
				conditions.add(partyStatusCondition);
				conditions.add(EntityUtil.getFilterByDateExpr());
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> partyRelList = delegator.findList("PartyFromRelnAndParty", mainConditons, null, null,
						null, false);
				if (UtilValidate.isNotEmpty(partyRelList)) {
					List<String> partyIdsFrom = EntityUtil.getFieldListFromEntityList(partyRelList, "partyIdFrom",
							true);
					conditions.clear();
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdsFrom));
					condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					EntityFindOptions efo = new EntityFindOptions();
					efo.setOffset(0);
					efo.setLimit(1000);

					List<GenericValue> userLoginIds = delegator.findList("UserLogin", condition, null, null, efo,
							false);
					if (UtilValidate.isNotEmpty(userLoginIds)) {

						for (GenericValue inviteUserLogin : userLoginIds) {

							Map<String, Object> data = new HashMap<String, Object>();
							String enabled = "Y";
							String invitePartyId = inviteUserLogin.getString("partyId");
							name = PartyHelper.getPartyName(delegator, invitePartyId, false);
							String isPrimary = PartyHelper.isPrimaryContact(delegator, invitePartyId, partyId);
							String primaryContactEmail = PartyHelper.getEmailAddress(delegator, invitePartyId,
									"PRIMARY_EMAIL");
							String primaryContactPhone = PartyHelper.getContactNumber(delegator, invitePartyId,
									"PRIMARY_PHONE");
							String designation = PartyHelper.getPartyDesignation(invitePartyId, delegator);
							if (UtilValidate.isNotEmpty(inviteUserLogin.getString("enabled"))) {
								enabled = inviteUserLogin.getString("enabled");
							} else
								enabled = "Y";
							data.put("invitePartyId", invitePartyId);
							data.put("name", name);
							data.put("isPrimary", isPrimary);
							data.put("primaryContactEmail", primaryContactEmail);
							data.put("primaryContactPhone", primaryContactPhone);
							data.put("designation", designation);
							data.put("enabled", enabled);
							data.put("domainEntityType", domainEntityType);
							if (UtilValidate.isNotEmpty(optionalAttParties)
									&& optionalAttParties.contains(invitePartyId)) {
								data.put("selected", "opt");
							}
							dataList.add(data);

						}
					}

				}
			}
			result.put("data", dataList);
		}

		catch (Exception e) {
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);

	}

	public static String disableLoginForContact(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String domainEntityType = request.getParameter("domainEntityType");
		String primaryContactId = request.getParameter("invitePartyId");
		String partyId = request.getParameter("partyId");
		Map<String, Object> data = FastMap.newInstance();
		GenericValue disableUserLogin = null;
		try {

			if (UtilValidate.isNotEmpty(primaryContactId)) {
				disableUserLogin = EntityQuery.use(delegator).from("UserLogin").where("partyId", primaryContactId)
						.queryFirst();
				if (UtilValidate.isNotEmpty(disableUserLogin)) {
					Timestamp disabledDateTime = UtilDateTime.nowTimestamp();
					disableUserLogin.put("enabled", "N");
					disableUserLogin.put("disabledDateTime", disabledDateTime);
					disableUserLogin.store();

					GenericValue emailTemplateGV = delegator.findOne("PretailLoyaltyGlobalParameters",
							UtilMisc.toMap("parameterId", "CLIENT_LOGIN_DISABLE"), false);
					if (UtilValidate.isNotEmpty(emailTemplateGV)) {
						String templateId = emailTemplateGV.getString("value");
						if (UtilValidate.isNotEmpty(templateId)) {
							sendEmailUsingTemplateId(delegator, primaryContactId, partyId, templateId, userLogin,
									dispatcher);
						}
					}

					else {
						data.put("errMsg", "No Template Added for this parameter.");
						return doJSONResponse(response, data);
					}

					data.put("Error_Message", disableUserLogin.getString("userLoginId") + " disabled successfully.");
				}

			}
		}

		catch (Exception e) {
			data.put("Error_Message", "Problem to disable " + disableUserLogin.getString("userLoginId"));
			Debug.logError(e, MODULE);
			return AjaxEvents.doJSONResponse(response, data);
		}

		return AjaxEvents.doJSONResponse(response, data);

	}

	public static String enableLoginForContact(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String partyId = request.getParameter("partyId");
		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String domainEntityType = request.getParameter("domainEntityType");
		String primaryContactId = request.getParameter("invitePartyId");
		Map<String, Object> data = FastMap.newInstance();
		GenericValue enableUserLogin = null;
		try {
			if (UtilValidate.isNotEmpty(primaryContactId)) {
				enableUserLogin = EntityQuery.use(delegator).from("UserLogin").where("partyId", primaryContactId)
						.queryFirst();
				if (UtilValidate.isNotEmpty(enableUserLogin)) {
					Timestamp disabledDateTime = UtilDateTime.nowTimestamp();
					enableUserLogin.put("enabled", "Y");
					enableUserLogin.put("disabledDateTime", null);
					enableUserLogin.store();
					GenericValue emailTemplateGV = delegator.findOne("PretailLoyaltyGlobalParameters",
							UtilMisc.toMap("parameterId", "CLIENT_LOGIN_ENABLE"), false);
					if (UtilValidate.isNotEmpty(emailTemplateGV)) {
						String templateId = emailTemplateGV.getString("value");
						if (UtilValidate.isNotEmpty(templateId)) {
							sendEmailUsingTemplateId(delegator, primaryContactId, partyId, templateId, userLogin,
									dispatcher);
						}
					}

					else {
						data.put("errMsg", "No Template Added for this parameter.");
						return doJSONResponse(response, data);
					}
					data.put("Error_Message", enableUserLogin.getString("userLoginId") + " enabled successfully.");
				}

			}
		}

		catch (Exception e) {
			data.put("Error_Message", "Problem to enable " + enableUserLogin.getString("userLoginId"));
			Debug.logError(e, MODULE);
			return AjaxEvents.doJSONResponse(response, data);
		}

		return AjaxEvents.doJSONResponse(response, data);

	}

	public static String resetPasswordForContact(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String domainEntityType = request.getParameter("domainEntityType");
		String primaryContactId = request.getParameter("invitePartyId");
		String partyId = request.getParameter("partyId");
		Map<String, Object> data = FastMap.newInstance();
		GenericValue userLoginContext = null;
		try {
			if (UtilValidate.isNotEmpty(primaryContactId)) {
				String clientDefaultPassword = CommonUtils.getRandomString(6);
				userLoginContext = EntityQuery.use(delegator).from("UserLogin").where("partyId", primaryContactId)
						.queryFirst();
				GenericValue userLoginAtt = EntityQuery.use(delegator).from("UserLoginAttribute")
						.where("userLoginId", userLoginContext.getString("userLoginId"), "attrName", "OTP_PWD")
						.queryFirst();

				if (UtilValidate.isNotEmpty(userLoginContext) && UtilValidate.isNotEmpty(userLoginAtt)) {
					// String newPassword = userLoginAtt.getString("attrValue");
					userLoginContext.set("userLoginId", userLoginContext.getString("userLoginId"));
					userLoginContext.set("currentPassword",
							org.ofbiz.base.crypto.HashCrypt.getDigestHash(clientDefaultPassword));
					userLoginContext.set("requirePasswordChange", "N");
					userLoginContext.store();
					// update in UL Attribute
					userLoginAtt.set("attrValue", clientDefaultPassword);
					userLoginAtt.store();
					// send mail
					GenericValue emailTemplateGV = delegator.findOne("PretailLoyaltyGlobalParameters",
							UtilMisc.toMap("parameterId", "CLIENT_RESET_PASSWORD"), false);
					if (UtilValidate.isNotEmpty(emailTemplateGV)) {
						String templateId = emailTemplateGV.getString("value");
						if (UtilValidate.isNotEmpty(templateId)) {
							sendEmailUsingTemplateId(delegator, primaryContactId, partyId, templateId, userLoginContext,
									dispatcher);
						}
					}

					else {
						data.put("errMsg", "No Template Added for this parameter.");
						return doJSONResponse(response, data);
					}
					data.put("Error_Message",
							"reset password sent successfully to " + userLoginContext.getString("userLoginId"));
				} else {
					data.put("Error_Message",
							"Problem to reset password for " + userLoginContext.getString("userLoginId"));
				}

			}
		}

		catch (Exception e) {
			data.put("Error_Message", "Problem to reset password for " + userLoginContext.getString("userLoginId"));
			Debug.logError(e, MODULE);
			return AjaxEvents.doJSONResponse(response, data);
		}

		return AjaxEvents.doJSONResponse(response, data);

	}

	public static String enableOrDisableLoginForAssoc(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String domainEntityType = request.getParameter("domainEntityType");
		String partyId = request.getParameter("partyId");
		String toDisable = request.getParameter("toDisable");
		Map<String, Object> data = FastMap.newInstance();
		GenericValue userLoginContext = null;
		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String partyIdStatus = "";
		String name = "";
		EntityCondition condition = null;
		try {

			List conditionList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(partyId)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				if (UtilValidate.isNotEmpty(partyId)) {
					conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
				}
				// construct role conditions
				EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,
								"CONTACT_REL_INV")

						));
				conditions.add(roleTypeCondition);
				conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"));
				EntityCondition partyStatusCondition = EntityCondition
						.makeCondition(
								UtilMisc.toList(
										EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,
												"PARTY_DISABLED"),
										EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)),
								EntityOperator.OR);
				conditions.add(partyStatusCondition);
				conditions.add(EntityUtil.getFilterByDateExpr());
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> partyRelList = delegator.findList("PartyFromRelnAndParty", mainConditons, null, null,
						null, false);
				if (UtilValidate.isNotEmpty(partyRelList)) {
					List<String> partyIdsFrom = EntityUtil.getFieldListFromEntityList(partyRelList, "partyIdFrom",
							true);
					conditions.clear();
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdsFrom));
					condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);

					List<GenericValue> userLoginIds = delegator.findList("UserLogin", condition, null, null, null,
							false);
					if (UtilValidate.isNotEmpty(userLoginIds)) {
						if (UtilValidate.isNotEmpty(toDisable) && toDisable.equals("Y")) {
							for (GenericValue inviteUserLogin : userLoginIds) {
								String invitePartyId = inviteUserLogin.getString("partyId");
								GenericValue disableUserLogin = EntityQuery.use(delegator).from("UserLogin")
										.where("partyId", invitePartyId).queryOne();
								Timestamp disabledDateTime = UtilDateTime.nowTimestamp();
								disableUserLogin.put("enabled", "N");
								disableUserLogin.put("disabledDateTime", disabledDateTime);
								disableUserLogin.store();
								request.setAttribute("partyId", partyId);
								request.setAttribute("_EVENT_MESSAGE_",
										"All Associate UserLoginId's disabled successfully.");

							}
						}
					}
					if (UtilValidate.isNotEmpty(toDisable) && toDisable.equals("N")) {
						for (GenericValue inviteUserLogin : userLoginIds) {
							String invitePartyId = inviteUserLogin.getString("partyId");
							GenericValue enableUserLogin = EntityQuery.use(delegator).from("UserLogin")
									.where("partyId", invitePartyId).queryOne();
							enableUserLogin.put("enabled", "Y");
							enableUserLogin.put("disabledDateTime", null);
							enableUserLogin.store();
							request.setAttribute("partyId", partyId);
							request.setAttribute("_EVENT_MESSAGE_",
									"All Associate UserLoginId's enabled successfully.");

						}
					}
				}
			}
		}

		catch (Exception e) {
			if (toDisable.equals("N")) {
				String errMsg = "Problem to enable userlogin for associate contact " + e.toString();
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
			if (toDisable.equals("Y")) {
				String errMsg = "Problem to disable userlogin for associate contact " + e.toString();
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
			Debug.logError(e, MODULE);
			String errMsg = "Problem to disable/enable userlogin for associate contact " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}

		return "success";

	}

	// send Email with template Id
	public static String sendEmailUsingTemplateId(Delegator delegator, String primaryContactId, String partyId,
			String templateId, GenericValue userLogin, LocalDispatcher dispatcher) {
		GenericValue primaryContactMailGv = null;
		String userName = "";
		String contactMechIdTo = "";
		try {
			primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
					.where("partyId", primaryContactId, "contactMechPurposeTypeId", "PRIMARY_EMAIL",
							"contactMechTypeId", "EMAIL_ADDRESS")
					.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
					.queryFirst();

			if (UtilValidate.isEmpty(primaryContactMailGv)) {
				primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
						.where("partyId", primaryContactId, "contactMechTypeId", "EMAIL_ADDRESS")
						.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
						.queryFirst();
			}
			if (UtilValidate.isNotEmpty(primaryContactMailGv)) {
				userName = primaryContactMailGv.getString("infoString");
				contactMechIdTo = primaryContactMailGv.getString("contactMechId");

			}
		} catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			Debug.logError(e2, MODULE);
		}
		if (UtilValidate.isNotEmpty(templateId)) {
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			Map<String, Object> requestContext = FastMap.newInstance();
			Map<String, Object> commEventMap = new HashMap<String, Object>();
			Map<String, Object> data = FastMap.newInstance();
			Timestamp now = UtilDateTime.nowTimestamp();
			GenericValue emailTemlateData = null;
			try {
				Debug.log("templateId===" + templateId);
				emailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId", templateId), false);
				Debug.log("subjct" + emailTemlateData.getString("subject"));
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				Debug.logError(e1, MODULE);
			}
			commEventMap.put("communicationEventTypeId", "EMAIL_COMMUNICATION");
			commEventMap.put("contactMechTypeId", "EMAIL_ADDRESS");
			commEventMap.put("contactMechIdTo", contactMechIdTo);
			commEventMap.put("statusId", "COM_PENDING");
			commEventMap.put("partyIdFrom", partyId);
			commEventMap.put("partyIdTo", primaryContactId);
			commEventMap.put("datetimeStarted", now);
			commEventMap.put("entryDate", now);
			commEventMap.put("subject", emailTemlateData.getString("subject"));
			commEventMap.put("userLogin", userLogin);
			Map<String, Object> createResult;
			try {
				createResult = dispatcher.runSync("createCommunicationEvent", commEventMap);
			} catch (GenericServiceException e) {
				return "Problem Creating In Communication Event";
			}
			if (ServiceUtil.isError(createResult)) {
				return "Error Creating In Communication Event";
			}
			String communicationEventId = (String) createResult.get("communicationEventId");
			String fromEmail = emailTemlateData.getString("senderEmail");
			String senderName = emailTemlateData.getString("senderName");
			String emailContent = "";
			// String templateFormContent =
			// emailTemlateData.getString("textContent");
			String templateFormContent = emailTemlateData.getString("templateFormContent");
			if (UtilValidate.isNotEmpty(templateFormContent)) {
				if (Base64.isBase64(templateFormContent)) {
					templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
				}
			}

			// prepare email content [start]
			Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
			extractContext.put("delegator", delegator);
			extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
			extractContext.put("fromEmail", fromEmail);
			extractContext.put("toEmail", userName);
			extractContext.put("partyId", primaryContactId);
			extractContext.put("emailContent", templateFormContent);
			extractContext.put("templateId", templateId);
			extractContext.put("templateId", templateId);

			Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
			emailContent = (String) extractResultContext.get("emailContent");
			// prepare email content [end]
			requestContext.put("communicationEventId", communicationEventId);
			requestContext.put("emailContent", emailContent);
			requestContext.put("templateId", templateId);
			requestContext.put("partyId", partyId);
			requestContext.put("subject", emailTemlateData.getString("subject"));
			requestContext.put("nto", userName);
			requestContext.put("nsender", fromEmail);
			requestContext.put("senderName", senderName);
			callCtxt.put("requestContext", requestContext);
			callCtxt.put("userLogin", userLogin);
			try {
				dispatcher.runAsync("common.sendEmail", callCtxt);
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				Debug.logError("Email send failed: ", MODULE);
			}
		}
		return userName;
	}

	// search Contacts For Client portal
	public static String searchContactsForClient(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String domainEntityType = request.getParameter("domainEntityType");
		String partyId = request.getParameter("partyId");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String partyIdStatus = "";
		String name = "";
		EntityCondition condition = null;
		try {

			List conditionList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(partyId)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				if (UtilValidate.isNotEmpty(partyId)) {
					conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
				}
				// construct role conditions
				EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,
								"CONTACT_REL_INV")

						));
				conditions.add(roleTypeCondition);
				conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"));
				EntityCondition partyStatusCondition = EntityCondition
						.makeCondition(
								UtilMisc.toList(
										EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,
												"PARTY_DISABLED"),
										EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)),
								EntityOperator.OR);
				conditions.add(partyStatusCondition);
				conditions.add(EntityUtil.getFilterByDateExpr());
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> partyRelList = delegator.findList("PartyFromRelnAndParty", mainConditons, null, null,
						null, false);
				if (UtilValidate.isNotEmpty(partyRelList)) {
					List<String> partyIdsFrom = EntityUtil.getFieldListFromEntityList(partyRelList, "partyIdFrom",
							true);
					conditions.clear();
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdsFrom));
					condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					EntityFindOptions efo = new EntityFindOptions();
					efo.setOffset(0);
					efo.setLimit(1000);

					List<GenericValue> userLoginIds = delegator.findList("UserLogin", condition, null, null, efo,
							false);
					if (UtilValidate.isNotEmpty(userLoginIds)) {
						for (GenericValue userLoginIdGv : userLoginIds) {
							String contactPartyId = userLoginIdGv.getString("partyId");
							GenericValue contactUserLogin = EntityQuery.use(delegator).from("UserLogin")
									.where("partyId", contactPartyId).queryFirst();
							Map<String, Object> data = new HashMap<String, Object>();
							String enabled = "N";
							Timestamp lastLoginTime = null;
							String contactUserLoginId = "";
							name = PartyHelper.getPartyName(delegator, contactPartyId, false);
							String isPrimary = PartyHelper.isPrimaryContact(delegator, contactPartyId, partyId);
							String primaryContactEmail = PartyHelper.getEmailAddress(delegator, contactPartyId,
									"PRIMARY_EMAIL");
							String primaryContactPhone = PartyHelper.getContactNumber(delegator, contactPartyId,
									"PRIMARY_PHONE");
							String designation = PartyHelper.getPartyDesignation(contactPartyId, delegator);
							if (UtilValidate.isNotEmpty(contactUserLogin)) {
								contactUserLoginId = contactUserLogin.getString("userLoginId");
								String requirePasswordChange = contactUserLogin.getString("requirePasswordChange");
								if (UtilValidate.isNotEmpty(contactUserLogin.getString("enabled"))) {
									enabled = contactUserLogin.getString("enabled");
								} else
									enabled = "Y";
								if (enabled.equals("Y") && (UtilValidate.isEmpty(requirePasswordChange)
										|| (requirePasswordChange.equals("N")))) {
									lastLoginTime = PartyWorker.findPartyLastLoginTime(contactPartyId, delegator);
									if (UtilValidate.isNotEmpty(lastLoginTime)) {
										data.put("lastLoginTime", lastLoginTime.toString());
									}
								}
							} else {
								data.put("lastLoginTime", "");
							}

							data.put("contactPartyId", contactPartyId);
							data.put("name", name);
							data.put("isPrimary", isPrimary);
							data.put("primaryContactEmail", primaryContactEmail);
							data.put("primaryContactPhone", primaryContactPhone);
							data.put("designation", designation);
							data.put("enabled", enabled);
							data.put("userLoginId", contactUserLoginId);
							data.put("domainEntityType", domainEntityType);

							dataList.add(data);

						}
					}

				}
			}
			result.put("data", dataList);

		} catch (Exception e) {
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);

	}

	public static String getEditContactData(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);
		String partyId = request.getParameter("partyId");
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> result = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				String primaryContactEmail = PartyHelper.getEmailAddress(delegator, partyId, "PRIMARY_EMAIL");
				String primaryContactPhone = PartyHelper.getContactNumber(delegator, partyId, "PRIMARY_PHONE");
				// String designation = PartyHelper.getPartyDesignation(
				// partyId,delegator);
				GenericValue partyDes = EntityQuery.use(delegator).select("designation").from("Person")
						.where("partyId", partyId).queryOne();
				String name = PartyHelper.getPartyName(delegator, partyId, false);
				data.put("primaryEmailId", primaryContactEmail);
				data.put("primaryPhoneNumber", primaryContactPhone);
				data.put("designation", partyDes.getString("designation"));
				data.put("name", name);
				results.add(data);
				// result.put("results", results);
			}
		} catch (Exception e) {
			Debug.logError("Problem While Fetching Service Request Note Data : " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String updateContactDetails(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String primaryEmailId = (String) context.get("primaryEmailId");
		String primaryContactNumber = (String) context.get("primaryContactNumber");
		String designation = (String) context.get("designation");
		Locale locale = (Locale) context.get("locale");
		String partyId = (String) context.get("contactPartyId");
		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");
		Map<String, Object> resEmailMap = new HashMap<String, Object>();

		Map<String, Object> data = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(partyId)) {

				List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,
						"PRIMARY_EMAIL"));
				conditionsList.add(EntityUtil.getFilterByDateExpr());
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				GenericValue contactMechPurpose = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose",
						mainConditons, UtilMisc.toSet("contactMechId"), null, null, false));
				Map<String, Object> emailUpdateMap = new HashMap<String, Object>();
				if (UtilValidate.isNotEmpty(contactMechPurpose)) {
					String emailContactMechId = contactMechPurpose.getString("contactMechId");
					emailUpdateMap.put("partyId", partyId);
					emailUpdateMap.put("contactMechId", emailContactMechId);
					emailUpdateMap.put("emailAddress", primaryEmailId);
					emailUpdateMap.put("allowSolicitation", "Y");
					emailUpdateMap.put("userLogin", userLogin);
					resEmailMap = dispatcher.runSync("updatePartyEmailAddress", emailUpdateMap);
				} else {
					String errMsg = "Problem While Fetching Email Data";
					Debug.logError(errMsg, MODULE);
					return doJSONResponse(response, errMsg);
				}
				// add or update telecom number
				List<EntityCondition> conditionsListTele = new ArrayList<EntityCondition>();
				Map<String, Object> serviceResults = null;
				conditionsListTele.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionsListTele.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,
						"PRIMARY_PHONE"));
				conditionsListTele.add(EntityUtil.getFilterByDateExpr());

				EntityCondition mainConditonsTele = EntityCondition.makeCondition(conditionsListTele,
						EntityOperator.AND);
				GenericValue contactMechPurposeTele = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose",
						mainConditonsTele, UtilMisc.toSet("contactMechId"), null, null, false));
				Map<String, Object> telecomUpdateMap = new HashMap<String, Object>();
				if (UtilValidate.isNotEmpty(contactMechPurposeTele) && UtilValidate.isNotEmpty(primaryContactNumber)) {
					String teleContactMechId = contactMechPurposeTele.getString("contactMechId");
					telecomUpdateMap.put("partyId", partyId);
					telecomUpdateMap.put("contactMechId", teleContactMechId);
					telecomUpdateMap.put("contactNumber", primaryContactNumber);
					telecomUpdateMap.put("userLogin", userLogin);
					serviceResults = dispatcher.runSync("updatePartyTelecomNumber", telecomUpdateMap);
					if (ServiceUtil.isError(serviceResults)) {
						String errMsg = "Problem While updating Phone Data";
						return doJSONResponse(response, errMsg);
					}
				} else if (UtilValidate.isNotEmpty(primaryContactNumber)) {
					telecomUpdateMap.put("partyId", partyId);
					telecomUpdateMap.put("contactNumber", primaryContactNumber);
					telecomUpdateMap.put("userLogin", userLogin);
					telecomUpdateMap.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
					serviceResults = dispatcher.runSync("common.createPartyTNExtForDndValidation", telecomUpdateMap);
					if (ServiceUtil.isError(serviceResults)) {
						String errMsg = "Problem While create Phone Number";
						data.put("message", errMsg);
						results.add(data);
						return doJSONResponse(response, results);
					}
				}
				if (UtilValidate.isNotEmpty(designation)) {
					GenericValue partyDes = EntityQuery.use(delegator).select("designation").from("Person")
							.where("partyId", partyId).queryOne();
					partyDes.set("partyId", partyId);
					partyDes.set("designation", designation);
					partyDes.store();
				}
			}
		} catch (Exception e) {
			Debug.logError("Problem While Fetching Contact Data : " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		data.put("message", "Successfully Updated");
		results.add(data);
		return doJSONResponse(response, results);
	}

	public static String changeLoginPassword(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String username = request.getParameter("userLoginId");
		String newPassword = request.getParameter("newPassword");
		String newPasswordVerify = request.getParameter("newPasswordVerify");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		boolean checkPwd = true;
		String sendEmail = request.getParameter("sendEmail");
		try {

			if (UtilValidate.isNotEmpty(newPassword) && UtilValidate.isNotEmpty(newPasswordVerify)) {
				if (newPassword.equals(newPasswordVerify)) {
					if (UtilValidate.isNotEmpty(username)) {
						GenericValue userLoginCheck = EntityQuery.use(delegator).from("UserLogin")
								.where("userLoginId", username).queryOne();
						if (UtilValidate.isNotEmpty(userLoginCheck)) {
							userLoginCheck.set("currentPassword",
									org.ofbiz.base.crypto.HashCrypt.getDigestHash(newPassword));
							userLoginCheck.set("requirePasswordChange", "N");
							userLoginCheck.store();
							if (UtilValidate.isNotEmpty(sendEmail)) {
								String partyId = userLoginCheck.getString("partyId");
								// send mail
								GenericValue emailTemplateGV = delegator.findOne("PretailLoyaltyGlobalParameters",
										UtilMisc.toMap("parameterId", "CLIENT_CHANGE_PASSWORD"), false);
								if (UtilValidate.isNotEmpty(emailTemplateGV)) {
									String templateId = emailTemplateGV.getString("value");
									if (UtilValidate.isNotEmpty(templateId)) {
										sendEmailUsingTemplateId(delegator, partyId, partyId, templateId,
												userLoginCheck, dispatcher);
									}
								}

								else {
									request.setAttribute("_EVENT_MESSAGE_", "No Template Added for this parameter.");
								}
							}
						}
						request.setAttribute("_EVENT_MESSAGE_", "Password Updated Successfully for User : " + username);
					}
				} else {
					request.setAttribute("userLoginId", username);
					request.setAttribute("_ERROR_MESSAGE_",
							"New Password and Confirm Password Must be Same : " + username);
					return "error";
				}
			} else {
				request.setAttribute("userLoginId", username);
				request.setAttribute("_ERROR_MESSAGE_", "New Password and Confirm Password Are Required : " + username);
				return "error";
			}

		} catch (Exception e) {
			String errMsg = "Problem While Fetching User Login " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}

	public static String getRfmMetricData(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String username = request.getParameter("userLoginId");

		String partyId = request.getParameter("partyId");
		String segmentType = request.getParameter("segmentType");
		String subGroup = request.getParameter("subGroup");
		String roleTypeId = request.getParameter("roleTypeId");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		List<Map<String, Object>> results = new ArrayList<>();
		Timestamp tsSubGroupSatrt = null;
		Timestamp tsSubGroupEnd = null;
		if (UtilValidate.isNotEmpty(subGroup)) {

			String subGroupSatrt = subGroup + "-01-01 00:00:00";
			String subGroupEnd = subGroup + "-12-31 23:59:59";

			tsSubGroupSatrt = Timestamp.valueOf(subGroupSatrt);
			tsSubGroupEnd = Timestamp.valueOf(subGroupEnd);

		}
		try {

			if (UtilValidate.isNotEmpty(partyId)) {
				List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				if (UtilValidate.isNotEmpty(segmentType)) {
					conditionsList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, segmentType));
				} else {
					if ("ACCOUNT".equals(roleTypeId)) {
						conditionsList.add(EntityCondition.makeCondition("groupId", EntityOperator.IN,
								UtilMisc.toList("RFS_SPEND_RANGE", "RFS_RECENCY", "RFS_FREQUENCY")));
					} else if ("CUSTOMER".equals(roleTypeId)) {
						conditionsList.add(EntityCondition.makeCondition("groupId", EntityOperator.IN,
								UtilMisc.toList("RFM_FREQUENCY", "RFM_RECENCY", "RFM_SPEND_RANGE")));
					}
				}
				if (UtilValidate.isNotEmpty(subGroup)) {
					conditionsList.add(EntityCondition.makeCondition("inceptionDate",
							EntityOperator.GREATER_THAN_EQUAL_TO, tsSubGroupSatrt));
					conditionsList.add(EntityCondition.makeCondition("inceptionDate", EntityOperator.LESS_THAN_EQUAL_TO,
							tsSubGroupEnd));
				}
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

				List<GenericValue> customFieldPartyClassificationDet = delegator
						.findList("CustomFieldPartyClassification", mainConditons, null, null, null, false);
				List<GenericValue> customFieldGroupList = delegator
						.findList("CustomFieldGroup",
								EntityCondition.makeCondition("groupId", EntityOperator.IN,
										UtilMisc.toList("RFM_FREQUENCY", "RFM_RECENCY", "RFM_SPEND_RANGE",
												"RFS_SPEND_RANGE", "RFS_RECENCY", "RFS_FREQUENCY")),
								null, null, null, false);

				if (UtilValidate.isNotEmpty(customFieldPartyClassificationDet)) {

					for (GenericValue eachClsfctnData : customFieldPartyClassificationDet) {
						Map<String, Object> data = new HashMap<String, Object>();
						List<GenericValue> groupIdData = null;
						if (UtilValidate.isNotEmpty(eachClsfctnData)) {
							groupIdData = EntityUtil.filterByCondition(customFieldGroupList,
									EntityCondition.makeCondition("groupId", EntityOperator.EQUALS,
											eachClsfctnData.getString("groupId")));
							data.put("customFieldId", eachClsfctnData.getString("customFieldId"));
							if (UtilValidate.isNotEmpty(groupIdData)) {
								GenericValue groupIdGv = EntityUtil.getFirst(groupIdData);
								data.put("groupId", groupIdGv.getString("groupName"));

							} else {
								data.put("groupId", eachClsfctnData.getString("groupId"));

							}

							data.put("groupActualValue", eachClsfctnData.getString("groupActualValue"));
							if (UtilValidate.isNotEmpty(eachClsfctnData.getTimestamp("inceptionDate"))) {
								data.put("inceptionDate", UtilDateTime
										.toDateString(eachClsfctnData.getTimestamp("inceptionDate"), "dd/MM/yyyy"));
							} else {
								data.put("inceptionDate", "");
							}
							if (UtilValidate.isNotEmpty(subGroup)) {
								data.put("subGroup", subGroup);
							} else {
								data.put("subGroup", UtilDateTime
										.toDateString(eachClsfctnData.getTimestamp("inceptionDate"), "yyyy"));

							}
						}

						results.add(data);
					}

				}

			}

		} catch (Exception e) {
			String errMsg = "Problem While Fetching RFM Metric Data" + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}

		return AjaxEvents.doJSONResponse(response, results);

	}

	public static String getTimeEntries(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String workEffortId = (String) context.get("workEffortId");
		try {
			List<GenericValue> rateTypeList = EntityQuery.use(delegator).select("rateTypeId", "description")
					.from("RateType").where("parentTypeId", "ACTIVITY").distinct().queryList();
			Map<String, Object> rateTypes = org.fio.admin.portal.util.DataUtil.getMapFromGeneric(rateTypeList,
					"rateTypeId", "description", false);
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> timeEntryList = EntityQuery.use(delegator).from("TimeEntry").where(condition)
					.orderBy("lastUpdatedTxStamp DESC").queryList();
			for (GenericValue timeEntry : timeEntryList) {
				String partyId = timeEntry.getString("partyId");
				String rateTypeId = timeEntry.getString("rateTypeId");
				//double hour = timeEntry.getDouble("hours");
				Map<String, Object> data = new HashMap<String, Object>();
				data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, timeEntry));

				GenericValue person1 = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryFirst();
				if (UtilValidate.isNotEmpty(person1)) {
					data.put("technician",
							person1.getString("firstName") + (UtilValidate.isNotEmpty(person1.getString("lastName"))
									? " " + person1.getString("lastName") : ""));
				}
				/*
				 * data.put("dateOfService",
				 * org.fio.admin.portal.util.DataUtil.convertDateTimestamp(timeEntry.getString(
				 * "fromDate"), new SimpleDateFormat("MM/dd/yyyy"),
				 * DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING));
				 */
				data.put("dateOfService",
						org.fio.admin.portal.util.DataUtil.convertDateTimestamp(timeEntry.getString("timeEntryDate"),
								new SimpleDateFormat("MM/dd/yyyy"), DateTimeTypeConstant.TIMESTAMP,
								DateTimeTypeConstant.STRING));
				data.put("purpose", rateTypes.get(rateTypeId));

				/*
				 * //calculatedAmount GenericValue partyRate =
				 * EntityQuery.use(delegator).from("PartyRate").where("partyId",
				 * partyId, "rateTypeId",
				 * rateTypeId).filterByDate().queryFirst();
				 * if(UtilValidate.isNotEmpty(partyRate)) { double rate =
				 * partyRate.getDouble("rate");
				 * if(UtilValidate.isNotEmpty(rate)) { double calculatedAmount =
				 * (hour* rate); data.put("calculatedAmount", calculatedAmount);
				 * } }
				 */
				dataList.add(data);
			}
			result.put("list", dataList);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			// e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getIssueMaterials(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String workEffortId = (String) context.get("workEffortId");
		try {

			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));

			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("IM", "IssueMaterial");
			dynamicViewEntity.addAliasAll("IM", null, null);
			dynamicViewEntity.addAlias("IM", "lastUpdatedTxStamp");

			dynamicViewEntity.addMemberEntity("P", "Person");
			dynamicViewEntity.addAlias("P", "firstName");
			dynamicViewEntity.addAlias("P", "lastName");
			dynamicViewEntity.addViewLink("IM", "P", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));

			dynamicViewEntity.addMemberEntity("PRD", "Product");
			dynamicViewEntity.addAlias("PRD", "productId");
			// dynamicViewEntity.addAlias("PRD", "productName");
			dynamicViewEntity.addAlias("PRD", "internalName");
			dynamicViewEntity.addAlias("PRD", "smallImageUrl");
			dynamicViewEntity.addAlias("PRD", "mediumImageUrl");
			dynamicViewEntity.addViewLink("IM", "PRD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));

			String defaultImageRoot = ComponentConfig.getRootLocation("bootstrap");
			defaultImageRoot = defaultImageRoot + "webapp" + File.separator + "bootstrap" + File.separator + "images"
					+ File.separator + "default-product-img.png";
			File image = new File(defaultImageRoot);
			String encodedImage = "";
			if (image.exists()) {
				byte[] fileContent = Files.readAllBytes(image.toPath());
				encodedImage = java.util.Base64.getEncoder().encodeToString(fileContent);
			}

			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> issueMaterialList = EntityQuery.use(delegator).from(dynamicViewEntity).where(condition)
					.orderBy("lastUpdatedTxStamp DESC").queryList();
			for (GenericValue issueMaterial : issueMaterialList) {
				String partyId = issueMaterial.getString("partyId");
				String productId = issueMaterial.getString("productId");
				String description = issueMaterial.getString("description");
				String issuedType = issueMaterial.getString("issuedType");
				Map<String, Object> data = new HashMap<String, Object>();
				data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, issueMaterial));
				data.put("description", EnumUtil.getEnumDescriptionByEnumId(delegator, description));
				data.put("technician",
						issueMaterial.getString("firstName")
						+ (UtilValidate.isNotEmpty(issueMaterial.getString("lastName"))
								? " " + issueMaterial.getString("lastName") : ""));
				data.put("createdDate",
						org.fio.admin.portal.util.DataUtil.convertDateTimestamp(issueMaterial.getString("addedDate"),
								new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"), DateTimeTypeConstant.TIMESTAMP,
								DateTimeTypeConstant.STRING));
				data.put("productDefaultImage",
						UtilValidate.isNotEmpty(encodedImage) ? "data:image/png;base64," + encodedImage : "");
				data.put("materialSource", EnumUtil.getEnumDescriptionByEnumId(delegator, issuedType));
				dataList.add(data);
			}
			result.put("list", dataList);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			// e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getPartyTimeZonesList(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = request.getParameter("partyId");

		try {

			if (UtilValidate.isNotEmpty(partyId)) {
				GenericValue partyDetails = EntityQuery.use(delegator).from("Party").where("partyId", partyId)
						.queryOne();
				if (partyDetails != null && partyDetails.size() > 0) {
					String timeZoneId = partyDetails.getString("timeZoneDesc");
					if (UtilValidate.isNotEmpty(timeZoneId)) {
						GenericValue enumeration = delegator.findOne("Enumeration",
								UtilMisc.toMap("enumId", timeZoneId), false);
						if (enumeration != null && enumeration.size() > 0) {
							Map<String, Object> inputMap = new HashMap<String, Object>();
							inputMap.put("timeZoneId", timeZoneId);
							inputMap.put("selected", true);
							inputMap.put("description", enumeration.getString("description"));
							results.add(inputMap);
						}
					}
				}
			}

			List<GenericValue> timeZoneEnumList = EntityQuery.use(delegator).from("Enumeration")
					.where(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "TIME_ZONE")).queryList();
			for (GenericValue timeZone : timeZoneEnumList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String enumId = timeZone.getString("enumId");
				String description = timeZone.getString("description");
				data.put("timeZoneId", enumId);
				data.put("description", description);
				data.put("selected", false);
				results.add(data);
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getTechnicianList(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String isIncludeLoggedInUser = (String) context.get("isIncludeLoggedInUser");

		String estimatedStartDate = (String) context.get("estimatedStartDate_date");
		String estimatedCompletionDate = (String) context.get("estimatedCompletionDate_date");
		String actualStartDate = (String) context.get("actualStartDate_date");
		String actualCompletionDate = (String) context.get("actualCompletionDate_date");

		String estimatedStartTime = (String) context.get("estimatedStartDate_time");
		String estimatedCompletionTime = (String) context.get("estimatedCompletionDate_time");
		String actualStartTime = (String) context.get("actualStartDate_time");
		String actualCompletionTime = (String) context.get("actualCompletionDate_time");
		String custRequestId = (String) context.get("custRequestId");
		String isResourceType = (String) context.get("isResourceType");

		List<String> ownerList = new ArrayList<>();

		try {

			Timestamp estimatedStartDateTime = ParamUtil.getTimestamp(estimatedStartDate, estimatedStartTime, "yyyy-MM-dd HH:mm");
			Timestamp estimatedCompletionDateTime = ParamUtil.getTimestamp(estimatedCompletionDate, estimatedCompletionTime, "yyyy-MM-dd HH:mm");
			Timestamp actualStartDateTime = ParamUtil.getTimestamp(actualStartDate, actualStartTime, "yyyy-MM-dd HH:mm");
			Timestamp actualCompletionDateTime = ParamUtil.getTimestamp(actualCompletionDate, actualCompletionTime, "yyyy-MM-dd HH:mm");

			Timestamp startDate = null;
			Timestamp endDate = null;
			if (UtilValidate.isNotEmpty(actualStartDateTime) && UtilValidate.isNotEmpty(actualCompletionDateTime)) {
				startDate = actualStartDateTime;
				endDate = actualCompletionDateTime;
			} else {
				startDate = estimatedStartDateTime;
				endDate = estimatedCompletionDateTime;
			}
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();

			String locationId = null;
			String state = null;
			String county = null;
			if (UtilValidate.isNotEmpty(custRequestId)) {
				String locationCustomFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties",
						"location.customFieldId", delegator);
				locationId = org.fio.homeapps.util.DataUtil.getCustRequestAttrValue(delegator, locationCustomFieldId,
						custRequestId);

				EntityCondition mainCondition = EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,
						custRequestId);

				GenericValue supplementory = EntityUtil.getFirst(delegator.findList("CustRequestSupplementory",
						mainCondition, UtilMisc.toSet("pstlStateProvinceGeoId", "pstlCountyGeoId"), null, null, false));
				if (UtilValidate.isNotEmpty(supplementory)) {
					state = supplementory.getString("pstlStateProvinceGeoId");
					county = supplementory.getString("pstlCountyGeoId");
				}
			}

			List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
			Set<String> techLoginIds = new TreeSet<String>();

			if (UtilValidate.isNotEmpty(locationId) && UtilValidate.isNotEmpty(state) && UtilValidate.isNotEmpty(county)) {	

				Map<String, Object> requestContext = FastMap.newInstance();

				requestContext.put("locationId", locationId);
				requestContext.put("startDate", startDate);
				requestContext.put("endDate", endDate);
				requestContext.put("state", state);
				requestContext.put("county", county);
				requestContext.put("isResourceType", isResourceType);

				callCtxt.put("requestContext", requestContext);

				callCtxt.put("userLogin", userLogin);

				callResult = dispatcher.runSync("admin.findResourceAvailability", callCtxt);

				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("responseContext"))) {

					Map<String, Object> responseContext = (Map<String, Object>) callResult.get("responseContext");
					Map<String, Map<String, Object>> techList = (Map<String, Map<String, Object>>) responseContext
							.get("techList");

					if (UtilValidate.isNotEmpty(techList)) {
						for (String technicianId : techList.keySet()) {
							Map<String, Object> data = new HashMap<>();
							Map<String, Object> tech = techList.get(technicianId);
							String technicianName = (String) tech.get("name");
							String techPriorityDesc = (String) tech.get("techPriorityDesc");
							String userLoginId = (String) tech.get("userLoginId");

							/*if (ownerList.contains(userLoginId) || UtilValidate.isEmpty(userLoginId)) {
								continue;
							}*/

							data.put("userLoginId", userLoginId);
							data.put("partyId", technicianId);
							data.put("userName", technicianName);
							data.put("techPriorityType", tech.get("techPriorityType"));
							data.put("techPriorityDesc", tech.get("techPriorityDesc"));
							data.put("techType", tech.get("techType"));
							results.add(data);

							techLoginIds.add(userLoginId);
						}
					}
				}
			} else {
				Debug.logError("Location, startDate, endDate, state, county cant be empty", MODULE);
				result.put("priorityTechError", "Location, startDate, endDate, state, county cant be empty");
			}

			Set<String> fieldToSelect = new TreeSet<String>();
			fieldToSelect.add("partyId");
			fieldToSelect.add("userLoginId");
			fieldToSelect.add("firstName");
			fieldToSelect.add("lastName");
			fieldToSelect.add("businessUnit");

			String roleTypeId = UtilValidate.isNotEmpty(context.get("roleTypeId")) ? (String) context.get("roleTypeId")
					: "SALES_REP";
			List<String> roles = new ArrayList<>();
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				String globalConfig = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, roleTypeId);
				if (UtilValidate.isNotEmpty(globalConfig) && globalConfig.contains(",")) {
					roles = org.fio.admin.portal.util.DataUtil.stringToList(globalConfig, ",");
				} else if (UtilValidate.isNotEmpty(globalConfig)) {
					roles.add(globalConfig);
				} else {
					if (roleTypeId.contains(",")) {
						roles = org.fio.admin.portal.util.DataUtil.stringToList(roleTypeId, ",");
					} else
						roles.add(roleTypeId);
				}
			}
			/*List<GenericValue> partyRoleList = EntityQuery.use(delegator).from("PartyRole")
					.where(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roles)).select(UtilMisc.toSet("partyId","roleTypeId")).queryList();
			*///Debug.log("techList----"+partyRoleList.size());
			
			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("PR", "PartyRole");
			dynamicViewEntity.addAlias("PR", "partyId");
			dynamicViewEntity.addAlias("PR", "roleTypeId");
			
			dynamicViewEntity.addMemberEntity("P", "Person");
			dynamicViewEntity.addAlias("P", "firstName");
			dynamicViewEntity.addAlias("P", "lastName");
			dynamicViewEntity.addAlias("P", "partyId");
			dynamicViewEntity.addViewLink("PR", "P", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
			
			dynamicViewEntity.addMemberEntity("UL", "UserLogin");
			dynamicViewEntity.addAlias("UL", "userLoginId");
			dynamicViewEntity.addAlias("UL", "enabled");
			dynamicViewEntity.addAlias("UL", "partyId");
			dynamicViewEntity.addViewLink("PR", "UL", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
			
			dynamicViewEntity.addMemberEntity("RT", "RoleType");
			dynamicViewEntity.addAlias("RT", "roleTypeId");
			dynamicViewEntity.addAlias("RT", "roleDescription", "description",null,Boolean.FALSE,Boolean.FALSE,null);
			dynamicViewEntity.addViewLink("PR", "RT", Boolean.TRUE, ModelKeyMap.makeKeyMapList("roleTypeId"));
			
			List conditions = FastList.newInstance();
			if (UtilValidate.isNotEmpty(techLoginIds)) {
				conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.NOT_IN, techLoginIds));
			}
			if (UtilValidate.isNotEmpty(ownerList)) {
				conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.NOT_IN, ownerList));
			}
			if (UtilValidate.isEmpty(isIncludeLoggedInUser)) {
				conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.NOT_EQUAL, userLogin.getString("userLoginId")));
			}
			conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roles));
			conditions.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			Debug.log("condition-getTech-"+condition);
			
			List<GenericValue> partyRoleList = EntityQuery.use(delegator).from(dynamicViewEntity).where(condition).queryList();
			Debug.log("condition-getTech-"+partyRoleList.size());
			
			partyRoleList.stream()
		    .filter(partyRole -> {
		        String enabled = partyRole.getString("enabled");
		        return UtilValidate.isNotEmpty(enabled) && enabled.equalsIgnoreCase("Y");
		    })
		    .map(partyRole -> {
		        String partyId = partyRole.getString("partyId");
		        String partyRoleTypeId = partyRole.getString("roleTypeId");
		        String userLoginId = partyRole.getString("userLoginId");
		        String firstName = partyRole.getString("firstName");
		        String lastName = partyRole.getString("lastName");
		        String roleDesc = partyRole.getString("roleDescription");
		        roleDesc = UtilValidate.isNotEmpty(roleDesc) ? roleDesc : partyRoleTypeId;
		        String userName = firstName + (UtilValidate.isNotEmpty(lastName) ? " " + lastName : "");
		        Map<String, Object> data = new HashMap<>();
		        data.put("userLoginId", userLoginId);
		        data.put("partyId", partyId);
		        data.put("userName", userName);
		        data.put("roleDesc", roleDesc);
		        if (org.fio.admin.portal.util.DataUtil.is3rdPartyTechnician(delegator, partyId)) {
		            data.put("techType", "CONTRACTOR");
		        } else {
		            data.put("techType", org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, partyId));
		        }
		        return data;
		    })
		    .forEach(results::add);
			
			result.put("techList", results);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, result);
	}

	public static String getCustomFieldMultiValueList(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String groupId = (String) context.get("groupId");
		String customFieldName = (String) context.get("customFieldName");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			GenericValue customField = EntityQuery.use(delegator).from("CustomField")
					.where("groupId", groupId, "customFieldName", customFieldName).queryFirst();
			if (UtilValidate.isNotEmpty(customField)) {
				String customFieldId = customField.getString("customFieldId");
				List<GenericValue> customFieldMultiValueList = EntityQuery.use(delegator).from("CustomFieldMultiValue")
						.where("customFieldId", customFieldId, "hide", "N").orderBy("sequenceNumber ASC").queryList();
				if (UtilValidate.isNotEmpty(customFieldMultiValueList)) {
					for (GenericValue customFieldMultiValue : customFieldMultiValueList) {
						Map<String, Object> data = new LinkedHashMap<String, Object>();
						data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator,
								customFieldMultiValue));
						dataList.add(data);
					}
				}

			}
			result.put("dataList", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, result);
	}

	public static String getSegmentCodeList(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String groupingCode = (String) context.get("groupingCode");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			GenericValue customFieldGroupingCode = EntityQuery.use(delegator).from("CustomFieldGroupingCode").where("groupingCode", groupingCode,"groupType","SEGMENTATION").queryFirst();
			if (UtilValidate.isNotEmpty(customFieldGroupingCode)) {
				String customFieldGroupingCodeId = customFieldGroupingCode.getString("customFieldGroupingCodeId");
				
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    			conditions.add(EntityCondition.makeCondition("groupingCode", EntityOperator.LIKE, ""+customFieldGroupingCodeId+"%"));
    			conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "SEGMENTATION"));
    			conditions.add(EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, "Y"));
            	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				
				List<GenericValue> customFieldGroupList = EntityQuery.use(delegator).select("groupId","groupName").from("CustomFieldGroup")
						.where(mainConditon).orderBy("sequence ASC").queryList();
				if (UtilValidate.isNotEmpty(customFieldGroupList)) {
					for (GenericValue customFieldGroup : customFieldGroupList) {
						Map<String, Object> data = new LinkedHashMap<String, Object>();
						data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, customFieldGroup));
						dataList.add(data);
					}
				}

			}
			result.put("dataList", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, result);
	}

	public static String getSegmentValueList(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String groupId = (String) context.get("groupId");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			List<GenericValue> customFieldList = EntityQuery.use(delegator).select("customFieldId","customFieldName").from("CustomField")
					.where("groupId", groupId, "groupType","SEGMENTATION", "isEnabled", "Y").orderBy("sequenceNumber ASC").queryList();
			if (UtilValidate.isNotEmpty(customFieldList)) {
				for (GenericValue customField : customFieldList) {
					Map<String, Object> data = new LinkedHashMap<String, Object>();
					data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, customField));
					dataList.add(data);
				}
			}
			result.put("dataList", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, result);
	}

	public static String getActivityTimeEntryCount(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String workEffortId = (String) context.get("workEffortId");

		Map<String, Object> result = FastMap.newInstance();

		try {

			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();

			if (UtilValidate.isNotEmpty(workEffortId)) {

				List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
				List<EntityCondition> conditionList = FastList.newInstance();

				conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
				conditionList.add(EntityUtil.getFilterByDateExpr());
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				long timeEntryCount = delegator.findCountByCondition("TimeEntry", mainConditons, null, UtilMisc.toSet("timeEntryId"), null);
				result.put("timeEntryCount", timeEntryCount);
			}

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String checkTechActivityStatus(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String workEffortId = (String) context.get("workEffortId");
		String assignedTechLoginIds = (String) context.get("assignedTechLoginIds");

		Map<String, Object> result = FastMap.newInstance();

		try {

			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();

			if (UtilValidate.isNotEmpty(workEffortId) && UtilValidate.isNotEmpty(assignedTechLoginIds)) {

				List<String> assignedTechPartyIds = new ArrayList<String>();
				if (UtilValidate.isNotEmpty(assignedTechLoginIds)) {
					assignedTechPartyIds = Arrays.asList( Arrays.stream(assignedTechLoginIds.split(","))
							.map(loginId -> org.fio.homeapps.util.DataUtil.getPartyIdByUserLoginId(delegator, loginId.toString()))
							.toArray(String[]::new)
							);
				}

				if (UtilValidate.isNotEmpty(assignedTechPartyIds)) {
					String message = "";
					for (String partyId : assignedTechPartyIds) {
						if (ResAvailUtil.isTechnicianStartedActivity(delegator, workEffortId, partyId)) {
							String name = PartyHelper.getPartyName(delegator, partyId, false);
							message += name+", ";
						}
					}
					if (UtilValidate.isNotEmpty(message)) {
						message += "already started activity. ";
					}
					result.put(GlobalConstants.RESPONSE_MESSAGE, message);
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getSrAssocParties(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String custRequestId = (String) context.get("srNumber");
		try {
			List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			if(UtilValidate.isNotEmpty(custRequestId)) {

				dataList = SrUtil.getSrAssocParties(delegator, custRequestId);

			}

			result.put("dataList", dataList);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			//e.printStackTrace();
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getPartyEmailList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String) context.get("partyId");
		try {
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			if(UtilValidate.isNotEmpty(partyId)) {

				dataList = UtilContactMech.getPartyEmailList(delegator, partyId, null);

			}

			result.put("dataList", dataList);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			//e.printStackTrace();
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String validateRateConfig(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		try {

			String partyId = (String) context.get("partyId");
			String rateTypeId = (String) context.get("rateTypeId");
			String responseStatus = "";

			if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(rateTypeId)) {
				GenericValue partyRate = EntityQuery.use(delegator).from("PartyRate").where("partyId", partyId, "rateTypeId", rateTypeId).filterByDate().queryFirst();
				if(UtilValidate.isEmpty(partyRate)) {
					partyRate = EntityQuery.use(delegator).from("PartyRate").where("partyId", "company", "rateTypeId", rateTypeId).filterByDate().queryFirst();
					if(UtilValidate.isEmpty(partyRate))
						responseStatus = "STANDARD_TECH";
					else
						responseStatus = "TECH";
				}

				result.put("responseStatus", responseStatus);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			} else {
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Required Parameter missed!");
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getPartyPostal(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String) context.get("partyId");
		try {
			Map<String, Object> data = new HashMap<>();
			if(UtilValidate.isNotEmpty(partyId)) {

				data = UtilContactMech.getPartyPostal(delegator, partyId, null);
				if (UtilValidate.isEmpty(data)) {
					data = new HashMap<>();
				}
			}

			result.put("data", data);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			//e.printStackTrace();
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getPhoneNumberExtension(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		String contactMechId = request.getParameter("contactMechId");
		String partyId = request.getParameter("partyId");
		try {
			if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(contactMechId)) {
				GenericValue partyContactMech = EntityQuery.use(delegator).from("PartyContactMech").where("partyId", partyId, "contactMechId", contactMechId).filterByDate().queryFirst();
				result.put("extension", UtilValidate.isNotEmpty(partyContactMech) && UtilValidate.isNotEmpty(partyContactMech.getString("extension")) ? partyContactMech.getString("extension") : "");
			}
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getSrOrderCount(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String srNumber = (String) context.get("srNumber");

		Map<String, Object> result = FastMap.newInstance();

		try {

			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();

			if (UtilValidate.isNotEmpty(srNumber)) {

				List<EntityCondition> conditionList = FastList.newInstance();

				conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "SERVICE_REQUEST"));
				conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, srNumber));

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				long orderLineCount = delegator.findCountByCondition("EntityOrderLineAssoc", mainConditons, null, UtilMisc.toSet("lineItemIdentifier"), null);
				result.put("orderLineCount", orderLineCount);
			}

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String createAttachmentData1(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String activeTab = (String) context.get("activeTab");
		String attachmentTitle = (String) context.get("attachmentTitle");
		String attchmentFIle = (String) context.get("attchmentFIle");
		String partyId = (String) context.get("partyId");
		String path = (String) context.get("path");
		String url = (String) context.get("url");
		String classificationEnumId = (String) context.get("classificationEnumId");
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String custRequestId = (String) context.get("custRequestId");
		String workEffortId = (String) context.get("workEffortId");
		String attachmentDescription = (String) context.get("attachmentDescription");
		String globalPathName = (String) context.get("globalPathName");
		String linkedFrom = (String) context.get("linkedFrom");
		String publicOrPrivate = (String) context.get("publicOrPrivate");
		// String helpfulLink =(String)context.get("helpfulLink");

		String filePath = null;

		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");

		Map<String, Object> result = FastMap.newInstance();
		String filePathnew = request.getParameter("path");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastContactDate = sdf.format(new Date());
		String name = "";

		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				@SuppressWarnings("unchecked")
				List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

				for (FileItem item : multiparts) {
					if (item.isFormField()) {

						String fName = item.getFieldName();
						String fValue = item.getString();
						if (fName.equals("partyId")) {
							partyId = fValue;
						} else if (fName.equals("path")) {
							path = fValue;
						} else if (fName.equals("attachmentDescription")) {
							attachmentDescription = fValue;
						} else if (fName.equals("classificationEnumId")) {
							classificationEnumId = fValue;
						} else if (fName.equals("salesOpportunityId")) {
							salesOpportunityId = fValue;
						} else if (fName.equals("custRequestId")) {
							custRequestId = fValue;
						} else if (fName.equals("domainEntityType")) {
							domainEntityType = fValue;
						} else if (fName.equals("domainEntityId")) {
							domainEntityId = fValue;
						} else if (fName.equals("workEffortId")) {
							workEffortId = fValue;
						} else if (fName.equals("globalPathName")) {
							globalPathName = fValue;
						} else if (fName.equals("linkedFrom")) {
							linkedFrom = fValue;
						} else if (fName.equals("publicOrPrivate")) {
							publicOrPrivate = fValue;
						}

					}
				}

				try {

					globalPathName = UtilValidate.isNotEmpty(globalPathName) ? globalPathName : "UPLOAD_LOC";
					if (UtilValidate.isNotEmpty(path) || UtilValidate.isNotEmpty(globalPathName)) {
						filePath = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, globalPathName);
						if (UtilValidate.isNotEmpty(filePath)) {
							File dir = new File(filePath);
							if (!dir.exists()) {
								dir.mkdirs();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Debug.logError(e, MODULE);
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
					return AjaxEvents.doJSONResponse(response, result);
				}

				String dataResourceId = delegator.getNextSeqId("DataResource");

				Locale locale = UtilHttp.getLocale(request);


				ServletFileUpload dfu = new ServletFileUpload(new DiskFileItemFactory(10240, FileUtil.getFile("runtime/tmp")));
				List<FileItem> lst = null;
				try {
					lst = UtilGenerics.checkList(dfu.parseRequest(request));
				} catch (FileUploadException e4) {
					request.setAttribute("_ERROR_MESSAGE_", e4.getMessage());
					Debug.logError(e4.getMessage(), MODULE);
					return "error";
				}

				if (lst.size() == 0) {
					String errMsg = "No files uploaded";
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					Debug.logWarning("No files uploaded", MODULE);
					return "error";
				}

				Map<String, Object> passedParams = new HashMap<String, Object>();
				FileItem fi = null;
				FileItem imageFi = null;
				byte[] imageBytes = {};
				for (int i = 0; i < lst.size(); i++) {
					fi = lst.get(i);
					//String fn = fi.getName();
					String fieldName = fi.getFieldName();
					if (fi.isFormField()) {
						String fieldStr = fi.getString();
						passedParams.put(fieldName, fieldStr);
					} else {
						imageFi = fi;
						imageBytes = imageFi.get();
						long size = imageFi.getSize();
					}
				}


				for (FileItem item : multiparts) {
					if (!item.isFormField()) {
						name = new File(item.getName()).getName();
						String extension = org.fio.admin.portal.util.DataUtil.getFileExtension(name);
						//BufferedImage bufferedImage = ImageIO.read(item.getInputStream());
						String fileRelativePath = filePath + File.separator + dataResourceId+"."+extension;
						File outputFile = new File(fileRelativePath);
						item.write(new File(fileRelativePath));
					}
				}
				String mimeTypeId = new MimetypesFileTypeMap().getContentType(name);

				GenericValue dataResource = delegator.makeValue("DataResource");

				dataResource.set("dataResourceId", dataResourceId);
				dataResource.set("dataResourceName", name);
				dataResource.set("dataResourceTypeId", "LOCAL_FILE");
				dataResource.set("statusId", "CTNT_PUBLISHED");
				dataResource.set("mimeTypeId", mimeTypeId);
				// dataResource.set("objectInfo", filePath+"/"+name + "_"
				// +partyId);
				dataResource.set("objectInfo", filePath + File.separator + dataResourceId + "."
						+ org.fio.admin.portal.util.DataUtil.getFileExtension(name));
				dataResource.create();
				GenericValue content = delegator.makeValue("Content");
				String contentId = delegator.getNextSeqId("Content");
				// added prefix for attachment Id
				contentId = "AT-" + contentId;
				// ended
				content.set("contentId", contentId);
				content.set("dataResourceId", dataResourceId);
				content.set("classificationEnumId", classificationEnumId);
				content.set("contentName", name);
				content.set("contentTypeId", "ATTACHMENT");
				content.set("description", attachmentDescription);
				content.set("domainEntityId", domainEntityId);
				content.set("domainEntityType", domainEntityType);
				content.set("linkedFrom", linkedFrom);
				content.set("createdDate", UtilDateTime.nowTimestamp());
				content.set("mimeTypeId", mimeTypeId);
				content.set("createdByUserLogin", userLogin.getString("userLoginId"));
				content.create();

				publicOrPrivate = UtilValidate.isNotEmpty(publicOrPrivate) && "PUBLIC".equals(publicOrPrivate) ? "Y" : "N";

				DataHelper.contentAssociate(delegator,
						UtilMisc.toMap("contentId", contentId, "partyId", partyId, "salesOpportunityId",
								salesOpportunityId, "custRequestId", custRequestId, "workEffortId", workEffortId,
								"publicOrPrivate", publicOrPrivate));

				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully created Attachment");
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			} else {
				GenericValue DataResource = delegator.makeValue("DataResource");
				String dataResourceId = delegator.getNextSeqId("DataResource");
				DataResource.set("dataResourceId", dataResourceId);
				DataResource.set("dataResourceName", url);
				DataResource.set("dataResourceTypeId", "URL_RESOURCE");
				DataResource.set("statusId", "CTNT_PUBLISHED");
				DataResource.set("mimeTypeId", "text/plain");
				DataResource.set("objectInfo", url);
				DataResource.create();
				GenericValue content = delegator.makeValue("Content");
				String contentId = delegator.getNextSeqId("Content");
				// added prefix for attachment Id
				contentId = "AT-" + contentId;
				// ended
				content.set("contentId", contentId);
				content.set("dataResourceId", dataResourceId);
				content.set("classificationEnumId", classificationEnumId);
				content.set("contentName", url);
				content.set("contentTypeId", "HYPERLINK");
				content.set("description", attachmentDescription);
				content.set("domainEntityId", domainEntityId);
				content.set("domainEntityType", domainEntityType);
				content.set("linkedFrom", linkedFrom);
				content.set("createdDate", UtilDateTime.nowTimestamp());
				content.set("createdByUserLogin", userLogin.getString("userLoginId"));
				content.create();

				if (UtilValidate.isNotEmpty(partyId)) {
					GenericValue partyContent = delegator.makeValue("PartyContent");
					partyContent.set("contentId", contentId);
					partyContent.set("partyId", partyId);
					partyContent.set("partyContentTypeId", "USERDEF");
					partyContent.set("fromDate", UtilDateTime.nowTimestamp());
					partyContent.create();
				}
				if (UtilValidate.isNotEmpty(salesOpportunityId)) {
					GenericValue opporContent = delegator.makeValue("OpportunityContent");
					opporContent.set("contentId", contentId);
					opporContent.set("salesOpportunityId", salesOpportunityId);
					opporContent.set("contentTypeId", "HYPERLINK");
					opporContent.set("fromDate", UtilDateTime.nowTimestamp());
					opporContent.create();
				}
				if (UtilValidate.isNotEmpty(custRequestId)) {
					GenericValue srContent = delegator.makeValue("CustRequestContent");
					srContent.set("contentId", contentId);
					srContent.set("custRequestId", custRequestId);
					srContent.set("contentTypeId", "HYPERLINK");
					srContent.set("fromDate", UtilDateTime.nowTimestamp());
					srContent.create();
				}
				if (UtilValidate.isNotEmpty(workEffortId)) {
					GenericValue activityContent = delegator.makeValue("WorkEffortContent");
					activityContent.set("contentId", contentId);
					activityContent.set("workEffortId", workEffortId);
					activityContent.set("workEffortContentTypeId", "ACTIVITY_HYPERLINK");
					activityContent.set("fromDate", UtilDateTime.nowTimestamp());
					// activityContent.set("helpfulLink", helpfulLink);
					activityContent.create();
				}

				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully created BookMarkUrl");
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);

	}
	// Agreement Program Template

	public static String searchRebatePrograms(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map < String, Object > context = UtilHttp.getCombinedMap(request);

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalLoginKey = request.getParameter("externalLoginKey");

		//String partyId = (String) context.get("partyId");
		String owner = UtilValidate.isNotEmpty(context.get("owner")) ? (String) context.get("owner") : (String) context.get("partyId");

		String agreementId = (String) context.get("agreementId");
		String programId = (String) context.get("programId");
		String payoutFrequency = (String) context.get("payoutFrequency");
		String payoutType = (String) context.get("payoutType");
		String programType = (String) context.get("programType");
		String fromDate = (String) context.get("fromDate");
		String thruDate = (String) context.get("thruDate");
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();

		long start = System.currentTimeMillis();

		try {

			String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);

			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();



			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {

				int viewIndex = 0;
				int highIndex = 0;
				int lowIndex = 0;
				int resultListSize = 0;
				int viewSize = 0;

				Map<String, Object> callCtxt = FastMap.newInstance();
				Map<String, Object> callResult = FastMap.newInstance();

				Map<String, Object> requestContext = new LinkedHashMap<>();
				requestContext.putAll(context);

				callCtxt.put("domainEntityType", domainEntityType);
				callCtxt.put("domainEntityId", domainEntityId);

				callCtxt.put("userLogin", userLogin);

				requestContext.put("totalGridFetch",
						org.groupfio.common.portal.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));

				callCtxt.put("requestContext", requestContext);

				callResult = dispatcher.runSync("rebate.findRebateTemplate", callCtxt);
				Debug.log("------callResult---------------"+callResult);
				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {

					List<GenericValue> rebateList = (List<GenericValue>) callResult.get("dataList");

					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					Map<String, Object> agreementTypes = AgreementDataHelper.getAgreementTypes(delegator, rebateList);
					//Map<String, Object> statusList = StatusUtil.getStatusList(delegator, rebateList, "currentStatusId", "IA_STATUS_ID");

					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, rebateList, "createdByUserLogin");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, rebateList, "lastModifiedByUserLogin");
					PartyHelper.getPartyNameByPartyIds(delegator, partyNames, rebateList, "partyIdFrom");
					PartyHelper.getPartyNameByPartyIds(delegator, partyNames, rebateList, "partyIdTo");
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);

					viewIndex = (int) callResult.get("viewIndex");
					highIndex = (int) callResult.get("highIndex");
					lowIndex = (int) callResult.get("lowIndex");
					resultListSize = (int) callResult.get("resultListSize");
					viewSize = (int) callResult.get("viewSize");

					int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for (GenericValue rebate : rebateList) {
						//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						Map<String, Object> data = new HashMap<String, Object>();

						//String agreementId = rebate.getString("agreementId");
						data.put("agreementId", rebate.getString("agreementId"));
						data.put("programName", rebate.getString("description"));
						data.put("description", rebate.getString("textData"));
						/*
						String dateAgreement = "";
						if (UtilValidate.isNotEmpty(rebate.getString("agreementDate"))) {
							dateAgreement = DataUtil.convertDateTimestamp(rebate.getString("agreementDate"), new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("agreementDate", dateAgreement);*/

						String dateFrom = "";
						if (UtilValidate.isNotEmpty(rebate.getString("fromDate"))) {
							dateFrom = DataUtil.convertDateTimestamp(rebate.getString("fromDate"), new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("fromDate", dateFrom);

						String dateThru = "";
						if (UtilValidate.isNotEmpty(rebate.getString("thruDate"))) {
							dateThru = DataUtil.convertDateTimestamp(rebate.getString("thruDate"), new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("thruDate", dateThru);

						String createdDate = "";
						if (UtilValidate.isNotEmpty(rebate.getString("createdDate"))) {
							createdDate = UtilDateTime.timeStampToString(rebate.getTimestamp("createdDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						/*data.put("createdOn", createdDate);
						data.put("createdByName", partyNames.get(rebate.getString("createdByUserLogin")));*/

						String modifiedDate = "";
						if (UtilValidate.isNotEmpty(rebate.getString("lastModifiedDate"))) {
							modifiedDate = UtilDateTime.timeStampToString(rebate.getTimestamp("lastModifiedDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						/*data.put("modifiedOn", modifiedDate);
						data.put("modifiedByName", partyNames.get(rebate.getString("lastModifiedByUserLogin")));

						data.put("domainEntityId", rebate.getString("domainEntityId"));
						data.put("domainEntityType", rebate.getString("domainEntityType"));
						data.put("domainEntityTypeDesc", DataHelper.convertToLabel( rebate.getString("domainEntityType") ));*/
						//	String payoutFrequency="";
						payoutFrequency="";
						payoutType="";
						String earnedAmount="";
						String completedAmount="";
						List<GenericValue> agreementItemList = delegator.findByAnd("AgreementItem", UtilMisc.toMap("agreementId", rebate.getString("agreementId")),null,false);		
						GenericValue agreementItemType=EntityUtil.getFirst(agreementItemList);
						if (UtilValidate.isNotEmpty(agreementItemType)) {
							//  EnumUtil.getEnumDescriptionByEnumId(delegator,agreementItemType.getString("payoutFrequency"));

							data.put("payoutFrequency_desc", EnumUtil.getEnumDescription(delegator, agreementItemType.getString("payoutFrequency"), "PAYOUT_FREQUENCY"));
							data.put("payoutType_desc", EnumUtil.getEnumDescription(delegator, agreementItemType.getString("payoutType"), "PAYOUT_TYPE"));
							data.put("amountType_desc", EnumUtil.getEnumDescription(delegator, agreementItemType.getString("amountType"), "REBATE_TYPE"));
							data.put("currencyUom_desc", EnumUtil.getEnumDescription(delegator, agreementItemType.getString("currencyUom"), "AMOUNT_TYPE"));

							data.put("payoutFrequency", agreementItemType.getString("payoutFrequency"));
							data.put("payoutType", agreementItemType.getString("payoutType"));
							data.put("amountType", agreementItemType.getString("amountType"));
							data.put("currencyUom", agreementItemType.getString("currencyUom"));


							data.put("earnedAmount",agreementItemType.getString("earnedAmount"));
							data.put("completedAmount", agreementItemType.getString("completedAmount"));
							data.put("programId", agreementItemType.getString("agreementItemTypeId"));
							String programName=agreementItemType.getString("agreementItemTypeId");
							if (UtilValidate.isNotEmpty(agreementItemType.getString("agreementItemTypeId"))) {

								GenericValue AgreementItemTypeDet = delegator.findOne("AgreementItemType", true, UtilMisc.toMap("agreementItemTypeId", agreementItemType.getString("agreementItemTypeId")));
								if (UtilValidate.isNotEmpty(AgreementItemTypeDet)) {
									programName=AgreementItemTypeDet.getString("description");
								}
							}
							data.put("programType", programName);


							data.put("expectedPayout", agreementItemType.getString("expectedPayout"));
							data.put("finishType", agreementItemType.getString("finishType"));
							data.put("productCategoryId", agreementItemType.getString("productCategoryId"));
							data.put("categoryId", agreementItemType.getString("categoryId"));
							//	data.put("isExcluded", agreementItemType.getString("isExcluded"));
							data.put("isExcluded_desc", EnumUtil.getEnumDescription(delegator, agreementItemType.getString("isExcluded"), "INDICATOR_TYPE"));	
							data.put("isExcluded", agreementItemType.getString("isExcluded"));	
							data.put("lyAmount", agreementItemType.getString("lyAmount"));
							data.put("tyAmount", agreementItemType.getString("tyAmount"));
							data.put("salePercentage", agreementItemType.getString("salePercentage"));

							data.put("finishType", agreementItemType.getString("finishType"));
							data.put("productCategoryId", agreementItemType.getString("productCategoryId"));
							data.put("categoryId", agreementItemType.getString("categoryId"));

							String finishType_desc="";
							GenericValue ProdCatalogDet=null;

							if (UtilValidate.isNotEmpty(agreementItemType.getString("finishType"))) {
								finishType_desc=CommonUtils.getCategoryNameByCategoryIds(delegator, agreementItemType.getString("finishType"));
							}
							data.put("finishType_desc", finishType_desc);

							String productCategoryId_desc="";
							GenericValue ProdCatalogCatDet=null;
							if (UtilValidate.isNotEmpty(agreementItemType.getString("productCategoryId"))) {
								productCategoryId_desc=CommonUtils.getCategoryNameByCategoryIds(delegator, agreementItemType.getString("productCategoryId"));
							}
							data.put("productCategoryId_desc", productCategoryId_desc);
							String categoryId_desc="";
							GenericValue ProdCategoryDet=null;
							if (UtilValidate.isNotEmpty(agreementItemType.getString("categoryId"))) {
								categoryId_desc=CommonUtils.getCategoryNameByCategoryIds(delegator, agreementItemType.getString("categoryId"));
							}
							data.put("categoryId_desc", categoryId_desc);
							Debug.log("-------data--------"+data);
							data.put("externalLoginKey", externalLoginKey);
						}
						dataList.add(data);

					}
					Debug.logInfo("prepare actual data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);

					result.put("highIndex", Integer.valueOf(highIndex));
					result.put("lowIndex", Integer.valueOf(lowIndex));

					result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
					result.put("totalRecords", nf.format(resultListSize));
					result.put("recordCount", resultListSize);
					result.put("chunkSize", viewSize);

					result.put("viewSize", viewSize);
					result.put("viewIndex", viewIndex);
				}
			} else {
				Debug.log("error==");
				Map<String, Object> data = new HashMap<String, Object>();
				if(UtilValidate.isNotEmpty(accessMatrixRes) && !ServiceUtil.isSuccess(accessMatrixRes)) {
					data.put("errorMessage", accessMatrixRes.get("errorMessage").toString());
				} else {
					data.put("errorMessage", "Access Denied");
				}
				dataList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}

		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}

	// Agreement Program Template

	public static String searchAgmtTemplates(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map < String, Object > context = UtilHttp.getCombinedMap(request);

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalLoginKey = request.getParameter("externalLoginKey");

		//String partyId = (String) context.get("partyId");
		String owner = UtilValidate.isNotEmpty(context.get("owner")) ? (String) context.get("owner") : (String) context.get("partyId");

		String agreementId = (String) context.get("agreementId");
		String programId = (String) context.get("programId");
		String payoutFrequency = (String) context.get("payoutFrequency");
		String payoutType = (String) context.get("payoutType");
		String fromDate = (String) context.get("fromDate");
		String thruDate = (String) context.get("thruDate");
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();

		long start = System.currentTimeMillis();

		try {

			String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);

			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();



			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {

				int viewIndex = 0;
				int highIndex = 0;
				int lowIndex = 0;
				int resultListSize = 0;
				int viewSize = 0;

				Map<String, Object> callCtxt = FastMap.newInstance();
				Map<String, Object> callResult = FastMap.newInstance();

				Map<String, Object> requestContext = new LinkedHashMap<>();
				requestContext.putAll(context);

				callCtxt.put("domainEntityType", domainEntityType);
				callCtxt.put("domainEntityId", domainEntityId);

				callCtxt.put("userLogin", userLogin);

				requestContext.put("totalGridFetch",
						org.groupfio.common.portal.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));

				callCtxt.put("requestContext", requestContext);

				callResult = dispatcher.runSync("rebate.findRebateTemplate", callCtxt);
				Debug.log("------callResult---------------"+callResult);
				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {

					List<GenericValue> rebateList = (List<GenericValue>) callResult.get("dataList");

					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					Map<String, Object> agreementTypes = AgreementDataHelper.getAgreementTypes(delegator, rebateList);
					//Map<String, Object> statusList = StatusUtil.getStatusList(delegator, rebateList, "currentStatusId", "IA_STATUS_ID");

					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, rebateList, "createdByUserLogin");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, rebateList, "lastModifiedByUserLogin");
					PartyHelper.getPartyNameByPartyIds(delegator, partyNames, rebateList, "partyIdFrom");
					PartyHelper.getPartyNameByPartyIds(delegator, partyNames, rebateList, "partyIdTo");
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);

					viewIndex = (int) callResult.get("viewIndex");
					highIndex = (int) callResult.get("highIndex");
					lowIndex = (int) callResult.get("lowIndex");
					resultListSize = (int) callResult.get("resultListSize");
					viewSize = (int) callResult.get("viewSize");

					int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for (GenericValue rebate : rebateList) {
						//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						Map<String, Object> data = new HashMap<String, Object>();

						//String agreementId = rebate.getString("agreementId");
						data.put("agreementId", rebate.getString("agreementId"));
						data.put("programName", rebate.getString("description"));
						data.put("description", rebate.getString("textData"));
						/*
						String dateAgreement = "";
						if (UtilValidate.isNotEmpty(rebate.getString("agreementDate"))) {
							dateAgreement = DataUtil.convertDateTimestamp(rebate.getString("agreementDate"), new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("agreementDate", dateAgreement);*/

						String dateFrom = "";
						if (UtilValidate.isNotEmpty(rebate.getString("fromDate"))) {
							dateFrom = DataUtil.convertDateTimestamp(rebate.getString("fromDate"), new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("fromDate", dateFrom);

						String dateThru = "";
						if (UtilValidate.isNotEmpty(rebate.getString("thruDate"))) {
							dateThru = DataUtil.convertDateTimestamp(rebate.getString("thruDate"), new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("thruDate", dateThru);

						String createdDate = "";
						if (UtilValidate.isNotEmpty(rebate.getString("createdDate"))) {
							createdDate = UtilDateTime.timeStampToString(rebate.getTimestamp("createdDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						/*data.put("createdOn", createdDate);
						data.put("createdByName", partyNames.get(rebate.getString("createdByUserLogin")));*/

						String modifiedDate = "";
						if (UtilValidate.isNotEmpty(rebate.getString("lastModifiedDate"))) {
							modifiedDate = UtilDateTime.timeStampToString(rebate.getTimestamp("lastModifiedDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						/*data.put("modifiedOn", modifiedDate);
						data.put("modifiedByName", partyNames.get(rebate.getString("lastModifiedByUserLogin")));

						data.put("domainEntityId", rebate.getString("domainEntityId"));
						data.put("domainEntityType", rebate.getString("domainEntityType"));
						data.put("domainEntityTypeDesc", DataHelper.convertToLabel( rebate.getString("domainEntityType") ));*/
						//	String payoutFrequency="";
						payoutFrequency="";
						payoutType="";
						String earnedAmount="";
						String completedAmount="";
						List<GenericValue> agreementItemList = delegator.findByAnd("AgreementItem", UtilMisc.toMap("agreementId", rebate.getString("agreementId")),null,false);		
						GenericValue agreementItemType=EntityUtil.getFirst(agreementItemList);
						if (UtilValidate.isNotEmpty(agreementItemType)) {
							data.put("payoutFrequency", agreementItemType.getString("payoutFrequency"));
							data.put("payoutType", agreementItemType.getString("payoutType"));
							data.put("earnedAmount", agreementItemType.getString("earnedAmount"));
							data.put("completedAmount", agreementItemType.getString("completedAmount"));
							data.put("externalLoginKey", externalLoginKey);
						}
						dataList.add(data);

					}
					Debug.logInfo("prepare actual data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);

					result.put("highIndex", Integer.valueOf(highIndex));
					result.put("lowIndex", Integer.valueOf(lowIndex));

					result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
					result.put("totalRecords", nf.format(resultListSize));
					result.put("recordCount", resultListSize);
					result.put("chunkSize", viewSize);

					result.put("viewSize", viewSize);
					result.put("viewIndex", viewIndex);
				}
			} else {
				Debug.log("error==");
				Map<String, Object> data = new HashMap<String, Object>();
				if(UtilValidate.isNotEmpty(accessMatrixRes) && !ServiceUtil.isSuccess(accessMatrixRes)) {
					data.put("errorMessage", accessMatrixRes.get("errorMessage").toString());
				} else {
					data.put("errorMessage", "Access Denied");
				}
				dataList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}

		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	} 

	public static String getAssocParties(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");

		try {
			List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			if(UtilValidate.isNotEmpty(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId)) {
				if (CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
					dataList = SrUtil.getSrAssocParties(delegator, domainEntityId);
				} else if (domainEntityType.equals(DomainEntityType.REBATE)) {
					dataList = CommonPortalUtil.getAgreementAssocParties(delegator, domainEntityId);
				} else {

					String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);

					List<EntityCondition> conditions = new ArrayList<EntityCondition>();
					conditions.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
					conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));

					EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);

					DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
					dynamicViewEntity.addMemberEntity("PA", "CommonPartyAssoc");
					dynamicViewEntity.addAlias("PA", "domainEntityId");
					dynamicViewEntity.addAlias("PA", "domainEntityType");
					dynamicViewEntity.addAlias("PA", "partyId");
					dynamicViewEntity.addAlias("PA", "roleTypeId");
					dynamicViewEntity.addAlias("PA", "fromDate");
					dynamicViewEntity.addAlias("PA", "thruDate");
					dynamicViewEntity.addAlias("PA", "lastUpdatedTxStamp");

					List<GenericValue> partyAssocList = EntityQuery.use(delegator).from(dynamicViewEntity).where(condition).filterByDate().queryList();
					if(UtilValidate.isNotEmpty(partyAssocList)) {
						for(GenericValue partyAssoc : partyAssocList) {
							Map<String, Object> data = new HashMap<>();
							String partyId = partyAssoc.getString("partyId");
							String customerName = PartyHelper.getPartyName(delegator, partyId, false);

							Map<String,String> partyContactInfo = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
							String phoneNumber = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("PrimaryPhone") : "";
							String infoString = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("EmailAddress") : "";

							String phoneSolicitation = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("phoneSolicitation") : "";
							String emailSolicitation = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("emailSolicitation") : "";

							data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, partyAssoc));
							data.put("phoneNumber", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(phoneNumber) );
							data.put("infoString", infoString );
							data.put("name", customerName);
							data.put("phoneSolicitation", phoneSolicitation );
							data.put("emailSolicitation", emailSolicitation );
							data.put("roleTypeDesc", org.groupfio.common.portal.util.DataUtil.getRoleTypeDescription(delegator, partyAssoc.getString("roleTypeId")));

							data.put("fromDate", UtilDateTime.timeStampToString(partyAssoc.getTimestamp("fromDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
							data.put("thruDate", UtilDateTime.timeStampToString(partyAssoc.getTimestamp("thruDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
							data.put("lastUpdatedTxStamp", UtilDateTime.timeStampToString(partyAssoc.getTimestamp("lastUpdatedTxStamp"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));

							dataList.add(data);
						}
					}

				}
			}
			result.put("list", dataList);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			//e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String addAssocParties(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String selectedPartyIds = request.getParameter("selectedPartyIds");
		String selectedRoleTypeIds = request.getParameter("selectedRoleTypeIds");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");

		Map<String, Object> result = FastMap.newInstance();

		try {
			if (UtilValidate.isNotEmpty(selectedPartyIds)) {
				List<GenericValue> tobeStore = new ArrayList<>();
				List<String> partyIds = Arrays.asList(selectedPartyIds.split(","));
				List<String> roleTypeIds = Arrays.asList(selectedRoleTypeIds.split(","));
				for (int i = 0; i < partyIds.size(); i++) {
					String partyId = partyIds.get(i);
					String roleTypeId = roleTypeIds.get(i);

					if (CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
						Map<String, Object> input = UtilMisc.toMap("custRequestId", domainEntityId, "partyId", partyId, "roleTypeId", roleTypeId);
						GenericValue entity = EntityQuery.use(delegator).from("CustRequestParty")
								.where(input).filterByDate().queryFirst();
						if (UtilValidate.isEmpty(entity)) {
							entity = delegator.makeValue("CustRequestParty", input);
							entity.put("fromDate", UtilDateTime.nowTimestamp());
							tobeStore.add(entity);
						}
					} else if (domainEntityType.equals(DomainEntityType.REBATE)) {
						Map<String, Object> input = UtilMisc.toMap("agreementId", domainEntityId, "partyId", partyId, "roleTypeId", roleTypeId);
						GenericValue entity = EntityQuery.use(delegator).from("AgreementRole")
								.where(input).queryFirst();
						if (UtilValidate.isEmpty(entity)) {
							entity = delegator.makeValue("AgreementRole", input);
							tobeStore.add(entity);
						}
					} else {
						Map<String, Object> input = UtilMisc.toMap("domainEntityId", domainEntityId, "domainEntityType", domainEntityType, "partyId", partyId, "roleTypeId", roleTypeId);
						GenericValue entity = EntityQuery.use(delegator).from("CommonPartyAssoc")
								.where(input).filterByDate().queryFirst();
						if (UtilValidate.isEmpty(entity)) {
							entity = delegator.makeValue("CommonPartyAssoc", input);
							entity.put("fromDate", UtilDateTime.nowTimestamp());
							tobeStore.add(entity);
						}
					}

				}

				if (UtilValidate.isNotEmpty(tobeStore)) {
					delegator.storeAll(tobeStore);
				}

				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(ModelService.SUCCESS_MESSAGE, "Successfully associate parties.");
			}
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String removeAssocParties(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String selectedPartyIds = request.getParameter("selectedPartyIds");
		String selectedRoleTypeIds = request.getParameter("selectedRoleTypeIds");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");

		Map<String, Object> result = FastMap.newInstance();

		try {
			if (UtilValidate.isNotEmpty(selectedPartyIds)) {
				List<GenericValue> tobeStore = new ArrayList<>();
				List<String> partyIds = Arrays.asList(selectedPartyIds.split(","));
				List<String> roleTypeIds = Arrays.asList(selectedRoleTypeIds.split(","));
				for (int i = 0; i < partyIds.size(); i++) {
					String partyId = partyIds.get(i);
					String roleTypeId = roleTypeIds.get(i);

					if (CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
						Map<String, Object> input = UtilMisc.toMap("custRequestId", domainEntityId, "partyId", partyId, "roleTypeId", roleTypeId);
						GenericValue entity = EntityQuery.use(delegator).from("CustRequestParty")
								.where(input).filterByDate().queryFirst();
						if (UtilValidate.isNotEmpty(entity)) {
							entity.put("thruDate", UtilDateTime.nowTimestamp());
							tobeStore.add(entity);
						}
					} else if (domainEntityType.equals(DomainEntityType.REBATE)) {
						Map<String, Object> input = UtilMisc.toMap("agreementId", domainEntityId, "partyId", partyId, "roleTypeId", roleTypeId);
						GenericValue entity = EntityQuery.use(delegator).from("AgreementRole")
								.where(input).queryFirst();
						if (UtilValidate.isNotEmpty(entity)) {
							entity.remove();
						}
					} else {
						Map<String, Object> input = UtilMisc.toMap("domainEntityId", domainEntityId, "domainEntityType", domainEntityType, "partyId", partyId, "roleTypeId", roleTypeId);
						GenericValue entity = EntityQuery.use(delegator).from("CommonPartyAssoc")
								.where(input).filterByDate().queryFirst();
						if (UtilValidate.isEmpty(entity)) {
							entity.put("thruDate", UtilDateTime.nowTimestamp());
							tobeStore.add(entity);
						}
					}

				}

				if (UtilValidate.isNotEmpty(tobeStore)) {
					delegator.storeAll(tobeStore);
				}

				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(ModelService.SUCCESS_MESSAGE, "Successfully removed associate parties.");
			}
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String getAllParties(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String firstName = (String) context.get("firstName");
		String lastName = (String) context.get("lastName");
		String emailId = (String) context.get("emailId");
		String phoneNum = (String) context.get("phoneNum");
		String roleTypeId = (String) context.get("roleTypeId");
		String groupName = (String) context.get("groupName");
		String isIncludeInactiveUser = (String) context.get("isIncludeInactiveUser");

		String custRequestId = (String) context.get("srNumber");

		try {
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			if (UtilValidate.isNotEmpty(isIncludeInactiveUser) && isIncludeInactiveUser.equals("Y")) {
			} else {
				if(!"CUSTOMER".equals(roleTypeId) && !"CONTACT".equals(roleTypeId)) {
					conditions.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
					
					conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
							EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"),
							EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
							));
				} 
				
				if("ACCOUNT".equals(roleTypeId) || "LEAD".equals(roleTypeId)) {
					conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("supplementalPartyTypeId", EntityOperator.EQUALS, null),
							EntityCondition.makeCondition("supplementalPartyTypeId", EntityOperator.EQUALS, "")
							));
				}
			}

			if(UtilValidate.isNotEmpty(firstName)) {
				conditions.add(EntityCondition.makeCondition(
						EntityOperator.OR,
						EntityCondition.makeCondition("firstName", EntityOperator.LIKE, ""+firstName+"%"),
						EntityCondition.makeCondition("groupName", EntityOperator.LIKE, ""+firstName+"%")
						));

			}
			if(UtilValidate.isNotEmpty(lastName)) {
				conditions.add(EntityCondition.makeCondition("lastName", EntityOperator.LIKE, ""+lastName+"%"));
			}
			if(UtilValidate.isNotEmpty(groupName)) {
				conditions.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE, ""+groupName+"%"));	
			}
			if(UtilValidate.isNotEmpty(emailId)) {
				conditions.add(EntityCondition.makeCondition("infoString", EntityOperator.LIKE, ""+emailId+"%"));
				conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"));
			}
			if(UtilValidate.isNotEmpty(phoneNum)) {
				conditions.add(EntityCondition.makeCondition("contactNumber", EntityOperator.LIKE, ""+phoneNum+"%"));
				conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE"));
			}
			if(UtilValidate.isNotEmpty(roleTypeId)) {
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			}
			String securityParentRole = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
			String parentTypeId = UtilValidate.isNotEmpty(context.get("parentTypeId")) ?(String) context.get("parentTypeId") : securityParentRole;

			if(UtilValidate.isNotEmpty(parentTypeId)) {
				conditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parentTypeId));
			}

			List<GenericValue> roleType = EntityQuery.use(delegator).from("RoleType").queryList();
			Map<String, Object> roleList = new HashMap<>();
			if(UtilValidate.isNotEmpty(roleType)) {
				roleList = org.fio.admin.portal.util.DataUtil.getMapFromGeneric(roleType, "roleTypeId", "description", false);
			}

			if(UtilValidate.isNotEmpty(custRequestId)) {
				List<GenericValue> custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId", custRequestId).filterByDate().queryList();
				if(UtilValidate.isNotEmpty(custRequestParty)) {
					List<String> existingParty = EntityUtil.getFieldListFromEntityList(custRequestParty, "partyId", true);
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, existingParty));
				}
			}
			

			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);

			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("P", "Party");
			dynamicViewEntity.addAlias("P", "partyId", "partyId", null, Boolean.FALSE, Boolean.TRUE, null);
			dynamicViewEntity.addAlias("P", "statusId");
			dynamicViewEntity.addAlias("P", "lastUpdatedTxStamp");

			dynamicViewEntity.addMemberEntity("UL", "UserLogin");
			dynamicViewEntity.addAlias("UL", "userLoginId");
			dynamicViewEntity.addAlias("UL", "enabled");
			dynamicViewEntity.addViewLink("P", "UL", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));

			dynamicViewEntity.addMemberEntity("PER", "Person");
			dynamicViewEntity.addAlias("PER", "firstName");
			dynamicViewEntity.addAlias("PER", "lastName");
			dynamicViewEntity.addViewLink("P", "PER", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));

			dynamicViewEntity.addMemberEntity("PG", "PartyGroup");
			dynamicViewEntity.addAlias("PG", "groupName");
			dynamicViewEntity.addViewLink("P", "PG", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));

			dynamicViewEntity.addMemberEntity("PR", "PartyRole");
			dynamicViewEntity.addAlias("PR", "roleTypeId");
			dynamicViewEntity.addViewLink("P", "PR", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

			dynamicViewEntity.addMemberEntity("RT", "RoleType");
			dynamicViewEntity.addAlias("RT", "parentTypeId");
			dynamicViewEntity.addViewLink("PR", "RT", Boolean.FALSE, ModelKeyMap.makeKeyMapList("roleTypeId"));

			dynamicViewEntity.addMemberEntity("PCM", "PartyContactMech");
			dynamicViewEntity.addAlias("PCM", "contactMechId");
			dynamicViewEntity.addAlias("PCM", "allowSolicitation");
			dynamicViewEntity.addAlias("PCM", "fromDate");
			dynamicViewEntity.addAlias("PCM", "thruDate");
			dynamicViewEntity.addViewLink("P", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));


			dynamicViewEntity.addMemberEntity("PCMP", "PartyContactMechPurpose");
			dynamicViewEntity.addAlias("PCMP", "contactMechPurposeTypeId");
			dynamicViewEntity.addAlias("PCMP", "purposeFromDate", "fromDate",null,Boolean.FALSE,Boolean.FALSE,null);
			dynamicViewEntity.addAlias("PCMP", "purposeThruDate", "thruDate",null,Boolean.FALSE,Boolean.FALSE,null);
			dynamicViewEntity.addViewLink("PCM", "PCMP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("contactMechId"));

			if(UtilValidate.isNotEmpty(emailId)) {
				dynamicViewEntity.addMemberEntity("CM", "ContactMech");
				dynamicViewEntity.addAlias("CM", "contactMechTypeId");
				dynamicViewEntity.addAlias("CM", "infoString");
				dynamicViewEntity.addViewLink("PCM", "CM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("contactMechId"));
			}
			if(UtilValidate.isNotEmpty(phoneNum)) {
				dynamicViewEntity.addMemberEntity("TC", "TelecomNumber");
				dynamicViewEntity.addAlias("TC", "contactNumber");
				dynamicViewEntity.addViewLink("PCM", "TC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("contactMechId"));
			}

			dynamicViewEntity.addMemberEntity("PSD", "PartySupplementalData");
			dynamicViewEntity.addAlias("PSD", "supplementalPartyTypeId");
			dynamicViewEntity.addViewLink("P", "PSD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));


			List<GenericValue> partyList = EntityQuery.use(delegator).from(dynamicViewEntity).where(condition).filterByDate("fromDate","thruDate","purposeFromDate","purposeThruDate").maxRows(1000).queryList();
			if(UtilValidate.isNotEmpty(partyList)) {
				for(GenericValue partyGv : partyList) {
					Map<String, Object> data = new HashMap<>();
					String partyId = partyGv.getString("partyId");
					data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, partyGv));
					String customerName = "";
					if(UtilValidate.isEmpty(partyGv.getString("firstName"))) {
						customerName = UtilValidate.isNotEmpty(partyGv) ? partyGv.getString("groupName") : "";
					} else {
						customerName = partyGv.getString("firstName")+ (UtilValidate.isNotEmpty(partyGv.getString("lastName")) ? " "+partyGv.getString("lastName") : "" );
					}
					Map<String,String> partyContactInfo = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
					String phoneNumber = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("PrimaryPhone") : "";
					String infoString = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("EmailAddress") : "";

					String phoneSolicitation = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("phoneSolicitation") : "";
					String emailSolicitation = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("emailSolicitation") : "";

					data.put("phoneNumber", phoneNumber );
					data.put("infoString", infoString );
					data.put("name", customerName);
					data.put("phoneSolicitation", phoneSolicitation );
					data.put("emailSolicitation", emailSolicitation );
					data.put("roleTypeDesc", UtilValidate.isNotEmpty(roleList) && UtilValidate.isNotEmpty(partyGv.getString("roleTypeId")) ? roleList.get(partyGv.getString("roleTypeId")) : "" );
					data.put("fromDate", UtilValidate.isNotEmpty(partyGv.getString("fromDate")) ? (org.fio.admin.portal.util.DataUtil.convertDateTimestamp(partyGv.getString("fromDate"), new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING)) : "");
					data.put("lastUpdatedTxStamp", UtilValidate.isNotEmpty(partyGv.getString("lastUpdatedTxStamp")) ? (org.fio.admin.portal.util.DataUtil.convertDateTimestamp(partyGv.getString("lastUpdatedTxStamp"), new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING)) : "");
					dataList.add(data);
				}
			}
			result.put("list", dataList);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			//e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String searchInvoices(HttpServletRequest request, HttpServletResponse response) {

		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<GenericValue> resultList = null;

		String custReqId = request.getParameter("srNumber");
		Debug.log("custReqId=== "+custReqId);
		List conditionList = FastList.newInstance();
		List conditionListInvoice = FastList.newInstance();
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();

			if (UtilValidate.isNotEmpty(custReqId)) {
				
				List conditionsList = FastList.newInstance();

				conditionsList
				.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custReqId));

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

				List<GenericValue> custRequestWorkEffortList = delegator.findList("CustRequestWorkEffort",
						mainConditons, UtilMisc.toSet("workEffortId"), null, null, false);
				if (UtilValidate.isNotEmpty(custRequestWorkEffortList)) {
					List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList,
							"workEffortId", true);
					Debug.log("workEffortIds==== "+workEffortIds);

					conditionList
					.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.IN, workEffortIds));
				} else {
					conditionList.add(
							EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, "999888999888"));
				}

				EntityCondition mainConditonsInvoiceAssoc = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				Debug.log("mainConditonsInvoiceAssoc==== "+mainConditonsInvoiceAssoc);
				List<GenericValue> custRequestWorkEffortInvoiceList = delegator.findList("InvoiceEntityAssoc",
						mainConditonsInvoiceAssoc, null, null, null, false);
				Debug.log("custRequestWorkEffortInvoiceList==== "+custRequestWorkEffortInvoiceList);
				if (UtilValidate.isNotEmpty(custRequestWorkEffortInvoiceList)) {
					List<String> invoiceIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortInvoiceList,
							"invoiceId", true);
					Debug.log("invoiceIds==== "+invoiceIds);
					conditionList.clear();
					conditionListInvoice
					.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
				}
			}


			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
					EntityFindOptions.CONCUR_READ_ONLY, false);
			efo.setOffset(0);
			efo.setLimit(100);

			Debug.logInfo("conditionListInvoice:" + conditionListInvoice, MODULE);
			EntityCondition mainConditonsInvoice = EntityCondition.makeCondition(conditionListInvoice, EntityOperator.AND);

			Debug.logInfo("mainConditonsInvoice: " + mainConditonsInvoice, MODULE);

			Debug.logInfo("list 1 start: " + UtilDateTime.nowTimestamp(), MODULE);

			Set<String> fieldsToSelect = new LinkedHashSet<String>();

			fieldsToSelect.add("partyId");
			fieldsToSelect.add("invoiceId");
			fieldsToSelect.add("invoiceTypeId");
			fieldsToSelect.add("statusId");
			fieldsToSelect.add("invoiceDate");
			fieldsToSelect.add("referenceNumber");
			fieldsToSelect.add("description");
			fieldsToSelect.add("paidDate");
			fieldsToSelect.add("externalId");

			GenericValue systemProperty = EntityQuery.use(delegator).select("systemPropertyValue")
					.from("SystemProperty")
					.where("systemResourceId", "general", "systemPropertyId", "fio.grid.fetch.limit").queryFirst();
			// set the page parameters
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}

			int fioGridFetch = UtilValidate.isNotEmpty(systemProperty)
					&& UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue"))
					? Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;

					int viewSize = fioGridFetch;
					try {
						viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
					} catch (Exception e) {
						viewSize = fioGridFetch;
					}

					int highIndex = 0;
					int lowIndex = 0;
					// get the indexes for the partial list
					lowIndex = viewIndex * viewSize + 1;
					highIndex = (viewIndex + 1) * viewSize;

					// set distinct on so we only get one row per
					// using list iterator
					EntityListIterator pli = EntityQuery.use(delegator).select(fieldsToSelect).from("Invoice")
							.where(mainConditonsInvoice).orderBy("-invoiceDate").cursorScrollInsensitive().fetchSize(highIndex).distinct()
							.cache(true).queryIterator();
					// get the partial list for this page
					resultList = pli.getPartialList(lowIndex, viewSize);
					// close the list iterator
					pli.close();

					String isEnableInvoiceModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_INVOICE_MODULE");
					String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
					String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
					if (UtilValidate.isNotEmpty(resultList)) {
						for (GenericValue invoiceDetails : resultList) {


							Map<String, Object> data = new HashMap<String, Object>();

							String invoiceResId = invoiceDetails.getString("invoiceId");
							String invoicePartyId = invoiceDetails.getString("partyId");
							String refNumber = invoiceDetails.getString("referenceNumber");
							String date = invoiceDetails.getString("invoiceDate");
							String paidDate = invoiceDetails.getString("paidDate");
							String invoiceStatusId = invoiceDetails.getString("statusId");
							String type = invoiceDetails.getString("invoiceTypeId");
							String invoiceExternalId = invoiceDetails.getString("externalId");

							String statusItemDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, invoiceStatusId,
									"INVOICE_STATUS");
							GenericValue invoiceTypeGv = EntityQuery.use(delegator).select("description")
									.from("InvoiceType")
									.where("invoiceTypeId",type ).queryFirst();
							if(UtilValidate.isNotEmpty(invoiceTypeGv)) {
								type = invoiceTypeGv.getString("description");
							}

							String name = org.fio.homeapps.util.DataUtil.getPartyName(delegator, invoicePartyId);

							if (UtilValidate.isNotEmpty(isEnableInvoiceModule) && isEnableInvoiceModule.equals("Y")) {
								BigDecimal outstandingAmount = BigDecimal.ZERO;
								BigDecimal invoiceTotal = BigDecimal.ZERO;
								outstandingAmount = org.groupfio.common.portal.invoice.InvoiceWorker.getInvoiceNotApplied(delegator,invoiceResId);
								invoiceTotal = org.groupfio.common.portal.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoiceResId);
								if (UtilValidate.isNotEmpty(outstandingAmount)) {
									data.put("outstandingAmount", outstandingAmount.setScale(2, BigDecimal.ROUND_CEILING).toString());
								}
								if (UtilValidate.isNotEmpty(invoiceTotal)) {
									data.put("total", invoiceTotal.setScale(2, BigDecimal.ROUND_CEILING).toString());
								}
							}
							
							data.put("srNumber", custReqId);
							data.put("invoiceId", invoiceResId);
							data.put("partyId", invoicePartyId);
							data.put("name", name);
							data.put("statusId", statusItemDesc);
							data.put("refNumber", refNumber);
							data.put("invoiceExternalId", invoiceExternalId);
							data.put("date",
									UtilValidate.isNotEmpty(invoiceDetails.get("invoiceDate"))
									? UtilDateTime.timeStampToString(invoiceDetails.getTimestamp("invoiceDate"),
											globalDateFormat, TimeZone.getDefault(), null) : "");
							data.put("paidDate",
									UtilValidate.isNotEmpty(invoiceDetails.get("paidDate"))
									? UtilDateTime.timeStampToString(invoiceDetails.getTimestamp("paidDate"),
											globalDateFormat, TimeZone.getDefault(), null) : "");
							data.put("invoiceType", type);
							GenericValue issueMaterial = EntityQuery.use(delegator).from("IssueMaterial").where("invoiceId", invoiceResId).queryFirst();
							String activityId=   UtilValidate.isNotEmpty(issueMaterial)?issueMaterial.getString("workEffortId"):"";
							data.put("activityId", activityId);
							
							String techName="";
							
							if (UtilValidate.isNotEmpty(issueMaterial)) {
								techName = issueMaterial.getString("partyId");
								techName = PartyHelper.getPartyName(delegator, issueMaterial.getString("partyId"),false);
							}
							data.put("techName", techName);
							
							String issuedType ="";
							
							data.put("issuedType", UtilValidate.isNotEmpty(issueMaterial)?issueMaterial.getString("issuedType"):"");
							
							dataList.add(data);
						}
					}

		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("errorMessage", e.getMessage());
			data.put("errorResult", new ArrayList<Map<String, Object>>());
			dataList.add(data);
		}
		//Debug.log("dataList==="+dataList);
		return AjaxEvents.doJSONResponse(response, dataList);
	}
	public static String findInvoiceItemList(HttpServletRequest request, HttpServletResponse response) {
		Debug.log("findInvoicesItemList===");
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<GenericValue> resultList = null;

		String invoiceId = request.getParameter("invoiceId");
		Debug.log("findInvoicesItemList==="+invoiceId);

		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();

			// construct role conditions
			if (UtilValidate.isNotEmpty(invoiceId)) {
				EntityCondition invoiceIdCondition = EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,
						invoiceId);
				conditions.add(invoiceIdCondition);
			}

			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
					EntityFindOptions.CONCUR_READ_ONLY, false);
			efo.setOffset(0);
			efo.setLimit(100);

			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);

			Debug.logInfo("mainConditons: " + mainConditons, MODULE);

			Debug.logInfo("list 1 start: " + UtilDateTime.nowTimestamp(), MODULE);

			Set<String> fieldsToSelect = new LinkedHashSet<String>();

			fieldsToSelect.add("invoiceItemSeqId");
			fieldsToSelect.add("invoiceItemTypeId");
			fieldsToSelect.add("productId");
			fieldsToSelect.add("quantity");
			fieldsToSelect.add("amount");
			fieldsToSelect.add("description");


			GenericValue systemProperty = EntityQuery.use(delegator).select("systemPropertyValue")
					.from("SystemProperty")
					.where("systemResourceId", "general", "systemPropertyId", "fio.grid.fetch.limit").queryFirst();
			// set the page parameters
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}

			int fioGridFetch = UtilValidate.isNotEmpty(systemProperty)
					&& UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue"))
					? Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;

					int viewSize = fioGridFetch;
					try {
						viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
					} catch (Exception e) {
						viewSize = fioGridFetch;
					}

					int highIndex = 0;
					int lowIndex = 0;
					// get the indexes for the partial list
					lowIndex = viewIndex * viewSize + 1;
					highIndex = (viewIndex + 1) * viewSize;

					// set distinct on so we only get one row per
					// using list iterator
					EntityListIterator pli = EntityQuery.use(delegator).select(fieldsToSelect).from("InvoiceItem")
							.where(mainConditons).orderBy("invoiceItemSeqId").cursorScrollInsensitive().fetchSize(highIndex).distinct()
							.cache(true).queryIterator();
					// get the partial list for this page
					resultList = pli.getPartialList(lowIndex, viewSize);

					// close the list iterator
					pli.close();

					String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
					String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
					if (UtilValidate.isNotEmpty(resultList)) {
						for (GenericValue invoiceItemDetails : resultList) {


							Map<String, Object> data = new HashMap<String, Object>();

							String invoiceResId = invoiceItemDetails.getString("invoiceId");
							String invoiceItemSeqId = invoiceItemDetails.getString("invoiceItemSeqId");
							String productId = invoiceItemDetails.getString("productId");
							String quantity = invoiceItemDetails.getString("quantity");
							String amount = invoiceItemDetails.getString("amount");
							String type = invoiceItemDetails.getString("invoiceItemTypeId");
							String description = invoiceItemDetails.getString("description");


							GenericValue invoiceItemTypeGv = EntityQuery.use(delegator).select("description")
									.from("InvoiceItemType")
									.where("invoiceItemTypeId",type ).queryFirst();
							if(UtilValidate.isNotEmpty(invoiceItemTypeGv)) {
								type = invoiceItemTypeGv.getString("description");
							}
							BigDecimal itemAmount = new BigDecimal(amount);
							BigDecimal invoiceTotalQty = BigDecimal.ONE;
							//outstandingAmount = org.groupfio.common.portal.invoice.InvoiceWorker.getInvoiceNotApplied(delegator,invoiceId);
							//invoiceTotal = org.groupfio.common.portal.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoiceId);
							data.put("invoiceId", invoiceId);
							data.put("invoiceItemSeqId", invoiceItemSeqId);
							data.put("invoiceItemType", type);
							data.put("productId", productId);
							data.put("description", description);
							if (UtilValidate.isNotEmpty(itemAmount)) {
								data.put("itemAmount", "$"+itemAmount.setScale(2, BigDecimal.ROUND_CEILING).toString());
							}

							if (UtilValidate.isEmpty(quantity)) {
								data.put("quantity",invoiceTotalQty);
							}else {
								data.put("quantity", quantity);
							}
							dataList.add(data);
						}
					}

		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("errorMessage", e.getMessage());
			data.put("errorResult", new ArrayList<Map<String, Object>>());
			dataList.add(data);
		}
		//Debug.log("dataList==="+dataList);
		return AjaxEvents.doJSONResponse(response, dataList);
	}

	public static String getTeamMembersList(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		// construct search conditions

		try {

			String roleTypeId = UtilValidate.isNotEmpty(context.get("roleTypeId")) ? (String) context.get("roleTypeId")
					: "SALES_REP";

			if (UtilValidate.isNotEmpty(roleTypeId)) {
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			} else {
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SALES_REP"));
			}
			if (UtilValidate.isNotEmpty(lastName)) {
				conditions.add(EntityCondition.makeCondition("lastName", EntityOperator.LIKE,
						EntityFunction.UPPER("" + lastName + "%")));
			}
			if (UtilValidate.isNotEmpty(firstName)) {
				conditions.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE,
						EntityFunction.UPPER("" + firstName + "%")));
			}

			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> partyToSummartyByRole = EntityQuery.use(delegator).from("PartyToSummaryByRole")
					.where(mainConditons).queryList();
			int id = 1;
			for (GenericValue roles : partyToSummartyByRole) {
				Map<String, Object> partyToSummartyByRoleMap = FastMap.newInstance();
				Map<String, Object> data = new HashMap<String, Object>();
				id = id + 1;
				data.put("id", id + "");
				data.put("name",
						roles.getString("firstName") + " " + roles.getString("lastName"));
				data.put("partyId", roles.getString("partyId"));
				results.add(data);
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	
	public static String getDedupPartyDetails(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String customerName = request.getParameter("customerName");
		String address1 = request.getParameter("address1");
		String zipCode = request.getParameter("zipCode");
		String middleName = "";
		try {
			if(UtilValidate.isNotEmpty(customerName)) {
				String[] names = customerName.split(" ");
				if(UtilValidate.isNotEmpty(names) && names.length == 1) {
					firstName = names[0];
				} else if(UtilValidate.isNotEmpty(names) && names.length == 2) {
					firstName = names[0];
					lastName = names[1]; 
				} else if(UtilValidate.isNotEmpty(names) && names.length == 3) {
					firstName = names[0];
					middleName = names[1];
					lastName = names[2];
				} else if(UtilValidate.isNotEmpty(names) && names.length > 3) {
					firstName = names[0];
					middleName = names[1];
					lastName = names[2];
				}
			}
			if(org.fio.admin.portal.util.DataUtil.validateFieldsNotEmpty(UtilMisc.toList(lastName,address1, zipCode))) {
				DynamicViewEntity dynamicEntity = new DynamicViewEntity();
				dynamicEntity.addMemberEntity("PER", "Person");
				dynamicEntity.addAlias("PER", "partyId");
				dynamicEntity.addAlias("PER", "firstName");
				dynamicEntity.addAlias("PER", "middleName");
				dynamicEntity.addAlias("PER", "lastName");
				
				dynamicEntity.addMemberEntity("PCM", "PartyContactMech");
				dynamicEntity.addAlias("PCM", "contactMechId");
				dynamicEntity.addViewLink("PER", "PCM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
				
				dynamicEntity.addMemberEntity("CM", "ContactMech");
				dynamicEntity.addAlias("CM", "contactMechTypeId");
				dynamicEntity.addViewLink("PCM", "CM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
				
				//contactMechExprs.add(EntityCondition.makeCondition("contactMechTypeId", "POSTAL_ADDRESS"));

				dynamicEntity.addMemberEntity("PA", "PostalAddress");
				dynamicEntity.addAlias("PA", "address1");
				dynamicEntity.addAlias("PA", "address2");
				dynamicEntity.addAlias("PA", "city");
				dynamicEntity.addAlias("PA", "stateProvinceGeoId");
				dynamicEntity.addAlias("PA", "countryGeoId");
				dynamicEntity.addAlias("PA", "postalCode");
				dynamicEntity.addAlias("PA", "postalCodeExt");
				dynamicEntity.addViewLink("PCM", "PA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
				
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS"));
				conditions.add(EntityCondition.makeCondition("lastName", EntityOperator.EQUALS, lastName));
				conditions.add(EntityCondition.makeCondition("address1", EntityOperator.EQUALS, address1));
				conditions.add(EntityCondition.makeCondition("postalCode", EntityOperator.EQUALS, zipCode));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				
				GenericValue dedupParty = EntityQuery.use(delegator).from(dynamicEntity).where(mainConditons).queryFirst();
				if(UtilValidate.isNotEmpty(dedupParty)) {
					result.put("partyId", dedupParty.getString("partyId"));
					result.put("name", dedupParty.getString("firstName") +(UtilValidate.isNotEmpty(dedupParty.getString("lastName")) ? " "+dedupParty.getString("lastName") : ""));
					result.put(EventResponse.STATUS, EventResponse.SUCCESS);
					result.put(EventResponse.MESSAGE, "PARTY_EXISTS");
				} else {
					result.put(EventResponse.STATUS, EventResponse.SUCCESS);
					result.put(EventResponse.MESSAGE, "PARTY_NOT_EXISTS");
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put(EventResponse.STATUS, EventResponse.ERROR);
			result.put(EventResponse.MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		
		return doJSONResponse(response, result);
	}
	public static String createNewCustomer(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<String, Object>();
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String customerName = request.getParameter("customerName");
		String address1 = request.getParameter("address1");
		String zipCode = request.getParameter("zipCode");
		String isContractor = request.getParameter("isContractor");	
		String middleName = "";
		try {
			if(UtilValidate.isNotEmpty(customerName)) {
				String[] names = customerName.split(" ");
				if(UtilValidate.isNotEmpty(names) && names.length == 1) {
					firstName = names[0];
				} else if(UtilValidate.isNotEmpty(names) && names.length == 2) {
					firstName = names[0];
					lastName = names[1]; 
				} else if(UtilValidate.isNotEmpty(names) && names.length == 3) {
					firstName = names[0];
					middleName = names[1];
					lastName = names[2];
				} else if(UtilValidate.isNotEmpty(names) && names.length > 3) {
					firstName = names[0];
					middleName = names[1];
					lastName = names[2];
				}
			}
			if(UtilValidate.isNotEmpty(firstName) && UtilValidate.isNotEmpty(lastName)) {
				Map<String, Object> result1 = dispatcher.runSync("cp.createCustomer", UtilMisc.toMap("userLogin", userLogin, "firstName", firstName, "middleName", middleName, "lastName", lastName, "isContractor", isContractor));
				if(ServiceUtil.isSuccess(result1)) {
					result.put("partyId", result1.get("partyId"));
					result.put(EventResponse.STATUS, EventResponse.SUCCESS);
					result.put(EventResponse.MESSAGE, "Customer Created Successfully.");
				} else {
					result.put(EventResponse.STATUS, EventResponse.ERROR);
					result.put(EventResponse.MESSAGE, ServiceUtil.getErrorMessage(result1));
				}
			} else {
				String errorMsg = "";
				if(UtilValidate.isEmpty(firstName)) {
					errorMsg = errorMsg+"First name";
				} 
				if(UtilValidate.isEmpty(lastName)) {
					errorMsg = errorMsg + (UtilValidate.isNotEmpty(errorMsg) ? " and " : "") +"Last name";
				} 
				if(UtilValidate.isNotEmpty(errorMsg)) {
					result.put(EventResponse.STATUS, EventResponse.ERROR);
					result.put(EventResponse.MESSAGE, errorMsg+" cannot be empty");
					return doJSONResponse(response, result);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put(EventResponse.STATUS, EventResponse.ERROR);
			result.put(EventResponse.MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		
		return doJSONResponse(response, result);
	}
	
	public static String createContactAction(HttpServletRequest request, HttpServletResponse response) {
	    Delegator delegator = (Delegator) request.getAttribute("delegator");
	    LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

	    Map < String, Object > result = new HashMap < String, Object > ();

	    String firstName = request.getParameter("firstName");
	    String lastName = request.getParameter("lastName");
	    String gender = request.getParameter("gender");
	    String designation = request.getParameter("designation");
	    String primaryPhoneNumber = request.getParameter("primaryPhoneNumber");
	    String primaryEmail = request.getParameter("primaryEmail");
	    String accountPartyId = request.getParameter("accountPartyId");
	    Map < String, Object > callResult = new HashMap < String, Object > ();
	    Map < String, Object > inpCxt = new HashMap < String, Object > ();
	    try {
	        inpCxt.put("firstName", firstName);
	        inpCxt.put("lastName", lastName);
	        inpCxt.put("gender", gender);
	        inpCxt.put("designation", designation);
	        inpCxt.put("primaryEmail", primaryEmail);
	        inpCxt.put("primaryPhoneNumber", primaryPhoneNumber);
	        inpCxt.put("accountPartyId", accountPartyId);
	        inpCxt.put("userLogin", userLogin);
	        callResult = dispatcher.runSync("crmsfa.createContact", inpCxt);
	        if (ServiceUtil.isSuccess(callResult)) {
	            result.put("response", "SUCCESS");
	            result.put("contactPartyId", callResult.get("contactPartyId"));
	        } else {
	            result.put("response", "ERROR");
	            result.put("errorMessage", callResult.get("errorMessage"));
	        }
	    } catch (Exception e) {
	        //e.printStackTrace();
	        Debug.logError(e.getMessage(), MODULE);
	        result.putAll(ServiceUtil.returnError(e.getMessage()));
	        result.put("response", "ERROR");
	        result.put("errorMessage", e.getMessage());
	    }
	    return doJSONResponse(response, result);
	}
	
	@SuppressWarnings("unchecked")
	public static String getEmailActivitiesOld(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String) context.get("partyId");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalKey = (String) context.get("externalLoginKey");
		Debug.log("externalLoginKey***********" + externalKey);
		Debug.log("domainEntityType***********" + domainEntityType);
		String searchType = request.getParameter("searchType");
		String mailStatus = request.getParameter("mailStatus");
		String subSearchType = request.getParameter("subSearchType");
		String displayType =  request.getParameter("displayType");
		String applyToAll =  UtilValidate.isNotEmpty(request.getParameter("applyToAll")) ? request.getParameter("applyToAll") : "";
		String filterBy =  request.getParameter("filterBy");
		
		Timestamp systemTime = UtilDateTime.nowTimestamp();
		
		List<Map<String, Object>> dataList = new LinkedList<Map<String,Object>>();

		try {
			
			String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

			// Integrate security matrix logic start
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String businessUnit = null;

			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			/*
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Read");
				accessMatrixMap.put("entityName", "CustRequest");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
			}
			*/
			// Integrate security matrix logic end
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				List<EntityCondition> conditionlist = FastList.newInstance();
				List<String> assignedWorkEffort = new ArrayList<String>();
				
				if(UtilValidate.isNotEmpty(filterBy)) {
					if("unassigned-emails".equals(filterBy)) {
						searchType = "QUEUE";
						displayType = "UN_ASSIGNED";
					} else if("assigned-emails".equals(filterBy)) {
						searchType = "QUEUE";
						applyToAll = "N";
						partyId = userLogin.getString("partyId");
					} else if("last-7-days".equals(filterBy)) {
						searchType = "QUEUE";
						applyToAll = "N";
						partyId = userLogin.getString("partyId");
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.WEEK_OF_MONTH, -1))),
								EntityCondition.makeCondition("entryDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.DAY_OF_WEEK, -1)))
								));
					}  else if("last-24-hours".equals(filterBy)) {
						searchType = "QUEUE";
						applyToAll = "N";
						partyId = userLogin.getString("partyId");
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.HOUR_OF_DAY, -24)),
								EntityCondition.makeCondition("entryDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.SECOND, -1))
								));
					}
				}
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				dynamicView.addMemberEntity("WE", "WorkEffort");
				dynamicView.addAlias("WE", "workEffortId","workEffortId", null, Boolean.FALSE, Boolean.TRUE, null);
				dynamicView.addAlias("WE", "workEffortName");
				dynamicView.addAlias("WE", "workEffortTypeId");
				dynamicView.addAlias("WE", "createdByUserLogin");
				dynamicView.addAlias("WE", "domainEntityType");
				dynamicView.addAlias("WE", "domainEntityId");
				dynamicView.addAlias("WE", "createdTxStamp");
				dynamicView.addAlias("WE", "lastUpdatedTxStamp");
				dynamicView.addMemberEntity("CEWE", "CommunicationEventWorkEff");
				dynamicView.addAlias("CEWE", "communicationEventId");
				dynamicView.addViewLink("WE", "CEWE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
				
				dynamicView.addMemberEntity("CE", "CommunicationEvent");
				dynamicView.addAlias("CE", "communicationEventTypeId");
				dynamicView.addAlias("CE", "subject");
				if(!"QUEUE".equals(searchType)) {
					dynamicView.addAlias("CE", "content");
				}
				dynamicView.addAlias("CE", "fromString");
				dynamicView.addAlias("CE", "toString");
				dynamicView.addAlias("CE", "ccString");
				dynamicView.addAlias("CE", "messageId");
				dynamicView.addAlias("CE", "entryDate");
				dynamicView.addViewLink("CEWE", "CE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("communicationEventId"));
				
				List<String> partyIdList = new ArrayList<String>(); 
				if(UtilValidate.isNotEmpty(partyId)) {
					partyIdList.add(partyId);
					String requestURI = (String) context.get("requestURI");
					if(UtilValidate.isNotEmpty(requestURI) && requestURI.contains("/viewAccount")) {
						List<String> contactIds = DataUtil.getContactPartyList(delegator, UtilMisc.toMap("partyIdTo", partyId,"roleTypeIdFrom","CONTACT","roleTypeIdTo","ACCOUNT", "partyRelationshipTypeId","CONTACT_REL_INV"));
						if(UtilValidate.isNotEmpty(contactIds))
							partyIdList.addAll(contactIds);
					}
					
					if("SERVICE_REQUEST".equals(searchType)) {
						dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
						dynamicView.addAlias("CRWE", "custRequestId");
						dynamicView.addViewLink("WE", "CRWE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						dynamicView.addMemberEntity("CRP", "CustRequestParty");
						dynamicView.addAlias("CRP", "crpPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("CRP", "roleTypeId");
						dynamicView.addAlias("CRP", "crpFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("CRP", "crpThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("CRWE", "CRP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("custRequestId"));
						
						/*
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("crpPartyId", EntityOperator.EQUALS, partyId),
								EntityUtil.getFilterByDateExpr("crpFromDate", "crpThruDate")
								)); */
						
						dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
						dynamicView.addAlias("WEPA", "wepaPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "roleTypeId", "roleTypeId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						ModelViewLink link1 = new ModelViewLink("WE", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("workEffortId"));
						ModelViewLink link2 = new ModelViewLink("CRP", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("partyId"));
						List<ModelViewLink> modelLinkList = new ArrayList<ModelViewLink>();
						modelLinkList.add(link1);
						modelLinkList.add(link2);
						dynamicView.addAllViewLinksToList(modelLinkList);
						

						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("wepaPartyId", EntityOperator.IN, partyIdList),
								EntityUtil.getFilterByDateExpr("wepFromDate", "wepThruDate")
								));
						
					} else if("OPPORTUNITY".equals(searchType)) {
						dynamicView.addMemberEntity("SOWE", "SalesOpportunityWorkEffort");
						dynamicView.addAlias("SOWE", "salesOpportunityId");
						dynamicView.addViewLink("WE", "SOWE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						dynamicView.addMemberEntity("SO", "SalesOpportunity");
						dynamicView.addAlias("SO", "opportunityName");
						dynamicView.addAlias("SO", "soPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("SOWE", "SO", Boolean.FALSE, ModelKeyMap.makeKeyMapList("salesOpportunityId"));
						
						dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
						dynamicView.addAlias("WEPA", "wepaPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "roleTypeId", "roleTypeId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						ModelViewLink link1 = new ModelViewLink("WE", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("workEffortId"));
						ModelViewLink link2 = new ModelViewLink("SO", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("partyId"));
						List<ModelViewLink> modelLinkList = new ArrayList<ModelViewLink>();
						modelLinkList.add(link1);
						modelLinkList.add(link2);
						dynamicView.addAllViewLinksToList(modelLinkList);
						
						//conditionlist.add(EntityCondition.makeCondition("soPartyId", EntityOperator.EQUALS, partyId));
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("wepaPartyId", EntityOperator.IN, partyIdList),
								EntityUtil.getFilterByDateExpr("wepFromDate", "wepThruDate")
								));
						
					} else if("ORDER".equals(searchType)) {
						dynamicView.addMemberEntity("OWE", "OrderWorkEffort");
						dynamicView.addAlias("OWE", "orderId");
						dynamicView.addViewLink("WE", "OWE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						dynamicView.addMemberEntity("RTM", "RmsTransactionMaster");
						dynamicView.addAlias("RTM", "billToPartyId", "billToPartyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("OWE", "RTM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("orderId"));
						
						dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
						dynamicView.addAlias("WEPA", "wepaPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "roleTypeId", "roleTypeId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						ModelViewLink link1 = new ModelViewLink("WE", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("workEffortId"));
						ModelViewLink link2 = new ModelViewLink("RTM", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("billToPartyId","partyId"));
						List<ModelViewLink> modelLinkList = new ArrayList<ModelViewLink>();
						modelLinkList.add(link1);
						modelLinkList.add(link2);
						dynamicView.addAllViewLinksToList(modelLinkList);
						
						//conditionlist.add(EntityCondition.makeCondition("soPartyId", EntityOperator.EQUALS, partyId));
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("wepaPartyId", EntityOperator.IN, partyIdList),
								EntityUtil.getFilterByDateExpr("wepFromDate", "wepThruDate")
								));
						
						
					} else if("ALL".equals(searchType)) {
						dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
						dynamicView.addAlias("WEPA", "wepaPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "roleTypeId", "roleTypeId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("wepaPartyId", EntityOperator.IN, partyIdList),
								EntityUtil.getFilterByDateExpr("wepFromDate", "wepThruDate")
								));
						
					} 
				} 
				if("QUEUE".equals(searchType)) {
					
					if("SERVICE_REQUEST".equals(subSearchType)) {
						dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
						dynamicView.addAlias("CRWE", "custRequestId");
						dynamicView.addViewLink("WE", "CRWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					} else if("OPPORTUNITY".equals(subSearchType)) {
						dynamicView.addMemberEntity("SOWE", "SalesOpportunityWorkEffort");
						dynamicView.addAlias("SOWE", "salesOpportunityId");
						dynamicView.addViewLink("WE", "SOWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					} else if("ORDER".equals(subSearchType)) {
						dynamicView.addMemberEntity("OWE", "OrderWorkEffort");
						dynamicView.addAlias("OWE", "orderId");
						dynamicView.addViewLink("WE", "OWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					} else {
						dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
						dynamicView.addAlias("CRWE", "custRequestId");
						dynamicView.addViewLink("WE", "CRWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						dynamicView.addMemberEntity("SOWE", "SalesOpportunityWorkEffort");
						dynamicView.addAlias("SOWE", "salesOpportunityId");
						dynamicView.addViewLink("WE", "SOWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						dynamicView.addMemberEntity("OWE", "OrderWorkEffort");
						dynamicView.addAlias("OWE", "orderId");
						dynamicView.addViewLink("WE", "OWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					}
					
					if(UtilValidate.isNotEmpty(partyId)) {
						
						dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
						dynamicView.addAlias("WEPA", "wepaPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "roleTypeId", "roleTypeId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("wepaPartyId", EntityOperator.EQUALS, partyId),
								EntityUtil.getFilterByDateExpr("wepFromDate", "wepThruDate")
								));
					
						//workEffPtyCond.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					} else if(!("N".equals(applyToAll))) {
						List<EntityCondition> workEffPtyCond = new ArrayList<EntityCondition>();
						workEffPtyCond.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"));
						workEffPtyCond.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "ACCOUNT"));
						List<GenericValue> workEffortPartyAssigns = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(EntityCondition.makeCondition(workEffPtyCond, EntityOperator.AND)).filterByDate("fromDate","thruDate").queryList();
						if(UtilValidate.isNotEmpty(workEffortPartyAssigns)) {
							assignedWorkEffort = EntityUtil.getFieldListFromEntityList(workEffortPartyAssigns, "workEffortId", true);
						}
						
						if("UN_ASSIGNED".equals(displayType)) {
							conditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.NOT_IN, assignedWorkEffort));
						} else if("ASSIGNED".equals(displayType)) {
							conditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, assignedWorkEffort));
						}
					}
				}
				if(UtilValidate.isNotEmpty(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId)) {
					conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType),
							EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId)
							));
				}
				/*
				if(UtilValidate.isNotEmpty(mailStatus)) {
					conditionlist.add(EntityCondition.makeCondition("mailStatus", EntityOperator.EQUALS, mailStatus));
				} */
				
				conditionlist.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS,"EMAIL"));
				
				EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				Debug.log("=== Condition : ====="+ condition, MODULE);
				List<GenericValue> emails = EntityQuery.use(delegator).from(dynamicView).where(condition).orderBy("lastUpdatedTxStamp DESC").queryList();


				if(UtilValidate.isNotEmpty(emails)) {
					int count = 0;
					for(GenericValue email : emails) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(email));
						String communicationEventId = email.getString("communicationEventId");
						String workEffortId =  email.getString("workEffortId");
						/*
						if(!"QUEUE".equals(searchType)) {
							
							String communicationEventTypeId = email.getString("communicationEventTypeId");

							List<GenericValue> commEventContentAssocList = EntityQuery.use(delegator).from("CommEventContentAssoc").where(EntityCondition.makeCondition("communicationEventId", EntityOperator.EQUALS, email.getString("communicationEventId"))).queryList();

							boolean isAttach = false;
							Debug.log("Get Attachment data start.");
							if(UtilValidate.isNotEmpty(commEventContentAssocList)){
								data.put("isAttachment","Y");
								isAttach =  true;
								Map<String, Object> requestContext = new HashMap<String, Object>();
								requestContext.put("workEffortId", workEffortId);
								requestContext.put("communicationEventId", communicationEventId);

								Map<String, Object> contentDetail = dispatcher.runSync("common.getFileContentData", UtilMisc.toMap("requestContext", requestContext, "userLogin", userLogin));
								if(ServiceUtil.isSuccess(contentDetail) && UtilValidate.isNotEmpty(contentDetail.get("resultMap"))) {
									Map<String, Object> resultMap = (Map<String, Object>) contentDetail.get("resultMap");
									List<GenericValue> fileContents = (List<GenericValue>) resultMap.get("fileContents");
									if(UtilValidate.isNotEmpty(fileContents)) {
										data.put("fileContents", fileContents);
									}
								}

							}else{
								data.put("isAttachment","N");
							}

							if(UtilValidate.isNotEmpty(communicationEventTypeId) && "GRAPH_EMAIL".equals(communicationEventTypeId)) {
								data.remove("content");
								Map<String, Object> mailContent = org.fio.admin.portal.util.DataUtil.convertToMap(email.getString("content"));
								//data.putAll(mailContent);
								data.put("isRead", mailContent.get("isRead"));
								data.put("isDraft", mailContent.get("isDraft"));
								data.put("bodyPreview", mailContent.get("bodyPreview"));
								//data.put("isAttachment", mailContent.get("isAttachment"));
								data.put("from", mailContent.get("from"));
								data.put("hasAttachments", mailContent.get("hasAttachments"));
								Map<String, Object> fromEmailMap = new HashMap<String, Object>();
								if(UtilValidate.isNotEmpty(mailContent) && UtilValidate.isNotEmpty(mailContent.get("from"))) {
									fromEmailMap = (Map<String, Object>) mailContent.get("from");
								}
								if(UtilValidate.isNotEmpty(fromEmailMap) && UtilValidate.isNotEmpty(fromEmailMap.get("emailAddress"))) {
									Map<String, Object> emailAddress = (Map<String, Object>) fromEmailMap.get("emailAddress");
									data.put("fromPartyName", UtilValidate.isNotEmpty(emailAddress) ? emailAddress.get("name"):"");
								}

								Map<String, Object> msgBody = (Map<String, Object>) mailContent.get("body");
								String emailCont = UtilValidate.isNotEmpty(msgBody) && UtilValidate.isNotEmpty(msgBody.get("content")) ? (String) msgBody.get("content") :"";
								if(isAttach) {
									if(emailCont.contains("<img "))
										emailCont = emailCont.replaceAll("<p class=\"MsoNormal\">&nbsp;</p>"," ");

									emailCont = emailCont.replaceAll("<img .*?>","&nbsp;");
								}
								data.put("message", emailCont);
							}
							else {
								String createdByUserLogin = (String) email.get("createdByUserLogin");
								GenericValue UserLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId",createdByUserLogin).queryFirst();
								if(UtilValidate.isNotEmpty(UserLoginPerson) && UtilValidate.isNotEmpty(UserLoginPerson.getString("partyId"))){
									data.put("fromPartyName",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, UserLoginPerson.getString("partyId"), false));
								}
								String emailCont = UtilValidate.isNotEmpty(email.getString("content")) ? email.getString("content") :"";
								if(isAttach) {
									if(emailCont.contains("<img "))
										emailCont = emailCont.replaceAll("<p class=\"MsoNormal\">&nbsp;</p>"," ");

									emailCont = emailCont.replaceAll("<img .*?>","&nbsp;");
								}
								data.put("message", emailCont);
							}
						}
						*/
						Timestamp entryDateTime = email.getTimestamp("entryDate");
						Timestamp last_5_minutes = UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.MINUTE, -5);
						if(UtilValidate.isNotEmpty(entryDateTime) && entryDateTime.after(last_5_minutes))
							data.put("isNewEmail", "Y");
						else
							data.put("isNewEmail", "N");
						
						data.put("entryDate", UtilValidate.isNotEmpty(email.get("entryDate")) ? UtilDateTime.timeStampToString(email.getTimestamp("entryDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : UtilValidate.isNotEmpty(email.get("createdTxStamp")) ? UtilDateTime.timeStampToString(email.getTimestamp("createdTxStamp"), globalDateTimeFormat, TimeZone.getDefault(), null) : "" );
						if("QUEUE".equals(searchType) && UtilValidate.isEmpty(partyId)) {
							if(assignedWorkEffort.contains(workEffortId)) {
								data.put("assignStatusId", "ASSIGNED");
							} else {
								data.put("assignStatusId", "UN_ASSIGNED");
							}
						} else {
							data.put("assignStatusId", "ASSIGNED");
						}
						
						if("unassigned-emails".equals(filterBy)) {
							data.put("assignBtnLabel", "Assign User");
						} else if("assigned-emails".equals(filterBy)) {
							data.put("assignBtnLabel", "Re-Assign User");
						} else if("last-7-days".equals(filterBy)) {
							data.put("assignBtnLabel", "Re-Assign User");
						} else if("last-24-hours".equals(filterBy)) {
							data.put("assignBtnLabel", "Re-Assign User");
						}
						data.put("assignBtn", count);
						data.put("domainAssignBtn", count);
						data.put("viewActBtn", count);
						data.put("readBtn", count);
						data.put("externalLoginKey", externalKey);
						data.put("domainEntityTypeDes", UtilValidate.isNotEmpty(email.getString("domainEntityType")) ? org.fio.admin.portal.util.DataHelper.getEnumDescription(delegator, email.getString("domainEntityType"), "ENTITY_DOMAIN") : "");
						
						dataList.add(data);
					}
				}
			} else {
				Debug.log("error==");
				String errorMessage = "";
				if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errorMessage = accessMatrixRes.get("errorMessage").toString();
				} else {
					errorMessage = "Access Denied";
				}
				result.put("list", new ArrayList<Map<String, Object>>());
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, errorMessage);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put("list",dataList);
		return doJSONResponse(response, result);
	}
	
	public static String workEffortPartAssignment(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String) context.get("partyId");
		String workEffortId = (String) context.get("workEffortId");
		String existPartyId = (String) context.get("existPartyId");
		String responseMsg = "User has been assigned successfully";
		try {
			
			if(UtilValidate.isNotEmpty(existPartyId)) {
				EntityCondition existWepaCon = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, existPartyId),
						EntityUtil.getFilterByDateExpr()
		                );
				List<GenericValue> existWepaList = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(existWepaCon).queryList();
				if(UtilValidate.isNotEmpty(existWepaList)) {
					for(GenericValue existWepa : existWepaList) {
						existWepa.set("thruDate", UtilDateTime.nowTimestamp());
						existWepa.store();
					}
				}
				responseMsg = "User has been re-assigned successfully";
			}
			
			if(UtilValidate.isNotEmpty(partyId)) {
				GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryFirst();
				if(UtilValidate.isNotEmpty(party) ) {
					String roleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, partyId);
					List<EntityCondition> conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
							EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
							EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
							EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList(roleTypeId)),
							EntityUtil.getFilterByDateExpr()
			                ));
					
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					GenericValue partyAssignment = EntityUtil.getFirst( delegator.findList("WorkEffortPartyAssignment", mainConditons, UtilMisc.toSet("partyId"), null, null, false) );
					if (UtilValidate.isEmpty(partyAssignment)) {
						Map<String, Object> callCtxt = UtilMisc.toMap("partyId", partyId, "workEffortId", workEffortId, "roleTypeId", roleTypeId, "statusId", "PRTYASGN_ASSIGNED", "userLogin", userLogin);
						callCtxt.put("assignedByUserLoginId", userLogin.getString("userLoginId"));
						Map<String, Object> callResult = dispatcher.runSync("assignPartyToWorkEffort", callCtxt);
			            if (ServiceUtil.isError(callResult)) {
			            	Debug.logError(ServiceUtil.getErrorMessage(callResult), MODULE);
			            	request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(callResult));
			            	return "error";
			            }
					}
				}
			}
			
			GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryFirst();
			if(UtilValidate.isNotEmpty(workEffort)) {
				String sub = workEffort.getString("workEffortName");
				
				Map<String,Object> getSrIdFromMailSubjectRsp = dispatcher.runSync("getPatternFromMailSubject", UtilMisc.toMap("subject", sub));

				if(ServiceUtil.isError(getSrIdFromMailSubjectRsp)) {
					Debug.logError("Service error : persistGraphMails : getSrIdFromMailSubject " +ServiceUtil.getErrorMessage(getSrIdFromMailSubjectRsp), MODULE);
				}

				String srId = (String) getSrIdFromMailSubjectRsp.get("srId");
				if(UtilValidate.isNotEmpty(srId)) {
					if(srId.contains("SR")) {
						workEffort.set("domainEntityType", "SERVICE_REQUEST");
						workEffort.set("domainEntityId", srId);
						workEffort.store();
					} else if(srId.contains("OP")) {
						workEffort.set("domainEntityType", "OPPORTUNITY");
						workEffort.set("domainEntityId", srId);
						workEffort.store();
					} else if(srId.contains("ORD")) {
						workEffort.set("domainEntityType", "ORDER");
						workEffort.set("domainEntityId", srId);
						workEffort.store();
					}
				} else {
					Debug.logInfo("Subject not match any pattern", MODULE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		request.setAttribute("_EVENT_MESSAGE_",responseMsg);
		return "success";
	}
	
	public static String searchEntityDomainList(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String requestUri = request.getParameter("requestUri");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		
		String searchText = request.getParameter("searchText");
		

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {

			List conditionList = FastList.newInstance();
			
			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			
			if(CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
				dynamicViewEntity = new DynamicViewEntity();
				dynamicViewEntity.addMemberEntity("CR", "CustRequest");
				dynamicViewEntity.addAlias("CR", "domainEntityId", "custRequestId", null, Boolean.FALSE, Boolean.FALSE, null);
				dynamicViewEntity.addAlias("CR", "description", "custRequestName", null, Boolean.FALSE, Boolean.FALSE, null);
				
			} else if("OPPORTUNITY".equals(domainEntityType)) {
				dynamicViewEntity = new DynamicViewEntity();
				dynamicViewEntity.addMemberEntity("SO", "SalesOpportunity");
				dynamicViewEntity.addAlias("SO", "domainEntityId", "salesOpportunityId", null, Boolean.FALSE, Boolean.FALSE, null);
				dynamicViewEntity.addAlias("SO", "description", "opportunityName", null, Boolean.FALSE, Boolean.FALSE, null);
			} else if("ORDER".equals(domainEntityType)) {
				dynamicViewEntity = new DynamicViewEntity();
				dynamicViewEntity.addMemberEntity("RTM", "RmsTransactionMaster");
				dynamicViewEntity.addAlias("RTM", "domainEntityId", "orderId", null, Boolean.FALSE, Boolean.TRUE, null);
				dynamicViewEntity.addAlias("RTM", "description", "skuDescription", null, Boolean.FALSE, Boolean.FALSE, null);
			}
			

			if (UtilValidate.isNotEmpty(searchText)) {
				EntityCondition searchCondition = EntityCondition
						.makeCondition(
								UtilMisc.toList(
										EntityCondition.makeCondition("domainEntityId", EntityOperator.LIKE, "" + searchText + "%"),
										EntityCondition.makeCondition("description", EntityOperator.LIKE, "" + searchText + "%")
										),
								EntityOperator.OR);
				conditionList.add(searchCondition);
			}

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			Debug.log("searchEntityDomain mainConditons: "+mainConditons);
			
			List<GenericValue> entityDomainList = EntityQuery.use(delegator).from(dynamicViewEntity).where(mainConditons).queryList();
			if (UtilValidate.isNotEmpty(entityDomainList)) {
				for (GenericValue entityDomain : entityDomainList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(entityDomain));
					dataList.add(data);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, dataList);

	}
	
	public static String domainAssignment(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String domainEntityType = (String) context.get("domainEntityType");
		String workEffId = (String) context.get("workEffortId");
		String domainEntityId = (String) context.get("domainEntityId");
		String responseMsg = "Domain Entity Type has been assigned successfully";
		try {
			
			if(UtilValidate.isNotEmpty(workEffId) && UtilValidate.isNotEmpty(domainEntityId) && UtilValidate.isNotEmpty(domainEntityType)) {
				List<String> workEffortIdList = new ArrayList<String>();
				if(workEffId.contains(","))
					workEffortIdList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(workEffId, ","));
				else
					workEffortIdList.add(workEffId);
				
				for(String workEffortId : workEffortIdList) {
					GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryFirst();
					List<String> contentIds = new ArrayList<String>();
					if(UtilValidate.isNotEmpty(workEffort)) {
						GenericValue commEventWorkEff = EntityQuery.use(delegator).from("CommunicationEventWorkEff").where("workEffortId", workEffortId).queryFirst();
						if (UtilValidate.isNotEmpty(commEventWorkEff)) {
							String communicationEventId = commEventWorkEff.getString("communicationEventId");
							List<GenericValue> fileContents = EntityQuery.use(delegator).from("CommEventContentAssoc").where("communicationEventId", communicationEventId).filterByDate().queryList();
							contentIds = EntityUtil.getFieldListFromEntityList(fileContents, "contentId", true);
							
							if(UtilValidate.isNotEmpty(contentIds)) {
								for(String contentId : contentIds) {
									GenericValue workEffortContent = delegator.makeValue("WorkEffortContent");
									workEffortContent.set("workEffortId", workEffortId);
									workEffortContent.set("contentId", contentId);
									workEffortContent.set("workEffortContentTypeId", "EMAIL_ATTACHMENT_DATA");
									workEffortContent.set("fromDate", UtilDateTime.nowTimestamp());
									workEffortContent.create();
								}
							}
						}
						/*
						workEffort.set("domainEntityType", domainEntityType);
						workEffort.set("domainEntityId", domainEntityId);
						workEffort.store();
						*/
						if(CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
							GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", domainEntityId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(workEffort)) {
								GenericValue custRequestWorkEffort = EntityQuery.use(delegator).from("CustRequestWorkEffort").where("custRequestId", domainEntityId, "workEffortId", workEffortId).queryFirst();
								if(UtilValidate.isEmpty(custRequestWorkEffort)) {
									custRequestWorkEffort = delegator.makeValue("CustRequestWorkEffort");
									custRequestWorkEffort.put("custRequestId", domainEntityId);
									custRequestWorkEffort.put("workEffortId", workEffortId);
									custRequestWorkEffort.create();
								}
								
								//Add domain type and id
								workEffort.set("domainEntityType", domainEntityType);
								workEffort.set("domainEntityId", domainEntityId);
								workEffort.store();
							}
						} else if("OPPORTUNITY".equals(domainEntityType)) {
							GenericValue salesOpportunity = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId", domainEntityId).queryFirst();
							if(UtilValidate.isNotEmpty(salesOpportunity) && UtilValidate.isNotEmpty(workEffort)) {
								
								String oppoPartyId = salesOpportunity.getString("partyId");
								
								GenericValue opportunityWorkEffort = EntityQuery.use(delegator).from("SalesOpportunityWorkEffort").where("salesOpportunityId", domainEntityId, "workEffortId", workEffortId).queryFirst();
								if(UtilValidate.isEmpty(opportunityWorkEffort)) {
									opportunityWorkEffort = delegator.makeValue("SalesOpportunityWorkEffort");
									opportunityWorkEffort.put("salesOpportunityId", domainEntityId);
									opportunityWorkEffort.put("workEffortId", workEffortId);
									opportunityWorkEffort.create();	
								}
								if(UtilValidate.isNotEmpty(oppoPartyId)) {
									GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", oppoPartyId).queryFirst();
									if(UtilValidate.isNotEmpty(party) ) {
										String roleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, oppoPartyId);
										List<EntityCondition> conditionList = FastList.newInstance();
										conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
												EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
												EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
												EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, oppoPartyId),
												EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList(roleTypeId)),
												EntityUtil.getFilterByDateExpr()
								                ));
										
										EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
										GenericValue partyAssignment = EntityUtil.getFirst( delegator.findList("WorkEffortPartyAssignment", mainConditons, UtilMisc.toSet("partyId"), null, null, false) );
										if (UtilValidate.isEmpty(partyAssignment)) {
											Map<String, Object> callCtxt = UtilMisc.toMap("partyId", oppoPartyId, "workEffortId", workEffortId, "roleTypeId", roleTypeId, "statusId", "PRTYASGN_ASSIGNED", "userLogin", userLogin);
											callCtxt.put("assignedByUserLoginId", userLogin.getString("userLoginId"));
											Map<String, Object> callResult = dispatcher.runSync("assignPartyToWorkEffort", callCtxt);
								            if (ServiceUtil.isError(callResult)) {
								            	Debug.logError(ServiceUtil.getErrorMessage(callResult), MODULE);
								            }
										}
									}
								}
								
								//Add domain type and id
								workEffort.set("domainEntityType", domainEntityType);
								workEffort.set("domainEntityId", domainEntityId);
								workEffort.store();
							}
						} else if("ORDER".equals(domainEntityType)) {
							GenericValue rmsTransactionMaster = EntityQuery.use(delegator).from("RmsTransactionMaster").where("orderId", domainEntityId).queryFirst();
							if(UtilValidate.isNotEmpty(rmsTransactionMaster) && UtilValidate.isNotEmpty(workEffort)) {
								GenericValue orderWorkEffort = EntityQuery.use(delegator).from("OrderWorkEffort").where("orderId", domainEntityId, "workEffortId", workEffortId).queryFirst();
								if(UtilValidate.isEmpty(orderWorkEffort)) {
									orderWorkEffort = delegator.makeValue("OrderWorkEffort");
									orderWorkEffort.put("orderId", domainEntityId);
									orderWorkEffort.put("workEffortId", workEffortId);
									orderWorkEffort.create();
								}
								
								//Add domain type and id
								workEffort.set("domainEntityType", domainEntityType);
								workEffort.set("domainEntityId", domainEntityId);
								workEffort.store();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		request.setAttribute("_EVENT_MESSAGE_",responseMsg);
		return "success";
	}
	
	public static String refreshEmailDownload(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		try {
			result = dispatcher.runSync("downloadExchangeMailAtLogin", UtilMisc.toMap("userLogin", userLogin));
			if(ServiceUtil.isError(result)) {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, ServiceUtil.getErrorMessage(result));
				return doJSONResponse(response, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Email refreshed successfully.");
		return doJSONResponse(response, result);
	}
	
	public static String getEmailDashboardDataCountList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<GenericValue> resultList = null;
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		String filterBy = (String) context.get("filterBy");
		String filterType = (String) context.get("filterType");

		String partyId = (String) context.get("partyId");
		
		List<Map<String, Object>> dataList = new ArrayList<>();
		long start = System.currentTimeMillis();
		Timestamp systemTime = UtilDateTime.nowTimestamp();
		ResultSet rs = null;

		try {
			String userLoginId = userLogin.getString("userLoginId");
			String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if(UtilValidate.isEmpty(partyId)) {
				partyId = userLogin.getString("partyId");
			}
			
			SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
			Connection con = (Connection)sqlProcessor.getConnection();
			String barType = "";
			String count = "";
			
			String last7daysSql = "SELECT COUNT(DISTINCT WE.WORK_EFFORT_ID), 'last7days' FROM WORK_EFFORT WE \r\n" + 
					"INNER JOIN COMMUNICATION_EVENT_WORK_EFF CEWE ON WE.WORK_EFFORT_ID = CEWE.WORK_EFFORT_ID \r\n" + 
					"INNER JOIN COMMUNICATION_EVENT CE ON CEWE.COMMUNICATION_EVENT_ID = CE.COMMUNICATION_EVENT_ID \r\n" + 
					"LEFT OUTER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON WE.WORK_EFFORT_ID = CRWE.WORK_EFFORT_ID \r\n" + 
					"LEFT OUTER JOIN SALES_OPPORTUNITY_WORK_EFFORT SOWE ON WE.WORK_EFFORT_ID = SOWE.WORK_EFFORT_ID \r\n" + 
					"LEFT OUTER JOIN ORDER_WORK_EFFORT OWE ON WE.WORK_EFFORT_ID = OWE.WORK_EFFORT_ID \r\n" + 
					"INNER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID \r\n" + 
					"WHERE CE.ENTRY_DATE >= '"+UtilDateTime.getDayStart(UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.WEEK_OF_MONTH, -1))+"' AND CE.ENTRY_DATE <= '"+UtilDateTime.getDayEnd(UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.DAY_OF_WEEK, -1))+"'\r\n" + 
					"AND WEPA.PARTY_ID = '"+partyId+"'\r\n" + 
					"AND ((WEPA.THRU_DATE IS NULL OR WEPA.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') \r\n" + 
					"AND (WEPA.FROM_DATE IS NULL OR WEPA.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))\r\n" + 
					"AND WE.WORK_EFFORT_TYPE_ID = 'EMAIL'";
			
			rs = sqlProcessor.executeQuery(last7daysSql);
			if(rs !=null){
				try{ 
					int i = 0;
					while (rs.next()) {
						Map<String, Object> data = new HashMap<String, Object>();
						count = rs.getString(1);
						barType = rs.getString(2);
						data.put("barId", barType);
						data.put("count", count);
						dataList.add(data);
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(rs !=null)
						rs.close();
				}
			}
			String last24hoursSql = "SELECT COUNT(DISTINCT WE.WORK_EFFORT_ID), 'last24hours' FROM WORK_EFFORT WE \r\n" + 
					"INNER JOIN COMMUNICATION_EVENT_WORK_EFF CEWE ON WE.WORK_EFFORT_ID = CEWE.WORK_EFFORT_ID \r\n" + 
					"INNER JOIN COMMUNICATION_EVENT CE ON CEWE.COMMUNICATION_EVENT_ID = CE.COMMUNICATION_EVENT_ID \r\n" + 
					"LEFT OUTER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON WE.WORK_EFFORT_ID = CRWE.WORK_EFFORT_ID \r\n" + 
					"LEFT OUTER JOIN SALES_OPPORTUNITY_WORK_EFFORT SOWE ON WE.WORK_EFFORT_ID = SOWE.WORK_EFFORT_ID \r\n" + 
					"LEFT OUTER JOIN ORDER_WORK_EFFORT OWE ON WE.WORK_EFFORT_ID = OWE.WORK_EFFORT_ID \r\n" + 
					"INNER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID \r\n" + 
					"WHERE CE.ENTRY_DATE >= '"+UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.HOUR_OF_DAY, -24)+"' AND CE.ENTRY_DATE <= '"+UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.SECOND, -1)+"'\r\n" + 
					"AND WEPA.PARTY_ID = '"+partyId+"'\r\n" + 
					"AND ((WEPA.THRU_DATE IS NULL OR WEPA.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') \r\n" + 
					"AND (WEPA.FROM_DATE IS NULL OR WEPA.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))\r\n" + 
					"AND WE.WORK_EFFORT_TYPE_ID = 'EMAIL'";
			
			rs = sqlProcessor.executeQuery(last24hoursSql);
			if(rs !=null){
				try{ 
					int i = 0;
					while (rs.next()) {
						Map<String, Object> data = new HashMap<String, Object>();
						count = rs.getString(1);
						barType = rs.getString(2);
						data.put("barId", barType);
						data.put("count", count);
						dataList.add(data);
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(rs !=null)
						rs.close();
				}
			}
			
			List<String> assignedWorkEffort = new ArrayList<String>();
			assignedWorkEffort.add("12ABCDEF");
			List<EntityCondition> workEffPtyCond = new ArrayList<EntityCondition>();
			workEffPtyCond.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"));
			workEffPtyCond.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "ACCOUNT"));
			List<GenericValue> workEffortPartyAssigns = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(EntityCondition.makeCondition(workEffPtyCond, EntityOperator.AND)).filterByDate("fromDate","thruDate").cache(true).queryList();
			if(UtilValidate.isNotEmpty(workEffortPartyAssigns)) {
				assignedWorkEffort = EntityUtil.getFieldListFromEntityList(workEffortPartyAssigns, "workEffortId", true);
			}
			
			String unassignedSql = "SELECT COUNT(DISTINCT WE.WORK_EFFORT_ID), 'unassigned-emails' FROM WORK_EFFORT WE \r\n" + 
					"INNER JOIN COMMUNICATION_EVENT_WORK_EFF CEWE ON WE.WORK_EFFORT_ID = CEWE.WORK_EFFORT_ID \r\n" + 
					"INNER JOIN COMMUNICATION_EVENT CE ON CEWE.COMMUNICATION_EVENT_ID = CE.COMMUNICATION_EVENT_ID \r\n" + 
					"LEFT OUTER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON WE.WORK_EFFORT_ID = CRWE.WORK_EFFORT_ID \r\n" + 
					"LEFT OUTER JOIN SALES_OPPORTUNITY_WORK_EFFORT SOWE ON WE.WORK_EFFORT_ID = SOWE.WORK_EFFORT_ID \r\n" + 
					"LEFT OUTER JOIN ORDER_WORK_EFFORT OWE ON WE.WORK_EFFORT_ID = OWE.WORK_EFFORT_ID\r\n" + 
					"WHERE WE.WORK_EFFORT_ID NOT IN ("+org.fio.admin.portal.util.DataUtil.toList(assignedWorkEffort, "")+") AND WE.WORK_EFFORT_TYPE_ID = 'EMAIL'";
			
			rs = sqlProcessor.executeQuery(unassignedSql);
			if(rs !=null){
				try{ 
					int i = 0;
					while (rs.next()) {
						Map<String, Object> data = new HashMap<String, Object>();
						count = rs.getString(1);
						barType = rs.getString(2);
						data.put("barId", barType);
						data.put("count", count);
						dataList.add(data);
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(rs !=null)
						rs.close();
				}
			}
			
			String assignedSql = "SELECT COUNT(DISTINCT WE.WORK_EFFORT_ID), 'assigned-emails' FROM WORK_EFFORT WE \r\n" + 
					"INNER JOIN COMMUNICATION_EVENT_WORK_EFF CEWE ON WE.WORK_EFFORT_ID = CEWE.WORK_EFFORT_ID \r\n" + 
					"INNER JOIN COMMUNICATION_EVENT CE ON CEWE.COMMUNICATION_EVENT_ID = CE.COMMUNICATION_EVENT_ID \r\n" + 
					"LEFT OUTER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON WE.WORK_EFFORT_ID = CRWE.WORK_EFFORT_ID \r\n" + 
					"LEFT OUTER JOIN SALES_OPPORTUNITY_WORK_EFFORT SOWE ON WE.WORK_EFFORT_ID = SOWE.WORK_EFFORT_ID \r\n" + 
					"LEFT OUTER JOIN ORDER_WORK_EFFORT OWE ON WE.WORK_EFFORT_ID = OWE.WORK_EFFORT_ID \r\n" + 
					"INNER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID \r\n" + 
					"WHERE WEPA.PARTY_ID = '"+partyId+"' \r\n" + 
					"AND ((WEPA.THRU_DATE IS NULL OR WEPA.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') \r\n" + 
					"AND (WEPA.FROM_DATE IS NULL OR WEPA.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))\r\n" + 
					"AND WE.WORK_EFFORT_TYPE_ID = 'EMAIL'";
			
			rs = sqlProcessor.executeQuery(assignedSql);
			if(rs !=null){
				try{ 
					int i = 0;
					while (rs.next()) {
						Map<String, Object> data = new HashMap<String, Object>();
						count = rs.getString(1);
						barType = rs.getString(2);
						data.put("barId", barType);
						data.put("count", count);
						dataList.add(data);
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(rs !=null)
						rs.close();
				}
			}
			
			if(!con.isClosed())
				con.close();
			if(sqlProcessor != null)
				sqlProcessor.close();
			
			//get unassigned sms
			
				
			String fromData = (String) context.get("fromData");
			String toData = (String) context.get("toData");
			String direction = (String) context.get("direction");
			String workEffortId = (String) context.get("workEffortId");
			
			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("CEWE", "CommunicationEventWorkEff");
			dynamicViewEntity.addAlias("CEWE", "communicationEventId");
			dynamicViewEntity.addMemberEntity("CE", "CommunicationEvent");
			dynamicViewEntity.addAlias("CE", "communicationEventTypeId");
			dynamicViewEntity.addAlias("CE", "fromData");
			dynamicViewEntity.addAlias("CE", "toData");
			dynamicViewEntity.addAlias("CE", "content");
			dynamicViewEntity.addAlias("CE", "subject");
			dynamicViewEntity.addAlias("CE", "externalMsgId");
			dynamicViewEntity.addAlias("CE", "msgSendStatus");
			dynamicViewEntity.addAlias("CE", "msgSentTime");
			dynamicViewEntity.addAlias("CE", "direction");
			dynamicViewEntity.addAlias("CE", "lastUpdatedTxStamp");
			dynamicViewEntity.addAlias("CE", "createdTxStamp");
			dynamicViewEntity.addViewLink("CEWE", "CE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("communicationEventId"));
			dynamicViewEntity.addMemberEntity("WE", "WorkEffort");
			dynamicViewEntity.addAlias("WE", "workEffortId");
			dynamicViewEntity.addAlias("WE", "workEffortName");
			dynamicViewEntity.addAlias("WE", "domainEntityType");
			dynamicViewEntity.addAlias("WE", "domainEntityId");
			dynamicViewEntity.addViewLink("CEWE", "WE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
			
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("communicationEventId");fieldsToSelect.add("fromData");fieldsToSelect.add("toData");
			fieldsToSelect.add("content");fieldsToSelect.add("subject");fieldsToSelect.add("externalMsgId");
			fieldsToSelect.add("msgSendStatus");fieldsToSelect.add("msgSentTime");fieldsToSelect.add("direction");
			fieldsToSelect.add("workEffortId");fieldsToSelect.add("workEffortName");
			
			conditions = new ArrayList<EntityCondition>();
			if(UtilValidate.isNotEmpty(fromData))
				conditions.add(EntityCondition.makeCondition("fromData", EntityOperator.EQUALS, fromData));
			
			if(UtilValidate.isNotEmpty(toData))
				conditions.add(EntityCondition.makeCondition("toData", EntityOperator.EQUALS, toData));
			
			direction = UtilValidate.isNotEmpty(direction) ? direction : "IN";
			if(UtilValidate.isNotEmpty(direction)) {
				if("INOUT".equals(direction))
					conditions.add(EntityCondition.makeCondition("direction", EntityOperator.IN, UtilMisc.toMap("IN", "OUT")));
				else
					conditions.add(EntityCondition.makeCondition("direction", EntityOperator.EQUALS, direction));
			}
			if(UtilValidate.isNotEmpty(workEffortId))
				conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
			
			conditions.add(EntityCondition.makeCondition("communicationEventTypeId", EntityOperator.EQUALS, "SMS_COMMUNICATION"));
			
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "")
					));
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, "")
				));
			
			EntityCondition mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			long recordCount = EntityQuery.use(delegator).select(fieldsToSelect)
					.from(dynamicViewEntity).where(mainCond).queryCount();
			
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("barId", "unassigned_sms");
			data.put("count", recordCount);
			dataList.add(data);
			
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}
	
	public static String markAsMailRead(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		try {
			String messageId = (String) context.get("messageId");
			String emailId = (String) context.get("toEmail");
			Map<String, Object> contentMap = new HashMap<String, Object>();
			
			String communicationEventId = (String) context.get("communicationEventId");
			if(UtilValidate.isNotEmpty(communicationEventId)) {
				GenericValue communicationEvent =  EntityQuery.use(delegator).from("CommunicationEvent").where("communicationEventId", communicationEventId).queryFirst();
				if(UtilValidate.isNotEmpty(communicationEvent)) {
					messageId = communicationEvent.getString("messageId");
					emailId = communicationEvent.getString("toString");
					contentMap = org.fio.admin.portal.util.DataUtil.convertToMap(communicationEvent.getString("content"));
				}
				
				GenericValue exchangeUser = EntityQuery.use(delegator).from("ExchangeUser").where("emailId",emailId).queryFirst();
				
				String exchangeUserId = UtilValidate.isNotEmpty(exchangeUser) ? exchangeUser.getString("userId") : "";
				
				if(UtilValidate.isNotEmpty(messageId) && UtilValidate.isNotEmpty(exchangeUserId)) {
					result = dispatcher.runSync("markMailRead", UtilMisc.toMap("userLogin", userLogin,"userId", exchangeUserId, "messageId", messageId, "mailFolder", "inbox"));
					if(ServiceUtil.isError(result)) {
						result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						result.put(ModelService.ERROR_MESSAGE, ServiceUtil.getErrorMessage(result));
						return doJSONResponse(response, result);
					}
					
					//update back the message response
					contentMap.put("isRead", true);
					String mailRes = org.fio.admin.portal.util.DataUtil.convertToJsonStr(contentMap);
					communicationEvent.set("content", mailRes);
					communicationEvent.store();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Mark as read");
		return doJSONResponse(response, result);
	}
	public static String loadAssocParties(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String domainType = (String) context.get("domainType");
		String domainId = (String) context.get("domainId");
		List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			if (UtilValidate.isNotEmpty(domainType) && UtilValidate.isNotEmpty(domainId)) {
				String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);
				if("OPPORTUNITY".equals(domainType)) {
					List<GenericValue> opportunityRoleList = EntityQuery.use(delegator).from("SalesOpportunityRole").where("salesOpportunityId", domainId).filterByDate().queryList();
					if(UtilValidate.isNotEmpty(opportunityRoleList)) {
						for(GenericValue opportunityRole : opportunityRoleList) {
							Map<String, Object> data = new HashMap<>();
							String partyId = opportunityRole.getString("partyId");
							String customerName = PartyHelper.getPartyName(delegator, partyId, false);
							
							Map<String,String> partyContactInfo = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
							String phoneNumber = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("PrimaryPhone") : "";
							String infoString = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("EmailAddress") : "";
							
							String phoneSolicitation = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("phoneSolicitation") : "";
							String emailSolicitation = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("emailSolicitation") : "";
							
							data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, opportunityRole));
							data.put("phoneNumber", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(phoneNumber) );
							data.put("infoString", infoString );
							data.put("name", customerName);
							data.put("phoneSolicitation", phoneSolicitation );
							data.put("emailSolicitation", emailSolicitation );
							data.put("roleTypeDesc", org.groupfio.common.portal.util.DataUtil.getRoleTypeDescription(delegator, opportunityRole.getString("roleTypeId")));
							
							data.put("fromDate", UtilDateTime.timeStampToString(opportunityRole.getTimestamp("fromDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
							data.put("lastUpdatedTxStamp", UtilDateTime.timeStampToString(opportunityRole.getTimestamp("lastUpdatedTxStamp"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
							
							dataList.add(data);
						}
					}
				}
				result.put("list", dataList);
			}
		}catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			//e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return doJSONResponse(response, result);
	}
	
	public static String getEmailCommunicationHistory(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String) context.get("partyId");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalKey = (String) context.get("externalLoginKey");
		Debug.log("externalLoginKey***********" + externalKey);
		Debug.log("domainEntityType***********" + domainEntityType);
		String searchType = request.getParameter("searchType");
		String mailStatus = request.getParameter("mailStatus");
		String subSearchType = request.getParameter("subSearchType");
		String displayType =  request.getParameter("displayType");
		String applyToAll =  UtilValidate.isNotEmpty(request.getParameter("applyToAll")) ? request.getParameter("applyToAll") : "";
		String filterBy =  request.getParameter("filterBy");
		
		Timestamp systemTime = UtilDateTime.nowTimestamp();
		
		List<Map<String, Object>> dataList = new LinkedList<Map<String,Object>>();

		try {
			
			String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

			// Integrate security matrix logic start
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String businessUnit = null;

			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			
			// Integrate security matrix logic end
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				List<EntityCondition> conditionlist = FastList.newInstance();
				List<String> assignedWorkEffort = new ArrayList<String>();
				
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				dynamicView.addMemberEntity("WE", "WorkEffort");
				dynamicView.addAlias("WE", "workEffortId","workEffortId", null, Boolean.FALSE, Boolean.TRUE, null);
				dynamicView.addAlias("WE", "workEffortName");
				dynamicView.addAlias("WE", "workEffortTypeId");
				dynamicView.addAlias("WE", "createdByUserLogin");
				dynamicView.addAlias("WE", "domainEntityType");
				dynamicView.addAlias("WE", "domainEntityId");
				dynamicView.addAlias("WE", "createdTxStamp");
				dynamicView.addAlias("WE", "lastUpdatedTxStamp");
				dynamicView.addMemberEntity("CEWE", "CommunicationEventWorkEff");
				dynamicView.addAlias("CEWE", "communicationEventId");
				dynamicView.addViewLink("WE", "CEWE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
				
				dynamicView.addMemberEntity("CE", "CommunicationEvent");
				dynamicView.addAlias("CE", "communicationEventTypeId");
				dynamicView.addAlias("CE", "subject");
				dynamicView.addAlias("CE", "content");
				dynamicView.addAlias("CE", "fromString");
				dynamicView.addAlias("CE", "toString");
				dynamicView.addAlias("CE", "ccString");
				dynamicView.addAlias("CE", "messageId");
				dynamicView.addAlias("CE", "entryDate");
				dynamicView.addViewLink("CEWE", "CE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("communicationEventId"));
				
				List<String> partyIdList = new ArrayList<String>(); 
				if(UtilValidate.isNotEmpty(partyId)) {
					partyIdList.add(partyId);
					String requestURI = (String) context.get("requestURI");
					if(UtilValidate.isNotEmpty(requestURI) && requestURI.contains("/viewAccount")) {
						List<String> contactIds = DataUtil.getContactPartyList(delegator, UtilMisc.toMap("partyIdTo", partyId,"roleTypeIdFrom","CONTACT","roleTypeIdTo","ACCOUNT", "partyRelationshipTypeId","CONTACT_REL_INV"));
						if(UtilValidate.isNotEmpty(contactIds))
							partyIdList.addAll(contactIds);
					}
					
					if("SERVICE_REQUEST".equals(searchType)) {
						dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
						dynamicView.addAlias("CRWE", "custRequestId");
						dynamicView.addViewLink("WE", "CRWE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						dynamicView.addMemberEntity("CRP", "CustRequestParty");
						dynamicView.addAlias("CRP", "crpPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("CRP", "roleTypeId");
						dynamicView.addAlias("CRP", "crpFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("CRP", "crpThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("CRWE", "CRP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("custRequestId"));
						
						/*
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("crpPartyId", EntityOperator.EQUALS, partyId),
								EntityUtil.getFilterByDateExpr("crpFromDate", "crpThruDate")
								)); */
						
						dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
						dynamicView.addAlias("WEPA", "wepaPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "roleTypeId", "roleTypeId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						ModelViewLink link1 = new ModelViewLink("WE", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("workEffortId"));
						ModelViewLink link2 = new ModelViewLink("CRP", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("partyId"));
						List<ModelViewLink> modelLinkList = new ArrayList<ModelViewLink>();
						modelLinkList.add(link1);
						modelLinkList.add(link2);
						dynamicView.addAllViewLinksToList(modelLinkList);
						

						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("wepaPartyId", EntityOperator.IN, partyIdList),
								EntityUtil.getFilterByDateExpr("wepFromDate", "wepThruDate")
								));
						
					} else if("OPPORTUNITY".equals(searchType)) {
						dynamicView.addMemberEntity("SOWE", "SalesOpportunityWorkEffort");
						dynamicView.addAlias("SOWE", "salesOpportunityId");
						dynamicView.addViewLink("WE", "SOWE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						dynamicView.addMemberEntity("SO", "SalesOpportunity");
						dynamicView.addAlias("SO", "opportunityName");
						dynamicView.addAlias("SO", "soPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("SOWE", "SO", Boolean.FALSE, ModelKeyMap.makeKeyMapList("salesOpportunityId"));
						
						dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
						dynamicView.addAlias("WEPA", "wepaPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "roleTypeId", "roleTypeId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						ModelViewLink link1 = new ModelViewLink("WE", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("workEffortId"));
						ModelViewLink link2 = new ModelViewLink("SO", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("partyId"));
						List<ModelViewLink> modelLinkList = new ArrayList<ModelViewLink>();
						modelLinkList.add(link1);
						modelLinkList.add(link2);
						dynamicView.addAllViewLinksToList(modelLinkList);
						
						//conditionlist.add(EntityCondition.makeCondition("soPartyId", EntityOperator.EQUALS, partyId));
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("wepaPartyId", EntityOperator.IN, partyIdList),
								EntityUtil.getFilterByDateExpr("wepFromDate", "wepThruDate")
								));
						
					} else if("ORDER".equals(searchType)) {
						dynamicView.addMemberEntity("OWE", "OrderWorkEffort");
						dynamicView.addAlias("OWE", "orderId");
						dynamicView.addViewLink("WE", "OWE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						dynamicView.addMemberEntity("RTM", "RmsTransactionMaster");
						dynamicView.addAlias("RTM", "billToPartyId", "billToPartyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("OWE", "RTM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("orderId"));
						
						dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
						dynamicView.addAlias("WEPA", "wepaPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "roleTypeId", "roleTypeId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						ModelViewLink link1 = new ModelViewLink("WE", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("workEffortId"));
						ModelViewLink link2 = new ModelViewLink("RTM", "WEPA", Boolean.FALSE, null, ModelKeyMap.makeKeyMapList("billToPartyId","partyId"));
						List<ModelViewLink> modelLinkList = new ArrayList<ModelViewLink>();
						modelLinkList.add(link1);
						modelLinkList.add(link2);
						dynamicView.addAllViewLinksToList(modelLinkList);
						
						//conditionlist.add(EntityCondition.makeCondition("soPartyId", EntityOperator.EQUALS, partyId));
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("wepaPartyId", EntityOperator.IN, partyIdList),
								EntityUtil.getFilterByDateExpr("wepFromDate", "wepThruDate")
								));
						
						
					} else if("ALL".equals(searchType)) {
						dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
						dynamicView.addAlias("WEPA", "wepaPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "roleTypeId", "roleTypeId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("wepaPartyId", EntityOperator.IN, partyIdList),
								EntityUtil.getFilterByDateExpr("wepFromDate", "wepThruDate")
								));
						
					} 
				} 
				if("QUEUE".equals(searchType)) {
					
					if("SERVICE_REQUEST".equals(subSearchType)) {
						dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
						dynamicView.addAlias("CRWE", "custRequestId");
						dynamicView.addViewLink("WE", "CRWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					} else if("OPPORTUNITY".equals(subSearchType)) {
						dynamicView.addMemberEntity("SOWE", "SalesOpportunityWorkEffort");
						dynamicView.addAlias("SOWE", "salesOpportunityId");
						dynamicView.addViewLink("WE", "SOWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					} else if("ORDER".equals(subSearchType)) {
						dynamicView.addMemberEntity("OWE", "OrderWorkEffort");
						dynamicView.addAlias("OWE", "orderId");
						dynamicView.addViewLink("WE", "OWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					} else {
						dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
						dynamicView.addAlias("CRWE", "custRequestId");
						dynamicView.addViewLink("WE", "CRWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						dynamicView.addMemberEntity("SOWE", "SalesOpportunityWorkEffort");
						dynamicView.addAlias("SOWE", "salesOpportunityId");
						dynamicView.addViewLink("WE", "SOWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						dynamicView.addMemberEntity("OWE", "OrderWorkEffort");
						dynamicView.addAlias("OWE", "orderId");
						dynamicView.addViewLink("WE", "OWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					}
					
					if(UtilValidate.isNotEmpty(partyId)) {
						
						dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
						dynamicView.addAlias("WEPA", "wepaPartyId", "partyId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepFromDate", "fromDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "wepThruDate", "thruDate", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addAlias("WEPA", "roleTypeId", "roleTypeId", null, Boolean.FALSE, Boolean.FALSE, null);
						dynamicView.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
						
						conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("wepaPartyId", EntityOperator.EQUALS, partyId),
								EntityUtil.getFilterByDateExpr("wepFromDate", "wepThruDate")
								));
					
						//workEffPtyCond.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					} else if(!("N".equals(applyToAll))) {
						List<EntityCondition> workEffPtyCond = new ArrayList<EntityCondition>();
						workEffPtyCond.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"));
						workEffPtyCond.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "ACCOUNT"));
						List<GenericValue> workEffortPartyAssigns = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(EntityCondition.makeCondition(workEffPtyCond, EntityOperator.AND)).filterByDate("fromDate","thruDate").queryList();
						if(UtilValidate.isNotEmpty(workEffortPartyAssigns)) {
							assignedWorkEffort = EntityUtil.getFieldListFromEntityList(workEffortPartyAssigns, "workEffortId", true);
						}
						
						if("UN_ASSIGNED".equals(displayType)) {
							conditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.NOT_IN, assignedWorkEffort));
						} else if("ASSIGNED".equals(displayType)) {
							conditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, assignedWorkEffort));
						}
					}
				}
				if(UtilValidate.isNotEmpty(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId)) {
					conditionlist.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType),
							EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId)
							));
				}
				/*
				if(UtilValidate.isNotEmpty(mailStatus)) {
					conditionlist.add(EntityCondition.makeCondition("mailStatus", EntityOperator.EQUALS, mailStatus));
				} */
				
				conditionlist.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS,"EMAIL"));
				
				EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				Debug.log("=== Condition : ====="+ condition, MODULE);
				List<GenericValue> emails = EntityQuery.use(delegator).from(dynamicView).where(condition).orderBy("lastUpdatedTxStamp DESC").queryList();


				if(UtilValidate.isNotEmpty(emails)) {
					int count = 0;
					for(GenericValue email : emails) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(email));
						String communicationEventId = email.getString("communicationEventId");
						String workEffortId =  email.getString("workEffortId");
						String communicationEventTypeId = email.getString("communicationEventTypeId");

						List<GenericValue> commEventContentAssocList = EntityQuery.use(delegator).from("CommEventContentAssoc").where(EntityCondition.makeCondition("communicationEventId", EntityOperator.EQUALS, email.getString("communicationEventId"))).queryList();

						boolean isAttach = false;
						Debug.log("Get Attachment data start.");
						if(UtilValidate.isNotEmpty(commEventContentAssocList)){
							data.put("isAttachment","Y");
							isAttach =  true;
							Map<String, Object> requestContext = new HashMap<String, Object>();
							requestContext.put("workEffortId", workEffortId);
							requestContext.put("communicationEventId", communicationEventId);

							Map<String, Object> contentDetail = dispatcher.runSync("common.getFileContentData", UtilMisc.toMap("requestContext", requestContext, "userLogin", userLogin));
							if(ServiceUtil.isSuccess(contentDetail) && UtilValidate.isNotEmpty(contentDetail.get("resultMap"))) {
								Map<String, Object> resultMap = (Map<String, Object>) contentDetail.get("resultMap");
								List<GenericValue> fileContents = (List<GenericValue>) resultMap.get("fileContents");
								if(UtilValidate.isNotEmpty(fileContents)) {
									data.put("fileContents", fileContents);
								}
							}

						}else{
							data.put("isAttachment","N");
						}

						if(UtilValidate.isNotEmpty(communicationEventTypeId) && "GRAPH_EMAIL".equals(communicationEventTypeId)) {
							data.remove("content");
							Map<String, Object> mailContent = org.fio.admin.portal.util.DataUtil.convertToMap(email.getString("content"));
							//data.putAll(mailContent);
							data.put("isRead", mailContent.get("isRead"));
							data.put("isDraft", mailContent.get("isDraft"));
							data.put("bodyPreview", mailContent.get("bodyPreview"));
							//data.put("isAttachment", mailContent.get("isAttachment"));
							data.put("from", mailContent.get("from"));
							data.put("hasAttachments", mailContent.get("hasAttachments"));
							Map<String, Object> fromEmailMap = new HashMap<String, Object>();
							if(UtilValidate.isNotEmpty(mailContent) && UtilValidate.isNotEmpty(mailContent.get("from"))) {
								fromEmailMap = (Map<String, Object>) mailContent.get("from");
							}
							if(UtilValidate.isNotEmpty(fromEmailMap) && UtilValidate.isNotEmpty(fromEmailMap.get("emailAddress"))) {
								Map<String, Object> emailAddress = (Map<String, Object>) fromEmailMap.get("emailAddress");
								data.put("fromPartyName", UtilValidate.isNotEmpty(emailAddress) ? emailAddress.get("name"):"");
							}

							Map<String, Object> msgBody = (Map<String, Object>) mailContent.get("body");
							String emailCont = UtilValidate.isNotEmpty(msgBody) && UtilValidate.isNotEmpty(msgBody.get("content")) ? (String) msgBody.get("content") :"";
							if(isAttach) {
								if(emailCont.contains("<img "))
									emailCont = emailCont.replaceAll("<p class=\"MsoNormal\">&nbsp;</p>"," ");

								emailCont = emailCont.replaceAll("<img .*?>","&nbsp;");
							}
							data.put("message", emailCont);
						}
						else {
							String createdByUserLogin = (String) email.get("createdByUserLogin");
							GenericValue UserLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId",createdByUserLogin).queryFirst();
							if(UtilValidate.isNotEmpty(UserLoginPerson) && UtilValidate.isNotEmpty(UserLoginPerson.getString("partyId"))){
								data.put("fromPartyName",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, UserLoginPerson.getString("partyId"), false));
							}
							String emailCont = UtilValidate.isNotEmpty(email.getString("content")) ? email.getString("content") :"";
							if(isAttach) {
								if(emailCont.contains("<img "))
									emailCont = emailCont.replaceAll("<p class=\"MsoNormal\">&nbsp;</p>"," ");

								emailCont = emailCont.replaceAll("<img .*?>","&nbsp;");
							}
							data.put("message", emailCont);
						}

						Timestamp entryDateTime = email.getTimestamp("entryDate");
						Timestamp last_5_minutes = UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.MINUTE, -5);
						if(UtilValidate.isNotEmpty(entryDateTime) && entryDateTime.after(last_5_minutes))
							data.put("isNewEmail", "Y");
						else
							data.put("isNewEmail", "N");

						data.put("entryDate", UtilValidate.isNotEmpty(email.get("entryDate")) ? UtilDateTime.timeStampToString(email.getTimestamp("entryDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : UtilValidate.isNotEmpty(email.get("createdTxStamp")) ? UtilDateTime.timeStampToString(email.getTimestamp("createdTxStamp"), globalDateTimeFormat, TimeZone.getDefault(), null) : "" );
						if("QUEUE".equals(searchType) && UtilValidate.isEmpty(partyId)) {
							if(assignedWorkEffort.contains(workEffortId)) {
								data.put("assignStatusId", "ASSIGNED");
							} else {
								data.put("assignStatusId", "UN_ASSIGNED");
							}
						} else {
							data.put("assignStatusId", "ASSIGNED");
						}

						if("unassigned-emails".equals(filterBy)) {
							data.put("assignBtnLabel", "Assign User");
						} else if("assigned-emails".equals(filterBy)) {
							data.put("assignBtnLabel", "Re-Assign User");
						} else if("last-7-days".equals(filterBy)) {
							data.put("assignBtnLabel", "Re-Assign User");
						} else if("last-24-hours".equals(filterBy)) {
							data.put("assignBtnLabel", "Re-Assign User");
						}
						data.put("assignBtn", count);
						data.put("domainAssignBtn", count);
						data.put("viewActBtn", count);
						data.put("readBtn", count);
						data.put("externalLoginKey", externalKey);
						data.put("domainEntityTypeDes", UtilValidate.isNotEmpty(email.getString("domainEntityType")) ? org.fio.admin.portal.util.DataHelper.getEnumDescription(delegator, email.getString("domainEntityType"), "ENTITY_DOMAIN") : "");

						dataList.add(data);
					}
				}
			} else {
				Debug.log("error==");
				String errorMessage = "";
				if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errorMessage = accessMatrixRes.get("errorMessage").toString();
				} else {
					errorMessage = "Access Denied";
				}
				result.put("list", new ArrayList<Map<String, Object>>());
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, errorMessage);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put("list",dataList);
		return doJSONResponse(response, result);
	}
	
	public static String deleteContactMechAjax(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<String, Object>();
		String contactMechId = request.getParameter("contactMechId");
		String partyId = request.getParameter("partyId");
		try {
			if(UtilValidate.isNotEmpty(contactMechId)) {
				Map<String,Object> resMap = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId", contactMechId,"userLogin",userLogin));
				if (ServiceUtil.isSuccess(resMap)) {
					
					List<GenericValue> contactMechPurposeDetList = delegator.findByAnd("PartyContactMechPurpose",
							UtilMisc.toMap("partyId", partyId,"contactMechId",contactMechId), null,false);
					for(GenericValue contactMechPurposeDet : contactMechPurposeDetList) {
					String contactMechPurposeTypeId=UtilValidate.isNotEmpty(contactMechPurposeDet)?contactMechPurposeDet.getString("contactMechPurposeTypeId"):"";
					if (UtilValidate.isNotEmpty(contactMechPurposeDet)) {
						try {
							contactMechPurposeDet.set("thruDate", UtilDateTime.nowTimestamp());
							contactMechPurposeDet.store();
						}catch (Exception e1) {
							e1.printStackTrace();
							result.put(EventResponse.STATUS, EventResponse.ERROR);
							result.put(EventResponse.MESSAGE, e1.getMessage());
							return doJSONResponse(response, result);
						}
					}
					}
					result.put(EventResponse.STATUS, EventResponse.SUCCESS);
					result.put(EventResponse.MESSAGE, resMap.get("responseMessage"));
				}else {
					result.put(EventResponse.STATUS, EventResponse.ERROR);
					result.put(EventResponse.MESSAGE, resMap.get("responseMessage"));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put(EventResponse.STATUS, EventResponse.ERROR);
			result.put(EventResponse.MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		
		return doJSONResponse(response, result);
	}
	
	public static String createInviteUser(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> data = FastMap.newInstance();
		Locale locale = UtilHttp.getLocale(request);
		String requestUri = request.getParameter("requestUri");
		String partyId = request.getParameter("partyId");
		String emailTemplateId =  request.getParameter("emailTemplateId");
		Map<String, Object> serviceResults = null;
		String userId = request.getParameter("userId");
		String errMsg = "";
		String contactMechIdTo = "";
		try {
			if(UtilValidate.isEmpty(userId)) {
				GenericValue primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
						.where("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL",
								"contactMechTypeId", "EMAIL_ADDRESS")
						.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
						.queryFirst();

				if (UtilValidate.isEmpty(primaryContactMailGv)) {
					primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
							.where("partyId", partyId, "contactMechTypeId", "EMAIL_ADDRESS")
							.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
							.queryFirst();
				}

				if (UtilValidate.isNotEmpty(primaryContactMailGv)) {
					userId = primaryContactMailGv.getString("infoString");
					contactMechIdTo = primaryContactMailGv.getString("contactMechId");

				} else {
					data.put("response", "error");
					data.put("responseMessage", "Email does not exists : " + userId);
					return doJSONResponse(response, data);
				}
			}
			
			GenericValue userLoginForMailCheck = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userId),
					false);
			if (UtilValidate.isNotEmpty(userLoginForMailCheck)) {
				data.put("response", "error");
				data.put("responseMessage", userLoginForMailCheck.getString("userLoginId") + " already in use");
				return doJSONResponse(response, data);

			}

			String clientDefaultPassword = CommonUtils.getRandomString(Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "temp.password.length.min", "7")));

			Map<String, Object> userLoginContext = new HashMap<String, Object>();
			GenericValue userLoginAtt = null;
			if (UtilValidate.isNotEmpty(userId)) {
				userLoginContext.put("userLoginId", userId);
				userLoginContext.put("currentPassword", clientDefaultPassword);
				userLoginContext.put("currentPasswordVerify", clientDefaultPassword);
				userLoginContext.put("requirePasswordChange", "N");
				userLoginContext.put("enabled", "Y");
				userLoginContext.put("partyId", partyId);
				userLoginContext.put("isClientPortal", "Y");
				GenericValue userLoginForMailId = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userId), false);
				if (UtilValidate.isNotEmpty(userLoginForMailId)) {
					data.put("response", "error");
					data.put("responseMessage", UtilProperties.getMessage(resource, "PartyUserNameInUse", locale));
					return doJSONResponse(response, data);

				}
				try {
					serviceResults = dispatcher.runSync("createUserLogin", userLoginContext);
					// add to UL Attribute
					userLoginAtt = delegator.makeValue("UserLoginAttribute");
					userLoginAtt.set("userLoginId", userId);
					userLoginAtt.set("attrName", "OTP_PWD");
					userLoginAtt.set("attrValue", clientDefaultPassword);
					userLoginAtt.create();
					// assign OWNER role to invite userlogin partyId
					Map<String, Object> callResult = FastMap.newInstance();
					GenericValue partyRole = EntityQuery.use(delegator).select("partyId", "roleTypeId").from("PartyRole").where("partyId", partyId, "roleTypeId", "OWNER").queryFirst();
					if (UtilValidate.isEmpty(partyRole)) {
						dispatcher.runAsync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId",
								"OWNER", "userLogin", userLogin));
					}
					
					// assign security group
					Map<String, Object> updateSecurityContext = new HashMap<String, Object>();
					List selectedGroupIds = new ArrayList();
					updateSecurityContext.put("userLogin", userLogin);
					updateSecurityContext.put("userLoginId", userId);
					String defaultSecurityGroupIds = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "INV_USR_DFLT_SRTY_GRP");
					if(UtilValidate.isNotEmpty(defaultSecurityGroupIds) && defaultSecurityGroupIds.contains(",")) {
						selectedGroupIds = org.fio.admin.portal.util.DataUtil.stringToList(defaultSecurityGroupIds, ",");
					} else if(UtilValidate.isNotEmpty(defaultSecurityGroupIds)) {
						selectedGroupIds.add(defaultSecurityGroupIds);
					}
					if(UtilValidate.isNotEmpty(selectedGroupIds)) {
						updateSecurityContext.put("selectedGroupIds", selectedGroupIds);
						dispatcher.runAsync("ap.addCustomSecurityGroupForInviteUsers", updateSecurityContext);
					}

				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					Debug.logError("createUserLogin  failed: ", MODULE);
				}

				// send Email to customer
				if (ServiceUtil.isSuccess(serviceResults)) {
					if (UtilValidate.isNotEmpty(emailTemplateId)) { 
						sendEmailUsingTemplateId(delegator, partyId, partyId, emailTemplateId, userLogin,
									dispatcher);
					} else {
						data.put("response", "error");
						data.put("responseMessage", "Please configure template for Invite User");
						return doJSONResponse(response, data);
					}
				}
			} else {
				data.put("response", "error");
				data.put("responseMessage", "Userlogin does not exists");
				return doJSONResponse(response, data);
			}
			
			// load the default custom field measurements
			List<String> measurementList = new ArrayList<String>();
			String defaultCustomMeasurements = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DFLT_CUSTOM_MEAS");
			if(UtilValidate.isNotEmpty(defaultCustomMeasurements) && defaultCustomMeasurements.contains(",")) {
				measurementList = org.fio.admin.portal.util.DataUtil.stringToList(defaultCustomMeasurements, ",");
			} else if(UtilValidate.isNotEmpty(defaultCustomMeasurements)) {
				measurementList.add(defaultCustomMeasurements);
			}
			if(UtilValidate.isNotEmpty(measurementList)) {
				List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
				for(String customFieldId : measurementList) {
					String customFieldValue = UtilAttribute.getAttrFieldValue(delegator, UtilMisc.toMap("customFieldId", customFieldId, "domainEntityType", "CUSTOMER", "partyId", partyId));
					if(UtilValidate.isNotEmpty(customFieldValue))
						customFieldValue = customFieldValue.replaceAll("lbs", "").trim();
					
					Map<String, Object> data1 = new HashMap<String, Object>();
					data1.put("type", customFieldId);
					data1.put("value", UtilValidate.isNotEmpty(customFieldValue) ? customFieldValue : "");
					dataList.add(data1);
				}
				String timeUnit= "DAY";
				String timeMeasure=request.getParameter("timeMeasure");
				Timestamp logEntryDate=UtilDateTime.nowTimestamp();
				
				if(UtilValidate.isEmpty(timeMeasure))
					timeMeasure = LocalDate.now().toString();
				Map<String, Object> serviceContext = new LinkedHashMap<String, Object>();
				Map<String, Object> requestContext = new LinkedHashMap<String, Object>();

				requestContext.put("userLoginId", userId);
				requestContext.put("timeUnit", timeUnit);
				requestContext.put("timeMeasure", timeMeasure);
				requestContext.put("logEntryDate", logEntryDate);

				requestContext.put("measures", dataList);
				serviceContext.put("requestContext", requestContext);

				serviceContext.put("userLogin", userLogin);

				Map<String, Object> result = dispatcher.runSync("cs.createCustomerMeasurement", serviceContext);
				if(!ServiceUtil.isSuccess(result)) {
					//return returnError(request, ServiceUtil.getErrorMessage(result));
					result.put("response", "error");
			    	result.put("responseMessage", ServiceUtil.getErrorMessage(result));
			    	return doJSONResponse(response, result);
				}
				
			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			data.put("response", "error");
			data.put("responseMessage", "Exception : "+e.getMessage());
			return doJSONResponse(response, data);
		}
	
		data.put("response", "success");
		data.put("responseMessage", "UserLogin Successfully Created");

		return doJSONResponse(response, data);
	}
	
	public static String resetPassword(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> data = FastMap.newInstance();
		Locale locale = UtilHttp.getLocale(request);
		String requestUri = request.getParameter("requestUri");
		String partyId = request.getParameter("partyId");
		String emailTemplateId =  request.getParameter("emailTemplateId");
		String userId = request.getParameter("userId");
		String contactMechIdTo = "";
		try {
			if(UtilValidate.isEmpty(userId)) {
				GenericValue primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
						.where("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL",
								"contactMechTypeId", "EMAIL_ADDRESS")
						.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
						.queryFirst();

				if (UtilValidate.isEmpty(primaryContactMailGv)) {
					primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
							.where("partyId", partyId, "contactMechTypeId", "EMAIL_ADDRESS")
							.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
							.queryFirst();
				}

				if (UtilValidate.isNotEmpty(primaryContactMailGv)) {
					userId = primaryContactMailGv.getString("infoString");
					contactMechIdTo = primaryContactMailGv.getString("contactMechId");

				} else {
					data.put("response", "error");
					data.put("responseMessage", "Email does not exists : " + userId);
					return doJSONResponse(response, data);
				}
			}
			
			String clientDefaultPassword = CommonUtils.getRandomString(Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "temp.password.length.min", "7")));
			GenericValue userLoginGV = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userId).queryFirst();
			Map<String, Object> userLoginContext = new HashMap<String, Object>();
			
			if (UtilValidate.isNotEmpty(userLoginGV) && UtilValidate.isNotEmpty(userId)) {
				GenericValue userLoginAtt = EntityQuery.use(delegator).from("UserLoginAttribute")
						.where("userLoginId", userLoginGV.getString("userLoginId"), "attrName", "OTP_PWD")
						.queryFirst();
				
				userLoginGV.set("currentPassword", org.ofbiz.base.crypto.HashCrypt.getDigestHash(clientDefaultPassword));
				userLoginGV.set("requirePasswordChange", "N");
				userLoginGV.store();
				// update in UL Attribute
				if(UtilValidate.isNotEmpty(userLoginAtt)) {
					userLoginAtt.set("attrValue", clientDefaultPassword);
					userLoginAtt.store();
				} else {
					userLoginAtt = delegator.makeValue("UserLoginAttribute");
					userLoginAtt.set("userLoginId", userId);
					userLoginAtt.set("attrName", "OTP_PWD");
					userLoginAtt.set("attrValue", clientDefaultPassword);
					userLoginAtt.create();
				}
				
				if (UtilValidate.isNotEmpty(emailTemplateId)) { 
					sendEmailUsingTemplateId(delegator, partyId, partyId, emailTemplateId, userLogin,
								dispatcher);
				} else {
					data.put("response", "error");
					data.put("responseMessage", "Please configure template for password reset");
					return doJSONResponse(response, data);
				}
				data.put("response", "success");
				data.put("responseMessage", "Reset password sent successfully to " + userId);
				
			} else {
				data.put("response", "error");
				data.put("responseMessage", "Userlogin does not exists");
				return doJSONResponse(response, data);
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			data.put("response", "error");
			data.put("responseMessage", "Exception : "+e.getMessage());
			return doJSONResponse(response, data);
			
		}
		return doJSONResponse(response, data);
	}
	
	public static String resetUserLoginStatus(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> data = FastMap.newInstance();
		Locale locale = UtilHttp.getLocale(request);
		String requestUri = request.getParameter("requestUri");
		String partyId = request.getParameter("partyId");
		String userLoginStatus = request.getParameter("userLoginStatus");
		String userId = request.getParameter("userId");
		try {
			if(UtilValidate.isEmpty(userId)) {
				GenericValue primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
						.where("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL",
								"contactMechTypeId", "EMAIL_ADDRESS")
						.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
						.queryFirst();

				if (UtilValidate.isEmpty(primaryContactMailGv)) {
					primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
							.where("partyId", partyId, "contactMechTypeId", "EMAIL_ADDRESS")
							.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
							.queryFirst();
				}

				if (UtilValidate.isNotEmpty(primaryContactMailGv)) {
					userId = primaryContactMailGv.getString("infoString");

				} else {
					data.put("response", "error");
					data.put("responseMessage", "Email does not exists : " + userId);
					return doJSONResponse(response, data);
				}
			}
			GenericValue userLoginGV = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userId).queryFirst();
			if (UtilValidate.isNotEmpty(userLoginGV) && UtilValidate.isNotEmpty(userId)) {
				userLoginGV.put("enabled", userLoginStatus);
				userLoginGV.put("disabledDateTime", userLoginStatus.equals("N") ? org.ofbiz.base.util.UtilDateTime.nowTimestamp() : null);
				userLoginGV.store();
				if(userLoginStatus.equals("N")) {
					data.put("response", "success");
					data.put("responseMessage",userId + " disabled successfully.");
				}else {
					data.put("response", "success");
					data.put("responseMessage",userId + " enabled successfully.");
				}
			} else {
				data.put("response", "error");
				data.put("responseMessage", "Userlogin does not exists");
				return doJSONResponse(response, data);
			}
			
		} catch (Exception e) {
			data.put("response", "error");
			data.put("responseMessage", "Exception : "+e.getMessage());
			return doJSONResponse(response, data);
		}
		return doJSONResponse(response, data);
	}
	public static String searchInvoiceTransactionMaster(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String partyId = request.getParameter("invoicePartyId");

		String invoiceDate = request.getParameter("invoiceDate");
		String invoiceTypeId = request.getParameter("invoiceTypeId"); 
		String invoiceId = request.getParameter("invoiceId");
		String location = request.getParameter("location");
		
		String searchText = request.getParameter("searchText");
		String orderByColumn = request.getParameter("orderByColumn");
		String limit = request.getParameter("fetchLimit");
		String orderBy = request.getParameter("orderBy");
		String dateFormat = request.getParameter("dateFormat");

		String externalLoginKey = request.getParameter("externalLoginKey");
		if(UtilValidate.isEmpty(partyId))
			partyId = request.getParameter("partyId");

		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		List<Object> values = new ArrayList<>();

		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			
			
			if (UtilValidate.isEmpty(partyId)) {
				result.put("data", dataList);
				return AjaxEvents.doJSONResponse(response, result);
			}

			List conditionList = FastList.newInstance();

		//	Debug.log("searchOrders conditions: "+mainConditons);
			SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM INVOICE_TRANSACTION_MASTER ");
			sb.append(" where bill_To_Party_Id = ? ");
			values.add(partyId);
			if (UtilValidate.isNotEmpty(invoiceId)) {
				sb.append(" AND INVOICE_ID = ? ");
				values.add(invoiceId);
			}
			if (UtilValidate.isNotEmpty(invoiceTypeId)) {
				sb.append(" AND invoice_Type = ? ");
				values.add(invoiceTypeId);
			}
			if (UtilValidate.isNotEmpty(location)) {
				sb.append(" AND store_Number = ? ");
				values.add(location);
			}

			if (UtilValidate.isNotEmpty(invoiceDate)) {
				Timestamp od = UtilDateTime.stringToTimeStamp(invoiceDate, "MM/dd/yyyy", TimeZone.getDefault(), Locale.getDefault());
				sb.append(" AND invoice_date >= ? ");
				sb.append(" AND invoice_date <= ? ");
				values.add(UtilDateTime.getDayStart(od));
				values.add(UtilDateTime.getDayEnd(od));
			}
			sb.append(" Group by invoice_id ");
			
			if(UtilValidate.isNotEmpty(orderBy)) {
			    sb.append(" ORDER BY ? ");
			    values.add(orderBy);
			}
			if(UtilValidate.isNotEmpty(limit)) {
				sb.append(" Limit ?");
				values.add(limit);
			}

			ResultSet rs = QueryUtil.getResultSet(sb.toString(), values, delegator);
			
			if (rs!=null) {

				  while (rs.next()) {
					Map<String, Object> data = new HashMap<String, Object>();
					
					invoiceId = rs.getString("INVOICE_ID");
					
					data.put("invoiceId", invoiceId);
					data.put("partyId", partyId);

					if (UtilValidate.isNotEmpty(invoiceId)) {
						String invoiceType = rs.getString("invoice_Type");
						String statusId = rs.getString("invoice_Status");
						String transactionNumber = rs.getString("transaction_number");
						
						GenericValue invoiceTypeGv = EntityQuery.use(delegator).select("description").from("InvoiceType").where("invoiceTypeId",invoiceType ).queryFirst();
						invoiceType = UtilValidate.isNotEmpty(invoiceTypeGv)?invoiceTypeGv.getString("description"):invoiceType;
						
						data.put("transactionNumber", transactionNumber);
						data.put("invoiceType", invoiceType);
						data.put("invoiceSequence", rs.getString("invoice_Sequence_Number"));
						data.put("invoiceDate",UtilValidate.isNotEmpty(rs.getTimestamp("invoice_Date"))
								? UtilDateTime.timeStampToString(rs.getTimestamp("invoice_Date"),
										UtilValidate.isNotEmpty(dateFormat)?dateFormat:globalDateFormat, TimeZone.getDefault(), null) : "");
						
						String statusItemDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId,"INVOICE_STATUS");
						statusId = UtilValidate.isNotEmpty(statusItemDesc)?statusItemDesc:statusId;
						data.put("statusId", statusId);
						
						data.put("billToPartyId", partyId);
						String billToPartyName = rs.getString("bill_To_Party_Name");
						billToPartyName = UtilValidate.isEmpty(billToPartyName)?PartyHelper.getPartyName(delegator, partyId, false):billToPartyName;
						data.put("billToPartyName", billToPartyName);
						
						data.put("billToPartyRefId", rs.getString("bill_To_Party_Ref_Id"));
						
						data.put("skuNumber", rs.getString("sku_Number"));
						data.put("skuDescription", rs.getString("sku_Description"));
						data.put("invoiceExtRefNumber", rs.getString("invoice_Ext_Ref_Number"));
						
						data.put("totalSalesAmount", rs.getBigDecimal("total_Sales_Amount"));
						BigDecimal totalValue = (BigDecimal) rs.getBigDecimal("total_Sales_Amount");
						data.put("totalSalesAmount", UtilValidate.isNotEmpty(totalValue)? totalValue.setScale(2, BigDecimal.ROUND_HALF_EVEN) :"0.00");
						
						data.put("totalInvoiceAmount", rs.getBigDecimal("total_Invoice_Amount"));
						BigDecimal totalInvValue = (BigDecimal) rs.getBigDecimal("total_Invoice_Amount");
						data.put("totalInvoiceAmount", UtilValidate.isNotEmpty(totalInvValue) ? totalInvValue.setScale(2, BigDecimal.ROUND_HALF_EVEN) :"0.00");
						
						BigDecimal extendedDiscount = rs.getBigDecimal("extended_Discount");
						BigDecimal unitCost = rs.getBigDecimal("unit_Cost");
						BigDecimal marginCost = rs.getBigDecimal("margin_Cost");
						BigDecimal quantitySold = rs.getBigDecimal("quantity_Sold");
						
						BigDecimal unitRetail = rs.getBigDecimal("unit_Retail");
						
						data.put("extendedDiscount", UtilValidate.isNotEmpty(extendedDiscount)?extendedDiscount:"0.00");
						data.put("unitCost", UtilValidate.isNotEmpty(unitCost) ? unitCost.setScale(2, BigDecimal.ROUND_HALF_EVEN) :"0.00");
						data.put("marginCost", UtilValidate.isNotEmpty(marginCost) ? marginCost.setScale(2, BigDecimal.ROUND_HALF_EVEN) :"0.00");
						data.put("quantitySold",UtilValidate.isNotEmpty(quantitySold) ? quantitySold:"0.00");
						data.put("unitRetail", UtilValidate.isNotEmpty(unitRetail) ? unitRetail.setScale(2, BigDecimal.ROUND_HALF_EVEN) :"0.00");
						BigDecimal discountAmount = (BigDecimal) rs.getBigDecimal("discount_Amount");
						data.put("discountAmount", UtilValidate.isNotEmpty(discountAmount) ? discountAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN) :"0.00");
						
						data.put("lastUpdatedStamp",UtilValidate.isNotEmpty(rs.getTimestamp("last_Updated_Stamp")) ? UtilDateTime
										.timeStampToString(rs.getTimestamp("last_Updated_Stamp"),
												globalDateTimeFormat, TimeZone.getDefault(), null) : "");
						data.put("createdStamp",UtilValidate.isNotEmpty(rs.getTimestamp("created_Stamp")) ? UtilDateTime
										.timeStampToString(rs.getTimestamp("created_Stamp"),
												globalDateTimeFormat, TimeZone.getDefault(), null) : "");
							
						data.put("externalLoginKey", externalLoginKey);
						data.put("externalId", UtilValidate.isNotEmpty(rs.getString("transaction_Number"))?rs.getString("transaction_Number"):"");
					}

					dataList.add(data);
				}

			}
			Debug.log("-----dataList----------"+dataList);
			result.put("data", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String getUsersListByRole(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String isIncludeLoggedInUser = (String) context.get("isIncludeLoggedInUser");
		String isIncludeInactiveUser = (String) context.get("isIncludeInactiveUser");
		try {
			String userLoginId = userLogin.getString("userLoginId");

			String roleTypeId = UtilValidate.isNotEmpty(context.get("roleTypeId")) ? (String) context.get("roleTypeId")
					: "SALES_REP";
			List<String> roles = new ArrayList<>();
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				String globalConfig = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, roleTypeId);
				if (UtilValidate.isNotEmpty(globalConfig) && globalConfig.contains(",")) {
					roles = org.fio.admin.portal.util.DataUtil.stringToList(globalConfig, ",");
				} else if (UtilValidate.isNotEmpty(globalConfig)) {
					roles.add(globalConfig);
				} else {
					if (roleTypeId.contains(",")) {
						roles = org.fio.admin.portal.util.DataUtil.stringToList(roleTypeId, ",");
					} else
						roles.add(roleTypeId);
				}
			}
			String _where_condition_ = "";
			String _sql_query_ = "SELECT UL.PARTY_ID, UL.USER_LOGIN_ID,"
					+ " concat(PER.FIRST_NAME, IFNULL(CONCAT(' ',PER.LAST_NAME),'')) AS USER_NAME,IFNULL(CM.INFO_STRING,'') AS EMAIL_ADDRESS ,"
					+ " RT.DESCRIPTION AS ROLE_DESC, PR.ROLE_TYPE_ID FROM user_login UL"
					+ " INNER JOIN party P ON UL.PARTY_ID = P.PARTY_ID"
					+ " LEFT OUTER JOIN party_role PR ON UL.PARTY_ID = PR.PARTY_ID"
					+ " LEFT OUTER JOIN role_type RT ON PR.ROLE_TYPE_ID = RT.ROLE_TYPE_ID"
					+ " LEFT OUTER JOIN person PER ON P.PARTY_ID = PER.PARTY_ID"
					+ " LEFT OUTER JOIN party_group PG ON P.PARTY_ID = PG.PARTY_ID"
					+ " LEFT OUTER JOIN party_contact_mech PCM ON p.party_id = PCM.PARTY_ID AND ((PCM.THRU_DATE IS NULL OR PCM.THRU_DATE > NOW()) AND (PCM.FROM_DATE IS NULL OR PCM.FROM_DATE <= NOW()))"
					+ " LEFT OUTER JOIN party_contact_mech_purpose pcmp ON pcm.CONTACT_MECH_ID = pcmp.CONTACT_MECH_ID AND pcmp.CONTACT_MECH_PURPOSE_TYPE_ID='PRIMARY_EMAIL'"
					+ " LEFT OUTER JOIN contact_mech CM ON pcm.CONTACT_MECH_ID = CM.CONTACT_MECH_ID ";

			if (UtilValidate.isNotEmpty(isIncludeInactiveUser) && isIncludeInactiveUser.equals("Y")) {
			} else {
				_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " (UL.ENABLED='Y' OR UL.ENABLED IS NULL)";
			}
			if (UtilValidate.isNotEmpty(isIncludeLoggedInUser) && isIncludeLoggedInUser.equals("N")) {
				_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " UL.USER_LOGIN_ID <> '"+userLoginId+"'";
			}
			if (UtilValidate.isNotEmpty(roles)) {
				_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " PR.ROLE_TYPE_ID IN ("+org.fio.admin.portal.util.DataUtil.toList(roles, "")+")";
			}

			SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));

			ResultSet rs = null;
			String _final_sql_script = _sql_query_+ (UtilValidate.isNotEmpty(_where_condition_) ? " WHERE "+_where_condition_ : "" ) + " GROUP BY UL.PARTY_ID";


			Debug.log("_final_sql_script ---->"+_final_sql_script, MODULE);
			rs = sqlProcessor.executeQuery(_final_sql_script);
			List<String> partyIds = new ArrayList<String>();
			if (rs != null) {
				ResultSetMetaData rsMetaData = rs.getMetaData();
				List<String> columnList = new ArrayList<String>();
				//Retrieving the list of column names
				int count = rsMetaData.getColumnCount();
				for(int i = 1; i<=count; i++) {
					columnList.add(rsMetaData.getColumnName(i));
				}
				
				while (rs.next()) {
					Map<String, Object> data = new HashMap<String, Object>();
					for(String columName : columnList) {
						String fieldName = ModelUtil.dbNameToVarName(columName);
						String fieldValue = rs.getString(columName);
						data.put(fieldName, fieldValue);
					}
					results.add(data);
				}
			}
			if(!rs.isClosed())
            	rs.close();
			
			if(sqlProcessor != null)
				sqlProcessor.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		} finally {
			
		}
		return doJSONResponse(response, results);
	}
	
	public static String getCoordinatorList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
		
		String county = (String) context.get("countyGeoId");
    	String state = (String) context.get("stateGeoId");
    	String isTechInspection = (String) context.get("isTechInspection");
    	String productStoreId = (String) context.get("productStoreId");
    	String isResourceType = (String) context.get("isResourceType");
    	try {
    		List<EntityCondition> conditionList = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(state) && UtilValidate.isNotEmpty(county)) {
    			conditionList.add(EntityCondition.makeCondition("state", EntityOperator.EQUALS, state));
    			conditionList.add(EntityCondition.makeCondition("county", EntityOperator.EQUALS, county));
    			
    			if (UtilValidate.isNotEmpty(productStoreId)) {
	        		conditionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
	        	}
	        	
	    		if (UtilValidate.isNotEmpty(isResourceType) && isResourceType.equals("TECH_INSPECTOR")) {
	    			conditionList.add(EntityCondition.makeCondition("isTechInspection", EntityOperator.EQUALS, "Y"));
	    		}
	    		
	    		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    		
	    		GenericValue coordinatorAssoc = EntityQuery.use(delegator).from("ProductStoreTechAssoc").where(mainConditons).queryFirst();
	    		if(UtilValidate.isNotEmpty(coordinatorAssoc)) {
	    			result.put("coordinator", coordinatorAssoc.getString("coordinator"));
	    			result.put("coordinatorName", coordinatorAssoc.getString("coordinatorName"));
	    			result.put("coordinatorLoginId", UtilValidate.isNotEmpty(coordinatorAssoc.getString("coordinator")) ? org.fio.homeapps.util.DataUtil.getPartyUserLoginId(delegator, coordinatorAssoc.getString("coordinator")) : "");
	    		}
	    		
    		}
    	} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
    	
    	return doJSONResponse(response, result);
	}
	
public static String getPrimaryTechnicianList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
		
		String county = (String) context.get("countyGeoId");
    	String state = (String) context.get("stateGeoId");
    	String isTechInspection = (String) context.get("isTechInspection");
    	String productStoreId = (String) context.get("productStoreId");
    	String isResourceType = (String) context.get("isResourceType");
    	try {
    		List<EntityCondition> conditionList = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(state) && UtilValidate.isNotEmpty(county)) {
    			conditionList.add(EntityCondition.makeCondition("state", EntityOperator.EQUALS, state));
    			conditionList.add(EntityCondition.makeCondition("county", EntityOperator.EQUALS, county));
    			
    			if (UtilValidate.isNotEmpty(productStoreId)) {
	        		conditionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
	        	}
	        	
	    		if (UtilValidate.isNotEmpty(isResourceType) && isResourceType.equals("TECH_INSPECTOR")) {
	    			conditionList.add(EntityCondition.makeCondition("isTechInspection", EntityOperator.EQUALS, "Y"));
	    		}
	    		
	    		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    		
	    		GenericValue coordinatorAssoc = EntityQuery.use(delegator).from("ProductStoreTechAssoc").where(mainConditons).orderBy("-createdStamp").queryFirst();
	    		String technicianId = "";
	    		String technicianName = "";
	    		if(UtilValidate.isNotEmpty(coordinatorAssoc)) {
	    			for (int i=1;i<=4;i++) {
	    				String techIdSeq = "technicianId0"+i;
	    				String techNameSeq = "technicianName0"+i;
	    				if (UtilValidate.isNotEmpty(coordinatorAssoc.getString(techIdSeq))) {
	    					technicianId = coordinatorAssoc.getString(techIdSeq);
	    					technicianName = coordinatorAssoc.getString(techNameSeq);
	    					break;
	    				}
	    			}
	    			result.put("technicianId", technicianId);
	    			result.put("technicianName", technicianName);
	    			result.put("technicianLoginId", UtilValidate.isNotEmpty(technicianId) ? org.fio.homeapps.util.DataUtil.getPartyUserLoginId(delegator, technicianId) : "");
	    		}
    		}
    	} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
    	
    	return doJSONResponse(response, result);
	}
	
	@SuppressWarnings("unchecked")
	public static String getEmailActivities(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
	
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
	
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
	
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String) context.get("partyId");
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
	
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalKey = (String) context.get("externalLoginKey");
		Debug.log("externalLoginKey***********" + externalKey);
		Debug.log("domainEntityType***********" + domainEntityType);
		String searchType = request.getParameter("searchType");
		String mailStatus = request.getParameter("mailStatus");
		String subSearchType = request.getParameter("subSearchType");
		String displayType = request.getParameter("displayType");
		String applyToAll = UtilValidate.isNotEmpty(request.getParameter("applyToAll"))
				? request.getParameter("applyToAll")
				: "";
		String filterBy = request.getParameter("filterBy");
	
		Timestamp systemTime = UtilDateTime.nowTimestamp();
	
		List<Map<String, Object>> dataList = new LinkedList<Map<String, Object>>();
	
		List<Map<String, Object>> resultList = new LinkedList<Map<String,Object>>();
		long start = System.currentTimeMillis();
		List<Object> values = new ArrayList<>();
		try {
	
			String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
	
			String globalMysqlDateTimeFormat = org.fio.admin.portal.util.DataUtil.getGlobalMysqlDateTimeFormat(delegator);
			
			// Integrate security matrix logic start
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String businessUnit = null;
	
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				//List<EntityCondition> conditionlist = FastList.newInstance();
				
				int highIndex = 0;
	            int lowIndex = 0;
	            long resultListSize = 0;
	            
	            GenericValue systemProperty = EntityQuery.use(delegator)
						.select("systemPropertyValue")
						.from("SystemProperty")
						.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
						.queryFirst();
	
				// set the page parameters
				int viewIndex = 0;
				try {
					viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
				} catch (Exception e) {
					viewIndex = 0;
				}
				result.put("viewIndex", Integer.valueOf(viewIndex));
	
				int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;
	
				int viewSize = fioGridFetch;
				try {
					viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
				} catch (Exception e) {
					viewSize = fioGridFetch;
				}
				result.put("viewSize", Integer.valueOf(viewSize));
				
				if ("unassigned_sms".equals(filterBy)) {
					String fromData = (String) context.get("fromData");
					String toData = (String) context.get("toData");
					String direction = (String) context.get("direction");
					String workEffortId = (String) context.get("workEffortId");
					String externalLoginKey = (String) context.get("externalLoginKey");
					
					DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
					dynamicViewEntity.addMemberEntity("CEWE", "CommunicationEventWorkEff");
					dynamicViewEntity.addAlias("CEWE", "communicationEventId");
					dynamicViewEntity.addMemberEntity("CE", "CommunicationEvent");
					dynamicViewEntity.addAlias("CE", "communicationEventTypeId");
					dynamicViewEntity.addAlias("CE", "fromData");
					dynamicViewEntity.addAlias("CE", "toData");
					dynamicViewEntity.addAlias("CE", "content");
					dynamicViewEntity.addAlias("CE", "subject");
					dynamicViewEntity.addAlias("CE", "externalMsgId");
					dynamicViewEntity.addAlias("CE", "msgSendStatus");
					dynamicViewEntity.addAlias("CE", "msgSentTime");
					dynamicViewEntity.addAlias("CE", "direction");
					dynamicViewEntity.addAlias("CE", "lastUpdatedTxStamp");
					dynamicViewEntity.addAlias("CE", "createdTxStamp");
					dynamicViewEntity.addViewLink("CEWE", "CE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("communicationEventId"));
					dynamicViewEntity.addMemberEntity("WE", "WorkEffort");
					dynamicViewEntity.addAlias("WE", "workEffortId");
					dynamicViewEntity.addAlias("WE", "workEffortName");
					dynamicViewEntity.addAlias("WE", "domainEntityType");
					dynamicViewEntity.addAlias("WE", "domainEntityId");
					dynamicViewEntity.addViewLink("CEWE", "WE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
					
					Set<String> fieldsToSelect = new TreeSet<String>();
					fieldsToSelect.add("communicationEventId");fieldsToSelect.add("fromData");fieldsToSelect.add("toData");
					fieldsToSelect.add("content");fieldsToSelect.add("subject");fieldsToSelect.add("externalMsgId");
					fieldsToSelect.add("msgSendStatus");fieldsToSelect.add("msgSentTime");fieldsToSelect.add("direction");
					fieldsToSelect.add("workEffortId");fieldsToSelect.add("workEffortName");
					
					 // get the indexes for the partial list
					//lowIndex = viewIndex * viewSize + 1;
					//highIndex = (viewIndex + 1) * viewSize;
					
					lowIndex = viewIndex * viewSize;
		            highIndex = (viewIndex + 1) * viewSize;
		            
					List<EntityCondition> conditions = new ArrayList<EntityCondition>();
					if(UtilValidate.isNotEmpty(fromData))
						conditions.add(EntityCondition.makeCondition("fromData", EntityOperator.EQUALS, fromData));
					
					if(UtilValidate.isNotEmpty(toData))
						conditions.add(EntityCondition.makeCondition("toData", EntityOperator.EQUALS, toData));
					
					direction = UtilValidate.isNotEmpty(direction) ? direction : "IN";
					if(UtilValidate.isNotEmpty(direction)) {
						if("INOUT".equals(direction))
							conditions.add(EntityCondition.makeCondition("direction", EntityOperator.IN, UtilMisc.toMap("IN", "OUT")));
						else
							conditions.add(EntityCondition.makeCondition("direction", EntityOperator.EQUALS, direction));
					}
					if(UtilValidate.isNotEmpty(workEffortId))
						conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
					
					conditions.add(EntityCondition.makeCondition("communicationEventTypeId", EntityOperator.EQUALS, "SMS_COMMUNICATION"));
					
					conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, null),
								EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "")
							));
					conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, null),
							EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, "")
						));
					
					EntityCondition mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					
					/*
					EntityQuery entityQuery =  new EntityQuery(delegator);
					entityQuery.from(dynamicViewEntity);
					entityQuery.where(mainCond);
					entityQuery.limit(viewSize);
					entityQuery.offset(lowIndex);
					entityQuery.orderBy("lastUpdatedTxStamp DESC");
					
					List<GenericValue> unassignedSmsList = entityQuery.queryList();
					if(UtilValidate.isNotEmpty(unassignedSmsList)) {
						Map<String, Object> data = new HashMap<String, Object>();
						for(GenericValue unassignSms : unassignedSmsList) {
							data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(unassignSms));
							dataList.add(data);
						}
						result.put("list", dataList);
					}*/
	
					List<GenericValue> resultList1 = EntityQuery.use(delegator).select(fieldsToSelect)
							.limit(viewSize)
							.offset(lowIndex)
							.from(dynamicViewEntity).where(mainCond)
							.cache(true).orderBy("lastUpdatedTxStamp DESC").queryList();
					
					resultListSize = QueryUtil.findCountByCondition(delegator, dynamicViewEntity, 
							mainCond, null, null, null, UtilMisc.toMap("totalCount", resultList1.size(), 
									"fioGridFetch", fioGridFetch));
	
					if(UtilValidate.isNotEmpty(resultList1)) {
						for(GenericValue unassignSms : resultList1) {
							Map<String, Object> data = new HashMap<String, Object>();
							data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(unassignSms));
							String fromPhoneNumber = UtilValidate.isNotEmpty(unassignSms.getString("fromData")) ? unassignSms.getString("fromData") : "";
							if(UtilValidate.isNotEmpty(fromPhoneNumber)) {
								String senderPartyId = org.fio.admin.portal.util.DataUtil.getPartyIdByPrmaryPhone(delegator, fromPhoneNumber);
								String senderName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, senderPartyId, false);
								data.put("fromData", fromPhoneNumber + " ( "+senderName + " ) ");
							}
							data.put("externalLoginKey", externalLoginKey);
							String smsDirection = UtilValidate.isNotEmpty(unassignSms.getString("direction")) ? unassignSms.getString("direction") : "";
							if(UtilValidate.isNotEmpty(smsDirection))
								data.put("direction", smsDirection.equals("IN") ? "Incoming" : (smsDirection.equals("OUT") ? "Outgoing" : ""));
							resultList.add(data);
						}
						//result.put("list", dataList);
					}
				} else {
					
					String _where_condition_ = "";
	
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "")
							+ " WE.WORK_EFFORT_TYPE_ID = 'EMAIL'";
					List<String> assignedWorkEffort = new ArrayList<String>();
	
					if (UtilValidate.isNotEmpty(filterBy)) {
						if ("unassigned-emails".equals(filterBy)) {
							searchType = "QUEUE";
							displayType = "UN_ASSIGNED";
						} else if ("assigned-emails".equals(filterBy)) {
							searchType = "QUEUE";
							applyToAll = "N";
							partyId = userLogin.getString("partyId");
						} else if ("last-7-days".equals(filterBy)) {
							searchType = "QUEUE";
							applyToAll = "N";
							partyId = userLogin.getString("partyId");
							Timestamp entryStartDate = UtilDateTime.getDayStart(UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.WEEK_OF_MONTH, -1));
							Timestamp entryEndDate = UtilDateTime.getDayStart(UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.WEEK_OF_MONTH, -1));
	
							_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "")
									+ " (CE.ENTRY_DATE >= '"+entryStartDate+"' AND CE.ENTRY_DATE >= '"+entryEndDate+"')";
						} else if ("last-24-hours".equals(filterBy)) {
							searchType = "QUEUE";
							applyToAll = "N";
							partyId = userLogin.getString("partyId");
							Timestamp entryStartDate = UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.HOUR_OF_DAY,-24);
							Timestamp entryEndDate = UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.SECOND, -1);
	
							_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "")
									+ " (CE.ENTRY_DATE >= '"+entryStartDate+"' AND CE.ENTRY_DATE >= '"+entryEndDate+"')";
						}
					}
					String assignBtnLabel = "";
					if ("unassigned-emails".equals(filterBy)) {
						assignBtnLabel = "Assign User";
					} else if ("assigned-emails".equals(filterBy)) {
						assignBtnLabel = "Re-Assign User";
					} else if ("last-7-days".equals(filterBy)) {
						assignBtnLabel = "Re-Assign User";
					} else if ("last-24-hours".equals(filterBy)) {
						assignBtnLabel = "Re-Assign User";
					}
	
					String _count_sql_query_ = "SELECT COUNT(WE.WORK_EFFORT_ID) as totalRecord";
					String _sql_query_ = "SELECT WE.WORK_EFFORT_ID, WE.WORK_EFFORT_NAME, WE.WORK_EFFORT_TYPE_ID, WE.CREATED_BY_USER_LOGIN, WE.DOMAIN_ENTITY_TYPE, "
							+ " WE.DOMAIN_ENTITY_ID, WE.CREATED_TX_STAMP, WE.LAST_UPDATED_TX_STAMP, CEWE.COMMUNICATION_EVENT_ID,"
							+ " CE.COMMUNICATION_EVENT_TYPE_ID, CE.SUBJECT, CE.FROM_STRING, CE.TO_STRING, CE.CC_STRING, CE.MESSAGE_ID, IFNULL(DATE_FORMAT(CE.ENTRY_DATE,'"+globalMysqlDateTimeFormat+"'), IFNULL(DATE_FORMAT(WE.CREATED_TX_STAMP,'"+globalMysqlDateTimeFormat+"'),'')) AS 'ENTRY_DATE', "
							+ " IF((CE.ENTRY_DATE >= NOW() - INTERVAL 5 MINUTE) , 'Y','N') AS IS_NEW_EMAIL";
	
					if (!"QUEUE".equals(searchType)) {
						_sql_query_ = _sql_query_  + ",CE.content";
					}
	
					String _common_table_sql_query_ = " FROM WORK_EFFORT WE"
							+ " INNER JOIN COMMUNICATION_EVENT_WORK_EFF CEWE ON WE.WORK_EFFORT_ID = CEWE.WORK_EFFORT_ID "
							+ " INNER JOIN COMMUNICATION_EVENT CE ON CEWE.COMMUNICATION_EVENT_ID = CE.COMMUNICATION_EVENT_ID ";
					_count_sql_query_ = _count_sql_query_ + _common_table_sql_query_;
					_sql_query_ = _sql_query_ + _common_table_sql_query_;
	
					List<String> partyIdList = new ArrayList<String>();
					if (UtilValidate.isNotEmpty(partyId)) {
						partyIdList.add(partyId);
						String requestURI = (String) context.get("requestURI");
						if (UtilValidate.isNotEmpty(requestURI) && requestURI.contains("/viewAccount")) {
							List<String> contactIds = DataUtil.getContactPartyList(delegator, UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdFrom", "CONTACT", "roleTypeIdTo", "ACCOUNT", "partyRelationshipTypeId", "CONTACT_REL_INV"));
							if (UtilValidate.isNotEmpty(contactIds))
								partyIdList.addAll(contactIds);
						}
					}
					if ("QUEUE".equals(searchType)) {
	
						if (UtilValidate.isNotEmpty(partyId)) {
	
							String _all_sql_query_ = " INNER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID";
							_sql_query_ = _sql_query_ + _all_sql_query_;
							_count_sql_query_ = _count_sql_query_ + _all_sql_query_;
	
							_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "")
									+ " WEPA.PARTY_ID IN ("+org.fio.admin.portal.util.DataUtil.toList(partyIdList,"")+") "
									+ " AND ((WEPA.THRU_DATE IS NULL OR WEPA.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (WEPA.FROM_DATE IS NULL OR WEPA.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))";
	
						} else if (!("N".equals(applyToAll))) {
	
							if ("UN_ASSIGNED".equals(displayType)) {
								_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "")
										+ " NOT EXISTS ( SELECT 1 FROM WORK_EFFORT_PARTY_ASSIGNMENT WEPA "
										+ " WHERE WEPA.WORK_EFFORT_ID = WE.WORK_EFFORT_ID AND WEPA.ROLE_TYPE_ID <> 'ACCOUNT' "
										+ " AND WEPA.STATUS_ID ='PRTYASGN_ASSIGNED') ";
							} else if ("ASSIGNED".equals(displayType)) {
								_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "")
										+ " EXISTS ( SELECT 1 FROM WORK_EFFORT_PARTY_ASSIGNMENT WEPA "
										+ " WHERE WEPA.WORK_EFFORT_ID = WE.WORK_EFFORT_ID AND WEPA.ROLE_TYPE_ID <> 'ACCOUNT' "
										+ " AND WEPA.STATUS_ID ='PRTYASGN_ASSIGNED') ";
							}
						}
					}
					if (UtilValidate.isNotEmpty(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId)) {
	
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "")
								+ " WE.DOMAIN_ENTITY_TYPE = ? AND WE.DOMAIN_ENTITY_ID= ?)";
						values.add(domainEntityType);
						values.add(domainEntityId);
					}
					//EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
					Debug.logInfo("mainConditons: "+_where_condition_, MODULE);
	
	
					// get the indexes for the partial list
					lowIndex = viewIndex * viewSize;
					//lowIndex = viewIndex * viewSize + 1;
					highIndex = (viewIndex + 1) * viewSize;
					Debug.logInfo("query start: "+UtilDateTime.nowTimestamp(), MODULE);
					// set distinct on so we only get one row per 
					SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
	
					ResultSet rs = null;
					_count_sql_query_ = _count_sql_query_ + (UtilValidate.isNotEmpty(_where_condition_) ?  " WHERE "+_where_condition_ : "");
					rs = QueryUtil.getResultSet(_count_sql_query_, values, delegator);
					if (rs != null) {
						while (rs.next()) {
							resultListSize = (int) rs.getLong("totalRecord");
						}
					}
	
					// String _final_sql_script = "SELECT * FROM ("+_sql_query_ + (UtilValidate.isNotEmpty(_where_condition_) ?  " WHERE "+_where_condition_ : "") + " ORDER BY WE.LAST_UPDATED_TX_STAMP DESC LIMIT "+lowIndex+", "+viewSize+") temp ";
					String _final_sql_script = "SELECT @sn:=@sn+1 AS ASSIGN_BTN, @sn AS DOMAIN_ASSIGN_BTN, @sn AS VIEW_ACT_BTN, @sn AS READ_BTN, '"+ assignBtnLabel +"' AS ASSIGN_BTN_LABEL, '" +externalKey +"' AS EXTERNAL_LOGIN_KEY, temp.* FROM ( " +_sql_query_ + ",(SELECT @sn:=0)t" + (UtilValidate.isNotEmpty(_where_condition_) ?  " WHERE "+_where_condition_ : "") + " ORDER BY WE.LAST_UPDATED_TX_STAMP DESC LIMIT "+lowIndex+", "+viewSize+") temp";
					Debug.log("_count_sql_query_ ---->"+_count_sql_query_, MODULE);
					Debug.log("_final_sql_script ---->"+_final_sql_script, MODULE);
					rs = QueryUtil.getResultSet(_final_sql_script, values, delegator);
					Debug.logInfo("list start : "+UtilDateTime.nowTimestamp(), MODULE);
					if (rs != null) {
						ResultSetMetaData rsMetaData = rs.getMetaData();
						List<String> columnList = new ArrayList<String>();
						int count = rsMetaData.getColumnCount();
	
						long start2 = System.currentTimeMillis();
						while (rs.next()) {
							Map<String, Object> data = new HashMap<String, Object>();
							for(int i = 1; i<=count; i++) {
								String value = "";
								data.put(ModelUtil.dbNameToVarName(rsMetaData.getColumnLabel(i)), rs.getString(i));
							}
							resultList.add(data);
						}
						long end2 = System.currentTimeMillis();
						Debug.logInfo("list preparing--->"+(end2-start2) / 1000f, MODULE);
					}
	
					if(!rs.isClosed())
						rs.close();
					if(sqlProcessor != null)
						sqlProcessor.close();
				}
				
				
	            
	            result.put("highIndex", Integer.valueOf(highIndex));
		        result.put("lowIndex", Integer.valueOf(lowIndex));
				Debug.logInfo("list end: "+UtilDateTime.nowTimestamp(), MODULE);
				
				result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
				result.put("totalRecords", nf.format(resultListSize));
				result.put("recordCount", resultListSize);
				result.put("chunkSize", viewSize);   
				Debug.logInfo("data ready: "+UtilDateTime.nowTimestamp(), MODULE);
			} else {
				Debug.log("error==");
				String errorMessage = "";
				if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errorMessage = accessMatrixRes.get("errorMessage").toString();
				} else {
					errorMessage = "Access Denied";
				}
				result.put("list", new ArrayList<Map<String, Object>>());
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, errorMessage);
	
			}
	
		} catch (Exception e) {
			e.printStackTrace();
	    	Debug.logError(e.getMessage(), MODULE);
	    	result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		Debug.logInfo("try end: "+UtilDateTime.nowTimestamp(), MODULE);
	    long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", resultList);
	    return doJSONResponse(response, result);
	}
	
	public static String createCustomerAction(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String firstName = (String) context.get("firstName");
		String lastName = (String) context.get("lastName");
		String gender = (String) context.get("gender");
		String designation = (String) context.get("designation");
		String primaryPhoneNumber = (String) context.get("primaryPhoneNumber");
		String primaryEmail = (String) context.get("primaryEmail");
		String dataSourceId = (String) context.get("dataSourceId");

		Map<String, Object> result = FastMap.newInstance();

		try {
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();

			callCtxt.put("firstName", firstName);
			callCtxt.put("lastName", lastName);
			callCtxt.put("gender", gender);
			callCtxt.put("designation", designation);
			callCtxt.put("primaryPhoneNumber", primaryPhoneNumber);
			callCtxt.put("primaryEmail", primaryEmail);
			callCtxt.put("dataSourceId", dataSourceId);
			
			callCtxt.put("userLogin", userLogin);
			callResult = dispatcher.runSync("cp.createCustomer", callCtxt);
	        if (ServiceUtil.isSuccess(callResult)) {
	        	result.put("partyId", callResult.get("partyId"));
	        	result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
	        } else {
	        	result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(callResult));
	        }
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String sync3rdPartyInvoice(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> ctx = UtilHttp.getCombinedMap(request);

		String custRequestId = (String) ctx.get("custRequestId");

		Map<String, Object> result = FastMap.newInstance();

		try {
			if(UtilValidate.isNotEmpty(custRequestId)) {
				
				String priceCustomFieldId = DataHelper.getCustomFieldId(delegator, "EXTERNAL_INFO", "Price");
				String priceDescCustomFieldId = DataHelper.getCustomFieldId(delegator, "EXTERNAL_INFO", "Third Party Invoice Numbers");
				//String priceValue =  DataUtil.getCustRequestAttribute(delegator, custRequestId, priceDescCustomFieldId);
				String invoiceDescValue =  DataUtil.getCustRequestAttribute(delegator, custRequestId, priceDescCustomFieldId);
				GenericValue custRequestAttribute = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId, "attrName",priceCustomFieldId).queryFirst();
				String priceValue = "";
				Timestamp enteredDate = null;
				if(UtilValidate.isNotEmpty(custRequestAttribute)) {
					priceValue = custRequestAttribute.getString("attrValue");
					enteredDate = custRequestAttribute.getTimestamp("createdTxStamp");
				}
				
				if(UtilValidate.isNotEmpty(priceValue)) {
					DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
					dynamicViewEntity.addMemberEntity("CRWE", "CustRequestWorkEffort");
					dynamicViewEntity.addAlias("CRWE", "custRequestId");
					dynamicViewEntity.addAlias("CRWE", "workEffortId");
					dynamicViewEntity.addMemberEntity("WE", "WorkEffort");
					dynamicViewEntity.addAlias("WE", "workEffortName");
					dynamicViewEntity.addAlias("WE", "currentStatusId");
					dynamicViewEntity.addAlias("WE", "workEffortPurposeTypeId");
					dynamicViewEntity.addAlias("WE", "estimatedStartDate");
					dynamicViewEntity.addAlias("WE", "actualStartDate");
					dynamicViewEntity.addAlias("WE", "createdTxStamp");
					dynamicViewEntity.addAlias("WE", "lastUpdatedTxStamp");
					dynamicViewEntity.addViewLink("CRWE", "WE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
					
					EntityCondition condition1 =  EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
							EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.EQUALS, "TEST_WORK_TYPE")
							);
					
					List<GenericValue> custRequestWorkEffort = EntityQuery.use(delegator).from(dynamicViewEntity).where(condition1).orderBy("createdTxStamp DESC").queryList();
					if(UtilValidate.isNotEmpty(custRequestWorkEffort)) {
						GenericValue workOrderActivity = custRequestWorkEffort.get(0);
						
						if(UtilValidate.isNotEmpty(workOrderActivity) && UtilValidate.isNotEmpty(workOrderActivity.getString("workEffortId"))) {
							
							String workEffortId = UtilValidate.isNotEmpty(workOrderActivity) ? workOrderActivity.getString("workEffortId") : "";
							
							Timestamp actualStartDate = workOrderActivity.getTimestamp("actualStartDate");
							if(actualStartDate == null) {
								result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
								result.put(GlobalConstants.RESPONSE_MESSAGE, "Activity not Started to log Time Entries");
								return doJSONResponse(response, result);
							}
							
							List<String> roles = new ArrayList<>();
							String activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
							if(UtilValidate.isNotEmpty(activityOwnerRole) && activityOwnerRole.contains(",")) {
								roles = org.fio.admin.portal.util.DataUtil.stringToList(activityOwnerRole, ",");
							} else if(UtilValidate.isNotEmpty(activityOwnerRole)) {
								roles.add(activityOwnerRole);
							}
							EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
													EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,workEffortId),
													EntityCondition.makeCondition("roleTypeId", EntityOperator.IN,roles)
													);
							List<GenericValue> activityOwnerList = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(condition).filterByDate().queryList();
							String partyId = "";
							String roleTypeId = "";
							if(UtilValidate.isNotEmpty(activityOwnerList)) {
								for(GenericValue activityOwner : activityOwnerList) {
									String ptyId = activityOwner.getString("partyId");
									if(org.fio.admin.portal.util.DataUtil.is3rdPartyTechnician(delegator, ptyId)) {
										partyId = ptyId;
										roleTypeId = activityOwner.getString("roleTypeId");
										break;
									}	
								}
							}
							if(UtilValidate.isNotEmpty(partyId)) {
								
								GenericValue timeEntry1 = EntityQuery.use(delegator).from("TimeEntry").where("partyId", partyId, "workEffortId",workEffortId).filterByDate().queryFirst();
								if(UtilValidate.isNotEmpty(timeEntry1)) {
									BigDecimal timeEntry1Cost = timeEntry1.getBigDecimal("cost");
									BigDecimal timeEntry1CostNew = new BigDecimal(priceValue);
									if (UtilValidate.isNotEmpty(timeEntry1Cost) && UtilValidate.isNotEmpty(priceValue) && timeEntry1Cost.compareTo(BigDecimal.ZERO) > 0 && timeEntry1CostNew.compareTo(timeEntry1Cost)==0) {
										result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
										result.put(GlobalConstants.RESPONSE_MESSAGE, "Time entry already exists");
										return doJSONResponse(response, result);
									}else {
										timeEntry1.set("thruDate", UtilDateTime.nowTimestamp());
										timeEntry1.store();
									}
								}
								
								Map<String, Object> ctx1 = new HashMap<String, Object>();
								String timesheetId = (String) ctx1.get("timesheetId");
								ctx1.put("partyId", partyId);
								ctx1.put("roleTypeId", roleTypeId);
								ctx1.put("workEffortId", workEffortId);
								ctx1.put("rateTypeId", "LABOR");
								ctx1.put("userLogin", userLogin);
								
								GenericValue timeEntry = EntityQuery.use(delegator).from("TimeEntry").where("workEffortId",workEffortId).filterByDate().queryFirst();
								if(UtilValidate.isEmpty(timeEntry) || UtilValidate.isEmpty(timeEntry.getString("timesheetId"))) {
									//create Timesheet
						            ModelService service = dispatcher.getDispatchContext().getModelService("createTimesheet");
									Map<String, Object> inputContext = service.makeValid(ctx1, "IN");
									inputContext.put("comments", workOrderActivity.getString("workEffortName")+ " time tracking");
									result = dispatcher.runSync(service.name, inputContext);
									if(!ServiceUtil.isSuccess(result)) {
										result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
										result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(result));
										return doJSONResponse(response, result);
									}
									timesheetId = (String) result.get("timesheetId");
								} else {
									timesheetId = timeEntry.getString("timesheetId");
								}
								
								GenericValue timesheetRole = EntityQuery.use(delegator).from("TimesheetRole").where("timesheetId", timesheetId, "partyId", partyId, "roleTypeId", roleTypeId).queryFirst();
								if(UtilValidate.isEmpty(timesheetRole)) {
									//create Timesheet Role
									ModelService service = dispatcher.getDispatchContext().getModelService("createTimesheetRole");
									Map<String, Object> inputContext = service.makeValid(UtilMisc.toMap("timesheetId",timesheetId, "partyId",partyId, "roleTypeId", roleTypeId,"userLogin",userLogin), "IN");
									result = dispatcher.runSync(service.name, inputContext);
									if(!ServiceUtil.isSuccess(result)) {
										result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
										result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(result));
										return doJSONResponse(response, result);
									}
								}
								
								//Create TimeEntry
								BigDecimal cost = BigDecimal.ZERO;
								double rate= 0.0d; 
								double timeEntered = 0.0d;
								String rateTypeId = (String) ctx1.get("rateTypeId");
								BigDecimal totalCost = UtilValidate.isNotEmpty(priceValue) ? new BigDecimal((String) priceValue) : BigDecimal.ZERO;
								
								ModelService service = dispatcher.getDispatchContext().getModelService("createTimeEntry");
								Map<String, Object> inputContext = service.makeValid(ctx1, "IN");
								inputContext.put("timesheetId", timesheetId);
								inputContext.put("partyId", partyId);
								inputContext.put("hours", timeEntered);
								inputContext.put("cost", totalCost);
								inputContext.put("comments", invoiceDescValue);
								inputContext.put("ratePerHour", new BigDecimal(rate));
								inputContext.put("timeEntryDate", UtilValidate.isNotEmpty(enteredDate) ? org.ofbiz.base.util.UtilDateTime.getDayStart(enteredDate) : UtilDateTime.nowTimestamp() );
								result = dispatcher.runSync(service.name, inputContext);
								if(!ServiceUtil.isSuccess(result)) {
									result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
									result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(result));
									return doJSONResponse(response, result);
									
								}
							} else {
								result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
								result.put(GlobalConstants.RESPONSE_MESSAGE, "Third party technician not found");
								return doJSONResponse(response, result);
							}
						}
					} else {
						result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						result.put(GlobalConstants.RESPONSE_MESSAGE, "Activity not found");
						return doJSONResponse(response, result);
					}
				} else {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Price not configured");
					return doJSONResponse(response, result);
				}
			}
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static String searchChildAccounts(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		List < Map < String, Object >> results = new ArrayList <> ();
		Map < String, Object > context = UtilHttp.getCombinedMap(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String,Object> inMap = FastMap.newInstance();
		String partyId = (String) context.get("partyId");
		GenericValue userLogin=getUserLogin(request);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List condList = FastList.newInstance();
			
			
			if(UtilValidate.isNotEmpty(partyId)){
					SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
					ResultSet rs = null;
					String _selectQuery ="";
					_selectQuery = "SELECT p.party_id AS PARTY_ID,pg.group_name AS GROUP_NAME,p.status_id,PI.id_value as EXTERNAL_ID,"
							+ "pa.ATTR_VALUE AS LOCATION,si.status_id AS STATUS_ID,si.description as STATUS_DESC FROM party_role pr \r\n" + 
							"LEFT JOIN party_relationship prs ON prs.PARTY_ID_to = pr.party_id\r\n" + 
							"LEFT JOIN party p ON p.PARTY_ID = prs.PARTY_ID_FROM\r\n" + 
							"LEFT JOIN party_role pr1 ON p.PARTY_ID = pr1.PARTY_ID\r\n" + 
							"LEFT JOIN party_group pg ON p.PARTY_ID = pg.PARTY_ID\r\n" + 
							"LEFT JOIN status_item si ON p.STATUS_ID = si.STATUS_ID\r\n" + 
							"LEFT OUTER JOIN party_identification pi ON pi.PARTY_ID = p.PARTY_ID AND PI.PARTY_IDENTIFICATION_TYPE_ID ='WW_CUST'\r\n" + 
							"LEFT OUTER JOIN party_attribute pa ON pa.PARTY_ID = p.PARTY_ID AND pa.attr_name='CUST_LOC_NAME'\r\n";
					String _where_condition ="";
					_where_condition =" WHERE prs.role_type_id_from ='ACCOUNT'\r\n" + 
							"AND prs.role_type_id_to ='ACCOUNT'\r\n" + 
							"AND prs.party_Relationship_Type_Id='REL_PARENT_ACCOUNT'\r\n" + 
							"AND pr.PARTY_ID='"+partyId+"'\r\n" + 
							"AND pr.role_type_id='PARENT_ACCOUNT'\r\n" + 
							"AND pr1.role_type_id='ACCOUNT'";
					String _final_query = _selectQuery +" "+ _where_condition;
					Debug.log("-----_final_query-------"+_final_query);
					rs = sqlProcessor.executeQuery(_final_query);
					if(rs !=null){
						try{ 
							while (rs.next()) {
								Map<String, Object> data = new HashMap<String, Object>();
								String accId = rs.getString("PARTY_ID");
								String groupName = rs.getString("GROUP_NAME");
								String externalId = rs.getString("EXTERNAL_ID");
								String statusId = rs.getString("STATUS_ID");
								String statusDesc = rs.getString("STATUS_DESC");
								String location = rs.getString("LOCATION");
								data.put("partyId", accId);
								data.put("groupName", groupName);
								data.put("statusId", statusId);
								data.put("statusDesc", statusDesc);
								data.put("externalId", externalId);
								String location_desc =location;
								data.put("location_desc", location_desc);
								dataList.add(data);
							}
						}catch (Exception e) {
							// TODO: handle exception
							Debug.log("-----Error In find child Accounts-------"+e);
						}
					}
					
					if(!rs.isClosed())
	                	rs.close();
					if(sqlProcessor != null)
						sqlProcessor.close();
			}
		} catch (Exception e) {
			result.put("data", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		Debug.log("-dataList--"+dataList);
		result.put("data", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Success");
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static String addChildAccount(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		List < Map < String, Object >> results = new ArrayList <> ();
		Map < String, Object > context = UtilHttp.getCombinedMap(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String,Object> inMap = FastMap.newInstance();
		String partyId = (String) context.get("partyId");
		String parentAccountId = (String) context.get("parentAccountId");
		GenericValue userLogin=getUserLogin(request);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(parentAccountId)){
				Map requestMap = FastMap.newInstance();
				Map callCxt = FastMap.newInstance();
				requestMap.putAll(context);
				callCxt.put("userLogin", userLogin);
				callCxt.put("requestContext", requestMap);
				Map<String,Object> serviceResult = dispatcher.runSync("common.addChildAccount", callCxt);
				if (ServiceUtil.isError(serviceResult)) {
					result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					result.put(ModelService.ERROR_MESSAGE, serviceResult.get("errorMessage"));
					return doJSONResponse(response, result);
				}
			}
		} catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Success");
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String partyReceiptData(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<>();
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		long start = System.currentTimeMillis();
		List<Map<String, String>> dataList = new LinkedList<>();
		List<GenericValue> resultList = new LinkedList<>();
		Map<String,Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String)context.get("partyId");
		try {
			
			List<EntityCondition> conditions = new ArrayList<>();
			// construct role conditions
			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition partyIdCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,
						partyId);
				conditions.add(partyIdCondition);
			}
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);

			
			GenericValue systemProperty = EntityQuery.use(delegator)
					.select("systemPropertyValue")
					.from("SystemProperty")
					.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
					.queryFirst();
			
            // set the page parameters
	        int viewIndex = 0;
	        try {
	            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
	        } catch (Exception e) {
	            viewIndex = 0;
	        }
	        result.put("viewIndex", Integer.valueOf(viewIndex));

	        int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;
	        
	        int viewSize = fioGridFetch;
	        try {
	            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
	        } catch (Exception e) {
	            viewSize = fioGridFetch;
	        }
	        result.put("viewSize", Integer.valueOf(viewSize));
			int highIndex = 0;
			int lowIndex = 0;
			int resultListSize = 0;
			try {
				// get the indexes for the partial list
				lowIndex = viewIndex * viewSize + 1;
				highIndex = (viewIndex + 1) * viewSize;
	
				// set distinct on so we only get one row per
				// using list iterator
				EntityListIterator pli = EntityQuery.use(delegator).from("EreceiptTransaction")
						.where(mainConditons).orderBy("createDate DESC").cursorScrollInsensitive().fetchSize(highIndex).distinct()
						.cache(true).queryIterator();
				// get the partial list for this page
				resultList = pli.getPartialList(lowIndex, viewSize);
				resultListSize = pli.getResultsSizeAfterPartialList();
				// close the list iterator
				pli.close();
			}catch(GenericEntityException e) {
				String errMsg = "Error: " + e.toString();
				Debug.logError(e, errMsg, MODULE);
			}
			
			if(UtilValidate.isNotEmpty(resultList)) {
				String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
				for(GenericValue resultData : resultList) {
					Map<String,String> data = org.fio.admin.portal.util.DataUtil.convertGenericToMap(resultData);
					data.put("createDate", org.fio.homeapps.util.UtilDateTime.timeStampToString(resultData.getTimestamp("createDate"),globalDateTimeFormat,TimeZone.getDefault(),null));
					//data.put("HTML",  org.ofbiz.base.util.Base64.base64Encode(resultData.getString("HTML")));
					dataList.add(data);
				}
				result.put("highIndex", Integer.valueOf(highIndex));
				result.put("lowIndex", Integer.valueOf(lowIndex));
				result.put("responseMessage", "success");
				result.put("list", dataList);
			}
			result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			result.put("totalRecords", nf.format(resultListSize));
			result.put("recordCount", resultListSize);
			result.put("chunkSize", viewSize);
		}catch(Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		Debug.logInfo("try end: "+UtilDateTime.nowTimestamp(), MODULE);
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		return doJSONResponse(response, result);
	}
	
	public static String getReceiptHtmlData(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String orderId = (String) request.getParameter("orderId");
		String htmlContent = null;
		try {
			GenericValue ereceiptTransaction = delegator.findOne("EreceiptTransaction", UtilMisc.toMap("orderId", orderId),false);
			if(UtilValidate.isNotEmpty(ereceiptTransaction)&& UtilValidate.isNotEmpty(ereceiptTransaction.getString("HTML"))) 
				doJSONResponse(response,UtilMisc.toMap("data",ereceiptTransaction.getString("HTML")));
		}catch(Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return doJSONResponse(response, "");
	}
	
	public static String updateLeadInfo(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String leadName = request.getParameter("companyName");
		String email = request.getParameter("email");
		String oldEmail = request.getParameter("email-old");
		String phone = request.getParameter("phone");
		String country = request.getParameter("country");
		String responseMsg = "Updated customer details and passcode sent successfully";
		String resultString = EventResponse.SUCCESS;
		Map<String,String> result = FastMap.newInstance();
		try {
			String leadAppUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LEAD_APP_URL");
			if(UtilValidate.isNotEmpty(oldEmail))
				leadAppUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LEAD_APP_UPD_URL");
			String apiUser = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LEAD_APP_USER");
			String apiKey = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LEAD_APP_KEY");
			Map<String, Object> requestJson = new LinkedHashMap<>();
			requestJson.put("lead_name", leadName);
			requestJson.put("lead_first_name", firstName);
			requestJson.put("lead_last_name", lastName);
			requestJson.put("lead_prim_phone", phone);
			requestJson.put("lead_prim_email", email);
			if(UtilValidate.isNotEmpty(oldEmail))
				requestJson.put("lead_old_email", oldEmail);
			requestJson.put("lead_country", country);
			if(UtilValidate.isNotEmpty(leadAppUrl)) {
				String input = org.fio.admin.portal.util.DataUtil.convertToJsonStr(requestJson);
				Map<String,Object> responseMap = UtilCommon.connect(input, leadAppUrl, apiUser, apiKey);
				String responseStr = (String) responseMap.get("responseString");
				String httpStatusCode = responseMap.get("responseCode")+"";
				Debug.logInfo("ww response json : "+responseStr, MODULE);
				if(UtilValidate.isNotEmpty(responseStr) && httpStatusCode.contains("200")) {
					String responseCode = "";
					if(DataUtil.isJSONValid(responseStr)) {
						responseMap = org.fio.admin.portal.util.DataUtil.convertToMap(responseStr);
						responseCode = UtilValidate.isNotEmpty(responseMap) && UtilValidate.isNotEmpty(responseMap.get("response_code")) ? (String) responseMap.get("response_code") : "";
					}
					if(responseCode.startsWith("S") || "SUCCESS!".equals(responseStr)) {
						responseMsg = "Customer details updated successfully";
						GenericValue customerDetailUpdated = org.fio.homeapps.util.DataUtil.pretailLoyaltyGlobalParameters(delegator, "IS_CUST_DETL_UPD");
						if(UtilValidate.isEmpty(customerDetailUpdated)) {
							customerDetailUpdated = delegator.makeValue("PretailLoyaltyGlobalParameters",UtilMisc.toMap("parameterId", "IS_CUST_DETL_UPD"));
						}
						customerDetailUpdated.set("value", "Y");
						delegator.createOrStore(customerDetailUpdated);
						
						GenericValue customerOtpAuthenticated = org.fio.homeapps.util.DataUtil.pretailLoyaltyGlobalParameters(delegator, "IS_CUST_DETL_VFD");
						if(UtilValidate.isEmpty(customerOtpAuthenticated)) {
							customerOtpAuthenticated = delegator.makeValue("PretailLoyaltyGlobalParameters",UtilMisc.toMap("parameterId", "IS_CUST_DETL_VFD"));
						}
						customerOtpAuthenticated.set("value", "N");
						delegator.createOrStore(customerOtpAuthenticated);
					} else {
						responseMsg = DataHelper.getErrorMsg(responseMap, responseStr, "Customer details partially updated. Error occurred while updating");
						resultString = EventResponse.ERROR;
					}
				} else {
					responseMsg = DataHelper.getErrorMsg(responseMap, responseStr, "Customer details partially updated. Error occurred while updating");
					resultString = EventResponse.ERROR;
				}
			}else {
				responseMsg = "No Api Url configured";
				resultString = EventResponse.ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseMsg = e.getMessage();
			resultString = EventResponse.ERROR;
		}
		result.put("responseMsg", responseMsg);
		result.put("result", resultString);
		return doJSONResponse(response, result);
	}
	
	public static String reSendOtp(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String email = request.getParameter("email");
		String responseMsg = "Otp sent successfully";
		String resultString = EventResponse.SUCCESS;
		Map<String,String> result = FastMap.newInstance();
		try {
			String leadAppUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LEAD_APP_AUTH_URL");
			String apiUser = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LEAD_APP_USER");
			String apiKey = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LEAD_APP_KEY");
			if(UtilValidate.isNotEmpty(leadAppUrl)) {
				Map<String, Object> requestJson = new LinkedHashMap<>();
				requestJson.put("lead_prim_email", email);
				requestJson.put("mode", "VERIFICATION");
				String input = org.fio.admin.portal.util.DataUtil.convertToJsonStr(requestJson);
				Map<String,Object> responseMap = UtilCommon.connect(input, leadAppUrl, apiUser, apiKey);
				String responseStr = (String) responseMap.get("responseString");
				String httpStatusCode = responseMap.get("responseCode")+"";
				Debug.logInfo("ww response json : "+responseStr, MODULE);
				if(UtilValidate.isNotEmpty(responseStr) && httpStatusCode.contains("200")) {
					String responseCode = "";
					if(DataUtil.isJSONValid(responseStr)) {
						responseMap = org.fio.admin.portal.util.DataUtil.convertToMap(responseStr);
						responseCode = UtilValidate.isNotEmpty(responseMap) && UtilValidate.isNotEmpty(responseMap.get("response_code")) ? (String) responseMap.get("response_code") : "";
					}
					if(responseCode.startsWith("S") || "SUCCESS!".equals(responseStr)) {
						responseMsg = "Otp sent successfully";
						GenericValue customerOtpAuthenticated = org.fio.homeapps.util.DataUtil.pretailLoyaltyGlobalParameters(delegator, "IS_CUST_DETL_VFD");
						if(UtilValidate.isEmpty(customerOtpAuthenticated)) {
							customerOtpAuthenticated = delegator.makeValue("PretailLoyaltyGlobalParameters",UtilMisc.toMap("parameterId", "IS_CUST_DETL_VFD"));
						}
						customerOtpAuthenticated.set("value", "N");
						delegator.createOrStore(customerOtpAuthenticated);
					} else {
						responseMsg = DataHelper.getErrorMsg(responseMap, responseStr, "Error occurred while sending otp");
						resultString = EventResponse.ERROR;
					}
				} else {
					responseMsg = DataHelper.getErrorMsg(responseMap, responseStr, "Error occurred while sending otp");
					resultString = EventResponse.ERROR;
				}
			}else {
				responseMsg = "No Api Url configured";
				resultString = EventResponse.ERROR;
			}
		}catch (Exception e) {
			e.printStackTrace();
			responseMsg = e.getMessage();
			resultString = EventResponse.ERROR;
		}
		result.put("responseMsg", responseMsg);
		result.put("result", resultString);
		return doJSONResponse(response, result);
	}
	
	public static String authenticateLeadInfo(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String email = request.getParameter("email");
		String otp = request.getParameter("otp");
		String responseMsg = "Authenticated customer details successfully";
		String resultString = EventResponse.SUCCESS;
		Map<String,String> result = FastMap.newInstance();
		try {
			String leadAppUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LEAD_APP_AUTH_URL");
			String apiUser = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LEAD_APP_USER");
			String apiKey = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LEAD_APP_KEY");
			if(UtilValidate.isNotEmpty(leadAppUrl)) {
				Map<String, Object> requestJson = new LinkedHashMap<>();
				requestJson.put("lead_prim_email", email);
				requestJson.put("otp", otp);
				requestJson.put("mode", "VALIDATION");
				String input = org.fio.admin.portal.util.DataUtil.convertToJsonStr(requestJson);
				Map<String,Object> responseMap = UtilCommon.connect(input, leadAppUrl, apiUser, apiKey);
				String responseStr = (String) responseMap.get("responseString");
				String httpStatusCode = responseMap.get("responseCode")+"";
				Debug.logInfo("ww response json : "+responseStr, MODULE);
				if(UtilValidate.isNotEmpty(responseStr) && httpStatusCode.contains("200")) {
					String responseCode = "";
					if(DataUtil.isJSONValid(responseStr)) {
						responseMap = org.fio.admin.portal.util.DataUtil.convertToMap(responseStr);
						responseCode = UtilValidate.isNotEmpty(responseMap) && UtilValidate.isNotEmpty(responseMap.get("response_code")) ? (String) responseMap.get("response_code") : "";
					}
					if(responseCode.startsWith("S") || "SUCCESS!".equals(responseStr)) {
						responseMsg = "Athenticated customer details successfully";
						GenericValue customerOtpAuthenticated = org.fio.homeapps.util.DataUtil.pretailLoyaltyGlobalParameters(delegator, "IS_CUST_DETL_VFD");
						if(UtilValidate.isEmpty(customerOtpAuthenticated)) {
							customerOtpAuthenticated = delegator.makeValue("PretailLoyaltyGlobalParameters",UtilMisc.toMap("parameterId", "IS_CUST_DETL_VFD"));
						}
						customerOtpAuthenticated.set("value", "Y");
						delegator.createOrStore(customerOtpAuthenticated);
						
						//finally cleanup the configurations
						//UtilCommon.cleanUpFreemiumDataConfigs(delegator);
					} else {
						responseMsg = DataHelper.getErrorMsg(responseMap, responseStr, "Error occurred while athenticating customer details");
						resultString = EventResponse.ERROR;
					}
				} else {
					responseMsg = DataHelper.getErrorMsg(responseMap, responseStr, "Error occurred while athenticating customer details");
					resultString = EventResponse.ERROR;
				}
			}else {
				responseMsg = "No Api Url configured";
				resultString = EventResponse.ERROR;
			}
		}catch (Exception e) {
			e.printStackTrace();
			responseMsg = e.getMessage();
			resultString = EventResponse.ERROR;
		}
		result.put("responseMsg", responseMsg);
		result.put("result", resultString);
		return doJSONResponse(response, result);
	}

	public static String getSkuDescriptionList(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String orderId = (String) request.getParameter("orderId");
		Map<String, Object> result = new LinkedHashMap<>();
		try {
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("skuNumber");
			fieldsToSelect.add("skuDescription");
			fieldsToSelect.add("smallImageUrl");
			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("RMST", "RmsTransactionMaster");
			dynamicViewEntity.addAlias("RMST", "skuNumber");
			dynamicViewEntity.addAlias("RMST", "skuDescription");
			dynamicViewEntity.addAlias("RMST", "orderId");

			dynamicViewEntity.addMemberEntity("PR", "Product");
			dynamicViewEntity.addAlias("PR", "smallImageUrl");
			dynamicViewEntity.addViewLink("RMST", "PR", Boolean.TRUE,
					ModelKeyMap.makeKeyMapList("skuNumber", "productId"));
			List<GenericValue> skuDescriptionList = EntityQuery.use(delegator).select(fieldsToSelect)
					.from(dynamicViewEntity).where(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId))
					.queryList();
			result.put("skuDescriptionList", skuDescriptionList);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return doJSONResponse(response, result);
	}
	
	public static String searchPromotions(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> context = UtilHttp.getCombinedMap(request);
		String searchText = (String)context.get("searchText");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> serviceContext = new HashMap<>();
		List<Map<String, String>> dataList = new ArrayList<>();
		try {
			if(UtilValidate.isNotEmpty(searchText)) {
				context.put("searchPromoKey", searchText);
				serviceContext.put("requestContext", context);
				serviceContext.put("userLogin", getUserLogin(request));

				Map<String, Object> result = dispatcher.runSync("loyalty.findPromotionList", serviceContext);
				if(ServiceUtil.isSuccess(result)) {
					for(GenericValue genericData : (List<GenericValue>)result.get("data")) {
						dataList.add(org.fio.admin.portal.util.DataUtil.convertGenericToMap(genericData));
					}
				}
			}

		}catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			return doJSONResponse(response, new ArrayList<Map<String,String>>());
		}
		return doJSONResponse(response, dataList);
	}
	
	public static String getPromoCampaign(HttpServletRequest request, HttpServletResponse response) {
	    Delegator delegator = (Delegator) request.getAttribute("delegator");
	    List<Map<String, String>> dataList = new ArrayList<>();
	    Map<String, Object> result = new HashMap<>();
	    Locale locale = UtilHttp.getLocale(request);
	    NumberFormat nf = NumberFormat.getInstance(locale);
	    Map<String, Object> context = UtilHttp.getCombinedMap(request);
	    String description = (String) context.get("description");
	    String productPromoId = (String) context.get("productPromoId");
	    List<GenericValue> productPromoCodeGroupList = null;
	    long start = System.currentTimeMillis();

	    try {
		String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);
		List<EntityCondition> entityConditionList = FastList.newInstance();
		if (UtilValidate.isNotEmpty(description))
		    entityConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("description", EntityOperator.LIKE, "%" + description + "%"),EntityOperator.OR, EntityCondition.makeCondition("productPromoCodeGroupId", EntityOperator.LIKE,"%" + description + "%")));
		if (UtilValidate.isNotEmpty(productPromoId))
		    entityConditionList.add(EntityCondition.makeCondition("productPromoId", productPromoId));

		// get the default general grid fetch limit
		GenericValue systemProperty = EntityQuery.use(delegator).select("systemPropertyValue").from("SystemProperty").where("systemResourceId", "general", "systemPropertyId", "fio.grid.fetch.limit").queryFirst();
		// set the page parameters
		int viewIndex = 0;
		try {
		    viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
		} catch (Exception e) {
		    viewIndex = 0;
		}
		result.put("viewIndex", Integer.valueOf(viewIndex));

		int fioGridFetch = UtilValidate.isNotEmpty(systemProperty)&& UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue"))? Integer.parseInt((String) systemProperty.getString("systemPropertyValue")): 1000;
		int viewSize = fioGridFetch;
		try {
		    viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
		} catch (Exception e) {
		    viewSize = fioGridFetch;
		}
		result.put("viewSize", Integer.valueOf(viewSize));

		int highIndex = 0;
		int lowIndex = 0;
		int resultListSize = 0;
		try {
		    // get the indexes for the partial list
		    lowIndex = viewIndex * viewSize + 1;
		    highIndex = (viewIndex + 1) * viewSize;
		    // set distinct on so we only get one row per order
		    // using list iterator
		    EntityListIterator pli = EntityQuery.use(delegator).from("ProductPromoCodeGroup").where(EntityCondition.makeCondition(entityConditionList, EntityOperator.AND)).orderBy("fromDate DESC").cursorScrollInsensitive().fetchSize(highIndex).distinct().cache(true).queryIterator();
		    // get the partial list for this page
		    productPromoCodeGroupList = pli.getPartialList(lowIndex, viewSize);
		    // attempt to get the full size
		    resultListSize = pli.getResultsSizeAfterPartialList();
		    // close the list iterator
		    pli.close();
		} catch (GenericEntityException e) {
		    String errMsg = "Error: " + e.toString();
		    Debug.logError(e, errMsg, MODULE);
		}

		if (UtilValidate.isNotEmpty(productPromoCodeGroupList)) {
		    for (GenericValue productPromoCodeGroup : productPromoCodeGroupList) {
			Map<String, String> data = org.fio.admin.portal.util.DataUtil.convertGenericToMap(productPromoCodeGroup);
			if (UtilValidate.isNotEmpty(productPromoCodeGroup.getString("fromDate"))) {
			    data.put("fromDate",org.fio.homeapps.util.UtilDateTime.timeStampToString(productPromoCodeGroup.getTimestamp("fromDate"),globalDateTimeFormat, TimeZone.getDefault(), null));
			}
			if (UtilValidate.isNotEmpty(productPromoCodeGroup.getString("thruDate"))) {
			    data.put("thruDate",org.fio.homeapps.util.UtilDateTime.timeStampToString(productPromoCodeGroup.getTimestamp("thruDate"),globalDateTimeFormat, TimeZone.getDefault(), null));
			}
			dataList.add(data);
		    }
		    result.put("highIndex", Integer.valueOf(highIndex));
		    result.put("lowIndex", Integer.valueOf(lowIndex));
		    result.put("responseMessage", "success");
		    result.put("list", dataList);
		}
		result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
		result.put("totalRecords", nf.format(resultListSize));
		result.put("recordCount", resultListSize);
		result.put("chunkSize", viewSize);
	    } catch (GenericEntityException e) {
		Debug.logError(e.getMessage(), MODULE);
		result.put("errorMessage", e.getMessage());
		result.put("responseMessage", "error");
		result.put("list", new ArrayList<Map<String, Object>>());
		return doJSONResponse(response, result);
	    }
	    long end = System.currentTimeMillis();
	    Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
	    result.put("timeTaken", (end - start) / 1000f);
	    result.put("list", dataList);
	    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
	    return doJSONResponse(response, result);
	}

	public static String searchQuotes(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, String>> dataList = new ArrayList<>();
		Map<String, Object> result = new HashMap<>();
		long start = System.currentTimeMillis();
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String) context.get("partyId");
		String oppoId = (String) context.get("oppoId");
		String externalLoginKey = (String) context.get("externalLoginKey");
		ResultSet rs = null;
		try {
			String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);
			String isEnableIUCInt = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IUC_INT_ENABLED");
			String iucUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IUC_URL");
			String token = org.fio.admin.portal.util.UtilCommon.getSSOToken(delegator, getUserLogin(request));
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				Debug.logError("error parsing view_index", MODULE);
			}
			result.put("viewIndex", Integer.valueOf(viewIndex));
			int fioGridFetch = DataUtil.defaultFioGridfetchLimit(delegator);
			int viewSize = fioGridFetch;
			try {
				viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
			} catch (Exception e) {
				Debug.logError("error parsing view_size", globalDateTimeFormat);;
			}
			result.put("viewSize", Integer.valueOf(viewSize));
			int highIndex = 0;
			int lowIndex = 0;
			int resultListSize = 0;
			SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
			Connection con = (Connection)sqlProcessor.getConnection();
			try {
				// get the indexes for the partial list
				lowIndex = viewIndex * viewSize;
				highIndex = (viewIndex + 1) * viewSize;
				String query = "SELECT q.quote_id, q.quote_name, (GET_QUOTE_ITEM_TOTAL(q.quote_id) + GET_QUOTE_ADJUSTMENT_TOTAL(q.quote_id)) AS total_amount,"
						+ " q.issue_date,q.valid_thru_date,q.valid_from_date,q.sales_channel_enum_id,q.status_id,si.description,q.party_id";
				query += " FROM quote q INNER JOIN status_item si ON si.status_id = q.status_id ";
				if(UtilValidate.isNotEmpty(oppoId))
					query += " INNER JOIN quote_attribute qa ON qa.attr_name = 'OPPO_ID' AND q.quote_id=qa.quote_id AND qa.attr_value = '"+oppoId+"'";
				if(UtilValidate.isNotEmpty(partyId))
					query += " WHERE party_id = '"+partyId+"'";
				query += "ORDER BY q.valid_from_date DESC LIMIT "+lowIndex+","+viewSize;
				rs = sqlProcessor.executeQuery(query);

				if(rs != null) {
					while(rs.next()) {
						Map<String, String> data = new HashMap<>();
						data.put("quoteId", rs.getString("quote_id"));
						data.put("quoteName", rs.getString("quote_name"));
						data.put("quoteIdName", DataUtil.combineValueKey(rs.getString("quote_name"), rs.getString("quote_id")));
						data.put("amount", rs.getString("total_amount"));
						data.put("status", rs.getString("description"));
						data.put("salesChannelEnumId", rs.getString("sales_channel_enum_id"));
						if (UtilValidate.isNotEmpty(rs.getString("issue_date"))) 
							data.put("issueDate",org.fio.homeapps.util.UtilDateTime.timeStampToString(rs.getTimestamp("issue_date"),globalDateTimeFormat, TimeZone.getDefault(), null));
						if (UtilValidate.isNotEmpty(rs.getString("valid_thru_date"))) 
							data.put("validThruDate",org.fio.homeapps.util.UtilDateTime.timeStampToString(rs.getTimestamp("valid_thru_date"),globalDateTimeFormat, TimeZone.getDefault(), null));
						if (UtilValidate.isNotEmpty(rs.getString("valid_from_date"))) 
							data.put("validFromDate",org.fio.homeapps.util.UtilDateTime.timeStampToString(rs.getTimestamp("valid_from_date"),globalDateTimeFormat, TimeZone.getDefault(), null));
						String quoteDetailUrl = "#";
						if (UtilValidate.isNotEmpty(isEnableIUCInt) && isEnableIUCInt.equals("Y")) {
							quoteDetailUrl = iucUrl + "sales/control/ViewQuote?quoteId="+data.get("quoteId")+"&token="+token;
						}
						data.put("quoteDetailUrl", quoteDetailUrl);
						dataList.add(data);
					}
					result.put("highIndex", Integer.valueOf(highIndex));
					result.put("lowIndex", Integer.valueOf(lowIndex));
					result.put("responseMessage", "success");
					result.put("list", dataList);
				}
			} catch (GenericEntityException e) {
				String errMsg = "Error: " + e.toString();
				Debug.logError(e, errMsg, MODULE);
			}finally {
				rs.close();
				con.close();
				sqlProcessor.close();
			}
			result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			result.put("totalRecords", nf.format(resultListSize));
			result.put("recordCount", resultListSize);
			result.put("chunkSize", viewSize);
		}catch(Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			result.put("errorMessage", e.getMessage());
			result.put("responseMessage", "error");
			result.put("list", new ArrayList<Map<String, Object>>());
			return doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static String getEnumerationsByType(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		List < Map < String, Object >> results = new ArrayList <> ();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String,Object> inMap = FastMap.newInstance();
		String enumTypeId = (String) request.getParameter("enumTypeId");
		String parentEnumId = (String) request.getParameter("parentEnumId");
		GenericValue userLogin=getUserLogin(request);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<GenericValue> enumeration = null;
		try {
			List condList = FastList.newInstance();
			
			if(UtilValidate.isNotEmpty(enumTypeId)){
				condList.add(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,enumTypeId));
				if(UtilValidate.isNotEmpty(parentEnumId)){
					condList.add(EntityCondition.makeCondition("parentEnumId",EntityOperator.EQUALS,parentEnumId));
				}
				if(UtilValidate.isNotEmpty(parentEnumId)){
					condList.add(EntityCondition.makeCondition("parentEnumId",EntityOperator.EQUALS,parentEnumId));
				}
				condList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS,"Y"),
						EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, null)
						)); 
				EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
				enumeration = delegator.findList("Enumeration", cond, UtilMisc.toSet("enumId","enumCode","description","name"), UtilMisc.toList("sequenceId"), null, false);
			}
		} catch (Exception e) {
			result.put("data", FastList.newInstance());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		result.put("data", enumeration);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Success");
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String getOpportunityStatistics(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		String requestUri = request.getParameter("requestUri");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String statusOpen = (String) context.get("statusOpen");
		String statusClosed = (String) context.get("statusClosed");
		String externalKey = (String) context.get("externalLoginKey");
		String partyId = request.getParameter("partyId");

		String salesOpportunityTypeId = (String) context.get("salesOpportunityTypeId");

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		long totalOppCount=0;
		long totalWonOppCount=0;
		long totalLostOppCount=0;
		long totalOpenOppCount=0;
		long totalOppEstAmount=0;
		long OppWonPercent=0;
		long OppLossPercent=0;

		double totalLostAmount=0;
		double totalWonAmount =0;
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			ResultSet rs = null;
			if (UtilValidate.isNotEmpty(partyId)) {
				String oppTypeEnabled = org.groupfio.common.portal.util.UtilOpportunity.isOpportunityTypeEnabled(delegator);
				SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
				PreparedStatement totalOppCountSqlPS = null;
				PreparedStatement totalWonOppCountSqlPS = null;
				PreparedStatement totalLostOppCountSqlPS = null;
				PreparedStatement totalOpenOppCountSqlPS = null;
				PreparedStatement totalOppEstAmountSqlPS = null;
				PreparedStatement totalOwnOppEstAmountSqlPS = null;
				PreparedStatement totalLostOppEstAmountSqlPS = null;
				List<Object> values = new ArrayList<>();
				List<Object> valuesTotalOpenOppCount = new ArrayList<>();

				String totalOppCountSql = "SELECT COUNT(*) as 'totalOppCount' FROM `sales_opportunity` WHERE  `party_id` = ?";
				String totalWonOppCountSql = "SELECT COUNT(*) as 'totalWonOppCount' FROM `sales_opportunity` WHERE  `party_id` = ? AND `opportunity_stage_id` ='SOSTG_WON' AND OPPORTUNITY_STATUS_ID!='OPPO_VOID' ";
				String totalLostOppCountSql = "SELECT COUNT(*) as 'totalLostOppCount' FROM `sales_opportunity` WHERE  `party_id` = ? AND `opportunity_stage_id` ='SOSTG_LOST' AND OPPORTUNITY_STATUS_ID!='OPPO_VOID' ";
				String totalOpenOppCountSql = "SELECT COUNT(*) as 'totalOpenOppCount' FROM `sales_opportunity` WHERE  `party_id` = ? AND `opportunity_status_id` ='OPPO_OPEN'";
				String totalOppEstAmountSql = "SELECT SUM(estimated_amount) as 'totalOppEstAmount' FROM `sales_opportunity` WHERE  `party_id` = ?";
				String totalOwnOppEstAmountSql = "SELECT SUM(estimated_amount) as 'totalOwnOppEstAmount' FROM `sales_opportunity` WHERE  `party_id` = ? AND `opportunity_stage_id` ='SOSTG_WON'";
				String totalLostOppEstAmountSql = "SELECT SUM(estimated_amount) as 'totalLostOppEstAmount' FROM `sales_opportunity` WHERE  `party_id` = ? AND `opportunity_stage_id` ='SOSTG_LOST'";

				values.add(partyId);
				valuesTotalOpenOppCount.add(partyId);

				if (UtilValidate.isNotEmpty(salesOpportunityTypeId)) {
					totalOppCountSql+=" AND SALES_OPPORTUNITY_TYPE_ID = ?";
					totalWonOppCountSql+=" AND SALES_OPPORTUNITY_TYPE_ID = ?";
					totalLostOppCountSql+=" AND SALES_OPPORTUNITY_TYPE_ID = ?";
					totalOpenOppCountSql+=" AND SALES_OPPORTUNITY_TYPE_ID = ?";
					totalOppEstAmountSql+=" AND SALES_OPPORTUNITY_TYPE_ID = ?";
					totalOwnOppEstAmountSql+=" AND SALES_OPPORTUNITY_TYPE_ID = ?";
					totalLostOppEstAmountSql+=" AND SALES_OPPORTUNITY_TYPE_ID = ?";

					values.add(salesOpportunityTypeId);
					valuesTotalOpenOppCount.add(salesOpportunityTypeId);
				}
				if (UtilValidate.isNotEmpty(statusOpen)) {
					totalOppCountSql+=" AND opportunity_status_id = ?";
					totalWonOppCountSql+=" AND opportunity_status_id = ?";
					totalLostOppCountSql+=" AND opportunity_status_id = ?";
					totalOppEstAmountSql+=" AND opportunity_status_id = ?";
					totalOwnOppEstAmountSql+=" AND opportunity_status_id = ?";
					totalLostOppEstAmountSql+=" AND opportunity_status_id = ?";

					values.add(statusOpen);
				}
				if (UtilValidate.isNotEmpty(statusClosed)) {
					totalOppCountSql+=" AND opportunity_status_id = ?";
					totalWonOppCountSql+=" AND opportunity_status_id = ?";
					totalLostOppCountSql+=" AND opportunity_status_id = ?";
					totalOppEstAmountSql+=" AND opportunity_status_id = ?";
					totalOwnOppEstAmountSql+=" AND opportunity_status_id = ?";
					totalLostOppEstAmountSql+=" AND opportunity_status_id = ?";

					values.add(statusClosed);
				}

				rs = QueryUtil.getResultSet(totalOppCountSql, values, delegator);
				if (rs != null) {
					while (rs.next()) {
						totalOppCount = rs.getLong("totalOppCount");
					}
				}

				rs = QueryUtil.getResultSet(totalWonOppCountSql, values, delegator);
				if (rs != null) {
					while (rs.next()) {
						totalWonOppCount = rs.getLong("totalWonOppCount");
					}
				}
				rs = QueryUtil.getResultSet(totalLostOppCountSql, values, delegator);
				if (rs != null) {
					while (rs.next()) {
						totalLostOppCount = rs.getLong("totalLostOppCount");
					}
				}
				rs = QueryUtil.getResultSet(totalOpenOppCountSql, valuesTotalOpenOppCount, delegator);
				if (rs != null) {
					while (rs.next()) {
						totalOpenOppCount = rs.getLong("totalOpenOppCount");
					}
				}
				rs = QueryUtil.getResultSet(totalOppEstAmountSql, values, delegator);
				if (rs != null) {
					while (rs.next()) {
						totalOppEstAmount = rs.getLong("totalOppEstAmount");
					}
				}
				rs = QueryUtil.getResultSet(totalOwnOppEstAmountSql, values, delegator);
				if (rs != null) {
					while (rs.next()) {
						totalWonAmount = rs.getDouble("totalOwnOppEstAmount");
					}
				}
				rs = QueryUtil.getResultSet(totalLostOppEstAmountSql, values, delegator);
				if (rs != null) {
					while (rs.next()) {
						totalLostAmount = rs.getDouble("totalLostOppEstAmount");
					}
				}
				sqlProcessor.close();

				double wonPerct = 0.0;
				double lostPerct = 0.0;
				String wonPerctStr = "0";
				String lostPerctStr = "0";

				if(totalWonOppCount>0) {
					wonPerct = ((Double.valueOf(""+totalWonOppCount)/Double.valueOf(""+(totalWonOppCount+totalLostOppCount))) * 100);
					DecimalFormat myFormatter = new DecimalFormat("###.##");
					wonPerctStr = myFormatter.format(wonPerct);
				}
				if(totalLostOppCount>0) {
					lostPerct = ((Double.valueOf(""+totalLostOppCount) /Double.valueOf(""+(totalWonOppCount+totalLostOppCount))) * 100);
					DecimalFormat myFormatter = new DecimalFormat("###.##");
					lostPerctStr = myFormatter.format(lostPerct);
				}
				String defaultCurrencyUom = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_CURRENCY_UOM","USD");
				Locale local = Locale.getDefault();
				String totalOppEstAmountStr = UtilFormatOut.formatCurrency(new BigDecimal(totalOppEstAmount), defaultCurrencyUom, local);

				String totalWonAmountStr = UtilFormatOut.formatCurrency(new BigDecimal(totalWonAmount), defaultCurrencyUom, local);

				String totalLostAmountStr = UtilFormatOut.formatCurrency(new BigDecimal(totalLostAmount), defaultCurrencyUom, local);

				data.put("totalOppCount", totalOppCount);
				data.put("totalWonOppCount", totalWonOppCount);
				data.put("totalLostOppCount", totalLostOppCount);
				data.put("totalOpenOppCount", totalOpenOppCount);
				data.put("totalOppEstAmount", totalOppEstAmountStr);
				data.put("OppWonPercent", wonPerctStr);
				data.put("OppLossPercent", lostPerctStr);

				data.put("totalWonAmount", totalWonAmountStr);
				data.put("totalLostAmount", totalLostAmountStr);
			}


		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.put("data", data);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			return doJSONResponse(response, result);
		}

		result.put("list",data);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);

	}

	public static String getAllAWSInstances(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		List list = new ArrayList<>();
		String resultString = null;
		try {
			//String apiEndpoint = "https://6itzhn0fzg.execute-api.us-east-1.amazonaws.com/dev/all_instances";
			String awsEc2Url = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "AWS_EC2_URL");
			if(UtilValidate.isNotEmpty(awsEc2Url)) {
				String apiEndpoint = awsEc2Url+"/all_instances";
				try {
					String urlString = apiEndpoint;
	            	urlString = urlString.replaceAll(" ", "%20");
	            	
					URL url = new URL(urlString);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					int responseCode = connection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {
						BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String inputLine;
						StringBuilder apiResponse = new StringBuilder();
						while ((inputLine = in.readLine()) != null) {
							apiResponse.append(inputLine);
						}
						in.close();
						resultString =  apiResponse.toString();
						JSONObject obj = JSONObject.fromObject(resultString);
						if(UtilValidate.isNotEmpty(obj)) {
							JSONObject body =  (JSONObject) obj.get("body");
							if(UtilValidate.isNotEmpty(body)) {
								JSONArray instances = (JSONArray) body.get("Instances");
								if(UtilValidate.isNotEmpty(instances)) {
									Iterator<JSONObject> itr  = instances.iterator();
									while(itr.hasNext()) {
										JSONObject instance = itr.next();
										Map<String,Object> dataMap = new HashMap<>();
										JSONObject tags = instance.getJSONObject("Tags");
										dataMap.put("name", tags.get("Name"));
										dataMap.put("instanceId", instance.getString("InstanceId"));
										dataMap.put("instanceState", instance.getString("State"));
										list.add(dataMap);
									}
								}
							}
						}
					} else {
						Debug.logError("Failed to retrieve data. Response code: " + responseCode, MODULE);
					}
					connection.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			result.put("list",list);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return doJSONResponse(response, result);
	}

	public static String updateAWSInstancesState(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String instanceId = (String) request.getParameter("instanceId");
		String state = (String) request.getParameter("state");
		List<String> instanceIds = UtilCommon.getArrayToList(request.getParameter("instanceId"));
		String resultString = null;
		try {
			//String apiEndpoint = "https://6itzhn0fzg.execute-api.us-east-1.amazonaws.com/dev/"+state;
			String awsEc2Url = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "AWS_EC2_URL");
			if(UtilValidate.isNotEmpty(awsEc2Url)) {
				String apiEndpoint = awsEc2Url+"/"+state;
				JSONObject jsonPayload = new JSONObject();
				StringJoiner joiner = new StringJoiner(", ", "[", "]");
				for (String str : instanceIds) {
					joiner.add("\"" + str + "\"");
				}
				jsonPayload.put("instances", joiner.toString());
				Debug.logError(jsonPayload.toString(), MODULE);
				if(DataUtil.isValidUrl(apiEndpoint)){
					try {
						String urlString = apiEndpoint;
		            	urlString = urlString.replaceAll(" ", "%20");
						URL url = new URL(urlString);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						connection.setDoOutput(true);
						try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
							wr.write(jsonPayload.toString().getBytes(StandardCharsets.UTF_8));
							wr.flush();
						}
						int responseCode = connection.getResponseCode();
						if (responseCode == HttpURLConnection.HTTP_OK) {
							BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
							String inputLine;
							StringBuilder apiResponse = new StringBuilder();
							while ((inputLine = in.readLine()) != null) {
								apiResponse.append(inputLine);
							}
							in.close();
							resultString =  apiResponse.toString();
						} else {
							Debug.logError("Failed to retrieve data. Response code: " + responseCode, MODULE);
						}
						connection.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}else {
					 Debug.log("Invalid or unsafe URL.");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return doJSONResponse(response, resultString);
	}

	public static String getAllCommunicationHistory(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		List<Map<String,Object>> records = new LinkedList<Map<String,Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String custRequestId = (String) context.get("custRequestId");
		String limit = (String) context.get("limit");
		boolean limitRecords = false;
		Integer limitInt = 0;
		if(UtilValidate.isNotEmpty(limit)){
			try {
				limitInt = Integer.valueOf(limit);
				limitRecords = true;
			}catch(NumberFormatException n) {
				n.printStackTrace();;
			}
		}
		List<String> filters = org.fio.admin.portal.util.DataUtil.stringToList((String)context.get("filters"), ",");
		List<String> workEffortTypeIds = new ArrayList<>();

		if(UtilValidate.isNotEmpty(custRequestId)) {
			try {
				String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
				String includeCountryCode = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "INCLUDE_COUNTRY_CODE", "Y"); 
				List<GenericValue> communicationEventTypeList = EntityQuery.use(delegator).from("CommunicationEventType").select("communicationEventTypeId","description").queryList();
				Map<String,Object> communicationEventTypeMap = org.fio.admin.portal.util.DataUtil.getMapFromGeneric(communicationEventTypeList, "communicationEventTypeId", "description", false);
				List<GenericValue> custRequestWorkEffortList = EntityQuery.use(delegator).select("workEffortId").from("CustRequestWorkEffort").where("custRequestId", custRequestId).orderBy("workEffortId").queryList();

				if(UtilValidate.isEmpty(filters)){
					filters.add("ALL");
				}
				boolean addEmail = false;
				boolean addPhone = false;
				if(UtilValidate.isNotEmpty(filters) && filters.contains("ALL")) {
					addEmail = true;
					addPhone = true;
				}else {
					if(filters.contains("SMS_COMMUNICATION")) {
						addPhone = true;
					}
					if(filters.contains("EMAIL_COMMUNICATION")) {
						addEmail = true;
					}
				}
				if(addEmail) {
					workEffortTypeIds.add(org.fio.homeapps.util.DataUtil.getWorkEffortTypeId(delegator, "E-Mail",null));
					if(!filters.contains("EMAIL_COMMUNICATION")) {
						filters.add("EMAIL_COMMUNICATION");
					}
				}
				if(addPhone) {
					workEffortTypeIds.add(org.fio.homeapps.util.DataUtil.getWorkEffortTypeId(delegator, "Phone Call",null));
					if(!filters.contains("SMS_COMMUNICATION")) {
						filters.add("SMS_COMMUNICATION");
					}
				}
				Debug.logInfo("filters -->"+filters, MODULE);
				Debug.logInfo("workEffortTypeIds -->"+workEffortTypeIds, MODULE);
				if(UtilValidate.isNotEmpty(custRequestWorkEffortList)){

					List<String> custRequestWorkEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList, "workEffortId", true);
					Debug.logInfo("custRequestWorkEffortIds -->"+custRequestWorkEffortIds, MODULE);

					if(UtilValidate.isNotEmpty(custRequestWorkEffortIds)){

						List<EntityCondition> emailWorkEffortConditionList = FastList.newInstance();
						emailWorkEffortConditionList.add(EntityCondition.makeCondition("workEffortId",EntityOperator.IN, custRequestWorkEffortIds));
						emailWorkEffortConditionList.add(EntityCondition.makeCondition("workEffortTypeId",EntityOperator.IN, workEffortTypeIds));

						EntityCondition emailWorkEffortCondition = EntityCondition.makeCondition(emailWorkEffortConditionList, EntityOperator.AND);

						List<GenericValue> custRequestEmailWorkEffortList = EntityQuery.use(delegator).from("WorkEffort").where(emailWorkEffortCondition).queryList();
						Map <String, String> workEffortDirectionMap = new HashMap <String, String> ();

						List<String> custRequestEmailWorkEffortIds = EntityUtil.getFieldListFromEntityList(custRequestEmailWorkEffortList, "workEffortId", true);
						Debug.logInfo("custRequestEmailWorkEffortIds -->"+custRequestEmailWorkEffortIds, MODULE);

						if(UtilValidate.isNotEmpty(custRequestEmailWorkEffortList) && custRequestEmailWorkEffortList.size()>0){

							for(GenericValue eachCustReqWorkEffort : custRequestEmailWorkEffortList) {
								workEffortDirectionMap.put(eachCustReqWorkEffort.getString("workEffortId"), eachCustReqWorkEffort.getString("direction"));
							}

							if(UtilValidate.isNotEmpty(custRequestEmailWorkEffortIds)){

								List<GenericValue> communicationEventWorkEffList = EntityQuery.use(delegator).from("CommunicationEventWorkEff").where(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, custRequestEmailWorkEffortIds)).queryList();

								Map <String, String> communicationDirectionMap = new HashMap <String, String> ();

								if(UtilValidate.isNotEmpty(communicationEventWorkEffList)){

									for(GenericValue eachCommWorkEffort :communicationEventWorkEffList){
										communicationDirectionMap.put(eachCommWorkEffort.getString("communicationEventId"), workEffortDirectionMap.get(eachCommWorkEffort.getString("workEffortId")));
									}

									List<String> emailCommunicationEventIds = EntityUtil.getFieldListFromEntityList(communicationEventWorkEffList, "communicationEventId", true);
									Debug.logInfo("emailCommunicationEventIds -->"+emailCommunicationEventIds, MODULE);

									if(UtilValidate.isNotEmpty(emailCommunicationEventIds)){

										List<EntityCondition> emailCommunicationEventConditionList = FastList.newInstance();
										emailCommunicationEventConditionList.add(EntityCondition.makeCondition("communicationEventId",EntityOperator.IN, emailCommunicationEventIds));
										emailCommunicationEventConditionList.add(EntityCondition.makeCondition("communicationEventTypeId",EntityOperator.IN,filters));

										EntityCondition emailCommunicationEventCondition = EntityCondition.makeCondition(emailCommunicationEventConditionList, EntityOperator.AND);

										List<GenericValue> communicationEventList = null;
										if(limitRecords) {
											communicationEventList = EntityQuery.use(delegator).from("CommunicationEvent").where(emailCommunicationEventCondition).orderBy("-entryDate").maxRows(limitInt).queryList();
										}else {
											communicationEventList = EntityQuery.use(delegator).from("CommunicationEvent").where(emailCommunicationEventCondition).orderBy("-entryDate").queryList();
										}
										Debug.logInfo("communicationEventList.size() -->"+communicationEventList.size(), MODULE);

										List<Map<String,Object>> communicationHistoryList = new ArrayList<>();

										if(UtilValidate.isNotEmpty(communicationEventList)){

											for(GenericValue eachCommunicationEvent : communicationEventList){

												Map <String, Object> communicationHistoryMap = new HashMap <>();
												String commEventId = eachCommunicationEvent.getString("communicationEventId");
												String communicationEventTypeDesc = (String) communicationEventTypeMap.get(eachCommunicationEvent.getString("communicationEventTypeId"));
												communicationHistoryMap.put("communicationEventTypeDesc",communicationEventTypeDesc);

												List<GenericValue> commEventContentAssocList = EntityQuery.use(delegator).from("CommEventContentAssoc").where(EntityCondition.makeCondition("communicationEventId", EntityOperator.EQUALS, commEventId)).queryList();

												if(UtilValidate.isNotEmpty(commEventContentAssocList)){
													communicationHistoryMap.put("isAttachment","Y");
													Map<String,Object> attachmentsResult = dispatcher.runSync("common.getFileContentData", UtilMisc.toMap("requestContext",UtilMisc.toMap("workEffortId", null, "communicationEventId", commEventId),"userLogin", userLogin));
													if(ServiceUtil.isSuccess(attachmentsResult)) {
														Map resultMap = (Map) attachmentsResult.get("resultMap");
														List<Map> fileContents = (List<Map>) resultMap.get("fileContents");
														if(UtilValidate.isNotEmpty(fileContents)) {
															communicationHistoryMap.put("fileContents",fileContents);
														}
													}
												}else{
													communicationHistoryMap.put("isAttachment","N");
												}

												String fromPartyName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, eachCommunicationEvent.getString("partyIdFrom"), false);

												communicationHistoryMap.put("direction", communicationDirectionMap.get(eachCommunicationEvent.getString("communicationEventId")));
												communicationHistoryMap.put("eventId", eachCommunicationEvent.getString("communicationEventId"));

												GenericValue CommunicationEventWorkEff = EntityUtil.getFirst(delegator.findByAnd("CommunicationEventWorkEff", UtilMisc.toMap("communicationEventId", eachCommunicationEvent.getString("communicationEventId")), null, false));

												if(UtilValidate.isNotEmpty(CommunicationEventWorkEff)){
													String workEffortId = CommunicationEventWorkEff.getString("workEffortId");
													if(UtilValidate.isNotEmpty(workEffortId)){
														GenericValue workEffortInfo = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryOne();
														String createdByUserLogin = workEffortInfo.getString("createdByUserLogin");

														GenericValue UserLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId",workEffortInfo.get("createdByUserLogin")).queryOne();
														if(UtilValidate.isNotEmpty(UserLoginPerson) && UtilValidate.isNotEmpty(UserLoginPerson.getString("partyId"))){
															communicationHistoryMap.put("fromPartyName",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, UserLoginPerson.getString("partyId"), false));
														}
														if("EMAIL_COMMUNICATION".equals(eachCommunicationEvent.getString("communicationEventTypeId"))) {
															communicationHistoryMap.put("toPartyName", org.fio.homeapps.util.UtilActivity.getPartyNamesFromCommExtension(delegator, UtilMisc.toMap("workEffortId", workEffortId, "workExtName", "TO", "wftExtType", "TO_TYPE")));
														}else if("SMS_COMMUNICATION".equals(eachCommunicationEvent.getString("communicationEventTypeId"))) {
															List<String> toPhoneList = UtilCommon.getArrayToList(eachCommunicationEvent.getString("toData"));
															String fromPhone = eachCommunicationEvent.getString("fromData");
															String partyNames = "";
															String partyNamesPhone = "";
															List<String> nameMapList = new ArrayList<>();
															List<String> namePhoneMapList = new ArrayList<>();
															for(String toPhone : toPhoneList) {
																String partyId = org.fio.admin.portal.util.DataUtil.getPartyIdByPrmaryPhone(delegator, toPhone);
																String name = "";
																String namePhone = "";
																Map<String,Object> phoneSplit = org.fio.admin.portal.util.DataUtil.phoneNumberSplitter(toPhone);
																if(UtilValidate.isNotEmpty(phoneSplit.get("nationalNumber"))) {
																	namePhone = org.fio.admin.portal.util.DataUtil.formatPhoneNumber((String) phoneSplit.get("nationalNumber"));
																	if(UtilValidate.isNotEmpty(phoneSplit.get("countryCode"))) {
																		namePhone = "+"+phoneSplit.get("countryCode")+"-"+ namePhone;
																	}
																}else {
																	namePhone = org.fio.admin.portal.util.DataUtil.formatPhoneNumber(toPhone);
																}
																if(UtilValidate.isNotEmpty(partyId)) {
																	name = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyId, false);
																	namePhone = name + " "+ namePhone;
																}
																if(UtilValidate.isNotEmpty(name))
																	nameMapList.add(name);
																if(UtilValidate.isNotEmpty(namePhone)){
																	namePhoneMapList.add(namePhone);
																}
															}
															if("IN".equals(communicationHistoryMap.get("direction"))) {
																communicationHistoryMap.put("toPartyName", "System");
																String fromPartyId = org.fio.admin.portal.util.DataUtil.getPartyIdByPrmaryPhone(delegator, fromPhone);
																if(UtilValidate.isNotEmpty(fromPartyId)) {
																	communicationHistoryMap.put("fromPartyName",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, fromPartyId, false));
																}
															}else {
																communicationHistoryMap.put("toPartyName", StringUtil.join(nameMapList, ","));
															}
															communicationHistoryMap.put("toPartyNamePhone", StringUtil.join(namePhoneMapList, ","));
															String imgData = eachCommunicationEvent.getString("imgData");
															communicationHistoryMap.put("imgData", UtilValidate.isNotEmpty(imgData)?imgData:"");
														}
													}
												}

												//communicationHistoryMap.put("fromPartyName", org.fio.homeapps.util.PartyHelper.getPartyName(delegator, eachCommunicationEvent.get("partyIdFrom"), false));
												communicationHistoryMap.put("message", eachCommunicationEvent.getString("content"));
												communicationHistoryMap.put("entryDate", UtilValidate.isNotEmpty(eachCommunicationEvent.get("entryDate")) ? UtilDateTime.timeStampToString(eachCommunicationEvent.getTimestamp("entryDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
												records.add(communicationHistoryMap);
											}

										}
									}
								}
							}
						}
					}
				}

			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		result.put("records", records);
		return doJSONResponse(response, result);

	}
	
	public static String getUnassignSmsList(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		List<Map<String, Object>> dataList = new LinkedList<Map<String,Object>>();
		
		long start = System.currentTimeMillis();
		try {
			
			String fromData = (String) context.get("fromData");
			String toData = (String) context.get("toData");
			String direction = (String) context.get("direction");
			String workEffortId = (String) context.get("workEffortId");
			String filterBy = (String) context.get("filterBy");
			String filterType = (String) context.get("filterType");
			String externalLoginKey = (String) context.get("externalLoginKeyId");
			GenericValue systemProperty = EntityQuery.use(delegator)
					.select("systemPropertyValue")
					.from("SystemProperty")
					.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
					.queryFirst();
            // set the page parameters
	        int viewIndex = 0;
	        try {
	            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
	        } catch (Exception e) {
	            viewIndex = 0;
	        }
	        result.put("viewIndex", Integer.valueOf(viewIndex));

	        int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;
	        
	        int viewSize = fioGridFetch;
	        try {
	            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
	        } catch (Exception e) {
	            viewSize = fioGridFetch;
	        }
	        result.put("viewSize", Integer.valueOf(viewSize));
	        
			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("CEWE", "CommunicationEventWorkEff");
			dynamicViewEntity.addAlias("CEWE", "communicationEventId");
			dynamicViewEntity.addMemberEntity("CE", "CommunicationEvent");
			dynamicViewEntity.addAlias("CE", "communicationEventTypeId");
			dynamicViewEntity.addAlias("CE", "fromData");
			dynamicViewEntity.addAlias("CE", "toData");
			dynamicViewEntity.addAlias("CE", "content");
			dynamicViewEntity.addAlias("CE", "subject");
			dynamicViewEntity.addAlias("CE", "externalMsgId");
			dynamicViewEntity.addAlias("CE", "msgSendStatus");
			dynamicViewEntity.addAlias("CE", "msgSentTime");
			dynamicViewEntity.addAlias("CE", "direction");
			dynamicViewEntity.addAlias("CE", "lastUpdatedTxStamp");
			dynamicViewEntity.addAlias("CE", "createdTxStamp");
			dynamicViewEntity.addViewLink("CEWE", "CE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("communicationEventId"));
			dynamicViewEntity.addMemberEntity("WE", "WorkEffort");
			dynamicViewEntity.addAlias("WE", "workEffortId");
			dynamicViewEntity.addAlias("WE", "workEffortName");
			dynamicViewEntity.addAlias("WE", "domainEntityType");
			dynamicViewEntity.addAlias("WE", "domainEntityId");
			dynamicViewEntity.addViewLink("CEWE", "WE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
			
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("communicationEventId");fieldsToSelect.add("fromData");fieldsToSelect.add("toData");
			fieldsToSelect.add("content");fieldsToSelect.add("subject");fieldsToSelect.add("externalMsgId");
			fieldsToSelect.add("msgSendStatus");fieldsToSelect.add("msgSentTime");fieldsToSelect.add("direction");
			fieldsToSelect.add("workEffortId");fieldsToSelect.add("workEffortName");
			
			int highIndex = 0;
			int lowIndex = 0;
			long resultListSize = 0;
			 // get the indexes for the partial list
			//lowIndex = viewIndex * viewSize + 1;
			//highIndex = (viewIndex + 1) * viewSize;
			
			lowIndex = viewIndex * viewSize;
            highIndex = (viewIndex + 1) * viewSize;
            
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			if(UtilValidate.isNotEmpty(fromData))
				conditions.add(EntityCondition.makeCondition("fromData", EntityOperator.EQUALS, fromData));
			
			if(UtilValidate.isNotEmpty(toData))
				conditions.add(EntityCondition.makeCondition("toData", EntityOperator.EQUALS, toData));
			
			direction = UtilValidate.isNotEmpty(direction) ? direction : "IN";
			if(UtilValidate.isNotEmpty(direction)) {
				if("INOUT".equals(direction))
					conditions.add(EntityCondition.makeCondition("direction", EntityOperator.IN, UtilMisc.toMap("IN", "OUT")));
				else
					conditions.add(EntityCondition.makeCondition("direction", EntityOperator.EQUALS, direction));
			}
			if(UtilValidate.isNotEmpty(workEffortId))
				conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
			
			conditions.add(EntityCondition.makeCondition("communicationEventTypeId", EntityOperator.EQUALS, "SMS_COMMUNICATION"));
			
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "")
					));
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, "")
				));
			
			EntityCondition mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			/*
			EntityQuery entityQuery =  new EntityQuery(delegator);
			entityQuery.from(dynamicViewEntity);
			entityQuery.where(mainCond);
			entityQuery.limit(viewSize);
			entityQuery.offset(lowIndex);
			entityQuery.orderBy("lastUpdatedTxStamp DESC");
			
			List<GenericValue> unassignedSmsList = entityQuery.queryList();
			if(UtilValidate.isNotEmpty(unassignedSmsList)) {
				Map<String, Object> data = new HashMap<String, Object>();
				for(GenericValue unassignSms : unassignedSmsList) {
					data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(unassignSms));
					dataList.add(data);
				}
				result.put("list", dataList);
			}*/

			List<GenericValue> resultList = EntityQuery.use(delegator).select(fieldsToSelect)
					.limit(viewSize)
					.offset(lowIndex)
					.from(dynamicViewEntity).where(mainCond)
					.cache(true).orderBy("lastUpdatedTxStamp DESC").queryList();
			
			resultListSize = QueryUtil.findCountByCondition(delegator, dynamicViewEntity, 
					mainCond, null, null, null, UtilMisc.toMap("totalCount", resultList.size(), 
							"fioGridFetch", fioGridFetch));

			if(UtilValidate.isNotEmpty(resultList)) {
				for(GenericValue unassignSms : resultList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(unassignSms));
					data.put("externalLoginKey", externalLoginKey);
					dataList.add(data);
				}
				result.put("list", dataList);
			}

			result.put("viewIndex", Integer.valueOf(viewIndex));
			result.put("highIndex", Integer.valueOf(highIndex));
			result.put("lowIndex", Integer.valueOf(lowIndex));
			
			result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			result.put("totalRecords", nf.format(resultListSize));
			result.put("recordCount", resultListSize);
			result.put("chunkSize", viewSize); 
			
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("Error : "+e.getMessage());
		}
		
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		return doJSONResponse(response, result);
	}
	
	public static String getSmsDashboardCountList(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		List<Map<String, Object>> dataList = new LinkedList<Map<String,Object>>();
		
		long start = System.currentTimeMillis();
		try {
			
			String fromData = (String) context.get("fromData");
			String toData = (String) context.get("toData");
			String direction = (String) context.get("direction");
			String workEffortId = (String) context.get("workEffortId");
			String filterBy = (String) context.get("filterBy");
			String filterType = (String) context.get("filterType");
			
			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("CEWE", "CommunicationEventWorkEff");
			dynamicViewEntity.addAlias("CEWE", "communicationEventId");
			dynamicViewEntity.addMemberEntity("CE", "CommunicationEvent");
			dynamicViewEntity.addAlias("CE", "communicationEventTypeId");
			dynamicViewEntity.addAlias("CE", "fromData");
			dynamicViewEntity.addAlias("CE", "toData");
			dynamicViewEntity.addAlias("CE", "content");
			dynamicViewEntity.addAlias("CE", "subject");
			dynamicViewEntity.addAlias("CE", "externalMsgId");
			dynamicViewEntity.addAlias("CE", "msgSendStatus");
			dynamicViewEntity.addAlias("CE", "msgSentTime");
			dynamicViewEntity.addAlias("CE", "direction");
			dynamicViewEntity.addAlias("CE", "lastUpdatedTxStamp");
			dynamicViewEntity.addAlias("CE", "createdTxStamp");
			dynamicViewEntity.addViewLink("CEWE", "CE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("communicationEventId"));
			dynamicViewEntity.addMemberEntity("WE", "WorkEffort");
			dynamicViewEntity.addAlias("WE", "workEffortId");
			dynamicViewEntity.addAlias("WE", "workEffortName");
			dynamicViewEntity.addAlias("WE", "domainEntityType");
			dynamicViewEntity.addAlias("WE", "domainEntityId");
			dynamicViewEntity.addViewLink("CEWE", "WE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
			
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("communicationEventId");fieldsToSelect.add("fromData");fieldsToSelect.add("toData");
			fieldsToSelect.add("content");fieldsToSelect.add("subject");fieldsToSelect.add("externalMsgId");
			fieldsToSelect.add("msgSendStatus");fieldsToSelect.add("msgSentTime");fieldsToSelect.add("direction");
			fieldsToSelect.add("workEffortId");fieldsToSelect.add("workEffortName");
			
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			if(UtilValidate.isNotEmpty(fromData))
				conditions.add(EntityCondition.makeCondition("fromData", EntityOperator.EQUALS, fromData));
			
			if(UtilValidate.isNotEmpty(toData))
				conditions.add(EntityCondition.makeCondition("toData", EntityOperator.EQUALS, toData));
			
			direction = UtilValidate.isNotEmpty(direction) ? direction : "IN";
			if(UtilValidate.isNotEmpty(direction)) {
				if("INOUT".equals(direction))
					conditions.add(EntityCondition.makeCondition("direction", EntityOperator.IN, UtilMisc.toMap("IN", "OUT")));
				else
					conditions.add(EntityCondition.makeCondition("direction", EntityOperator.EQUALS, direction));
			}
			if(UtilValidate.isNotEmpty(workEffortId))
				conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
			
			conditions.add(EntityCondition.makeCondition("communicationEventTypeId", EntityOperator.EQUALS, "SMS_COMMUNICATION"));
			
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "")
					));
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, "")
				));
			
			EntityCondition mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			long recordCount = EntityQuery.use(delegator).select(fieldsToSelect)
					.from(dynamicViewEntity).where(mainCond).queryCount();
			
			//result.put("recordCount", recordCount);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("barId", "unassigned_sms");
			data.put("count", recordCount);
			dataList.add(data);
			
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("Error : "+e.getMessage());
		}
		
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("list", dataList);
		return doJSONResponse(response, result);
	}
	
	public static String searchSrDetails(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		List<GenericValue> resultList = null;

		List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String fromPhoneNumber = (String) context.get("fromPhoneNumber");
		String externalLoginKey = (String) context.get("externalLoginKey");
		List<GenericValue> custRequestList = null;
		List<GenericValue> custRequestPartyList = null;
		List < EntityCondition > conditions = new ArrayList<EntityCondition>();
		try {
			String partyId = org.fio.admin.portal.util.DataUtil.getPartyIdByPrmaryPhone(delegator, fromPhoneNumber);
			if(UtilValidate.isNotEmpty(partyId)) {
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				
				dynamicView.addMemberEntity("CR", "CustRequest");
				dynamicView.addAlias("CR", "custRequestName");
				dynamicView.addAlias("CR", "responsiblePerson");
				dynamicView.addAlias("CR", "statusId");

				dynamicView.addMemberEntity("CRP", "CustRequestParty");
				dynamicView.addAlias("CRP", "custRequestId","custRequestId", null, Boolean.FALSE, Boolean.TRUE, null);
				dynamicView.addAlias("CRP", "partyId");
				dynamicView.addViewLink("CR", "CRP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));


				conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_WRK_COMPL")));
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				custRequestList = EntityQuery.use(delegator).from(dynamicView).where(condition).queryList();
				if(UtilValidate.isNotEmpty(custRequestList)) {
					for(GenericValue custRequest : custRequestList) {
						Map<String, Object> data = new HashMap<String, Object>();
						String custRequestId = (String) custRequest.get("custRequestId");
						String custRequestName = (String) custRequest.get("custRequestName");
						String responsiblePerson = (String) custRequest.get("responsiblePerson");
						String ownerPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, responsiblePerson);
						responsiblePerson = org.fio.homeapps.util.DataUtil.getUserLoginName(delegator, ownerPartyId);
						data.put("custRequestId", custRequestId);
						data.put("custRequestName", custRequestName);
						data.put("responsiblePerson", responsiblePerson);
						dataList.add(data);
						
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return doJSONResponse(response, result);
		}

		result.put("list", dataList);
		return doJSONResponse(response, result);
	}

	public static String domainAssignmentExt(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String domainEntityType = (String) context.get("domainEntityType");
		String workEffId = (String) context.get("workEffortId");
		String domainEntityId = (String) context.get("domainEntityId");
		String srNumber = (String) context.get("srNumber");
		String partyId = (String) context.get("partyId");
		String responseMsg = "Domain Entity Type has been assigned successfully";
		try {
			
			if(UtilValidate.isNotEmpty(workEffId) && UtilValidate.isNotEmpty(domainEntityId) && UtilValidate.isNotEmpty(domainEntityType)) {
				List<String> workEffortIdList = new ArrayList<String>();
				if(workEffId.contains(","))
					workEffortIdList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(workEffId, ","));
				else
					workEffortIdList.add(workEffId);
				
				for(String workEffortId : workEffortIdList) {
					GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryFirst();
					List<String> contentIds = new ArrayList<String>();
					if(UtilValidate.isNotEmpty(workEffort)) {
						GenericValue commEventWorkEff = EntityQuery.use(delegator).from("CommunicationEventWorkEff").where("workEffortId", workEffortId).queryFirst();
						if (UtilValidate.isNotEmpty(commEventWorkEff)) {
							String communicationEventId = commEventWorkEff.getString("communicationEventId");
							List<GenericValue> fileContents = EntityQuery.use(delegator).from("CommEventContentAssoc").where("communicationEventId", communicationEventId).filterByDate().queryList();
							contentIds = EntityUtil.getFieldListFromEntityList(fileContents, "contentId", true);
							
							if(UtilValidate.isNotEmpty(contentIds)) {
								for(String contentId : contentIds) {
									GenericValue workEffortContent = delegator.makeValue("WorkEffortContent");
									workEffortContent.set("workEffortId", workEffortId);
									workEffortContent.set("contentId", contentId);
									workEffortContent.set("workEffortContentTypeId", "EMAIL_ATTACHMENT_DATA");
									workEffortContent.set("fromDate", UtilDateTime.nowTimestamp());
									workEffortContent.create();
								}
							}
						}
						/*
						*/
						if(CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
							GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", domainEntityId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(workEffort)) {
								GenericValue custRequestWorkEffort = EntityQuery.use(delegator).from("CustRequestWorkEffort").where("custRequestId", domainEntityId, "workEffortId", workEffortId).queryFirst();
								if(UtilValidate.isEmpty(custRequestWorkEffort)) {
									custRequestWorkEffort = delegator.makeValue("CustRequestWorkEffort");
									custRequestWorkEffort.put("custRequestId", domainEntityId);
									custRequestWorkEffort.put("workEffortId", workEffortId);
									custRequestWorkEffort.create();
								}
								
								//Add domain type and id
								workEffort.set("domainEntityType", domainEntityType);
								workEffort.set("domainEntityId", domainEntityId);
								workEffort.store();
								
								//Trigger sr owner notification for sms received.
								Map<String, Object> srOwnerNotificationResMap = dispatcher.runSync("common.triggerSrSMSReceivedEmail", UtilMisc.toMap("userLogin", userLogin,"custRequestId",domainEntityId));
        						if(!ServiceUtil.isSuccess(srOwnerNotificationResMap)) {
        							Debug.logError("Sr owner notification failed....!!!", MODULE);
        						}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		request.setAttribute("_EVENT_MESSAGE_",responseMsg);
		return doJSONResponse(response, result);
	}

}
