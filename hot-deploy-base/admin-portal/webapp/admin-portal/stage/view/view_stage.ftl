<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <#assign extra='<a href="/admin-portal/control/findOppoStage" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
 			<@sectionFrameHeaderTab title="View Opportunity Stage" tabId="ViewOppoStage" extra=extra/> 

            <#include "component://admin-portal/webapp/admin-portal/stage/tab_menu.ftl"/>
            <div class="tab-content">
                <div id="details" class="tab-pane fade active show">
                    ${screens.render("component://admin-portal/widget/stage/OpportunityStageScreens.xml#ViewOppoStageDetail")}
                </div>
                <div id="administration" class="tab-pane fade">
					${screens.render("component://admin-portal/widget/stage/OpportunityStageScreens.xml#AdministrationInfo")}
				</div>
            </div>
        </div>
    </div>
</div>
