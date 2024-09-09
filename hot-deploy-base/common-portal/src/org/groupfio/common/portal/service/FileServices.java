package org.groupfio.common.portal.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.transaction.Transaction;

import org.apache.commons.fileupload.FileItem;
import org.groupfio.common.portal.util.DataHelper;
import org.imgscalr.Scalr;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class FileServices {

	private static final String MODULE = FileServices.class.getName();
	
	public static Map<String, Object> getFileContentData(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Map requestContext = (Map) context.get("requestContext");
		
		String workEffortId = (String) requestContext.get("workEffortId");
		String communicationEventId = (String) requestContext.get("communicationEventId");

		try{
			if (UtilValidate.isNotEmpty(workEffortId) || UtilValidate.isNotEmpty(communicationEventId)) {
				List<EntityCondition> conditionList = FastList.newInstance();
				if (UtilValidate.isNotEmpty(workEffortId) && UtilValidate.isEmpty(communicationEventId)) {
					conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
					GenericValue commEventWorkEff = EntityUtil.getFirst(delegator.findList("CommunicationEventWorkEff", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("communicationEventId"), null, null, false));
					if (UtilValidate.isNotEmpty(commEventWorkEff)) {
						communicationEventId = commEventWorkEff.getString("communicationEventId");
					}
				}
				
				if (UtilValidate.isNotEmpty(communicationEventId)) {
					conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("communicationEventId", EntityOperator.EQUALS, communicationEventId));
					Set<String> fieldsToSelect  = new TreeSet<String>();
					fieldsToSelect.add("mimeTypeId");fieldsToSelect.add("dataResourceId");fieldsToSelect.add("drObjectInfo");
					fieldsToSelect.add("createdByUserLogin");fieldsToSelect.add("fromDate");fieldsToSelect.add("thruDate");
					fieldsToSelect.add("createdDate");fieldsToSelect.add("contentName");fieldsToSelect.add("statusId");
					fieldsToSelect.add("contentId");fieldsToSelect.add("contentTypeId");fieldsToSelect.add("lastModifiedDate");
					fieldsToSelect.add("communicationEventId");
					List<GenericValue> fileContents = delegator.findList("CommEventContentDataResource", EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false);
					resultMap.put("fileContents", fileContents);
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	public static Map<String, Object> uploadFile(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Map requestContext = (Map) context.get("requestContext");
		
		String communicationEventId = (String) requestContext.get("communicationEventId");
		String contentTypeId = (String) requestContext.get("contentTypeId");
		
		String partyId = (String) requestContext.get("partyId");
		String salesOpportunityId = (String) requestContext.get("salesOpportunityId");
		String custRequestId = (String) requestContext.get("custRequestId");
		
		String domainEntityId = (String) requestContext.get("domainEntityId");
		String domainEntityType = (String) requestContext.get("domainEntityType");
		String linkedFrom = (String) requestContext.get("linkedFrom");
		String workEffortId = (String) requestContext.get("workEffortId");
		String workEffortTypeId = (String) requestContext.get("workEffortTypeId");
		
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			String maxAttachmentSizeMB = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "EMAIL_ATTACHMENT_MAX_SIZE");
			long maxAttachmentSize = DataHelper.convertMBtoBytes( maxAttachmentSizeMB );
			
			String maxFileSizeMB = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ATTACHMENT_SIZE");
			long maxFileSize = DataHelper.convertMBtoBytes( maxFileSizeMB );
			
			List<FileItem> attachmentList = new ArrayList<FileItem>();
			
			// attachment validation [start]
			long totalAttachmentSize = 0;
			Map < String, Object > multiPartMap = (Map < String, Object > ) requestContext.get("multiPartMap");
			if (UtilValidate.isNotEmpty(multiPartMap)) {
                Iterator < String > mpit = multiPartMap.keySet().iterator();
                while (mpit.hasNext()) {
                    String key = mpit.next();

                    // Since the ServiceEventHandler adds all form inputs to the map, just deal with the ones matching the correct input name (eg. 'uploadedFile_0', 'uploadedFile_1', etc)
                    if (!key.startsWith("uploadedFile")) {
                        continue;
                    }
                    // Some browsers will submit an empty string for an empty input type="file", so ignore the ones that are empty
                    if (UtilValidate.isEmpty(multiPartMap.get(key))) {
                        continue;
                    }
                    
                    FileItem item = (FileItem) multiPartMap.get(key);
                    System.out.println("file size> "+item.getSize());
                    if (item.getSize() > maxFileSize) {
                    	result.putAll(ServiceUtil.returnError("File# "+item.getName()+", Size# "+DataHelper.convertBytestoMB(item.getSize())+"MB, exceed MAX size# "+maxFileSizeMB+"MB"));
		    			return result;
                    }
                    
                    ByteBuffer uploadedFile = ByteBuffer.allocate(item.get().length);
                    uploadedFile.put(item.get());
                    String uploadedFileName = item.getName();
                    
                    // Check to see that we have everything
                    if (UtilValidate.isEmpty(uploadedFileName)) {
                        continue; // not really a file if there is no name
                    //} else if (UtilValidate.isEmpty(uploadedFile) || UtilValidate.isEmpty(uploadedFileContentType)) {
                    } else if (UtilValidate.isEmpty(uploadedFile)) {
                        String errMsg = "Missing file upload data: "+uploadedFileName;
						Debug.logError(errMsg, MODULE);
						continue;
                    }
                    
                    totalAttachmentSize += item.getSize();
                    attachmentList.add(item);
                }
                
                if (totalAttachmentSize > maxAttachmentSize) {
    				result.putAll(ServiceUtil.returnError("Total attachment size# "+DataHelper.convertBytestoMB(totalAttachmentSize)+"MB, exceed MAX size# "+maxAttachmentSizeMB+"MB"));
        			return result;
    			}
            }
			// attachment validation [end]
			
			int fileCounter = 1;
			if (UtilValidate.isNotEmpty(attachmentList)) {
				for (FileItem item : attachmentList) {
					
					ByteBuffer uploadedFile = ByteBuffer.allocate(item.get().length);
                    uploadedFile.put(item.get());
                    String uploadedFileName = item.getName();
                    String uploadedFileContentType= URLConnection.guessContentTypeFromName(uploadedFileName);
                    
                    if (UtilValidate.isNotEmpty(uploadedFileContentType)) {
                    	uploadedFileContentType = new MimetypesFileTypeMap().getContentType(uploadedFileName);
                    }
					
					// Populate the context for the DataResource/Content/CommEventContentAssoc creation service
                    Map < String, Object > createContentContext = new HashMap < String, Object > ();
                    try {
                        createContentContext.put("userLogin", userLogin);
                        createContentContext.put("contentName", uploadedFileName);
                        createContentContext.put("contentTypeId", contentTypeId);
                        createContentContext.put("uploadedFile", uploadedFile);
                        createContentContext.put("uploadFolder", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "UPLOAD_LOC"));
                        createContentContext.put("_uploadedFile_fileName", uploadedFileName);//fileName
                        createContentContext.put("_uploadedFile_contentType", uploadedFileContentType);//img/png
                        
                        createContentContext.put("domainEntityId", domainEntityId);
                        createContentContext.put("domainEntityType", domainEntityType);
                        createContentContext.put("linkedFrom", linkedFrom);
                        createContentContext.put("contentIdPrefix", "AT-");

                        callResult = dispatcher.runSync("uploadFile", createContentContext);
                        
                        if (ServiceUtil.isError(callResult)) {
                        	String errMsg = "Error Create Contain failed: "+ServiceUtil.getErrorMessage(callResult);
                        	result.putAll(ServiceUtil.returnError(errMsg));
    		    			return result;
                        }
                        
                        String contentId = (String) callResult.get("contentId");
                        if (UtilValidate.isNotEmpty(contentId)) {
                        	
                        	if (UtilValidate.isNotEmpty(workEffortTypeId) && workEffortTypeId.equals("E-mail")) {
	                        	callResult = dispatcher.runSync("createCommEventContentAssoc", UtilMisc.toMap("contentId", contentId, "communicationEventId", communicationEventId,
	                                "sequenceNum", new Long(fileCounter), "userLogin", userLogin));
	                            if (ServiceUtil.isError(callResult)) {
	                            	String errMsg = "Error Create Contain failed: "+ServiceUtil.getErrorMessage(callResult);
									result.putAll(ServiceUtil.returnError(errMsg));
		    		    			return result;
	                            }
                        	}
                            
                            // content association
                            DataHelper.contentAssociate(delegator, UtilMisc.toMap("contentId", contentId, "partyId", partyId, "salesOpportunityId", salesOpportunityId, "custRequestId", custRequestId, "workEffortId", workEffortId, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType));
                            
                        } else {
                        	String errMsg = "Upload file ran successfully for [" + uploadedFileName + "] but no contentId was returned";
                        	result.putAll(ServiceUtil.returnError(errMsg));
    		    			return result;
                        }
                        
                    } catch (GenericServiceException e) {
                    	String errMsg = "Error Create Upload failed: "+ServiceUtil.getErrorMessage(callResult);
                    	result.putAll(ServiceUtil.returnError(errMsg));
		    			return result;
                    }
                    fileCounter++;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.put("resultMap", resultMap);
		result.putAll(ServiceUtil.returnSuccess("Successfully uploaded files"));
		return result;
	}
	
	public static Map<String, Object> imageFileValidation(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		String filePath = (String) context.get("filePath");
			File outputFile = new File(filePath);
			if(outputFile.exists()) {
				return ServiceUtil.returnSuccess();
			}
    	} catch (Exception e) {
    		return ServiceUtil.returnError(e.getMessage());
		}
    	return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> imageResize(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String filePath = (String) context.get("filePath");
			File outputFile = new File(filePath);
			if(outputFile.exists()) {
				BufferedImage bufferedImage = ImageIO.read(outputFile);
				String fileName = outputFile.getName();
				String extension = org.fio.admin.portal.util.DataUtil.getFileExtension(fileName);
				
	        	System.out.println("image resize start...."+UtilDateTime.nowTimestamp());
	        	BufferedImage resizedImage = Scalr.resize(bufferedImage, 
	            								Scalr.Method.ULTRA_QUALITY,
	            								Scalr.Mode.AUTOMATIC, 1024, 768, 
	            								Scalr.OP_ANTIALIAS);
	        	
	        	outputFile.setReadable(true);
        		outputFile.setExecutable(true);
        		outputFile.delete();
        		
	            ImageIO.write(resizedImage, extension, outputFile);
	            System.out.println("image resize end...."+UtilDateTime.nowTimestamp());
	            resizedImage.flush();
			}
			
		} catch (Exception e) {
		}
		
		result.putAll(ServiceUtil.returnSuccess("Successfully uploaded files"));
		return result;
	}
}
