<#include StringUtil.wrapString(iconTemplateLocation!)!>
<#include StringUtil.wrapString(logoTemplateLocation!)!>
<#assign username = requestParameters.userLoginId?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="Muruganantham">
    <@icon/>
    <title>
        Enter User Name
    </title>
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/login.css" rel="stylesheet">
    <link href="/bootstrap/css/blue.css" rel="stylesheet">
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/bootstrap/css/font-awesome.min.css" rel="stylesheet">
    <script src="/bootstrap/js/jquery.min.js">
    <script src="/bootstrap/js/bootstrap-notify.min.js"></script>
    <script src="/bootstrap/js/bootstrap-notify.js"></script>
    <script src="/bootstrap/js/highlight.min"></script>
    <link href="/bootstrap/css/animate.css" rel="stylesheet">
    </script>
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
	    
</head>
<body class="forget_password">
    <div class="container-fluid">
        <div class="container">
            <div class="row" id="pwd-container">
                <div class="col-md-3">
                    <#assign username = requestParameters.userLoginId?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
                </div>
                <div class="col-md-6">
                    <section class="login-form">
                        <form method="post" action="<@ofbizUrl>resetPasswordAction</@ofbizUrl>" role="login" class="form-horizontal">
                        <@logo class="img-responsive login-logo"/>
                        <h2>Reset your password</h2>
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                                <span class="input-group-text value-text">User Login ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
                            </div>
                                <input type="hidden" name="userLoginId" value="${username}" />
                                <input type="hidden" name="partyId" value="<#if requestParameters.partyId?has_content>${requestParameters.partyId}</#if>" />
                                <input id="login-username" type="text" class="form-control" name="USERNAME" value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>" placeholder="Username">
                            </div>
                            <div class="col-12">
                                <input class="btn btn-sm  btn-primary navbar-dark" type="submit" value="Reset Password" />
                                <a href="viewUserDetail?userLoginId=<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>" class="btn btn-sm  btn-primary navbar-dark">Back</a>
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
</body>
</html>	