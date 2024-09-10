<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <@sectionFrameHeader title="${uiLabelMap.FindBusinessUnits!}" />
        <div class="col-md-12 col-lg-12 col-sm-12 ">
            <div id="accordion">
                <div class="row">
                    <div class="iconek">
                        <div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
                    </div>
                </div>
                <div>
                    <div>
                        <div class="border rounded bg-light margin-adj-accordian pad-top">
                             <form action="businessUnits" method="post" id="searchForm" name="searchForm">
                                      <@inputHidden 
                                        id="searchCriteria"
                                      />
                                <div class="row p-2">
                                    <div class="col-lg-6 col-md-6 col-sm-12">
                                            <@dropdownCell
                                            name="buName"
                                            id="buName"
                                            options=buId
                                            value="${requestParameters.buName?if_exists}"
                                            allowEmpty=true
                                            placeholder ="${uiLabelMap.SelectBuName!}"
                                            />
                                          <#--<@inputCell    
                                            name="buName"
                                            id="buName"
                                            placeholder ="${uiLabelMap.BusinessUnitName!}" 
                                            />-->
                                           <@dropdownCell
                                            name="buType"
                                            id="buType"
                                            options=buTypeId
                                            value="${requestParameters.buType?if_exists}"
                                            allowEmpty=true
                                            placeholder ="${uiLabelMap.SelectBuType!}" 
                                            />
                                    </div>
                                    <div class="col-lg-6 col-md-6 col-sm-12">
                                          <#--  <@inputCell    
                                            name="parentBuName"
                                            id="parentBuName"
                                            placeholder ="${uiLabelMap.ParentBu!}" 
                                            />-->
                                            <@dropdownCell
                                            name="parentBuName"
                                            id="parentBuName"
                                            options=parentId
                                            value="${requestParameters.parentBuName?if_exists}"
                                            allowEmpty=true
                                            placeholder ="${uiLabelMap.SelectParentBuName!}"
                                            />
                                            <@dropdownCell
                                            name="buStatus"
                                            id="buStatus"
                                            options=statusId
                                            value="${requestParameters.buStatus?if_exists}"
                                            allowEmpty=true
                                            placeholder ="${uiLabelMap.SelectBuStatus!}" 
                                            />
                                    </div>
                                    <div class="col-lg-12 col-md-12 col-sm-12">
                                        <div class="text-right">
                                            <@button 
                                            label="${uiLabelMap.Search}"
                                            onclick="javascript: return doSearch();"
                                            />
                                        </div>
                                    </div>
                                </div><#-- End row p-2-->
                            </form>
                        </div><#-- End pad-top-->
                    </div>
                </div>
            </div><#-- End accordion-->
            <div class="clearfix"></div>
            <div class="page-header border-b pt-2">
                <button type="reset" class="btn btn-xs btn-primary" onclick="onBtExport()"><i class="fa fa-file-excel-o" aria-hidden="true"></i>  Export</button>
                <h2 class="float-left">
                    List of Business Units
                </h2>
                <a  title="Create" href="/admin-portal/control/createBusinessUnits_old" class="btn btn-primary btn-xs ml-2">
                <i class="fa fa-plus " aria-hidden="true"></i> Create</a>
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
                <#-- <@AgGrid 
                    userid="${userLogin.userLoginId}" 
                    instanceid="BU08" 
                    styledimensions='{"width":"100%","height":"80vh"}'
                    autosave="false"
                    autosizeallcol="true" 
                    debug="true"
                    requestbody='${searchCriteria!}'
                    endpoint="/admin-portal/control/getBusinessUnit"
                    /> -->
            </div>
            <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/businessUnit_old.js"></script> 
        </div>
    </div><#-- End main-->
</div><#-- End row-->
<script>

function doSearch(){
        loadAgGrid();
    }
 $("#buName").change(function() {
         loadAgGrid();
    });
$("#buType").change(function() {
         loadAgGrid();
    });
$("#parentBuName").change(function() {
         loadAgGrid();
    });
$("#buStatus").change(function() {
         loadAgGrid();
    });
</script>
    
    