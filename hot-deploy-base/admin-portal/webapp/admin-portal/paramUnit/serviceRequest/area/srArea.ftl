<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
	
      <div class="row">
        <div id="main" role="main">
          <#assign extra='<a href="activeSrArea" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         
<form action ="<@ofbizUrl>createServiceRequestArea?externalLoginKey=${requestAttributes.externalLoginKey!}</@ofbizUrl>" method="post" id="createSrCategoryForm" data-toggle="validator">

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>

          <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
         
         <@sectionFrameHeader title="${uiLabelMap.CreateSrCategory!}" extra=extra />
          
          	<@dynaScreen 
				instanceId="PARAM_SR_AREA"
				modeOfAction="CREATE"
				/>
			                                
          <div class="form-group offset-2">
            <div class="text-left ml-1 pad-10">
            
            <#--<@fromCommonAction showCancelBtn=false showClearBtn=true/>-->
            
			             
              <@formButton
                     btn1type="button"
                     btn1label="${uiLabelMap.Save}"
                     btn1onclick="return srCategoryValidation();"
                     btn2=true
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                     btn2id="sr-category-reset-btn"
                   /> 
            </div>
          </div>
          
          </div>
          </form>
        </div>
      </div>
<script>
	$('#srArea').click(function () {
		$('#srArea_error').html("");
	});
	$('#typeId').change(function () {
		$('#typeId_error').html("");
	});
	$('#status').change(function () {
		$('#status_error').html("");
	});
	$(document).ready(function() {
		$('#sr-category-reset-btn').click(function() {
			$("#sequenceNumber").val('');
		});
	});
	function srCategoryValidation() {
		var typeId = $('#typeId').val();
		var srCategory = $('#srArea').val();
		var status = $('#status').val();
		if(typeId != "" && srCategory != "" && status != ""){
			$.ajax({
				type: "POST",
				url: "validateSrCategory",
				async: true,
				data: {
					"typeId": typeId,
					"category": srCategory
				},
				success: function(data) {
					if(data.isAvailable == "Y"){
						$('#srArea_error').html("Entered SR Category name already exists!!");
					}else{
						$('#createSrCategoryForm').submit();
					}
				}
			});
		return false;
		}else{
			if(typeId == ""){
				$('#typeId_error').html("Please select an item in the list.");
			}
			if(srCategory == ""){
				$('#srArea_error').html("Please fill out this field");
			}
			if(status == ""){
				$('#status_error').html("Please select an item in the list.");
			}
		}
	}
</script>
