/**
 * 
 */
package org.groupfio.etl.process.scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.etl.process.util.SftpUtility;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Group Fio
 *
 */
public class FileDownloadScheduler {

	private static String MODULE = FileDownloadScheduler.class.getName();
	
	public static Map<String, Object> downloadFileByGroup(DispatchContext dctx, Map<String, ? extends Object> context) {
		Debug.logInfo("file download group process start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			List<GenericValue> modelGruops = delegator.findByAnd("EtlProcessGrouping", null, UtilMisc.toList("sequenceNo"), false);
			
			Map<String, List<GenericValue>> modelGroupList = new HashMap<String, List<GenericValue>>();
			
			if (UtilValidate.isNotEmpty(modelGruops)) {
				
				for (GenericValue mg : modelGruops) {
					String groupId = mg.getString("groupId");
					if (modelGroupList.get(groupId) != null) {
						modelGroupList.get(groupId).add(mg);
					} else {
						List<GenericValue> modelGrouping = new ArrayList<GenericValue>();
						modelGrouping.add(mg);
						modelGroupList.put(groupId, modelGrouping);
					}
					
				}
				
				for (String groupId : modelGroupList.keySet()) {
					
					List<GenericValue> modelGrouping = modelGroupList.get(groupId);
					
					for (GenericValue mg : modelGrouping) {
						
						GenericValue etlProcess = EntityQuery.use(delegator).from("EtlProcess").where("processId", mg.getString("processId")).queryOne();
						if (UtilValidate.isNotEmpty(etlProcess)) {
							GenericValue etlModel = EntityQuery.use(delegator).from("EtlModel").where("modelName", etlProcess.getString("modalName")).queryOne();
							if (UtilValidate.isNotEmpty(etlModel)) {
								
								String modelName = etlModel.getString("modelName");
								
								GenericValue modelDefault = EntityUtil.getFirst( delegator.findByAnd("EtlModelDefaults", UtilMisc.toMap("modelName", modelName, "propertyName", "isSftpEnable"), null, false) );
								
								if (UtilValidate.isNotEmpty(modelDefault) && UtilValidate.isNotEmpty(modelDefault.getString("propertyValue")) && modelDefault.getString("propertyValue").equals("Y")) {
									
									GenericValue sftpConfig = EntityUtil.getFirst( delegator.findByAnd("SftpConfiguration", UtilMisc.toMap("modelName", modelName), null, false) );
									
									if (UtilValidate.isNotEmpty(sftpConfig) 
											&& UtilValidate.isNotEmpty(sftpConfig.getString("host"))
											&& UtilValidate.isNotEmpty(sftpConfig.getString("userName"))
											&& UtilValidate.isNotEmpty(sftpConfig.getString("password"))
											) {
										
										Debug.logInfo("start file download for model# "+modelName, MODULE);
										
										String sftpUsername = sftpConfig.getString("userName");
							            String sftpPassword = sftpConfig.getString("password");
							            String sftpPort = sftpConfig.getString("port");
							            String sftpHost = sftpConfig.getString("host");
							            String sftpLocation = sftpConfig.getString("location");
							            
							            int port = 22;
							            if (UtilValidate.isNotEmpty(sftpPort)) {
							            	port = Integer.parseInt(sftpPort);
							            }
										
							            SftpUtility sftp = new SftpUtility(sftpHost,port, sftpUsername, sftpPassword, "/");
							            
							            sftp.setCreateDir(true);
							            
										String modelFolderName = etlModel.getString("modelId").concat("-").concat(etlModel.getString("modelName")).concat("-").concat(groupId);
										Debug.logInfo("modelFolderName> "+modelFolderName, MODULE);
										
										String localImportLocation = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.import.location");
										File targetLocalLocation = new File(localImportLocation + modelFolderName);
										if (!targetLocalLocation.exists()) {
											targetLocalLocation.mkdir();
										}
										
										String sftpImportLocation = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.sftp.import.location");
										sftpImportLocation = sftpImportLocation + modelFolderName;
										if (UtilValidate.isNotEmpty(sftpLocation)) {
											sftpImportLocation = sftpLocation;
										}
										
										List<String> files = new ArrayList();
										
										//Copy main List files to local
										List processedFiles = sftp.readFileFromDir(sftpImportLocation, targetLocalLocation.getAbsolutePath());
										
										Debug.logInfo("SFTP downloaded Files>>>> "+processedFiles, MODULE);
										if(processedFiles.size() > 0) {
											files.addAll(processedFiles);
										}
										
										Debug.logInfo("end file download for model# "+modelName, MODULE);
									}
									
								}
								
							}
						}
						
					}
					
				}
				
			}
			
		} catch (Exception e) {
			Debug.logError("file download group process Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		Debug.logInfo("file download group process end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
}
