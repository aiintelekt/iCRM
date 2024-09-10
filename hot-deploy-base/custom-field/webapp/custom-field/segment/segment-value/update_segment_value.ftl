<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "EditSegmentValue") />  
<div class="row">
    <div id="main" role="main" class="pb-0">
        <#assign extra='<a href="findSegmentValue" class="btn btn-xs btn-primary m5 tooltips" title="Cancel" >Cancel</a>'/>
        <#if customFieldGroup.groupId?has_content>
        <#assign extra=extra+'<a href="editSegmentCode?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="Back to ${customFieldGroup.groupName!}" >Back</a>'/>
        </#if>
        <#if customField.isEnabled?has_content && customField.isEnabled == "Y" >
        <#assign extra=extra+'<a href="#" class="btn btn-xs btn-primary m5 tooltips active-btn" title="Disable Segment Value" data-isEnabled="N">Disable</a>'/>
        <#else>
        <#assign extra=extra+'<a href="#" class="btn btn-xs btn-primary m5 tooltips active-btn" title="Enable Segment Value" data-isEnabled="Y">Enable</a>'/>
        </#if>
        <#--<#assign extra=extra+'<a href="segmentValueCustomer?customFieldId=${customField.customFieldId}&groupId=${customFieldGroup.groupId!}" class="btn btn-xs btn-primary m5 tooltips <#if !customFieldGroup.groupId?has_content>disabled</#if>" title="${uiLabelMap.ManageCustomers?has_content}" >${uiLabelMap.ManageCustomers?has_content}</a>'/>-->
        <#assign extra=extra+'<a href="segmentValueCustomer?customFieldId=${customField.customFieldId}&groupId=${customFieldGroup.groupId!}" class="btn btn-xs btn-primary m5 tooltips '/>
            <#if !customFieldGroup.groupId?has_content>
            <#assign extra=extra+'disabled' />
            </#if>
            <#assign extra=extra+'" title="${uiLabelMap.ManageCustomers!}" >${uiLabelMap.ManageCustomers!}</a>'/>
        <div  class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <@sectionFrameHeaderTab extra=extra title="${uiLabelMap.Update} ${uiLabelMap.SegmentValue}" tabId="EditSegmentValue"> <#if customFieldGroup.groupName?has_content>for [ ${customFieldGroup.groupName} <i class="fa fa-arrow-right" aria-hidden="true"></i> ${customField.customFieldName} ]</#if></@sectionFrameHeaderTab>
            <div class="col-md-6 col-sm-6" style="padding:0px">
                <div class="portlet-body form">
                    <form id="segment-value-form" role="form" class="form-horizontal" action="<@ofbizUrl>updateSegmentValue</@ofbizUrl>" encType="multipart/form-data" method="post" >
                        <div class="form-body">
                            <#-- <@dropdownInput 
                            id="roleTypeId"
                            label=uiLabelMap.roleTypeId
                            options=roleTypeList
                            required=true
                            value=customField.roleTypeId
                            allowEmpty=false
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
		                            label=uiLabelMap.segmentCode
		                            options=groupList
		                            required=false
		                            value=customField.groupId
		                            allowEmpty=true
		                            placeholder=uiLabelMap.pleaseSelect
		                            />
                            </#if>
                            <@inputRow 
	                            id="customFieldId"
	                            label=uiLabelMap.segmentValueId
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
	                            allowEmpty=true
	                            placeholder=uiLabelMap.pleaseSelect
	                            />		
                            <#if groupId?has_content>
                            <#-- 
                            <div class="page-header">
                                <h2>${uiLabelMap.Configuration} ${uiLabelMap.SegmentValue}</h2>
                            </div>
                            -->
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
	                                dataError="Please Enter Data"
	                                />		
	                                </#if>	
                            </div>
                            <@dropdownCell 
							id="isDefault"
							label=uiLabelMap.isDefault
							options=yesNoOptions
							value=customField.isDefault
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
	                            dataError="Please Enter Digits Only"
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
                        	<#assign cancelActionUrl = "viewSegmentValueForGroup"/>
                        <#else>
                        	<#assign cancelActionUrl = "findSegmentValue"/>
                        </#if>			
                        <div class="form-group row"">
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
${screens.render("component://custom-field/webapp/widget/custom-field/screens/segment/SegmentScreens.xml#SegmentValueCampaignConfig")}
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
    
    $('.active-btn').click(function () {
    
    	resetDefaultEvents();
    
    	//alert($(this).attr("data-isEnabled"));
    	var actionType = $(this).attr("data-isEnabled");
    	var actionUrl = "";
    	if (actionType == "Y") {
    		actionUrl = "enabledSegmemntCodeValue";
    	} else if (actionType == "N") {
    		actionUrl = "disabledSegmemntCodeValue";
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
    					$('.active-btn').attr("title", "Disable Segment Code");
    					$('.active-btn').attr("data-original-title", "Disable Segment Code");
    					$('.active-btn').attr("data-isEnabled", "N");
    					$('#isEnabled').val("N");
    				} else {
    					$('.active-btn').html("Enable");
    					$('.active-btn').attr("title", "Enable Segment Code");
    					$('.active-btn').attr("data-original-title", "Enable Segment Code");
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
    	
    	$("#single-value-config").hide();
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
    						$("#single-value-config").show();
    						$('#valueData').attr('required','required');
    						$('#valueMax').removeAttr('required');
    						$('#valueMin').removeAttr('required');
    					} else if (data.segmentCode.valueCapture == "MULTIPLE") {
    						$('#valueData').removeAttr('required');
    						$('#valueMax').removeAttr('required');
    						$('#valueMin').removeAttr('required');
    						//$("#multiple-value-config").show();
    					} else if (data.segmentCode.valueCapture == "RANGE") {
    						$("#range-value-config").show();
    						$('#valueData').removeAttr('required');
    						$('#valueMax').attr('required','required');
    						$('#valueMin').attr('required','required');
    					}
    					
    					$("#value-capture").val( "[ " + data.segmentCode.valueCapture + " ]" );
    					$("#valueCaptureId").val(data.segmentCode.valueCapture);
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
    $('#valueData').keyup(function(){	
    		if($(this).val()!=""){
    		
    		$('#valueData_error').hide();
    	}
    });
    
    function onSubmitValidate() {	
    	var valid=true;	
    	if($('#customFieldName').val()==""){
    	$('#customFieldName_error').html("Please Enter Segment Value Name");
    	$('#customFieldName_error').show();
    		valid=false;
    	}
    	else if($('#customFieldName').val().length>250){
    	$('#customFieldName_error').html("Please Enter less than 250 Characters");
    	$('#customFieldName_error').show();
    		valid=false;
    	}
    	if($('#valueData').val()=="" && $("#valueCaptureId").val() == "SINGLE"){
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