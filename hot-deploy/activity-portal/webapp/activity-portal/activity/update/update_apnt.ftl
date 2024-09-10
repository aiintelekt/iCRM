<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
    <div id="main" role="main">
    	<div id="" class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <#assign extra='<a href="/activity-portal/control/viewActivity?workEffortId=${inputContext.workEffortId!}" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
       
        <div class="clearfix"></div>
        <form id="mainForm" method="post" action="<@ofbizUrl>updateApntAction</@ofbizUrl>" data-toggle="validator"> 
        	<@inputHidden id="workEffortTypeId" value="${inputContext.workEffortTypeId!}"/>
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            	 <@sectionFrameHeader title="${uiLabelMap.UpdateApnt!}" extra=extra />
            	<@dynaScreen 
					instanceId="ACTIVITY_APNT_BASE"
					modeOfAction="UPDATE"
					/>
            	
            </div>
            
            <div class="col-md-12 col-lg-12 col-sm-12 ">
         		<@textareaLarge label="Description" id="description" value="${inputContext.description!}" rows="4"/>
      		</div>
            
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10 p-2">
            
            <@submit label="${uiLabelMap.Save}"/>
            <@cancel label="Cancel" onclick="/activity-portal/control/viewActivity?workEffortId=${inputContext.workEffortId!}"/>
         	
            </div>
        </form>
        </div>
    </div>
</div>

<@partyPicker 
	instanceId="partyPicker"
	/> 

<script>

$(document).ready(function() {

initDateRange("actualStartDate_date_picker", "actualCompletionDate_date_picker", null, null);
getUsers("${loggedUserId!}", "${loggedUserPartyName!}");
getAttendees();

$('#mainFrom').validator().on('submit', function (e) {
	if (!e.isDefaultPrevented()) {
  		var valid = validateDateRange($("#actualStartDate_date").val(), $("#actualStartDate_time").val(), $("#actualCompletionDate_date").val(), $("#actualCompletionDate_time").val());
  		if (!valid) {
  			showAlert("error", "Start Date should be less than End Date");
  			e.preventDefault();
  		}
  	}
});

});

function getUsers(loggedInUserId, userName) {
    var userOptionList = '<option value="'+loggedInUserId+'">'+userName+'</option>';
    $.ajax({
        type: "GET",
        url:'/common-portal/control/getUsersList?externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function(data) {
        	for (var i = 0; i < data.length; i++) {
                var type = data[i];
                userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
            }
        }
    });
   $("#owner").html(DOMPurify.sanitize(userOptionList));
}

function getAttendees() {
	var userOptionList = '<option value="">Please Select</option>';
	var reqOptionList = '';
	var optOptionList = '';
	$.ajax({
		type : "GET",
		url : '/common-portal/control/getAttendeeList',
		data : {
			"workEffortId" : "${inputContext.workEffortId!}",
			"externalLoginKey" : "${requestAttributes.externalLoginKey!}"
		},
		async : false,
		success : function(data) {
			if (data) {
				if (data.responseMessage == "success") {
					for (var i = 0; i < data.attendeesList.length; i++) {
						var entry = data.attendeesList[i];
						if (entry != null) {
							if (entry.selected == "opt") {
								optOptionList += '<option value="'
										+ entry.partyId + '" selected>'
										+ entry.userName + '</option>';
							} else if (entry.selected == "req") {
								reqOptionList += '<option value="'
										+ entry.partyId + '" selected>'
										+ entry.userName + '</option>';
							} else {
								userOptionList += '<option value="'
										+ entry.partyId + '">' + entry.userName
										+ '</option>';
							}
						}
					}
					reqOptionList += userOptionList;
					optOptionList += userOptionList;
				}
			}
		}
	});

	$("#requiredAttendees").html(DOMPurify.sanitize(reqOptionList));
    $("#optionalAttendees").html(DOMPurify.sanitize(optOptionList));
}

</script>