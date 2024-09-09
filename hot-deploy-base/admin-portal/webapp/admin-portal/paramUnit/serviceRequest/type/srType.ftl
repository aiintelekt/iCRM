<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
	
<div class="row">
	<div id="main" role="main">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<#assign extra='<a href="srType" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
		<@sectionFrameHeaderTab title="${uiLabelMap.CreateSrType!}"  />         
		<form action ="<@ofbizUrl>srTypeCreation</@ofbizUrl>" method="post" id="createSrTypeForm" data-toggle="validator">
			<@dynaScreen 
				instanceId="PARAM_SR_TYPE"
				modeOfAction="CREATE"
				/>		 		 		 		 		 
			<div class="form-group offset-2">
				<div class="text-left ml-1 p-2">	      
			      <@formButton
				     btn1type="button"
				     btn1label="${uiLabelMap.Save}"
				     btn1onclick="return srTypeValidation();"
				     btn2=true
				     btn2type="reset" 
				     btn2label="${uiLabelMap.Clear}"
				     btn2id="sr-type-reset-btn"
				   /> 	
				</div>
			</div>		  
		</form>
	</div>
</div>
</div>
<script>
	$('#srType').click(function () {
		$('#srType_error').html("");
	});
	$('#sequenceNumber').click(function () {
		$('#sequenceNumber_error').html("");
	});
	$('#status').change(function () {
		$('#status_error').html("");
	});
	$(document).ready(function() {
		$('#sr-type-reset-btn').click(function() {
			$("#sequenceNumber").val('');
		});
	});
		function srTypeValidation() {
		var srType = $('#srType').val();
		var status = $('#status').val();
		var sequenceNumber = $('#sequenceNumber').val();
		if(srType != "" && status != "" && sequenceNumber != ""){
			$.ajax({
				type: "POST",
				url: "validateSrCategory",
				async: true,
				data: {
					"srType": srType
				},
				success: function(data) {
					if(data.isAvailable == "Y"){
						$('#srType_error').html("Entered SR Type name already exists!!");
					}else{
						$('#createSrTypeForm').submit();
					}
				}
			});
		return false;
		}else{
			if(srType == ""){
				$('#srType_error').html("Please fill out this field");
			}
			if(status == ""){
				$('#status_error').html("Please select an item in the list.");
			}
			if(sequenceNumber == ""){
				$('#sequenceNumber_error').html("Please fill out this field");
			}
		}
	}
</script>