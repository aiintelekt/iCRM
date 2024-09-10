<script type="text/javascript" src="/bootstrap/js/jquery.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/jquery.validate-1.15.1.min.js"></script>
<#assign countryList = delegator.findByAnd("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY"),Static["org.ofbiz.base.util.UtilMisc"].toList("geoName"),true)?if_exists>
<body class="modal-open" style="padding-right: 10px;" data-new-gr-c-s-check-loaded="14.1104.0" data-gr-ext-installed="">
    <div id="subscribe" class="modal fade show" data-backdrop="static" style="padding-right: 10px; display: block;">
      <div class="modal-dialog">
          <div class="modal-content">
    <div class="row" style="">
    <div class="col-md-4 left-bg-row">
        <img id="crm-img" src="/bootstrap/images/fm-icrm-img.jpg" style="width: 100%;padding: 50% 2%;">
    </div>
    <div class="col-md-8 right-bg-row">
              <div class="modal-header">
                  <h3 class="modal-title register-title">Registration For iCRM</h3>
              </div>
              <div class="modal-body">
                  <div class="col-md-12 col-lg-12 col-sm-12 alert-msg">
                      <span class="success-msg" style="display:none"><i class="fa fa-check-circle" aria-hidden="true"></i>&nbsp;<span id="success-msg"></span></span>
                      <span class="error-msg" style="display:none"><i class="fa fa-exclamation-circle" aria-hidden="true">&nbsp;</i><span id="error-msg"></span></span>
                  </div>
                  <form action="#" id="registrationForm" novalidate="novalidate" class="row">
                      <div class="col-md-6 col-lg-6 col-sm-12 ">
                            <div class="form-group row">
                                <label class="col-sm-12 col-form-label">First Name<span class="text-danger"> *</span></label>
                               <div class="col-sm-12">
                                <input type="text" class="form-control" tabindex="1" name="firstName" placeholder="First Name " required="" aria-required="true">
                                <span class="help-block" id="firstName_error"></span>
                                </div>
                            </div>
                            
                            <div class="form-group row">
                                <label class="col-sm-12 col-form-label">Email<span class="text-danger"> *</span></label>
                               <div class="col-sm-12">
                                <input type="hidden" class="form-control" name="email-old">
                                <input type="text" class="form-control" tabindex="3" name="email" placeholder="Email">
                                <span class="help-block" id="email_error"></span>
                                </div>
                            </div>
                            <div class="form-group row">
                                <label class="col-sm-12 col-form-label">Company Name <span class="text-danger"> *</span></label>
                               <div class="col-sm-12">
                                  <input type="text" class="form-control" tabindex="5" name="companyName" placeholder="Company Name">
                                <span class="help-block" id="companyName_error"></span>
                                </div>
                            </div>
                        </div>
                  <div class="col-md-6 col-lg-6 col-sm-12 ">
                            <div class="form-group row">
                                <label class="col-sm-12 col-form-label">Last Name <span class="text-danger"> *</span></label>
                               <div class="col-sm-12">
                                <input type="text" class="form-control" tabindex="2" name="lastName" placeholder="Last Name">
                                <span class="help-block" id="lastName_error"></span>
                                </div>
                            </div>
                            
                            <div class="form-group row">
                                <label class="col-sm-12 col-form-label">Phone<span class="text-danger"> *</span></label>
                               <div class="col-sm-12">
                                <input type="text" class="form-control" tabindex="4" name="phone" placeholder="Phone">
                                <span class="help-block" id="phone_error"></span>
                                </div>
                            </div>
                            <div class="form-group row">
                                <label class="col-sm-12 col-form-label">Country <span class="text-danger"> *</span></label>
                                <div class="col-sm-12">
                                  <select name="country" tabindex="6" class="ui dropdown search form-control fluid show-tick valid" data-live-search="true" aria-required="true" aria-invalid="false">
                                    <option value="">Country</option>
                                    <#list countryList as country>
                                        <option value="${country.geoId}"<#if country.geoId == "USA"> selected</#if>>${country.geoName}</option>
                                    </#list>
                                  </select>
                                <span class="help-block" id="country_error"></span>
                                </div>
                            </div>
                        </div><div class="col-md-12 col-lg-12 col-sm-12 ">
                            <div class="form-group row">
                                <label class="col-sm-12 col-form-label"></label>
                                <div class="col-sm-12">
                                  <input type="checkbox"  tabindex="7" name="terms" class="form-check-input" value=""> 
                                  <label class="form-check-label" style="margin-left: 30px;">I agree to the Privacy Policy</label>
                                  <br><span class="help-block" id="terms_error"></span>
                                </div>
                            </div>
                            <div class="form-group row">
                                <label class="col-sm-12 col-form-label"></label>
                                <div class="col-sm-12">
                                  <label class="form-check-label">By registering, you confirm that you agree to the processing of your personal data by Group FiO as described in the Privacy Statement.</label>
                                </div>
                            </div>
                            <div class="form-group row  product ">
                               
                               <div id="reg-enbl-btn" class="col-sm-12" style="text-align: right;margin-bottom: 0px;">
                                  <a href="javascript:updateLeadInfo();"><span class="btn btn-sm btn-primary mt-2" style="width: 100%;">Register</span></a> 
                                </div>
                                <div id="reg-dis-btn" style="display:none" class="col-sm-12">
                                    <button class="btn btn-primary" style="width:100%" type="button" disabled>
                                        <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                                        Loading...
                                    </button>
                                </div>
                            </div>
                        </div>
                       <div class="col-md-12 col-lg-12 col-sm-12 alert-msg" id="wrn-msg" style="display:none">
                          <span class="upd-eml-msg" style=""><i class="fa fa-exclamation-circle" aria-hidden="true">&nbsp;</i><span>Please resubmit the form with valid email</span></span>
                       </div>
                  </form>
                  <form action="#" id="passcodeValidation" style="display:none" novalidate="novalidate" class="row">
                      <div class="col-md-12 col-lg-12 col-sm-12 ">
                          <div class="form-group row">
                                <label class="col-sm-12 col-form-label">Email<span class="text-danger"> *</span></label>
                               <div class="col-sm-12">
                                <input type="text" class="form-control" readonly name="email" placeholder="Email">
                                <span class="help-block" id="auth_email_error"></span>
                                </div>
                            </div>
                            <div class="form-group row">
                                <label class="col-sm-12 col-form-label">OTP<span class="text-danger"> *</span></label>
                               <div class="col-sm-12">
                                    <input type="password" id="otp" name="otp" class="form-control" placeholder="OTP " required="" aria-required="true">
                                <span class="help-block" id="otp_error"></span>
                                </div>
                            </div>
                            <div class="form-group row  product ">
                               <div id="otp-enbl-btn" class="col-sm-12" style="text-align: right;margin-bottom: 0px;">
                                  <a href="javascript:authenticateLeadInfo();"><span class="btn btn-sm btn-primary mt-2" style="width: 100%;">Validate OTP</span></a> 
                                </div>
                                <div id="otp-dis-btn" style="display:none" class="col-sm-12">
                                    <button class="btn btn-primary" style="width:100%" type="button" disabled>
                                        <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                                        Loading...
                                    </button>
                                </div>
                                <div class="col-sm-12 resend-otp"><a href="javascript:resendOtp();">Resend OTP</a></div>
                                <div class="col-sm-12 resend-otp" style="margin-top: auto;">Wrong Email? <a href="javascript:updateEmail();" class="resend-otp">Click Here</a></div>
                            </div>
                        </div>
                  </form>
              </div>
          </div></div>
      </div>
  </div>
  <script>
      $(document).ready(function(){
          $("#subscribe").modal('show');
	      $.validator.addMethod("alpha_dash", function(value, element) {
	          return this.optional(element) || /^[a-z0-9_]+$/i.test(value); 
	      }, "Alphanumerics, spaces, underscores & dashes only.");
	      $.validator.addMethod("emailId", function(value, element) {
	          return this.optional(element) || /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(value);
	      }, "Please enter valid Email");
	      $.validator.addMethod("phoneNo", function(value, element) {
	          return this.optional(element) || /^\d{10,13}$/.test(value);
	      }, "Please enter valid Phone Number");
	      $("#registrationForm").validate({
			onkeyup: function(element) {
				$(element).valid();
			},
			rules: {
				//processId: {"alpha_dash": true, "required": true},
				firstName: "required",
				lastName: "required",
				email: {"required":true,"emailId":true},
				phone: {"required":true,"phoneNo":true},
				companyName: "required",
				country: "required",
				terms: "required"
			},
			messages: {
				firstName: "Please enter First Name",
				lastName: "Please enter Last Name",
				email: {"required":"Please enter email","emailId":"Please enter valid Email"},
				phone: {"required":"Please enter phone number","phoneNo":"Please enter valid Phone Number"},
				companyName: "Please enter Company Name",
				country: "Please enter Country",
				terms: "Please accept Privacy Policy"
			},
			errorPlacement: function(error, element) {
				if(element.attr("name")){
					$("#"+element.attr("name")+"_error").text(error.html());
				}
			},
			unhighlight: function(element, errorClass, validClass) {
				$(element).removeClass(errorClass).addClass(validClass);
				$(element).closest(".form-group").find(".help-block").html("");
			}
		});
		$("#passcodeValidation").validate({
			onkeyup: function(element) {
				$(element).valid();
			},
			rules: {
				email: "required",
				otp: "required"
			},
			messages: {
				email: "Please enter Email",
				otp: "Please enter OTP"
				
			},errorPlacement: function(error, element) {
				
				if(element.attr("name")=="email")
				{
					$("#auth_email_error").text(error.html());
				}
				if(element.attr("name")=="otp")
				{
					$("#otp_error").text(error.html());
				}
			},
			unhighlight: function(element, errorClass, validClass) {
				$(element).removeClass(errorClass).addClass(validClass);
				$(element).closest(".form-group").find(".help-block").html("");
			}
		});
	});
      function updateLeadInfo(){
         $(".success-msg").hide();
         $(".error-msg").hide();
         $("#registrationForm .help-block").html("");
         if($("#registrationForm").valid()){
         $("#reg-enbl-btn").hide();
         $("#reg-dis-btn").show();
         $.ajax({
	        async: true,
	        url: '/common-portal/control/updateLeadInfo',
	        type: "POST",
	        data: $("#registrationForm").serialize(),
	        success: function(data) {
                if (data && data.result) {
                    if(data.result == "success"){
                       $("#registrationForm").hide();
					   $("#crm-img").css("padding", "30% 2%");
                       $("#passcodeValidation").show();
                       $("#passcodeValidation input[name='email']").val($("#registrationForm input[name='email']").val());
                       $("#success-msg").html("OTP sent to your email. Please enter the OTP to complete the registration process.");
                       $(".success-msg").show();
                       $("#otp-enbl-btn").show();
                    }else{
                       if(data.responseMsg)
                          $("#error-msg").html(data.responseMsg);
                       else
                          $("#error-msg").html("Error occured when updating customer details");
                       $(".error-msg").show();
                    }
                    $("#reg-enbl-btn").show();
                    $("#reg-dis-btn").hide();
                }
            }
         });
         }
      }
      function resendOtp(){
         $(".success-msg").hide();
         $(".error-msg").hide();
         $("#otp-enbl-btn").hide();
         $("#otp-dis-btn").show();
         $("#otp").val("");
         $.ajax({
	        async: true,
	        url: '/common-portal/control/reSendOtp',
	        type: "POST",
	        data: $("#passcodeValidation").serialize(),
	        success: function(data) {
                if (data && data.result) {
                    $("#otp-dis-btn").hide();
                    if(data.result == "success"){
                       $("#success-msg").html("OTP sent to your Email. Please enter the OTP to complete the registration process.");
                       $(".success-msg").show();
                    }else{
                       if(data.responseMsg)
                          $("#error-msg").html(data.responseMsg);
                       else
                          $("#error-msg").html("Error occured when sending OTP, please try again");
                       $(".error-msg").show();
                    }
                    $("#otp-enbl-btn").show();
                }
            }
         });
      }
      function authenticateLeadInfo(){
         $(".success-msg").hide();
         $(".error-msg").hide();
         $("#passcodeValidation .help-block").html("");
         if($("#passcodeValidation").valid()){
         $("#otp-enbl-btn").hide();
         $("#otp-dis-btn").show();
         $.ajax({
	        async: true,
	        url: '/common-portal/control/authenticateLeadInfo',
	        type: "POST",
	        data: $("#passcodeValidation").serialize(),
	        success: function(data) {
                if (data && data.result) {
                    $("#otp-dis-btn").hide();
                    if(data.result == "success"){
                       $("#success-msg").html("OTP validated successfully");
                       $(".success-msg").show();
                       window.location = window.location;
                    }else{
                       if(data.responseMsg)
                          $("#error-msg").html(data.responseMsg);
                       else
                          $("#error-msg").html("Error occured when validating OTP");
                       $(".error-msg").show();
                       $("#otp-enbl-btn").show();
                    }
                }
            }
         });
         }
      }
      function updateEmail(){
         $(".success-msg").hide();
         $(".error-msg").hide();
         $("#passcodeValidation").hide();
         $("#crm-img").css("padding", "50% 2%");
         $("#registrationForm input[name='email-old']").val($("#passcodeValidation input[name='email']").val());
         $("#reg-enbl-btn").find("span").text("Update");
         $("#wrn-msg").show();
         $("#registrationForm").show();
          $("#otp").val("");
      }
      
  </script>
  <!-- Bootstrap core JavaScript
  ================================================== -->
  <!-- Placed at the end of the document so the pages load faster -->
  <#-- <script>window.jQuery || document.write('<script src="themes/bootstrap/js/jquery.min.js"><\/script>')</script>
  <script src="themes/bootstrap/js/bootstrap.min.js"></script>-->
  <#-- <div class="modal-backdrop fade show"></div>-->
</body>
<style>
    body{
        background-color : #02829d !important;
    }
    .modal-title {
        margin-left: auto;
        margin-right: auto;
    }
    #registrationForm input[type="checkbox"]:checked:after {
        margin-top: 0px;
    }
    .help-block{
        color: #a94442;
    }
</style>
<style>
    body{color: rgba(41,42,51,.78);}

    /*fio-custom.css:276*/
    .form-control {
        font-size: 14px !important;
        color: #17181f;
        -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
        -moz-box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
        box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
        -webkit-transition: border linear .2s,box-shadow linear .2s;
        -moz-transition: border linear .2s,box-shadow linear .2s;
        -o-transition: border linear .2s,box-shadow linear .2s;
        transition: border linear .2s,box-shadow linear .2s;
        border: 1px solid rgba(61,64,78,.12);
        border-radius: 4px !important;
        padding: 0.375rem 0.3rem;
    }

    /*dropdown.css:388*/
    select.ui.dropdown {
        height: 35px !important;
        padding: 0.5em;
        border: 1px solid rgba(61,64,78,.12);
        visibility: visible;
    }

    .modal-content {
        height: auto;
        overflow-x: hidden;
        overflow-y: auto;
    }
    h3.modal-title.register-title {
        font-size: 1.5rem;
        font-weight: bold;
        color: #02829d;
    }
    .left-bg-row {
       background-color: #ffffff;
    }
    .right-bg-row {
       background-color: #eaeef1;
    }
    .modal-dialog {
       max-width: 900px;
       margin: 1.75rem auto;
       margin-top: 10%
   }
   .resend-otp {
	   margin-left: auto;
	   margin-right: auto;
	   margin-top: 2rem;
	   text-align: center !important;
   }
   span.backward-slash {
       color: #02829d;
   }
    span.success-msg {
        color: #71d571;
        border: 1px solid #d5e9d5;
        background-color: #e9f9ea;
        padding: 0.6rem 0.6rem;
        border-radius: 4px;
        font-size: 0.9rem;
        text-align: left;
        font-weight: 600;
        display: block;
    }
    span.error-msg {
        color: #db4b54;
        border: 1px solid #f7dfe0;
        background-color: #fdedee;
        padding: 0.6rem 0.6rem;
        border-radius: 4px;
        font-size: 0.9rem;
        font-weight: 600;
        text-align: left;
        display: block;
    }
    .alert-msg {
        margin: 0px 0px 8px 0px;
        padding: 0px;
        width: 100%;
}
.spinner-border-sm {
    width: 1rem;
    height: 1rem;
    border-width: 0.2em;
}
.spinner-border {
    display: inline-block;
    width: 2rem;
    height: 2rem;
    vertical-align: text-bottom;
    border: 0.25em solid currentColor;
    border-right-color: transparent;
    border-radius: 50%;
    -webkit-animation: spinner-border .75s linear infinite;
    animation: spinner-border .75s linear infinite;
}
.sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    overflow: hidden;
    clip: rect(0,0,0,0);
    white-space: nowrap;
    border: 0;
}
</style>
<style type="text/css">
  
.btn {
    display: inline-block;
    font-weight: 400;
    color: #212529;
    text-align: center;
    vertical-align: middle;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    background-color: transparent;
    border: 1px solid transparent;
    padding: .375rem .75rem;
    font-size: 1rem;
    line-height: 1.5;
    border-radius: .25rem;
    transition: color .15s ease-in-out, background-color .15s ease-in-out, border-color .15s ease-in-out, box-shadow .15s ease-in-out
}

@media (prefers-reduced-motion:reduce) {
    .btn {
        transition: none
    }
}

.btn:hover {
    color: #212529;
    text-decoration: none
}

.btn.focus,
.btn:focus {
    outline: 0;
    box-shadow: 0 0 0 .2rem rgba(0, 123, 255, .25)
}

.btn.disabled,
.btn:disabled {
    opacity: .65
}

.btn:not(:disabled):not(.disabled) {
    cursor: pointer
}

a.btn.disabled,
fieldset:disabled a.btn {
    pointer-events: none
}

.btn-primary {
    color: #fff;
    background-color: #02829d;
    border-color: #02829d;
}

.btn-primary:hover {
    color: #fff;
    background-color: #025d70;
    border-color: #025d70;
}
@media (prefers-reduced-motion:reduce) {
    .collapsing.width {
        transition: none
    }
}

@-webkit-keyframes spinner-border {
    to {
        -webkit-transform: rotate(360deg);
        transform: rotate(360deg)
    }
}

@keyframes spinner-border {
    to {
        -webkit-transform: rotate(360deg);
        transform: rotate(360deg)
    }
}

.spinner-border {
    display: inline-block;
    width: 2rem;
    height: 2rem;
    vertical-align: -.125em;
    border: .25em solid currentcolor;
    border-right-color: transparent;
    border-radius: 50%;
    -webkit-animation: .75s linear infinite spinner-border;
    animation: .75s linear infinite spinner-border
}

.spinner-border-sm {
    width: 1rem;
    height: 1rem;
    border-width: .2em
}
span.upd-eml-msg {
    padding: 0.6rem 0.6rem;
    border-radius: 4px;
    font-size: 0.9rem;
    font-weight: 600;
    text-align: left;
    display: block;
}
</style>