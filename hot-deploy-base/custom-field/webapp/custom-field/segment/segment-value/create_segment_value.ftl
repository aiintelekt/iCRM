<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<div class="row">
    <div id="main" role="main">  
    <div  class="col-lg-12 col-md-12 col-sm-12 dash-panel"> 
    <#assign extra=""/> 
		<#if customFieldGroup.groupId?has_content>
		<#-- <a href="findSegmentValue" class="btn btn-xs btn-primary m5 tooltips" title="Cancel" >Cancel</a> -->
		<#assign extra='<a href="viewSegmentValueForGroup?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="Back to ${customFieldGroup.groupName!}" >Back</a>'/>
		</#if>
		<@sectionFrameHeader title="${uiLabelMap.Create} ${uiLabelMap.SegmentValue}" extra=extra ><#if customFieldGroup.groupName?has_content>for [ ${customFieldGroup.groupName} ] </#if></@sectionFrameHeader>
<#if customFieldGroup.groupId?has_content>
	<#assign actionUrl = "createSegmentValue"/>
<#else>
	<#assign actionUrl = "createSegmentValueIndividual"/>
</#if>
	<div class="col-md-6 col-sm-6" style="padding:0px">		
		<div class="portlet-body form">
			<form id="segment-value-form" role="form" class="form-horizontal" action="<@ofbizUrl>${actionUrl}</@ofbizUrl>" encType="multipart/form-data" method="post" >
				
			<div class="form-body">
			<#if groupId?has_content>	
			<input type="hidden" id="groupId" name="groupId" value="${customField.groupId!}" />					
			<#-- <@readonlyInput 
				id="groupId"
				label=uiLabelMap.segmentCode
				value=customField.groupId
				isHiddenInput=true
				/> -->
			<#else>	
			<@dropdownCell 
				id="groupId"
				label=uiLabelMap.segmentCode
				options=groupList
				required=true
				value=customField.groupId
				allowEmpty=true
				dataLiveSearch=true
				placeholder=uiLabelMap.pleaseSelect
				dataError="Please Select Segment Code"
				/>
			
			</#if>	
			<@inputRow 
				id="customFieldId"
				label=uiLabelMap.segmentValueId
				placeholder=uiLabelMap.segmentValueId
				value=customField.customFieldId
				required=true
				maxlength=250
				dataError="Please Enter Segment Value Id"
				/>		
									
			<@inputRow 
				id="customFieldName"
				label=uiLabelMap.segmentValueName
				placeholder=uiLabelMap.segmentValueName
				value=customField.customFieldName
				required=true
				maxlength=255
				dataError="Please Enter Segment Value Name"
				/>	
			<@inputHidden id="valueCaptureId" />
			<@inputRow 
				id="valueCapture"
				label=uiLabelMap.valueCapture
				value=customFieldGroup.valueCapture
				readonly=true
				/>
				
			<@dropdownCell 
				id="isEnabled"
				label=uiLabelMap.isEnabled
				options=yesNoOptions
				required=fales
				value=customField.isEnabled
				allowEmpty=truegroupId
				placeholder=uiLabelMap.pleaseSelect
				/>		
			
			<#-- <div class="page-header">
				<h2>${uiLabelMap.Configuration} ${uiLabelMap.SegmentValue} <span id="value-capture"></span></h2>
			</div> -->
			
			<#-- <input type="hidden" name="valueCapture" value="${customFieldGroup.valueCapture!}" /> -->
			<input type="hidden" name="valueSeqNum" value="1" />		
			
			<div id="range-value-config" style="display: none"> 
			
			<@inputRow 
				id="valueMin"
				label=uiLabelMap.valueMin
				placeholder=uiLabelMap.valueMin
				value=valueConfig.valueMin
				required=true				
				maxlength=20
				dataError="Please Enter Minimum Value"
				/>	
				
			<@inputRow 
				id="valueMax"
				label=uiLabelMap.valueMax
				placeholder=uiLabelMap.valueMax
				value=valueConfig.valueMax
				required=true				
				maxlength=20
				dataError="Please Enter Maximum Value"
				/>
			
			</div>
						
			<div id="single-value-config" style="display: none">		
			
			<@inputRow 
				id="valueData"
				label=uiLabelMap.valueData
				placeholder=uiLabelMap.valueData
				value=valueConfig.valueData
				required=true
				/>	
																								
			</div>
			
			<@dropdownCell 
				id="isDefault"
				label=uiLabelMap.isDefault
				options=yesNoOptions
				required=fales
				allowEmpty=true
				placeholder=uiLabelMap.pleaseSelect
				/>	
			
			<@inputRow 
				id="sequenceNumber"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=customField.sequenceNumber
				maxlength=20
				dataError="Please Enter Digits only"
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
			
			<div class="form-group row">
                    <div class="offset-sm-4 col-sm-7 p-2">
                       <@submit label="${uiLabelMap.submit!}" onclick="javascript:return onSubmitValidate(this);"/>
                    	<@reset label="${uiLabelMap.Clear!}" onclick="javascript:clearFields();"/>
                    </div>
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

jQuery(document).ready(function() { 

$('#segment-value-form').validator();

loadSegmentCode();

$('#groupId').on('change', function(){

	loadSegmentCode();

});
	
});

function loadSegmentCode() {
	
	$("#single-value-config").hide();
	$("#range-value-config").hide();
	
	var groupId = $("#groupId").val();
	
	if (groupId) {
	
		//$("#segment-value-form").validator('destroy');
	
		$.ajax({
		      
			type: "POST",
	     	url: "loadSegmentCode",
	        data:  {"groupId": groupId},
	        success: function (data) { 
	        console.log("valueCapture"+data.segmentCode.valueCapture);
	            
	            if (data.code == 200) {
					
					if (data.segmentCode.valueCapture == "SINGLE") {
						$("#single-value-config").show();
						$('#valueData').attr('required','required');
						$('#valueMax').removeAttr('required');
						$('#valueMin').removeAttr('required');
					} else if (data.segmentCode.valueCapture == "MULTIPLE") {
						$('#valueData').removeAttr('required');
						$('#valueMax').removeAttr('required');
						$('#valueMin').removeAttr('required');
					} else if (data.segmentCode.valueCapture == "RANGE") {
						$("#range-value-config").show();
						$('#valueData').removeAttr('required');
						$('#valueMax').attr('required','required');
						$('#valueMin').attr('required','required');
					}
					
					$("#valueCapture").val( "[ " + data.segmentCode.valueCapture + " ]" );
					$("#valueCaptureId").val(data.segmentCode.valueCapture);
					$("#segment-value-form").validator('update');
					
				} else {
					showAlert ("error", data.message);
					 console.log(""+data);  
				}  
				    	
	        }
	        
		});
	}
	
}
function clearFields(){
$('#customFieldId_error').hide();
$('#customFieldName_error').hide();
$('#valueData_error').hide();
$('#groupId_error').hide();
}
	
	$('#customFieldId').keyup(function(){	
		if($(this).val()!=""){
		
		$('#customFieldId_error').hide();
	}
});
$('#customFieldName').keyup(function(){	
		if($(this).val()!=""){
		
		$('#customFieldName_error').hide();
	}
});
$('#valueData').keyup(function(){	
		if($(this).val()!=""){
		
		$('#valueData_error').hide();
	}
});
$('#groupId').change(function(){	
		if($(this).val()!=""){
		
		$('#groupId_error').hide();
	}
	$('#valueData_error').html("-");
});
$('#sequenceNumber').keyup(function(){	
		if($(this).val()!=""){		
		$('#sequenceNumber_error').hide();
		}
	});
	$('#valueMin').keyup(function(){	
		if($(this).val()!=""){		
		$('#valueMin_error').hide();
		}
	});
	$('#valueMax').keyup(function(){	
		if($(this).val()!=""){		
		$('#valueMax_error').hide();
		}
	});



	function onSubmitValidate() {	
	var valid=true;
	if($('#groupId').val()==""){
	$('#groupId_error').html("Please Select Segment Code");	
	$('#groupId_error').show();
		valid=false;
	}
	if($('#customFieldId').val()==""){
	$('#customFieldId_error').html("Please Enter Segment Value Id");
	$('#customFieldId_error').show();
		valid=false;
	}
	else if($('#customFieldId').val().length>250){
	$('#customFieldId_error').html("Please Enter less than 60 Characters");
	$('#customFieldId_error').show();
		valid=false;
	}
	
	if($('#customFieldName').val()==""){
	$('#customFieldName_error').html("Please Enter Segment Value Name");
	$('#customFieldName_error').show();
		valid=false;
	}
	else if($('#customFieldName').val().length>250){
	$('#customFieldName_error').html("Please Enter less than 60 Characters");
	$('#customFieldName_error').show();
		valid=false;
	}
	if($("#valueCaptureId").val() == "SINGLE" && $('#valueData').val()==""){
	$('#valueData_error').html("Please Enter Data");
	$('#valueData_error').show();
		valid=false;
	}
		
	if($('#sequenceNumber').val() != ""){
		if(!new RegExp(/^[0-9]{0,20}$/).test($('#sequenceNumber').val())){
		$('#sequenceNumber_error').html("Please Enter Digits only");
			$('#sequenceNumber_error').show();
			valid=false;
		}
	}
	if($('#valueMin').val()=="" && $("#valueCaptureId").val() == "RANGE"){
		$('#valueMin_error').html("Please Enter Minimum Value");
		$('#valueMin_error').show();
		valid=false;
	}
	else if($('#valueMin').val() != ""){
		if(!new RegExp(/^[0-9]{0,20}$/).test($('#valueMin').val())){
		$('#valueMin_error').html("Please Enter Digits only");
			$('#valueMin_error').show();
			valid=false;
		}
	} 
	if($('#valueMax').val()=="" && $("#valueCaptureId").val() == "RANGE"){
		$('#valueMax_error').html("Please Enter Maximum Value");
		$('#valueMax_error').show();
		valid=false;
	}
	else if($('#valueMax').val() != ""){
		if(!new RegExp(/^[0-9]{0,20}$/).test($('#valueMax').val())){
		$('#valueMax_error').html("Please Enter Digits only");
			$('#valueMax_error').show();
			valid=false;
		}
	} 	
		
	return valid;
	 
   }

</script>
