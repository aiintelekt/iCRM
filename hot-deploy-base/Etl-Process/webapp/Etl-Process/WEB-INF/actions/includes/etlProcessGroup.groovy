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


String filterdProcessGroup = "";
 if(request.getParameter("filterdProcessGroup")!=null)
  {
        filterdProcessGroup=request.getParameter("filterdProcessGroup");
  }
context.put("filterdProcessGroup",filterdProcessGroup);
Debug.logInfo("filterdProcessGroup is "+filterdProcessGroup,"");

EtlProcessList = delegator.findAll("EtlProcess",false);
EtlProcessList = EntityUtil.orderBy(EtlProcessList, UtilMisc.toList("processId"));
context.put("EtlProcessList", EtlProcessList);

EtlModelGrouping = delegator.findAll("EtlModelGrouping",false);
EtlModelGrouping = EntityUtil.orderBy(EtlModelGrouping, UtilMisc.toList("sequenceNo"));
context.put("EtlModelGrouping", EtlModelGrouping);

ModelList = delegator.findAll("EtlModel",false);
ModelList = EntityUtil.orderBy(ModelList, UtilMisc.toList("modelId"));
context.put("ModelList", ModelList);

EtlProcessList = delegator.findAll("EtlProcess",false);
EtlProcessList = EntityUtil.orderBy(EtlProcessList, UtilMisc.toList("processId"));
context.put("EtlProcessList", EtlProcessList);


EtlProcessGrouping=FastList.newInstance();

if (UtilValidate.isNotEmpty(filterdProcessGroup) && !"ALL".equals(filterdProcessGroup)) {
	EntityCondition condition=EntityCondition.makeCondition("groupId",EntityOperator.EQUALS,filterdProcessGroup);
	//EtlProcessGrouping=delegator.findByCondition("EtlProcessGrouping", condition, null, UtilMisc.toList("sequenceNo"));
		EtlProcessGrouping = delegator.findAll("EtlProcessGrouping",false);
		EtlProcessGrouping = EntityUtil.filterByCondition(EtlProcessGrouping, condition);
		EtlProcessGrouping =  EntityUtil.orderBy(EtlProcessGrouping, UtilMisc.toList("sequenceNo"));
	Debug.logInfo("EtlProcessGrouping::::::if::::::","");
	context.put("EtlProcessGrouping", EtlProcessGrouping);
}else{
	EtlProcessGrouping = delegator.findAll("EtlProcessGrouping",false);
	EtlProcessGrouping = EntityUtil.orderBy(EtlProcessGrouping, UtilMisc.toList("sequenceNo"));
	Debug.logInfo("EtlProcessGrouping::::::else if::::::","");
	context.put("EtlProcessGrouping", EtlProcessGrouping);
}


/*Map processgroupserviceInput=new HashMap();
processgroupviewSize = parameters.get("PROCESSGROUP_VIEW_SIZE");
Debug.logInfo("processgroupviewSize is "+processgroupviewSize,""); 
processgroupviewIndex = parameters.get("PROCESSGROUP_VIEW_INDEX");
Debug.logInfo("processgroupviewIndex is "+processgroupviewIndex,""); 
processgroupdefaultViewSize = new java.lang.Integer(5);
Debug.logInfo("processgroupdefaultViewSize is "+processgroupdefaultViewSize,"");
//context.put("productkeyword",productkeyword);
context.put("processgroupdefaultViewSize", processgroupdefaultViewSize);


// set the limit view
Boolean processgrouplimitViewObj = request.getAttribute("processgrouplimitView");
processgrouplimitView = true;
	if (processgrouplimitViewObj != null) 
	{
	    processgrouplimitView = processgrouplimitViewObj;
	}

context.put("processgrouplimitView", processgrouplimitView);

processgroupserviceInput = UtilMisc.toMap("viewIndexString", processgroupviewIndex, "viewSizeString", processgroupviewSize, 
        "defaultViewSize", processgroupdefaultViewSize, "limitView", processgrouplimitView);

processgroupserviceInput.put("FinalRecordValues",EtlProcessGrouping);

	if(FinalRecordValues != null)
	{	
		Map processgroupcatResult = dispatcher.runSync("EtlGroupingPagination",processgroupserviceInput);

		if (processgroupcatResult != null)
		{
		    context.put("processgrouprecord", processgroupcatResult.get("subCat"));
		    context.put("processgroupviewIndex", processgroupcatResult.get("viewIndex"));
		    context.put("processgroupviewSize", processgroupcatResult.get("viewSize"));
		    context.put("processgrouplowIndex", processgroupcatResult.get("lowIndex"));
		    context.put("processgrouphighIndex", processgroupcatResult.get("highIndex"));
		    context.put("processgrouplistSize", processgroupcatResult.get("listSize"));
		    Debug.logInfo("processgroupcatResult is "+processgroupcatResult,""); 
		     
		    }
	}*/





