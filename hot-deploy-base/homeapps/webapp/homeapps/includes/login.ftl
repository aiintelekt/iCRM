<#include StringUtil.wrapString(iconTemplateLocation!)!>
<#include StringUtil.wrapString(logoTemplateLocation!)!>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="Muruganantham">
    <@icon/>
    <title><#if titleProperty?has_content>${titleProperty?if_exists}</#if></title>
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/login.css" rel="stylesheet">
    <link href="/bootstrap/css/blue.css" rel="stylesheet">
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/bootstrap/css/font-awesome.min.css" rel="stylesheet">
    <script src="/bootstrap/js/jquery.min.js"> </script>
    <script src="/bootstrap/js/bootstrap-notify.min.js"></script>
    <script src="/bootstrap/js/bootstrap-notify.js"></script>
    <script src="/bootstrap/js/highlight.min"></script>
    <link href="/bootstrap/css/animate.css" rel="stylesheet">
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
<#if (errorMessage?has_content || errorMessageList?has_content)>
<script>
$(document).ready(function() {
  $.notify({
	title: '${uiLabelMap.CommonFollowingErrorsOccurred}',
	<#if errorMessage?has_content>
		message:'<p>${StringUtil.wrapString(errorMessage)}</p>',
    </#if>
	<#if errorMessageList?has_content>
        <#list errorMessageList as errorMsg>
     		message:'<p>${StringUtil.wrapString(errorMsg)}</p>',
        </#list>
    </#if>
	target: '_blank'
	},{
		element: 'body',
		position: null,
		type: "danger",
		allow_dismiss: true,
		newest_on_top: false,
		showProgressbar: false,
		placement: {
			from: "top",
			align: "right"
		},
		offset: {
				x: 0,
				y: 20
			},
		spacing: 10,
		z_index: 1031,
		delay: 8000,
		timer: 5000,
		url_target: '_blank',
		mouse_over: null,
		animate: {
			enter: 'animated fadeInDown',
			exit: 'animated fadeOutUp'
		},
		icon_type: 'class',
		template: '<div data-notify="container" class="col-sm-3 alert alert-{0}" role="alert">' +
			'<button type="button" aria-hidden="true" class="close" data-notify="dismiss">×</button>' +
			'<span data-notify="icon"></span> ' +
			'<span data-notify="title">{1}</span> ' +
			'<span data-notify="message">{2}</span>' +
			'<div class="progress" data-notify="progressbar">' +
				'<div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>' +
			'</div>' +
			'<a href="{3}" target="{4}" data-notify="url"></a>' +
		'</div>' 
  	});
  });
  </script>
</#if>

  </head>
  <body>

    <div class="container-fluid">
      <div class="container">
        <div class="row" id="pwd-container">
          <div class="col-md-4"></div>
          <div class="col-md-4 padding-l ">
            <section class="login-form">
            <#--<#if (errorMessage?has_content || errorMessageList?has_content || eventMessage?has_content || eventMessageList?has_content)>
				<div class="alert alert-danger alert-dismissible show in " id="alert">
				    <button type="button" class="close" data-dismiss="alert">x</button>
				    <strong>${uiLabelMap.CommonFollowingErrorsOccurred}:</strong>
			 	 <#if errorMessage?has_content>
			        <p>${StringUtil.wrapString(errorMessage)}</p>
			      </#if>
			      <#if errorMessageList?has_content>
			        <#list errorMessageList as errorMsg>
			          <p>${StringUtil.wrapString(errorMsg)}</p>
			        </#list>
			     </#if>
				</div>
				<script>
					 $("#alert").fadeTo(2000, 500).slideUp(500, function(){
		              	 $("#alert").slideUp(1000);
		                });  
				</script>
			</#if> -->
              <form method="post" action="<@ofbizUrl>main</@ofbizUrl>" role="login" class="form-horizontal">
                <@logo width="80%" />
				<h2>Log in to your account</h2>
				<div class="input-group mb-3">
                <div class="input-group-prepend">
                	<span class="input-group-text" id="basic-addon1"><i class="glyphicon glyphicon-user"></i></span>
                </div>
               <input id="login-username" type="text" class="form-control" name="USERNAME" value="" placeholder="Username">     
            </div>
				<div class="input-group mb-3">
                <div class="input-group-prepend">
                <span class="input-group-text" id="basic-addon1"><i class="glyphicon glyphicon-lock"></i></span>
               </div>
               <input id="login-password" type="password" class="form-control" name="PASSWORD" placeholder="Password">
            </div>
                <div class="pwstrength_viewport_progress"></div>
                <#--<a href="" name="go" class="btn btn-md btn-primary btn-block">Sign in</a> -->
                <input type="submit" name="go" class="btn btn-md btn-primary btn-block" value="Sign in">
                <div>
                  <a href="<@ofbizUrl>forgotPassword_step1</@ofbizUrl>">Forgot your password?</a>
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
    <!-- Bootstrap core JavaScript
      ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script>window.jQuery || document.write('<script src="/bootstrap/js/jquery.min.js"><\/script>')</script>
    <script src="/bootstrap/js/bootstrap.min.js"></script>
    <script>
		$(document).ready (function(){
		            $("#success-alert").hide();
		            $("#myWish").click(function showAlert() {
		                $("#success-alert").fadeTo(2000, 500).slideUp(500, function(){
		              	 $("#success-alert").slideUp(500);
		                });   
		            });
		 });
	</script>
  </body>
</html>


