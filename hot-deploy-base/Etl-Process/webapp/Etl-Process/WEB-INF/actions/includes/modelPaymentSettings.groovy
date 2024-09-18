

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.opentaps.common.util.UtilCommon;
import org.opentaps.common.util.UtilMessage;
import org.opentaps.domain.DomainsLoader;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.ofbiz.Repository;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;

String modelId = parameters.get("model");

//System.out.println("modelId====="+modelId);
String storeId ="";

if(UtilValidate.isNotEmpty(modelId)){
	findModel = delegator.findByPrimaryKey("EtlModel",UtilMisc.toMap("modelId",modelId));
	checkStore = EntityUtil.getFirst(delegator.findByAnd("EtlDefaultsMapping",UtilMisc.toMap("model",findModel.getString("modelName"),"etlFieldName","storeId")));
	if(UtilValidate.isNotEmpty(checkStore)){
		context.put("storeId",checkStore.getString("defaultValue"));
	}
}
