<!-- BEGIN FORGOT PASSWORD FORM -->
<form class="forget-form" action="/custom-field/control/forgotpassword" method="post" name="forgotpassword" data-toggle="validator" role="form">
	<h3>Forget Password ?</h3>
	<p>
		 Enter your username below to reset your password.
	</p>
	<div class="form-group">
		<div class="input-icon">
			<i class="fa fa-envelope"></i>
			<input class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="Username" name="USERNAME" required/>
		</div>
	</div>
	<div class="form-actions">
		<button type="button" id="back-btn" class="btn">
		<i class="m-icon-swapleft"></i> Back </button>
		<button type="submit" class="btn blue pull-right">
		Submit <i class="m-icon-swapright m-icon-white"></i>
		</button>
	</div>
</form>
<!-- END FORGOT PASSWORD FORM -->