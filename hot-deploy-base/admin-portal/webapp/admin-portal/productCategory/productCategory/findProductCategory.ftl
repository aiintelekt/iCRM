<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
      <div class="row">
        <div id="main" role="main">
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
          	<@sectionFrameHeader title="${uiLabelMap.FindProductCategory!}" />
            <div id="">
              <#--<div class="row">
                <div class="iconek">
                  <div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
                </div>
              </div>-->
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
                              id="prodCatalogId"
                              name="prodCatalogId"
                              placeholder="Select Product Catalog"
                              options=catalogList!
                              value="${requestParameters.prodCatalogId?if_exists}"
                              allowEmpty=true
                              />
                           
                        </div>
                        <div class="col-md-4 col-lg-4 col-sm-12">
                          <#assign categories = delegator.findByAnd("ProductCategory", null, null, false)?if_exists />
                          <#assign catalogList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(categories, "productCategoryId","categoryName")?if_exists />
                           <@dropdownCell 
                              id="productCategoryId" 
                              name="productCategoryId"
                              placeholder="Select Product Category"
                              options=catalogList!
                              value="${requestParameters.productCategoryId?if_exists}"
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
            
			<#assign rightContent='<a title="Create" href="/admin-portal/control/createProductCategory" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
						 		
			<#-- <@AgGrid
			gridheadertitle=uiLabelMap.ListOfProductCategories
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
		    instanceid="PC_PRO_CATEGORY" 
		    autosizeallcol="true"
		    debug="false"
		    />  
		    
		    <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/productCategory/findProductCategory.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfProductCategories-Grid"
			instanceId="PC_PRO_CATEGORY"
			jsLoc="/admin-portal-resource/js/ag-grid/productCategory/findProductCategory.js"
			headerLabel=uiLabelMap.ListOfProductCategories!
			headerId="product-category-grid-action-container"
			subFltrClearId="product-category-sub-filter-clear-btn"
			savePrefBtnId="product-category-save-pref-btn"
			clearFilterBtnId="product-category-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			headerExtra=rightContent!
			exportBtnId="product-category-list-export-btn"
			/>							
			 </div>	               
          </div>
        </div>
      </div>
      
<script>  

</script>