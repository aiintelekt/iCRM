<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />

<script>
	<#if readOnlyPermission!>
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
    </#if>
</script>
<div class="row" style="">
	<div class="col-lg-12 col-md-12 col-sm-12">
	<#assign rightContent=""/>
	<#if readOnlyPermission!>
	<#else>
		<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
	
		<#if hasPermission>
			<#assign rightContent= rightContent +'<button type="button" class="btn btn-xs btn-primary m5" data-toggle="modal" data-target="#add-from-existing"><i class="fa fa-plus fa-1" aria-hidden="true"></i> ${uiLabelMap.Add!}</button>' />
		</#if>
	</#if>
	<#assign gridUpdate = false />
	<#assign isSrAssocPartyUpdate = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "SR_ASSOC_PARTY_UPDATE","N")?if_exists>
	<#if isSrAssocPartyUpdate?has_content && "Y" == isSrAssocPartyUpdate!>
		<#assign gridUpdate = true />
	</#if>
	<@AgGrid
		gridheadertitle=uiLabelMap.ListofAssociatedParties!
		gridheaderid="associated-parties"
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=true
		insertBtn=false
		updateBtn=gridUpdate!
		removeBtn=false
		headerextra=rightContent!
	    userid="${userLogin.userLoginId}" 
	    shownotifications="true" 
	    instanceid="SR_ASSOC_PARTY_LIST" 
	    autosizeallcol="true"
	    debug="false"
	    statusBar=true
	    serversidepaginate=false
	    refreshPrefBtnId="sr-assoc-refresh-pref-btn"
	    savePrefBtnId="sr-assoc-save-pref-btn"
	    updateBtnId="sr-assoc-update-btn"
	    clearFilterBtnId="sr-assoc-clear-filter-btn"
	    subFltrClearId="sr-assoc-sub-filter-clear-btn"
	    exportBtnId="sr-assoc-export-btn"
	    removeBtnId="sr-assoc-remove-btn"
	    serversidepaginate=false
		statusBar=true
	    />    
	         
	<script type="text/javascript" src="/sr-portal-resource/js/ag-grid/services/list-sr-assoc-parties.js"></script>
	</div>
</div>

<div  id="add-from-existing" class="modal fade bd-example-modal-lg add-contact-party" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" style="overflow-y: scroll; height:auto; width:auto;">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h2 class="modal-title">${uiLabelMap.findParty!}</h2>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>
            <div class="modal-body" style="padding-bottom: 8px;">
                <div class="card-header">
                    <form  method="post" id="searchAssocForm" name="searchAssocForm">
                        <@inputHidden 
                        	id="externalLoginKey"
                        	value="${requestAttributes.externalLoginKey!}"
                        	/>
                       	<@inputHidden 
                        	id="isIncludeInactiveUser"
                        	value="N"
                        	/>
                        <@inputHidden 
                        	id="srNumber"
                        	value="${requestParameters.srNumber!}"
                        	/>
                        <div class="row">
                            <div class="col-md-3 col-sm-3">
                                <@inputCell    
	                                id="firstName"
	                                placeholder = "First Name"
	                                />
                            </div>
                            <div class="col-md-3 col-sm-3">
                                <@inputCell    
	                                id="lastName"
	                                placeholder = "Last Name"
	                                />
                            </div>
                            <div class="col-md-3 col-sm-3">
                                <@inputCell    
	                                id="emailId"
	                                placeholder = uiLabelMap.email
	                                />
                            </div>
                            <div class="col-md-3 col-sm-3">
                                <@inputCell    
	                                id="phoneNum"
	                                placeholder = uiLabelMap.phoneNumber
	                                />
                            </div>
                            <div class="col-md-3 col-sm-3">
	                        	<#assign roleList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("roleTypeId","description").from("RoleType").where("parentTypeId","SECURITY_ROLE").queryList())?if_exists />
                        		<#assign roleMap = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(roleList, "roleTypeId","description")?if_exists />
                        
							   	<@dropdownCell 
							   		id="roleTypeId"
							   	  	placeholder="${uiLabelMap.Role}"
							   	  	options=roleMap
							   	  	allowEmpty=true
							   	  	value="CUST_SERVICE_REP"
							   	  	/>
						   	</div>
                            <div class="col-md-2 col-sm-2">
                                <@button
	                                id="sr-party-main-search-btn"
	                                label="${uiLabelMap.Find}"
	                                />
                            </div>
                        </div>
                    </form>
                </div>
                
	            <#assign rightContent1='<button type="button" class="btn btn-xs btn-primary m5" id="add-party-to-sr-btn"><i class="fa fa-plus fa-1" aria-hidden="true"></i> ${uiLabelMap.Add!}</button>' />
              <#-- <@AgGrid
	                userid="${userLogin.userLoginId}" 
	                instanceid="ALL_PARTIES"  
	                shownotifications="true"
	                autosizeallcol="true"
	                debug="false"
	                headerextra=rightContent1!
	                gridheadertitle="Party List"
	                gridheaderid="sr-assoc-party-list"
	                serversidepaginate=false
	                insertBtn=false
	                removeBtn=false
	                updateBtn=false
	                exportBtn=false
	                refreshPrefBtnId="sr-party-refresh-pref-btn"
				    savePrefBtnId="sr-party-save-pref-btn"
				    clearFilterBtnId="sr-party-clear-filter-btn"
				    exportBtnId="sr-party-export-btn"
				    removeBtnId="sr-party-remove-btn"
	                />
                <script type="text/javascript" src="/sr-portal-resource/js/ag-grid/services/find-all-parties.js"></script>-->
				<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
				<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				
						<@fioGrid 
							id="sr-party-grid"
							instanceId="ALL_PARTIES"
							jsLoc="/sr-portal-resource/js/ag-grid/services/find-all-parties.js"
							headerLabel="Party List"
							headerId="party-list-grid-action-container"
							subFltrClearId="sr-party-sub-clear-filter-btn"
							clearFilterBtnId="sr-party-clear-filter-btn"
							savePrefBtnId="sr-party-save-pref-btn"
							headerBarClass="grid-header-no-bar"
							exportBtnId="sr-party-export-btn"
							subFltrClearBtn = true
							savePrefBtn=true
							clearFilterBtn=true
							exportBtn=true
							headerExtra=rightContent1!
							serversidepaginate=false
							statusBar=false
							/>
                <form method="post" action="createSrPartyAssoc" id="createSrPartyAssocForm" name="createSrPartyAssocForm" novalidate="true" data-toggle="validator">
                    <@inputHidden
	                    id="custRequestId"
	                    value="${requestParameters.srNumber!}"/>
	                <@inputHidden
                        id="userLoginId"
                        value="${userLogin.userLoginId!}"
                        /> 
                    <@inputHidden
                        id="activeTab"
                        value="associated-parties"
                        />
                    <@inputHidden
	                    id="selecteddRows"
	                    value=""
	                    />
                </form>
            </div>
            <div class="modal-footer" style="border-top: 0px;padding-top: 0px;">
            </div>
        </div>
    </div>
</div>
 