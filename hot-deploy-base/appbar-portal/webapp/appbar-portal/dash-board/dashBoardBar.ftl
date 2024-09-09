<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
      <@sectionFrameHeader  title="Example App Bars" />
      <div class="col-lg-12 col-md-12 col-sm-12 mid">
        <h1>Sample Dashboard Bar</h1>
		<div class="dashboard-adj">
			<@AppBar
				appBarId="SALES_DASHBOARD"
				appBarTypeId="DASHBOARD"
				id="appbar1"
		        isEnableUserPreference=true
		        colSize="col-xl-3 col-lg-6 col-md-12 col-sm-12"
	            />
		</div>
		<h1>Sample KPI Bar</h1>
        <div class="card-head margin-adj mt-2 mb-4">
	     	<@AppBar
				appBarId="ACCOUNT_KPI_BAR"
				appBarTypeId="KPI"
				id="appbar2"
		        isEnableUserPreference=true
	            />
        </div>
        <#assign addActivities = '
            <div class="dropdown-menu" aria-labelledby="E10007">
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="#"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
                <a class="dropdown-item" href="#"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
                <a class="dropdown-item" href="#"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
                <a class="dropdown-item" href="#"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
                <a class="dropdown-item" href="#"><i class="fa fa-plus-square" aria-hidden="true"></i> Others</a>
            </div>
            ' />
		<#assign toggleDropDownData = {"E10007":addActivities!} />
		<h1>Sample Action Bar</h1>
        <div class="card-head margin-adj mt-2">
        	<@AppBar  
	        	appBarId="ACTION_APP_BAR"
	            appBarTypeId="ACTION"
	            id="appbar3"
	            extra=extra!
	            toggleDropDownData=toggleDropDownData!
	            isEnableUserPreference=true
	            />
		</div>
      </div>
   </div>
</div>
