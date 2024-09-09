/**
 * 
 */
package org.etlprocess.upload;

/**
 * @author palani
 * @since 
 *
 */
public class ETLConstants {
	
	// Resource bundles	
    public static final String configResource = "Etl-Process";
    
    public static final String DEFAULT_DELEGATOR_NAME = "default";
	
	public static final String PROCESSING_STATUS_NOT = "N";
	public static final String PROCESSING_STATUS_STARTED = "Y";
	public static final String PROCESSING_STATUS_COMPLETED = "C";
	public static final String PROCESSING_STATUS_FAILED = "F";
	
    public static final String ETL_ORDER_IMP_SER = "CreateEtlOrdersLoad"; 
    public static final String ETL_ORDER_FULFILL_SER = "etl.importFulfillmentOrders";
    public static final String ETL_PRODUCT_EXP_SER = "etl.productExtraction"; 
    public static final String ETL_CUST_IMP_SER = "importCustomers";
    public static final String ETL_SUPP_IMP_SER = "importSuppliers";
    public static final String ETL_ACC_IMP_SER = "importAccounts";
    public static final String ETL_CONT_IMP_SER = "importContacts";
    public static final String ETL_CAT_IMP_SER = "createProductCategory";
    public static final String ETL_PRO_IMP_SER = "importProducts";
    public static final String ETL_ORD_IMP_SER = "importOrders";
    public static final String ETL_INV_IMP_SER = "DataImportInvoiceMain";
    public static final String ETL_INVITM_IMP_SER = "DataImportInvoiceItem";
    public static final String ETL_PROD_SUPPLEMENTARY_SER = "DataImportProductSupplementary";
	public static final String ETL_ACT_SER = "DataImportActivity";
    
}
