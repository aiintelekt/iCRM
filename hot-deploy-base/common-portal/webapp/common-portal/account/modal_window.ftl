<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign partyId= request.getParameter("partyId")! />
<#if partyId?exists && partyId?has_content>
<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
</#if>

<#macro addPartyAccount instanceId fromAction="">

<#-- Assign Account Model---> 
<!-- Find parent account pop for create and update account-->
<div id="${instanceId!}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.findAccounts!}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="card-header">
               <#-- <form method="post" action="#" id="accountList" class="form-horizontal" name="accountList" novalidate="novalidate" data-toggle="validator"> -->
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
                     <@button id="findAssocAccount" label="Find"/>
                     
                  </div>
               <#-- </form> -->
               <div class="clearfix"> </div>
            </div>
            <div class="clearfix"> </div>
            <div class="page-header">
               <h2 class="float-left">Accounts List </h2>
            </div>
            <div class="table-responsive">
               <table id="ajaxAccountdatatable" class="table table-striped">
                  <thead>
                     <tr>
                        <th>Account Name</th>
                        <th>Status</th>
                        <th>City</th>
                        <th>State</th>
                        <th>Phone Number</th>
                        <th>E-Mail Address</th>
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
<form method="post" action="assignAccount" id="assignAccount" name="assignAccount" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
   <input type="hidden" name="contactPartyId" value="${partySummary?if_exists.partyId?if_exists}">
   <input type="hidden" name="partyId" value="${partySummary?if_exists.partyId?if_exists}">
   <input type="hidden" name="party" value="ACCOUNT">
   <input type="hidden" name="activeTab" value="account" />
   <input type="hidden" name="donePage" value="viewContact">
   <input type="hidden" name="accountPartyId" id="accountPartyIdModal" value="" />
</form>
<script>
   $('#${instanceId!}').on('click', '.parentSet', function() {
       var value = $(this).children("span").attr("value");
       if (value != null && value != "") {
           $('#accountPartyIdModal').val(value);
           $('#${instanceId!}').modal('hide');
           document.assignAccount.submit();
       }
   });

   $('#findAccount').click(function() {
       $('#parentPartyId').val("");
       $("#accountSearchPartyId").val("");
       $("#searchGroupName").val("");
       $("#searchEmailId").val("");
       $("#searchPhoneNum").val("");
       findAccountsModal();
   });
   $(document).ready(function() {
       //findAccountsModal();
   });
   $('#findAssocAccount').on('click',function(){
     	findAccountsModal();
   });
   
   function findAccountsModal() {
   		
   		console.log('findAccountsModal...........');
   		
   		var accountSearchPartyId = $("#accountSearchPartyId").val();
       	var searchGroupName = $("#searchGroupName").val();
       	var searchEmailId = $("#searchEmailId").val();
       	var searchPhoneNum = $("#searchPhoneNum").val();
				   
   		$("#ajaxAccountdatatable").DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "/common-portal/control/searchAccounts",
               "type": "POST",
               data: {
                   "accountSearchPartyId": accountSearchPartyId,
                   "searchGroupName": searchGroupName,
                   "searchEmailId": searchEmailId,
                   "searchPhoneNum": searchPhoneNum,
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "order": [],
           "columns": [{
                   "data": "partyId",
                   "render": function(data, type, row, meta) {
                       if (type === 'display') {
                           data = '<a href="#" class="parentSet"><span id="parAccId_' + row.id + '" name="parAccId_' + row.id + '" value ="' + data + '"></span>' + row.groupName + '(' + data + ')</a>';
                       }
                       return data;
                   }
               },
               {
                   "data": "statusId"
               },
               {
                   "data": "city"
               },
               {
                   "data": "state"
               },
               {
                   "data": "phoneNumber"
               },
               {
                   "data": "infoString"
               }
           ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
   }

$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	
	findAccountsModal()
	
});

});      
   
</script>

</#macro>

<#macro addParty instanceId fromAction="">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.findParty!}</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="card-header popup-bot">
            		<form method="post" id="findPartyForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
							
								<@inputRow 
								id="${instanceId!}_partyId"
								name="partyId"
								placeholder=uiLabelMap.partyId
								inputColSize="col-sm-12"
								required=false
								/> 	
								
								<@inputRow 
								id="${instanceId!}_partyName"
								name="name"
								placeholder=uiLabelMap.Name
								inputColSize="col-sm-12"
								required=false
								/> 	
							
								<input type="radio"  name="roleTypeId" value="ACCOUNT" checked /> Account
		                      	<input type="radio"  name="roleTypeId" value="LEAD"/> Lead
		                      	<input type="radio"  name="roleTypeId" value="ALL"/> All
						  </div>
						 
						  <div class="col-md-4 col-md-4 form-horizontal">
						  
						  		<@inputRow 
								id="${instanceId!}_localName"
								name="localName"
								placeholder=uiLabelMap.localName
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
						      <@inputRow 
								id="${instanceId!}_phone"
								name="phone"
								placeholder=uiLabelMap.Phone
								inputColSize="col-sm-12"
								required=false
								/> 	
                              
	                           <@button 
				            		label="${uiLabelMap.Find}"
				            		id="relatedParty-search-btn"
				            	/>
				           		<@reset
				        			label="${uiLabelMap.Reset}"
				        		/>
				        		
                    	 </div>
					</div>
				</form>
			</div>
			<#assign rightContent='<button title="Refresh" id="relatedParty-refresh-btn" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-refresh" aria-hidden="true"></i>  </button>'/>
			
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
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="ADD_RELATED_PARTY_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-related-party.js"></script>-->
			<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
			<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfPartys-grid"
			instanceId="ADD_RELATED_PARTY_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/picker/find-related-party.js"
			headerLabel=uiLabelMap.ListOfPartys
			headerId="${instanceId!}_party-grid-action-container"
			savePrefBtn=false
			subFltrClearBtn=false
			clearFilterBtn=false
			exportBtn=false
			savePrefBtnId="related-party-save-pref-btn"
			clearFilterBtnId="related-party-clear-filter-btn"
			subFltrClearId="related-party-sub-filter-btn"
			headerBarClass="grid-header-no-bar"
			headerExtra=rightContent!
			/>
      	</div>
	      	<div class="modal-footer" style="border-top: 0px;padding-top: 0px;">
	             
	        </div>
    	</div>
  	</div>
</div>

<form method="post" id="assignPartyForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
   <input type="hidden" name="contactPartyId" value="${partySummary?if_exists.partyId?if_exists}">
   <input type="hidden" name="partyId" value="${partySummary?if_exists.partyId?if_exists}">
   <input type="hidden" name="party" value="">
   <input type="hidden" name="activeTab" value="accounts" />
   <input type="hidden" name="donePage" value="viewContact">
   <input type="hidden" name="accountPartyId" value="" />
   <input type="hidden" name="leadPartyId" value="" />
</form>

<script>

$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#relatedParty-refresh-btn").trigger('click');
});

}); 

function addRelatedParty(name, value, roleTypeId) {
   	if (value != null && value != "") {
  		$('#assignPartyForm input[name=party]').val(roleTypeId);
  		
  		if (roleTypeId == "ACCOUNT") {
  			$('#assignPartyForm input[name=accountPartyId]').val(value);
  			$("#assignPartyForm").attr("action", "assignAccount");
  		} else if (roleTypeId == "LEAD") {
  			$('#assignPartyForm input[name=leadPartyId]').val(value);
  			$("#assignPartyForm").attr("action", "assignLead");
  		}
  		
     	$('#${instanceId!}').modal('hide');
     	$("#assignPartyForm").submit();
 	}
}

</script>

</#macro>

<#macro childAccountPicker instanceId>
<#-- Child Account Model---> 
<div id="${instanceId!}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Child Accounts</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="">
                <form method="post" action="#" id="child-accounts-form" class="form-horizontal" name="child-accounts-form" novalidate="novalidate" data-toggle="validator"> 
                  <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                  <input type="hidden" name="partyId" id="parentAccountId" value="${inputContext?if_exists.partyId!}"/>
                  
                  <input type="hidden" name="childAccountId_desc" id="childAccountId_desc" value=""/>
                  <input type="hidden" name="childAccountId_val" id="childAccountId_val" value=""/>
               </form>
            </div>
            <div class="clearfix"> </div>
            <div class="">
            <#assign rightContent='<button title="Refresh" id="childAcc-refresh-btn" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-refresh" aria-hidden="true"></i>  </button>'/>
			<#assign rightContent=rightContent+'<button id="child-reassign" data-pickerWindow="accountPicker" data-pickerInputId="childAccountId" title="Add Child" class="btn btn-xs btn-primary picker-window"><i class="fa fa-plus" aria-hidden="true"></i> Add Child</button>'/>
						
			<#-- data-toggle="modal" data-target="#accountPicker" -->
			<#-- <@AgGrid
				gridheadertitle=""
				gridheaderid="childAccounts-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				headerextra=rightContent!
				refreshPrefBtnId="childAcc-refresh-pref-btn"
				savePrefBtnId="childAcc-save-pref-btn"
				clearFilterBtnId="childAcc-clear-filter-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="CHILD_ACCOUNTS" 
			    autosizeallcol="true"
			    debug="false"
			    />   
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/account/find-child-accounts.js"></script>--> 
		<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
		<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

						<@fioGrid 
								id="childAccountsGrid"
								instanceId="CHILD_ACCOUNTS"
								jsLoc="/common-portal-resource/js/ag-grid/account/find-child-accounts.js"
								headerLabel=""
								headerId="childAccountsGrid-action-container"
								savePrefBtnId="childAcc-save-pref-btn"
								clearFilterBtnId="childAcc-clear-filter-btn"
								headerBarClass="grid-header-no-bar"
								savePrefBtn=false
								clearFilterBtn=false
								exportBtn=true
								subFltrClearBtn=false
								headerExtra=rightContent!
								subFltrClearId="childAcc-sub-filter-clear-btn"
								exportBtnId="childAcc-export-btn"
								/>
            </div>
            
         </div>
         <div class="modal-footer">
            <button type="sbmit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
         </div>
      </div>
   </div>
</div>
<script>
  
$(document).ready(function() {

$("#childAccountId_desc").change(function(){
	if ($(this).val()){
		let value = $("#childAccountId_val").val();
		addChildAccount(value);
	}
});

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#childAcc-refresh-btn").trigger('click');
});

});  
</script>
</#macro>

</#if>