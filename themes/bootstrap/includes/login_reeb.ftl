<#include StringUtil.wrapString(logoTemplateLocation!)!>
<style>

.login-leftimg {
	width: 49vw;
}

@media only screen and (max-width: 990px) @media only screen and (max-width: 768px) {
	.login-form {
		margin-top: 20px !important;
	}

	.padding-l {
		-webkit-box-ordinal-group: 2;
		-moz-box-ordinal-group: 2;
		-ms-flex-order: 2;
		-webkit-order: 2;
		order: 2;
		margin-bottom: 4vw !important;
	}
}

</style>

<div class="container-fluid">
	<div class="">
		<div class="row" id="pwd-container">
			<div class="col-md-6 padding-l ">
				<section class=""> <img class="img-responsive login-leftimg" src="/bootstrap/images/reebfsp_login_black1.jpg"> </section>
			</div>
			<div class="col-md-5">
				<section class="login-form">
					<form method="post" action="<@ofbizUrl>main</@ofbizUrl>" role="login" class="form-horizontal"> <@logo class="img-responsive login-logo"/>
						<h2>Log In to iCRM</h2>
						<div class="input-group mb-3">
							<div class="input-group-prepend"> <span class="input-group-text" id="basic-addon1"><i class="glyphicon glyphicon-user"></i></span> </div>
							<input id="login-username" type="text" class="form-control" name="USERNAME" value="" placeholder="Username"> </div>
						<div class="input-group mb-3">
							<div class="input-group-prepend"> <span class="input-group-text" id="basic-addon1"><i class="glyphicon glyphicon-lock"></i></span> </div>
							<input id="login-password" type="password" class="form-control" name="PASSWORD" placeholder="Password"> </div>
						<div class="pwstrength_viewport_progress"></div>
						<input class="btn btn-md btn-primary btn-block font-weight-bold" type="submit" value="Log In">
						<div class="clearfix"> </div>
						<div class="pt-2"> <a href="<@ofbizUrl>forgotPassword</@ofbizUrl>">Forgot Password?</a> </div>
					</form>
				</section>
			</div>
			<div class="col-md-1"></div>
		</div>
	</div>
</div>