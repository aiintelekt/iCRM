<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.dataTables.min.js"></script>
<#-- 
<div class="page-header border-b">
	<h2>Find Customers</h2>
</div>
 -->
<div class="card-header mt-2 mb-3">
   <form method="post" action="#" id="searchContant" class="form-horizontal" name="searchContant" novalidate="novalidate" data-toggle="validator">
      <div class="row">
      	<div class="col-md-2 col-sm-2">
      		<@simpleDropdownInput 
				id="searchRoleTypeId"
				options=roleTypeList
				required=false
				allowEmpty=true
				emptyText = "Role"
				dataLiveSearch=true
				/>	
			
		</div>
         <div class="col-md-2 col-sm-2">
         	<@simpleInput 
				id="searchPartyId"
				placeholder="Customer ID"
				tooltip = "Customer ID"
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleInput 
				id="searchFirstName"
				placeholder="Name"
				tooltip = "Name"
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleInput 
				id="searchEmailId"
				placeholder="Email Address"
				tooltip = "Email Address"
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleInput 
				id="searchPhoneNum"
				placeholder="Phone Number"
				tooltip = "Phone Number"
				/>
         </div>
         
         <@fromSimpleAction id="find-customer-button" showCancelBtn=false isSubmitAction=false submitLabel="Find Customers"/>
        	
      </div>
   </form>
   <div class="clearfix"> </div>
</div>

<input type="hidden" id="customFieldId" value="${customFieldId!}"/>
<input type="hidden" id="groupId" value="${groupId!}"/>

<div class="clearfix"> </div>
<div class="page-header mt-2 mb-2 nav-tabs">
	<h2 class="float-left ml-1">Customer List </h2>
	<div class="float-right">
		<input class="btn btn-xs btn-primary mt-2 mr-1" id="add-selected-customer-button" value="Add Selected Customers" type="button">
	</div>
</div>

<div class="table-responsive">
	<table id="customer-list" class="table table-striped">
		<thead>
			<tr>
				<th>Customer ID</th>
                <th>Name</th>
                <th>Status</th>
                <th>City</th>
                <th>State</th>
                <th>Phone Number</th>
                <th>Email Address</th>
                <th><div class="ml-1"><input id="add-select-all" type="checkbox"></div></th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<script>

jQuery(document).ready(function() {   

$("#add-select-all").change(function(){  
    var status = this.checked; 
    $('input[name="customers"]').each(function(){ 
        this.checked = status; 
    });
});

$('#find-customer-button').on('click', function(){
	findCustomers();
});

$('#add-selected-customer-button').on('click', function(){
	
	var rowsSelected = [];
			
	$('input[name="customers"]:checked').each(function() {
		//alert(this.value);
   		console.log(this.value);
   		
   		rowsSelected.push(this.value);
   		
	});
	
	if (rowsSelected.length == 0) {
		showAlert ("error", "Please select customers to be add");
		return;
	}	
		
	var customFieldId = $('#customFieldId').val();
	var groupId = $('#groupId').val();
	
	$.ajax({
		      
		type: "POST",
     	url: "addSelectedSegmentCustomer",
        data:  {"customFieldId": customFieldId, "groupId": groupId, "rowsSelected": rowsSelected},
        success: function (data) {   
            
            if (data.code == 200) {
				//showAlert ("success", "success count: "+data.successCount+", already exists count: "+data.alreadyExistsCount);
				showAlert ("success", data.successCount + " customers successfully added!");
				findCustomers();
            	findSelectedCustomers();
			} else {
				showAlert ("error", data.message);
			}  
			    	
        }
        
	});
		
});

});

findCustomers();
function findCustomers() {
	
	var searchPartyId = $("#searchPartyId").val();
   	var searchFirstName = $("#searchFirstName").val();
   	var searchEmailId = $("#searchEmailId").val();
   	var searchPhoneNum = $("#searchPhoneNum").val();
   	var searchRoleTypeId = $("#searchRoleTypeId").val();
   	
   	var groupId = $("#groupId").val();
   	var customFieldId = $('#customFieldId').val();
   	
   	var url = "searchCustomers?searchPartyId="+searchPartyId+"&searchFirstName="+searchFirstName+"&searchEmailId="+searchEmailId+"&searchPhoneNum="+searchPhoneNum+"&groupId="+groupId+"&customFieldId="+customFieldId+"&searchRoleTypeId="+searchRoleTypeId;
   
	$('#customer-list').DataTable( {
		    "processing": true,
		    "serverSide": true,
		    "destroy": true,
		    "autoWidth": false,
		    "ajax": {
	            "url": url,
	            "type": "POST",
	            "async": true
	        },
	        "pageLength": 20,
	        "stateSave": true,
	        
	        "columnDefs": [ 
	        	{
					"targets": 7,
					"orderable": false
				} 
			],
					      
	        "columns": [
					        	
	            { "data": "partyId" },
	            { "data": "name" },
	            { "data": "statusId" },
	            { "data": "city" },
	            { "data": "state" },
	            { "data": "phoneNumber" },
	            { "data": "infoString" },
	            
	            
	        	{ "data": "partyId",
		          "render": function(data, type, row, meta){
		            if(type === 'display'){
		                data = '<div class="ml-1"><input type="checkbox" name="customers" value="' + row.partyId + '"></div>';
		            }
		            return data;
		         }
		      	},
	            
	        ],
	        "initComplete": function(settings, json) {
			    resetDefaultEvents();
			}
		});
}

</script>
