<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros_dbs.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
   <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
<@sectionFrameHeader title="${uiLabelMap.find} ${uiLabelMap.auditOperator}" />
<#-- <div class="page-header border-b">
	<h1>${uiLabelMap.find} ${uiLabelMap.auditOperator}</h1>
</div>-->
<div class="mt-2 mb-3">
   <form method="post" action="#" id="findAuditOperator" class="form-horizontal" name="findAuditOperator" novalidate="novalidate" data-toggle="validator">
      <div class="row">
      	 
         <div class="col-md-2 col-sm-2">
         	<@simpleDropdownInput 
				id="operatorType"
				options=operatorTypes
				required=false
				value=filterContext.operatorType
				allowEmpty=true
				emptyText = uiLabelMap.selectMaker
				/>	
         </div>
         <div class="col-md-2 col-sm-2">
			<@simpleInput 
				id="userLoginId"
				placeholder=uiLabelMap.oneBankId
				value=filterContext.userLoginId
				required=false
				maxlength=60
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleDropdownInput 
				id="userStatus"
				options=userStatusList
				required=false
				value=filterContext.userStatus
				allowEmpty=true
				emptyText = uiLabelMap.userStatus
				/>	
         </div>
         <@fromSimpleAction id="find-auditOperator-button" showCancelBtn=false isSubmitAction=false submitLabel="Search"/>
      </div>
   </form>
   <div class="clearfix"> </div>
</div>
</div>