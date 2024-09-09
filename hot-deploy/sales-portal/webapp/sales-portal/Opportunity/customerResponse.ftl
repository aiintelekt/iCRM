 <#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<input type="hidden" name="activeTab" value="curRep" />	
 
<div class="page-header border-b pt-2"> 
	<@sectionFrameHeader  title="Call Details" />                             
</div>
<div class="table-responsive">                                    
	<div id="ContactAgGrid1"  class="ag-theme-balham"></div>
    <script type="text/javascript" src="/sales-portal-resource/js/ag-grid/call-details.js"></script>
</div>
