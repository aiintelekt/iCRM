/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.util.WalletUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Group Fio
 *
 */
public class WalletDataValidator implements Validator {

	private static String MODULE = WalletDataValidator.class.getName();
	
	private boolean validate;
	
	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.validator.Validator#validate(java.util.Map)
	 */
	@Override
	public Map<String, Object> validate(Map<String, Object> context) {

		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> data = (Map<String, Object>) context.get("data");
		Map<String, Object> validationMessage = new HashMap<String, Object>();
		
		try {
			
			setValidate(true);
			
			Delegator delegator = (Delegator) context.get("delegator");
			String modelName = ParamUtil.getString(context, "modelName");
			
			Integer rowNumber = ParamUtil.getInteger(context, "rowNumber");
			String taskName = ParamUtil.getString(context, "taskName");
			String tableName = ParamUtil.getString(context, "tableName");
			
			String message = null;
			
			if (UtilValidate.isEmpty(data.get("vaType"))) {
				setValidate(false);
				message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.vaType.empty", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
				validationMessage.put("vaType", message);
			} else if (!data.get("vaType").equals("S")){
				setValidate(false);
				message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.vaType.notsupport", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
				validationMessage.put("vaType", message);
			}
			
			if (isValidate()) {
			
				if (UtilValidate.isEmpty(data.get("action"))) {
					setValidate(false);
					message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.action.empty", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
					validationMessage.put("action", message);
				} else if (!data.get("action").equals("A") && !data.get("action").equals("D")) {
					setValidate(false);
					message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.action.invalid", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
					validationMessage.put("action", message);
				}
				
				if (UtilValidate.isEmpty(data.get("prefix"))) {
					setValidate(false);
					message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.prefix.empty", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
					validationMessage.put("prefix", message);
				}
				
				if (UtilValidate.isEmpty(data.get("corpCode"))) {
					setValidate(false);
					message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.corpCode.empty", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
					validationMessage.put("corpCode", message);
				}
				
				if (UtilValidate.isEmpty(data.get("systemName"))) {
					setValidate(false);
					message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.systemName.empty", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
					validationMessage.put("systemName", message);
				}
				
				if (UtilValidate.isEmpty(data.get("masterWalletNumber"))) {
					setValidate(false);
					message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.masterWalletNumber.empty", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
					validationMessage.put("masterWalletNumber", message);
/*				} else if (UtilValidate.isNotEmpty(data.get("operatingWalletNumber"))) {
					GenericValue masterWallet = WalletUtil.getActiveWalletAccount(delegator, data.get("masterWalletNumber").toString());
					if (UtilValidate.isEmpty(masterWallet)) {
						setValidate(false);
						message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.masterWalletNumber.invalid", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
						validationMessage.put("masterWalletNumber", message);
					} else {
						GenericValue masterWalletRole = WalletUtil.getActiveWalletAccountRole(delegator, masterWallet.getString("billingAccountId"));
						if (UtilValidate.isEmpty(masterWalletRole)) {
							setValidate(false);
							message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.masterWalletNumber.invalid", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
							validationMessage.put("masterWalletNumber", message);
						} else if (!masterWalletRole.getString("roleTypeId").equals("MASTER_ACCT_OWNER")) {
							setValidate(false);
							message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.masterWalletNumber.invalid", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
							validationMessage.put("masterWalletNumber", message);
						}
					}*/
				}else
				{
					String masterWalletNumber = (String)data.get("masterWalletNumber");

					//1. Validation for special character 
					Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
					Matcher matcher = pattern.matcher(masterWalletNumber);
					if (matcher.find()) {
						Debug.logError("Master Wallet Number  contains special characters", MODULE);
						setValidate(false);
						message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.masterWalletNumber.invalid", Locale.ENGLISH) + " or special character available [Wallet No:" + data.get("masterWalletNumber") + "]" + " [Row No:" + rowNumber + "]";
						validationMessage.put("masterWalletNumber", message);
					}
                    
                    
                    
					
				}
				
				if ( (UtilValidate.isNotEmpty(data.get("action")) && data.get("action").equals("A")) && UtilValidate.isNotEmpty(data.get("masterWalletNumber")) && UtilValidate.isEmpty(data.get("operatingWalletNumber"))) {
					GenericValue walletAccount = EntityUtil.getFirst( delegator.findByAnd("BillingAccount", UtilMisc.toMap("externalAccountId", data.get("masterWalletNumber").toString()), null, false) );
					if (UtilValidate.isNotEmpty(walletAccount)) {
						setValidate(false);
						message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.masterWalletNumber.duplicate", Locale.ENGLISH) + " [Wallet No:" + data.get("masterWalletNumber") + "]" + " [Row No:" + rowNumber + "]";
						validationMessage.put("masterWalletNumber", message);
					}
				}
				
				if ( (UtilValidate.isNotEmpty(data.get("action")) && data.get("action").equals("A")) && UtilValidate.isNotEmpty(data.get("masterWalletNumber")) && UtilValidate.isNotEmpty(data.get("operatingWalletNumber"))) {
					
					boolean proceed = false;
					GenericValue operatorWalletAccount = WalletUtil.getActiveWalletAccount(delegator, data.get("operatingWalletNumber").toString());

					GenericValue masterWalletAccount = WalletUtil.getActiveWalletAccount(delegator, data.get("masterWalletNumber").toString());
					if(UtilValidate.isNotEmpty(operatorWalletAccount))
					{

						String operatorWalletAccName = operatorWalletAccount.getString("name");
						String operatordataWalletName = (String)data.get("operatingWalletName");

						//we need to check if both name is empty
						if(UtilValidate.isNotEmpty(operatorWalletAccName) && UtilValidate.isNotEmpty(operatordataWalletName))
						{
							if(!operatorWalletAccName.equals(operatordataWalletName))
							{
								proceed = true;
							}
						}else if((UtilValidate.isEmpty(operatorWalletAccName) && UtilValidate.isNotEmpty(operatordataWalletName)) || UtilValidate.isNotEmpty(operatorWalletAccName) && UtilValidate.isEmpty(operatordataWalletName))
						{
								proceed = true;
						}
					}
					//added for master and operating account validation by m.vijayakumar@04-04-2018
					if( proceed )
					{
						if(UtilValidate.isNotEmpty(operatorWalletAccount) && UtilValidate.isNotEmpty(masterWalletAccount))
						{
							String operatingPartyId = operatorWalletAccount.getString("billingAccountId");
							String masterPartyId = masterWalletAccount.getString("billingAccountId");
							List<GenericValue> operatingBillingAcRole = delegator.findByAnd("BillingAccountRole", UtilMisc.toMap("billingAccountId",operatingPartyId), null, false);
							if(UtilValidate.isNotEmpty(operatingBillingAcRole))
							{
								List<String> masterAccounts = EntityUtil.getFieldListFromEntityList(operatingBillingAcRole, "parentBillingAccountId", true);
								if(UtilValidate.isNotEmpty(masterAccounts))
								{
									if(!masterAccounts.contains(masterPartyId))
									{

										setValidate(false);
										message = " Invalid relationship between master wallet number  [ " + data.get("masterWalletNumber") + "]" + " and operating wallet number [" + data.get("operatingWalletNumber") + "]";
										validationMessage.put("masterWalletNumber", message);
									}
								}
							}
						}

						//check for valid invalid operating with junk master
						if(UtilValidate.isNotEmpty(operatorWalletAccount) && UtilValidate.isEmpty(masterWalletAccount)){
							//Oops tring to upload existing operating to some new master so need to eject it first
							setValidate(false);
							message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.operatingWalletNumber.duplicate", Locale.ENGLISH) + " [Wallet No:" + data.get("operatingWalletNumber") + "]";
							validationMessage.put("operatingWalletNumber", message);
						}
					}else
					{
						
						//1.validation for duplicate operating account
						if (UtilValidate.isNotEmpty(operatorWalletAccount)) {
							setValidate(false);
							message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.operatingWalletNumber.duplicate", Locale.ENGLISH) + " [Wallet No:" + data.get("operatingWalletNumber") + "]" + " [Row No:" + rowNumber + "]";
							validationMessage.put("operatingWalletNumber", message);
						}
						
						
						//2. validation for duplicate master accounts 
						if(UtilValidate.isNotEmpty(masterWalletAccount))
						{
							setValidate(false);
							message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.masterWalletNumber.duplicate", Locale.ENGLISH) + " [Wallet No:" + data.get("masterWalletNumber") + "]" + " [Row No:" + rowNumber + "]";
							validationMessage.put("masterWalletNumber", message);
						}
	
					}
                    
                    
				}
				
				if (UtilValidate.isNotEmpty(data.get("operatingWalletNumber")) && UtilValidate.isEmpty(data.get("operatingWalletName"))) {
					setValidate(false);
					message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.operatingWalletName.empty", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
					validationMessage.put("operatingWalletName", message);
				}
				
				/*if (UtilValidate.isEmpty(data.get("operatingWalletName"))) {
					setValidate(false);
					message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.operatingWalletName.empty", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
					validationMessage.put("operatingWalletName", message);
				}
				
				if (UtilValidate.isEmpty(data.get("operatingWalletNumber"))) {
					setValidate(false);
					message = UtilProperties.getMessage("Etl-ProcessUiLabels.xml", "wallet.data.operatingWalletNumber.empty", Locale.ENGLISH) + " [Row No:" + rowNumber + "]";
					validationMessage.put("operatingWalletNumber", message);
				}*/
				
			}
			
			if (!isValidate()) {
				
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, "Wallet Import Faild. Please check in Error logs section for the details.");
				
			} else {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Wallet Import Faild. Please check in Error logs section for the details.");
			
			return response;
		}
		
		response.put("data", data);
		response.put("validationMessage", validationMessage);
		
		return response;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}
	
}
