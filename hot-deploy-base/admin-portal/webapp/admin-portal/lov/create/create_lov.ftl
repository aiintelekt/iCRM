<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="/admin-portal/control/findLov" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>createLovAction</@ofbizUrl>" data-toggle="validator">
            <div class="col-lg-12 col-md-12 col-sm-12">
                <@sectionFrameHeader title="${uiLabelMap.CreateLov!}" extra=extra />
                <@dynaScreen
                    instanceId="LOV_BASE"
                    modeOfAction="CREATE"
                    />
                <div class="form-group offset-2">
                    <div class="text-left ml-1 pad-10">
                        <@formButton
                            btn1type="submit"
                            btn1label="${uiLabelMap.Save}"
                            btn2=true btn2onclick="resetForm()"
                            btn2id="clear-lov-fields"
                            btn2type="reset"
                            btn2label="${uiLabelMap.Clear}"
                            />
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
<script>

$(document).ready(function() {
	$('#lovTypeId').on("change", function () {
		let lovTypeIdValue = $('#lovTypeId').val();
		if(lovTypeIdValue != ""){
		$.ajax({
				async: false,
				type: "POST",
				url: "getLovSequenceNum",
				data: {"lovTypeId": lovTypeIdValue},
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
$('#clear-lov-fields').on("click", function () {
	$('#sequence').val("");
	$('div #sequence').text("");
});
</script>