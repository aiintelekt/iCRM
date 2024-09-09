<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
   
   	<#assign extra='<a href="updateProductCatalog?prodCatalogId=${inputContext.prodCatalogId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="findProductCatalog" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
 
      <#assign extraLeft=''/>
            
      	<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
      	
      	<@sectionFrameHeaderTab title="${uiLabelMap.ViewProductCatalog!}" tabId="viewProductCatalog" extra=extra?if_exists extraLeft=extraLeft/> 
				
			 <@dynaScreen 
				instanceId="PC_PRO_CATALOG"
				modeOfAction="VIEW"
				/>    
					
        
        	 <div class="clearfix"></div>
		        <form action="#" method="post" id="searchForm" name="searchForm">
		            <@inputHidden 
		              id="prodCatalogId"
		              name="prodCatalogId"
		              value="${inputContext.get('prodCatalogId')!}"
		            />
		        </form>
		        
		        <#--<@AgGrid
					gridheadertitle=uiLabelMap.AuditHistory!
					gridheaderid="sr-type-audit-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=true
					insertBtn=false
					updateBtn=false
					removeBtn=false
				    userid="${userLogin.userLoginId}" 
				    shownotifications="true" 
				    instanceid="SR_TYPE_AUDIT_LOG" 
				    autosizeallcol="true"
				    debug="false"
				    />  
		        <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/audit-log-history/sr-type-audit-log.js"></script>
		         
		        <h2 class="float-left">${uiLabelMap.AuditHistory!} </h2>
		        <div class="clearfix"></div>
		        
		        <div class="table-responsive">
		                <div class="loader text-center" id="loader" sytle="display:none;">
		                  <span></span>
		                  <span></span>
		                  <span></span>
		                </div>
		               <div id="grid" style="width: 100%;" class="ag-theme-balham"></div>
		                <#-- <@AgGrid 
		                userid="${userLogin.userLoginId}" 
		                instanceid="SRT01" 
		                styledimensions='{"width":"100%","height":"80vh"}'
		                autosave="false"
		                autosizeallcol="true" 
		                debug="true"
		                requestbody='${searchCriteria!}'
		                endpoint="/admin-portal/control/getSrType"
		                /> --
		              </div>
		              <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/audit-log-history/sr-type-audit-log.js"></script> 
		        </div> -->
        
        </div>
       
   </div>
</div>

