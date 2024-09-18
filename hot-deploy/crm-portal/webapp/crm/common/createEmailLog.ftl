<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<div id="emailLogModal" class="modal fade" role="dialog">
<div class="modal-dialog">
<!-- Modal content-->
<div class="modal-content">
<div class="modal-header">
  <h4 class="modal-title">Log Email</h4>
  <button type="reset" class="close" data-dismiss="modal">&times;</button>
</div>
<div class="modal-body">
<div class="">
<form method="post" action="logTask" id="logTaskFormEmail" class="form-horizontal" name="logTaskFormEmail" novalidate="novalidate" data-toggle="validator">
  <#if requestParameters.donePageCallLog?exists && requestParameters.donePageCallLog?has_content>
     <#assign requestURI = "${requestParameters.donePageCallLog}"/>
  <#else>
  <#assign requestURI = "viewContact"/> 
  <#if request.getRequestURI().contains("viewLead")>
    <#assign requestURI = "viewLead"/>
  <#elseif request.getRequestURI().contains("viewAccount")>
    <#assign requestURI = "viewAccount"/>
  </#if>
  </#if>
  <input type="hidden" name="partyId" value="${internalPartyId?if_exists}"/>
  <input type="hidden" name="donePage" value="${requestURI?if_exists}"/>
  <input type="hidden" name="activeTab" value="logCall"/>
  <input type="hidden" value="WEPT_TASK_EMAIL" name="workEffortPurposeTypeId">
  <input type="hidden" value="${internalPartyId?if_exists}" name="internalPartyId">
  <input type="hidden" value="${fromPartyId?if_exists}" name="fromPartyId">
  <input type="hidden" value="Y" name="outbound">
  <input type="hidden" id="duration" size="25" value="1:00" name="duration" class="inputBox"> 
  <input type="hidden" name="actualStartDate" value="" form="logTaskForm">
  <div class="row padding-r">
      <div class="col-md-12 col-sm-12">
       <@generalInput 
       id="workEffortName"
       label=uiLabelMap.subject
       placeholder=uiLabelMap.Subject
       value=""
       required=true
       />
       <div class="form-group row">
         <label  class="col-form-label col-sm-4">${uiLabelMap.message}</label>
         <div class="col-sm-7">
           <textarea class="form-control" id="content" name="content"></textarea>
         </div>
       </div>
    </div>
  </div>
  <div class="clearfix"></div>
    <div class="col-md-12 col-sm-12">
    <div class="form-group row">
      <div class="offset-sm-4 col-sm-9">
        <input type="submit" class="btn btn-sm btn-primary navbar-dark mt-2 ml-1" value="Submit"/>
      </div>
    </div>
  </div>
</form>
</div>
</div>
<div class="modal-footer">
  <button type="sbmit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
</div>
</div>
</div>
</div>