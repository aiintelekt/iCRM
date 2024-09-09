<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

    <div class="row">
        <div id="main" role="main">
                <#assign extra='<a href="findAlertCategory" class="btn btn-xs btn-primary">
                <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
                <@sectionFrameHeader title="${uiLabelMap.createAlertCategorySetUp!}" extra=extra />
                <div class="col-lg-12 col-md-12 col-sm-12">
<form method="post" action="<@ofbizUrl>alertCategoryCreation</@ofbizUrl>" class="form-horizontal" name="mainFrom" id="mainFrom">

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>

<@dynaScreen 
	instanceId="PARAM_ALT_CAT"
	modeOfAction="CREATE"
	/>
						
						<#-- 
                        <div class="row">
                            <div class="col-md-12 col-lg-6 col-sm-12 ">
                                <@inputRow    
                                    name="alertName"
                                    id="alertName"
                                    placeholder = "${uiLabelMap.AlertName!}"
                                    required = true
                                    label="${uiLabelMap.AlertName!}"
                                    value=inputContext.alertName!
                                    isMakerChange=auditTrackList.alertName
                                    />
                                <@dropdownCell
                                    name="alertType"
                                    id="alertType"
                                    label="${uiLabelMap.AlertType!}"
                                    allowEmpty =  true
                                    options=alertTypeId
                                    placeholder = "${uiLabelMap.SelectAlertType!}"
                                    value=inputContext.alertType!
                                    isMakerChange=auditTrackList.alertType
                                     required = true
                                    />
                                <#assign alertPriorities = delegator.findByAnd("Enumeration", {"enumTypeId" : "PRIORITY_LEVEL","enumService","N","enumEntity","N"}, null, false)>
                                <#assign alertPriorityList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(alertPriorities, "enumCode","description")?if_exists /> 
                                <@dropdownCell
                                    name="alertPriority"
                                    id="alertPriority"
                                    required = true
                                    label="${uiLabelMap.AlertPriority!}"
                                    allowEmpty =  true
                                    options=alertPriorityList
                                    placeholder = "${uiLabelMap.SelectAlertPriority!}"
                                    value=inputContext.alertPriority!
                                    isMakerChange=auditTrackList.alertPriority
                                    />
                                <@checkbox
                                    name="autoClosure"
                                    id="autoClosure"
                                    label="${uiLabelMap.AutoClosure!}"
                                    checked=false
                                    value="Y"
                                    />
                                <@inputRow
                                    name="duration"
                                    id="duration"
                                    placeholder = "${uiLabelMap.Duration!}"
                                    label="${uiLabelMap.Duration!}"
                                    value=inputContext.duration!
                                    isMakerChange=auditTrackList.duration
                                    style="display:none;"
                                    />
                                <@inputRow    
                                    label="${uiLabelMap.SequenceNumber!}"
                                    id="sequenceNumber"
                                    name="sequenceNumber"
                                    placeholder="${uiLabelMap.SequenceNumber!}"
                                    value=inputContext.sequenceNumber!
                                    isMakerChange=auditTrackList.sequenceNumber
                                    />
                                <@dropdownCell
                                    name="status"
                                    id="status"
                                    required = true
                                    label="${uiLabelMap.AlertStatus!}"
                                    allowEmpty =  true
                                    options=statusId
                                    placeholder = "${uiLabelMap.SelectAlertStatus!}"
                                    value=inputContext.status!
                                    isMakerChange=auditTrackList.status
                                    />
                                <@inputArea
                                    name="remarks"
                                    id="remarks"
                                    placeholder = "${uiLabelMap.Remarks!}"
                                    label="${uiLabelMap.Remarks!}"
                                    value=inputContext.remarks!
                                    isMakerChange=auditTrackList.remarks
                                    />
                            </div>
                              -->
                              
                            <div class="form-group offset-2">
                              <div class="text-left ml-3">
                            <@formButton
                                 btn1type="submit"
                                 btn1label="${uiLabelMap.Save}"
                                 btn1onclick="return formSubmission();"
                                 btn2=true
                                 btn2onclick = "resetForm()"
                                 btn2type="reset"
                                 btn2label="${uiLabelMap.Clear}"
                               /> 
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
 
<script type="text/javascript">
    $(document).ready(function(){
        $('input[name="autoClosure"]').click(function(){
            if($(this).is(":checked")){
                $("#duration_row").show();
                $("#autoClosure").val('Y');
            }
            else if($(this).is(":not(:checked)")){
                $("#duration_row").hide();
                $("#duration").prop('required',true);
                $("#autoClosure").val('N');
            }
        });
    });
    /*
    $('input[type="checkbox"]').each(function(){
        var $t=$(this);
        var set = $t.prop('checked') ? 'Y' : 'N';
        $('input[name="'+$t.attr('name')+'"]').val(set);
    }); */
$("#sequenceNumber").keyup(function() {
   var exp = new RegExp("^[0-9]{0,10}$");
   var seqDigit = $("#sequenceNumber").val();
    if (exp.test(seqDigit)) {
           $("#sequenceNumber_error").empty();
      } else if(seqDigit==''){
           $("#sequenceNumber_error").empty();
      }else {
           $("#sequenceNumber_error").empty();
           $("#sequenceNumber_error").append('<ul class="list-unstyled text-danger"><li>Please enter only numeric.</li></ul>');
      }
});    
$("#alertName").keyup(function(){  
    if($("#alertName").val() != null){
    $("#alertName_error").html("");  
    }
}); 
$("#status").change(function(){  
    if($("#status").val() != null){
    $("#status_error").html("");  
}
});
$("#alertPriority").change(function(){  
    if($("#alertPriority").val() != null){
    $("#alertPriority_error").html("");  
}
});
$("#alertType").change(function(){  
    if($("#alertType").val() != null){
    $("#alertType_error").html("");  
}
});
 function formSubmission(){
    var isValid = "Y";
    var alertName =  $("#alertName").val();
    var status =  $("#status").val();
    var alertPriority =  $("#alertPriority").val();
    var alertType =  $("#alertType").val();
    var sequenceNumber =  $("#sequenceNumber").val();
    var seqerr="";
    if(alertName!=''&& status!=''&& alertPriority!=''&& alertType!=''){
         if(sequenceNumber != "")
       {
            var exp = new RegExp("^[0-9]{0,10}$");
            var seqDigit = $("#sequenceNumber").val();
                if (exp.test(seqDigit)) {
                    $("#sequenceNumber_error").empty();
                 }else {
                    $("#sequenceNumber_error").empty();
                    $("#sequenceNumber_error").append('<ul class="list-unstyled text-danger"><li>Please enter only numeric</li></ul>');
                    seqerr = "Y";
                    }
        }
       if( seqerr!="")
            return false;
        else
            return true;
    }
    else{
       if(status == "") {
          $("#status_error").html('');
          $("#status_error").append('<ul class="list-unstyled text-danger"><li id="status_err">Please select Status </li></ul>');
           isValid = "N";
        }
        if(alertPriority == "") {
          $("#alertPriority_error").html('');
          $("#alertPriority_error").append('<ul class="list-unstyled text-danger"><li id="status_err">Please select Priority</li></ul>');
           isValid = "N";
        }
         if(alertName == "") {
           $("#alertName_error").html('');
           $("#alertName_error").append('<ul class="list-unstyled text-danger"><li id="alertName_err">Please enter Alert Category Name</li></ul>');
            isValid = "N";
        }
        if(alertType == "") {
           $("#alertType_error").html('');
           $("#alertType_error").append('<ul class="list-unstyled text-danger"><li id="alertType_err">Please select Alert Type</li></ul>');
            isValid = "N";
        }
         if(sequenceNumber != "")
       {
            var exp = new RegExp("^[0-9]{0,10}$");
            var seqDigit = $("#sequenceNumber").val();
                if (exp.test(seqDigit)) {
                    $("#sequenceNumber_error").empty();
                 }else {
                    $("#sequenceNumber_error").empty();
                    $("#sequenceNumber_error").append('<ul class="list-unstyled text-danger"><li>Please enter only numeric</li></ul>');
                    seqerr = "Y";
                    }
        }
        if(isValid == "N"){
          return false;
        } 
        else if(isValid == "Y")
        {
             if( seqerr!="")
                return false;
            else
                return true;
        }
    }
}
</script>

<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>