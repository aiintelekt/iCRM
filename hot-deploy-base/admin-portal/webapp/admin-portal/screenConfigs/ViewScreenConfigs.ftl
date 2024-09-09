<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div id="main" role="main" class="pd-btm-title-bar">
      <@sectionFrameHeader title="Find Screen Configurations" />
      <div class="col-lg-12 col-md-12 col-sm-12">
      
      	<form method="post" action="findMarketingCampaigns" id="findMarketingCampaigns" class="form-horizontal" name="findMarketingCampaigns" novalidate="novalidate" data-toggle="validator">
      	<div class="row p-2">
                    <div class="col-lg-4 col-md-6 col-sm-12">
                        <#assign componentList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("componentName","uiLabels").from("OfbizComponentAccess").where("isHide","N").queryList())?if_exists />
                        <#assign components = (Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(componentList, "componentName","uiLabels"))?if_exists />

                        <@dropdownCell
                            id="module"
                            placeholder="Select Module"
                            allowEmpty=true
                            options=components!
                            value="${requestParameters.module?if_exists}"
                            />
                    </div>
                    <div class="col-lg-4 col-md-6 col-sm-12">
                        <@inputCell
                            id="layout"
                            placeholder="Layout Name"
                            value="${requestParameters.layout?if_exists}"
                            />
                    </div>
                    <div class="col-lg-4 col-md-6 col-sm-12">
                        <@button
                            id="findCls"
                            label="${uiLabelMap.Find}"
                            />
                    </div>
                </div>
      	</form>
      
      <div class="clearfix"></div>
      <div class="page-header border-b pt-2">
        <@headerH2 title="View Screen Configurations"/>
        <div class="clearfix"></div>
      </div>
      <div class="clearfix"></div>
      <div id="viewClsGrid" style="width: 100%;" class="ag-theme-balham"></div>
      <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/screenConfigs/viewClsGrid.js"></script>
               
      </div>
</div>


<script>     
$(document).ready(function() {
	$("#findCls").click(function(event) {
	    event.preventDefault(); 
	    loadClsGrid();
	});
});
</script>