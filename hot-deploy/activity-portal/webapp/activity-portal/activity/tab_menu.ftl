<#assign requestURI = request.getRequestURI()/>

<ul class="nav nav-tabs mt-3">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link active" href="#details">
			<i class="fa fa-user fa-1" aria-hidden="true"></i> Activity Details 
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