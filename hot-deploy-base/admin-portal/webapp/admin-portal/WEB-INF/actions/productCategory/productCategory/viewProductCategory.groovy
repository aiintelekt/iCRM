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

import org.fio.admin.portal.constant.AdminPortalConstant
import org.fio.admin.portal.util.DataHelper;
import org.apache.commons.lang.StringUtils;


delegator = request.getAttribute("delegator");

inputContext = new LinkedHashMap<String, Object>();

String productCategoryId = request.getParameter("productCategoryId");
String prodCatalogId = request.getParameter("prodCatalogId");

if(UtilValidate.isNotEmpty(prodCatalogId)&&UtilValidate.isNotEmpty(productCategoryId)) {
	productCategory = EntityUtil.getFirst(delegator.findByAnd("ProdCatalogCategory", UtilMisc.toMap("prodCatalogId", prodCatalogId,"productCategoryId",productCategoryId),["fromDate DESC"], false));
	if (productCategory != null) {
		inputContext.put("prodCatalogId", productCategory.getString("prodCatalogId"));
		inputContext.put("productCategoryId", productCategory.getString("productCategoryId"));
		inputContext.put("sequenceNumber", productCategory.getString("sequenceNum"));
		isEnable = "Y"
		if(UtilValidate.isNotEmpty(productCategory.getString("thruDate")))
			isEnable = "N"
		inputContext.put("isEnable",isEnable );
		productCategoryName = EntityUtil.getFirst(delegator.findByAnd("ProductCategory", UtilMisc.toMap("productCategoryId", productCategory.getString("productCategoryId")), null, false));
		
		if(productCategoryName !=null) {
			inputContext.put("categoryName", productCategoryName.getString("categoryName"));
		}
	}
}


context.put("inputContext", inputContext);

