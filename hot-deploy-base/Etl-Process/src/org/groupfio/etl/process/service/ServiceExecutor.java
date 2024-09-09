/**
 * 
 */
package org.groupfio.etl.process.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.groupfio.etl.process.util.CommonUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * @author Sharif
 *
 */
public class ServiceExecutor {

	private Delegator delegator;
	
	private HttpServletRequest servletRequest;
	private HttpServletResponse servletResponse;
	
	private Map<String, Object> reqContext;
	
	public Map<String, Object> execute() throws Exception {
		
		Map<String, Object> res = new HashMap<String, Object>();
		
		String processId = (String) reqContext.get("processId");
		String modelName = (String) reqContext.get("modelName");
		
		Map<Long, Long> rangeList = new HashMap<Long, Long>();
		
		List<GenericValue> modelDefaultRangeList = delegator.findByAnd("EtlModelDefaultRange", UtilMisc.toMap("modelName", modelName), null, false);
		if (UtilValidate.isNotEmpty(modelDefaultRangeList)) {
			for (GenericValue range : modelDefaultRangeList) {
				rangeList.put(range.getLong("startRange"), range.getLong("endRange"));
			}
		}
		
		reqContext.put("rangeList", rangeList);
		
		reqContext.put("servletRequest", servletRequest);
		reqContext.put("servletResponse", servletResponse);
		
		String etlProcessTableName = CommonUtil.getEtlProcessTableName(delegator, processId);
		CommonImportService commonImportService = ServiceFactory.getCommonImportService();
		
		if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportSupplier".equals(etlProcessTableName)) {
			res = commonImportService.importSupplier(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportCustomer".equals(etlProcessTableName)) {
			res = commonImportService.importCustomer(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportContact".equals(etlProcessTableName)) {
			res = commonImportService.importContact(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportAccount".equals(etlProcessTableName)) {
			res = commonImportService.importAccount(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportInvoiceHeader".equals(etlProcessTableName)) {
			res = commonImportService.importInvoiceHeader(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportInvoiceItem".equals(etlProcessTableName)) {
			res = commonImportService.importInvoiceItem(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportProduct".equals(etlProcessTableName)) {
			res = commonImportService.importProduct(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportCategory".equals(etlProcessTableName)) {
			res = commonImportService.importCategory(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "EtlImportOrderFields".equals(etlProcessTableName)) {
			res = commonImportService.importOrder(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "FioLockboxBatchStaging".equals(etlProcessTableName)) {
			res = commonImportService.importLockboxBatch(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "FioLockboxBatchItemStaging".equals(etlProcessTableName)) {
			res = commonImportService.importLockboxBatchItem(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportWallet".equals(etlProcessTableName)) {
			res = commonImportService.importWallet(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportLead".equals(etlProcessTableName)) {
			res = commonImportService.importLead(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportEmplPosition".equals(etlProcessTableName)) {
			res = commonImportService.importEmplPosition(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportProductSupplementary".equals(etlProcessTableName)) {
			res = commonImportService.importProductSupplementary(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportItm".equals(etlProcessTableName)) {
			res = commonImportService.importItm(reqContext);
			
		} else if (UtilValidate.isNotEmpty(etlProcessTableName) && "DataImportActivity".equals(etlProcessTableName)) {
			res = commonImportService.importActivity(reqContext);
		}
		
		return res;
		
	}

	public Delegator getDelegator() {
		return delegator;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public HttpServletResponse getServletResponse() {
		return servletResponse;
	}

	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}

	public Map<String, Object> getReqContext() {
		return reqContext;
	}

	public void setReqContext(Map<String, Object> reqContext) {
		this.reqContext = reqContext;
	}
	
}
