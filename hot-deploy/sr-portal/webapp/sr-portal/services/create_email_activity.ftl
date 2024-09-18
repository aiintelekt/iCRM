<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sales-portal/webapp/sales-portal/activities-home/modalForActivity.ftl">
<#include "component://sr-portal/webapp/sr-portal/services/findCustomerModal.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/validation-activity.js"></script>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/activity-utils.js"></script>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/common/common-utils.js"></script>
<style>
.create-email .header-title{
	padding-bottom: 0px;
}
.create-email .dash-panel{
	padding: 0px 0px 0px 0px !important;
}
</style>
<div class="row">
        
	<div id="main" role="main">
			
	            
       <#-- <div class="col-lg-12 col-md-12 col-sm-12">
        	 <@pageSectionHeader title="Activity Details"/>
        </div>-->
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel create-email">
    	<form method="post" action="createEmailActivityAction" id="createEmailActivity" name="createEmailActivity" class="form-horizontal" name="phone" novalidate="novalidate" data-toggle="validator" enctype="multipart/form-data">
        	
        	<@inputHidden name="partyId" id="partyId" value = "${partyId!}"/>
        	<#assign custRequestId = '${srNumber!}' >
            <@inputHidden name="custRequestId" id="custRequestId" value = "${custRequestId!}"/>
            <@inputHidden name="ccEmailIds" id="ccEmailIds" value = ""/>
			<#assign primaryEmail=""/>
            <#assign PrimaryContact=Static["org.groupfio.common.portal.util.PartyPrimaryContactMechWorker"].getPartyPrimaryContactMechValueMaps(delegator, partyId, Static["org.ofbiz.base.util.UtilMisc"].toMap("isRetriveEmail", true),true)!>
        	<#if PrimaryContact?has_content> 
				<#assign primaryEmail=PrimaryContact.get("EmailAddress")!/>
				<@inputHidden name="primaryEmail" id="primaryEmail" value="${primaryEmail!}" />
			</#if>
        	<input type="hidden" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
        	<input type="hidden" name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
        	<div>
        	<div>
                <@sectionFrameHeader   title="${uiLabelMap.createMailActivity!}" />
	            <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
	            <@inputHidden name="ownerBu" id="ownerBu" />
	            <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "E-mail", "active", "Y").queryFirst()! />
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
	            <#assign userLoginEmail=""/>		                     				
				<#assign userloginContact=Static["org.groupfio.common.portal.util.PartyPrimaryContactMechWorker"].getPartyPrimaryContactMechValueMaps(delegator, userLogin.partyId, Static["org.ofbiz.base.util.UtilMisc"].toMap("isRetriveEmail", true),true)!>
	    		<#if userloginContact?has_content> 
					<#assign userLoginEmail=userloginContact.get("EmailAddress")!/>
					<@inputHidden name="loginEmail" id="loginEmail" value="${userLoginEmail!}" />
				</#if>
				                 
                 <@dynaScreen 
	                instanceId="CREATE_EMAIL_ACTIVITY"
	                modeOfAction="CREATE"
	             />
        		</div>
				
			<div class="row p-2">
            	<div class="col-md-12 col-lg-12 col-sm-12" id="emlContent">
                    
                    <@textareaLarge
		               id="emailContent"
		               groupId = "htmlDisplay"
		               label=uiLabelMap.Html
		               rows="3"
		               value = template
		               required = false
		               txareaClass = "ckeditor"
		               />
					<script>
				        CKEDITOR.replace( 'emailContent',{
				        	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
							autoGrow_minHeight : CKEditorUtil.autoGrowMinHeight,
							removePlugins : CKEditorUtil.removePlugins
				        });
				    </script>
             	</div>
          	</div>
			
			<div class="row padding-r">
            	<div class="col-md-6 col-sm-6">
            	<@inputRowFilePicker 
				id="attachment"
				label="Attachments"
				placeholder="Select Attachment"
				/>
            	</div>
            </div>
                    
            <div class="row">
                <div class="offset-md-2 col-sm-10 pb-3">
                    <div class="text-left ml-1">
                      <@formButton
                         btn1type="submit"
                         btn1id=""
                         btn1label="Send"
                         btn2=true
                         btn2onclick = "resetFormToReload()"
                         btn2type="reset"
                         btn2label="${uiLabelMap.Clear}"
                       />
                   </div>
                </div>
              </div>
           </div>
    	</form>
	</div>
</div>
</div> 
 
<@templatePicker 
	instanceId="templatePicker"
/>	

<@partyPicker 
	instanceId="partyPicker"
/>

<@inputHidden id="externalLoginKey" value = "${requestAttributes.externalLoginKey!}"/>	
			
<script>
$(document).ready(function() {

	$("#createEmailActivity").validator().on('submit', function (event) {  
	    if (event.isDefaultPrevented()) {
			var $form = $(this);
			$form.data('submitted', false);
			return false;
        } else{
        	var submitForm = true;
		    var htmlContent = CKEDITOR.instances["emailContent"].getData();
	    	$('#emailContent').val(htmlContent);
	    
		    var submitForm = true;
		    
		    var cc = "";
		    var ccEmailArray = $("#ncc").val();
		    var type = $("#ncc").attr("type");
		    if (type && type=="text"){
		    	if (ccEmailArray){
		        	ccEmailArray=ccEmailArray.split(",");
		        }
		    }
		    for (var i = 0; i < ccEmailArray.length; i++) {
		        var email = ccEmailArray[i];
		        if (cc == "") {
		            cc = email;
		        } else {
		            cc = cc + "," + email;
		        }
		    }
		    $("#ccEmailIds").val(cc);
		    
		    if(submitForm){
		    	var $form = $(this);
			    if ($form.data('submitted') === true) {
			      event.preventDefault();
			    } else {
			      $form.data('submitted', true);
			    }
		    } 	
        }
	    
	});
	
    var party = $("#partyId").val();
    
    var userName = $("#userName").val();
    $("#owner").change(function() {
        var owner = $("#owner").val();
        if (owner != undefined && owner != null)
            getBusinessUnit(owner);
    });
    
    var defaultFrom = $('#loginEmail').val();
    if (defaultFrom != null && defaultFrom != "" & defaultFrom != undefined) {
        var defaultLoggedInUserEmail = '<option value="' + defaultFrom + '" selected>' + defaultFrom + '</option>';
        $("#nsender").html(defaultLoggedInUserEmail);
        $("#nsender").dropdown('refresh');
    }
    
    var loggedInUserId = $("#loggedInUserId").val();
    if (loggedInUserId != undefined && loggedInUserId != null)
        getBusinessUnit(loggedInUserId);
    getUsers(loggedInUserId, userName);
   	
    var cNo = $("#cNo").val();
    if (cNo == null || cNo == undefined || cNo == "") {
        $("#cNo").val($("#partyId").val());
        cNo = $("#partyId").val();
    }
    
    if (cNo != null && cNo != undefined && cNo != "") {
        //loadContacts();
        //loadCcContacts();
    }
    
    $("#contactId").change(function() {
        var owner = $("#owner").val();
        if (owner != undefined && owner != null) {
            getBusinessUnit(owner);
      	}      
      	
      	console.log('direction: '+$("input[name='direction']:checked").val());
      	onChangeEmailDirection($("input[name='direction']:checked").val(), $(this).val(), "${requestAttributes.externalLoginKey!}");
    });
    
    ACTUTIL.loadSrAssocParties('${(parameters.domainEntityId)!}', 'contactId', null, "${requestAttributes.externalLoginKey!}");
    ACTUTIL.loadSrAssocPartyEmails('${(parameters.domainEntityId)!}', 'ncc', null, "${requestAttributes.externalLoginKey!}");
    
});

$(function() {
    var partyId = $("#partyId").val();
    var salesOpportunityId = $("#salesOpportunityId").val();

    $("#owner").change(function() {
        var owner = $("#owner").val();
        if (owner != undefined && owner != null)
            getBusinessUnit(owner);
    });
    $("#findcustomerSr").click(function() {
        loadAgGrid();
    });

});

function resetFormToReload() {
    window.location.href = window.location.href;
}

function loadContacts() {
    var dataSourceOptions = "";
    var ntoOptions = "";
    var partyId = $("#partyId").val();
    $.ajax({
        type: "POST",
        url: "/common-portal/control/getPrimaryContacts",
        data: {
            "partyId": partyId,
            "toEmailDD": "Y",
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function(data) {
            if (data) {
                if (data.responseMessage == "success") {
                    for (var i = 0; i < data.partyRelContacts.length; i++) {
                        var entry = data.partyRelContacts[i];
                        if (entry.selected != null) {
                            dataSourceOptions += '<option value="' + entry.contactId + '" selected>' + entry.name + '</option>';
                        } else {
                            dataSourceOptions += '<option value="' + entry.contactId + '">' + entry.name + '</option>';
                        }
                    }
                    for (var i = 0; i < data.toMapList.length; i++) {
                        var entry = data.toMapList[i];
                        if (entry != null) {
                            if (entry.selected != null) {
                                ntoOptions += '<option value="' + entry.EmailAddress + '" selected>' + entry.EmailAddress + '</option>';
                                $('#contactId').val(entry.primaryContactId);
                            } else {
                                ntoOptions += '<option value="' + entry.EmailAddress + '">' + entry.EmailAddress + '</option>';
                                if (i == 0) {
                                    $('#contactId').val(entry.primaryContactId);
                                }
                            }
                        }
                    }
                } else {
                    for (var i = 0; i < data.length; i++) {
                        var entry = data[i];
                        dataSourceOptions += '<option value="' + entry.contactId + '">' + entry.name + '</option>';
                    }

                }
            }
        }

    });

    $("#nto").html(ntoOptions);
    $("#nto").dropdown('refresh');
}

function getBusinessUnit(owner) {
    var owner = owner;
    $.ajax({
        type: "POST",
        url: "getBusinessUnitName",
        async: false,
        data: {
            "owner": owner
        },
        success: function(data) {
            result = data;
            if (result && result[0] != undefined && result[0].businessId != undefined) {
                $("#ownerBu").val(result[0].businessId);
                $("#ownerBuDesc").val(result[0].businessunitName);
            } else {
                $("#ownerBu").val("");
                $("#ownerBuDesc").val("");
            }
        },
        error: function(data) {
            result = data;
            showAlert("error", "Error occured while fetching Business Unit");
        }
    });
}

function formSubmission() {
    var valid = true;
    /*
    if ($('#cNo').val() == "") {
        showAlert('error', 'Please Select Customer');
        valid = false;
    }
	*/
    var htmlContent = CKEDITOR.instances["emailContent"].getData();
    $('#emailContent').val(htmlContent);

    return valid;
}

function getUsers(loggedInUserId, userName) {
    var userOptionList = '<option value="' + loggedInUserId + '">' + userName + '</option>';
    $.ajax({
        type: "GET",
        url: '/common-portal/control/getUsersList',
        async: false,
        success: function(data) {
            for (var i = 0; i < data.length; i++) {
                var type = data[i];
                userOptionList += '<option value="' + type.userLoginId + '">' + type.userName + '</option>';
            }
        }
    });
    $("#owner").html(userOptionList);
}

</script>