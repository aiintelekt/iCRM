<#assign requestURI = request.getRequestURI()/>
<#assign activeTab = request.getAttribute("activeTab")?if_exists/>
<#-- <#assign request = requestURI+"?" /> -->

<script>

$(document).ready(function() {

	<#if !activeTab?has_content>
    	<#assign activeTab = requestParameters.activeTab!>
    </#if>
    
    <#if activeTab?has_content && activeTab == "details">
    	$('.nav-tabs a[href="#details"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "contacts">
    	$('.nav-tabs a[href="#contacts"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "communication-history">
    	$('.nav-tabs a[href="#communication-history"]').tab('show');	
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
    <#else>
    	$('.nav-tabs a[href="#details"]').tab('show');	
    </#if>
    
});

</script>

<script>

jQuery(document).ready(function() {	



});

</script>