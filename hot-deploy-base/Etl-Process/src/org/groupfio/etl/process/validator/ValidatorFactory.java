/**
 * 
 */
package org.groupfio.etl.process.validator;

/**
 * @author Sharif
 *
 */
public final class ValidatorFactory {

	private static final DefaultValueValidator DEFAULT_VALUE_VALIDATOR = new DefaultValueValidator();
	private static final CustomerDataValidator CUSTOMER_DATA_VALIDATOR = new CustomerDataValidator();
	private static final ContactDataValidator CONTACT_DATA_VALIDATOR = new ContactDataValidator();
	private static final SupplierDataValidator SUPPLIER_DATA_VALIDATOR = new SupplierDataValidator();
	private static final InvoiceHeaderDataValidator INVOICE_HEADER_DATA_VALIDATOR = new InvoiceHeaderDataValidator();
	private static final InvoiceItemDataValidator INVOICE_ITEM_DATA_VALIDATOR = new InvoiceItemDataValidator();
	private static final ProductDataValidator PRODUCT_DATA_VALIDATOR = new ProductDataValidator();
	private static final AccountDataValidator ACCOUNT_DATA_VALIDATOR = new AccountDataValidator();
	private static final CategoryDataValidator CATEGORY_DATA_VALIDATOR = new CategoryDataValidator();
	private static final OrderDataValidator ORDER_DATA_VALIDATOR = new OrderDataValidator();
	private static final LockboxBatchDataValidator LOCKBOXBATCH_DATA_VALIDATOR = new LockboxBatchDataValidator();
	private static final LockboxBatchItemDataValidator LOCKBOXBATCH_ITEM_DATA_VALIDATOR = new LockboxBatchItemDataValidator();
	private static final WalletDataValidator WALLET_DATA_VALIDATOR = new WalletDataValidator();
	private static final LeadDataValidator LEAD_DATA_VALIDATOR = new LeadDataValidator();
	private static final EmplPositionDataValidator EMPL_POSITION_DATA_VALIDATOR = new EmplPositionDataValidator();
	private static final ProductSupplementaryValidator PSD_DATA_VALIDATOR = new ProductSupplementaryValidator();
	private static final ItmValidator ITM_DATA_VALIDATOR = new ItmValidator();
	private static final ActivityValidator ACT_DATA_VALIDATOR = new ActivityValidator();
	
	public static DefaultValueValidator getDefaultValueValidator () {
		return DEFAULT_VALUE_VALIDATOR;
	}
	
	public static CustomerDataValidator getCustomerDataValidator () {
		return CUSTOMER_DATA_VALIDATOR;
	}
	
	public static ContactDataValidator getContactDataValidator () {
		return CONTACT_DATA_VALIDATOR;
	}
	
	public static SupplierDataValidator getSupplierDataValidator () {
		return SUPPLIER_DATA_VALIDATOR;
	}
	
	public static InvoiceHeaderDataValidator getInvoiceHeaderDataValidator () {
		return INVOICE_HEADER_DATA_VALIDATOR;
	}
	
	public static InvoiceItemDataValidator getInvoiceItemDataValidator () {
		return INVOICE_ITEM_DATA_VALIDATOR;
	}
	
	public static ProductDataValidator getProductDataValidator () {
		return PRODUCT_DATA_VALIDATOR;
	}
	
	public static AccountDataValidator getAccountDataValidator () {
		return ACCOUNT_DATA_VALIDATOR;
	}
	
	public static CategoryDataValidator getCategoryDataValidator () {
		return CATEGORY_DATA_VALIDATOR;
	}
	
	public static OrderDataValidator getOrderDataValidator () {
		return ORDER_DATA_VALIDATOR;
	}
	
	public static LockboxBatchDataValidator getLockboxBatchDataValidator () {
		return LOCKBOXBATCH_DATA_VALIDATOR;
	}
	
	public static LockboxBatchItemDataValidator getLockboxBatchItemDataValidator () {
		return LOCKBOXBATCH_ITEM_DATA_VALIDATOR;
	}
	
	public static WalletDataValidator getWalletDataValidator () {
		return WALLET_DATA_VALIDATOR;
	}
	
	public static LeadDataValidator getLeadDataValidator () {
		return LEAD_DATA_VALIDATOR;
	}
	
	public static EmplPositionDataValidator getEmplPositionDataValidator () {
		return EMPL_POSITION_DATA_VALIDATOR;
	}
	
	public static ProductSupplementaryValidator getProductSupplementaryDataValidator () {
		return PSD_DATA_VALIDATOR;
	}
	
	public static ItmValidator getItmDataValidator () {
		return ITM_DATA_VALIDATOR;
	}
	
	public static ActivityValidator getActivityDataValidator () {
		return ACT_DATA_VALIDATOR;
	}

}
