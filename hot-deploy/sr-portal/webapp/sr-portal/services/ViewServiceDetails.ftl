<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#include "component://sr-portal/webapp/sr-portal/services/modal_window.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<script>
    CKEDITOR.env.isCompatible = true;
</script>
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
<script>
	<#if readOnlyPermission!>
		$(document).ready(function(){
	        $('a').each(function(){ 
	        	var elementId = $(this).attr('id')
	        	if(elementId === "link_cNo"){
	        		$(this).addClass("view-link");
	        		$(this).attr("href", "#"); // Set herf value
	            	$(this).attr("target","");	
	        	} else if(elementId === "link_primaryContactDesc"){
	        		$(this).addClass("view-link");
	        		$(this).attr("href", "#"); // Set herf value
	            	$(this).attr("target","");	
	        	} else if(elementId === "link_customerId"){
	        		$(this).addClass("view-link");
	        		$(this).attr("href", "#"); // Set herf value
	            	$(this).attr("target","");	
	        	} else if(elementId === "link_addressLocation"){
	        		$(this).addClass("view-link");
	        		$(this).attr("href", "#"); // Set herf value
	            	$(this).attr("target","");	
	        	}
	        	
	        });
	        
	        $("a.view-link").click(function () {
	            $("#accessDenied").modal("show");
	            return false;
	        });
	    });
    </#if>
</script>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "profileDetails") />  
<#assign srNumberUrlParam = requestParameters.srNumber!>
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
<#assign pretailParam = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "FROM_EMAIL_ID").queryOne()! />
<#if pretailParam?exists && pretailParam?has_content>
	<#assign fromEmailId = pretailParam.value!>
</#if>
<style>
</style>
<input type="hidden" name="srNumberUrlParam" id="srNumberUrlParam" value="${srNumberUrlParam!}" />
<input type="hidden" name="currentSrStatusId" id="currentSrStatusId" value="${currentSrStatusId!}" />
<input type="hidden" name="fromPartyId" id="fromPartyId" value="${fromPartyId!}" />
<@inputHidden  id="selectedOwnerId" value="${ownerUserLoginId?if_exists}" />
<input type="hidden" id="soMaterial" value="${soMaterial!}" />
<input type="hidden" id="materialType" value="${materialType!}" />
<input type="hidden" id="majorMaterial" value="${inputContext.materialCategory!majorMaterial!}" />
<input type="hidden" id="subMaterialCategory" value="${inputContext.materialSubCategory!subMaterialCategory!}" />
<input type="hidden" id="postalCode" value="${generalPostalCode!}" />
<input type="hidden" id="isAllowToCloseSR" value="${isAllowToCloseSR!}" />
<input type="hidden" id="workEffortNameStr" value="${workEffortNameStr!}" />
<input type="hidden" name="primary" id="primary" value="${inputContext.primary!}" />
<form method="post" id="srCopyForm" action="<@ofbizUrl>createServiceRequest?copy=Y</@ofbizUrl>" class="form-horizontal" novalidate="novalidate" data-toggle="validator" >
    <input type="hidden" name="srNumber" id="srNumber" value="${srNumberUrlParam!}">
</form>
<form method="post" id="srReopenForm" action="<@ofbizUrl>updateServiceRequestAction</@ofbizUrl>" class="form-horizontal" novalidate="novalidate" data-toggle="validator" >
    <input type="hidden" name="srNumber" id="srNumber" value="${srNumberUrlParam!}">
    <input type="hidden" name="srStatusId" id="srStatusId" value="SR_OPEN">
    <input type="hidden" name="reopenFlag" id="reopenFlag" value="Y">
    <input type="hidden" name="owner" id="owner" value="${ownerUserLoginId?if_exists}" />
    <input type="hidden" name="fromEmailId" id="fromEmailId" value="${fromEmailId!}" />
    <input type="hidden" name="srName" id="srName" value="${srName!}" />
    <input type="hidden" id="isAllowReopen" value="${isAllowReopen!}" />
    <input type="hidden" name="selFromPartyId" id="selFromPartyId" value="${fromPartyId!}" />
    <input type="hidden" name="selDescription" id="selDescription" value="${description!}" />	
    <input type="hidden" name="primary" value="${inputContext.primary!}" />
    <input type="hidden" name="customerId" value="${existCustomerId!}" />
    <input type="hidden" name="contractorId" value="${existContractorId!}" />
    <input type="hidden" name="reasonCode" value="${inputContext.reasonIds!}" />
    <input type="hidden" name="causeCategory" value="${inputContext.causeCategoryIds!}" />
</form>
<div class="pt-2">
    <h2 class="d-inline-block">FSR Details</h2>
    <ul class="flot-icone">
    	
    	<#if isEnableProgramAct?has_content && isEnableProgramAct=="Y" && (inputContext.isProgramTemplate?has_content && inputContext.isProgramTemplate=="Y")>
    	<li class="mt-0">
    		<span id="gen-prog-act-btn" class="btn btn-xs btn-primary" title="Generate Program Activities"><i class="fa fa-cog" aria-hidden="true"></i> Generate Act </span>
    	</li>
    	</#if>
		<li class="mt-0" style="margin-right: -13px;">
			<div class="form-group row" id="dropDowm_row" style="width: 260px;">
				<div class="col-sm-11">
					<#assign srStatusMap = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(srStatusList, "statusId", "description", false)?if_exists />       		
					<#if srCustReqStatusId?has_content && srStatusMap?has_content && (srCustReqStatusId == "SR_CLOSED" || srCustReqStatusId == "SR_CANCELLED") >
					<span>FSR Status : ${srStatusMap.get(srCustReqStatusId!)} </span>
					<#else>
					<select id="statusId" name="statusId" class="ui dropdown search form-control input-sm">
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
       <#-- <li class="mt-0">
            <#assign srStatusMap = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(srStatusList, "statusId", "description", false)?if_exists />       		
            <#if srCustReqStatusId?has_content && srStatusMap?has_content && (srCustReqStatusId == "SR_CLOSED" || srCustReqStatusId == "SR_CANCELLED") >
            <span>FSR Status : ${srStatusMap.get(srCustReqStatusId!)} </span>
            <#else> -->
            <#--  ${statusItem.description!} -->
            <#--	<#if readOnlyPermission!>
            	<#else>
	            <select id="statusId" name="statusId" class="inputBox">
	                <#list srStatusList as srStatus>
	                <#if srCustReqStatusId?if_exists == srStatus.statusId?if_exists>
	                <option value="${srStatus.statusId?if_exists}" selected="selected">${srStatus.description?if_exists}</option>
	                <#else>
	                <option value="${srStatus.statusId?if_exists}" >${srStatus.description?if_exists}</option>
	                </#if>
	                </#list>  
	            </select>
	            </#if>
            </#if>
        </li>-->
        <li class="mt-0">
            <#if allowCloseDateEditStatus?has_content && srCustReqStatusId?has_content && allowCloseDateEditStatus.contains(srCustReqStatusId?if_exists)?has_content>
            <span type="button" class="btn btn-xs btn-primary" id="sr-closedate-btn" data-toggle="modal" data-target="#datePickerModal" title="Update closed date"><i class="fa fa-calendar" aria-hidden="true"></i></span>
            </#if>
        </li>
        <#if readOnlyPermission!>
        <#else>
	        <#if  (custRequest?if_exists.statusId?if_exists == "SR_CLOSED" || custRequest?if_exists.statusId?if_exists == "SR_CANCELLED" )&& srReopen?if_exists == "Y">
		        <li class="mt-0">
		            <span id="reopen-btn" class="btn btn-xs btn-primary"><i class="fa fa-check" aria-hidden="true"></i> Reopen </span>
		        </li>
	        </#if>
	        <#if custRequest?if_exists.statusId?if_exists != "SR_CLOSED">
		        <#if  custRequest?if_exists.statusId?if_exists != "SR_CANCELLED">
			        <#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
			        <#if hasPermission>
				        <li class="mt-0">
				            <span id="save-btn" class="btn btn-xs btn-primary"><i class="fa fa-save" aria-hidden="true"></i> Save </span>
				        </li>
			        </#if>
			        <#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "UPDATE_OPERATION")?if_exists />
			        <#if hasPermission>
				        <li class="mt-0">
				            <a href="<@ofbizUrl>updateServiceRequest?srNumber=</@ofbizUrl>${context.custRequestId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
				        </li>
			        </#if>
			        <#-- <#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "RESOLVE_OPERATION")?if_exists />
			        -->
			        <#assign resolveEnabled = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "ENABLE_FSR_RESOLVE_BTN","Y")?if_exists>
			        <#if resolveEnabled?has_content && resolveEnabled == "Y">
				        <li class="mt-0">
				            <span id="resolve-btn" data-toggle="confirmation" title="Are you sure you want to Resolve?" class="btn btn-xs btn-primary"><i class="fa fa-check" aria-hidden="true"></i>Resolve </span>
				        </li>
			        </#if>
			        <#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "ASSIGN_OPERATION")?if_exists />
			        <#if hasPermission>
				        <li class="mt-0">
				            <#-- <span data-toggle="modal" data-target="#partyResponsible" title="Reassign" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Reassign</span>  -->
				            <span data-toggle="modal" data-target="#partyResponsible" title="Reassign" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Reassign</span>
				        </li>
			        </#if>
		        </#if>
	        </#if>
	          <#--  <#if  custRequest?if_exists.statusId?if_exists == "SR_CLOSED">  -->
	          <#if isCopySr?has_content && "Y"==isCopySr?if_exists>
		        <li class="mt-0">
		            <span id="copy-btn" class="btn btn-xs btn-primary"><i class="fa fa-check" aria-hidden="true"></i> Copy </span>
		        </li>
		       </#if>
	     <#--    </#if>  -->
     	</#if>
        <#if !isTsmUserLoggedIn || (isTsmUserLoggedIn && inputContext.description?has_content)>
        	<li class="mt-0"><span id="tsm-desc-btn" title="" class="btn btn-xs btn-primary"><i class="fa fa-question-circle" aria-hidden="true"></i> TSM Desc </span></li>
        </#if>
        <#assign viewExternalInfo = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "SR_VIEW_3RD_PTY_INV_INFO","N")?if_exists>
        <#if viewExternalInfo == "Y" && (inputContext.thirdPartyInvoiceNumber?has_content || inputContext.thirdPartyInvoicePrice?has_content)>
        	<li class="mt-0"><span type="button" class="btn btn-xs btn-primary" id="thirdPartyInv-desc-btn" data-toggle="modal" data-target="#thirdPartyInvDescModal" title="Third Party Invoice"><i class="fa fa-info-circle" aria-hidden="true"></i> Invoice</span></li>
        </#if>
        <li>${helpUrl}</li>
    </ul>
</div>
<div class="col-md-12 col-lg-12 col-sm-12">
    <@inputHidden  id="selectedOwnerId" value="${ownerUserLoginId?if_exists}" />
    <@inputHidden id="loggedInUserId" value="${userLogin.partyId?if_exists}" />
    <@inputHidden id="allowToCloseSR" value="${allowToCloseSR?if_exists}" />
    <@inputHidden id="wayOfClose" value="" />
    <@inputHidden id="dataSets" value="" />
    <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
    <#assign person = delegator.findOne("Person", findMap, true)!>
    <#if person?has_content>
	    <#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
	    <@inputHidden id="userName" value="${userName!}"/>
    </#if>
    <@dynaScreen 
	    instanceId="ADD_SERVICE_REQUEST"
	    modeOfAction="VIEW"
	    />
    <#-- 
    <@responsiblePicker 
    instanceId="partyResponsible"
    />	
    -->
    <@reassignPicker 
	    instanceId="partyResponsible"
	    />	
</div>

<#-- 
<#if inputContext.isCopySr?has_content && inputContext.isCopySr=="Y">
<div class="col-md-12 col-lg-12 col-sm-12 ">
    <@dynaScreen 
	    instanceId="SR_SOURCE"
	    modeOfAction="VIEW"
	    />
</div>
</#if>
 -->
 
<div class="col-md-12 col-lg-12 col-sm-12 ">
    <@dynaScreen 
	    instanceId="SR_CUSTOMER_CONTACT"
	    modeOfAction="VIEW"
	    />
</div>

<div class="col-md-12 col-lg-12 col-sm-12">
    <div class="form-group row" style="">
        <div class="col-sm-2 col-form-label field-text">TAT</div>
        <div class="col-sm-7 value-text">
            <input type='text' size='2' value='${tatDays!}' readOnly/> Days <input type='text' size='2' value='${tatHrs!}' readOnly/> Hrs <input type='text' size='2' value='${tatMins!}' readOnly/> Mins
        </div>
    </div>
</div>
<div class="col-md-12 col-lg-12 col-sm-12">
    <div class="form-group row" style="">
        <div class="col-sm-2 col-form-label field-text">Address</div>
        <div class="col-sm-7 value-text">
            ${srPostalAddress!}
            <#if isUspsAddrVerified?has_content && isUspsAddrVerified=="Y">
            	<img src="/bootstrap/images/usps-icon.png" title="USPS postal verified"/>
            </#if>
            <#if latitude?has_content && longitude?has_content>
            	<b><a target="_blank" id="link_addressLocation" href="/uiadv-portal/control/geoMap?lat=${latitude!}&lan=${longitude!}&srNumber=${srNumberUrlParam!}&externalLoginKey=${requestAttributes.externalLoginKey!}"><img src="/bootstrap/images/marker-icon.png" title="Map It"/></a></b>
            </#if>
        </div>
    </div>
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
	    label=uiLabelMap.RequestedResolution
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

<div class="col-md-12 col-lg-12 col-sm-12 ">
    <@textareaLarge
	    id="coordinatorDesc"
	    label="Coordinator Description"
	    rows="5"
	    required = false
	    txareaClass = "ckeditor"
	    value=coordinatorDesc!
	    />
    <script>
        CKEDITOR.replace( 'coordinatorDesc',{
        	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
        on: {
          change: function( evt ) {
              console.log( evt ); 
              CKEDITOR.dom.element.createFromHtml( '<input type="hidden" value="Y" id="coordinatorChanged" />' ).appendTo( CKEDITOR.document.getBody() );
          }
        }
        });
    </script>
</div>

<div class="col-md-12 col-lg-12 col-sm-12 ">
    <@textareaLarge
	    id="actualResolution"
	    label=uiLabelMap.ActualResolution!
	    rows="5"
	    required = false
	    txareaClass = "ckeditor"
	    value=actualResolution!
	    />
    <script>
        CKEDITOR.replace( 'actualResolution',{
        	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
        	on: {
	             change: function( evt ) {
	                 console.log( evt ); 
	                 CKEDITOR.dom.element.createFromHtml( '<input type="hidden" value="Y" id="actualResolutionChanged" />' ).appendTo( CKEDITOR.document.getBody());
	             }
            }
        });
    </script>
</div>

<#-- 
<div class="col-md-12 col-lg-12 col-sm-12 ">
    <div class="form-group row">
        <label class="col-sm-4 col-form-label " for="actualResolution">
            <h2 class="float-left col-form-label has-error">Actual Resolution</h2>
        </label>
        <div class="col-sm-12">
            <textarea class="form-control" rows="5" id="actualResolution" name="actualResolution" placeholder="" autocomplete="off">${inputContext.actualResolution!}</textarea>
            <div class="help-block with-errors" id="actualResolution_error"></div>
        </div>
    </div>
</div>
-->
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

<div id="accessDenied" class="modal fade " tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content alert alert-danger">
      <div class="modal-header">
        <h5 class="modal-title">Alert!</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p><h1>You do not have the permission.</h1></p>
      </div>
      <#--
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
      </div> -->
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
<div id="tsm-desc-modal" class="modal fade" >
    <div class="modal-dialog modal-lg" style="max-width: 700px;">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">TSM Description</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form id="add-attr-form" method="post" data-toggle="validator">
                    <input type="hidden" name="activeTab" value="attributes" />
                    <input type="hidden" name="srNumber" value="${srNumber?if_exists}">
                    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                    <@displayCell 
                    id="tsm-desc"
                    label="Description"
                    value="${StringUtil.wrapString(tsmDescription?if_exists)}"
                    />	
                    <div class="form-group offset-2">
                        <div class="text-left ml-3">
                            <#-- 
                            <@formButton
                            btn1type="submit"
                            btn1label="${uiLabelMap.Save}"
                            btn2=true
                            btn2id="srAttr-reset-btn"
                            btn2type="reset"
                            btn2label="${uiLabelMap.Clear}"
                            />
                            --> 	
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<style>
    #confirmationModel .modal-content {
    height: auto !important;
    overflow-x: hidden !important;
    overflow-y: auto !important;
    }
</style>
<div class="modal fade " id="confirmationModel" tabindex="-1" role="dialog" aria-labelledby="confirmationModelLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="confirmationModelLabel">Confirmation</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div>Time Entries Missing for the Technician Activities of this SR.</div>
                <span> Do You Still Want to Close the SR?</span>
            </div>
            <div class="modal-footer">
            	<button type="button" class="btn btn-primary" id="confirmYes">Yes</button>
                <button type="button" class="btn btn-secondary" id="confirmNo" data-dismiss="modal">No</button>
            </div>
        </div>
    </div>
</div>

<@datePickerModal
instanceId="datePickerModal"/>


<@thirdPartyInvDescModal
instanceId="thirdPartyInvDescModal"/>

<#if isEnableProgramAct?has_content && isEnableProgramAct=="Y">
<@generateProgramActivity 
instanceId="gen-prog-act"
/>
</#if>
<style>
div#cNo>a{
	font-weight: bold;
    font-family: sans-serif;
}
div#primaryContactDesc>a{
	font-weight: bold;
    font-family: sans-serif;
}
div#customerId>a{
	font-weight: bold;
    font-family: sans-serif;
}
div#contractorId>a{
	font-weight: bold;
    font-family: sans-serif;
}
div#domainEntityId>a{
	font-weight: bold;
    font-family: sans-serif;
}
</style>
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
    var custRequestId = $('#srNumberUrlParam').val();
	var dataSets = {
            "statusId": "SR_CLOSED",
            "externalId": custRequestId
        };
    $('#resolve-btn').on('click', function () {
        var srNumber = $('#srNumberUrlParam').val();
        //var resolution = document.getElementById('resolution').value.trim();
        var resolution = CKEDITOR.instances["resolution"].getData();
        var description = $('#description').val();
        var resolutionInstance = CKEDITOR.instances.resolution;
        var resolutionVal = CKEDITOR.instances.resolution.document.getBody().getText();
        var allowSRClose =$('#allowToCloseSR').val();    
        var resolveFlag = false;
        var isCustDisputeExist = "${inputContext.isCustDisputeExist!'N'}" 
        var owner = $("#selectedOwnerId").val();
        var reasonCode = "${inputContext.reasonIds!}";
        var causeCategory = "${inputContext.causeCategoryIds!}";
        var fromEmailId = $("#fromEmailId").val();
		
		if(allowSRClose != "" && allowSRClose == "N"){
        	 showAlert("error", "You are not allowed to close FSRs");
        	 setTimeout(location.reload.bind(location), 1500);
             return false;
        }  
		
		if ("Y"  === isCustDisputeExist) {
			//errorMsg = "An SR with Cause Category of Customer Dispute cannot be closed.";
		 	showAlert("error", "An SR with Cause Category of Customer Dispute cannot be closed.");
			return false;
		}
		var errorMsg = "<b>The Below Fields are Mandatory for FSR Closure:</b>";
		var isError = false;
        var ownerVal=$('#owner').val();       
        if (ownerVal == "") {
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Owner";
        	else
        		errorMsg = errorMsg + "Owner";
        	
        	isError = true;
        	/*
        	showAlert("error", "Owner should not be empty to close SR's");
        	setTimeout(location.reload.bind(location), 1500);
            return false;
            */
        }
              
        var startusIdVal=$("#statusId").val();       
        if(startusIdVal == ""){
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Status";
        	else
        		errorMsg = errorMsg + "Status";
        	
        	isError = true;
        	/*
        	showAlert("error", "Status should not be empty to close SR's");
        	setTimeout(location.reload.bind(location), 1500);
            return false;
            */
        }
        var zipCodeVal=$("#postalCode").val();       
        if(zipCodeVal == "" || zipCodeVal == undefined){
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Zip";
        	else
        		errorMsg = errorMsg + "Zip";
        	
        	isError = true;
        	/*
        	showAlert("error", "Zip code should not be empty to close SR's");
        	setTimeout(location.reload.bind(location), 1500);
            return false;
            */
        }	
        var soMaterialVal=$("#soMaterial").val();		
        if(soMaterialVal == "" || soMaterialVal == undefined){
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Special Order Material";
        	else
        		errorMsg = errorMsg + "Special Order Material";
        	
        	isError = true;
        	/*
        	showAlert("error", "Fill up Special Order Material before closing the SR");
        	setTimeout(location.reload.bind(location), 1500);
            return false;
            */
        }	
        var materialTypeVal=$("#materialType").val();		
        if(materialTypeVal == "" || materialTypeVal == undefined){
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Material Type";
        	else
        		errorMsg = errorMsg + "Material Type";
        	
        	isError = true;
        	/*
        	showAlert("error", "Fill up Material Type before closing the SR");
        	setTimeout(location.reload.bind(location), 1500);
            return false;
            */
        }	
        var majorMaterialVal=$("#majorMaterial").val();		
        if(majorMaterialVal == "" || majorMaterialVal == undefined){
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Material Category";
        	else
        		errorMsg = errorMsg + "Material Category";
        	
        	isError = true;
        	/*
        	showAlert("error", "Material Category should not be empty to close SR's");
        	setTimeout(location.reload.bind(location), 1500);
            return false;
            */
        }
        var subMaterialCategoryVal=$("#subMaterialCategory").val();		
        if(subMaterialCategoryVal == "" || subMaterialCategoryVal == undefined){
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Sub Material Category";
        	else
        		errorMsg = errorMsg + "Sub Material Category";
        	
        	isError = true;
        }
        
        if (resolutionVal == "") {
			/*
            var message = "Resolution field is mandatory to resolve the SR !"
            $('#submitModal').modal('show');
            $("#message").html(message); */
            
            if(errorMsg)
            	errorMsg = errorMsg + "<br>Requested Resolution";
            else
            	errorMsg = errorMsg + "Requested Resolution";
            
            isError = true;
            /*
            showAlert("error", "Resolution field is mandatory to resolve the SR!");
            return false;
            */
        }
        var descriptionVal = CKEDITOR.instances.description.document.getBody().getText();
        if (descriptionVal == "") {
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Description";
        	else
        		errorMsg = errorMsg + "Description";
        	
        	isError = true;
        	
        	/*
			showAlert("error", "Description field is mandatory to update the SR!");
            return false;
            */
        }
        var actualResolutionVal = CKEDITOR.instances.actualResolution.document.getBody().getText();
        if (actualResolutionVal == "") {
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Brief Description of Resolution";
        	else
        		errorMsg = errorMsg + "Brief Description of Resolution";
        	
        	isError = true;
        }
        
        
		dataSets = {
            "statusId": "SR_CLOSED",
            "owner": owner,
            "cNo": "${inputContext.cNo!}",
            "fromEmailId": fromEmailId,
            "custRequestId": srNumber,
            "resolution": resolution,
            "description": description,
            "srName" : "${inputContext.srName!}",
            "primary": $("#primary").val()
        };
        
        var isOpenActExists = "${isOpenActExists!}";
        if(isOpenActExists == "Y"){
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>There are Open Activities tagged to this " + srNumber + ". Please close the Open Activities before closing/cancelling the SR!";
        	else
        		errorMsg = errorMsg + "There are Open Activities tagged to this " + srNumber + ". Please close the Open Activities before closing/cancelling the SR!";
        	
        	isError = true;
        	/*
       		showAlert("error", "There are Open Activities tagged to this " + srNumber + ". Please close the Open Activities before closing/cancelling the SR!");
            return false;
            */
        }
        
        if (reasonCode == "") {
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Reason Code";
        	else
        		errorMsg = errorMsg + "Reason Code";
        	
        	isError = true;
        }
        
        if (causeCategory == "") {
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Cause Category";
        	else
        		errorMsg = errorMsg + "Cause Category";
        	
        	isError = true;
        }
        
        if(isError){
        	showAlert("error", errorMsg);
        	setTimeout(location.reload.bind(location), 1500);
            return false;
        }
        
		var isAllowToCloseSRVal=$("#isAllowToCloseSR").val();
        if(isAllowToCloseSRVal == "" || isAllowToCloseSRVal == "N") {
        	$("#wayOfClose").val("RESOLVE");
        	$("#dataSets").val(JSON.stringify(dataSets));
        	$('#confirmationModel').modal("show");	
        } else{
        	resolveFlag = true;
        }
        
        if(resolveFlag){
        	$("#wayOfClose").val("RESOLVE");
        	$("#dataSets").val(JSON.stringify(dataSets));
	        closerSR();
        }
    });
    
    $('#confirmYes').click(function(){
        closerSR();
    	$('#confirmationModel').modal("hide");
    });
        	
	$('#confirmNo').click(function(){
		$('#confirmationModel').modal("hide");
		var currentSrStatusId1 = $('#currentSrStatusId').val();
		$('#statusId').val(currentSrStatusId1);
	});

    function closerSR(){
    	var wayOfClose = $("#wayOfClose").val();
    	if("RESOLVE" === wayOfClose){
    		var dataSets = $("#dataSets").val();
    		var srNumber1 = $('#srNumberUrlParam').val();
    		$.ajax({
	            type: "POST",
	            url: "updateSRResolveStatus",
	            data: JSON.parse(dataSets),
	            async: false,
	            success: function (data) {
	                if (data[0].resolutionFlag == "resolution") {
	                    showAlert("error", "Resolution field is mandatory to resolve the SR");
	                }
	                if (data[0].resolveActivityflag == "closeActivity") {
	                    setTimeout(showAlert("error", "There are Open Activities tagged to this " + srNumber1 + ". Please close the Open Activities before closing/cancelling the SR"), 10000);
	                }
	                if (data[0].resolutionFlag != "resolution" && data[0].resolveActivityflag != "closeActivity") {
	                    setTimeout(location.reload.bind(location), 500);
	                    showAlert("success", "Resolved Successfully");
	                }
	            }
	        });
    	} else if("STATUS_CHANGE" === wayOfClose){
    		var dataSets = $("#dataSets").val();
    		var srNumber1 = $('#srNumberUrlParam').val();
    		$.ajax({
                type: "POST",
                url: "updateSrStatus",
                async: false,
                data: JSON.parse(dataSets),
                success: function (data) {
                    var mesg = data[0];
                    if (mesg == "error") {
                        $('div[data-notify=container]').css('z-index', '100000');
                        setTimeout(function () {
                            showAlert("error", "Please close the Open Activities before closing/cancelling the " + srNumber1);
                        }, 1000);
						setTimeout(location.reload.bind(location), 1000);

                    }else if(mesg == "errMsg") {
                		setTimeout(location.reload.bind(location), 3000);
                		setTimeout(showAlert("error", "You are not allowed to close SR's"), 2000);
                    }else if(mesg == "errAccessMsg") {
                		setTimeout(location.reload.bind(location), 3000);
                		setTimeout(showAlert("error", "Access Denied"), 2000);
                    }else if(mesg == "errSchMsg") {
                		setTimeout(location.reload.bind(location), 3000);
                		setTimeout(showAlert("error", "Please Schedule Activities before changing the FSR Status to Scheduled."), 2000);
                    } else {
                        setTimeout(location.reload.bind(location), 500);

                        showAlert("success", "FSR Status Updated Successfully" + ": " + srNumber1);

                    }
                }
            });
    	}
    	
    }

    $('#save-btn').on('click', function () {
        var srNumber = $('#srNumberUrlParam').val();
        var isDescriptionChanged = $("#descriptionChanged").val();
        var isResolutionChanged = $("#resolutionChanged").val();
        var isActualResolutionChanged = $("#actualResolutionChanged").val();
        var isCoordinatorChanged = $("#coordinatorChanged").val();
        
        var description = CKEDITOR.instances["description"].getData();
        var resolution = CKEDITOR.instances["resolution"].getData();
        var actualResolution = CKEDITOR.instances["actualResolution"].getData();
        var coordinatorDesc = CKEDITOR.instances["coordinatorDesc"].getData();
        
        var resolutionVal = CKEDITOR.instances.resolution.document.getBody().getText();
        var descriptionVal = CKEDITOR.instances.description.document.getBody().getText();
        if (descriptionVal == "") {
			showAlert("error", "Description field is mandatory to update the SR!");
            return false;
        }
        if (resolutionVal == "") {
			showAlert("error", "Resolution field is mandatory to update the SR!");
            return false;
        }
        if (isDescriptionChanged === "Y"  ||  isResolutionChanged === "Y" || isActualResolutionChanged === "Y" || isCoordinatorChanged === "Y") {

            var dataSets = {
                "srNumber": srNumber,
                "description": description,
                "resolution": resolution,
                "actualResolution":actualResolution,
                "coordinatorDesc":coordinatorDesc
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

    $('#statusId').on('change', function () {

        var srNumber = $('#srNumberUrlParam').val();
        var statusId = $("#statusId").val();
        var owner = $("#selectedOwnerId").val();
        var fromEmailId = $("#fromEmailId").val();
        //var resolution = $('#resolution').val();
        //var description = $('#description').val();
        
        var resolution = (CKEDITOR.instances["resolution"].getData()).trim();
        var description = (CKEDITOR.instances["description"].getData()).trim();
        var reasonCode = "${inputContext.reasonIds!}";
        var causeCategory = "${inputContext.causeCategoryIds!}";
        var srflag = true;
	
		var dataSets1 = {
			"srNumber": srNumber,
            "srStatusId": statusId,
            "owner": owner,
            "cNo": "${inputContext.cNo!}",
            "fromEmailId": fromEmailId,
            "generalPostalCode" : "${inputContext.generalPostalCode!}",
            "generalPostalCodeExt" : "${inputContext.generalPostalCodeExt!}",
            "generalCountryGeoId" : "${inputContext.generalCountryGeoId!}",
            "generalCity" : "${inputContext.generalCity!}",
            "countyGeoId" : "${inputContext.countyGeoId!}",
            "generalAddress1" : "${inputContext.generalAddress1!}",
            "generalAddress2" : "${inputContext.generalAddress2!}",
            "homePhoneNumber" : "${inputContext.homePhoneNumber!}",
            "offPhoneNumber" : "${inputContext.offPhoneNumber!}",
            "mobilePhoneNumber" : "${inputContext.mobilePhoneNumber!}",
            "contractorOffNumber" : "${inputContext.contractorOffNumber!}",
            "contractorMobileNumber" : "${inputContext.contractorMobileNumber!}",
            "contractorPrimaryEmail" : "${inputContext.contractorPrimaryEmail!}",
            "customerPrimaryEmail" : "${inputContext.customerPrimaryEmail!}",
            "srName" : "${inputContext.srName!}",
            "description": description,
            "resolution": resolution,
            "primary": $("#primary").val(),
            "causeCategory":"${inputContext.causeCategoryIds!}",
            "reasonCode":"${inputContext.reasonIds!}"
        };

        if (statusId == "SR_CANCELLED") {
            srflag = false;
        } else if (statusId == "SR_CLOSED") {
            srflag = false;
        }
        var changeFlag = true;
        if (statusId == "SR_CLOSED"){
        	var typeId = "${srTypeId!}";
        	if(!("REEB_REC_INS_OLY" === typeId)){
        		var errorMsg = "<b>The Below Fields are Mandatory for FSR Closure:</b>";
        		var isError = false;
        		var isCustDisputeExist = "${inputContext.isCustDisputeExist!'N'}" 
				if ("Y"  === isCustDisputeExist) {
				 	showAlert("error", "An SR with Cause Category of Customer Dispute cannot be closed.");
					$('#statusId').val(currentSrStatusId);
		            return false;
				}
				
		        var ownerVal=$("#owner").val();       
		        if (ownerVal == "") {
		        	if(errorMsg)
						errorMsg = errorMsg + "<br>Owner";
					else
						errorMsg = errorMsg + "Owner";
					srflag = false;
					isError = true;
					/*
		        	showAlert("error", "Owner should not be empty to close SR's");
		        	setTimeout(location.reload.bind(location), 1500);
		        	return false;
		        	*/
		        }     
		        
		        if(statusId == ""){
		        	if(errorMsg)
						errorMsg = errorMsg + "<br>Status";
					else
						errorMsg = errorMsg + "Status";
		        	srflag = false;
		        	isError = true;
		        }
		        var zipCodeVal=$("#postalCode").val();		
		        if(zipCodeVal == "" || zipCodeVal == undefined){
		        	if(errorMsg)
						errorMsg = errorMsg + "<br>Zip";
					else
						errorMsg = errorMsg + "Zip";
		        	srflag = false;
		        	isError = true;
		        }
		        var soMaterialVal=$("#soMaterial").val();		
		        if(soMaterialVal == "" || soMaterialVal == undefined){
		        	if(errorMsg)
						errorMsg = errorMsg + "<br>Special Order Material";
					else
						errorMsg = errorMsg + "Special Order Material";
		        	isError = true;
		        }	
		        var materialTypeVal=$("#materialType").val();		
		        if(materialTypeVal == "" || materialTypeVal == undefined){
		        	if(errorMsg)
						errorMsg = errorMsg + "<br>Material Type";
					else
						errorMsg = errorMsg + "Material Type";
						
					isError = true;   
		        }
		        var majorMaterialVal=$("#majorMaterial").val();		
		        if(majorMaterialVal == "" || majorMaterialVal == undefined){
		        	if(errorMsg)
						errorMsg = errorMsg + "<br>Material Category";
					else
						errorMsg = errorMsg + "Material Category";
					
					isError = true;
		        }
		        
		        var subMaterialCategoryVal=$("#subMaterialCategory").val();		
		        if(subMaterialCategoryVal == "" || subMaterialCategoryVal == undefined){
		        	if(errorMsg)
		        		errorMsg = errorMsg + "<br>Sub Material Category";
		        	else
		        		errorMsg = errorMsg + "Sub Material Category";
		        	
		        	isError = true;
		        }
        	}
        	
	      	var resolutionVal = CKEDITOR.instances.resolution.document.getBody().getText();
	        if (resolutionVal == "") {
	            if(errorMsg)
	            	errorMsg = errorMsg + "<br>Requested Resolution";
	            else
	            	errorMsg = errorMsg + "Requested Resolution";
	            
	            isError = true;
	        }
	        var descriptionVal = CKEDITOR.instances.description.document.getBody().getText();
	        if (descriptionVal == "") {
	        	if(errorMsg)
	        		errorMsg = errorMsg + "<br>Description";
	        	else
	        		errorMsg = errorMsg + "Description";
	        	
	        	isError = true;
	        }
	       	var actualResolutionVal = CKEDITOR.instances.actualResolution.document.getBody().getText();
	        if (actualResolutionVal == "") {
	        	if(errorMsg)
	        		errorMsg = errorMsg + "<br>Brief Description of Resolution";
	        	else
	        		errorMsg = errorMsg + "Brief Description of Resolution";
	        	
	        	isError = true;
	        }
	        
	        var isOpenActExists = "${isOpenActExists!}";
	        if(isOpenActExists == "Y"){
	        	if(errorMsg)
					errorMsg = errorMsg + "<br>There are Open Activities tagged to this " + srNumber + ". Please close the Open Activities before closing/cancelling the SR!";
				else
					errorMsg = errorMsg + "There are Open Activities tagged to this " + srNumber + ". Please close the Open Activities before closing/cancelling the SR!";
				$('#statusId').val(currentSrStatusId);
				
				isError = true;
	        }
	        
	        if (reasonCode == "") {
	        	if(errorMsg)
	        		errorMsg = errorMsg + "<br>Reason Code";
	        	else
	        		errorMsg = errorMsg + "Reason Code";
	        	
	        	isError = true;
	        }
	        
	        if (causeCategory == "") {
	        	if(errorMsg)
	        		errorMsg = errorMsg + "<br>Cause Category";
	        	else
	        		errorMsg = errorMsg + "Cause Category";
	        	
	        	isError = true;
	        }
	        
	        if(isError){
	        	showAlert("error", errorMsg);
	        	setTimeout(location.reload.bind(location), 1500);
	            return false;
	        }
	       	var isAllowToCloseSRVal=$("#isAllowToCloseSR").val();
	        if(isAllowToCloseSRVal == "" || isAllowToCloseSRVal == "N") {
	        	changeFlag = false;
	        	$("#wayOfClose").val("STATUS_CHANGE");
	        	$("#dataSets").val(JSON.stringify(dataSets1));
	        	$('#confirmationModel').modal("show");
	        } else{
	        	changeFlag = true;
	        }
        }

		if(changeFlag){
			if (statusId && srflag) {
				$("#wayOfClose").val("STATUS_CHANGE");
	        	$("#dataSets").val(JSON.stringify(dataSets1));
	            closerSR();
	        } else {
	            $("#statusModal").modal();
	
	        }
		}

    });

    $("#saveModal").click(function (e) {
        $("#statusModal").modal('hide');
        var srNumber = $('#srNumberUrlParam').val();
        var statusId = $("#statusId").val();
        var selectedOwnerId = $("#selectedOwnerId").val();
        var fromEmailId = $("#fromEmailId").val();
        var srName = $("#srName").val();
        var selFromPartyId = $("#selFromPartyId").val();
        var selDescription = $("#selDescription").val();
        
        var resolutionInstance = CKEDITOR.instances.resolution;
        var resolCheck = true;
        var resolutionVal = CKEDITOR.instances.resolution.document.getBody().getText();
        var descriptionVal = CKEDITOR.instances.description.document.getBody().getText();
        if (descriptionVal == "") {
			showAlert("error", "Description field is mandatory to update the SR!");
            return false;
        }
        if (resolutionVal == "") {
			showAlert("error", "Resolution field is mandatory to update the SR!");
            return false;
        }
        if(statusId === "SR_CLOSED" && isEmptyCKEd(resolutionInstance)){
            resolCheck = false;
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
                    "resolution": resolution,
                    "owner": selectedOwnerId,
                    "fromEmailId": fromEmailId,
                    "srName": srName,
                    "generalPostalCode" : "${inputContext.generalPostalCode!}",
                    "generalPostalCodeExt" : "${inputContext.generalPostalCodeExt!}",
                    "generalCountryGeoId" : "${inputContext.generalCountryGeoId!}",
                    "generalCity" : "${inputContext.generalCity!}",
            		"countyGeoId" : "${inputContext.countyGeoId!}",
                    "generalAddress1" : "${inputContext.generalAddress1!}",
                    "generalAddress2" : "${inputContext.generalAddress2!}",
                    "homePhoneNumber" : "${inputContext.homePhoneNumber!}",
                    "offPhoneNumber" : "${inputContext.offPhoneNumber!}",
                    "mobilePhoneNumber" : "${inputContext.mobilePhoneNumber!}",
                    "contractorOffNumber" : "${inputContext.contractorOffNumber!}",
                    "contractorMobileNumber" : "${inputContext.contractorMobileNumber!}",
                    "contractorPrimaryEmail" : "${inputContext.contractorPrimaryEmail!}",
                    "customerPrimaryEmail" : "${inputContext.customerPrimaryEmail!}",
                    "srName" : "${inputContext.srName!}",
                    "selFromPartyId": selFromPartyId,
                    "selDescription": selDescription,
                    "causeCategory":"${inputContext.causeCategoryIds!}",
       	 			"reasonCode":"${inputContext.reasonIds!}"
                },
                success: function (data) {
                    var mesg = data[0];
                    if (mesg == "error") {
                        setTimeout(location.reload.bind(location), 3000);
                        $('div[data-notify=container]').css('z-index', '100000');
                        setTimeout(showAlert("error", "There are Open Activities tagged to this " + srNumber + ". Please close the Open Activities before closing/cancelling the SR"), 10000);
                    } else if(mesg == "errMsg") {
                    		setTimeout(location.reload.bind(location), 2100);
                    		setTimeout(showAlert("error", "You are not allowed to close SR's"), 2000);
                    } else if(mesg == "errAccessMsg") {
                		setTimeout(location.reload.bind(location), 3000);
                		setTimeout(showAlert("error", "Access Denied"), 2000);
                    } else if(mesg == "errSchMsg") {
                		setTimeout(location.reload.bind(location), 3000);
                		setTimeout(showAlert("error", "Please Schedule Activities before changing the FSR Status to Scheduled"), 2000);
                    } else {
                        setTimeout(location.reload.bind(location), 500);
                        showAlert("success", "SR Updated Successfully" + ": " + srNumber);
                    }
                }
            });

        } else {
            //setTimeout(location.reload.bind(location),500);
            $("#statusId").val('${srCustReqStatusId!}');
            showAlert("error", "Resolution field is mandatory to close the SR!");
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
        $("#statusId").val('${srCustReqStatusId!}');
        //setTimeout(location.reload.bind(location),100);
    });

    $('#reopen-btn').on('click', function (event) {
    	var reopen=$("#isAllowReopen").val();
    	if(reopen != "" && reopen== "Y"){
    		$("#srReopenForm").submit();
    	}else{
    		showAlert("error", "SR Has Crossed the Reopen Day Limit: "+"${srReopenDays!}");
    	}
    });
    
$("#tsm-desc-btn").click(function(event) {
    $("#tsm-desc-modal").modal('show');
});

<#-- 
<#if isOpenOrderAssocTab?has_content && isOpenOrderAssocTab=="Y">
window.open("/sr-portal/control/createSrOrderAssoc?srNumber=${srNumberUrlParam!}&externalLoginKey=${requestAttributes.externalLoginKey!}");
</#if>
-->

});
	
</script>
<style>
.input-group-addon{
	padding: 1px 7px 1px 15px;
}
</style>
