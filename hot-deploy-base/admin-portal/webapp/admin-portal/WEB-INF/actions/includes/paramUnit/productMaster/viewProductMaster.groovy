import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.Debug;
import java.util.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.fio.admin.portal.util.DataHelper;
import org.ofbiz.base.util.UtilValidate;
import java.util.List;
import org.fio.admin.portal.util.DataHelper;
import org.fio.admin.portal.constant.AdminPortalConstant;

categoryId = null;
status=null;
productId=request.getParameter("productCode");
GenericValue viewProduct = EntityQuery.use(delegator).from("Product").where("productId", productId).queryOne();
if (UtilValidate.isNotEmpty(viewProduct))
{
	context.put("productCode",productId);
	context.put("productName",viewProduct.getString("productName"));
	context.put("productOne",viewProduct.getString("productLevel1"));
	context.put("productTwo",viewProduct.getString("productLevel2"));
	context.put("sourceSystem",viewProduct.getString("productSourceSystem"));
	context.put("fundCode",viewProduct.getString("fundCode"));
	context.put("schemeCode",viewProduct.getString("schemeCode"));
	categoryId=viewProduct.getString("primaryProductCategoryId");
	if (UtilValidate.isNotEmpty(categoryId)){
		 GenericValue getCategory = EntityQuery.use(delegator).from("ProductCategory").where("productCategoryId", categoryId).queryOne();
        if(UtilValidate.isNotEmpty(getCategory)){
            	context.put("productSubCategory", getCategory.getString("categoryName"));
        }
	}
	status=viewProduct.getString("isActive");
	if (UtilValidate.isNotEmpty(status))
	{
		GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status,"enumTypeId",AdminPortalConstant.AlertCategoryConstant.STATUS_ID), null, false));
		if (UtilValidate.isNotEmpty(getStatus))
		{
			context.put("status",viewProduct.getString("description"));
		}
	}
}