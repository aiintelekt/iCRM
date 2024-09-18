<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="page-header border-0 row">
	<@pageSectionHeader title="Opportunities" class ="col-12 px-0 border-b mb-2" />
</div>


<div class="nav-tabs">
<ul class="nav nav-tabs">
	<#-- <li class="nav-item" id="assigned-customer-tab"><a data-toggle="tab" href="#tab1"
		class="nav-link active">${uiLabelMap.AssignedCustomer!}</a></li> -->
		<li class="nav-item"><a data-toggle="tab" href="#notes" class="nav-link active">Notes</a></li>
		<li class="nav-item"><a data-toggle="tab" href="#logCall" class="nav-link">${uiLabelMap.logCall}</a></li>
	<#--<li class="nav-item"><a data-toggle="tab" href="#hadoop-tab1"
		class="nav-link active">CaAccount Data</a></li> -->
						
</ul>
</div>
<div class="tab-content">
	
	<div id="notes" class="tab-pane fade show active">		
		  ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#note")}		
	</div>
	
	<div id="logCall" class="tab-pane fade">
         ${screens.render("component://crm/webapp/widget/crm/screens/common/CommonScreens.xml#callDetails")}
      </div>
	
	
</div>

