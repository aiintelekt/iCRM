<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
    <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
    <@sectionFrameHeader title="${uiLabelMap.Create} ${uiLabelMap.EconomicMetric}" />
	<div class="col-md-6 col-sm-6" style="padding:0px">		
		<div class="portlet-body form">
			<form id="mainFrom" role="form" class="form-horizontal" action="<@ofbizUrl>createEconomicMetric</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
				
			<div class="form-body">
			
			<@inputRow 
				id="groupId"
				label=uiLabelMap.economicMetricId
				placeholder=uiLabelMap.economicMetricId
				value=customFieldGroup.groupId
				required=true
				maxlength=250
				dataError="Please Enter Economic Code"
				/>
			
			<@inputRow 
				id="groupName"
				label=uiLabelMap.economicMetricName
				placeholder=uiLabelMap.economicMetricName
				value=customFieldGroup.groupName
				required=true
				maxlength=255
				dataError="Please Enter Economic Code Name" 
				/>
			
			<@dropdownCell 
				id="groupingCode"
				label=uiLabelMap.groupingCode
				options=groupingCodeList
				required=false
				value=customFieldGroup.groupingCode
				allowEmpty=true
				dataLiveSearch=true
				placeholder=uiLabelMap.pleaseSelect
				/>														
			
			<#-- 
			<@generalInput 
				id="serviceName"
				label=uiLabelMap.serviceName
				placeholder=uiLabelMap.serviceName
				value=customFieldGroup.serviceName
				required=false
				maxlength=255
				/>
			 -->			 	
			<@dropdownCell
				id="serviceTypeId"
				label=uiLabelMap.serviceTypeId
				options=serviceTypeList
				required=fales
				value=customFieldGroup.serviceTypeId
				allowEmpty=true
				placeholder=uiLabelMap.pleaseSelect
				/>	
			
			<@dropdownCell
				id="serviceConfigId"
				label=uiLabelMap.serviceName
				allowEmpty=true
				placeholder=uiLabelMap.pleaseSelect
				/>	
			
			<#--  
			<div class="form-group row">
			   <label class="col-sm-4 col-form-label " for="serviceName">${uiLabelMap.serviceName!}</label>
			   <div class="col-sm-7">
			      <div class="input-icon ">
			         <select class="custom-select ui dropdown search form-control input-sm" id="serviceConfigId" name="serviceConfigId">
				      	<option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
				      </select>
			         <div class="help-block with-errors" id="serviceConfigId_error"></div>
			         <i class=""></i>
			      </div>
			   </div>
			</div>	
			-->
						
			<#--	
			<@dropdownInput 
				id="microServiceConfigId"
				label=uiLabelMap.microService
				options=microServiceList
				required=false
				value=customFieldGroup.microServiceConfigId
				allowEmpty=true
				/>																																																																																															
			
			<@dropdownInput 
				id="webhookConfigId"
				label=uiLabelMap.webhook
				options=webhookList
				required=false
				value=customFieldGroup.webhookConfigId
				allowEmpty=true
				/>							
			 -->			
			
			<#-- <@dropdownInput 
				id="historicalCapture"
				label=uiLabelMap.historicalCapture
				options=yesNoOptions
				required=fales
				value=customFieldGroup.historicalCapture
				allowEmpty=true
				/> -->																																																																							
			
			<@dropdownCell 
				id="valueCapture"
				label=uiLabelMap.valueCapture
				options=valueCaptureList
				required=true
				value=customFieldGroup.valueCapture
				allowEmpty=true
				placeholder=uiLabelMap.pleaseSelect
				dataError="Please Select Value Capture" 
				/>			
				
			<@dropdownCell 
				id="isCampaignUse"
				label=uiLabelMap.isCampaignUse
				options=yesNoOptions
				required=fales
				value=customFieldGroup.isCampaignUse
				allowEmpty=true
				placeholder=uiLabelMap.pleaseSelect
				/>																																																																																																																																																																																																																																																																																																																																																																																																																						
			
			<#-- <@dropdownInput 
				id="classType"
				label=uiLabelMap.classType
				options=classTypeList
				required=fales
				value=customFieldGroup.classType
				allowEmpty=true
				/> -->																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																			
			
			<@dropdownCell 
				id="type"
				label=uiLabelMap.type
				options=typeList
				required=fales
				value=customFieldGroup.type
				allowEmpty=true
				placeholder=uiLabelMap.pleaseSelect
				/>
				
			<@dropdownCell 
				id="isUseDynamicEntity"
				label=uiLabelMap.isUseDynamicEntity
				options=yesNoOptions
				required=fales
				value=customFieldGroup.isUseDynamicEntity
				allowEmpty=true
				placeholder=uiLabelMap.pleaseSelect
				/>	
			<@dropdownCell 
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
				
			<@inputRow 
				id="sequence"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=customFieldGroup.sequence
				dataError="Please Enter Digits Only"
				maxlength=20
				type="number"
				/>			
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																										
			</div>
			
			<div class="form-group row">
                    <div class="offset-sm-4 col-sm-7 p-2">
                       <@submit label="${uiLabelMap.submit!}"/>
                    	<@reset
                    		label="${uiLabelMap.Clear!}"/>
                    </div>
                </div>
			
		</form>			
							
		</div>
	</div>		
	</div>
</div>
</div>

<script>

jQuery(document).ready(function() {

	loadServiceList();

	$("#serviceTypeId").change(function() {
		loadServiceList()
	});

});

function loadServiceList() {
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	var serviceNameOptions = '<option value="" data-content="' + nonSelectContent + '" selected="">Please Select</option>';

	if ($("#serviceTypeId").val() == "INTERNAL" || $("#serviceTypeId").val() == "WEBHOOK_PUSH") {

		$.ajax({

			type: "POST",
			url: "getCustomFieldGroupServices",
			data: {
				"serviceTypeId": $("#serviceTypeId").val()
			},
			async: false,
			success: function(data) {

				if (data.code == 200) {

					for (var i = 0; i < data.services.length; i++) {
						var service = data.services[i];
						serviceNameOptions += '<option value="' + service.serviceConfigId + '">' + service.description + '</option>';
					}

				}

			}

		});

	}

	$("#serviceConfigId").html(serviceNameOptions);

	$('#serviceConfigId').dropdown('refresh');
}

</script>
