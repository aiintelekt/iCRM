<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
    <div id="main" role="main">
        <form id="mainFrom" method="post" action="<@ofbizUrl>updateSqlGroupAction</@ofbizUrl>" data-toggle="validator"> 
        	<input type="hidden" name="sqlGroupId" value="${inputContext.sqlGroupId!}">
            <div class="col-lg-12 col-md-12 col-sm-12">
            	<#assign extra='<a href="/admin-portal/control/viewSqlGroup?approvalId=${inputContext.sqlGroupId!}" class="btn btn-xs btn-primary back-btn">
		        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
		        <@sectionFrameHeader title="${uiLabelMap.UpdateSqlGroup!}" extra=extra />
        
            	<@dynaScreen 
					instanceId="SQLGRP_BASE"
					modeOfAction="UPDATE"
					/>
            </div>
            
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
            
            <@submit label="${uiLabelMap.Save}"/>
            <@cancel label="Cancel" onclick="/admin-portal/control/viewSqlGroup?sqlGroupId=${inputContext.sqlGroupId!}"/>
         	
            </div>
        </form>
    </div>
</div>

<script>

$(document).ready(function() {

//initDateRange("estimatedStartDate_date_picker", "estimatedCompletionDate_date_picker", null, null);
//initDateRange("actualStartDate_date_picker", "actualCompletionDate_date_picker", null, null);

$('#mainFrom').validator().on('submit', function (e) {
	if (!e.isDefaultPrevented()) {
  		
  	}
});

});

</script>