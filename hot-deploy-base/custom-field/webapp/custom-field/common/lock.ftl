<link href="/cf-resource/css/lock2.css" rel="stylesheet" type="text/css"/>

<style type="text/css">
.login .content {
	background: none;
}
</style>

<!-- BEGIN LOCK FORM -->
	
<div class="page-lock">
	<#--<div class="page-logo">
		<a class="brand" href="index.html">
		<img src="../../assets/admin/layout4/img/logo-big.png" alt="logo"/>
		</a>
	</div>-->
	<div class="page-body">
		<img class="page-lock-img" src="/metronic/img/media/profile.jpg" alt="">
		<div class="page-lock-info">
			<h1>${loggedPartyName!}</h1>
			<span class="email">
			<#if loggedPartyEmail?exists>
				${loggedPartyEmail}
			<#else>
				${userLogin.userLoginId}	
			</#if>
			</span>
			<span class="locked">
			Locked </span>
			
			<#--
			<div class="alert alert-danger display-hide">
				<button class="close" data-close="alert"></button>
				<span>
				Enter any username and password. </span>
			</div>
			-->
			
			<form class="form-inline" action="<@ofbizUrl>lock</@ofbizUrl>" data-toggle="validator" method="post" role="form">
				<input type="hidden" name="TRY_UNLOCK" value="TRUE">
				<div class="input-group input-medium">
					<input type="password" class="form-control" placeholder="Password" name="PASSWORD" required>
					<span class="input-group-btn">
					<button type="submit" class="btn blue icn-only"><i class="m-icon-swapright m-icon-white"></i></button>
					</span>
				</div>
				<!-- /input-group -->
				<div class="relogin">
					<a href="<@ofbizUrl>logout</@ofbizUrl>">
					Not ${loggedPartyName!} ? </a>
				</div>
			</form>
		</div>
	</div>
	<div class="page-footer-custom">
		 2015 &copy; El Seif Engineering Contracting. All Rights Reserved
	</div>
</div>

<!-- END LOCK FORM -->