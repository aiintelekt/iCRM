<#if Static["org.groupfio.common.portal.util.UtilCommon"].isRegistrationDoneOrNotNeeded(delegator)>
<#include StringUtil.wrapString(logoTemplateLocation!)!>

<script type="text/javascript" src="/bootstrap/js/custom.js"></script>
<style>
	.spanCursor{cursor: pointer;}
</style>
<div class="container-fluid">
   <div>
      <div class="row" id="pwd-container">
         <div class="col-md-6 padding-l ">
            <section>
               <img src="/bootstrap/images/icrm-img.jpg" alt="icrm banner image" class="img-responsive login-leftimg">
            </section>
         </div>
         
         <div class="col-md-5">
            <section class="login-form">
               <form method="post" action="<@ofbizUrl>main</@ofbizUrl>" role="login" class="form-horizontal">
                  <@logo class="img-responsive login-logo"/>
                  <h2>Log in to your account</h2>
                  <div class="input-group mb-3">
                     <div class="input-group-prepend"> <span class="input-group-text" id="basic-addon1"><i class="glyphicon glyphicon-user"></i></span> </div>
                     <input id="login-username" type="text" class="form-control" name="USERNAME" value="" placeholder="Username"> 
                  </div>
                  <div class="input-group mb-3">
                     <div class="input-group-prepend"> <span class="input-group-text" id="basic-addon1"><i class="glyphicon glyphicon-lock"></i></span> </div>
                     <input id="login-password" type="password" class="form-control" name="PASSWORD" placeholder="Password"> 
                     <span class="input-group-text spanCursor"><i class="fa fa-eye-slash" id="login-password-verify" onclick="togglePassword('login-password', 'login-password-verify')"></i></span>
                  </div>
                  <div class="pwstrength_viewport_progress"></div>
                  <input class="btn btn-md btn-primary btn-block font-weight-bold" type="submit" value="Log in" />
                  <div class="clearfix"> </div>
                  <div class="pt-2"> Forgot your password? <a href="<@ofbizUrl>forgotPassword</@ofbizUrl>">Reset Password</a> </div>
               </form>
            </section>
         </div>
         <div class="col-md-1"></div>
      </div>
   </div>
</div>
<#else>
	<#include "registrationForm.ftl">
</#if>