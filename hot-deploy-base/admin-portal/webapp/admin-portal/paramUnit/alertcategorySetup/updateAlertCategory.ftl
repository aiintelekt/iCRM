<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

    <div class="row">
        <div id="main" role="main">
                <#assign extra='<a href="viewAlertCategory?alertCategoryId=${inputContext.alertCategoryId!}" class="btn btn-xs btn-primary">
                <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
                <@sectionFrameHeader title="${uiLabelMap.updateAlertCategorySetUp!}" extra=extra />
                <div class="col-lg-12 col-md-12 col-sm-12">
<form method="post" action="<@ofbizUrl>alertCategoryUpdation</@ofbizUrl>" class="form-horizontal" name="mainFrom" id="mainFrom">

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
						
						<@inputHidden    
                            id="alertCategoryId"
                            name="alertCategoryId"
                            value=inputContext.alertCategoryId!
                           />
                                   
                    	<@dynaScreen 
						instanceId="PARAM_ALT_CAT"
						modeOfAction="UPDATE"
						/>               
						
                             <div class="offset-md-2 col-sm-10">
                                <input type="submit" class="btn btn-sm btn-primary" onclick="return formSubmission();" value="Update">
                                <a href="viewAlertCategory?alertCategoryId=${inputContext.alertCategoryId!}"class="btn btn-sm btn-secondary"> Cancel</a>   
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
function formCancel(){
var url = "viewAlertCategory?alertCategoryId=${alertCategoryId!}";
window.location(url);
}
</script>

<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>
