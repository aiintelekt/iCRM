<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
      <div class="row">
        <div id="main" role="main">
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
          <@sectionFrameHeader title="${uiLabelMap.FindProductCatalog!}" />
            <div id="">
              <#-- <div class="row">
                <div class="iconek">
                  <div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
                </div>
              </div>  -->
              <div class="">
              <form action="#" method="post" id="searchForm" name="searchForm">
                      <@inputHidden 
                        id="searchCriteria"
                      />  
                      <div class="row">
                        <div class="col-md-4 col-lg-4 col-sm-12">
                          <#assign catalogs = delegator.findByAnd("ProdCatalog", null, null, false)?if_exists />
                          <#assign catalogList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(catalogs, "prodCatalogId","catalogName")?if_exists />
                           <@dropdownCell 
                              id="productCataLog"
                              name="productCataLog"
                              placeholder="Select Product Catalog"
                              options=catalogList!
                              value="${requestParameters.productCataLog?if_exists}"
                              allowEmpty=true
                              />
                           
                        </div>
                                <div class="col-lg-4 col-md-4col-sm-12">
                                        <div class="text-left">
                                            <@button 
                                            label="${uiLabelMap.Search}"
                                            id="main-search-btn"
                                            />
                                        </div>
                                    </div>
                        
                        
                      </div>
                  
                  </form>
                  </div>
                
              
            </div>
            <div class="clearfix"></div>
            
            </div>
            
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            
			<#assign rightContent='<a title="Create" href="/admin-portal/control/createProductCatalog" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
						 		
			<#-- <@AgGrid
			gridheadertitle=uiLabelMap.ListOfProductCatalogs
			gridheaderid="sr-type-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent
			
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="PC_PRO_CATALOG" 
		    autosizeallcol="true"
		    debug="false"
		    />  
		    
		    <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/productCategory/findProductCatalog.js"></script>-->  										
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfProductCatalogs-Grid"
			instanceId="PC_PRO_CATALOG"
			jsLoc="/admin-portal-resource/js/ag-grid/productCategory/findProductCatalog.js"
			headerLabel=uiLabelMap.ListOfProductCatalogs!
			headerId="product-catalogs-grid-action-container"
			subFltrClearId="product-catalogs-sub-filter-clear-btn"
			savePrefBtnId="product-catalogs-save-pref-btn"
			clearFilterBtnId="product-catalogs-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			exportBtnId="product-catalogs-list-export-btn"
			headerExtra=rightContent!
			/>
			</div>             
          </div>
        </div>
      </div>
      
<script>  

</script>