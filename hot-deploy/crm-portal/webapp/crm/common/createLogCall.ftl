<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#if request.getRequestURI().contains("logCall")>
<div class="page-header border-b">
  <#if logTask?exists && logTask?has_content && logTask == "Log Email">
     <h1 class="float-left">Log Email</h1>
  <#else>
     <h1 class="float-left">Log Call</h1>
  </#if>
</div>
<#else>
<div id="callLogModal" class="modal fade" role="dialog">
<div class="modal-dialog">
<!-- Modal content-->
<div class="modal-content">
<div class="modal-header">
  <h4 class="modal-title">Call Log</h4>
  <button type="reset" class="close" data-dismiss="modal">&times;</button>
</div>
<div class="modal-body">
<div class="">
</#if>
<form method="post" action="logTask" id="logTaskFormCall" class="form-horizontal" name="logTaskFormCall" novalidate="novalidate" data-toggle="validator">
  <#if requestParameters.donePageCallLog?exists && requestParameters.donePageCallLog?has_content>
     <#assign requestURI = "${requestParameters.donePageCallLog}"/>
  <#else>
  <#assign requestURI = "viewContact"/> 
  <#if request.getRequestURI().contains("viewLead")>
    <#assign requestURI = "viewLead"/>
  <#elseif request.getRequestURI().contains("viewAccount")>
    <#assign requestURI = "viewAccount"/>
  <#elseif request.getRequestURI().contains("viewCustomer")>
    <#assign requestURI = "viewCustomer"/>
  </#if>
  </#if>
  <input type="hidden" name="partyId" value="${internalPartyId?if_exists}"/>
  <input type="hidden" name="donePage" value="${requestURI?if_exists}"/>
  <input type="hidden" id="activeTabLogCall" name="activeTab" value="opportunites"/>
  <input type="hidden" value="${workEffortPurposeTypeId?if_exists}" name="workEffortPurposeTypeId">
  <input type="hidden" value="${internalPartyId?if_exists}" name="internalPartyId">
  <input type="hidden" value="${fromPartyId?if_exists}" name="fromPartyId">
  <input type="hidden" value="Y" name="outbound">
  <input type="hidden" id="duration" size="25" value="1:00" name="duration" class="inputBox"> 
  <input type="hidden" name="actualStartDate" value="" form="logTaskForm">
  <div class="row padding-r">
    <#if request.getRequestURI().contains("logCall")>
      <div class="col-md-6 col-sm-6">
    <#else>
      <div class="col-md-12 col-sm-12">
    </#if>
      <#--<div class="form-group row ">
        <label  class="col-sm-4 col-form-label">Type</label>
        <div class="col-sm-7">
            <#if workEffortPurposeTypeId?if_exists == "WEPT_TASK_PHONE_CALL">
                Phone Call
            <#elseif workEffortPurposeTypeId?if_exists == "WEPT_TASK_EMAIL">
                Email
            <#elseif workEffortPurposeTypeId?if_exists == "WEPT_TASK_CHAT">
                Chat
            </#if>
        </div>
      </div>
      <@inputRow 
       id="internalPartyId"
       label=uiLabelMap.internalParty
       placeholder=uiLabelMap.internalParty
       value="${internalPartyId?if_exists}"
       required=true
       />-->
       <@inputRow 
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
      <#--<div class="form-group row has-error">
        <label  class="col-sm-4 col-form-label">Start Date</label>
        <div class="col-sm-7">
          <div class="input-group date" id="datetimepicker7">
            <input type='text' class="form-control input-sm" placeholder="YYYY-MM-DD HH:MM:SS" data-date-format="YYYY-MM-DD hh:mm:ss"/>
            <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
            </span>
          </div>
          <div class="help-block with-errors"></div>
        </div>
      </div>
    </div>-->
    
    <#--<div class="col-md-6 col-sm-6">
      <div class="form-group row has-error">
        <label  class="col-sm-4 col-form-label">${uiLabelMap.inboundOrOutbound}</label>
        <div class="col-sm-7">
          <select class="custom-select ui dropdown form-control input-sm" data-live-search="true" id="outbound" name="outbound">
            <option value="Y">Outbound</option>
            <option value="N">Inbound</option>
          </select>
          <div class="help-block with-errors"></div>
        </div>
      </div>-->
      
      <#--<@generalInput 
      id="fromPartyId"
      label=uiLabelMap.externalParty
      placeholder=uiLabelMap.externalParty
      value="${fromPartyId?if_exists}"
      required=false
      />
      <@inputRow 
      id="salesOpportunityId"
      label=uiLabelMap.opportunity
      placeholder=uiLabelMap.opportunity
      value=""
      required=false
      />
      <@inputRow 
      id="duration"
      label=uiLabelMap.duration
      placeholder="1:00"
      value="1:00"
      required=false
      />-->
      
    </div>
  </div>
  <div class="clearfix"></div>
  <#--<div class="row padding-r">
    <label  class="col-form-label col-sm-4">${uiLabelMap.message}</label>
    <div class="col-sm-7">
      <textarea class="form-control" id="content" name="content"></textarea>
      <script>
        $(document).ready(function() {
            $('#contentCallLog').summernote({
              tabsize: 2,
              height: 100
            });
        });
      </script>
    </div>
  </div>-->
  <#if request.getRequestURI().contains("logCall")>
    <div class="col-md-6 col-sm-6">
  <#else>
    <div class="col-md-12 col-sm-12">
  </#if>
    <div class="form-group row">
      <div class="offset-sm-4 col-sm-9">
        <#--<button type="reset" class="btn btn-sm btn-primary navbar-dark mt">Create</button>-->
        <@submit class="btn btn-sm btn-primary navbar-dark mt-2 ml-1" label="Submit"/>
        <#--<button type="submit" class="btn btn-sm btn-primary navbar-dark mt" data-dismiss="modal">Clear</button>-->
      </div>
    </div>
  </div>
</form>
<#if request.getRequestURI().contains("logCall")>

<#else>
</div>
</div>
<div class="modal-footer">
  <button type="submit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
</div>
</div>
</div>
</div>
</#if>
<#include "component://crm/webapp/crm/common/createEmailLog.ftl"/>