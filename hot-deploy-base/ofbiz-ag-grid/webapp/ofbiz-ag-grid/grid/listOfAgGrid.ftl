<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
        <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
	    	<@sectionFrameHeader title="${uiLabelMap.FindAgGridInstance!}"  />
            <div class="">
                <form action="" method="post" id="searchForm" name="searchForm">
                    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                    <input type="hidden" name="userLoginId" value="${userLogin.userLoginId?if_exists}"/>
                    <div class="row">
                        <div class="col-lg-6 col-md-6 col-sm-12">
                            <@inputCell
	                            name="instanceId"
	                            id="instanceId"
	                            placeholder ="${uiLabelMap.InstanceId!}"
	                            />
	                        <@inputCell
	                            name="role"
	                            id="role"
	                            placeholder ="${uiLabelMap.Type!}"
	                            />
                        </div>
                        <div class="col-lg-6 col-md-6 col-sm-12">
                            <@inputCell
	                            name="name"
	                            id="name"
	                            placeholder ="${uiLabelMap.Name!}" 
	                            />
	                        <@inputCell
	                            name="userId"
	                            id="userId"
	                            placeholder ="${uiLabelMap.UserLoginId!}"
	                           	/>
                        </div>
                        <div class="col-lg-12 col-md-12 col-sm-12 pad-10">
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
            <div class="clearfix"></div>
            </div>
            
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	        <#--<@AgGrid 
            	userid="${userLogin.userLoginId!}" 
	            instanceid="AG_GRID_LIST" 
	            shownotifications="true"
	            autosizeallcol="true"
	            debug="false"
	            gridheadertitle=uiLabelMap.ListOfAgGrid!
		    	gridheaderid="listOfAgGridBtns"
		    	statusBar=true
		    	serversidepaginate=false
            	/>
	        <script type="text/javascript" src="/ofbiz-ag-grid-resource/js/ag-grid/list-of-ag-grid.js"></script>
	        -->
	        <@fioGrid
				id="ag-grid-list"
				instanceId="AG_GRID_LIST"
				jsLoc="/ofbiz-ag-grid-resource/js/ag-grid/list-of-ag-grid.js"
				headerLabel=uiLabelMap.ListOfAgGrid!
				headerExtra=extraRight!
				headerBarClass="grid-header-no-bar"
				headerId="ag-grid-list-tle"
				savePrefBtnId="ag-grid-list-save-pref"
				clearFilterBtnId="ag-grid-list-clear-pref"
				subFltrClearId="ag-grid-list-clear-sub-ftr"
				serversidepaginate=false
				statusBar=true
				exportBtn=true
				exportBtnId="ag-grid-list-export-btn"
				savePrefBtn=true
				clearFilterBtn=true
				subFltrClearBtn=true
				/>
	        </div>
    	</div>
	</div>
</div>