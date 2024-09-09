import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;

String modelId = request.getParameter("model");
uomCurrencyList = delegator.findByAnd("Uom",UtilMisc.toMap("uomTypeId","CURRENCY_MEASURE"));
context.uomCurrencyList=uomCurrencyList;
List Productresult=new ArrayList();
if(UtilValidate.isNotEmpty(modelId)){
	for(int i=1;i<=12;i++)
	{
	condition = EntityCondition.makeCondition([
	EntityCondition.makeCondition("dmTypeName", EntityOperator.EQUALS, "DMG_PRODUCT_ADDITIONAL"),
	EntityCondition.makeCondition("dmStgColName", EntityOperator.EQUALS, "ATTRIBUTE_"+i)],
	EntityOperator.AND);
	GenericValue gv = delegator.findByPrimaryKey("EtlDataMapping",UtilMisc.toMap("etlModelId",modelId,"dmTypeName","DMG_PRODUCT_ADDITIONAL","dmStgColName","ATTRIBUTE_"+i));
	if(UtilValidate.isNotEmpty(gv))
		Productresult.add(gv);
	}
	context.Productresult=Productresult;
	context.totalProductAttrRecords=12;
}


GenericValue local1Gen=delegator.findByPrimaryKey("EtlDataMapping",UtilMisc.toMap("etlModelId",modelId,"dmTypeName", "GENERAL","dmStgColName","LOCALE_PRIMARY"));
context.locale1gen=local1Gen;
if(UtilValidate.isEmpty(local1Gen)){
	 local1Gen=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName", "GENERAL","dmStgColName","LOCALE_PRIMARY"));
	context.locale1gen=local1Gen;
}
if(UtilValidate.isNotEmpty(local1Gen))
{
String locale1=(String)local1Gen.get("dmTypeValue");
context.put("locale1",locale1);

}
GenericValue local2Gen=delegator.findByPrimaryKey("EtlDataMapping",UtilMisc.toMap("etlModelId",modelId,"dmTypeName", "GENERAL","dmStgColName","LOCALE_SECONDARY"));

if(UtilValidate.isEmpty(local2Gen)){
	 local2Gen=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName", "GENERAL","dmStgColName","LOCALE_SECONDARY"));
	context.local2Gen=local2Gen;
}
    if(UtilValidate.isNotEmpty(local2Gen))
    {
    	String locale2=(String)local2Gen.get("dmTypeValue");
context.put("locale2",locale2);

    } 
 

if(UtilValidate.isEmpty(Productresult)){
	for(int i=1;i<=12;i++)
	{
	condition = EntityCondition.makeCondition([
	EntityCondition.makeCondition("dmTypeName", EntityOperator.EQUALS, "DMG_PRODUCT_ADDITIONAL"),
	EntityCondition.makeCondition("dmStgColName", EntityOperator.EQUALS, "ATTRIBUTE_"+i)],
	EntityOperator.AND);
	
	Productresult.add(delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName","DMG_PRODUCT_ADDITIONAL","dmStgColName","ATTRIBUTE_"+i)));;
	}
	context.Productresult=Productresult;
	context.totalProductAttrRecords=12; 
}

conditionModel = EntityCondition.makeCondition([
EntityCondition.makeCondition("dmTypeName", EntityOperator.EQUALS, dmTypeName),
EntityCondition.makeCondition("etlModelId", EntityOperator.EQUALS, modelId),
EntityCondition.makeCondition("dmStgColName", EntityOperator.LIKE, "DESC%")],
EntityOperator.AND);
descresult = delegator.findList("EtlDataMapping", conditionModel, null, null, null, false);
context.put("descresult",descresult);
context.totalAttrRecords=context.descresult.size(); 

if(UtilValidate.isEmpty(descresult)){
	condition = EntityCondition.makeCondition([
	EntityCondition.makeCondition("dmTypeName", EntityOperator.EQUALS, dmTypeName),
	EntityCondition.makeCondition("dmStgColName", EntityOperator.LIKE, "DESC%")],
	EntityOperator.AND);
	context.descresult = delegator.findList("DmgDataMapping", condition, null, null, null, false);
	context.totalAttrRecords=context.descresult.size();
}

largeImage=delegator.findByPrimaryKey("EtlDataMapping",UtilMisc.toMap("etlModelId",modelId,"dmTypeName",dmTypeName,"dmStgColName","IMAGE_URL_PREFIX_LARGE"));
context.put("largeImage",largeImage);
if(UtilValidate.isEmpty(largeImage))
	context.largeImage=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","IMAGE_URL_PREFIX_LARGE"));
	
DetailImage=delegator.findByPrimaryKey("EtlDataMapping",UtilMisc.toMap("etlModelId",modelId,"dmTypeName",dmTypeName,"dmStgColName","IMAGE_URL_PREFIX_DETAIL"));
context.put("DetailImage",DetailImage);
if(UtilValidate.isEmpty(DetailImage))
	context.DetailImage=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","IMAGE_URL_PREFIX_DETAIL"));
	
mediumImage=delegator.findByPrimaryKey("EtlDataMapping",UtilMisc.toMap("etlModelId",modelId,"dmTypeName",dmTypeName,"dmStgColName","IMAGE_URL_PREFIX_MEDIUM"));
context.put("mediumImage",mediumImage);
if(UtilValidate.isEmpty(mediumImage))
	context.mediumImage=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","IMAGE_URL_PREFIX_MEDIUM"));

smallImage=delegator.findByPrimaryKey("EtlDataMapping",UtilMisc.toMap("etlModelId",modelId,"dmTypeName",dmTypeName,"dmStgColName","IMAGE_URL_PREFIX_SMALL"));
context.put("smallImage",smallImage);
if(UtilValidate.isEmpty(smallImage))
	context.smallImage=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","IMAGE_URL_PREFIX_SMALL"));

uomId=delegator.findByPrimaryKey("EtlDataMapping",UtilMisc.toMap("etlModelId",modelId,"dmTypeName",dmTypeName,"dmStgColName","CURRENCY_UOM_ID"));
context.put("uomId",uomId);
if(UtilValidate.isEmpty(uomId))
	context.uomId=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","CURRENCY_UOM_ID"));

purposeId=delegator.findByPrimaryKey("EtlDataMapping",UtilMisc.toMap("etlModelId",modelId,"dmTypeName",dmTypeName,"dmStgColName","PRODUCT_PRICE_PURPOSE_ID"));
context.put("purposeId",purposeId);
if(UtilValidate.isEmpty(purposeId))
	context.purposeId=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","PRODUCT_PRICE_PURPOSE_ID"));

groupId=delegator.findByPrimaryKey("EtlDataMapping",UtilMisc.toMap("etlModelId",modelId,"dmTypeName",dmTypeName,"dmStgColName","PRODUCT_STORE_GROUP_ID"));
context.put("groupId",groupId);
if(UtilValidate.isEmpty(groupId)){
context.groupId=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","PRODUCT_STORE_GROUP_ID"));
}

univesal=delegator.findByPrimaryKey("EtlDataMapping",UtilMisc.toMap("etlModelId",modelId,"dmTypeName",dmTypeName,"dmStgColName","UNIVERSAL"));
context.put("univesal",univesal);
if(UtilValidate.isEmpty(univesal)){
context.univesal =delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","UNIVERSAL"));
}

