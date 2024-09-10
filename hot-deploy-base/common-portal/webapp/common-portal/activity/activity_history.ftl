<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign requestURI = "viewContact"/> 
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif request.getRequestURI().contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
</#if>

<@pageSectionHeader title="Call Log" 
	extra='<div class="">
      <span class="btn btn-xs btn-primary m5" data-toggle="modal" 
      	data-target="#callLogModal" data-original-title="Log Call">Call Log</span>
   </div>' />
<div class="clearfix"></div>
<@inputHidden id="callLogListData" value=activityHistoryListStr />
<@inputHidden id="donePage" value="${requestURI!}" />
<div class="table-responsive">				
	<div id="callLogGrid" style="width: 100%;" class="ag-theme-balham"></div>   			
</div>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/activity-history.js"></script>