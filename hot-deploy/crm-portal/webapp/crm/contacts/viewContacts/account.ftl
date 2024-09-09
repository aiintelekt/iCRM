<div class="page-header">
   <div class="float-right">
      <#--<a href="createAccount" class="btn btn-xs btn-primary m5"> Create New</a>-->
      <button class="btn btn-xs btn-primary m5" data-toggle="modal" data-target="#accountModal" >Assign Account</button>
   </div>
   <h2 class="">Accounts</h2>
</div>

<#--  Contact And Account Association Start -->
${screens.render("component://crm/webapp/widget/crm/screens/common/CommonScreens.xml#contactAndAccountAssoc")}
<#--  Contact And Account Association End -->

<#-- Assign Account Model---> 
<!-- Find parent account pop for create and update account-->
<div id="accountModal" class="modal fade" role="dialog">
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
                     <@fromSimpleAction id="findAssocAccount" showCancelBtn=false isSubmitAction=false submitLabel="Find"/>
                     <#--
                     <div class="col-md-1 col-sm-1">
                        <button type="button" class="btn btn-sm btn-primary navbar-dark m5" onclick="javascript:findAccountsModal();">Find Accounts</button>
                     </div> -->
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
   $('#accountModal').on('click', '.parentSet', function() {
       var value = $(this).children("span").attr("value");
       if (value != null && value != "") {
           $('#accountPartyIdModal').val(value);
           $('#accountModal').modal('hide');
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
       findAccountsModal();
   });
   $('#findAssocAccount').on('click',function(){
     findAccountsModal();
   });
   
   function findAccountsModal() {
       var accountSearchPartyId = $("#accountSearchPartyId").val();
       var searchGroupName = $("#searchGroupName").val();
       var searchEmailId = $("#searchEmailId").val();
       var searchPhoneNum = $("#searchPhoneNum").val();
       var url = "searchAccounts?accountSearchPartyId=" + accountSearchPartyId + "&searchGroupName=" + searchGroupName + "&searchEmailId=" + searchEmailId + "&searchPhoneNum=" + searchPhoneNum;
       $('#ajaxAccountdatatable').DataTable({
           "processing": true,
           "serverSide": true,
           "destroy": true,
           "filter": false,
           "ordering": false,
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
           ]
       });
   }
</script>
<#-- 
<div id="accountModal" class="modal fade" role="dialog">
<div class="modal-dialog modal-md">
   <div class="modal-content">
      <div class="modal-header">
         <h4 class="modal-title">Accounts</h4>
         <button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
         <form method="post" action="assignAccount" id="assignAccount" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            <input type="hidden" name="contactPartyId" value="${partySummary?if_exists.partyId?if_exists}">
            <input type="hidden" name="partyId" value="${partySummary?if_exists.partyId?if_exists}">
            <input type="hidden" name="party" value="ACCOUNT">
            <input type="hidden" name="activeTab" value="account" />
            <input type="hidden" name="donePage" value="viewContact">
            <div class="form-group row row has-error">
               <label  class="col-sm-4 col-form-label">Account</label>
               <div class="col-sm-7">
                  <#assign accParty = delegator.findByAnd("PartyRole", {"roleTypeId" : "ACCOUNT"}, [], false)>
                  <select class="form-control input-sm" name="accountPartyId" id="accountPartyId" required>
                     <#if !accParty?has_content>
                     <option value="_NA_">N/A</option>
                     <#else>
                     <option value="">---Select---</option>
                     <#if partyToAccountByRole?exists && partyToAccountByRole?has_content>
                     <#list partyToAccountByRole as accParty>
                     <#assign defaultStates = delegator.findOne("PartyGroup", {"partyId" : accParty?if_exists.partyId?if_exists}, true)>
                     <#assign selected="selected=\"selected\"">
                     <option value="${defaultStates?if_exists.partyId?if_exists}">${defaultStates?if_exists.groupName?if_exists}</option>
                     </#list>
                     </#if>
                     </#if>
                  </select>
                  <div class="help-block with-errors"></div>
               </div>
            </div>
            <div class="modal-footer">
               <Input type="submit" class="btn btn-sm btn-primary" value="Assign Account" />
            </div>
         </form>
      </div>
   </div>
</div>
-->
<#-- Assign Account modal End-->