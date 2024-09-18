<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
    <div class="row">
        <div id="main" role="main">
          <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <#assign extra='<a href="viewBusinessUnits?productStoreGroupId=${productStoreGroupId!}" class="btn btn-xs btn-primary">
                <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
            <@sectionFrameHeader title="${uiLabelMap.ViewTeam!}" extra=extra />
          
                <div class="" id="cp">
                    <div class="row">
                        <div class="col-md-6 col-lg-3 col-sm-12 ">
                            <@displayCell    
                                label="${uiLabelMap.TeamName!}"
                                value="${teamName!}"
                                />
                        </div>
                       <div class="col-lg-3 col-md-6 col-sm-12 ">
                            <@displayCell    
                                label="${uiLabelMap.TeamId!}"
                                value="${emplTeamId!}"
                                />
                        </div>
                        <div class="col-md-6 col-lg-3 col-sm-12 ">
                            <@displayCell    
                                label="${uiLabelMap.BusinessUnit!}"
                                value="${businessUnitName!}"
                                />
                        </div>
                        <div class="col-lg-3 col-md-6 col-sm-12 ">
                            <@displayCell    
                                label="${uiLabelMap.TeamStatus!}"
                                value="${status!}"
                                />
                        </div>
                        <@inputHidden    
                            id="emplTeamId"
                            name="emplTeamId"
                            value="${emplTeamId!}"
                          />
                    </div>
                </div>
                <div class=" pt-2">
                    <div class="clearfix"></div>
                </div>
                <div class="clearfix"></div>
                <div class="table-responsive">
                <div class="loader text-center" id="loader" sytle="display:none;">
                      <span></span>
                      <span></span>
                      <span></span>
                    </div>
                <div id="grid1" style="width: 100%;" class="ag-theme-balham"></div>
                  <#--   <@AgGrid 
                        userid="${userLogin.userLoginId}" 
                        instanceid="BU06" 
                        styledimensions='{"width":"100%","height":"80vh"}'
                        autosave="false"
                        autosizeallcol="true" 
                        debug="true"
                        requestbody='{"emplTeamId":"${emplTeamId!}"}'
                        />-->
                <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/bu-users.js"></script>
                </div>
                <div class="clearfix"></div>
                <!--<div class="page-header border-b mt-2">
                    <h2>Audit History</h2>
                    <div class="clearfix"></div>
                </div>
                <div class="table-responsive">
                    
                </div>-->
            </div>
        </div>
    </div>    