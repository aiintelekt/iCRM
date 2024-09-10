
<#assign requestURI = request.getRequestURI()/>
<#-- <#assign request = requestURI+"?" /> -->

<#assign pretailParamForPersona = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "PERSONA_TAB_ENABLED").queryOne()! />
<#if pretailParamForPersona?exists && pretailParamForPersona?has_content>
	<#assign isPretailParamEnabledForPersona = pretailParamForPersona.value!>
</#if>

<ul class="nav nav-tabs mt-3">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link active" href="#a-details">
			<#--<i class="fa fa-user fa-1" aria-hidden="true"></i>--> Invoice Items 
		</a>
	</li>
	<#-- <li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#communicationInfo">
			<i class="fa fa-address-book-o fa-1" aria-hidden="true"></i> Communication 
		</a>
	</li> -->
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-contactInfo">
			<#--<i class="fa fa-address-book fa-1" aria-hidden="true"></i>--> Contact Info
		</a>
	</li>
	
	
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-association">
			<#--<i class="fa fa-file-text fa-1" aria-hidden="true"></i>--> Associations
		</a>
	</li>
	<#--<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-payment">
			<i class="fa fa-user-plus" aria-hidden="true"></i> Make Payment
		</a>
	</li>-->
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-appliedPayments">
			<#--<i class="fa fa-user-plus" aria-hidden="true"></i>-->Applied Payments
		</a>
	</li>
<#--<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-paymentInfo">
			<i class="fa fa-user-plus" aria-hidden="true"></i> Payment Info
		</a>
	</li>-->
	<#--<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-communication">
			<i class="fa fa-id-badge" aria-hidden="true"></i>  Communication History
		</a>
	</li>-->
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#a-status">
			<#--<i class="fa fa-id-badge" aria-hidden="true"></i>-->  Invoice Status
		</a>
	</li>
	<li class="nav-item" style="display:none">
		<a data-toggle="tab" class="nav-link" href="#administration">
			<#--<i class="fa fa-user-plus" aria-hidden="true"></i>--> Administration
		</a>
	</li>
	
</ul>

<script>

$(document).ready(function() {

	<#if !activeTab?has_content>
    	<#assign activeTab = requestParameters.activeTab!>
    </#if>
    
    <#if activeTab?has_content && activeTab == "a-details">
    	$('.nav-tabs a[href="#a-details"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "a-contactInfo">
    gg
    	$('.nav-tabs a[href="#a-contactInfo"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "a-association">
    	$('.nav-tabs a[href="#a-association"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "a-payment">
    	$('.nav-tabs a[href="#a-payment"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "a-appliedPayments">
    	$('.nav-tabs a[href="#a-appliedPayments"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "a-paymentInfo">
    	$('.nav-tabs a[href="#a-paymentInfo"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "a-status">
    	$('.nav-tabs a[href="#a-status"]').tab('show');
   <#elseif activeTab?has_content && activeTab == "a-sendPerMail">
    	$('.nav-tabs a[href="#a-communication"]').tab('show'); 
     <#else>
    
    	$('.nav-tabs a[href="#a-details"]').tab('show');	
    </#if>
    
});

</script>

