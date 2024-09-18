<!-- Bootstrap core CSS -->
   <#--  <link href="/lms-mobile-resource/css/fio-custom.css" rel="stylesheet"> -->
   <link rel="stylesheet" href="/crm-resource/css/createleadform.css" type="text/css"/>
   	<script src="/lms-mobile-resource/js/jquery.bootstrap.wizard.js"></script>
    <script src="/lms-mobile-resource/js/leads.js"></script>
    <script src="/crm-resource/js/crmlead.js"></script>

    <#include "component://lms-mobile/webapp/lms-mobile/lib/mobileMacros.ftl"/>
<div class="row">
   <div id="main" role="main" class="mt-2">
   <h1 class="text-center">Create Lead</h1>
   <div class="col-md-12 col-lg-12 col-sm-12 ">
 <#assign partyId= request.getParameter("partyId")!>

<div id="bar" class="progress row">
	<div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>
</div>
<div class="row mb-2 mt-2">
    <div id="div_step1" class="col-4 text-center clear-fix">Step 1</div>
    <div id="div_step2" class="col-4 text-center clear-fix">Step 2</div>
    <div id="div_step3" class="col-4 text-center clear-fix">Step 3</div>
 </div>

  <div id="createLeadWizard" class="rootwizard">
    	<div class="row" id="displayMessageTab" style="display:none;">
	         <div class="col-12">
			    <div class="alert alert-danger alert-dismissible rounded-0" id="alertStatusTab">
				   <#--  <button type="submit" class="close" id="closeButtonTab" data-dismiss="alert">x</button> -->
			       <p class="mb-0" id="returnMessageTab"></p>            
			    </div>
		     </div>
		 </div>
		<div class="px-3" >
		 <form method="POST" name="createLead" id="createLead" onsubmit="return goNext()" action="<@ofbizUrl>saveLead</@ofbizUrl>" >
		 <#-- First Tab Begins.. -->
		 <div id="tab1">
			<div class="bg-light1 row">
			 <div class="col-12 mt-2">
			   <h6>Company</h6>
			 </div>
			</div>
			<div class="clearfix"> </div>
	      <@inputBox name="companyName" id="companyName"  label="Company name*" type="text" placeholder="Company Name Goes Here" />
          <@dropdownBox 
	          id="constitution"
	          label="Constitution"
	          dataLiveSearch=true
	          allowEmptyValue="Please Select"
	          options=constitutionList
          />
          <@inputBox name="pincode" id="pincode"  label="Pin code" type="text" placeholder="Key in code" />
          <@dropdownBox 
	          id="city"
	          label="City"
	          dataLiveSearch=true
	          allowEmptyValue="Please Select"
	          options=cityList
          />
          <@dropdownBox 
	          id="state"
	          label="State"
	          dataLiveSearch=true
	          allowEmptyValue="Please Select"
	          options=indiaStateList
          />
		</div>
		 
		 <#-- Second Tab Begins.. -->
		 <div id="tab2" style="display:none">
		<div class="bg-light1 row">
		 <div class="col-12 mt-2">
		   <h6 class="display-7">Contact</h6>
		 </div>
		</div>
         <div class="clearfix"> </div>
          <@inputBox name="firstName" id="firstName"  label="First name*" type="text" placeholder="First Name Goes Here" />
          <@inputBox name="lastName" id="lastName"  label="Last name" type="text" placeholder="Last Name Goes Here" />
          <@dropdownBox 
	          id="designation"
	          label="Designation"
	          dataLiveSearch=true
	          allowEmptyValue="Please Select"
	          options=designationList
          />
          <@inputBox name="mobileNo" id="mobileNo"  label="Mobile Number" type="text" value="+91 " placeholder="Enter Mobile number" />
          <@inputBox name="emailAddress" id="emailAddress"  label="Email Address" type="text" placeholder="sample@company.com" />
		 </div>
		 
		 <#-- Third Tab Begins.. -->
		 <div id="tab3" style="display:none">
		 <div class="bg-light1 row">
			 <div class="col-12 mt-2">
			   <h6 class="display-7">Lead Source</h6>
			 </div>
			</div>
          <div class="clearfix"> </div>
          <@dropdownBox 
	          id="leadSource"
	          label="Lead Source*"
	          dataLiveSearch=true
	          allowEmptyValue="Please Select"
	          options=lead_SourceList
          />
          <@dropdownBox 
	          id="intermediaryName"
	          label="Intermediary Partners Name"
	          dataLiveSearch=true
	          allowEmptyValue="Please Select"
	          options=tcpList
          />
		 </div>
		 
		 <#-- Button part begins -->
		<footer class="footer border btn-responsive fixed-bottom mb-5">
		 	<div class="container-fluid">
	          <div class="col-12 p-1">
	            <div class="row pager wizard">
	              <div class="col-6 border-right">
	                <ul class="list-group">
	                 <#-- <li onclick="goBack()"><a href="javascript:void(0);" id="back" class="btn btn-sm rounded-0 btn-block text-dark"></a></li>  -->
	                 <li><input type='button' onclick="goBack()" id="back" class="btn btn-sm rounded-0 btn-block text-dark"/></li>
	               </ul>
	              </div>
	              <div class="col-6 p-0">
	                <ul class="list-group">
	                  <li><input type='submit' id="next" class="btn btn-sm rounded-0 btn-block"/></li>
	                </ul>
	              </div>
	            </div>
	          </div>
	     	</div>
       	  </footer>
	 </form>	
	</div>	 
 </div>
 </div>
 </div>
 </div>
 </div>
<form method="POST" action="<@ofbizUrl>assignLead</@ofbizUrl>" name="assignLead" id="assignLead" >
<input type="hidden" name="leadId" id="companyId"/>
<input type="hidden" value="${loginPartyId}" name="rm" id="rmId" />
</form>
<script>
        function loadState() {
            if ($('#city').val().length > 0) {
                $.ajax({

                    type: "POST",
                    url: "getGeoAssocState",
                    data: {
                        "geoIdTo": $("#city").val(),
	                	"geoAssocTypeId": "COUNTY_CITY"
                    },
                    async: false,
                    success: function(data) {
                        if (data.code == 200) {
                            for (var i = 0; i < data.results.length; i++) {
	                        $('.search').val('');
	                        var result = data.results[i];
	                        var groupNameOptions = '<option value="' + result.geoId + '" selected="selected">' + result.geoName + '</option>';
	                        $("#state").html(groupNameOptions);
	                    	}
	                    	if(!isLoadWithPin){
	                    		$('#pincode').val('');
	                    	}
	                    	isLoadWithPin=false;
                        }
                    }

                });
            }
            else{
            	$("#city").dropdown('clear');
                $("#state").dropdown('clear');
            }
        }
        
$(document).ready(function(){
  $('.ui.dropdown.search').dropdown({
  	clearable: true
  });
  
  $("#city").change(function() {
            loadState();
  });
  
  
  
$('#companyName').bind("on keyup", function(event){
	wordMatch();
	validateCompanyInfo();
});

  	$('#pincode').bind("blur keyup", function(event) {
  	 	validateCompanyInfo();
		if ($('#pincode').val().trim() != "") {
            if ($('#pincode').val().length == 6) {
                $.ajax({
                    type: "POST",
                    url: "getPostalCodeDetail",
                    data: {
                        "postalCode": $("#pincode").val()
                    },
                    async: false,
                    success: function(data) {
                        if (data.code == 200) {
                            if (data.postalCodeDetail.postalCode) {
                            	isLoadWithPin=true;
                                selectedCity = data.postalCodeDetail.city;
                                $("#city").val(data.postalCodeDetail.city).change();
                                $("#state").val(data.postalCodeDetail.stateProvinceGeoId).change();
                                
                            } else {
                            	showError('Invalid PIN Code');
                                $("#city").dropdown('clear');
                                $("#state").dropdown('clear');
                                selectedCity = null;
                            }
                        }
                    }

                });
            }
            else{
            	showError('Pincode should be 6 characters');
            	$("#city").dropdown('clear');
                $("#state").dropdown('clear');
            }
        }
       
    });  	  	  	    	  	
  });

function verifyLeadExists(){
  var valid = true;
  var ajaxUrl = '<@ofbizUrl>verifyLeadExists</@ofbizUrl>';
  var companyName = $('#companyName').val().trim();    	
  	$.ajax({
        url: ajaxUrl,
        type: 'POST',
        async: false,            
        data: {"companyName" : companyName,
               "constitution" : $('#constitution').val()
              },
        error: function(msg) {
            alert("An error occurred loading content! : " +JSON.stringify( msg));
            return false;
        },
        success: function(msg) {
             var currentUser = '${loginPartyId}' ;
             var exists = msg.exists;
             var company = msg.company;
             var isCentral = msg.isCentral;
             console.log(msg);
             if(exists){
             	 var companyId = company.partyId;
	             var assignedTo = company.partyIdTo;
	             var statusId = company.statusId;
	             
	             if(assignedTo == currentUser) {
	             	if(statusId != "DROPPED"){
	             	  showError('Lead is already assigned to you');
	             	  valid = false;
	             	}else{
	                    $.ajax({
                           type: "POST",
                           url: "checkLeadAssignment",
                           data: {"companyName" : $('#companyName').val(),
                                    "constitution" : $('#constitution').val()
                                 },
                           async: false,
                           success: function(data) {
                             if (data.code == 200) {
                                var centralTeamId = data.centralTeamId;
                                var leadId = data.leadId;
                                console.log('leadId---'+centralTeamId);
                                window.location.href = "viewLead?partyId="+leadId+"&leadNew=Y";
                                return false;
                              }else{
                                valid = true;
                              }
                           }
                         });
	             	}
	             		
	             } else if(undefined == assignedTo || null == assignedTo) {
	             
	             	 submitAssign(companyId);
	             	valid = false;
	             } else if (assignedTo != currentUser) {
                    if(statusId != "DROPPED"){
                       if (isCentral) {
                          submitAssign(companyId);
                          valid = false;
                       }else{
                         showError('Lead already exists and assigned to another RM');
                         valid =  false;
                       }
                     }else{
                       $.ajax({
                           type: "POST",
                           url: "checkLeadAssignment",
                           data: {"companyName" : $('#companyName').val(),
                                    "constitution" : $('#constitution').val()
                                 },
                           async: false,
                           success: function(data) {
                             if (data.code == 200) {
                                var centralTeamId = data.centralTeamId;
                                var leadId = data.leadId;
                                console.log('leadId---'+centralTeamId);
                                window.location.href = "viewLead?partyId="+leadId+"&leadNew=Y";
                                return false;
                              }else{
                                valid = true;
                              }
                           }
                         });
                    }
	             }
             } else {
                 //valid =true;
                 $.ajax({
                   type: "POST",
                   url: "checkLeadAssignment",
                   data: {"companyName" : $('#companyName').val(),
                                    "constitution" : $('#constitution').val()
                                 },
                   async: false,
                   success: function(data) {
                     if (data.code == 200) {
                        var centralTeamId = data.centralTeamId;
                        var leadId = data.leadId;
                        var leadDropped = data.leadDropped;
                        console.log('leadId---'+centralTeamId);
                        if(leadDropped){
                          window.location.href = "viewLead?partyId="+leadId+"&leadNew=Y";
                          return false;
                          valid = false;
                        }else{
                          valid = true;
                        }
                    }else{
                      valid = true;
                    }
                   }
                 });
             }
             
              return valid;
        }
     });
  
  return valid;
  }
  function submitAssign(companyId){
      $('#companyId').val(companyId);
      
      $('#assignLead').submit();
  }
    
  function validateContactInfo() {
  	  var valid = true; 
  	  var mob=$("#mobileNo").val().trim();
  	  var email=$("#emailAddress").val().trim();
  	  var fname=$("#firstName").val().trim();
  	  var lname=$("#lastName").val().trim();
  	  var desi=$("#designation").val().trim();

	  if(fname.length == 0) {
 	  	showError('Please enter first name');
	  	valid = false;
	  }else if(!validMaxLength(fname,100)) {
	  	showError('First name cannot be more than 100 characters');
	  	valid = false;
	  }
	  if(lname.length > 0 && !validMaxLength(lname,100)) {
	  	showError('Last name cannot be more than 100 characters');
	  	valid = false;
	  }
	  if(mob.length > 3 && !validPhone(mob)){
	  	valid = false;
	  }
	  if(email.length > 0 && !validEmail(email)){
	  	showError('Please enter valid email Address');
	  	valid = false;
	  }
	  
	  if(valid) {
  		hideError("tab");
  	  }
  	  
  	  return valid;
  }
  
  function validateLeadSource() {
  
  	  var valid = true; 
  	  var leadS=$("#leadSource").val().trim();
  	  var ipn=$("#intermediaryName").val().trim();
 	  if(leadS.length == 0) {
 	  	showError('Please select lead source');
	  	valid = false;
	  }else if(leadS == "TCP" || leadS == "TEV"){
	  	if(ipn.length == 0){
		  	showError('Please select Intermediary partner name');
		  	valid = false;
	  	}
	  }
	  if(valid) {
  		hideError("tab");
  	  }  
  	  return valid;
	}
  
   /** Mobile Number validation with country code */
    $("#mobileNo").on("keyup", function(event) { 
       var phone = $('#mobileNo').val().trim();
	  	if(phone.length > 1){
	  		if(phone.substring(0,1) == "+"){
	  			if(phone.length > 2){
	  				$('#mobileNo').val(phone.substring(0,3) + " " + phone.substring(3).trim());
	  			}
	  		}
	  		else{
	  			$('#mobileNo').val(phone.substring(0,2) + " " + phone.substring(2).trim());
	  		}
		}
		validPhone($('#mobileNo').val());           
   });
   
    $("#emailAddress").on("keyup", function(event) { 
		validateContactInfo();
   });
   
    $("#leadSource").on("change", function(event) { 
		validateLeadSource();
   });
   
    $("#intermediaryName").on("change", function(event) { 
		validateLeadSource();
   });
   
    $("#firstName").on("keyup", function(event) { 
		validateContactInfo();
   });
   $("#lastName").on("keyup", function(event) { 
		validateContactInfo();
   });
   
   function validate() {
  	  var valid = false;  
  	  if($('#tab1').is(':visible')){
  	  	valid = validateCompanyInfo();
  	  }else if($('#tab2').is(':visible')){
  	  	valid  = validateContactInfo(this);
  	  }else if($('#tab3').is(':visible')){
  	  	valid  = validateLeadSource();
  	  } else {
  	  	  var validCompanyInfo = validateCompanyInfo();
		  var validContactInfo = validateContactInfo(this);
		  var validLeadSource = validateLeadSource();
		if(validCompanyInfo && validContactInfo && validLeadSource){
			valid = true;
		} 
  	  }
	    
  	return valid;
  }
                        
</script>
