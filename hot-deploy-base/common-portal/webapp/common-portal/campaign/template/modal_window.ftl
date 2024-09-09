<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<#macro createTemplateContentModal instanceId fromAction="">
<div id="${instanceId!}" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" >
    <div class="modal-dialog modal-lg" style="max-width: 1700px;">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Template Content</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form id="templateContent-add-form" method="post" data-toggle="validator">
                    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                    <input type="hidden" name="templateContentId" id="templateContentId" />
                    <input type="hidden" name="domainEntityType" value="${domainEntityType!}">
                    <input type="hidden" name="domainEntityId" value="${domainEntityId!}">
                    <@dynaScreen 
                    instanceId="TPL_CONT_BASE"
                    modeOfAction="CREATE"
                    />
                    <div class="form-group offset-2">
                        <div class="text-left ml-3">
                            <@formButton
                            btn1type="submit"
                            btn1label="${uiLabelMap.Save}"
                            btn2=true
                            btn2id="${instanceId!}-reset-btn"
                            btn2type="reset"
                            btn2label="${uiLabelMap.Clear}"
                            />
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<script>
$(document).ready(function() {

$('#${instanceId!}').on('hidden.bs.modal', function (e) {
  	$("#${instanceId!}-reset-btn").trigger( "click" );
  	$("#templateContentId").val('');
});

initiateFromValidation('templateContent-add-form');

});

function editTemplateContent(templateContentId) {
	$('#${instanceId!}').modal('show');
	$.ajax({
		type: "POST",
     	url: "/common-portal/control/getTemplateContentData",
        data: {"templateContentId": templateContentId, "domainEntityType": "${domainEntityType!}", "domainEntityId": "${domainEntityId!}", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (result) {   
            if (result.code == 200) {
            	for (var fieldName in result.data){
				    //console.log("name: "+fieldName+", value: "+result.data[fieldName]);
				    if (result.data[fieldName]) {
				    	$('#'+fieldName).val( result.data[fieldName] );
				    }
				}
				
				$('#contentTplId_error').html("");
				$('#tagId_error').html("");
				$('.ui.dropdown.search').dropdown({
					clearable: true
				});
            }
        }
	}); 
}
</script> 
</#macro>

<#macro viewTemplateContentModal instanceId fromAction="">
<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title"> Content Text View</h2>
        <button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
        <div class="clearfix"></div>
		
        <@displayCell
		     label="Content Text"
		     value=""
		     id="templateContentText"
		     labelColSize="col-sm-2"
		     />
      </div>
      <div class="modal-footer">
        <button type="sbmit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<script>
function viewTemplateContentInfo(templateContentId){
    $.ajax({
        type: "POST",
        url: "/common-portal/control/getTemplateContentData",
        async: true,
        data:  {"templateContentId": templateContentId, "domainEntityType": "${domainEntityType!}", "domainEntityId": "${domainEntityId!}", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        success: function(result) {
        	if (result.code == 200) {
        		data = result.data;
        		
        		var templateContentId=data.templateContentId;
    			var contentText=data.contentText;
            	document.getElementById("templateContentText").innerHTML=contentText;
            	
            	$("#view-templateContent-modal").modal('show');
        	}
	 	}
    });
}
</script> 
</#macro>