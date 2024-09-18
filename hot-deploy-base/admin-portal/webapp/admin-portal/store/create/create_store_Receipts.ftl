<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/lib/picker_macro.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="/admin-portal/control/findStoreReceipts" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>createStoreReceipt</@ofbizUrl>" data-toggle="validator" enctype="multipart/form-data">
            <div class="col-lg-12 col-md-12 col-sm-12">
            <@sectionFrameHeader title="Create Store Receipts" extra=extra />
            	<@dynaScreen 
					instanceId="CREATE_STORE_RECEIPT"
					modeOfAction="CREATE"
					/>
            	
            </div>
            
             <div class="col-md-12 col-lg-12 col-sm-12">
             
             <div class="form-group row storeImage" id="storeImage_row" style="">
	         	<label class="col-sm-2 field-text" id="storeImage_label">Store Image</label>
 
		   	 	<div class=" col-sm-6">
		       	<input type="file" id="storeImage" name="storeImage" class="" placeholder="Choose File" accept=".jpeg,.png,.jpg">
		       	<div class="help-block with-errors" id="storeImage_error"></div>
		    	</div>
			</div>
          <#--  <div class="">
			     <@inputRowFilePicker
				    id="storeImage"
				    label="Store Image"
				    required = false
				    labelColSize="col-sm-2"
				    inputColSize="col-sm-3"
				 />
				
			</div>-->
			</div>
            <div class="col-md-12 col-lg-12 col-sm-12 ">
			    <@textareaLarge
				    id="storeHTML"
				    name="storeHTML"
				    label="Store HTML"
				    rows="5"
				    required = false
				    txareaClass = "ckeditor"
				    value=""
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
<@productStorePicker instanceId="productStorePicker"
/>
<script>
var regex = '';
$(document).ready(function() {
//$(".input-group-addon").hide();
 $('input[name="storeImage"]').on( 'change', function() {
   myfile= $(this).val();
   var ext = myfile.split('.').pop().toLowerCase();
   if(ext=="jpeg" || ext=="png" || ext=="jpg"){
       
   } else{
         showAlert("error","Only JPEG, PNG,JPG file allowed");
		 $(this).val('');
		 return false;
   }
});
//to display state list based on country selected
var countryGeoId = $('#country').val();
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