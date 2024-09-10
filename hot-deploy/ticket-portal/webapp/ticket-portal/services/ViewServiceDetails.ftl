<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>

 <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "profileDetails") />  
<#if srNumber?has_content>
<#assign srNumberUrlParam = srNumber!>
<#else>
<#assign srNumberUrlParam = requestParameters.srNumber!>
</#if>
<#if srNumberUrlParam?exists && srNumberUrlParam?has_content>
	<#assign custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", srNumberUrlParam).queryOne()! />
	<#if custRequest?exists && custRequest?has_content>
		<#assign srCustReqStatusId = custRequest.statusId!>
	</#if>
</#if>

<#assign srReopen = "N">
<#assign pretailParam = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "SR_REOPEN_PARAMETER").queryOne()! />
<#if pretailParam?exists && pretailParam?has_content>
	<#if pretailParam.value?has_content> 
		<#assign userLoginSecurityGroupDetails = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("UserLoginSecurityGroupPermission", {"userLoginId" : userLogin.userLoginId,"permissionId":pretailParam.value}, [], false))?if_exists/>
			<#if userLoginSecurityGroupDetails?exists && userLoginSecurityGroupDetails?has_content> 
			<#assign srReopen = "Y">
		</#if>
	</#if>
</#if>
<#assign requiredResolution = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "SR_REQUIRED_RESOLUTION","Y")?if_exists>
<input type="hidden" name="srNumberUrlParam" id="srNumberUrlParam" value="${srNumberUrlParam!}" />
<input type="hidden" name="currentSrStatusId" id="currentSrStatusId" value="${currentSrStatusId!}" />
<input type="hidden" name="fromPartyId" id="fromPartyId" value="${fromPartyId!}" />
<input type="hidden" name="requiredResolution" id="requiredResolution" value="${requiredResolution!}" />
<form method="post" id="srCopyForm" action="<@ofbizUrl>createServiceRequest?copy=Y</@ofbizUrl>" class="form-horizontal" novalidate="novalidate" data-toggle="validator" >
	<input type="hidden" name="srNumber" id="srNumber" value="${srNumberUrlParam!}">
</form>

<form method="post" id="srReopenForm" action="<@ofbizUrl>updateServiceRequestAction</@ofbizUrl>" class="form-horizontal" novalidate="novalidate" data-toggle="validator" >
	<input type="hidden" name="srNumber" id="srNumber" value="${srNumberUrlParam!}">
	<input type="hidden" name="srStatusId" id="srStatusId" value="SR_OPEN">
	<input type="hidden" name="reopenFlag" id="reopenFlag" value="Y">
</form>

<div class="pt-2">
	<h2 class="d-inline-block">SR Details</h2>
	<ul class="flot-icone">
		<li class="mt-0" style="margin-right: -13px;">
			<div class="form-group row" id="dropDowm_row" style="width: 260px;">
				<div class="col-sm-11">
					<#assign srStatusMap = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(srStatusList, "statusId", "description", false)?if_exists />
					<#if srCustReqStatusId?has_content && (srCustReqStatusId == "SR_CLOSED" || srCustReqStatusId == "SR_CANCELLED")>
					<span>SR Status: ${srStatusMap.get(srCustReqStatusId!)} </span>
					<#else>
					<select id="serviceStatusId" name="statusId" class="ui dropdown search form-control input-sm">
						<option value="">Select Status</option>
						<#list srStatusList as srStatus>
						<#if srCustReqStatusId?if_exists == srStatus.statusId?if_exists>
						<option value="${srStatus.statusId?if_exists}" selected="selected">${srStatus.description?if_exists}</option>
						<#else>
						<option value="${srStatus.statusId?if_exists}">${srStatus.description?if_exists}</option>
						</#if>
						</#list>
					</select>
					<div class="help-block with-errors" id="sections_error"></div>
					</#if>
				</div>
				<div class="col-sm-1"></div>
			</div>
		</li>
		<#if (custRequest?if_exists.statusId?if_exists == "SR_CLOSED" || custRequest?if_exists.statusId?if_exists == "SR_CANCELLED") && srReopen?if_exists == "Y">
		<li class="mt-0">
			<span id="reopen-btn" class="btn btn-xs btn-primary"><i class="fa fa-check" aria-hidden="true"></i> Reopen </span>
		</li>
		</#if>
		<#if custRequest?if_exists.statusId?if_exists == "SR_CLOSED">
		<li class="mt-0">
			<span id="copy-btn" class="btn btn-xs btn-primary"><i class="fa fa-check" aria-hidden="true"></i> Copy </span>
		</li>
		</#if>
		<#if custRequest?if_exists.statusId?if_exists != "SR_CLOSED">
		<#if custRequest?if_exists.statusId?if_exists != "SR_CANCELLED">
		<li class="mt-0">
			<span id="save-btn" class="btn btn-xs btn-primary"><i class="fa fa-save" aria-hidden="true"></i> Save </span>
		</li>
		<li class="mt-0">
			<span id="resolve-btn" data-toggle="confirmation" title="Are you sure do you want Resolve ?" class="btn btn-xs btn-primary"><i class="fa fa-check" aria-hidden="true"></i>Resolve </span>
		</li>
		<li class="mt-0">
			<span data-toggle="modal" data-target="#partyResponsible" title="Reassign" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Reassign</span>
		</li>
		<li class="mt-0">
			<a href="<@ofbizUrl>updateServiceRequest?srNumber=</@ofbizUrl>${context.custRequestId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
		</li>
		</#if>
		</#if>
		<li>${helpUrl}</li>
	</ul>
</div>
<div></div>
<div class="col-md-12 col-lg-12 col-sm-12">
	
	<@inputHidden  id="selectedOwnerId" value="${ownerUserLoginId?if_exists}" />
	<@inputHidden id="loggedInUserId" value="${userLogin.partyId?if_exists}" />
    <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
    <#assign person = delegator.findOne("Person", findMap, true)!>
    <#if person?has_content>
    	<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
    	<@inputHidden id="userName" value="${userName!}"/>
    </#if>
    
	<@dynaScreen 
		instanceId="SR_BASE_GEN"
		modeOfAction="VIEW"
	/>
	<@responsiblePicker 
	instanceId="partyResponsible"
	/>	
</div>

<div class="col-md-12 col-lg-12 col-sm-12">
	&nbsp;TAT  &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<input type='text' size='2' value='${tatDays!}' readOnly/> Days <input type='text' size='2' value='${tatHrs!}' readOnly/> Hrs <input type='text' size='2' value='${tatMins!}' readOnly/> Mins
</div>

<div class="col-md-12 col-lg-12 col-sm-12 ">
	<#--
	<@inputArea
		inputColSize="col-sm-12"
     	id="description"
     	label=uiLabelMap.Description
     	rows="10"
     	placeholder = uiLabelMap.Description
     	value="${description!}"
     	readonly=false
    /> 
    --> 
    <@textareaLarge
    	id="description"
	   	label=uiLabelMap.Description
	   	rows="5"
	   	required = false
	   	txareaClass = "ckeditor"
	   	value=description!
	   	/>
	<script>          
	    CKEDITOR.replace( 'description',{
	    	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
	    	on: {
		        change: function( evt ) {
		            console.log( evt ); 
		            CKEDITOR.dom.element.createFromHtml( '<input type="hidden" value="Y" id="descriptionChanged" />' ).appendTo( CKEDITOR.document.getBody());
		        }
		    }
        });
	</script>
</div>

<div class="col-md-12 col-lg-12 col-sm-12 ">
	<#--
	<@inputArea
    	inputColSize="col-sm-12"
     	id="resolution"
     	label=uiLabelMap.Resolution
     	rows="10"
     	placeholder = uiLabelMap.Resolution 
     	value="${resolution!}"
     	readonly=false  
	 />
	 -->
	 <@textareaLarge
       id="resolution"
       label=uiLabelMap.Resolution
       rows="5"
       required = false
       txareaClass = "ckeditor"
       value=resolution!
       />
    <script>
        CKEDITOR.replace( 'resolution',{
        	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
		    on: {
		        change: function( evt ) {
		            console.log( evt ); 
		            CKEDITOR.dom.element.createFromHtml( '<input type="hidden" value="Y" id="resolutionChanged" />' ).appendTo( CKEDITOR.document.getBody() );
		        }
		    }
        });             
        // resize the editor after it has been fully initialized
        //CKEDITOR.on('instanceLoaded', function(e) {e.editor.resize("100%", 400)} );
    </script>
</div>
<div id="submitModal" class="modal fade">
		    <div class="modal-dialog">
		      <div class="modal-content">
            	<div class="modal-header">
            	<span id="message"></span>
	               <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            	</div>
		            <div>
		            </div>
            		<div class="modal-footer">
		              <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="OK" onclick="return false;">
			        </div>
			      </div>
			   </div>
			</div> 

            <div class="clearfix"></div>

 <div id="statusModal" class="modal fade" role="dialog">
	<div class="modal-dialog" style="width:60%;">
    	<div class="modal-content">
            <div class="modal-body">
            		<h4 class="modal-title">Are you sure you want to Close/Cancel the SR?</h4>
             		<div class="modal-footer">
                 		<@submit class="btn btn-sm btn-primary navbar-dark" id="saveModal" label="${uiLabelMap.Yes}" />
                 		<@submit class="btn btn-sm btn-primary navbar-dark" id="closeModal" label="${uiLabelMap.No}" />
                 	</div>
        	</div>
    	</div>
	</div>
</div>

<script>
$(document).ready(function () {
	
    //var textAreaFlag = "N";
    var partyId = $("#fromPartyId").val();
    if (partyId && partyId != "") {
        getPartyRoleTypeId(partyId);
    }

    var currentSrStatusId = $('#currentSrStatusId').val();
    if (currentSrStatusId && currentSrStatusId != "") {
        if ("SR_CLOSED" == currentSrStatusId) {
            $('#description').attr('disabled', 'disabled');
            $('#resolution').attr('disabled', 'disabled');
        }
        if ("SR_CANCELLED" == currentSrStatusId) {
            $('#description').attr('disabled', 'disabled');
            $('#resolution').attr('disabled', 'disabled');
        }
    }
	
    $('#resolve-btn').on('click', function () {
        var srNumber = $('#srNumberUrlParam').val();
        //var resolution = document.getElementById('resolution').value.trim();
        var resolution = CKEDITOR.instances["resolution"].getData();
        var description = $('#description').val();
        var resolutionInstance = CKEDITOR.instances.resolution;
        var isRequiredResolution = $("#requiredResolution").val();
       
		if (!isRequiredResolution || (isRequiredResolution && isRequiredResolution=="Y")) {
	        if (isEmptyCKEd(resolutionInstance)) {
				/*
	            var message = "Resolution field is mandatory to resolve the SR !"
	            $('#submitModal').modal('show');
	            $("#message").html(message); */
	            showAlert("error", "Resolution field is mandatory to resolve the SR!");
	            return false;
	        }
        }
        var dataSets = {
            "statusId": "SR_CLOSED",
            "externalId": srNumber,
            "resolution": resolution,
            "description": description,
            "fromPartyId":"${fromPartyId!}",
            "cNo":"${inputContext.cNo?if_exists}",
            "srName":"${srName!}"
        };
        $.ajax({
            type: "POST",
            url: "updateSRResolveStatus",
            data: dataSets,
            async: false,
            success: function (data) {
                if (data[0].resolutionFlag == "resolution") {
                    showAlert("error", "Resolution field is mandatory to resolve the SR");
                }
                if (data[0].resolveActivityflag == "closeActivity") {
                    setTimeout(showAlert("error", "There are Open Activities tagged to this " + srNumber + ". Please close the Open Activities before closing/cancelling the SR"), 10000);
                }
                if (data[0].resolutionFlag != "resolution" && data[0].resolveActivityflag != "closeActivity") {
                    setTimeout(location.reload.bind(location), 500);
                    showAlert("success", "Resolved Successfully");
                }
            }
        });
    });

    $('#save-btn').on('click', function () {
        var srNumber = $('#srNumberUrlParam').val();
        var isDescriptionChanged = $("#descriptionChanged").val();
        var isResolutionChanged = $("#resolutionChanged").val();
        var description = CKEDITOR.instances["description"].getData();
        var resolution = CKEDITOR.instances["resolution"].getData();
        if (isDescriptionChanged === "Y"  ||  isResolutionChanged === "Y") {

            var dataSets = {
                "srNumber": srNumber,
                "description": description,
                "resolution": resolution
            };

            $.ajax({
                type: "POST",
                url: "updateSr",
                data: dataSets,
                async: false,
                success: function (data) {
                    showAlert("success", "SR Updated Successfully" + ": " + srNumber);
                },
                error: function (data) {
                    showAlert("error", "Error occured while updating the SR!");
                }
            });
            $("#descriptionChanged").val("");
        	$("#resolutionChanged").val("");
        }
    });

    $('#serviceStatusId').on('change', function () {
		console.log('statusId changed: '+$(this).val());
        var srNumber = $('#srNumberUrlParam').val();
        var statusId = $("#serviceStatusId").val();
        //var resolution = $('#resolution').val();
        //var description = $('#description').val();
        
        var resolution = (CKEDITOR.instances["resolution"].getData()).trim();
        var description = (CKEDITOR.instances["description"].getData()).trim();
        var srflag = true;


        if (statusId == "SR_CANCELLED") {
            srflag = false;
        } else if (statusId == "SR_CLOSED") {
            srflag = false;
        }

        if (statusId && srflag) {
            $.ajax({
                type: "POST",
                url: "updateSrStatus",
                async: false,
                data: {
                    "srNumber": srNumber,
                    "srStatusId": statusId,
                    "description": description,
                    "resolution": resolution
                },
                success: function (data) {
                    var mesg = data[0];
                    if (mesg == "error") {

                        setTimeout(location.reload.bind(location), 500);
                        $('div[data-notify=container]').css('z-index', '100000');

                        setTimeout(function () {
                            showAlert("error", "Please close the Open Activities before closing/cancelling the " + srNumber);
                        }, 3000);


                    } else {
                        setTimeout(location.reload.bind(location), 500);

                        showAlert("success", "SR Updated Successfully" + ": " + srNumber);

                    }
                }
            });
        } else {
            $("#statusModal").modal();

        }

    });

    $("#saveModal").click(function (e) {
        $("#statusModal").modal('hide');
        var srNumber = $('#srNumberUrlParam').val();
        var statusId = $("#serviceStatusId").val();
        var resolutionInstance = CKEDITOR.instances.resolution;
        var resolCheck = true;
         var isReqResolution = $("#requiredResolution").val();
		 if (!isReqResolution || (isReqResolution && isReqResolution=="Y")) {
	        if(statusId === "SR_CLOSED" && isEmptyCKEd(resolutionInstance)){
	            resolCheck = false;
	        }
        }
        var resolution = (CKEDITOR.instances["resolution"].getData()).trim();
        if (resolCheck) {
            $.ajax({
                type: "POST",
                url: "updateSrStatus",
                async: false,
                data: {
                    "srNumber": srNumber,
                    "srStatusId": statusId,
                    "resolution": resolution
                },
                success: function (data) {
                    var mesg = data[0];
                    if (mesg == "error") {

                        setTimeout(location.reload.bind(location), 3000);
                        $('div[data-notify=container]').css('z-index', '100000');

                        setTimeout(showAlert("error", "There are Open Activities tagged to this " + srNumber + ". Please close the Open Activities before closing/cancelling the SR"), 10000);
                    } else {
                        setTimeout(location.reload.bind(location), 500);

                        showAlert("success", "SR Updated Successfully" + ": " + srNumber);

                    }
                }
            });

        } else {
            //setTimeout(location.reload.bind(location),500);
            $("#serviceStatusId").val('${srCustReqStatusId!}');
            var isReqResolution = $("#requiredResolution").val();
			if (!isReqResolution || (isReqResolution && isReqResolution=="Y")) {
           		 showAlert("error", "Resolution field is mandatory to close the SR!");
            }
        }
    });

    $('#copy-btn').on('click', function (event) {

        $("#srCopyForm").submit();

    });

    function getPartyRoleTypeId(partyId) {
        var partyId = partyId;
        $.ajax({
            type: "POST",
            url: "getPartyRoleTypeId",
            async: false,
            data: {
                "partyId": partyId
            },
            success: function (data) {
                result = data;
                if (result && result[0] != undefined && result[0].roleTypeId != undefined)
                    var roleTypeId = result[0].roleTypeId;
                if ("LEAD" == roleTypeId) {
                    $('#orderId_label').remove();
                    $('#orderId').remove();
                }
            },
            error: function (data) {
                result = data;
                showAlert("error", "Error occured while fetching Party Role");
            }
        });
    }
	
	/*
    $("textarea").change(function () {
        textAreaFlag = "Y";
    });
    */

    $("#closeModal").click(function (e) {
        $("#statusModal").modal('hide');
        $("#serviceStatusId").val('${srCustReqStatusId!}');
        //setTimeout(location.reload.bind(location),100);
    });

    $('#reopen-btn').on('click', function (event) {
        $("#srReopenForm").submit();
    });

});
	
</script>

