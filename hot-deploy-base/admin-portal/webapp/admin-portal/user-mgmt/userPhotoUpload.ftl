<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl" />
<div class="row pt-2">
	<div class="col-md-6 col-lg-6 col-sm-6 ">
		<form action="uploadPartyImage" id="partyImageForm" name="partyImageForm" method="POST" enctype="multipart/form-data">
			<@inputHidden id="userLoginId" name="userLoginId" value="${userData.userLoginId!}" />
			<@inputHidden id="partyId" value="${userData.partyId?if_exists}" />
			<@inputHidden id="activeTab" value="photoupload" />
			<div id="upload">
				<@inputRow
					id="uploadFile"
					type="file"
					label="Upload"
					required=false
					/>
			</div>
			<div class="form-group row buttons" id="buttons_row" style="">
				<label class="col-sm-4 field-text" id=""></span></label>
				<div class=" col-sm-8 left">
					<@button
						class="btn btn-sm btn-primary navbar-dark"
						id="user-image-upload"
						label="${uiLabelMap.Upload}"
						/>
				</div>
			</div>
		</form>
		<img src="${userData.userPhoto!'/bootstrap/images/default-avatar-profile.jpg'}" style="width: 7vw;height: auto;">
	</div>
</div>
<script>
	$(document).ready(function() {
		$('#user-image-upload').click(function(){
			var inp = document.getElementById('uploadFile');
			var filePath = inp.value;
			// Allowing file type
			var allowedExtensions = /(\.jpg|\.jpeg|\.png|\.gif)$/i;
			if(inp.files.length === 0){
				showAlert("error", "Please choose image");
				inp.focus();
			} else if(!allowedExtensions.exec(filePath)) {
				showAlert("error", "Please upload file having extensions .jpeg/.jpg/.png/.gif only.");
				inp.focus();
			} else{
				$('#partyImageForm').submit();
			}
		});
	});
</script>