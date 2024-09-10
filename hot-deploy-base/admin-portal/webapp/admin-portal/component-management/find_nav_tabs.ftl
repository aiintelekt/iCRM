<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
    <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
    <@sectionFrameHeader title="${uiLabelMap.FindNavTab}"/>
    <div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
        <div class="panel panel-default">
            <div class="panel-heading" role="tab" id="headingTwo">
                <h4 class="panel-title">
                    <a role="button" data-toggle="collapse" data-parent="#accordionMenu"
                        href="#accordionDynaBase" aria-expanded="false"
                        aria-controls="collapseOne">${uiLabelMap.Filter}
                    </a>
                </h4>
            </div>
            <div id="accordionDynaBase" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
                <form method="post" id="searchForm" name="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
                    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                    <input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
                    <div class="panel-body">
                        <@dynaScreen 
                            instanceId="FIND_NAV_TAB"
                            modeOfAction="CREATE"
                            />
                        <div class="row find-srbottom">
                            <div class="col-lg-12 col-md-12 col-sm-12">
                                <div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
                                    <@button
                                        id="nav-tab-search-btn"
                                        label="${uiLabelMap.Find}"
                                        />
                                    <@reset
                                        label="${uiLabelMap.Reset}"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script>
$(document).ready(function() {
    $("#searchComponentId").change(function() {
        let componentId = $("#searchComponentId").val();
        if (componentId != null) {
            $.ajax({
                type: "POST",
                url: "getTabConfigIdList",
                async: true,
                data: {"componentId": componentId},
                success: function(data) {
                    if (data != null) {
                        let dropDownData = "";
                        $.each(data, function(index, item) {
                            dropDownData = dropDownData+"<option value='" + item.tabConfigId + "'>" + item.tabConfigId + "</option>";
                        });
                        $("#tabConfigId").dropdown('clear');
                        $('#tabConfigId').html("");
                        $("#tabId").dropdown('clear');
                        $("#tabId").html("");
                        $("#tabConfigId").append(dropDownData);
                    }else{
                        $("#tabConfigId").dropdown('clear');
                        $("#tabConfigId").html("");
                        $("#tabId").dropdown('clear');
                        $("#tabId").html("");
                    }
                }
            });
        }
    });
    $("#tabConfigId").change(function() {
        let componentId = $("#searchComponentId").val();
        let tabConfigId = $("#tabConfigId").val();
        if (componentId != null && tabConfigId != null) {
            $.ajax({
                type: "POST",
                url: "getTabIdList",
                async: true,
                data: {
                    "componentId": componentId,
                    "tabConfigId": tabConfigId
                },
                success: function(data) {
                    if (data != null) {
                        let tabDropDownData = "";
                        $.each(data, function(index, item) {
                            tabDropDownData = tabDropDownData + "<option value='" + item.tabId + "'>" + item.tabName + "</option>";
                        });
                        $("#tabId").dropdown('clear');
                        $("#tabId").html("");
                        $("#tabId").append(tabDropDownData);
                    } else {
                        $("#tabId").dropdown('clear');
                        $("#tabId").html("");
                    }
                }
            });
        }
    });
});
</script>