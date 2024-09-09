<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<div class="row">
    <div id="main" role="main">
    <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "UpdateEconomicMetric") />  
		<#assign extra='<a href="findEconomicMetric" class="btn btn-xs btn-primary m5 tooltips" title="Cancel" >Cancel</a>'/>
		
		<#if customFieldGroup.groupId?has_content>
		<#assign extra=extra+'<a href="editEconomicMetric?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="Back to ${customFieldGroup.groupName!}" >Back</a>'/>
		</#if>
		
		<#if customField.isEnabled?has_content && customField.isEnabled == "Y" >
			<#assign extra=extra+'<a href="#" class="btn btn-xs btn-primary m5 tooltips active-btn" title="Disable Economic Metric" data-isEnabled="N">Disable</a>'/>
		<#else>
			<#assign extra=extra+'<a href="#" class="btn btn-xs btn-primary m5 tooltips active-btn" title="Enable Economic Metric" data-isEnabled="Y">Enable</a>'/>
		</#if>
		<#assign extra=extra+'<a href="economicValueCustomer?customFieldId=${customField.customFieldId}&groupId=${customFieldGroup.groupId!}" class="btn btn-xs btn-primary m5 tooltips ' />
		<#if !customFieldGroup.groupId?has_content>
		<#assign extra=extra+'disabled' />
		</#if>
		<#assign extra=extra+'" title="${uiLabelMap.ManageCustomers!}" >${uiLabelMap.ManageCustomers!}</a>'/>
	
<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
<@sectionFrameHeaderTab title="${uiLabelMap.Update} ${uiLabelMap.EconomicValue}" tabId="UpdateEconomicMetric" extra=extra?if_exists > <#if customFieldGroup.groupName?has_content>for [ ${customFieldGroup.groupName} <i class="fa fa-arrow-right" aria-hidden="true"></i> ${customField.customFieldName} ]</#if></@sectionFrameHeaderTab>
	<div class="col-md-6 col-sm-12 ">
	
		
		<div class="portlet-body form">
			<form id="segment-value-form" role="form" class="form-horizontal" action="<@ofbizUrl>updateEconomicValue</@ofbizUrl>" encType="multipart/form-data" method="post" >
					
			<div class="form-body">
			
			<#-- <@dropdownInput 
				id="roleTypeId"
				label=uiLabelMap.roleTypeId
				options=roleTypeList
				required=true
				value=customField.roleTypeId
				allowEmpty=false
				tooltip = uiLabelMap.roleTypeId
				/> -->
							
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
				label=uiLabelMap.economicMetric
				options=groupList
				required=false
				value=customField.groupId
				allowEmpty=true
				/>
			</#if>
			
			<@inputRow 
				id="customFieldId"
				label=uiLabelMap.economicValueId
				value=customField.customFieldId
				readonly=true
				/>
			<#-- 
			<@generalInput 
				id="customFieldId"
				label=uiLabelMap.segmentValueId
				placeholder=uiLabelMap.segmentValueId
				value=customField.customFieldId
				required=true
				maxlength=20
				/>	
			 -->
			<@inputRow 
				id="customFieldName"
				label=uiLabelMap.economicValueName
				placeholder=uiLabelMap.economicValueName
				value=customField.customFieldName
				required=true
				maxlength=255
				dataError="Please Enter Economic Metric Name"
				/>
				
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
				required=false
				value=customField.isEnabled
				allowEmpty=true
				/>		
				
			<#if groupId?has_content>
					
			<#-- <div class="page-header"> 
				<h2>${uiLabelMap.Configuration} ${uiLabelMap.SegmentValue}</h2>
			</div> -->
			
			<#-- <input type="hidden" name="valueCapture" value="${customFieldGroup.valueCapture!}" /> -->
			<input type="hidden" name="valueSeqNum" value="1" />	
			
			<div id="range-value-config" style="display: none"> 
			
			<@inputRow 
				id="valueMin"
				label=uiLabelMap.valueMin
				placeholder=uiLabelMap.valueMin
				value=valueConfig.valueMin
				required=false
				maxlength=20
				/>	
				
			<@inputRow 
				id="valueMax"
				label=uiLabelMap.valueMax
				placeholder=uiLabelMap.valueMax
				value=valueConfig.valueMax
				required=false
				maxlength=20
				/>
			
			</div>	
			
			<#-- <div id="single-value-config" style="display: none">			
												
			<@generalInput 
				id="valueData"
				label=uiLabelMap.valueData
				placeholder=uiLabelMap.valueData
				value=valueConfig.valueData
				required=true
				/>		
			</div> -->
			</#if>	
			
			
			
			<@inputRow 
				id="sequenceNumber"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=customField.sequenceNumber				
				dataError="Please Enter Digits only"
				maxlength=20
				/>	
				<@inputRowPicker 
			        inputColSize=" col-sm-8 left"
			        labelColSize="col-sm-4 field-text"
			        glyphiconClass= "fa fa-id-card"
			        pickerWindow="promoCampaignPicker"
			        label="Coupon Campaign"
			        value=customField.productPromoCodeGroupId!''
			        desValue=customField.productPromoCodeGroupId!''
			        id="productPromoCodeGroupId"  
			        name="productPromoCodeGroupId" 
			        placeholder="Coupon Campaign"
			        />																																																																												
			 																																																																																																																																																																																																																																																	
			</div>
			
			<#if groupId?has_content>
				<#assign cancelActionUrl = "viewEconomicValueForGroup"/>
			<#else>
				<#assign cancelActionUrl = "findEconomicValue"/>
			</#if>
			<div class="form-group row">
                    <div class="offset-sm-4 col-sm-7 p-2">
                      <@submit label="Submit" onclick="javascript:return onSubmitValidate(this);"/>
					  <@cancel label="Cancel" onclick="${cancelActionUrl}?groupId=${groupId!}"/>
                    </div>
                </div>
			</form>			
							
		</div>	
</div>
</div>
</div>
</div>

${screens.render("component://custom-field/webapp/widget/custom-field/screens/segment/SegmentScreens.xml#SegmentValueMultiValue")}
<@promoCampaignPicker 
	instanceId="promoCampaignPicker"
	/>
<script>

jQuery(document).ready(function() { 

$('#segment-value-form').validator();

//loadSegmentCode();

$('#groupId').on('change', function(){

	//loadSegmentCode();

});

$('.active-btn').click(function () {

	resetDefaultEvents();

	//alert($(this).attr("data-isEnabled"));
	var actionType = $(this).attr("data-isEnabled");
	var actionUrl = "";
	if (actionType == "Y") {
		actionUrl = "enableEconomicMetric";
	} else if (actionType == "N") {
		actionUrl = "disabledEconomicMetric";
	}
	
	// title="In Active Segment Code" data-isActive="N"
	
	$.ajax({
			      
		type: "POST",
     	url: actionUrl,
        data:  {"customFieldId": "${customField.customFieldId!}"},
        success: function (data) {   
            
            if (data.code == 200) {
				showAlert ("success", data.message);
				
				if (actionType == "Y") {
					$('.active-btn').html("Disable");
					$('.active-btn').attr("title", "Disable Economic Metric");
					$('.active-btn').attr("data-original-title", "Disable Economic Metric");
					$('.active-btn').attr("data-isEnabled", "N");
					$('#isEnabled').val("N");
				} else {
					$('.active-btn').html("Enable");
					$('.active-btn').attr("title", "Enable Economic Metric");
					$('.active-btn').attr("data-original-title", "Enable Economic Metric");
					$('.active-btn').attr("data-isEnabled", "Y");
					$('#isEnabled').val("Y");
				}
				
			} else {
				showAlert ("error", returnedData.message);
			}
			    	
        }
        
	});    

});

});

function loadSegmentCode() {
	
	//$("#single-value-config").hide();
	$("#multiple-value-config").hide();
	$("#range-value-config").hide();
	
	var groupId = $("#groupId").val();
	
	if (groupId) {
	
		//$("#segment-value-form").validator('destroy');
	
		$.ajax({
		      
			type: "POST",
	     	url: "loadSegmentCode",
	        data:  {"groupId": groupId},
	        success: function (data) {   
	            
	            if (data.code == 200) {
					
					if (data.segmentCode.valueCapture == "SINGLE") {
						//$("#single-value-config").show();
					} else if (data.segmentCode.valueCapture == "MULTIPLE") {
						$("#multiple-value-config").show();
					} else if (data.segmentCode.valueCapture == "RANGE") {
						$("#range-value-config").show();
					}
					
					$("#value-capture").html( "[ " + data.segmentCode.valueCapture + " ]" );
					
					$("#segment-value-form").validator('update');
					
				} else {
					//showAlert ("error", data.message);
				}  
				    	
	        }
	        
		});
	}
	
}


$('#customFieldName').keyup(function(){	
		if($(this).val()!=""){		
		$('#customFieldName_error').hide();
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
	$('#customFieldName_error').html("Please Enter Economic Metric Name");
	$('#customFieldName_error').show();
		valid=false;
	}
	else if($('#customFieldName').val().length>250){
	$('#customFieldName_error').html("Please Enter less than 250 Characters");
	$('#customFieldName_error').show();
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
