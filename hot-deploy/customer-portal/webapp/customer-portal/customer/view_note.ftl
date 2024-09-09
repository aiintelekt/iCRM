<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<#include "component://customer-portal/webapp/customer-portal/customer/modal_window.ftl" />
<div class="row" style="width:100%">
    <form method="post" id="viewNoteForm" name="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
        <@inputHidden id="productPromoCodeId" value="${productPromoCodeId!}" />
    </form>
    <form method="post" id="notesForm" name="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
        <@inputHidden id="partyId" value="${partyId!}" />
        <@inputHidden id="domainEntityId" value="${productPromoCodeId!}" />
        <@inputHidden id="domainEntityType" value="${domainEntityType!}" />
    </form>
    <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <#assign rightContent='
		<button onclick="createForm()" class="btn btn-sm btn-primary disabled">
			<i class="fa fa-note" aria-hidden="true"></i>Create New
		</button>' />
		<#assign rightContent=rightContent+'<span class="btn btn-xs btn-primary" id="remove-notes-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?" aria-describedby="confirmation531820">
		<i class="fa fa-remove" aria-hidden="true"></i> Remove</span>' />
	<#assign exportBtn=true>
	<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
	<#assign exportBtn=false>
	</#if>
       <#-- <@AgGrid gridheadertitle=uiLabelMap.Notes gridheaderid="notes-grid-action-container" savePrefBtn=true clearFilterBtn=true exportBtn=exportBtn insertBtn=false updateBtn=false removeBtn=true removeBtnId="remove-notes-btn" userid="${userLogin.userLoginId}" shownotifications="true" headerextra=rightContent! instanceid="VIEW_NOTE_LIST" autosizeallcol="true" debug="false" statusBar=false />
        <script type="text/javascript" src="/loyalty-portal-resource/js/ag-grid/notes/notes.js"></script>-->
      <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	  <input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

        					<@fioGrid 
								id="notesGrid"
								instanceId="VIEW_NOTE_LIST"
								jsLoc="/loyalty-portal-resource/js/ag-grid/notes/notes.js"
								headerLabel=uiLabelMap.Notes
								headerId="notes-grid-action-container"
								savePrefBtnId="notes-save-pref-btn"
								clearFilterBtnId="notes-clear-filter-btn"
								headerBarClass="grid-header-no-bar"
								savePrefBtn=false
								clearFilterBtn=false
								exportBtn=exportBtn!
								subFltrClearBtn=false
								subFltrClearId="notes-sub-filter-clear-btn"
								exportBtnId="notes-export-btn"
								headerExtra=rightContent!
								/>
    </div>
</div>
<div class="col-lg-12 col-md-12 col-sm-12">
    <div id="createNote" class="modal fade" role="dialog">
        <div class="modal-dialog modal-md modal-lg" style="max-width: 900px;">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">Notes</h4>
                    <button type="reset" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <form id="createNoteForms" name="createNoteInfoForm" action="#" method="post" novalidate="novalidate" data-toggle="validator">
                        <div class="col-lg-12 col-md-12 col-sm-12">
                            <@dynaScreen instanceId="CREATE_COUPON_NOTE" modeOfAction="CREATE" />
                        </div>
                        <div class="modal-footer" id="addFooter">
                            <div class="text-left ml-1">
                                <input type="submit" class="btn btn-sm btn-primary disabled" value="Create" />
                                <button type="submit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Cancel</button>
                            </div>
                        </div>
                  	<form>
                </div>
            </div>
        </div>
    </div>
</div>