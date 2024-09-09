<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
   	<#assign extra='<a href="updateProductSubCategory?productCategoryId=${inputContext.categoryId!}&prodCatalogId=${inputContext.prodCatalogId!}&subCategoryId=${inputContext.subCategoryId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="findProductSubCategory" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
        
      <#assign extraLeft=''/>
            
      	<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
      	
        <@sectionFrameHeaderTab title="${uiLabelMap.ViewProductSubCategory!}" tabId="viewProductSubCategory" extra=extra?if_exists extraLeft=extraLeft/> 
				
			 <@dynaScreen 
				instanceId="PC_PRO_SUB_CATEGORY"
				modeOfAction="VIEW"
				/>    
					
        
        	 <div class="clearfix"></div>
		        <form action="#" method="post" id="searchForm" name="searchForm">
		            <@inputHidden 
		              id="productCategoryId"
		              name="productCategoryId"
		              value="${inputContext.get('productCategoryId')!}"
		            />
		            <@inputHidden 
		              id="prodCatalogId"
		              name="prodCatalogId"
		              value="${inputContext.get('prodCatalogId')!}"
		            />
		            <@inputHidden 
		              id="subCategoryId"
		              name="subCategoryId"
		              value="${inputContext.get('subCategoryId')!}"
		            />
		        </form>
		        
		        
        
        </div>
       
   </div>
</div>

