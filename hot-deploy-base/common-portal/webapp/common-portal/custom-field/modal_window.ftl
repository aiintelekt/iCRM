<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<#macro displayCustomFieldsParams instanceId>
<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-md">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title"><span id="${instanceId!}-fieldName"></span> - Params</h2>
        
        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        
      </div>
      <div class="modal-body">
		<ul class="" id="${instanceId!}-params">
		</ul>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<script>
$(document).ready(function() {

$(".cf-param-list").click(function () {
	console.log('cf param modal click');
	let customFieldId = $(this).attr('data-customFieldId');
	let customFieldName = $(this).attr('data-customFieldName');
	let paramData = $(this).attr('data-paramData');
	console.log('paramData> '+paramData);
	let paramsHtml = '';
	paramData.split(/\s*,\s*/).forEach(function(param) {
	    //console.log(param);
	    if (param) {
	    	paramsHtml += '<li class="mt-0">'+param+'</li>';
	    }
	});
	$("#${instanceId!}-params").html(paramsHtml);
	$("#${instanceId!}-fieldName").html(customFieldName);
    $("#${instanceId!}").modal("show");
});  
            
});

</script>
</#macro>

