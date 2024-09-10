<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign partyId= request.getParameter("partyId")! />
<#assign isShowHelpUrl="Y">
<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y" >
<#assign isShowHelpUrl="N">
</#if>
<#if partyStatusId?if_exists != "PARTY_DISABLED">
 	<@sectionFrameHeaderTab title="${uiLabelMap.Segmentation!}" tabId="Segmentation" isShowHelpUrl=isShowHelpUrl!/> 
	${screens.render("component://common-portal/widget/custom-field/CustomFieldScreens.xml#AddSegmentation")}
</#if>
${screens.render("component://common-portal/widget/custom-field/CustomFieldScreens.xml#ListSegmentation")}

<script>

function loadSegmentCodeList(groupingCode, groupId) {
	var nonSelectContent = "<span class='nonselect'>Select ${uiLabelMap.segmentCode!}</span>";
	var groupNameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select ${uiLabelMap.segmentCode!}</option>';		
			
	$.ajax({
		type: "POST",
     	url: "/common-portal/control/getCustomFieldGroups",
        data:  {"groupingCode": $("#"+groupingCode).val(), "roleTypeId": "${roleTypeId!}", "groupType": "SEGMENTATION", "isActive": "Y"},
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

function loadSegmentValueList(groupId, fieldId) {
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