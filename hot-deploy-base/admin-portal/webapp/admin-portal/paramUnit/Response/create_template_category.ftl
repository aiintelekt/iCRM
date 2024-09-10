<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
     <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <#assign extra='<a href="/admin-portal/control/findTemplateCategory" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <@sectionFrameHeader title="Create Template Category" extra=extra />
        <div class="clearfix"></div>
        <form id="mainForm" method="post" action="<@ofbizUrl>createTemplateCategoryAction</@ofbizUrl>" data-toggle="validator">    
        
           
            	
            	<@dynaScreen 
					instanceId="TEMPLATE_CATEGORY_BASE"
					modeOfAction="CREATE"
					/>
            	
            <div class="form-group offset-2">
            <div class="text-left ml-3 pad-10">
         
            <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn2=true
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2id="clear-temp-lov-fields"
                     btn2label="${uiLabelMap.Clear}"
                   />
            </div>
            </div>
        </form>
    </div>
</div>
</div>
<script>

$(document).ready(function() {
	$('#parentTemplateCategoryId').on("change", function () {
		let lovTypeIdValue = $('#parentTemplateCategoryId').val();
		if(lovTypeIdValue != ""){
		$.ajax({
				async: false,
				type: "POST",
				url: "getLovSequenceNum",
				data: {"lovTypeId": lovTypeIdValue,"type":"TEMP_CAT_SETUP"},
				success: function (data) {
					$('#sequence').val(data.sequenceNum);
					$('div #sequence').text(data.sequenceNum);
				}
			});
		} else{
			$('#sequence').val("");
			$('div #sequence').text("");
		}
	});
});
$('#clear-temp-lov-fields').on("click", function () {
	$('#sequence').val("");
	$('div #sequence').text("");
});
</script>