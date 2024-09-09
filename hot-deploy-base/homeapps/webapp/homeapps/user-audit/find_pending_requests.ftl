<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros_dbs.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
.ui.selection.dropdown.visible, .ui.selection.dropdown.active {
	z-index: 1000;
}
</style>
<div class="row">
   <div id="main" role="main">
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
<@sectionFrameHeader title="${uiLabelMap.find} ${uiLabelMap.pendingRequests}"/>
        <div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="true"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter}
					</a>
				</h4>
			</div>
<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
   <form method="post" action="#" id="findPendingRequest" class="form-horizontal" name="findPendingRequest" novalidate="novalidate" data-toggle="validator">
      <div class="row pb-2">
      	 
      	 <div class="col-md-4 col-lg-4 col-sm-12">
         	<@simpleDropdownInput 
				id="requestType"
				options=requestTypeList
				required=false
				value=filterContext.requestType
				allowEmpty=true
				emptyText = uiLabelMap.requestType
				/>	
         </div>
         <div class="col-md-4 col-lg-4 col-sm-12">
         	<@simpleDropdownInput 
				id="modeOfAction"
				options=actionList
				required=false
				value=filterContext.modeOfAction
				allowEmpty=true
				emptyText = uiLabelMap.modeOfAction
				/>	
         </div>
         <div class="col-md-4 col-lg-4 col-sm-12">
         	<@simpleDropdownInput 
				id="statusId"
				options=statusList
				required=false
				value=filterContext.statusId
				allowEmpty=true
				emptyText = uiLabelMap.status
				/>	
         </div>
         
         <div class="col-md-4 col-lg-4 col-sm-12">
         	<@simpleDropdownInput 
				id="makerPartyId"
				options=makerList
				required=false
				value=filterContext.makerPartyId
				allowEmpty=true
				emptyText = uiLabelMap.maker
				/>	
         </div>
         <div class="col-md-4 col-lg-4 col-sm-12">
         	<@simpleDropdownInput 
				id="chekerPartyId"
				options=chekerList
				required=false
				value=filterContext.chekerPartyId
				allowEmpty=true
				emptyText = uiLabelMap.cheker
				/>	
         </div>
         
         <div class="col-md-4 col-lg-4 col-sm-12">
      		<@simpleDateInput 
				id="fromDate"
				placeholder=uiLabelMap.fromDate
				/>
         </div>
         <div class="col-md-4 col-lg-4 col-sm-12">
      		<@simpleDateInput 
				id="thruDate"
				placeholder=uiLabelMap.thruDate
				/>
         </div>
         <div class="col-lg-12 col-md-12 col-sm-12">
			<div class="text-right">
         <@button id="main-search-btn" label="Search"/>
        </div>
      </div>
      </div>
   </form>
   <div class="clearfix"> </div>
</div>
</div>
