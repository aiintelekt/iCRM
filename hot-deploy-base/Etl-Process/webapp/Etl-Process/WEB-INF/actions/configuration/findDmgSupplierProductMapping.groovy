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


context.firstGroupStoreId=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","FIRST_PRODUCT_STORE_GROUP_ID"));
context.secondGroupStoreId=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","SECOND_PRODUCT_STORE_GROUP_ID"));
context.thirdGroupStoreId=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","THIRD_PRODUCT_STORE_GROUP_ID"));
context.fourthGroupStoreId=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","FOURTH_PRODUCT_STORE_GROUP_ID"));
context.fifthGroupStoreId=delegator.findByPrimaryKey("DmgDataMapping",UtilMisc.toMap("dmTypeName",dmTypeName,"dmStgColName","FIFTH_PRODUCT_STORE_GROUP_ID"));

