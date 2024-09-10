<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#-- <#include "component://common-portal/webapp/common-portal/attachment/model_attachment_window.ftl"/> -->
<script type="text/javascript" src="/bootstrap/js/jquery.validate-1.15.1.min.js"></script>
<style>
.ag-row .ag-cell {
  display: grid;
  //justify-content: center; /* align horizontal */
  align-items: center;
}
</style>
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
<#if readOnlyPermission!>
<script>
	$(document).ready(function(){
        $('a.view-link').each(function(){ 
            $(this).attr("href", "#"); // Set herf value
            $(this).attr("target","");
        });
        
        $("a.view-link").click(function () {
            $("#accessDenied").modal("show");
            return false;
        });
    });
</script>
</#if>

<div class="pt-2 align-lists">
	<form method="post" id="attachment-search-form" class="form-horizontal" novalidate="true" data-toggle="validator">	
		<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
		<input type="hidden" name="domainEntityType" value="ACTIVITY">
		<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
		<input type="hidden" name="workEffortId" value="${workEffortId!}">
		<input type="hidden" name="currentStatusId" value="${inputContext.currentStatusId!}">
		
	</form>
</div>
<div class="row">
	<div class="col-lg-12 col-md-12 col-sm-12">
		<#assign removeAccess = false />
		<#if readOnlyPermission!>
		<#else>
			<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
			<#if hasPermission && inputContext?has_content && inputContext.currentStatusId?if_exists !="IA_MCOMPLETED">
				<#assign extra = '<span id="create-act-attachment-btn" title="attachment" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-edit" aria-hidden="true"></i> Add Attachment </span>
				                  <span id="create-act-bookmark-btn" title="BookMarkURLs" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-edit" aria-hidden="true"></i> Bookmark URL </span>' />
				<#assign removeAccess = true />
				<#assign extra = extra+'<span class="btn btn-xs btn-primary" id="remove-attachment-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>'/>
			</#if>
		</#if>
		
		<#-- 
		<@createAttachmentModal 
			instanceId="create-act-attachment"
			path="sr-portal"
			/>
		-->
		<div id="attachment-grid" style="width: 100%;" class="ag-theme-balham"></div>
			<#-- <@AgGrid
				gridheadertitle="Bookmarks and Files"
				gridheaderid="attachment-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=true
				insertBtn=false
				updateBtn=false
				removeBtn=removeAccess!
				headerextra=extra!
				refreshPrefBtnId="attachment-refresh-pref-btn"
				savePrefBtnId="attachment-save-pref-btn"
				clearFilterBtnId="attachment-clear-filter-btn"
				exportBtnId="attachment-export-btn"
				removeBtnId="remove-attachment-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="ACTIVITY_ATTACHMENTS" 
			    autosizeallcol="true"
			    debug="false"
		    	/>  
		  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/attachment/find-activity-attachment.js"></script>-->
  	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="attachment-grid"
			instanceId="ACTIVITY_ATTACHMENTS"
			jsLoc="/common-portal-resource/js/ag-grid/attachment/find-activity-attachment.js"
			headerLabel="Bookmarks and Files"
			headerId="attachment-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=false
			subFltrClearBtn = false
			exportBtn=true
			exportBtnId="attachment-list-export-btn"
			savePrefBtnId ="attachment-save-pref-btn"
			clearFilterBtnId ="attachment-clear-filter-btn"
			subFltrClearId="sub-filter-clear-btn"
			headerExtra=extra!
			/>
			
	</div>
  	
</div>

<#-- Modal start -->
<div id="create-act-attachment" class="modal fade" >
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Add Attachment</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body poplabel-left" id="attachment">
                <form name="add-attachment-form1" id="add-attachment-form1" action="" enctype="multipart/form-data" method="post">
                <div class="row p-1">
                    <div class="col-md-12 col-lg-12 col-sm-12 ">
                        <input type="hidden" id="path" name="path" value="${path!}">
                        <input type="hidden" id="activeTab" name="activeTab" value="attachments">
                        <input type="hidden" id="partyId" name="partyId" value="${partyId?if_exists}"/>
                        <input type="hidden" id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                        <input type="hidden" id="domainEntityType" name="domainEntityType" value="${domainEntityType!}">
                        <input type="hidden" id="domainEntityId" name="domainEntityId" value="${domainEntityId!}">
                        <input type="hidden" id="workEffortId" name="workEffortId" value="${workEffortId!}">
                        <input type="hidden" id="linkedFrom" name="linkedFrom" value="${workEffortId!}">
                        <input type="hidden" id="globalPathName" name="globalPathName" value="ACT_IMG_UPLOAD_PATH">
                        <input type="hidden" id="attachmentType" name="attachmentType" value="PUBLIC">
                        
                        <#if inputContext.enumValues?has_content>
                        	<#assign entityList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(inputContext.enumValues, "enumId","description")?if_exists />
                        <#else>
	                        <#assign entities = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId","CONTENT_CLASS","isEnabled","Y"), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId"), false)>
	                        <#assign entityList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(entities, "enumId","description")?if_exists />
                        </#if>
                        <@dropdownCell 
	                        id="classificationEnumId"
	                        name="classificationEnumId"
	                        placeholder=uiLabelMap.Classification
	                        options=entityList!
	                        label= "Classification"
	                        value="${requestParameters.classificationEnumId?if_exists}"
	                        allowEmpty=false
	                        />
	                     
                        <@inputArea
                        id="attachmentDescription"
                        label="Attachment Description"
                        rows="3"
                        placeholder = "Description"
                        value = ""
                        required=false
                        maxlength="255"
                        />
                        <div id= "upload">
                            <@inputRow id="uploadFile" type="file" label="Upload" required =true />
                        </div>
                        <div>
                      		&nbsp;Attachment Type &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                      		<input type="radio"  name="publicOrPrivateAtt" id="publicOrPrivateAtt" value="PUBLIC" checked /> Public
	                    	&nbsp;&nbsp; <input type="radio"  name="publicOrPrivateAtt" id="publicOrPrivateAtt" value="PRIVATE"/> Private
	                    </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <@button class="btn btn-sm btn-primary navbar-dark" id="add-attachment-form-submit" label="${uiLabelMap.Save}"/>
                    <@reset label="${uiLabelMap.Reset}"	/>
                </div>
                </form>
            </div>
        </div>
    </div>
</div>
<#-- modal end -->

<#-- Modal start -->
<div id="create-bookmark-modal" class="modal fade" >
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Bookmark URL</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body poplabel-left" id="bookmark">
                <form name="add-bookmark-form" id="add-bookmark-form" action="" enctype="multipart/form-data" method="post">
                <div class="row p-1">
                    <div class="col-md-12 col-lg-12 col-sm-12 ">
                        <input type="hidden" id="path" name="path" value="${path!}">
                        <input type="hidden" id="activeTab" name="activeTab" value="attachments">
                        <input type="hidden" id="partyId" name="partyId" value="${partyId?if_exists}"/>
                        <input type="hidden" id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                        <input type="hidden" id="domainEntityType" name="domainEntityType" value="${domainEntityType!}">
                        <input type="hidden" id="domainEntityId" name="domainEntityId" value="${domainEntityId!}">
                        <input type="hidden" id="workEffortId" name="workEffortId" value="${workEffortId!}">
                        <input type="hidden" id="linkedFrom" name="linkedFrom" value="${workEffortId!}">
                        <input type="hidden" id="globalPathName" name="globalPathName" value="ACT_IMG_UPLOAD_PATH">
                        
                        <#if inputContext.enumValues?has_content>
                        	<#assign entityList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(inputContext.enumValues, "enumId","description")?if_exists />
                        <#else>
	                        <#assign entities = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId","CONTENT_CLASS","isEnabled","Y"), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId"), false)>
	                        <#assign entityList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(entities, "enumId","description")?if_exists />
                        </#if>
                        <@dropdownCell 
	                        id="classificationEnumId1"
	                        name="classificationEnumId"
	                        placeholder=uiLabelMap.Classification
	                        options=entityList!
	                        label= "Classification"
	                        value=""
	                        allowEmpty=false
	                        />
	                     
                        <@inputArea
                        id="attachmentDescription1"
                        label="Attachment Description"
                        rows="3"
                        placeholder = "Description"
                        value = ""
                        required=false
                        maxlength="255"
                        />
                        <div id= "upload">
                            <@inputRow id="url" type="text" label="URL" required =true placeholder = "http://www.groupfio.com" />
                        </div>
                        <#-- 
                        <#assign options = "{Y:Yes, N:No}" />
                        <@radioInputCell
					        id="helpfulLink"
					        name="helpfulLink"
					        label="Is Helpfull Link"
					        options=yesNoOptions!
					        value="${requestParameters.helpfulLink!'N'}"
					        />	
                        -->
                    </div>
                </div>
                <div class="modal-footer">
                    <@button class="btn btn-sm btn-primary navbar-dark" id="add-bookmark-form-submit" label="${uiLabelMap.Save}"/>
                    <@reset label="${uiLabelMap.Reset}"	/>
                </div>
                </form>
            </div>
        </div>
    </div>
</div>

<style>
	.image-model{
		 max-width: fit-content !important;
		 display: flex !important;
		 justify-content: center;
		 align-items: center;
	}
	.modal-content{
		height: auto;
	    overflow-x: hidden !important;
	    overflow-y: hidden !important;
	}
</style>
<div id="img-preview" class="modal fade" role="dialog" aria-hidden="true" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog image-model" id="image-model">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Image Preview</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<span class="text-center">
            		<img class="" id="preview-image" name="preview-image" width="400" height="500" src="" ></img>
            	</span>
            </div>
            <div class="modal-footer">
                <button type="sbmit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
            </div>
        </div>
    </div>
</div>
<#-- modal end -->


	
<script>

jQuery(document).ready(function() {
	
	$('#create-act-attachment-btn').on('click', function() {
		$('#create-act-attachment').modal("hide");
		$('#create-act-attachment').modal("show");
	});
	
	$('#refresh-attachment-btn').on('click', function() {
		getattachmentRowData();
	});
	
	$('input[type=radio][name=publicOrPrivateAtt]').change(function() {
		$("#attachmentType").val(this.value);
	});
	
	$('#create-act-bookmark-btn').on('click', function() {
		$('#create-bookmark-modal').modal("show");
	});

});

</script>
