
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<@sectionFrameHeader title="Customer Home" />

	<@AppBar 
		appBarId="MY_CUST_DASH"
		appBarTypeId="DASHBOARD"
		id="appbar1"
		isEnableUserPreference=true
		/>
</div>

<script>
	$('#open-calls').click(function(){
		window.location.href = "/customer-portal/control/outBoundCallList";
	});
</script>