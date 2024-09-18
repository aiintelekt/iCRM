<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="/admin-portal/control/viewStoreReceipt?productStoreId=${inputContext.productStoreId!}" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>updateStoreReceipt</@ofbizUrl>" data-toggle="validator" enctype="multipart/form-data">
            <div class="col-lg-12 col-md-12 col-sm-12">
            <@sectionFrameHeader title="Edit Store Receipt" extra=extra />
            	<@dynaScreen 
					instanceId="CREATE_STORE_RECEIPT"
					modeOfAction="UPDATE"
					/>
            	
            </div>
            <div class="col-md-12 col-lg-12 col-sm-12">
          <#--   <div class="">
			    <@inputRowFilePicker
				    id="storeImage"
				    label="Store Image"
				    required = false
				    labelColSize="col-sm-2"
				    inputColSize="col-sm-3"
				 />
			</div>-->
			 <div class="form-group row storeImage" id="storeImage_row" style="">
	         	<label class="col-sm-2 field-text" id="storeImage_label">Store Image</label>
 
		   	 	<div class=" col-sm-6">
		       	<input type="file" id="storeImage" name="storeImage" class="" placeholder="Choose File" accept=".jpeg,.png,.jpg">
		       	<div class="help-block with-errors" id="storeImage_error"></div>
		       	
		       	<#if inputContext?has_content && inputContext.storeImage_link?has_content>
		       	<div id="ext_image_div" style="display:block">
		       	Current Image : 
		       	<a href="${inputContext.storeImage_link!}" id="link_storeImage" class="" target="_blank" data-original-title="" title="Image" 
				data-toggle="popover" data-trigger="focus" 
				data-content="<img src='${inputContext.storeImage_link!}' id='store_image_img' 
				class='img-fluid'/>">${inputContext.storeImage!}</a>
				</#if>
		    	</div>
		    	</div>
		    	
			</div>
			</div>
            <div class="col-md-12 col-lg-12 col-sm-12 ">
			    <@textareaLarge
				    id="storeHTML"
				    name="storeHTML"
				    label="Store HTML"
				    rows="5"
				    required = false
				    txareaClass = "ckeditor"
				    value="${inputContext.storeHTML!}"
				 />
			    <script>     
					CKEDITOR.replace( 'storeHTML',{
						customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
						autoGrow_minHeight : CKEditorUtil.autoGrowMinHeight,
						removePlugins : CKEditorUtil.removePlugins,
						readOnly: false
					});
			        //   CKEDITOR.on('storeHtml', function(e) {e.editor.resize("100%", 400)} );
			    </script>
			</div>
            <div class="offset-md-2 col-sm-10">
            <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn1onclick=""
                     btn1id="create-cust-btn"
                     btn2=true
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
            </div>
        </form>
    </div>
</div>
<script>

$(document).ready(function() {
	//$(".input-group-addon").hide();
//	$("#productStoreId_val").attr("disabled","disabled");
//	$("#productStoreId_desc").attr("disabled","disabled");
$("#link_storeImage").popover({ placement: 'right', trigger: 'hover', html: true, delay: { show: 50, hide: 400 } });
 $('input[name="storeImage"]').on( 'change', function() {
   myfile= $(this).val();
   var ext = myfile.split('.').pop().toLowerCase();
   if(ext=="jpeg" || ext=="png" || ext=="jpg"){
       $("#ext_image_div").hide();
   } else{
         showAlert("error","Only JPEG, PNG,JPG file allowed");
		 $(this).val('');
		 return false;
   }
});
var stateSelectedId = "${inputContext?if_exists.state?if_exists}";
var countryGeoId = $('#country').val();
var list = "";
if (countryGeoId != null && countryGeoId != "") {
	var urlFindStateString = "/common-portal/control/getStateDataJSON?countryGeoId=" + countryGeoId + "&externalLoginKey=${requestAttributes.externalLoginKey!}";
	$.ajax({
			type: 'POST',
			async: true,
			url: urlFindStateString,
			success: function(states) {
				$('[id="mainFrom"] #state').empty();
				list = $('[id="mainFrom"] #state');
				list.append("<option value=''>Select State</option>");
				if (states.length == 0) {
				list.append("<option value = ''>N/A</option>");
				} else {
					for (var i = 0; i < states.length; i++) {
						if (stateSelectedId != null && stateSelectedId != "" && states[i].geoId == stateSelectedId) {
							list.append("<option  value =" + states[i].geoId + " selected>" + states[i].geoName + " </option>");
						} else {
							list.append("<option  value =" + states[i].geoId + ">" + states[i].geoName + "</option>");
						}
					}
				}
			}
		});
		$('[id="mainFrom"] #state').append(list);
		$('[id="mainFrom"] #state').dropdown('refresh');
	}
//to display state list based on country selected
console.log('countryGeoId', countryGeoId);
regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
regex = regexJson.regex;
console.log('regex', regex);
		if ($('#country').val()) {
			getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'country', 'state', 'stateList', 'geoId', 'geoName', '${stateValue!}');
		}
	$('#country').change(function(e, data) {
	$('#generalPostalCode').val('');
	$('#generalPostalCodeExt').val('');
	getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'country', 'state', 'stateList', 'geoId', 'geoName', '${stateValue!}');
	var countryGeoId = $('#country').val();
		if (countryGeoId != '') {
			regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
			regex = regexJson.regex;
		} else {
			$('#state').html('<option value="">Please Select</option>');
		}
});
});
</script>	