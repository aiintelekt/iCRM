<#-- 
<div class="page-header border-b">
	<h2>Find Customers</h2>
</div>
 -->
 <#-- 
<div class="card-header mt-2 mb-3">
   <form method="post" action="#" id="searchContant" class="form-horizontal" name="searchContant" novalidate="novalidate" data-toggle="validator">
      <div class="row">
      	<div class="col-md-2 col-sm-2">
			<div class="form-group row mr">
				<select class="ui dropdown form-control input-sm tooltips" title="Please select role" id="searchRoleTypeId" name="searchRoleTypeId" >
					<option value="">Please Select Role Type</option>
					
					<#if roleTypeList?has_content>
					<#list roleTypeList.entrySet() as entry>  
      				<option value="${entry.key}"  >${entry.value!}</option>
      				</#list>
					</#if>
					
				</select>
			</div>
		</div>
         <div class="col-md-2 col-sm-2">
            <div class="form-group row mr">
               <input type="text" class="form-control input-sm" name="contactSearchPartyId" id="contactSearchPartyId" placeholder="Customer ID">
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
         <div class="col-md-1 col-sm-1">
            <input type="button" class="btn btn-sm btn-primary mt" id="find-assigned-customer-button" value="Find Customers"/>
         </div>
      </div>
   </form>
   <div class="clearfix"> </div>
</div>
 -->

<input type="hidden" id="customFieldId" value="${customFieldId!}"/>
<input type="hidden" id="groupId" value="${groupId!}"/>

<div class="clearfix"> </div>
<div class="page-header mt-5 mb-2 nav-tabs">
 	<h2 class="float-left ml-1">Assigned Customer List</h2>
  	<div class="float-right">
  		<input class="btn btn-xs btn-danger mt-2 mr-1" id="remove-selected-customer-button" value="Remove Selected Customers" type="button">	
	</div>		
</div>

<div class="table-responsive">
	<table id="selected-customer-list" class="table table-striped">
		<thead>
			<tr>
				<th>Customer ID</th>
                <th>Name</th>
                <th>Status</th>
                <th>City</th>
                <th>State</th>
                <th>Phone Number</th>
                <th>Email Address</th>
                
                <#if customFieldGroup.groupType?has_content && customFieldGroup.groupType == "SEGMENTATION">
                <th>Evaluted Value</th>
                </#if>
                <#if customFieldGroup.groupType?has_content && customFieldGroup.groupType == "ECONOMIC_METRIC">
                <th>Economic Value</th>
                </#if>
                
                <th>Entry Date</th>
                
                <th><div class="ml-1"><input id="remove-select-all" type="checkbox"></div></th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<script>

jQuery(document).ready(function() {  

$("#remove-select-all").change(function(){  
    var status = this.checked; 
    $('input[name="selected-customers"]').each(function(){ 
        this.checked = status; 
    });
});

$('#find-assigned-customer-button').on('click', function(){
	findSelectedCustomers();
});

$('#remove-selected-customer-button').on('click', function(){
		
	var rowsSelected = [];
			
	$('input[name="selected-customers"]:checked').each(function() {
		//alert(this.value);
   		console.log(this.value);
   		
   		rowsSelected.push(this.value);
   		
	});
	
	if (rowsSelected.length == 0) {
		showAlert ("error", "Please select assigned customers to be removed");
		return;
	}	
				
	var customFieldId = $('#customFieldId').val();
	var groupId = $('#groupId').val();
	
	$.ajax({
		      
		type: "POST",
     	url: "removeSelectedSegmentCustomer",
        data:  {"customFieldId": customFieldId, "groupId": groupId, "rowsSelected": rowsSelected},
        success: function (data) {   
        
			if (data.code == 200) {
				showAlert ("success", "remove count: "+data.successCount);
				findCustomers();
            	findSelectedCustomers();
			} else {
				showAlert ("error", data.message);
			}           
						    	
        }
        
	});
	
});

});

findSelectedCustomers();
function findSelectedCustomers() {
	
	var customFieldId = $('#customFieldId').val();
	var groupId = $('#groupId').val();
	
   	var contactSearchPartyId = $("#contactSearchPartyId").val();
   	var searchFirstName = $("#searchFirstName").val();
   	var searchEmailId = $("#searchEmailId").val();
   	var searchPhoneNum = $("#searchPhoneNum").val();
   	//var searchRoleTypeId = $("#searchRoleTypeId").val();
   	
   	var url = "searchSegmentCustomers?contactSearchPartyId="+contactSearchPartyId+"&searchFirstName="+searchFirstName+"&searchEmailId="+searchEmailId+"&searchPhoneNum="+searchPhoneNum+"&groupId="+groupId+"&customFieldId="+customFieldId;
	
	var actionColumnIndex = 9;   
	<#-- <#if customFieldGroup.groupType?has_content && customFieldGroup.groupType == "ECONOMIC_METRIC">
		actionColumnIndex = 8;
	</#if> -->
	         
	$('#selected-customer-list').DataTable( {
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
					"targets": actionColumnIndex,
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
	            
	            <#if customFieldGroup.groupType?has_content && customFieldGroup.groupType == "SEGMENTATION">
	            { "data": "groupActualValue" },
	            </#if>
	            <#if customFieldGroup.groupType?has_content && customFieldGroup.groupType == "ECONOMIC_METRIC">
	            { "data": "propertyValue" },
	            </#if>
	            
	            { "data": "entryDate" },
	            
	            { "data": "partyId",
		          "render": function(data, type, row, meta){
		            if(type === 'display'){
		                data = '<div class="ml-1"><input type="checkbox" name="selected-customers" value="' + row.partyId + '"></div>';
		            }
		            return data;
		         }
		      	},
	            
	        ],
	        "initComplete": function(settings, json) {
			    resetDefaultEvents();
			}
		});
		
	$("#remove-select-all").prop('checked', false);
			
}

</script>
