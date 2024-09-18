<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      <@sectionFrameHeader title="${uiLabelMap.FindProduct!}" />
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
                     <div class="col-lg-4 col-md-6 col-sm-12">
                        <#assign catalogs = delegator.findByAnd("ProductCategoryCatalogAssoc", null, null, false)?if_exists />
                        <#assign catalogList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(catalogs, "catalogId","catalogName")?if_exists />
                        <@dropdownCell 
                        id="prodCatalogId"
                        name="prodCatalogId"
                        placeholder="Select Product Catalog"
                        options=catalogList!
                        value="${requestParameters.prodCatalogId?if_exists}"
                        allowEmpty=true
                        />
                        <#assign categories = delegator.findByAnd("ProductCategoryCatalogAssoc", null, null, false)?if_exists />
                        <#assign categoryList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(categories, "categoryId","categoryName")?if_exists />
                        <@dropdownCell 
                        id="productCategoryId" 
                        name="productCategoryId"
                        placeholder="Select Product Catalog"
                        options=categoryList!
                        value="${requestParameters.productCategoryId?if_exists}"
                        allowEmpty=true
                        />
                     </div>
                     <div class="col-lg-4 col-md-6 col-sm-12">
                        <#assign subCategories = delegator.findByAnd("ProductCategoryCatalogAssoc", null, null, false)?if_exists />
                        <#assign subCategoryList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(categories, "subCategoryId","subCategoryName")?if_exists />
                        <@dropdownCell 
                        id="productSubCategoryId" 
                        name="productSubCategoryId"
                        placeholder="Product Sub Category"
                        options=subCategoryList!
                        value="${requestParameters.productSubCategoryId?if_exists}"
                        allowEmpty=true
                        />
                     </div>
                     <div class="col-lg-4 col-md-6 col-sm-12">
	                        <#assign products = delegator.findByAnd("ProductCategoryCatalogAssoc", null, null, false)?if_exists />
	                        <#assign productList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(products, "productId","productName")?if_exists />
	                        <@dropdownCell 
	                        id="productId" 
	                        name="productId"
	                        placeholder="Select Product"
	                        options=productList!
	                        value="${requestParameters.productId?if_exists}"
	                        allowEmpty=true
	                        />
	                     <div class="search-btn">
	                        <@button
	                        id="main-search-btn"
	                        label="${uiLabelMap.Find}"
	                        />
	                        <@reset
	                        label="${uiLabelMap.Reset}"
	                        />
	                     </div>
                     </div>
                  </div>
            </div>
            </form>
         </div>
      </div>
      <div class="clearfix"></div>
       </div>
 <div  style="width:100%">     
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel"> 
      <#assign rightContent='<a title="Create" href="/product-portal/control/createCmsProduct?externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
    <#-- <@AgGrid
      gridheadertitle=uiLabelMap.ListOfProducts
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
      instanceid="PC_PROD" 
      autosizeallcol="true"
      debug="false"
      />  
      <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/productCategory/findProduct.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfProducts-Grid"
			instanceId="PC_PROD"
			jsLoc="/admin-portal-resource/js/ag-grid/productCategory/findProduct.js"
			headerLabel=uiLabelMap.ListOfProducts!
			headerId="pc-grid-action-container"
			subFltrClearId="pc-sub-filter-clear-btn"
			savePrefBtnId="pc-save-pref-btn"
			clearFilterBtnId="pc-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			exportBtnId="pc-list-export-btn"
			headerExtra=rightContent!
			/>							
   </div>
</div>
</div>
</div>
</div>
<script>  </script>