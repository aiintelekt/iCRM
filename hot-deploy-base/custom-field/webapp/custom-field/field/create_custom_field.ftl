<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<div class="row">
    <div id="main" role="main">
    <#assign extra='' />
		<#if customFieldGroup.groupId?has_content>
		<#assign extra='<a href="findCustomField" class="btn btn-xs btn-primary m5 tooltips" title="Cancel" >Cancel</a>' />
		<#assign extra=extra+'<a href="editCustomFieldGroup?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="Back to ${customFieldGroup.groupName!}" >Back</a>'/>
		</#if>
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
<@sectionFrameHeader extra=extra  title="${uiLabelMap.Create} ${uiLabelMap.CustomField}"/>
<#if customFieldGroup.groupId?has_content>
	<#assign actionUrl = "createCustomField"/>
<#else>
	<#assign actionUrl = "createCustomFieldIndividual"/>
</#if>
	<div class="col-md-7 col-sm-12" style="padding:0px">
		
		<div class="portlet-body form">
			<form id="custom_field_form" role="form" class="form-horizontal" action="<@ofbizUrl>${actionUrl}</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
				
			<div class="form-body">
			
			<#if groupId?has_content>
			<input type="hidden" id="groupId" name="groupId" value="${customField.groupId!}" />						
			<#-- <@readonlyInput 
				id="groupId"
				label=uiLabelMap.customGroup
				value=customField.groupId
				isHiddenInput=true
				/> -->
			<#else>	
			<@dropdownCell 
				id="groupId"
				label=uiLabelMap.customGroup
				options=groupList
				required=false
				value=customField.groupId
				allowEmpty=true
				tooltip = uiLabelMap.customGroup
				dataLiveSearch=true
				placeholder=uiLabelMap.pleaseSelect
				/>
			</#if>
			<#-- 
			<@dropdownInput 
				id="roleTypeId"
				label=uiLabelMap.roleTypeId
				options=roleTypeList
				required=true
				value=customField.roleTypeId
				allowEmpty=true
				tooltip = uiLabelMap.roleTypeId
				dataLiveSearch=true
				/>
			 -->
			<@inputRow 
				id="customFieldName"
				label=uiLabelMap.customFieldName
				placeholder=uiLabelMap.customFieldName
				value=customField.customFieldName
				tooltip = uiLabelMap.customFieldName
				required=true
				maxlength=255
				dataError="Please Enter Field Name"
				/>
							
			<@dropdownCell 
				id="customFieldType"
				label=uiLabelMap.customFieldType
				options=fieldTypeList
				required=true
				value=customField.customFieldType
				allowEmpty=true
				tooltip = uiLabelMap.customFieldType
				placeholder=uiLabelMap.pleaseSelect
				dataError="Please Select Field Type"
				/>	
				
			<@dropdownCell 
				id="customFieldFormat"
				label=uiLabelMap.customFieldFormat
				options=fieldFormatList
				required=true
				value=customField.customFieldFormat
				allowEmpty=true
				tooltip = uiLabelMap.customFieldFormat
				placeholder=uiLabelMap.pleaseSelect
				dataError="Please Select Field Format"
				/>	
				
			<@dropdownCell 
				id="customFieldLength"
				label=uiLabelMap.fieldLength
				options=fieldLengthList
				required=false
				value=customField.customFieldLength
				allowEmpty=true
				tooltip = uiLabelMap.fieldLength				
				placeholder=uiLabelMap.pleaseSelect/>
			<#-- <@dropdownCell 
				id="roleTypeId"
				label=uiLabelMap.roleTypeId
				options=roleTypeList
				required=true
				value=roleConfig?if_exists
				allowEmpty=true
				dataLiveSearch=true
				placeholder=uiLabelMap.pleaseSelect
				dataError="Please Select Role Type"
				/>			
			 -->	
			<@inputRow 
				id="sequenceNumber"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=customField.sequenceNumber
				tooltip = uiLabelMap.sequence
				maxlength=20
				dataError="Please Enter Digits Only"
				/>	
				
			<@dropdownCell 
				id="hide"
				label=uiLabelMap.hide
				options=yesNoOptions
				required=false
				value=customField.hide
				allowEmpty=true
				tooltip = uiLabelMap.hide
				placeholder=uiLabelMap.pleaseSelect
				/>	
				
			<@dropdownCell 
				id="paramDisplayType"
				label=uiLabelMap.paramDisplayType
				options=paramDisplayTypes
				required=false
				value=customField.paramDisplayType
				allowEmpty=true
				tooltip = uiLabelMap.paramDisplayType
				/>
			<@inputRowPicker 
		        inputColSize=" col-sm-8 left"
		        labelColSize="col-sm-4 field-text"
		        glyphiconClass= "fa fa-id-card"
		        pickerWindow="promoCampaignPicker"
		        label="Coupon Campaign"
		        value=customField.sequenceNumber!
		        desValue=""
		        id="productPromoCodeGroupId"  
		        name="productPromoCodeGroupId" 
		        placeholder="Coupon Campaign"
		        />																					
														
			</div>
			
			<div class="offset-sm-4 col-sm-7 p-2">
				<@submit label=uiLabelMap.submit onclick="javascript:return onSubmitValidate(this);"/>
			</div>
			
		</form>			
							
		</div>	
	</div>
</div>
</div>
</div>
<@promoCampaignPicker 
	instanceId="promoCampaignPicker"
	/>
<script>
$('#customFieldName').keyup(function(){	
		if($(this).val()!=""){
		
		$('#customFieldName_error').hide();
	}
});
$('#customFieldType').change(function(){	
		if($(this).val()!=""){
		
		$('#customFieldType_error').hide();
	}
});
$('#customFieldFormat').change(function(){	
		if($(this).val()!=""){
		
		$('#customFieldFormat_error').hide();
	}
});
$('#roleTypeId').change(function(){	
		if($(this).val()!=""){
		
		$('#roleTypeId_error').hide();
	}
});

$('#sequenceNumber').keyup(function(){	
		if($(this).val()!=""){		
		$('#sequenceNumber_error').hide();
		}
	});



function onSubmitValidate() {	
	var valid=true;
	if($('#customFieldName').val()==""){
	$('#customFieldName_error').html("Please Enter Field Name");
	$('#customFieldName_error').show();	
		valid=false;
	}
	else if($('#customFieldName').val().length>600){
	$('#customFieldName_error').html("Please Enter less than 60 Characters");
	$('#customFieldName_error').show();
		valid=false;
	}
	if($('#customFieldType').val()==""){
	$('#customFieldType_error').html("Please Select Field Type");
	$('#customFieldType_error').show();
		valid=false;
	}
	if($('#customFieldFormat').val()==""){
	$('#customFieldFormat_error').html("Please Select Field Format");
	$('#customFieldFormat_error').show();
		valid=false;
	}
	if($('#roleTypeId').val()==""){
	$('#roleTypeId_error').html("Please Select Role Type");
	$('#roleTypeId_error').show();
		valid=false;
	}
	if($('#sequenceNumber').val() != ""){
		if(!new RegExp(/^[0-9]{0,20}$/).test($('#sequenceNumber').val())){
		$('#sequenceNumber_error').html("Please Enter Digits only");
			$('#sequenceNumber_error').show();
			valid=false;
			}	
		}
		 	
	return valid;
	 
   }
</script>
