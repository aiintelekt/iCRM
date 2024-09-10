<#assign requestURI = request.getRequestURI()/>
<#-- <#assign request = requestURI+"?" /> -->

<#assign pretailParamForPersona = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "PERSONA_TAB_ENABLED").queryOne()! />
<#if pretailParamForPersona?exists && pretailParamForPersona?has_content>
	<#assign isPretailParamEnabledForPersona = pretailParamForPersona.value!>
</#if>

<ul class="nav nav-tabs mt-3" id="nav-tab-focus">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link active" href="#details">
			<#--<i class="fa fa-user fa-1" aria-hidden="true"></i>--> Customer Profile 
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contactInfo">
			<#--<i class="fa fa-address-book fa-1" aria-hidden="true"></i>--> Contact Info
		</a>
	</li>
	<#if isPretailParamEnabledForPersona?has_content && isPretailParamEnabledForPersona=="Y">
		<li class="nav-item">
			<a data-toggle="tab" class="nav-link" href="#kpiMetrics">
				<#--<i class="fa fa-microchip fa-1" aria-hidden="true"></i>--> Persona
			</a>
		</li>
	</#if>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#singlePageEdit">
			<#--<i class="fa fa-edit" aria-hidden="true"></i>--> Single Page Edit 
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#serviceRequests">
			<#--<img src="/bootstrap/images/con-icon.png" width="16" height="17">--> Service Requests
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#c-opportunities">
			<#--<img src="/bootstrap/images/add-opportunities.png" width="16" height="16">--> Opportunites
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#c-communicationHistory">
			<#--<i class="fa fa-address-book-o fa-1" aria-hidden="true"></i>--> Communication History
		</a>
	</li>
	<#if isEnabledOrderModule?has_content && isEnabledOrderModule=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#c-orders">
			<#--<i class="fa fa-list" aria-hidden="true"></i>--> Orders
		</a>
	</li>
	</#if>
	<#if isEnableIUCInt?has_content && isEnableIUCInt=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#quotes">
			<#--<i class="fa fa-list" aria-hidden="true"></i>--> Quotes
		</a>
	</li>
	</#if>
	<#if isEnableInvoiceModule?has_content && isEnableInvoiceModule=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#c-invoice">
			<#--<i class="fa fa-group" aria-hidden="true"></i>--> Invoice
		</a>
	</li>
	</#if>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#c-activities">
			<#--<img src="/bootstrap/images/add-activity.png" width="16" height="16">--> Activities
		</a>
	</li>
	
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#c-notes">
			<#--<i class="fa fa-file-text fa-1" aria-hidden="true"></i>--> Notes
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#c-attachments">
			<#--<i class="fa fa-user-plus" aria-hidden="true"></i>--> Bookmarks and Files
		</a>
	</li>
	
	<li class="nav-item" style="display:none">
		<a data-toggle="tab" class="nav-link" href="#administration">
			<#--<i class="fa fa-user-plus" aria-hidden="true"></i>--> Administration
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#c-campaigns">
			<#--<img src="/bootstrap/images/campaign-manager.png" width="16" height="16">--> Campaigns
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#c-mergedParties">
			<#--<i class="fa fa-group" aria-hidden="true"></i>--> Merged Customer
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
	<#if isEnableProgramAct?has_content && isEnableProgramAct=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#programs">
			Programs
		</a>	
	</li>
	</#if>
	<#if isEnableCouponModule?has_content && isEnableCouponModule=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#coupons">
			Coupons
		</a>	
	</li>
	</#if>
	<#if isEnabledReceiptModule?has_content && isEnabledReceiptModule=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#c-receipt">
			<#--<i class="fa fa-list" aria-hidden="true"></i>--> Receipts
		</a>
	</li>
	</#if>
	<#if isEnabledEarnedValueModule?has_content && isEnabledEarnedValueModule=="Y">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#e-value">
			Earned Value
		</a>	
	</li>
	</#if>
</ul>

<script>

$(document).ready(function() {

	<#if !activeTab?has_content>
    	<#assign activeTab = requestParameters.activeTab!>
    </#if>
    
    <#if activeTab?has_content && activeTab == "details">
    	$('.nav-tabs a[href="#details"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "contactInfo">
    	$('.nav-tabs a[href="#contactInfo"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "singlePageEdit">
    	$('.nav-tabs a[href="#singlePageEdit"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "customFields">
    	$('.nav-tabs a[href="#kpiMetrics"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "orders">
    	$('.nav-tabs a[href="#orders"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "serviceRequests">
    	$('.nav-tabs a[href="#serviceRequests"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "opportunites">
    	$('.nav-tabs a[href="#opportunites"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "activities">
    	$('.nav-tabs a[href="#activities"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "accounts">
    	$('.nav-tabs a[href="#accounts"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "notes">
    	$('.nav-tabs a[href="#notes"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "administration">
    	$('.nav-tabs a[href="#administration"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "attachments">
    	$('.nav-tabs a[href="#attachments"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "mergedParties">
    	$('.nav-tabs a[href="#mergedParties"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "invoice">
    	$('.nav-tabs a[href="#c-invoice"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "coupons">
    	$('.nav-tabs a[href="#coupons"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "receipt">
    	$('.nav-tabs a[href="#c-receipt"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "earnedValue">
    	$('.nav-tabs a[href="#e-value"]').tab('show');	
    <#else>
    	$('.nav-tabs a[href="#details"]').tab('show');	
    </#if>
    
});

</script>

<script>

jQuery(document).ready(function() {	



});

</script>