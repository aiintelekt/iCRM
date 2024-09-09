<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<#macro leadConvertModal instanceId >
<div id="${instanceId!}" class="modal fade" style="z-index: 99999;">
    <div class="modal-dialog modal-lg" style="max-width: 600px;">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">
                	${uiLabelMap.ConvertLead!}
               	</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<form method="post" id="${instanceId!}-lead-convert-form" action="<@ofbizUrl>convertLead</@ofbizUrl>" class="form-horizontal" name="" novalidate="novalidate" data-toggle="validator">
                    <input type="hidden" name="leadPartyId" value="${parameters.partyId!}">
                    <div class="row p-1">
                        <div class="col-md-12 col-lg-12 col-sm-12 ">
                            <@radioInputCell
					        id="${instanceId!}-convertType"
					        name="leadConvertType"
					        options=leadConvertOptions
					        value="ACCOUNT"
							inputColSize="col-sm-12"
					        />
                            <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                        </div>
                    </div>
                </form>
                
            	<div class="modal-footer">
                    <#-- <@button id="${instanceId!}-lead-convert-btn" class="btn btn-sm btn-primary navbar-dark" label="${uiLabelMap.Convert}" /> -->
                    <input type="button" id="${instanceId!}-lead-convert-btn" name="${instanceId!}-lead-convert-btn" data-toggle="confirmation" title="Are you sure to Convert ?" class="btn btn-sm btn-primary navbar-dark" value="Convert">
                    <button type="button" class="btn btn-sm btn-primary navbar-dark" id="btnclose" data-dismiss="modal">Close</button>
                    <#-- <@reset id="${instanceId!}-reset-statuschange-form" label="${uiLabelMap.Reset}" class="btn btn-sm btn-primary navbar-dark"/> -->
                </div>
            </div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {

$("#${instanceId!}-lead-convert-btn").click(function () {
	let convertType = $('#${instanceId!}-lead-convert-form input[name="leadConvertType"]:checked').val();
	console.log('click lead convert ....' + convertType);
	
	if (convertType) {
		$('#${instanceId!}-lead-convert-form').submit();
	}
	
});

/*
$('#${instanceId!}').on('hidden.bs.modal', function (e) {
	$("#${instanceId!}-apv-statuschange-form")[0].reset();
}); 
*/

});
</script> 
</#macro>
