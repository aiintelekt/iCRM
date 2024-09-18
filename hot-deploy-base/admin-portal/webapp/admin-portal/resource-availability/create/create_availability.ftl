<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#assign extra='<a href="/admin-portal/control/findResAvail" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
<div class="row">
	<div id="main" role="main">
		<form id="mainFrom" method="post" action="<@ofbizUrl>createResAvailAction</@ofbizUrl>" data-toggle="validator">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@sectionFrameHeader title="${uiLabelMap.CreateResAvail!}" extra=extra/>
				<@dynaScreen
					instanceId="RES_AVAIL_BASE"
					modeOfAction="CREATE"
					/>
			</div>
			<br>
			<div class="offset-md-2 col-sm-12">
				<@formButton
					btn1type="submit"
					btn1label="${uiLabelMap.Save}"
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

getUsers("${loggedUserId!}", "${loggedUserPartyName!}");

initDateRange("fromDate_date_picker", "thruDate_date_picker", null, null);

$('#mainFrom').validator().on('submit', function (e) {
	if (!e.isDefaultPrevented()) {
  		var valid = validateDateRange($("#fromDate_date").val(), $("#fromDate_time").val(), $("#thruDate_date").val(), $("#thruDate_time").val());
  		if (!valid) {
  			showAlert("error", "Start Date should be less than End Date");
  			e.preventDefault();
  		}
  	}
});	

<#if workStartTime?has_content>
$('#fromDate_time').timepicker('setTime', '${StringUtil.wrapString(workStartTime)}');
</#if>
<#if workEndTime?has_content>
$('#thruDate_time').timepicker('setTime', '${StringUtil.wrapString(workEndTime)}');
</#if>

});

function getUsers(loggedInUserId,userName) {
    var userOptionList = '<option value=""></option>';//'<option value="'+loggedInUserId+'">'+userName+'</option>';
    $.ajax({
        type: "GET",
        //url:'/common-portal/control/getUsersList?roleTypeId=ACT_OWNER&externalLoginKey=${requestAttributes.externalLoginKey!}',
        url:'/common-portal/control/getUsersList?roleTypeId=${resAvailRoles!}&externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function(data) {
        	for (var i = 0; i < data.length; i++) {
                var type = data[i];
                userOptionList += '<option value="'+type.partyId+'">'+type.userName+' ('+ type.roleDesc +') </option>';
            }
        }
    });
    
   	$("#partyId").html(userOptionList);
}

</script>