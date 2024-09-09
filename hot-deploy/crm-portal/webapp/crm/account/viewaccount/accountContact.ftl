<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign accountPartyId = parameters.partyId?if_exists />
<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
</#if>
<div class="page-header">
   <div class="float-right">
      <a href="<@ofbizUrl>createContact?accountPartyId=${accountPartyId?if_exists}&tabId=account</@ofbizUrl>" class="btn btn-xs btn-primary m5" > ${uiLabelMap.createNew}</a>
      <button type="button" class="btn btn-xs btn-primary m5" data-toggle="modal" data-target="#contactModal">${uiLabelMap.addFromExisting}</button>
   </div>
   <h2 class="">${uiLabelMap.Contacts}</h2>
</div>

<#--  Contact And Account Association Start -->
${screens.render("component://crm/webapp/widget/crm/screens/common/CommonScreens.xml#contactAndAccountAssoc")}
<#--  Contact And Account Association End -->
<#--  Add Contact Modal Start-->
<div id="contactModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.findContacts!}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="col-md-12 col-lg-12 col-sm-12">
            <div class="border rounded bg-light margin-adj-accordian pad-top">
               <#-- <form method="post" action="#" id="searchContant" class="form-horizontal" name="searchContant" novalidate="novalidate" data-toggle="validator"> -->
                  <div class="row p-2">
                     <div class="col-md-2 col-sm-2">
                     	<@inputCell    
		                    id="contactSearchPartyId"
		                    placeholder =uiLabelMap.contactId
		                    />
                     </div>
                     <div class="col-md-2 col-sm-2">
                     	<@inputCell    
		                    id="searchFirstName"
		                    placeholder = "Name"
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
                     <@submit id="findContacts" label="Find"/>
                  </div>
               <#-- </form> -->
               <div class="clearfix"> </div>
            </div>
            <div class="clearfix"> </div>
            <div class="page-header">
               <h2 class="">Contacts List </h2>
            </div>
            <div class="table-responsive">
               <form name="assignContactToAccount" >
                  <input type="hidden" name="activeTab" value="contact" />
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
            <button type="submit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
         </div>
      </div>
   </div>
</div>
<#--  Add Contact Modal End-->
<form method="post" name="contactAccountForm" id="contactAccountForm">
   <input type="hidden" name="activeTab" value="contact" />
   <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
   <input type="hidden" name="partyId" id="partyId" value="${accountPartyId?if_exists}"/>
   <input type="hidden" name="accountPartyId" id="accountPartyId" value="${accountPartyId?if_exists}"/>
   <input type="hidden" name="contactPartyId" id="contactPartyId" value=""/>
</form>

<script>
   $(function(){
     findContacts();
   });
   
   $("#findContacts").on('click',function(){
     findContacts();
   });
   
   function removeContactFromAccount(contactPartyId) {
       $('#contactPartyId').val(contactPartyId);
       $("#contactAccountForm").attr("action", "removeContactFromAccount");
       $('#contactAccountForm').submit();
   }
   
   $('#contactModal').on('click', '.addContact', function() {
       var value = $(this).children("span").attr("value");
       $('#contactPartyId').val(value);
       $("#contactAccountForm").attr("action", "addContactFromAccount");
       $('#contactAccountForm').submit();
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