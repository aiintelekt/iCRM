<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<@pageSectionHeader title=uiLabelMap.customer 
	extra='<div class="form-group">
              <span class="fa fa-files-o btn btn-xs btn-primary m5" data-toggle="modal" data-target="#myModal" alt="Assign to me" title="Assign to me"></span>
              <a href="updateContactForm?partyId=${partySummary?if_exists.partyId?if_exists}"><span class="fa fa-edit btn btn-xs btn-primary m5" title="Edit"></span></a>				 
              <span class="fa fa-times btn btn-xs btn-danger m5" data-toggle="modal" data-target="#myModal" alt="Deactivate Contact" title="Deactivate Contact"></span>
           </div>' />
<div class="row padding-r ">
   <div class="col-md-4 col-sm-4 form-horizontal">
   		<@displayCell label="Name" 
   			value="${partySummary?if_exists.firstName?if_exists} ${partySummary?if_exists.lastName?if_exists} (${partySummary?if_exists.partyId?if_exists})" />
		<@displayCell label="${uiLabelMap.firstName}" 
   			value="${partySummary?if_exists.firstName?default('-')}" />
		<@displayCell label="${uiLabelMap.lastName}" 
   			value="${partySummary?if_exists.lastName?default('-')}" />
   </div>
   <div class="col-md-4 col-sm-4 form-horizontal">
   		<@displayCell label="${uiLabelMap.salutation}" 
   			value="${partySummary?if_exists.personalTitle?if_exists}" />
		<#assign gender = delegator.findByAnd("Enumeration",{"enumTypeId","GENDER","enumCode","${partySummary?if_exists.gender?if_exists}"},[], false)?if_exists/>
     	<#if gender?exists && gender?has_content>
         	<#assign gender = gender.get(0).description?if_exists />
         </#if>
		<@displayCell label="${uiLabelMap.gender}" 
   			value="${gender?default('-')}" />
		<@displayCell label="${uiLabelMap.birthDate}" 
   			value="${partySummary?if_exists.birthDate?default('-')}" />
   </div>
   <div class="col-md-4 col-sm-4 form-horizontal">
   		<@displayCell label="${uiLabelMap.description}" 
   			value="${partySummary?if_exists.description?default('-')}" />
		<@displayCell label="${uiLabelMap.department}" 
   			value="${partySummary?if_exists.departmentName?default('-')}" />      
   </div>
</div>
<div class="clearfix"> </div>
<script>
$("#updateCallStatus").click(function () {
   var form = document.getElementById("callStatusUpdate");
   var callStatus = $("#callStatusForm").val();
   var callBackDate = $("#callBackDateForm").val();
   if((callStatus != null && callStatus != "") || (callBackDate != null && callBackDate != "")) {
      form.callStatus.value = callStatus;
      form.callBackDate.value = callBackDate;
      form.submit();
   } else {
      $.notify({
      message : '<p>Select Call Status or Call Back Date</p>',
   });
   }
});
</script>

