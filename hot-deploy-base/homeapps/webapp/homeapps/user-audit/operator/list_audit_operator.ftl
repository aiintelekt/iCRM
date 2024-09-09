<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros_dbs.ftl"/>
<#-- <@sectionFrameHeader title="${uiLabelMap.ListOf} ${uiLabelMap.auditOperator}"/> -->
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
    <div class="page-header border-b pt-2">
        <h2 class="float-left"> ${uiLabelMap.ListOf} ${uiLabelMap.auditOperator} </h2>
        <a id="apply-button" title="Apply" href="#" class="btn btn-primary btn-xs ml-2"><i class="fa fa-check-circle" aria-hidden="true"></i> Apply</a>
        <a id="remove-button" title="Remove" href="#" class="btn btn-primary btn-xs ml-2"><i class="fa fa-times" aria-hidden="true"></i> Remove</a>
        <div class="clearfix"></div>
    </div>
    <div class="table-responsive">
        <table class="table table-hover" id="list_audit_operator">
            <thead>
                <tr>
                    <th>${uiLabelMap.oneBankId!}</th>
                    <th>${uiLabelMap.userStatus!}</th>
                    <th>${uiLabelMap.createdOn!}</th>
                    <th>${uiLabelMap.modifiedOn!}</th>
                    <th>${uiLabelMap.operatorType!}</th>
                    <th>${uiLabelMap.isMaker!}</th>
                    <th>${uiLabelMap.isChecker!}</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>
</div>
</div>
<script type="text/javascript">

jQuery(document).ready(function() {		

$('#find-auditOperator-button').on('click', function(){

	findAuditOperators();

});
	
$('#apply-button').on('click', function(){
		
	var rowsSelectedMaker = [];
	var rowsSelectedCheker = [];
			
	$('input[name="selected-isMaker"]:checked').each(function() {
   		rowsSelectedMaker.push(this.value);
	});
	
	$('input[name="selected-isCheker"]:checked').each(function() {
   		rowsSelectedCheker.push(this.value);
	});
	
	$.ajax({
		      
		type: "POST",
     	url: "applyUserAuditOperator",
        data:  {"rowsSelectedMaker": rowsSelectedMaker, "rowsSelectedCheker": rowsSelectedCheker},
        success: function (data) {   
        
			if (data.code == 200) {
				showAlert ("success", "apply count: "+data.successCount);
            	findAuditOperators();
			} else {
				showAlert ("error", data.message);
			}           
						    	
        }
        
	});
	
});

$('#remove-button').on('click', function(){
		
	var rowsSelectedMaker = [];
	var rowsSelectedCheker = [];
			
	$('input[name="selected-isMaker"]:checked').each(function() {
   		rowsSelectedMaker.push(this.value);
	});
	
	$('input[name="selected-isCheker"]:checked').each(function() {
   		rowsSelectedCheker.push(this.value);
	});
	
	$.ajax({
		      
		type: "POST",
     	url: "removeUserAuditOperator",
        data:  {"rowsSelectedMaker": rowsSelectedMaker, "rowsSelectedCheker": rowsSelectedCheker},
        success: function (data) {   
        
			if (data.code == 200) {
				showAlert ("success", "remove count: "+data.successCount);
            	findAuditOperators();
			} else {
				showAlert ("error", data.message);
			}           
						    	
        }
        
	});
	
});

/*
$("#remove-multivalue-select-all").change(function(){  
    var status = this.checked; 
    $('input[name="selected-multivalues"]').each(function(){ 
        this.checked = status; 
    });
});	
*/
		
findAuditOperators();
function findAuditOperators() {
	
	var operatorType = $("#operatorType").val();
	var userLoginId = $("#userLoginId").val();
	var userStatus = $("#userStatus").val();
		   	
   	//var url = "searchChargeCodes?searchCondition="+searchCondition+"&searchConditionOpertor="+searchConditionOpertor+"&searchValue="+searchValue;
    var url = "searchUserAuditOperator";
   
	$('#list_audit_operator').DataTable( {
		    "processing": true,
		    "serverSide": true,
		    "searching": false,
		    "destroy": true,
		    "ajax": {
	            "url": url,
	            "type": "POST",
	            "data": {
                   "operatorType": operatorType,
                   "userLoginId": userLoginId,
                   "userStatus": userStatus,
                }
	        },
	        "pageLength": 20,
	        "stateSave": false,
	        	      
	        "columns": [
					        	
	            { 
	            	"data": "userLoginId", 
	            	"orderable": false
	            },
	            { "data": "enabled",
		          "render": function(data, type, row, meta){
		          	var data = 'Active';
		          	
		            if(row.enabled === 'N'){
		                data = 'InActive';
		            }
		            
		            return data;
		          }
		         },
		         
		         { 
	            	"data": "createdStamp", 
	            	"orderable": false
	            },
	            { 
	            	"data": "lastUpdatedStamp", 
	            	"orderable": false
	            },
	            
	            { 
	            	"data": "operatorType", 
	            	"orderable": false
	             },
	            
	            { "data": "userLoginId",
		          "render": function(data, type, row, meta){
		            if(type === 'display'){
		                data = '<div class="ml-1"><input type="checkbox" name="selected-isMaker" value="' + row.userLoginId + '"></div>';
		            }
		            return data;
		         }
		      	},
		      	
		      	{ "data": "userLoginId",
		          "render": function(data, type, row, meta){
		            if(type === 'display'){
		                data = '<div class="ml-1"><input type="checkbox" name="selected-isCheker" value="' + row.userLoginId + '"></div>';
		            }
		            return data;
		         }
		      	},
		         
	            /*
	            { "data": "productId",
		          "render": function(data, type, row, meta){
		          	var data = '<div class="text-center ml-1" >';
		          	
		            if(type === 'display'){
		                data += '<a class="btn btn-xs btn-secondary tooltips" href="chargeCodeUpdate?productId='+row.productId+'" data-original-title="Details" ><i class="fa fa-pencil-square-o"></i></a>';
		            }
		            
		            data += "</div>";
		            return data;
		          }
		         },*/     
	        ],
	        "fnDrawCallback": function( oSettings ) {
	      		resetDefaultEvents();
	    	}
		});
}	
	
});	
	
</script>