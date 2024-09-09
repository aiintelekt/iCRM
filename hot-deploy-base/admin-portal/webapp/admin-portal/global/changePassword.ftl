<#include StringUtil.wrapString(logoTemplateLocation!)!>
<#assign userLoginId = requestParameters.userLoginId?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
<#if !userLoginId?has_content && userLogin?has_content>
	<#assign userLoginId = userLogin.userLoginId!>
</#if>
<script type="text/javascript" src="/bootstrap/js/jquery.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/jquery.validate-1.15.1.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/custom.js"></script>
<style>
	.spanCursor{cursor: pointer;}
	#changePasswordForm .short{ color:#FF0000;font-weight: bold;
    font-family: system-ui;
    padding-left: 5px;
    padding-top: 4px; } 
	#changePasswordForm .weak{ color:#E66C2C; font-weight: bold;
    font-family: system-ui;
    padding-left: 5px;
    padding-top: 4px;} 
	#changePasswordForm .good{ color:#2D98F3; font-weight: bold;
    font-family: system-ui;
    padding-left: 5px;
    padding-top: 4px;} 
	#changePasswordForm .strong{ color:#006400; font-weight: bold;
    font-family: system-ui;
    padding-left: 5px;
    padding-top: 4px;}
</style>
<div class="col-md-3"></div>
<div class="col-md-6 padding-l ">
    <section class="login-form">
        <form method="post" name="changePasswordForm" id="changePasswordForm" action="<@ofbizUrl>changeUserPasswordService</@ofbizUrl>" role="login" class="form-horizontal">
            <@logo/>
            <h2>Reset your password</h2>
            <div class="help-block with-errors" id="login_error"></div>
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text value-text" style="padding-right: 50px;">User Login ID</span>
                </div>
                <input type="hidden" name="userLoginId" value="${userLoginId!}"/>
                <input id="userLoginId" type="text" class="form-control" name="userLoginId" value="${userLoginId!}" readonly >
            </div>
            
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text value-text" id="basic-addon1" style="padding-right: 23px;">Current Password</span>
                </div>
                <input type="password" class="form-control" id="oldPassword" name="oldPassword" placeholder="Current Password" value="" autocomplete="off"/>
					<span class="input-group-text spanCursor"><i class="fa fa-eye-slash" id="togglePasswords" onclick="togglePassword('oldPassword', 'togglePasswords')"></i></span>
            </div>
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text value-text" id="basic-addon1" style="padding-right: 41px;">New Password</span>
                </div>
                <input type="password" class="form-control" id="password" name="password" placeholder="New Password" value="" autocomplete="off"/>
					<span class="input-group-text spanCursor"><i class="fa fa-eye-slash" id="newPasswords" onclick="togglePassword('password', 'newPasswords')"></i></span><span id="password_strength"></span>
            </div>
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text value-text" id="basic-addon1" style="padding-right: 19px;">Confirm Password</span>
                </div>
                <input type="password" class="form-control" id="confirmpassword" name="confirmpassword" placeholder="Confirm Password" value="" autocomplete="off" />
					<span class="input-group-text spanCursor"><i class="fa fa-eye-slash" id="confirmPasswords" onclick="togglePassword('confirmpassword', 'confirmPasswords')"></i></span>
             </div>
            <div class="offset-md-2 col-sm-10">
                <input class="btn btn-sm btn-primary navbar-dark" type="submit" value="Reset Password"/>
                <a href="${requestParameters.requestUrl!}" class="btn btn-sm btn-primary navbar-dark">Back</a>
            </div>
            <div class="clearfix">
            </div>
        </form>
    </section>
</div>
<div class="col-md-3"></div>

<script>
$(function(){
	$("#changePasswordForm").validate({
		rules: {
			oldPassword:{
				required: true
			},
            password: {
            	required: true,
            	minlength: 6
            },
            confirmpassword: {
                equalTo: "#password",
                minlength: 6
            }
        },
        messages: {
        	oldPassword:{
        		required: "Please enter current password"
        	},
            password: {
            	required: "Please enter password",
            	minlength: jQuery.validator.format("Enter at least {0} characters")
            },
            confirmpassword:{
            	equalTo: "Password mismatch",
            	minlength: jQuery.validator.format("Enter at least {0} characters")
            } 
        }, 
        errorPlacement: function(error,element){
        	$("#login_error").html("");
        	error.appendTo($("#login_error"));
        }
	});
	
	$("#password").focusout(function(){
		$('#password_strength').html("");
	});
	
	$('#password').keyup(function () {
	    $('#password_strength').html(checkStrength($('#password').val()))
	});
	function checkStrength(password) { 
		
		var strength = 0 
		
		if (password.length < 6) { 
			$('#password_strength').removeClass(); 
			$('#password_strength').addClass('short'); 
			return 'Too short'; 
		} 
		
		if (password.length > 7) strength += 1;
		
		if (password.match(/([a-z].*[A-Z])|([A-Z].*[a-z])/)) strength += 1 ;
		
		if (password.match(/([a-zA-Z])/) && password.match(/([0-9])/)) strength += 1;
		
		if (password.match(/([!,%,&,@,#,$,^,*,?,_,~])/)) strength += 1;
		
		if (password.match(/(.*[!,%,&,@,#,$,^,*,?,_,~].*[!,",%,&,@,#,$,^,*,?,_,~])/)) strength += 1;
		
		if (strength < 2 ) { 
		 	$('#password_strength').removeClass(); 
		 	$('#password_strength').addClass('weak'); 
		 	return 'Weak'; 
		} else if (strength == 2 ) { 
		 	$('#password_strength').removeClass(); 
		 	$('#password_strength').addClass('good');
		 	return 'Good';
		} else { 
		 	$('#password_strength').removeClass(); 
		 	$('#password_strength').addClass('strong');
		 	return 'Strong'; 
		}
	}
});
</script>