<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
<#assign salesOpportunityId = '${requestParameters.salesOpportunityId?if_exists}'>
<#assign srNumber = '${requestParameters.srNumber?if_exists}'>
 <#assign workEffortId = '${requestParameters.workEffortId?if_exists}'> 

 	<#if salesOpportunityId?has_content>
		<#assign partyList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("partyId","roleTypeId").from("SalesOpportunityRole").where("salesOpportunityId",salesOpportunityId).queryFirst())?if_exists />
		<#assign partyId = "">	
		<#assign roleTypeId = "">
		<#assign customerType = "">
			<#if partyList?has_content>
					<#assign partyId = "${partyList.partyId?if_exists}">
					<#assign roleTypeId = "${partyList.roleTypeId?if_exists}">
		    </#if>
		    <#if partyId?has_content && roleTypeId?has_content>
		    	<#assign roleTypeAndPartyDetails = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("description").from("RoleTypeAndParty").where("partyId",partyId, "roleTypeId",roleTypeId).queryOne())?if_exists />
				<#if roleTypeAndPartyDetails?has_content>
					<#assign customerType = "${roleTypeAndPartyDetails.description?if_exists}">
					<#assign personList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("nationalId").from("Person").where("partyId",partyId).queryOne())?if_exists />
					<#if personList?has_content>
						<#assign nationalId = "${personList.nationalId?if_exists}">  
					</#if>
					<#assign partyIdtnList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue").from("PartyIdentification").where("partyId",partyId).queryFirst())?if_exists />
					<#if partyIdtnList?has_content>
						<#if roleTypeId == "NON_CRM">
							<#assign vPlusId = "${partyIdtnList.idValue?if_exists}" >
						<#elseif roleTypeId == "PROSPECT">	
							<#assign prospectId = "${partyIdtnList.idValue?if_exists}" >
						<#elseif roleTypeId == "CUSTOMER">	
							<#assign cifNo = "${partyIdtnList.idValue?if_exists}" >	
						</#if>
					</#if>
				</#if>
		    </#if>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="Customer Type" value="${customerType?if_exists}"/>
        	<@displayCell label="National ID" value="${nationalId?if_exists}"/>
    	</div>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="CIF ID" value="${cifNo?if_exists}" />
    	</div>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="Prospect ID"value="${prospectId?if_exists}" />
    	</div>
   		<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="V+ ID" value="${vPlusId?if_exists}"/>
    	</div>
    <#elseif srNumber?has_content>
        <#assign partyList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("fromPartyId","custRequestId").from("CustRequest").where("custRequestId",srNumber).queryFirst())?if_exists />
		<#assign partyId = "">	
		<#assign roleTypeId = "">
		<#assign customerType = "">
			<#if partyList?has_content>
				<#assign partyId = "${partyList.fromPartyId?if_exists}">
				<#assign roleList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("roleTypeId").from("PartyRole").where("partyId",partyId).queryFirst())?if_exists />
				<#if roleList?has_content>
					<#assign roleTypeId = "${roleList.roleTypeId?if_exists}">
				</#if>
		    </#if>
		    <#if partyId?has_content && roleTypeId?has_content>
		    	<#assign roleTypeAndPartyDetails = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("description").from("RoleTypeAndParty").where("partyId",partyId, "roleTypeId",roleTypeId).queryOne())?if_exists />
				<#if roleTypeAndPartyDetails?has_content>
					<#assign customerType = "${roleTypeAndPartyDetails.description?if_exists}">
					<#assign personList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("nationalId").from("Person").where("partyId",partyId).queryOne())?if_exists />
					<#if personList?has_content>
						<#assign nationalId = "${personList.nationalId?if_exists}">  
					</#if>
					<#assign partyIdtnList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue").from("PartyIdentification").where("partyId",partyId).queryFirst())?if_exists />
					<#if partyIdtnList?has_content>
						<#if roleTypeId == "NON_CRM">
							<#assign vPlusId = "${partyIdtnList.idValue?if_exists}" >
						<#elseif roleTypeId == "PROSPECT">	
							<#assign prospectId = "${partyIdtnList.idValue?if_exists}" >
						<#elseif roleTypeId == "CUSTOMER">	
							<#assign cifNo = "${partyIdtnList.idValue?if_exists}" >	
						</#if>
					</#if>
				</#if>
		    </#if>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="Customer Type" value="${customerType?if_exists}"/>
        	<@displayCell label="National ID" value="${nationalId?if_exists}"/>
    	</div>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="CIF ID" value="${cifNo?if_exists}" />
    	</div>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="Prospect ID"value="${prospectId?if_exists}" />
    	</div>
   		<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="V+ ID" value="${vPlusId?if_exists}"/>
    	</div>	
    <#elseif workEffortId?has_content>
    	<#assign partyList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("partyId","roleTypeId").from("WorkEffortPartyAssignment").where("workEffortId",workEffortId).queryFirst())?if_exists />
		<#assign partyId = "">	
		<#assign roleTypeId = "">
		<#assign customerType = "">
			<#if partyList?has_content>
				<#assign partyId = "${partyList.partyId?if_exists}">
				<#assign roleTypeId = "${partyList.roleTypeId?if_exists}">
			</#if>
		    <#if partyId?has_content && roleTypeId?has_content>
		    	<#assign roleTypeAndPartyDetails = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("description").from("RoleTypeAndParty").where("partyId",partyId, "roleTypeId",roleTypeId).queryOne())?if_exists />
				<#if roleTypeAndPartyDetails?has_content>
					<#assign customerType = "${roleTypeAndPartyDetails.description?if_exists}">
					<#assign personList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("nationalId").from("Person").where("partyId",partyId).queryOne())?if_exists />
					<#if personList?has_content>
						<#assign nationalId = "${personList.nationalId?if_exists}">  
					</#if>
					<#assign partyIdtnList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue").from("PartyIdentification").where("partyId",partyId).queryFirst())?if_exists />
					<#if partyIdtnList?has_content>
						<#if roleTypeId == "NON_CRM">
							<#assign vPlusId = "${partyIdtnList.idValue?if_exists}" >
						<#elseif roleTypeId == "PROSPECT">	
							<#assign prospectId = "${partyIdtnList.idValue?if_exists}" >
						<#elseif roleTypeId == "CUSTOMER">	
							<#assign cifNo = "${partyIdtnList.idValue?if_exists}" >	
						</#if>
					</#if>
				</#if>
		    </#if>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="Customer Type" value="${customerType?if_exists}"/>
        	<@displayCell label="National ID" value="${nationalId?if_exists}"/>
    	</div>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="CIF ID" value="${cifNo?if_exists}" />
    	</div>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="Prospect ID"value="${prospectId?if_exists}" />
    	</div>
   		<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="V+ ID" value="${vPlusId?if_exists}"/>
    	</div> 
    <#else>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="Customer Type" id="customerType"/>
        	<@displayCell label="National ID" id="nationalNo"/>
    	</div>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="CIF ID" id="cifNo" />
    	</div>
    	<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="Prospect ID" id = "prospectNo"/>
    	</div>
   		<div class="col-md-6 col-lg-3 col-sm-12">
        	<@displayCell label="V+ ID" id = "vPlusNo"/>
    	</div>
   </#if> 
    
</div>
  
<script>
	jQuery(document).ready( function() {
		var salesOpportunityId  = $("#salesOpportunityId").val();
		var srNumber = $("#srNumber").val();
		var flag  = $("#flag").val();
		if ((salesOpportunityId != "" || srNumber != "") && flag != undefined && flag != null && flag != "") {
			getCustCommunicationDetails();
		}
	});
	getCustCommunicationDetails();
	function getCustCommunicationDetails(){
		
		<#if requestParameters.partyId?has_content>
			$("#customerId").val("${requestParameters.partyId!}");
		</#if>
		
		var customerId  = $("#customerId").val();
		if (customerId != undefined && customerId != null) {
	    var result = null;
	    $.ajax({
	        type: "POST",
	        url: "getCustomerForAddServiceRequest",
	        async: false,
	        data: {"cinNumber": customerId},
	        success: function(data) {
	            result=data[0];
	            $.each(result, function(name, val) {
		            if(name !=null && name != "" && name != 'undefined'){
		            	if(name == "phoneSolicitation"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		if(val == "Y"){
			            			document.querySelector("i#phone").setAttribute('class','fa fa-check fa-1 text-success');
			            			document.querySelector("i#sms").setAttribute('class','fa fa-check fa-1 text-success');
			            		}else{
			            			document.querySelector("i#phone").setAttribute('class','fa fa-times fa-1 text-danger');
			            			document.querySelector("i#sms").setAttribute('class','fa fa-times fa-1 text-danger');
			            		}
		            		}else{
			            		document.querySelector("i#phone").setAttribute('class','fa fa-times fa-1 text-danger');
			            		document.querySelector("i#sms").setAttribute('class','fa fa-times fa-1 text-danger');
			            	}
		            	}
		            	if(name == "emailSolicitation"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		if(val == "Y"){
			            			document.querySelector("i#email").setAttribute('class','fa fa-check fa-1 text-success');
			            		}else{
			            			document.querySelector("i#email").setAttribute('class','fa fa-times fa-1 text-danger');
			            		}
		            		}else{
			            		document.querySelector("i#email").setAttribute('class','fa fa-times fa-1 text-danger');
			            	}
		            	}
		            	if(name == "addressSolicitation"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		if(val == "Y"){
			            			document.querySelector("i#address").setAttribute('class','fa fa-check fa-1 text-success');
			            		}else{
			            			document.querySelector("i#address").setAttribute('class','fa fa-times fa-1 text-danger');
			            		}
		            		}else{
			            		document.querySelector("i#address").setAttribute('class','fa fa-times fa-1 text-danger');
			            	}
		            	}
		            	if(name == "emailAddr"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		$("#mail").text(val);
		            		}else{
			            		$("#mailImg").remove();
			            	}
		            	}
		            	if(name == "phoneNumber"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		 $("#mobile").text(val);
		            		}else{
			            		$("#mobileImg").remove();
			            	}
		            	}
		            	if(name == "custName"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		 $("#custName").text(val);
		            		}
		            	}
		            	if(name == "operSrCount"){
			            	if(val !=null && val != "" && val != 'undefined'){
				         		$("#operSrCount").text(val);
			            	}else if(val == 0){
			            		$("#operSrCount").text(val);
			            	}
			            }
			            if(name == "opportunitiesCount"){
			            	if(val !=null && val != "" && val != 'undefined'){
				          		$("#opportunitiesCount").text(val);
			            	}else if(val == 0){
			            		$("#opportunitiesCount").text(val);
			            	}
			            }
		            }
		        });
	        },error: function(data) {
	        	result=data;
				showAlert("error", "Error occured while fetching Party Communication Data!");
			}
	    });
    }
	
	}
</script>
