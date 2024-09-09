<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
        <div class="row">
            <div id="main" role="main">
            
            <#assign extra='<a href="updateAlertCategory?alertCategoryId=${inputContext.alertCategoryId!}" class="btn btn-xs btn-primary text-right"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="findAlertCategory" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back </a>' />
            
            <#-- 
            <#if security.hasPermission("DBS_ADMPR_PUS_ACS_EDIT", userLogin)>
            <#assign extra='<a href="updateAlertCategory?alertCategoryId=${alertCategoryId!}" class="btn btn-xs btn-primary text-right"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="findAlertCategory" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back </a>' />
             <#else>
             <#assign extra='<a href="findAlertCategory" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back </a>' />
             </#if>
               -->
               
         <@sectionFrameHeader title="${uiLabelMap.ViewAlertCategorySetup!}" extra=extra />
         
         
                    <div class="col-md-12 col-lg-12 col-sm-12">
                    
                    <@dynaScreen 
					instanceId="PARAM_ALT_CAT"
					modeOfAction="VIEW"
					/>
                    	
            <div class="clearfix"></div>
           <!-- <div class="page-header border-b pt-2">
              <h2 >Audit History</h2>             
              <div class="clearfix"></div>
            </div>
                    <div class="table-responsive">
                 <div id="cbggrid" style="height: 300px; width: 100%;" class="ag-theme-balham"></div>
            </div>-->
                </div>
            </div>
        </div>
