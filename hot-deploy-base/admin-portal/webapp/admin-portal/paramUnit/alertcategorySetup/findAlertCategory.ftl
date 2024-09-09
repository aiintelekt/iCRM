<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
    <div class="row">
        <div id="main" role="main">
          <@sectionFrameHeader title="${uiLabelMap.FindAlertCategories!}" />
            <div class="col-lg-12 col-md-12 col-sm-12">
                <div id="accordion">
                    <div class="row">
                        <div class="iconek">
                            <div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
                        </div>
                    </div>
                    <div>
                        <div>
                            <div class="border rounded bg-light margin-adj-accordian pad-top">
                                <form action="findAlertCategory" method="post" id="searchForm" name="searchForm">
                                      <@inputHidden 
                                        id="searchCriteria"
                                      />
                                    <div class="row p-2">
                                        <div class="col-md-4 col-lg-4 col-sm-12">
                                            <@dropdownCell
                                            name="alertCategoryName"
                                            id="alertCategoryName"
                                            options=alertNameId
                                            value="${requestParameters.alertCategoryName?if_exists}"
                                            allowEmpty=true
                                            placeholder ="${uiLabelMap.SelectAlertCategoryName!}" 
                                            />
                                            
                                        </div>
                                        <div class="col-md-4 col-lg-4 col-sm-12">
                                            <@dropdownCell
                                            name="alertType"
                                            id="alertType"
                                            options=alertTypeId
                                            value="${requestParameters.alertType?if_exists}"
                                            allowEmpty=true
                                            placeholder ="${uiLabelMap.SelectAlertType!}" 
                                            />
                                        </div>
                                        <div class="col-md-4 col-lg-4 col-sm-12">
                                         <#assign alertPriorities = delegator.findByAnd("Enumeration", {"enumTypeId" : "PRIORITY_LEVEL","enumService","ServiceRequest","enumEntity","ServiceRequest"}, null, false)>
                                         <#assign alertPriorityList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(alertPriorities, "enumCode","description")?if_exists /> 
                                            <@dropdownCell
                                            name="alertPriority"
                                            id="alertPriority"
                                            allowEmpty=true
                                            options=alertPriorityList!
                                            value="${requestParameters.alertPriority?if_exists}"
                                            placeholder = "${uiLabelMap.SelectAlertPriority!}" 
                                            />
                                            <div class="text-right">
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
                    </div>
                </div>
                <div class="clearfix"></div>
                
                 <#assign rightContent='<a title="Create" href="/admin-portal/control/createAlertCategory" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
						 		
				<@AgGrid
				gridheadertitle="List Of Alert Categories"
				gridheaderid="alert-category-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=true
				insertBtn=false
				updateBtn=false
				removeBtn=false
				headerextra=rightContent
				
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="PARAM_ALT_CAT" 
			    autosizeallcol="true"
			    debug="false"
			    gridoptions='{"pagination": true, "paginationPageSize": 10 }'
			    />  	
			    
			    <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/param-unit/alert-category.js"></script>  		
                 
            </div>
        </div>
    </div>
   
<script>

</script>
    