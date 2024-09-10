<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<#assign extraLeft = ""/>
	<#assign extraLeft = '<div class="form-check-inline mr-2 mb-0 mt-1">
			    <label for="filterTypeMy">
			    <input type="radio" id="filterTypeMy" name="filterType" value="my-lead" class="form-check-input" checked>
			    <span></span>
			    <span class="check"></span>
			    <span class="box"></span>
			 	My Leads</label>
			</div>
			<div class="form-check-inline mr-2 mb-0 mt-1">
			    <label for="filterTypeTeam">
			    <input type="radio" id="filterTypeTeam" name="filterType" value="my-team-lead" class="form-check-input">
			    <span></span>
			    <span class="check"></span>
			    <span class="box"></span>
			    My Team\'s Leads</label>
			</div>
			<div class="form-check-inline mr-2 mb-0 mt-1">
			    <label for="filterTypeBu">
			    <input type="radio" id="filterTypeBu" name="filterType" value="my-bu-lead" class="form-check-input">
			    <span></span>
			    <span class="check"></span>
			    <span class="box"></span>
			    Company Leads</label>
			</div>
			'/>
			
	<#assign extraLeft = extraLeft />

	<@sectionFrameHeader title="Lead Home" extraLeft=extraLeft! leftCol="col-lg-7 col-md-12 col-sm-12" rightCol="col-lg-5 col-md-12 col-sm-12" />
	<@AppBar 
		appBarId="MY_LEAD_DASH"
		appBarTypeId="DASHBOARD"
		id="appbar1"
		isEnableUserPreference=true
		animateEffect="bounce"
		/>
</div>