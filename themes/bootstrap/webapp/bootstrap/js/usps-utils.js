USPSUTIL = window.USPSUTIL || {};

(function(uspsutil){
	
	uspsutil.performUspsAddrValidation = function (paramList, externalLoginKey) {
		var actionUrl = "/postal-app/control/performUspsAddrValidation";
		var geoDetails;
		var inputData = {};
		for (var key in paramList) {
		    if (paramList.hasOwnProperty(key)) {
		    	inputData[key] = $("#"+paramList[key]).val();
		    }
		}
		inputData['externalLoginKey'] = externalLoginKey;
		inputData = JSON.parse(JSON.stringify(inputData));
		var valid = true;
		$.ajax({
			type: "POST",
	     	url: "/postal-app/control/performUspsAddrValidation",
	     	data: inputData,
	        async: false,
	        success: function (data) {   
	            if (data.code == 200) {
	            	if (data.isValidAddress) {
	            		$("#isUspsAddrVerified").val('Y');
	            		var address = data.address;
	            		for (var key in paramList) {
	            		    if (paramList.hasOwnProperty(key)) {
	            		    	$("#"+paramList[key]).val(address[key]);
	            		    }
	            		}
		            	$('.ui.dropdown.search').dropdown({clearable: true});	
		            	if (!confirm("Address info verified from USPS, Are you sure to proceed ?")) {
		            		valid = false;
		            	}
	            	} else {
	            		$("#isUspsAddrVerified").val('N');
	            		valid = false;
	            		if (confirm("USPS Validation: "+data.errorMessage+" Are you sure to proceed ?")) {
		            		valid = true;
		            	}
	            		//valid = false;
	            		//showAlert("error", "USPS Validation: "+data.errorMessage);
	            	}
	            }
	        }
	        
		});   
		return valid;
    }
	
})(USPSUTIL);