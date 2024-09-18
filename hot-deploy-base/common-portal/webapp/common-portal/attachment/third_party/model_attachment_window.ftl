<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign custRequestIdd= request.getParameter("srNumber")! />

<#macro createAttachmentModal instanceId>

<input type="hidden" name="globalFileSize" id="globalFileSize" value="${globalFileSize!}" />

<div id="${instanceId!}" class="modal fade">
	<div class="modal-dialog modal-lg">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Add Attachment</h4>
				<button type="button" class="close" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body poplabel-left" id="attachment">
				<form name="add-attachment-thirdpty-form1" id="add-attachment-thirdpty-form1" action="" enctype="multipart/form-data" method="post">
					<div class="row p-1">
						<div class="col-md-12 col-lg-12 col-sm-12 ">
							<input type="hidden" id="path" name="path" value="${path!}">
							<input type="hidden" id="activeTab" name="activeTab" value="thirdpty-attachment">
							<input type="hidden" id="partyId" name="partyId" value="${partyId?if_exists}" />
							<input type="hidden" id="custRequestId1" name="custRequestId1" value="${custRequestIdd!}">
							<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
							<input type="hidden" id="domainEntityType1" name="domainEntityType" value="${domainEntityType!}">
							<input type="hidden" id="domainEntityId" name="domainEntityId" value="${domainEntityId!}">
							<input type="hidden" id="attachmentType" name="attachmentType" value="PUBLIC">
							<@dropdownCell id="classificationEnumId" name="classificationEnumId" placeholder=uiLabelMap.Classification options=contentClassificationList! label="Classification" value="${requestParameters.classificationEnumId?if_exists}" allowEmpty=false required=true />
							<@inputArea id="attachmentDescription" label="Attachment Description" rows="3" placeholder="Description" value="" required=false maxlength="255" />
							<div id="upload">
								<@inputRow id="uploadFile_thirdpty" name="uploadFile" type="file" label="Upload" required=true /> 
							</div>
								
							<@radioInputCell 
							id="publicOrPrivateAtt" 
							name="publicOrPrivateAtt" 
							label="Attachment Type" 
							options=attachmentTypes 
							value="PUBLIC" 
							required=true
							/>
							
							<@inputRow 
							id="invoiceAmount"
							label="Invoice Amount"
							placeholder="Invoice Amount"
							type="number"
							pattern="(\\d*)"
							required=true
							/>
							  
						</div>
					</div>
					<div class="modal-footer">
						<@button class="btn btn-sm btn-primary navbar-dark" id="add-attachment-thirdpty-form-submit" label="${uiLabelMap.Save}" />
						<@reset label="${uiLabelMap.Reset}" /> 
					</div>
				</form>
			</div>
		</div>
	</div>
</div>

<script>
$(document).ready(function() {
	$("#${instanceId!}").on("show.bs.modal", function(e) {
		$('.clear').click();
		document.getElementById('publicOrPrivateAtt').checked = true;
	    $("#add-attachment-thirdpty-form1 #uploadFile_thirdpty").val("");
		$("#add-attachment-thirdpty-form1 #attachmentDescription").val("");	
		$('#add-attachment-thirdpty-form1')[0].reset();
   	});
   	
	$('input[type=radio][name=publicOrPrivateAtt]').change(function() {
		$("#add-attachment-thirdpty-form1 input[name=attachmentType]").val(this.value);
	});
	
});

</script> 

</#macro>

<#macro imgPreviewModal instanceId>
<style>
	.image-model{
		 max-width: fit-content !important;
		 display: flex !important;
		 justify-content: center;
		 align-items: center;
	}
	.modal-content{
		height: auto;
	    overflow-x: hidden !important;
	    overflow-y: hidden !important;
	}
</style>
<div id="img-preview" class="modal fade" role="dialog" aria-hidden="true" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog image-model" id="image-model">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Image Preview</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<span class="text-center">
            		<img class="" id="preview-image" name="preview-image" width="400" height="500" src="" ></img>
            	</span>
            </div>
            <div class="modal-footer">
                <button type="sbmit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
            </div>
        </div>
    </div>
</div>
</#macro>


 