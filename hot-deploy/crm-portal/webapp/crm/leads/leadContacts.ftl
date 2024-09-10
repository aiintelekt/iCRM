<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<#assign leadPartyId = parameters.partyId?if_exists />
<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
</#if>
<div class="clearfix"> </div>
<div class="page-header">
   <h2 class="float-left">${uiLabelMap.Contacts}</h2>
   <div class="float-right">
      <a href="<@ofbizUrl>createContact?leadPartyId=${leadPartyId?if_exists}&tabId=lead</@ofbizUrl>" class="btn btn-xs btn-primary m5" > ${uiLabelMap.createNew}</a>
      <button type="button" class="btn btn-xs btn-primary m5" data-toggle="modal" data-target="#contactModal">${uiLabelMap.addFromExisting}</button>
   </div>
</div>

<#--  Add Contact Modal Start ==================================-->
<div id="contactModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.findContacts!}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="card-header">
               <#-- <form method="post" action="#" id="searchContant" class="form-horizontal" name="searchContant" novalidate="novalidate" data-toggle="validator"> -->
                  <div class="row">
                     <div class="col-md-2 col-sm-2">
                        <div class="form-group row mr">
                           <input type="text" class="form-control input-sm" name="contactSearchPartyId" id="contactSearchPartyId" placeholder="Contact ID">
                        </div>
                     </div>
                     <div class="col-md-2 col-sm-2">
                        <div class="form-group row mr">
                           <input type="text" class="form-control input-sm" name="searchFirstName" id="searchFirstName" placeholder="Name">
                        </div>
                     </div>
                     <div class="col-md-2 col-sm-2">
                        <div class="form-group row mr">
                           <input type="text" class="form-control input-sm" name="searchEmailId" id="searchEmailId" placeholder="Email Address">
                        </div>
                     </div>
                     <div class="col-md-2 col-sm-2">
                        <div class="form-group row mr">
                           <input type="text" class="form-control input-sm" name="searchPhoneNum" id="searchPhoneNum" placeholder="Phone Number">
                        </div>
                     </div>
                     <@fromSimpleAction id="findContacts" showCancelBtn=false isSubmitAction=false submitLabel="Find"/>
                     <#-- 
                     <div class="col-md-1 col-sm-1">
                        <input type="button" class="btn btn-sm btn-primary" onclick="javascript:findContants();" value="Find"/>
                     </div> -->
                  </div>
               <#-- </form> -->
               <div class="clearfix"> </div>
            </div>
            <div class="clearfix"> </div>
            <div class="page-header">
               <h2 class="float-left">Contacts List </h2>
            </div>
            <div class="table-responsive">
               <form name="assignContactToAccount" >
                  <input type="hidden" name="activeTab" value="contacts" />
                  <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                  <input type="hidden" name="partyId" id="partyId" value="${accountPartyId?if_exists}"/>
                  <table id="ajaxdatatable" class="table table-striped">
                     <thead>
                        <tr>
                           <th>Contact Name</th>
                           <th>Status</th>
                           <th>City</th>
                           <th>State</th>
                           <th>Phone Number</th>
                           <th>E-Mail Address</th>
                        </tr>
                     </thead>
                  </table>
               </form>
            </div>
         </div>
         <div class="modal-footer">
            <button type="sbmit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
         </div>
      </div>
   </div>
</div>
<#--  Add Contact Modal End-->




<div class="panel-group" id="LeadContacts" role="tablist" aria-multiselectable="true">
   <#if leadContactAssocList?exists && leadContactAssocList?has_content>
   <#assign i = 1/>
   <#list leadContactAssocList as leadContactsAssoc>
   <div class="panel panel-default">
      <div class="panel-heading" role="tab" id="heading_${i}" >
      	<div class="float-right position-absolute"  style="right: 0; padding-right: 65px; padding-top: 10px;">
      	 	<#if requestURI == "viewLead">
               <form name="updateDefaultContact_${i}" id="updateDefaultContact_${i}" method="post" action="updateDefaultContact">
                  <input type="hidden" name="activeTab" value="contacts" />
                  <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                  <input type="hidden" name="partyIdFrom" value="${leadContactsAssoc.contactId?if_exists}"/>
                  <input type="hidden" name="partyId" value="${leadContactsAssoc.leadId?if_exists}"/>
                  <input type="hidden" name="roleTypeIdFrom" value="CONTACT"/>
                  <input type="hidden" name="roleTypeIdTo" value="LEAD"/>
                  <label class="checkbox-inline" for="statusId_${i}">
                  	<input type="checkbox" class="checkbox" name="statusId_${i}" id="statusId_${i}" onchange="javascript:updateContactEmailType('updateDefaultContact_${i}','${i}');" <#if leadContactsAssoc.statusId?exists && leadContactsAssoc.statusId?has_content && leadContactsAssoc.statusId=="PARTY_DEFAULT">checked</#if>/>Default
                  	<input type="hidden" name="statusId" id="statusId"/>
                  </label> &nbsp;
                  <label class="checkbox-inline" for="isMarketable_${i}">
                  	<input type="checkbox" class="checkbox" name="isMarketable_${i}" id="isMarketable_${i}" onchange="javascript:updateContactEmailType('updateDefaultContact_${i}','${i}');" <#if leadContactsAssoc.isMarketable?exists && leadContactsAssoc.isMarketable?has_content && leadContactsAssoc.isMarketable=="Y">checked</#if>/>Marketing
                  	<input type="hidden" name="isMarketable" id="isMarketable"/>
                  </label>
                  
               </form>
            </#if>
        </div>
         <h4 class="panel-title">
            <a class="panel-collapse collapse show" role="button" data-parent="#AccountContacts" <#if requestURI == "viewAccount"> href="<@ofbizUrl>viewContact?partyId=${leadContactsAssoc.contactId!}</@ofbizUrl>" <#elseif requestURI == "viewLead"> href="<@ofbizUrl>viewContact?partyId=${leadContactsAssoc.contactId!}</@ofbizUrl>" </#if> aria-expanded="true" aria-controls="heading_${i}" style="">
            <#if requestURI == "viewLead">
            ${leadContactsAssoc.name!} (${leadContactsAssoc.contactId!})
            <#elseif requestURI == "viewContact">
            ${leadContactsAssoc.companyName!} (${leadContactsAssoc.accountId!})
            </#if>
            </a>
         </h4>
      </div>
      <div id="Contact_${i}" class="panel-collapse collapse show" data-parent="#" aria-labelledby="Contacts" style="">
         <div class="panel-body">
            <div class="col-md-12 col-sm-12">
               <div class="row">
                  <div class="col-md-4 col-sm-4">
                     <div class="border rounded pl-2 pr-2">
                        <div class="page-header">
                           <h2 class="float-left display-4">Email</h2>
                           <div class="float-right">
                              <i class="fa fa-plus btn btn-xs btn-primary" aria-hidden="true" data-toggle="modal" data-target="#emailModal" onclick=findEmail("emailtableid_${i}","${leadContactsAssoc.contactId!}","${leadContactsAssoc.leadId!}","${leadContactsAssoc.partyRelAssocId!}");></i>
                           </div>
                        </div>
                        <div class="table-responsive">
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
                           <h2 class="float-left display-4">Phone Numbers</h2>
                          
                           <div class="float-right">
                              <i class="fa fa-plus btn btn-xs btn-primary" aria-hidden="true" data-toggle="modal" data-target="#phoneModal" onclick=findPhone("phonetableid_${i}","${leadContactsAssoc.contactId!}","${leadContactsAssoc.leadId!}","${leadContactsAssoc.partyRelAssocId!}");></i>
                           </div>
                
                        </div>
                        <div class="table-responsive">
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
                           <div class="float-right">
                              <i class="fa fa-plus btn btn-xs btn-primary" aria-hidden="true" data-toggle="modal" data-target="#designationModal" onclick=findDesignation("designationtableid_${i}","${leadContactsAssoc.contactId!}","${leadContactsAssoc.leadId!}","${leadContactsAssoc.partyRelAssocId!}");></i>
                           </div>
                        </div>
                        <div class="table-responsive">
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
		   	var contactPartyId = "${leadContactsAssoc.contactId!}";
		   	var leadPartyId = "${leadContactsAssoc.leadId!}";
		   	var partyRelAssocId = "${leadContactsAssoc.partyRelAssocId!}";
		   	
		   	emailTableAppend("emailtableid_${i}", contactPartyId, leadPartyId, partyRelAssocId);
	   		phoneTableAppend("phonetableid_${i}", contactPartyId, leadPartyId, partyRelAssocId);
	   		designationTableAppend("designationtableid_${i}", contactPartyId, leadPartyId, partyRelAssocId);
	   });
   </script>
   <#assign i =i+1/>
   </div>
   </#list>
   </#if>	
</div>
<#--  Add Email Modal Start-->
<div id="emailModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Add Email</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="card-header">
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
            </div>
            <div class="modal-footer">
               <button type="sbmit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
            </div>
         </div>
      </div>
   </div>
</div>
<#--  Add Email Modal End-->
<#--  Add Phone Modal Start-->
<div id="phoneModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Add Phone Numbers</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="card-header">
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
            </div>
            <div class="modal-footer">
               <button type="sbmit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
            </div>
         </div>
      </div>
   </div>
</div>
<#--  Add Phone Modal End-->
<#--  Add Designation Modal Start-->
<div id="designationModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Add Designation</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="card-header">
               <div class="table-responsive">
                  <table id="ajaxDesignationDatatable" class="table table-striped">
                     <thead>
                        <tr>
                           <th>Positions</th>
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
</div>
<#--  Add Designation Modal End-->

<form method="post" name="contactLeadForm" id="contactLeadForm">
   <input type="hidden" name="activeTab" value="contacts" />
   <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
   <input type="hidden" name="partyId" id="partyId" value="${leadPartyId?if_exists}"/>
   <input type="hidden" name="leadPartyId" id="leadPartyId" value="${leadPartyId?if_exists}"/>
   <input type="hidden" name="contactPartyId" id="contactPartyId" value=""/>
</form>
<script>
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
	/*
   function acctContactID(contactPartyId, acctPartyId, partyRelAssocId) {
       emailTableAppend("emailtableid_" + contactPartyId, contactPartyId, acctPartyId, partyRelAssocId);
       phoneTableAppend("phonetableid_" + contactPartyId, contactPartyId, acctPartyId, partyRelAssocId);
       designationTableAppend("designationtableid_" + contactPartyId, contactPartyId, acctPartyId, partyRelAssocId);
   } */
   
   function emailTableAppend(id, contactPartyId, leadPartyId, partyRelAssocId) {
       $("#" + id).DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactAndLeadAssocDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "EMAIL"
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
                          editButton = '<i class="fa fa-edit btn btn-xs btn-primary" id="'+row.id+'" aria-hidden="true" onclick=updateEmailTD("'+id+'","'+row.contactMechId+'","'+row.id+'","'+contactPartyId+'","'+leadPartyId+'","'+partyRelAssocId+'");></i>';
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
   
   function phoneTableAppend(id, contactPartyId, leadPartyId, partyRelAssocId) {
       $("#" + id).DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactAndLeadAssocDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "PHONE"
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
                          editButton = '<i class="fa fa-edit btn btn-xs btn-primary" id="'+row.id+'" aria-hidden="true" onclick=updatePhoneTD("'+id+'","'+row.contactMechId+'","'+row.id+'","'+contactPartyId+'","'+leadPartyId+'","'+partyRelAssocId+'");></i>';
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
   
   function designationTableAppend(id, contactPartyId, leadPartyId, partyRelAssocId) {
       $("#" + id).DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactAndLeadAssocDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "DESIGNATION"
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
                       data = '<i class="fa fa-edit btn btn-xs btn-primary" id="'+row.id+'" aria-hidden="true" onclick=updateDesignationTD("'+id+'","'+row.designationId+'","'+row.id+'","'+contactPartyId+'","'+leadPartyId+'","'+partyRelAssocId+'");></i>';
                       return data;
                   }
               },
           ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
   }
   
   function findEmail(id, contactPartyId, leadPartyId, partyRelAssocId) {
       $("#ajaxEmailDatatable").DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactsLeadDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "EMAIL"
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "order": [],
           "columns": [{
                   "data": "infoString",
                   "render": function(data, type, row, meta) {
                       data = '<a href="#" onclick=addEmail("' + id + '","' + row.contactPartyId + '","' + row.leadPartyId + '","' + row.contactMechId + '","' + row.partyRelAssocId + '")>' + data + '</a>';
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
   
   function findPhone(id, contactPartyId, leadPartyId, partyRelAssocId) {
       $("#ajaxPhoneDatatable").DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactsLeadDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "PHONE"
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "order": [],
           "columns": [{
                   "data": "contactNumber",
                   "render": function(data, type, row, meta) {
                       data = '<a href="#" onclick=addPhone("' + id + '","' + row.contactPartyId + '","' + row.leadPartyId + '","' + row.contactMechId + '","' + row.partyRelAssocId + '")>' + data + '</a>';
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
   
   function findDesignation(id, contactPartyId, leadPartyId, partyRelAssocId) {
       $("#ajaxDesignationDatatable").DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactsLeadDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "DESIGNATION"
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "order": [],
           "columns": [{
               "data": "designationName",
               "render": function(data, type, row, meta) {
                   data = '<a href="#" onclick=addDesignation("' + id + '","' + row.contactPartyId + '","' + row.leadPartyId + '","' + row.designationId + '","' + row.partyRelAssocId + '")>' + data + '</a>';
                   return data;
               }
           }, ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
   }
   
   function addEmail(id, contactPartyId, leadPartyId, contactMechId, partyRelAssocId) {
       $('#emailModal').modal('hide');
       if (id != null && id != "" && contactPartyId != null && contactPartyId != "" && leadPartyId != null && leadPartyId != "" && contactMechId != null && contactMechId != "" && partyRelAssocId != null && partyRelAssocId != "") {
           jQuery.ajax({
               url: "addContactsToLeadAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "contactMechId": contactMechId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "EMAIL"
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Email successfully added");
                   emailTableAppend(id, contactPartyId, leadPartyId, partyRelAssocId);
               }
           });
       }
   }
   
   function addPhone(id, contactPartyId, leadPartyId, contactMechId, partyRelAssocId) {
       $('#phoneModal').modal('hide');
       if (id != null && id != "" && contactPartyId != null && contactPartyId != "" && leadPartyId != null && leadPartyId != "" && contactMechId != null && contactMechId != "" && partyRelAssocId != null && partyRelAssocId != "") {
           jQuery.ajax({
               url: "addContactsToLeadAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "contactMechId": contactMechId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "PHONE"
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Phone successfully added");
                   phoneTableAppend(id, contactPartyId, leadPartyId, partyRelAssocId);
               }
           });
       }
   }
   
   function addDesignation(id, contactPartyId, leadPartyId, designationId, partyRelAssocId) {
       $('#designationModal').modal('hide');
       if (id != null && id != "" && contactPartyId != null && contactPartyId != "" && leadPartyId != null && leadPartyId != "" && designationId != null && designationId != "" && partyRelAssocId != null && partyRelAssocId != "") {
           jQuery.ajax({
               url: "addContactsToLeadAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "contactMechId": designationId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "DESIGNATION"
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Position successfully added");
                   designationTableAppend(id, contactPartyId, accountPartyId, partyRelAssocId);
               }
           });
       }
   }
   
   function updateEmailTD(id, contactMechId, rowId, contactPartyId, leadPartyId, partyRelAssocId) {
      var row = $("#"+rowId).closest('tbody tr');
      var solicitation = row.find("#solicitationTD").text();
      var infoString = row.find("#infoStringTD").text();
      
      if((solicitation != null && solicitation != "") || (infoString != null && infoString != "")) {
         $("#"+rowId).removeClass().addClass("fa fa-check-square-o btn btn-xs btn-success");
         $("#"+rowId).attr("onclick","updateEmail('"+id+"','"+contactMechId+"','"+rowId+"','"+contactPartyId+"','"+leadPartyId+"','"+partyRelAssocId+"')");
   
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
   
   function updateEmail(id, contactMechId, rowId, contactPartyId, leadPartyId, partyRelAssocId) {
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
             jQuery.ajax({
               url: "updateLeadContactAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "contactMechId": contactMechId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "EMAIL",
                   "solicitation": solicitation,
                   "infoString": infoString
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Email successfully updated");
                   emailTableAppend(id, contactPartyId, leadPartyId, partyRelAssocId);
               }
             });
          }
   }
   
   function updatePhoneTD(id, contactMechId, rowId, contactPartyId, leadPartyId, partyRelAssocId) {
      var row = $("#"+rowId).closest('tbody tr');
      var solicitation = row.find("#solicitationTD").text();
      var contactNumber = row.find("#contactNumberTD").text();
      
      if((solicitation != null && solicitation != "") || (contactNumber != null && contactNumber != "")) {
         $("#"+rowId).removeClass().addClass("fa fa-check-square-o btn btn-xs btn-success");
         $("#"+rowId).attr("onclick","updatePhone('"+id+"','"+contactMechId+"','"+rowId+"','"+contactPartyId+"','"+leadPartyId+"','"+partyRelAssocId+"')");
   
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
   
   function updatePhone(id, contactMechId, rowId, contactPartyId, leadPartyId, partyRelAssocId) {
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
             jQuery.ajax({
               url: "updateLeadContactAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "contactMechId": contactMechId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "PHONE",
                   "solicitation": solicitation,
                   "contactNumber": contactNumber
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Phone successfully updated");
                   phoneTableAppend(id, contactPartyId, leadPartyId, partyRelAssocId);
               }
             });
          }
   }
   
   function updateDesignationTD(id, designationId, rowId, contactPartyId, leadPartyId, partyRelAssocId) {
      var row = $("#"+rowId).closest('tbody tr');
      var designationName = row.find("#designationNameTD").text();
      
      if(designationName != null && designationName != "") {
         $("#"+rowId).removeClass().addClass("fa fa-check-square-o btn btn-xs btn-success");
         $("#"+rowId).attr("onclick","updateDesination('"+id+"','"+designationId+"','"+rowId+"','"+contactPartyId+"','"+leadPartyId+"','"+partyRelAssocId+"')");
      }
      if(designationName != null && designationName != "") {
         row.find("#designationNameTD").html("").append("<input type='text' id='designationName' name='designationName' value=\""+designationName+"\" class='form-control input-sm'>");
      }
   }
   
   function updateDesination(id, designationId, rowId, contactPartyId, leadPartyId, partyRelAssocId) {
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
               url: "updateLeadContactAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "leadPartyId": leadPartyId,
                   "contactMechId": designationId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "DESIGNATION",
                   "designationName": designationName
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Designation successfully updated");
                   designationTableAppend(id, contactPartyId, leadPartyId, partyRelAssocId);
               }
             });
          }
   }
   
   
    $(function(){
     findContacts();
   });
   
   $("#findContacts").on('click',function(){
     findContacts();
   });
   
   function removeContactFromAccount(contactPartyId) {
       $('#contactPartyId').val(contactPartyId);
       $("#contactLeadForm").attr("action", "removeContactFromAccount");
       $('#contactLeadForm').submit();
   }
   
   $('#contactModal').on('click', '.addContact', function() {
       var value = $(this).children("span").attr("value");
       $('#contactPartyId').val(value);
       $("#contactLeadForm").attr("action", "assignContactToLead");
       $('#contactLeadForm').submit();
       $('#parentAccountModal').modal('hide');
   });
   
   function findContacts() {
       var contactSearchPartyId = $("#contactSearchPartyId").val();
       var searchFirstName = $("#searchFirstName").val();
       var searchEmailId = $("#searchEmailId").val();
       var searchPhoneNum = $("#searchPhoneNum").val();
       var url = "searchContacts?contactSearchPartyId=" + contactSearchPartyId + "&searchFirstName=" + searchFirstName + "&searchEmailId=" + searchEmailId + "&searchPhoneNum=" + searchPhoneNum;
       $('#ajaxdatatable').DataTable({
           "processing": true,
           "serverSide": true,
           "destroy": true,
           "ajax": {
               "url": url,
               "type": "POST"
           },
           "Paginate": true,
           "language": {
               "emptyTable": "No data available in table",
               "info": "Showing _START_ to _END_ of _TOTAL_ entries",
               "infoEmpty": "No entries found",
               "infoFiltered": "(filtered1 from _MAX_ total entries)",
               "lengthMenu": "Show _MENU_ entries",
               "zeroRecords": "No matching records found",
               "oPaginate": {
                   "sNext": "Next",
                   "sPrevious": "Previous"
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "stateSave": true,
           "columns": [{
                   "data": "partyId",
                   "render": function(data, type, row, meta) {
                       if (type === 'display') {
                           data = '<a href="#" class="addContact"><span id="parAccId_' + row.id + '" name="parAccId_' + row.id + '" value ="' + data + '"></span>' + row.name + '(' + data + ')</a>';
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
           ]
       });
   }
</script>
