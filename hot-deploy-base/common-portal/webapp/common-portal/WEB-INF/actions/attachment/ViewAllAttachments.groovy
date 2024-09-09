import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime
import org.ofbiz.base.component.ComponentConfig
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;

delegator = request.getAttribute("delegator");
GenericValue userLogin = request.getAttribute("userLogin");

uiLabelMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

loggedUserPartyId = userLogin.getString("partyId");
loggedUserId = userLogin.getString("userLoginId");
loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", loggedUserId);
context.put("loggedUserPartyId", loggedUserPartyId);

String domainEntityType = request.getParameter("domainEntityType");
String externalLoginKey = request.getParameter("externalLoginKey");
String domainEntityId = request.getParameter("domainEntityId");
String requestURI = request.getParameter("requestURI");
context.put("domainEntityType", domainEntityType);
context.put("domainEntityId", domainEntityId);

String domainEntityTypeDesc = "FSR";
if (UtilValidate.isNotEmpty(domainEntityType) && !domainEntityType.equals("SERVICE_REQUEST")) {
	domainEntityTypeDesc = org.groupfio.common.portal.util.DataHelper.convertToLabel(domainEntityType);
}
context.put("domainEntityTypeDesc", domainEntityTypeDesc);
context.put("domainEntityName", org.groupfio.common.portal.util.DataHelper.getDomainEntityName(delegator, domainEntityId, domainEntityType));

if(UtilValidate.isNotEmpty(requestURI) && requestURI.contains("client-portal")) {
	context.put("domainEntityLink","/client-portal/control/viewServiceRequest?srNumber="+domainEntityId+"&externalLoginKey="+externalLoginKey+"#sr-notes")
} else
	context.put("domainEntityLink", org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom(domainEntityId, domainEntityType, externalLoginKey));

String contentIdsList = request.getParameter("contentIdsList");
String custRequestId = request.getParameter("custRequestId");
if(UtilValidate.isEmpty(custRequestId)) {
	custRequestId = request.getParameter("domainEntityId");
}
List<EntityCondition> conditionlist = FastList.newInstance();
List<String> contentIds = new ArrayList<String>();
List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

conditionlist.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, domainEntityId));
conditionlist.add(EntityCondition.makeCondition("contentTypeId", EntityOperator.IN,UtilMisc.toList("SR_ATTACHMENT_DATA")));
conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

EntityCondition custReqCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
List<GenericValue> srContentDataList = delegator.findList("CustRequestContent", custReqCondition, null,null, null, false);
if (UtilValidate.isNotEmpty(srContentDataList)) {
contentIds = EntityUtil.getFieldListFromEntityList(srContentDataList, "contentId", true);
}

conditionlist.clear();
if (UtilValidate.isNotEmpty(contentIdsList)) {
	conditionlist.add(EntityCondition.makeCondition("contentId", EntityOperator.IN, Arrays.asList(contentIdsList.split(","))));
}else {
	conditionlist.add(EntityCondition.makeCondition("contentId", EntityOperator.IN, contentIds));
}

List<String> orderBy = UtilMisc.toList("createdTxStamp DESC");
EntityCondition Contentcondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
List<GenericValue> contentDataList = delegator.findList("Content", Contentcondition, null, orderBy, null, false);
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
String mountPointLoc = "/common-portal-resource/images/temp/" + loggedUserId + "/";
String bootstrapImageLoc = "/bootstrap/images/";
String commonPortalImageTempLoc = ComponentConfig.getRootLocation("common-portal") + "webapp" + mountPointLoc;

if (UtilValidate.isNotEmpty(contentDataList)) {
	for (GenericValue entry : contentDataList) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("contentId", entry.getString("contentId"));
		data.put("contentType", entry.getString("contentTypeId"));
		data.put("contentName", entry.getString("contentName"));
		data.put("description", entry.getString("description"));
		data.put("createdDate",UtilValidate.isNotEmpty(entry.get("createdDate"))? UtilDateTime.timeStampToString(entry.getTimestamp("createdDate"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
		String dataResourceId = entry.getString("dataResourceId");
		if (UtilValidate.isNotEmpty(dataResourceId)) {
			GenericValue dataResource = EntityQuery.use(delegator).from("DataResource").where("dataResourceId", dataResourceId).queryFirst();
			if (UtilValidate.isNotEmpty(dataResource)) {
				String filePath = dataResource.getString("objectInfo");
				File image = new File(filePath);
				String fileName = image.isFile() ? image.getName() : "";
				String fileExtension = org.fio.admin.portal.util.DataUtil.getFileExtension(fileName);
				if (nonImageFormat.contains(fileExtension)) {
					data.put("imageUrl", bootstrapImageLoc + fileExtension + ".png");
				} else if (image.exists()) {
					String fileRelativePath = commonPortalImageTempLoc + dataResourceId+"."+fileExtension;
					File desImage = new File(fileRelativePath);
					FileUtils.copyFile(image, desImage);
					data.put("imageUrl", mountPointLoc+ dataResourceId+"."+fileExtension);
				} else {
					data.put("imageUrl", bootstrapImageLoc + "default-product-img.png");
				}
			}
		}
		dataList.add(data);
	}
}

String attachmentCount = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "VIEW_ALL_ATTACHMENT_COUNT","5");

context.put("attachmentList", dataList);
context.put("attachmentCount", Integer.parseInt(attachmentCount));

