
<#assign requestURI = request.getRequestURI()/>
<#-- <#assign request = requestURI+"?" /> -->

<#assign pretailParamForPersona = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "PERSONA_TAB_ENABLED").queryOne()! />
<#if pretailParamForPersona?exists && pretailParamForPersona?has_content>
	<#assign isPretailParamEnabledForPersona = pretailParamForPersona.value!>
</#if>

<ul class="nav nav-tabs mt-3" id="nav-tab-focus">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link active" href="#details">
			<#--<i class="fa fa-user fa-1" aria-hidden="true"></i> --> Lead Profile 
		</a>
	</li>
	<#-- <li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#communicationInfo">
			<i class="fa fa-address-book-o fa-1" aria-hidden="true"></i> Communication 
		</a>
	</li> -->
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contactInfo">
			<#--<i class="fa fa-address-book fa-1" aria-hidden="true"></i> --> Contact Info
		</a>
	</li>
	<#if isPretailParamEnabledForPersona?has_content && isPretailParamEnabledForPersona=="Y">
		<li class="nav-item">
			<a data-toggle="tab" class="nav-link" href="#lead-persona">
				<#--<i class="fa fa-microchip fa-1" aria-hidden="true"></i>  --> Persona
			</a>
		</li>
	</#if>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contacts">
			<#--<i class="fa fa-address-book-o fa-1" aria-hidden="true"></i> --> ${uiLabelMap.relatedParties!}
		</a>
	</li>
	<#if leadStatusId?has_content && leadStatusId=="LEAD_QUALIFIED">
		<li class="nav-item">
			<a data-toggle="tab" class="nav-link" href="#opportunities">
				<#--<img src="/bootstrap/images/add-opportunities.png" width="16" height="16"> --> Opportunites
			</a>
		</li>
	</#if>
	<#if leadStatusId?has_content && leadStatusId=="LEAD_QUALIFIED">
		<li class="nav-item">
			<a data-toggle="tab" class="nav-link" href="#lead-sr">
				<#-- <img src="/bootstrap/images/con-icon.png" width="16" height="16"> --> Service Requests 
			</a>
		</li>	
	</#if>
		<li class="nav-item">
			<a data-toggle="tab" class="nav-link" href="#lead-activities">
				<#-- <img src="/bootstrap/images/add-activity.png" width="16" height="16"> --> Activities
			</a>
		</li>
	
	
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#lead-notes">
			<#-- <i class="fa fa-file-text fa-1" aria-hidden="true"></i> --> Notes
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#lead-attachments">
			<#-- <i class="fa fa-user-plus" aria-hidden="true"></i>  --> Bookmarks and Files
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#lead-history">
			<#-- <i class="fa fa-user-plus" aria-hidden="true"></i>  --> Lead History
		</a>
	</li>
	
	<li class="nav-item" style="display:none">
		<a data-toggle="tab" class="nav-link" href="#lead-administration">
			<#-- <i class="fa fa-user-plus" aria-hidden="true"></i>  --> Administration
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#lead-campaigns">
			<#-- <img src="/bootstrap/images/campaign-manager.png" width="16" height="16">  --> Campaigns
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#lead-mergedParties">
			<#-- <i class="fa fa-group" aria-hidden="true"></i>  --> Merged Lead
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
    <#elseif activeTab?has_content && activeTab == "communicationInfo">
    	$('.nav-tabs a[href="#communicationInfo"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "contactInfo">
    	$('.nav-tabs a[href="#contactInfo"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "lead-attribute">
    	$('.nav-tabs a[href="#lead-persona"]').tab('show'); 
    <#elseif activeTab?has_content && activeTab == "customFields">
    	$('.nav-tabs a[href="#lead-persona"]').tab('show'); 	  
    <#elseif activeTab?has_content && activeTab == "opportunites">
    	$('.nav-tabs a[href="#opportunites"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "activities">
    	$('.nav-tabs a[href="#lead-activities"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "cmpHistory">
    	$('.nav-tabs a[href="#cmpHistory"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "offers">
    	$('.nav-tabs a[href="#offers"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "preferences">
    	$('.nav-tabs a[href="#preferences"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "alerts">
    	$('.nav-tabs a[href="#alerts"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "contacts">
    	$('.nav-tabs a[href="#contacts"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "notes">
    	$('.nav-tabs a[href="#lead-notes"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "administration">
    	$('.nav-tabs a[href="#lead-administration"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "attachments">
    	$('.nav-tabs a[href="#lead-attachments"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "mergedParties">
    	$('.nav-tabs a[href="#lead-mergedParties"]').tab('show');	
    <#else>
    
    	$('.nav-tabs a[href="#details"]').tab('show');	
    </#if>
    
});

</script>

<script>

jQuery(document).ready(function() {	



});

</script>