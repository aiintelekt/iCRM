<style>	
   #optradio .radio input[type=radio], .radio-inline input[type=radio]{
   margin-left:0px !important;
   }
   .filter-option.pull-left{
   padding : 4px !important;
   }
   span.fa.fa-gear.pull-right.btn-info.btn-circle {
   font-size: 12px;
   height: 25px;
   width: 26px;
   }
   .configuration a:hover { 
   color: #fff !important;;
   text-decoration: none; 
   }
</style>
<head>
   <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.min.css">
   <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
   <script src="/etl-process-resource/js/login-soft.js" type="text/javascript"></script>
</head>
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
            <h4 class="modal-title">${uiLabelMap.uploadCsvFile} </h4>
         </div>
         <div class="modal-body">
            <!--for list name input-->
            <form name="csv-upload" id="csv-upload" action="<@ofbizUrl>uploadFile</@ofbizUrl>" method="POST" enctype="multipart/form-data">
               <div>
                  <#if listNameDb?has_content>
                  <div id="main_div_new_create">
                     <div id="newList_adder">
                        <span class="strong">${listNameRequest?if_exists}</span>&nbsp;&nbsp;<a class="btn btn-xs btn-info default" id="new_list_creation">Create New</a>
                        <input type="hidden" id="csvListName" value="${listNameRequest?if_exists}" name="csvListName">
                        <input type="hidden" value="${listNameRequest?if_exists}" name="listId">
                        <#if etlModel?has_content>
                        <input type="hidden" value="${etlModel.groupId?if_exists}" name="groupId" id="groupId">
                        <input type="hidden" value="${etlModel.serviceName?if_exists}" name="serviceId">
                        </#if>
                     </div>
                  </div>
                  <#else>
                  <input type="text" name="csvListName" id="csvListName" class="form-control" placeholder="${uiLabelMap.modelName}">
                  <span class="error" id="list_name_error"></span>	
                  <div>&nbsp;&nbsp;&nbsp;</div>
                  <select name="groupId" id="groupId" class="form-control" placeholder="Select Group">
                     <option selected disabled value="">${uiLabelMap.selectGroup}</option>
                     <#assign etlGroups=delegator.findAll("EtlGrouping",false)?if_exists/>
                     <#if etlGroups?has_content>
                     <#list etlGroups as group>
                     <option value="${group.groupId?if_exists}"<#if group.groupId?if_exists==requestParameters.groupId?if_exists>selected</#if>>${group.groupName?if_exists} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>	
                     </#list>
                     </#if>
                  </select>
                  <div>&nbsp;&nbsp;&nbsp;</div>
                  <select name="serviceId" id="serviceId" class="form-control" placeholder="Select Service">
                     <#assign EtlProcessService =delegator.findAll("EtlProcessService",false)?if_exists />
                     <#if EtlProcessService?has_content>
                     <option selected disabled value="">Select Service</option>
                     <#list EtlProcessService as service>
                     <option value="${service.serviceId?if_exists}" data-fileTplLoc="${service.fileTplLoc!}" data-isAbsoluteTplLoc="${service.isAbsoluteTplLoc!}" <#if process?has_content><#if process.serviceName?if_exists==service.serviceId?if_exists> selected</#if></#if>>${service.serviceName?if_exists}</option>
                     </#list>
                     </#if>
                  </select>
                  </#if>
                  <div>&nbsp;&nbsp;&nbsp;</div>
                  <#if !textDelimiter?has_content>
                  <select name="delimiter" class="form-control input-sm"
                     placeholder="Select Delimiter">
                     <option value="">Please Select Delimiter for txt and dat file</option>
                     <option value="TAB" >TAB</option>
                     <option value="COMMA" >COMMA</option>
                     <option value="SEMICOLON" >SEMICOLON</option>
                     <option value="PIPELINE" >PIPELINE</option>
                  </select>
                  <div>&nbsp;&nbsp;&nbsp;</div>
                  </#if>
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
                        <span class="fileinput-new"> ${uiLabelMap.selectFile} </span>
                        <span class="fileinput-exists"> ${uiLabelMap.change} </span>
                        <input type="hidden"><input type="file" name="csv_fileName" id="csv_fileName"> </span>
                        <a href="javascript:;" class="input-group-addon btn red fileinput-exists" data-dismiss="fileinput">  ${uiLabelMap.removeButton} </a>
                     </div>
                     <!--end of input-group input-group-sm-->
                     <span class="error" id="file_name_error"></span>
                  </div>
                  <!--end of fileinput process-->
                  <input type="checkbox" name="isExport" value="Y"> ${uiLabelMap.isExport}
                  <span id="downloadFile" class="btn btn-xs btn-primary ml-0">Download</span>

               </div>
               <!--end of file input-->
         </div>
         <div class="modal-footer">
         <#if fileName?has_content>
         <input type="button" class="btn btn-xs btn-info default" value="${uiLabelMap.add}" id="fileProgress">
         <#else>
         <input type="button" class="btn btn-xs btn-info default uploadButton" value="${uiLabelMap.add}" id="fileProg">
         </#if>
         </div>
         </form>
      </div>
      <!-- /.modal-content -->
   </div>
   <!-- /.modal-dialog -->
</div>
<#--@prompt modal-->
<script>
   $( ".uploadButton" ).click(function() {
   		var groupId = $("#csv-upload #groupId").val();
   		var csvListName = $("#csv-upload #csvListName").val();
		if (!groupId) {
			notificationProgress("Please select Group", "error");
			return false;	
		} 
		if (!csvListName) {
			notificationProgress("Model Name Required", "error");
			return false;	
		} 
		$("#csv-upload").submit(); 
   });
   
</script>
<div class="modal fade draggable-modal" id="promptId" tabindex="-1" role="basic" aria-hidden="true" >
   <div class="modal-dialog modal-sm">
      <div class="modal-content">
         <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
            <h4 class="modal-title">Import</h4>
         </div>
         <div class="modal-body">
            <div class="fileinput fileinput-new pull-left" data-provides="fileinput" >
               <div class="input-group input-group-sm">
                  <!--end of form-control uneditable-input input-fixed input-group-sm-->
               </div>
               <!--end of input-group input-group-sm-->
               <span class="error" id="file_name_error"></span> 
            </div>
            <!--end of fileinput process-->
         </div>
         <!--end of modal-body-->
         <div class="modal-footer">
            <input type="submit" class="btn btn-info default" value="Import" >
         </div>
      </div>
      <!-- /.modal-content -->
   </div>
   <!-- /.modal-dialog -->
</div>
<#--@End-->
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
            <input type="submit" class="btn btn-info default" value="Import" >
         </div>
      </div>
      <!-- /.modal-content -->
   </div>
   <!-- /.modal-dialog -->
</div>
<#--end of #2.file upload button process-->  
<#--for help modal window dynamic do not use-->
<div class="modal fade modal-dynamo" id="" role="dialog">
   <div class="modal-dialog modal-sm" style="width:75%;">
      <div class="modal-content">
         <div class="modal-header modal-dynamo-header">
         </div>
         <div class="modal-body modal-dynamo-body" style="text-align: justify;">
            <p>Etl-process testing description</p>
         </div>
         <#--this is help modal footer please use when needed-->
         <#--
         <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
         </div>
         -->
      </div>
   </div>
</div>
<#--end of #3 dynamic modal window-->
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
      transition: all 0.35s ease;, border-radius 0s;
      width: 32px;
      height: 32px;
      background-color: #fff;
      box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075) inset;
      border-radius: 25px;
      border: 1px solid #ccc;
      }
      /* .search-form .form-group input.form-control {
      padding-right: 20px;
      border: 0 none;
      background: transparent;
      box-shadow: none;
      display:block;
      }*/
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
      .search-form .form-group:focus,
      .search-form .form-group:active{
      outline:none;
      width: 300px;
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
      .wrapper {
      }
      .box{
      margin: 17px auto;
      width: 335px;
      height: 39px;
      }
      .container-2{
      width: 70px;
      height: 0px;
      position: relative;
      color: #00AFF0;
      border: none;
      font-size: 10pt;
      float: right;
      color: #00AFF0;
      padding-left: 27px;
      padding-top: 0px;
      }
      .container-2 input#search{
      width: 35px;
      height: 40px;
      border: none;
      font-size: 10pt;
      float: right;
      color: #262626;
      padding-right: 35px;
      -webkit-border-radius: 5px;
      -moz-border-radius: 5px;
      border-radius: 5px;
      color: #fff;
      -webkit-transition: width .55s ease;
      -moz-transition: width .55s ease;
      -ms-transition: width .55s ease;
      -o-transition: width .55s ease;
      transition: width .55s ease;
      }
      .container-2 input#search::-webkit-input-placeholder {
      color: #082935;
      }
      .container-2 input#search:-moz-placeholder { /* Firefox 18- */
      color: #082935;  
      }
      .container-2 input#search::-moz-placeholder {  /* Firefox 19+ */
      color: #082935;  
      }
      .container-2 input#search:-ms-input-placeholder {  
      color: #082935;  
      }
      .container-2 .icon{
      position: absolute;
      top: 50%;
      margin-left: 17px;
      margin-top: 12px;
      z-index: 1;
      color: #4f5b66;
      }
      .container-2 input#search:focus, .container-2 input#search:active{
      outline:none;
      color: #082935;
      width: 315px;
      }
      .container-2:hover input#search{
      color: #082935;
      width: 315px;
      }
      .container-2:hover .icon{
      color: #00AFF0;
      }
      .wrapper .tooltip {
      background: #1496bb;
      bottom: 100%;
      color: black;
      display: block;
      left: 0px;
      margin-bottom: 15px;
      opacity: 0;
      padding: 0px;
      pointer-events: none;
      position: absolute;
      width: 100%;
      -webkit-transform: translateY(10px);
      -moz-transform: translateY(10px);
      -ms-transform: translateY(10px);
      -o-transform: translateY(10px);
      transform: translateY(10px);
      -webkit-transition: all .25s ease-out;
      -moz-transition: all .25s ease-out;
      -ms-transition: all .25s ease-out;
      -o-transition: all .25s ease-out;
      transition: all .25s ease-out;
      -webkit-box-shadow: 2px 2px 6px rgba(0, 0, 0, 0.28);
      -moz-box-shadow: 2px 2px 6px rgba(0, 0, 0, 0.28);
      -ms-box-shadow: 2px 2px 6px rgba(0, 0, 0, 0.28);
      -o-box-shadow: 2px 2px 6px rgba(0, 0, 0, 0.28);
      box-shadow: 2px 2px 6px rgba(0, 0, 0, 0.28);
      }
      /* This bridges the gap so you can mouse into the tooltip without it disappearing */
      .wrapper .tooltip:before {
      bottom: -20px;
      content: " ";
      display: block;
      height: 20px;
      left: 0;
      position: absolute;
      width: 100%;
      }  
      /* CSS Triangles - see Trevor's post */
      .wrapper .tooltip:after {
      border-left: solid transparent 10px;
      border-right: solid transparent 10px;
      border-top: solid #1496bb 10px;
      bottom: -10px;
      content: " ";
      height: 0;
      left: 50%;
      margin-left: -13px;
      position: absolute;
      width: 0;
      }
      .wrapper:hover .tooltip {
      opacity: 1;
      pointer-events: auto;
      -webkit-transform: translateY(0px);
      -moz-transform: translateY(0px);
      -ms-transform: translateY(0px);
      -o-transform: translateY(0px);
      transform: translateY(0px);
      }
      /* IE can just show/hide with no transition */
      .lte8 .wrapper .tooltip {
      display: none;
      }
      .lte8 .wrapper:hover .tooltip {
      display: block;
      }											
   </style>
   <#--end of scroll bar check-->
   <#--only for mannual script-->
   <script>
      var destinationObjValue="";
      
      $(document).ready(function(){
      
      	
      
      	//for tooltip
      
      	 $('[data-toggle="tooltip"]').tooltip(); 
      
      	
      
      	
      $( "#dialog" ).dialog();
      	
      
      	//for file Uploading
      
      	$("#fileProgress").click(function(){
      
      			
      
      		var fileName = $("#csv_fileName").val();
      
      		var listName = $("#csvListName").val();
      
      		if(listName=="")
      
      		{
      
      			
      			$("#list_name_error").html("<b>Please enter Model Name</b>");
      			
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
      
      			
      
      			//$("#csv-upload").submit();
      
      		}else
      
      		{
      
      			
      			$("#file_name_error").html("<b>Please select File</b>");
      			return false;
      
      		}
      
      	
      
      	});
      
      	//for keypress event validate for modal name
      	$('#csvListName').keyup(function() {
      				var keyValue = $(this).val();
      				if(keyValue!="")
      				{
      					$("#list_name_error").text("");
      				}else
      				{
      						$("#list_name_error").html("<b>Please enter Model Name</b>");
      				}
      				
           var $th = $(this);
           $th.val( $th.val().replace(/[^a-zA-Z\s]/g, function(str) {
            		$("#list_name_error").html("<b>Please provide valid list name</b>");
            	 return '';
             } ) );
       });
      	
      	
      	//for onchange event validate of csv_fileName
      	$("#csv_fileName").change(function(){
      	
      		var fileName = $(this).val();
      		if(fileName!="")
      		{
      			$("#file_name_error").html("");
      		}
      	});
      	
      
      	//to hide error/success message after particular time period
      
      	$(".hideWhen").delay(5000).slideUp(1000);
      
      	
      
      	
      
      	//get the column list from the tableName
      
      	
      
      	$("#getSelected").change(function(){
      
      		var selectedIs = $(this).val();
      
      		//alert("Seelcted data are ");
      
      		$.ajax({
      
            	type: "POST",
      
                url: "autoMapElements",
      
                data:  {"tableName": selectedIs, "listName": "${listNameRequest?if_exists}"},
      
                success: function (data) {                
      
                 	
      				//alert("Success data is onload ");
                 	$(".tableFieldName").empty();
      
                 	var i=0;
      
                  	$.each(data, function(i, obj) {
      
      			  		var field = obj.etlFieldName;
      				// alert(field);
      			  		var etl_id = obj.seqId;
      
      			  		var is_prime = obj.isPrime;
      			  
      			  		if(field!=null)
      			  		{
      				 		var drop_style = "";
      	var prime_style ="";
      	if(is_prime=="Y")
      	{
      		prime_style = "<span class='' style='color:red!important;'>* </span>"
      	}
      			  				
      			  			i=i+1;
      
       			$(".tableFieldName").append("<li  class='list-group-item' id=etl_dst_"+etl_id+" value="+field+" style='' id='mapTab'><span class='wrapper'><button class='btn btn-default btn-circle btn-fix btn-xs' style='' id='mappTab_"+etl_id+"' is_prime_field="+is_prime+" onclick='validateRadioWithCheckBox(this)' value="+field+"><span class='fa fa-plus'/>&nbsp;"+prime_style+" "+field+"</button></span>&nbsp;"+drop_style+"<a  onclick='showModalHelpWindo(this)' data-toggle='modal' data-target_id=etl_dst_"+etl_id+" data-target=#etl_dst_"+etl_id+"><span class='fa fa-question pull-right btn-info btn-circle'/></a></li>");
      
      			  		}
      
      				});
      
      				rearrangeMappedElements ();  
      
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
      		<#assign fileName = request.getAttribute("fileName")?if_exists>
      		$("#main_div_new_create").append("<input type='hidden' value='${listNameRequest?if_exists}' name='selectedModelName'><input type='hidden' id='existingFile' value='' name='existingFile'><input type='text' name='csvListName' id='csvListName' class='form-control' placeholder='Model Name'><span class='error' id='list_name_error'></span><div><label class='mt-checkbox mt-checkbox-outline'><input type='checkbox' value='Y'  id='chk' onclick='hideUpload()'><span class='pulsate-regular'>Do you want upload same file :${fileName?if_exists}</span></label></div>");
      
      	});							
      
      	//for edit operation
      	 
      	<#if editEtl?has_content>
       	<#if editEtl?if_exists=="Y">
      	    
      rearrangeMappedElements ();  	
        	  	  	
      function rearrangeMappedElements () {  
      
      <#--				    				    
      	<#if parameters.listId?has_content>
        	var cnts = 0;
        	$("#mappedElements li").each(function(){
       				cnts = cnts+1;
       		});
       			if(cnts!=0)
       			{
       				$("#getSelected").attr("disabled","disabled");
       			}
        	
        	<#else>
        		
        		
        				
        	</#if>
      	-->
      	
      	$("#mappedElements").empty();	
        		
       		$("#etlMappingElements li").each(function(){
      				$(this).css("display", "block");
      			});
      	
      	//return;		
      							
        	var selectedValue  = $("#getSelected option:selected").val();
        	if(selectedValue!=null && selectedValue!="" && selectedValue!="undefined" && typeof selectedValue!=undefined)
        	{
        	
      //for the purpose printing the tableName and further details
      //1.table Name is automatic so again we are calling the ajax request In here two time same request is calling	      	
        		$.ajax({
      
              	type: "POST",
        
                	url: "getColumnsByTable",
        
                 	data:  {"tableName": selectedValue},
        
                	success: function (data) {                
        
                		$(".tableFieldName").empty();
        
                   	var i=0;
        
                    	$.each(data, function(i, obj) {
        
        			  		var field = obj.etlFieldName;
        
        			  		var etl_id = obj.seqId;
        
        			  		var is_prime = obj.isPrime;
        			  
        			  		if(field!=null)
        			  		{
        			 
        			 			var drop_style = "";
        			 			var prime_style ="";
      			if(is_prime=="Y")
      			{
      				prime_style = "<span class='' style='color:red!important;'>* </span>"
      			}
        
        			  			i=i+1;
        
       			  		$(".tableFieldName").append("<li  class='list-group-item' id=etl_dst_"+etl_id+" value="+field+" style='' id='mapTab'><span class='wrapper'><button class='btn btn-default btn-circle btn-fix btn-xs' style='' id='mappTab_"+etl_id+"' is_prime_field="+is_prime+" onclick='validateRadioWithCheckBox(this)' value="+field+"><span class='fa fa-plus'/>&nbsp;"+prime_style+" "+field+"</button></span>&nbsp;"+drop_style+"<a  onclick='showModalHelpWindo(this)' data-toggle='modal' data-target_id=etl_dst_"+etl_id+" data-target=#etl_dst_"+etl_id+"><span class='fa fa-question pull-right btn-info btn-circle'/></a></li>");
        
        			  		}
        
        				});
        			
        				//2. for the purpose of printing the mapped elements value at the mapped elements
                 	var listName_from_req =  "${listNameRequest?if_exists}";
                 
                 	//for the purpose of printing the mapped elements details
                
                	if(listName_from_req=="" || listName_from_req=="undefined" || typeof listName_from_req=="undefined" || listName_from_req=="null")
                	{
                		return false;
                	}
                
                 	$.ajax({
       
                     	type: "POST",
         
                     	url: "getCompleteAssociatedFromEtlMapping",
         
                     	data:  {"listName": listName_from_req,"tableName":selectedValue},
         
                     	success: function (data) {   
                     		
                   		for(var i=0;i<data.length;i++)
                      	{
                      		
                      		var is_prime_check_bulk = data[i].isPrime;
                      		
                      		var style_for_prime="";
                       	if(is_prime_check_bulk=="Y")
          	            	{
          	            		style_for_prime="<span class='fa fa-key'/>";
          	            	}
                      	
                      		//hidden to change view of mapped elements
                       	$("#mappedElements").append("<li  class='list-group-item' id='mappedElements_"+data[i].Id+"' value='' style=''><span style='text-overflow:ellipsis!important;overflow:hidden;    word-break: break-all;' title='"+data[i].etlFieldName+"'>"+data[i].etlCustomFieldName+"</span><span class='glyphicon glyphicon-arrow-right'/>&nbsp;&nbsp;<span style='color:red'>"+style_for_prime+"</span><span style='text-overflow:ellipsis!important;overflow:hidden' title='"+data[i].tableColumnName+"'>"+data[i].tableColumnName+"</span><a onclick='removeListItemFromeEtlElementMap("+data[i].Id+",this)' value="+data[i].Id+" etl_map_ele=mappTab_"+data[i].mappTap+"><i class='fa fa-remove' style='color: red;'></i></a><a onclick='showModelElementConfig(this)' data-etlelementid='"+data[i].Id+"' data-etlfieldname='"+data[i].etlFieldName+"'><span class='fa fa-gear pull-right btn-info btn-circle'></span></a></li>");
                       	
        							$("#mappTab_"+data[i].mappTap).append("<i class='fa fa-check pull-right' style='color:green' id=image_mappTab_"+data[i].mappTap+"/>");
        							
                     			$("#mappTab_"+data[i].mappTap).attr("disabled","true");
                     			$("#columnSortable_"+data[i].Id).css("display","none");
                     		
                      	}
                     	
                    	}
               	});   
        			}
        
         		});
      			      
        	}
      
      }
         							
      		//end @vijayakumar
       	</#if>
      	</#if>
      	//end of edit operation					
      
      											
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
      
      	$("#getSelected").attr("disabled","disabled");
      
      	var etlMappingElement = "";
      	var customElementName = "";
      
      	$("#csvSortable li").each(function() {
      
      		var availorNot = $(this).html().search(new RegExp("checked", "i"));
      		
      		if(availorNot>-1)
      		{
      			
      			var etlElementId = $(this).attr("data-etlElementId");
      			
      			var theGotText = $(this).attr("data-etlFieldName");
            			
      			customElementName = $("#etlFieldName_"+etlElementId).val();
      			
      			if (!customElementName) {
      				return true;
      			}
      			
      			var theVar = $(this).html();
            
      			var etlElementMapElementHide = $(this).attr("id");
            
      			//to display none when the clicked process is ready
            
      			$("#"+etlElementMapElementHide).css('display','none');
      			
      			$("span[class='checked']").prop('class', '');
            
      			//end of click none and attribute changes
            
      			etlMappingElement  = $.trim(theGotText);
            			
      		}
      
      	});
      	
      	if (!customElementName) {
      		notificationProgress("Please fill element: "+etlMappingElement, "error");
      		return;
      	}
        
      	var destinationTable =  $(element).val();
          
      	var destinationTableId = $(element).attr('id');
      
      	var is_prime_check = $(element).attr("is_prime_field");
      
      	//destinationObjValue= destinationTableId;
          
      	var tableName = $("#getSelected").val();
      
      	if(etlMappingElement=="")
      	{
      
      		//var msg="Please select csv header name";
      
      		//notificationProgress(msg,"info");
      
      		
      		$("#etlMappingElements").notify(
      
      						  "Please Select Source Header Name Here",'info', 
      
      						  { position:"bottom" }
      
      						  
      
      						);
      
      	} else {
      
      		var listMapper = $("#csvListName").val();
      
      		//alert("We are going to map "+etlMappingElement+" with "+destinationTable+" to the "+tableName+" for the "+listMapper);
      		
      
      		<#assign delete_count=delete_count+1/> 
      
      		//the process of sending ajax request with the storage process of etlmappingelement table
      
      		$.ajax({
      
                  type: "POST",
      
                  url: "getMapElement",
      
                  data:  {"tableName": tableName, "etlMappingElement":etlMappingElement, "destinationTable":destinationTable, "modalName":listMapper, "customElementName":customElementName},
      
                  success: function (data) { 
      
      					var style_for_prime = "";
      	            		if(is_prime_check=="Y")
      	            		{
      	            			style_for_prime="<span class='fa fa-key'/>";
      	            		}
      						
      	            		
      	            		$("#mappedElements").append("<li  class='list-group-item' id='mappedElements_"+data.Id+"' value='' style=''><span style='text-overflow:ellipsis!important;overflow:hidden;   word-break: break-all;' title='"+data.etlFieldName+"'>"+data.etlCustomFieldName+"</span><span class='glyphicon glyphicon-arrow-right'/>&nbsp;&nbsp;<span style='color:red'>"+style_for_prime+"</span><span style='text-overflow:ellipsis!important;overflow:hidden' title='"+data.tableColumnName+"'>"+data.tableColumnName+"</span><a onclick='removeListItemFromeEtlElementMap("+data.Id+",this)' value="+data.Id+" etl_map_ele="+destinationTableId+"><i class='fa fa-remove' style='color: red;'></i></a><a onclick='showModelElementConfig(this)' data-etlelementid='"+data.Id+"' data-etlfieldname='"+data.etlFieldName+"'><span class='fa fa-gear pull-right btn-info btn-circle'></span></a></li>");
      						
                  			$(element).attr("disabled","true");
      
                  			$(element).append("<i class='fa fa-check pull-right' style='color:green' id=image_"+destinationTableId+"/>");
      				      
                  			var msg10=data.etlFieldName+" mapped with "+data.tableColumnName;
      
      						notificationProgress(msg10,"success");
      
                  			
      
                  			}
      
               		});
      
      	
      
      	}
      
      $("#etlMappingElements li").each(function(){
      //alert($(this).html());
      }); 
      
      
      }							
      
      		
      
      	//for removing the list item from the etlMapping element
      
      	function removeListItemFromeEtlElementMap(element,this_obj)
      
      	{
      	 var cnt = 0;
      		var etl_dest_id = $(this_obj).attr('etl_map_ele');
      
      		$("#mappedElements li").each(function(){
      				cnt = cnt+1;
      		});
      		
      		if(cnt==1)
      		{	
      			$("#getSelected").removeAttr('disabled');
      		}
      	
      
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
      
function transferDatafromStagingToProd() {
	var mappedElements_length = $("#mappedElements li").length;
	if(mappedElements_length==0) {
		var msg1="No Mapping found, Unable to save";
		notificationProgress(msg1,"error");
	} else {
		var listName_fn = $("#csvListName").val();
		if(listName_fn!="") {
			$.ajax({
  	            type: "POST",
  	            url: "migrateFromStagingToEtlFinal",
  	            data:  {"listName": listName_fn},
  	            success: function (data) {
   					if(data.deleted=="N" ) {
   						if(data.errorMsg!=null) {
   							notificationProgress(data.errorMsg,"error");
   						} else {
   							notificationProgress("Failed to Complete Proccess","error");
   						}
   					} else {
  	            		var msg5 = "Data Loaded Successfully...!";
  	            		window.location = "<@ofbizUrl>applyEtlModel?model=${listNameRequest?if_exists}</@ofbizUrl>";
  						notificationProgress(msg5,"success");
   					}
  	            }
			});
  
		} else {
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
      			notificationProgress("No Model Name Found, Unable to delete","warn");
      
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
      		 $("#getSelected").removeAttr("disabled");
      	}
      
      
      
      //for dynamic modal window date:21/03/2016
      function showModalHelpWindo(element)
      {
      var data_target = $(element).attr('data-target_id');
      var wholeData = $(element).parent().text();
      
      
      //showing modal window
      $('.modal-dynamo').attr('id',data_target);
      
      //showing the dynamic help header content
      $('.modal-dynamo-header').html("<h4><b>"+wholeData+"</b><div  class='social_media'><a href='#' class='btn btn-primary btn-circle'><span class='fa fa-file-pdf-o'></a><a href='#' class='btn btn-primary btn-circle'><span class='fa fa-youtube'></a><a href='#' class='btn btn-primary btn-circle'><span class='fa fa-facebook'></span></a><a href='#' class='btn btn-primary btn-circle'><span class='fa fa-wikipedia-w'></span></a>&nbsp;&nbsp;&nbsp;<a href='#' class=''><button type='button' class='close' data-dismiss='modal'>&times;</button></a></div></h4>");
      
      
      //selected table name for message of dynamically
      var table_name_attr = $("#getSelected").val();
      
      var field = $(element).parent().text().trim();
      
      //request are passing as per the table name message(C), messageModule(P)
      $.ajax({
      
      	            type: "POST",
      
      	            url: "getHelpContent",
      
      	            data:  {"messageModuleId": "ETL_PROCESS","references":table_name_attr,"MessageTypeId":field},
      
      	            success: function (data) {
      					if(data.messageDescription!="null" || data.messageDescription!="undefined" || typeof data.messageDescription!="undefined")
      					{
      						 $(".modal-dynamo-body").html(data.messageDescription);
      					}
      	            	
      
      	             }
      
                   });
                   
        //end of help content ajax request
      
      
      
      }
      //end @dynamic modal window
      
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
   <div class="portlet light">
      <div class="portlet-title band">
         <div class="caption font-red-sunglo">
            <i class="icon-share font-red-sunglo"></i>
            <#if requestParameters.listId?has_content>
            <span class="caption-subject bold uppercase">${uiLabelMap.editModelMapping}</span>
            <#else>
            <span class="caption-subject bold uppercase">${uiLabelMap.createModel}</span>
            </#if>
            <span class="caption-helper"></span>
         </div>
         <#if listNameRequest?has_content>
         <div class="caption font-red-sunglo configuration" style="float: right">
            <a data-toggle="modal" href="#modelConfiguration">
            <i class="icon-eyeglasses font-red-sunglo"></i>
            <span class="caption-subject uppercase">
            Configuration
            </span>
            </a>
            <span class="caption-helper"></span>
         </div>
         </#if>
      </div>
   </div>
   <#--end of modals-->
   <#--portlet 1-->
   <div class="row-fluid">
   <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12" >
      <div class="portlet light portlet-margin" >
         <!--style="background-image:url('/opentaps_images/transparent_text_effect.png');background-repeat: no-repeat;background-position: center;   min-height: 550px !important;border: 0px solid #B6BFC1 !important"-->
         <div class="portlet-title tin-tin" style="display: flex;">
            <div class="caption-subject">
            </div>
            <div class="margin-resolve" style="width:100%;">
               <div class="band">
                  <span class="portlet-title tin-tin"><span class="fa fa-gift"></span>&nbsp;${uiLabelMap.csvFileHeaderName}</span>
               </div>
               <#if parameters.listId?has_content>
               </#if>
               <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 input-group"<#if parameters.listId?has_content> style="/*display:none*/"</#if>>
               <form action="" class="search-form">
                  <div class="box">
                     <div class="container-2">
                        <span class="icon"><i class="fa fa-search"></i></span>
                        <input type="search" id="search" placeholder="${uiLabelMap.search}" onkeyup="filter(this,'csvSortable')" />
                     </div>
                     <#--
                     <div class="form-group has-feedback">
                        <label for="search" class="sr-only">${uiLabelMap.search}</label>
                        <input type="text" class="form-control" name="search" id="search" placeholder="${uiLabelMap.search}" onkeyup="filter(this,'csvSortable')">
                        <span class="glyphicon glyphicon-search form-control-feedback"></span>-->
                     </div>
               </form>
               <#--<input type="text" class="form-control" placeholder="Search.." id="csvtxtList" onkeyup="filter(this,'csvSortable')" aria-describedby="basic-addon1" style="border-bottom-left-radius: 20px;border-top-left-radius: 20px;float: right !important;">-->
               <span class="input-group-addon "  style="background-color:transparent;border: 0px solid #ccc !important; ">
               <a  data-toggle="modal" href="#draggable" id="listTesting">
               &nbsp;&nbsp;  <span class="fa fa-plus" data-toggle="tooltip" title="${uiLabelMap.addSourceFile} " style="font-size: 16px;"/>
               </a>
               &nbsp;&nbsp;&nbsp;&nbsp;
               <#--getting all available list in the etlmapping table-->
               <span class="dropdown" style="display:none">
               <a class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-tasks" style='color:#32c5d2'></i>
               <ul class="dropdown-menu">
               <#if etlSet?has_content>
               <#list etlSet as list_name>
               <li><a href="main?listName=${list_name?if_exists}" >${list_name?if_exists}</a></li>
               </#list>
               </#if>
               </ul>
               </span>
               <#--end @vijayakumar-->
               </span>
               </div>
            </div>
            <#--<a class="white badge" data-toggle="modal" href="#draggable"> <span class="fa fa-plus"/> </a>-->
         </div>
         <div class="portlet-body" style=" min-height: 40px;">
            <div class="scroller" style=" max-height: 300px; overflow: auto;    overflow-x: auto; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1" id="csvSortable">
               <#if listNameDb?has_content>
               <h4 class="model_name"><b>${uiLabelMap.modelName} :</b> ${listNameRequest?if_exists} </h4>
               </#if>
               <#--for display all the data from EtlMappingElements table-->
               <#if etlMappingElements?has_content>
               <ul class="list-group" id="etlMappingElements">
                  <#assign count=0 />
                  <#list etlMappingElements as etlElement>
                  <li class="list-group-item" value="${etlElement.etlFieldName?if_exists}" data-etlElementId="${etlElement.Id?if_exists}" data-etlFieldName="${etlElement.etlFieldName?if_exists}" id="columnSortable_${etlElement.Id?if_exists}" style="padding-top: 5px;!important"> 
                     <label class="radio-inline" style="font-size: 11px;">
                     <input type="radio" name="optradio" value="${etlElement.etlFieldName?if_exists}" id="optradio">
                     <#--&nbsp;${etlElement.etlFieldName?if_exists}-->
                     <input style="color: black" type="text" name="" value="<#if etlElement.etlCustomFieldName?has_content>${etlElement.etlCustomFieldName}<#else>${etlElement.etlFieldName}</#if>" id="etlFieldName_${etlElement.Id?if_exists}">
                     </label>
                     <a onclick="showModelElementConfig(this)" data-etlElementId="${etlElement.Id?if_exists}" data-etlFieldName="${etlElement.etlFieldName?if_exists}">
                     <span class="fa fa-gear pull-right btn-info btn-circle"></span>
                     </a>
                  </li>
                  <#assign count=count+1 />	
                  </#list>
               </ul>
               </#if>
               <#--end @EtlMappingElements-->
            </div>
         </div>
         <#--
         <h3 class="bgText" style="position:relative;">CSV FILE HEADER NAMES</h3>
         -->
      </div>
   </div>
   <!--end of col-md-4 etc-->
   <#--end of portlet body-->
   <#--file uploading process-->
   <form name="csv-upload" id="csv-upload" action="<@ofbizUrl>uploadCSVFile1</@ofbizUrl>" method="POST" enctype="multipart/form-data">
      <input type="hidden" name="csvFile" id="csvFile">
      <input type="hidden" name="listName" id="listName">
   </form>
   <#--end of file uploading process-->
   <#--portlet 2-->
   <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
   <div class="portlet light portlet-margin">
      <div class="portlet-title tin-tin">
         <div class="band">
            <span class="portlet-title tin-tin" style="/*color:#337AB7*/"><span class="fa fa-gift"></span>&nbsp;${uiLabelMap.destinationTables}</span>
         </div>
         <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12" style="color:#87AFC7">
            <form action="" class="search-form">
               <#-- 
               <div class="form-group has-feedback">
                  <label for="search" class="sr-only">${uiLabelMap.search}</label>
                  <input type="text" class="form-control" name="search" id="search" placeholder="${uiLabelMap.search}" onkeyup="filter(this,'columnSortable')">
                  <span class="glyphicon glyphicon-search form-control-feedback"></span>-->
                  <div class="box">
                     <div class="container-2">
                        <span class="icon"><i class="fa fa-search"></i></span>
                        <input type="search" id="search" placeholder="${uiLabelMap.search}" onkeyup="filter(this,'columnSortable')" />
                     </div>
                  </div>
                  <!--end of form group and has-feedback-->
            </form>
            <select class="bs-select form-control" data-live-search="true" data-size="8" id="getSelected">		                
            <#if eltTableNameList?has_content>
            <option value="" disabled selected>${uiLabelMap.selectTable}</option>
            <#list eltTableNameList as eltTableName>
            <option value="${eltTableName.tableName?if_exists}" <#if editTableName?has_content><#if editTableName?if_exists==eltTableName.tableName>selected</#if></#if>>${eltTableName.tableTitle?if_exists}</option>
            </#list>
            </#if>
            </select>
            </div>
            <!--end of color:#87AFC7-->
         </div>
         <!--end of portlet-title tin-tin-->
         <div class="portlet-body">
            <div class="scroller" style="max-height: 300px; overflow: auto;    overflow-x: auto; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1">
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
      <div class="portlet light portlet-margin">
         <div class="portlet-title tin-tin">
            <div class="band"> <span class="portlet-title tin-tin" style="/*color:#337AB7*/"><span class="fa fa-gift"></span>&nbsp;${uiLabelMap.mappingElements}</span></div>
            <div class="pull-right">
               <#--<i data-toggle="tooltip" title="Import File"><button type="button" class="btn blue btn-outline btn-xs" data-toggle="modal" href="#fileUpload" > <span class="fa fa-upload" /></button></i>-->
               <#--<i data-toggle="tooltip" title="Save Mapped Data"><button type="button" class="btn btn-success btn-xs" onclick="transferDatafromStagingToProd()"><span class="fa fa-save"/></button></i>-->
            </div>
            <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
               <form action="" class="search-form">
                  <#-- 
                  <div class="form-group has-feedback">
                     <label for="search" class="sr-only">${uiLabelMap.search}</label>
                     <input type="text" class="form-control" name="search" id="search" placeholder="${uiLabelMap.search}" onkeyup="filter(this,'mappedElements')">
                     <span class="glyphicon glyphicon-search form-control-feedback"></span>-->
                     <div class="box">
                        <div class="container-2">
                           <span class="icon"><i class="fa fa-search"></i></span>
                           <input type="search" id="search" placeholder="${uiLabelMap.search}" onkeyup="filter(this,'mappedElements')"/>
                        </div>
                     </div>
               </form>
               </div>
               <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                  <button type="button" class="btn btn-danger btn-xs pull-right" value="${listNameRequest?if_exists}" style="float:right" onclick="removeAllMapping(this)">${uiLabelMap.removeAll}</button>
                  <button type="button" class="btn btn-primary btn-xs pull-right" value="${listNameRequest?if_exists}" style="margin-right:5px;" onclick="transferDatafromStagingToProd()">${uiLabelMap.save}</button>
                  <#if requestParameters.listId?has_content>
                  <#--<button type="button" class="btn btn-primary btn-xs pull-right" data-toggle="modal" href="#demo_modal_process" >View Process</button>-->
                  </#if>
               </div>
            </div>
            <div class="portlet-body">
               <div class="">
                  <div class="scroller" style="max-height: 300px; overflow: auto;    overflow-x: auto; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1">
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
   <!--Popup process-->
   <div class="modal fade" id="demo_modal_process">
   <div class="modal-dialog modal-lg">
   <div class="modal-content c-square">
   <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-label="Close">
      <span aria-hidden="true">\D7</span>
      </button>
      <h4 class="modal-title ">Update Process</h4>
   </div>
   <!--end of modal header-->
   <div class="modal-body">
      <!--update Process -->
      <div class="row">
         <div class="col-md-12">
            <div class="portlet light">
               <div class="portlet-title">
                  <div class="">
                     <i class="icon-settings "></i>
                     <span class="caption-subject bold uppercase">Update Process</span>
                  </div>
               </div>
               <div class="portlet-body form">
                  <form role="" method="post" name="updateProcess" id="updateProcess" action="<@ofbizUrl>updateEtlProcessModel</@ofbizUrl>">
                     <div class="form-body">
                        <input type="hidden" class="form-control" name="modelId" id="modelId" value="${requestParameters.listId?if_exists}">
                        <input type="hidden" class="form-control" name="eProcessId" id="form_control_1" value="<#if process?has_content>${process.processId?if_exists}</#if>" placeholder="Process Id">
                        <label for="form_control_1"><span class="caption-subject bold uppercase">Process Id &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="padder">:&nbsp;<#if process?has_content>${process.processId?if_exists}</#if></span></span></label>
                        <br>
                        <label for="form_control_1"><span class="caption-subject bold uppercase">Model Name  &nbsp;<span class="padder">:&nbsp;<#if process?has_content>${process.modalName?if_exists}</#if></span></span></label>
                        <div class="form-group form-md-line-input">
                           <input type="text" class="form-control onkey_press" name="eProcessName" id="eProcessName" value="<#if process?has_content>${process.processName?if_exists}</#if>" placeholder="Process Name">
                           <label for="form_control_1"></label>
                           <span class="help-block error_too" id="processName_error}"></span>
                        </div>
                        <div class="form-group form-md-line-input">
                           <select class="form-control" name="updateEtlProcessService" id="updateEtlProcessService">
                              <#--<#assign EtlProcessService =delegator.findAll("EtlProcessService") />-->
                              <#if EtlProcessService?has_content>
                              <option selected disabled>Select Service</option>
                              <#list EtlProcessService as service>
                              <option value="${service.serviceId?if_exists}" <#if process?has_content><#if process.serviceName?if_exists==service.serviceId?if_exists> selected</#if></#if>>${service.serviceName?if_exists}</option>
                              </#list>
                              </#if>
                           </select>
                           <label for="form_control_1"></label>
                           <span class="help-block" id="processName_error"></span>
                        </div>
                        <div class="form-group form-md-line-input">
                           <textarea class="form-control" rows="3" name="eDescription" placeholder="Description" value=""><#if process?has_content>${process.description?if_exists}</#if></textarea>
                           <label for="form_control_1"></label>
                        </div>
                     </div>
                     <div class="form-actions noborder pull-right">
                        <button type="submit" id="processForm11" class="btn blue restict_form sub_bt">Update</button>
                     </div>
                  </form>
               </div>
            </div>
         </div>
      </div>
      <!--End-->
   </div>
   <!--End-->
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
   
   function hideUpload(){
   	if (document.getElementById("chk").checked == true){
   		$(".fileinput").hide();
   		$("#existingFile").val("Y");
   		//alert("d");
   	}else{
   		//alert("s");
   		$(".fileinput").show();
   		$("#existingFile").val("N");
   	}
   }
   
   /*$("#fileProg").click(function(){
   	$("#csv-upload").submit();
   });*/	
   
$(document).ready(function() {

	$("#downloadFile").click(function(event) {
	    console.log('click download file');
	    //let serviceName = $('#csv-upload input[name=serviceId] option:selected').val();
	    let serviceId = $('#serviceId option:selected').val();
	    console.log('serviceId > '+serviceId);
	    let fileTplLoc = $('#serviceId option:selected').attr('data-fileTplLoc');
	    let isAbsoluteTplLoc = $('#serviceId option:selected').attr('data-isAbsoluteTplLoc');
	    
	    if (!serviceId) {
	    	showAlert ("error", 'Please select service!');
	    	return false;
	    }
	    
	    if (isAbsoluteTplLoc && isAbsoluteTplLoc=='Y') {
	    	window.location = fileTplLoc;
	    } else {
	    	window.location = 'downloadFile?resourceName=etl-process-resource&componentName=Etl-Process&fileName='+fileTplLoc;
	    }
	    
	    //window.open('downloadFile?resourceName=etl-process-resource&componentName=Etl-Process&fileName='+fileTplLoc, '_blank');
	});

});      
   
</script>
<#--for destination tab id -->
<input type="hidden" id="dest_tab_id"/>
<#--end @destination tab id-->
