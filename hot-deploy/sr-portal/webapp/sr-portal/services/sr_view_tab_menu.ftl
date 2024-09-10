<#assign requestURI = request.getRequestURI()/>
<#assign activeTab = request.getAttribute("activeTab")?if_exists/>
<#-- <#assign request = requestURI+"?" /> -->
<#assign requestFrom = requestParameters.requestFrom! />

<ul class="nav nav-tabs mt-3" id="nav-tab-focus">
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('sr-details')! == "Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link <#if !requestFrom?has_content || (requestFrom?has_content && !("CREATE" == requestFrom!))>active</#if>" href="#sr-details">
			<#-- <i class="fa fa-user fa-1" aria-hidden="true"></i>--> FSR Details 
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('sr-orders')! == "Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link <#if requestFrom?has_content && "CREATE" == requestFrom!>active</#if>" href="#sr-orders">
			<#--<i class="fa fa-list" aria-hidden="true"></i> --> Orders
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('customFields')! == "Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#customFields">
			<#-- <i class="fa fa-microchip fa-1" aria-hidden="true"></i>--> Attributes
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('sr-attachments')! == "Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sr-attachments">
			<#-- <i class="fa fa-paperclip fa-1" aria-hidden="true"></i> --> Bookmarks and Files
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('sr-notes')! == "Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sr-notes">
			<#-- <i class="fa fa-file-text fa-1" aria-hidden="true"></i> --> Notes
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('sr-activities')! == "Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sr-activities">
			<#-- <img src="/bootstrap/images/add-activity.png" width="16" height="16"> --> Activities
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('associated-parties')! == "Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#associated-parties">
			<#-- <i class="fa fa-industry fa-1" aria-hidden="true"></i>--> Associated Parties
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('sr-time-entry')! == "Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sr-time-entry">
			<#-- <i class="fa fa-clock-o fa-1" aria-hidden="true"></i> --> Time Entries
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('sr-issue-material')! == "Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sr-issue-material">
			<#-- <i class="fa fa-industry fa-1" aria-hidden="true"></i> --> Issued Materials
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('sr-history')! == "Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sr-history">
			<#-- <i class="fa fa-history fa-1" aria-hidden="true"></i> --> FSR History
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('sr-communication-history')! == "Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sr-communication-history">
			<#-- <i class="fa fa-history fa-1" aria-hidden="true"></i> --> Communication History
		</a>
	</li>
	</#if>
	<#if enableFsrInvoiceTab?has_content && enableFsrInvoiceTab=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sr-invoice">
			Technician Invoice
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('approval')! == "Y">
	<#if isApprovalEnabled?has_content && isApprovalEnabled=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#approval">
			<#-- <i class="fa fa-history fa-1" aria-hidden="true"></i> --> Approval
		</a>
	</li>
	</#if>
	</#if>
	 
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('sr-administration')! == "Y">
	<li class="nav-item" class="nav-link" href="#sr-administration">
		<a data-toggle="tab" class="nav-link" href="#sr-administration">
			<#-- <i class="fa fa-user-plus" aria-hidden="true"></i> --> Administration
		</a>
	</li>
	</#if>
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('survey')! == "Y">
	<li class="nav-item" class="nav-link" href="#survey">
		<a data-toggle="tab" class="nav-link" href="#survey">
			<#-- <i class="fa fa-user-plus" aria-hidden="true"></i> --> Survey
		</a>
	</li>
	</#if>
	
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('thirdpty-attachment')! == "Y">
	<#if isApprovalEnabled?has_content && isApprovalEnabled=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#thirdpty-attachment">
			<#-- <i class="fa fa-history fa-1" aria-hidden="true"></i> --> 3rd Party Invoice
		</a>
	</li>
	</#if>
	</#if>
		
	<#-- 
	<#if srTabMap?exists && srTabMap?has_content && srTabMap.get('rebate') == "Y">
		<#if isEnableRebateModule?has_content && isEnableRebateModule=="Y">
		<li class="nav-item">
			<a data-toggle="tab" class="nav-link" href="#rebate">
				Rebate
			</a>
		</li>
		</#if>
	</#if>
	 -->
</ul>

<script>

$(document).ready(function() {

	<#if !activeTab?has_content>
    	<#assign activeTab = requestParameters.activeTab!>
    </#if>
    
    <#if activeTab?has_content && activeTab == "details">
    	$('.nav-tabs a[href="#details"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "customFields">
    	$('.nav-tabs a[href="#customFields"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "contacts">
    	$('.nav-tabs a[href="#contacts"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "sr-communication-history">
    	$('.nav-tabs a[href="#sr-communication-history"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "activities">
    	$('.nav-tabs a[href="#activities"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "notes">
    	$('.nav-tabs a[href="#notes"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "attachments">
    	$('.nav-tabs a[href="#attachments"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "administration">
    	$('.nav-tabs a[href="#administration"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "sr-history">
    	$('.nav-tabs a[href="#sr-history"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "orders">
    	$('.nav-tabs a[href="#orders"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "invoices">
    	$('.nav-tabs a[href="#invoices"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "associated-parties">
    	$('.nav-tabs a[href="#associated-parties"]').tab('show');
    <#else>
    	$('.nav-tabs a[href="#details"]').tab('show');	
    </#if>
    
});

</script>
