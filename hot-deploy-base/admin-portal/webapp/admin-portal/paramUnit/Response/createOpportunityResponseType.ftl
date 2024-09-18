<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros_dbs.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

<div class="row">
  <div id="main" role="main">
      <#assign extra='<a href="opportunityResponseType" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
      <@sectionFrameHeader title="${uiLabelMap.CreateOpportunityResponseType!}" extra=extra />
         <form id="mainFrom" action ="<@ofbizUrl>processOppResponseType</@ofbizUrl>" method="post">
             <#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
           <div class="col-md-12 col-lg-6 col-sm-12 ">
                    <#assign status = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "OPP_CALL_OUTCOME")?if_exists />
                   <#assign statusList1 = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(status, "enumId","description")?if_exists /> 
                        <@dropdownCell 
                              id="parentEnumId"
                              name="parentEnumId"
                              label=uiLabelMap.callOutcome
                              placeholder="Select Call OutCome"
                              options=statusList1!
                               value="${requestParameters.parentEnumId?if_exists}"
                              allowEmpty=true
                              />
           </div>
           <div class="col-md-12 col-lg-6 col-sm-12 ">
          <@inputHidden id="typeId" name="typeId" value="OPP_RESPONSE_TYPE"/>
   				 <@inputRow
          		 id="description"
                 label=uiLabelMap.responseType
          		 placeholder = uiLabelMap.Description
                 value = "${description?if_exists}"
                 maxlength="255"
                 required = true
                  />
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
           $("#description_error").append('<ul class="list-unstyled text-danger"><li id="description_err">Please Enter Opportunity Response Reason</li></ul>');
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
