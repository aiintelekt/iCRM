<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#assign requestURI = request.getRequestURI()/>
<#assign activeTab = request.getAttribute("activeTab")?if_exists/>
<style>
    input[type="file"] {
    height: auto !important;
    }
</style>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <#assign extra='<a href="findUser" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
        <#assign extraLeft='<a href="#" class="btn btn-xs btn-primary"> Folin Yang</a>
        <a href="#" class="btn btn-xs btn-primary"> Windy Lai</a>
        <a href="#" class="btn btn-xs btn-primary">Darell Lim</a>
        <a href="#" class="btn btn-xs btn-primary"> June Cheng</a>
        <a href="#" class="btn btn-xs btn-primary"> Shphia Goh</a>'/>
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <@sectionFrameHeader 
            title="${uiLabelMap.UserName}: ${userData.firstName!uiLabelMap.NotAvailable} ${userData.lastName!uiLabelMap.NotAvailable}"
            extra=extra?if_exists
            />
            <@sectionHeader 
            title="${uiLabelMap.Information}"
            />  
            <div class="clearfix"></div>

                <div class="text-right position-absolute" style="right:20px;margin-bottom: 4px;">
                    <a title="Change Password" href="/admin-portal/control/resetPassword?partyId=${userData.partyId!}&USERNAME=${userData.userLoginId!}" class="btn btn-primary btn-xs ml-2">Change Password</a>
                </div>
            </ul>
            <@inputHidden id="partyId" name="partyId" value="${userData.partyId!}"/>
            <@navTab 
                instanceId="VIEW_USER"
                activeTabId="admin-details"
                />
        </div>
    </div>
    <#-- End main-->
</div>
<#-- End row -->
<div id="addsecurity" class="modal fade" role="dialog">
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Add Custom Security Groups</h3>
                <button type="reset" class="close" data-dismiss="modal"></button>
            </div>
            <div class="modal-body">
               <#-- <@AgGrid 
                userid="${userLogin.userLoginId}" 
                instanceid="ADD_CUSTOM_SECURITY"
                shownotifications="true"
                autosizeallcol="true"
                debug="false"
                insertBtn=false
                updateBtn=false
                removeBtn=false
                gridheadertitle=uiLabelMap.CustomSecurityGroup!
                gridheaderid="listCustomSecurityBtns"
                statusBar=true
                serversidepaginate=false
                refreshPrefBtnId="add-custom-security-ref-pref"
                savePrefBtnId="add-custom-security-save-pref"
                clearFilterBtnId="add-custom-security-clear-filter"
                exportBtnId="add-custom-security-export"
                /> 
                <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/add-custom-security.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="CustomSecurityGroup-Grid"
			instanceId="ADD_CUSTOM_SECURITY"
			jsLoc="/admin-portal-resource/js/ag-grid/user-mgmt/add-custom-security.js"
			headerLabel=uiLabelMap.CustomSecurityGroup!
			headerId="CustomSecurityGroup-grid-action-container"
			subFltrClearId="add-custom-security-sub-filter-clear-btn"
            savePrefBtnId="add-custom-security-save-pref"
            clearFilterBtnId="add-custom-security-clear-filter"
            exportBtnId="add-custom-security-export-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			/>
                <form method="post" action="addCustomSecurityGroup" id="addSecurityGroupForm" name="addSecurityGroupForm" novalidate="true" data-toggle="validator">
                    <@inputHidden
                    id="partyId"
                    value="${userData.partyId!}"/>
                    <@inputHidden
                    id="userLoginId"
                    value="${requestParameters.userLoginId!}"
                    />
                    <@inputHidden
                    id="selectedGroupIds"
                    value=""
                    />
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
                <@button
                label="Add"
                id="add-custom-security-btn"
                />
            </div>
        </div>
    </div>
</div>
<div id="addTeamToUser" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Add Team</h3>
                <button type="reset" class="close" data-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <#-- <@AgGrid 
                userid="${userLogin.userLoginId}" 
                instanceid="ADD_TEAM"
                shownotifications="true"
                autosizeallcol="true"
                debug="false"
                insertBtn=false
                updateBtn=false
                removeBtn=false
                gridheadertitle="List of Teams"
                gridheaderid="listOfTeamBtns"
                statusBar=true
                serversidepaginate=false
                refreshPrefBtnId="add-team-ref-pref"
                savePrefBtnId="add-team-save-pref"
                clearFilterBtnId="add-team-clear-filter"
                exportBtnId="add-team-export"
                /> 
                <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/add-user-team.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="listOfTeamBtns-Grid"
			instanceId="ADD_TEAM"
			jsLoc="/admin-portal-resource/js/ag-grid/user-mgmt/add-user-team.js"
			headerLabel="List of Teams"
			headerId="listOfTeamBtns-grid-action-container"
			subFltrClearId="add-team-sub-filter-clear-btn"
			savePrefBtnId="add-team-save-pref-btn"
			clearFilterBtnId="add-team-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			exportBtnId="add-team-list-export-btn"
			/>
                <form method="post" action="addUserTeam" id="addTeamToUserForm" name="addTeamToUserForm" novalidate="true" data-toggle="validator">
                    <@inputHidden
                    id="partyId"
                    value="${userData.partyId!}"/>
                    <@inputHidden
                    id="userLoginId"
                    value="${requestParameters.userLoginId!}"
                    />
                    <@inputHidden
                    id="isNative"
                    value="${requestParameters.isNative!}"
                    />
                    <@inputHidden
                    id="activeTab"
                    value="teams"
                    />
                    <@inputHidden
                    id="selectedRows"
                    value=""
                    />
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
                <@button
                label="Add"
                id="add-team-btn"
                />
            </div>
        </div>
    </div>
</div>
<div id="addrole" class="modal fade">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">${uiLabelMap.AddRole!}</h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <#-- <@AgGrid 
                userid="${userLogin.userLoginId}" 
                instanceid="ADD_ROLE"
                shownotifications="true"
                autosizeallcol="true"
                debug="false"
                insertBtn=false
                updateBtn=false
                removeBtn=false
                gridheadertitle="List of Roles"
                gridheaderid="listAddRoleBtns"
                statusBar=true
                serversidepaginate=false
                refreshPrefBtnId="add-role-ref-pref"
                savePrefBtnId="add-role-save-pref"
                clearFilterBtnId="add-role-clear-filter"
                exportBtnId="add-role-export"
                /> 
                <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/add-user-role.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="listAddRoleBtns-Grid"
			instanceId="ADD_ROLE"
			jsLoc="/admin-portal-resource/js/ag-grid/user-mgmt/add-user-role.js"
			headerLabel="List of Roles"
			headerId="listAddRoleBtns-grid-action-container"
			subFltrClearId="add-role-sub-filter-clear-btn"
			savePrefBtnId="add-role-save-pref-btn"
			clearFilterBtnId="add-role-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			exportBtnId="add-role-list-export-btn"
			/>
                <form method="post" action="addRoleToUser" id="addRoleToUserForm" name="addRoleToUserForm" novalidate="true" data-toggle="validator">
                    <@inputHidden
                    id="partyId"
                    value="${userData.partyId!}"/>
                    <@inputHidden
                    id="activeTab"
                    value="roles"/>
                    <@inputHidden
                    id="parentTypeId"
                    value="SECURITY_ROLE"
                    />
                    <@inputHidden
                    id="userLoginId"
                    value="${requestParameters.userLoginId!}"
                    /> 
                    <@inputHidden
                    id="selectedRowsUser"
                    value=""
                    />
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
                <@button
                label="Add"
                id="add-role-btn"
                />
            </div>
        </div>
    </div>
</div>
<div id="addRolesToUser" class="modal fade">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">${uiLabelMap.AddRole!}</h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <#-- <@AgGrid 
                userid="${userLogin.userLoginId}" 
                instanceid="ADD_ROLES_TO_USER"
                shownotifications="true"
                autosizeallcol="true"
                debug="false"
                insertBtn=false
                updateBtn=false
                removeBtn=false
                gridheadertitle="List of Roles"
                gridheaderid="listAddRoleBtns"
                statusBar=true
                serversidepaginate=false
                refreshPrefBtnId="add-roles-to-user-ref-pref"
                savePrefBtnId="add-roles-to-user-save-pref"
                clearFilterBtnId="add-roles-to-user-clear-filter"
                exportBtnId="add-roles-to-user-export"
                /> 
                <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/add-roles-to-user.js"></script>-->
                    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="listAddUserRolesBtns-Grid"
			instanceId="ADD_ROLES_TO_USER"
			jsLoc="/admin-portal-resource/js/ag-grid/user-mgmt/add-roles-to-user.js"
			headerLabel="List of Roles"
			headerId="listAddUserRolesBtns-grid-action-container"
			subFltrClearId="add-user-role-sub-filter-clear-btn"
			savePrefBtnId="add-user-role-save-pref-btn"
			clearFilterBtnId="add-user-role-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			exportBtnId="add-user-role-list-export-btn"
			/>
                <form method="post" action="addRolesToUser" id="addRolesToUserForm" name="addRolesToUserForm" novalidate="true" data-toggle="validator">
                    <@inputHidden
                    id="partyId"
                    value="${userData.partyId!}"/>
                    <@inputHidden
                    id="userLoginId"
                    value="${requestParameters.userLoginId!}"
                    /> 
                    <@inputHidden
                    id="selecteddRows"
                    value=""
                    />
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
                <@button
                label="Add"
                id="add-roles-to-user-btn"
                />
            </div>
        </div>
    </div>
</div>
<script>
    $(document).ready(function() {
    	<#if !activeTab?has_content>
        	<#assign activeTab = requestParameters.activeTab!>
        </#if>
        <#if activeTab?has_content && activeTab == "details">
        	$('.nav-tabs a[href="#admin-details"]').tab('show');
        <#elseif activeTab?has_content && activeTab == "contact">
        	$('.nav-tabs a[href="#admin-contact"]').tab('show');
        <#elseif activeTab?has_content && activeTab == "teams">
        	$('.nav-tabs a[href="#admin-teams"]').tab('show');
        <#elseif activeTab?has_content && activeTab == "roles">
        	$('.nav-tabs a[href="#admin-roles"]').tab('show');	
        <#elseif activeTab?has_content && activeTab == "security">
        	$('.nav-tabs a[href="#admin-security"]').tab('show');		
        <#elseif activeTab?has_content && activeTab == "photoupload">
        	$('.nav-tabs a[href="#admin-imageUpload"]').tab('show');
        <#else>
        	$('.nav-tabs a[href="#details"]').tab('show');	
        </#if>
        
        var url = document.URL;
    	var hash = url.substring(url.indexOf('#'));
    	$(".nav-tabs").find("li a").each(function(key, val) {
    		if (hash == $(val).attr('href')) {
    			$(val).click();
    		}
    		$(val).click(function(ky, vl) {
    			location.hash = $(this).attr('href');
    		});
    	});
    	
        
    });
    
</script>
<#-- Contact information create/update modal -->
<#-- 
<div id="createUpdateEmail" class="modal fade mt-2 save-modal" role="dialog">
<div class="modal-dialog modal-md">
<!-- Modal content--
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">${uiLabelMap.UpdateEmailAddress!}</h3>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
        </div>
        <form id="emailForm" name="emailForm" method="post" data-toggle="validator">
            <div class="modal-body">
                <@inputHidden 
                id="contactMechId"
                />
                <@inputHidden 
                id="activeTab"
                value="contact"
                />
                <@dropdownCell 
                id="contactMechPurposeTypeId"
                label="${uiLabelMap.Type}"
                placeholder="${uiLabelMap.ContactTypeId}"
                options=""
                allowEmpty=true
                disabled=true
                />
                <@inputRow
                id="emailAddress"
                label="${uiLabelMap.Email}"
                placeholder="${uiLabelMap.Email}"
                value=""
                required=true
                />
            </div>
            <div class="modal-footer">
                <@submit
                id="formSubmit"
                label="${uiLabelMap.Update}"
                />
            </div>
        </form>
    </div>
    </div>
    </div>
    <script>
    $("#createUpdateEmail").on("show.bs.modal", function(e) {
    var contactMech = $(e.relatedTarget).data('contact-mech');
    var contactPurposeTypeId = $(e.relatedTarget).data('contact-purpose-type-id');
    var modal = $(this);
    if(contactMech !=null && contactMech != "" && contactMech != 'undefined'){
        modal.find('.modal-title').text("${uiLabelMap.UpdateEmailAddress!}");
        $("#emailForm input[name=formSubmit]").val("${uiLabelMap.Update}");
        $("#emailForm input[name=contactMechId]").val(contactMech.contactMechId);
        $("#emailForm input[name=emailAddress]").val(contactMech.infoString);
        $("#emailForm input[name=contactMechPurposeTypeId]").val(contactPurposeTypeId);
    } else{
        modal.find('.modal-title').text("${uiLabelMap.CreateEmailAddress!}");
        $("#emailForm input[name=formSubmit]").val("${uiLabelMap.Create}");
        $("#emailForm input[name=contactMechId]").val('');
        $("#emailForm input[name=emailAddress]").val('');
        $("#emailForm input[name=contactMechPurposeTypeId]").val('');
    }
    /*for (const [key, value] of Object.entries(contactMech)) {
      console.log(key, value);
    }*/
    });
    </script>
    
    <div id="createUpdatePhone" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog modal-md">
    <!-- Modal content--
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">${uiLabelMap.UpdatePhoneNumber!}</h3>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
        </div>
        <form id="phoneForm" name="phoneForm" method="post" data-toggle="validator">
            <div class="modal-body">
                <@inputHidden 
                id="contactMechId"
                />
                <@inputHidden 
                id="activeTab"
                value="contact"
                />
                <@dropdownCell 
                id="contactMechPurposeTypeId"
                label="${uiLabelMap.Type}"
                placeholder="${uiLabelMap.ContactTypeId}"
                options=""
                allowEmpty=true
                disabled=true
                />  
                <@inputRow
                id="contactNumber"
                label="${uiLabelMap.PhoneNumber}"
                placeholder="${uiLabelMap.PhoneNumber}"
                value=""
                required=true
                />
            </div>
            <div class="modal-footer">
                <@submit
                id="formSubmit"
                label="${uiLabelMap.Update}"
                />
            </div>
        </form>
    </div>
    </div>
    </div>
    <script>
    $("#createUpdatePhone").on("show.bs.modal", function(e) {
    var contactMech = $(e.relatedTarget).data('contact-mech');
    var contactPurposeTypeId = $(e.relatedTarget).data('contact-purpose-type-id');
    var modal = $(this);
    if(contactMech !=null && contactMech != "" && contactMech != 'undefined'){
        modal.find('.modal-title').text("${uiLabelMap.UpdatePhoneNumber!}");
        $("#phoneForm input[name=formSubmit]").val("${uiLabelMap.Update}");
        $("#phoneForm input[name=contactMechId]").val(contactMech.contactMechId);
        $("#phoneForm input[name=contactNumber]").val(contactMech.contactNumber);
        $("#phoneForm input[name=contactMechPurposeTypeId]").val(contactPurposeTypeId);
    } else{
        modal.find('.modal-title').text("${uiLabelMap.CraetePhoneNumber!}");
        $("#phoneForm input[name=formSubmit]").val("${uiLabelMap.Create}");
        $("#phoneForm input[name=contactMechId]").val('');
        $("#phoneForm input[name=contactNumber]").val('');
        $("#phoneForm input[name=contactMechPurposeTypeId]").val('');
    }
    });
    </script>
    
    <div id="createUpdatePostal" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog modal-md">
    <!-- Modal content--
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">${uiLabelMap.UpdateAddress!}</h3>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
        </div>
        <form id="addressForm" name="addressForm" method="post" data-toggle="validator">
            <div class="modal-body">
                <@inputHidden 
                id="contactMechId"
                />
                <@inputHidden 
                id="activeTab"
                value="contact"
                />
                <@dropdownCell 
                id="contactMechPurposeTypeId"
                label="${uiLabelMap.Type}"
                placeholder="${uiLabelMap.ContactTypeId}"
                options=""
                allowEmpty=true
                disabled=true
                />  
                <@inputRow
                id="address1"
                label="${uiLabelMap.Address1}"
                placeholder="${uiLabelMap.Address1}"
                value=""
                required=true
                />
                <@inputRow
                id="address2"
                label="${uiLabelMap.Address2}"
                placeholder="${uiLabelMap.Address2}"
                value=""
                />
                <@inputRow
                id="address3"
                label="${uiLabelMap.Address3}"
                placeholder="${uiLabelMap.Address3}"
                value=""
                />
                <@inputRow
                id="city"
                label="${uiLabelMap.City}"
                placeholder="${uiLabelMap.City}"
                value=""
                />
                <@inputState
                name="stateProvinceGeoId"
                label="${uiLabelMap.StateOrProvince}"
                />   
                <@inputRow
                id="postalCode"
                label="${uiLabelMap.ZipOrPostalCode}"
                placeholder="${uiLabelMap.ZipOrPostalCode}"
                value=""
                />
                <@inputCountry
                name="countryGeoId"
                defaultCountry=true
                label="${uiLabelMap.CountryOrRegion}"
                countryValue=""
                stateValue=""
                />
            </div>
            <div class="modal-footer">
                <@submit
                id="formSubmit"
                label="${uiLabelMap.Update}"
                />
            </div>
        </form>
    </div>
    </div>
    </div>  
    
    <script>
    $("#addRoleSubmit").click(function () {
         var form = document.getElementById("addRoleToUserForm");
           var rowdata = getSelectedUserRolesRows();
           
         if(rowdata){
             rowdata.forEach(element => {
             
             var roleTypeId = element.roleTypeId;
             var parentTypeId = element.parentTypeId;
             
             $(form).append(
             $('<input>').attr('type', 'hidden').attr('name', 'roleTypeId').val(roleTypeId));
            });
            form.submit();
          } else {
             $.notify({
               message : '<p>Please select atleast one record in the list</p>',
             });
          }
      });	
    
    
    
    
    
    $("#createUpdatePostal").on("show.bs.modal", function(e) {
       var contactMech = $(e.relatedTarget).data('contact-mech');
       var contactPurposeTypeId = $(e.relatedTarget).data('contact-purpose-type-id');
       var modal = $(this);
       if(contactMech !=null && contactMech != "" && contactMech != 'undefined'){
           modal.find('.modal-title').text("${uiLabelMap.UpdateAddress!}");
           $("#addressForm input[name=formSubmit]").val("${uiLabelMap.Update}");
           $("#addressForm input[name=contactMechId]").val(contactMech.contactMechId);
           $("#addressForm input[name=contactMechPurposeTypeId]").val(contactPurposeTypeId);
           $("#addressForm input[name=address1]").val(contactMech.address1);
           $("#addressForm input[name=address2]").val(contactMech.address2);
           $("#addressForm input[name=address3]").val(contactMech.address3);
           $("#addressForm input[name=city]").val(contactMech.city);
           $("#addressForm input[name=postalCode]").val(contactMech.postalCode);
           $("#addressForm input[name=countryGeoId]").val(contactMech.countryGeoId);
           
       } else{
           modal.find('.modal-title').text("${uiLabelMap.CreateAddress!}");
           $("#addressForm input[name=formSubmit]").val("${uiLabelMap.Create}");
           $("#addressForm input[name=contactMechId]").val('');
           $("#addressForm input[name=contactMechPurposeTypeId]").val('');
       }
       
    });
    </script>-->