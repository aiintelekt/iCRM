<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
      <div class="row">
        <div id="main" role="main">
          <@sectionFrameHeader title="${uiLabelMap.FindCallOutcome!}" />
            <div class="col-lg-12 col-md-12 col-sm-12">
            <div id="accordion">
              <div class="row">
                <div class="iconek">
                  <div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
                </div>
              </div>
              <div class="border rounded bg-light margin-adj-accordian pad-top">
              <form action="#" method="post" id="searchForm" name="searchForm">
                      <@inputHidden 
                        id="searchCriteria"
                      />  
                      <@inputHidden 
                        id="enumTypeId"
                        value="OPP_CALL_OUTCOME"
                      />  
                      <div class="row p-2">
                        <div class="col-md-4 col-lg-4 col-sm-12">
                      <#assign statuses = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "OPP_CALL_OUTCOME")?if_exists />
                   <#assign statusList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(statuses, "enumId","description")?if_exists /> 
                        <@dropdownCell 
                              id="enumId"
                              name="enumId"
                              placeholder="Select description"
                              options=statusList!
                               value="${requestParameters.enumId?if_exists}"
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
            
            <div class="page-header border-b pt-2">
		        <@headerH2 title="${uiLabelMap.ListOfCallOutcome!}" class="float-left"/>
		        <div class="float-right" id="main-grid-action-container">
		        <a title="Create" href="<@ofbizUrl>createOpportunityCallOutcome</@ofbizUrl>" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>
		        </div>
		        <div class="clearfix"></div>
		    </div>
		    
		    <@AgGrid
					userid="${userLogin.userLoginId}" 
					instanceid="OPPTN_CALL_OUTCOME" 
					autosizeallcol="true" 
					gridoptions='{"pagination": true, "paginationPageSize": 10 }'
					/>
            
            
             <#--                
             <div class="table-responsive">
                
               <div id="gridCalloutcome" style="width: 100%;"  class="ag-theme-balham"></div>
              <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/param-unit/opportunityCallOutCome.js"></script>
              </div> 
               -->
                          
          </div>
        </div>
      </div>   
<script> 

function loadMainGrid(fag) {
		
	$.ajax({
	  url:'/admin-portal/control/getOpportunityConfigData',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  fag.setRowData(data)
	  }
	})
	
}  
 
/* 
    function doSearch(){
             loadAgGrid();
    }
    $("#enumId").change(function() {
         loadAgGrid();
    });
*/

</script>