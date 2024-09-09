<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

      <div class="row">
        <div id="main" role="main">
          <#assign extra='<a href="viewActivityType?enumId=${inputContext.activityTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeader title="${uiLabelMap.UpdateActivityType!}" extra=extra />
         
<form id="mainFrom" action ="<@ofbizUrl>updateActivityType</@ofbizUrl>" method="post">

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>

<input type="hidden" id="enumId" name="enumId" value="${inputContext.activityTypeId!}"/>

          <div class="col-md-12 col-lg-12 col-sm-12 ">
          	
          	<@inputHidden    
              id="activityTypeId"
              name="activityTypeId"
              value=inputContext.activityTypeId!
            />
          	
          	<@dynaScreen 
				instanceId="PARAM_ACT_TYP"
				modeOfAction="UPDATE"
				/>
          	
          </div>
          <div class="form-group offset-2">
            <div class="text-left ml-3">
                  <input type="submit" class="btn btn-sm btn-primary" onclick="return formSubmission();" value="Update">
               <a href="viewActivityType?enumId=${inputContext.activityTypeId!}"class="btn btn-sm btn-secondary"> Cancel</a>   
            </div>
          </div>
          </form>
        </div>
      </div>
<script>
  function formSubmission(){
    var isValid = "Y";
    var activityType =  $("#activityType").val();
    var activityParent =  $("#activityParent").val();
    var status =  $("#status").val();
    var sequenceNumber =  $("#sequenceNumber").val();
    var seqerr="";
    if(activityType!=''&& status!=''&& activityParent!=''){
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
        if(activityType == "") {
          $("#activityType_error").html('');
          $("#activityType_error").append('<ul class="list-unstyled text-danger"><li id="status_err">Please select Activity Type</li></ul>');
           isValid = "N";
        }
        if(activityParent == "") {
          $("#activityParent_error").html('');
          $("#activityParent_error").append('<ul class="list-unstyled text-danger"><li id="status_err">Please select Activity Parent</li></ul>');
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
$("#activityType").change(function(){  
    if($("#activityType").val() != null){
    $("#activityType_error").html("");  
    }
}); 
$("#activityParent").change(function(){  
    if($("#activityParent").val() != null){
    $("#activityParent_error").html("");  
    }
});
$("#status").change(function(){  
    if($("#status").val() != null){
    $("#status_error").html("");  
}
});
 </script>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>      