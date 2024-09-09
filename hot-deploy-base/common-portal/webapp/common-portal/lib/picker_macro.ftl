<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/modal_window.ftl"/>

<#-- <#assign srNumber = requestParameters.srNumber!>
<#assign partyId= request.getParameter("partyId")! />
<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif request.getRequestURI().contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
<#elseif request.getRequestURI().contains("findOpportunity")>
<#assign requestURI = "findOpportunity"/>
<#elseif request.getRequestURI().contains("viewOpportunity")>
<#assign requestURI = "viewOpportunity"/>
<#elseif request.getRequestURI().contains("viewServiceRequest")>
<#assign requestURI = "viewServiceRequest"/>
<#elseif request.getRequestURI().contains("viewCallListCustomer")>
<#assign requestURI = "viewCallListCustomer"/>
</#if>-->
<#assign srNumber = requestParameters.srNumber!>
<#assign salesOpportunityId = request.getParameter("salesOpportunityId")!>
<#assign partyId = request.getParameter("partyId")!>
<#if salesOpportunityId == "" && inputContext?has_content>
<#assign salesOpportunityId = inputContext.salesOpportunityId!>
</#if>
<#if partyId == "" && inputContext?has_content>
<#assign partyId = inputContext.partyId!>
</#if>
<#assign srNumberValue = srNumber!>
<#assign salesOpportunityIdValue =salesOpportunityId!>
<#assign partyIdValue = partyId!>
<#assign requestUri = request.getRequestURI()/>
<#if requestUri.contains("screenRender")>
<#assign requestUri=request.getParameter("requestUri")!>
</#if>
<#if requestUri?has_content>
<#assign requestURI = "viewContact"/>
<#assign requestURIName = requestURI!/>
<#list ["viewLead", "viewAccount", "viewCustomer", "findOpportunity", "viewOpportunity", "viewServiceRequest", "viewCallListCustomer"] as requestUriName>
<#if requestUri?contains(requestUriName)>
<#assign requestURI = requestUriName!>
<#assign requestURIName = requestUriName!>
</#if>
</#list>
</#if>
<style>
.popover.confirmation{
	z-index: 1000000000 !important;
}
</style>
<#macro accountPicker instanceId fromAction="">

<div id="${instanceId!}" class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg" style="min-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Accounts List</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" name="acc-searchForm" id="acc-searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	        	   <div class="row">
	               <div class="col-md-2 col-sm-2">
	                  <div class="form-group row mr">
	                   <input type="text" class="form-control input-sm" id="accountSearchPartyId" name="accountSearchPartyId" value="${accountSearchPartyId?if_exists}" placeholder="Account ID">
	                  </div>
	               </div>
	               <div class="col-md-2 col-sm-2">
	                  <div class="form-group row mr">
	                     <input type="text" class="form-control input-sm" id="searchGroupName" name="searchGroupName" placeholder="Name">
	                  </div>
	               </div>
	               <div class="col-md-2 col-sm-2">
	                  <div class="form-group row mr">
	                     <input type="text" class="form-control input-sm" id="searchEmailId" name="searchEmailId" placeholder="Email Address">
	                  </div>
	               </div>
	               <div class="col-md-2 col-sm-2">
	                  <div class="form-group row mr">
	                     <input type="text" class="form-control input-sm" id="searchPhoneNum" name="searchPhoneNum" placeholder="Phone Number">
	                  </div>
	               </div>
	               <div class="col-md-1 col-sm-1">
	                <@button
		            	id="acc-search-btn"
		           	    label="${uiLabelMap.Find}"
		            />
	               </div>
	            </div>
				</form>
			</div>
			<#assign rightContent='<button title="Refresh" id="acc-refresh-btn" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-refresh" aria-hidden="true"></i>  </button>'/>
			
		<#-- <@AgGrid
				gridheadertitle=""
				gridheaderid="${instanceId!}-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				headerextra=rightContent!
				refreshPrefBtnId="acc-refresh-pref-btn"
				savePrefBtnId="acc-save-pref-btn"
				clearFilterBtnId="acc-clear-filter-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="ACCOUNT_PICKER" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/account/find-accounts-list.js"></script>-->
		<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
		<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

						<@fioGrid 
								id="${instanceId!}-Grid"
								instanceId="ACCOUNT_PICKER"
								jsLoc="/common-portal-resource/js/ag-grid/account/find-accounts-list.js"
								headerLabel=""
								headerId="${instanceId!}-action-container"
								savePrefBtnId="acc-save-pref-btn"
								clearFilterBtnId="acc-clear-filter-btn"
								headerBarClass="grid-header-no-bar"
								savePrefBtn=false
								clearFilterBtn=false
								exportBtn=false
								subFltrClearBtn=false
								headerExtra=rightContent!
								subFltrClearId="acc-sub-filter-clear-btn"
								exportBtnId="acc-export-btn"
								/>
      	</div>
    	</div>
  	</div>
 	
</div>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#acc-refresh-btn").trigger('click');
});

});
</script>
</#macro>

<#macro responsiblePicker instanceId fromAction="">

<!-- Find parent account pop for create and update account-->
<div id="${instanceId!}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Find Team Members</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="popup-bot">
               <form method="post" action="#" id="FindTeamMembers" class="form-horizontal" name="FindTeamMembers" novalidate="novalidate" data-toggle="validator">
                  <div class="row">
                     <div class="col-lg-4 col-md-2 col-sm-2">
                        <div class="form-group row mr">
                           <input type="text" class="form-control input-sm" id="firstName" name="firstName" value="" placeholder="First Name">
                        </div>
                     </div>
                     <div class="col-lg-4 col-md-2 col-sm-2">
                        <div class="form-group row mr">
                           <input type="text" class="form-control input-sm" id="lastName" name="lastName" placeholder="Last Name">
                        </div>
                     </div>
                     <div class="col-lg-4 col-md-1 col-sm-1">
                        <button type="button" class="btn btn-sm btn-primary navbar-dark m5" onclick="javascript:getTeamMembersPRF();">Find Team Members</button>
                     </div>
                  </div>
               </form>
               <div class="clearfix"> </div>
            </div>
            <div class="clearfix"> </div>
            <div class="popup-agtitle">
               <h2 class="float-left">Team Members</h2>
            </div>
            <div class="clearfix"> </div>
            <div class="table-responsive">
               <table id="ajaxFindTeamMembersdatatablePRF" class="table table-striped">
                  <thead>
                     <tr>
                        <th>Name</th>
                     </tr>
                  </thead>
               </table>
            </div>
         </div>
         <div class="modal-footer">
            <button type="sbmit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
         </div>
      </div>
   </div>
</div>

<form name="personResponsibleParty" id="personResponsibleParty" method="POST" action="<@ofbizUrl>personResponsibleParty</@ofbizUrl>" style="display:none;">
   <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
   <input type="hidden" name="donePage" value="${requestURI?if_exists}"/>
   <input type="hidden" name="roleTypeIdFrom" value="${partyRoleTypeId?if_exists}"/>
   <input type="hidden" name="accountPartyId" value=""/>
   <input type="hidden" name="salesOpportunityId" value="${salesOpportunityId?if_exists}"/>
   <input type="hidden" name="srNumber" value="${srNumber?if_exists}"/>
   <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
</form>

<script>

$(document).ready(function() {
	$('#firstName').val("");
	$("#lastName").val("");
	getTeamMembersPRF();
});

function reassignParty(value) {
	if (value != null && value != "") {
		$("#personResponsibleParty input[name=accountPartyId]").val(value);
		document.personResponsibleParty.submit();
	}
}

function getTeamMembersPRF() {
	var firstName = $("#firstName").val();
	var lastName = $("#lastName").val();
	var url = "/common-portal/control/getTeamMembers?firstName=" + firstName
			+ "&lastName=" + lastName
			+ "&externalLoginKey=${requestAttributes.externalLoginKey!}";
	$('#ajaxFindTeamMembersdatatablePRF').DataTable(
			{
				"processing" : true,
				"serverSide" : true,
				"destroy" : true,
				"ajax" : {
					"url" : url,
					"type" : "POST"
				},
				"Paginate" : true,
				"pageLength" : 10,
				"bAutoWidth" : false,
				"stateSave" : true,
				"columns" : [
						{
							"data" : "partyId",
							"render" : function(data, type, row, meta) {
							if ('${selectedRMId!}'!=null && '${selectedRMId!}'==data){
								return row.name+ '(' + data	+ ')';
							}else{
								data = '<a href="#" onclick=reassignParty("'
										+ data + '")>' + row.name + '(' + data
										+ ')</a>';
								return data;
							}
							
							}
						},

				]
			});
}

</script>

</#macro>

<#macro reassignPicker instanceId fromAction="">
	<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	    <div class="modal-dialog modal-lg" style="max-width: 1200px;">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h2 class="modal-title">Find User</h2>
	                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
	            </div>
	            <div class="modal-body" style="padding-bottom: 8px;">
	            	<div class="card-header popup-bot">
	                    <form  method="post" id="searchReassignForm" name="searchReassignForm">
	                        <@inputHidden 
	                        	id="externalLoginKey"
	                        	value="${requestAttributes.externalLoginKey!}"
	                        	/>
	                        <@inputHidden 
	                        	id="userId"
	                        	value="${userLogin.userLoginId?if_exists}"
	                        	/>
	                        	
	                        <#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?exists>
	                        	<@inputHidden 
		                        	id="activeTeamMember"
		                        	value="Y"
		                        	/>
	                        </#if>
	                        <div class="row">
	                            <div class="col-md-6 col-sm-6">
								   	<@dropdownCell 
								   		id="roleTypeId"
								   	  	placeholder="${uiLabelMap.Role}"
								   	  	options=reassignOwnerList!
								   	  	value="CUST_SERVICE_REP"
								   	  	/>
							   	</div>
	                            <div class="col-md-2 col-sm-2">
	                                <@button
		                                id="sr-reassign-search-btn"
		                                label="${uiLabelMap.Find}"
		                                />
	                            </div>
	                        </div>
	                    </form>
	                </div>
	                <#-- 
	                <@AgGrid
		                gridheadertitle="List of Users"
		                gridheaderid="${instanceId!}_user-list-container"
		                savePrefBtn=true
		                clearFilterBtn=true
		                exportBtn=false
		                insertBtn=false
		                updateBtn=false
		                removeBtn=false
		                refreshPrefBtnId="reassignPicker-refresh-pref-btn"
						savePrefBtnId="reassignPicker-save-pref-btn"
						clearFilterBtnId="reassignPicker-clear-filter-btn"
		                userid="${userLogin.userLoginId}" 
		                shownotifications="true" 
		                instanceid="PICKER_USERS_LIST" 
		                autosizeallcol="true"
		                debug="false"
		                />    
	                <script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-users.js"></script>
	                -->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	                <@fioGrid
						instanceId="PICKER_USERS_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/picker/find-users.js"
						headerLabel="List of Users"
						headerId="list_of_user_tle"
						savePrefBtnId="reassignPicker-save-pref"
						clearFilterBtnId="reassignPicker-clear-pref"
						subFltrClearId="reassignPicker-clear-sub-ftr"
						headerBarClass="grid-header-no-bar"
						serversidepaginate=false
						statusBar=false
						exportBtn = false
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						/>
	                
	            </div>
	        </div>
	    </div>
	</div>
	<form name="personResponsibleParty" id="personResponsibleParty" method="POST" action="<@ofbizUrl>personResponsibleParty</@ofbizUrl>" style="display:none;">
	   <input type="hidden" name="partyId" value="${partyIdValue?if_exists}"/>
	   <input type="hidden" name="campaignListId" value="${campaignListId?if_exists}"/>
	   <input type="hidden" name="donePage" value="${requestURIName?if_exists}"/>
	   <input type="hidden" name="roleTypeIdFrom" value="${partyRoleTypeId?if_exists}"/>
	   <input type="hidden" name="accountPartyId" value=""/>
	   <input type="hidden" name="salesOpportunityId" value="${salesOpportunityIdValue?if_exists}"/>
	   <input type="hidden" name="srNumber" value="${srNumberValue?if_exists}"/>
	   <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	</form>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#sr-reassign-search-btn").trigger('click');
});

});
function reassignParty(value) {
	if (value != null && value != "") {
		$("#${instanceId!}").modal('hide');
		$("#personResponsibleParty input[name=accountPartyId]").val(value);
		//document.personResponsibleParty.accountPartyId.value = value;
		document.personResponsibleParty.submit();
	}
}
</script>
</#macro>

<#macro businessUnitPicker instanceId fromAction="">

<div id="${instanceId!}" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Find Parent BU</h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <div class="table-responsive">
                <div class="loader text-center" id="loader" sytle="display:none;">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
                <div id="grid1" style="width: 100%;" class="ag-theme-balham"></div>
                  
                </div>
                  <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/bu-parent.js"></script> 
            </div>
            
        </div>
    </div>
</div>

<script>


</script>

</#macro>

<#macro partyPicker instanceId fromAction="" isShowRoleFilter="Y" roleTypeFilter="" isShowPartyLevelFilter="">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1045px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.findParty!}</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" id="findPartyForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		    <input type="hidden" id="userId" value="${userLogin.userLoginId!}">

            			<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
            			<input type="hidden" id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
            			<#if isShowRoleFilter?has_content && isShowRoleFilter=="N" && roleTypeFilter?has_content>
            				<input type="hidden" name="roleTypeId" value="${roleTypeFilter!}">
            			</#if>
            			<#assign pretailEnabledForRoleType = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "FIND_PARTY_ROLETYPE").queryOne()! />
						<#if pretailEnabledForRoleType?has_content && pretailEnabledForRoleType?exists>
							<#assign pretailForPartyRoleType = pretailEnabledForRoleType.value!>
						</#if>
						<@dynaScreen 
							instanceId="FIND_PARTY"
							modeOfAction="CREATE"
							/>
						<div class="row">
						<#-- <div class="col-md-4 col-md-4 form-horizontal">
								<@inputRow 
								id="${instanceId!}_partyName"
								name="name"
								placeholder=uiLabelMap.Name
								inputColSize="col-sm-12"
								required=false
								/>
								<@inputRow 
								id="${instanceId!}_partyId"
								name="partyId"
								placeholder=uiLabelMap.partyId
								inputColSize="col-sm-12"
								required=false
								/> 
								
						  </div>
						 
						  <div class="col-md-4 col-md-4 form-horizontal">
						  		
						  		<@inputRow 
								id="${instanceId!}_phone"
								name="phone"
								placeholder=uiLabelMap.Phone
								inputColSize="col-sm-12"
								required=false
								/> 
								
						  </div>
						  
						   <div class="col-md-4 col-md-4 form-horizontal">
						  		
								<@inputRow 
								id="${instanceId!}_emailAddress"
								name="email"
								placeholder=uiLabelMap.email
								inputColSize="col-sm-12"
								required=false
								/>
						  		
						  </div>-->
							 
					</div>
					
					<div class="row">  
						<#-- <div class="col-md-4 col-md-4 form-horizontal text-right">
						       
							  <#if isShowPartyLevelFilter?has_content && isShowPartyLevelFilter=="Y">		
							  <@dropdownCell 
							    id="partyLevel"
								label="Party Level"
							    options=partyLevelList?if_exists
							    required=false
							    allowEmpty=true
							    placeholder = uiLabelMap.partyLevel!
								/>	
							  </#if>
                              
                              <#if isShowRoleFilter?has_content && isShowRoleFilter=="Y">
                              <div class="create-opp-check">
                               	<input type="radio"  name="roleTypeId" value="ACCOUNT" <#if !(applicationType?has_content && applicationType=='B2C')>checked</#if> /> Account
		                      	<input type="radio"  name="roleTypeId" value="LEAD" /> Lead
		                      	<#if !applicationType?has_content || (applicationType=='B2C' || applicationType=='BOTH')>
		                      	<input type="radio"  name="roleTypeId" value="CUSTOMER" <#if applicationType?has_content && applicationType=='B2C'>checked</#if> /> Customer
		                      	</#if>
		                      	<input type="radio"  name="roleTypeId" value="ALL"/> All 
		                      </div>	
		                      </#if>	 	
											        		
                    	 </div>-->
                    	<br>
                    	 <div class="col-md-12 form-horizontal text-right">
                    	 	<div class="search-btn">
	                           <@button 
				            		label="${uiLabelMap.Find}"
				            		id="partyPicker-search-btn"
				            	/>
				           	 </div>	
                    	 </div>
                    </div>
				</form>
			</div>
			<#assign rightContent='<button title="Refresh" id="partyPicker-refresh-btn" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-refresh" aria-hidden="true"></i>  </button>'/>
			
			<#if !applicationType?has_content || (applicationType=='B2C' || applicationType=='BOTH')>
			<#assign rightContent= rightContent + '<span title="Create Cust" id="${instanceId!}_create_cust" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create Cust </span>'/>
			</#if>
			
			<#-- <@AgGrid
				gridheadertitle=uiLabelMap.ListOfPartys
				gridheaderid="${instanceId!}_party-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				headerextra=rightContent!				
				refreshPrefBtnId="partyPicker-refresh-pref-btn"
				savePrefBtnId="partyPicker-save-pref-btn"
				clearFilterBtnId="partyPicker-clear-filter-btn"	
				subFltrClearId="partyPicker-sub-filter-clear-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="PICKER_PARTY_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-party.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
			<@fioGrid 
			id="ListOfPartys"
			instanceId="PICKER_PARTY_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/picker/find-party.js"
			headerLabel=uiLabelMap.ListOfPartys
			headerId="${instanceId!}_party-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			subFltrClearBtn=true
			headerBarClass="grid-header-no-bar"
			subFltrClearId="partyPicker-sub-filter-clear-btn"
			clearFilterBtnId="partyPicker-clear-filter-btn"	
			savePrefBtnId="partyPicker-save-pref-btn"
			headerExtra=rightContent!
			/>
      	</div>
    	</div>
  	</div>
</div>

<#if !applicationType?has_content || (applicationType=='B2C' || applicationType=='BOTH')>
<@createCustomerModal 
instanceId="create-customer"
/>
</#if>

<script>
$(document).ready(function() {
$("#findPartyForm .partyLevel").hide();
$("#findPartyForm .roleTypeId").hide();

<#if isShowRoleFilter?has_content && isShowRoleFilter=="Y">
	$("#findPartyForm .roleTypeId").show();
	<#if pretailForPartyRoleType?has_content>
		$("#findPartyForm [name='roleTypeId'][value='${pretailForPartyRoleType!}']").prop("checked", true);
	</#if>
</#if>
<#if isShowPartyLevelFilter?has_content && isShowPartyLevelFilter=="Y">
	$("#findPartyForm .partyLevel").show();
</#if>

$("#${instanceId!}_create_cust").click(function () {
	$('#create-customer').modal('show');
});

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#partyPicker-refresh-btn").trigger('click');
});
    
});
</script>

</#macro>

<#macro dealerPicker instanceId fromAction="">

<div id="${instanceId!}" class="modal fade bd-example-modal-lg popup2" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.findDealer!}</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" id="findDealerForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            			<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
            			<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
            			<input type="hidden" name="generalCountryGeoId1" id="generalCountryGeoId1" value="USA" />
            			<input type="hidden" name="roleTypeId" value="ACCOUNT">
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
								
								<@inputRow 
								id="${instanceId!}_partyName"
								name="name"
								placeholder=uiLabelMap.Name
								inputColSize="col-sm-12"
								required=false
								/> 	
								
								<@dropdownCell 
									id="${instanceId!}_generalState"
									name="generalState"
									placeholder="State"
									inputColSize="col-sm-12"
									required=false
									allowEmpty=true
									/>	
									
						  </div>
						 
						  <div class="col-md-4 col-md-4 form-horizontal">
						  		
								<@inputRow 
								id="${instanceId!}_phone"
								name="phone"
								placeholder=uiLabelMap.Phone
								inputColSize="col-sm-12"
								required=false
								/> 
								
								<@dropdownCell 
								id="${instanceId!}_generalCity"
								name="generalCity"
								placeholder="City"
								inputColSize="col-sm-12"
								required=false
								allowEmpty=true
								/>
								
						  </div>
						  
						  <div class="col-md-4 col-md-4 form-horizontal text-right">
						     	<@inputRow 
								id="${instanceId!}_emailAddress"
								name="email"
								placeholder=uiLabelMap.email
								inputColSize="col-sm-12"
								required=false
								/>
								
		                      <div class="search-btn">
	                           <@button 
				            		label="${uiLabelMap.Find}"
				            		id="dealerPicker-search-btn"
				            		/>
				            	<@reset
					        		label="${uiLabelMap.Reset}"
					        		/>
				           	 </div>	
				        		
                    	 </div>
					</div>
				</form>
			</div>
			
			<#-- <@AgGrid
				gridheadertitle=uiLabelMap.ListOfDealers
				gridheaderid="${instanceId!}_party-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="dealer-refresh-pref-btn"
				savePrefBtnId="dealer-save-pref-btn"
				clearFilterBtnId="dealer-clear-filter-btn"
				subFltrClearId="dealer-sub-filter-clear-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="PICKER_PARTY_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-dealer.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
			<@fioGrid 
			id="ListOfDealers_${instanceId!}"
			instanceId="PICKER_PARTY_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/picker/find-dealer.js"
			headerLabel=uiLabelMap.ListOfDealers
			headerId="${instanceId!}_-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			subFltrClearBtn = true
			headerBarClass="grid-header-no-bar"
			subFltrClearId="dealer-sub-filter-clear-btn"
			clearFilterBtnId="dealer-clear-filter-btn"	
			savePrefBtnId="dealer-save-pref-btn"
			subFltrClearId="dealer-sub-filter-clear-btn"
			/>
      	</div>
    	</div>
  	</div>
</div>
<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	loadCity();
	$("#dealerPicker-search-btn").trigger('click');
});	
			
var countryGeoId = $('#findDealerForm #generalCountryGeoId1').val();
if ( $('#findDealerForm #generalCountryGeoId1').val()) {
    getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId1', '${instanceId!}_generalState', 'stateList', 'geoId', 'geoName', '${stateValue!}','',true);
}

$("#${instanceId!}_generalState").change(function() {
    loadCity();
});

});

function loadCity() {
    var cityIdCode = "";
    
    var cityOptions = '<option value="" selected="">Select City</option>';
    let cityList = new Map();
    var state = $("#findDealerForm #${instanceId!}_generalState").val();
    
    $.ajax({
        type: "POST",
        url: "/uiadv-portal/control/searchZipCodeAssocs",
        data: {
            "state": state,
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function(result) {
            if (result.code == 200) {
                for (var i = 0; i < result.data.length; i++) {
                    var data = result.data[i];
                    cityList.set(data.city, data.city);
                }
            }
        }
    });

    for (let key of cityList.keys()) {
        if (cityIdCode && cityIdCode == key || (cityList.size === 1)) {
            cityOptions += '<option value="' + key + '" selected>' + cityList.get(key) + '</option>';
        } else {
            cityOptions += '<option value="' + key + '">' + cityList.get(key) + '</option>';
        }
    }
    
    $("#${instanceId!}_generalCity").html(cityOptions).change();
    $("#${instanceId!}_generalCity").dropdown('refresh');
}

</script>

</#macro>

<#macro campaignPicker instanceId fromAction="">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.findCampaign!}</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" id="findCampaignsForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            			<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
            			<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
								<#if domainEntityType?has_content && domainEntityType=="CAMPAIGN">
									<#assign campaignStatusList = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("statusId","description").from("StatusItem").orderBy("sequenceId").where("statusTypeId","MKTG_CAMP_STATUS").queryList()?if_exists />    
        						<#assign campaignStatus = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(campaignStatusList,"statusId","description",false)?if_exists />
								<#else>
								<#assign campaignStatus  = Static["org.fio.admin.portal.util.DataUtil"].toLinkedMap("MKTG_CAMP_PUBLISHED","Published","MKTG_CAMP_SCHEDULED","Scheduled","MKTG_CAMP_INPROGRESS","In Progress") />
								</#if>
								<#assign campaignTypeList = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("campaignTypeId","description").from("CampaignType").orderBy("sequenceNo").queryList()?if_exists />    
        						<#assign deliveryChannelList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(campaignTypeList,"campaignTypeId","description",false)?if_exists />
                					
								<@inputRow 
								id="${instanceId!}_campaignId"
								name="campaignId"
								placeholder=uiLabelMap.campaignId
								inputColSize="col-sm-12"
								required=false
								/> 	
								
							   	<@dropdownCell 
							    id="statusId"
							    options=campaignStatus?if_exists
							    required=false
							    allowEmpty=true
							    placeholder = uiLabelMap.campaignStatus
								/>
							
						  </div>
						 
						  <div class="col-md-4 col-md-4 form-horizontal">
						  
						  		<@inputRow 
								id="${instanceId!}_campaignName"
								name="campaignName"
								placeholder=uiLabelMap.campaignName
								inputColSize="col-sm-12"
								required=false
								/> 		

						  </div>
						  
						  <div class="col-md-4 col-md-4 form-horizontal text-left">
						  		
						  		<@dropdownCell 
							    id="campaignType"
							    options=deliveryChannelList?if_exists
							    required=false
							    allowEmpty=true
							    placeholder = uiLabelMap.deliveryChannel
								/>
								
							  <div class="search-btn">
		                           <@button 
					            		label="${uiLabelMap.Find}"
					            		id="campaignPicker-search-btn"
					            	/>
					           		<@reset
					        			label="${uiLabelMap.Reset}"
					        		/>
				        	   </div>
                    	 </div>
					</div>
				</form>
			</div>
			<#assign rightContent='<button title="Refresh" id="campaignPicker-refresh-btn" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-refresh" aria-hidden="true"></i>  </button>'/>
			
			<#-- <@AgGrid
				gridheadertitle=uiLabelMap.ListOfCampaigns
				gridheaderid="${instanceId!}_campaign-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				headerextra=rightContent!
				refreshPrefBtnId="campaignPicker-refresh-pref-btn"
				savePrefBtnId="campaignPicker-save-pref-btn"
				clearFilterBtnId="campaignPicker-clear-filter-btn"	
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="PICKER_CAMPAIGN_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-campaign.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="ListOfCampaignsGrid"
						instanceId="PICKER_CAMPAIGN_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/picker/find-campaign.js"
						headerLabel=uiLabelMap.ListOfCampaigns
						headerId="${instanceId!}_grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						headerBarClass="grid-header-no-bar"
						subFltrClearId="campaignPicker-sub-filter-clear-btn"
						savePrefBtnId="campaignPicker-save-pref-btn"
                   		clearFilterBtnId="campaignPicker-clear-filter-btn"
                    	exportBtnId="campaignPicker-export-btn"
						exportBtn=false
						headerExtra=rightContent!
						/>
      	</div>
    	</div>
  	</div>
</div>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#campaignPicker-refresh-btn").trigger('click');
});

});

</script>

</#macro>

<#macro templatePicker instanceId templateCategoryId="" fromAction="">
<div id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="max-width: 1200px;">
        <!-- Modal content-->
        <div class="modal-content" id="searchTemplate">
            <div class="modal-header">
                <@headerH4 title="${uiLabelMap.findTemplates}" class="modal-title">${uiLabelMap.findTemplates}</@headerH4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>
            <div class="modal-body">
            	<input type="hidden" name="temp_picker_instance" id="temp_picker_instance" value="${instanceId!}" />
            	<form method="post" id="${instanceId!'find'}_Form" name="${instanceId!'find'}_Form" action="" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            	<input type="hidden" name="templateCategories" value="${templateCategoryId!}"/>
            	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                <div class="row">
                    <div class="col-md-4 col-sm-4">
                        <div class="form-group row mr">
                            <@inputCell 
	                            id="tempalateName"
	                            inputColSize="col-sm-12"
	                            value=tempName!
	                            placeholder="Templates Name"
	                            required=false
	                            />
                        </div>
                    </div>
                    <div class="col-md-3 col-sm-2">
                        <div class="form-group">
                            <@dropdownCell 
	                            id = "emailEngine"
	                            options = emailEngineTypeList
	                            value=""
	                            placeholder="Email Engine"
	                            dataLiveSearch = true
	                             required=false
	                             allowEmpty=true
	                            />
                        </div>
                        
                    </div>
                    <div class="col-md-3 col-sm-3">
                    	<@button 
		            		id="find-temp-search-btn" 
                        	label="${uiLabelMap.Find}"
		            		/>
		           		<@reset
		        			label="${uiLabelMap.Reset}"
		        			id="temp-reset-btn" 
		        			/>
                    </div>
                </div>
                </form>
                <br>
                <div class="clearfix"></div>
               <#-- <@AgGrid
					gridheadertitle="List of Templates"
					gridheaderid="${instanceId!}_campaign-grid-action-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=false
					insertBtn=false
					updateBtn=false
					removeBtn=false
					refreshPrefBtnId="template-refresh-pref-btn"
					savePrefBtnId ="template-save-pref-btn"
					clearFilterBtnId ="template-clear-filter-btn"
					exportBtnId ="template-export-btn"
										
				    userid="${userLogin.userLoginId}" 
				    shownotifications="true" 
				    instanceid="TEMPLATE_PICKER_LIST" 
				    autosizeallcol="true"
				    debug="false"
				    />  -->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfTemplates"
			instanceId="TEMPLATE_PICKER_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/picker/find-template.js"
			headerLabel="List of Templates"
			headerId="${instanceId!}_campaign-grid-action-container"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=false
			subFltrClearBtn = false
			headerBarClass="grid-header-no-bar"
			savePrefBtnId ="template-save-pref-btn"
			clearFilterBtnId ="template-clear-filter-btn"
			subFltrClearId="template-sub-filter-clear-btn"
			/>
				 <script>
				 
				 	$('#${instanceId!}').on('shown.bs.modal', function (e) {
						$('#temp_picker_instance').val('${instanceId!}');
						$('#find-temp-search-btn').trigger('click');
					});
				 
				 </script>
                 <#-- <script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-template.js"></script> -->
                <div class="clearfix"></div>
                <span id="find_temp_trigger" ></span>
            </div>
        </div>
    </div>
</div>
</#macro>

<#macro templatePicker2 instanceId templateCategoryId="">
<div id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="max-width: 1200px;">
        <div class="modal-content" id="searchTemplate">
            <div class="modal-header">
                <@headerH4 title="${uiLabelMap.findTemplates}" class="modal-title">${uiLabelMap.findTemplates}</@headerH4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>
            <div class="modal-body">
            	<input type="hidden" name="temp_picker_instance" id="temp_picker_instance" value="${instanceId!}" />
            	<form method="post" id="${instanceId}_Form" name="${instanceId}_Form" action="" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                <div class="row">
                
                	<div class="col-lg-10 col-md-10 col-sm-10">
						<@dynaScreen 
							instanceId="FIND_TPL_PICKER"
							modeOfAction="CREATE"
							/>
					</div>
					
					<div class="col-lg-2 col-md-2 col-sm-2">
						<div class="text-right" style="padding-top: 35px;">
					     	<@button
					        id="${instanceId!}-search-btn"
					        label="${uiLabelMap.Find}"
					        />
			            </div>
					</div>
					                    
                </div>
                
                <div class="row padding-r">
                	<div class="col-lg-12 col-md-12">
                        <span id="${instanceId}-tpl-picker-add-btn" class="btn btn-xs btn-primary float-right text-right">Add</span>
                    </div>
                </div>
                
                <div id="${instanceId}_tplContent" class="row padding-r tplPicker" style="height: 350px">
                    
                </div>
                </form>
                <br>
                <div class="clearfix"></div>
            </div>
        </div>
    </div>
</div>

<div id="${instanceId}_tpl-des-modal" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title"><span id="${instanceId}_tpl-title">Use of Templates</span></h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group row">
                    <div class="col-sm-12">
                        <p id="${instanceId}_tpl-des" style="font-size: 20px;font-weight: normal;"></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {

$("#${instanceId}-tpl-picker-add-btn").click(function() {
	let tplId = $('input[name="${instanceId}-selectedTemplate"]:checked').val();
	let tplName = $('input[name="${instanceId}-selectedTemplate"]:checked').attr('attr-tplName');
	console.log(tplId);
	console.log(tplName);
	
	setPickerWindowValue(tplName, tplId);
});

$("#${instanceId!}-search-btn").click(function() {
	let form = document.getElementById("${instanceId!}_Form");
    let formData = new FormData(form);
	let serializedData = {};

    formData.forEach((value, key) => {
    	key = key.replace('_p', '');
      	serializedData[key] = value;
    });

	//console.log(serializedData);
	
	let tplContent = '';
	$.ajax({
		async: false,
		url: "/common-portal/control/findTemplatesAjax",
		type:"POST",
		data: JSON.parse(JSON.stringify($("#${instanceId!}_Form").serialize())),
		//data: serializedData,
		success: function(data){
			//gridApi.setRowData(data.data);
			console.log(data.data);
			for (let i = 0; i < data.data.length; i++) {
        		var tpl = data.data[i];
        		tplContent += '<div class="col-md-4 col-sm-4">'
                        + '<div class="thumb-template-shadow">'
                        + '    <label class="image-checkbox" style="margin-bottom: 0px;">'
                        + '        <input type="radio" name="${instanceId}-selectedTemplate" attr-tplName="'+tpl.templateName+'" value="'+tpl.templateId+'">'
                        + '        <img src="'+tpl.previewImg+'" alt="Checked" class="template-img">'
                        + '    </label>'
                        + '    <div class="thumb-template-info">'
                        + '        <div class="thumb-template-title">'
                        + '            <div class="thumb-template-name">'
                        + '                <h2 class="template-title"> '+tpl.templateName+' </h2>'
                        + '            </div>'
                        + '        </div>'
                        + '    </div>'
                        + '    <div class="template-actions">'
                        + '        <div class="button-action">'
                        + '            <span aria-hidden="true" attr-tplId="'+tpl.templateId+'" class="btn btn-clas ${instanceId}-useOfTemplate-btn">Use</span>'
                        + '        </div>'
                        + '        <div class="button-demonstration">'
                        + '            <span aria-hidden="true" attr-tplId="'+tpl.templateId+'" class="btn btn-clas ${instanceId}-benefitOfTemplate-btn">Benefits</span>'
                        + '        </div>'
                        + '        <div class="button-more">'
                        + '            <a href="/campaign/control/getTemplate?templateId='+tpl.templateId+'" target="_blank">View</a>'
                        + '        </div>'
                        + '    </div>'
                        + '</div>'
                	+ '</div>';
        	}
		}
	});
	//alert(tplContent);
	$("#${instanceId}_tplContent").html(tplContent);
	
	$('.${instanceId}-useOfTemplate-btn').unbind( "click" );
	$('.${instanceId}-useOfTemplate-btn').bind( "click", function( event ) {
		let templateId = $(this).attr('attr-tplId');
		console.log('templateId> '+templateId);
		$("#${instanceId}_tpl-des").html(getTemplateDescription(templateId, 'useOfTemplate'));
		$("#${instanceId}_tpl-title").html('Use of Templates');
		$('#${instanceId}_tpl-des-modal').modal('show');
	});
	
	$('.${instanceId}-benefitOfTemplate-btn').unbind( "click" );
	$('.${instanceId}-benefitOfTemplate-btn').bind( "click", function( event ) {
		let templateId = $(this).attr('attr-tplId');
		console.log('templateId> '+templateId);
		$("#${instanceId}_tpl-des").html(getTemplateDescription(templateId, 'benefitOfTemplate'));
		$("#${instanceId}_tpl-title").html('Bebefits of Templates');
		$('#${instanceId}_tpl-des-modal').modal('show');
	});
	
});

$("#emailEngine_p").change(function() {
	var nonSelectContent = "<span class='nonselect'>Select Category</span>";
	var nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select Category</option>';		
								
	$.ajax({
		type: "POST",
     	url: "/dyna-screen/control/getDynamicData",
        //data: {"productId": $(this).val(),"externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        data : {
			"filterData" : { lookupFieldFilter: '{ 	"entity_name": "TemplateCategory", 	"name_field": "templateCategoryName", "value_field": "templateCategoryId", "filter_value": {"parentTemplateCategoryId": "'+$(this).val()+'"} }' }
			,"externalLoginKey": "${requestAttributes.externalLoginKey!}"
		},
        async: false,
        success: function (data) {   
            if (data.code == 200) {
            	for (var key in data.fieldDataList) {
					if (key) {
						nameOptions += '<option value="'+key+'">'+data.fieldDataList[key]+'</option>';
					}
				}
            }
        }
	});    
	
	$("#templateCategoryId_p").html( nameOptions );
	$("#templateCategoryId_p").dropdown('refresh');
});

$("#fromDate_p_picker").on("dp.change", function (e) {
 	$('#thruDate_p_picker').data("DateTimePicker").minDate(e.date);
});      
$("#thruDate_p_picker").on("dp.change", function (e) {
   $('#fromDate_p_picker').data("DateTimePicker").maxDate(e.date);
});

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	//$('#find-temp-search-btn').trigger('click');
	$("#emailEngine_p").trigger('change');	
	$("#${instanceId!}-search-btn").trigger('click');
});
	
});

const getTemplateDescription = (templateId, fieldId) => {
	let description = '';
	$.ajax({
		type: "POST",
     	url: "/dyna-screen/control/getDynamicData",
        //data: {"productId": $(this).val(),"externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        data : {
			"filterData" : { lookupFieldFilter: '{ 	"entity_name": "TemplateMaster", 	"name_field": "'+fieldId+'", "value_field": "templateId", "filter_value": {"templateId": "'+templateId+'"} }' }
			,"externalLoginKey": "${requestAttributes.externalLoginKey!}"
		},
        async: false,
        success: function (data) {   
            if (data.code == 200) {
            	for (var key in data.fieldDataList) {
					if (key) {
						description = data.fieldDataList[key];
						break;
					}
				}
            }
        }
	});
	return description;
}
 
</script>
</#macro>

<#macro templateDetail instanceId templateId>
<div id="${instanceId}_tpl-des-modal" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title"><span id="${instanceId}_tpl-title">Use of Templates</span></h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group row">
                    <div class="col-sm-12">
                        <p id="${instanceId}_tpl-des" style="font-size: 20px;font-weight: normal;"></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>	

<script>
$(document).ready(function() {
	$('.${instanceId}-useOfTemplate-btn').unbind( "click" );
	$('.${instanceId}-useOfTemplate-btn').bind( "click", function( event ) {
		let templateId = '${templateId!}';
		console.log('templateId> '+templateId);
		$("#${instanceId}_tpl-des").html(getTemplateDescription(templateId, 'useOfTemplate'));
		$("#${instanceId}_tpl-title").html('Use of Templates');
		$('#${instanceId}_tpl-des-modal').modal('show');
	});
	
	$('.${instanceId}-benefitOfTemplate-btn').unbind( "click" );
	$('.${instanceId}-benefitOfTemplate-btn').bind( "click", function( event ) {
		let templateId = '${templateId!}';
		console.log('templateId> '+templateId);
		$("#${instanceId}_tpl-des").html(getTemplateDescription(templateId, 'benefitOfTemplate'));
		$("#${instanceId}_tpl-title").html('Bebefits of Templates');
		$('#${instanceId}_tpl-des-modal').modal('show');
	});
});

const getTemplateDescription = (templateId, fieldId) => {
	let description = '';
	$.ajax({
		type: "POST",
     	url: "/dyna-screen/control/getDynamicData",
        //data: {"productId": $(this).val(),"externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        data : {
			"filterData" : { lookupFieldFilter: '{ 	"entity_name": "TemplateMaster", 	"name_field": "'+fieldId+'", "value_field": "templateId", "filter_value": {"templateId": "'+templateId+'"} }' }
			,"externalLoginKey": "${requestAttributes.externalLoginKey!}"
		},
        async: false,
        success: function (data) {   
            if (data.code == 200) {
            	for (var key in data.fieldDataList) {
					if (key) {
						description = data.fieldDataList[key];
						break;
					}
				}
            }
        }
	});
	return description;
}
</script>
</#macro>

<#macro contactPicker instanceId fromAction="">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.findParty!}</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" id="findPartyForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
            		
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
							
								<@inputRow 
								id="firstName"
								placeholder=uiLabelMap.firstName
								inputColSize="col-sm-12"
								required=false
								/> 
								
								<@inputRow 
								id="lastName"
								placeholder=uiLabelMap.lastName
								inputColSize="col-sm-12"
								required=false
								/>
							
						  </div>
						 
						  <div class="col-md-4 col-md-4 form-horizontal">
						  
						  		<@inputRow 
								id="emailAddress"
								placeholder=uiLabelMap.email
								inputColSize="col-sm-12"
								required=false
								/> 
								
								<@inputRow 
								id="contactNumber"
								placeholder="Phone"
								inputColSize="col-sm-12"
								required=false
								/>	

						  </div>
						  
						  <div class="col-md-4 col-md-4 form-horizontal text-right">
						  		<@inputRow 
								id="partyId"
								placeholder="Contact ID"
								inputColSize="col-sm-12"
								required=false
								/> 
								<div class="search-btn"
		                           <@button 
					            		label="${uiLabelMap.Find}"
					            		id="partyPicker-search-btn"
					            	/>
					           		<@reset
					        			label="${uiLabelMap.Reset}"
					        		/>
				        		 </div>
                    	 </div>
					</div>
				</form>
			</div>
			
			<@AgGrid
				gridheadertitle=uiLabelMap.ListOfPartys
				gridheaderid="${instanceId!}_party-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="contactPicker-refresh-pref-btn"
				savePrefBtnId="contactPicker-save-pref-btn"
				clearFilterBtnId="contactPicker-clear-filter-btn"	
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="CONTACT_PICKER_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-contact.js"></script>
      	</div>
    	</div>
  	</div>
</div>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#partyPicker-search-btn").trigger('click');
});

});
</script>
</#macro>

<#macro customerPicker instanceId fromAction="" pickerTitle="Customers">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Find ${pickerTitle!}</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" name="findCustForm" id="findCustForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
            		<input type="hidden" name="isHomeOwner" value="Y"/>
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
								
								<@inputRow 
								id="customerId"
								placeholder="Customer ID"
								inputColSize="col-sm-12"
								required=false
								/>
								
								<@inputRow 
								id="emailAddress"
								placeholder=uiLabelMap.email
								inputColSize="col-sm-12"
								required=false
								/>
								
						  </div>
						 
						  <div class="col-md-4 col-md-4 form-horizontal">
						  		
						  		<@inputRow 
								id="firstName"
								placeholder=uiLabelMap.firstName
								inputColSize="col-sm-12"
								required=false
								/>
								
								<@inputRow 
								id="contactNumber"
								placeholder="Phone"
								inputColSize="col-sm-12"
								required=false
								/>	

						  </div>
						  
						  <div class="col-md-4 col-md-4 form-horizontal text-right">
						  	 <@inputRow 
								id="lastName"
								placeholder=uiLabelMap.lastName
								inputColSize="col-sm-12"
								required=false
								/>
						  	<div class="search-btn">
	                           <@button 
				            		label="${uiLabelMap.Find}"
				            		id="customerPicker-search-btn"
				            	/>
				           		<@reset
				        			label="${uiLabelMap.Reset}"
				        		/>
				        	</div>	
                    	 </div>
					</div>
				</form>
			</div>
			
			<#-- <@AgGrid
				gridheadertitle="List of ${pickerTitle!}"
				gridheaderid="${instanceId!}_Customer-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="customerPicker-refresh-pref-btn"
				savePrefBtnId="customerPicker-save-pref-btn"
				clearFilterBtnId="customerPicker-clear-filter-btn"
				subFltrClearId="customerPicker-sub-filter-clear-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="CUSTOMER_PICKER_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-customer.js"></script>-->
			<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  			<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="${instanceId!}_Customer-grid"
			instanceId="CUSTOMER_PICKER_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/picker/find-customer.js"
			headerLabel="List of ${pickerTitle!}"
			headerId="${instanceId!}_Customer-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=false
			subFltrClearBtn=false
			savePrefBtnId ="customerPicker-save-pref-btn"
			clearFilterBtnId ="customerPicker-clear-filter-btn"
			subFltrClearId="customerPicker-sub-filter-clear-btn"
			/>
      	</div>
    	</div>
  	</div>
</div>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#customerPicker-search-btn").trigger('click');
});

});
</script>
</#macro>
<#macro contractorPicker instanceId fromAction="">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Find Contractor</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" name="findContractorForm" id="findContractorForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
            		<input type="hidden" name="roleTypeId" value="CUSTOMER"/>
            		<input type="hidden" name="isContractor" value="Y"/>
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
								
								<@inputRow 
								id="customerId"
								placeholder="Contractor ID"
								inputColSize="col-sm-12"
								required=false
								/>
								
								<@inputRow 
								id="emailAddress"
								placeholder=uiLabelMap.email
								inputColSize="col-sm-12"
								required=false
								/>
								
						  </div>
						 
						  <div class="col-md-4 col-md-4 form-horizontal">
						  
						  		<@inputRow 
								id="firstName"
								placeholder=uiLabelMap.firstName
								inputColSize="col-sm-12"
								required=false
								/>
								
								<@inputRow 
								id="contactNumber"
								placeholder="Phone"
								inputColSize="col-sm-12"
								required=false
								/>	

						  </div>
						  
						  <div class="col-md-4 col-md-4 form-horizontal text-right">
						  	 
						  	 <@inputRow 
								id="lastName"
								placeholder=uiLabelMap.lastName
								inputColSize="col-sm-12"
								required=false
								/>
								
						  	<div class="search-btn">
	                           <@button 
				            		label="${uiLabelMap.Find}"
				            		id="contractorPicker-search-btn"
				            	/>
				           		<@reset
				        			label="${uiLabelMap.Reset}"
				        		/>
				        	</div>	
                    	 </div>
					</div>
				</form>
			</div>
			
			<#-- <@AgGrid
				gridheadertitle="List of Contractors"
				gridheaderid="${instanceId!}_Contractor-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="contractorPicker-refresh-pref-btn"
				savePrefBtnId="contractorPicker-save-pref-btn"
				clearFilterBtnId="contractorPicker-clear-filter-btn"
				subFltrClearId="contractorPicker-sub-filter-clear-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="CONTRACTOR_PICKER_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-contractor.js"></script>-->
			<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  			<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="${instanceId!}_Contractor-grid"
			instanceId="CONTRACTOR_PICKER_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/picker/find-contractor.js"
			headerLabel="List of Contractors"
			headerId="${instanceId!}_Contractor-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=false
			subFltrClearBtn=false
			savePrefBtnId ="contractorPicker-save-pref-btn"
			clearFilterBtnId ="contractorPicker-clear-filter-btn"
			subFltrClearId="contractorPicker-sub-filter-clear-btn"
			/>
      	</div>
    	</div>
  	</div>
</div>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#contractorPicker-search-btn").trigger('click');
});

});
</script>
</#macro>
<#-- added for customer reassign -->
<#macro responsiblePickerCustomer instanceId fromAction="">

<!-- Find parent account pop for create and update account-->
<div id="${instanceId!}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Find Team Members</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="popup-bot">
               <form method="post" action="#" id="FindTeamMembers" class="form-horizontal" name="FindTeamMembers" novalidate="novalidate" data-toggle="validator">
                  <div class="row">
                     <div class="col-lg-4 col-md-2 col-sm-2">
                        <div class="form-group row mr">
                           <input type="text" class="form-control input-sm" id="fName" name="fName" value="" placeholder="First Name">
                        </div>
                     </div>
                     <div class="col-lg-4 col-md-2 col-sm-2">
                        <div class="form-group row mr">
                           <input type="text" class="form-control input-sm" id="lName" name="lName" placeholder="Last Name">
                        </div>
                     </div>
                     <div class="col-md-1 col-sm-1">
                        <button type="button" class="btn btn-sm btn-primary navbar-dark m5" onclick="javascript:getTeamMembersPRFCust();">Find Team Members</button>
                     </div>
                  </div>
               </form>
               <div class="clearfix"> </div>
            </div>
            <div class="clearfix"> </div>
            <div class="popup-agtitle" id="c-team-member">
               <h2 class="float-left">Team Members</h2>
            </div>
            <div class="clearfix"> </div>
            <div class="table-responsive">
               <table id="ajaxFindTeamMembersdatatableCust" class="table table-striped">
                  <thead>
                     <tr>
                        <th>Name</th>
                     </tr>
                  </thead>
               </table>
            </div>
         </div>
         <div class="modal-footer">
            <button type="sbmit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
         </div>
      </div>
   </div>
</div>

<form name="personResponsiblePartyCust" id="personResponsiblePartyCust" method="POST" action="<@ofbizUrl>personResponsibleParty</@ofbizUrl>" style="display:none;">
   <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
   <input type="hidden" name="donePage" value="${requestURI?if_exists}"/>
   <input type="hidden" name="roleTypeIdFrom" value="${partyRoleTypeId?if_exists}"/>
   <input type="hidden" name="accountPartyId" value=""/>
   <input type="hidden" name="salesOpportunityId" value="${salesOpportunityId?if_exists}"/>
   <input type="hidden" name="srNumber" value="${srNumber?if_exists}"/>
   <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
</form>

<script>

$(document).ready(function() {
	$('#fName').val("");
	$("#lName").val("");
	getTeamMembersPRFCust();
});

function reassignPartyCust(value) {
	if (value != null && value != "") {
		$("#personResponsiblePartyCust input[name=accountPartyId]").val(value);
		document.personResponsiblePartyCust.submit();
	}
}

function getTeamMembersPRFCust() {
	var fName = $("#fName").val();
	var lName = $("#lName").val();
	var url = "/common-portal/control/getTeamMembers?firstName=" + fName
			+ "&lastName=" + lName
			+ "&externalLoginKey=${requestAttributes.externalLoginKey!}";
	$('#ajaxFindTeamMembersdatatableCust').DataTable(
			{
				"processing" : true,
				"serverSide" : true,
				"destroy" : true,
				"ajax" : {
					"url" : url,
					"type" : "POST"
				},
				"Paginate" : true,
				"pageLength" : 10,
				"bAutoWidth" : false,
				"stateSave" : true,
				"columns" : [
						{
							"data" : "partyId",
							"render" : function(data, type, row, meta) {
							if ('${selectedRMId!}'!=null && '${selectedRMId!}'==data){
								return row.name+ '(' + data	+ ')';
							}else{
								data = '<a href="#" onclick=reassignPartyCust("'
										+ data + '")>' + row.name + '(' + data
										+ ')</a>';
								return data;
							}
							
							}
						},

				]
			});
}

</script>

</#macro>

<#macro resourcePickerFromCal instanceId calInstanceId searchFromId="" isActivateLegend="N" isInitializeCal="N" durationFieldId="duration">

<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-lg" style="width: 80%;max-width: 1200px;">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 id="${instanceId!}_des_title" class="modal-title"></h2><span id='calendar_type_title'>Technicians</span>
        <ul class="flot-icone">
        	<li class="mt-0">
     		<@dropdownCell 
			id="techPriorityType"
			options=techPriorityTypeList
			placeholder=uiLabelMap.PriorityType
			style="width: 200px"
			required=false
			allowEmpty=true
			/>
        	</li>
     		<li class="mt-0">
     		<span id="apply-selected-cal-slot" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Apply</span>
        	</li>
        	<li class="mt-0">
     		<button type="reset" class="close" data-dismiss="modal">&times;</button>
        	</li>
        </ul>	
      </div>
      <div class="modal-body">
        <div class="clearfix"></div>
        
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		        
		<@mobiScroll 
			instanceId="${calInstanceId!}"
			searchFromId="${searchFromId!}"
			isInitializeCal="${isInitializeCal!}"
			/>
			
		<#if isActivateLegend?has_content && isActivateLegend=="Y">	
		<ul id="${calInstanceId!}-calendar-legend" class="legend dash-panel"></ul>
		</#if>			
		</div>
        
      </div>
      <div class="modal-footer">
        <button type="sbmit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<form id="tech-cal-search-from" method="post" data-toggle="validator">
	<input type="hidden" name="estimatedStartDate_date" value="">
	<input type="hidden" name="estimatedCompletionDate_date" value="">
	<input type="hidden" name="estimatedStartDate_time" value="">
	<input type="hidden" name="estimatedCompletionDate_time" value="">
	<input type="hidden" name="custRequestId" value="${(parameters.domainEntityId)!}">
	<input type="hidden" name="isAllowSelect" value="Y">
	<input type="hidden" name="techPriorityType" value="">
	<input type="hidden" name="assignedTechLoginIds" value="">
	<input type="hidden" name="isSkipCalSlot" value="Y">
	<input type="hidden" name="isResourceType" value="">
	<input type="hidden" name="is3PartyTechnician" value="${is3PartyTechnician!}">
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}">
</form>

<script>

var selectedTechCalSlot = new Map();
var calInst;

const getAssignedTechLoginIds = () => {
	var assignedTechLoginIds = new Set();
	//console.log($("#owner").val());
	for (const prop of $("#owner").val()) {
		//console.log(prop);
		assignedTechLoginIds.add(prop);
	}
	console.log(assignedTechLoginIds);
	
	$('#techPriorityType').val('');
	if (assignedTechLoginIds.size > 0) {
		return Array.from(assignedTechLoginIds).join();
	}
	return '';
}

$(document).ready(function() {

$('#book-appointment').on('click', function() {

	if ($('input[name=isSchedulingRequired]:checked').val() === 'N') {
		showAlert('error', 'Function Not Available for Non-scheduling Tasks!');
		return;
	}

	if ($('#statusId').val() === 'IA_OPEN') {
		showAlert('error', 'Schedule Task function is disabled due to Task status is in Open!');
		return;
	}

	if (!$('#estimatedStartDate_date').val() || !$('#estimatedCompletionDate_date').val() || !$('#duration').val()) {
		showAlert('error', 'Please select duration, schedule start date and end date!');
		return;
	}
	
	$('#tech-cal-search-from input[name="estimatedStartDate_date"]').val( $('#estimatedStartDate_date').val() );
	$('#tech-cal-search-from input[name="estimatedCompletionDate_date"]').val( $('#estimatedCompletionDate_date').val() );
	$('#tech-cal-search-from input[name="estimatedStartDate_time"]').val( $('#estimatedStartDate_time').val() );
	$('#tech-cal-search-from input[name="estimatedCompletionDate_time"]').val( $('#estimatedCompletionDate_time').val() );
	$('#tech-cal-search-from input[name="isSkipCalSlot"]').val( 'N' );
	
	$('#techPriorityType').val('');
	var assignedTechLoginIds = getAssignedTechLoginIds();
	if (assignedTechLoginIds.length > 0) {
		$('#tech-cal-search-from input[name="assignedTechLoginIds"]').val( assignedTechLoginIds );
		$('#tech-cal-search-from input[name="techPriorityType"]').val('REEB-ASSIGNED');
		
		$('#techPriorityType').val('REEB-ASSIGNED');
		$('#techPriorityType').trigger('change');
	}
		
	$('#avlTechnicianPicker_des_title').html("Schedule Calendar");
	$('#avlTechnicianPicker').modal("show");
	
	var context = new Map();
	context.set('min', new Date($('#estimatedStartDate_date').val() + " " + "00:00"));
	context.set('max', new Date($('#estimatedCompletionDate_date').val() + " " + "00:00"));
	
	calInst = initiateEventCal(context);
	
});	

$('#avlTechnicianPicker').on('shown.bs.modal', function (e) {
	console.log('tech cal shown');
	$('.ui.dropdown.search').dropdown({
		clearable: true
	});	
	if ($('#techPriorityType').val() == 'REEB-ASSIGNED') {
		$('.selected-cal-slot').attr('checked', true);
	}
}); 

$('#techPriorityType').on('change', function() {
	console.log('onchange techPriorityType: '+$(this).val());
	<#if searchFromId?has_content>
	$('#tech-cal-search-from input[name="techPriorityType"]').val( $(this).val() );
	</#if>
	if (!calInst) {
		return false;
	}
	loadEvents(calInst, eventInst);
	
	if ($('#techPriorityType').val() == 'REEB-ASSIGNED') {
		setTimeout(function(){
			console.log('assigned checked');
			$('.selected-cal-slot').attr('checked', true);
		}, 1000);
	}
});

$('#release-appointment').on('click', function() {

	$.ajax({
		type: "POST",
     	url: "/admin-portal/control/releaseCalBooking?externalLoginKey=${requestAttributes.externalLoginKey!}",
        data: {"domainEntityType": "ACTIVITY", "domainEntityId": "${inputContext.activityId!}"},
        async: false,
        success: function (data) {   
            if (data.code == 200) {
            	showAlert("success", "Successfully release calendar booking");
            	$("#ownerBookedCalSlots").val('');
            	$(".${durationFieldId}").removeClass("disabled");
            	$('input[name=isSchedulingRequired]').prop("disabled", false);
            	selectedTechCalSlot = new Map();
            	$("#owner").val([]);
				$('#owner').dropdown('clear');
				$("#owner").trigger("change");
				
				prepareActivityDateInput($('#statusId').val());
				
				$('#tech-cal-search-from input[name="assignedTechLoginIds"]').val('');
				$("#main .flot-icone")[0].innerHTML = "";
            } else {
            	showAlert("error", data.message);
            }
        }
	});
	
});	

<#if selectedCalSlot?has_content>
let ownerBookedCalSlot = JSON.parse('${StringUtil.wrapString(ownerBookedCalSlot)}')
for (var userLoginId in ownerBookedCalSlot){
    var calSlot = ownerBookedCalSlot[userLoginId];
    
    let techCalSlot = new Map();
	techCalSlot.set("startTime", calSlot.startTime);
	techCalSlot.set("endTime", calSlot.endTime);
	selectedTechCalSlot.set(userLoginId, techCalSlot);
}
</#if>	

<#if workStartTime?has_content>
if ($('#estimatedStartDate_time').val() == '0:00') {
	$('#estimatedStartDate_time').timepicker('setTime', '${StringUtil.wrapString(workStartTime)}');
}
</#if>
<#if workEndTime?has_content>
if ($('#estimatedCompletionDate_time').val() == '0:00') {
	$('#estimatedCompletionDate_time').timepicker('setTime', '${StringUtil.wrapString(workEndTime)}');
}
</#if>

<#if ownerBookedCalSlot?has_content>
$(".${durationFieldId}").addClass("disabled");
$('input[name=isSchedulingRequired]').prop("disabled", true);
$("#estimatedStartDate_date").prop("disabled", true);
$("#estimatedStartDate_time").prop("disabled", true);
$("#estimatedCompletionDate_date").prop("disabled", true);
$("#estimatedCompletionDate_time").prop("disabled", true);
</#if>

mobiscroll.settings = {
	eventText: 'technician',   
	eventsText: 'technicians',
};
			
$('#avlTechnicianPicker').on('shown.bs.modal', function (e) {
	
	$('#apply-selected-cal-slot').unbind("click");
	$('#apply-selected-cal-slot').bind( "click", function(event) {
		var count = 0;
		console.log('is modal open: '+$('#avlTechnicianPicker').is(':visible'));
		$("input:checkbox[name=cal-slot]:checked").each(function(){
    		//alert($(this).attr('data-id')+" "+$(this).attr('data-startTime')+" "+$(this).attr('data-endTime'));
    		
    		var startTime = $(this).attr('data-startTime');
    		var endTime = $(this).attr('data-endTime');
    		
    		let techCalSlot = new Map();
			techCalSlot.set("startTime", startTime);
			techCalSlot.set("endTime", endTime);
			
			selectedTechCalSlot.set($(this).attr('data-id'), techCalSlot);
			
			var owner = $("#owner").val();
			owner.push($(this).attr('data-id'));
			$('#owner').val(owner); 
									
			if (count == 0) {
				if (startTime) {
					var estimatedStartDate = startTime.split(" ");
					$('#estimatedStartDate_date').val(estimatedStartDate[0]);
					$('#estimatedStartDate_time').timepicker('setTime', estimatedStartDate[1]);
					
					<#if durationFieldId?has_content>
					if($('#${durationFieldId}').val()) {
						var estimatedCompletionDate = moment(startTime).add($('#${durationFieldId}').val(), "hours").format('YYYY-MM-DD HH:mm');
						estimatedCompletionDate = estimatedCompletionDate.split(" ");
						$('#estimatedCompletionDate_date').val(estimatedCompletionDate[0]);
						$('#estimatedCompletionDate_time').timepicker('setTime', estimatedCompletionDate[1]);
					}
					</#if>
				}
				/*if (endTime) {
					var estimatedCompletionDate = endTime.split(" ");
					$('#estimatedCompletionDate_date').val(estimatedCompletionDate[0]);
					$('#estimatedCompletionDate_time').timepicker('setTime', estimatedCompletionDate[1]);
				}*/
			}
			count += 1;
		});
		
		if (count == 0) {
			showAlert('error', "Please select technicians!");
		} else {
			$("#owner").trigger("change");
			
			var ownerBookedCalSlots = JSON.stringify(map_to_object(selectedTechCalSlot))
			$('#ownerBookedCalSlots').val(ownerBookedCalSlots);
			
			$('#avlTechnicianPicker').modal("hide");
		}
				
	});
	
	if (!calInst) {
		calInst.navigate(new Date($('#estimatedStartDate_date').val() + " " + "00:00"));
	}
});	

$("#estimatedStartDate_date_picker").on("dp.change", function (e) {
 	adjustSchEndDate();
});

$("#estimatedStartDate_time").on("change", function (e) {
 	adjustSchEndDate();
});

$("#${durationFieldId}").on("change", function (e) {
	console.log('duration change...');
	if ($("#ownerBookedCalSlots").val()) {
		showAlert('error', "Release appointment to enable edit for schedule date!");
	} else {
		adjustSchEndDate();	
	}
});
	
});

const adjustSchEndDate = () => {
	let workEffortPurposeTypeId = $('#workEffortPurposeTypeId').val();
	let estimatedStartDate = $('#estimatedStartDate_date').val() + " " + $('#estimatedStartDate_time').val();
	
	if (workEffortPurposeTypeId && (workEffortPurposeTypeId=='TEST_WORK_TYPE' || workEffortPurposeTypeId=='TEST_WORK_TYPE_001')) {
		<#if durationFieldId?has_content>
	 	if($('#${durationFieldId}').val() && $('#estimatedStartDate_date').val()) {
	 		var estimatedCompletionDate = moment(estimatedStartDate).add($('#${durationFieldId}').val(), "hours").format('YYYY-MM-DD HH:mm');
			estimatedCompletionDate = estimatedCompletionDate.split(" ");
			console.log('estimatedCompletionDate[1] > '+estimatedCompletionDate[1]);
			$('#estimatedCompletionDate_date').val(estimatedCompletionDate[0]);
			$('#estimatedCompletionDate_time').timepicker('setTime', estimatedCompletionDate[1]);
			
			//$("#owner").val([]);
			//$('#owner').dropdown('clear');
			
			$('#tech-cal-search-from input[name="estimatedStartDate_date"]').val( $('#estimatedStartDate_date').val() );
			$('#tech-cal-search-from input[name="estimatedCompletionDate_date"]').val( $('#estimatedCompletionDate_date').val() );
			$('#tech-cal-search-from input[name="estimatedStartDate_time"]').val( $('#estimatedStartDate_time').val() );
			$('#tech-cal-search-from input[name="estimatedCompletionDate_time"]').val( $('#estimatedCompletionDate_time').val() );
	 	}
		</#if>
	} else {
		if($('#estimatedStartDate_date').val()) {
			$('#estimatedCompletionDate_date').val($('#estimatedStartDate_date').val());
			
			$('#estimatedStartDate_time').val('0:00');
	    	$('#estimatedCompletionDate_time').timepicker('setTime', '0:00');
	    	$("#estimatedStartDate_time").prop("disabled", true);
	    	$("#estimatedCompletionDate_time").prop("disabled", true);
			
			$('#tech-cal-search-from input[name="estimatedStartDate_date"]').val( $('#estimatedStartDate_date').val() );
			$('#tech-cal-search-from input[name="estimatedCompletionDate_date"]').val( $('#estimatedCompletionDate_date').val() );
			$('#tech-cal-search-from input[name="estimatedStartDate_time"]').val( '0:00' );
			$('#tech-cal-search-from input[name="estimatedCompletionDate_time"]').val( '0:00' );
		}
	}
 	
}

function selectCalEvent(id, startTime, endTime) {
	
	$('#avlTechnicianPicker').modal("hide");
	
	let techCalSlot = new Map();
	techCalSlot.set("startTime", startTime);
	techCalSlot.set("endTime", endTime);
	
	if (selectedTechCalSlot.get(id)) {
		var temptechCalSlot = selectedTechCalSlot.get(id);
		console.log("temptechCalSlot: "+temptechCalSlot);
		var tempStartTime = temptechCalSlot.get("startTime");
		var tempEndTime = temptechCalSlot.get("endTime");
		console.log("tempStartTime: "+tempStartTime+", tempEndTime: "+tempEndTime);
		if (tempStartTime === startTime && tempEndTime === endTime) {
			showAlert("error", "Technician already selected!");
			return;
		}
	}
	
	selectedTechCalSlot.set(id, techCalSlot);

	var owner = $("#owner").val();
	owner.push(id);
	$('#owner').val(owner);
	$("#owner").trigger("change");
	
	if (startTime) {
		var estimatedStartDate = startTime.split(" ");
		$('#estimatedStartDate_date').val(estimatedStartDate[0]);
		$('#estimatedStartDate_time').timepicker('setTime', estimatedStartDate[1]);
		
		<#if durationFieldId?has_content>
		if($('#${durationFieldId}').val()) {
			var estimatedCompletionDate = moment(startTime).add($('#${durationFieldId}').val(), "hours").format('YYYY-MM-DD HH:mm');
			estimatedCompletionDate = estimatedCompletionDate.split(" ");
			$('#estimatedCompletionDate_date').val(estimatedCompletionDate[0]);
			$('#estimatedCompletionDate_time').timepicker('setTime', estimatedCompletionDate[1]);
		}
		</#if>
	}
	/*if (endTime) {
		var estimatedCompletionDate = endTime.split(" ");
		$('#estimatedCompletionDate_date').val(estimatedCompletionDate[0]);
		$('#estimatedCompletionDate_time').timepicker('setTime', estimatedCompletionDate[1]);
	}*/
	
	var ownerBookedCalSlots = JSON.stringify(map_to_object(selectedTechCalSlot))
	$('#ownerBookedCalSlots').val(ownerBookedCalSlots);
	//alert($('#ownerBookedCalSlots').val());
	
}
</script>

</#macro>


<#macro orderPicker instanceId fromAction="">

<div id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Find Order</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" name="find-order-form" id="find-order-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						            			
        				<div class="row">
						<div class="col-lg-10 col-md-10 col-sm-10">
							<@dynaScreen 
								instanceId="FIND_ORDER_PICKER"
								modeOfAction="CREATE"
								/>
						</div>
						
						<div class="col-lg-2 col-md-2 col-sm-2">
							<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
						     	<@button
						        id="${instanceId!}-search-btn"
						        label="${uiLabelMap.Find}"
						        />	
						     	<#-- <@reset
						     	id="${instanceId!}-reset-btn"
								label="${uiLabelMap.Reset}"/> -->
				            </div>
						</div>
						
						</div>
						            			
					</form>
				</div>
			
				<#-- <@AgGrid
				gridheadertitle="List of Orders"
				gridheaderid="${instanceId!}-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="orderPicker-refresh-pref-btn"
				savePrefBtnId="orderPicker-save-pref-btn"
				clearFilterBtnId="orderPicker-clear-filter-btn"
				subFltrClearId="orderPicker-sub-filter-clear-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="ORDER_PICKER_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
				<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-order.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="${instanceId!}-grid"
			instanceId="ORDER_PICKER_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/picker/find-order.js"
			headerLabel="List of Orders"
			headerId="${instanceId!}-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=false
			subFltrClearBtn=false
			savePrefBtnId ="orderPicker-save-pref-btn"
			clearFilterBtnId ="orderPicker-clear-filter-btn"
			subFltrClearId="order-clear-sub-ftr"
			/>
      		</div>
      	
    	</div>
  	</div>
</div>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function(){
	
	<#if defaultLocationId?has_content>
	$("#pickerLocation").val("${defaultLocationId!}");
	$("#pickerLocation").trigger( "change" )
	$("div.ui.dropdown.search.form-control.fluid.show-tick.pickerLocation.selection > i").addClass("clear");
	</#if>
	
	<#if inputContext.orderPartyId?has_content>
	$("#find-order-form input[name=orderPartyId]").val("${inputContext.orderPartyId!}");
	$("#orderPartyId_desc").val("${StringUtil.wrapString(inputContext.orderPartyId_desc?if_exists)}");
	</#if>

	$('#${instanceId!}-search-btn').trigger("click");
	//console.log('currentPickerInputId: '+currentPickerInputId+', currentPickerWindowId: '+currentPickerWindowId);
	parentPickerInputId = currentPickerInputId;
	parentPickerWindowId = currentPickerWindowId;
});

$('#${instanceId!}').on('hidden.bs.modal', function (e) {
	$('#${instanceId!}-reset-btn').trigger("click");
});
			
});
</script>
</#macro>
<#macro agmtTemplatePicker instanceId fromAction="">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Find Agreement Template</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" id="rebate-find-AgmtTemplates" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            			<input type="hidden" name="agreementId" value="_TEMP"/>
	          <input type="hidden" name="agreementTypeId" value="AGMT_PRGM_TEMPLATE"/>
	        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<div class="row">
							
					</div>
				</form>
			</div>
		   <@AgGrid
				gridheadertitle="List of Agreement Templates"
				gridheaderid="${instanceId!}-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="agretpl-refresh-pref-btn"
				savePrefBtnId="agretpl-save-pref-btn"
				clearFilterBtnId="agretpl-clear-filter-btn"
				exportBtnId="agretpl-export-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="AGREEMENT_PICKER_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />       
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-agmt-template-picker.js"></script>
			
      	</div>
    	</div>
  	</div>
</div>

<script>
function setAgmtTemplateValue(templateId){
	console.log();
	$('#selagreementTempId').val(templateId);
	$('#agmtForm').submit();
}
</script>

</#macro>

<#macro prgmTemplatePicker instanceId fromAction="">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Find Programs Template</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" id="rebate-find-progams" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            			<input type="hidden" name="agreementId" value="_TEMP"/>
	          <input type="hidden" name="agreementTypeId" value="PROGRAM_TEMPLATES"/>
	        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<div class="row">
							<#-- <div class="col-md-4 col-md-4 form-horizontal">
							
								<@inputRow 
								id="${instanceId!}_partyId"
								name="partyId"
								placeholder=uiLabelMap.partyId
								inputColSize="col-sm-12"
								required=false
								/> 	
								
								<@inputRow 
								id="${instanceId!}_emailAddress"
								name="email"
								placeholder=uiLabelMap.email
								inputColSize="col-sm-12"
								required=false
								/>
								
						  </div>
						  <div class="col-md-4 col-md-4 form-horizontal text-right">
		                      <div class="search-btn">
	                           <@button 
				            		label="${uiLabelMap.Find}"
				            		id="partyPicker-search-btn"
				            	/>
				           	 </div>	
				        		
                    	 </div> -->
					</div>
				</form>
			</div>
		   <@AgGrid
				gridheadertitle="List of Programs"
				gridheaderid="${instanceId!}-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="progtpl-refresh-pref-btn"
				savePrefBtnId="progtpl-save-pref-btn"
				clearFilterBtnId="progtpl-clear-filter-btn"
				exportBtnId="progtpl-export-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="REBATE_PROGRAM_PICKER" 
			    autosizeallcol="true"
			    debug="false"
			    />       
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-prgm-template-picker.js"></script>
			
      	</div>
    	</div>
  	</div>
</div>

<script>
</script>

</#macro>

<#macro programTemplatePicker instanceId fromAction="">
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
   <div class="modal-dialog modal-lg" style="max-width: 1200px;">
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Find Program Templates</h4>
            <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
         </div>
         <div class="modal-body" style="padding-bottom: 8px;">
            <div class="popup-bot">
               <form method="post" id="agr-find-progams" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
                  <input type="hidden" name="agreementId" value="_TEMP"/>
                  <input type="hidden" name="agreementTypeId" value="PROGRAM_TEMPLATES"/>
                  <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                  <div class="row">
                  </div>
               </form>
            </div>
            <@AgGrid
			gridheadertitle="List of Program Templates"
			gridheaderid="agr-prog-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			refreshPrefBtnId="agr-prog-refresh-pref-btn"
		    savePrefBtnId="agr-prog-save-pref-btn"
		    clearFilterBtnId="agr-prog-clear-filter-btn"
		    exportBtnId="agr-prog-export-btn"
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="AGR_PROG_PICKER" 
		    autosizeallcol="true"
		    debug="false"
		    statusBar=true
		    serversidepaginate=false
		    serversidepaginate=false
			statusBar=true
		    /> 
                  
            <script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-agr-prgm-template-picker.js"></script>
         </div>
      </div>
   </div>
</div>
<script></script>
</#macro>

<#macro approvalTemplatePicker instanceId fromAction="" isShowRoleFilter="Y" roleTypeFilter="" isShowPartyLevelFilter="">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Find Template</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" name="find-apvtpl-form" id="find-apvtpl-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<div class="row">
        				<div class="col-lg-12 col-md-12 col-sm-12">
							<@dynaScreen 
								instanceId="FIND_APVTPL_PICKER"
								modeOfAction="CREATE"
								/>
						</div>
						</div>
							<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
						     	<@button
						        id="${instanceId!}-search-btn"
						        label="${uiLabelMap.Find}"
						        />	
						     	<@reset
						     	id="${instanceId!}-reset-btn"
								label="${uiLabelMap.Reset}"/>
				            </div>
						            			
					</form>
				</div>
			
			<@AgGrid
			gridheadertitle="Approval Templates"
			gridheaderid="apvtpl-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			refreshPrefBtnId="apvtpl-refresh-pref-btn"
		    savePrefBtnId="apvtpl-save-pref-btn"
		    clearFilterBtnId="apvtpl-clear-filter-btn"
		    exportBtnId="apvtpl-export-btn"
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="APV_TPL_PICKER" 
		    autosizeallcol="true"
		    debug="false"
		    statusBar=true
		    serversidepaginate=false
		    serversidepaginate=false
			statusBar=true
		    /> 
			      
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-apv-tpl.js"></script>
			
      	</div>
    	</div>
  	</div>
</div>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#${instanceId!}-search-btn").trigger('click');
});

});
</script>

</#macro>

<#macro paymentPicker instanceId fromAction="">
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Find Payment</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" id="findPaymentForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            			<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
            			<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
							
								<@inputRow 
								id="${instanceId!}_paymentId"
								name="paymentId"
								placeholder="Payment Id"
								inputColSize="col-sm-12"
								required=false
								/> 	
								
								
							
						  </div>
						 
						  <div class="col-md-4 col-md-4 form-horizontal">
						  <@inputRow 
								id="${instanceId!}_partyId"
								name="partyId"
								placeholder=" Invoice Party Id"
								inputColSize="col-sm-12"
								required=false
								/>
								
						      

						  </div>
						  
						  <div class="col-md-4 col-md-4 form-horizontal text-right">
						      <@inputRow 
								id="${instanceId!}_amount"
								name="amount"
								placeholder="Amount Applied"
								inputColSize="col-sm-12"
								required=false
								/> 	
						     	
		                      	
		                      <div class="search-btn">
	                           <@button 
				            		label="${uiLabelMap.Find}"
				            		id="paymentPicker-search-btn"
				            	/>
				           	 </div>	
				        		
                    	 </div>
					</div>
				</form>
			</div>
			
			<@AgGrid
				gridheadertitle="List Of Payments"
				gridheaderid="${instanceId!}_payment-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="paymentPicker-refresh-pref-btn"
				savePrefBtnId="paymentPicker-save-pref-btn"
				clearFilterBtnId="paymentPicker-clear-filter-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="PICKER_PAYMENT_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-payment.js"></script>
			
      	</div>
    	</div>
  	</div>
</div>

<script>

</script>

</#macro>

<#macro responsiblePickerAccount instanceId isExecutePartyAssoc="Y">
	<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	    <div class="modal-dialog modal-lg" style="width: 750px; max-width: 1200px;">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h4 class="modal-title">Find User</h4>
	                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
	            </div>
	            <div class="modal-body" style="padding-bottom: 8px;">
	            	<div class="card-header">
	                    <form  method="post" id="searchTeamMembersForm" name="searchTeamMembersForm">
	                        <@inputHidden 
	                        	id="externalLoginKey"
	                        	value="${requestAttributes.externalLoginKey!}"
	                        	/>
	                        <div class="row">
	                        	
	                            <div class="col-md-3 col-sm-3">
	                                <@inputCell    
		                                id="firstName"
		                                placeholder = "First Name"
		                                />
	                            </div>
	                            <div class="col-md-3 col-sm-3">
	                                <@inputCell    
		                                id="lastName"
		                                placeholder = "Last Name"
		                                />
	                            </div>
	                           
	                            <div class="col-md-2 col-sm-2">
	                                <@button
		                                id="team-mem-search-btn"
		                                label="${uiLabelMap.Find}"
		                                />
	                            </div>
	                        </div>
	                    </form>
	                </div>
	                <#assign rightContent='<button title="Refresh" id="resPickerAccount-refresh-btn" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-refresh" aria-hidden="true"></i>  </button>'/>
			
	               <#-- <@AgGrid
		                gridheadertitle="List of Team Members"
		                gridheaderid="${instanceId!}_user-list-container"
		                savePrefBtn=true
		                clearFilterBtn=true
		                exportBtn=false
		                insertBtn=false
		                updateBtn=false
		                removeBtn=false
		                headerextra=rightContent!
		                refreshPrefBtnId="resPickerAccount-refresh-pref-btn"
						savePrefBtnId="resPickerAccount-save-pref-btn"
						clearFilterBtnId="resPickerAccount-clear-filter-btn"
						subFltrClearId="resPickerSub-filter-clear-btn"
		                userid="${userLogin.userLoginId}" 
		                shownotifications="true" 
		                instanceid="PICKER_TM_LIST" 
		                autosizeallcol="true"
		                debug="false"
		                />    
	                <script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-team-members.js"></script>-->
	                	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
						<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	                <@fioGrid 
						id="user-list"
						instanceId="PICKER_TM_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/picker/find-team-members.js"
						headerLabel="List of Team Members"
						headerId="${instanceId!}_user-list-container"
						savePrefBtn=false
						clearFilterBtn=false
						exportBtn=false
						subFltrClearBtn=false
						headerBarClass="grid-header-no-bar"
						subFltrClearId="resPickerSub-filter-clear-btn"
						clearFilterBtnId="resPickerAccount-clear-filter-btn"
						savePrefBtnId="resPickerAccount-save-pref-btn"
						headerExtra=rightContent!
						/>
	            </div>
	        </div>
	    </div>
	</div>
	<#if isExecutePartyAssoc?has_content && isExecutePartyAssoc=="Y">
	<form name="personResponsibleParty" id="personResponsibleParty" method="POST" action="<@ofbizUrl>personResponsibleParty</@ofbizUrl>" style="display:none;">
	   <input type="hidden" name="partyId" value="${partyIdValue?if_exists}"/>
	   <input type="hidden" name="donePage" value="${requestURIName?if_exists}"/>
	   <input type="hidden" name="roleTypeIdFrom" value="${partyRoleTypeId?if_exists}"/>
	   <input type="hidden" name="accountPartyId" value=""/>
	   <input type="hidden" name="salesOpportunityId" value="${salesOpportunityIdValue?if_exists}"/>
	   <input type="hidden" name="srNumber" value="${srNumberValue?if_exists}"/>
	   <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	</form>
	</#if>
<script>

$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#resPickerAccount-refresh-btn").trigger('click');
});

}); 

function reassignParty(value, desc) {
	if (value != null && value != "") {
		$("#${instanceId!}").modal('hide');
		<#if isExecutePartyAssoc?has_content && isExecutePartyAssoc=="Y">
		$("#personResponsibleParty input[name=accountPartyId]").val(value);
		document.personResponsibleParty.submit();
		<#else>
			setPickerWindowValue(desc, value);
		</#if>
	}
}
</script>
</#macro>

<#macro workEffortAssignmentPicker instanceId fromAction="">

<div id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Find Users</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" name="findUserForm" id="findUserForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="roleTypeId" value="EMPLOYEE"/>
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
								
								<@inputRow 
								id="userId"
								placeholder="User Id"
								inputColSize="col-sm-12"
								required=false
								/>
								
								<@inputRow 
								id="firstName"
								placeholder=uiLabelMap.firstName
								inputColSize="col-sm-12"
								required=false
								/>
								
						  	</div>
						    
						  <div class="col-md-4 col-md-4 form-horizontal text-right">
						  	 <@inputRow 
								id="lastName"
								placeholder=uiLabelMap.lastName
								inputColSize="col-sm-12"
								required=false
								/>
						  	<div class="search-btn">
	                           <@button 
				            		label="${uiLabelMap.Find}"
				            		id="user-picker-search-btn"
				            	/>
				           		<@reset
				        			label="${uiLabelMap.Reset}"
				        		/>
				        	</div>	
                    	 </div>
					</div>
				</form>
			</div>
			
			<#-- <@AgGrid
				gridheadertitle="List of User"
				gridheaderid="${instanceId!}-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="userPicker-refresh-pref-btn"
				savePrefBtnId="userPicker-save-pref-btn"
				clearFilterBtnId="userPicker-clear-filter-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="USER_PICKER_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/users-picker.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="userPickerGrid"
						instanceId="USER_PICKER_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/picker/users-picker.js"
						headerLabel="List of User"
						headerId="${instanceId!}-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						exportBtn=false
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="userPicker-clear-pref-btn"
						subFltrClearId="userPicker-sub-filter-clear-btn"
						savePrefBtnId="userPicker-save-filter-btn"
						/>
      	</div>
    	</div>
  	</div>
</div>
<form method="post" id="wepaForm" name="wepaForm" action="<@ofbizUrl>workEffortPartAssignment</@ofbizUrl>" style="display:none;" >
	<@inputHidden id="workEffortId" />
	<@inputHidden id="partyId" />
	<@inputHidden id="existPartyId" />
</form>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#user-picker-search-btn").trigger('click');
});

	$('#${instanceId!}').on('shown.bs.modal', function (e) {
		var primaryId = $(e.relatedTarget).data('primary-id');
		var existPartyId = $(e.relatedTarget).data('exist-party-id');
		$('#workEffortId').val(primaryId);
		if(existPartyId != null && existPartyId != "" && existPartyId !="undefined")
			$('#existPartyId').val(existPartyId);
		
	});
});

function partyAssignment(partyId){
	$("#wepaForm #partyId").val(partyId);
	$("#${instanceId!}").modal('hide');
	$("#wepaForm").submit();
}
		
</script>
</#macro>

<#macro domainEntityPicker instanceId fromAction="" style="">

<div id="${instanceId!}" class="modal fade bd-example-modal-lg" style="${style!}" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Find List</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" name="findUserForm" id="findUserForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="domainEntityTypeId" value=""/>
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
								
								<@inputRow 
								id="searchText"
								placeholder="Search Text"
								inputColSize="col-sm-12"
								required=false
								/>
								
						  	</div>
						    
						  <div class="col-md-4 col-md-4 form-horizontal text-right">
						  	<div class="search-btn">
	                           <@button 
				            		label="${uiLabelMap.Find}"
				            		id="user-picker-search-btn"
				            	/>
				           		<@reset
				        			label="${uiLabelMap.Reset}"
				        		/>
				        	</div>	
                    	 </div>
					</div>
				</form>
			</div>
			
			<@AgGrid
				gridheadertitle=""
				gridheaderid="${instanceId!}-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="domainPicker-refresh-pref-btn"
				savePrefBtnId="domainPicker-save-pref-btn"
				clearFilterBtnId="domainPicker-clear-filter-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="DOMAIN_DATA_PICKER_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-domain-entity-list.js"></script>
      	</div>
    	</div>
  	</div>
</div>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#user-picker-search-btn").trigger('click');
	//alert("domainAssignForm------->"+$("#domainAssignForm #domainEntityType").val());
});

});

function partyAssignment(partyId){
	$("#wepaForm #partyId").val(partyId);
	$("#${instanceId!}").modal('hide');
	$("#wepaForm").submit();
}
		
</script>
</#macro>

<#macro promoCampaignPicker instanceId templateCategoryId="" fromAction="">
<div id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
<div class="modal-dialog modal-lg" style="max-width: 1200px;">
<!-- Modal content-->
<div class="modal-content">
<div class="modal-header">
	<h4 class="modal-title">Find Promo Campaign</h4>
	<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
</div>
<div class="modal-body">
<input type="hidden" name="promo_campaign_picker_instance" id="promo_campaign_picker_instance" value="${instanceId!}" />
<form method="post" id="${instanceId!'find'}_Form" name="${instanceId!'find'}_Form" action="" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
	<input type="hidden" name="templateCategories" value="${templateCategoryId!}"/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	<input type="hidden" id="productPromoId" name="productPromoId" value="${productPromoId!}"/>
	<div class="row">
		<div class="col-lg-12 col-md-12 col-sm-12">
			<@dynaScreen
				instanceId="FIND_PROMO_CAMPAIGN"
				modeOfAction="CREATE"
				/>
		</div>
		<div class="col-lg-12 col-md-12 col-sm-12">
		<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
			<@button 
				id="find-promo-search-btn" 
				label="${uiLabelMap.Find}"
				/>
			<@reset
				label="${uiLabelMap.Reset}"
				id="promo-reset-btn" 
				/>
		</div>
		</div>
	</div>
</form>
<br>
<div class="clearfix"></div>
			<#-- <@AgGrid
				gridheadertitle="Find Promo Campaign"
				gridheaderid="pc-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=true
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="pc-refresh-pref-btn"
				savePrefBtnId="pc-save-pref-btn"
				clearFilterBtnId="pc-clear-filter-btn"
				exportBtnId="pc-export-btn"
				userid="${userLogin.userLoginId}"
				shownotifications="true"
				instanceid="LIST_PROMO_CAMPAIGN"
				autosizeallcol="true"
				debug="false"
				statusBar=true
				serversidepaginate=false
				serversidepaginate=false
				statusBar=true
				/>-->
	  <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	  <input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

        					<@fioGrid 
								id="promoCampaignGrid"
								instanceId="LIST_PROMO_CAMPAIGN"
								jsLoc="/common-portal-resource/js/ag-grid/promotion/find-promo-campaign.js"
								headerLabel="Find Promo Campaign"
								headerId="pc-grid-action"
								savePrefBtnId="pc-save-pref-btn"
								clearFilterBtnId="pc-clear-filter-btn"
								headerBarClass="grid-header-no-bar"
								savePrefBtn=false
								clearFilterBtn=false
								exportBtn=true
								subFltrClearBtn=false
								subFltrClearId="pc-sub-filter-clear-btn"
								exportBtnId="pc-export-btn"
								/>
<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#find-promo-search-btn").trigger('click');
});

});

$("#${instanceId!'find'}_Form").on("keypress", function(event) {
	var keyPressed = event.keyCode || event.which;
	if (keyPressed === 13) {
		$('#promo_campaign_picker_instance').val('${instanceId!}');
		$('#find_promo_camp_trigger').click();
		event.preventDefault();
		return false;
	}
});
$("#${instanceId!'find'}_Form #find-promo-search-btn").click(function() {
	$('#promo_campaign_picker_instance').val('${instanceId!}');
	$('#find_promo_camp_trigger').click();
});
$('#${instanceId!}').on('shown.bs.modal', function(e) {
	$('#promo_campaign_picker_instance').val('${instanceId!}');
	$('#find_promo_camp_trigger').click();
});

$('#${instanceId!}').on('hidden.bs.modal', function(e) {
	$("#promo-reset-btn").trigger("click");
});
</script>
<#-- <script type = "text/javascript" src = "/common-portal-resource/js/ag-grid/promotion/find-promo-campaign.js" ></script>-->
<div class="clearfix"></div>
<span id="find_promo_camp_trigger" ></span>
</div>
</div>
</div>
</div>
</#macro>

<#macro opportunityPicker instanceId templateCategoryId="" fromAction="" pickerWindow="Y">
<div id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
<div class="modal-dialog modal-lg" style="max-width: 1200px;">
<!-- Modal content-->
<div class="modal-content">
<div class="modal-header">
	<h4 class="modal-title">Associated Opportunity</h4>
	<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
</div>
<div class="modal-body">
<div class="popup-bot">
<input type="hidden" name="pickerWindow" id="opportunityPicker_pickerWindow" value="${pickerWindow!}"/>
<form method="post" id="${instanceId!'find'}_Form" name="${instanceId!'find'}_Form" action="" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	<input type="hidden" name="parentOpportunityTypeId" id="${instanceId!}_parentOpportunityTypeId" value="${salesOpportunityTypeId!}"/>
	<input type="hidden" name="notIncludeOpportunityId" id="notIncludeOpportunityId" value="${inputContext.salesOpportunityId!}"/>
	<input type="hidden" name="oppoPartyId" id="opportunityPicker_partyId" value="${inputContext.partyId!}"/>
	<div class="row">
    <div class="col-lg-12 col-md-12 col-sm-12">
        <@dynaScreen instanceId="FIND_OPPO_PICKER" modeOfAction="CREATE" />
    </div>
	</div>
	<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
	    <@button id="find-oppo-search-btn" label="${uiLabelMap.Find}" />
	    <@reset id="${instanceId!}-reset-btn" label="${uiLabelMap.Reset}" />
	</div>
</form>
</div>
<#assign rightContent='<button id="oppo-assoc-assoc-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-plus" aria-hidden="true"></i> Associate</button>
		' />
<div class="clearfix"></div>
			<#-- <@AgGrid
				gridheadertitle="Opportunities List"
				gridheaderid="pc-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				headerextra=rightContent!
				refreshPrefBtnId="oppo-refresh-pref-btn"
				savePrefBtnId="oppo-save-pref-btn"
				clearFilterBtnId="oppo-clear-filter-btn"
				exportBtnId="oppo-export-btn"
				userid="${userLogin.userLoginId}"
				shownotifications="true"
				instanceid="OPPORTUNITY_PICKER"
				autosizeallcol="true"
				debug="false"
				serversidepaginate=false
				serversidepaginate=false
				statusBar=true
				/>
<script type = "text/javascript" src = "/common-portal-resource/js/ag-grid/opportunity/find-opportunity-picker.js" ></script>-->
		<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
		<input type="hidden" id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

							<@fioGrid 
								id="oppo"
								instanceId="OPPORTUNITY_PICKER"
								jsLoc="/common-portal-resource/js/ag-grid/opportunity/find-opportunity-picker.js"
								headerLabel="Opportunities List"
								headerId="pc-grid-action-container"
								savePrefBtnId="oppo-list-save-pref-btn"
								clearFilterBtnId="oppo-list-clear-filter-btn"
								exportBtnId="oppo-list-export-btn"
								headerBarClass="grid-header-no-bar"
								subFltrClearId="oppo-list-clear-sub-ftr"
								headerExtra=rightContent!
								savePrefBtn=false
								subFltrClearBtn = false
								clearFilterBtn=false
								exportBtn=false
								/>
<div class="clearfix"></div>
</div>
</div>
</div>
</div>
<script>
$(document).ready(function() {
	$("#${instanceId!}-reset-btn").click();
	var salesOpportunityTypeId = $("#salesOpportunityTypeId").val();
	if (salesOpportunityTypeId) {
		getOpportunities(salesOpportunityTypeId);
	}
	$("#salesOpportunityTypeId").change(function(){
		let value = $(this).val();
		getOpportunities(value);
	});
	$("#opportunityTypeId").change(function(){
		let oppType = $("#salesOpportunityTypeId").val();
		let val = $(this).val();
		if (val && val =="BASE" && oppType && oppType =="BASE"){
			$("#opportunityTypeId").val('');
			$("#opportunityTypeId").dropdown('refresh');
			$("#opportunityTypeId").trigger('change');
			showAlert("error","Cannot associate opportunity of same Type");
			return false;
		}
	});
	
	$("#${instanceId!}-reset-btn").click(function(){
		$("#${instanceId!'find'}_Form")[0].reset();
	});
	$("#partyId_desc").change(function(){
		console.log($(this).val());
		console.log($("#partyId_val").val());
		if ($(this).val()){
			$("#opportunityPicker_partyId").val($("#partyId_val").val());
		}
	});
	$(".picker-window[data-pickerwindow=opportunityPicker]").click(function(){
		$("#find-oppo-search-btn").trigger("click");
	});
});
  function getOpportunities(value){
  $("#opportunityTypeId").val('');
  $("#opportunityTypeId").dropdown('refresh');
	if (value) {
		$("#${instanceId!}_parentOpportunityTypeId").val(value);
	}
	if (!value) {
		$("#opportunityTypeId").val('');
		$("#${instanceId!}_parentOpportunityTypeId").val('');
	}
	$("#opportunityTypeId").dropdown('refresh');
	//$("#find-oppo-search-btn").trigger("click");
}
 
</script>
</#macro>

<#macro parentBUPicker instanceId fromAction="">
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content" id="mainFrom">
			<div class="modal-header">
				<h4 class="modal-title">${uiLabelMap.findParentBU!}</h4>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div id="scroll-1">
				<div class="modal-body">
					<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
						<@inputHidden name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey}"/>
						<div class="panel-body">
							<@dynaScreen
								instanceId="FIND_PARENT_BU"
								modeOfAction="CREATE"
								/>
							<div class="row find-srbottom">
								<div class="col-lg-12 col-md-12 col-sm-12">
									<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
										<@button
											id="parentBUPicker-search-btn"
											label="${uiLabelMap.Find}"
											/>
										<@reset
											label="${uiLabelMap.Reset}"
											/>
									</div>
								</div>
							</div>
						</div>
					</form>
					<@AgGrid
						gridheadertitle=uiLabelMap.ParentBUList
						gridheaderid="${instanceId!}-grid-action-container"
						savePrefBtn=true
						clearFilterBtn=true
						exportBtn=true
						insertBtn=false
						updateBtn=false
						removeBtn=false
						userid="${userLogin.userLoginId}"
						removeBtnId=""
						refreshPrefBtnId="parentBUPicker-refresh-pref-btn"
						savePrefBtnId="parentBUPicker-save-pref-btn"
						clearFilterBtnId="parentBUPicker-clear-filter-btn"
						subFltrClearId="parentBUPicker-sub-filter-clear-btn"
						exportBtnId="parentBUPicker-export-btn"
						shownotifications="true"
						instanceid="PARENTBU_LIST"
						autosizeallcol="true"
						debug="false"
						/>
					<script type = "text/javascript" src = "/common-portal-resource/js/ag-grid/picker/find-parentbu.js" ></script>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#parentBUPicker-search-btn").trigger('click');
});

});
</script>
</#macro>

<#macro processFlowPicker instanceId fromAction="">
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content" id="mainFrom">
			<div class="modal-header">
				<h4 class="modal-title">${uiLabelMap.FindProcessFlow!}</h4>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div id="scroll-1">
				<div class="modal-body">
					<form method="post" id="find-process-flow" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
						<@inputHidden name="externalLoginKey" value="${requestAttributes.externalLoginKey}"/>
						<div class="panel-body">
							<@dynaScreen
									instanceId="PROCESS_FLOW_FIND"
									modeOfAction="CREATE"
									/>
							<div class="row find-srbottom">
								<div class="col-lg-12 col-md-12 col-sm-12">
									<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
										<@button
											id="processFlow-refresh-btn"
											label="${uiLabelMap.Find}"
											/>
										<@reset
											label="${uiLabelMap.Reset}"/>
									</div>
								</div>
							</div>
						</div>
					</form>
					<#-- <@AgGrid
						gridheadertitle=uiLabelMap.ListProcessFlow
						gridheaderid="process-flow-grid-action-container"
						savePrefBtn=true
						clearFilterBtn=true
						exportBtn=true
						insertBtn=false
						updateBtn=false
						removeBtn=false
						userid="${userLogin.userLoginId}"
						shownotifications="true"
						instanceid="PROCESS_FLOW_LIST"
						autosizeallcol="true"
						debug="false"
						serversidepaginate=true
						statusBar=true
						/>
					<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-process-flow.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="ListProcessFlowGrid"
						instanceId="PROCESS_FLOW_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/picker/find-process-flow.js"
						headerLabel=uiLabelMap.ListProcessFlow
						headerId="process-flow-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						headerBarClass="grid-header-no-bar"
						subFltrClearId="process-flow-sub-filter-clear-btn"
						savePrefBtnId="process-flow-save-pref-btn"
                   		clearFilterBtnId="process-flow-clear-filter-btn"
                    	exportBtnId="process-flow-export-btn"
						exportBtn=true
						serversidepaginate=true
						statusBar=true
						/>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#processFlow-refresh-btn").trigger('click');
});

});
</script>
</#macro>

<#macro findOrderDealerPicker instanceId fromAction="">

<div id="${instanceId!}" class="modal fade bd-example-modal-lg popup2" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.findDealer!}</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" id="findOrderDealerForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            			<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
            			<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
            			<input type="hidden" name="generalCountryGeoId1" id="generalCountryGeoId1" value="USA" />
            			<input type="hidden" name="roleTypeId" value="ACCOUNT">
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
								
								<@inputRow 
								id="${instanceId!}_partyName"
								name="name"
								placeholder=uiLabelMap.Name
								inputColSize="col-sm-12"
								required=false
								/> 	
								
								<@dropdownCell 
									id="${instanceId!}_generalState"
									name="generalState"
									placeholder="State"
									inputColSize="col-sm-12"
									required=false
									allowEmpty=true
									/>	
									
						  </div>
						 
						  <div class="col-md-4 col-md-4 form-horizontal">
						  		
								<@inputRow 
								id="${instanceId!}_phone"
								name="phone"
								placeholder=uiLabelMap.Phone
								inputColSize="col-sm-12"
								required=false
								/> 
								
								<@dropdownCell 
								id="${instanceId!}_generalCity"
								name="generalCity"
								placeholder="City"
								inputColSize="col-sm-12"
								required=false
								allowEmpty=true
								/>
								
						  </div>
						  
						  <div class="col-md-4 col-md-4 form-horizontal text-right">
						     	<@inputRow 
								id="${instanceId!}_emailAddress"
								name="email"
								placeholder=uiLabelMap.email
								inputColSize="col-sm-12"
								required=false
								/>
								
		                      <div class="search-btn">
	                           <@button 
				            		label="${uiLabelMap.Find}"
				            		id="findOrderDealerPicker-search-btn"
				            		/>
				            	<@reset
					        		label="${uiLabelMap.Reset}"
					        		/>
				           	 </div>	
				        		
                    	 </div>
					</div>
				</form>
			</div>
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
			<@fioGrid 
			id="ListOfDealers"
			instanceId="DEALER_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/picker/find-order-dealer.js"
			headerLabel=uiLabelMap.ListOfDealers
			headerId="findOrderDealerPicker_party-grid-action-container"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=false
			subFltrClearBtn = false
			headerBarClass="grid-header-no-bar"
			subFltrClearId="findOrderDealerPicker-sub-filter-clear-btn"
			clearFilterBtnId="findOrderDealerPicker-clear-filter-btn"	
			savePrefBtnId="findOrderDealerPicker-save-pref-btn"
			subFltrClearId="findOrderDealerPicker-sub-filter-clear-btn"
			/>
      	</div>
    	</div>
  	</div>
</div>
<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	loadCity();
	$("#findOrderDealerPicker-search-btn").trigger('click');
});	
			
var countryGeoId = $('#findOrderDealerForm #generalCountryGeoId1').val();
if ( $('#findOrderDealerForm #generalCountryGeoId1').val()) {
    getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId1', '${instanceId!}_generalState', 'stateList', 'geoId', 'geoName', '${stateValue!}','',true);
}

$("#${instanceId!}_generalState").change(function() {
    loadCity();
});

});

function loadCity() {
    var cityIdCode = "";
    
    var cityOptions = '<option value="" selected="">Select City</option>';
    let cityList = new Map();
    var state = $("#findOrderDealerForm #${instanceId!}_generalState").val();
    
    $.ajax({
        type: "POST",
        url: "/uiadv-portal/control/searchZipCodeAssocs",
        data: {
            "state": state,
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function(result) {
            if (result.code == 200) {
                for (var i = 0; i < result.data.length; i++) {
                    var data = result.data[i];
                    cityList.set(data.city, data.city);
                }
            }
        }
    });

    for (let key of cityList.keys()) {
        if (cityIdCode && cityIdCode == key || (cityList.size === 1)) {
            cityOptions += '<option value="' + key + '" selected>' + cityList.get(key) + '</option>';
        } else {
            cityOptions += '<option value="' + key + '">' + cityList.get(key) + '</option>';
        }
    }
    
    $("#${instanceId!}_generalCity").html(cityOptions).change();
    $("#${instanceId!}_generalCity").dropdown('refresh');
}

</script>

</#macro>
<#macro findTemplatePicker instanceId templateCategoryId="" fromAction="">
<div id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="max-width: 1200px;">
        <!-- Modal content-->
        <div class="modal-content" id="searchTemplate">
            <div class="modal-header">
                <@headerH4 title="${uiLabelMap.findTemplates!}" class="modal-title">${uiLabelMap.findTemplates!}</@headerH4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>
            <div class="modal-body">
            	<input type="hidden" name="find_temp_picker_instance" id="find_temp_picker_instance" value="${instanceId!}" />
            	<form method="post" id="${instanceId!'find'}_Form" name="${instanceId!'find'}_Form" action="" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            	<input type="hidden" name="templateCategories" value="${templateCategoryId!}"/>
            	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                <div class="row">
                    <div class="col-md-4 col-sm-4">
                        <div class="form-group row mr">
                            <@inputCell 
	                            id="tempalateName"
	                            inputColSize="col-sm-12"
	                            value=tempName!
	                            placeholder="Templates Name"
	                            required=false
	                            />
                        </div>
                    </div>
                    <div class="col-md-3 col-sm-2">
                        <div class="form-group">
                            <@dropdownCell 
	                            id = "emailEngine"
	                            options = emailEngineTypeList!
	                            value=""
	                            placeholder="Email Engine"
	                            dataLiveSearch = true
	                             required=false
	                             allowEmpty=true
	                            />
                        </div>
                        
                    </div>
                    <div class="col-md-3 col-sm-3">
                    	<@button 
		            		id="find-temp-list-search-btn" 
                        	label="${uiLabelMap.Find}"
		            		/>
		           		<@reset
		        			label="${uiLabelMap.Reset}"
		        			id="temp-reset-btn" 
		        			/>
                    </div>
                </div>
                </form>
                <br>
                <div class="clearfix"></div>
				<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
				<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
			
			<#assign instId="${instanceId!}">
			<#if instId=="findTemplatePicker">
					<@fioGrid 
						id="find-template-grid"
						instanceId="FIND_TEMPLATE_PICKER_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/picker/find-template-list-picker.js"
						headerLabel="List of Templates"
						headerId="${instanceId!}_campaign-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						exportBtn=false
						subFltrClearBtn = false
						headerBarClass="grid-header-no-bar"
						savePrefBtnId ="find-template-save-pref-btn"
						clearFilterBtnId ="find-template-clear-filter-btn"
						subFltrClearId="find-template-sub-filter-clear-btn"
						/>
				<#else>
						<@fioGrid 
						id="find-template-grid"
						instanceId="FIND_TEMPLATE_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/picker/find-template-list.js"
						headerLabel="List of Templates"
						headerId="${instanceId!}_campaign-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						exportBtn=false
						subFltrClearBtn = false
						headerBarClass="grid-header-no-bar"
						savePrefBtnId ="find-template-save-pref-btn"
						clearFilterBtnId ="find-template-clear-filter-btn"
						subFltrClearId="find-template-sub-filter-clear-btn"
						/>
			</#if>
				 <script>
				 
				 	$('#${instanceId!}').on('shown.bs.modal', function (e) {
						$('#find_temp_picker_instance').val('${instanceId!}');
						$('#find-temp-list-search-btn').trigger('click');
					});
				 
				 </script>
                <div class="clearfix"></div>
                <span id="find_temp_list_trigger" ></span>
            </div>
        </div>
    </div>
</div>
</#macro>