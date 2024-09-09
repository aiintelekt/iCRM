<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main">
		<div class="clearfix"></div>
		<form id="mainFrom" method="post" data-toggle="validator" onsubmit="return submitCreateNavTab();">
			<div class="col-lg-12 col-md-12 col-sm-12">
			<@sectionFrameHeader title="${uiLabelMap.CreateNavTab!}" extra=extra/>
			<@dynaScreen
			instanceId="CREATE_NAV_TAB"
			modeOfAction="CREATE"
			/>
			</div>
			<div class="clearfix"></div>
			<div class="offset-md-2 col-sm-10">
			<@formButton
			btn1type="submit"
			btn1label="${uiLabelMap.Save}"
			btn1id="create-nav-tab-button"
			btn2=true
			btn2onclick = "resetForm()"
			btn2type="reset"
			btn2id="reset-nav-tab-button"
			btn2label="${uiLabelMap.Clear}"
			/>
			</div>
		</form>
	</div>
</div>
