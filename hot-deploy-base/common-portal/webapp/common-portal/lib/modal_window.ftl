<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<#macro createContactModal instanceId fromAction="">
<div id="${instanceId!}" class="modal fade" data-keyboard="false" data-backdrop="static">
   <div class="modal-dialog modal-lg" style="max-width: 1700px;">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title"></h4>
            <button type="button" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <form id="createContactForm" method="post" data-toggle="validator">
            	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
               <@inputHidden id="accountPartyId" name="accountPartyId" value="${rebatePartyIdTo?if_exists}"/>    
               <div class="row">
                  <#--  
                  <div class="col-md-10 col-sm-10">
                     <@dynaScreen 
                     instanceId="QUICK_CREATE_CONT_BASE"
                     modeOfAction="CREATE"
                     />
                  </div>
                  -->
                  <#assign emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9-_]+(?:.[a-zA-Z]{2,3})*$" />			
                  <div class="col-md-5 col-sm-5">
                     <@inputRow 
                     id="firstName"
                     name="firstName"
                     label='First Name'
                     placeholder='First Name'
                     value=""
                     required=true
                     maxlength="100"
                     />	
                     <@inputRow 
                     id="lastName"
                     name="lastName"
                     label='Last Name'
                     placeholder='Last Name'
                     value="${prgmName!}"
                     required=true
                     maxlength="100"
                     />	
                  </div>
                  <div class="col-md-5 col-sm-5">
                     <@dropdownCell
                     id="designation"
                     name="designation"
                     allowEmpty=true
                     options=designationList!
                     placeholder="Select Designation"
                     label="Designation"
                     required=false
                     />
                   <#--  <@dropdownCell
                     id="gender"
                     name="gender"
                     allowEmpty=true
                     options=genderList!
                     placeholder="Select Gender"
                     label="Gender"
                     required=true
                     /> -->
                  </div>
               </div>
               <div class="row">
                  <div class="col-md-5 col-sm-5">
                     <@inputRow 
                     id="primaryEmail"
                     name="primaryEmail"
                     label='Email'
                     placeholder='example@company.com'
                     value=""
                     required=true
                     maxlength="100"
                     pattern=emailPattern
                     />	
                     <@inputRow 
                     id="primaryPhoneNumber"
                     name="primaryPhoneNumber"
                     label='Phone'
                     placeholder='Phone'
                     value=""
                     type="number"
                     required=false
                     maxlength="20"
                     />	
                  </div>
               </div>
               <div class="clearfix"></div>
               <div class="offset-md-2 col-sm-10">
                  <@formButton
                  btn1type="submit"
                  btn1label="${uiLabelMap.Save}"
                  btn1onclick=""
                  btn2=true
                  btn2onclick = "resetForm()"
                  btn2type="reset"
                  btn2label="${uiLabelMap.Clear}"
                  />
               </div>
            </form>
         </div>
         <div class="modal-footer">
            <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
         </div>
      </div>
   </div>
</div>

<script>
$(document).ready(function() {
    $('#createContactForm').validator().on('submit', function(e) {
        var firstName = $("#firstName").val();
        var lastName = $("#lastName").val();
        var primaryEmail = $("#primaryEmail").val();
        var designation = $("#designation").val();
        var accountPartyId = $("#accountPartyId").val();
        if (!accountPartyId) {
            showAlert("error", "Unable to get account partyId!");
            return false;
        }
        if (!firstName || !lastName || !primaryEmail || !accountPartyId) {
            showAlert("error", "Please fill all required fields!");
            return false;
        }
        $.ajax({
            type: "POST",
            url: "/common-portal/control/createContactActionAjax",
            data: JSON.parse(JSON.stringify($("#createContactForm").serialize())),
            async: false,
            success: function(result) {
                if (result.response == "SUCCESS") {
                    var contactPartyId = result.contactPartyId;
                    if (contactPartyId) {
                        var prtyName = "";
                        var firstName = $("#firstName").val();
                        var lastName = $("#lastName").val();
                        prtyName = firstName + " " + lastName;
                        $('#approval-create-contact').modal('hide');
                        var option = "";
                        option += "<option value='" + contactPartyId + "'>" + prtyName + "</option>";
                        $("#primaryContactId").append(option);
                        $("#e-sign-add-contact-btn").hide();
                    }
                } else {
                	errorMessage=result.errorMessage;
                	if (errorMessage){
                		showAlert("error", errorMessage);
                	}else{
                    	showAlert("error", "Unable To Create Contact Please Try Again!");
                    }
                    $('#approval-create-contact').modal('hide');
                    return false;
                }
            },
            error: function(result) {
                errorMessage=result.errorMessage;
                if (errorMessage){
                	showAlert("error", errorMessage);
                }else{
                 	showAlert("error", "Unable To Create Contact Please Try Again!");
                }
                $('#approval-create-contact').modal('hide');
                return false;
            }
        });
    });
});
</script> 
</#macro>

<#macro createCustomerModal instanceId fromAction="">
<div id="${instanceId!}" class="modal fade" data-keyboard="false" data-backdrop="static">
   <div class="modal-dialog modal-lg" style="max-width: 1700px;">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Create Customer</h4>
            <button type="button" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
         	<div id="cust-refresh"></div>
            <form id="${instanceId!}_createCustomerForm" method="post" data-toggle="validator">
            	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
               	<@dynaScreen 
                    instanceId="CUST_BASE_SHORT"
                    modeOfAction="CREATE"
                    />
               	
               	<div class="form-group offset-2">
                    <div class="text-left ml-3">
                        <@formButton
                        btn1type="submit"
                        btn1label="${uiLabelMap.Save}"
                        btn2=true
                  		btn2type="reset"
                        btn2label="${uiLabelMap.Clear}"
                        btn2id="${instanceId!}-reset-btn"
                        />
                    </div>
                </div>
            </form>
         </div>
         <div class="modal-footer">
            <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
         </div>
      </div>
   </div>
</div>

<script>
$(document).ready(function() {

	$('#${instanceId!}_createCustomerForm').validator().on('submit', function (e) {
		if (e.isDefaultPrevented()) {
	    	// handle the invalid form...
			
	  	} else {
	  		e.preventDefault();
	  		
	  		var firstName = $("#${instanceId!}_createCustomerForm input[name=firstName]").val();
	        var lastName = $("#${instanceId!}_createCustomerForm input[name=lastName]").val();
	        var primaryEmail = $("#${instanceId!}_createCustomerForm input[name=primaryEmail]").val();
	        if (!firstName || !lastName) {
	            showAlert("error", "Please fill all required fields!");
	            return false;
	        }
	        $.ajax({
	            type: "POST",
	            url: "/common-portal/control/createCustomerActionAjax",
	            data: JSON.parse(JSON.stringify($("#${instanceId!}_createCustomerForm").serialize())),
	            async: false,
	            success: function(result) {
	            	if (result.code == 200) {
	            		console.log('#partyId: '+result.partyId);
	            		showAlert ("success", "Successfully created customer!");
	            		$('#findPartyForm input[name=partyId]').val(result.partyId);
	            		$('#cust-refresh').trigger('click');
	            		$("#${instanceId!}-reset-btn").trigger('click');
	            	} else {
	            		showAlert ("error", result.message);
	            	}
	            	
	            	$('#${instanceId!}').modal('hide');
	            },
	            error: function(result) {
	                errorMessage=result.message;
	                if (errorMessage){
	                	showAlert("error", errorMessage);
	                }else{
	                 	showAlert("error", "Unable To Create Customer Please Try Again!");
	                }
	                $('#${instanceId!}').modal('hide');
	                return false;
	            }
	        });
	  	}
	});
	
});
</script> 
</#macro>

