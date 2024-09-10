<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="col-lg-12 col-md-12 col-sm-12">

<form method="post" id="searchContactsForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">

<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
<input type="hidden" name="salesOpportunityId" value="${salesOpportunityId!}">

<div class="card-header margin-adj-accordian pad-top">
	<div class="row p-2">
		<div class="col-lg-4 col-md-6 col-sm-12">
		
			<@inputRow 
			id="partyId"
			placeholder=uiLabelMap.contactId
			inputColSize="col-sm-12"
			iconClass="fa fa-user"
			required=false
			/> 
			
			<@inputRow 
			id="firstName"
			placeholder=uiLabelMap.firstName
			inputColSize="col-sm-12"
			iconClass="fa fa-user-circle-o"
			required=false
			/> 
			
			<@inputRow 
			id="lastName"
			placeholder=uiLabelMap.lastName
			inputColSize="col-sm-12"
			iconClass="fa fa-user-circle-o"
			required=false
			/>
			
		</div>
		<div class="col-lg-4 col-md-6 col-sm-12">
		
			<@inputRow 
			id="emailAddress"
			placeholder=uiLabelMap.email
			inputColSize="col-sm-12"
			iconClass="fa fa-envelope"
			required=false
			/> 
			
			<@inputRow 
			id="contactNumber"
			placeholder=uiLabelMap.phone
			inputColSize="col-sm-12"
			iconClass="fa fa-phone"
			required=false
			/>
			
		</div>
		
		<div class="col-md-2 col-sm-2">
	     	<@button
	        id="contacts-search-btn"
	        label="${uiLabelMap.Find}"
	        />	
     	</div>
		
	</div>
</div>

</form>
	
</div>
	
<script>     
$(document).ready(function() {
	
});
</script>