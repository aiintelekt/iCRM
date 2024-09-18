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

condition = EntityCondition.makeCondition([
EntityCondition.makeCondition("dmTypeName", EntityOperator.EQUALS, dmTypeName),
EntityCondition.makeCondition("dmStgColName", EntityOperator.LIKE, "ATTR%")],
EntityOperator.AND);
context.leadresult = delegator.findList("DmgDataMapping", condition, null, null, null, false);
context.totalAttrRecords=context.leadresult.size(); 


conditionCustField = EntityCondition.makeCondition([
EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MY_LEAD")],
EntityOperator.AND);
context.AccountresultCustField = delegator.findList("FioCustomFieldTemplate", conditionCustField, null, null, null, false);

context.dmgCustAttr1 = delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_1"));
context.dmgCustAttr2 = delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_2"));
context.dmgCustAttr3 = delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_3"));
context.dmgCustAttr4 = delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_4"));
context.dmgCustAttr5 = delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_5"));