<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<div class="row">
	<div id="main" role="main" >
		<form id="mainFrom" name="mainFrom" action="<@ofbizUrl>uploadLogoImage</@ofbizUrl>" onsubmit="return validateImageFormat()" method="POST" enctype="multipart/form-data">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@sectionFrameHeader title="${uiLabelMap.LogoImageConfig!}" />
				<div class="col-sm-6">
					<@dropdownCell 
						id="imageType"
						name="imageType"
						required=true
						allowEmpty=false
						placeholder = "Image type"
						dataLiveSearch=true
						options=imageType!
						label="Image Type"
						/>
				</div>
				<div class="col-sm-6">
					<@inputRow
						id="uploadImage"
						name="uploadImage"
						type="file"
						label="Image"
						required=true
						/> 
					<p style="font-size: 12px;"></p>
				</div>
				
				<div class="offset-md-2 col-sm-6">
					<@submit label="${uiLabelMap.Upload!}"/>
				</div>
			</div>
		</form>
	</div>
</div>
<script>
$('#imageType').on('change', function(){
	let uploadLogoImage = $(this).val();
	let mainFrom = $('#mainFrom');
	mainFrom.attr('action', "uploadLogoImage?imageType="+uploadLogoImage+"");
});

function validateImageFormat(){
	let imageType = $('#imageType').val();
	let image = $('#uploadImage').val();
	let imgExt = image.split('.').pop().toLowerCase();
	if(imageType == 'logo'){
		if(imgExt!='jpeg' && imgExt!='jpg' && imgExt!='png'){
			showAlert("error","Only image files are allowed");
			$('#uploadImage').val('');
			return false;
		}
	}else if (imageType == 'icon'){
		if(imgExt!='ico'){
			showAlert("error","Only ICO files are allowed");
			$('#uploadImage').val('');
			return false;
		}
	}
}
</script>
