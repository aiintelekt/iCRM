<#assign requestURI = "viewContact"/>
<#assign roleTypeIdFrom = "CONTACT"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#assign roleTypeIdFrom = "LEAD"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#assign roleTypeIdFrom = "ACCOUNT"/>
</#if>

<!-- Find parent account pop for create and update account-->
<div id="teamMemberModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Find Team Members</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="card-header">
               <form method="post" action="#" id="FindTeamMembers" class="form-horizontal" name="FindTeamMembers" novalidate="novalidate" data-toggle="validator">
                  <div class="row">
                     <div class="col-md-2 col-sm-2">
                        <div class="form-group row mr">
                           <input type="text" class="form-control input-sm" id="firstName" name="firstName" value="" placeholder="First Name">
                        </div>
                     </div>
                     <div class="col-md-2 col-sm-2">
                        <div class="form-group row mr">
                           <input type="text" class="form-control input-sm" id="lastName" name="lastName" placeholder="Last Name">
                        </div>
                     </div>
                     <div class="col-md-1 col-sm-1">
                        <button type="button" class="btn btn-sm btn-primary navbar-dark m5" onclick="javascript:getTeamMembersPRF();">Find Team Members</button>
                     </div>
                  </div>
               </form>
               <div class="clearfix"> </div>
            </div>
            <div class="clearfix"> </div>
            <div class="page-header">
               <h2 class="float-left">Team Members</h2>
            </div>
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
   <input type="hidden" name="roleTypeIdFrom" value="${roleTypeIdFrom?if_exists}"/>
   <input type="hidden" name="accountPartyId" value="" id="accountPartyId"/>
</form>
<script>
   $(document).ready(function(){
    $('#firstName').val("");
    $("#lastName").val("");
    getTeamMembersPRF();
   });
   
   function reassignParty(value) {
     if(value != null && value != "") {
        document.personResponsibleParty.accountPartyId.value=value;
        document.personResponsibleParty.submit();
     }
   }
         
   function getTeamMembersPRF(){
   var firstName = $("#firstName").val();
   var lastName = $("#lastName").val();
   var url = "getTeamMembers?firstName="+firstName+"&lastName="+lastName;
   $('#ajaxFindTeamMembersdatatablePRF').DataTable( {
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
            "bAutoWidth":false,
            "stateSave": true,
            "columns": [
               { "data": "partyId",
   	            "render": function(data, type, row, meta){
   	            data = '<a href="#" onclick=reassignParty("'+data+'")>'+row.name+'('+data+')</a>';
   	            return data;
   	         }
   	      	},
   	      	
             ]
   	});
   }
   
</script>