fagReady("INVOICE_APPLIED_PAY_SR", function(el, api, colApi, gridApi){
	$("#refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#export-btn").click(function () {
    	gridApi.csvExport();
    });
    
    $("#main-search-btn").click(function () {
    	 loadInvoicesPayments(gridApi, api);
    });
    
    $("#remove-payment-btn").click(function () {
    	
		var selectedData = api.getSelectedRows();
		if (selectedData.length > 0) {
			
		    var paymentApplicationId = "";
		    var invoiceId = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	paymentApplicationId = data.paymentApplicationId;
		    	//invoiceId = data.invoiceId;
		    }
		    
		    var inputData = {"paymentApplicationId": paymentApplicationId};
		    $.ajax({
				type : "POST",
				url : "/accounting-portal/control/removeAppliedPaymentAction",
				async : true,
				data : inputData,
				// data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
				success : function(result) {
						showAlert ("success", "Successfully Removed Payment applied ");
						loadInvoicesPayments(gridApi, api)
					/*} else {
						showAlert ("error", data.message);
					}*/
				},
				error : function() {
					console.log('Error occured');
					showAlert("error", "Error occured!");
				},
				complete : function() {
				}
			});
			
		} else {
			showAlert("error", "Please select atleast one row to be removed!");
		}
	
});
    //on create payment button
    $('#create-payment-form-submit').on('click', function (e) {
		var partyId= $("#create-payment #partyId").val();
		var paymentTypeId= $("#createPayment #paymentTypeId").val();
		var paymentMethodId= $("#createPayment #paymentMethodId").val();
		var outstandingAmount= $("#createPayment #outstandingAmount").val();
		var paymentRefNumber= $("#createPayment #paymentRefNumber").val();
		var comments= $("#createPayment #comments").val();
		var invoiceId= $("#create-payment #invoiceId").val();
		var total= $("#create-payment #total").val();
		if(outstandingAmount > total){
			var message = "Entered Amount should be less than outstanding Amount !";
			 showAlert ("error", message);
			 return false;
		}
		$.ajax({
        type: "POST",
        url: "createPaymentForInvoice",
        data: { 
	            "domainEntityType":$("#domainEntityType").val(),
	            "partyId": partyId,
	            "paymentTypeId":paymentTypeId,
	            "paymentMethodId":paymentMethodId,
	            "outstandingAmount":outstandingAmount,
	            "paymentRefNumber":paymentRefNumber,
	            "comments":comments,
	            "invoiceId":invoiceId
	            
        },
        sync: true,
        success: function(data) {
        	$('#create-payment-modal').modal("hide");
        	$('.clear').click();
		    $("#paymentRefNumber").val("");
		    $("#comments").val("");
         	  loadInvoicesPayments(gridApi, api);
         	  var message = "Payment Created Successfully.";
          	  showAlert ("success", message);
          	  var pendingAmounttoDis = document.getElementById("outstandingAmount").innerHTML;
	          	pendingAmounttoDis = pendingAmounttoDis.replace(".00","");
	          	pendingAmounttoDis = pendingAmounttoDis.replace("$","");
          	 $("#createPayment #outstandingAmount").val(pendingAmounttoDis);
          	  
        }

    });
	e.preventDefault();
    });
    
    //update payment details
    $('#update-payment-form-submit').on('click', function (e) {
		var partyId= $("#updatePayment #partyId_val").val();
		var paymentId= $("#updatePayment #paymentId").val();
		var paymentApplicationId= $("#updatePayment #paymentApplicationId").val();
		var paymentTypeId= $("#updatePayment #payPaymentTypeId").val();
		var paymentMethodId= $("#updatePayment #payPaymentMethodId").val();
		var referenceNumber= $("#updatePayment #paymentRefNum").val();
		var comments= $("#updatePayment #payComments").val();
		$.ajax({
        type: "POST",
        url: "updatePaymentForInvoice",
        data: { 
	            "domainEntityType":$("#domainEntityType").val(),
	            "partyId": partyId,
	            "paymentId": paymentId,
	            "paymentApplicationId": paymentApplicationId,
	            "paymentTypeId":paymentTypeId,
	            "paymentMethodId":paymentMethodId,
	            "referenceNumber":referenceNumber,
	            "comments":comments
	            
        },
        sync: true,
        success: function(data) {
        	$('#update-payment-modal').modal("hide");
             var message = "Payment Updated Successfully.";
          	  showAlert ("success", message);
          	  //location.reload();
         	  loadInvoicesPayments(gridApi, api);
         	 
				
        }

    });
	e.preventDefault();
    });
    loadInvoicesPayments(gridApi, api);
});

var listInvoicesAppliedPaymentsUrl = "";
function loadInvoicesPayments(gridApi, api) {
	if(listInvoicesAppliedPaymentsUrl == ""){
		resetGridStatusBar();
		listInvoicesAppliedPaymentsUrl = getGridDataFetchUrl("INVOICE_APPLIED_PAYMENTS");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(listInvoicesAppliedPaymentsUrl != null && listInvoicesAppliedPaymentsUrl != "" && listInvoicesAppliedPaymentsUrl !="undefined"){
		api.showLoadingOverlay();
		$.ajax({
		  async: false,
		  url:listInvoicesAppliedPaymentsUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
		  success: function(data){
			  gridApi.setRowData(data);
			  var result1 = data[0];
			  var outstandingAmount = result1.outstandingAmount;
				document.getElementById("outstandingAmount").innerHTML = DOMPurify.sanitize("$"+outstandingAmount);
		  }
		});
	}
}

function editPayment(paymentApplicationId) {
	$('#update-payment-modal').modal('show');
	$.ajax({
		type: "POST",
     	url: "/accounting-portal/control/getPaymentData",
        data: {"paymentApplicationId": paymentApplicationId, "invoiceId": "${requestAttributes.invoiceId!}"},
        async: false,
        success: function (result) {   
            	for (var fieldName in result.data){
				    console.log("name: "+fieldName+", value: "+result.data[fieldName]);
				    if (result.data[fieldName]) {
				    	$('#'+fieldName).val( result.data[fieldName] );
				    }
				}
				$('.ui.dropdown.search').dropdown({
					clearable: true
				});
        }
	}); 
}