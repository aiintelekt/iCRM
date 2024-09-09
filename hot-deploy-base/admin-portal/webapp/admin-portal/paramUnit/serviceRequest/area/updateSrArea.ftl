<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

      <div class="row">
        <div id="main" role="main">
          <#assign extra='<a href="viewServiceRequestArea?custRequestCategoryId=${inputContext.custRequestCategoryId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

<form action ="<@ofbizUrl>updateSrArea</@ofbizUrl>" method="post">

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>

          <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
         
         <@sectionFrameHeaderTab title="${uiLabelMap.UpdateSrCategory!}" tabId="UpdateSrCategory"  extra=extra/> 
          
          	<@inputHidden 
                          id="custRequestCategoryId"
                          name="custRequestCategoryId"
                          value = inputContext.custRequestCategoryId
                        />
          
          	<@dynaScreen 
				instanceId="PARAM_SR_AREA"
				modeOfAction="UPDATE"
				/>
          
          <div class="form-group offset-2">
            <div class="text-left ml-1 pad-10">
            <input type="submit" class="btn btn-sm btn-primary" onclick="return formSubmission();" value="Update">
                             <a href="viewServiceRequestArea?custRequestCategoryId=${custRequestCategoryId!}"class="btn btn-sm btn-secondary"> Cancel</a>
              
          <#--    <@fromCommonAction showCancelBtn=false showClearBtn=true/> --> 
              
              <#-- 
              <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Update}"
                     btn1onclick="return formSubmission();"
                     btn2=true
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
                -->
                
                        
            </div>
          </div>
          
          </div>
          </form>
        </div>
      </div>

<script>
   $("#srArea").keyup(function() {
   var srArea = $("#srArea").val();
   $("#srArea_error").empty();
      
});

 $("#typeId").change(function() {
   var typeId = $("#typeId").val();
   $("#typeId_error").empty();
      
});
 $("#status").change(function() {
   var status = $("#status").val();
   $("#status_error").empty();
      
});

function formSubmission(){
    var isValid = "Y";
    var typeId =  $("#typeId").val();
    var status =  $("#status").val();
    var srArea =  $("#srArea").val();
    var sequenceNumber =  $("#sequenceNumber").val();
    var seqerr="";
    if(typeId!=''&& status!=''&& srArea!=''){
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
    }else{
        //alert($("#createHolidayConfig").serialize());
        if(typeId == "") {
           $("#typeId_error").html('');
           $("#typeId_error").append('<ul class="list-unstyled text-danger"><li id="srTypeId_err">Please select SR Type</li></ul>');
            isValid = "N";
        }
        if(status == "") {
          $("#status_error").html('');
          $("#status_error").append('<ul class="list-unstyled text-danger"><li id="status_err">Please select Status </li></ul>');
           isValid = "N";
        }
        if(srArea == "") {
          $("#srArea_error").html('');
          $("#srArea_error").append('<ul class="list-unstyled text-danger"><li id="status_err">Please enter SR Category </li></ul>');
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
   </script>   
   
