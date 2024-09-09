<div class="page-header">
   <h2 class="float-left">Loan Details</h2>   
</div>
<div class="table-responsive">
	<table id="caLoan-list" class="table table-striped">
		<thead>
			<tr>
			<th>Trade Limit</th>
            <th>Trade Limit Expiry</th>
            <th>Current Trade Utilisation</th>
            <th>Loan OutStanding Amount</th>
            <th>Loan Facility Limit</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<script>


function findcaLoanDetails() {
	
	var searchPartyId = $("#partyId").val();
   	
   	var url = "searchHdaFacilities?searchPartyId="+searchPartyId;
   
	$('#caLoan-list').DataTable( {
		    "processing": true,
		    "serverSide": true,
		    "destroy": true,
		    "ajax": {
	            "url": url,
	            "type": "POST"
	        },
	        "pageLength": 10,
	        "stateSave": true,
	        /*
	        "columnDefs": [ 
	        	{
					"targets": 7,
					"orderable": false
				} 
			],
			*/		      
	        "columns": [
					        	
	            
	            { "data": "facId" },
	            { "data": "facId" },
	            { "data": "facId" },
	            { "data": "facId" },
	            { "data": "facId" },
	            
	           
	        ],
	        "initComplete": function(settings, json) {
			    resetDefaultEvents();
			}
		});
}

</script>
