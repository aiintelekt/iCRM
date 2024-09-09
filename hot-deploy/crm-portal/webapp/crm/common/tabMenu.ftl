
<#assign requestURI = request.getRequestURI()/>
<#assign request = requestURI+"?" />

<ul class="nav nav-tabs">
   <li class="nav-item"><a data-toggle="tab" href="#details" class="nav-link">Basic Details </a></li>
   <li class="nav-item"><a data-toggle="tab" href="#contactInfo" class="nav-link">Contact Information</a></li>
   <#--<#if request?contains("viewAccount?")> 
       <li class="nav-item"><a data-toggle="tab" href="#contact" class="nav-link">Contacts</a></li>
   <#elseif request?contains("viewContact?")>
       <li class="nav-item"><a data-toggle="tab" href="#accounts" class="nav-link">Accounts</a></li>
   </#if>
   <#if request?contains("viewAccount?")>
       <li class="nav-item"><a data-toggle="tab" href="#accountDetails" class="nav-link">Account Details</a></li>
   </#if>-->
   <#-- <li class="nav-item"><a data-toggle="tab" href="#hadoop" class="nav-link">Hadoop</a></li>-->
   <li class="nav-item"><a data-toggle="tab" href="#opportunites" class="nav-link">Opportunities</a></li>
   <#--<li class="nav-item"><a data-toggle="tab" href="#notes" class="nav-link">Notes</a></li> -->
   <#-- <li class="nav-item"><a data-toggle="tab" href="#logCall" class="nav-link">${uiLabelMap.logCall}</a></li> -->
   <#-- <#if request?contains("viewAccount?")>
   <li class="nav-item"><a data-toggle="tab" href="#search" class="nav-link">Search</a></li>
   </#if> -->
   <li class="nav-item"><a data-toggle="tab" href="#campaignDetails" class="nav-link">Campaign Details</a></li>
   <li class="nav-item"><a data-toggle="tab" href="#customFields" class="nav-link">Attributes</a></li>
   <li class="nav-item"><a data-toggle="tab" href="#segmentation" class="nav-link">Segmentation</a></li>
   <li class="nav-item"><a data-toggle="tab" href="#economicsMetrics" class="nav-link">Economic Metrics</a></li>
   <#--<li class="nav-item"><a data-toggle="tab" href="#formValue" class="nav-link">Form Values</a></li>-->
   
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
    	$('.nav-tabs a[href="#customFields"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "contact">
    	$('.nav-tabs a[href="#contact"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "accounts">
    	$('.nav-tabs a[href="#accounts"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "opportunites">
    	$('.nav-tabs a[href="#opportunites"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "notes">
    	$('.nav-tabs a[href="#notes"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "logCall">
    	$('.nav-tabs a[href="#logCall"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "search">
    	$('.nav-tabs a[href="#search"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "campaignDetails">
    	$('.nav-tabs a[href="#campaignDetails"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "formValue">
    	$('.nav-tabs a[href="#formValue"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "segmentation">
    	$('.nav-tabs a[href="#segmentation"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "economicsMetrics">
    	$('.nav-tabs a[href="#economicsMetrics"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "hadoop">
    	$('.nav-tabs a[href="#hadoop"]').tab('show');	
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