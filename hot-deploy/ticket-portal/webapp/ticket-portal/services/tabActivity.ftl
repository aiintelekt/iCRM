<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
	#dropdown10 {
    	color: black;
	}
</style>

<div class="page-header border-b pt-2">
    <#if security.hasEntityPermission("ADD", "_ACTIVITY", session)>
    	<#assign extraLeft='<a href="#" title="Add Activity" id="dropdown10" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="text-drak left-icones"><i class="fa fa-plus fa-1 right-icones ml-2" aria-hidden="true" style="font-size: 18px;"></i></a>
    	<div class="dropdown-menu" aria-labelledby="cust-icon"></a>
        	<h4 class="bg-light pl-1 mt-2">Add Activities<h4/>

            <a class="dropdown-item" href="/ticket-portal/control/srAddTask?srNumber=${(parameters.srNumber)!}"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a>
            <a class="dropdown-item" href="/ticket-portal/control/srAddPhoneCall?srNumber=${(parameters.srNumber)!}"><i class="fa fa-phone" aria-hidden="true"></i> phone Call</a>
            <a class="dropdown-item" href="/ticket-portal/control/srAddEmail?srNumber=${(parameters.srNumber)!}"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
            <a class="dropdown-item" href="/ticket-portal/control/srAddAppointment?srNumber=${(parameters.srNumber)!}"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
            <a class="dropdown-item" href="/ticket-portal/control/srAddOthers?srNumber=${(parameters.srNumber)!}"><i class="fa fa-square" aria-hidden="true"></i> Others</a>

    	</div>'
    	extra='<a href="#" class="text-dark"><i class="fa fa-refresh fa-1" aria-hidden="true"></i></a><small>2019/03/26 01:10:14</small>'/>
    	<@sectionFrameHeader  title="Activities" extraLeft=extraLeft />
	</#if>
</div>

<div class="table-responsive">
    <div id="activityGrid" style="height:380px; width: 100%;" class="ag-theme-balham"></div>
    <script type="text/javascript" src="/ticket-portal-resource/js/ag-grid/services/tabActivity.js"></script>
</div>
