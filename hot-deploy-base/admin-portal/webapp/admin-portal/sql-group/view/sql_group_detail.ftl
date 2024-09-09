<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<#assign updateUrl = "main">

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "") />  
<div class="page-header border-b pt-2">
  <h2 class="d-inline-block">Details</h2>
   <ul class="flot-icone">
     
     <#-- <#if (inputContext.currentStatusId!="IA_MCOMPLETED" && inputContext.currentStatusId !="IA_CLOSED")> -->
     <li class="mt-0">
        <a href="/admin-portal/control/updateSqlGroup?sqlGroupId=${inputContext.sqlGroupId!}" class="btn btn-xs btn-primary m5"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
     </li>
     <#-- </#if> -->
      <span calss="float-right" id="act-details">${helpUrl?if_exists}</span>
  </ul>
</div>

<div class="col-md-12 col-lg-12 col-sm-12">
			
	<@dynaScreen 
	instanceId="SQLGRP_BASE"
	modeOfAction="VIEW"
	/>

</div>

<script>     
$(document).ready(function() {

});
</script>