<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<div class="row">
   <div id="main" role="main">      
   <#--     <div class="float-right">  --> 
         <#if customFieldGroup.groupId?has_content>
         <#-- <a href="findSegmentValue" class="btn btn-xs btn-primary m5 tooltips" title="Cancel" >Cancel</a> -->
         <#assign extra='<a href="viewEconomicValueForGroup?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="Back to ${customFieldGroup.groupName!}" >Back</a>'/>
         </#if>
   <#--     </div>  --> 
   <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      <@sectionFrameHeader extra=extra?if_exists title="${uiLabelMap.Create} ${uiLabelMap.EconomicValue}"><#if customFieldGroup.groupName?has_content>for [ ${customFieldGroup.groupName} ]</#if></@sectionFrameHeader>
     
      <#if customFieldGroup.groupId?exists && customFieldGroup.groupId?has_content>
      <ul class="nav nav-tabs">
         <#-- 
         <li class="nav-item"><a data-toggle="tab" href="#createEconomicMetric"
            class="nav-link active">${uiLabelMap.Create} ${uiLabelMap.EconomicValue}</a></li>
         -->      
         <li class="nav-item"><a data-toggle="tab" href="#createEconomicMetric"
            class="nav-link active show">Create Economic Metric</a></li>
         <#--  
         <li class="nav-item"><a data-toggle="tab" href="#uploadEconomicMetric"
            class="nav-link">Upload Customers</a></li>
         -->   
     
      </ul>
    </#if>
    
      <div class="">
             
         <div id="createEconomicMetric" class="tab-pane fade show active">
            <#--  <@pageSectionHeader title="Create Economic Metric " />  -->
            <#-- 
            <div class="page-header">
               <h2 class="float-left">${uiLabelMap.Create} ${uiLabelMap.EconomicValue}</h2>
            </div>
            -->
            <#if customFieldGroup.groupId?has_content>
            <#assign actionUrl = "createEconomicValue"/>
            <#else>
            <#assign actionUrl = "createEconomicValueIndividual"/>
            </#if>
            <div class="col-md-6 col-sm-6" style="padding:0px">
               <div class="portlet-body form">
                  <form id="economic-value-form" role="form" class="form-horizontal" action="<@ofbizUrl>${actionUrl}</@ofbizUrl>" encType="multipart/form-data" method="post" >
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
                        label=uiLabelMap.economicMetric
                        options=groupList
                        required=true
                        value=customField.groupId
                        allowEmpty=true
                        dataLiveSearch=true
                        placeholder=uiLabelMap.pleaseSelect
                        dataError="Please Select Economic Code"
                        />
                        </#if>
                        <@inputRow 
                        id="customFieldId"
                        label=uiLabelMap.economicValueId
                        placeholder=uiLabelMap.economicValueId
                        value=customField.customFieldId
                        required=true
                        maxlength=250
                        dataError="Please Enter Economic Code Id"
                        />		
                        <@inputRow 
                        id="customFieldName"
                        label=uiLabelMap.economicValueName
                        placeholder=uiLabelMap.economicValueName
                        value=customField.customFieldName
                        required=true
                        maxlength=255
                        dataError="Please Enter Economic Code Name"
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
                        required=fales
                        value=customField.isEnabled
                        allowEmpty=true
                        placeholder=uiLabelMap.pleaseSelect
                        />	
                        <#-- 
                        <div class="page-header">
                           <h2>${uiLabelMap.Configuration} ${uiLabelMap.SegmentValue} <span id="value-capture"></span></h2>
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
                           required=false
                           type="number"
                           maxlength=20
                           />	
                           <@inputRow 
                           id="valueMax"
                           label=uiLabelMap.valueMax
                           placeholder=uiLabelMap.valueMax
                           value=valueConfig.valueMax
                           required=false
                           type="number"
                           maxlength=20
                           />
                        </div>
                        <#--			
                        <div id="single-value-config" style="display: none">		
                           <@generalInput 
                           id="valueData"
                           label=uiLabelMap.valueData
                           placeholder=uiLabelMap.valueData
                           value=valueConfig.valueData
                           required=true
                           />	
                        </div>
                        -->
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
					        value=customField.sequenceNumber!
					        desValue=""
					        id="productPromoCodeGroupId"  
					        name="productPromoCodeGroupId" 
					        placeholder="Coupon Campaign"
					        />		
                     </div>
                     <div class="form-group row">
                        <div class="offset-sm-4 col-sm-7 p-2">
                           <@submit
                           label=uiLabelMap.submit onclick="javascript:return onSubmitValidate(this);"/>
                           <@reset label=uiLabelMap.Clear onclick="javascript:clearEconomicValue();"/>
                        </div>
                     </div>
                  </form>
               </div>
            </div>
         </div>
            <#if customFieldGroup.groupId?exists && customFieldGroup.groupId?has_content>
              <div id="uploadEconomicMetric" class="tab-pane fade">
            ${screens.render("component://custom-field/webapp/widget/custom-field/screens/economic/EconomicScreens.xml#uploadEconomicMetric")}
             </div>
            </#if>
         
         </div>    
        </div> <#--  tab content close -->
   </div> <#--  main close -->
</div>
</div> <#--  row close -->

<@promoCampaignPicker 
	instanceId="promoCampaignPicker"
	/>
<script>
   jQuery(document).ready(function() { 
   
   $('#economic-value-form').validator();
   
   loadEconomicMetric();
   
   $('#groupId').on('change', function(){
   
   	loadEconomicMetric();
   
   });
   
   });
   
   function loadEconomicMetric() {
   	
   	//$("#single-value-config").hide();
   	$("#range-value-config").hide();
   	
   	var groupId = $("#groupId").val();
   	
   	if (groupId) {
   	
   		//$("#economic-value-form").validator('destroy');
   	
   		$.ajax({
   		      
   			type: "POST",
   	     	url: "loadSegmentCode",
   	        data:  {"groupId": groupId},
   	        success: function (data) {   
   	            
   	            if (data.code == 200) {
   					
   					if (data.segmentCode.valueCapture == "SINGLE") {
   						//$("#single-value-config").show();
   					} else if (data.segmentCode.valueCapture == "MULTIPLE") {
   						
   					} else if (data.segmentCode.valueCapture == "RANGE") {
   						$("#range-value-config").show();
   					}
   					
   					$("#valueCapture").html( "[ " + data.segmentCode.valueCapture + " ]" );
   					
   					$("#economic-value-form").validator('update');
   					
   				} else {
   					//showAlert ("error", data.message);
   				}  
   				    	
   	        }
   	        
   		});
   	}
   	
   	
   	
   }
   jQuery(document).ready(function() { 
       <#if !activeTab?has_content>
           <#assign activeTab = requestParameters.activeTab!>
       </#if>
       
       <#if activeTab?has_content && activeTab == "createEconomicMetric">
           $('.nav-tabs a[href="#createEconomicMetric"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "uploadEconomicMetric">
           $('.nav-tabs a[href="#uploadEconomicMetric"]').tab('show'); 
       <#else>
           $('.nav-tabs a[href="#createEconomicMetric"]').tab('show'); 
       </#if>
   });
   
   
   
   $('#groupId').keyup(function(){	
   		if($(this).val()!=""){
   		
   		$('#groupId_error').hide();
   	}
   });
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
   $('#sequenceNumber').keyup(function(){	
   		if($(this).val()!=""){		
   		$('#sequenceNumber_error').hide();
   		}
   	});
   function clearEconomicValue(){
   $('#groupId_error').hide();
   $('#customFieldId_error').hide();
   $('#customFieldName_error').hide();	
   }
   function onSubmitValidate() {	
   	var valid=true;
   	if($('#groupId').val()==""){
   	$('#groupId_error').html("Please Select Economic Code");
   	$('#groupId_error').show();
   		valid=false;
   	}
   	
   	if($('#customFieldId').val()==""){
   	$('#customFieldId_error').html("Please Enter Economic Code Id");
   	$('#customFieldId_error').show();
   		valid=false;
   	}
   	else if($('#customFieldId').val().length>60){
   	$('#customFieldId_error').html("Please Enter less than 60 Characters");
   	$('#customFieldId_error').show();
   		valid=false;
   	}
   	if($('#customFieldName').val()==""){
   	$('#customFieldName_error').html("Please Enter Economic Code Name");
   	$('#customFieldName_error').show();
   		valid=false;
   	}
   	else if($('#customFieldName').val().length>60){
   	$('#customFieldName_error').html("Please Enter less than 60 Characters");
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
