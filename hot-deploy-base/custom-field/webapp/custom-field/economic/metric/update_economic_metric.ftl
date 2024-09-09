<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.dataTables.min.js"></script>
<div class="row">

    <div id="main" role="main">
		<#-- <a href="/cf-resource/data/dynamic-entitymodel.sql;jsessionid=${session.id}" class="btn btn-xs btn-primary m5 tooltips" title="Download Dynamic Entity Creation Script" download>Download SQL</a> -->
		<#assign extra='<a href="viewEconomicValueForGroup?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="View Economic Metrics for ${customFieldGroup.groupName!}" >View Economic Metrics</a>'/>
		<#assign extra=extra+'<a href="economicValueForGroup?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="Add Economic Metrics for ${customFieldGroup.groupName!}" >Add Economic Metrics</a>'/>
		<#if customFieldGroup.isActive?has_content && customFieldGroup.isActive == "Y" >
			<#assign extra=extra+'<a href="#" class="btn btn-xs btn-primary tooltips active-btn" title="Inactive Economic Code" data-isActive="N">Inactive</a>'/>
		<#else>
			<#assign extra=extra+'<a href="#" class="btn btn-xs btn-primary tooltips active-btn" title="Active Economic Code" data-isActive="Y">Active</a>'/>
		</#if>
		 

<div class="col-lg-12 col-md-12 col-sm-12 dash-panel"> 
 <@sectionFrameHeaderTab extra=extra title="${uiLabelMap.Update} ${uiLabelMap.EconomicMetric} [ ${customFieldGroup.groupName} ]" tabId="EditEconomicMetric"/>
<div class="row padding-r" >
	<div class="col-md-7 col-sm-12"> 			
		<div class="portlet-body form">
			<form id="mainFrom" role="form" class="form-horizontal" action="<@ofbizUrl>updateEconomicMetric</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
						
			<div class="form-body">
			
			<@inputRow 
				id="groupId"
				label=uiLabelMap.economicMetricId
				value=customFieldGroup.groupId
				readonly=true
				/>
			
			<@inputRow 
				id="groupName"
				label=uiLabelMap.economicMetricName
				placeholder=uiLabelMap.economicMetricName
				value=customFieldGroup.groupName
				required=true
				maxlength=255
				dataError="Please Enter Segment Code Name"
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
			
			<input type="hidden" id="configuredServiceId" value="${customFieldGroup.serviceConfigId!}">
			<#--<div class="form-group row">
			   <label class="col-sm-4 col-form-label " for="serviceName">${uiLabelMap.serviceName!}</label>
			   <div class="col-sm-7">
			      <div class="input-icon ">
			         <select class="custom-select ui dropdown search form-control input-sm" id="serviceConfigId" name="serviceConfigId" >
				      	<option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
				      </select>
			         <div class="help-block with-errors" id="serviceConfigId_error"></div>
			         <i class=""></i>
			      </div>
			   </div>
			</div> -->			
			<@dropdownCell
				id="serviceConfigId"
				label=uiLabelMap.serviceName			
				allowEmpty=true
				placeholder=uiLabelMap.pleaseSelect
				/>	
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
			 <#-- 
			 <@dropdownInput 
				id="historicalCapture"
				label=uiLabelMap.historicalCapture
				options=yesNoOptions
				required=fales
				value=customFieldGroup.historicalCapture
				allowEmpty=true
				disabled=true
				/>																																																																							
			 -->
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
						
			
			<@inputRow 
				id="sequence"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=customFieldGroup.sequence				
				dataError="Please Enter Digits only"
				type="number"
				/>																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																									
			</div>
			<div class="form-group row">
                    <div class="offset-sm-4 col-sm-7">
                      <@submit label="Submit"/>
					  <@cancel label="Cancel"/>
                    </div>
                </div>			
		</form>			
							
		</div>
			
	</div>
	
			<div class="col-md-5 col-sm-12">	
				<@pageSectionHeader title="${uiLabelMap.AssignRole!}"/>		
				<div class="portlet-body form">
					<form id="createRoleConfigForm" role="form" class="form-horizontal" action="<@ofbizUrl>createRoleConfig</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
						<input type="hidden" name="groupId" value="${customFieldGroup.groupId!}" />
						<input type="hidden" name="isCompleteReset" value="N" />
									
						<div class="form-body">
						
						<@dropdownCell
							id="roleTypeId"
							label=uiLabelMap.roleTypeId
							options=roleTypeList
							required=true
							value=roleConfig?if_exists
							allowEmpty=true
							dataLiveSearch=true
							/>	
						</div>
						
						<div class="clearfix"> </div>
						<div class="col-md-12 col-sm-12">
							<div class="form-group row">
								<div class="offset-sm-4 col-sm-1">
									<@button id="roleConfigBtn" label="Assign"/>
								</div>
								<#-- <div class="col-sm-6 mt-2 ml-4">
									<div class="alert-danger small">Assign new Role will be replace with existing Role!!!</div>
								</div> -->
							</div>
						</div>
					</form>					
				</div>
				
				<div class="clearfix"></div>
				<h2 class="float-left">${uiLabelMap.List} ${uiLabelMap.Role}</h2>
				<div class="clearfix"></div>
				<div class="page-header">
				</div>
				<div class="table-responsive">
					<table id="role-config-list" class="table table-striped">
						<thead>
							<tr>
								<th>${uiLabelMap.roleTypeId!}</th>
								<#-- <th>${uiLabelMap.sequence!}</th> -->
								<th class="text-center">Action</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				
				<div class="clearfix"></div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>

jQuery(document).ready(function() {

	$('.active-btn').click(function() {

		resetDefaultEvents();

		//alert($(this).attr("data-isActive"));
		var actionType = $(this).attr("data-isActive");
		var actionUrl = "";
		if (actionType == "Y") {
			actionUrl = "activateSegmemntCode";
		} else if (actionType == "N") {
			actionUrl = "inActivateSegmemntCode";
		}

		// title="In Active Segment Code" data-isActive="N"

		$.ajax({

			type: "POST",
			url: actionUrl,
			data: {
				"groupId": "${customFieldGroup.groupId!}"
			},
			success: function(data) {

				if (data.code == 200) {
					showAlert("success", data.message);

					if (actionType == "Y") {
						$('.active-btn').html("Inactive");
						$('.active-btn').attr("title", "Inactive Economic Code");
						$('.active-btn').attr("data-original-title", "Inactive Economic Code");
						$('.active-btn').attr("data-isActive", "N");
					} else {
						$('.active-btn').html("Active");
						$('.active-btn').attr("title", "Active Economic Code");
						$('.active-btn').attr("data-original-title", "Active Economic Code");
						$('.active-btn').attr("data-isActive", "Y");
					}

				} else {
					showAlert("error", returnedData.message);
				}

			}

		});

	});

	$('#roleConfigBtn').click(function() {

		if ($('#roleTypeId').val()) {
			$.post('createRoleConfig', $('#createRoleConfigForm').serialize(), function(returnedData) {

				if (returnedData.code == 200) {

					showAlert("success", returnedData.message)

					$('#createRoleConfigForm')[0].reset();
					findRoleConfigs();

				} else {
					showAlert("error", returnedData.message)
				}

			});
		} else {
			showAlert("error", "fill up all the values");
		}

	});

	if ($("#serviceTypeId").val()) {
		loadServiceList();
	}

	$("#serviceTypeId").change(function() {

		loadServiceList();

	});

	findRoleConfigs();

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
	console.log("serviceNameOptions", serviceNameOptions);
	$("#serviceConfigId").html(serviceNameOptions);

	if ($("#configuredServiceId").val() != "") {
		$("#serviceConfigId").val($("#configuredServiceId").val());
	}

	$('#serviceConfigId').dropdown('refresh');
}

function findRoleConfigs() {

	$('#role-config-list').DataTable({
		"processing": true,
		"destroy": true,
		"ajax": {
			"url": "/custom-field/control/getRoleConfigs?groupId=${customFieldGroup.groupId!}",
			"type": "POST"
		},
		"pageLength": 10,
		"order": [
			[1, "asc"]
		],
		"columns": [{
				"data": "roleType"
			},
			//{ "data": "sequenceNumber" },
			{
				"data": "roleTypeId",
				"render": function(data, type, row, meta) {
					if (type === 'display') {
						data =
							'<div class="text-center">' +
							'<a class="btn btn-xs btn-danger tooltips remove-role-config" href="javascript:removeRoleConfig(' + row.roleConfigId + ')" data-original-title="Remove" data-config-id="' + row.roleConfigId + '"><i class="fa fa-times red"></i></a>' +
							'</div>';
					}
					return data;
				}
			},
		],
		"fnDrawCallback": function(oSettings) {
			resetDefaultEvents();
		}
	});

}

function removeRoleConfig(roleConfigId) {

	//alert(roleConfigId);

	$.ajax({

		type: "POST",
		url: "removeRoleConfig",
		data: {
			"roleConfigId": roleConfigId
		},
		success: function(data) {

			findRoleConfigs();

		}

	});

}

</script>
