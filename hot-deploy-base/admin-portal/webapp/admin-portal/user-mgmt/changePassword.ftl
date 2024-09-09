<#include StringUtil.wrapString(iconTemplateLocation!)!>
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
<body>
    <div class="container-fluid">
        <div class="container">
            <div class="row" id="pwd-container">
                <div class="col-md-4"></div>
                <div class="col-md-4 padding-l ">
                    <section class="login-form">
                        <form method="post" action="<@ofbizUrl>changePasswordAction</@ofbizUrl>" role="login" class="form-horizontal">
                            
                        	<input type="hidden" name="userLoginId" value="${username}"/>
                        
                        	<div><center><b>Enter Password</b></center></div>
                        
                        	<div></div>
      
					      	<table cellspacing="0">
					        	<tr>
					          		<td class="label">${uiLabelMap.UserName}</td>
					          		<td>${username}</td>
					        	</tr>
					        
					        	<tr>
					          		<td class="label">${uiLabelMap.NewPassword}</td>
					          		<td><input type="password" name="newPassword" value="" size="20"/></td>
					        	</tr>
					        
					        	<tr>
					          		<td class="label">${uiLabelMap.ConfirmPassword}</td>
					          		<td><input type="password" name="newPasswordVerify" value="" size="20"/></td>
					        	</tr>
					        
					        	<tr>
					          		<td colspan="2" align="center">
					            		<input type="submit" value="${uiLabelMap.CommonSubmit}"/>
					          		</td>
					        	</tr>
					      </table>
                            
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
    <script>
        window.jQuery || document.write('<script src="/bootstrap/js/jquery.min.js"><\/script>')
    </script>
    <script src="/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>




