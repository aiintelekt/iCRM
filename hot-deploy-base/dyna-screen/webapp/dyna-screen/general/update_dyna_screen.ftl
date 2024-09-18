<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://dyna-screen/webapp/dyna-screen/lib/picker_macro.ftl"/>
<#include "component://dyna-screen/webapp/dyna-screen/general/modal_window.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
	
<div class="row">
<div id="main" role="main">
<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
<#assign extra='
	<span id="update-dyna-screen" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</span>
	<span id="export-screen-btn" title="Export" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-edit" aria-hidden="true"></i> Export </span>
	<a href="findDynaScreen" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>
	' />
	
<@sectionFrameHeaderTab title="${uiLabelMap.updateDynaScreen!} [${inputContext.screenDisplayName!}]" tabId="updateDynaScreen" extra=extra/> 

<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">

	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="headingTwo">
			<h4 class="panel-title">
				<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
					href="#accordionDynaBase" aria-expanded="false"
					aria-controls="collapseOne"> Base Section </a>
			</h4>
		</div>
		
		<div id="accordionDynaBase" class="panel-collapse collapse hide" role="tabpanel" aria-labelledby="headingOne">
			<div class="panel-body">
				
				<form id="mainFrom" action ="<@ofbizUrl>dynaScreenUpdateAction</@ofbizUrl>" method="post">
	
				<input type="hidden" name="dynaConfigId" id="dynaConfigId" value="${inputContext.dynaConfigId!}"/>		 
				
				<div id="dyna-screen-fields"></div>
						 		 		 
				<div class="col-md-12 col-lg-12 col-sm-12 ">
			        <@dynaScreen 
							instanceId="CREATE_DYNA_SCREEN"
							modeOfAction="UPDATE"
							/>
			       <#-- <div class="row padding-r">
			        <div class="col-md-6 col-sm-6 form-horizontal">
			          
			        <@dropdownCell 
						id="componentMountPoint"
						label=uiLabelMap.module
						options=componentList
						required=false
						value=inputContext.componentMountPoint
						allowEmpty=true
						/>	
						
					<@inputRow 
						id="screenDisplayName"
						label=uiLabelMap.displayName
						placeholder=uiLabelMap.displayName
						value=inputContext.screenDisplayName!
						required=true
						/>	
						
					<@dropdownCell 
						id="layoutType"
						label=uiLabelMap.layoutType
						options=layoutTypeList
						required=true
						value=inputContext.layoutType
						allowEmpty=true
						/>
						
					<@dropdownCell 
						id="securityGroupId"
						label=uiLabelMap.securityGroup
						options=securityGroupList
						required=false
						value=inputContext.securityGroupId
						allowEmpty=true
						/>	
						
					<@inputRow 
						id="defaultMessage"
						label=uiLabelMap.defaultMessage
						placeholder=uiLabelMap.defaultMessage
						required=false
						value=inputContext.defaultMessage
						/> 	
						
					<@dropdownCell 
						id="labelColSize"
						label=uiLabelMap.labelColSize
						options=colSizeList
						required=false
						allowEmpty=true
						value=inputContext.labelColSize
						/>				
						
					</div>
					
					<div class="col-md-6 col-sm-6 form-horizontal">
					
					<@inputDate
				        id="fromDate"
				        label=uiLabelMap.fromDate
				        type="date"
				        value=inputContext.fromDate
				        placeholder=uiLabelMap.fromDate
				        />
				        
				  	<@inputDate
				        id="thruDate"
				        label=uiLabelMap.thruDate
				        type="date"
				        value=inputContext.thruDate
				        placeholder=uiLabelMap.thruDate
				        />   
				        
				 	<@dropdownCell 
						id="isPrimary"
						label=uiLabelMap.isPrimary
						options=yesNoOptions
						required=false
						allowEmpty=true
						value=inputContext.isPrimary!
						/>
						
					<@dropdownCell 
						id="isDisabledDyna"
						label=uiLabelMap.isDisabled
						options=yesNoOptions
						required=false
						allowEmpty=true
						value=inputContext.isDisabled!
						/>	
						
					<@dropdownCell 
						id="isFullscreen"
						label=uiLabelMap.isFullscreen
						options=yesNoOptions
						required=false
						allowEmpty=true
						value=inputContext.isFullscreen
						/>	
						
					<@dropdownCell 
						id="inputColSize"
						label=uiLabelMap.inputColSize
						options=colSizeList
						required=false
						allowEmpty=true
						value=inputContext.inputColSize
						/>						
					
					</div>
					</div>	-->	
					             
				</div>
				
			</form>
			
			</div>

		</div>

	</div>

</div>         
        
</div>
</div>
</div>

<form method="post" id="export-dyna-screen-form" action="exportDynaConfiguration" class="form-horizontal">
	<input type="hidden" name="dynaConfigIds" value="${inputContext.dynaConfigId!}">
</form>

<div class="row" style="width:100%">
  	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
  	<div class="border-b pt-2">
        <@headerH2 title="${uiLabelMap.listOfFields!}" class="float-left"/>
        <div class="float-right">
        
        <span id="add-dd-value-btn" title="Drop down value" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-plus" aria-hidden="true"></i> DD Value </span>
        <span id="add-field-detail-config-btn" title="Field Config" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-plus" aria-hidden="true"></i> Detail Config </span>
        <span id="add-field-adv-config-btn" title="Field Config" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-plus" aria-hidden="true"></i> Advance Config </span>
        
        <span id="preview-screen-field-btn" title="Quick Preview" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-eye" aria-hidden="true"></i> Quick Preview </span>
        <a target="_blank" href="previewDynaScreen?dynaConfigId=${inputContext.dynaConfigId!}" title="Detail Preview" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-eye" aria-hidden="true"></i> Detail Preview </a>
        
        <span id="add-screen-field-btn" title="Create" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-plus" aria-hidden="true"></i> Add </span>
        <span id="remove-screen-field-btn" title="Remove" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-times" aria-hidden="true"></i> Remove </span>
        
        <#if security.hasPermission("DYNA_SCN_LBL_MANG", userLogin)>
		<a target="_blank" href="/webtools/control/SearchLabels?externalLoginKey=${requestAttributes.externalLoginKey}" class="btn btn-primary btn-xs ml-2"><i class="fa fa-cogs" aria-hidden="true"></i> Label Manager</a>
		</#if>
		
        </div>
        <div class="clearfix"></div>
    </div>  
    	
  	<div id="dyna-field-grid" style="width: 100%;" class="ag-theme-balham"></div>
  	<script type="text/javascript" src="/dyna-screen-resource/js/ag-grid/create-widget/dyna_screen_step2.js"></script>
           
  	</div>
</div>



<div id="preview-modal-view" class="modal fade" >
  <div class="modal-dialog modal-xl">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title">Preview</h2>
        
        <@radioInputCell
			id="previewMode"
			name="previewMode"
			options=previewModes
			inputColSize="col-sm-12"
			value="CREATE"
			/>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        
      </div>
      <div class="modal-body">
        
		<form id="mainFromPreview" method="post" data-toggle="validator">

		<div id="preview-create" style="visibility: visible">		 		 		 		 
		<@dynaScreen 
			instanceId=inputContext.dynaConfigId!
			modeOfAction="CREATE"
			isConfigScreen="Y"
			/>
			
		<div class="form-group offset-2">
			<div class="text-left ml-3">
		      
		      <@formButton
			     btn1type="submit"
			     btn1label="${uiLabelMap.Save}"
			     btn1onclick="return formSubmission();"
			     btn2=true
			     btn2type="reset"
			     btn2label="${uiLabelMap.Clear}"
			   />
				 	
			</div>
		</div>	
		</div>
		
		<div id="preview-view" style="visibility: visible">		 		 		 		 
		<@dynaScreen 
			instanceId=inputContext.dynaConfigId!
			modeOfAction="VIEW"
			isConfigScreen="Y"
			/>
		</div>
		
		<div id="preview-update" style="visibility: visible">		 		 		 		 
		<@dynaScreen 
			instanceId=inputContext.dynaConfigId!
			modeOfAction="UPDATE"
			isConfigScreen="Y"
			/>
			
		<div class="form-group offset-2">
			<div class="text-left ml-3">
		      
		      <@formButton
			     btn1type="submit"
			     btn1label="${uiLabelMap.Save}"
			     btn1onclick="return formSubmission();"
			     btn2=true
			     btn2type="reset"
			     btn2label="${uiLabelMap.Clear}"
			   />
				 	
			</div>
		</div>	
		</div>
		
		</form>
        
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
</div>

<@labelPicker 
	instanceId="uiLabelPicker"
	isOnScreen=false
	/>

<@partyPicker 
	instanceId="partyPicker"
	/> 

<@addStaticData 
	instanceId="data-modal-view"
	/> 
<script src="/bootstrap/js/ag-grid-community.min.js"></script>
<link rel="stylesheet" href="/bootstrap/css/ag-grid.css">
<link rel="stylesheet" href="/bootstrap/css/ag-theme-balham.css">
<script>

$(document).ready(function() {

$('#preview-create').show(); $('#preview-view').hide(); $('#preview-update').hide();

$("input[name=previewMode]").change(function() {
	var previewMode = $(this).val();

	if (previewMode=="CREATE") {
		$('#preview-create').show(); $('#preview-view').hide(); $('#preview-update').hide();
	} else if (previewMode=="VIEW") {
		$('#preview-create').hide(); $('#preview-view').show(); $('#preview-update').hide();
	} else if (previewMode=="UPDATE") {
		$('#preview-create').hide(); $('#preview-view').hide(); $('#preview-update').show();
	}
		
});

loadScreenFieldGrid();

$('#add-dd-value-btn').on('click', function() {

	var selectedData = gridOptions.api.getSelectedRows();
    if (selectedData.length == 0 || selectedData.length > 1) {
    	showAlert ("error", "Select single field");
    } else {
    
    	selectedData = selectedData[0];
    	
    	if (selectedData.fieldType != "DROPDOWN" && selectedData.fieldType != "RADIO") {
    		showAlert ("error", "Field type should be Drop Down or Radio");
    	} else if (selectedData.lookupTypeId != "STATIC_DATA") {
    		showAlert ("error", "Lookup type should be STATIC DATA");
    	} else {
    		$('#data-modal-view').modal("show");
			    	
	    	$('#field-value-title').html( selectedData.fieldName );
	    	$('#selectedDynaFieldId').val( selectedData.dynaFieldId );
	    	
	    	loadFieldDataGrid();
    	}
    }
    
});

$('#preview-screen-field-btn').on('click', function() {
	$('#preview-modal-view').modal("show");
});

$('#add-field-adv-config-btn').on('click', function() {

	var selectedData = gridOptions.api.getSelectedRows();
    if (selectedData.length == 0 || selectedData.length > 1) {
    	showAlert ("error", "Select single field");
    } else {
    
    	selectedData = selectedData[0];
    	
    	if (selectedData.fieldType == "HIDDEN" || selectedData.fieldType == "DISPLAY" || selectedData.fieldType == "DATE_RANGE") {
    		showAlert ("error", "No advance field configuration for Hidden/Display/Date Range");
    	} else {
    		//$('#field-config-modal-view').modal("show");
			    	
	    	window.open("/dyna-screen/control/fieldAdvConfigUpdate?dynaConfigId=${inputContext.dynaConfigId!}&dynaFieldId="+selectedData.dynaFieldId);
    	}
    }
    
});

$('#add-field-detail-config-btn').on('click', function() {

	var selectedData = gridOptions.api.getSelectedRows();
    if (selectedData.length == 0 || selectedData.length > 1) {
    	showAlert ("error", "Select single field");
    } else {
    
    	selectedData = selectedData[0];
    		
	    window.open("/dyna-screen/control/fieldDetailConfigUpdate?dynaConfigId=${inputContext.dynaConfigId!}&dynaFieldId="+selectedData.dynaFieldId);
    }
    
});

$("#export-screen-btn").click(function(event) {
    $("#export-dyna-screen-form").submit();
});

});

</script>
   