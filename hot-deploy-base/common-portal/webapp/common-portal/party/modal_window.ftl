<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<#macro createPartyAssocModal instanceId fromAction="">
<div id="${instanceId!}" class="modal fade" >
    <div class="modal-dialog modal-lg" style="max-width: 1700px;">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.findParty!}</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form id="add-partyassoc-form" method="post" data-toggle="validator">
                    <input type="hidden" name="activeTab" value="associated-parties" />
                    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                    
                    <div class="row">
						<div class="col-md-4 col-md-4 form-horizontal">
							<@inputRow 
							id="${instanceId!}_partyId"
							name="partyId"
							placeholder=uiLabelMap.partyId
							inputColSize="col-sm-12"
							required=false
							/> 	
							
							<@inputRow 
							id="${instanceId!}_emailAddress"
							name="email"
							placeholder=uiLabelMap.email
							inputColSize="col-sm-12"
							required=false
							/>
					  	</div>
					 
					  	<div class="col-md-4 col-md-4 form-horizontal">
					  		<@inputRow 
							id="${instanceId!}_localName"
							name="localName"
							placeholder=uiLabelMap.localName
							inputColSize="col-sm-12"
							required=false
							/> 		
							<@inputRow 
							id="${instanceId!}_phone"
							name="phone"
							placeholder=uiLabelMap.Phone
							inputColSize="col-sm-12"
							required=false
							/> 	
					  	</div>
					  
					  	<div class="col-md-4 col-md-4 form-horizontal text-right">
					      	<@inputRow 
								id="${instanceId!}_partyName"
								name="name"
								placeholder=uiLabelMap.Name
								inputColSize="col-sm-12"
								required=false
								/> 	
						  
							  <#if isShowPartyLevelFilter?has_content && isShowPartyLevelFilter=="Y">		
							  <@dropdownCell 
							    id="partyLevel"
							    options=partyLevelList?if_exists
							    required=false
							    allowEmpty=true
							    placeholder = uiLabelMap.partyLevel
								/>	
							  </#if>
							  
	                        	<#assign roleList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("roleTypeId","description").from("RoleType").where("parentTypeId","SECURITY_ROLE").queryList())?if_exists />
                        		<#assign roleMap = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(roleList, "roleTypeId","description")?if_exists />
							   	<@dropdownCell 
							   		id="roleTypeId"
							   	  	placeholder="${uiLabelMap.Role}"
							   	  	options=roleMap
							   	  	allowEmpty=true
							   	  	value="CUST_SERVICE_REP"
							   	  	/>
	                          
	                      	<div class="search-btn pb-3">
                           	<@button 
			            		label="${uiLabelMap.Find}"
			            		id="add-partyassoc-search-btn"
			            	/>
			           	 	</div>	
			        		
                	 	</div>
					</div>
                </form>
            	
            	<#local rightContent1='<button type="button" class="btn btn-xs btn-primary m5" id="add-partyassoc-btn"><i class="fa fa-plus fa-1" aria-hidden="true"></i> ${uiLabelMap.Add!}</button>' />
	           <#-- <@AgGrid
					gridheadertitle=uiLabelMap.ListOfPartys
					gridheaderid="${instanceId!}_partyassoc-grid-action-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=false
					insertBtn=false
					updateBtn=false
					removeBtn=false
					headerextra=rightContent1!
					refreshPrefBtnId="partyassoc-refresh-pref-btn"
				    savePrefBtnId="partyassoc-save-pref-btn"
				    clearFilterBtnId="partyassoc-clear-filter-btn"
				    exportBtnId="partyassoc-export-btn"
				    removeBtnId="partyassoc-remove-btn"					
				    userid="${userLogin.userLoginId}" 
				    shownotifications="true" 
				    instanceid="ALL_PARTIES" 
				    autosizeallcol="true"
				    debug="false"
				    />    
				         
				<script type="text/javascript" src="/common-portal-resource/js/ag-grid/party/add-party-assoc.js"></script>-->
				<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
				<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				
						<@fioGrid 
							id="ListOfPartys-grid"
							instanceId="ALL_PARTIES"
							jsLoc="/common-portal-resource/js/ag-grid/party/add-party-assoc.js"
							headerLabel=uiLabelMap.ListOfPartys!
							headerId="ListOfPartys-grid-action-container"
							subFltrClearId="partyassoc-sub-clear-filter-btn"
							clearFilterBtnId="partyassoc-clear-filter-btn"
							savePrefBtnId="partyassoc-save-pref-btn"
							headerBarClass="grid-header-no-bar"
							exportBtnId="partyassoc-export-btn"
							subFltrClearBtn = true
							savePrefBtn=true
							clearFilterBtn=true
							exportBtn=true
							headerExtra=rightContent1!
							serversidepaginate=false
							statusBar=false
							/>
	            
	            <div class="modal-footer">
	                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
	            </div>
            
            </div>
            
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
    


});
</script> 
</#macro>