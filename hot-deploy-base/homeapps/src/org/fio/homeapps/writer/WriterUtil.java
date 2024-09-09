package org.fio.homeapps.writer;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.fio.homeapps.writer.Writer;
import org.fio.homeapps.writer.WriterFactory;
import org.fio.homeapps.writer.WriterUtil;
import org.fio.homeapps.rest.response.Response;
import org.fio.homeapps.util.ResponseUtils;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;

/**
 * @author Sharif
 *
 */
public class WriterUtil {

    private static String MODULE = WriterUtil.class.getName();
    
    public static String writeLog(Delegator delegator, String serviceName, String version, String clientLogRefId, Object requestedData, Object response,
            String responseCode, String responseStatus, String clientRegistryId, Timestamp requestedTime, Timestamp responsedTime, String msguid, String systemName) {
        return writeLog(delegator, serviceName, version, clientLogRefId, requestedData, response,
                 responseCode, responseStatus, clientRegistryId, requestedTime, responsedTime, msguid, null, systemName);
    }

    public static String writeLog(Delegator delegator, String serviceName, String clientLogRefId, Object requestedData, Object response,
            String responseCode, String responseStatus, String clientRegistryId, Timestamp requestedTime, Timestamp responsedTime, String msguid, String systemName) {
        return writeLog(delegator, serviceName, null, clientLogRefId, requestedData, response,
                 responseCode, responseStatus, clientRegistryId, requestedTime, responsedTime, msguid, null, systemName);
    }
    
    public static String writeLog(Delegator delegator, String serviceName, String clientLogRefId, Object requestedData, Object response,
            String responseCode, String responseStatus, String clientRegistryId, Timestamp requestedTime, Timestamp responsedTime) {
        return writeLog(delegator, serviceName, null, clientLogRefId, requestedData, response,
                 responseCode, responseStatus, clientRegistryId, requestedTime, responsedTime, null, null, null);
    }

    public static String writeLog(Delegator delegator, String serviceName, String version, String clientLogRefId, Object requestedData, Object response,
            String responseCode, String responseStatus, String clientRegistryId, Timestamp requestedTime, Timestamp responsedTime, String msguid, String orgId, String systemName) {

        try {
            Writer writer = WriterFactory.getLogWriter();
            Map<String, Object> writerContext = new HashMap<String, Object>();
            writerContext.put("delegator", delegator);
            writerContext.put("serviceName", serviceName);
            writerContext.put("version", version);
            writerContext.put("clientLogRefId", clientLogRefId);
            writerContext.put("requestedData", requestedData);
            writerContext.put("responseCode", responseCode);
            writerContext.put("responseStatus", responseStatus);
            writerContext.put("clientRegistryId", clientRegistryId);
            writerContext.put("requestedTime", requestedTime);
            writerContext.put("responsedTime", responsedTime);
            writerContext.put("msguid", msguid);
            writerContext.put("orgId", orgId);
            writerContext.put("systemName", systemName);

            Map<String, Object> writerResponse = writer.write(writerContext);
            if (ResponseUtils.isError(writerResponse)) {
                return null;
            }
            if (response instanceof Response) {
                ((Response) response).setResponse_ref_id(writerResponse.get("ofbizApiLogId").toString());
                TransactionUtil.begin();
                GenericValue apiLog = (GenericValue) writerResponse.get("apiLog");
                
                JSON jsonFeed = JSON.from(response);
                String responsedData = jsonFeed.toString();
                
                apiLog.put("responsedData", responsedData);
                apiLog.store();
                TransactionUtil.commit();
            }
            
            return (String) writerResponse.get("ofbizApiLogId");
        } catch (Exception e) {
            Debug.logError("Error write log>>" + e.getMessage(), MODULE);
            return null;
        }
    }
}

