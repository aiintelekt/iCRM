<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<form method="post" id="lov-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				<input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
				<div><@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.Lov}"/></div>
				<@dynaScreen
					instanceId="LOV_FIND"
					modeOfAction="CREATE"
					/>
				<div class="offset-md-2 col-sm-12">
					<@button
						id="main-search-btn"
						label="${uiLabelMap.Find}"
						/>
					<@reset
						label="${uiLabelMap.Reset}"
						/>
				</div>
				<br>
			</form>
		</div>
	</div>
</div>
	
<script>     
$(document).ready(function() {

});
</script>