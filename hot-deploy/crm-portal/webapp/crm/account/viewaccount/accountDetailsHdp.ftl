
<div class="page-header">
   <h2 class="">Current Ledger Balance</h2>   
</div>
<div class="table-responsive">
	<table id="caAccount-list" class="table table-striped">
		<thead>
			<tr>
			<th>Account Number</th>
            <th>Currency</th>
            <th>Amount</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<script>




function findCaAccounts() {
	
	var searchPartyId = $("#partyId").val();

   	var url = "searchHdaCaAccouts?searchPartyId="+searchPartyId;
   
	$('#caAccount-list').DataTable( {
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
					        	
	            
	            { "data": "acctNum" },
	            { "data": "acctCcyCde" },
	            { "data": "acctCcyAmt" },
	            
	           
	        ],
	        "initComplete": function(settings, json) {
			    resetDefaultEvents();
			}
		});
}

</script>


<div class="page-header">
   <h2 class="">Overdraft Facility Limit</h2>   
</div>
<div class="table-responsive">
   <table class="table table-striped" id="hda-facilities-list">
      <thead>
         <tr>
            <th>Activated Limit</th>
            <th>Currency</th>
            <th>Description</th>
         </tr>
      </thead>
      <tbody>
        <#-- <#if hdpFacility?has_content>
           <#list hdpFacility as hdpFacility>
           <tr>
              <td> ${hdpFacility.activatedLimit?if_exists} </td>
              <td> ${hdpFacility.currency?if_exists}</td>
              <td> ${hdpFacility.desc?if_exists}</td>
              </tr>
           </#list>
         </#if>-->
      </tbody>
   </table>
</div>


<script >

function findHdaFacilities() {
	
	var searchPartyId = $("#partyId").val();
   	
   	//var fromDate = $('#findFacilitiesForm input[name="fromDate"]').val();
	//var thruDate = $('#findFacilitiesForm input[name="thruDate"]').val();
   	
   	var url = "searchHdaFacilities?searchPartyId="+searchPartyId;
   
	$('#hda-facilities-list').DataTable( {
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
					        	
	            { "data": "activatedLimGrpCcy" },
	            { "data": "facCcy" },
	            { "data": "facDesc" },
	           
	         	        ],
	        "initComplete": function(settings, json) {
			    resetDefaultEvents();
			}
		});
}
  
  </script>

