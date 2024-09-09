<link href="/metronic/css/components.min.css" id="style_components" rel="stylesheet" type="text/css"/>
<style>
#processId_error,#processName_error
{
	color:red;
	font-size: 11px;
	font-weight: bold;
}
.icon-btn > div {
    font-size: 9px !important;
}
<style>
.page-header md-shadow-z-1-i navbar navbar-fixed-top{height:35px !important;}
</style>
</style>


<#--style added by m.vijayakumar for must styles-->
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
    
    .wrapper {

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
.page-header.navbar .top-menu .navbar-nav>li.dropdown-user .dropdown-toggle {
padding: 8px 10px 18px 10px !important;
}
 .portlet>.portlet-title>.actions>.btn-group>.btn.btn-sm, .portlet>.portlet-title>.actions>.btn.btn-sm {
padding: 8px 10px 0px 0px!important;
font-size: 13px;
line-height: 1.5;
}		

button.btn.dropdown-toggle.selectpicker.btn-default
{
	padding : 5px 25px 0px 0px !important;
}	

.bootstrap-select.btn-group .btn .caret
{
	top:70% !important;
}
	
	button.btn.btn-xs.green-turquoise {
		margin-bottom: 3px;
		}						
   </style>
   
   
<script type="text/javascript" src="/crmsfa_js/jquery.validate.js"></script>
<script>
$(document).ready(function(){
		
		
		
			
		
		//always error message need to be hidden
		$(".help-block").html();
		
		
		$.validator.addMethod("alpha_dash", function(value, element) {
       			 return this.optional(element) || /^[a-z0-9_]+$/i.test(value); 
          }, "Alphanumerics, spaces, underscores & dashes only.");
          
          
	$("#processForm").validate({
		
		rules: {
			processId: {"alpha_dash": true, "required": true},
			processName: "required",
			
		},
		messages: {
			processId: "Process Id required without any space",
			processName: "Please enter Process Name",
			
		},errorPlacement: function(error, element) {
			
				if(element.attr("name")=="processId")
				{
					$("#processId_error").text(error.html());
				}
				if(element.attr("name")=="processName")
				{
					$("#processName_error").text(error.html());
				}
			}
	});	
	
	
 //for form validation of processform11 due to multiple form manual validation is taken added at 17/05/2016
 
	 
	 $("div[data-toggle='modal']").click(function(){
	 	$(".error_too").html("");
});
	 
	 
	$(".restict_form").click(function(){
		var current_count = $(this).attr("counts");
		var eProcessName = $("#eProcessName_"+current_count).val();
    	
    	if(eProcessName!="")
    	{
    			$("#processForm1_"+current_count).submit();
    	}else
    	{
    		$("#processName_error_"+current_count).html("<b style='color:red'>Please provide Process Name</b>");
    		$(".error_too").css("opacity","2");
    	}
	});
	
		$(".onkey_press").keyup(function(){
			if($(this).val()!="")
			{
				$(".error_too").html("");
				
			}else
			{
				$(".error_too").html("<b style='color:red'>Please provide Process Name</b>");
				$(".error_too").css("opacity","2");
			}
		});
	
});
//end of form validation







  //for filter search box process added from headerMain.ftl @ 26/09/2016
      
      function filter(element,id) {
      
      	   var value = $(element).val();
      
      	   $("#"+id+" div").each(function() {
      
      	     if ($(this).text().search(new RegExp(value, "i")) > -1) {
      
      			
      	       $(this).show();
      
      	     } else {
      
      	       $(this).hide();
      
      	     }
      
      	   });
      
      	 }
      	 
      	 function  getModelBasedList(element)
      	 {
      	 	var selectedModel = $(element).val();
      	 	if(selectedModel!="")
      	 	{
      	 		$("#etlProcessTableName").val(selectedModel);
      	 		$("#EltBasedList").submit();
      	 	}
      	 }
   	 function  getGroupList(element)
      	 {
      	 	var selectedModel = $(element).val();
      	 	if(selectedModel!="")
      	 	{
      	 		$("#groupId").val(selectedModel);
      	 		$("#EtlGroupList").submit();
      	 	}
      	 }
      	 
</script>
<head>
</head>

<!-- BEGIN HEADER -->

<body style="overflow: scroll;">





<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12" style="">

<div></div>




<#--portlet 1-->

<div class="row-fluid">

	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
	<div>

		<div>

			<div>
								</div>

								<div class="actions" style="width:100%">

									<#--<span class=" bold" style="color:#337AB7">ETL PROCESS CONFIGURATION</span>-->
	<div class="portlet-body-grey">								
	<div class="portlet light ">

		<div class="portlet-title band">
			<div class="caption">
				<i class="icon-settings "></i>
				<span class="caption-subject ">${uiLabelMap.processList}</span>
			</div>
		&nbsp;&nbsp;&nbsp;
			
			<div class="actions inline-display " style="display:inline-block !important" style="width:20%!important;">
				
			<div class="btn-group  btn_twoPx" style="">
				<button type="button" class="btn btn-xs btn-info" data-toggle="modal" href="#fileUpload" style="text-transform: capitalize;">${uiLabelMap.createNew}</button>&nbsp;&nbsp;
			</div>
			<div class="inline-display" style="display:inline-flex !important;" >
					<div class="btn-group">
						<#if etlPr_ocess?has_content>	
							<select class="bs-select form-control btn-xs"  data-live-search="true" data-size="8" onchange="getModelBasedList(this);">
							<option value="">${uiLabelMap.selectType}</option>
							
							<option value="DataImportAccount" <#if requestParameters.etlProcessTableName?has_content><#if requestParameters.etlProcessTableName=="DataImportAccount">Selected</#if></#if>>Account Model</option>
							<option value="DataImportLead" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportLead">Selected</#if></#if>>Lead Model</option>
							<option value="DataImportCustomer" <#if requestParameters.etlProcessTableName?has_content><#if requestParameters.etlProcessTableName=="DataImportCustomer">Selected</#if></#if>>Customer Model</option>
							
							<#-- 
							<option value="DataImportSupplier" <#if requestParameters.etlProcessTableName?has_content><#if requestParameters.etlProcessTableName=="DataImportSupplier">Selected</#if></#if>>Supplier Model</option>
							<option value="DataImportCategory" <#if requestParameters.etlProcessTableName?has_content><#if requestParameters.etlProcessTableName=="DataImportCategory">Selected</#if></#if>>Category Model</option>
							<option value="DataImportProduct" <#if requestParameters.etlProcessTableName?has_content><#if requestParameters.etlProcessTableName=="DataImportProduct">Selected</#if></#if>>Product Model</option>
							<option value="EtlImportOrderFields" <#if requestParameters.etlProcessTableName?has_content><#if requestParameters.etlProcessTableName=="EtlImportOrderFields">Selected</#if></#if>>Purchase order Model</option>
							<option value="DataImportInvoiceHeader" <#if requestParameters.etlProcessTableName?has_content><#if requestParameters.etlProcessTableName=="DataImportInvoiceHeader">Selected</#if></#if>>Invoice Model</option>
							<option value="DataImportInvoiceItem" <#if requestParameters.etlProcessTableName?has_content><#if requestParameters.etlProcessTableName=="DataImportInvoiceItem">Selected</#if></#if>>Invoice Item Model</option>
							<option value="FioLockboxBatchStaging" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="FioLockboxBatchStaging">Selected</#if></#if>>Lockbox Model</option>
							<option value="FioLockboxBatchItemStaging" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="FioLockboxBatchItemStaging">Selected</#if></#if>>Lockbox Item Model</option>
							<option value="DataImportWallet" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportWallet">Selected</#if></#if>>Wallet Model</option>
							 -->
							
							<#--<#list etlPr_ocess as listEtl>
								<option value="${listEtl.tableName?if_exists}" <#if etlProcessTableName?has_content && etlProcessTableName?if_exists==listEtl.tableName?if_exists>selected</#if>>${listEtl.tableTitle?if_exists} </option>	
							</#list>-->
							</select>
						</#if>
							
					</div>
					&nbsp;&nbsp;
					<div>
							<#assign etlGroups=delegator.findAll("EtlGrouping",false)?if_exists/>
						<#if etlGroups?has_content>	
							<select class="bs-select form-control btn-xs"  data-live-search="true" data-size="8" onchange="getGroupList(this);">
							<option value="">${uiLabelMap.selectChannels}</option>
							<#list etlGroups as group>
								<option value="${group.groupId?if_exists}"<#if group.groupId?if_exists==requestParameters.groupId?if_exists>selected</#if>>${group.groupName?if_exists}</option>	
							</#list>
							</select>
						</#if>
					</div>
				</div><!--inline-display-->
			</div>
		</div>
		<#--<div class="portlet-body form">-->
			<#--<form role="form" method="post" name="processForm" action="<@ofbizUrl>createEtlProcess</@ofbizUrl>">
				<div class="form-body">
					<div class="form-group">
						<label>Process ID</label>
						<div class="input-group">
							<input type="text" name="processId" class="form-control" placeholder="Process ID" value="${processId?if_exists}"> </div>
					</div>
					<div class="form-group">
						<label>Process Name</label>
						<div class="input-group">
							<input type="text" name="processName" class="form-control" placeholder="Process Name" value="${processName?if_exists}"> </div>
					</div>
					
					<div class="form-group">
						<label>Description</label>
						<div class="input-group">
							<textarea class="form-control" placeholer="Message">
                                      </textarea>
					</div>
				</div>
				<div class="form-actions">
					<button type="submit" class="btn blue">Create</button>
					<button type="button" class="btn default">Cancel</button>
				</div>
			</form>-->
			<#assign colorsSize = containerColors.size?if_exists/>
			<#assign index=0/>
			<#if processList?has_content>
			<#assign headerGrp = request.getParameter("groupId")?if_exists />
			<#if headerGrp?has_content>
			<#assign headerGroup = delegator.findOne("EtlGrouping",{"groupId", "${headerGrp?if_exists}"},false)?if_exists/>
					<fieldset class="scheduler-border">
		    		<legend class="scheduler-border">${headerGroup.groupName?if_exists}</legend>
		    		&nbsp;&nbsp;
		    </#if></div>
<div class="scroller" style=" max-height: 400px; overflow: auto;overflow-x: auto; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1" id="csvSortable">
			<#assign count=0 />
			<#list processList as process>
			
		
			<#assign count=count+1 />
	<div class="col-md-3 col-sm-3 col-xs-12">
	<div class="color-demo tooltips"  data-toggle="modal" data-target="#demo_modal_${process.processId?if_exists}_${process.tableName?if_exists}">
	<#--<#if process.sequenceNo?has_content><span class="badge badge-primary pull-right">${process.sequenceNo?if_exists}</span></#if>-->
		<div class="color-view bg-${process.tableName?if_exists} bg-font-${process.tableName?if_exists} bold uppercase" style="font-size:11px;"> ${process.processName?if_exists} </div>
		<#--<div class="color-info bg-white c-font-11 sbold" style="font-size:11px;"> ${process.processId?if_exists} </div>-->
	</div>

	<div class="modal fade" id="demo_modal_${process.processId?if_exists}_${process.tableName?if_exists}">
		<div class="modal-dialog modal-lg">
			<div class="modal-content c-square">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title ">${uiLabelMap.updateProcess}</h4>
				</div><!--end of modal header-->
				<div class="modal-body">
			<!--update Process -->
					
					<div class="row">
					<div class="col-md-12">
						<#--<div class="">
							<label>Process Id</label>
							<div class="input-group">
								<input type="text" name="uProcessId" id="uProcessId_${index}" class="form-control" value="${process.processId?if_exists}" placeholder="Process ID" value=""> </div>
						</div>
						<div class="">
							<label>Process Name</label>
							<div class="input-group">
								<input type="text" name="uProcessName" id="uProcessName_${index}" class="form-control" value="${process.processName?if_exists}" placeholder="Process Name" value=""> </div>
						</div>		
						<div class="">
									<label>Description</label>
									<div class="input-group">
										<textarea class="form-control" name="uDescription" id="uDescription_${index}" value="${process.description?if_exists}" placeholer="Message">${process.description?if_exists}
			                                      </textarea>
								</div>
							</div>-->
				<div class="portlet light">
						<div class="portlet-title">
							<div class="">
								<i class="icon-settings "></i>
								<span class="caption-subject bold uppercase">${uiLabelMap.updateProcess}</span>
							</div>
						</div>
						<div class="portlet-body form">
							<form role="" method="post" name="processForm1_${count?if_exists}" id="processForm1_${count?if_exists}" action="<@ofbizUrl>updateEtlProcess</@ofbizUrl>"  counts="${count?if_exists}">
								<div class="form-body">
										<input type="hidden" class="form-control" name="eProcessId" id="form_control_1" value="${process.processId?if_exists}" placeholder="Process Id">
									<label for="form_control_1"><span class="caption-subject bold uppercase">${uiLabelMap.processId} &nbsp;<span class="padder">:&nbsp;${process.processId?if_exists}</span></span></label>
									<br>
									<label for="form_control_1"><span class="caption-subject bold uppercase">${uiLabelMap.modelName}  &nbsp;<span class="padder">:&nbsp;${process.modalName?if_exists}</span></span></label>
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control onkey_press" name="eProcessName" id="eProcessName_${count?if_exists}" value="${process.processName?if_exists}" placeholder="${uiLabelMap.processName}">
										<label for="form_control_1"></label>
										<span class="help-block error_too" id="processName_error_${count?if_exists}"></span>
									</div>
									<div class="form-group form-md-line-input">
										<select class="form-control" name="updateEtlProcessService" id="updateEtlProcessService">
										<#assign EtlProcessService =delegator.findAll("EtlProcessService",false)?if_exists />
										<#if EtlProcessService?has_content>
											<option selected disabled>Select Service</option>
											<#list EtlProcessService as service>
												<option value="${service.serviceId?if_exists}" <#if process.serviceName?if_exists==service.serviceId?if_exists> selected</#if>>${service.serviceName?if_exists}</option>
											</#list>
										</#if>
											
										</select>
										<label for="form_control_1"></label>
										<span class="help-block" id="processName_error"></span>
									</div>
								<div class="form-group form-md-line-input">
			                        <textarea class="form-control" rows="3" name="eDescription" placeholder="${uiLabelMap.description}" value="">${process.description?if_exists}</textarea>
			                        <label for="form_control_1"></label>
			                    </div>
								</div>
								<div class="form-actions noborder pull-right">
									<button type="button" id="processForm11_${count?if_exists}" class="btn blue restict_form sub_bt" counts="${count?if_exists}">${uiLabelMap.update}</button>
									
								</div>
							</form>
						</div>
					</div>
						</div>
						<#--<div class="col-md-4">
						<div class="portlet light bordered" style="border: 1px solid #B6BFC1 !important">
		                    <div class="portlet-title">
		                      <span class="portlet-title bold" style="color:#337AB7">Description</span>
		                    </div>
		                    <div class="portlet-body">
		                      <div class="scroller" style="max-height: 400px; overflow: auto;    overflow-x: auto; width: auto;text-align:justify" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1">
		                        Etl-process testing description for TestEtl-process testing description for TestEtl-process testing description for TestEtl-process testing description for Test
		                        Etl-process testing description for TestEtl-process testing description for TestEtl-process testing description for TestEtl-process testing description for Test
		                        Etl-process testing description for TestEtl-process testing description for TestEtl-process testing description for TestEtl-process testing description for Test
		                      </div>
		                    </div>
		                  </div>
						</div>-->
						<#--<div class="col-md-4">
							<img class="" alt="Etl" src="/opentaps_images/etl.jpg" width="200" height="175">
						</div>-->
					</div>
					<#--<div class="modal-footer">
						<a href="#" id="updateProcess" onclick="updateForm(${index});" class="btn ${process.tableName?if_exists}">Update</a>
					</div>-->
	
				<!--End-->
				</div>
			</div>
		</div>
	</div>
</div>
<#assign index = index+1/>
</#list>
</div>
</#if>
			<#--</div>-->
			</div>
			</div>
			</div>
		</div>
		</div><!--end of col-md-4 etc-->
		<form name ="updateEtlProcess"  action="<@ofbizUrl>updateEtlProcess</@ofbizUrl>" id="updateEtlProcess" method="post">
      <input type="hidden" name="eProcessId" id="eProcessId" value="">
      <input type="hidden" name="eProcessName" id="eProcessName" value="">
      <input type="hidden" name="eDescription" id="eDescription" value="">
    </form>
	<div class="page-footer">

		<div class="scroll-to-top" style="display: block;">

			<i class="icon-arrow-up"></i>

		</div>
	</div>

   </div><!--end of row-->
	<div class="row-fluid">			    	

		<h3 class="" style="color:#87AFC7;text-shadow: 2px 2px #FF0000;">&nbsp;&nbsp;</h3>	    

	</div>
<div class="modal fade draggable-modal" id="fileUpload" tabindex="-1" role="basic" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
        <h4 class="modal-title">${uiLabelMap.createProcess}</h4>
      </div>
      <div class="modal-body">
      <div class="row">
      <div class="col-md-12">
		<#--<form role="" method="post" name="processForm" action="<@ofbizUrl>createEtlProcess</@ofbizUrl>">
				<div class="form-body">
					<div class="form-group">
						<label>Process ID</label>
						<div class="input-group">
							<input type="text" name="processId" class="form-control" placeholder="Process ID" value=""> </div>
					</div>
					<div class="form-group form-md-line-input">
						<input type="text" class="form-control" id="form_control_1"name="processId" placeholder="Process ID">
						<label for="form_control_1">Process ID</label>
						<span class="help-block">Some help goes here...</span>
					</div>
					<div class="form-group">
						<label>Process Name</label>
						<div class="input-group">
							<input type="text" name="processName" class="form-control" placeholder="Process Name" value=""> </div>
					</div>
					
					<div class="form-group">
						<label>Description</label>
						<div class="input-group">
							<textarea class="form-control"  name="description" placeholer="Message">
                            </textarea>
					</div>
				</div>
				<div class="form-actions">
					<button type="submit" class="btn blue">Create</button>
					
				</div>
			</form>-->
	<div class="portlet light">
			<div class="portlet-title">
				<div class="">
					<i class="icon-settings "></i>
					<span class="caption-subject bold uppercase">${uiLabelMap.createProcess}</span>					
				</div>
				<#--<span class="pull-right"><button type="submit" class="btn blue">Create</button></span>-->
			</div>
			<div class="portlet-body form">
				<form role="" method="post" name="processForm" action="<@ofbizUrl>createEtlProcess</@ofbizUrl>" id="processForm">
					<div class="form-body">
						<div class="form-group form-md-line-input">
							<input type="text" class="form-control" name="processId" id="processId" value="" placeholder="${uiLabelMap.processId}">
							<label for="form_control_1"></label>
							<span class="help-block" id="processId_error"></span>
						</div>
						<div class="form-group form-md-line-input">
							<input type="text" class="form-control" name="processName" id="processName" value="" placeholder="${uiLabelMap.processName}">
							<label for="form_control_1"></label>
							<span class="help-block" id="processName_error"></span>
						</div>
						
						<div class="form-group form-md-line-input">
							<select class="form-control" name="etlProcessService" id="etlProcessService">
							<#assign EtlProcessService =delegator.findAll("EtlProcessService",false)?if_exists />
							<#if EtlProcessService?has_content>
								<option selected disabled>Select Service</option>
								<#list EtlProcessService as service>
									<option value="${service.serviceId?if_exists}">${service.serviceName?if_exists}</option>
								</#list>
							</#if>
								
							</select>
							<label for="form_control_1"></label>
							<span class="help-block" id="processName_error"></span>
						</div>
						
						
					<div class="form-group form-md-line-input">
                        <textarea class="form-control" rows="3" name="description" placeholder="${uiLabelMap.description}" value=""></textarea>
                        <span class="help-block" id="description_error"></span>
                    </div>
					</div>
					<div class="form-actions noborder pull-right">
						<button type="submit" class="btn btn-sm btn-primary sub_bt btn-xs">${uiLabelMap.create}</button>
						
					</div>
				</form>
			</div>
		</div>
			</div>
			</div>
			<#--<div class="col-md-4">
			<img class="" alt="Etl" src="/opentaps_images/etl.jpg">
			</div>-->
		</div>
		</div>
        <!--end of fileinput process-->
      </div>
      <!--end of modal-body-->
    </div>
    <!-- /.modal-content -->
  </div>
  <!-- /.modal-dialog -->
</div>

</body>



<script type="text/javascript">
function updateForm(i){
	var uProcessId = $("#uProcessId_"+i).val();
	var uProcessName = $("#uProcessName_"+i).val();
	var uDescription = $("#uDescription_"+i).val();
	//alert(uDescription);
	$("#eProcessId").val(uProcessId);
	$("#eProcessName").val(uProcessName);
	$("#eDescription").val(uDescription);
	//alert(eDescription);
	$("#updateEtlProcess").submit();

}


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

<form name="EtlGroupList" id="EtlGroupList">
	<input  type="hidden" name="etlProcessTableName"  id="etlProcessTableName1" value="${requestParameters.etlDestTableName?if_exists}">
	<input  type="hidden" name="groupId"  id="groupId">
</form>	

<form name="EltBasedList" id="EltBasedList">
	<input  type="hidden" name="etlProcessTableName"  id="etlProcessTableName">
	<input  type="hidden" name="groupId"  id="groupId" value="${requestParameters.groupId?if_exists}">
</form>	


