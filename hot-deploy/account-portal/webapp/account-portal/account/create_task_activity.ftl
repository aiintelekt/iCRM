<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/validation-activity.js"></script>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/activity-utils.js"></script>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
        <form id="mainFrom" method="post" action="<@ofbizUrl>createTaskActivityAction</@ofbizUrl>" data-toggle="validator" onsubmit="return submitActivityForm();"> 
		<div class="col-lg-12 col-md-12 col-sm-12">
    		<input type="hidden" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
    		<input type="hidden" name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
        	
        	<@sectionFrameHeader   title="${uiLabelMap.createTaskActivity!}" />
        	<#assign cifNo = '${requestParameters.partyId!}' >
            <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
            <@inputHidden name="ownerBu" id="ownerBu" />
            <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Task", "active", "Y").queryFirst()! />
            <@inputHidden id="srTypeId" value="${(srType.code)!}"/>
            <@inputHidden id="workEffortTypeId" value="${(srType.value)!}"/>
            <@inputHidden id="loggedInUserId" value="${userLogin.userLoginId?if_exists}" />
            <#assign userName = userLogin.userLoginId>
            <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
            <#assign person = delegator.findOne("Person", findMap, true)!>
            <#if person?has_content>
            	<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
            	<@inputHidden id="userName" value="${userName!}"/>
            </#if>
            <@inputHidden name="isSchedulingRequired" value = "N"/>
			            
            <@dynaScreen 
                instanceId="CREATE_TASK_ACTIVITY_ACCT"
                modeOfAction="CREATE"
             />
            
             <div class="col-md-12 col-lg-12 col-sm-12 activity-desc">
     			<@textareaLarge  label="Description" id="messages" rows="4"/>
  			</div>
  			
           	<div class="offset-md-2 col-sm-10 p-2">
           		<@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn2=true
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                />
            </div>
    	</form>
    	</div>
    	
    </div>
</div>

<@partyPicker 
	instanceId="partyPicker"
	/>
	
<script>
$(document).ready(function() {

    onLoadDefaultElementsBehaviour();
    
    $("#owner").change(function() {
        var owner = $("#owner").val();
        if (owner != undefined && owner != null) {
            ACTUTIL.loadBusinessUnit(owner, 'ownerBu', 'ownerBuDesc', null, "${requestAttributes.externalLoginKey!}");
      	}      
    });
	    
    $('#type').val($('#workEffortTypeId').val());
    $('#type').attr('readonly', 'readonly');
    $('#ownerBuDesc').attr('readonly', 'readonly');

    var typeId = $("#srTypeId").val();
    if (typeId != "") {
        loadSubTypes(typeId);
    }

    var userName = $("#userName").val();
    var loggedInUserId = $("#loggedInUserId").val();
    if (loggedInUserId != undefined && loggedInUserId != null) {
        ACTUTIL.loadBusinessUnit(loggedInUserId, 'ownerBu', 'ownerBuDesc', null, "${requestAttributes.externalLoginKey!}");
   	}     
    
    ACTUTIL.loadOwners(null, null, null, "${requestAttributes.externalLoginKey!}");
    
    ACTUTIL.loadContacts('${requestParameters.partyId!}', null, 'contactId', null, "${requestAttributes.externalLoginKey!}");
	    
});


function onLoadDefaultElementsBehaviour() {

    var today = new Date();
    var dd = String(today.getDate()).padStart(2, '0');
    var mm = String(today.getMonth() + 1).padStart(2, '0');
    var yyyy = today.getFullYear();

    today = dd + '/' + mm + '/' + yyyy;
    $('#taskDate').val(today);
}

function loadSubTypes(typeId) {
    var nonSelectContent = "<span class='nonselect'>Please Select</span>";
    var subTypes = '<option value="" data-content="' + nonSelectContent + '" selected="">Please Select</option>';
    $.ajax({
        type: "POST",
        url: "getIASubTypes",
        data: {
            "iaTypeId": typeId
        },
        async: false,
        success: function(data) {
            var sourceDesc = data.results;
            for (var i = 0; i < data.length; i++) {
                var type = data[i];
                subTypes += '<option value="' + type.subTypeId + '">' + type.subTypeDesc + '</option>';
            }
        }
    });
    $("#srSubTypeId").html(DOMPurify.sanitize(subTypes));
}

</script>