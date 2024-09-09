<div class="page-header md-shadow-z-1-i navbar navbar-fixed-top" style="height:1px !important;">
  <!-- BEGIN HEADER INNER -->
  <div class="page-header-inner">
    <div class="container-fluid">
      <div class="menu-toggler dropdown-toggle" data-toggle="dropdown"></div>
      <div style="color:#87AFC7;text-shadow: 2px 2px #FF0000;font-size:25px;margin-left: 25px;">FIO-ETL Process</div>
      <ul class="dropdown-menu" role="menu">
        <li>
          <div class="portlet box blue">
            <div class="portlet-body">
              <a class="btn btn-xs" href="<@ofbizUrl>main</@ofbizUrl>">
                <#--<i class="fa fa-bar-chart-o"></i>-->
                <div>
                  create Model
                </div>
              </a>
              <a class="btn btn-xs" href="<@ofbizUrl>applyEtlModel</@ofbizUrl>">
                <#--<i class="fa fa-bar-chart-o"></i>-->
                <div>
                  Apply Model
                </div>
              </a>
             
             <a class="btn btn-xs" href="<@ofbizUrl>etlProcessConfiguration</@ofbizUrl>">
                <#--<i class="fa fa-bar-chart-o"></i>-->
                <div>
                  Etl Process
                </div>
              </a>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
</div>
<#assign listNameRequest ="" >
<#if listNameDb?has_content>
<#assign listNameRequest = listNameDb.listName?if_exists>
</#if>
<#--modal process-->
<#--#1. for csv file upload modal window-->
<div class="modal fade draggable-modal" id="draggable" tabindex="-1" role="basic" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
        <h4 class="modal-title">Upload CSV File </h4>
      </div>
      <div class="modal-body">
        <!--for list name input-->
        <div>
          <#if listNameDb?has_content>
          <div id="main_div_new_create">
            <div id="newList_adder">
              <span class="bold">${listNameRequest?if_exists}</span>&nbsp;&nbsp;<button type="button" class="btn btn-default btn-xs" id="new_list_creation" style="background-color:white">Create New</button>
              <input type="hidden" id="csvListName" value="${listNameRequest?if_exists}">
            </div>
          </div>
          <#else>
          <input type="text" name="csvListName" id="csvListName" class="form-control" placeholder="Please enter Modal Name">
          <span class="error" id="list_name_error"></span>	
          </#if>
          <div>&nbsp;&nbsp;&nbsp;</div>
        </div>
        <div>
          <!--end of listname input-->
          <!--for file input-->
          <div class="fileinput fileinput-new pull-left" data-provides="fileinput" >
            <div class="input-group input-group-sm">
              <div class="form-control uneditable-input input-fixed input-group-sm" data-trigger="fileinput">
                <i class="fa fa-file fileinput-exists"></i>&nbsp;
                <span class="fileinput-filename" name="csvFile"> </span>
              </div>
              <!--end of form-control uneditable-input input-fixed input-group-sm-->
              <span class="input-group-addon btn default btn-file">
              <span class="fileinput-new"> Select file </span>
              <span class="fileinput-exists"> Change </span>
              <input type="hidden"><input type="file" name="csv_fileName" id="csv_fileName"> </span>
              <a href="javascript:;" class="input-group-addon btn red fileinput-exists" data-dismiss="fileinput"> Remove </a>
            </div>
            <!--end of input-group input-group-sm-->
            <span class="error" id="file_name_error"></span> 
          </div>
          <!--end of fileinput process-->
        </div>
        <!--end of file input-->
      </div>
      <div class="modal-footer">
        <input type="submit" class="btn btn-info" value="ADD" id="fileProgress">
      </div>
    </div>
    <!-- /.modal-content -->
  </div>
  <!-- /.modal-dialog -->
</div>
<#--end of #1. csv header file upload-->
<#--#2. file upload button on mapping element-->
<div class="modal fade draggable-modal" id="fileUpload" tabindex="-1" role="basic" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
        <h4 class="modal-title">Import</h4>
      </div>
      <div class="modal-body">
        <div class="fileinput fileinput-new pull-left" data-provides="fileinput" >
          <div class="input-group input-group-sm">
            <div class="form-control uneditable-input input-fixed input-group-sm" data-trigger="fileinput">
              <i class="fa fa-file fileinput-exists"></i>&nbsp;
              <span class="fileinput-filename" name="csvFile"> </span>
            </div>
            <!--end of form-control uneditable-input input-fixed input-group-sm-->
            <span class="input-group-addon btn default btn-file">
            <span class="fileinput-new"> Select file </span>
            <span class="fileinput-exists"> Change </span>
            <input type="hidden"><input type="file" name="csv_fileName" id="csv_fileName"> </span>
            <a href="javascript:;" class="input-group-addon btn red fileinput-exists" data-dismiss="fileinput"> Remove </a>
          </div>
          <!--end of input-group input-group-sm-->
          <span class="error" id="file_name_error"></span> 
        </div>
        <!--end of fileinput process-->
      </div>
      <!--end of modal-body-->
      <div class="modal-footer">
        <input type="submit" class="btn btn-info" value="Import" >
      </div>
    </div>
    <!-- /.modal-content -->
  </div>
  <!-- /.modal-dialog -->
</div>
<#--end of #2.file upload button process-->  
<#--modals do not enter any thing here NOTE IMPORTANT-->
<head>
  <#--for file upload js-->
  <script src="/metronic/js/bootstrap-fileinput.js" type="text/javascript"></script>
  <#--end of file upload js-->
  <#--file upload plugin-->
  <link rel="stylesheet" type="text/css" href="/metronic/css/bootstrap-fileinput.css"/>
  <#--end @fileupload plugin-->
  <#--for draggle modal-->
  <script src="/metronic/js/ui-modals.min.js" type="text/javascript"></script>
  <#--end of draggle modal-->
  <#--for form validation process-->
  <#--end of formvalidation-->
  <#--for scroll bar check-->
  <#--for alert popup-->
  <script src="/metronic/js/bootstrap-confirmation.min.js" type="text/javascript"></script>
  <script src="/metronic/js/ui-confirmations.min.js" type="text/javascript"></script>
  <#--end of alert popup-->
  <#--notification of js-->
  <script src="/metronic/js/notify.min.js" type="text/javascript"></script>
  <script src="/metronic/js/notify.js" type="text/javascript"></script>
  <#--end of notification-->
  <#--for loading screen-->
  <script src="/metronic/js/jquery.blockui.min.js" type="text/javascript"></script>
  <script src="/metronic/js/ui-blockui.min.js" type="text/javascript"></script>
  <#--end of loading screen-->
  <style>
  
  
	 .page-header.navbar .menu-toggler.dropdown-toggle{float:left !important;margin: 7px 0 0 0 !important;}
	  .page-header.navbar {
	  width: 100%;
	  padding: 0 20px 0 20px;
	  margin: 0;
	  border: 0px;
	  padding: 0px;
	  box-shadow: none;
	  height: 75px;
	  min-height: 0px !important;
	  }
    ::-webkit-scrollbar {
    width: 12px;
    }
    ::-webkit-scrollbar-track {
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3); 
    border-radius: 10px;
    }
    ::-webkit-scrollbar-thumb {
    border-radius: 10px;
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.5); 
    }
    button.btn.dropdown-toggle.selectpicker.btn-default {
    background-color: white !important;
    }
    .md-shadow-z-3, .modal {
    box-shadow: 0 0px 0px rgba(0, 0, 0, 0.0), 0 0px 0px rgba(0, 0, 0, 0.0);
    }
    @media only screen and (max-width: 500px) {
    body {
    background-color: red;
    }
    }
    .search-form .form-group {
    float: right !important;
    transition: all 0.35s, border-radius 0s;
    width: 32px;
    height: 32px;
    background-color: #fff;
    box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075) inset;
    border-radius: 25px;
    border: 1px solid #ccc;
    }
    .search-form .form-group input.form-control {
    padding-right: 20px;
    border: 0 none;
    background: transparent;
    box-shadow: none;
    display:block;
    }
    .search-form .form-group input.form-control::-webkit-input-placeholder {
    display: none;
    }
    .search-form .form-group input.form-control:-moz-placeholder {
    /* Firefox 18- */
    display: none;
    }
    .search-form .form-group input.form-control::-moz-placeholder {
    /* Firefox 19+ */
    display: none;
    }
    .search-form .form-group input.form-control:-ms-input-placeholder {
    display: none;
    }
    .search-form .form-group:hover,
    .search-form .form-group.hover {
    width: 100%;
    border-radius:25px 25px 25px 25px;
    }
    .search-form .form-group span.form-control-feedback {
    position: absolute;
    top: -1px;
    right: -2px;
    z-index: 2;
    display: block;
    width: 34px;
    height: 34px;
    line-height: 34px;
    text-align: center;
    color: #3596e0;
    left: initial;
    font-size: 14px;
    }
    .error
    {
    font-size:12;
    color:red;
    }												
  </style>
  <#--end of scroll bar check-->
  <#--only for mannual script-->
  <script>
    var destinationObjValue="";
    
    $(document).ready(function(){
    
    	
    
    	//for tooltip
    
    	 $('[data-toggle="tooltip"]').tooltip(); 
    
    	
    
    	
    
    	
    
    	//for file Uploading
    
    	$("#fileProgress").click(function(){
    
    			
    
    		var fileName = $("#csv_fileName").val();
    
    		var listName = $("#csvListName").val();
    
    		if(listName=="")
    
    		{
    
    			
    
    			$("#list_name_error").text("Please enter list Name");
    
    			return false;
    
    		}else
    
    		{
    
    			$("#list_name_error").text("");
    
    		}
    
    		if(fileName!="" )
    
    		{
    
    			$("#file_name_error").text("");
    
    			var filenameWithext = fileName.split(/(\\|\/)/g).pop();
    
    			$("#csvFile").val(filenameWithext);
    
    			
    
    			$("#listName").val(listName);
    
    			
    
    			$("#csv-upload").submit();
    
    		}else
    
    		{
    
    			
    
    			$("#file_name_error").text("Please select File");
    
    		}
    
    	
    
    	});
    
    	
    
    	//to hide error/success message after particular time period
    
    	$(".hideWhen").delay(5000).slideUp(1000);
    
    	
    
    	
    
    	//get the column list from the tableName
    
    	
    
    	$("#getSelected").change(function(){
    
    		var selectedIs = $(this).val();
    
    		
    
    		$.ajax({
    
                type: "POST",
    
                url: "getColumnsByTable",
    
                data:  {"tableName": selectedIs},
    
                success: function (data) {                
    
               	
    
               	$(".tableFieldName").empty();
    
               	var i=0;
    
                $.each(data, function(i, obj) {
    
    			
    
    			  var field = obj.etlFieldName;
    
    			  var etl_id = obj.seqId;
    
    			  var is_prime = obj.isPrime;
    			  
    			  
    			  if(field!=null)
    
    			  {
    
    			 
    
    			  	i=i+1;
    
    			  	$(".tableFieldName").append("<li  class='list-group-item' id=etl_dst_"+etl_id+" value="+field+" style='height: 36px;vertical-align: text-top;' id='mapTab'><button class='btn btn-default btn-circle btn-xs' style='background-color:#E2E7F3;height: 19px;font-size: 10px;' id='mappTab_"+i+"' is_prime_field="+is_prime+" onclick='validateRadioWithCheckBox(this)' value="+field+"><span class='fa fa-plus'/>&nbsp;"+field+"</button></li>");
    
    			  }
    
    			});
    
    													            }
    
            });
    
            
    
            
    
    	
    
    	});	
    
    	
    
    	
    
    	//disable etl_destination table option when list of etlMappingElements is empty
    
    	$("button[data-id='getSelected']").click(function(){
    
    		
    
    		var etlMappingElements_length = $("#etlMappingElements li").length;
    
    		if(etlMappingElements_length==0)
    
    		{
    
    			$("button[data-id='getSelected']").prop('disabled', true);
    
    						//var msg8 = "Please select CSV File ";
    
    						//notificationProgress(msg8,"info");
    
    						
    
    						
    
    						$("#listTesting").notify(
    
    						  "Please select CSV file here",'info', 
    
    						  { position:"right" }
    
    						  
    
    						);
    
    		}
    
    	});
    
    	
    
    		
    
    		
    
    	//for form create new operation
    
    	$("#new_list_creation").click(function(){
    
    		$("#newList_adder").remove();
    
    		$("#main_div_new_create").append("<input type='text' name='csvListName' id='csvListName' class='form-control' placeholder='Please enter Modal Name'><span class='error' id='list_name_error'></span>");
    
    	});							
    
    											
    
    });
    
    
    
    
    
    //for filter search box process
    
    function filter(element,id) {
    
    	   var value = $(element).val();
    
    	   $("#"+id+" li").each(function() {
    
    	     if ($(this).text().search(new RegExp(value, "i")) > -1) {
    
    	       $(this).show();
    
    	     } else {
    
    	       $(this).hide();
    
    	     }
    
    	   });
    
    	 }
    
    	
    
    	<#assign delete_count=0/> 
    
    	 
    
    //to validate that the radio and button clicked or not
    
    function validateRadioWithCheckBox(element){
    
    	
    
    	var etlMappingElement = "";
    
    	$("#csvSortable li").each(function() {
    
    		var availorNot = $(this).html().search(new RegExp("checked", "i"));
    
    		if(availorNot>-1)
    
    		{
    
    			var theGotText = $(this).text();
    
    			var theVar = $(this).html();
    
    			
    
    			var etlElementMapElementHide = $(this).attr("id");
    
    			
    
    			
    
    			
    
    			//to display none when the clicked process is ready
    
    			
    
    			$("#"+etlElementMapElementHide).css('display','none');
    
    			$("span[class='checked']").prop('class', '');
    
    			
    
    			//end of click none and attribute changes
    
    			
    
    			
    
    			etlMappingElement  = $.trim(theGotText)
    
    		}
    
    	});
    
    	
    
    	var destinationTable =  $(element).val();
    
    	
    
    	var destinationTableId = $(element).attr('id');
    
    	var is_prime_check = $(element).attr("is_prime_field");
    
    	//destinationObjValue= destinationTableId;
    
    	//alert("The id need to be hide when u needed is "+destinationTableId);
    
    	
    
    	
    
    	
    
    	var tableName = $("#getSelected").val();
    
    	if(etlMappingElement=="")
    
    	{
    
    		//var msg="Please select csv header name";
    
    		//notificationProgress(msg,"info");
    
    		
    
    		$("#etlMappingElements").notify(
    
    						  "Please Select Csv Header Name Here",'info', 
    
    						  { position:"bottom" }
    
    						  
    
    						);
    
    	}else
    
    	{
    
    		var listMapper = $("#csvListName").val();
    
    		//alert("We are going to map "+etlMappingElement+" with "+destinationTable+" to the "+tableName+" for the "+listMapper);
    
    		
    
    		<#assign delete_count=delete_count+1/> 
    
    		//the process of sending ajax request with the storage process of etlmappingelement table
    
    		$.ajax({
    
                type: "POST",
    
                url: "getMapElement",
    
                data:  {"tableName": tableName,"etlMappingElement":etlMappingElement,"destinationTable":destinationTable,"modalName":listMapper},
    
                success: function (data) { 
    
    					var style_for_prime = "";
    	            		if(is_prime_check=="Y")
    	            		{
    	            			style_for_prime="*";
    	            		}
    						
    	            		
    						
    	            		$("#mappedElements").append("<li  class='list-group-item' id='mappedElements_"+data.Id+"' value='' style='height: 29px;vertical-align: text-top;font-size: 11px;padding-top: 5px;'>"+data.etlFieldName+"<span class='glyphicon glyphicon-arrow-right'/>&nbsp;&nbsp;<span style='color:red'>"+style_for_prime+"</span>"+data.tableColumnName+"<a href='#' onclick='removeListItemFromeEtlElementMap("+data.Id+",this)' value="+data.Id+" etl_map_ele="+destinationTableId+"><i class='fa fa-remove' style='color: red;'></i></a></li>");
    
                			$(element).attr("disabled","true");
    
                			$(element).append("<i class='fa fa-check pull-right' style='color:green' id=image_"+destinationTableId+"/>");
    
                			
    
                			var msg10=data.etlFieldName+" mapped with "+data.tableColumnName;
    
    						notificationProgress(msg10,"success");
    
                			
    
                			}
    
             		});
    
    	
    
    	}
    
    
    
    }							
    
    		
    
    	//for removing the list item from the etlMapping element
    
    	function removeListItemFromeEtlElementMap(element,this_obj)
    
    	{
    
    		var etl_dest_id = $(this_obj).attr('etl_map_ele');
    
    	
    
    		$.ajax({
    
                type: "POST",
    
                url: "removeEtlMappedElement",
    
                data:  {"Id": element},
    
                success: function (data) { 
    
             		
    
             		var deleted = data.deleted;
    
             		if(deleted=="Y")
    
             			{
    
             				
    
             				$("#mappedElements_"+element).remove();
    
             				renableDroppedData(element,etl_dest_id);
    
             				var msg6 = "Mapping removed";
    
    						notificationProgress(msg6,"warn");
    
             			}
    
             		}
    
             	});
    
             
    
    	}	
    
    	
    
    	//to renable the disabled/dropped data
    
    	function renableDroppedData(id,etl_dest_id)
    
    	{
    
    		$("#columnSortable_"+id).show();
    
    		$("#"+etl_dest_id).attr("disabled",false);
    
    		$("#image_"+etl_dest_id).remove();
    
    	}
    
    	
    
    	//transfer data from staging table to production table
    
    	function transferDatafromStagingToProd()
    
    	{
    
    		var mappedElements_length = $("#mappedElements li").length;
    
    		if(mappedElements_length==0)
    
    		{
    
    			var msg1="No Mapping found, Unable to save";
    
    			notificationProgress(msg1,"error");
    
    		
    
    		}else
    
    		{
    
    			var listName_fn = $("#csvListName").val();
    
    			if(listName_fn!="")
    
    			{
    
    				$.ajax({
    
    	            type: "POST",
    
    	            url: "migrateFromStagingToEtlFinal",
    
    	            data:  {"listName": listName_fn},
    
    	            success: function (data) {
    
    	            		var msg5 = "Data Loaded Successfully...!";
    
    						notificationProgress(msg5,"success");
    
    	             							}
    
                 });
    
    			
    
    			}else
    
    			{
    
    				var msg2 = "List Name Error!Please check the actual flow";
    
    				notificationProgress(msg2,"error");
    
    			}
    
    			
    
    		
    
    		
    
    		}
    
    		
    
    	
    
    	}
    
    	
    
    	
    
    	//for notification message
    
    	function notificationProgress(msg,errorType)
    
    	{
    
    		$.notify(msg,errorType);
    
    	}
    
    	//for remove all mapping from the list 
    	function removeAllMapping(element){
    		var listName = $(element).val();
    		var mappedElements_length = $("#mappedElements li").length;
    		if(listName=="undefined" || listName=="" || typeof listName==="undefined")
    		{
    			notificationProgress("No Modal Name Found, Unable to delete","warn");
    			return false;
    		}
    		
    		if(mappedElements_length==0)
    		{
    			notificationProgress("No Mapping Found, Unable to delete","warn");
    			return false;
    		}
    		$.ajax({
    
    	            type: "POST",
    
    	            url: "removeAllMapping",
    
    	            data:  {"listName": listName},
    
    	            success: function (data) {
    					if(data.set=="Y")
    					{
    						 notificationProgress("Total Mapping removed","success");
    						
    					}
    	            	
    
    	             							}
    
                 });
    	
    	
    		//to renable the hidden buttons
    		$("#mappedElements li").each(function(){
    			var theHtmlValue = $(this).attr("id");
    			
    				$("#"+theHtmlValue+" a").each(function(){
    					var value_data_as_id = $(this).attr("value");
    					var etl_map_ele = $(this).attr("etl_map_ele");
    					renableDroppedData(value_data_as_id,etl_map_ele);
    					
    				});	
    			
    			
    		});
    		 $("#mappedElements").empty();
    	}
    
  </script>
  <#--end of mannual script-->	
  <style>
  .bgText{
   	color: #FFF;
    font-family: sans-serif;
    font-size: 16pt;
    font-weight: bold;
    line-height: 2em;
    padding: .5em;
    text-shadow: 0 0 .5em #87AFC7;
	-webkit-transform: rotate(314deg);
	-moz-transform: rotate(314deg);
	-o-transform: rotate(314deg);
  }
  </style>
</head>
<!-- BEGIN HEADER -->
<body style="overflow: scroll;">
  <div class="col-md-12 col-lg-12 col-xs-12 col-sm-12">
    <div>&nbsp;&nbsp;&nbsp;&nbsp;</div>
    <#--Error header-->
    <#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
    <#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>
    <#-- display the error messages -->
    <#if errorMessageList?has_content || opentapsErrors.toplevel?size != 0>
    <#list opentapsErrors.toplevel as errorMsg>
    <div class="alert alert-danger hideWhen">
      <button class="close" data-close="alert"></button>
      <strong>Error! </strong>${StringUtil.wrapString(errorMsg)}
    </div>
    </#list>
    <#list errorMessageList?if_exists as errorMsg>
    <div class="alert alert-danger hideWhen">
      <button class="close" data-close="alert"></button>
      <strong>Error! </strong>${StringUtil.wrapString(errorMsg)}
    </div>
    </#list>
    </#if>
    <#--display event message-->
    <#if eventMessageList?has_content>
    <#list eventMessageList as eventMsg>
    <div class="alert alert-info hideWhen">
      <button class="close" data-close="alert"></button>
      <strong> MESSAGE :</strong>${StringUtil.wrapString(eventMsg)}
    </div>
    </#list>
    </#if>
  </div>
  <!--end of Error header-->
  <#--end of Error header-->
  <#--end of modals-->
  <#--portlet 1-->
  <div class="row-fluid">
    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12" >
      <div class="portlet light" style=" background-image:url('/opentaps_images/opentaps_logo.png');background-repeat: no-repeat;background-position: center;   min-height: 550px !important;border: 1px solid #B6BFC1 !important">
        <div class="portlet-title" style="display: flex;">
          <div class="caption-subject  bold " style="color:#87AFC7">
          </div>
          <div class="actions" style="width:100%">
            <span class=" bold" style="color:#337AB7">CSV FILE HEADER NAMES</span>
            <div class="input-group">
              <form action="" class="search-form">
                <div class="form-group has-feedback">
                  <label for="search" class="sr-only">Search</label>
                  <input type="text" class="form-control" name="search" id="search" placeholder="search" onkeyup="filter(this,'csvSortable')">
                  <span class="glyphicon glyphicon-search form-control-feedback"></span>
                </div>
              </form>
              <#--<input type="text" class="form-control" placeholder="Search.." id="csvtxtList" onkeyup="filter(this,'csvSortable')" aria-describedby="basic-addon1" style="border-bottom-left-radius: 20px;border-top-left-radius: 20px;float: right !important;">-->
              <span class="input-group-addon "  style="background-color:white;padding-bottom: 15px;border: 0px solid #ccc !important; ">
              <a  data-toggle="modal" href="#draggable" id="listTesting">
                <span class="fa fa-plus" data-toggle="tooltip" title="Add CSV File "/>
              </a>
              &nbsp;&nbsp;&nbsp;&nbsp;
              <#--getting all available list in the etlmapping table-->
              <span class="dropdown" style="display:none">
              <a class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-tasks" style='color:#32c5d2'></i>
              <ul class="dropdown-menu">
              <#list etlSet as list_name>
              <li><a href="main?listName=${list_name?if_exists}" >${list_name?if_exists}</a></li>
              </#list>
              </ul>
              </span>
              <#--end @vijayakumar-->
              </span>
            </div>
     
            <#if listNameDb?has_content>
            <div class="" style="background-color:white;color:#768876;font-weight:bold;color:#219174">Model Name : ${listNameRequest?if_exists} </div>
            </#if>
          </div>
          <#--<a class="white badge" data-toggle="modal" href="#draggable"> <span class="fa fa-plus"/> </a>-->
        </div>
        <div class="portlet-body" style=" min-height: 40px;">
          <div class="scroller" style=" max-height: 400px; overflow: auto;    overflow-x: auto; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1" id="csvSortable">
            <#--for display all the data from EtlMappingElements table-->
            <#if etlMappingElements?has_content>
            <ul class="list-group" id="etlMappingElements">
              <#assign count=0 />
              <#list etlMappingElements as etlElement>
              <li class="list-group-item" value="${etlElement.etlFieldName?if_exists}" id="columnSortable_${etlElement.Id?if_exists}" style="height: 29px;padding-top: 5px;!important"> 
                <label class="radio-inline" style="font-size: 11px;"><input type="radio" name="optradio" value="${etlElement.etlFieldName?if_exists}" id="optradio">&nbsp;${etlElement.etlFieldName?if_exists}</label>
              </li>
              <#assign count=count+1 />	
              </#list>
            </ul>
            </#if>
            <#--end @EtlMappingElements-->
          </div>
        </div>
        <h3 class="bgText" style="position:relative;">CSV FILE HEADER NAMES</h3>
      </div>
    </div>
    <!--end of col-md-4 etc-->
    <#--end of portlet body-->
    <#--file uploading process-->
    <form name="csv-upload" id="csv-upload" action="<@ofbizUrl>uploadCSVFile</@ofbizUrl>" method="POST">
      <input type="hidden" name="csvFile" id="csvFile">
      <input type="hidden" name="listName" id="listName">
    </form>
    <#--end of file uploading process-->
    <#--portlet 2-->
    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
      <div class="portlet light bordered" style=" background-image:url('/opentaps_images/opentaps_logo.png');background-repeat: no-repeat;background-position: center;  min-height: 550px !important;border: 1px solid #B6BFC1 !important">
        <div class="portlet-title">
          <span class="portlet-title bold" style="color:#337AB7">DESTINATION TABLES</span>
          <div style="color:#87AFC7">
            <form action="" class="search-form">
              <div class="form-group has-feedback">
                <label for="search" class="sr-only">Search</label>
                <input type="text" class="form-control" name="search" id="search" placeholder="search" onkeyup="filter(this,'columnSortable')">
                <span class="glyphicon glyphicon-search form-control-feedback"></span>
              </div>
              <!--end of form group and has-feedback-->
            </form>
            <select class="bs-select form-control btn-xs" data-live-search="true" data-size="8" id="getSelected">
              <#if eltTableNameList?has_content>
              <option value="" selected disabled>Select Table...</option>
              <#list eltTableNameList as eltTableName>
              <option value="${eltTableName.tableName?if_exists}">${eltTableName.tableTitle?if_exists}</option>
              </#list>
              </#if>
            </select>
          </div>
          <!--end of color:#87AFC7-->
        </div>
        <!--end of portlet-title-->
        <div class="portlet-body">
          <div class="scroller" style="max-height: 400px; overflow: auto;    overflow-x: auto; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1">
            <ul class="list-group tableFieldName" id="columnSortable" >
              <li  style="height: 29px;">&nbsp;&nbsp;&nbsp;</li>
            </ul>
          </div>
          <!--end of scroller-->
        </div>
        <!--end of portlet-body-->
      </div>
      <!--end of portlet-light-->
    </div>
    <!--end of col-lg-4 col-md-4 col-sm-4 col-xs-12-->
    <#--end of portlet 2-->
    <#--portlet 3-->
    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
      <div class="portlet light bordered" style=" background-image:url('/opentaps_images/opentaps_logo.png');background-repeat: no-repeat;background-position: center;min-height: 550px !important;border: 1px solid #B6BFC1 !important">
        <div class="portlet-title">
          <span class="portlet-title bold" style="color:#337AB7"><span class="fa fa-gift"></span>&nbsp;MAPPING ELEMENTS</span>
          <div class="pull-right">
            <i data-toggle="tooltip" title="Import File"><button type="button" class="btn blue btn-outline btn-xs" data-toggle="modal" href="#fileUpload" > <span class="fa fa-upload" /></button></i>
            <i data-toggle="tooltip" title="Save Mapped Data"><button type="button" class="btn btn-success btn-xs" onclick="transferDatafromStagingToProd()"><span class="fa fa-save"/></button></i>
          </div>
          <div>&nbsp;&nbsp;&nbsp;</div>
          <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
            <form action="" class="search-form">
              <div class="form-group has-feedback">
                <label for="search" class="sr-only">Search</label>
                <input type="text" class="form-control" name="search" id="search" placeholder="search" onkeyup="filter(this,'mappedElements')">
                <span class="glyphicon glyphicon-search form-control-feedback"></span>
              </div>
            </form>
          </div>
           
           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
            <button type="button" class="btn btn-danger btn-xs pull-right" value="${listNameRequest?if_exists}" style="float:right" onclick="removeAllMapping(this)">Remove All</button>
        </div>
        </div>
        <div class="portlet-body">
          <div class="">
            <div class="scroller" style="max-height: 400px; overflow: auto;    overflow-x: auto; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1">
              <ul class="list-group" id="mappedElements" >
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
    <#--end of portlet 2-->
    <#--end of portlet 2-->
    <div class="page-footer">
      <div class="scroll-to-top" style="display: block;">
        <i class="icon-arrow-up"></i>
      </div>
    </div>
  </div>
  <!--end of row-->
  <div class="row-fluid">
    <h3 class="" style="color:#87AFC7;text-shadow: 2px 2px #FF0000;">&nbsp;&nbsp;</h3>
  </div>
</body>
<script type="text/javascript">
  var ComponentsDropdowns = function () {
  
  
  
  var handleBootstrapSelect = function() {
  
        $('.bs-select').selectpicker({
  
            iconBase: 'fa',
  
            tickIcon: 'fa-check'
  
        });
  
  }
  
         
  
       return {
  
        //main function to initiate the module
  
        init: function () {            
  
            handleBootstrapSelect();
  
        }
  
    };
  
  
  
  }();
  
  
  
  ComponentsDropdowns.init();
  
  
  
</script>

<#--for destination tab id -->
<input type="hidden" id="dest_tab_id"/>
<#--end @destination tab id-->