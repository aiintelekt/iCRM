<#assign requestURI = request.getRequestURI()/>

<ul class="nav nav-tabs mt-3" style="margin-top: -10px !important; margin-bottom: 10px">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link active" href="#sr-main-search">
			<i class="fa fa-address-book fa-1" aria-hidden="true"></i> FSR Search 
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#ho-postal-search">
			<i class="fa fa-address-card fa-1" aria-hidden="true"></i> Postal Search
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#tech-search">
			<i class="fa fa-user fa-1" aria-hidden="true"></i> Tech/Activity Schedule Search 
		</a>
	</li>
	<#-- 
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sr-homeowner-search">
			<i class="fa fa-user fa-1" aria-hidden="true"></i> Homeowner Search
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sr-contractor-search">
			<i class="fa fa-user fa-1" aria-hidden="true"></i> Contractor Search
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sr-attribute-search">
			<i class="fa fa-user fa-1" aria-hidden="true"></i> Attribute Search
		</a>
	</li>
	-->
	<#-- <li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#tech-search">
			<i class="fa fa-user fa-1" aria-hidden="true"></i> Search by Tech / Activity Based 
		</a>
	</li> -->
</ul>

<script>

$(document).ready(function() {

	<#if !activeTab?has_content>
    	<#assign activeTab = requestParameters.activeTab!>
    </#if>
    
    <#if activeTab?has_content && activeTab == "sr-main-search">
    	$('.nav-tabs a[href="#sr-main-search"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "ho-postal-search">
    	$('.nav-tabs a[href="#ho-postal-search"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "tech-search">
    	$('.nav-tabs a[href="#tech-search"]').tab('show');	
    <#else>
    	$('.nav-tabs a[href="#sr-main-search"]').tab('show');	
    </#if>
    
});

</script>

<script>

jQuery(document).ready(function() {	



});

</script>