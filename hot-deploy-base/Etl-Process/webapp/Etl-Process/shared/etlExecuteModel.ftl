<link href="/metronic/css/components.min.css" id="style_components" rel="stylesheet" type="text/css"/>
<#--style added by m.vijayakumar for must styles-->
 <style>
 .dropdown-menu.open{
 margin-left: -45px !important;
 }
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
.bs-select.form-control.btn-xs{
padding : 4px !important;
}
   </style>

<#--end @vijayakumar for style-->

<script type="text/javascript" src="/crmsfa_js/jquery.validate.js"></script>
<script>
$(document).ready(function(){
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
			
		}
	});	
});

  //for filter search box process added from headerMain.ftl @ 25/09/2016
      
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
      	 		$("#etlDestTableName").val(selectedModel);
      	 		$("#EltBasedList").submit();
      	 	}
      	 }
         	 function  getGroupList(element)
      	 {
      	 	var selectedModel = $(element).val();
      	 	//alert("the selected model value is "+selectedModel);
      	 	if(selectedModel!="")
      	 	{
      	 		$("#groupIdEtlDrop").val(selectedModel);
      	 		$("#EtlGroupList").submit();
      	 	}
      	 }
</script>


<!-- BEGIN HEADER -->

<body style="overflow: scroll;">





<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12" style="">

<div></div>





<#--portlet 1-->

<div class="row-fluid">

	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
	<div>

		<div >

			<div>
		</div>

	<div class="actions" style="width:100%">

									<#--<span class=" bold" style="color:#337AB7">ETL PROCESS CONFIGURATION</span>-->
	<div>								
	<div class="portlet-body-grey">
		<div class="portlet light">

		<div class="portlet-title  band">
			<div class="caption">
				<i class="icon-settings "></i>
				<span class="caption-subject">${uiLabelMap.modelsList}</span>
			</div>
		&nbsp;&nbsp;&nbsp;
		<div class="actions display_full_width" >
		<#--<#assign etlGroups=delegator.findAll("EtlGrouping")/>-->
		<#if etlGroups?has_content>	
			<select class="bs-select form-control btn-xs"  data-live-search="true" data-size="8" onchange="getGroupList(this);">
			<option value="">${uiLabelMap.selectChannels}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
			<#--<option value="MASTER_PRODUCT">Master Product</option>-->
			<#list etlGroups as group>
				<option value="${group.groupId?if_exists}"<#if group.groupId?if_exists==requestParameters.groupId?if_exists>selected</#if>>${group.groupName?if_exists} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>	
			</#list>
			</select>
		</#if>
		</div>
			<div class="actions display_full_width"  style="padding-right: 20px;">
			
		<#if etlPr_ocess?has_content>	
			<select class="bs-select form-control btn-xs"  data-live-search="true" data-size="8" onchange="getModelBasedList(this);">
			<option value="" >${uiLabelMap.selectType}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
			
			<option value="DataImportLead" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportLead">Selected</#if></#if>>Lead Model</option>
			<option value="DataImportAccount" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportAccount">Selected</#if></#if>>Account Model</option>
			<option value="DataImportCustomer" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportCustomer">Selected</#if></#if>>Customer Model</option>
			
			<#-- 
			<option value="DataImportSupplier" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportSupplier">Selected</#if></#if>>Supplier Model</option>
			<option value="DataImportCategory" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportCategory">Selected</#if></#if>>Category Model</option>
			<option value="DataImportProduct" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportProduct">Selected</#if></#if>>Product Model</option>
			<option value="EtlImportOrderFields" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="EtlImportOrderFields">Selected</#if></#if>>Purchase Order Model</option>
			<option value="DataImportInvoiceHeader" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportInvoiceHeader">Selected</#if></#if>>Invoice Model</option>
			<option value="DataImportInvoiceItem" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportInvoiceItem">Selected</#if></#if>>Invoice Item Model</option>
			<option value="FioLockboxBatchStaging" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="FioLockboxBatchStaging">Selected</#if></#if>>Lockbox Model</option>
			<option value="FioLockboxBatchItemStaging" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="FioLockboxBatchItemStaging">Selected</#if></#if>>Lockbox Item Model</option>
			<option value="DataImportWallet" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportWallet">Selected</#if></#if>>Wallet Model</option>
			 -->		
			<#--<#list etlPr_ocess as listEtl>
				<option value="${listEtl.tableName?if_exists}" <#if etlDestTableName?has_content && etlDestTableName?if_exists==listEtl.tableName?if_exists>selected</#if>>
				<#if listEtl.tableName?if_exists=="DmgPhysicalInventory">Physical Inventory Import<#else>${listEtl.tableTitle?if_exists}</#if>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </option>	
			</#list>-->
			
			</select>
		</#if>	
		</div>
				<div class="btn-group">
					<#--<button type="button" class="btn green-turquoise" data-toggle="modal" href="#fileUpload" >Create New</button>-->
					<ul class="dropdown-menu pull-right">
						<li>
							<a href="javascript:;">
								<i class="fa fa-pencil"></i> Edit </a>
						</li>
						<li>
							<a href="javascript:;">
								<i class="fa fa-trash-o"></i> Delete </a>
						</li>
					</ul>
				</div>
			</div>
		
		
			<#assign colorsSize = containerColors.size?if_exists/>
			<#assign index=0/>
			<#if etlSet?has_content>
			<#assign headerGrp = request.getParameter("groupId")?if_exists />
			<#if headerGrp?has_content>
			<#assign headerGroup = delegator.findByPrimaryKey("EtlGrouping",Static["org.ofbiz.base.util.UtilMisc"].toMap("groupId", "${headerGrp?if_exists}"))/>
					<fieldset class="scheduler-border">
		    		<legend class="scheduler-border">${headerGroup.groupName?if_exists}</legend>
		    		
		    </#if></div>
 <div class="scroller" style=" max-height: 400px; overflow: auto;overflow-x: auto; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1" id="csvSortable">		
	
	
	<div id="csvSortable">
			<#list etlSet as listEtl>
	<div class="col-md-2 col-sm-2 col-xs-6">
<div class="page-toolbar">
<div class="btn-group pull-right">
	<#--><a class="tooltips btn-circle" data-original-title="Edit Model" href="<@ofbizUrl>applyEtlModel?model=${listEtl.listName?if_exists}&groupId=${parameters.groupId?if_exists}</@ofbizUrl>" style="color: #fff !important;"><i class="icon-bag"></i></a>-->
	<a target="_blank" href="<@ofbizUrl>applyEtlModel?model=${listEtl.listName?if_exists}&groupId=${parameters.groupId?if_exists} &id=1</@ofbizUrl>" class="tooltips btn btn-xs white" data-original-title="Execute Model">
                                                        <i class="icon-social-dribbble"></i>
                                                                    </a>
<a target="_blank" href="<@ofbizUrl>myHome?listId=${listEtl.listName?if_exists}</@ofbizUrl>" class="tooltips btn btn-xs white" data-original-title="Edit Model">
                                                                        <i class="fa fa-edit"></i>
                                                                    </a>                                                                    
	<#--<a class="tooltips btn-circle" data-original-title="Execute Model" href="#" style="color: #fff !important;"><i class="icon-shield"></i></a>-->
</div>
</div>
	<#assign minititle =  listEtl.listName?if_exists/>
	<a target="_blank" href="<@ofbizUrl>applyEtlModel?model=${listEtl.listName?if_exists}&groupId=${parameters.groupId?if_exists}</@ofbizUrl>" style="color:white;text-decoration: none;">
	<div class="color-demo tooltips" data-original-title="" >
		<#if listEtl.sequenceNo?has_content><span class="badge badge-primary pull-right">${listEtl.sequenceNo?if_exists}</span></#if>
		<div style="font-size:10px;"  data-original-title="${listEtl.listName?if_exists}" class="tooltips color-view bg-${containerColors.get(index)} bg-font-${listEtl.color?if_exists} bold uppercase" style="font-size:11px;">
		<#--<#if (ln> 15)>${ln}=${listEtl.listName?if_exists}...<#else>${listEtl.listName?if_exists}</#if>-->
		<#if minititle?length &lt; 12>
		${minititle}
		<#else>
		${minititle?substring(0,11)} ...
		</#if>
		</div>
		<#--<div class="color-info bg-white c-font-11 sbold" style="font-size:10px;"><a target="_blank" href="<@ofbizUrl>applyEtlModel?model=${listEtl.listName?if_exists}&groupId=${parameters.groupId?if_exists}</@ofbizUrl>" style="color:black;" >${listEtl.listName?if_exists}</a> </div>-->
	</div>
</a>
	<div class="modal fade" id="demo_modal_${listEtl.color?if_exists}">
		<div class="modal-dialog modal-lg">
			<div class="modal-content c-square">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title bold uppercase font-blue">Execute Model</h4>
				</div>
				<div class="modal-body">
			        <div class="portlet light">
          <div class="portlet-title">
            <div class="caption">
              <form class="form-horizontal" role="form">
                <div class="form-group">
                  <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                      <h4>
                        <div class="label label-default" style="background-color: white;color: grey;">Importing</div>
                      </h4>
                    </div>
                    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                      <h4>
                        <div class="label label-default" style="background-color: white;color: grey;">#Proceeded</div>
                      </h4>
                    </div>
                    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                      <h4>
                        <div class="label label-default" style="background-color: white;color: grey;">#Not Proceeded</div>
                      </h4>
                    </div>
                  </div>
                </div>
                <div class="form-group">
                  <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                      <label class="">Lines</label>
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
              <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
              <i class="icon-social-dribbble font-green"></i>
              <span class="caption-subject font-green bold uppercase">Upload a File</span>
            </div>
          </div>
     </div>
 <div class="portlet-body container">
            <#--portlet body -->
            <form class="form-horizontal" role="form" method="post" action="uploadEtlFile?id=1" enctype="multipart/form-data">
            <input type="hidden" name="processId" id="processId" value=""/>
              <div class="form-group">
                <label class="control-label col-sm-2" for="email">File to Import</label>
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
              </div>
              <div class="form-group">
                <div class="col-sm-offset-2 col-sm-6">          
                  <label style="color:red">												       
                  Pick the right excel (2003 edition) and upload
                  / upload text file with tab delimiter
                  </label>
                </div>
              </div>
              <div class="form-group">
                <label class="control-label col-sm-2" for="email">File Format:</label>
                <div class="col-sm-4">
                  <select class="form-control btn-xs" data-size="8" id="getSelected">
                    <option value="csv">CSV</option>
                  </select>
                </div>
              </div>
             
              <div class="form-group">
                <div class="col-sm-offset-2 col-sm-8">          
                  <input type="submit" class="btn btn-default btn-sm" style="background-color:white" value="Upload"/>
                </div>
              </div>
            </form>
            <!--end of form-->
            <#--end of portlet body-->
          </div>
				</div>
			</div>
			
		</div>
		
	</div>
</div>

<#assign index = index+1/>
<#if index == 7>
	<#assign index = 0/>
</#if>
</#list>
</div>

</div>
</fieldset>
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
        <h4 class="modal-title">Create Process</h4>
      </div>
      <div class="modal-body">
      <div class="row">
      <div class="col-md-12">
	<div class="portlet light bordered">
			<div class="portlet-title">
				<div class="caption font-red-sunglo">
					<i class="icon-settings font-red-sunglo"></i>
					<span class="caption-subject bold uppercase">Create Process</span>
				</div>
			</div>
			<div class="portlet-body form">
				<form role="" method="post" name="processForm" action="<@ofbizUrl>createEtlProcess</@ofbizUrl>">
					<div class="form-body">
						<div class="form-group form-md-line-input">
							<input type="text" class="form-control" name="processId" id="form_control_1" value="" placeholder="Process Id">
							<label for="form_control_1"></label>
							<span class="help-block">Some help goes here...</span>
						</div>
						<div class="form-group form-md-line-input">
							<input type="text" class="form-control" name="processName" id="form_control_1" value="" placeholder="Process Name">
							<label for="form_control_1"></label>
							<span class="help-block">Some help goes here...</span>
						</div>
					<div class="form-group form-md-line-input">
                        <textarea class="form-control" rows="3" name="description" placeholder="Description" value=""></textarea>
                        <label for="form_control_1"></label>
                    </div>
					</div>
					<div class="form-actions noborder">
						<button type="submit" class="btn blue">Create</button>
						
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
	<input  type="hidden" name="etlDestTableName"  id="etlDestTableName1" value="${requestParameters.etlDestTableName?if_exists}">
	<input  type="hidden" name="groupId"  id="groupIdEtlDrop" value="${requestParameters.groupId?if_exists}">
</form>	
<form name="EltBasedList" id="EltBasedList">
	<input  type="hidden" name="etlDestTableName"  id="etlDestTableName">
	<input  type="hidden" name="groupId"  id="groupId" value="${requestParameters.groupId?if_exists}">
</form>	