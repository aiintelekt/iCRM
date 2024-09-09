<#assign accountPartyId = parameters.partyId?if_exists />
<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
</#if>
<div class="clearfix"> </div>
<div class="panel-group" id="AccountContacts" role="tablist" aria-multiselectable="true">
   <#if accountContactAssocList?exists && accountContactAssocList?has_content>
   <#assign i = 1/>
   <#list accountContactAssocList as accountContactAssoc>
   <div class="panel panel-default">
      <div class="panel-heading" role="tab" id="heading_${i}" >
      	<div class="float-right position-absolute"  style="right: 0; padding-right: 65px; padding-top: 10px;">
      	 	<#if requestURI == "viewAccount">
               <form name="updateDefaultContact_${i}" id="updateDefaultContact_${i}" method="post" action="updateDefaultContact">
                  <input type="hidden" name="activeTab" value="contact" />
                  <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                  <input type="hidden" name="partyIdFrom" value="${accountContactAssoc.contactId?if_exists}"/>
                  <input type="hidden" name="partyId" value="${accountContactAssoc.accountId?if_exists}"/>
                  <input type="hidden" name="roleTypeIdFrom" value="CONTACT"/>
                  <input type="hidden" name="roleTypeIdTo" value="ACCOUNT"/>
                  <label class="checkbox-inline" for="statusId_${i}">
                  	<input type="checkbox" class="checkbox" name="statusId_${i}" id="statusId_${i}" onchange="javascript:updateContactEmailType('updateDefaultContact_${i}','${i}');" <#if accountContactAssoc.statusId?exists && accountContactAssoc.statusId?has_content && accountContactAssoc.statusId=="PARTY_DEFAULT">checked</#if>/>Default
                  	<input type="hidden" name="statusId" id="statusId"/>
                  </label> &nbsp;
                  <label class="checkbox-inline" for="isMarketable_${i}">
                  	<input type="checkbox" class="checkbox" name="isMarketable_${i}" id="isMarketable_${i}" onchange="javascript:updateContactEmailType('updateDefaultContact_${i}','${i}');" <#if accountContactAssoc.isMarketable?exists && accountContactAssoc.isMarketable?has_content && accountContactAssoc.isMarketable=="Y">checked</#if>/>Marketing
                  	<input type="hidden" name="isMarketable" id="isMarketable"/>
                  </label>
                  
                  <#--
                  <#if accountContactAssoc.statusId?exists && accountContactAssoc.statusId?has_content && accountContactAssoc.statusId=="PARTY_DEFAULT">
                  <input type="checkbox" class="checkbox" name="statusId" id="statusId" onchange="javascript:document.updateDefaultContact_${i}.submit();" checked/>Default
                  <#else>
                  <input type="checkbox" class="checkbox" name="statusId" id="statusId" onchange="javascript:document.updateDefaultContact_${i}.submit();" value="PARTY_DEFAULT"/>Default
                  </#if>
                  </label> &nbsp;
                  <label class="checkbox-inline" for="isMarketable">
            	  <#if accountContactAssoc.isMarketable?exists && accountContactAssoc.isMarketable?has_content && accountContactAssoc.isMarketable=="Y">
                  	<input type="checkbox" class="checkbox" name="isMarketable" id="isMarketable" onchange="javascript:document.updateDefaultContact_${i}.submit();" checked/>Marketing
                  <#else>
                  	<input type="checkbox" class="checkbox" name="isMarketable" id="isMarketable" onchange="javascript:document.updateDefaultContact_${i}.submit();" value="Y"/>Marketing
                  </#if>
            	  </label>
            	  -->
            	  
               </form>
            </#if>
            <#--	
            <div class="row checkbox">
            	<label class="checkbox-inline" for="statusId">
            		<input type="checkbox" class="checkbox" name="statusId" id="statusId" />Default 
            	</label> &nbsp;
            	<label class="checkbox-inline" for="isMarketable">
            		<input type="checkbox" class="checkbox" name="isMarketable" id="isMarketable" />Marketing
            	</label> 
            </div> -->
        </div>
         <h4 class="panel-title">
            <a class="panel-collapse collapse show" role="button" data-parent="#AccountContacts" <#if requestURI == "viewAccount"> href="<@ofbizUrl>viewContact?partyId=${accountContactAssoc.contactId!}</@ofbizUrl>" <#elseif requestURI == "viewContact"> href="<@ofbizUrl>viewAccount?partyId=${accountContactAssoc.accountId!}</@ofbizUrl>" </#if> aria-expanded="true" aria-controls="heading_${i}" style="">
            <#if requestURI == "viewAccount">
            ${accountContactAssoc.name!} (${accountContactAssoc.contactId!})
            <#elseif requestURI == "viewContact">
            ${accountContactAssoc.companyName!} (${accountContactAssoc.accountId!})
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
                              <i class="fa fa-plus btn btn-xs btn-primary" aria-hidden="true" data-toggle="modal" data-target="#emailModal" onclick=findEmail("emailtableid_${i}","${accountContactAssoc.contactId!}","${accountContactAssoc.accountId!}","${accountContactAssoc.partyRelAssocId!}");></i>
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
                              <i class="fa fa-plus btn btn-xs btn-primary" aria-hidden="true" data-toggle="modal" data-target="#phoneModal" onclick=findPhone("phonetableid_${i}","${accountContactAssoc.contactId!}","${accountContactAssoc.accountId!}","${accountContactAssoc.partyRelAssocId!}");></i>
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
                              <i class="fa fa-plus btn btn-xs btn-primary" aria-hidden="true" data-toggle="modal" data-target="#designationModal" onclick=findDesignation("designationtableid_${i}","${accountContactAssoc.contactId!}","${accountContactAssoc.accountId!}","${accountContactAssoc.partyRelAssocId!}");></i>
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
		   	var contactPartyId = "${accountContactAssoc.contactId!}";
		   	var acctPartyId = "${accountContactAssoc.accountId!}";
		   	var partyRelAssocId = "${accountContactAssoc.partyRelAssocId!}";
		   	
		   	emailTableAppend("emailtableid_${i}", contactPartyId, acctPartyId, partyRelAssocId);
	   		phoneTableAppend("phonetableid_${i}", contactPartyId, acctPartyId, partyRelAssocId);
	   		designationTableAppend("designationtableid_${i}", contactPartyId, acctPartyId, partyRelAssocId);
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
   
   function emailTableAppend(id, contactPartyId, accountPartyId, partyRelAssocId) {
       $("#" + id).DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactAndAcctAssocDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
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
                          editButton = '<i class="fa fa-edit btn btn-xs btn-primary" id="'+row.id+'" aria-hidden="true" onclick=updateEmailTD("'+id+'","'+row.contactMechId+'","'+row.id+'","'+contactPartyId+'","'+accountPartyId+'","'+partyRelAssocId+'");></i>';
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
   
   function phoneTableAppend(id, contactPartyId, accountPartyId, partyRelAssocId) {
       $("#" + id).DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactAndAcctAssocDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
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
                          editButton = '<i class="fa fa-edit btn btn-xs btn-primary" id="'+row.id+'" aria-hidden="true" onclick=updatePhoneTD("'+id+'","'+row.contactMechId+'","'+row.id+'","'+contactPartyId+'","'+accountPartyId+'","'+partyRelAssocId+'");></i>';
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
   
   function designationTableAppend(id, contactPartyId, accountPartyId, partyRelAssocId) {
       $("#" + id).DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactAndAcctAssocDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
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
                       data = '<i class="fa fa-edit btn btn-xs btn-primary" id="'+row.id+'" aria-hidden="true" onclick=updateDesignationTD("'+id+'","'+row.designationId+'","'+row.id+'","'+contactPartyId+'","'+accountPartyId+'","'+partyRelAssocId+'");></i>';
                       return data;
                   }
               },
           ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
   }
   
   function findEmail(id, contactPartyId, accountPartyId, partyRelAssocId) {
       $("#ajaxEmailDatatable").DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactsDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
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
                       data = '<a href="#" onclick=addEmail("' + id + '","' + row.contactPartyId + '","' + row.accountPartyId + '","' + row.contactMechId + '","' + row.partyRelAssocId + '")>' + data + '</a>';
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
   
   function findPhone(id, contactPartyId, accountPartyId, partyRelAssocId) {
       $("#ajaxPhoneDatatable").DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactsDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
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
                       data = '<a href="#" onclick=addPhone("' + id + '","' + row.contactPartyId + '","' + row.accountPartyId + '","' + row.contactMechId + '","' + row.partyRelAssocId + '")>' + data + '</a>';
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
   
   function findDesignation(id, contactPartyId, accountPartyId, partyRelAssocId) {
       $("#ajaxDesignationDatatable").DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getContactsDetails",
               "type": "POST",
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
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
                   data = '<a href="#" onclick=addDesignation("' + id + '","' + row.contactPartyId + '","' + row.accountPartyId + '","' + row.designationId + '","' + row.partyRelAssocId + '")>' + data + '</a>';
                   return data;
               }
           }, ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
   }
   
   function addEmail(id, contactPartyId, accountPartyId, contactMechId, partyRelAssocId) {
       $('#emailModal').modal('hide');
       if (id != null && id != "" && contactPartyId != null && contactPartyId != "" && accountPartyId != null && accountPartyId != "" && contactMechId != null && contactMechId != "" && partyRelAssocId != null && partyRelAssocId != "") {
           jQuery.ajax({
               url: "addContactsToAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
                   "contactMechId": contactMechId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "EMAIL"
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Email successfully added");
                   emailTableAppend(id, contactPartyId, accountPartyId, partyRelAssocId);
               }
           });
       }
   }
   
   function addPhone(id, contactPartyId, accountPartyId, contactMechId, partyRelAssocId) {
       $('#phoneModal').modal('hide');
       if (id != null && id != "" && contactPartyId != null && contactPartyId != "" && accountPartyId != null && accountPartyId != "" && contactMechId != null && contactMechId != "" && partyRelAssocId != null && partyRelAssocId != "") {
           jQuery.ajax({
               url: "addContactsToAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
                   "contactMechId": contactMechId,
                   "partyRelAssocId": partyRelAssocId,
                   "contactType": "PHONE"
               },
               error: function(msg) {
                   showAlert("error", msg);
               },
               success: function(msg) {
                   showAlert("success", "Phone successfully added");
                   phoneTableAppend(id, contactPartyId, accountPartyId, partyRelAssocId);
               }
           });
       }
   }
   
   function addDesignation(id, contactPartyId, accountPartyId, designationId, partyRelAssocId) {
       $('#designationModal').modal('hide');
       if (id != null && id != "" && contactPartyId != null && contactPartyId != "" && accountPartyId != null && accountPartyId != "" && designationId != null && designationId != "" && partyRelAssocId != null && partyRelAssocId != "") {
           jQuery.ajax({
               url: "addContactsToAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
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
   
   function updateEmailTD(id, contactMechId, rowId, contactPartyId, accountPartyId, partyRelAssocId) {
      var row = $("#"+rowId).closest('tbody tr');
      var solicitation = row.find("#solicitationTD").text();
      var infoString = row.find("#infoStringTD").text();
      
      if((solicitation != null && solicitation != "") || (infoString != null && infoString != "")) {
         $("#"+rowId).removeClass().addClass("fa fa-check-square-o btn btn-xs btn-success");
         $("#"+rowId).attr("onclick","updateEmail('"+id+"','"+contactMechId+"','"+rowId+"','"+contactPartyId+"','"+accountPartyId+"','"+partyRelAssocId+"')");
   
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
   
   function updateEmail(id, contactMechId, rowId, contactPartyId, accountPartyId, partyRelAssocId) {
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
               url: "updateAccountContactAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
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
                   emailTableAppend(id, contactPartyId, accountPartyId, partyRelAssocId);
               }
             });
          }
   }
   
   function updatePhoneTD(id, contactMechId, rowId, contactPartyId, accountPartyId, partyRelAssocId) {
      var row = $("#"+rowId).closest('tbody tr');
      var solicitation = row.find("#solicitationTD").text();
      var contactNumber = row.find("#contactNumberTD").text();
      
      if((solicitation != null && solicitation != "") || (contactNumber != null && contactNumber != "")) {
         $("#"+rowId).removeClass().addClass("fa fa-check-square-o btn btn-xs btn-success");
         $("#"+rowId).attr("onclick","updatePhone('"+id+"','"+contactMechId+"','"+rowId+"','"+contactPartyId+"','"+accountPartyId+"','"+partyRelAssocId+"')");
   
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
   
   function updatePhone(id, contactMechId, rowId, contactPartyId, accountPartyId, partyRelAssocId) {
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
               url: "updateAccountContactAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
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
                   phoneTableAppend(id, contactPartyId, accountPartyId, partyRelAssocId);
               }
             });
          }
   }
   
   function updateDesignationTD(id, designationId, rowId, contactPartyId, accountPartyId, partyRelAssocId) {
      var row = $("#"+rowId).closest('tbody tr');
      var designationName = row.find("#designationNameTD").text();
      
      if(designationName != null && designationName != "") {
         $("#"+rowId).removeClass().addClass("fa fa-check-square-o btn btn-xs btn-success");
         $("#"+rowId).attr("onclick","updateDesination('"+id+"','"+designationId+"','"+rowId+"','"+contactPartyId+"','"+accountPartyId+"','"+partyRelAssocId+"')");
      }
      if(designationName != null && designationName != "") {
         row.find("#designationNameTD").html("").append("<input type='text' id='designationName' name='designationName' value=\""+designationName+"\" class='form-control input-sm'>");
      }
   }
   
   function updateDesination(id, designationId, rowId, contactPartyId, accountPartyId, partyRelAssocId) {
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
               url: "updateAccountContactAssoc",
               type: 'POST',
               data: {
                   "contactPartyId": contactPartyId,
                   "accountPartyId": accountPartyId,
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
                   designationTableAppend(id, contactPartyId, accountPartyId, partyRelAssocId);
               }
             });
          }
   }
</script>
