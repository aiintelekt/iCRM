<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sr-portal/webapp/sr-portal/services/findCustomerModal.ftl"/>
<#include "component://sales-portal/webapp/sales-portal/activities-home/modalForActivity.ftl">
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>
<div class="row">
	<div id="main" role="main">
    	<#assign workEfforts = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("workEffortId").from("WorkEffort").where("workEffortTypeId","85004").maxRows(5).orderBy("-estimatedStartDate").queryList())?if_exists />
        <#assign workEffortIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(workEfforts, "workEffortId", true)>
        <#assign extraLeftActivity=''/>
        <#list workEffortIds as workEffortId>
        	<#assign extraLeftActivity= '${extraLeftActivity}' + '<a id=task1 title="${workEffortId}" href="#" class="btn btn-primary btn-xs" onclick="#"> ${workEffortId}</a>'/>
       	</#list>
		<div class="card-head margin-adj mt-0 d-none">
            ${screens.render("component://sr-portal/widget/services/ServicesScreens.xml#CustomerForAddServiceRequest")}
        </div>
        <div class="col-lg-12 col-md-12 col-sm-12">
           <@pageSectionHeader title="Activity Details"/>
        </div>

       	<form method="post" action="<#if (parameters.srNumber)?has_content>addSREmailEvent<#else>srAddEmailEvent</#if>" id="SrEmail" class="form-horizontal" name="phone" novalidate="novalidate" data-toggle="validator">
        	<input type="hidden" name="custRequestId" value="${(parameters.srNumber)!}"/>
                	<div class="col-md-12 col-md-6 col-sm-12 ">
                    	<@inputHidden  id="cNo" value=""/>
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
						    	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js'
						    });
						</script>
                    </div>
               	</div>

                <div class="row">
                	<div class="form-group 1">
                    	<div class="text-left ml-3">
                        	<@formButton
                            	btn1type="submit"
                                btn1label="${uiLabelMap.Save}"
                                btn1onclick="return formSubmission();"
                                btn2=true
                                btn2onclick = "resetFormToReload()"
                                btn2type="reset"
                                btn2label="${uiLabelMap.Clear}"
                          	/>
                    	</div>
                	</div>
           		</div>
    	</form>
	</div>
</div>


<@partyPicker 
	instanceId="partyPicker"
	/>
<script>

$(document).ready(function() {
		 var owner  = $("#owner").val();
		 if(owner != undefined && owner != null)
		 	getBusinessUnit(owner);
		 	
		 onLoadDefaultElementsBehaviour();
		 var userName  = $("#userName").val();	
		  $('#type').val($('#workEffortTypeId').val());
		$('#type').attr('readonly','readonly');
		$('#ownerBuDesc').attr('readonly','readonly');
		 	var loggedInUserId  = $("#loggedInUserId").val();
	    if(loggedInUserId != undefined && loggedInUserId != null)
			   	getBusinessUnit(loggedInUserId);
	    getUsers(loggedInUserId,userName);	
	});
	
	$(function() {
	
		$("#owner").change(function() {
			var owner  = $("#owner").val(); 
		   	if(owner != undefined && owner != null)
		   		getBusinessUnit(owner);	    
		});
		
		 $("#partyId_desc").on("change", function() {

		var nonSelectContent = "<span class='nonselect'>Select Contact</span>";
		var dataSourceOptions = '';
		var ntoOptions="";
		var partyId = $("#partyId_val").val();
		$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getPrimaryContacts",
	        data: {"partyId": partyId, "toEmailDD":"Y", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
	        async: false,
	        success: function (data) {   
	            if (data) {
	            if(data.responseMessage=="success"){
	            	for (var i = 0; i < data.partyRelContacts.length; i++) {
	            		var entry = data.partyRelContacts[i];
	            		if(entry.selected!=null){
	            		dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
	            		}else{
	            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
	            		}
	            	}
	            	for (var i = 0; i < data.toMapList.length; i++) {
	            		var entry = data.toMapList[i];
	            		if(entry!=null){
	            		if(entry.selected!=null){
	            			ntoOptions += '<option value="'+entry.EmailAddress+'" selected>'+entry.EmailAddress+'</option>';
	            		}else{
	            			ntoOptions += '<option value="'+entry.EmailAddress+'">'+entry.EmailAddress+'</option>';
	            		}
	            		}
	            	}
	            	}else{
	            	for (var i = 0; i < data.length; i++) {
	            		var entry = data[i];
	            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
	            	}
	            	
	            	}
	            }
	        }
	        
		});    
		
		$("#contactId").html( dataSourceOptions );
				
		$("#contactId").dropdown('refresh');
		$("#nto").html( ntoOptions );
				
		$("#nto").dropdown('refresh');
	});
		
	});
	
	
	function getBusinessUnit(owner) {
		var owner = owner;
	    	$.ajax({
		    	type: "POST",
		        url: "getBusinessUnitName",
		        async: false,
		        data: { "owner": owner },
		        success: function(data) {
		        	result=data;
		            if(result && result[0] != undefined && result[0].businessunitName != undefined)
		            	$("#ownerBuDesc").val(result[0].businessunitName);
		            else
		            	$("#ownerBuDesc").val("");
		        },error: function(data) {
		        	result=data;
					showAlert("error", "Error occured while fetching Business Unit");
				}
		    });
	}
	function resetFormToReload(){
    	window.location.href=window.location.href;
    }
	function onLoadDefaultElementsBehaviour(){
        
        var today = new Date();
		var dd = String(today.getDate()).padStart(2, '0');
		var mm = String(today.getMonth() + 1).padStart(2, '0');
		var yyyy = today.getFullYear();
		var hh= today.getHours();
		var m= today.getSeconds();
		today = mm + '/' + dd + '/' + yyyy +" "+hh+":"+m;
		$('#startTime').val(today.toLocaleString([],{hour12: false,dateStyle:"short",timeStyle:"short"}).replace(",",""));
    }
	function getUsers(loggedInUserId,userName) {
        var userOptionList = '<option value="'+loggedInUserId+'">'+userName+'</option>';
        $.ajax({
            type: "GET",
            url:'/common-portal/control/getUsersList',
            async: false,
            success: function(data) {
            	for (var i = 0; i < data.length; i++) {
                    var type = data[i];
                    userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
                }
            }
        });
       $("#owner").html(userOptionList);
	}
	function formSubmission(){
    	var valid = true;
    	if($('#partyId_val').val() == ""){
	 		showAlert('error','Please select Customer');
	 		valid = false;
	 	}else{
	 		$('#cNo').val($('#partyId_val').val());
	 	}
	 	return valid;
	 }
	 
</script>
    