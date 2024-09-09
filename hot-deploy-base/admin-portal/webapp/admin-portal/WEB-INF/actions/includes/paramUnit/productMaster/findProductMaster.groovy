import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.admin.portal.constant.AdminPortalConstant.AlertCategoryConstant
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import java.util.LinkedHashMap;
import org.fio.admin.portal.util.DataHelper;
import java.util.function.Function
import java.util.stream.Collectors
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

List<GenericValue> productDetails = delegator.findByAnd("Product", null, null, false);
context.put("productId", DataHelper.getDropDownOptions(productDetails, "productId", "productId"));

List<GenericValue> alertTypeDetails = delegator.findByAnd("Product",  null, null, false);
context.put("productNameId", DataHelper.getDropDownOptions(alertTypeDetails, "productId", "productName"));

List<GenericValue> alertNameDetails = delegator.findByAnd("ProductCategory",  null, null, false);
context.put("categoryNameId", DataHelper.getDropDownOptions(alertNameDetails, "productCategoryId", "categoryName"));

searchCriteria = request.getParameter("searchCriteria");
println('==searchCriteria==='+searchCriteria);
if(UtilValidate.isEmpty(searchCriteria)) {
	List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
	Set<String> fieldsToSelect = new TreeSet<String>();

	fieldsToSelect.add("productId");
    fieldsToSelect.add("productName");
    fieldsToSelect.add("primaryProductCategoryId");
    fieldsToSelect.add("productLevel1");
    fieldsToSelect.add("productLevel2");
    fieldsToSelect.add("productSourceSystem");
    fieldsToSelect.add("schemeCode");
    fieldsToSelect.add("isActive");
    fieldsToSelect.add("createdOn");
    fieldsToSelect.add("modifiedOn");
    fieldsToSelect.add("lastUpdatedTxStamp");
    String category=null;
    String status=null;
	
	List<GenericValue> productMasters = EntityQuery.use(delegator).select(fieldsToSelect).from("Product").orderBy("-lastUpdatedTxStamp").queryList();
    if(productMasters != null && productMasters.size() > 0)
    {
        for(GenericValue productMaster : productMasters) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("productCode", productMaster.getString("productId"));
            data.put("productsName", productMaster.getString("productName"));
            data.put("levelOne", productMaster.getString("productLevel1"));
			data.put("levelTwo", productMaster.getString("productLevel2"));
            data.put("createdOn", productMaster.getString("createdOn"));
            data.put("createdBy", productMaster.getString("createdByUserLoginId"));
			data.put("modifiedOn", productMaster.getString("modifiedOn"));
			data.put("modifiedBy", productMaster.getString("modifiedUserLoginId"));
			data.put("source", productMaster.getString("productSourceSystem"));
			data.put("scheme", productMaster.getString("schemeCode"));
			category=productMaster.getString("primaryProductCategoryId");
            if(UtilValidate.isNotEmpty(category)){
                GenericValue getCategory = EntityQuery.use(delegator).from("ProductCategory").where("productCategoryId", category).queryOne();
                if(UtilValidate.isNotEmpty(getCategory)){
                	data.put("categoryId", getCategory.getString("categoryName"));
                }
            }
            status=productMaster.getString("isActive");
            if(UtilValidate.isNotEmpty(status)){
                GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status,"enumTypeId",AlertCategoryConstant.STATUS_ID), null, false));
                if (UtilValidate.isNotEmpty(getStatus)){
                    data.put("status",getStatus.getString("description"));
                }
            }
           
            data.put("productId", productMaster.getString("productId"));
            results.add(data);
        }
    }
}else {
	context.put("searchCriteria", searchCriteria);
}