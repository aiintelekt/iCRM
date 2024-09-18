<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
 <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "profileDetails") />  
 <script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>

<div class="pt-2">
      <h2 class="d-inline-block">Store Receipt Details</h2>
       <ul class="flot-icone">
          <li class="mt-0">
            <a href="<@ofbizUrl>editStoreReceipt?productStoreId=</@ofbizUrl>${parameters.productStoreId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
         </li>
         <li class="mt-0">${helpUrl?if_exists}</li>
      </ul>
      
</div>

<div class="col-md-12 col-lg-12 col-sm-12">
	<@dynaScreen 
	instanceId="CREATE_STORE_RECEIPT"
	modeOfAction="VIEW"
	/>
	
</div>
<div class="col-md-12 col-lg-12 col-sm-12">
	<div class="form-group row storeImage" id="storeImage_row" style="">
		<label class="col-sm-2 field-text" id="storeImage_label">Stroe Image</label>
	 	<div class=" col-sm-6">
		<a href="${inputContext.storeImage_link!}" id="link_storeImage" class="" target="_blank" data-original-title="" title="Image" 
			data-toggle="popover" data-trigger="focus" 
			data-content="<img src='${inputContext.storeImage_link!}' id='store_image_img' 
			class='img-fluid'/>">${inputContext.storeImage!}</a>
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
    </script>
</div>
<script>
$(document).ready(function(){
	$("#link_storeImage").popover({ placement: 'right', trigger: 'hover', html: true, delay: { show: 50, hide: 400 } });
	
	$("#link_storeImageURL").popover({ placement: 'right', trigger: 'hover', html: true, delay: { show: 50, hide: 400 } });
	
	//use if from dyna
	$("#link_storeImageURL").mouseover(function(){
		var href = $('#link_storeImageURL').prop('href');
		$("#link_storeImageURL").attr("data-toggle","popover");
		$("#link_storeImageURL").attr("data-trigger","focus");
		$("#link_storeImageURL").attr("data-content","<img src='"+href+"' id='store_image_img' class='img-fluid'/>");
	}); 
});
</script>