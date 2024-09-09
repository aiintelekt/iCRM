<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
		<#assign extra='<a href="/admin-portal/control/viewResAvail?entryId=${inputContext.entryId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
		<form id="mainFrom" method="post" action="<@ofbizUrl>updateResAvailAction</@ofbizUrl>" data-toggle="validator">
			<@inputHidden id="entryId" value="${inputContext.entryId!}"/>
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@sectionFrameHeader title="${uiLabelMap.UpdateResAvail!}" extra=extra/>
				<@dynaScreen 
					instanceId="RES_AVAIL_BASE"
					modeOfAction="UPDATE"
					/>
			</div>
			<div class="offset-md-2 col-sm-10">
				<@submit
					label="${uiLabelMap.Save}"
					/>
				<@cancel
					label="Cancel"
					onclick="/admin-portal/control/viewResAvail?entryId=${inputContext.entryId!}"
					/>
			</div>
		</form>
	</div>
</div>

<script>

$(document).ready(function() {

initDateRange("fromDate_date_picker", "thruDate_date_picker", null, null);
getUsers("${loggedUserId!}", "${loggedUserPartyName!}");

$('#mainFrom').validator().on('submit', function (e) {
	if (!e.isDefaultPrevented()) {
  		var valid = validateDateRange($("#fromDate_date").val(), $("#fromDate_time").val(), $("#thruDate_date").val(), $("#thruDate_time").val());
  		if (!valid) {
  			showAlert("error", "Start Date should be less than End Date");
  			e.preventDefault();
  		}
  	}
});

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
   	$("#partyId").val('${inputContext.partyId!}');
}

</script>