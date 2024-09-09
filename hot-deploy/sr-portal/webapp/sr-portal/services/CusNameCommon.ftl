<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/> 
<#include "component://sales-portal/webapp/sales-portal/teleSales/viewMail.ftl">
<#include "component://sales-portal/webapp/sales-portal/teleSales/viewCall.ftl">
<#include "component://sales-portal/webapp/sales-portal/teleSales/createAlert.ftl">
<#include "component://sales-portal/webapp/sales-portal/teleSales/viewAlerts.ftl"> 
 
         <#assign srNumberUrlParam = requestParameters.srNumber!>
         
         <#assign customerCin = "${responseObj.cinNumber?if_exists}!">
         <@inputHidden name="customerCin" id="customerCin" value="${customerCin?if_exists}!"/>
              <h2> <a href="#">${responseObj.customerName?if_exists}</a></h2>
              <a href="" class="text-dark left-icones" data-toggle="modal" data-target="#viewAlerts" title="View Customer Alerts"><i class="fa fa-bell-o custicons" aria-hidden="true"></i></a>
              <a href="#" class="text-dark left-icones" data-toggle="modal" data-target="#createAlert" title="Add Customer Alert"><img src="/bootstrap/images/add-customer-alert.png" class="cust-icon" width="21" height="22"> </a>

              <a href="addservicerequest" title="Add Service Request" class="text-dark left-icones"><i class="fa fa-plus-square custicons" aria-hidden="true"></i></a>

              <a href="<@ofbizUrl>newOpportunity?srNumber=${requestParameters.srNumber?if_exists}</@ofbizUrl>" title="Add Opportunity" class="text-dark left-icones"><img src="/bootstrap/images/add-opportunities.png" class="cust-icon" width="21" height="22"></a>
              <!--a href="#" title="Add Activities" class="text-dark left-icones" data-toggle="modal" data-target="#myModal4"><i class="fa fa-trophy fa-1" aria-hidden="true"></i> </a!-->
              <a href="srAddTask" title="Add Activity" id="dropdown09" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="text-dark left-icones"><i class="fa fa-calendar" aria-hidden="true"></i></a>
              <div class="dropdown-menu" aria-labelledby="dropdown09"><a href="srAddTask" title="Add Activity" id="dropdown09" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="text-dark left-icones">
                <h4>Add Activities</h4>
                </a><a class="dropdown-item" href="srAddTask?srNumber=${requestParameters.srNumber?if_exists}"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
                <a class="dropdown-item" href="srAddPhoneCall?srNumber=${requestParameters.srNumber?if_exists}"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
                <a class="dropdown-item" href="srAddEmail?srNumber=${requestParameters.srNumber?if_exists}"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
                <a class="dropdown-item" href="srAddAppointment?srNumber=${requestParameters.srNumber?if_exists}"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
                <a class="dropdown-item" href="srAddOthers?srNumber=${requestParameters.srNumber?if_exists}"><i class="fa fa-plus-square" aria-hidden="true"></i> Others</a>
              </div> 
              <ul class="text-right">
                <li> <a href="findServiceRequests?cinNumber=${responseObj.cinNumber?if_exists}" data-toggle="tooltip" data-placement="top" title="" class="text-dark mr-3" data-original-title="View Service Requests"> <span class="rounded-circle badge badge-secondary position-absolute ">${operSrCount?if_exists}</span><i class="fa fa-plus-square custicons fa-1" aria-hidden="true"></i></a></li>
                <li id='mailImg'><i class="fa fa-envelope fa-1" aria-hidden="true"></i> <a id='mail' href="#" class="ml-2 mr-2 text-dark" data-toggle="modal" data-target="#viewMail">  </a></li>
                <li id='mobileImg'><i class="fa fa-phone fa-1" aria-hidden="true"></i> (+65) <a id='mobile' href="#" class="mr-2 text-dark" aria-hidden="true" data-toggle="modal" data-target="#viewCall">  </a> </li>
              </ul>
               <ul class="text-right">
                        <li><div class="checkbox bglight"><label>Phone <i id="phone" class="fa fa-times fa-1 text-danger" aria-hidden="true"></i></label></div></li>
                        <li><div class="checkbox bglight"><label>Email <i id="email" class="fa fa-times fa-1 text-danger" aria-hidden="true"></i></label></div></li>
                        <li><div class="checkbox bglight"><label>Postal <i id="address" class="fa fa-times fa-1 text-danger" aria-hidden="true"></i></label></div></li>
                        <li><div class="checkbox bglight"><label>SMS <i id="sms" class="fa fa-times fa-1 text-danger" aria-hidden="true"></i></label></div></li>
                     </ul>
         
   
<script>   
	$(document).ready(function() {
	   partyCommunicationEvents();
	});

	function partyCommunicationEvents() {
		var result = null;
		var srNumberUrlParam = document.getElementById('srNumberUrlParam').value;
		if(srNumberUrlParam !=null && srNumberUrlParam != "" && srNumberUrlParam != 'undefined'){
		    $.ajax({
		        type: "POST",
		        url: "getCustomerCommunicationInfo",
		        async: false,
		        data: {"srNumber": srNumberUrlParam},
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
	
	