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
EtlGrouping = delegator.findAll("EtlGrouping",false);
EtlGrouping = EntityUtil.orderBy(EtlGrouping, UtilMisc.toList("sequenceNo"));
context.put("EtlGrouping", EtlGrouping);


EtlModelGrouping = delegator.findAll("EtlModelGrouping",false);
EtlModelGrouping = EntityUtil.orderBy(EtlModelGrouping, UtilMisc.toList("sequenceNo"));
context.put("EtlModelGrouping", EtlModelGrouping);

ModelList = delegator.findAll("EtlModel",false);
ModelList = EntityUtil.orderBy(ModelList, UtilMisc.toList("modelId"));
context.put("ModelList", ModelList);

EtlProcessList = delegator.findAll("EtlProcess",false);
EtlProcessList = EntityUtil.orderBy(EtlProcessList, UtilMisc.toList("processId"));
context.put("EtlProcessList", EtlProcessList);

EtlProcessGrouping = delegator.findAll("EtlProcessGrouping",false);
EtlProcessGrouping = EntityUtil.orderBy(EtlProcessGrouping, UtilMisc.toList("sequenceNo"));
context.put("EtlProcessGrouping", EtlProcessGrouping);

/*Map serviceInput=new HashMap();
viewSize = parameters.get("VIEW_SIZE");
Debug.logInfo("viewSize is "+viewSize,""); 
viewIndex = parameters.get("VIEW_INDEX");
Debug.logInfo("viewIndex is "+viewIndex,""); 
defaultViewSize = new java.lang.Integer(5);
Debug.logInfo("defaultViewSize is "+ViewSize,"");
//context.put("productkeyword",productkeyword);
context.put("defaultViewSize", defaultViewSize);


// set the limit view
Boolean limitViewObj = request.getAttribute("limitView");
limitView = true;
	if (limitViewObj != null) 
	{
	    limitView = limitViewObj;
	}

context.put("limitView", limitView);

serviceInput = UtilMisc.toMap("viewIndexString", viewIndex, "viewSizeString", viewSize, 
        "defaultViewSize", defaultViewSize, "limitView", limitView);

serviceInput.put("FinalRecordValues",EtlGrouping);

	if(FinalRecordValues != null)
	{	
		Map catResult = dispatcher.runSync("EtlGroupingPagination",serviceInput);

		if (catResult != null)
		{
		    context.put("record", catResult.get("subCat"));
		    context.put("viewIndex", catResult.get("viewIndex"));
		    context.put("viewSize", catResult.get("viewSize"));
		    context.put("lowIndex", catResult.get("lowIndex"));
		    context.put("highIndex", catResult.get("highIndex"));
		    context.put("listSize", catResult.get("listSize"));
		    Debug.logInfo("catResult is "+catResult,""); 
		     
		    }
	}*/
