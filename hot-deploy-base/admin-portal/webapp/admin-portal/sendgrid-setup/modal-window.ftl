<style>
	.popover.confirmation{
	z-index:1000000000!important;
	}
</style>
<div>
	<div id="editSendGridConfig" class="modal fade" role="dialog">
		<div class="modal-dialog modal-md modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">Edit Sendgrid Setup</h4>
					<button type="reset" class="close" data-dismiss="modal">&times;</button>
				</div>
				<div class="modal-body" style="padding-bottom: 8px;">
					<div class="popup-bot">
						<form id="updateSendGridConfig" name="editSendGridConfigForm" action="#" method="post" data-toggle="validator" class="form-horizontal" novalidate="novalidate">
						<@inputHidden id="check" value=""/>
						<@dynaScreen 
							instanceId="UPDATE_SENDGRID_SETUP"
							modeOfAction="UPDATE"
							/>
						<div class="modal-footer" id="addFooter">
							<div class="text-left ml-1">
								<input type="submit" class="btn btn-sm btn-primary disabled" id="updateSendgrid" value="Update"/>
								<button type="submit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Cancel</button>
							</div>
						</div>
						<form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>