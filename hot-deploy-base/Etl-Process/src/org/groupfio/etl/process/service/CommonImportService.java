/**
 * 
 */
package org.groupfio.etl.process.service;

import java.util.Map;

/**
 * @author Sharif
 *
 */
public interface CommonImportService {

	public Map<String, Object> importSupplier(Map<String, Object> context);
	public Map<String, Object> importCustomer(Map<String, Object> context);
	public Map<String, Object> importContact(Map<String, Object> context);
	public Map<String, Object> importAccount(Map<String, Object> context);
	public Map<String, Object> importInvoiceHeader(Map<String, Object> context);
	public Map<String, Object> importInvoiceItem(Map<String, Object> context);
	public Map<String, Object> importProduct(Map<String, Object> context);
	public Map<String, Object> importCategory(Map<String, Object> context);
	public Map<String, Object> importOrder(Map<String, Object> context);
	public Map<String, Object> importLockboxBatch(Map<String, Object> context);
	public Map<String, Object> importLockboxBatchItem(Map<String, Object> context);
	public Map<String, Object> importWallet(Map<String, Object> context);
	public Map<String, Object> importLead(Map<String, Object> context);
	public Map<String, Object> importEmplPosition(Map<String, Object> context);
	public Map<String, Object> importProductSupplementary(Map<String, Object> context);
	public Map<String, Object> importItm(Map<String, Object> context);
	public Map<String, Object> importActivity(Map<String, Object> context);
	
}
