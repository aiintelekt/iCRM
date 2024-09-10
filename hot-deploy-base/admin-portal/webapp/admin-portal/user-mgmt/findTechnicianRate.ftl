<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign requestURI = ""/>
<#if request.getRequestURI().contains("main")>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "main") />
</#if>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <@sectionFrameHeader title="Find Technician Rate" extra=helpUrl! />
            <div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
                <div class="panel panel-default">
                    <div class="panel-heading" role="tab" id="headingTwo">
                        <h4 class="panel-title">
                            <a role="button" data-toggle="collapse" data-parent="#accordionMenu"
                                href="#accordionDynaBase" aria-expanded="true"
                                aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
                        </h4>
                    </div>
                    <div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
                        <form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
                            <@inputHidden 
					       	  	id="defaultRate"
					       	  	value="N"
					       	  	/>
                            <div class="panel-body">
                                <@dynaScreen 
                                instanceId="FIND_TECHNICIAN_RATE"
                                modeOfAction="CREATE"
                                />
                                <div class="text-right pd-cbx-lbls pad-10" style="padding-top: 0px;">
                                    <@button
                                    id="main-search-btn"
                                    label="${uiLabelMap.Find}"
                                    />	
                                    <@reset
                                    label="${uiLabelMap.Reset}"/>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
   
$(document).ready(function(){
    getTechnicianUsers();	
    $("#partyId").change(function() {
		var partyIdVal  = $(this).val();
		if(partyIdVal && partyIdVal === "company"){
			$("#defaultRate").val("Y");
		}
	});
});

function getTechnicianUsers() {
    var userOptionList = '<option value=""></option>';
    $.ajax({
        type: "GET",
        url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function(data) {
        	userOptionList += '<option value="company">Standard Rates</option>';
        	for (var i = 0; i < data.length; i++) {
                var type = data[i];
                userOptionList += '<option value="'+type.partyId+'">'+type.userName+' ('+ type.roleDesc +') </option>';
            }
        }
    });
   $("#partyId").html(userOptionList);
}
</script>
