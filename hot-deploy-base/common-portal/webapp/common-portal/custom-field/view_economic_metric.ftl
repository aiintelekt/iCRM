<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign partyId= request.getParameter("partyId")! />
<#assign isShowHelpUrl="Y">
<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y" >
<#assign isShowHelpUrl="N">
</#if>
 <div class="p-1"></div>
 <@sectionFrameHeaderTab title="Economic Metrics" tabId="EconomicMetric" isShowHelpUrl=isShowHelpUrl!/> 

<#if partyStatusId?if_exists != "PARTY_DISABLED">
${screens.render("component://common-portal/widget/custom-field/CustomFieldScreens.xml#AddEconomicMetric")}
</#if>
${screens.render("component://common-portal/widget/custom-field/CustomFieldScreens.xml#ListEconomicMetric")}

<script>

function loadEconomicCodeList(groupingCode, groupId) {
	var nonSelectContent = "<span class='nonselect'>Select ${uiLabelMap.segmentCode!}</span>";
	var groupNameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select ${uiLabelMap.segmentCode!}</option>';		
			
	$.ajax({
		type: "POST",
     	url: "/common-portal/control/getCustomFieldGroups",
        data:  {"groupingCode": $("#"+groupingCode).val(), "roleTypeId": "${roleTypeId!}", "groupType": "ECONOMIC_METRIC", "isActive": "Y"},
        async: false,
        success: function (data) {   
            if (data.code == 200) {
            	for (var i = 0; i < data.groups.length; i++) {
            		var group = data.groups[i];
            		groupNameOptions += '<option value="'+group.groupId+'">'+group.groupName+'</option>';
            	}
            }
        }
        
	});    
	
	$("#"+groupId).html( groupNameOptions );
			
	$('#'+groupId).dropdown('refresh');
		
}

function loadEconomicMetricList(groupId, fieldId) {
	var nonSelectContent = "<span class='nonselect'>Select ${uiLabelMap.segmentCode!}</span>";
	var fieldNameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select ${uiLabelMap.segmentCode!}</option>';		
			
	$.ajax({
		type: "POST",
     	url: "/common-portal/control/getCustomFields",
        data:  {"groupId": $("#"+groupId).val(), "isEnabled": "Y"},
        async: false,
        success: function (data) {   
            if (data.code == 200) {
            	for (var i = 0; i < data.fields.length; i++) {
            		var field = data.fields[i];
            		fieldNameOptions += '<option value="'+field.customFieldId+'">'+field.customFieldName+'</option>';
            	}
            }
        }
        
	});    
	
	$("#"+fieldId).html( fieldNameOptions );
			
	$('#'+fieldId).dropdown('refresh');
		
}

</script>