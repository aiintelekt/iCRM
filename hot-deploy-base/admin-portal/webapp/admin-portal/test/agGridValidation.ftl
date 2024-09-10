<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
    	<@sectionFrameHeader title="${uiLabelMap.FindPerson!}" />
        <div class="col-md-12 col-lg-12 col-sm-12 ">
            <div class="border rounded bg-light margin-adj-accordian pad-top">
                <form action="" method="post" id="searchForm" name="searchForm">
                    <div class="row p-2">
                        <div class="col-lg-6 col-md-6 col-sm-12">
                            <@inputCell
	                            name="partyId"
	                            id="partyId"
	                            placeholder ="${uiLabelMap.PartyId!}"
	                            />
                        </div>
                        <div class="col-lg-6 col-md-6 col-sm-12">
                            <@inputCell
	                            name="name"
	                            id="name"
	                            placeholder ="${uiLabelMap.Name!}" 
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
            <#-- 
            <@gridHeader title=uiLabelMap.ListOfPerson
		    	id="viewPersonBtns"
		    	insertBtn=true
		    	updateBtn=true
		    	removeBtn=true
		    	refreshPrefBtn=false
		    	savePrefBtn=true
		    	clearFilterBtn=true
		    	exportBtn=true
		    	/>
		    	
            <@AgGrid
	            userid="${userLogin.userLoginId}" 
	            shownotifications="true" 
	            instanceid="GRID_TEST_1" 
	            autosizeallcol="true"
	            debug="false"
	            /> -->
	        <@AgGrid 
            	userid="${userLogin.userLoginId!}" 
	            instanceid="GRID_TEST_1" 
	            shownotifications="true"
	            autosizeallcol="true"
	            debug="false"
	            gridheadertitle=uiLabelMap.ListOfPerson!
		    	gridheaderid="viewPersonBtns"
            	/>
	        <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/test-ag-grid1.js"></script>
    	</div>
    	
    	<@sectionFrameHeader title="${uiLabelMap.Grid2!}" />
        <div class="col-md-12 col-lg-12 col-sm-12 ">
        	<div class="border rounded bg-light margin-adj-accordian pad-top">
                <form action="" method="post" id="searchForm2" name="searchForm2">
                    <div class="row p-2">
                        <div class="col-lg-6 col-md-6 col-sm-12">
                            <@inputCell
	                            name="partyId"
	                            id="partyId"
	                            placeholder ="${uiLabelMap.PartyId!}"
	                            />
                            <@inputCell
	                            name="name"
	                            id="name"
	                            placeholder ="${uiLabelMap.Name!}" 
	                            />
                        </div>
                        <div class="col-lg-12 col-md-12 col-sm-12">
                            <div class="text-right">
                                <@button 
                                label="${uiLabelMap.Search}"
                                id="main-search-btn1"
                                />
                            </div>
                        </div>
                    </div>
                    <#-- End row p-2-->
                </form>
            </div>
            
            <@AgGrid 
            	userid="${userLogin.userLoginId!}" 
	            instanceid="GRID_TEST_2" 
	            gridheadertitle=uiLabelMap.ListOfParty!
		    	insertBtnId="insert-btn1"
		    	updateBtnId="update-btn1"
		    	removeBtnId="remove-btn1"
		    	refreshPrefBtnId="refresh-pref-btn1"
		    	savePrefBtnId="save-pref-btn1"
		    	clearFilterBtnId="clear-filter-btn1"
		    	exportBtnId="export-btn1"
            	/>
            <#--
            <@gridHeader title=uiLabelMap.ListOfParty
		    	id="viewPartyBtns"
		    	refreshPrefBtn=false
		    	insertBtnId="insert-btn1"
		    	updateBtnId="update-btn1"
		    	removeBtnId="remove-btn1"
		    	refreshPrefBtnId="refresh-pref-btn1"
		    	savePrefBtnId="save-pref-btn1"
		    	clearFilterBtnId="clear-filter-btn1"
		    	exportBtnId="export-btn1"
		    	/>
           <@AgGrid
	            userid="${userLogin.userLoginId}" 
	            instanceid="GRID_TEST_2" 
	            autosizeallcol="true" 
	            debug="true"
	            gridoptions='{"pagination": true, "paginationPageSize": 10,"custom":{"dataUniqueIdField":"partyId"} }'
	            /> 
	            -->
			<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/test-ag-grid2.js"></script>           
        </div>
        
    	
    </div>
    <#-- End main-->
</div>
<#-- End row-->