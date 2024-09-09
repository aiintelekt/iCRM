<#include StringUtil.wrapString(iconTemplateLocation!)!>
<#include StringUtil.wrapString(logoTemplateLocation!)!>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <@icon/>
    <title>
        <#if titleProperty?has_content>${titleProperty?if_exists}</#if>
    </title>
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/login.css" rel="stylesheet">
    <link href="/bootstrap/css/blue.css" rel="stylesheet">
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/bootstrap/css/font-awesome.min.css" rel="stylesheet">
    <script src="/bootstrap/js/jquery.min.js">
    <script src="/bootstrap/js/highlight.min"></script>
    <link href="/bootstrap/css/animate.css" rel="stylesheet">
    </script>
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.min.js" crossorigin="anonymous"> </script> 
    <script type="text/javascript" language="javascript" src="/bootstrap/js/jquery-ui.js"></script>
    <!-- Custom  CSS -->   
	<link href="/bootstrap/css/fio-custom.css" rel="stylesheet">
	  
</head>
<body class="forget_password">
	<#include "component://bootstrap/includes/messages.ftl"/>
    <div class="container-fluid">
        <div class="container">
            <div class="row" id="pwd-container">
                <div class="col-md-3">
					<#-- 
                	<#assign url = request.getRequestURL()! />
    				<#assign requestUrl = url?split('/')?chunk(3)[0]?join("/") />
    				${requestUrl!} -->
    			</div>
                <div class="col-md-6">
                    <section class="login-form">
                        <form method="post" action="<@ofbizUrl>forgotPasswordEmail</@ofbizUrl>" role="login" class="form-horizontal">
                        	<@logo/>
                            <#assign requestURI = request.getRequestURI()/>
                            <#if requestURI.contains("/sr-mob-portal")>
                            	<input type="hidden" id="passwordScreenLocation" name="passwordScreenLocation" value="component://sr-mob-portal/widget/mobile/ServiceMobileScreens.xml#PasswordEmail" />
                            </#if>
                            
                            <div class="input-group mb-3">
                                <div class="input-group-prepend">
                                    <span class="input-group-text" id="basic-addon1"><i class="glyphicon glyphicon-user"></i></span>
                                </div>
                                <input id="login-username" type="text" class="form-control" name="USERNAME" value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>" placeholder="Username/Email">
                            </div>
                            <ul class="list-unstyled">
                            	<li class="help-block with-errors buttontext" id="login-username_error" style="text-align:center;"></li>
                            </ul>
                            <div class="clearfix"></div>
                            <div class="pwstrength_viewport_progress"></div>
                                <input type="submit" name="EMAIL_PASSWORD" class="btn btn-md btn-primary btn-block font-weight-bold" onclick="javascript:return validate();" value="${uiLabelMap.SendPasswordResetEmail}">
                                <div class="clearfix"></div>
                                <div class="pt-2">
                                    <a href='<@ofbizUrl>authview</@ofbizUrl>' class="buttontext">${uiLabelMap.ReturnToLogin}</a>
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
    <script type="text/javascript" language="javascript" src="/bootstrap/js/bootstrap-notify.min.js"></script>
    <script>
        $('#login-username').keyup(function () {
        var username  = $("#login-username").val();
        if(username != "")
            $("#login-username_error").hide();
        });
        function validate(){
            var username  = $("#login-username").val();
            if(username)
                username = username.trim();
            if(username == ""){
                $("#login-username_error").html("Please enter username/email");
                $("#login-username_error").show();
                return false;
            }else{
                $("#login-username_error").hide();
                return true;
            }
        }
    </script>
</body>
</html>