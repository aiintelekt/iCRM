<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
		 <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "contactInfo") />  
	
<div class="pt-2">
      <h2 class="d-inline-block">General Details</h2>
       <ul class="flot-icone">
         <#-- <li class="mt-0"><a href="#" class=" text-dark"><i class="fa fa-refresh fa-1" aria-hidden="true"></i> </a> </li> -->
          <li class="mt-0">
         <#if partySummary.lastModifiedDate?has_content> 
            <small>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(partySummary.lastModifiedDate!, "yyyy-MM-dd")}</small>
         </#if>
         </li>
         <#if partySummary.statusId?has_content && partySummary.statusId == "PARTY_ENABLED"> 
         <li class="mt-0">
            <a href="<@ofbizUrl>updateContact?partyId=</@ofbizUrl>${inputContext.partyId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
         </li>
         </#if>
         <li>${helpUrl?if_exists}</li>

      </ul>
     
</div>

<div class="col-md-12 col-lg-12 col-sm-12">
			
	<@dynaScreen 
	instanceId="VIEW_CONT_BASE"
	modeOfAction="VIEW"
	/>
	
</div>