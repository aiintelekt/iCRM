<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	
  	<div class="col-lg-12 col-md-12 col-sm-12">
	<#-- 
	<div class="page-header border-b pt-2">
        <@headerH2 title="${uiLabelMap.listOfDynaScreens!}" class="float-left"/>
        <div class="float-right">
        
        <button id="remove-screen-btn" data-toggle="confirmation" title="Are you sure to REMOVE ?" class="btn btn-primary btn-xs ml-2 " >
        	<i class="fa fa-times" aria-hidden="true"></i> Remove 
        </button>
        <span id="export-screen-btn" title="Export" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-edit" aria-hidden="true"></i> Export </span>
        
        </div>
        <div class="clearfix"></div>
    </div>    	
	 -->
	   	  	  	
  	<div id="activity-grid" style="width: 100%;" class="ag-theme-balham"></div>
  	<@AgGrid
	gridheadertitle=""
	gridheaderid="activity-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	refreshPrefBtnId="activity-refresh-pref-btn"
	savePrefBtnId="activity-save-pref-btn"
	clearFilterBtnId="activity-clear-filter-btn"
	exportBtnId="activity-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="ACTIVITES" 
    autosizeallcol="true"
    debug="false"
    /> 
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/find-activity.js"></script>
           
  	</div>
</div>
