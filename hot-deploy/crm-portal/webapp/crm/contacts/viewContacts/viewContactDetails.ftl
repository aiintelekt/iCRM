<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="page-header pb-1">
   <div class="col-md-6 col-sm-6 float-right">
      <div class="col-md-12 col-sm-12 form-horizontal">
         <div class="row">
            <div class="col-md-5 col-sm-5 pl-0">
               <div class="form-group">
                  <#assign enumerationCallStatusList  = delegator.findByAnd("Enumeration",Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId","CALL_STATUS"),Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId"), false)/>
                  <#-- <@dropdownCell
                    name="callStatusForm"
                    id="callStatusForm"
                    options=enumerationCallStatusList
                    allowEmpty=true
                    placeholder ="Select Call Status"
                    /> -->
                  <select id="callStatusForm" name="callStatusForm" class="ui dropdown search form-control input-sm">
                     <option value=""> Select Call Status </option>
                     <#if enumerationCallStatusList?exists && enumerationCallStatusList?has_content>
                     <#list enumerationCallStatusList as enumerationCallStatus>
                        <option value="${enumerationCallStatus.enumId?if_exists}">${enumerationCallStatus.description?if_exists}</option>
                     </#list>
                     </#if>
                  </select>
               </div>
            </div>
            <div class="col-md-5 col-sm-5 pl-0">
               <div class="form-group">
                  <div class="input-group date" id="datetimepicker11">
                     <input type='text' class="form-control input-sm" name="callBackDateForm" id="callBackDateForm" data-date-format="DD-MM-YYYY" value="${callBackDateOCL?if_exists}" placeholder="DD-MM-YYYY" />
                     <span class="input-group-addon mr-2"><span class="glyphicon glyphicon-calendar"></span>
                     </span>
                     <@submit id="updateCallStatus" label="Save" />
                  </div>
               </div>
            </div>
            <div class="col-md-2 col-sm-2 px-0">
               <div class="form-group">
                  <span class="fa fa-files-o btn btn-xs btn-primary m5" data-toggle="modal" data-target="#myModal" alt="Assign to me" title="Assign to me"></span>
                  <a href="updateContactForm?partyId=${partySummary?if_exists.partyId?if_exists}"><span class="fa fa-edit btn btn-xs btn-primary m5" title="Edit"></span></a>				 
                  <span class="fa fa-times btn btn-xs btn-danger m5" data-toggle="modal" data-target="#myModal" alt="Deactivate Contact" title="Deactivate Contact"></span>
               </div>
            </div>
         </div>
      </div>
   </div>
   <div class="col-md-6 col-sm-6 pl-0">
      <h2 class="d-inline-block">Contact</h2>
   </div>
   <form name="callStatusUpdate" id="callStatusUpdate" method="post" action="<@ofbizUrl>callStatusUpdate</@ofbizUrl>"/>
      <input type="hidden" name="partyId" id="partyId" value="${partySummary?if_exists.partyId?if_exists}" />
      <input type="hidden" name="donePage" value="viewContact"/>
      <input type="hidden" name="callStatus" id="callStatus" value=""/>
      <input type="hidden" name="callBackDate" id="callBackDate" value=""/>
      <input type="hidden" name="marketingCampaignId" value="${marketingCampContList?if_exists.marketingCampaignId?if_exists}"/>
      <input type="hidden" name="contactListId" value="${marketingCampContList?if_exists.contactListId?if_exists}"/>
      <input type="hidden" name="campaignListId" id="campaignListId" value="${marketingCampContList?if_exists.contactListId?if_exists}"/>
   </form>
</div>
<div class="row padding-r ">
   <div class="col-md-4 col-sm-4 form-horizontal">
      <div class="form-group row has-error mb-0">
         <label  class="col-sm-5 col-form-label">Name</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.firstName?if_exists} ${partySummary?if_exists.lastName?if_exists} (${partySummary?if_exists.partyId?if_exists})</label>
         </div>
      </div>
      <div class="form-group row has-error mb-0">
         <label  class="col-sm-5 col-form-label has-error">${uiLabelMap.firstName}</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.firstName?default("-")}</label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">${uiLabelMap.lastName}</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.lastName?default("-")}</label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">${uiLabelMap.birthDate}</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.birthDate?default("-")}</label>
         </div>
      </div>
     <#-- <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Person Responsible For</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if personResponsible?exists && personResponsible?has_content>${personResponsible?if_exists}<#else>${uiLabelMap.CommonNone}</#if></label>
            <span class="fa fa-user-o btn btn-xs btn-primary" data-toggle="modal" data-target="#teamMemberModal" alt="Reassign" title="Reassign"></span>
         </div>
      </div>-->
   </div>
   <div class="col-md-4 col-sm-4 form-horizontal">
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">${uiLabelMap.salutation}</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.personalTitle?if_exists}</label>
         </div>
      </div>
     <#--  <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">${uiLabelMap.preferredCurrency}</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.preferredCurrencyUomId?if_exists}</label>
         </div>
      </div> -->
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">${uiLabelMap.department}</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.departmentName?default("-")}</label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">${uiLabelMap.designation}</label>
         <div class="col-sm-7">
            <#assign hdpContactDesigAssoc  = delegator.findByAnd("HdpContactDesignationAssoc",Static["org.ofbiz.base.util.UtilMisc"].toMap("contactId","${partySummary?if_exists.partyId?if_exists}"),Static["org.ofbiz.base.util.UtilMisc"].toList("hdpContactDesignationAssocId"), false)/>
            <#if hdpContactDesigAssoc?exists && hdpContactDesigAssoc?has_content>
               <#list hdpContactDesigAssoc as hdpContactDesigAssocGV>
                 <#if hdpContactDesigAssocGV?exists && hdpContactDesigAssocGV.designationName?has_content>
                 <label class="col-form-label input-sm fw">${hdpContactDesigAssocGV.designationName?default("-")}</label>
                 </#if>
			</#list>
			<#else>
			<label class="col-form-label input-sm fw">-</label>
			</#if>
         </div>
      </div>
   </div>
   <div class="col-md-4 col-sm-4 form-horizontal">
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">${uiLabelMap.description}</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.description?default("-")}</label>
         </div>
      </div>
      <#--<div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">${uiLabelMap.note}</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.importantNote?if_exists}</label>
         </div>
      </div>-->
      <div class="form-group row mb-0">
      		 <#assign classifications = delegator.findByAnd("CustomFieldPartyClassification",{"groupId","GENDER","partyId","${partySummary?if_exists.partyId?if_exists}"},[], false)?if_exists/>
             <#if classifications?exists && classifications?has_content>
             	<#assign gender = classifications.get(0).customFieldId?if_exists />
             	<#assign customField = delegator.findOne("CustomField",{"customFieldId",gender},false)?if_exists />
             </#if>
         <label  class="col-sm-5 col-form-label">${uiLabelMap.gender}</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if customField?has_content>${customField?if_exists.customFieldName?default("-")}<#else>-</#if></label>
         </div>
      </div>
      <#-- <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">${uiLabelMap.marketingCampaign}</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${marketingCampaigns?if_exists.campaignName?if_exists}</label>
         </div>
      </div>-->
   </div>
</div>
<div class="clearfix"> </div>
<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">

<#-- Call Status History -->
<div class="panel panel-default">
   <div class="panel-heading" role="tab" id="headingTwo">
      <h4 class="panel-title">
         <a class="panel-collapse collapse show" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#callHistory" aria-expanded="false" aria-controls="headingTwo">
         Call Status History
         </a>
      </h4>
   </div>
   <div id="callHistory" class="panel-collapse collapse show" data-parent="#accordionMenu" aria-labelledby="CallHistory">
      <div class="panel-body">
         ${screens.render("component://crm/webapp/widget/crm/screens/common/CommonScreens.xml#callHistory")}
      </div>
   </div>
</div>

<#-- <div class="panel panel-default">
   <div class="panel-heading" role="tab" id="headingTwo">
      <h4 class="panel-title">
         <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#PendingActivities" aria-expanded="false" aria-controls="headingTwo">
         Pending Activities
         </a>
      </h4>
   </div>
 <div id="PendingActivities" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="PendingActivities">
      <div class="panel-body">
         <div class="float-right">   <button type="reset" class="btn btn-xs btn-primary m5"> New Event</button><button type="reset" class="btn btn-xs btn-primary m5"> New Activity</button>
         </div>
         <div class="table-responsive">
            <table class="table table-striped">
               <thead>
                  <tr>
                     <th>Activity</th>
                     <th>Sequence Number</th>
                     <th>Status</th>
                     <th>Resource</th>
                     <th>Scheduled Date</th>
                     <th>Due Date</th>
                     <th>Type</th>
                     <th>Purpose</th>
                  </tr>
               </thead>
               <tbody>
                  <tr>
                     <td colspan="8">No Orders Found</td>
                  </tr>
               </tbody>
            </table>
         </div>
      </div>
   </div>
</div>
<!--div class="panel panel-default">
   <div class="panel-heading" role="tab" id="headingTwo">
     <h4 class="panel-title">
       <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#OpenOrders" aria-expanded="false" aria-controls="headingTwo">
       Open Orders
       </a>
     </h4>
   </div>
   <div id="OpenOrders" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="PendingActivities">
     <div class="panel-body">
       <p class="float-right">   <a type="reset" class="btn btn-xs btn-primary mt"> Orders</a>
       </p>
       <div class="table-responsive">
         <table class="table table-striped">
           <thead>
             <tr>
               <th>Order Date</th>
               <th>Order Name and ID</th>
               <th>PO #</th>
               <th>Customer</th>
               <th>Status</th>
               <th>Ship Before Date</th>
               <th>Amount</th>
             </tr>
           </thead>
           <tbody>
             <tr>
               <td colspan="7">No Orders Found</td>
             </tr>
           </tbody>
         </table>
       </div>
     </div>
   </div>
   </div>
<#-- <div class="panel panel-default">
   <div class="panel-heading" role="tab" id="headingTwo">
      <h4 class="panel-title">
         <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#Payment" aria-expanded="false" aria-controls="headingTwo">
         Payment and Shipping Accounts
         </a>
      </h4>
   </div>
   <div id="Payment" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="PendingActivities">
      <div class="panel-body">
         <p class="float-right">
            <select class="ui dropdown form-control input-sm" data-live-search="true">
               <option value="" >Create New</option>
               <option value="" >Begins With</option>
               <option value="" >Is Empty</option>
               <option value="" >Not Equal</option>
            </select>
         </p>
         <div class="clearfix"> </div>
         No payment method information on file.
         <div class="clearfix"> </div>
      </div>
   </div>
</div>-->
<#--<div class="panel panel-default">
   <div class="panel-heading" role="tab" id="headingTwo">
      <h4 class="panel-title">
         <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#ActivitiesHistory" aria-expanded="false" aria-controls="headingTwo">
         Activities History
         </a>
      </h4>
   </div>
   <div id="ActivitiesHistory" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="PendingActivities">
      <div class="panel-body">
         <div class="float-right">   <button type="reset" class="btn btn-xs btn-primary m5"> Log Call</button> <button type="reset" class="btn btn-xs btn-primary m5"> Log Email</button>
         </div>
         <div class="table-responsive">
            <table class="table table-striped">
               <thead>
                  <tr>
                     <th>Type</th>
                     <th>Purpose</th>
                     <th>Activity</th>
                     <th>Status</th>
                     <th>Actual Started Date</th>
                     <th>Actual Completion Date</th>
                     <th>Remove</th>
                  </tr>
               </thead>
               <tbody>
                  <tr>
                     <td colspan="7">No records to display</td>
                  </tr>
               </tbody>
            </table>
         </div>
      </div>
   </div>
</div>

<#include "component://crm/webapp/crm/common/bookMark.ftl" />-->
</div>
<#-- ${screens.render("component://crm/widget/screens/contacts/ContactScreens.xm#createBookMarkUrl")} -->
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

