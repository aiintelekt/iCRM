<#assign lId = requestParameters.listId?if_exists/> 
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
            <div>
               <#if listNameDb?has_content>
               <div id="main_div_new_create">
                  <div id="newList_adder">
                     <span class="bold">${listNameRequest?if_exists}</span>&nbsp;&nbsp;<button type="button" class="btn btn-default btn-xs" id="new_list_creation" style="background-color:white">Create New</button>
                     <input type="hidden" id="csvListName" value="${listNameRequest?if_exists}">
                  </div>
               </div>
               <#else>
               <input type="text" name="csvListName" id="csvListName" class="form-control" placeholder="Please enter Model Name">
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
<div class="modal fade draggable-modal" id="eModel" tabindex="-1" role="basic" aria-hidden="true">
   <div class="modal-dialog">
      <div class="modal-content">
         <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
            <h4 class="modal-title">${uiLabelMap.configureExportModel} </h4>
            <h4 class="modal-title"></h4>
         </div>
         <div class="row-fluid">
            <table class="table  table-striped table-condensed flip-content">
               <thead class="flip-content">
                  <tr>
                     <th style="color:#49494b;">${uiLabelMap.modelId}</th>
                     <th style="color:#49494b;">${uiLabelMap.modelName} </th>
                     <th style="color:#49494b;">${uiLabelMap.assignedTo}</th>
                     <th></th>
                  </tr>
               </thead>
               <tbody>
                  <#assign index=0/>
                  <#if exportModels?has_content>
                  <#list exportModels as expModel>
                  <form name="assignm" action="<@ofbizUrl>assignExpModel</@ofbizUrl>" method="post">
                     <input type="hidden" name="impModelId" value="${requestParameters.model?if_exists}"/>
                     <input type="hidden" name="expModelId" value="${expModel.modelId?if_exists}"/>
                     <tr>
                        <td>${expModel.modelId?if_exists}</td>
                        <td>${expModel.modelName?if_exists}</td>
                        <#if expModel.expModelId?has_content>
                        <#assign assignModel = delegator.findOne("EtlModel", {"modelId" : expModel.expModelId?if_exists},false)/>
                        <td>${assignModel.modelName?if_exists}</td>
                        <td><span class="">${uiLabelMap.assigned}</span></td>
                        <#else>
                        <td><span class="red" id=span_${index}>${uiLabelMap.notAssigned}</span></td>
                        <td><input type="submit" class="btn btn-xs btn-primary"  id="assignModel" value ="${uiLabelMap.assign}"/></td>
                        </#if>
                     </tr>
                     <#assign index =index+1/>
                  </form>
                  </#list>
                  </#if>
               </tbody>
            </table>
         </div>
      </div>
      <!-- /.modal-content -->
   </div>
   <!-- /.modal-dialog -->
</div>
<#--end of #2.file upload button process-->  
<#--modals do not enter any thing here NOTE IMPORTANT-->
<#--for file upload js-->
<script src="/metronic/js/bootstrap-fileinput.js" type="text/javascript"></script>
<#--end of file upload js-->
<#--file upload plugin-->
<#-- Manjesh -->
<link href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
<#-- End Manjesh -->
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
<#--for ajax block ui-->
<script src="/metronic/js/jquery.blockUI.js" type="text/javascript"></script>
<script type="text/javascript"> 
   $(document).ready(function() { 
   		<#--	<#if result?has_content && result?if_exists=="lock">
              $.blockUI({
               message: '<h3><img src="/etl_images/busy.gif" />Process executing... Please wait</h3>' 
               }); 
   		</#if>
   		<#if request.getAttribute("execute")?has_content && request.getAttribute("execute")?if_exists=="lock">
              $.blockUI({
               message: '<h3><img src="/etl_images/busy.gif" />Process executing... Please wait</h3>' 
               }); 
   		</#if>-->
   }); 
</script>
<#--end of ajax block ui-->
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
   .gradiant {
   background: #D7EFD7;font-weight: bold;
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
   h3 {
   font-size: 17px !important;
   }	
   @media only screen and (min-width: 768px)
   {
   .bottom_wider{
   margin-top:15px !important;
   }
   }
   .btn-group.bootstrap-select.bs-select.form-control.btn-sm {
   padding: 0px 0px 0px 0px !important;
   }
   //Manjesh
   .box{
   height: 35px;
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
   height: 30px;
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
   margin-top: 7px;
   z-index: 1;
   color: #4f5b66;
   }
   .container-2 input#search:focus, .container-2 input#search:active{
   outline:none;
   color: #082935;
   width: 350px;
   }
   .container-2:hover input#search{
   color: #082935;
   width: 350px;
   }
   .container-2:hover .icon{
   color: #00AFF0;
   }
   .boxS{
   margin: 0px auto;
   width: 200px;
   height: 20px;
   padding-bottom: 25px;
   }
   .container-2S{
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
   .container-2S input#search{
   width: 35px;
   height: 30px;
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
   .container-2S input#search::-webkit-input-placeholder {
   color: #082935;
   }
   .container-2S input#search:-moz-placeholder { /* Firefox 18- */
   color: #082935;  
   }
   .container-2S input#search::-moz-placeholder {  /* Firefox 19+ */
   color: #082935;  
   }
   .container-2S input#search:-ms-input-placeholder {  
   color: #082935;  
   }
   .container-2S .icon{
   position: absolute;
   top: 50%;
   margin-left: 17px;
   margin-top: 7px;
   z-index: 1;
   color: #4f5b66;
   }
   .container-2S input#search:focus, .container-2 input#search:active{
   outline:none;
   color: #082935;
   width: 250px;
   }
   .container-2S:hover input#search{
   color: #082935;
   width: 250px;
   }
   .container-2S:hover .icon{
   color: #00AFF0;
   }		
   .filter-option.pull-left{
   padding : 4px !important;
   }
   .bootstrap-select.btn-group .btn .caret{
   margin-top: -15px !important;
   }
</style>
<#--end of scroll bar check-->
<#--only for mannual script-->
<script>
   var destinationObjValue="",active_tab_id="";
   var retrieved_tab = "${active_tab?if_exists}"+"";
   
   $(document).ready(function(){
   
   //to hide always header of upload section
   $(".action_upload_title_view").hide();
   
   
   //for the purpose of default loading the data
   <#if requestParameters.model?has_content>
   var modalName = "${requestParameters.model?if_exists}";
   
   	var tabel_attr = $("a[onld_attr='default_ld']").attr('table_attr');
   	 $("#"+modalName).prop('disabled', true);
   	//to display back to the view screen
   	$("#applyToProcess_view").show(1000);
   	$("#file_upload_view").hide(1000);
   	
   	//end of disable back to the end screen
   	
   	var listName = "${requestParameters.model?if_exists}";
   	var listId = "${requestParameters.model?if_exists}";
   	var table_attr_value = tabel_attr;
   	
   	global_list_name = listName;
   	global_table_name  = table_attr_value;
   	
   	
   	//getting the etlsourcetable Data
   	$.ajax({
   
   	            type: "POST",
   
   	            //url: "getEtlSource",
   	            url: "getCompleteAssociatedFromEtlMapping",
   
   	            data:  {"listName": listName,"tableName":table_attr_value},
   
   	            success: function (data) {
   	            var length_data = data.length;
   	         	$("#mappedElements").empty();
   		           if(length_data==0)
   		           {
   		           	notificationProgress("Empty Association","info");
   		           	return false;
   		           }
   		           
   		            for(var i=0;i<length_data;i++)
   		            {
   		            //var pId = data[i].processId;
   		             document.getElementById("uploadEtl").action = "uploadFile?modelName="+listName+"&id=1";
   		            	$("#executeModalData").attr('value',data[i].processId);	
   		            	$("#removeModalAssoc").attr('value',data[i].processId);
   		            
   		            
   		            
    		            //added by m.vijayakumar date:13/05/2016
    		            $("#process_det option").each(function(){
    		            	var process_det_value = $(this).val().trim();
    		            	if(process_det_value==data[i].processId)
    		            	{
    		            		$(this).attr("selected",true);
    		            		
   
   					//to display at the top place of the value selected
   					$("button[data-id='process_det']>span.filter-option").text($(this).text());
   					
    		            		
    		            	}
    		            	
    		            });
    		            
    		            //end @vijayakumar
   		            
   		            	//alert("The primary key or not "+data[i].isPrime);
   		            	var is_prime_cnt = data[i].isPrime;
   		            	var style_prime = "";
   		            	if(is_prime_cnt=="Y")
   		            	{
   		            		style_prime="<span class='fa fa-key'/>";
   		            	}
   		            	//hidden for style changes
   		            	$("#mappedElements").append("<li  class='list-group-item' value='' style=''><span style='text-overflow:ellipsis!important;overflow:hidden' title='"+data[i].etlFieldName+"'>"+data[i].etlCustomFieldName+"</span><span class='glyphicon glyphicon-arrow-right'/>&nbsp;&nbsp;<span style='color:red'>"+style_prime+"</span><span style='text-overflow:ellipsis!important;overflow:hidden' data-toggle='tooltip' title='"+data[i].tableColumnName+"'>"+data[i].tableColumnName+"</span></li>");
   		            	//end of style changes
   		            	if(data[i].processDescription!="" || data[i].processDescription!=null || data[i].processDescription!="undefined" || typeof data[i].processDescription!=undefined)
   		            	{
   		            			$('#process_description').text(data[i].processDescription);
   		            	}
   		            	
   			
   			//$("#mappedElements").append("<li  class='list-group-item' value='' style='' ><details style='background-color:#E5F3E9;  '><summary style='background-color: #eee;    cursor: pointer;   font-weight: 500; '>"+data[i].etlFieldName+"</summary>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color:red'>"+style_prime+"</span>"+data[i].tableColumnName+"</li>");
   		            	
   		            }
   	            													            		
   	            }
   
                });	
   
   	
                   
                   
      	$("ul[class='nav nav-tabs'] li").each(function(){
      		$(this).css("display","block");
      	});
      
   <#assign etl_process_req = delegator.findByAnd("EtlSourceTable" ,Static["org.ofbiz.base.util.UtilMisc"].toMap("listName",requestParameters.model?if_exists),null,false)/>
           <#if etl_process_req?has_content>
                  <#assign etlProcess1 = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(etl_process_req) />
   </#if>
   		var execute_table_name_url="${etlProcess1.tableName?if_exists}";
   
   $("#execute_table_name").val(execute_table_name_url)
   	$("ul[class='nav nav-tabs'] li").each(function(){
       		var current_table_name = $(this).children().attr("id");
       		//alert(execute_table_name_url+"_____"+current_table_name);
       		if(current_table_name!=execute_table_name_url)
       		{
       			$(this).css("display","none");
       		}
       });
       
       
        if(execute_table_name_url!="")
   	    {
   	    	$("ul[class='nav nav-tabs'] li").each(function(){
   	    	
   			    	if($(this).hasClass('active'))
   			    	{
   			    				$(this).removeClass("active");
   			    				
   	    			}
   	    				var retrieveATT = $(this).children().attr("href").split("#");
   	    				var od = retrieveATT[1];
   	    				
   	    				$("#"+od).removeClass("in active");
   	    			
   	    		if($(this).children().attr("id")==execute_table_name_url)
   	    		{
   	    			
   	    				$(this).addClass("active");
   	    				
   	    				//get the href content
   	    				var retrieveATT = $(this).children().attr("href").split("#");
   	    				var od = retrieveATT[1];
   	    				
   	    				
   	    				$("#"+od).addClass("in");
   	    				$("#"+od).addClass("active");
   	    				
   	    		}
   	    		
   	    			
   	    		
   	    	});
   	    }
      
      	
      </#if>
   //end of default loading the data
   
   //fix on file process
   <#if request.getParameter("id")?has_content>
   
   	$("#applyToProcess_view").hide();
   	$("#file_upload_view").show();
   </#if>
   //end of file process
   	
   
   	//for tooltip
   
   	 $('[data-toggle="tooltip"]').tooltip(); 
   
   	
    //to get active tab details added by m.vijayakumar date:14/05/2016
      /* $(this).click(function(){
    
    	$("ul[class='nav nav-tabs'] li").each(function(){
    		if($(this).hasClass('active'))
    		{
    			var active_tab = $(this).children().attr("id");
    			active_tab_id = active_tab;
    			
    		}
    		
    	});
    });
    
   //make select the tab id 
   	    var retrieved_tab = "DmgPartyLead";
     
     if(retrieved_tab!="")
     {
     	$("ul[class='nav nav-tabs'] li").each(function(){
     	
       	if($(this).hasClass('active'))
       	{
       				$(this).removeClass("active");
       				
     			}
     				var retrieveATT = $(this).children().attr("href").split("#");
     				var od = retrieveATT[1];
     				
     				$("#"+od).removeClass("in active");
     			
     		if($(this).children().attr("id")==retrieved_tab)
     		{
     			
     				$(this).addClass("active");
     				
     				//get the href content
     				var retrieveATT = $(this).children().attr("href").split("#");
     				var od = retrieveATT[1];
     				
     				
     				$("#"+od).addClass("in");
     				$("#"+od).addClass("active");
     				
     		}
     		
     			
     		
     	});
   	    }*/
    
    
    //make select the tab id 
   	
   
   	
   
   	//to hide error/success message after particular time period
   
   	$(".hideWhen").delay(5000).slideUp(1000);
   
   	//for the purpose of change etl process details
   	$(".modal_select").change(function(){
   		
   		var groupId = $("#group_deta").val();
   		$("#groupByFilter").val();
   		var selected_filter = $(this).val();
   		if(selected_filter=="" || selected_filter==null || typeof selected_filter=="undefined" || selected_filter=="undefined")
   		{
   		notificationProgress("Unable to filter, Filter type is missing","error");
   		return false;
   		}
   		
   		//setting the value for the form at default all will be stored
   		$("#filterBy").val(selected_filter);
   		
   		//let's submit the form
   		$("#applyEtlModel").submit();
   
   	});
   	
   	
   	$("#group_deta").change(function(){
   		var group_details = $(this).val();
   		//alert(group_details);
   		$("#groupByFilter").val(group_details);
   		$(".modal_select").each(function(){
   			var checkedClass = $(this).parent().attr("class");
   			if(checkedClass=="checked")
   			{
   				$("#filterBy").val($(this).val());
   			}
   		});
   		$("#applyEtlModel").submit();
   	});
   	//added by m.vijayakumar date:17/06/2016 for 
   											
   
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
   
   	
   
   
   	//for notification message
   
   	function notificationProgress(msg,errorType)
   
   	{
   
   		$.notify(msg,errorType);
   
   	}
   
   	
   //created for apply screen date:17/03/2016
   var global_list_name,global_table_name;
   	
   	//get for etl resource action
   	function getEtlResource(element){
   
 		$("#executeModalData").attr("seq_num","");
   		$("#executeModalData").attr("group_id","");
		   	
   		//to provide sequence number based iteration
   		var sequenceNum = $(element).attr("sequ_num");
   		var group_id = $(element).attr("group_id");
   	
   		if(sequenceNum!="") //sequence number must one to display the bulk upload propertiese
   		{
    		if(group_id=="")
    		{
    			notificationProgress("Group Id is missing, Please configure properly","info");
    			return false;
    		}
   			$("#executeModalData").attr("seq_num",sequenceNum);
   			$("#executeModalData").attr("group_id",group_id);
   			//alert(sequenceNum);
   		}
   	
   		//to display back to the view screen
   		$("#applyToProcess_view").show(1000);
   		$("#file_upload_view").hide(1000);
   	
   		//end of disable back to the end screen
   	
   		var listName = $(element).attr('value');
   		var listId = $(element).attr('id');
   		var table_attr_value = $(element).attr('table_attr');
   	
   		global_list_name = listName;
   		global_table_name  = table_attr_value;
   	
   		//getting the etlsourcetable Data
   		$.ajax({
			type: "POST",
			url: "getEtlSource",
			data:  {"listName": listName, "tableName": table_attr_value},
			success: function (data) {
				var length_data = data.length;
				$("#mappedElements").empty();
				if(length_data==0)
				{
					notificationProgress("Empty Association","info");
					return false;
				}
			   
				for(var i=0;i<length_data;i++)
				{
					$("#executeModalData").attr('value', data[i].processId);	
					$("#executeModalData").attr('table_name', data[i].tableName);	
					$("#executeModalData").attr('data-modelName', listName);	
					$("#removeModalAssoc").attr('value', data[i].processId);
				
					$("#process_det option").each(function() {
						var process_det_value = $(this).val().trim();
						if(process_det_value==data[i].processId)
						{
							$(this).attr("selected",true);
		
							//to display at the top place of the value selected
							$("button[data-id='process_det']>span.filter-option").text($(this).text());
						}
					
					});
				
					//alert("The primary key or not "+data[i].isPrime);
					var is_prime_cnt = data[i].isPrime;
					var style_prime = "";
					if(is_prime_cnt=="Y")
					{
						style_prime="<span class='fa fa-key'/>";
					}
					//hidden for style changes
					$("#mappedElements").append("<li  class='list-group-item' value='' style=''><span style='text-overflow:ellipsis!important;overflow:hidden' title='"+data[i].etlFieldName+"'>"+data[i].etlFieldName+"</span><span class='glyphicon glyphicon-arrow-right'/>&nbsp;&nbsp;<span style='color:red'>"+style_prime+"</span><span style='text-overflow:ellipsis!important;overflow:hidden' data-toggle='tooltip' title='"+data[i].tableColumnName+"'>"+data[i].tableColumnName+"</span></li>");
					//end of style changes
					//alert("The got descriptio value s are "+data[i].processDescription);
					if(data[i].processDescription!="" || data[i].processDescription!==null || data[i].processDescription!="undefined" || typeof data[i].processDescription!=undefined)
					{
						$('#process_description').text(data[i].processDescription);
					}
					
					if(data[i].processDescription==null)
					{
						$('#process_description').html("<b style='color:white'> Description not found</b>");	
					}
		
					if(data[i].processDescription==null)
					{
						$('#process_description').html("<b style='color:white'> Description not found</b>");	
					}
		
					//$("#mappedElements").append("<li  class='list-group-item' value='' style='padding: 2px!important;font-size: 12px;' ><details style='background-color:#E5F3E9;  '><summary style='background-color: #eee;    cursor: pointer;   font-weight: 500; '>"+data[i].etlFieldName+"</summary>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color:red'>"+style_prime+"</span>"+data[i].tableColumnName+"</li>");
					
				}
			}
		});
   }
   	
   
   //for association modal to process
   function associateModalToProcess()
   {
   													
   	var process_det_selected = $("#process_det :selected").val();
   	var process_det_id = $("#process_det :selected").attr("id");
   	var process_det_selected_text  = $("#process_det :selected").text();
   	
   	
   	
   	$("#process_det_id").prop('disabled','true');
   	
   	
   	
   	
   	if(global_table_name=="undefined" || typeof global_table_name==="undefined")
   	{
   	
   		notificationProgress("Model's table Name is missing","info");
   		return false;
   	}
   	
   	if(process_det_id==undefined || process_det_id=="" || typeof process_det_id==="undefined")
   	{
   		notificationProgress("Please select Process name","warn");
   		return false;
   	}
   	
   	if(global_list_name=="undefined" || typeof global_list_name === "undefined")
   	{
   			notificationProgress("Please select Model Name","info");
   		    return false;
   	}else
   	{
   		//make disable whenever we needed
   		$("#"+global_list_name).css('pointer-events','none');
   		$("#"+global_list_name).closest( "li" ).addClass("gradiant");
   		$("#executeModalData").attr('value',process_det_id);
   		$("#removeModalAssoc").attr('value',process_det_id);
   		$.ajax({
   
   	            type: "POST",
   
   	            url: "getModalProcessAssociation",
   
   	            data:  {"processId": process_det_selected,"modalName":global_list_name,"tableName":global_table_name},
   
   	            success: function (data) {
   	            	var success_msg = data.set;
   	            	var notification_msg = "none of the message recieved";
   	            	var notiMsg_type = "info";
   	            	if(success_msg =="Y")
   	            	{
   	            	if(data.processDescription!="" || data.processDescription!=null || data.processDescription!="undefined" || typeof data.processDescription!=undefined)
   	            	{
   	            			$("#process_description").text(data.etlDescription);
   	            	}
   	            	
   	            		$("#"+global_list_name).css("color","green");
   	            		notification_msg =  global_list_name+" associated with "+process_det_selected_text;
   	            		notiMsg_type = "success";
   	            	}else
   	            	{
   	            		notification_msg = global_list_name+" association with "+process_det_selected_text+" failed";
   	            		notiMsg_type = "error";
   	            	}
   	            	notificationProgress(notification_msg,notiMsg_type);
   				}
                });
   		
   	}
   }
   
   var execute_table_name="";
   
	//for the purpose of execute modal activity
	function executeModal(element) {
	   		
		//for the purpose of  execute model based on multiple upload
	   	var sequenceNumber = $(element).attr("seq_num");
	   	//alert("The Sequence number is "+sequenceNumber);
	   	if(sequenceNumber!="" && (sequenceNumber=="1" || sequenceNumber=="01")) {
	   		$("#nav_caption").css("display","none");
	   		$("#check_container").css("display","block");
	   		$("#check_container1").css("display","block");
	   		$(".action_upload_title_view").show();
	   		$("#download_viewer").empty();
	   		<#if groupByFilter?has_content>
	     		 <#assign etlTemplateDownload1 = delegator.findByAnd("EtlTemplateDownload", { "groupId" : groupByFilter?if_exists},null,false)?if_exists>
	                 <#if etlTemplateDownload1?has_content>
	                 $("#download_viewer").append('<option></option>');
	                   <#list etlTemplateDownload1 as template>
	                   	$("#download_viewer").append('<option value="${template.seqId?if_exists}">${template.downloadInterface?default("Download")}</option>');
	                   </#list>
	                    </#if>
	   			
	   		</#if>
				   		
	   	} else {
	   		var groupId = "${parameters.groupId?if_exists}";
	   		if(groupId=="") {
	   			$("#nav_caption").css("display","block");
	   			$("#check_container").css("display","none");
	   			$("#check_container1").css("display","none");
	   		}
	   		$(".action_upload_title_view").hide();
	   		
	   	}
		<#if !parameters.groupId?has_content>
			$("#groupIdExport").val($(element).attr("group_id"));
			$("#modelExport").val(global_list_name);
		
		</#if>
	   		
	   	var etlProcessElement =  $(element).val();
	   	var requestData = {'processId': etlProcessElement};
	    $.getJSON('getEtlImportType', requestData, function(data) {      
			if(data.result=="lock"){  
	 	 	<#--$.blockUI(
			{ message: '<h3><img src="/etl_images/busy.gif" /> Process executing... Please wait</h3>' }
			);--> 
			}  	
	    });  
	   	//to make display all the tab when before the function call because previous function will display all the tab
	   	$("ul[class='nav nav-tabs'] li").each(function(){
	   		$(this).css("display","block");
	   	});
	   	//end of tab display process
	      	
	   	processId  = $(element).val();
	   	if(processId==null || processId=="undefined" || typeof processId=="undefined" || processId=="") {
	   
	   		notificationProgress("Process missing , Please follow the sequence ","warn");
	   		return false;
	   	}
	   	$("#processId").val(processId);
	   	
	   	let modelName = $(element).attr('data-modelName');
	   	$("#uploadEtl").attr("action", "uploadFile?modelName="+modelName+"&id=1")
	   	
	   	//alert("This way u can able to get process id "+processId);
	   	//THIS IS LIST NAME IF NEED LIST NAME MEANS PLEASE USE FROM HERE
	   	$("#applyToProcess_view").hide(500);
	   	$("#file_upload_view").show(500);
	   	
	    execute_table_name = $(element).attr("table_name");
	      	
	  	if(execute_table_name=="") {
	  		<#if execute_table_name?has_content>
	   		execute_table_name = ${execute_table_name?if_exists};
	   		</#if>
	  	}
		      	
	  	$("#execute_table_name").val(execute_table_name);
	  	$("ul[class='nav nav-tabs'] li").each(function(){
	   		var current_table_name = $(this).children().attr("id");
	   		//alert(execute_table_name+"_____"+current_table_name);
	   		if(current_table_name!=execute_table_name)
	   		{
	   			$(this).css("display","none");
	   		}
	   	});
			       
	  	if(execute_table_name!="") {
	    	$("ul[class='nav nav-tabs'] li").each(function(){
	    	
				if($(this).hasClass('active')) {
			    	$(this).removeClass("active");
			    				
	    		}
				var retrieveATT = $(this).children().attr("href").split("#");
				var od = retrieveATT[1];
				
				$("#"+od).removeClass("in active");
	    			
	    		if($(this).children().attr("id")==execute_table_name) {
	    			
					$(this).addClass("active");
					
					//get the href content
					var retrieveATT = $(this).children().attr("href").split("#");
					var od = retrieveATT[1];
										
					$("#"+od).addClass("in");
					$("#"+od).addClass("active");
	    				
	    		}
	    		
	    	});
	   	    	
	   	}
	      	
	}




   
   function removeAssocModelToProcess(element)
   {
   	processId = $(element).val();
   	if(processId=="" || processId=="undefined" || typeof processId ==="undefined")
   	{
   		notificationProgress("Process is missing for remove,Please follow the sequence","error");
   		return false;
   	}
   	$.ajax({
   
   	            type: "POST",
   
   	            url: "removeAssocModelToProcess",
   
   	            data:  {"processId": processId},
   
   	            success: function (data) {
   	          
   	            
   	            	if(data.set=="Y")
   	            	{
   	            		$("#"+global_list_name).parent().css("background-color","white");
   	            		$("#"+global_list_name).parent().css("font-weight","normal");
   	            		
   	            		//added by m.vijayakumar date:30/03/2016 for disable the value associated to execute
                   $("#executeModalData").removeAttr("value");
                   $("#process_description").text("");
                   //end @vijayakumar
   	            		$("button[data-id='process_det']>span.filter-option").text("Select Process");
   	            		notificationProgress("Association Removed","success");
   	            	}
   	            	
   	            }
   	            	
                });
   	
   }
   
   	function whenPageLoadStarts()
   	{
   		var fileName = $("#csv_fileName_lst").val();
   		
   		if(fileName=="" || typeof fileName=="undefined" || fileName=="undefined" || fileName==null)
   		{
   			$("#file_name_error_csv").html("<b>Please select file</b>");
   			return false;
   		}else
   		{
      $("#model").val(global_list_name);
      <#if restrictUpload?has_content>
      	notificationProgress("Please wait model is running...","error");
      <#else>
   			$("#uploadEtl").submit();
   		//for load when needed
   		$.blockUI({
    		message: "<img src='/images/44.gif'><br><font color='black'><b>Processing...</b></font>" , 
    		overlayCSS: { backgroundColor: '#EAEAEA' },
   	 	css: { 
            border: 'none', 
            padding: '15px', 
            backgroundColor: 'none', 
            '-webkit-border-radius': '10px', 
            '-moz-border-radius': '10px', 
            opacity: .9, 
            color: '#fff' 
     	   } }); 
      </#if>
   			
   		
   	}
   	
   		
   	}
   	 //for block ui popup
   	 function whenPageOnload()
   	 {
   		setTimeout($.unblockUI, 1000);
   	}
   	 
   	 //page refresh process
   	 function pageRefres()
   	 {
   	 	//location.reload();
   var url = window.location.href;    
   if (url.indexOf('?') > -1 &&  window.location.href.indexOf("&id=1") ===  -1){
     url += '&id=1'
   }
   window.location.href = url;
   	 }
   	 
   	 
   	 //added at 15/06/2016
   	 function actionResolver(element)
   	 {
   	 
   	 		
   	 		//to make request able to handle date:21/06/2016
   	 		var groupId = "${requestParameters.groupId?if_exists}";
   	 		var model_list_id = "${requestParameters.model?if_exists}";
   	 		
   	 		
   	 		if(groupId == "")
   	 		{
   	 			groupId = $("#executeModalData").attr("group_id");
   	 		}
   	 		
   	 		if(model_list_id=="")
   	 		{
   	 			model_list_id = global_list_name;
   	 		}
   	 		
   	 	if($(element).is(':checked'))
   	 	{
   	 		
   	 		
   	 		
   	 		 //alert("The got groupid and model id is "+groupId+" --  "+model_list_id);
   	 		 
   	 		
   	 		//end @vijayakumar
   	 		$("#uploadEtl").attr("action","executeAllModels?groupId="+groupId+"&processId="+model_list_id+"&id=1")
   	 		
   	 	}else
   	 	{
   	 	 	//alert("The unchecked group details "+groupId+" --  "+model_list_id);
   	 		
   	 		$("#uploadEtl").attr("action","uploadFile?groupId="+groupId+"&processId="+model_list_id+"&id=1")
   	 	}
   	 }
   	 
   	 //function for common download process
   	 function commonDownload(element)
   	 {
   	 
   	 	//setting fo the download sequence option
   	 	var downloadSeq = $(element).val();
   	 	if(downloadSeq=="")
   	 	{
   	 		//notificationProgress("Download Configuration missing","error");
   	 	}else
   	 	{
   	 		$("#downloadSequence").val(downloadSeq);
   	 		$("#downloadCommonExport").submit();
   	 	}
   	 	//end @vijayakumar
   	 	
   	 	
   	 }
</script>
<#--end of mannual script-->	
<!-- BEGIN HEADER -->
<body style="overflow: scroll;">
   <div class="col-md-12 col-lg-12 col-xs-12 col-sm-12" style="">
      <div></div>
      <#--end of modals-->
      <#--portlet 1-->
      <div class="row-fluid">
         <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12"<#if requestParameters.model?has_content> style="display:none;"</#if>>
         <div class="portlet light" style="    min-height: 550px !important;border: 0px solid #B6BFC1 !important">
            <div class="portlet-title tin-tin" style="display: flex;">
               <div class="caption-subject   " style="color:#87AFC7">
               </div>
               <div class="margin-resolve" style="width:100%">
                  <div class="input-group" style="width:100%">
                     <div class="band" style="/*color:#337AB7*/"><span class="fa fa-gift"></span>&nbsp;${uiLabelMap.modelList}</div>
                     <#--getting the drop down of group id-->
                     <div class="straight col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <div>
                           <div>&nbsp;&nbsp;</div>
                           <#assign etlGroups=delegator.findAll("EtlGrouping",false)?if_exists/>
                           <#if etlGroups?has_content>	
                           <select class="form-control btn-xs" id="group_deta">
                              <option value="">${uiLabelMap.selectChannels}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
                              <#list etlGroups as group>
                              <option value="${group.groupId?if_exists}" <#if group.groupId?if_exists==groupByFilter?if_exists>selected</#if>>${group.groupName?if_exists} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>	
                              </#list>
                           </select>
                           </#if>
                        </div>
                        <div>&nbsp;</div>
                        <#--end of drop down group id-->
                        <#--getting all available list in the etlmapping table-->
                        <div class="pull-right" style="float:right">
                           <form action="" class="search-form pull-right" style="float:right">
                              <div class="boxS">
                                 <div class="container-2S">
                                    <span class="icon"><i class="fa fa-search"></i></span>
                                    <input type="search" id="search" placeholder="${uiLabelMap.search}"  onkeyup="filter(this,'csvSortable')" />
                                 </div>
                              </div>
                              <#-- 
                              <div class="form-group has-feedback">
                                 <label for="search" class="sr-only">${uiLabelMap.search}</label>
                                 <input type="text" class="form-control" name="search" id="search" placeholder="${uiLabelMap.search}" onkeyup="filter(this,'csvSortable')">
                                 <span class="glyphicon glyphicon-search form-control-feedback"></span>
                              </div>
                              -->
                           </form>
                           <div>&nbsp;</div>
                        </div>
                     </div>
                     <!--end of straight-->
                     <#--end @vijayakumar-->
                     </span>
                  </div>
               </div>
               <div>
               </div>
               <#--<a class="white badge" data-toggle="modal" href="#draggable"> <span class="fa fa-plus"/> </a>-->
            </div>
            <div class="portlet-body" style=" min-height: 40px;">
               <#if !request.getParameter("model")?has_content>
               <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                  <form class="form-inline" role="form" >
                     <div class="form-group">
                        <label style='font-size:13px'>
                           <div class="radio"><input type="radio" name="optradio" value="All" class='modal_select' <#if !request.getParameter("filterBy")?has_content || ( request.getParameter("filterBy")?has_content && request.getParameter("filterBy")?if_exists=="All") > checked="checked"</#if>></div>
                           &nbsp;&nbsp;${uiLabelMap.all}
                        </label>
                     </div>
                     <div class="form-group">
                        <label style='font-size:13px'>
                           <div class="radio"><input type="radio" name="optradio" value="Assigned" class='modal_select' <#if request.getParameter("filterBy")?has_content && request.getParameter("filterBy")?if_exists=="Assigned" > checked="checked"</#if>></div>
                           &nbsp;&nbsp;${uiLabelMap.assigned}
                        </label>
                     </div>
                     <div class="form-group">
                        <label style='font-size:13px'>
                           <div class="radio"><input type="radio" name="optradio" value="Unassigned" class='modal_select' <#if request.getParameter("filterBy")?has_content && request.getParameter("filterBy")?if_exists=="Unassigned" > checked="checked"</#if>></div>
                           &nbsp;&nbsp;${uiLabelMap.unAssigned}
                        </label>
                     </div>
                  </form>
               </div>
               </#if>
               <div class="scroller" style="max-height: 400px; overflow: auto;    overflow-x: auto; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1" id="csvSortable">
                  <div>
                     <ul class="list-group">
                        <#assign etlProcess1 = ""/>
                        <#assign checkProcess = ""/>
                        <#if requestParameters.model?has_content>
                        <#assign etl_process = delegator.findByAnd("EtlSourceTable", {"listName" : requestParameters.model?if_exists},null,false)/>
                        <#if etl_process?has_content>
                        <#assign etlProcess1 = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(etl_process) />
                        <#assign checkProcess = delegator.findByAnd("EtlProcess", {"modalName" : etlProcess1.listName?if_exists},null,false)/>
                        </#if>
                        <li class="list-group-item" <#if checkProcess?has_content>style="font-weight: bold"</#if>><a href="#" value="${requestParameters.model?if_exists}" id="${requestParameters.model?if_exists}" onld_attr="default_ld" onload="test(this)" <#--<#if etl_process?has_content> style="pointer-events:none;"</#if>--> table_attr="<#if etlProcess1?has_content>${etlProcess1.tableName?if_exists}</#if>">${requestParameters.model?if_exists}</a>
                        <a href="javascript:;" class="btn btn-xs default" style="float:right;padding-right: 1px;padding-left: 1px;"><#--<i class="fa fa-user"></i>--></a>
                        <a target="_blank" href="<@ofbizUrl>myHome?listId=${requestParameters.model?if_exists}&title=2</@ofbizUrl>" class="btn btn-xs blue" style="float:right;padding-right: 1px;padding-left: 1px;margin-right: 6px;"><i class="fa fa-edit"></i></a>
                        </li>
                        <#else>
                        <#if etlSet?has_content>
                        <#list etlSet as list_name>
                        <#assign etl_process = delegator.findByAnd("EtlProcess", {"modalName" : list_name.listName},null,false)/>
                        <#assign sequence_nums = ""/>
                        <#if groupByFilter?has_content>
                        <#assign etl_sequence_num = delegator.findByAnd("EtlModelGrouping", {"groupId" : groupByFilter,"modelName",list_name.listName?if_exists},null,false)?if_exists/>
                        <#if etl_sequence_num?has_content>
                        <#assign sequence_num = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(etl_sequence_num) />
                        <#if sequence_num?has_content>
                        <#assign sequence_nums = "${sequence_num.sequenceNo?if_exists}"/>
                        </#if>
                        </#if>
                        </#if>
                        <li class="list-group-item" <#if etl_process?has_content>style="font-weight: bold"</#if>><a href="#" value="${list_name.listName?if_exists}" id="${list_name.listName?if_exists}"  onclick="getEtlResource(this)"  sequ_num="${sequence_nums?if_exists}"  group_id='${groupByFilter?if_exists}'<#--<#if etl_process?has_content> style="pointer-events:none;"</#if>--> table_attr="${list_name.tableName?if_exists}">${list_name.listName?if_exists}</a>
                        <span class="pull-right" style="float:right">
                        <a href="javascript:;" class="btn btn-xs default" style="float:right;padding-right: 0px;padding-left: 0px;"><#--<i class="fa fa-user"></i>--></a>
                        <a target="_blank" href="<@ofbizUrl>myHome?listId=${list_name.listName?if_exists}&title=2</@ofbizUrl>" class="btn btn-xs blue" style="float:right;padding-right: 1px;padding-left: 1px;margin-right: 0px;margin-left: 0px!important;"><i class="fa fa-edit"></i></a>
                        <#if sequence_nums?has_content><span class="make-circle">${sequence_nums?if_exists}</span>&nbsp;</#if>
                        </span>
                        </li>
                        </#list>
                        </#if>            
                        </#if>
                     </ul>
                  </div>
                  <#--for display all the data from EtlMappingElements table-->
                  <#--end @EtlMappingElements-->
               </div>
            </div>
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
      <#--end of portlet 2-->
      <#--portlet 3-->
      <#if requestParameters.id?has_content || !requestParameters.id?has_content>
      <style>
         #file_upload_view{
         display:block;
         }
      </style>
      </#if>
      <div <#if requestParameters.model?has_content>class="row"<#else>class="col-lg-8 col-md-8 col-sm-8 col-xs-12"</#if> >
      <div id="file_upload_view" style="display:none">
         <#--starting of upload form process-->
         <div class="portlet light" style="  border: 1px solid #B6BFC1 !important;">
            <span class='pull-right'> <input type='button' class='btn btn-xs btn-primary' onclick="pageRefres();" value="${uiLabelMap.refresh}" style="margin: 3px 2px !important;"  <#if etlModelGroupingList?has_content>style="display:none!important;"</#if>></span>
            <div class="portlet-title tin-tin">
               <#if etlModelGroupingList?has_content || uploadOnce?if_exists=="Y"> 
               <div class="caption">
                  <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
                  <i class="icon-social-dribbble " id="upload_fixe"></i>
                  <span class="caption-subject" id="upload_fixe">${uiLabelMap.uploadFile}</span>
               </div>
               </#if>
               <div class="caption" id="nav_caption" <#if etlModelGroupingList?has_content ||  uploadOnce?if_exists=="Y"> style="display:none!important;"</#if>>
               <ul class="nav nav-tabs">
                  <li><a data-toggle="tab" href="#customer_tab" id="DataImportCustomer">${uiLabelMap.customer}</a></li>
                  <li><a data-toggle="tab" href="#account_tab" id="DataImportAccount">${uiLabelMap.account}</a></li>
                  <li><a data-toggle="tab" href="#lead_tab" id="DataImportLead">${uiLabelMap.Lead}</a></li>
                  <li><a data-toggle="tab" href="#contact_tab" id="DataImportContact">${uiLabelMap.Contact}</a></li>
                  <li><a data-toggle="tab" href="#prod_suppl_tab" id="DataImportProductSupplementary">${uiLabelMap.ProductSupplementary}</a></li>
                  <li><a data-toggle="tab" href="#itm_tab" id="DataImportItm">${uiLabelMap.ITM}</a></li>
                   <li><a data-toggle="tab" href="#activity" id="DataImportActivity">${uiLabelMap.Activity}</a></li>
                 
                  <#-- 
                  <li><a data-toggle="tab" href="#supplier_tab" id="DataImportSupplier">${uiLabelMap.supplier}</a></li>
                  <li><a data-toggle="tab" href="#category_tab" id="DataImportCategory">${uiLabelMap.category}</a></li>
                  <li><a data-toggle="tab" href="#product_Ext" id="DataImportProduct">${uiLabelMap.product}</a></li>
                  <li><a data-toggle="tab" href="#orderImport" id="EtlImportOrderFields">${uiLabelMap.multiChannelOrderImport}</a></li>
                  <li><a data-toggle="tab" href="#invoice_tab" id="DataImportInvoiceHeader">${uiLabelMap.invoice}</a></li>
                  <li><a data-toggle="tab" href="#invoiceItem_tab	" id="DataImportInvoiceItem">${uiLabelMap.invoiceItem}</a></li>
                  <li><a data-toggle="tab" href="#lockboxBatch_tab" id="FioLockboxBatchStaging">${uiLabelMap.lockboxBatch}</a></li>
                  <li><a data-toggle="tab" href="#lockboxBatchItem_tab" id="FioLockboxBatchItemStaging">${uiLabelMap.lockboxBatchItem}</a></li>
                  <li><a data-toggle="tab" href="#wallet_tab" id="DataImportWallet">${uiLabelMap.wallet}</a></li>
                  -->
                  <#--
                  <li><a data-toggle="tab" href="#orderFulfillmentImport" id="EtlOrderFulfillment">${uiLabelMap.orderFulfillmentImport}</a></li>
                  <li><a data-toggle="tab" href="#supplier_tab" id="supplier">Supplier</a></li>
                  <li class="active"><a data-toggle="tab" href="#lead_tab" id="DmgPartyLead">${uiLabelMap.lead}</a></li>
                  <li><a data-toggle="tab" href="#opportunity_tab" id="DmgOpportunity">${uiLabelMap.opportunity}</a></li>
                  <li><a data-toggle="tab" href="#supplierProduct_tab" id="DmgSupplierProduct">${uiLabelMap.supplierProduct}</a></li>
                  <li><a data-toggle="tab" href="#productAssoc_tab	" id="DmgKitProductAssociate">${uiLabelMap.productAssociation}</a></li>
                  <li><a data-toggle="tab" href="#inventory_tab" id="DmgInventoryLoad">${uiLabelMap.inventory}</a></li>
                  <li><a data-toggle="tab" href="#phy_inventory_tab" id="DmgPhysicalInventory">${uiLabelMap.physicalInventory}</a></li>
                  <li><a data-toggle="tab" href="#orderImport" id="EtlImportOrderFields">${uiLabelMap.multiChannelOrderImport}</a></li>
                  <li><a data-toggle="tab" href="#orderFulfillmentImport" id="EtlOrderFulfillment">${uiLabelMap.orderFulfillmentImport}</a></li>
                  -->
               </ul>
               <#--for lead tab-->
               <div class="tab-content">
                  <div id="lead_tab" class="tab-pane fade in active">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${leadsProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${leadsNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of lead tab-->
                  <#--for customer tab-->
                  <div id="customer_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${customersProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${customersNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of customer tab-->
                  <#--for contact tab-->
                  <div id="contact_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${contactsProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${contactsNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of contact tab-->
                  <#--for category tab-->
                  <div id="category_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${categoriesProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${categoriesNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of  category tab-->
                  <#--for account tab-->
                  <div id="account_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${accountsProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${accountsNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of  account tab-->
                  <#--for opportunity tab-->
                  <div id="opportunity_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${opportunitysProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${opportunitysNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of opportunity tab-->
                  <#--for supplier tab-->
                  <div id="supplier_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${suppliersProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${suppliersNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of Supplier tab-->
                  <#--for invoice tab-->
                  <div id="invoice_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${InvoiceHeaderProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${InvoiceHeaderNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of invoice tab-->
                  <#--for invoiceItem tab-->
                  <div id="invoiceItem_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${InvoiceItemProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${InvoiceItemNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of invoiceItem tab-->
                  <#--for Supplier Product tab-->
                  <div id="supplierProduct_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${InvoiceItemProcessed?if_exists}12</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${InvoiceItemNotProcessed?if_exists}12</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of Supplier Product tab-->
                  <#--for product Association tab-->
                  <div id="productAssoc_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${kitProductAssociatesProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${kitProductAssociatesNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of Product Association tab-->
                  <#--for Inventory tab-->
                  <div id="inventory_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${invProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${invNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of Inventory tab-->
                  <#--for Product Ext tab-->
                  <div id="product_Ext" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${productsProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${productNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of Product Ext tab-->
                  <#--for Physical Inventory tab-->
                  <div id="phy_inventory_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">${phyInvProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">${phyInvNotProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <a href="<@ofbizUrl>etlProcessPhysicalInventory?model=${requestParameters.model?if_exists}</@ofbizUrl>" class="btn btn-sm btn-primary" >Process</a>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of Inventory tab-->
                  <#--for Order tab-->
                  <div id="orderImport" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">Order Header Lines</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">${orderHeadersProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">${orderHeadersNotProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <input type="button" class="btn btn-sm btn-primary" value="Import Orders" onclick="submitImportOrder();"/>
                              </div>
                           </div>
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">Order Item Lines</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">${orderItemsProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">${orderItemsNotProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                              </div>
                           </div>
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">Customer Lines</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">${customersProcessedCount?if_exists}</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">${customersNotProcessedCount?if_exists}</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of order tab-->
                  <#--for Order fulfillment tab-->
                  <div id="orderFulfillmentImport" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">Order Header Lines</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">${orderCompleteProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <label class="">${orderNotCompleteProcessed?if_exists}</label>
                              </div>
                              <#--
                              <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                 <input type="button" class="btn btn-sm btn-primary" value="Import Orders" onclick="submitImportOrder();"/>
                              </div>
                              -->    
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of order fulfillment tab-->
                  <#--for lockboxBatch tab-->
                  <div id="lockboxBatch_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${lockboxBatchProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${lockboxBatchNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of lockboxBatch tab-->
                  <#--for lockboxBatchItem tab-->
                  <div id="lockboxBatchItem_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${lockboxBatchItemProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${lockboxBatchItemNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of lockboxBatchItem tab-->
                  <#--for wallet tab-->
                  <div id="wallet_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${walletProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${walletNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of wallet tab-->
                  <#--for product supplementary tab-->
                  <div id="prod_suppl_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${productSupplementaryProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${productSupplementaryNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of product supplementary tab-->
                  <#--for ITM tab-->
                  <div id="prod_suppl_tab" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${itmProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${itmNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of ITM tab-->
                  <#--for activiy tab-->
                  <div id="activity" class="tab-pane fade">
                     <form class="form-horizontal" role="form">
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">${uiLabelMap.importing}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.processed}</div>
                                 </h4>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <h4>
                                    <div class="label label-default" style="background-color: white;color: grey;">#${uiLabelMap.notProcessed}</div>
                                 </h4>
                              </div>
                           </div>
                        </div>
                        <div class="form-group">
                           <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${uiLabelMap.lines}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${activityProcessed?if_exists}</label>
                              </div>
                              <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                 <label class="">${activityNotProcessed?if_exists}</label>
                              </div>
                           </div>
                        </div>
                     </form>
                  </div>
                  <#--end of activity tab-->
               </div>
               <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
               <i class="icon-social-dribbble" id="upload_fixe"></i>
               <span class="caption-subject   uppercase" id="upload_fixe">${uiLabelMap.uploadFile}</span>
            </div>
         </div>
         <div class="portlet-body">
            <div class="row" id="margin_adjuster">
               <div <#if etlModelGroupingList?has_content> class="col-lg-5 col-md-5 col-sm-12 col-xs-12"<#else> class="col-lg-10 col-md-10 col-sm-12 col-xs-12"</#if>>
               <#--portlet body -->
               <span style="font-size: 18px;display:none;" class="action_upload_title_view"><i class="icon-social-dribbble"></i>&nbsp;&nbsp;<span class="caption-subject">${uiLabelMap.uploadFile}</span><br><br></span>
               <form class="form-horizontal" id="uploadEtl" role="form" method="post" action="uploadFile?modelName=${requestParameters.model?if_exists}&id=1" enctype="multipart/form-data">
                  <input type="hidden" name="processId" id="processId" value="${requestParameters.processId?if_exists}"/>
                  <input type="hidden" name="tab_id" id="tabs_ids" value="lead" />
                  <input type="hidden" name="groupId" id="groupId" value="${requestParameters.groupId?if_exists}" />
                  <div class="form-group">
                     <label class="control-label col-sm-3" for="email">${uiLabelMap.fileToImport}</label>
                     <div class="col-sm-4">
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
                              <input type="hidden"><input type="file" name="csv_fileName" id="csv_fileName_lst"> </span>
                              <a href="javascript:;" class="input-group-addon btn red fileinput-exists" data-dismiss="fileinput">  ${uiLabelMap.removeButton} </a>
                           </div>
                           <!--end of input-group input-group-sm-->
                           <span class="error" id="file_name_error_csv"></span> 
                        </div>
                        <!--end of fileinput process-->
                     </div>
                  </div>
                  <div class="form-group">
                     <div class="col-sm-offset-3 col-sm-6">          
                        <label style="color:red">		
                        ${uiLabelMap.pickRightCsv}
                        </label>
                     </div>
                  </div>
                  <#-- 
                  <div class="form-group">
                     <label class="control-label col-sm-3" for="email">${uiLabelMap.fileFormat}</label>
                     <div class="col-sm-4">
                        <select class="form-control btn-xs" data-size="8" id="getSelected">
                           <option value="csv">${uiLabelMap.csv}</option>
                        </select>
                     </div>
                  </div>
                  -->
                  <#if requestParameters.groupId?has_content>
                  <#assign etlUploadRequest = delegator.findByAnd("EtlModelGrouping", {"modelName" : requestParameters.model?if_exists, "groupId" : requestParameters.groupId?if_exists,"sequenceNo":"01"},null,false)>
                  <#if etlUploadRequest?has_content>
                  <div class="form-group">
                     <label class="control-label col-sm-3" for="downloader" style="    padding-top: 20px;">CSV Format </label>
                     <div class="col-sm-6 bottom_wider" style="display: inline-flex;">
                        <#assign etlTemplateDownload = delegator.findByAnd("EtlTemplateDownload", { "groupId" : requestParameters.groupId?if_exists},null,false)?if_exists>
                        <#if etlTemplateDownload?has_content>
                        <select name="downloader" id="downloader"  onclick="commonDownload(this);" class="form-control btn-xs">
                           <option></option>
                           <#list etlTemplateDownload as template>
                           <option value="${template.seqId?if_exists}">${template.downloadInterface?default("Download")}</option>
                           <#--<label> 
                           <a href ="#" data_attr="${template.seqId?if_exists}" onclick="commonDownload(this);">${template.downloadInterface?default("Download")}</a>
                           </label>-->	
                           </#list>
                        </select>
                        </#if>
                     </div>
                  </div>
                  </#if>
                  </#if>
                  <div class="form-group" style="display:none" id="check_container1">
                     <label class="control-label col-sm-3" for="email">CSV Format </label>
                     <div class="col-sm-4 bottom_wider"  style="display:inline-flex;">
                        <select name="download_viewer" id="download_viewer"  onclick="commonDownload(this);" class="form-control">
                        </select>
                     </div>
                  </div>
                  <#if etlModelGroupingList?has_content || uploadOnce?if_exists=="Y">
                  <div class="form-group" >
                     <div class="col-sm-offset-3 col-sm-6">          
                        <label style="">												       
                        <input type="checkbox" class="checkbox" onclick="actionResolver(this);">&nbsp;Auto Import
                        </label>
                     </div>
                  </div>
                  </#if>
                  <div class="form-group" style="display:none" id="check_container">
                     <div class="col-sm-offset-3 col-sm-6">          
                        <label style="">												       
                        <input type="checkbox" class="checkbox" onclick="actionResolver(this);">&nbsp;Auto Import
                        </label>
                     </div>
                  </div>
                  <div class="form-group">
                     <div class="col-sm-offset-3 col-sm-8">          
                        <input type="button" class="btn btn-primary btn-sm" value="${uiLabelMap.upload}" onclick="whenPageLoadStarts();" onload="whenPageOnload();"/>
                     </div>
                  </div>
                  <input type="hidden" name="tab_id" id="tab_id">
                  <input type="hidden" name="execute_table_name" id="execute_table_name" value=""> 
                  <input type="hidden" name="model" id="model" value="${requestParameters.model?if_exists}">
               </form>
               <!--end of form-->
               <#--end of portlet body-->
            </div>
            <#--end of first col-6 -->
            <#if etlModelGroupingList?has_content>
            <div class="col-lg-7 col-md-7 col-sm-12 col-xs-12">
               <table class="table">
                  <th class="info">Channel</th>
                  <th class="info">Model</th>
                  <th class="info">Processed</th>
                  <th class="info">Not Processed</th>
                  <th class="info">Status</th>
                  <th class="info">Processed Date</th>
                  <#list etlModelGroupingList as etlModel>
                  <tr >
                     <td>
                        <#if groupName?has_content>
                        <#if groupName.groupName?has_content>${groupName.groupName?if_exists}</#if>
                        </#if>
                     </td>
                     <td>				                	
                        <#assign etlUploadRequest= delegator.findOne("EtlModelGrouping",Static["org.ofbiz.base.util.UtilMisc"].toMap("modelId","${etlModel.etlModelId?if_exists}", "groupId","${etlModel.groupId?if_exists}" ),false)?if_exists>
                        ${etlUploadRequest.modelName?if_exists}
                     </td>
                     <td>
                        <span style="float:right;">
                        <#if etlModel.importType?if_exists=="CATEGORY">
                        ${categorysProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="PRODUCT">
                        ${productsProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="SUPPLIER">
                        ${suppliersProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="SUPPLIERPRODUCT">
                        ${supplierProductsProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="INVENTORY">
                        ${invProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="PRODUCTASSOC">
                        ${kitProductAssociatesProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="PRODUCTEXT">
                        ${proExtProcessed?if_exists}
                        </#if>
                        </span>
                     </td>
                     <td>
                        <span style="float:right;">
                        <#if etlModel.importType?if_exists=="CATEGORY">
                        ${categorysNotProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="PRODUCT">
                        ${productsNotProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="SUPPLIER">
                        ${suppliersNotProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="SUPPLIERPRODUCT">
                        ${supplierProductsNotProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="INVENTORY">
                        ${invNotProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="PRODUCTASSOC">
                        ${kitProductAssociatesNotProcessed?if_exists}
                        </#if>
                        <#if etlModel.importType?if_exists=="PRODUCTEXT">
                        ${proExtNotProcessed?if_exists}
                        </#if>
                        </span>
                     <td>
                        <#if etlModel.status?if_exists=="FINISHED">
                        <i class="fa fa-check"/>
                        </#if>
                        <#if etlModel.status?if_exists="ERROR">
                        <i style="color:red;" class="fa fa-remove"/>
                        </#if>
                        <#if etlModel.status?if_exists="INIT">
                        <i style="color:orange;" class="fa fa-coffee"/>
                        </#if>
                     </td>
                     <td>${etlModel.fromDate?if_exists}</td>
                  </tr>
                  </#list>          
               </table>
            </div>
            <!--end of col-lg 4-->
            </#if>
         </div>
         <!--end of row-->
      </div>
   </div>
   <#--end of starting upload form process-->
   </div>
   <div id="applyToProcess_view">
      <div class="portlet light bordered" style="    min-height: 550px !important;border: 0px solid #B6BFC1 !important">
         <div class="portlet-title tin-tin">
            <div class="band">
               <span class="portlet-title tin-tin " style="/*color:#337AB7*/"><span class="icon-equalizer font-blue-sunglo"></span>&nbsp;${uiLabelMap.applyToProcess}</span>
               <#if export?has_content && export.isExport?if_exists!="Y">
               <div class="pull-right">
                  <div class="btn-group">
                     <button type="button" class="btn btn-xs btn-info" data-toggle="modal" href="#eModel" style="text-transform: capitalize;">${uiLabelMap.configureExportModel}</button>
                  </div>
               </div>
               </#if>
            </div>
            <div>&nbsp;&nbsp;&nbsp;</div>
            <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
               <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                  <#--to display the available process list when all the lead is displayint-->
                  <#if requestParameters.model?has_content>
                  <div class="form-group">
                     <table class="table borderless">
                        <tr>
                           <td class="col-lg-3" style="width: 115px;border-top :0px solid #ddd;    border-bottom: 0px !important;">
                              <b>${uiLabelMap.modelName}</b> 
                           </td>
                           <td style="border-top :0px solid #ddd;    border-bottom: 0px !important;">: ${requestParameters.model?if_exists}&nbsp;<a target="_blank" href="<@ofbizUrl>myHome?listId=${requestParameters.model?if_exists}&title=2</@ofbizUrl>" class="btn btn-xs blue" style="float:right;padding-right: 1px;padding-left: 1px;margin-right: 6px;"><i class="fa fa-edit"></i></a></td>
                        </tr>
                        <tr>
                           <#assign etlProcessGroup=delegator.findByAnd("EtlProcess", Static["org.ofbiz.base.util.UtilMisc"].toMap("modalName",requestParameters.model?if_exists),null,false)>
                           <#if etlProcessGroup?has_content>
                           <#assign etlProcessSingle = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(etlProcessGroup) />
                           <td class="col-lg-3" style="width: 115px;border-top :0px solid #ddd;    border-bottom: 0px !important;">
                              <b>${uiLabelMap.processName}</b> 
                           </td>
                           <td style="border-top :0px solid #ddd;    border-bottom: 0px !important;	">: ${etlProcessSingle.processName?if_exists}&nbsp;<a data-toggle="modal" href="#demo_modal_process" class="btn btn-xs blue" style="float:right;padding-right: 1px;padding-left: 1px;margin-right: 6px;"><i class="fa fa-edit"></i></a></td>
                        </tr>
                     </table>
                     <#else>
                     <table>
                        <tr>
                           <td  style="    padding: 0px 0px 0px 6px !important;width: 100%;border-top :0px solid #ddd;    border-bottom: 0px !important;"><b style="color:#ff0000">${uiLabelMap.processAssociationIsNotFound}</b></td>
                        </tr>
                     </table>
                     </#if>  
                  </div>
                  <!--end of form group-->
                  <#else>
                  <form class="form-inline" role="form">
                     <div class="form-group">
                        <#if etlProcess?has_content>
                        <select  class="bs-select form-control btn-sm" data-live-search="true" data-size="8" id="process_det">
                           <option value="" disabled selected>${uiLabelMap.selectProcess}</option>
                           <#list etlProcess as etl_pro>
                           <option value="${etl_pro.processId?if_exists}" id="${etl_pro.processId?if_exists}" <#if etl_pro.modalName?has_content> disabled </#if>>${etl_pro.processName?if_exists}<#--<#if etl_pro.modalName?has_content> <span class="applied">Applied</span></#if>--></option>
                           </#list>													
                        </select>
                        </#if>						                      						
                     </div>
                     <div class="form-group">
                        <a href="#" class="btn btn-sm btn-primary pull-right btn-xs align_bottom" style="float:right" onclick="associateModalToProcess()">${uiLabelMap.apply}</a>					                      						
                     </div>
                     <div class="form-group">
                        <a href="#" class="btn btn-sm btn-danger align_bottom pull-right btn-xs" style="float:right" value="" id="removeModalAssoc" onclick="removeAssocModelToProcess(this)">Remove</a>
                     </div>
                  </form>
                  </#if>
                  <#--end of display the available process list-->				                      						
               </div>
               <!--end of col-md-6 etc-->
               <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 pull-right">
                  <div class="hidden-sm hidden-md hidden-lg">&nbsp;</div>
                  <form action="" class="search-form">
                     <div class="box">
                        <div class="container-2">
                           <span class="icon"><i class="fa fa-search"></i></span>
                           <input type="search" id="search" placeholder="${uiLabelMap.search}" onkeyup="filter(this,'mappedElements')" />
                        </div>
                        <#--
                        <div class="form-group has-feedback">
                           <label for="search" class="sr-only">${uiLabelMap.search}</label>
                           <input type="text" class="form-control" name="search" id="search" placeholder="${uiLabelMap.search}" onkeyup="filter(this,'mappedElements')">
                           <span class="glyphicon glyphicon-search form-control-feedback"></span>-->
                        </div>
                  </form>
                  </div>
                  <!--end of 6 process-->
               </div>
               <!--end of col-lg-12 col-md-12 col-sm-12 col-xs-12-->
               <!--end of col-12-->
            </div>
            <!--end of portlet title-->
            <div class="portlet-body">
               <div class="scroller" style=" overflow: auto;    overflow-x: auto; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1">
                  <#--inner portlet-->
                  <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                     <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                        <div class="portlet light" style="">
                           <div class="portlet-title tin-tin band-b">
                              <span class="portlet-title tin-tin " style="/*color:#337AB7*/">${uiLabelMap.description}</span>
                           </div>
                           <div class="portlet-body">
                              <div class="scroller" style="max-height: 400px; overflow: auto;word-wrap: break-word;overflow-x: auto; width: auto;text-align:justify" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1" id="process_description">
                              </div>
                           </div>
                        </div>
                     </div>
                     <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                        <div class="portlet light" style="min-height: 300px;">
                           <div class="portlet-title tin-tin  band-b">
                              <span class="portlet-title tin-tin " style="/*color:#337AB7*/">${uiLabelMap.mappedElements}</span>
                           </div>
                           <!--end of portlet title-->
                           <div class="portlet-body">
                              <div class="">
                                 <div class="scroller" style="max-height: 245px; overflow: auto;    overflow-x: auto; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1">
                                    <ul class="list-group" id="mappedElements" >
                                    </ul>
                                 </div>
                                 <!--end of scroller-->
                              </div>
                              <!--end of unknown class-->
                           </div>
                           <!--end of portlet body-->
                        </div>
                        <div class="portlet-footer pull-right" style="float:right">
                           <form class="form-inline" role="form">
                              <div class="form-group">
                                 <#assign tableName = "">
                                 <#if etlProcess1?has_content>
                                 <#assign tableName = etlProcess1.tableName?if_exists />
                                 </#if>
                                 <a class="btn btn-sm btn-primary  btn-xs" style="float:right" value="" table_name="${tableName?if_exists}" id="executeModalData" data-modelName="${parameters.model?if_exists}" onclick="executeModal(this)">${uiLabelMap.execute}</a>
                              </div>
                           </form>
                        </div>
                     </div>
                     <!--end of portlet-->
                  </div>
               </div>
               <!--end of col-md-6 etc-->
               <#--end of inner portlet-->
            </div>
            <!--end of portlet body-->
         </div>
         <!--end of portlet-->
      </div>
      <!--end of apply to process_view tab-->
   </div>
   <!--end of div col-8-->
   <#--end of portlet 2-->
   <#--end of portlet 2-->
   <div class="page-footer">
      <div class="scroll-to-top" style="display: block;">
         <i class="icon-arrow-up"></i>
      </div>
   </div>
   <!--end of row-->
</body>
<div class="row-fluid">
   <h3 class="" style="color:#87AFC7;text-shadow: 2px 2px #FF0000;">&nbsp;&nbsp;</h3>
</div>
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
<script>
   function submitImportOrder(){
   	document.multiChannelOrderImport.submit();
   }
</script>
<#--for destination tab id -->
<input type="hidden" id="dest_tab_id"/>
<#--end @destination tab id-->
<#--for the purpose of filtering the etlset -->
<form action="applyEtlModel" name="applyEtlModel" id="applyEtlModel" method="POST">
   <input type="hidden" name="filterBy" id="filterBy" value="all">
   <input type="hidden" name="groupByFilter" id="groupByFilter" value="${groupByFilter?if_exists}">
</form>
<#--end @filter process-->
<#--common download option-->
<form name="downloadCommonExport" id="downloadCommonExport" action="<@ofbizUrl>commonDownload</@ofbizUrl>">
   <input type="hidden" name="model" id="modelExport" value="${requestParameters.model?if_exists}">
   <input type="hidden" name="groupIdExport" id="groupIdExport" value="${parameters.groupId?if_exists}"/>
   <input type="hidden" name="downloadSequence" id="downloadSequence" />
</form>
<#--end of common download-->
<form name="multiChannelOrderImport" id="multiChannelOrderImport" action="<@ofbizUrl>scheduleServiceMULTI_CHANNELFTP</@ofbizUrl>">
   <input type="hidden" name="SERVICE_TIME" value="">
   <input type="hidden" name="importedFrom" value="MULTI_CHANNEL">
   <input type="hidden" name="accessType" value="FTP">
   <input type="hidden" name="POOL_NAME" value="pool">
   <input type="hidden" name="SERVICE_NAME" value="om.MultiChannelImportOrders">
   <input type="hidden" name="sel_service_name" value="om.MultiChannelImportOrders">
   <input type="hidden" name="sectionHeaderUiLabel" value="Import Orders">
   <input type="hidden" name="model" value="${requestParameters.model?if_exists}">
   <input id="reserveInventory" name="reserveInventory" type="hidden" value="True">
   <input id="importedFrom" name="importedFrom" type="hidden" value="MULTI_CHANNEL">
   <input id="importEmptyOrders" name="importEmptyOrders" type="hidden" value="False">
   <input id="importCustomerAsAccount" name="importCustomerAsAccount" type="hidden" value="False">
   <input id="companyPartyId" name="companyPartyId" type="hidden" value="Company">
   <input id="calculateGrandTotal" name="calculateGrandTotal" type="hidden" value="True">
</form>
<!--Popup process-->
<div class="modal fade" id="demo_modal_process">
<div class="modal-dialog modal-lg">
<div class="modal-content c-square">
<div class="modal-header">
   <button type="button" class="close" data-dismiss="modal" aria-label="Close">
   <span aria-hidden="true">×</span>
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
                     <input type="hidden" class="form-control" name="modelId" id="modelId" value="${requestParameters.model?if_exists}">
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
                           <#assign EtlProcessService =delegator.findAll("EtlProcessService",false)?if_exists />
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
<script>
   function assignModel(impModelId,expModelId,index1) {
   if(impModelId !="" && expModelId != "") {
   	$.post("assignExpModel",{"impModelId":impModelId,"expModelId":expModelId},function(data) {
   		if(data != null && data != "") {
   			for(var i=0;i<data.length;i++) {
   			if(data[i].result=="success"){
   					$("#span_"+index1).html("Assigned");
   				}			
   			}
   		}
   	});
   }
   function assignModel1(){
   	alert("1");
   }
</script>
