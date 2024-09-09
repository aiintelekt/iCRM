<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/> 
 <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "activities") />  

 <#assign extraLeft ='<div class="form-check-inline ml-30">
			<label class="form-check-label"> 
			<input type="checkbox" class="form-check-input activity-status" id="openchk" name="openchk" value="IA_OPEN">Open
			</label>
		</div>
		<div class="form-check-inline">
			<label class="form-check-label"> 
			<input type="checkbox" class="form-check-input activity-status" id="closedchk" name="closedchk" value="IA_MCOMPLETED">Completed
			</label>
		</div>' />
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
	gridheadertitle="Activities"
	gridheaderid="activity-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	helpBtn=true
	helpUrl=helpUrl!
	headerextra=rightContent!
	headerextraleft = extraLeft!
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
