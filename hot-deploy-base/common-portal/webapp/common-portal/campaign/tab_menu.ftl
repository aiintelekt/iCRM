<#assign requestURI = request.getRequestURI()/>
<ul class="nav nav-tabs mt-3" id="nav-tab-focus">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link active" href="#cc-phone">
			Phone
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#cc-email">
			Email
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#cc-sms">
			SMS
		</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#cc-postal">
			Postal
		</a>
	</li>
</ul>

<script>
$(document).ready(function() {
	<#if !activeTab?has_content>
    	<#assign activeTab = requestParameters.activeTab!>
    </#if>
    
    <#if activeTab?has_content && activeTab == "cc-email">
    	$('.nav-tabs a[href="#cc-email"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "cc-phone">
    	$('.nav-tabs a[href="#cc-phone"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "cc-sms">
    	$('.nav-tabs a[href="#cc-sms"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "cc-postal">
    	$('.nav-tabs a[href="#cc-postal"]').tab('show');
    <#else>
    	$('.nav-tabs a[href="#cc-phone"]').tab('show');	
    </#if>
});
</script>
<#macro campaignTabForm name partyId campaignType>
<form method="post" action="" id="${name}" name="${name}" novalidate="novalidate" data-toggle="validator">
	<input type="hidden" name="partyId" value="${partyId!}">
	<input type="hidden" name="isCampaignForParty" value="Y">
	<input type="hidden" name="campaignType" value="${campaignType}">
</form>
</#macro>