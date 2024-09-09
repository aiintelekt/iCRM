/**
 * 
 */
package org.groupfio.customfield.service.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.customfield.service.CustomfieldServiceConstants;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Sharif
 *
 */
public class SegmentServices {

	private static final String MODULE = SegmentServices.class.getName();
	public static final String resource = "CustomFieldUiLabels";
	
	public static Map autoCampaignSegmentation(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String offerCodeCount = (String) context.get("offerCodeCount");
		
		Boolean isCampaignTypeChanged = (Boolean) context.get("isCampaignTypeChanged");
		String oldCampaignType = (String) context.get("oldCampaignType");
		
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("marketingCampaignId", marketingCampaignId);

		try {

			GenericValue campaign = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaign",UtilMisc.toMap("marketingCampaignId", marketingCampaignId), null, false) );

			if (UtilValidate.isNotEmpty(campaign)) {
				
				if (UtilValidate.isNotEmpty(isCampaignTypeChanged) && isCampaignTypeChanged && UtilValidate.isNotEmpty(oldCampaignType)) {
					
					String segmentCodeId = oldCampaignType + "_" + campaign.getString("marketingCampaignId");
					
					Map<String, Object> segmentCodeContext = new HashMap<String, Object>();
					segmentCodeContext.put("groupId", segmentCodeId);
					segmentCodeContext.put("userLogin", userLogin);

					Map<String, Object> segmentCodeResult = dispatcher.runSync("segment.deleteSegmentCode", segmentCodeContext);
					if (!ServiceUtil.isError(segmentCodeResult)) {
						Debug.logInfo("Successfully delete segment code during autoCampaignSegmentation: ", MODULE);
					}
				}

				GenericValue campaignAction = EntityUtil.getFirst( delegator.findByAnd("CustomFieldCampaignAction",UtilMisc.toMap("campaignTypeId", campaign.getString("campaignTypeId")), null, false) );

				if (UtilValidate.isNotEmpty(campaignAction)) {
					Map<String, Object> segmentCodeContext = new HashMap<String, Object>();

					String segmentCodeId = campaign.getString("campaignTypeId") + "_" + campaign.getString("marketingCampaignId");

					segmentCodeContext.put("groupId", segmentCodeId);
					segmentCodeContext.put("groupName", "AGS: "+campaign.getString("campaignName"));
					segmentCodeContext.put("valueCapture", "SINGLE");
					segmentCodeContext.put("groupingCode", CustomfieldServiceConstants.GROUPING_CODE_CAMPAIGN.get(campaign.getString("campaignTypeId")));

					segmentCodeContext.put("serviceTypeId", campaignAction.getString("serviceTypeId"));
					segmentCodeContext.put("serviceConfigId", campaignAction.getString("serviceConfigId"));

					segmentCodeContext.put("userLogin", userLogin);

					Map<String, Object> segmentCodeResult = dispatcher.runSync("segment.createSegmentCode", segmentCodeContext);

					if (!ServiceUtil.isError(segmentCodeResult)) {

						// create default role association [start]

						Map<String, Object> roleContext = new HashMap<String, Object>();

						roleContext.put("groupId", segmentCodeId);
						if(UtilValidate.isNotEmpty(campaign.getString("roleTypeId"))){
							String roleTypeId=campaign.getString("roleTypeId");
							if("ACCOUNT".equals(roleTypeId)){
								roleTypeId="CONTACT";
							}
							roleContext.put("roleTypeId", roleTypeId);
							roleContext.put("sequenceNumber", "1");
							SegmentServices.createRoleConfig(delegator, roleContext);

						}else{/*
							roleContext.put("roleTypeId", "ACCOUNT");
							roleContext.put("sequenceNumber", "1");
							SegmentService.createRoleConfig(delegator, roleContext);

							roleContext.put("roleTypeId", "CONTACT");
							roleContext.put("sequenceNumber", "1");
							SegmentService.createRoleConfig(delegator, roleContext);

							roleContext.put("roleTypeId", "LEAD");
							roleContext.put("sequenceNumber", "2");
							SegmentService.createRoleConfig(delegator, roleContext);
						*/}
						// create default role association [end]

						List<GenericValue> campaignActionTypes = delegator.findByAnd("CustomFieldCampaignActionType", UtilMisc.toMap("campaignTypeId", campaign.getString("campaignTypeId")), UtilMisc.toList("sequenceNumber"), false);
						Long sequenceNumber = new Long(1);
						for (GenericValue campaignActionType : campaignActionTypes) {

							Map<String, Object> segmentValueContext = new HashMap<String, Object>();

							String segmentValueId = segmentCodeId + "_" + campaignActionType.getString("actionType");

							segmentValueContext.put("groupId", segmentCodeId);
							segmentValueContext.put("customFieldId", segmentValueId);
							segmentValueContext.put("customFieldName", campaignActionType.getString("actionName"));
							segmentValueContext.put("valueCapture", "SINGLE");
							segmentValueContext.put("valueData", segmentValueId);
							segmentValueContext.put("isEnabled", "Y");
							segmentValueContext.put("sequenceNumber", ""+sequenceNumber);

							segmentValueContext.put("userLogin", userLogin);

							Map<String, Object> segmentValueResult = dispatcher.runSync("segment.createSegmentValue", segmentValueContext);
							if (!ServiceUtil.isError(segmentValueResult)) {
								Debug.logInfo("auto generated segment value: "+segmentValueId, MODULE);
								sequenceNumber++;
							}

						}
						if(UtilValidate.isNotEmpty(offerCodeCount)){
							int offerCodeCountInt = Integer.parseInt(offerCodeCount);
							if(offerCodeCountInt > 0){
								Map<String, Object> segmentValueOfferContext = new HashMap<String, Object>();
								segmentValueOfferContext.put("groupId", segmentCodeId);
								segmentValueOfferContext.put("offerCodeCount", offerCodeCount);
								segmentValueOfferContext.put("marketingCampaignId", marketingCampaignId);
								segmentValueOfferContext.put("sequenceNumber", sequenceNumber);
								segmentValueOfferContext.put("userLogin", userLogin);
								Map<String, Object> segmentValueOfferResult = dispatcher.runSync("segment.offerCreateSegmentValue", segmentValueOfferContext);
								if (!ServiceUtil.isError(segmentValueOfferResult)) {
									Debug.logInfo("auto generated offer segment value: ", MODULE);
								}

							}
						}

					}
				}
			} else {
				result.putAll(ServiceUtil.returnSuccess("Campaign not found for auto segmentation with CampaignId: "+marketingCampaignId));
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnSuccess(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully auto campaign segmentation.."));

		return result;

	}

	public static Map offerCreateSegmentValue(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String groupId = (String) context.get("groupId");
		String offerCodeCount = (String) context.get("offerCodeCount");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		Long sequenceNumber= (Long) context.get("sequenceNumber");
		int offerCodeCountInt = Integer.parseInt(offerCodeCount);
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("marketingCampaignId", marketingCampaignId);

		try {

			GenericValue campaign = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaign",UtilMisc.toMap("marketingCampaignId", marketingCampaignId), null, false) );

			if (UtilValidate.isNotEmpty(campaign)) {
				if(sequenceNumber == 0){
					sequenceNumber = new Long(1);
				}

				if(offerCodeCountInt > 0){
					for (int offSeg=1;offSeg<=offerCodeCountInt;offSeg++) {

						Map<String, Object> segmentValueContext = new HashMap<String, Object>();

						String segmentValueId = campaign.getString("campaignTypeId") + "_" + marketingCampaignId + "_OFFER" +offSeg;

						segmentValueContext.put("groupId", groupId);
						segmentValueContext.put("customFieldId", segmentValueId);
						segmentValueContext.put("customFieldName", "OFFER_"+offSeg);
						segmentValueContext.put("valueCapture", "SINGLE");
						segmentValueContext.put("valueData", segmentValueId);
						segmentValueContext.put("isEnabled", "Y");
						segmentValueContext.put("sequenceNumber", ""+sequenceNumber);

						segmentValueContext.put("userLogin", userLogin);

						Map<String, Object> segmentValueResult = dispatcher.runSync("segment.createSegmentValue", segmentValueContext);
						if (!ServiceUtil.isError(segmentValueResult)) {
							Debug.logInfo("auto generated segment value: "+segmentValueId, MODULE);
							sequenceNumber++;

						}

					}
				}

			}else {
				result.putAll(ServiceUtil.returnSuccess("Campaign not found for auto segmentation with CampaignId: "+marketingCampaignId));
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnSuccess(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully auto campaign segmentation.."));

		return result;

	}
	public static Map autoCampaignSegmentationForOfferUpdate(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String groupId = (String) context.get("groupId");
		String offerCodeCount = (String) context.get("offerCodeCount");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		int offerCodeCountInt=0;
		if (UtilValidate.isNotEmpty(offerCodeCount)) {
			offerCodeCountInt = Integer.parseInt(offerCodeCount);
		}
		Map<String, Object> result = new HashMap<String, Object>();

		try {

			GenericValue campaign = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaign",UtilMisc.toMap("marketingCampaignId", marketingCampaignId), null, false) );
			
			if (UtilValidate.isNotEmpty(campaign)) {
				
				Long sequenceNumberToComp = delegator.findCountByCondition("CustomFieldCampaignActionType",
						EntityCondition.makeCondition("campaignTypeId", EntityOperator.EQUALS, campaign.getString("campaignTypeId")), null,
						null);
				
				
				//Long sequenceNumberToComp = new Long(6);
				if(UtilValidate.isEmpty(groupId)){
					groupId=campaign.getString("campaignTypeId") + "_" + marketingCampaignId;
				}
				String roleGroup=groupId;
				Map<String, Object> roleContext = new HashMap<String, Object>();
				if(UtilValidate.isNotEmpty(campaign.getString("roleTypeId"))){
					List<GenericValue> roleConfigList = delegator.findByAnd("CustomFieldRoleConfig",UtilMisc.toMap("groupId", roleGroup), null, false);
					String roleTypeId=campaign.getString("roleTypeId");
					if("ACCOUNT".equals(roleTypeId)){
						roleTypeId="CONTACT";
					}
					if(roleConfigList!=null && roleConfigList.size()>1){
						for(GenericValue roleConfig:roleConfigList){
							roleConfig.remove();
						}
						
						roleContext.put("groupId", roleGroup);
						roleContext.put("roleTypeId", roleTypeId);
						roleContext.put("sequenceNumber", "1");
						SegmentServices.createRoleConfig(delegator, roleContext);
					}else if(roleConfigList!=null && roleConfigList.size()==1){
						GenericValue roleConfig=roleConfigList.get(0);
						roleConfig.put("roleTypeId", roleTypeId);
						roleConfig.store();

					}else{
						roleContext.put("groupId", roleGroup);
						roleContext.put("roleTypeId", roleTypeId);
						roleContext.put("sequenceNumber", "1");
						SegmentServices.createRoleConfig(delegator, roleContext);

					}


				}else{/*
					List<GenericValue> roleConfigList = delegator.findByAnd("CustomFieldRoleConfig",UtilMisc.toMap("groupId", roleGroup), null, false);
					if(roleConfigList!=null && roleConfigList.size()>0){
						for(GenericValue roleConfig:roleConfigList){
							roleConfig.remove();
						}

						roleContext.put("groupId", roleGroup);
						roleContext.put("roleTypeId", "ACCOUNT");
						roleContext.put("sequenceNumber", "1");
						SegmentService.createRoleConfig(delegator, roleContext);

						roleContext.put("roleTypeId", "CONTACT");
						roleContext.put("sequenceNumber", "1");
						SegmentService.createRoleConfig(delegator, roleContext);

						roleContext.put("roleTypeId", "LEAD");
						roleContext.put("sequenceNumber", "2");
						SegmentService.createRoleConfig(delegator, roleContext);


					}
				*/}

				List<EntityCondition> findOfferConditions = UtilMisc.toList(EntityCondition.makeConditionMap("groupId", groupId),
						EntityCondition.makeCondition("sequenceNumber", EntityOperator.GREATER_THAN, sequenceNumberToComp));
				List <GenericValue> offerList = EntityQuery.use(delegator).from("CustomField").select("customFieldId", "groupId", "groupType", "groupName", "customFieldName","sequenceNumber")
						.where(findOfferConditions).orderBy("sequenceNumber DESC").queryList();

				int seqSize=0; 
				if(offerList!=null && offerList.size()>0){
					seqSize=offerList.size();
				}
				Long sequenceNumber=new Long(0);
				if(seqSize > 0){
					sequenceNumber = new Long(sequenceNumberToComp+seqSize+1);
				}else if(seqSize ==0){
					sequenceNumber = new Long(sequenceNumberToComp+seqSize+1);
				}
				if(offerCodeCountInt > seqSize){
					int toIterateLen=offerCodeCountInt-seqSize;
					for (int offSeg=1;offSeg<=toIterateLen;offSeg++) {

						Map<String, Object> segmentValueContext = new HashMap<String, Object>();
						int offSegAdd=seqSize+offSeg;
						String segmentValueId = campaign.getString("campaignTypeId") + "_" + marketingCampaignId + "_OFFER" +offSegAdd;
						segmentValueContext.put("groupId", groupId);
						segmentValueContext.put("customFieldId", segmentValueId);
						segmentValueContext.put("customFieldName", "OFFER_"+offSegAdd);
						segmentValueContext.put("valueCapture", "SINGLE");
						segmentValueContext.put("valueData", segmentValueId);
						segmentValueContext.put("isEnabled", "Y");
						segmentValueContext.put("sequenceNumber", ""+sequenceNumber);

						segmentValueContext.put("userLogin", userLogin);

						Map<String, Object> segmentValueResult = dispatcher.runSync("segment.createSegmentValue", segmentValueContext);
						if (!ServiceUtil.isError(segmentValueResult)) {
							Debug.logInfo("auto generated segment value: "+segmentValueId, MODULE);
							sequenceNumber++;

						}

					}
				}else if(offerCodeCountInt < seqSize){
					int toIterateLen=seqSize-offerCodeCountInt;
					for (int offSeg=1;offSeg <=toIterateLen;offSeg++) {

						int offSegSub=seqSize;
						String segmentValueId = campaign.getString("campaignTypeId") + "_" + marketingCampaignId + "_OFFER" +offSegSub;
						/*GenericValue segmentValueIdGen = delegator.findOne("CustomField",
								UtilMisc.toMap("customFieldId",segmentValueId), false);
						if(UtilValidate.isNotEmpty(segmentValueIdGen)){
							GenericValue valueConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldValueConfig", UtilMisc.toMap("customFieldId", segmentValueId), null, false) );
							if(valueConfig!=null && valueConfig.size()>0){
								valueConfig.remove();
							}
							segmentValueIdGen.remove();
							seqSize--;
						}*/
						Map<String, Object> deleteSegmentValueContext = new HashMap<String, Object>();
						int offSegAdd=seqSize+offSeg;
						deleteSegmentValueContext.put("customFieldId", segmentValueId);
						deleteSegmentValueContext.put("userLogin", userLogin);

						Map<String, Object> segmentValueDeleteResult = dispatcher.runSync("segment.deleteSegmentValue", deleteSegmentValueContext);
						if (!ServiceUtil.isError(segmentValueDeleteResult)) {
							Debug.logInfo("auto generated segment value: "+segmentValueId, MODULE);
							seqSize--;

						}

					}

				}

			}else {
				result.putAll(ServiceUtil.returnSuccess("Campaign not found for auto segmentation with CampaignId: "+marketingCampaignId));
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnSuccess(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully auto campaign segmentation.."));

		return result;

	}
	
	public static void createRoleConfig(Delegator delegator, Map<String, Object> context) {

		try {

			String sequenceNumber = ParamUtil.getString(context, "sequenceNumber");
			String groupId = ParamUtil.getString(context, "groupId");
			String customFieldId = ParamUtil.getString(context, "customFieldId");
			String roleTypeId = ParamUtil.getString(context, "roleTypeId");

			GenericValue roleConfig = delegator.makeValue("CustomFieldRoleConfig");

			String configId = delegator.getNextSeqId("CustomFieldRoleConfig");

			roleConfig.put("customFieldRoleConfigId", configId);

			roleConfig.put("groupId", groupId);
			roleConfig.put("customFieldId", customFieldId);

			roleConfig.put("roleTypeId", roleTypeId);

			roleConfig.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1));

			roleConfig.create();
		} catch (Exception e) {
			e.printStackTrace();
		}

	} 

    public static String uploadSegmentsService(HttpServletRequest request, HttpServletResponse response) throws ComponentException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        String fileLocation = UtilProperties.getPropertyValue("custom-field", "segmentImportFilePath");
        String groupId = request.getParameter("groupId");
        String customFieldId = request.getParameter("customFieldId");
        String activeTab = request.getParameter("activeTab");
        String msg = "Customers imported into the Segment Sucessfully";
        if (UtilValidate.isNotEmpty(fileLocation)) {
            //String filePath = ComponentConfig.getRootLocation("custom-field") + fileLocation;
        	String filePath = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SEG_IMP_PATH");
        	
            File store = new File(filePath);
            String updateMode = request.getParameter("updateMode");
            Locale locale = UtilHttp.getLocale(request);
            DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
            fileItemFactory.setRepository(store);
            //CSVReader csvReader = null;
            try {
                GenericValue customFieldFileUpload = null;
                String originalFileName = "";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHMMSS");
                String requestId = dateFormat.format(new Date());
                if (ServletFileUpload.isMultipartContent(request)) {
                    List < FileItem > multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(request);

                    for (FileItem item: multiparts) {
                        if (!item.isFormField()) {
                            originalFileName = new File(item.getName()).getName();
                            File f = new File(filePath + originalFileName);
                            if (f.exists()) {
                                f.delete();
                            }
                            File uploadFile = new File(filePath + originalFileName);
                            item.write(uploadFile);
                            Debug.log("uploadFile original path: " + uploadFile, MODULE);
                        }
                        if (item.isFormField()) {
                            String fName = item.getFieldName();
                            String fValue = item.getString();
                            if ("groupId".equals(fName)) {
                                groupId = fValue;
                            } else if ("customFieldId".equals(fName)) {
                                customFieldId = fValue;
                            } else if ("updateMode".equals(fName)) {
                                updateMode = fValue;
                            } else if ("activeTab".equals(fName)) {
                                activeTab = fValue;
                            }
                        }
                    }

                    if (UtilValidate.isEmpty(updateMode)) {
                        updateMode = "APPEND";
                    }

                    if (UtilValidate.isNotEmpty(groupId) && UtilValidate.isNotEmpty(customFieldId)) {
                        String newFileName = "SEG_UPLOAD_" + customFieldId + "_" + requestId + ".csv";
                        File file = new File(filePath  + originalFileName);
                        File newFile = new File(filePath + newFileName);
                        if (file.exists()) {
                            file.renameTo(newFile);
                        }
                        Debug.log("uploadFile NEW path: " + newFile, MODULE);

                        if (UtilValidate.isNotEmpty(requestId)) {

                            TransactionUtil.begin();
                            customFieldFileUpload = delegator.makeValue("CustomFieldFileUpload", UtilMisc.toMap("requestId", requestId,
                                "customFieldType", "SEGMENTATION", "customFieldGroupId", groupId, "segmentValueId", customFieldId, "status", "FILE_UPLOADED",
                                "originalFileName", originalFileName, "systemFileName", newFileName, "folderLocation", filePath, "uploadedBy", userLogin.getString("partyId"),
                                "updateMode", updateMode));
                            customFieldFileUpload.create();
                            TransactionUtil.commit();

                            try {
                                String fileExtension = UtilProperties.getPropertyValue("campaign", "fio-princess-file-extension");
                                if (UtilValidate.isEmpty(fileExtension)) {
                                    fileExtension = ".sh";
                                }

                                String jarMainClassName = UtilProperties.getPropertyValue("custom-field", "fio-segmentImport-jar-main-class-name");
                                
                                String ofbizHome = System.getProperty("ofbiz.home");
                                String jarPath = null;
                                GenericValue systemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "campaignlist", "systemPropertyId", "jarpath").queryOne();
                                if (UtilValidate.isNotEmpty(systemProperty)) {
                                	jarPath = systemProperty.getString("systemPropertyValue");
                                }
                                if (UtilValidate.isEmpty(jarPath)) {
                                    request.setAttribute("_ERROR_MESSAGE_", "External jarPath is not configured.");
                                    return "error";
                                }

                                String location = filePath;
                                String jarName = UtilProperties.getPropertyValue("campaign", "fio-CampList-jar-name");
                                jarPath = ofbizHome+jarPath;
                                Debug.log("external app jar location: "+jarPath, MODULE);

                                //File f = new File(location + File.separator + marketingCampaignId + fileExtension);
                                String fileName = location + requestId + fileExtension;
                                Debug.log("sh file location: "+fileName, MODULE);
                                
                                File genList = new File(fileName);
                                Debug.log("sh file path: " + genList, MODULE);
                                Writer writer = new java.io.FileWriter(genList);
                                writer.append("cd " + jarPath);
                                writer.append("\n");
                                writer.append("java -cp " + jarName + " " + jarMainClassName + " " + requestId);
                                writer.close();
                                genList.setExecutable(true);
                                genList.setReadable(true);
                                genList.setWritable(true);
                                Debug.log("before  starting", MODULE);
                                ProcessBuilder processBuilder = new ProcessBuilder(fileName);
                                Process p = processBuilder.start();
                                try {
                                    Debug.log("statrted waiting", MODULE);
                                    p.waitFor();
                                    Debug.log("after starting", MODULE);
                                    //genList.delete();
                                    
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        	                        String line = null;
        	                        Debug.log("External app log start", MODULE);
        	                        while ((line = reader.readLine()) != null) {
        	                        	Debug.log(line, MODULE);
        	                        }
        	                        Debug.log("External app log end", MODULE);
                                    
                                    Debug.log("jarrss n full---->" + jarName + " " + jarMainClassName + " " + requestId, MODULE);
                                } catch (Exception e) {
                                	e.printStackTrace();
                                    Debug.log("Exception in External application" +e.getMessage(), MODULE);
                                }
                            } catch (Exception e) {
                                Debug.log("Exception in Segment Import" +e.getMessage(), MODULE);
                            }
                            GenericValue customFieldPartyClassification = EntityQuery.use(delegator).from("CustomFieldFileUpload").where("customFieldGroupId", groupId, 
                                "segmentValueId", customFieldId, "requestId",requestId,"customFieldType", "SEGMENTATION" ).queryFirst();
                            if(customFieldPartyClassification != null && customFieldPartyClassification.size() > 0) {
                                msg = customFieldPartyClassification.getString("message");
                            }
                        }
                    }
                }

            } catch (Exception e1) {
            	e1.printStackTrace();
                Debug.log("Exception in Upload Economic Metrics Service" + e1.getMessage(), MODULE);
                request.setAttribute("groupId", groupId);
                request.setAttribute("customFieldId", customFieldId);
                request.setAttribute("activeTab", activeTab);
                request.setAttribute("_ERROR_MESSAGE_", e1.getMessage());
                return "error";
            }
        }
        
        request.setAttribute("groupId", groupId);
        request.setAttribute("customFieldId", customFieldId);
        request.setAttribute("activeTab", activeTab);
        request.setAttribute("_EVENT_MESSAGE_", msg);
        return "success";
    }

	public static Map<String, Object> createSegmentMap(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> results = ServiceUtil.returnSuccess("Segment Mapped Successfully");
		String dwFieldId = (String) context.get("masterField");
		String dwFieldTable = (String) context.get("masterTable");
		String segmentId = (String) context.get("segment");
		String dwTypeId = (String) context.get("fieldType");
		try {
			if(UtilValidate.isNotEmpty(dwFieldId) && UtilValidate.isNotEmpty(dwFieldTable) && UtilValidate.isNotEmpty(dwTypeId)) {
			GenericValue segmentMapping = delegator.makeValue("SegmentMapping");
			segmentMapping.put("dwFieldId", dwFieldId);
			segmentMapping.put("dwFieldTable", dwFieldTable);
			segmentMapping.put("segmentId", segmentId);
			segmentMapping.put("dwTypeId", dwTypeId);
			segmentMapping.create();
			} else {
			results = ServiceUtil.returnSuccess("Failed to Map Segment");
			}
		} catch (Exception e) {
			Debug.logInfo("Exception in Mapping Segment" + e.getMessage(), MODULE);
			return ServiceUtil.returnError("Failed to Map Segment");
		}
		return results;
	}
	
}
