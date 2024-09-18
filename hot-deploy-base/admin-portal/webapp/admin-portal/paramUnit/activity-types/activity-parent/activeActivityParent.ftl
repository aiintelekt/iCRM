<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
      <div class="row">
        <div id="main" role="main">
          <@sectionFrameHeader title="${uiLabelMap.FindActivityParent!}" />

          <div class="col-lg-12 col-md-12 col-sm-12"> 
<div id="accordion">
              <div class="row">
                <div class="iconek">
                  <div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
                </div>
              </div>
			                
                  <div class="border rounded bg-light margin-adj-accordian pad-top">
                    <form action="activeActivityParent" method="post" id="searchForm" name="searchForm">
                      <@inputHidden 
                        id="searchCriteria"
                      />
                      <div class="row p-2">
                        <div class="col-md-4 col-lg-4 col-sm-12">
                        <#assign srTypes = delegator.findByAnd("WorkEffortAssocTriplet", {"type" : "RelatedTo"}, null, false)>
                           <#assign srTypeList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srTypes, "code","value")?if_exists />
                           <@dropdownCell 
                              id="activityParent"
                              name="activityParent"
                              placeholder=uiLabelMap.SelectActivityParent
                              options=srTypeList!
                              value="${requestParameters.activityParent?if_exists}"
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
            
			<#assign rightContent='<a title="Create" href="/admin-portal/control/activityParent" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
						 		
			<@AgGrid
			gridheadertitle=uiLabelMap.ListOfActivityParent
			gridheaderid="activity-parent-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent
			
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="PARAM_ACT_PRNT" 
		    autosizeallcol="true"
		    debug="false"
		    gridoptions='{"pagination": true, "paginationPageSize": 10 }'
		    />  		
		    
		    <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/param-unit/activity-parent.js"></script>  
             
          </div>
        </div>
      </div>
      
<script> 

</script>