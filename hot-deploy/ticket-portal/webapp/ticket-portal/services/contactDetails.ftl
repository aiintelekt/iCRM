<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
 
<div class="page-header border-b pt-2">
	<div class="row">
    	<h2 class="d-inline-block">&#160;&#160;&#160;${uiLabelMap.contactInformation}</h2>
     	&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
     	&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
     	&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
     	&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
     	&nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp;
     	&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp;
     	&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp;
     	<label>Phone <i id="phone1" class="fa fa-times fa-1 text-danger" aria-hidden="true"></i></label>
     	<label>Email <i id="email1" class="fa fa-times fa-1 text-danger" aria-hidden="true"></i></label>
     	<label>Postal <i id="address1" class="fa fa-times fa-1 text-danger" aria-hidden="true"></i></label>
     	<label>SMS <i id="sms1" class="fa fa-times fa-1 text-danger" aria-hidden="true"></i></label>
	</div>
</div>
<h4 class="bg-light pl-1 mt-2">${uiLabelMap.phone}</h4>
<div class="table-responsive">
	<table  class="table table-striped table-bordered" style="width:100%;">
    	<thead>
        	<tr>
            	<th width="10%">${uiLabelMap.purpose}</th>
                <th>${uiLabelMap.contactInformation}</th>
            </tr>
        </thead>
        <tbody>
        	<tr>
                <td>${uiLabelMap.primary}</td>
                <td></td>
            </tr>
            <tr>
            	<td>${uiLabelMap.home}</td>
                <td></td>
	        </tr>
            <tr>
                <td>${uiLabelMap.office}</td>
                <td></td>
            </tr>
            <tr>
                <td>${uiLabelMap.mobile}</td>
                <td id="phoneNumber">${phoneNumber?if_exists}</td>
            </tr>
            <tr>
                <td>${uiLabelMap.otp}</td>
                <td></td>
            </tr>
            <tr>
                <td>${uiLabelMap.fax}</td>
                <td></td>
            </tr>
        </tbody>
    </table>
</div>
<h4 class="bg-light pl-1 mt-2">${uiLabelMap.email}</h4>
<div class="table-responsive">
    <table  class="table table-striped table-bordered" style="width:100%;">
        <thead>
            <tr>
                <th width="10%">${uiLabelMap.purpose}</th>
                <th>${uiLabelMap.contactInformation}</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>${uiLabelMap.primary}</td>
                <td id="emailAddr">${emailAddr?if_exists}</td>
            </tr>
            <tr>
                <td>${uiLabelMap.personal}</td>
                <td></td>
            </tr>
            <tr>
                <td>${uiLabelMap.work}</td>
                <td></td>
            </tr>
        </tbody>
    </table>
</div>
<h4 class="bg-light pl-1 mt-2">${uiLabelMap.address}</h4>
<div class="table-responsive">
    <table  class="table table-striped table-bordered" style="width:100%;">
        <thead>
            <tr>
                <th width="10%">${uiLabelMap.purpose}</th>
                <th>${uiLabelMap.contactInformation}</th>
            </tr>
        </thead>
        <tbody>
        	<tr>
                <td>${uiLabelMap.primary}</td>
                <td></td>
            </tr>
            <tr>
                <td>${uiLabelMap.home}</td>
                <td id="Address">${Address?if_exists}</td>
            </tr>
            <tr>
                <td>${uiLabelMap.work}</td>
                <td></td>
            </tr>
        </tbody>
    </table>
</div>

<script type="text/javascript">
	var contactdet="";
   	function getContactDetails() {
    	const url=window.location.search;
		const urlParam=new URLSearchParams(url);
		const urlSaleId=urlParam.get("srNumber");
		partyCommunicationEventss();	
		$.ajax({   
			type: "POST",
	     	url: "getContactDetails",
	        data:  {"srNumber": urlSaleId},
	        success: function (data) {   
	             result = data[0];
	             if(result!=null && result.emailAddr !=null && result.emailAddr != "" && result.emailAddr != 'undefined'){
	             	document.getElementById("emailAddr").innerHTML=result.emailAddr;
	             }
	             if(result!=null && result.Address !=null && result.Address != "" && result.Address != 'undefined'){
	             	document.getElementById("Address").innerHTML=result.Address;
	             }
	             if(result!=null && result.phoneNumber !=null && result.phoneNumber != "" && result.phoneNumber != 'undefined'){
	             	document.getElementById("phoneNumber").innerHTML=result.phoneNumber; 	
	             }
	        },error: function(data) {
	        	result=data;
				console.log('Error occured');
				showAlert("error", "Error occured while fetching Tiles Data!");
			}
		});
	}		        

	function partyCommunicationEventss() {
		var result = null;
		const url=window.location.search;
		const urlParam=new URLSearchParams(url);
		const urlsrId=urlParam.get("srNumber");
		
		if(urlsrId !=null && urlsrId != "" && urlsrId != 'undefined'){
			$.ajax({
		    	type: "POST",
		        url: "getCustomerCommunicationInfo",
		        async: false,
		        data: {"srNumber": urlsrId},
		        success: function(data) {
		        	result=data[0];
		            $.each(result, function(name, val) {
			        	if(name !=null && name != "" && name != 'undefined'){
			            	if(name == "phoneSolicitation"){
			            		if(val !=null && val != "" && val != 'undefined'){
				            		if(val == "Y"){
				            			document.querySelector("i#phone1").setAttribute('class','fa fa-check fa-1 text-success');
				            			document.querySelector("i#sms1").setAttribute('class','fa fa-check fa-1 text-success');
				            		}else{
				            			document.querySelector("i#phone1").setAttribute('class','fa fa-times fa-1 text-danger');
				            			document.querySelector("i#sms1").setAttribute('class','fa fa-times fa-1 text-danger');
				            		}
			            		}else{
				            		document.querySelector("i#phone1").setAttribute('class','fa fa-times fa-1 text-danger');
				            		document.querySelector("i#sms1").setAttribute('class','fa fa-times fa-1 text-danger');
				            	}
			            	}
			            	if(name == "emailSolicitation"){
			            		if(val !=null && val != "" && val != 'undefined'){
				            		if(val == "Y"){
				            			document.querySelector("i#email1").setAttribute('class','fa fa-check fa-1 text-success');
				            		}else{
				            			document.querySelector("i#email1").setAttribute('class','fa fa-times fa-1 text-danger');
				            		}
			            		}else{
				            		document.querySelector("i#email1").setAttribute('class','fa fa-times fa-1 text-danger');
				            	}
			            	}
			            	if(name == "addressSolicitation"){
			            		if(val !=null && val != "" && val != 'undefined'){
				            		if(val == "Y"){
				            			document.querySelector("i#address1").setAttribute('class','fa fa-check fa-1 text-success');
				            		}else{
				            			document.querySelector("i#address1").setAttribute('class','fa fa-times fa-1 text-danger');
				            		}
			            		}else{
				            		document.querySelector("i#address1").setAttribute('class','fa fa-times fa-1 text-danger');
				            	}
			            	}
			            }
			        });
		        },error: function(data) {
		        	result=data;
					showAlert("error", "Error occured while fetching Party Communication Data!");
				}
		    });
	    } 
	}
</script>
 