<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/screenConfigs/screenConfigs.js"></script>
<script language="JavaScript" type="text/javascript">
    function addRow(){
        addSpecifications();
    }
</script>

<div class="row">
   <div id="main" role="main">
    <#assign extra='
      <a href="viewScreenConfigs" class="btn btn-xs btn-primary text-right">
         Back
      </a>' />
    <#assign title = "${uiLabelMap.CreateScreenConfigs!}"/>
    <#if requestParameters.clsId?has_content>
        <#assign title = "${uiLabelMap.EditScreenConfigs!}"/>
    </#if>
      <@sectionFrameHeader title="${title!}" extra=extra/>
        <div class="border rounded bg-light pad-top">
        <#assign formAction = "createScreenConfigAction">
            <form action="${formAction}" method="post" id="createScreenConfig" name="createScreenConfig">
                <@inputHidden id="clsId" name="clsId" value="${requestParameters.clsId?if_exists}"/>
                <div class="row p-2">
                    <div class="col-lg-4 col-md-6 col-sm-12">
                        <#assign componentList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("componentName","uiLabels").from("OfbizComponentAccess").where("isHide","N").queryList())?if_exists />
                        <#assign components = (Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(componentList, "componentName","uiLabels"))?if_exists />

                        <@dropdownCell
                            id="module"
                            placeholder="Select Module"
                            allowEmpty=true
                            required=true
                            options=components!
                            value="${requestParameters.module?if_exists}"
                            />
                        <@inputCell
                            id="layout"
                            placeholder="Layout Name"
                            required=true
                            value="${requestParameters.layout?if_exists}"
                            />
                    </div>
                    <div class="col-lg-4 col-md-6 col-sm-12">
                        <@inputCell
                            id="screen"
                            placeholder="Screen Name"
                            required=true
                            value="${requestParameters.screen?if_exists}"
                            />
                        <@inputCell
                            id="screenService"
                            placeholder="Screen Service"
                            value="${requestParameters.screenService?if_exists}"
                            />
                    </div>
                    <div class="col-lg-4 col-md-6 col-sm-12">
                        <@inputCell
                            id="requestUri"
                            placeholder="Request URI"
                            readonly="true"
                            value="${requestParameters.requestUri?if_exists}"
                            />
                        <@button
                            id="addSpecifications"
                            label="${uiLabelMap.Add}"
                            onclick="javascript: return addRow();"
                            />
                    </div>
                </div> <#-- End row p-2-->
                <div class="row p-4">
                        <div id="myGrid" style="width: 100%;" class="ag-theme-balham"></div>
                </div> <#-- End row p-4-->
                <div class="row p-2">
                    <div class="col-lg-4 col-md-6 col-sm-12">
                        <@button
                            id="buttonCSC"
                            label="${uiLabelMap.Save}"
                            onclick="javascript:processRequest('createScreenConfig','/admin-portal/control/${formAction}');"
                            />
                    </div><#-- End Column -->
                </div> <#-- End row p-2-->
            </form>
        </div> <#-- End pad-top-->
   </div> <#-- End main-->
</div> <#-- End row-->
