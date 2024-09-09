<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="/activity-portal/control/findActivity" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>createApntAction</@ofbizUrl>" data-toggle="validator">    
        	
        	<input type="hidden" name="domainEntityType" value="${domainEntityType!}"/>
        	<input type="hidden" name="domainEntityId" value="${domainEntityId!}"/>
        	<@inputHidden name="ownerBu" id="ownerBu" />
            <@inputHidden id="workEffortTypeId" value="${workEffortTypeId!}"/>
            <@inputHidden id="loggedInUserId" value="${loggedUserId}!" />
            <@inputHidden id="userName" value="${loggedUserPartyName!}"/>
        	
            <div class="col-lg-12 col-md-12 col-sm-12">
            	<@sectionFrameHeader title="${uiLabelMap.CreateApnt!}" extra=extra?if_exists />
            	<@dynaScreen 
					instanceId="ACTIVITY_APNT_BASE"
					modeOfAction="CREATE"
					/>
            	
            </div>
            
            <div class="col-md-12 col-lg-12 col-sm-12 ">
         		<@textareaLarge  label="Description" id="description" rows="4"/>
      		</div>
            
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
         
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
    $.ajax({
        type: "GET",
        url:'/common-portal/control/getAttendeeList',
         data: {"partyId": $("#partyId").val(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function(data) {
        	if (data) {
            if(data.responseMessage=="success"){
            	for (var i = 0; i < data.attendeesList.length; i++) {
            		var entry = data.attendeesList[i];
            		if(entry!=null){
            		userOptionList += '<option value="'+entry.partyId+'">'+entry.userName+'</option>';
            		}
            	}
            }
            }
            }
    });
   $("#requiredAttendees").html(DOMPurify.sanitize(userOptionList));
   $("#optionalAttendees").html(DOMPurify.sanitize(userOptionList));
}

</script>