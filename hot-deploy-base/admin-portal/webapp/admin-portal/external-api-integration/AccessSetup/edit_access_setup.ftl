<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="/admin-portal/control/viewAccessSetup?channelAccessId=${inputContext.channelAccessId!}" id="update-back-btn" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>editAccessSetupAction</@ofbizUrl>" data-toggle="validator" onsubmit="javascript:return validateFromThruDate();">
            <div class="col-lg-12 col-md-12 col-sm-12">
            <@sectionFrameHeader title="${uiLabelMap.EditAccessSetup!}" extra=extra />
            	<@dynaScreen 
					instanceId="CREATE_ACCESS_SETUP"
					modeOfAction="UPDATE"
					/>
            	
            </div>
            <div class="offset-md-2 col-sm-10">
            <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn1onclick=""
                     btn1id="update-access-setup-btn"
                     btn2=true
                     btn2onclick = "cancelUpdate()"
                     btn2type="button"
                     btn2label="${uiLabelMap.Cancel}"
                   />
            </div>
        </form>
    </div>
</div>
<script>

$(document).ready(function() {
 $("#fromDate,#thruDate").focus(function() {}).blur(function() {
      validateFromThruDate();
 });
});
function cancelUpdate(){
	location.href="<@ofbizUrl>viewAccessSetup?channelAccessId=${inputContext.channelAccessId!}</@ofbizUrl>";
}
function validateFromThruDate(){
	$("#thruDate_error").html('');
    var fromDate = $("#fromDate").val();
    var thruDate = $("#thruDate").val();
    var thruDateLen = $("#thruDate").val().trim().length;
    if (fromDate == "" && thruDateLen > 0) {
    	$("#fromDate_error").html('');
        $("#fromDate_error").append('Please enter from date');
        return false;
     } else if (fromDate != "" && thruDate != "") {
        $("#thruDate_error").empty();
        if (new Date(thruDate) < new Date(fromDate)) {
     	   $("#thruDate_error").html('');
           $("#thruDate_error").append('Thru Date should not lesser than From date');
           return false;
        }
     }
};
</script>