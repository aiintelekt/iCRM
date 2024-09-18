<!-- Reassign the person Responsible-->
<div id="reassign1" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Modal Header</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <p>Some text in the modal.</p>
         </div>
         <div class="modal-footer">
            <button type="reset" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
         </div>
      </div>
   </div>
</div>

<!-- Find parent account pop for create and update account-->

<div id="parentAccountModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.findAccounts!}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="card-header">
	         <form method="post" action="#" id="accountList" class="form-horizontal" name="accountList" novalidate="novalidate" data-toggle="validator">
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
	               <#--<div class="col-md-2 col-sm-2">
	                  <div class="form-group row mr">
	                     <input type="text" class="form-control input-sm" id="searchCompanyName" name="searchCompanyName" placeholder="Company Name">
	                  </div>
	               </div> -->
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
	                  <button type="button" class="btn btn-sm btn-primary navbar-dark m5" onclick="javascript:findAccounts();">Find Accounts</button>
	               </div>
	            </div>
	         </form>
	         <div class="clearfix"> </div>
	      </div>
	      <div class="clearfix"> </div>
	      <div class="page-header">
	         <h2 class="float-left">Accounts List </h2>
	      </div>
	      <div class="table-responsive">
	         <table id="ajaxdatatable" class="table table-striped">
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
<script>
$(document).ready(function() {
   findAccounts();
});

$('#parentAccountModal').on('click', '.parentSet', function(){
    var value = $(this).children("span").attr("value");
    $('#parentCoDetails').val(value);
    $('#parentAccountModal').modal('hide');
});

$('#findAccount').click(function(){
	$('#parentPartyId').val("");
	$("#accountSearchPartyId").val("");
	$("#searchGroupName").val("");
	$("#searchEmailId").val("");
	$("#searchPhoneNum").val("");
	findAccounts();
});

function findAccounts(){
var accountSearchPartyId = $("#accountSearchPartyId").val();
var searchGroupName = $("#searchGroupName").val();
var searchEmailId = $("#searchEmailId").val();
var searchPhoneNum = $("#searchPhoneNum").val();
var url = "searchAccounts?accountSearchPartyId="+accountSearchPartyId+"&searchGroupName="+searchGroupName+"&searchEmailId="+searchEmailId+"&searchPhoneNum="+searchPhoneNum;
$('#ajaxdatatable').DataTable( {
	    "processing": true,
	    "serverSide": true,
	    "destroy": true,
	    "ordering": false,
	    "filter" : false,
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
	            if(type === 'display'){
	                data = '<a href="#" class="parentSet"><span id="parAccId_'+row.id+'" name="parAccId_'+row.id+'" value ="' + data + '"></span>'+row.groupName+'('+data+')</a>';
	            }
	            return data;
	         }
	      	},
            { "data": "statusId" },
            { "data": "city" },
            { "data": "state" },
            { "data": "phoneNumber" },
            { "data": "infoString" }
          ]
	});
}

</script>