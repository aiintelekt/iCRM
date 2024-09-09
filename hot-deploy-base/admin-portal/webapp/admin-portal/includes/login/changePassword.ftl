<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#-- 
<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
<#assign tenantId = requestParameters.userTenantId!>

<center>
<div class="screenlet login-screenlet">
<#assign forgotPwdFlag = parameters.forgotPwdFlag?has_content />
<div class="login-screenlet">
  <div class="screenlet-title-bar">
    <h3>${uiLabelMap.CommonPasswordChange}</h3>
  </div>
  <div class="screenlet-body">
    <form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform">
      <input type="hidden" name="requirePasswordChange" value="Y"/>
      <input type="hidden" name="USERNAME" value="${username}"/>
      <input type="hidden" name="userTenantId" value="${tenantId}"/>
      <input type="hidden" name="forgotPwdFlag" value="${parameters.forgotPwdFlag!}" />
      <table cellspacing="0">
        <tr>
          <td class="label">${uiLabelMap.CommonUsername}</td>
          <td>${username}</td>
        </tr>
        <#if forgotPwdFlag?has_content && forgotPwdFlag?string == "true">
          <tr>
            <td><input type="hidden" name="PASSWORD" value="${parameters.password!}" size="20"/></td>
          </tr>
        <#else>
          <tr>
            <td class="label">${uiLabelMap.CommonCurrentPassword}</td>
            <td><input type="password" name="PASSWORD" value="" size="20" /></td>
          </tr>
        </#if>
        <tr>
          <td class="label">${uiLabelMap.CommonNewPassword}</td>
          <td><input type="password" name="newPassword" value="" size="20"/></td>
        </tr>
        <tr>
          <td class="label">${uiLabelMap.CommonNewPasswordVerify}</td>
          <td><input type="password" name="newPasswordVerify" value="" size="20"/></td>
        </tr>
        <#if securityQuestion?has_content>
          <tr>
            <td class="label">${uiLabelMap.SecurityQuestiom}</td>
            <td>
              <input type="hidden" name="securityQuestion" value="${securityQuestion.enumId!}" />
                ${securityQuestion.description!}
            </td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.SecurityAnswer}</td>
            <td><input type="text" class='inputBox' name="securityAnswer" id="SECURITY_ANSWER" value="" maxlength="100" /></td>
          </tr>
        </#if>
        <tr>
          <td colspan="2" align="center">
            <input type="submit" value="${uiLabelMap.CommonSubmit}"/>
          </td>
        </tr>
      </table>
    </form>
  </div>
</div>
</center>

<script language="JavaScript" type="text/javascript">
  document.loginform.PASSWORD.focus();
</script>
-->
<#include StringUtil.wrapString(logoTemplateLocation!)!>
<#assign userLoginId = requestParameters.userLoginId?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
<#if !userLoginId?has_content && userLogin?has_content>
	<#assign userLoginId = userLogin.userLoginId!>
</#if>
<#assign userLoginId = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
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
        <form method="post" name="changePasswordForm" id="changePasswordForm" action="<@ofbizUrl>login</@ofbizUrl>" role="login" class="form-horizontal">
            <@logo/>
            <input type="hidden" name="requirePasswordChange" value="Y"/>
		    <input type="hidden" name="USERNAME" value="${userLoginId!}"/>
		    <input type="hidden" name="forgotPwdFlag" value="${parameters.forgotPwdFlag!}" />
            <input type="hidden" name="PASSWORD" value="${parameters.password!}" />
            <h2>Reset your Login</h2>
            <div class="help-block with-errors" id="login_error"></div>
            <#-- 
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
                <input type="password" class="form-control" name="PASSWORD" placeholder="Current Password" value="" autocomplete="off"/>
            </div> -->
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text value-text" id="basic-addon1" style="padding-right: 41px;">New Password</span>
                </div>
                <input type="password" class="form-control" id="newPassword" name="newPassword" placeholder="New Password" value="" autocomplete="off"/>
                <span class="input-group-text spanCursor"><i class="fa fa-eye-slash" id="newPasswords" onclick="togglePassword('newPassword', 'newPasswords')"></i></span>
                <span id="password_strength"></span>
            </div>
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text value-text" id="basic-addon1" style="padding-right: 19px;">Confirm Password</span>
                </div>
                <input type="password" class="form-control" id="newPasswordVerify" name="newPasswordVerify" placeholder="Confirm Password" value="" autocomplete="off" />
                <span class="input-group-text spanCursor"><i class="fa fa-eye-slash" id="newPasswordVerifys" onclick="togglePassword('newPasswordVerify', 'newPasswordVerifys')"></i></span>
            </div>
            <div class="offset-md-2 col-sm-10">
                <input class="btn btn-sm btn-primary navbar-dark" type="submit" value="Reset Password"/>
                <#-- <a href="${requestParameters.requestUrl!}" class="btn btn-sm btn-primary navbar-dark">Back</a> -->
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
            newPassword: {
            	required: true,
            	minlength: 6
            },
            newPasswordVerify: {
                equalTo: "#newPassword",
                minlength: 6
            }
        },
        messages: {
        	oldPassword:{
        		required: "Please enter current password"
        	},
            newPassword: {
            	required: "Please enter password",
            	minlength: jQuery.validator.format("Enter at least {0} characters")
            },
            newPasswordVerify:{
            	equalTo: "Password mismatch",
            	minlength: jQuery.validator.format("Enter at least {0} characters")
            } 
        }, 
        errorPlacement: function(error,element){
        	$("#login_error").html("");
        	error.appendTo($("#login_error"));
        }
	});
	
	$("#newPassword").focusout(function(){
		$('#password_strength').html("");
	});
	
	$('#newPassword').keyup(function () {
	    $('#password_strength').html(checkStrength($('#newPassword').val()))
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
