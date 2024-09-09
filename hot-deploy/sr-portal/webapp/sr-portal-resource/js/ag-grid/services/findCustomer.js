$(function() {
	$('#findcustomer #close').click(function(){
		$('#findCustomerForm input[type=reset]').click();
	});
});

var gridOptions = null;
var columnDefs = [
	{
	    "headerName":"CIF",
	    "field":"cin",
	    "cellEditor":"agTextCellEditor",
	    cellRenderer: function(params) {
	    	return '<a data-dismiss="modal" >' + params.data.cin + '</a>'
		}
	
	},
    {
        "headerName":"Suffix",
        "field":"suffix",
        "cellEditor":"agTextCellEditor",
    },
    {
        "headerName":"Customer Type",
        "field":"customerType",
        "cellEditor":"agTextCellEditor",
    },
    {
        "headerName":"Unique Identifier",
        "field":"uid",
        "cellEditor":"agTextCellEditor",
    },
    {
        "headerName":"Customer Name",
        "field":"name",
        "cellEditor":"agTextCellEditor",
    },
    {
        "headerName":"Email",
        "field":"email",
        "cellEditor":"agTextCellEditor",
    },
    {
        "headerName":"Phone Number",
        "field":"phone",
        "cellEditor":"agTextCellEditor",
    },
    {
        "headerName":"Date of Birth",
        "field":"dob",
        "cellEditor":"agTextCellEditor",
    }
];

function sizeToFit() {
    gridOptions.api.sizeColumnsToFit();
    gridOptions.api.addEventListener('cellClicked', cellClickedHandler);
}

function loadAgGrid(){
	$("#myGrid").empty();
	gridOptions = {
		defaultColDef: {
			filter: true,
			sortable: true,
			resizable: true,
			cellStyle: {color: 'red'}
        },
        columnDefs: columnDefs,
        rowData: getGridData(),
        floatingFilter: true,
        rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 5,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
            sizeToFit();
        }
    }
    var eGridDiv = document.querySelector("#myGrid");
    new agGrid.Grid(eGridDiv, gridOptions);    
}

function getGridData() {
	var errorMessage = null;
    var resultData = null;
    var cin = $("#cin").val();
    var name = $("#name").val();
    var uid = $("#uid").val();
    var cName = $("#cName").val();
    var email = $("#email").val();
    var account = $("#account").val();
    var apNo = $("#apNo").val();
    var phone = $("#phone").val();
    var dob = $("#dob").val();
    var roleTypeId = $("input[name=roleTypeId]:checked").val();
    
    item = {}
    if(cin){
    	item ["cinNumber"] = cin;
    } 
    if(name){
    	item ["name"] = name;
    }
    if(uid){
    	item ["uid"] = uid;
    }
     if(cName){
    item ["cName"] = cName;
    }
     if(email){
    	item ["email"] = email;
    }
     if(account){
    	item ["account"] = account;
    }
     if(apNo){
    	item ["apNo"] = apNo;
    }
    if(phone){
    	item ["phone"] = phone;
    }
    if(dob){
    	item ["dob"] = dob;
    }
    if(roleTypeId){
    	item ["roleTypeId"] = roleTypeId;
    	item ["start"] = 0;
    	item ["length"] = 100;
    }
    if (item != undefined || item.length > 0) {
    	var fromData = JSON.stringify(item);
	    $.ajax({
	        type: "POST",
	        url: "findSRCustomers",
	        async: true,
	        data: JSON.parse(fromData),
	        success: function(data) {
	            var result = data[0];
	            if(result != null || result != undefined){
	                errorMessage = result.errorMessage;
	                resultData = result.errorResult;
	            }
	            if(errorMessage != null || errorMessage != undefined) {
	                showAlert("error", errorMessage);
	            }else{
	                gridOptions.api.setRowData(data);
	            }
	        },
	        error: function() {
	            showAlert("error", "Error occured!");
	        },
	        complete: function() {
	            $('#loader').hide();
	        }
	    }); 
	}
}

function cellClickedHandler(event) {
	$("#cNo").val(event.value);
    $('#myAnchor').parents().find('.card-head').removeClass('d-none');
    $('#custName').parents().find('.card-head').removeClass('d-none');
    var cinNumber = event.value;
    if (cinNumber != undefined && cinNumber != null) {
	    var result = null;
	    
	    $.ajax({
	        type: "POST",
	        url: "getCustomerForAddServiceRequest",
	        async: false,
	        data: {"cinNumber": cinNumber},
	        success: function(data) {
	            result=data[0];
	            $.each(result, function(name, val) {
		            if(name !=null && name != "" && name != 'undefined'){
		            	if(name == "phoneSolicitation"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		if(val == "Y"){
			            			document.querySelector("i#phone").setAttribute('class','fa fa-check fa-1 text-success');
			            			document.querySelector("i#sms").setAttribute('class','fa fa-check fa-1 text-success');
			            		}else{
			            			document.querySelector("i#phone").setAttribute('class','fa fa-times fa-1 text-danger');
			            			document.querySelector("i#sms").setAttribute('class','fa fa-times fa-1 text-danger');
			            		}
		            		}else{
			            		document.querySelector("i#phone").setAttribute('class','fa fa-times fa-1 text-danger');
			            		document.querySelector("i#sms").setAttribute('class','fa fa-times fa-1 text-danger');
			            	}
		            	}
		            	if(name == "emailSolicitation"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		if(val == "Y"){
			            			document.querySelector("i#email").setAttribute('class','fa fa-check fa-1 text-success');
			            		}else{
			            			document.querySelector("i#email").setAttribute('class','fa fa-times fa-1 text-danger');
			            		}
		            		}else{
			            		document.querySelector("i#email").setAttribute('class','fa fa-times fa-1 text-danger');
			            	}
		            	}
		            	if(name == "addressSolicitation"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		if(val == "Y"){
			            			document.querySelector("i#address").setAttribute('class','fa fa-check fa-1 text-success');
			            		}else{
			            			document.querySelector("i#address").setAttribute('class','fa fa-times fa-1 text-danger');
			            		}
		            		}else{
			            		document.querySelector("i#address").setAttribute('class','fa fa-times fa-1 text-danger');
			            	}
		            	}
		            	if(name == "emailAddr"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		$("#mail").text(val);
		            		}else{
			            		$("#mailImg").remove();
			            	}
		            	}
		            	if(name == "phoneNumber"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		 $("#mobile").text(val);
		            		}else{
			            		$("#mobileImg").remove();
			            	}
		            	}
		            	if(name == "firstName"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		 $("#myAnchor").text(val);
		            		}
		            	}
		            	if(name == "cifNo"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		 $("#cifNo").text(val);
			            		 $("#prospectNo").text('');
			            		 $("#vPlusNo").text('');
		            		}
		            	}
		            	if(name == "prospectNo"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		 $("#prospectNo").text(val);
			            		 $("#cifNo").text('');
			            		 $("#vPlusNo").text('');
		            		}
		            	}
		            	if(name == "vPlusNo"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		 $("#vPlusNo").text(val);
			            		 $("#cifNo").text('');
			            		 $("#prospectNo").text('');
		            		}
		            	}
		            	if(name == "nationalNo"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		 $("#nationalNo").text(val);
		            		}
		            	}
		            	if(name == "customerType"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		 $("#customerType").text(val);
		            		}
		            	}
		            	if(name == "custName"){
		            		if(val !=null && val != "" && val != 'undefined'){
			            		 $("#custName").text(val);
		            		}
		            	}
		            	if(name == "operSrCount"){
			            	if(val !=null && val != "" && val != 'undefined'){
				         		$("#operSrCount").text(val);
			            	}else if(val == 0){
			            		$("#operSrCount").text(val);
			            	}
			            }
			            if(name == "opportunitiesCount"){
			            	if(val !=null && val != "" && val != 'undefined'){
				          		$("#opportunitiesCount").text(val);
			            	}else if(val == 0){
			            		$("#opportunitiesCount").text(val);
			            	}
			            }
		            }
		        });
	        },error: function(data) {
	        	result=data;
				showAlert("error", "Error occured while fetching Party Communication Data!");
			}
	    });
	    if (null != document.getElementById('isRequestFromAddSalesOpportunity')) {
	    	var isRequestFromAddSalesOpportunity = document.getElementById('isRequestFromAddSalesOpportunity').value;
	    	if (isRequestFromAddSalesOpportunity == "Y") {
	    		$("#customerId").val(event.value);
	    		loadOriginatingSRs();
	    		loadOriginatingAlerts();
	    	}
	    }
    }
}
