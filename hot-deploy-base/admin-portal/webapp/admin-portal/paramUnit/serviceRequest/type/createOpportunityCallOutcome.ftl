<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros_dbs.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

  <div class="row">
        <div id="main" role="main">
          <#assign extra='<a href="findOpportunityCallOutCome" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeader title="${uiLabelMap.CreateCallOutcome!}" extra=extra />
         
<form id="mainFrom" action ="<@ofbizUrl>createOpportunityCallOutcomeService</@ofbizUrl>" method="post">

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>

          <div class="col-md-12 col-lg-6 col-sm-12 ">
         
          <@inputHidden id="enumTypeId" name="typeId" value="OPP_CALL_OUTCOME"/>
<@inputRow
          id="description"
          label=uiLabelMap.Description
          placeholder = uiLabelMap.Description
        value = "${description?if_exists}"
          maxlength="255"
          required = true
          />
 <#assign isMultiLingualList  = Static["org.fio.admin.portal.util.DataUtil"].toLinkedMap("Y","Yes","N","No") />
          
                   
          </div>
          <div class="form-group offset-2">
            <div class="text-left ml-3">
             
              <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn1onclick="return formSubmission();"
                     btn2=true
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
             
             
            </div>
          </div>
 </form>
        </div>
      </div>

<script>

$("#description").keyup(function() {
   var description = $("#description").val();
   $("#description_error").empty();
     
});


function formSubmission(){
     var isValid = "Y";
    var description =  $("#description").val();
    if(description!=''){
    }else{
        if(description == "") {
           $("#description_error").html('');
           $("#description_error").append('<ul class="list-unstyled text-danger"><li id="description_err">Please Enter Opportunity Call Outcome</li></ul>');
           isValid = "N";
        }
        if(isValid == "N"){
          return false;
        }
        else if(isValid == "Y")
        {
       return true;
        }
    }
}
</script>
   
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>