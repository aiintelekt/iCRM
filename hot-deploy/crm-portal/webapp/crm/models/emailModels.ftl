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


<div id="customerModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.findEmail}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="card-header">
	         <form method="post" action="#" id="accountList" class="form-horizontal" name="accountList" novalidate="novalidate" data-toggle="validator">
	            <div class="row">
	               <div class="col-md-2 col-sm-2">
	                  <div class="form-group row mr">
	                     <input type="text" class="form-control input-sm" id="customerId" name="customerId" value="${customerId?if_exists}" placeholder="${uiLabelMap.customerId}">
	                  </div>
	               </div>
	               <div class="col-md-2 col-sm-2">
	                  <div class="form-group row mr">
	                     <input type="text" class="form-control input-sm" id="customerName" name="customerName" placeholder="${uiLabelMap.customerName}">
	                  </div>
	               </div>
	               <div class="col-md-2 col-sm-2">
	                  <div class="form-group row mr">
	                     <input type="text" class="form-control input-sm" id="emailId" name="emailId" placeholder="${uiLabelMap.email}">
	                  </div>
	               </div>
	               <div class="col-md-1 col-sm-1">
	                  <button type="button" class="btn btn-sm btn-primary navbar-dark m5" onclick="javascript:findEmail();">${uiLabelMap.findEmail}</button>
	               </div>
	            </div>
	         </form>
	         <div class="clearfix"> </div>
	      </div>
	      <div class="clearfix"> </div>
	      <div class="page-header">
	         <h2 class="float-left">${uiLabelMap.emailList}</h2>
	      </div>
	      <div class="table-responsive">
	         <table id="ajaxdatatable" class="table table-striped">
	            <thead>
	               <tr>
	                  <th>${uiLabelMap.customerId}</th>
	                  <th>${uiLabelMap.customerName}</th>
	                  <th>${uiLabelMap.email}</th>
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
$('#customerModal').on('click', '.parentSet', function(){
    var value = $(this).children("span").attr("value");
    $('#parentPartyId').val(value);
    $('#parentAccountModal').modal('hide');
});

$('#findAccount').click(function(){
	$('#customerId').val("");
	$("#customerName").val("");
	$("#emailId").val("");
	findAccounts();
});

function findEmail(){
var customerId = $("#customerId").val();
var customerName = $("#customerName").val();
var emailId = $("#emailId").val();
var url = "getCustomerEmail?customerId="+customerId+"&customerName="+customerName+"&emailId="+emailId;
$('#ajaxdatatable').DataTable( {
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
	            if(type === 'display'){
	                data = '<a href="#" class="parentSet"><span id="parAccId_'+row.id+'" name="parAccId_'+row.id+'" value ="' + data + '"></span>'+data+'</a>';
	            }
	            return data;
	         }
	      	},
	      	{ "data": "groupName" },
            { "data": "statusId" },
            { "data": "city" },
            { "data": "state" },
            { "data": "phoneNumber" },
            { "data": "infoString" }
          ]
	});
}

</script>