<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<link href="/bootstrap/css/dualselectlist.css" rel="stylesheet">
<script src="/bootstrap/js/dualselectlist.jquery.js"></script>
<script type="text/javascript" >
   $(document).ready(function() {
       <#if !activeTab?has_content>
         <#assign activeTab = requestParameters.activeTab!>
       </#if>
       
       <#if activeTab?has_content && activeTab == "prospectDetailsTab">
         $('.nav-tabs a[href="#prospectDetailsTab"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "sourceOfWealthTab">
         $('.nav-tabs a[href="#sourceOfWealthTab"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "contactTab">
         $('.nav-tabs a[href="#contactTab"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "activitiesTab">
         $('.nav-tabs a[href="#activitiesTab"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "opportunitiesTab">
         $('.nav-tabs a[href="#opportunitiesTab"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "notesAndAttachmentTab">
         $('.nav-tabs a[href="#notesAndAttachmentTab"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "offersTab">
    	$('.nav-tabs a[href="#offersTab"]').tab('show'); 
       <#elseif activeTab?has_content && activeTab == "serviceRequestsTab">
    	$('.nav-tabs a[href="#serviceRequestsTab"]').tab('show');
      <#elseif activeTab?has_content && activeTab == "administrationTab">
    	$('.nav-tabs a[href="#administrationTab"]').tab('show');  
         <#else>
         $('.nav-tabs a[href="#prospectDetailsTab"]').tab('show');
       </#if>
   });
</script>
<div class="top-band bg-white mb-0">
	<div class="col-lg-12 col-md-12 col-sm-12">
		<div class="row">
			<marquee behavior="scroll" direction="left" class="text-danger">"System maintenance scheduled for 03-09-2019 from 8 AM SGT to 10 AM SGT. During this time, users may experience unavailability of services"</marquee>
		</div>
	</div>
</div>
<div class="container-fluid">
	<div class="row">
		<div id="main" role="main">
			<div class="top-band bg-light">
				<div class="col-lg-12 col-md-12 col-sm-12">
					<div class="row">
						<div class="col-lg-12 col-md-12 col-sm-12">
							<h3 class="float-left">Recently Viewed:  </h3>
							<div class="text-left">
								<a href="view-prospect.php" class="btn btn-xs btn-primary"> 18034</a>
								<a href="view-prospect.php" class="btn btn-xs btn-primary"> 18034</a>
								<a href="view-prospect.php" class="btn btn-xs btn-primary"> 18034</a>
								<a href="view-prospect.php" class="btn btn-xs btn-primary"> 18034</a>
								<a href="view-prospect.php" class="btn btn-xs btn-primary"> 18034</a>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>    
          	<div class="col-lg-12 col-md-12 col-sm-12 mid">
          		<div class="card-head margin-adj mt-0">
            		${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#cusNameCommon")}
          		</div>
          	</div>
          	<div class="clearfix"></div>
		</div>
	</div>
</div>
<div class="card-head margin-adj mt-1">
	<div class="row">
		<div class="col-md-4">
			<h6>Prospect : Information</h6>
			<h3>18034</h3>
		</div>
		<div class="col-md-8 right-details">
			<div class="bd-callout">
				<small>METRIC 2</small>
				<h5>--</h5>
			</div>
			<div class="bd-callout">
				<small>METRIC 1</small>
				<h5>--</h5>
			</div>
			<div class="bd-callout">
				<small>Aging of Prospect</small>
				<h5>100 days</h5>
			</div>
			<div class="bd-callout">
				<small>Prospect Since</small>
				<h5>05/03/2019 7:39</h5>
			</div>
			<div class="bd-callout">
				<small>Source ID</small>
				<h5>--</h5>
			</div>
		</div>
	</div>
</div>

<div class="card-head margin-adj mt-2" id="cp">
	<div class="row">
		<div class="col-md-12 col-lg-6 col-sm-12 ">
			<div class="row">
				<div class="col-4 field-text">Customer </div>
				<div class="col-7 value-text">Zhang Ziyi Sgcrm</div>
			</div>
			<div class="row">
				<div class="col-4 field-text">CIF ID </div>
				<div class="col-7 value-text">S1014810C</div>
			</div>
		</div>
		<div class="col-lg-6 col-md-12 col-sm-12 ">
			<div class="row">
				<div class="col-4 field-text">Prospect </div>
				<div class="col-7 value-text">002</div>
			</div>
			<div class="row">
				<div class="col-4 field-text">CIN Suffix </div>
				<div class="col-7 value-text">002</div>
			</div>
		</div>
	</div>
</div>
<ul class="nav nav-tabs mt-2">
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#prospectDetailsTab" id="prospectDetailsTabId">${uiLabelMap.prospectDetails}</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#sourceOfWealthTab" id="sourceOfWealthTabId">${uiLabelMap.sourceOfWealth}</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#contactTab" id="contactTabId">${uiLabelMap.contact}</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#activitiesTab" id="activitiesTabId">${uiLabelMap.activities}</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#opportunitiesTab" id="opportunitiesTabId">${uiLabelMap.opportunities}</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#notesAndAttachmentTab" id="notesAndAttachmentTabId">${uiLabelMap.notesAndAttachment}</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#offersTab" id="offersTabId">${uiLabelMap.offers}</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link " href="#serviceRequestsTab" id="serviceRequestsTabId">${uiLabelMap.serviceRequests}</a>
	</li>
	<li class="nav-item">
		<a data-toggle="tab" class="nav-link" href="#administrationTab" id="administrationTabId">${uiLabelMap.administration}</a>
	</li>
	<div class="text-right position-absolute" style="right:20px;">
		<!--a title="Copy" href="#" class="btn btn-primary btn-xs mt-1" ><i class="fa fa-clone  " aria-hidden="true"></i> Copy</a!-->
		<a title="ReOpen" href="#" class="btn btn-primary btn-xs mt-1" >
			<i class="fa fa-retweet " aria-hidden="true"></i> ReOpen
		</a>
		<a title="Save " href="#" class="btn btn-primary btn-xs mt-1">
			<i class="fa fa-save " aria-hidden="true"></i> Save
		</a>
		<a title="Close" href="#" class="btn btn-primary btn-xs mt-1" >
			<i class="fa fa-window-close-o " aria-hidden="true"></i> Close
		</a>
		<!--a id="export_to_excel_icon" title="Edit" href="edit-service-request.php" class="btn btn-primary btn-xs mt-1"><i class="fa fa-pencil" aria-hidden="true"></i> Edit </a!-->
	</div>
</ul>
<div class="tab-content">
	<div id="prospectDetailsTab" class="tab-pane fade in ">
		<@pageSectionHeader title="${uiLabelMap.general}"/>
		<#include "component://sales-portal/webapp/sales-portal/Prospect/viewProspectDetails.ftl"/>
	</div>
	<div id="sourceOfWealthTab" class="tab-pane fade in">
		<@pageSectionHeader title="${uiLabelMap.sourceOfWealth}"/>
		<#include "component://sales-portal/webapp/sales-portal/Prospect/sourceOfWealthDetails.ftl"/>
	</div>
	<div id="contactTab" class="tab-pane fade in">
		<#include "component://sales-portal/webapp/sales-portal/Prospect/contactDetails.ftl"/>
	</div>
	<div id="activitiesTab" class="tab-pane fade in">
		<@pageSectionHeader title="${uiLabelMap.activities}"/>
	</div>
	<div id="opportunitiesTab" class="tab-pane fade in">
		<@pageSectionHeader title="${uiLabelMap.opportunities}"/>
	</div>
	<div id="notesAndAttachmentTab" class="tab-pane fade in">
		<@pageSectionHeader title="${uiLabelMap.notesAndAttachment}"/>
	</div>
	<div id="offersTab" class="tab-pane fade in">
		<@pageSectionHeader title="${uiLabelMap.offers}"/>
	</div>
	<div id="serviceRequestsTab" class="tab-pane fade in">
		<@pageSectionHeader title="${uiLabelMap.serviceRequests}"/>
	</div>
	<div id="administrationTab" class="tab-pane fade in">
		<@pageSectionHeader title="${uiLabelMap.administration}"/>
		<#include "component://sales-portal/webapp/sales-portal/Prospect/administrationDetails.ftl"/>
	</div>
</div>