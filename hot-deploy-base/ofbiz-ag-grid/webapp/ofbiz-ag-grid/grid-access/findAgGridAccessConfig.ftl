<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
        <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
	    	<@sectionFrameHeader title="${uiLabelMap.FindAgGridAccessConfig!}" />
            <div class="">
                <form action="" method="post" id="searchForm" name="searchForm">
                    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                    <input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
                    <div class="row">
                        <div class="col-lg-6 col-md-6 col-sm-12">
                            <@inputCell
	                            name="instanceId"
	                            id="instanceId"
	                            placeholder ="${uiLabelMap.InstanceId!}"
	                            />
                        </div>
                        <div class="col-lg-6 col-md-6 col-sm-12">
                           <@inputCell
	                            name="groupId"
	                            id="groupId"
	                            placeholder ="${uiLabelMap.GroupId!}"
	                            />
                        </div>
                        <#--<div class="col-lg-6 col-md-6 col-sm-12">
                        </div> -->
                        <div class="col-lg-12 col-md-12 col-sm-12">
                            <div class="text-right pad-10">
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
            <div class="clearfix"></div>
            </div>
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <#--
            <@AgGrid 
            	userid="${userLogin.userLoginId!}" 
	            instanceid="AG_GRID_ACCESS_CONFIG" 
	            shownotifications="true"
	            autosizeallcol="true"
	            debug="false"
	            gridheadertitle=uiLabelMap.ListOfAgGridAccessConfiguration!
		    	gridheaderid="agGridAccessConfigBtn"
            	/>
	        <script type="text/javascript" src="/ofbiz-ag-grid-resource/js/ag-grid/ag-grid-access-config.js"></script>
	        -->
	        <@fioGrid
				id="ag-grid-access-config"
				instanceId="AG_GRID_ACCESS_CONFIG"
				jsLoc="/ofbiz-ag-grid-resource/js/ag-grid/ag-grid-access-config.js"
				headerLabel=uiLabelMap.ListOfAgGridAccessConfiguration!
				headerExtra=extraRight!
				headerBarClass="grid-header-no-bar"
				headerId="ag-grid-access-config-tle"
				savePrefBtnId="ag-grid-access-config-save-pref"
				clearFilterBtnId="ag-grid-access-config-clear-pref"
				subFltrClearId="ag-grid-access-config-clear-sub-ftr"
				serversidepaginate=false
				statusBar=true
				exportBtn=true
				exportBtnId="ag-grid-access-export-btn"
				savePrefBtn=true
				clearFilterBtn=true
				subFltrClearBtn=true
				/>
    	</div>
    	</div>
	</div>
</div>