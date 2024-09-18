<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign extra='<a href="/admin-portal/control/accessSetup" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
<div class="row">
	<div id="main" role="main">
		<form id="mainFrom" method="post" action="<@ofbizUrl>createAccessSetupAction</@ofbizUrl>" data-toggle="validator">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@sectionFrameHeader title="${uiLabelMap.CreateAccessSetup!}" extra=extra/>
				<@dynaScreen
					instanceId="CREATE_ACCESS_SETUP"
					modeOfAction="CREATE"
					/>
			</div>
			<br>
			<div class="offset-md-2 col-sm-12">
				<@formButton
					btn1type="submit"
					btn1label="${uiLabelMap.Save}"
					btn2=true
					btn2onclick = "resetForm()"
					btn2type="reset"
					btn2label="${uiLabelMap.Clear}"
					/>
			</div>
		</form>
	</div>
</div>
<script>
 $(document).ready(function() {
	initDateRange("fromDate_picker", "thruDate_picker", null, null);
});
</script>