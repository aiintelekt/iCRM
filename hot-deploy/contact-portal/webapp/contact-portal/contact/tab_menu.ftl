
<#assign requestURI = request.getRequestURI()/>
<#-- <#assign request = requestURI+"?" /> -->

<#assign pretailParamForPersona = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "PERSONA_TAB_ENABLED").queryOne()! />
<#if pretailParamForPersona?exists && pretailParamForPersona?has_content>
	<#assign isPretailParamEnabledForPersona = pretailParamForPersona.value!>
</#if>

<ul class="nav nav-tabs mt-3" id="nav-tab-focus">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link active" href="#details">
			<#--<i class="fa fa-user fa-1" aria-hidden="true"></i>--> Contact Profile 
		</a>
	</li>
	<#-- <li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#communicationInfo">
			<i class="fa fa-address-book-o fa-1" aria-hidden="true"></i> Communication 
		</a>
	</li> -->
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contactInfo">
			<#--<i class="fa fa-address-book fa-1" aria-hidden="true"></i>--> Contact Info
		</a>
	</li>
	<#if isPretailParamEnabledForPersona?has_content && isPretailParamEnabledForPersona=="Y">
		<li class="nav-item">
			<a data-toggle="tab" class="nav-link" href="#contact-kpiMetrics">
				<#--<i class="fa fa-microchip fa-1" aria-hidden="true"></i>--> Persona
			</a>
		</li>
	</#if>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contact-rp">
			<#--<i class="fa fa-address-book-o fa-1" aria-hidden="true"></i>--> Related Parties
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contact-sr">
			<#--<img src="/bootstrap/images/con-icon.png" width="16" height="17">--> Service Requests
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contact-opportunities">
			<#--<img src="/bootstrap/images/add-opportunities.png" width="16" height="16">--> Opportunites
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contact-activities">
			<#--<img src="/bootstrap/images/add-activity.png" width="16" height="16">--> Activities
		</a>
	</li>
	
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contact-notes">
			<#--<i class="fa fa-file-text fa-1" aria-hidden="true"></i>--> Notes
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contact-attachments">
			<#--<i class="fa fa-user-plus" aria-hidden="true"></i>--> Bookmarks and Files
		</a>
	</li>
	
	<li class="nav-item" style="display:none">
		<a data-toggle="tab" class="nav-link" href="#contact-administration">
			<#--<i class="fa fa-user-plus" aria-hidden="true"></i>--> Administration
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contact-campaigns">
			<#--<img src="/bootstrap/images/campaign-manager.png" width="16" height="16">--> Campaigns
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contact-mergedParties">
			<#--<i class="fa fa-group" aria-hidden="true"></i>--> Merged Contact
		</a>
	</li>
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
    <#elseif activeTab?has_content && activeTab == "customFields">
    	$('.nav-tabs a[href="#contact-kpiMetrics"]').tab('show');
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