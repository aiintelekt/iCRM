<#include StringUtil.wrapString(logoTemplateLocation!)!>
<script type="text/javascript" src="/bootstrap/js/jquery.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/jquery.validate-1.15.1.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/custom.js"></script>
<style>
	.spanCursor{cursor: pointer;}
</style>
    <div class="container-fluid">
        <div class="container">
            <div class="row" id="pwd-container">
                <div class="col-md-3">
                    <#assign username = requestParameters.userLoginId?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
                    <#assign userLoginPartyId = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("partyId").from("UserLogin").where("userLoginId",username!).queryOne())?if_exists />
                </div>
                <div class="col-md-6">
                   <section class="login-form">
                       <form method="post" action="<@ofbizUrl>changePasswordAction</@ofbizUrl>" role="login" class="form-horizontal">
                           <@logo class="img-responsive login-logo" />
                           <h2>Reset your password</h2>
                           <div class="input-group mb-3">
                               <div class="input-group-prepend">
                                   <span class="input-group-text value-text">User Login ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
                               </div>
                               <input type="hidden" name="userLoginId" value="${username}" />
                               <input id="userLoginId" type="text" class="form-control value-text" name="userLoginId" value="${username}" readonly>
                           </div>
                           <#if !request.getRequestURI().contains("admin-portal")>
                               <div class="input-group mb-3">
                                   <div class="input-group-prepend">
                                       <span class="input-group-text value-text" id="basic-addon1">Temp Password&nbsp;&nbsp;&nbsp;</span>
                                   </div>
                                   <input type="password" class="form-control" name="oneTimePassword" placeholder="Temp Password" value="" />
                               </div>
                           </#if>
                           <div class="input-group mb-3">
                               <div class="input-group-prepend">
                                   <span class="input-group-text value-text" id="basic-addon1">New Password&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
                               </div>
                               <input type="password" id="password" class="form-control" name="newPassword" placeholder="New Password" value="" />
                               <span class="input-group-text spanCursor"><i class="fa fa-eye-slash" id="newPasswords" onclick="togglePassword('password', 'newPasswords')"></i></span>
                           </div>
                           <ul class="list-unstyled offset-md-2 col-sm-10">
                               <li class="help-block with-errors" id="newPassword_error" style="text-align:center;"></li>
                           </ul>
                           <div class="input-group mb-3">
                               <div class="input-group-prepend">
                                   <span class="input-group-text value-text" id="basic-addon1">Confirm Password</span>
                               </div>
                               <input type="password" id="newPasswordVerify" class="form-control" name="newPasswordVerify" placeholder="Confirm Password" value="" />
                               <span class="input-group-text spanCursor"><i class="fa fa-eye-slash" id="newPasswordVerifys" onclick="togglePassword('newPasswordVerify', 'newPasswordVerifys')"></i></span>
                           </div>
                           <div class="offset-md-2 col-sm-10">
                               <ul class="list-unstyled">
                                   <li class="help-block with-errors" id="confirmPassword_error" style="text-align:center;"></li>
                               </ul>
                           </div>
                           <div class="pwstrength_viewport_progress"></div>
                           <div class="offset-md-2 col-sm-10">
                               <input class="btn btn-sm btn-primary navbar-dark" type="submit" onclick="javascript:return onSubmitValidate(this);" value="Reset Password" />
                               <a href="resetPassword?partyId=<#if userLoginPartyId.partyId?has_content>${userLoginPartyId.partyId}</#if>&USERNAME=${username}" class="btn btn-sm  btn-primary navbar-dark">Back</a>
                           </div>
                           <div class="clearfix">
                           </div>
                       </form>
                   </section>
                </div>
                <div class="col-md-3"></div>
            </div>
        </div>
    </div>
    <div class="text-center footer-img">
        <img style="display: inline-block;" class="img-fluid" src="/bootstrap/images/footer-bg.png" alt="">
    </div>

<script>
	$('#password').keyup(function (){
		var password =document.getElementsByName("newPassword")[0].value;
		if(password != "")
			$("#newPassword_error").hide();
		});
	$('#newPasswordVerify').keyup(function (){
		var confirmPassword =document.getElementsByName("newPasswordVerify")[0].value;
		if(confirmPassword != "")
			$("#confirmPassword_error").hide();
		});
	function onSubmitValidate(){
		var password =document.getElementsByName("newPassword")[0].value;
		var confirmPassword =document.getElementsByName("newPasswordVerify")[0].value;
		if(password != "" && confirmPassword != "" && password != confirmPassword){
			$("#newPassword_error").hide();
			$('#confirmPassword_error').html("Password mismatch");
			$('#confirmPassword_error').show();
			return false;
		}else if(password == "" && confirmPassword == ""){
			$("#newPassword_error").html("Please enter new password");
			$("#newPassword_error").show();
			$("#confirmPassword_error").html("Please enter confirm password");
			$("#confirmPassword_error").show();
			return false;
		}else if(confirmPassword == ""){
			$("#newPassword_error").hide();
			$("#confirmPassword_error").html("Please enter confirm password");
			$("#confirmPassword_error").show();
			return false;
		}else if(password == ""){
			$("#newPassword_error").html("Please enter new password");
			$("#newPassword_error").show();
			$("#confirmPassword_error").hide();
			return false;
		}else{
			$("#newPassword_error").hide();
			$("#confirmPassword_error").hide();
			return true;
		}
	}
</script>