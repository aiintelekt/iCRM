<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <#assign extra='<a href="updateAgGridAccessConfig?instanceId=${agGridAccessConfig?if_exists.instanceId!}&groupId=${agGridAccessConfig?if_exists.groupId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="findAgGridAccessConfig" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
       
        <@sectionFrameHeaderTab title="${uiLabelMap.ViewAgGridAccessConfig}" tabId="ViewAgGridAccessConfig" extra=extra?if_exists/> 

        <div class="col-lg-12 col-md-12 col-sm-12">
            <form method="post" action="updateEntityOperations" id="entityConfigureForm" name="entityConfigureForm" data-toggle="validator" >
                <div class="row">
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <@displayCell
                            label="${uiLabelMap.InstanceId!}"
                            value="${agGridAccessConfig?if_exists.instanceId!uiLabelMap.defaultValue}"
                            />
                        <@displayCell
                            label="${uiLabelMap.GroupId!}"
                            value="${agGridAccessConfig?if_exists.groupId!uiLabelMap.defaultValue}"
                            />  
                        
                        <#assign agGridOptionsEnum = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "AG_GRID_OPTIONS")?if_exists />
                        <#assign agGridOptionsList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(agGridOptionsEnum, "enumId","description")?if_exists />  
                      
                        <@inputCheckBox
                            id="options"
                            label="${uiLabelMap.Options}"
                            optionList=agGridOptionsList!
							optionValues=agGridAccessConfig?if_exists.options!
                            disabled=true
                            />
                    </div>
                </div>
            </form>
            <div class="clearfix"></div>
        </div>
    </div>
    <#-- main end -->
</div>
<#-- row end -->