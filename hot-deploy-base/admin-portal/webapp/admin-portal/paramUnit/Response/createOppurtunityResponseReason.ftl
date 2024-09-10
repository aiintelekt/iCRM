 <#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/> 
<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros_dbs.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
	
  <div class="row">
        <div id="main" role="main">
          <#assign extra='<a href="oppResponseReason" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeader title="${uiLabelMap.CreateResponseReason!}" extra=extra />
         
<form id="mainFrom" action ="<@ofbizUrl>processOppResponseReason</@ofbizUrl>" method="post">

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
		<div class="col-md-12 col-lg-6 col-sm-12 ">
		    <#assign statuses = delegator.findByAnd("Enumeration", {"enumTypeId" : "OPP_CALL_OUTCOME"}, null, false)>
                      <#assign statusList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(statuses, "enumId","description")?if_exists />
                        
                          <@dropdownCell 
                              id="parentEnumId"
                              label=uiLabelMap.oppoCallOutcome
                              name="parentEnumId"
                              placeholder="Select Opportunity Call Outcome"
                              options=statusList!
                               value="${requestParameters.oppoCallOutcome?if_exists}"
                              allowEmpty=true
                              />
                           </div> 
  <@inputHidden id="oppoCallOutcome" name="oppoCallOutcome" value="parentEnumId" />
        <div class="col-md-12 col-lg-6 col-sm-12 ">
        <#assign statuses = delegator.findByAnd("Enumeration", {"enumTypeId" : "OPP_RESPONSE_TYPE"}, null, false)>
        <#assign statusList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(statuses, "enumId","description")?if_exists />
                          <@dropdownCell
	                        id="responseTypeId" 
	                        label=uiLabelMap.responseTypeId
	                        name="responseTypeId" 
	                        placeholder="Select Opportunity Response Type"      
	                        allowEmpty=true   
	                        options=statusList!
	                        value="${requestParameters.responseTypeId?if_exists}"
	                             
                        /> 
                       </div>  
          <div class="col-md-12 col-lg-6 col-sm-12 ">
          <#assign enumDetail = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("enumId","enumTypeId","description","isMultiLingual").from("Enumeration").where("enumId",requestParameters.enumId!).queryOne())?if_exists />
           <@inputHidden id="enumId" name="enumId" value="${enumDetail.enumId?if_exists}"/>
           <#if enumDetail?has_content && enumDetail.enumTypeId?has_content>
           		<@inputHidden id="typeId" name="typeId" value="${enumDetail.enumTypeId?if_exists}"/>
           <#else>
           		<@inputHidden id="typeId" name="typeId" value="OPP_RESPONSE_REASON"/>
           </#if>
			<@inputRow
         		id="description"
         		label=uiLabelMap.Description
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

    
 $("#parentEnumId").change(function() {
         var parentEnumId =  $("#parentEnumId").val();
    var oppoCallOutcome  = $("#parentEnumId").val(); 
 if (oppoCallOutcome != "") {
        loadResponseType(oppoCallOutcome);
    }else{
    	$("#responseTypeId").html('');
    	$("#oppoResponseReasonId").html('');
    }
  
    });
 $("#oppoCallOutcome").change(function() {
    var oppoCallOutcome  = $("#oppoCallOutcome").val();
});
   function loadResponseType(oppoCallOutcome) {
        var nonSelectContent = "<span class='nonselect'>Please Select ResponseType</span>";
        var responseTypeOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
     
        $.ajax({
            type: "POST",
            url: "getOppoResponseReasonType",
            data: { "oppoCallOutcome": oppoCallOutcome },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
                   for (var i = 0; i < data.length; i++) {
                        var responseType = data[i];
                        responseTypeOptions += '<option value="'+responseType.enumId+'">'+responseType.description+'</option>';
                    }
            }
        });
       
        $("#responseTypeId").html(responseTypeOptions);
}
</script>
   
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>
   