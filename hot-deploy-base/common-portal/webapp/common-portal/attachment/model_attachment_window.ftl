<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign salesOppIdd= request.getParameter("salesOpportunityId")! />
<#if custRequestId?has_content>
<#assign custRequestIdd = custRequestId!>
<#else>
<#assign custRequestIdd = request.getParameter("srNumber")!>
</#if>
<#macro createAttachmentModal instanceId path fromAction="" isMultiple=false>

<#assign pretailParam = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "ATTACHMENT_SIZE").queryOne()! />
<#if pretailParam?exists && pretailParam?has_content>
	<#assign globalFileSize = pretailParam.value!>
</#if>

<#local fileLimits = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "FILE_LIMITS", "3") />

<input type="hidden" name="globalFileSize" id="globalFileSize" value="${globalFileSize!}" />
<input type="hidden" name="fileLimits" id="fileLimits" value="${fileLimits!3}" />

<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title">Add Attachment</h4>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body poplabel-left" id="attachment">
                   <form name="add-attachment-form1" id="add-attachment-form1" action="" enctype="multipart/form-data" method="post">
                      <div class="row p-1">
	                   		<div class="col-md-12 col-lg-12 col-sm-12 ">
	                   		    <input type="hidden" id="path" name="path" value="${path!}">
	                   		    <input type="hidden" id="activeTab" name="activeTab" value="attachments">
	                   		    <input type="hidden" id="partyId" name="partyId" value="${partyId?if_exists}"/>
	                   		    <input type="hidden" id="salesOpportunityId1" name="salesOpportunityId1" value="${salesOppIdd!}">
	                   		    <input type="hidden" id="custRequestId1" name="custRequestId1" value="${custRequestIdd!}">
	                   		    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
								<input type="hidden" id="domainEntityType1" name="domainEntityType" value="${domainEntityType!}">
								<input type="hidden" id="domainEntityId" name="domainEntityId" value="${domainEntityId!}">
								<input type="hidden" id="attachmentType" name="attachmentType" value="PUBLIC">
								
	                          <#if inputContext.enumValues?has_content>
	                                <#assign entityList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(inputContext.enumValues, "enumId","description")?if_exists />
	                            <#else>
	                              <#assign entities = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId","CONTENT_CLASS","isEnabled","Y"), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId"), false)>
                          		  <#assign entityList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(entities, "enumId","description")?if_exists />
	                           </#if>
	                            <@dropdownCell 
	                              id="classificationEnumId"
	                              name="classificationEnumId"
	                              placeholder=uiLabelMap.Classification
	                              options=entityList!
	                              label= "Classification"
	                              value="${requestParameters.classificationEnumId?if_exists}"
	                              allowEmpty=false
	                              /> 
		                      	<@inputArea
						   			id="attachmentDescription"
						   			label="Attachment Description"
						   			rows="3"
						   			placeholder = "Description"
						   			value = ""
						   			required=false
						   			maxlength="255"
						   		/>
		                      
	                    		<div id= "upload">
	                    			<@inputRow id="uploadFile" type="file" label="Upload" required =true multiple=isMultiple! />
	                    		</div>
	                    		
	                    		<div>
		                      		&nbsp;Attachment Type &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		                      		<input type="radio"  name="publicOrPrivateAtt" id="publicOrPrivateAtt" value="PUBLIC" checked /> Public
			                    	&nbsp;&nbsp; <input type="radio"  name="publicOrPrivateAtt" id="publicOrPrivateAtt" value="PRIVATE"/> Private
			                    </div>
								
	                    	</div>
						</div>          
                    	<div class="modal-footer">
                    		<@button class="btn btn-sm btn-primary navbar-dark" id="add-attachment-form-submit" label="${uiLabelMap.Save}"/>
                    		<@reset label="${uiLabelMap.Reset}"	/>
                    	</div>
                  </form>
    	 </div>
    </div>
  </div>
</div>

<script>
$(document).ready(function() {
	$("#create-attachment-modal").on("hidden.bs.modal", function () {
		$(".modal-backdrop").remove();
	});
	$("#${instanceId!}").on("show.bs.modal", function(e) {
		$('.clear').click();
		document.getElementById('publicOrPrivateAtt').checked = true;
	    $("#add-attachment-form1 #uploadFile").val("");
		$("#add-attachment-form1 #attachmentDescription").val("");	
		$('#add-attachment-form1')[0].reset();
   	});
   	
    document.getElementById('uploadFile_error').innerHTML = "";
    $("#add-attachment-form1").validate({
        rules: {
            uploadFile: {
                required: true,
                uploadFile: true,
            }
        }
    });
    $("#uploadFile").change(function() {
        $("#uploadFile").blur().focus();
        document.getElementById('uploadFile_error').innerHTML = "";
    });
	
	$('input[type=radio][name=publicOrPrivateAtt]').change(function() {
		$("#attachmentType").val(this.value);
	});
	
    function validateUploadFile() {

        if ($('#uploadFile')[0].files.length === 0) {
            // alert("Attachment is Required");
            document.getElementById('uploadFile_error').innerHTML = " Attachment is Required*";
            $('#uploadFile').focus();

            return false;
        } else {

            const fi = document.getElementById('uploadFile');
            var fileLimitsVal = $("#fileLimits").val();
            var fileLimits = parseInt(fileLimitsVal);
			//var fi = document.getElementById('uploadFile');
			if(fi.files.length > fileLimits){
				document.getElementById('uploadFile_error').innerHTML = " Please select maximum " + fileLimits + " files";
                return false;
			}
			
			for (var i = 0; i <= fi.files.length - 1; i++) {
				var uploadedFileSize = fi.files.item(i).size;
	            var globalFileSizeVal = $("#globalFileSize").val();
	            if (globalFileSizeVal && globalFileSizeVal != "") {
	                if (uploadedFileSize && uploadedFileSize / 1024 / 1024 > globalFileSizeVal) {
	                    document.getElementById('uploadFile_error').innerHTML = " File Size Should Not Exceed " + globalFileSizeVal + " MB";
	                    return false;
	                }
	            }
			}
            
            
            
            document.getElementById('uploadFile_error').innerHTML = "";
            return true;
        }
    }

	<#-- 
    $('#add-attachment-form-submit').on('click', function(e) {
        //var partyId= $("#add-attachment-form1 input[name=partyId]").val();
        //console.log("partyId-------------"+partyId);
        var isvalid = validateUploadFile();
        if (isvalid) {
            var fd = new FormData();
            var files = $('#uploadFile')[0].files[0];
            fd.append('file', files);
            fd.append('classificationEnumId', $("#classificationEnumId").val());
            fd.append('attachmentDescription', $("#attachmentDescription").val());
            fd.append('partyId', $("#add-attachment-form1 #partyId").val());
            fd.append('path', $("#path").val());
            fd.append('salesOpportunityId', $("#salesOpportunityId1").val());
            fd.append('custRequestId', $("#custRequestId1").val());
            fd.append('domainEntityType', $("#domainEntityType1").val());

            $.ajax({
                type: "POST",
                processData: false,
                contentType: false,
                url: "createattachmentData",
                data: fd,
                sync: true,
                success: function(data) {


                    $('#${instanceId!}').modal('hide');
                    $("input:file").val("");
                    $("textarea").val("");
                    getattachmentRowData();
                    showAlert("success", "Successfully created attachment");
                }
            });
        }
        e.preventDefault();
    });
     -->
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

<div id="${instanceId!}" class="modal fade" role="dialog" aria-hidden="true" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog image-model" id="image-model">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Image Preview</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<span class="text-center">
            		<img class="" id="${instanceId!}-preview-image" name="${instanceId!}-preview-image" width="400" height="500" src="" ></img>
            	</span>
            </div>
            <div class="modal-footer" style="justify-content: center;">
            	<span style="    font-size: 16px;color: crimson;">Please click image to Rotate.</span>
                <#-- <button type="sbmit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button> -->
            </div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
	
$('#${instanceId!}-preview-image').click(function(){
	const img = document.getElementById('${instanceId!}-preview-image');
	rotateImage(img.attributes.src.value, 90, function(resultBase64) {
	  img.setAttribute('src', resultBase64);
	});
});
	
});

function previewImage(imageEncoded) {
	$("#${instanceId!}-preview-image").attr('src', imageEncoded);
	$('#${instanceId!}').modal("show");
}

function rotateImage(imageSrc, degrees, callback) {
  const canvas = document.createElement('canvas');
  const ctx    = canvas.getContext('2d');
  const image  = new Image();

  image.onload = function () {
    canvas.width  = degrees % 180 === 0 ? image.width : image.height;
    canvas.height = degrees % 180 === 0 ? image.height : image.width;

    ctx.translate(canvas.width / 2, canvas.height / 2);
    ctx.rotate(degrees * Math.PI / 180);
    ctx.drawImage(image, image.width / -2, image.height / -2);

    callback(canvas.toDataURL());
  };

  image.src = imageSrc;
}

</script> 
</#macro>


 