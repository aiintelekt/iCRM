<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
        <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
    		<@sectionFrameHeader title="${uiLabelMap.FindAppBar!}"  />
            <div class="">
                <form action="" method="post" id="searchForm" name="searchForm">
                    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                    <input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
                    <div class="row p-2">
                        <div class="col-lg-6 col-md-6 col-sm-12">
                            <@inputCell
	                            name="appBarId"
	                            id="appBarId"
	                            placeholder ="${uiLabelMap.appBarId!}"
	                            />
							<@inputCell
	                            name="appBarName"
	                            id="appBarName"
	                            placeholder ="${uiLabelMap.appBarName!}"
	                           	/>
	                        
                        </div>
                        <div class="col-lg-6 col-md-6 col-sm-12">
                            <@dropdownCell
	                            id="appBarTypeId" 
	                            placeholder="Select App Bar Type"
	                            options=appBarTypeList!
	                            value="${requestParameters.appBarTypeId!}"
	                        	/>
	                        <@dropdownCell
	                            id="barBarStatus" 
	                            placeholder="Select Status"
	                            options=appBarStatusList!
	                            value="${requestParameters.barBarStatus!}"
	                        	/>
	                        
                        </div>
                        <div class="col-lg-12 col-md-12 col-sm-12">
                            <div class="text-right">
                                <@button 
                                label="${uiLabelMap.Search}"
                                id="main-search-btn"
                                />
                            </div>
                        </div>
                    </div>
                    <#-- End row p-2-->
                </form>
            </div>
            <#-- End pad-top-->
            </div>
            <div class="clearfix"></div>
            
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <#assign extraRight='<a title="Create" href="/appbar-portal/control/createAppBar" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create</a>' />
	        <#--
	        <@AgGrid 
            	userid="${userLogin.userLoginId!}" 
	            instanceid="APP_BAR_LIST" 
	            shownotifications="true"
	            autosizeallcol="true"
	            debug="false"
	            gridheadertitle=uiLabelMap.ListOfAppBar!
		    	gridheaderid="listOfAppBarBtns"
		    	headerextra=extraRight!
            	/>
	        <script type="text/javascript" src="/appbar-portal-resource/js/ag-grid/list-of-app-bar.js"></script>
	        -->
	        <@fioGrid
				id="app-bar-list"
				instanceId="APP_BAR_LIST"
				jsLoc="/appbar-portal-resource/js/ag-grid/list-of-app-bar.js"
				headerLabel=uiLabelMap.ListOfAppBar!
				headerExtra=rightContent!
				headerBarClass="grid-header-no-bar"
				headerId="app-bar-list-tle"
				savePrefBtnId="app-bar-list-save-pref"
				clearFilterBtnId="app-bar-list-clear-pref"
				subFltrClearId="app-bar-list-clear-sub-ftr"
				serversidepaginate=false
				statusBar=false
				exportBtn=true
				exportBtnId="app-bar-list-export-btn"
				savePrefBtn=true
				clearFilterBtn=true
				subFltrClearBtn=true
				headerExtra =extraRight!
				/>

	        </div>
    	</div>
	</div>
</div>