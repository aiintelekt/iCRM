<#assign requestURI = request.getRequestURI()/>

<script>

$(document).ready(function() {

	<#if !activeTab?has_content>
		<#assign activeTab = requestParameters.activeTab!>
	</#if>

	<#if activeTab?has_content && activeTab == "details">
		$('.nav-tabs a[href="#details"]').tab('show');	
	<#elseif activeTab?has_content && activeTab == "administration">
		$('.nav-tabs a[href="#administration"]').tab('show');
	<#else>
		$('.nav-tabs a[href="#details"]').tab('show');	
	</#if>
});

</script>

<script>

jQuery(document).ready(function() {	

/*
$("a[href='#accountDetails']").on('shown.bs.tab', function(e) {
	findCaAccounts();
	findHdaFacilities();
});
*/

});

</script>