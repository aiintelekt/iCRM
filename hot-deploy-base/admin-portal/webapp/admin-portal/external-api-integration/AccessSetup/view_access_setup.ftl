<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign extra='<a href="/admin-portal/control/accessSetup" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="editChannelAccess?channelAccessId=${inputContext.channelAccessId?if_exists}" class="btn btn-xs btn-primary">
        <i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="/admin-portal/control/accessSetup" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <form id="mainFrom" action="" data-toggle="validator" novalidate="true"> 
        <div class="col-lg-12 col-md-12 col-sm-12">
            <@sectionFrameHeader title="${uiLabelMap.ViewAccessSetup!}" extra=extra />
            <div id="form_details" >
                <@dynaScreen 
                    instanceId="CREATE_ACCESS_SETUP"
                    modeOfAction="VIEW"
                    />
                <div class="clearfix"></div>
            </div>
        </div>
        </form>
    </div>
</div>

<script>
	$(function(){
		
	});
</script>