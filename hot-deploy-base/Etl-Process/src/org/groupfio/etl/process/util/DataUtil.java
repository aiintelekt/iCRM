/**
 * 
 */
package org.groupfio.etl.process.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class DataUtil {
	
	private static String MODULE = DataUtil.class.getName();

	public static void prepareCustomerData(Map<String, Object> data) {
		
		try {
			
			if (UtilValidate.isNotEmpty(data.get("annualRevenue"))) {
				BigDecimal annualRevenue = ParamUtil.getBigDecimal(data, "annualRevenue");
				data.put("annualRevenue", annualRevenue);
			} else {
				data.put("annualRevenue", null);
			}
			
			if (UtilValidate.isNotEmpty(data.get("firstName")) && data.get("firstName").toString().length() > 255) {
				data.put("firstName", data.get("firstName").toString().substring(0, 255));
			}
			
			if (UtilValidate.isNotEmpty(data.get("lastName")) && data.get("lastName").toString().length() > 255) {
				data.put("lastName", data.get("lastName").toString().substring(0, 255));
			}
			
			if (UtilValidate.isNotEmpty(data.get("middleName")) && data.get("middleName").toString().length() > 255) {
				data.put("middleName", data.get("middleName").toString().substring(0, 255));
			}
			
			if (UtilValidate.isNotEmpty(data.get("birthDate"))) {
				Timestamp birthDate = UtilDateTime.stringToTimeStamp((String)data.get("birthDate"), "yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
				data.put("birthDate", UtilDateTime.timeStampToString(birthDate, "MM/dd/yyyy", TimeZone.getDefault(), Locale.getDefault()));
			}
			
			if (UtilValidate.isNotEmpty(data)) {
				for (String key : data.keySet()) {
					data.put(key, UtilValidate.isNotEmpty(data.get(key)) ? data.get(key) : null);
				}
			}
		} catch (Exception e) {
			Debug.logError("prepareCustomerData error: "+e.getMessage(), MODULE);
		}
		
	}
	
	public static void prepareContactData(Map<String, Object> data) {
		try {
			if (UtilValidate.isNotEmpty(data.get("firstName")) && data.get("firstName").toString().length() > 255) {
				data.put("firstName", data.get("firstName").toString().substring(0, 255));
			}
			
			if (UtilValidate.isNotEmpty(data.get("lastName")) && data.get("lastName").toString().length() > 255) {
				data.put("lastName", data.get("lastName").toString().substring(0, 255));
			}
			
			if (UtilValidate.isNotEmpty(data.get("middleName")) && data.get("middleName").toString().length() > 255) {
				data.put("middleName", data.get("middleName").toString().substring(0, 255));
			}
			
			if (UtilValidate.isNotEmpty(data.get("birthDate"))) {
				Timestamp birthDate = UtilDateTime.stringToTimeStamp((String)data.get("birthDate"), "yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
				data.put("birthDate", UtilDateTime.timeStampToString(birthDate, "MM/dd/yyyy", TimeZone.getDefault(), Locale.getDefault()));
			}
			
			if (UtilValidate.isNotEmpty(data)) {
				for (String key : data.keySet()) {
					data.put(key, UtilValidate.isNotEmpty(data.get(key)) ? data.get(key) : null);
				}
			}
		} catch (Exception e) {
			Debug.logError("prepareContactData error: "+e.getMessage(), MODULE);
		}
	}
	
	public static void prepareLeadData(Map<String, Object> data) {
		
		if (UtilValidate.isNotEmpty(data.get("annualRevenue"))) {
			BigDecimal annualRevenue = ParamUtil.getBigDecimal(data, "annualRevenue");
			data.put("annualRevenue", annualRevenue);
		} else {
			data.put("annualRevenue", null);
		}
		
		if (UtilValidate.isNotEmpty(data.get("noOfEmployees"))) {
			Long noOfEmployees = ParamUtil.getLong(data, "noOfEmployees");
			data.put("noOfEmployees", noOfEmployees);
		} else {
			data.put("noOfEmployees", null);
		}
		
		if (UtilValidate.isNotEmpty(data.get("companyName")) && data.get("companyName").toString().length() > 255) {
			data.put("companyName", data.get("companyName").toString().substring(0, 255));
		}
		
		if (UtilValidate.isNotEmpty(data.get("keyContactPerson1")) && data.get("keyContactPerson1").toString().length() > 20) {
			data.put("keyContactPerson1", data.get("keyContactPerson1").toString().substring(0, 20));
		}
		
		if (UtilValidate.isNotEmpty(data.get("keyContactPerson2")) && data.get("keyContactPerson2").toString().length() > 20) {
			data.put("keyContactPerson2", data.get("keyContactPerson2").toString().substring(0, 20));
		}
		
		if (UtilValidate.isNotEmpty(data.get("keyContactPerson2")) && data.get("keyContactPerson2").toString().length() > 20) {
			data.put("keyContactPerson2", data.get("keyContactPerson2").toString().substring(0, 20));
		}
		
		if (UtilValidate.isNotEmpty(data.get("keyContactPerson2")) && data.get("keyContactPerson2").toString().length() > 20) {
			data.put("keyContactPerson2", data.get("keyContactPerson2").toString().substring(0, 20));
		}
		
		if (UtilValidate.isNotEmpty(data)) {
			for (String key : data.keySet()) {
				data.put(key, UtilValidate.isNotEmpty(data.get(key)) ? data.get(key) : null);
			}
		}
	}

	public static void prepareAccountData(Map<String, Object> data) {
		if (UtilValidate.isNotEmpty(data.get("annualRevenue"))) {
			BigDecimal annualRevenue = ParamUtil.getBigDecimal(data, "annualRevenue");
			data.put("annualRevenue", annualRevenue);
		} else {
			data.put("annualRevenue", null);
		}
		
		if (UtilValidate.isNotEmpty(data.get("noOfEmployees"))) {
			Long noOfEmployees = ParamUtil.getLong(data, "noOfEmployees");
			data.put("noOfEmployees", noOfEmployees);
		} else {
			data.put("noOfEmployees", null);
		}
		
		if (UtilValidate.isNotEmpty(data.get("companyName")) && data.get("companyName").toString().length() > 255) {
			data.put("companyName", data.get("companyName").toString().substring(0, 255));
		}
		
		if (UtilValidate.isNotEmpty(data.get("keyContactPerson1")) && data.get("keyContactPerson1").toString().length() > 20) {
			data.put("keyContactPerson1", data.get("keyContactPerson1").toString().substring(0, 20));
		}
		
		if (UtilValidate.isNotEmpty(data.get("keyContactPerson2")) && data.get("keyContactPerson2").toString().length() > 20) {
			data.put("keyContactPerson2", data.get("keyContactPerson2").toString().substring(0, 20));
		}
		
		if (UtilValidate.isNotEmpty(data.get("keyContactPerson2")) && data.get("keyContactPerson2").toString().length() > 20) {
			data.put("keyContactPerson2", data.get("keyContactPerson2").toString().substring(0, 20));
		}
		
		if (UtilValidate.isNotEmpty(data.get("keyContactPerson2")) && data.get("keyContactPerson2").toString().length() > 20) {
			data.put("keyContactPerson2", data.get("keyContactPerson2").toString().substring(0, 20));
		}
		
		if (UtilValidate.isNotEmpty(data)) {
			for (String key : data.keySet()) {
				data.put(key, UtilValidate.isNotEmpty(data.get(key)) ? data.get(key) : null);
			}
		}
		
	}
	
	//////////////////////////////////////////////////
	
	public static void prepareSupplierData(Map<String, Object> data) {
		
	}
	
	public static void prepareInvoiceHeaderData(Map<String, Object> data) {
		
		if (UtilValidate.isNotEmpty((String) data.get("invoiceDate"))) {
			Timestamp invoiceDt = Timestamp.valueOf((String) data.get("invoiceDate"));
			data.put("invoiceDate", invoiceDt);
		}
		
		if (UtilValidate.isNotEmpty((String) data.get("dueDate"))) {
			Timestamp dueDate = Timestamp.valueOf((String) data.get("dueDate"));
			data.put("dueDate", dueDate);
		}
		
		if (UtilValidate.isNotEmpty((String) data.get("paidDate"))) {
			Timestamp paidDate = Timestamp.valueOf((String) data.get("paidDate"));
			data.put("paidDate", paidDate);
		}
		
		if (UtilValidate.isNotEmpty(data.get("adjustedAmount"))) {
			BigDecimal adjustedAmount = new BigDecimal((String) data.get("adjustedAmount"));
			data.put("adjustedAmount", adjustedAmount);
		}
		
		if (UtilValidate.isNotEmpty(data.get("appliedAmount"))) {
			BigDecimal appliedAmount = new BigDecimal((String) data.get("appliedAmount"));
			data.put("appliedAmount", appliedAmount);
		}
		
		if (UtilValidate.isNotEmpty(data.get("invoiceTotal"))) {
			BigDecimal invoiceTotal = new BigDecimal((String) data.get("invoiceTotal"));
			data.put("invoiceTotal", invoiceTotal);
		}
		
		if (UtilValidate.isNotEmpty(data.get("openAmount"))) {
			BigDecimal openAmount = new BigDecimal((String) data.get("openAmount"));
			data.put("openAmount", openAmount);
		}
		
	}
	
	public static void prepareInvoiceItemData(Map<String, Object> data) {
		
		if (UtilValidate.isNotEmpty(data.get("amount"))) {
			BigDecimal amount = new BigDecimal((String) data.get("amount"));
			data.put("amount", amount);
		}
		
		if (UtilValidate.isNotEmpty(data.get("quantity"))) {
			BigDecimal quantity = new BigDecimal((String) data.get("quantity"));
			data.put("quantity", quantity);
		}
		
	}
	
	public static void prepareProductData(Map<String, Object> data) {
		
		if (UtilValidate.isNotEmpty(data.get("weight"))) {
			BigDecimal weight = new BigDecimal((String) data.get("weight"));
			data.put("weight", weight);
		}
		
		if (UtilValidate.isNotEmpty(data.get("productLength"))) {
			BigDecimal productLength = new BigDecimal((String) data.get("productLength"));
			data.put("productLength", productLength);
		}
		
		if (UtilValidate.isNotEmpty(data.get("width"))) {
			BigDecimal width = new BigDecimal((String) data.get("width"));
			data.put("width", width);
		}
		
		if (UtilValidate.isNotEmpty(data.get("height"))) {
			BigDecimal height = new BigDecimal((String) data.get("height"));
			data.put("height", height);
		}
		
		if (UtilValidate.isNotEmpty(data.get("price"))) {
			BigDecimal price = new BigDecimal((String) data.get("price"));
			data.put("price", price);
		}
		
		if (UtilValidate.isNotEmpty(data.get("purchasePrice"))) {
			BigDecimal purchasePrice = new BigDecimal((String) data.get("purchasePrice"));
			data.put("purchasePrice", purchasePrice);
		}
		
		if (UtilValidate.isNotEmpty((String) data.get("createdDate"))) {
			Timestamp createdDate = Timestamp.valueOf((String) data.get("createdDate"));
			data.put("createdDate", createdDate);
		}
		
	}
	
	public static void prepareCategoryData(Map<String, Object> data) {
		
		
		
	}
	
	public static void prepareOrderData(Map<String, Object> data) {
		
		if (UtilValidate.isNotEmpty((String) data.get("orderDate"))) {
			Timestamp orderDate = Timestamp.valueOf((String) data.get("orderDate"));
			data.put("orderDate", orderDate);
		}
		
	}
	
	public static String getEtlFieldName(Delegator delegator, String modelName, String elementName) {
		
		GenericValue mappedElement;
		try {
			mappedElement = EntityUtil.getFirst( delegator.findByAnd("EtlMappingElements", UtilMisc.toMap("listName", modelName, "tableColumnName", elementName), null, false) );
			if (UtilValidate.isNotEmpty(mappedElement)) {
				return mappedElement.getString("etlFieldName");
			}
		} catch (Exception e) {
			Debug.logError("getMappedElementName ERROR: "+e.getMessage(), MODULE);
		}
		
		return elementName;
	}
	
	public static void prepareLockboxBatchData(Map<String, Object> data) {
		
		if (UtilValidate.isNotEmpty(data.get("totalDepositAmount"))) {
			BigDecimal totalDepositAmount = new BigDecimal((String) data.get("totalDepositAmount"));
			data.put("totalDepositAmount", totalDepositAmount);
		}
		
		if (UtilValidate.isNotEmpty(data.get("noOfCheques"))) {
			Long noOfCheques = new Long((String) data.get("noOfCheques"));
			data.put("noOfCheques", noOfCheques);
		}
		
	}
	
	public static void prepareLockboxBatchItemData(Map<String, Object> data) {
		
		if (UtilValidate.isNotEmpty(data.get("invoiceAmount"))) {
			BigDecimal invoiceAmount = new BigDecimal((String) data.get("invoiceAmount"));
			data.put("invoiceAmount", invoiceAmount);
		}
		
		if (UtilValidate.isNotEmpty(data.get("discountAmount"))) {
			BigDecimal discountAmount = new BigDecimal((String) data.get("discountAmount"));
			data.put("discountAmount", discountAmount);
		} else {
			data.put("discountAmount", null);
		}
		
		if (UtilValidate.isNotEmpty(data.get("chequeAmount"))) {
			BigDecimal chequeAmount = new BigDecimal((String) data.get("chequeAmount"));
			data.put("chequeAmount", chequeAmount);
		}
		
	}
	
	public static void prepareWalletData(Map<String, Object> data) {
		
	}
	
	public static void prepareEmplPositionData(Map<String, Object> data) {
		
		
		
	}
	
	public static String getLeadId(String prefix, String sequenceNumber){
		
		String formattedPostCode = "";
		if (UtilValidate.isNotEmpty(prefix)) {
			formattedPostCode = prefix;
		}
		
		if(UtilValidate.isNotEmpty(sequenceNumber)){
			int length = sequenceNumber.length();
			if (length==1) {
				formattedPostCode += "0000" + (sequenceNumber);
	        }
			else if (length==2) {
				formattedPostCode += "000" + (sequenceNumber);
	        }
			else if (length==3) {
				formattedPostCode += "00" + (sequenceNumber);
	        }
			else if (length==4) {
				formattedPostCode += "0" + (sequenceNumber);
			}
	        else{
	        	formattedPostCode += (sequenceNumber);
	        }
		}
		
		return formattedPostCode;
	}
	
	public static Map<String, Object> getDndStatus(Delegator delegator, String telecomNumber) {
		String dndStatus = "N";
		String solicitationStatus = "Y";
		Map<String, Object> rsponseMap = new HashMap<String, Object>();
		try {
		GenericValue dndMaster = EntityQuery.use(delegator).from("DndMaster").where("number", telecomNumber).orderBy("lastUpdatedStamp DESC").queryFirst();
		if(dndMaster != null && dndMaster.size() > 0) {
			String dndIndicator = dndMaster.getString("indicator");
			String dndSeqId = dndMaster.getString("seqId");
			if(UtilValidate.isNotEmpty(dndIndicator)) {
				rsponseMap.put("dndIndicator", dndIndicator);
				rsponseMap.put("dndSeqId", dndSeqId);
				if("A".equalsIgnoreCase(dndIndicator)) {
					solicitationStatus = "N";
					dndStatus = "Y";
				} else if("D".equalsIgnoreCase(dndIndicator)) {
					solicitationStatus = "Y";
					dndStatus = "N";
				}
			} 
		}
		} catch (GenericEntityException ex) {
			Debug.log("Exception in getDndStatus method: " +ex.getMessage());
		}
		rsponseMap.put("dndStatus", dndStatus);
		rsponseMap.put("solicitationStatus", solicitationStatus);
		return rsponseMap;
	}
	
	public static boolean validateDndAuditLogDetails(Delegator delegator, String telecomNumber, String partyId, String dndIndicator) {
		Boolean dndValidation = false;
		try {
		GenericValue dndAuditLogDetails = EntityQuery.use(delegator).from("DndAuditLogDetails")
				.where("partyId", partyId, "dndNumber", telecomNumber, "dndIndicator", dndIndicator)
				.queryFirst();
		if(dndAuditLogDetails == null || dndAuditLogDetails.size() < 1) {
			dndValidation = true;
		}
		} catch (GenericEntityException ex) {
			Debug.log("Exception in validateDndAuditLogDetails method: " +ex.getMessage());
		}
		
		return dndValidation;
	}
	
    @SuppressWarnings("unchecked")
    public static GenericValue makeDndAuditLogDetails(String dndSeqId, String partyId, String changeStatus, String dndNumber, String dndIndicator, Timestamp now, Delegator delegator) {
        Map<String, Object> dndAuditLogDetails = FastMap.newInstance();
        dndAuditLogDetails.put("seqId", delegator.getNextSeqId("DndAuditLogDetails"));
        dndAuditLogDetails.put("partyId", partyId);
        dndAuditLogDetails.put("dndSeqId", dndSeqId);
        dndAuditLogDetails.put("changeStatus", changeStatus);
        dndAuditLogDetails.put("dndNumber", dndNumber);
        dndAuditLogDetails.put("dndIndicator", dndIndicator);
        dndAuditLogDetails.put("changeDate", now);
        return delegator.makeValue("DndAuditLogDetails", dndAuditLogDetails);
    }
    
    public static <T> List<T> getFieldListFromMapList(List<Map<String, Object>> genericValueList, String fieldName, boolean distinct) {
        if (genericValueList == null || fieldName == null) {
            return null;
        }
        List<T> fieldList = new LinkedList<T>();
        Set<T> distinctSet = null;
        if (distinct) {
            distinctSet = new HashSet<T>();
        }

        for (Map<String, Object> value: genericValueList) {
            T fieldValue = UtilGenerics.<T>cast(value.get(fieldName));
            if (fieldValue != null) {
                if (distinct) {
                    if (!distinctSet.contains(fieldValue)) {
                        fieldList.add(fieldValue);
                        distinctSet.add(fieldValue);
                    }
                } else {
                    fieldList.add(fieldValue);
                }
            }
        }

        return fieldList;
    }
    
	public static void prepareProductSupplementaryData(Map<String, Object> data) {
		if (UtilValidate.isNotEmpty((String) data.get("offerDate"))) {
			Timestamp offerDate = Timestamp.valueOf((String) data.get("offerDate"));
			data.put("offerDate", offerDate);
		}
		if (UtilValidate.isNotEmpty((String) data.get("expirationDate"))) {
			Timestamp expirationDate = Timestamp.valueOf((String) data.get("expirationDate"));
			data.put("expirationDate", expirationDate);
		}
	}
	
	public static void prepareItmData(Map<String, Object> data) {
		if (UtilValidate.isNotEmpty((String) data.get("invoiceDate"))) {
			Timestamp invoiceDate = Timestamp.valueOf((String) data.get("invoiceDate"));
			data.put("invoiceDate", invoiceDate);
		}
		if (UtilValidate.isNotEmpty((String) data.get("returnedDate"))) {
			Timestamp returnedDate = Timestamp.valueOf((String) data.get("returnedDate"));
			data.put("returnedDate", returnedDate);
		}
		
		if (UtilValidate.isNotEmpty(data.get("totalSalesAmount"))) {
			BigDecimal totalSalesAmount = new BigDecimal((String) data.get("totalSalesAmount"));
			data.put("totalSalesAmount", totalSalesAmount);
		}
		if (UtilValidate.isNotEmpty(data.get("totalTenderAmount_1"))) {
			BigDecimal totalTenderAmount_1 = new BigDecimal((String) data.get("totalTenderAmount_1"));
			data.put("totalTenderAmount_1", totalTenderAmount_1);
		}
		if (UtilValidate.isNotEmpty(data.get("totalTenderAmount_2"))) {
			BigDecimal totalTenderAmount_2 = new BigDecimal((String) data.get("totalTenderAmount_2"));
			data.put("totalTenderAmount_2", totalTenderAmount_2);
		}
		if (UtilValidate.isNotEmpty(data.get("totalTaxAmount"))) {
			BigDecimal totalTaxAmount = new BigDecimal((String) data.get("totalTaxAmount"));
			data.put("totalTaxAmount", totalTaxAmount);
		}
		if (UtilValidate.isNotEmpty(data.get("unitRetail"))) {
			BigDecimal unitRetail = new BigDecimal((String) data.get("unitRetail"));
			data.put("unitRetail", unitRetail);
		}
		if (UtilValidate.isNotEmpty(data.get("unitCost"))) {
			BigDecimal unitCost = new BigDecimal((String) data.get("unitCost"));
			data.put("unitCost", unitCost);
		}
		if (UtilValidate.isNotEmpty(data.get("quantitySold"))) {
			BigDecimal quantitySold = new BigDecimal((String) data.get("quantitySold"));
			data.put("quantitySold", quantitySold);
		}
		if (UtilValidate.isNotEmpty(data.get("itemNetSales"))) {
			BigDecimal itemNetSales = new BigDecimal((String) data.get("itemNetSales"));
			data.put("itemNetSales", itemNetSales);
		}
		if (UtilValidate.isNotEmpty(data.get("extendedDiscount"))) {
			BigDecimal extendedDiscount = new BigDecimal((String) data.get("extendedDiscount"));
			data.put("extendedDiscount", extendedDiscount);
		}
		if (UtilValidate.isNotEmpty(data.get("totalLineTaxAmount"))) {
			BigDecimal totalLineTaxAmount = new BigDecimal((String) data.get("totalLineTaxAmount"));
			data.put("totalLineTaxAmount", totalLineTaxAmount);
		}
		if (UtilValidate.isNotEmpty(data.get("discountAmount"))) {
			BigDecimal discountAmount = new BigDecimal((String) data.get("discountAmount"));
			data.put("discountAmount", discountAmount);
		}
		if (UtilValidate.isNotEmpty(data.get("numberOfReturns"))) {
			BigDecimal numberOfReturns = new BigDecimal((String) data.get("numberOfReturns"));
			data.put("numberOfReturns", numberOfReturns);
		}
		if (UtilValidate.isNotEmpty(data.get("totalReturnedAmount"))) {
			BigDecimal totalReturnedAmount = new BigDecimal((String) data.get("totalReturnedAmount"));
			data.put("totalReturnedAmount", totalReturnedAmount);
		}
		if (UtilValidate.isNotEmpty(data.get("totalAdjustmentNonTaxAndDiscount"))) {
			BigDecimal totalAdjustmentNonTaxAndDiscount = new BigDecimal((String) data.get("totalAdjustmentNonTaxAndDiscount"));
			data.put("totalAdjustmentNonTaxAndDiscount", totalAdjustmentNonTaxAndDiscount);
		}
		
	}
	
	public static void prepareActivityData(Map<String, Object> data) {
		if (UtilValidate.isNotEmpty((String) data.get("estimatedStartDate"))) {
			Timestamp estimatedStartDate = Timestamp.valueOf((String) data.get("estimatedStartDate"));
			data.put("estimatedStartDate", estimatedStartDate);
		}
		if (UtilValidate.isNotEmpty((String) data.get("estimatedCompletionDate"))) {
			Timestamp estimatedCompletionDate = Timestamp.valueOf((String) data.get("estimatedCompletionDate"));
			data.put("estimatedCompletionDate", estimatedCompletionDate);
		}
		
	}
	
	
}
