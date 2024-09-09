<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div role="main" class="pd-btm-title-bar">
<div class="">

<div class="">
	<h2>Find Parties</h2>
</div>

   <form method="post" action="#" id="searchContant" class="form-horizontal" name="searchContant" novalidate="novalidate" data-toggle="validator">
      <div class="margin-adj-accordian"> 
	      <div class="row">
	      	<#-- <div class="col-md-4 col-lg-4 col-sm-12">
	      		<@dropdownCell 
					id="searchRoleTypeId"
					options=roleTypeList
					required=false
					allowEmpty=true
					placeholder="Role"
					dataLiveSearch=true
					/>	
			</div> -->
	         <div class="col-lg-4 col-md-6 col-sm-12">
	         	<@inputCell 
					id="searchPartyId"
					placeholder="Party ID"
					tooltip = "Party ID"
					/>
					<@inputCell 
					id="searchPhoneNum"
					placeholder="Phone Number"
					tooltip = "Phone Number"
					/>
	         </div>
	         <div class="col-lg-4 col-md-6 col-sm-12">
	         	<@inputCell 
					id="searchFirstName"
					placeholder="Name"
					tooltip = "Name"
					/>
	         </div>
	         <div class="col-lg-4 col-md-6 col-sm-12">
	         	<@inputCell 
					id="searchEmailId"
					placeholder="Email Address"
					tooltip = "Email Address"
					/>
					 <div class="float-right" >
		         	<@button
			        id="find-assigned-customer-button"
			        label="${uiLabelMap.Find}"
			        />
			     	<@reset
					label="${uiLabelMap.Reset}"
					/>
		         </div>
	         </div>
	        
		        
	      </div>
		</div>
   </form>

</div>
</div>

<input type="hidden" id="customFieldId" value="${customFieldId!}"/>
<input type="hidden" id="groupId" value="${groupId!}"/>

<div role="main" class="pd-btm-title-bar">
<div class="">

<div class="">
  <h2 class="d-inline-block">Assigned Party List</h2>
   <ul class="flot-icone">
     <li class="mt-0">
     <input class="btn btn-xs btn-danger" id="remove-selected-customer-button" value="Remove Selected Parties" type="button" data-toggle="confirmation" title="Are you sure to Remove ?">
     </li>
  </ul>
</div>

<div class="table-responsive">
	<table id="selected-customer-list" class="table table-striped">
		<thead>
			<tr>
				<th>Party ID</th>
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

</div>
</div>
<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.dataTables.min.js"></script>
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
				showAlert ("success", "Assigned party removed successfully, count: "+data.successCount);
            	findSelectedCustomers();
			} else {
				showAlert ("error", data.message);
			}           
						    	
        }
        
	});
	
});

findSelectedCustomers();

});

function findSelectedCustomers() {
	
	var customFieldId = $('#customFieldId').val();
	var groupId = $('#groupId').val();
	
   	var searchPartyId = $("#searchPartyId").val();
   	var searchFirstName = $("#searchFirstName").val();
   	var searchEmailId = $("#searchEmailId").val();
   	var searchPhoneNum = $("#searchPhoneNum").val();
   	//var searchRoleTypeId = $("#searchRoleTypeId").val();
   	
   	var url = "searchSegmentCustomers?searchPartyId="+searchPartyId+"&searchFirstName="+searchFirstName+"&searchEmailId="+searchEmailId+"&searchPhoneNum="+searchPhoneNum+"&groupId="+groupId+"&customFieldId="+customFieldId;
	
	var actionColumnIndex = 9;   
	<#-- <#if customFieldGroup.groupType?has_content && customFieldGroup.groupType == "ECONOMIC_METRIC">
		actionColumnIndex = 8;
	</#if> -->
	         
	$('#selected-customer-list').DataTable( {
		    "processing": true,
		    "serverSide": true,
		    "destroy": true,
		    "autoWidth": false,
		    "searching": false,
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
	        	
	            { "data": "nameWithIdLink" },
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
