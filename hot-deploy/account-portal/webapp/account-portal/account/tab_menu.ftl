
<#assign requestURI = request.getRequestURI()/>
<#-- <#assign request = requestURI+"?" /> -->

<#assign pretailParamForPersona = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "PERSONA_TAB_ENABLED").queryOne()! />
<#if pretailParamForPersona?exists && pretailParamForPersona?has_content>
	<#assign isPretailParamEnabledForPersona = pretailParamForPersona.value!>
</#if>

<ul class="nav nav-tabs mt-3" id="nav-tab-focus">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link active" href="#a-details">
			<#--<i class="fa fa-user fa-1" aria-hidden="true"></i>--> Account Profile 
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-communicationInfo">
			<i class="fa fa-address-book-o fa-1" aria-hidden="true"></i> Communication History
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-contactInfo">
			<#--<i class="fa fa-address-book fa-1" aria-hidden="true"></i>--> Contact Info
		</a>
	</li>
	<#if isPretailParamEnabledForPersona?has_content && isPretailParamEnabledForPersona=="Y">
		<li class="nav-item">
			<a data-toggle="tab" class="nav-link" href="#a-kpiMetrics">
				<#--<i class="fa fa-microchip fa-1" aria-hidden="true"></i>--> Persona
			</a>
		</li>
	</#if>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-contacts">
			<#--<i class="fa fa-address-book-o fa-1" aria-hidden="true"></i>--> ${uiLabelMap.relatedParties!}
		</a>
	</li>
	<#if isEnableInvoiceModule?has_content && isEnableInvoiceModule=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-invoice">
			<#--<i class="fa fa-group" aria-hidden="true"></i>--> Invoice
		</a>
	</li>
	</#if>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-orders">
			<#--<i class="fa fa-list" aria-hidden="true"></i>--> Orders
		</a>
	</li>
	<#if isEnableIUCInt?has_content && isEnableIUCInt=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#quotes">
			<#--<i class="fa fa-list" aria-hidden="true"></i>--> Quotes
		</a>
	</li>
	</#if>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-opportunities">
			<#--<img src="/bootstrap/images/add-opportunities.png" width="16" height="16">--> Opportunites
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-sr">
			<#--<img src="/bootstrap/images/con-icon.png" width="16" height="17">--> Service Requests
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-activities">
			<#--<img src="/bootstrap/images/add-activity.png" width="16" height="16">--> Activities
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-notes">
			<#--<i class="fa fa-file-text fa-1" aria-hidden="true"></i>--> Notes
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-attachments">
			<#--<i class="fa fa-user-plus" aria-hidden="true"></i>--> Bookmarks and Files
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-onboarding">
			<#--<i class="fa fa-id-badge" aria-hidden="true"></i>--> Onboarding
		</a>
	</li>
	<li class="nav-item" style="display:none">
		<a data-toggle="tab" class="nav-link" href="#administration">
			<#--<i class="fa fa-user-plus" aria-hidden="true"></i>--> Administration
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-campaigns">
			<#--<img src="/bootstrap/images/campaign-manager.png" width="16" height="16">--> Campaigns
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-mergedParties">
			<#--<i class="fa fa-group" aria-hidden="true"></i>--> Merged Account
		</a>
	</li>
	
	<#if isEnableRebateModule?has_content && isEnableRebateModule=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#rebate">
			Rebate
		</a>
	</li>
	</#if>
	<#if isEnableSurvey?has_content && isEnableSurvey=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#survey">
			Survey
		</a>
	</li>
	</#if>
</ul>

<script>

$(document).ready(function() {

	<#if !activeTab?has_content>
    	<#assign activeTab = requestParameters.activeTab!>
    </#if>
    
    <#if activeTab?has_content && activeTab == "a-details">
    	$('.nav-tabs a[href="#a-details"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "communicationInfo">
    	$('.nav-tabs a[href="#communicationInfo"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "contactInfo">
    	$('.nav-tabs a[href="#a-contactInfo"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "customFields">
    	$('.nav-tabs a[href="#a-kpiMetrics"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "orders">
    	$('.nav-tabs a[href="#orders"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "serviceRequests">
    	$('.nav-tabs a[href="#serviceRequests"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "opportunites">
    	$('.nav-tabs a[href="#opportunites"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "activities">
    	$('.nav-tabs a[href="#activities"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "cmpHistory">
    	$('.nav-tabs a[href="#cmpHistory"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "offers">
    	$('.nav-tabs a[href="#offers"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "preferences">
    	$('.nav-tabs a[href="#preferences"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "notes">
    	$('.nav-tabs a[href="#notes"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "alerts">
    	$('.nav-tabs a[href="#alerts"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "contacts">
    	$('.nav-tabs a[href="#contacts"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "administration">
    	$('.nav-tabs a[href="#administration"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "attachments">
    	$('.nav-tabs a[href="#attachments"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "mergedParties">
    	$('.nav-tabs a[href="#mergedParties"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "onboarding">
    	$('.nav-tabs a[href="#onboarding"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "invoice">
    	$('.nav-tabs a[href="#a-invoice"]').tab('show');
    <#else>
    
    	$('.nav-tabs a[href="#details"]').tab('show');	
    </#if>
    
});

</script>

<script>

jQuery(document).ready(function() {	

$("a[href='#accountDetails']").on('shown.bs.tab', function(e) {
	findCaAccounts();
	findHdaFacilities();
});

$("a[href='#loanDetails']").on('shown.bs.tab', function(e) {
	findcaLoanDetails();	
});


});

</script>