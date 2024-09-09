<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<#-- 
<div class="page-header border-b">
	<h2>Find Customers</h2>
</div>
 -->
<div class="card-header mt-2 mb-3">
   <form method="post" action="#" id="findLoanAccountForm" class="form-horizontal" name="findLoanAccountForm" novalidate="novalidate" data-toggle="validator">
      <div class="row">
      	<div class="col-md-2 col-sm-2">
      		<@simpleDateInput 
				name="fromDate"
				/>
         </div>
         <div class="col-md-2 col-sm-2">
      		<@simpleDateInput 
				name="thruDate"
				/>
         </div>
         
         <@fromSimpleAction id="find-loanAccount-button" showCancelBtn=false isSubmitAction=false submitLabel="Find"/>
        	
      </div>
   </form>
   <div class="clearfix"> </div>
</div>
<#-- 
<input type="hidden" id="customFieldId" value="${customFieldId!}"/>
<input type="hidden" id="groupId" value="${groupId!}"/>
 -->
<div class="clearfix"> </div>
<div class="page-header mt-2 mb-2 nav-tabs">
	<h2 class="float-left ml-1">LoanAccountData List </h2>
	<div class="float-right">
		<#-- <input class="btn btn-xs btn-primary mt-2 mr-1" id="add-selected-loanAccount-button" value="Add Selected Customers" type="button"> -->
	</div>
</div>

<div class="table-responsive">
	<table id="hda-loanAccount-list" class="table table-striped">
		<thead>
			<tr>
				<th>lcin</th>
                <th>acctNum</th>
                <th>cntryCde</th>
                <th>businessDt</th>
                <#if loanAccountHeaderConfigs?has_content>
                <#list loanAccountHeaderConfigs as config>
                	<#if config.hdrRmVisible?has_content && config.hdrRmVisible == "Y">
                		<th>
                		<#if config.hdrUiLabel?has_content>
                			${uiLabelMapDM.get(config.hdrUiLabel)}
                		<#else>
                			<#assign hdrName = Static["org.fio.crm.util.DataHelper"].sqlPropToJavaProp( config.hdrName ) />
                			${hdrName!}	
                		</#if>
                		</th>
                	</#if>
                </#list>
                </#if>
                <th>Processed Date</th>
                <#-- <th><div class="ml-1"><input id="add-select-all-loanAccount" type="checkbox"></div></th> -->
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<script>

jQuery(document).ready(function() {   

$("#add-select-all-loanAccount").change(function(){  
    var status = this.checked; 
    $('input[name="customers"]').each(function(){ 
        this.checked = status; 
    });
});

$('#find-loanAccount-button').on('click', function(){
	findHdaLoanAccounts();
});

$('#add-selected-loanAccount-button').on('click', function(){
	
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
	
	/*	
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
	*/
		
});

});

//findHdaLoanAccounts();
function findHdaLoanAccounts() {
	
	var searchPartyId = $("#partyId").val();
	
	var fromDate = $('#findLoanAccountForm input[name="fromDate"]').val();
	var thruDate = $('#findLoanAccountForm input[name="thruDate"]').val();
   	
   	var url = "searchHdaLoanAccounts?searchPartyId="+searchPartyId+"&fromDate="+fromDate+"&thruDate="+thruDate;
   
	$('#hda-loanAccount-list').DataTable( {
		    "processing": true,
		    "serverSide": true,
		    "destroy": true,
		    "ajax": {
	            "url": url,
	            "type": "POST"
	        },
	        "pageLength": 20,
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
					        	
	            { "data": "lcin" },
	            { "data": "acctNum" },
	            { "data": "cntryCde" },
	            { "data": "businessDt" },
	            
	            <#if loanAccountHeaderConfigs?has_content>
                <#list loanAccountHeaderConfigs as config>
                	<#if config.hdrRmVisible?has_content && config.hdrRmVisible == "Y">
                		<#assign hdrName = Static["org.fio.crm.util.DataHelper"].sqlPropToJavaProp( config.hdrName ) />
                		{ "data": "${hdrName!}" },
                	</#if>
                </#list>
                </#if>
	            
	            { "data": "processedTime" },
	            /*
	        	{ "data": "lcin",
		          "render": function(data, type, row, meta){
		            if(type === 'display'){
		                data = '<div class="ml-1"><input type="checkbox" name="ca-accounts" value="' + row.lcin + '"></div>';
		            }
		            return data;
		         }
		      	}
	            */
	        ],
	        "fnDrawCallback": function( oSettings ) {
	      		resetDefaultEvents();
	    	}
		});
}

</script>
