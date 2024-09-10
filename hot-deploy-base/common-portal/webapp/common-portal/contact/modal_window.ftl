<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign partyId= request.getParameter("partyId")! />
<#assign requestURI="${requestURI!}">
<#if !requestURI?has_content>
<#assign requestURI = request.getRequestURI()/>
<#if requestURI.contains("screenRender")>
<#assign requestURI=request.getParameter("requestUri")!>
</#if>
</#if>
<#if requestURI?has_content>
<#if requestURI?contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif requestURI?contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif requestURI?contains("viewOpportunity")>
<#assign requestURI = "viewOpportunity"/>
<#elseif requestURI?contains("viewServiceRequest")>
<#assign requestURI = "viewServiceRequest"/>
<#else>
<#assign requestURI = "viewContact"/>
</#if>
</#if>
 	
<#macro addPartyContact instanceId fromAction="">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg add-contact-party" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" style="overflow-y: scroll;overflow-x: scroll; height:auto; width:auto;">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
                <h2 class="modal-title">${uiLabelMap.findParty!}</h2>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
                <form  method="post" id="searchContactsForm" name="searchContactsForm">
  					 <input type="hidden" name="srFromPartyId" id="srFromPartyId" value="${srFromPartyId?if_exists}"/>
            			<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
            			<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				<div class="row">
							
							
					<div class="col-md-2 col-sm-2">
                     	<@inputCell    
		                    id="contactSearchPartyId"
		                    placeholder =uiLabelMap.contactId
		                    />
                     </div>
                     <div class="col-md-2 col-sm-2">
                     	<@inputCell    
		                    id="searchFirstName"
		                    placeholder = "First Name"
		                    />
                     </div>
                      <div class="col-md-2 col-sm-2">
                     	<@inputCell    
		                    id="searchLastName"
		                    placeholder = "Last Name"
		                    />
                     </div>
                     <div class="col-md-2 col-sm-2">
                     	<@inputCell    
		                    id="searchEmailId"
		                    placeholder = uiLabelMap.email
		                    />
                     </div>
                     <div class="col-md-2 col-sm-2">
                     	<@inputCell    
		                    id="searchPhoneNum"
		                    placeholder = uiLabelMap.phoneNumber
		                    />
                     </div>
						 
						<div class="col-md-2 col-sm-2">
				     	<@button
				        id="main-search-btn"
				        label="${uiLabelMap.Find}"
				        />
				      </div> 
						  
						
					</div>
				</form>
			</div>
			
			<div class="popup-bot">
			
			<#-- <@AgGrid
		        userid="${userLogin.userLoginId}" 
		        instanceid="CONTACTS_GRID"  
		        shownotifications="true"
	            autosizeallcol="true"
	            debug="false"
	            gridheadertitle="Contacts List"
		    	gridheaderid="${instanceId!}_addcontact-grid-action-container"
		    	
		    	serversidepaginate=false
		    	insertBtn=false
		    	removeBtn=false
		    	updateBtn=false
		    	headerextra=rightContent
		    	exportBtn=false
		        />
		    </div>   
		       
	       <script type="text/javascript" src="/common-portal-resource/js/ag-grid/service-request/findContacts.js"></script>-->
			<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
			<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="addcontact-grid"
			instanceId="CONTACTS_GRID"
			jsLoc="/common-portal-resource/js/ag-grid/service-request/findContacts.js"
			headerLabel="Contacts List"
			headerId="${instanceId!}_addcontact-grid-action-container"
			savePrefBtn=false
			subFltrClearBtn=false
			clearFilterBtn=false
			savePrefBtnId="contact-save-pref-btn"
			clearFilterBtnId="contact-clear-filter-btn"
			subFltrClearId="contact-sub-filter-btn"
			headerBarClass="grid-header-no-bar"
			headerExtra=rightContent!
			exportBtn=false
			/>
      	</div>
	      	<div class="modal-footer" style="border-top: 0px;padding-top: 0px;">
	             
	        </div>
    	</div>
  	</div>
</div>

<form method="post" id="${instanceId!}_contactAccountForm">
   <input type="hidden" name="activeTab" value="contacts" />
   <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
   <input type="hidden" name="partyId" id="partyId" value="${partyId?if_exists}"/>
   <input type="hidden" name="accountPartyId" value="${partyId?if_exists}"/>
   <input type="hidden" name="leadPartyId" value="${partyId?if_exists}"/>
   <input type="hidden" name="contactPartyId" value=""/>
</form>

<form method="post" id="${instanceId!}_addContactToOpportunity" action="addContactToOpportunity">
   <input type="hidden" name="activeTab" value="contacts" />
   <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
   <input type="hidden" name="salesOpportunityId" value="${salesOpportunityId?if_exists}"/>
   <input type="hidden" name="contactPartyId" value=""/>
</form>

<form method="post" id="${instanceId!}_addContactToSr">
   <input type="hidden" name="activeTab" value="contacts" />
   <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
   <input type="hidden" name="partyId" id="partyId" value="${partyId?if_exists}"/>
   <input type="hidden" name="accountPartyId" value="${partyId?if_exists}"/>
   <input type="hidden" name="srNumber" value="${srNumber?if_exists}"/>
   <input type="hidden" name="srFromPartyId" id="srFromPartyId" value="${srFromPartyId?if_exists}"/>
   <input type="hidden" name="contactPartyId" value=""/>
</form>
<script>


$(document).ready(function() {


});

function addContacts(contactPartyId){
	<#if requestURI?has_content>
		<#if requestURI == "viewOpportunity">
			$('#${instanceId!}_addContactToOpportunity input[name=contactPartyId]').val(contactPartyId);
			$('#${instanceId!}_addContactToOpportunity').submit();
		<#elseif requestURI == "viewServiceRequest">
			$('#${instanceId!}_addContactToSr input[name=contactPartyId]').val(contactPartyId);
			$("#${instanceId!}_addContactToSr").attr("action", "addContactToSr");
			$('#${instanceId!}_addContactToSr').submit();
		<#else>
			$('#${instanceId!}_contactAccountForm input[name=contactPartyId]').val(contactPartyId);
			$("#${instanceId!}_contactAccountForm").attr("action", "addContactFromParty");
			$('#${instanceId!}_contactAccountForm').submit();
		</#if>
	</#if>
			$('#${instanceId!}').modal('hide');
}
</script>

</#macro>

<#macro submitConfirmation instanceId onclick="">
<div id="${instanceId!}" class="modal fade">
	<div class="modal-dialog">
  		<div class="modal-content">
			<div class="modal-header">
				<span id="message"></span>
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    		</div>
        	<div>
  			</div>
		  	<div class="modal-footer">
				<input id="${instanceId!}-submit-btn" type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Update" onclick="${onclick!}">
		        <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Cancel" onclick="return false;">
			</div>
		</div>
	</div>
</div>
</#macro>
