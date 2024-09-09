/*
 * Copyright (c) Open Source Strategies, Inc.
 * 
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */
/* Copyright (c) Open Source Strategies, Inc. */

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.Debug;

import javolution.util.FastList;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilValidate;

custRequestId = parameters.get("custRequestId");
String filterdGroup = "";
 if(request.getParameter("filterdGroup")!=null)
  {
       filterdGroup=request.getParameter("filterdGroup");
  }

context.put("filterdGroup",filterdGroup);
Debug.logInfo("filterdGroup is "+filterdGroup,"");


ModelList = delegator.findAll("EtlModel",false);
ModelList = EntityUtil.orderBy(ModelList, UtilMisc.toList("modelId"));
context.put("ModelList", ModelList);

EtlModelGrouping=FastList.newInstance();

if (UtilValidate.isNotEmpty(filterdGroup) && !"ALL".equals(filterdGroup)) {
	EntityCondition cond=EntityCondition.makeCondition("groupId",EntityOperator.EQUALS,filterdGroup);
	//EtlModelGrouping=delegator.findByCondition("EtlModelGrouping", cond, null, UtilMisc.toList("sequenceNo"));
		EtlModelGrouping = delegator.findAll("EtlModelGrouping",false);
		EtlModelGrouping = EntityUtil.filterByCondition(EtlModelGrouping, cond);
		EtlModelGrouping =  EntityUtil.orderBy(EtlModelGrouping, UtilMisc.toList("sequenceNo"));
	Debug.logInfo("EtlModelGrouping::::::if::::::","");
	context.put("EtlModelGrouping", EtlModelGrouping);
}else{
	EtlModelGrouping = delegator.findAll("EtlModelGrouping",false);
	EtlModelGrouping = EntityUtil.orderBy(EtlModelGrouping, UtilMisc.toList("sequenceNo"));
	Debug.logInfo("EtlModelGrouping::::::else if::::::",""+EtlModelGrouping);
	context.put("EtlModelGrouping", EtlModelGrouping);
}

ModelList = delegator.findAll("EtlModel",false);
ModelList = EntityUtil.orderBy(ModelList, UtilMisc.toList("modelId"));
context.put("ModelList", ModelList);

EtlProcessList = delegator.findAll("EtlProcess",false);
EtlProcessList = EntityUtil.orderBy(EtlProcessList, UtilMisc.toList("processId"));
context.put("EtlProcessList", EtlProcessList);

EtlProcessGrouping = delegator.findAll("EtlProcessGrouping",false);
EtlProcessGrouping = EntityUtil.orderBy(EtlProcessGrouping, UtilMisc.toList("sequenceNo"));
context.put("EtlProcessGrouping", EtlProcessGrouping);

/*Map modelgroupserviceInput=new HashMap();
modelgroupviewSize = parameters.get("MODELGROUP_VIEW_SIZE");
Debug.logInfo("modelgroupviewSize is "+modelgroupviewSize,""); 
modelgroupviewIndex = parameters.get("MODELGROUP_VIEW_INDEX");
Debug.logInfo("modelgroupviewIndex is "+modelgroupviewIndex,""); 
modelgroupdefaultViewSize = new java.lang.Integer(5);
Debug.logInfo("modelgroupdefaultViewSize is "+modelgroupdefaultViewSize,"");
//context.put("productkeyword",productkeyword);
context.put("modelgroupdefaultViewSize", modelgroupdefaultViewSize);


// set the limit view
Boolean modelgrouplimitViewObj = request.getAttribute("modelgrouplimitView");
modelgrouplimitView = true;
	if (modelgrouplimitViewObj != null) 
	{
	    modelgrouplimitView = modelgrouplimitViewObj;
	}

context.put("modelgrouplimitView", modelgrouplimitView);

modelgroupserviceInput = UtilMisc.toMap("viewIndexString", modelgroupviewIndex, "viewSizeString", modelgroupviewSize, 
        "defaultViewSize", modelgroupdefaultViewSize, "limitView", modelgrouplimitView);

modelgroupserviceInput.put("FinalRecordValues",EtlModelGrouping);

	if(FinalRecordValues != null)
	{	
		Map modelgroupcatResult = dispatcher.runSync("EtlGroupingPagination",modelgroupserviceInput);

		if (catResult != null)
		{
		    context.put("modelgrouprecords", modelgroupcatResult.get("subCat"));
		    context.put("modelgroupviewIndex", modelgroupcatResult.get("viewIndex"));
		    context.put("modelgroupviewSize", modelgroupcatResult.get("viewSize"));
		    context.put("modelgrouplowIndex", modelgroupcatResult.get("lowIndex"));
		    context.put("modelgrouphighIndex", modelgroupcatResult.get("highIndex"));
		    context.put("modelgrouplistSize", modelgroupcatResult.get("listSize"));
		    Debug.logInfo("modelgroupcatResult is "+modelgroupcatResult,""); 
		     
		    }
	}*/





