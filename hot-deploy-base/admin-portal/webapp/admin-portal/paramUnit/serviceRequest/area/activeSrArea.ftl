<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
      <div class="row">
        <div id="main" role="main">
        <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />  

          <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <div id="">
              <#-- <div class="row">
                <div class="iconek">
                  <div class="arrow-down" style="margin-bottom:10px;" onclick="this.classList.toggle('active')"></div>
                </div>
              </div>  -->
              
         	<@sectionFrameHeader title="${uiLabelMap.FindSrCategory!}" />
                
                  <div class="">
                   <form action="activeSrArea" method="post" id="searchForm" name="searchForm">
                      <@inputHidden 
                        id="searchCriteria"
                      />                      
                      <div class="row p-2">                        
                        <div class="col-md-6 col-lg-6 col-sm-12">
                        	<#assign srCategories = delegator.findByAnd("CustRequestAssoc", {"type" : "SRCategory"}, null, false)>
                           <#assign srCategoryList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srCategories, "code","value")?if_exists />
                           <@dropdownCell 
                              id="srCategory"
                              placeholder="Select SR Category"
                              options=srCategoryList!
                              value="${requestParameters.srCategory?if_exists}"
                              allowEmpty=true
                              />                   
                          
                        </div>
	                     <div class="col-md-2 col-sm-2">
	            	     	<@button
	            	        id="main-search-btn"
	            	        label="${uiLabelMap.Find}"
	            	        />
	            	     	<@reset
	            			label="${uiLabelMap.Reset}"/>
	                 	</div>
                      </div>
                     
                  </form>
                </div>
              
            </div>
            <div class="clearfix"></div>
            </div>   
			<div class="clearfix"></div>
			
			<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <#assign rightContent='<a title="Create" href="/admin-portal/control/serviceRequestArea" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
						 		
			<#-- <@AgGrid
			gridheadertitle=uiLabelMap.ListOfSrCAtegory
			gridheaderid="sr-category-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent
			
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="PARAM_SR_AREA" 
		    autosizeallcol="true"
		    debug="false"
		    gridoptions='{"pagination": true,"enableBrowserTooltips": true,"filter": true,
		    		"floatingFilter": true,
		    		"domLayout": "autoHeight", "paginationPageSize": 10 }'
		    />  	
		    
		    <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/param-unit/sr-category.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfSrCAtegory-Grid"
			instanceId="PARAM_SR_AREA"
			jsLoc="/admin-portal-resource/js/ag-grid/param-unit/sr-category.js"
			headerLabel=uiLabelMap.ListOfSrCAtegory!
			headerId="sr-category-grid-action-container"
			subFltrClearId="sr-category-sub-filter-clear-btn"
			savePrefBtnId="sr-category-save-pref-btn"
			clearFilterBtnId="sr-category-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			exportBtnId="sr-category-list-export-btn"
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			headerExtra=rightContent!
			/>
            </div> 
          </div>
        </div>
      </div>
      
<script>  

</script>