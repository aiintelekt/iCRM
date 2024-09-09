<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "")/>
<#assign extras='<a href="/admin-portal/control/updateResAvail?entryId=${inputContext.entryId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>'/>
<@pageSectionHeader title="General Details" extra=extras/>
<div class="col-md-12 col-lg-12 col-sm-12">
	<@dynaScreen
		instanceId="RES_AVAIL_BASE"
		modeOfAction="VIEW"
		/>
</div>

<script>     
$(document).ready(function() {



});
</script>