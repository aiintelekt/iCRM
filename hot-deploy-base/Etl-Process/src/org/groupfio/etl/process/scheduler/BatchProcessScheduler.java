/**
 * 
 */
package org.groupfio.etl.process.scheduler;

import java.util.Map;

import org.groupfio.etl.process.job.AccountImportJob;
import org.groupfio.etl.process.job.CategoryImportJob;
import org.groupfio.etl.process.job.CustomerImportJob;
import org.groupfio.etl.process.job.EmplPositionImportJob;
import org.groupfio.etl.process.job.InvoiceHeaderImportJob;
import org.groupfio.etl.process.job.InvoiceItemImportJob;
import org.groupfio.etl.process.job.LeadImportJob;
import org.groupfio.etl.process.job.LockboxImportJob;
import org.groupfio.etl.process.job.OrderImportJob;
import org.groupfio.etl.process.job.ProductImportJob;
import org.groupfio.etl.process.job.SupplierImportJob;
import org.groupfio.etl.process.job.WalletImportJob;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Group Fio
 *
 */
public class BatchProcessScheduler {

	private static String MODULE = BatchProcessScheduler.class.getName();
	
	public static Map<String, Object> processCustomerData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processCustomerData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			CustomerImportJob job = new CustomerImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processCustomerData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processCustomerData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> processSupplierData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processSupplierData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			SupplierImportJob job = new SupplierImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processSupplierData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processSupplierData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> processInvoiceHeaderData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processInvoiceHeaderData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			InvoiceHeaderImportJob job = new InvoiceHeaderImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processInvoiceHeaderData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processInvoiceHeaderData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> processInvoiceItemData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processInvoiceItemData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			InvoiceItemImportJob job = new InvoiceItemImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processInvoiceItemData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processInvoiceItemData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> processProductData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processProductData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			ProductImportJob job = new ProductImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processProductData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processProductData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> processAccountData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processAccountData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			AccountImportJob job = new AccountImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processAccountData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processAccountData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> processCategoryData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processCategoryData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			CategoryImportJob job = new CategoryImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processCategoryData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processCategoryData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> processOrderData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processOrderData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			OrderImportJob job = new OrderImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processOrderData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processOrderData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> processLockboxBatchData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processLockboxBatchData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			LockboxImportJob job = new LockboxImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processLockboxBatchData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processLockboxBatchData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> processWalletData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processWalletData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			WalletImportJob job = new WalletImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processWalletData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processWalletData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> processLeadData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processLeadData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			LeadImportJob job = new LeadImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processLeadData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processLeadData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> processEmplPositionData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Debug.logInfo("processEmplPositionData start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			EmplPositionImportJob job = new EmplPositionImportJob();
			job.setDelegator(delegator);
			job.setDispatcher(dispatcher);
			job.setUserLogin(userLogin);
			job.setCheckModelProcess(true);
			job.start();
			
		} catch (Exception e) {
			Debug.logError("processEmplPositionData Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		
		Debug.logInfo("processEmplPositionData end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
}
