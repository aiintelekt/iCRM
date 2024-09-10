<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign partyId= request.getParameter("partyId")! />

<div class="row">
	<#assign rightContent = "">
	<#assign isEnableRemoveBtn = false>
	<#assign rightContent = '
			<div class="form-check-inline ml-30">
				<label class="form-check-label"> 
				<input type="checkbox" class="form-check-input" name="filterGroupCode" id="filter_segment_filterGroupCode"> Include Email Group
				</label>
			</div>
		' />
	<#if partyStatusId?if_exists != "PARTY_DISABLED">
		<#assign isEnableRemoveBtn = true>
				<#assign rightContent = rightContent+'<span class="btn btn-xs btn-primary" id="segmentation-remove-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>'/>
	</#if>
	<div class="col-lg-12 col-md-12 col-sm-12">
	<#assign exportBtn=true>
	<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
	<#assign exportBtn=false>
	</#if>
	<#-- <@AgGrid
		gridheadertitle=""
		gridheaderid="segmentation-grid-action-container"
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=exportBtn
		insertBtn=false
		updateBtn=false
		removeBtn=isEnableRemoveBtn
		headerextra=rightContent!
		refreshPrefBtnId="segmentation-refresh-pref-btn"
		savePrefBtnId="segmentation-save-pref-btn"
		clearFilterBtnId="segmentation-clear-filter-btn"
		exportBtnId="segmentation-export-btn"
		removeBtnId="segmentation-remove-btn"
	    userid="${userLogin.userLoginId}" 
	    shownotifications="true" 
	    instanceid="SEGMENTATIONS" 
	    autosizeallcol="true"
	    debug="false"
	    /> 

  		<script type="text/javascript" src="/common-portal-resource/js/ag-grid/custom-field/find-segmentation.js"></script>-->
  		
<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="segmentation-grid"
			instanceId="SEGMENTATIONS"
			jsLoc="/common-portal-resource/js/ag-grid/custom-field/find-segmentation.js"
			headerLabel=""
			headerId="segmentation-grid-action-container"
			savePrefBtn=false
			clearFilterBtn=false
			subFltrClearBtn=false
			exportBtn=exportBtn!
			headerBarClass="grid-header-no-bar"
			savePrefBtnId="segmentation-save-pref-btn"
			clearFilterBtnId="segmentation-clear-filter-btn"
			subFltrClearId="segmentation-sub-filter-btn"
			exportBtnId ="segmentation-export-btn"
			headerExtra=rightContent!
			/>
  	 </div>
  	 
</div>
