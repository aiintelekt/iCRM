<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main" class="pd-btm-title-bar">
   <#if requestParameters.enumId?exists>
   <#assign extra='<a href="findOpportunityCallOutCome" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      <@sectionFrameHeader 
            title="${uiLabelMap.EditOpportunityCallOutcome!}"
            extra=extra!
            />
   <#assign enumDetail = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("enumId","description","isMultiLingual","enumTypeId").from("Enumeration").where("enumId",requestParameters.enumId!).queryOne())?if_exists />
   <form method="post" action="oppCallOutcomeUpdation" name="createSecurityRoleForm" id="createSecurityRoleForm" data-toggle="validator">
   <#else>
   <#assign extra='<a href="findOpportunityCallOutCome" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      <@sectionFrameHeader 
            title="${uiLabelMap.EditOpportunityCallOutcome!}"
            extra=extra!
            />
    <form method="post" action="oppCallOutcomeUpdation" name="editOpportunityCallOutcomeForm" id="editOpportunityCallOutcomeForm"  >
   </#if>
    
      <div class="col-lg-12 col-md-12 col-sm-12">
         <div class="row">
            <div class="col-md-12 col-lg-6 col-sm-12 ">
                <@inputHidden id="enumId"
                     name="enumId"
                     value="${enumDetail.enumId!}" />
                <@inputHidden id="typeId"
                     name="typeId"
                     value="${enumDetail.enumTypeId!}" />
                <@inputRow
                    id="description"
                    label="${uiLabelMap.Description!}"
                    placeholder="${enumDetail.description!}"
                    required=true
                    value="${enumDetail.description!}"
                    maxlength=100
                    />
          
            </div>
         </div>
         <div class="form-group offset-2">
            <div class="text-left ml-1">
               <@submit 
                   label="${uiLabelMap.Update!}"
                   />
               <@cancel
                   label="${uiLabelMap.Cancel!}"
                   onclick="findOpportunityCallOutCome?enumId=requestParameters.enumId?exists"
                   />
           
            </div>
         </div>
      </div>
    </form>
    </div>
</div>
