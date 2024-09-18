<#include "component://common-portal/webapp/common-portal/contact/modal_window.ftl"/>
<#-- <#assign partyId = parameters.partyId?if_exists />
<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif request.getRequestURI().contains("viewServiceRequest")>
<#assign requestURI = "viewServiceRequest"/>
</#if>-->
<#assign requestUri=request.getParameter("requestUri")!>
<#assign partyId= request.getParameter("partyId")! />
<#if partyId=="" && inputContext?has_content>
<#assign partyId= "${inputContext.partyId?if_exists}" />
</#if>
<#if requestUri?has_content>
<#assign requestURI = "viewContact"/>
<#if requestUri.contains("viewLead")!>
<#assign requestURI = "viewLead"/>
<#elseif requestUri.contains("viewAccount")!>
<#assign requestURI = "viewAccount"/>
<#elseif requestUri.contains("viewServiceRequest")!>
<#assign requestURI = "viewServiceRequest"/>
</#if>
</#if>
<div class="clearfix"> </div>

<div class="panel-group" id="PartyContacts" role="tablist" aria-multiselectable="true">
   <#if partyContactAssocList?exists && partyContactAssocList?has_content>
 
   <#assign i = 1/>
   <#list partyContactAssocList as partyContactAssoc>
	   <#assign targetPartyId = "${partyId!}"/>
		<#assign srStatusId= context.get("currentSrStatusId")?if_exists />
	   <#assign targetRoleTypeId = "${partyRoleTypeId!}"/>
	    <#if requestURI == "viewServiceRequest">
	    <#assign targetPartyId = "${srFromPartyId}"/>
	    
	    </#if>
	   <#if requestURI == "viewContact">
	   		<#assign targetPartyId = "${partyContactAssoc.partyId!}"/>
	   		<#assign targetRoleTypeId = "${partyContactAssoc.assocRoleTypeId!}"/>
	   	</#if>
	   
	   <div class="panel panel-default">
	      <div class="panel-heading" role="tab" id="heading_${i}" >
	     	<div class="float-right position-absolute"  style="right: 0; padding-right: 65px; padding-top: 10px;">
				<#if requestURI == "viewAccount" || requestURI == "viewLead" || requestURI == "viewServiceRequest">
	           <form name="updateDefaultContact_${i}" id="updateDefaultContact_${i}" method="post" action="updateDefaultContact">
	              <input type="hidden" name="activeTab" value="contacts" />
	              <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
	              <input type="hidden" name="partyIdFrom" value="${partyContactAssoc.contactId?if_exists}"/>
	              <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
	              <input type="hidden" name="roleTypeIdFrom" value="CONTACT"/>
	              <input type="hidden" name="roleTypeIdTo" value="${partyRoleTypeId!}"/>
	              <input type="hidden" name="srNumber" value="${srNumber?if_exists}"/>
	               <#if requestURI == "viewAccount" || requestURI == "viewLead" >
	
	              <label class="checkbox-inline" for="statusId_${i}">
	              	<input type="checkbox" class="checkbox" name="statusId_${i}" id="statusId_${i}" onchange="javascript:updateContactEmailType('updateDefaultContact_${i}','${i}');" <#if partyContactAssoc.statusId?exists && partyContactAssoc.statusId?has_content && partyContactAssoc.statusId=="PARTY_DEFAULT">checked</#if>/>Primary
	              	<input type="hidden" name="statusId" id="statusId"/>
	              </label> &nbsp;
	              <label class="checkbox-inline" for="isMarketable_${i}">
	              	<input type="checkbox" class="checkbox" disabled="disabled" name="isMarketable_${i}" id="isMarketable_${i}" onchange="javascript:updateContactEmailType('updateDefaultContact_${i}','${i}');" <#if partyContactAssoc.isMarketable?exists && partyContactAssoc.isMarketable?has_content && partyContactAssoc.isMarketable=="Y">checked</#if>/>Marketing
	              	<input type="hidden" name="isMarketable" id="isMarketable"/>
	              </label>
	              <#elseif requestURI == "viewServiceRequest" && partyContactAssoc.statusId?if_exists=="PARTY_DEFAULT">
	                <label class="checkbox-inline" for="statusId_${i}">
	              	<input type="checkbox" class="checkbox" name="statusId_${i}" id="statusId_${i}"  <#if partyContactAssoc.statusId?exists && partyContactAssoc.statusId?has_content && partyContactAssoc.statusId=="PARTY_DEFAULT">checked</#if> disabled="disabled"/>SR Primary
	              	<input type="hidden" name="statusId" id="statusId"/>
	              </label>
	              
	               </#if>
	              <#if partyStatusId?if_exists != "PARTY_DISABLED">
	               <#if requestURI == "viewAccount" || requestURI == "viewLead" >
	                <button id="remove-contact-btn_${i}" data-toggle="confirmation" title="Are you sure to REMOVE ?" class="btn btn-primary btn-xs ml-2 remooveContact">
	       			<i class="fa fa-times" aria-hidden="true"></i> Remove
					
	               <#elseif requestURI == "viewServiceRequest" && partyContactAssoc.statusId?if_exists !="PARTY_DEFAULT">
	             
	                <#if srStatusId?has_content && ("SR_CLOSED" == srStatusId || "SR_CANCELLED" == srStatusId)>
	                 
	               <#else>
	               
	                <button id="remove-contact-btn_${i}" data-toggle="confirmation" title="Are you sure to REMOVE ?" class="btn btn-primary btn-xs ml-2 remooveContactFromSr">
	       			<i class="fa fa-times" aria-hidden="true"></i> Remove
	       			
	       			</#if>
	       			</#if>
	       			</#if>
	
	           </form>
	        </#if>    
	        </div>
	         <h4 class="panel-title" id="related-subtitle">
	         	<#assign assocViewUrl = "#"/>
	         	<#if partyContactAssoc.assocRoleTypeId == "ACCOUNT"> 
	         		<#assign assocViewUrl = "/account-portal/control/viewAccount?partyId=${partyContactAssoc.partyId!}&externalLoginKey=${requestAttributes.externalLoginKey!}"/>
	         	<#elseif partyContactAssoc.assocRoleTypeId == "CONTACT"> 
	         		<#assign assocViewUrl = "/contact-portal/control/viewContact?partyId=${partyContactAssoc.contactId!}&externalLoginKey=${requestAttributes.externalLoginKey!}"/>
	         	<#elseif partyContactAssoc.assocRoleTypeId == "LEAD"> 
	         		<#assign assocViewUrl = "/lead-portal/control/viewLead?partyId=${partyContactAssoc.partyId!}&externalLoginKey=${requestAttributes.externalLoginKey!}"/>	
	         	</#if>
	            <a class="panel-collapse collapse show text-info" role="button" target="_blank" data-parent="#PartyContacts" href="${assocViewUrl!}"  aria-expanded="true" aria-controls="heading_${i}" style="">
	            	${partyContactAssoc.name!} (${partyContactAssoc.partyId!}), Role : ${partyContactAssoc.assocRoleTypeId!}
	            </a>
	         </h4>
	         <div>&nbsp</div>
	      </div>
	      <div id="Contact_${i}" class="panel-collapse collapse show" data-parent="#" aria-labelledby="Contacts" style="">
	         <div class="panel-body">
	            <div class="col-md-12 col-sm-12">
	               <div class="row">
	                  
	                  <div class="col-md-4 col-sm-4">
	                     <div class="border rounded pl-2 pr-2">
	                        <div class="page-header">
	                           <h2 class="float-left display-4">Email</h2>
	                           <#if partyStatusId?if_exists != "PARTY_DISABLED">
	                           <div class="float-right">
	                              <i class="fa fa-plus btn btn-xs btn-primary emailModal" aria-hidden="true" data-toggle="modal" data-target="#emailModal" data-value="emailForm${i}"></i>
	                              <form name="emailForm${i}" id="emailForm${i}">
	                              	<input type="hidden" name="rowId" id="rowId" value="emailtableid_${i}" />
	                              	<input type="hidden" name="contactPartyId" id="contactPartyId" value="${partyContactAssoc.contactId!}" />
	                              	<input type="hidden" name="partyId" id="partyId" value="${targetPartyId!}" />
	                              	<input type="hidden" name="partyRelAssocId" id="partyRelAssocId" value="${partyContactAssoc.partyRelAssocId!}" />
	                              	<input type="hidden" name="targetRoleTypeId" id="targetRoleTypeId" value="${targetRoleTypeId!}" />
	                              	<input type="hidden" name="contactType" id="contactType" value="EMAIL" />
	                              	<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
	                              </form>
	                           </div>
	                           </#if>
	                        </div>
	                        <div class="table-responsive" style="margin-top: 24px">
	                           <table class="table table-striped" id="emailtableid_${i}">
	                              <thead>
	                                 <tr>
	                                    <th>Email</th>
	                                    <th>Type</th>
	                                    <th>Solicitation</th>
	                                    <th>Action</th>
	                                 </tr>
	                              </thead>
	                           </table>
	                        </div>
	                     </div>
	                  </div>
	                  
	                  <div class="col-md-4 col-sm-4">
	                     <div class="border rounded pl-2 pr-2">
	                        <div class="page-header">
	                           <h2 class="float-left display-4">Phone</h2>
	                          <#if partyStatusId?if_exists != "PARTY_DISABLED">
	                           <div class="float-right">
	                              <i class="fa fa-plus btn btn-xs btn-primary phoneModal" aria-hidden="true" data-toggle="modal" data-target="#phoneModal" data-value="phoneForm${i}"></i>
	                              <form name="phoneForm${i}" id="phoneForm${i}">
	                              	<input type="hidden" name="rowId" id="rowId" value="phonetableid_${i}" />
	                              	<input type="hidden" name="contactPartyId" id="contactPartyId" value="${partyContactAssoc.contactId!}" />
	                              	<input type="hidden" name="partyId" id="partyId" value="${targetPartyId!}" />
	                              	<input type="hidden" name="partyRelAssocId" id="partyRelAssocId" value="${partyContactAssoc.partyRelAssocId!}" />
	                              	<input type="hidden" name="targetRoleTypeId" id="targetRoleTypeId" value="${targetRoleTypeId!}" />
	                              	<input type="hidden" name="contactType" id="contactType" value="PHONE" />
	                              	<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
	                              </form>
	                           </div>
	                           </#if>
	                
	                        </div>
	                        <div class="table-responsive" style="margin-top: 24px">
	                           <table class="table table-striped" id="phonetableid_${i}">
	                              <thead>
	                                 <tr>
	                                    <th>Phone</th>
	                                    <th>Type</th>
	                                    <th>Solicitation</th>
	                                    <th>Action</th>
	                                 </tr>
	                              </thead>
	                           </table>
	                        </div>
	                     </div>
	                  </div>
	                  
	                  <div class="col-md-4 col-sm-4">
	                     <div class="border rounded pl-2 pr-2">
	                        <div class="page-header">
	                           <h2 class="float-left display-4">Designation</h2>
	                           <#if partyStatusId?if_exists != "PARTY_DISABLED">
	                           <div class="float-right">
	                              <i class="fa fa-plus btn btn-xs btn-primary designationModal" aria-hidden="true" data-toggle="modal" data-target="#designationModal" data-value="designationForm${i}" ></i>
	                              <form name="designationForm${i}" id="designationForm${i}">
	                              	<input type="hidden" name="rowId" id="rowId" value="designationtableid_${i}" />
	                              	<input type="hidden" name="contactPartyId" id="contactPartyId" value="${partyContactAssoc.contactId!}" />
	                              	<input type="hidden" name="partyId" id="partyId" value="${targetPartyId!}" />
	                              	<input type="hidden" name="partyRelAssocId" id="partyRelAssocId" value="${partyContactAssoc.partyRelAssocId!}" />
	                              	<input type="hidden" name="targetRoleTypeId" id="targetRoleTypeId" value="${targetRoleTypeId!}" />
	                              	<input type="hidden" name="contactType" id="contactType" value="DESIGNATION" />
	                              	<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
	                              </form>
	                              
	                           </div>
	                           </#if>
	                        </div>
	                        <div class="table-responsive" style="margin-top: 24px">
	                           <table class="table table-striped" id="designationtableid_${i}">
	                              <thead>
	                                 <tr>
	                                    <th>Position</th>
	                                    <th>Action</th>
	                                 </tr>
	                              </thead>
	                           </table>
	                        </div>
	                     </div>
	                  </div>
	                  
	               </div>
	               <#-- end row -->
	            </div>
	            <#-- end col-12 -->
	         </div>
	         <#-- panel body -->
	      </div>
	   
	   <script>
		   $(function(){
			   	var contactPartyId = "${partyContactAssoc.contactId!}";
			   	var partyId = "${targetPartyId!}";
			   	var partyRelAssocId = "${partyContactAssoc.partyRelAssocId!}";
			   	
			   	emailTableAppend("emailtableid_${i}", contactPartyId, partyId, partyRelAssocId);
		   		phoneTableAppend("phonetableid_${i}", contactPartyId, partyId, partyRelAssocId);
		   		designationTableAppend("designationtableid_${i}", contactPartyId, partyId, partyRelAssocId);
		   });
	   </script>
	   <#assign i =i+1/>
	   </div>
   </#list>
   </#if>	
</div>
<#--  Add Email Modal Start-->
<div id="emailModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg" style="width:50% !important;">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Add Email</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="">
            
            	<#-- <@AgGrid
					gridheadertitle="List of Email"
					gridheaderid="email-grid-action-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=false
					insertBtn=false
					updateBtn=false
					removeBtn=false
					refreshPrefBtnId="email-refresh-pref-btn"
					savePrefBtnId="email-save-pref-btn"
					clearFilterBtnId="email-clear-filter-btn"
										
				    userid="${userLogin.userLoginId}" 
				    shownotifications="true" 
				    instanceid="PARTY_EMAIL_LIST" 
				    autosizeallcol="true"
				    debug="false"
				    />    
				         
				<script type="text/javascript" src="/common-portal-resource/js/ag-grid/party/party-email.js"></script> -->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfEmail"
			instanceId="PARTY_EMAIL_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/party/party-email.js"
			headerLabel="List of Email"
			headerId="email-grid-action-container"
			savePrefBtn=false
			subFltrClearBtn=false
			clearFilterBtn=false
			exportBtn=false
			headerBarClass="grid-header-no-bar"
			savePrefBtnId ="email-save-pref-btn"
			clearFilterBtnId ="email-clear-filter-btn"
			subFltrClearId="email-sub-filter-clear-btn"
			/>
				<#-- 
               <div class="table-responsive">
                  <table id="ajaxEmailDatatable" class="table table-striped">
                     <thead>
                        <tr>
                           <th>Email</th>
                           <th>Purpose</th>
                           <th>Solicitation</th>
                        </tr>
                     </thead>
                  </table>
               </div>
               -->
            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
            </div>
         </div>
      </div>
   </div>
</div>
<#--  Add Email Modal End-->
<#--  Add Phone Modal Start-->
<div id="phoneModal" class="modal fade bd-example-modal-lg" role="dialog" aria-hidden="true">
   <div class="modal-dialog modal-lg" style="width:50% !important;">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Add Phone Numbers</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="">
            	<#-- <@AgGrid
					gridheadertitle="List of Phone"
					gridheaderid="phone-grid-action-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=false
					insertBtn=false
					updateBtn=false
					removeBtn=false
					refreshPrefBtnId="phone-refresh-pref-btn"
					savePrefBtnId="phone-save-pref-btn"
					clearFilterBtnId="phone-clear-filter-btn"
										
				    userid="${userLogin.userLoginId}" 
				    shownotifications="true" 
				    instanceid="PARTY_PHONE_LIST" 
				    autosizeallcol="true"
				    debug="false"
				    />    
				         
				<script type="text/javascript" src="/common-portal-resource/js/ag-grid/party/party-phone.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfPhone"
			instanceId="PARTY_PHONE_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/party/party-phone.js"
			headerLabel="List of Phone"
			headerId="phone-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=false
			subFltrClearBtn=true
			headerBarClass="grid-header-no-bar"
			savePrefBtnId ="phone-save-pref-btn"
			clearFilterBtnId ="phone-clear-filter-btn"
			subFltrClearId="phone-sub-filter-clear-btn"
			/>
            	<#-- 
               <div class="table-responsive">
                  <table id="ajaxPhoneDatatable" class="table table-striped">
                     <thead>
                        <tr>
                           <th>Phone Number</th>
                           <th>Purpose</th>
                           <th>Solicitation</th>
                        </tr>
                     </thead>
                  </table>
               </div>
                -->
            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
            </div>
         </div>
      </div>
   </div>
</div>
<#--  Add Phone Modal End-->
<#--  Add Designation Modal Start-->
<div id="designationModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg" style="width:75% !important;">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Add Designation</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="">
            	<#-- <@AgGrid
				gridheadertitle="List of Designation"
				gridheaderid="designation-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtnId="designation-refresh-pref-btn"
				savePrefBtnId="designation-save-pref-btn"
				clearFilterBtnId="designation-clear-filter-btn"
									
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="DESIGNATION_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/party/party-designation.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfDesignation"
			instanceId="DESIGNATION_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/party/party-designation.js"
			headerLabel="List of Designation"
			headerId="designation-grid-action-container"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=false
			subFltrClearBtn=false
			headerBarClass="grid-header-no-bar"
			savePrefBtnId ="designation-save-pref-btn"
			clearFilterBtnId ="designation-clear-filter-btn"
			subFltrClearId="designation-sub-filter-clear-btn"
			/>
				<#-- 
               <div class="table-responsive">
                  <table id="ajaxDesignationDatatable" class="table table-striped">
                     <thead>
                        <tr>
                           <th>Positions</th>
                        </tr>
                     </thead>
                  </table>
               </div>
               -->
            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
            </div>
         </div>
      </div>
   </div>
</div>

<@submitConfirmation 
	instanceId="submit-confirm-modal"
	/>

<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.dataTables.min.js"></script>

<#--  Add Designation Modal End-->
<script>

$(document).ready(function() {
	
	$(".remooveContact").on('click', function(e) {
	
		e.preventDefault();
		var id = this.id;
		var idArray = id.split('_');
		var idNum = idArray[1];
		var formId = "updateDefaultContact_"+idNum ;
		var form = document.getElementById(formId);
		var isDefault = form.elements['statusId_'+idNum].checked;
		if(isDefault){
			form.elements['statusId'].value = "PARTY_DEFAULT";
		} else{
			form.elements['statusId'].value = "";
		}
		$('form').attr('action',"removeContact");
		form.submit();	
	});
	
	$(".remooveContactFromSr").on('click', function(e) {
		e.preventDefault();
		var id = this.id;
		var idArray = id.split('_');
		var idNum = idArray[1];
		var formId = "updateDefaultContact_"+idNum ;
		var form = document.getElementById(formId);
		
		$('form').attr('action',"remooveContactFromSr");
		form.submit();	
	});

});
	function updateContactEmailType(myform,id){
		var form = document.getElementById(myform);
		var isMarketable = form.elements['isMarketable_'+id].checked;
		if(isMarketable){
			form.elements['isMarketable'].value = "Y";
		} else{
			form.elements['isMarketable'].value = "N";
		}
		
		var isDefault = form.elements['statusId_'+id].checked;
		if(isDefault){
			form.elements['statusId'].value = "PARTY_DEFAULT";
		} else{
			form.elements['statusId'].value = "";
		}
		
		form.submit();
	}
	
   function emailTableAppend(id, contactPartyId, partyId, partyRelAssocId) {
       $("#" + id).DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "/common-portal/control/getContactAndPartyAssocDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "EMAIL",
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "paging": false,
           "ordering": false,
           "info": false,
           "searching": false,
           'columnDefs': [
           {
             'targets': 0,
             'createdCell':  function (td, cellData, rowData, row, col) {
                $(td).attr('id', 'infoStringTD'); 
              }
           },
           {
             'targets': 2,
             'createdCell':  function (td, cellData, rowData, row, col) {
                $(td).attr('id', 'solicitationTD'); 
              }
           }
           ],
           "columns": [{
                   "data": "infoString"
               },
               {
                   "data": "purposeDescription"
               },
               {
                   "data": "allowSolicitation"
               },
               {
                   "data": "id",
                   "render": function(data, type, row, meta) {
                       var isEdit = row.isEdit;
                       var editButton = "";
                       if(isEdit != null && isEdit != "" && isEdit == "Y") {
                          editButton = '<i class="fa fa-edit btn btn-xs btn-primary" id="'+row.id+'" aria-hidden="true" onclick=updateEmailTD("'+id+'","'+row.contactMechId+'","'+row.id+'","'+contactPartyId+'","'+partyId+'","'+partyRelAssocId+'");></i>';
                       }
                       return editButton;
                   }
               },
           ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
   }
   
   function phoneTableAppend(id, contactPartyId, partyId, partyRelAssocId) {
       $("#" + id).DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "/common-portal/control/getContactAndPartyAssocDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "PHONE",
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "paging": false,
           "ordering": false,
           "info": false,
           "searching": false,
           'columnDefs': [
           {
             'targets': 0,
             'createdCell':  function (td, cellData, rowData, row, col) {
                $(td).attr('id', 'contactNumberTD'); 
              }
           },
           {
             'targets': 2,
             'createdCell':  function (td, cellData, rowData, row, col) {
                $(td).attr('id', 'solicitationTD'); 
              }
           }
           ],
           "columns": [{
                   "data": "contactNumber"
               },
               {
                   "data": "purposeDescription"
               },
               {
                   "data": "allowSolicitation"
               },
               {
                   "data": "id",
                   "render": function(data, type, row, meta) {
                       var isEdit = row.isEdit;
                       var editButton = "";
                       if(isEdit != null && isEdit != "" && isEdit == "Y") {
                          editButton = '<i class="fa fa-edit btn btn-xs btn-primary" id="'+row.id+'" aria-hidden="true" onclick=updatePhoneTD("'+id+'","'+row.contactMechId+'","'+row.id+'","'+contactPartyId+'","'+partyId+'","'+partyRelAssocId+'","'+row.purposeTypeId+'");></i>';
                       }
                       return editButton;
                   }
               },
           ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
   }
   
   function designationTableAppend(id, contactPartyId, partyId, partyRelAssocId) {
       $("#" + id).DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "/common-portal/control/getContactAndPartyAssocDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "DESIGNATION",
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "paging": false,
           "ordering": false,
           "info": false,
           "searching": false,
           'columnDefs': [
           {
             'targets': 0,
             'createdCell':  function (td, cellData, rowData, row, col) {
                $(td).attr('id', 'designationNameTD'); 
              }
           },
           ],
           "columns": [{
                   "data": "designationName"
               },
               {
                   "data": "designationName",
                   "render": function(data, type, row, meta) {
                       data = '<i class="fa fa-edit btn btn-xs btn-primary" id="'+row.id+'" aria-hidden="true" onclick=updateDesignationTD("'+id+'","'+row.designationId+'","'+row.id+'","'+contactPartyId+'","'+partyId+'","'+partyRelAssocId+'");></i>';
                       return data;
                   }
               },
           ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
   }
   
   function findEmail(id, contactPartyId, partyId, partyRelAssocId, targetRoleTypeId) {
       $("#ajaxEmailDatatable").DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "/common-portal/control/getContactsDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "EMAIL",
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "order": [],
           "columns": [{
                   "data": "infoString",
                   "render": function(data, type, row, meta) {
                       data = '<a href="#" onclick=addEmail("' + id + '","' + row.contactPartyId + '","' + row.partyId + '","' + row.contactMechId + '","' + row.partyRelAssocId + '","'+targetRoleTypeId+'")>' + data + '</a>';
                       return data;
                   }
               },
               {
                   "data": "purpose"
               },
               {
                   "data": "solicitation"
               },
           ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
   }
   
   function findPhone(id, contactPartyId, partyId, partyRelAssocId, targetRoleTypeId) {
       $("#ajaxPhoneDatatable").DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "/common-portal/control/getContactsDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "PHONE",
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "order": [],
           "columns": [{
                   "data": "contactNumber",
                   "render": function(data, type, row, meta) {
                       data = '<a href="#" onclick=addPhone("' + id + '","' + row.contactPartyId + '","' + row.partyId + '","' + row.contactMechId + '","' + row.partyRelAssocId + '","'+targetRoleTypeId+'")>' + data + '</a>';
                       return data;
                   }
               },
               {
                   "data": "purpose"
               },
               {
                   "data": "solicitation"
               },
           ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
   }
   
   function findDesignation(id, contactPartyId, partyId, partyRelAssocId, targetRoleTypeId) {
       $("#ajaxDesignationDatatable").DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "/common-portal/control/getContactsDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "DESIGNATION",
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "order": [],
           "columns": [{
               "data": "designationName",
               "render": function(data, type, row, meta) {
                   data = '<a href="#" onclick=addDesignation("' + id + '","' + row.contactPartyId + '","' + row.partyId + '","' + row.designationId + '","' + row.partyRelAssocId + '","'+targetRoleTypeId+'")>' + data + '</a>';
                   return data;
               }
           }, ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
   }
   
   
   
   function addEmail(id, contactPartyId, partyId, contactMechId, partyRelAssocId, targetRoleTypeId) {
       $('#emailModal').modal('hide');
       if (id != null && id != "" && contactPartyId != null && contactPartyId != "" && partyId != null && partyId != "" && contactMechId != null && contactMechId != "" && partyRelAssocId != null && partyRelAssocId != "") {
           jQuery.ajax({
               url: "/common-portal/control/addContactsToAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "contactMechId": contactMechId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "EMAIL",
                   "partyRoleTypeId": targetRoleTypeId,
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Email successfully added");
                   emailTableAppend(id, contactPartyId, partyId, partyRelAssocId);
                   //location.reload();
               }
           });
       }
   }
   
   function addPhone(id, contactPartyId, partyId, contactMechId, partyRelAssocId, targetRoleTypeId) {
       $('#phoneModal').modal('hide');
       if (id != null && id != "" && contactPartyId != null && contactPartyId != "" && partyId != null && partyId != "" && contactMechId != null && contactMechId != "" && partyRelAssocId != null && partyRelAssocId != "") {
           jQuery.ajax({
               url: "/common-portal/control/addContactsToAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "contactMechId": contactMechId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "PHONE",
                   "partyRoleTypeId": targetRoleTypeId,
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Phone successfully added");
                   phoneTableAppend(id, contactPartyId, partyId, partyRelAssocId);
                   //location.reload();
               }
           });
       }
   }
   
   function addDesignation(id, contactPartyId, partyId, designationId, partyRelAssocId, targetRoleTypeId) {
       $('#designationModal').modal('hide');
       if (id != null && id != "" && contactPartyId != null && contactPartyId != "" && partyId != null && partyId != "" && designationId != null && designationId != "" && partyRelAssocId != null && partyRelAssocId != "") {
           jQuery.ajax({
               url: "/common-portal/control/addContactsToAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "contactMechId": designationId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "DESIGNATION",
                   "partyRoleTypeId": targetRoleTypeId,
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Position successfully added");
                   designationTableAppend(id, contactPartyId, partyId, partyRelAssocId);
               }
           });
       }
   }
   
   function updateEmailTD(id, contactMechId, rowId, contactPartyId, partyId, partyRelAssocId) {
      var row = $("#"+rowId).closest('tbody tr');
      var solicitation = row.find("#solicitationTD").text();
      var infoString = row.find("#infoStringTD").text();
      
      if((solicitation != null && solicitation != "") || (infoString != null && infoString != "")) {
         $("#"+rowId).removeClass().addClass("fa fa-check-square-o btn btn-xs btn-success");
         $("#"+rowId).attr("onclick","updateEmail('"+id+"','"+contactMechId+"','"+rowId+"','"+contactPartyId+"','"+partyId+"','"+partyRelAssocId+"')");
   
      }
      
      var data = '<select class="ui dropdown form-control input-sm" data-original-title="Hide" id="solicitation" name="solicitation">';
      if(solicitation != null && solicitation != "" && solicitation == "Y") {
         data = data + '<option value="Y" selected>Yes</option>';
      } else {
         data = data + '<option value="Y">Yes</option>';
      }
      if(solicitation != null && solicitation != "" && solicitation == "N") {
         data = data + '<option value="N" selected>No</option>';
      } else {
         data = data + '<option value="N">No</option>';
      }
      data = data + '</select>';
      
   
      if(solicitation != null && solicitation != "") {
         row.find("#solicitationTD").html("").append(data);
      } 
      if(infoString != null && infoString != "") {
         row.find("#infoStringTD").html("").append("<input type='text' id='infoString' name='infoString' value=\""+infoString+"\" class='form-control input-sm'>");
      }
   }
   function updateEmail(id, contactMechId, rowId, contactPartyId, partyId, partyRelAssocId) {
          var row = $("#"+rowId).closest('tbody tr');
          var solicitation = row.find("#solicitation").val();
          var infoString = row.find("#infoString").val();
          if(infoString != null && infoString != "") {
             var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
             if (reg.test(infoString) == false) {
                $.notify({
                      message : '<p>Invalid Email Address</p>',
                 });
                 return false;
             }
          }
          if((infoString == null || infoString == "")) {
              $.notify({
                      message : '<p>Please enter valid email</p>',
              });
              return false;
          }
          if(contactPartyId != null && contactPartyId != "") {
          
          	$.ajax({
	        	type: "POST",
	        	url : "/common-portal/control/getDuplicateEmailList",
	        	async: false,
	         	data: { "primaryEmail": infoString,
	         		"partyId": partyId, "screenType": "UPDATE"},
	        	success: function(data) {
	        		var message = data.Error_Message;
	        		if(!(message === "NO_RECORDS")){
	        			valid = false;
				     	$('#submit-confirm-modal').modal('show');
				        $("#message").html(message);
				        $('#submit-confirm-modal-submit-btn').attr('onclick', 'updateEmailAction(\''+id+'\', \''+contactPartyId+'\', \''+partyId+'\', \''+contactMechId+'\', \''+partyRelAssocId+'\', \''+solicitation+'\', \''+infoString+'\')');
				  	} else {
				  		updateEmailAction(id, contactPartyId, partyId, contactMechId, partyRelAssocId, solicitation, infoString);
				  	}
	        	}
	      	});
			             
          }
   }
	function updateEmailAction(id, contactPartyId, partyId, contactMechId, partyRelAssocId, solicitation, infoString) {
   		jQuery.ajax({
               url: "/common-portal/control/updatePartyContactAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "contactMechId": contactMechId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "EMAIL",
                   "solicitation": solicitation,
                   "infoString": infoString,
                   "partyRoleTypeId": "${partyRoleTypeId!}",
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Email successfully updated");
                   emailTableAppend(id, contactPartyId, partyId, partyRelAssocId);
                   //location.reload();
               }
             });		
   	}
   
   function updatePhoneTD(id, contactMechId, rowId, contactPartyId, partyId, partyRelAssocId, purposeTypeId) {
      var row = $("#"+rowId).closest('tbody tr');
      var solicitation = row.find("#solicitationTD").text();
      var contactNumber = row.find("#contactNumberTD").text();
      
      if((solicitation != null && solicitation != "") || (contactNumber != null && contactNumber != "")) {
         $("#"+rowId).removeClass().addClass("fa fa-check-square-o btn btn-xs btn-success");
         $("#"+rowId).attr("onclick","updatePhone('"+id+"','"+contactMechId+"','"+rowId+"','"+contactPartyId+"','"+partyId+"','"+partyRelAssocId+"','"+purposeTypeId+"')");
   
      }
      
      var data = '<select class="ui dropdown form-control input-sm" data-original-title="Hide" id="solicitation" name="solicitation">';
      if(solicitation != null && solicitation != "" && solicitation == "Y") {
         data = data + '<option value="Y" selected>Yes</option>';
      } else {
         data = data + '<option value="Y">Yes</option>';
      }
      if(solicitation != null && solicitation != "" && solicitation == "N") {
         data = data + '<option value="N" selected>No</option>';
      } else {
         data = data + '<option value="N">No</option>';
      }
      data = data + '</select>';
      
   
      if(solicitation != null && solicitation != "") {
         row.find("#solicitationTD").html("").append(data);
      } 
      if(contactNumber != null && contactNumber != "") {
         row.find("#contactNumberTD").html("").append("<input type='text' id='contactNumber' name='contactNumber' value=\""+contactNumber+"\" class='form-control input-sm'>");
      }
   }
   
   function updatePhone(id, contactMechId, rowId, contactPartyId, partyId, partyRelAssocId, purposeTypeId) {
          var row = $("#"+rowId).closest('tbody tr');
          var solicitation = row.find("#solicitation").val();
          var contactNumber = row.find("#contactNumber").val();
          if((contactNumber == null || contactNumber == "")) {
              $.notify({
                      message : '<p>Please enter Contact number</p>',
              });
              return false;
          }
          if(contactPartyId != null && contactPartyId != "") {
          	
          	if (purposeTypeId && purposeTypeId == 'PHONE_MOBILE') {
          		$.ajax({
		        	type: "POST",
		        	url : "/common-portal/control/getDuplicatePhoneNumber",
		        	async: false,
		         	data: { "primaryPhoneNumber": contactNumber,
		         		"partyId": partyId, "screenType": "UPDATE"},
		        	success: function(data) {
		        		var message = data.Error_Message;
		        		if(!(message === "NO_RECORDS")){
		        			valid = false;
					     	$('#submit-confirm-modal').modal('show');
					        $("#message").html(message);
					        $('#submit-confirm-modal-submit-btn').attr('onclick', 'updatePhoneAction(\''+id+'\', \''+contactPartyId+'\', \''+partyId+'\', \''+contactMechId+'\', \''+partyRelAssocId+'\', \''+solicitation+'\', \''+contactNumber+'\')');
					  	} else {
					  		updatePhoneAction(id, contactPartyId, partyId, contactMechId, partyRelAssocId, solicitation, contactNumber);
					  		//location.reload();
					  	}
		        	}
		      	});	
          	} else {
          		updatePhoneAction(id, contactPartyId, partyId, contactMechId, partyRelAssocId, solicitation, contactNumber);
          		//location.reload();
          	}
             
          }
   }
   	function updatePhoneAction(id, contactPartyId, partyId, contactMechId, partyRelAssocId, solicitation, contactNumber) {
   		jQuery.ajax({
               url: "/common-portal/control/updatePartyContactAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "contactMechId": contactMechId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "PHONE",
                   "solicitation": solicitation,
                   "contactNumber": contactNumber,
                   "partyRoleTypeId": "${partyRoleTypeId!}",
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Phone successfully updated");
                   phoneTableAppend(id, contactPartyId, partyId, partyRelAssocId);
                   //location.reload();
               }
             });
   	}
   
   function updateDesignationTD(id, designationId, rowId, contactPartyId, partyId, partyRelAssocId) {
      var row = $("#"+rowId).closest('tbody tr');
      var designationName = row.find("#designationNameTD").text();
      
      if(designationName != null && designationName != "") {
         $("#"+rowId).removeClass().addClass("fa fa-check-square-o btn btn-xs btn-success");
         $("#"+rowId).attr("onclick","updateDesination('"+id+"','"+designationId+"','"+rowId+"','"+contactPartyId+"','"+partyId+"','"+partyRelAssocId+"')");
      }
      if(designationName != null && designationName != "") {
         row.find("#designationNameTD").html("").append("<input type='text' id='designationName' name='designationName' value=\""+designationName+"\" class='form-control input-sm'>");
      }
   }
   
   function updateDesination(id, designationId, rowId, contactPartyId, partyId, partyRelAssocId) {
          var row = $("#"+rowId).closest('tbody tr');
          var designationName = row.find("#designationName").val();
          if((designationName == null || designationName == "")) {
              $.notify({
                      message : '<p>Please enter position</p>',
              });
              return false;
          }
          if(contactPartyId != null && contactPartyId != "") {
             jQuery.ajax({
               url: "/common-portal/control/updatePartyContactAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "partyId": partyId,
                   "contactMechId": designationId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "DESIGNATION",
                   "designationName": designationName,
                   "partyRoleTypeId": "${partyRoleTypeId!}",
                   "externalLoginKey": "${requestAttributes.externalLoginKey!}"
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Designation successfully updated");
                   designationTableAppend(id, contactPartyId, partyId, partyRelAssocId);
               }
             });
          }
   }
</script>
<style>
.btn.btn-primary.btn-xs.ml-2.remooveContactFromSr{
margin-right: 40px !important;
}
.checkbox-inline{
margin-right: 40px !important;
}
</style>
