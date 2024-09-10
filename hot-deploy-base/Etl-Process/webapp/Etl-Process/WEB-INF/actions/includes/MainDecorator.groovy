
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityFindOptions
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.groupfio.etl.process.util.DefaultValueUtil;
import org.ofbiz.base.util.UtilProperties;

listName = request.getParameter("listName");
if(UtilValidate.isEmpty(listName)){
	listName = parameters.get("listId");
}
if(UtilValidate.isEmpty(listName)){
	listName = request.getParameter("csvListName");
}
if(UtilValidate.isEmpty(listName)){
	listName = request.getAttribute("listName");
}

if(UtilValidate.isNotEmpty(listName))
{
	etlMappingElements = delegator.findByAnd("EtlMappingElements",UtilMisc.toMap("listName",listName),null,false);
	if(UtilValidate.isNotEmpty(etlMappingElements))
	{
		context.put("etlMappingElements",etlMappingElements);
	
		//for getting the table name Note: this is only for table is associated with ONLY ONCE
		etlTableName_etl = "";
		for(GenericValue etlMappingElement:etlMappingElements)
		{
			if(UtilValidate.isNotEmpty(etlMappingElement.getString("tableName")))
			{
				etlTableName_etl = etlMappingElement.getString("tableName");
			
			}
		}
	
		context.put("editTableName",etlTableName_etl);
		context.put("editEtl","Y");
		//end @vijayakumar
	}
	
	etlModel = EntityUtil.getFirst(delegator.findByAnd("EtlModel", UtilMisc.toMap("modelName", listName),null,false));
	context.put("etlModel", etlModel);
	
}
	//colors
	List containerColors = new ArrayList();
	containerColors.add("blue");
	containerColors.add("green-turquoise");
	containerColors.add("blue-dark");
	containerColors.add("yellow-casablanca");
	containerColors.add("green-jungle");
	containerColors.add("purple-intense");
	containerColors.add("red-soft");
	int j=0;
	context.put("containerColors",containerColors);
	//getting the available lists
	
	groupByFilter = request.getParameter("groupByFilter");
	Debug.logInfo("The filter by group is "+groupByFilter,"");
	
	String etlDestTableName = request.getParameter("etlDestTableName");
	Set etlSet = null;
	if(UtilValidate.isNotEmpty(etlDestTableName))
	{
		etlMappListOnly = delegator.findByAnd("EtlSourceTable",UtilMisc.toMap("tableName",etlDestTableName),null,false);
		context.put("etlDestTableName",etlDestTableName);
	}else
	{
		etlMappListOnly = delegator.findAll("EtlSourceTable",false);
	}
	if(UtilValidate.isNotEmpty(groupByFilter))
	{
		etlModelGrouping  = delegator.findByAnd("EtlModelGrouping",UtilMisc.toMap("groupId",groupByFilter),null,false);
		if(UtilValidate.isNotEmpty(etlModelGrouping))
		{
			List<String> etlAvailGroup = EntityUtil.getFieldListFromEntityList(etlModelGrouping,"modelName",true);
			//etlMappListOnly = delegator.findByCondition("EtlSourceTable",new EntityExpr("listName",EntityOperator.IN,etlAvailGroup),null,null);
			etlMappListOnly = delegator.findAll("EtlSourceTable",false); //new EntityExpr("listName",EntityOperator.IN,etlAvailGroup),null,null);
			etlMappListOnly = EntityUtil.filterByCondition(etlMappListOnly, new EntityExpr("listName",EntityOperator.IN,etlAvailGroup));
			Debug.logInfo("with etl resouce table data "+etlMappListOnly,"");
			
			context.put("groupByFilter",groupByFilter);	
		}
	}
	
	//end @vijayakumar
	
	filterBy = request.getParameter("filterBy");
	if(UtilValidate.isNotEmpty(etlMappListOnly))
	{
		List etlList = new ArrayList();
		for(GenericValue g:etlMappListOnly)
		{
		
		
		Map listAttribute = new HashMap();
		listAttribute.put("listName",g.getString("listName"));
		listAttribute.put("tableName",g.getString("tableName"));
			if(UtilValidate.isNotEmpty(filterBy))
			{
				if(filterBy.equals("Assigned"))
				{
					etlProcess = delegator.findByAnd("EtlProcess",UtilMisc.toMap("modalName",g.getString("listName")),null,false);
					if(UtilValidate.isNotEmpty(etlProcess))
					{
					etlList.add(listAttribute);
					}
				}
				
				if(filterBy.equals("Unassigned"))
				{
					etlProcess = delegator.findByAnd("EtlProcess",UtilMisc.toMap("modalName",g.getString("listName")),null,false)
					if(UtilValidate.isEmpty(etlProcess))
					{
						etlList.add(listAttribute);
					}
				}
				if(filterBy.equals("All"))
				{
					etlList.add(listAttribute);
				}
			}else
			{
				etlList.add(listAttribute);
			}
		
	
		}
		 etlSet = new LinkedHashSet(etlList);
		context.put("etlSet",etlSet);
	}


	
	context.put("listName",listName);
	
	
	
	//to get etl_destination table data only for distinct value
	//eltDestination = delegator.findByAnd("EtlDestination",null,UtilMisc.toList("tableName","tableTitle"),false);
	eltDestination = delegator.findList("EtlDestination",null,UtilMisc.toSet("tableName","tableTitle"),null,null,false);
	if(UtilValidate.isNotEmpty(eltDestination))
	{
		if(UtilValidate.isNotEmpty(eltDestination))
		{
			HashSet etlDestinationSet =  new HashSet(eltDestination);
			context.put("eltTableNameList",new ArrayList(etlDestinationSet));
		}
	}
		
			
			
			
			//for the purpose of getting the list name
			forListName = delegator.findByAnd("EtlMappingElements",UtilMisc.toMap("listName",listName),null,false);
			
			if(UtilValidate.isNotEmpty(forListName))
			{
				listDb = EntityUtil.getFirst(forListName);
				context.put("listNameDb",listDb);
				
				modelDefaultRangeList = delegator.findByAnd("EtlModelDefaultRange", UtilMisc.toMap("modelName", listName),null,false);
				context.put("modelDefaultRangeList", modelDefaultRangeList);
				
				textDelimiter = DefaultValueUtil.getTextDelimiter(listName, delegator);
				context.put("textDelimiter", textDelimiter);
				
				sftpConfig = EntityUtil.getFirst(delegator.findByAnd("SftpConfiguration", UtilMisc.toMap("modelName", listName),null,false));
				context.put("sftpConfig", sftpConfig);
				
				modelDefault = new HashMap<String, Object>();
				modelDefaults = delegator.findByAnd("EtlModelDefaults", UtilMisc.toMap("modelName", listName),null,false);
				if(UtilValidate.isNotEmpty(modelDefaults)) { 
					for (GenericValue md : modelDefaults) {
						modelDefault.put(md.getString("propertyName"), md.getString("propertyValue"));
					}
				}
				context.put("modelDefault", modelDefault);	
				System.out.println(modelDefault);
				
				modelFolderName = etlModel.getString("modelId").concat("-").concat(etlModel.getString("modelName")).concat("-").concat(etlModel.getString("groupId"));
				sftpImportLocation = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.sftp.import.location");
				sftpImportLocation = sftpImportLocation + modelFolderName;
				context.put("sftpImportLocation", sftpImportLocation);
				
			}
		

	
	
	//for gettting etl process table
	etlProcess = delegator.findAll("EtlProcess",false);
	if(UtilValidate.isNotEmpty(etlProcess))
	{
		context.put("etlProcess",etlProcess);
	}
	

			
			
//Palanivel
String processId = request.getParameter("processId");
String processName = request.getParameter("processName");

context.put("processId",processId);
context.put("processName",processName);


int i=0;
etlProcessTableName = request.getParameter("etlProcessTableName");
Debug.logInfo("etoososo"+etlProcessTableName,"");
if(UtilValidate.isNotEmpty(etlProcessTableName))
{
	//processList = delegator.findByAnd("EtlProcess",new EntityExpr("tableName",etlProcessTableName),null,false);
	processList = delegator.findByAnd("EtlProcess",UtilMisc.toMap("tableName", etlProcessTableName),null,false);
	context.put("etlProcessTableName",etlProcessTableName);
}else
{
processList = delegator.findAll("EtlProcess",false);
}


if(UtilValidate.isNotEmpty(processList)){
	for(GenericValue gv : processList){
		gv.put("tableName", containerColors.get(i));
		i++;
		if(i==7)
		i=0;
	}
}
context.put("processList",processList);
	 
context.put("containerColors", containerColors);

active_tab = request.getParameter("active_tab");
Debug.logInfo("Teh activ essss "+active_tab,"");
context.put("active_tab",active_tab);



//added by m.vijayakumar date:20/05/2016
execute_table_name = request.getParameter("execute_table_name");

Debug.logInfo("Teh got execute table name is "+execute_table_name,"");



//page title fix for all
title = request.getParameter("title");
if(UtilValidate.isNotEmpty(title))
{
	//etlTitle = delegator.findByPrimaryKey("EtlTitle",UtilMisc.toMap("Id",title));
	etlTitle = EntityQuery.use(delegator).from("EtlTitle").where("Id", title).cache().queryFirst();
	if(UtilValidate.isNotEmpty(etlTitle))
	{
		context.put("titleProperty",etlTitle.getString("pageTitle"));
	}
	
}

	
	findOptions = new EntityFindOptions();
	findOptions.setDistinct(true);
	etlPr_ocess = delegator.findList("EtlDestination", new EntityExpr("tableName",EntityOperator.NOT_EQUAL,""), UtilMisc.toSet("tableName","tableTitle"), null, findOptions, false)
	if(UtilValidate.isNotEmpty(etlPr_ocess))
	{
			context.put("etlPr_ocess",etlPr_ocess);
	}
	
//palanivel
	ArrayList al = new ArrayList();
	String model = request.getParameter("model");
	if(UtilValidate.isNotEmpty(model)){
		fieldList = delegator.findByAnd("EtlSourceTable",UtilMisc.toMap("listName",model),null,false);
		if(UtilValidate.isNotEmpty(fieldList)){
			for(GenericValue fields : fieldList){
				String tableColumn = fields.getString("tableColumnName");
				String table = fields.getString("tableName");
				GenericValue gg = EntityUtil.getFirst(delegator.findByAnd("EtlDefaultsConfig",UtilMisc.toMap("etlFieldName",tableColumn,"etlTableName",table),null,false));
				Map fieldMap = new HashMap();
				if(UtilValidate.isNotEmpty(gg)){
					fieldMap.put("fieldName",tableColumn);
					fieldMap.put("table",table);
					al.add(fieldMap);
					}
				}
			}
		}
		context.put("defaultFields",al);
		
//Palanivel :error Logs	
String errorTable = parameters.get("etlDestTableName");

//System.out.println("++++++++errorTable+++++++"+errorTable);

//added by m.vijayakumar date:07/06/2016 for the purpose of getting error log details based on model name
String errLogModel = request.getParameter("model");
String etlTask = request.getParameter("etlDestTableName");
	
	//UtilValidate.isNotEmpty(errorTable) && 
Debug.logInfo("The got error log model is "+errLogModel+"----------"+etlTask,"");
if(UtilValidate.isEmpty(errLogModel) && UtilValidate.isEmpty(etlTask)){
		errorList = delegator.findAll("EtlLogProcError",false);
		errorList = EntityUtil.orderBy(errorList, UtilMisc.toList("timeStamp DESC"));
		context.put("errorLogs",errorList);
		context.put("etlModelName",errLogModel);
}else
{

	//log conditions
	conditionList = new ArrayList();
	if(UtilValidate.isNotEmpty(errLogModel))
	{
		conditionList.add(new EntityExpr("listId",EntityOperator.EQUALS,errLogModel));
	}
	
	if(UtilValidate.isNotEmpty(etlTask))
	{
		conditionList.add(new EntityExpr("tableName",EntityOperator.EQUALS,etlTask));
	}
	
	if(UtilValidate.isNotEmpty(conditionList))
	{
		EntityConditionList entityConditionList = new EntityConditionList(conditionList,EntityOperator.AND);
		//errorList = delegator.findByCondition("EtlLogProcError",entityConditionList,null,null);
		
		errorList = delegator.findAll("EtlLogProcError",false);
		errorList = EntityUtil.orderBy(errorList, UtilMisc.toList("timeStamp DESC"));
		errorList = EntityUtil.filterByCondition(errorList, entityConditionList);
		
		context.put("errorLogs",errorList);
		context.put("etlModelName",errLogModel);	
	}
	
	
	//end of log conditions
	
}
//Group 
String groupId = request.getParameter("groupId");
if(UtilValidate.isNotEmpty(groupId) && !"DEFAULT".equals(groupId) && UtilValidate.isEmpty(etlProcessTableName)){
	List groupModelList = new ArrayList();
	List<GenericValue> groupModel = null;
	if(UtilValidate.isEmpty(etlDestTableName))
	{
	 groupModel = delegator.findByAnd("EtlModelGrouping",UtilMisc.toMap("groupId",groupId),null,false);
	 groupModel = EntityUtil.orderBy(groupModel, UtilMisc.toList("sequenceNo"));
	}
	 else
	 {
	  groupModel = delegator.findByAnd("EtlModelGrouping",UtilMisc.toMap("groupId",groupId,"tableName",etlDestTableName),null,false);
	   groupModel = EntityUtil.orderBy(groupModel, UtilMisc.toList("sequenceNo"));
	 }
	
	
	Debug.logInfo("The group model details are "+groupModel,"");
	
	if(UtilValidate.isNotEmpty(groupModel)){
		for(GenericValue gModel :groupModel){
			Map listGroup = new HashMap();
			listGroup.put("listName",gModel.getString("modelName"));
			listGroup.put("tableName",gModel.getString("tableName"));
			listGroup.put("sequenceNo",gModel.getString("sequenceNo"));
			groupModelList.add(listGroup);
			
		}
		
	}
	Set etlGroupSet = new LinkedHashSet(groupModelList);
	context.put("etlSet",etlGroupSet);
}else{
	context.put("etlSet",etlSet);
}

if(UtilValidate.isNotEmpty(groupId) && !"DEFAULT".equals(groupId)){
	List groupProcessList = new ArrayList();
	List<GenericValue> groupProcess = null;
	if(UtilValidate.isEmpty(etlProcessTableName))
	{
	 	//groupProcess = delegator.findByCondition("EtlProcessGrouping",new EntityExpr("groupId",EntityOperator.EQUALS,groupId),null,UtilMisc.toList("sequenceNo ASC"));
		groupProcess = delegator.findAll("EtlProcessGrouping",false);
		groupProcess = EntityUtil.filterByCondition(groupProcess, new EntityExpr("groupId",EntityOperator.EQUALS,groupId));
		groupProcess =  EntityUtil.orderBy(groupProcess, UtilMisc.toList("sequenceNo ASC"));
		
		}
	 else
	  {
		  groupProcess = delegator.findByAnd("EtlProcessGrouping",UtilMisc.toMap("groupId",groupId,"tableName",etlProcessTableName),null,false);
		  groupProcess =  EntityUtil.orderBy(groupProcess, UtilMisc.toList("sequenceNo"));
	  }
	if(UtilValidate.isNotEmpty(groupProcess)){
		for(GenericValue gv : groupProcess){
		gv.put("tableName", containerColors.get(i));
		i++;
		if(i==7)
		i=0;
	}
		
	}
	context.put("processList",groupProcess);
	
}else{
	context.put("processList",processList);
}
	
if(UtilValidate.isNotEmpty(groupId)){
	dataMappingModels = delegator.findByAnd("EtlModelGrouping",UtilMisc.toMap("groupId",groupId),null,false);
	context.put("dataMappingModels",dataMappingModels);
}	

//Upload Request
String uploadModel = request.getParameter("model");
String uploadTable ="";
if(UtilValidate.isNotEmpty(uploadModel)){
	GenericValue findTable = EntityUtil.getFirst(delegator.findByAnd("EtlSourceTable",UtilMisc.toMap("listName",uploadModel),null,false));
	if(UtilValidate.isNotEmpty(findTable)){
		uploadTable =  findTable.getString("tableName");
	}
	GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
				if(UtilValidate.isNotEmpty(checkUploadRequest)){
					context.put("result","lock");
				}
	/*if("DmgProduct".equals(uploadTable)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","PRODUCT","status","RUNNING"),null,false));
				if(UtilValidate.isNotEmpty(checkImportType)){
					context.put("result","lock");
				}
				
			}else if("DmgCategory".equals(uploadTable)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","CATEGORY","status","RUNNING"),null,false));
				if(UtilValidate.isNotEmpty(checkImportType)){
					context.put("result","lock");
				}
				
			}else if("DmgPartySupplier".equals(uploadTable)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","SUUPLIER","status","RUNNING"),null,false));
				if(UtilValidate.isNotEmpty(checkImportType)){
					context.put("result","lock");
				}
				
			}else if("DmgSupplierProduct".equals(uploadTable)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","SUPPLIERPRODUCT","status","RUNNING"),null,false));
				if(UtilValidate.isNotEmpty(checkImportType)){
					context.put("result","lock");
				}
				
			}else if("DmgKitProductAssociate".equals(uploadTable)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","PRODUCTASSOC","status","RUNNING"),null,false));
				if(UtilValidate.isNotEmpty(checkImportType)){
					context.put("result","lock");
				}
				
			}else if("DmgPartyLead".equals(uploadTable)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","LEAD","status","RUNNING"),null,false));
				if(UtilValidate.isNotEmpty(checkImportType)){
					context.put("result","lock");
				}
				
			}else if("DmgPartyCustomer".equals(uploadTable)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","CUSTOMER","status","RUNNING"),null,false));
				if(UtilValidate.isNotEmpty(checkImportType)){
					context.put("result","lock");
				}
				
			}	*/
}
	
	
	
	//added by m.vijayakumar date:15/06/2016 for group based check box list
	
	/*if(UtilValidate.isNotEmpty(groupId))
	{
		session.setAttribute("groupId", groupId);
	}else
	{
		groupId = session.getAttribute("groupId");
	}*/
	
	
	
		Debug.logInfo("The input Before value is "+groupId+" -- "+uploadModel+"","");
		if(UtilValidate.isNotEmpty(groupId) && UtilValidate.isNotEmpty(uploadModel))
		{
			Debug.logInfo("The input after value is "+groupId+" -- "+uploadModel+"","");
			etlModelGroupingList = delegator.findByAnd("EtlModelGrouping",UtilMisc.toMap("groupId",groupId,"modelName",uploadModel,"sequenceNo","01"),null,false);
			Debug.logInfo("Sequence based list "+etlModelGroupingList,"");
			if(UtilValidate.isNotEmpty(etlModelGroupingList))
			{
				
					Debug.logInfo("The process is ready for inside"+etlModelGroupingList,"");
				
					//check for pending status
					etlPending = delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("groupId",groupId,"status","RUNNING"),null,false);
					if(UtilValidate.isNotEmpty(etlPending))
					{
						context.put("restrictUpload","Y");
					}
					//end of pending status
					context.put("uploadOnce","Y");
					if(UtilValidate.isNotEmpty(groupId))
					{
						etlGroupName = EntityQuery.use(delegator).from("EtlGrouping").where("groupId", groupId).cache().queryFirst();
						//etlGroupName = delegator.findByPrimaryKey("EtlGrouping",UtilMisc.toMap("groupId",groupId));
						context.put("groupName",etlGroupName);
					}
					
					context.put("etlModelGroupingList",delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("groupId",groupId)),null,false);
				
			}	
		
		}
		
	
	
	//end @vijayakumar	
	
	if(UtilValidate.isNotEmpty(listName)){
	
		processInfo = EntityUtil.getFirst(delegator.findByAnd("EtlProcess",UtilMisc.toMap("modalName",listName),null,false));
		context.put("process",processInfo);
	}	
	
	
	if(UtilValidate.isNotEmpty(model)){
	
		processInfo = EntityUtil.getFirst(delegator.findByAnd("EtlProcess",UtilMisc.toMap("modalName",model),null,false));
		context.put("process",processInfo);
	}	
	
	String exportModel = parameters.get("model");
	export = EntityUtil.getFirst(delegator.findByAnd("EtlModel",UtilMisc.toMap("modelName",exportModel),null,false));
	context.put("export",export);
	
	
	exportModels = delegator.findByAnd("EtlModel",UtilMisc.toMap("isExport","Y"),null,false);
	context.put("exportModels",exportModels);
	
	if(UtilValidate.isNotEmpty(etlDestTableName)){
		ArrayList list = new ArrayList();
		List<GenericValue> etlSourceTable = delegator.findByAnd("EtlSourceTable", UtilMisc.toMap("tableName",etlDestTableName),null,false);
		if(UtilValidate.isNotEmpty(etlSourceTable)){
			List<String> etlSourceTableList = EntityUtil.getFieldListFromEntityList(etlSourceTable, "listName", true);
			if(UtilValidate.isNotEmpty(etlSourceTableList)){
				EntityCondition conditionss = EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("modelName",EntityOperator.IN,etlSourceTableList)
		   ),EntityOperator.AND);
			List<GenericValue> etlModelsList = delegator.findList("EtlModel", conditionss, null, null, null, false);
			if(UtilValidate.isNotEmpty(etlModelsList))
				for(GenericValue gv : etlModelsList)
				{
					Map m = new HashMap();
					String isExport = gv.getString("isExport");
					if(UtilValidate.isEmpty(isExport)){
						m.put("listName",gv.getString("modelName"));
						m.put("tableName",gv.getString("tableName"));
						list.add(m);
					}
					
				}
				context.put("etlModelsList", list);
			}
		}
	}


accordionModelDefaultOptions = UtilMisc.toMap("accordion_range", UtilMisc.toMap("accordionName", "Data Range", "accordionDetailScreen", "component://Etl-Process/webapp/widget/Etl-Process/screens/common/GeneralScreens.xml#modelConfigRange"));
context.put("accordionModelDefaultOptions", accordionModelDefaultOptions);
