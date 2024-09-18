<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/service-request/service_request.js"></script>
<script>
    CKEDITOR.env.isCompatible = true;
</script>
<#assign copyFlag = requestParameters.copy!>
<#assign pretailParam = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "FROM_EMAIL_ID").queryOne()! />
<#if pretailParam?exists && pretailParam?has_content>
	<#assign fromEmailId = pretailParam.value!>
</#if>

<#assign pretailParamForAppUrl = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "APP_URL").queryOne()! />
<#if pretailParamForAppUrl?exists && pretailParamForAppUrl?has_content>
	<#assign appUrl = pretailParamForAppUrl.value!>
</#if>

<div class="row">
    <div id="main" role="main">
		
		<#assign extra1='<a id="create-contact" href="/contact-portal/control/createContact?accountPartyId=${inputContext.cNo!}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary ${isActive!}"  target="_blank">
	    <i class="fa fa-plus create" aria-hidden="true"></i> Create Dealer Contact</a><a href="/customer-portal/control/createCustomer?externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary"  target="_blank">
	    <i class="fa fa-plus create" aria-hidden="true"></i> Create HO/Contractor</a>' />
		
		<#assign tsmDescBtn = "">
		<#-- <#if !isTsmUserLoggedIn || (isTsmUserLoggedIn && inputContext.description?has_content)>
			<#assign tsmDescBtn = '<span id="tsm-desc-btn" title="" class="btn btn-xs btn-primary"><i class="fa fa-question-circle" aria-hidden="true"></i> TSM Desc </span>'>
		</#if>
		 -->
		 <#assign tsmDescBtn = extra1 + tsmDescBtn + '
        <button onclick="javascript:return formSubmission();" class="btn btn-xs btn-primary">
        <i class="fa fa-save" aria-hidden="true"></i> Save</button>' />
        <#assign extra = tsmDescBtn + '
        <a href="/sr-portal/control/viewServiceRequest?srNumber=${context.custRequestId!}" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" <#if copyFlag?if_exists == "Y"> action="<@ofbizUrl>addServiceRequestEvent</@ofbizUrl>" <#else> action="<@ofbizUrl>updateServiceRequestAction</@ofbizUrl>" </#if>  data-toggle="validator">
            <div class="col-lg-12 col-md-12 col-sm-12">
            	<#if copyFlag?if_exists == "Y">
		        	<@sectionFrameHeader title="${uiLabelMap.AddServiceRequest!}" extra=extra />
		        <#else>
		        	<@sectionFrameHeader title="${uiLabelMap.UpdateServiceRequest!}" extra=extra />
		        </#if>
            	<@inputHidden id="copyFlag" name="copyFlag" value="${copyFlag?if_exists}"/>
		        <@inputHidden id="srNumber" value="${parameters.srNumber?if_exists}"/>
		        <@inputHidden id="selectedContactId" value="${selectedContactId?if_exists}"/>
		        <@inputHidden  id="selectedOwnerId" value="${ownerUserLoginId?if_exists}" />
		        <@inputHidden  id="srTypeIdd" value="${srTypeId?if_exists}" />
		        <@inputHidden  id="srCategoryIdd" value="${srCategoryId?if_exists}" />
		        <@inputHidden  id="srSubCategoryIdd" value="${srSubCategoryId?if_exists}" />
		        <@inputHidden id="loggedInUserId" value="${userLogin.userLoginId?if_exists}" />
		        <@inputHidden id="primContactName" value="${primContactName?if_exists}" />
		        <@inputHidden id="primContactId" value="${primContactId?if_exists}" />
		        <@inputHidden id="allowToCloseSR" value="${allowToCloseSR?if_exists}" />
		        <@inputHidden id="homeOwnAddress" value="${inputContext.selectedHomeOwnerAddress!}" />
		        
		        <@inputHidden id="srCurrentStatusId" value="${srStatusId!}" />
		        
		        <@inputHidden id="materialCategoryId" value="${inputContext.materialCategory!}" />
		        <@inputHidden id="materialSubCategoryId" value="${inputContext.materialSubCategory!}" />
		        <@inputHidden id="finishTypeId" value="${inputContext.finishType!}" />
		        <@inputHidden id="finishColorId" value="${inputContext.finishColor!}" />
		        <input type="hidden" id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
		        <input type="hidden" name="fromEmailId" id="fromEmailId" value="${fromEmailId!}" />
		        <input type="hidden" name="appUrl" id="appUrl" value="${appUrl!}" />
		        <@inputHidden id="selectedSalesPerson" value="${inputContext.salesPerson!}"/>
		        <@inputHidden id="isSalesRole" value="${inputContext.isSalesRole!}"/>
		        <@inputHidden id="selectedPrimaryTechnician" value="${inputContext.primaryTechnician!}"/>
		        <input type="hidden" id="postalIdVal" />
				<input type="hidden" id="countryCodeVal" />
				<input type="hidden" id="countyVal" />
				<input type="hidden" id="cityIdVal" />
				<input type="hidden" id="stateIdVal" />
				<input type="hidden" id="soMaterial" value="${soMaterial!}" />
				<input type="hidden" id="materialType" value="${materialType!}" />
				<input type="hidden" id="majorMaterial" value="${inputContext.materialCategory!majorMaterial!}" />
				<input type="hidden" id="subMaterialCategory" value="${inputContext.materialSubCategory!subMaterialCategory!}" />
				<input type="hidden" id="postalCode" value="${generalPostalCode!}" />
				<input type="hidden" id="isAllowToCloseSR" value="${isAllowToCloseSR!}" />
				<input type="hidden" id="workEffortNameStr" value="${workEffortNameStr!}" />
				<input type="hidden" id="sourceDocumentId" name="sourceDocumentId" value="${sourceDocumentId!}" />
				
		        <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
		        <#assign person = delegator.findOne("Person", findMap, true)!>
		        <#if person?has_content>
		        	<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
		        	<@inputHidden id="userName" value="${userName!}"/>
		        </#if>
			
            	<#if copyFlag?if_exists == "Y">
		        	<@dynaScreen 
						instanceId="ADD_SERVICE_REQUEST"
						modeOfAction="CREATE"
						/>
		    	<#else>
		        	<@dynaScreen 
						instanceId="ADD_SERVICE_REQUEST"
						modeOfAction="UPDATE"
						/>
		    	</#if>
            </div>
            
            <div class="col-lg-12 col-md-12 col-sm-12">
                <h4 class="bg-light pl-1 mt-2">FSR Address </h4>
                <@dynaScreen 
				instanceId="SR_ADDR"
				modeOfAction="CREATE"
				/>
            </div>
            
            <div class="col-md-12 col-lg-12 col-sm-12 ">
	 			<h4 class="bg-light pl-1 mt-2">FSR Contact Info </h4>
	            <@dynaScreen 
					instanceId="SR_CUSTOMER_CONTACT"
					modeOfAction="CREATE"
					/>
            </div>
            
            <div class="col-md-12 col-lg-12 col-sm-12">
            	<#--
		 		<@inputArea
 					inputColSize="col-sm-12"
		         	id="description"
		         	label=uiLabelMap.Description
		         	rows="10"
		         	placeholder = uiLabelMap.Description
		         	value="${description!}"
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
						autoGrow_minHeight : CKEditorUtil.autoGrowMinHeight,
						removePlugins : CKEditorUtil.removePlugins
			        });
				</script>
				
		 	</div>
		 	
		 	<div class="col-md-12 col-lg-12 col-sm-12">
		 		<#-- 
		 		<@inputArea
		        	inputColSize="col-sm-12"
		         	id="resolution"
		         	label=uiLabelMap.Resolution
		         	rows="10"
		         	placeholder = uiLabelMap.Resolution
		         	value="${resolution!}"   
				 />
				 -->
				 
				<@textareaLarge
			       id="resolution"
			       label="Requested Resolution"
			       rows="5"
			       required = true
			       txareaClass = "ckeditor"
			       value=resolution!
			       />
			    <script>
			        CKEDITOR.replace( 'resolution',{
			        	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
						autoGrow_minHeight : CKEditorUtil.autoGrowMinHeight,
						removePlugins : CKEditorUtil.removePlugins
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
						autoGrow_minHeight : CKEditorUtil.autoGrowMinHeight,
						removePlugins : CKEditorUtil.removePlugins
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
						autoGrow_minHeight : CKEditorUtil.autoGrowMinHeight,
						removePlugins : CKEditorUtil.removePlugins
			        });
			    </script>
			</div>
						
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
            	<#if copyFlag?if_exists == "Y">
	               	<@formButton
						btn1type="submit"
	                    btn1label="${uiLabelMap.Save}"
	                    btn1onclick="return formSubmission();"
	                    btn2=true
	                    btn2onclick = "resetForm()"
	                    btn2type="reset"
	                    btn2label="${uiLabelMap.Clear}"
	                 />
            	<#else>
	                <@submit label="${uiLabelMap.Save}" onclick="return formSubmission();"/>
					<@cancel label="${uiLabelMap.Cancel}" onclick="/sr-portal/control/viewServiceRequest?srNumber=${context.custRequestId!}"/>
            	</#if>
            </div>
        </form>
    </div>
</div>

<@partyPicker 
instanceId="partyPicker"
/> 
<@customerPicker 
instanceId="customerPicker"
/>
<@contractorPicker 
instanceId="contractorPicker"
/>
<#-- <@dealerPicker 
instanceId="dealerPicker"
/>-->
<@findOrderDealerPicker 
instanceId="findOrderDealerPicker"
/>
<div id="submitModal" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
	        	<h5 class="modal-title">Confirmation</h5>
	        	<button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          		<span aria-hidden="true">&times;</span>
	        	</button>
	      	</div>
	      	<div class="modal-body">
	       		<span id="message"></span>
	      	</div>
	      	<div class="modal-footer">
	       		<input type="button" class="btn btn-sm btn-primary navbar-dark" value="Yes" id="createSubmitModal">
				<input type="button" class="btn btn-sm btn-secondary" data-dismiss="modal" value="No" onclick="return false;">
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
		    value="${tsmDescription!}"
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
        <button type="button" class="btn btn-secondary" id="confirmNo" data-dismiss="modal">No</button>
        <button type="button" class="btn btn-primary" id="confirmYes">Yes</button>
      </div>
    </div>
  </div>
</div>
	
<script>
var isUspsVarified = false;
var isChangingHomeownerAddress = false;
$(document).ready(function() {
	dynamicRequiredField();
	var srTypeId = $('#srTypeId').val();
	if("REEB_REC_INS_OLY" === srTypeId){
		//$("#onceAndDone").prop("checked", true);
		$("input[name=onceAndDone][value='N']").attr('checked', false);
		$("input[name=onceAndDone][value='Y']").attr('checked', true);
		getAllSrStatuses("Y");
	} else{
		$("input[name=onceAndDone][value='Y']").attr('checked', false);
		$("input[name=onceAndDone][value='N']").attr('checked', true);
		var sr_status = $("#srStatusId").val();
		if(sr_status === "SR_CLOSED")
			getAllSrStatuses("N");
	}
	
	$('.cNo .picker-window-erase').click(function () {
		$('#create-contact').click();
	});
	
	$("#cNo_desc").on("change", function() {
		var cinNo = $('#cNo_val').val();
		$("#create-contact").attr("href","/contact-portal/control/createContact?accountPartyId="+cinNo+"&externalLoginKey=${requestAttributes.externalLoginKey!}");
	});
	
	$('#create-contact').click(function(){
		var cinNo = $('#cNo_val').val();
		$("#create-contact").attr("href","/contact-portal/control/createContact?accountPartyId="+cinNo+"&externalLoginKey=${requestAttributes.externalLoginKey!}");
	});
	
	var accountCNo = $("#cNo_val").val();
	
	$( ".ContactID-input .ContactID .icon" ).after('<span id=""><i id="contact-refresh" class="icon fa fa-refresh" style="float: right; margin-right: -25px;"></i></span>');
	
	$("#contact-refresh").click(function(){
		accountCNo = $("#cNo_val").val();
		//$("#contact-refresh").html('<img src="/sr-portal-resource/images/input-spinner.gif" />');
		if(accountCNo != null && accountCNo != "" && accountCNo !="undefined"){
			getPrimaryContacts(accountCNo);
		}
		//$("#contact-refresh").html('<img src="/sr-portal-resource/images/refresh-black.png" />');
		
	});
	
	loadSegmentCodeData('FSR_FINISH_TYPE', 'finishType');
	loadSegmentCodeData('FSR_MATERIAL_CATEGORY', 'materialCategory');

    if ($('#materialCategoryId').length){
    	$("#materialSubCategory").dropdown('clear');
        loadSegmentValueData($('#materialCategoryId').val(), "materialSubCategory");
    }
    if ($('#finishTypeId').length){
    	$("#finishColor").dropdown('clear');
        loadSegmentValueData($('#finishTypeId').val(), "finishColor");
    }


    $("#finishType").change(function() {
        $("#finishColor").prop('required', false);
        $("#finishColor").dropdown('clear');
        $("#finishColor_error").html('');
        var finishTypeVal = this.value;
        if (finishTypeVal != null && finishTypeVal != "" && finishTypeVal != "undefined" && (finishTypeVal == "UNFINISHED" || finishTypeVal == "Unfinished")) {
            $('<input>').attr({
			    type: 'hidden',
			    id: 'finishColorId',
			    name: 'finishColorId',
			    value: 'NA'
			}).appendTo('form');
        } else{$("#finishColor").prop('required', true);}
        loadSegmentValueData(this.value, "finishColor")
    });

    var copyFlag = $("#copyFlag").val();
    if (copyFlag && "Y" == copyFlag) {
        //$("#orderId").val("");
    }

    $('#ownerBu').attr('readonly', 'readonly');

    var partyId = $("#cNo_val").val();
    if (partyId == undefined) {
        partyId = $("[name='cNo']").val();
    }
    if (partyId != "") {
        getPrimaryContacts(partyId);
        getPartyRoleTypeId(partyId);
    }
    loadCategory();
    var selectedOwnerId = $("#selectedOwnerId").val();
    if (selectedOwnerId != "" || selectedOwnerId == "") {
        getUsers();
    }
    
    $("#countyGeoId").on("change", function() {
        getLocation();
        loadCoordinator();
    });
    
    getSalesPerson();
    getPrimaryTechnician();
    
    var cNo1 = $("#cNo_val").val();
    if (cNo1 != "" && cNo1 != null) {
        getCustomersAddress(cNo1);
    }
    
    var customerId1 = $("#customerId_val").val();
    if (customerId1 != "" && customerId1 != null) {
        getCustomersAddress(customerId1);
    }
    
    $("#customerId_desc").on("change", function() {
    	var customerName1 = this.value;
    	if(customerName1 == "" || customerName1 == null){
    		$("#customerId_val").val("");
    	}
    	customerId = $("#customerId_val").val();
    	if(customerId && customerName1){
    		getCustomersAddress(customerId);
	        getHomeOwnerPhoneNumbers(customerId, "Y");
	        //autoPopulateSrName();
    	} else{
    		var primary =$("input[type=radio][name='primary']:checked").val();
    		if("HOME" == primary){
    			$("#homeOwnerAddress").val("");
		        $("#generalStateProvinceGeoId").dropdown('clear');
				$('#stateIdVal').val('');
				var stateValue=$('#stateIdVal').val();			
				getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
				$('#cityIdVal').val('');	
				$("#generalCity").dropdown('clear');
				$("#countyGeoId").dropdown('clear');
				$("#location").dropdown('clear');
				$("div.ui.dropdown.search.form-control.fluid.show-tick.location.selection > i").removeClass("clear");
				$("#countyVal").val('');
				$('#generalAddress1').val('');
				$('#generalAddress2').val('');			
				$('#generalPostalCode').val('');
				$('#generalPostalCodeExt').val('');
    		}
    	}
    });

    var contractor = $("#contractorId_val").val();
    if (contractor != "" && contractor != null) {
        getCustomersAddress(contractor);
    }

    $("#contractorId_desc").on("change", function() {
    	if(!contractor){
    		contractor = $("#contractorId_val").val();
    	}
    	var contractorName1 = this.value;
    	if(contractorName1 == "" || contractorName1 == null){
    		$("#contractorId_val").val("");
    	}
    	if(contractor && contractorName1){
    		getCustomersAddress($("#contractorId_val").val());
	        getHomeOwnerPhoneNumbers($("#contractorId_val").val(), "N");
	        //autoPopulateSrName();
    	} else{
    		var primary =$("input[type=radio][name='primary']:checked").val();
    		if("CONTRACTOR" == primary){
    			$("#homeOwnerAddress").val("");
		        $("#generalStateProvinceGeoId").dropdown('clear');
				$('#stateIdVal').val('');
				var stateValue=$('#stateIdVal').val();			
				getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
				$('#cityIdVal').val('');	
				$("#generalCity").dropdown('clear');
				$("#countyGeoId").dropdown('clear');
				$("#location").dropdown('clear');
				$("div.ui.dropdown.search.form-control.fluid.show-tick.location.selection > i").removeClass("clear");
				$("#countyVal").val('');
				$('#generalAddress1').val('');
				$('#generalAddress2').val('');			
				$('#generalPostalCode').val('');
				$('#generalPostalCodeExt').val('');
    		}
    	}
    });
    
    $("#cNo_desc").on("change", function() {
        //autoPopulateSrName();
    });
    
    $('input[type=radio][name=primary]').change(function() {
        dynamicRequiredField();
    });
    $("#ContactID").on("change", function() {
    	var selectedVal = this.value;
    	if(selectedVal == "") {
	    	$('#ContactID').val(selectedVal);
		    $('#ContactID').dropdown("set selected", selectedVal);
	    } else{
	    	$("#ContactID_error").html('');
	    }
	    $('#ContactID').dropdown('refresh');
    });
    
    
    var srCategoryId = $("#srCategoryIdd").val();
    if (srCategoryId != "" && srCategoryId != undefined) {
        loadSubCategory(srCategoryId);
    }

    $("div.ui.dropdown.search.form-control.fluid.show-tick.srCategoryId.selection > i").addClass("clear");
    $("div.ui.dropdown.search.form-control.fluid.show-tick.srSubCategoryId.selection > i").addClass("clear");

    var countryGeoId = $('#generalCountryGeoId').val();
    regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
    regex = regexJson.regex;
    if ($('#generalCountryGeoId').val()) {
        getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${inputContext.generalStateProvinceGeoId!}',null, true);
    }

    $('#generalCountryGeoId').change(function(e, data) {

        $("#generalStateProvinceGeoId").dropdown('clear');
        $('#generalPostalCode').val('');
        $('#generalPostalCodeExt').val('');
        getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${inputContext.generalStateProvinceGeoId!}',null, true);
        var countryGeoId = $('#generalCountryGeoId').val();
        if (countryGeoId != '') {
            regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
            regex = regexJson.regex;
        } else {
            $('#generalStateProvinceGeoId').html('<option value="">Please Select</option>');
        }
    });

    $('#generalPostalCode').keyup(function(e) {
        validatePostalCode('generalPostalCode', regex);
    });

    $('#generalPostalCodeExt').keyup(function(e) {
        validatePostalCodeExt('generalPostalCode');
    });

	$("#homePhoneNumber").on("keypress, keydown", function(event) {
       var phone = $(this).val();
       if (phone != null && phone != "" && phone != undefined) {
       	   $("#homePhoneNumber_error").html("");
       } else {
           $("#homePhoneNumber_error").html("Please fill in this field.");
       }
   	});
   	
	$("#contractorHomeNumber").on("keypress, keydown", function(event) {
	   var phone = $(this).val();
	   if (phone != null && phone != "" && phone != undefined) {
	   	   $("#contractorHomeNumber_error").html("");
	   } else {
	       $("#contractorHomeNumber_error").html("Please fill in this field.");
	   }
	});
   
    $('#mainFrom').validator().on('submit', function(e) {
    	$("#ContactID_error").html('');
    	$("#homePhoneNumber_error").html("");
    	$("#contractorHomeNumber_error").html("");
    	
    	var resolutionVal = CKEDITOR.instances.resolution.document.getBody().getText();
		var descriptionVal = CKEDITOR.instances.description.document.getBody().getText();
		var isError = false;
		var errorMsg = "<b>The Below Fields are Mandatory for FSR Closure:</b>";
		if (descriptionVal == "") {
			if(errorMsg)
        		errorMsg = errorMsg + "<br>Description";
        	else
        		errorMsg = errorMsg + "Description";
        	
        	isError = true;
        }
        if (resolutionVal == "") {
            if(errorMsg)
            	errorMsg = errorMsg + "<br>Requested Resolution";
            else
            	errorMsg = errorMsg + "Requested Resolution";
            
            isError = true;
            
        }
        
        if(isError){
        	showAlert("error", errorMsg);
            return false;
        }
        
        if (!e.isDefaultPrevented()) {
            var valid = true; 
            
            var firstName = $('#firstName').val();
			var customerName = $('#customerId_desc').val();
			var address1 = $('#generalAddress1').val();
			var generalPostalCode = $('#generalPostalCode').val();
			
			var customerId_val = $("#customerId_val").val();
			if(customerName && (customerId_val == "" || customerId_val == null || customerId_val == "undefined")){
				
				var primaryPhone = $("#homePhoneNumber").val();
				if(primaryPhone === null || primaryPhone === "" || primaryPhone === "undefined"){
					$("#homePhoneNumber_error").html("Please fill in this field.");
					$("#homePhoneNumber").focus();
					return false;
				} else{
					$.ajax({
			        	type: "POST",
			        	url : "/common-portal/control/getDedupPartyDetails",
			        	async: false,
			         	data: { "firstName": firstName,"customerName": customerName,"address1": address1,
			         		"zipCode": generalPostalCode, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
			        	success: function(data) {
			        		var status = data.status;
			        		var message = data.message;
			        		if(status && status === "success") {
			        			if(message && message === "PARTY_EXISTS"){
				        			valid = false;
							     	$('#submitModal').modal('show');
							        $("#message").html("Customer already exists with the name of <b>"+data.name+".</b> Do you want to create a new Customer?");
							        $("#createSubmitModal").attr("onclick","createNewCustomer()");
							        
						  		} else if(message && message === "PARTY_NOT_EXISTS"){
				        			valid = false;
							     	$('#submitModal').modal('show');
							        $("#message").html("This Customer does not exist in the system. Do you want to create a new Customer?");
							        $("#createSubmitModal").attr("onclick","createNewCustomer()");
							  	}
						  	}
			        	}
			      	});
			      	return valid;	
				}
			}
			
			
			var contractorName = $('#contractorId_desc').val();
			var contractorId_val = $("#contractorId_val").val();
			if(contractorName && (contractorId_val == "" || contractorId_val == null || contractorId_val == "undefined")){
				var primaryPhone = $("#contractorHomeNumber").val();
				if(primaryPhone === null || primaryPhone === "" || primaryPhone === "undefined"){
					$("#contractorHomeNumber_error").html("Please fill in this field.");
					$("#contractorHomeNumber").focus();
					return false;
				} else{
					
					$.ajax({
			        	type: "POST",
			        	url : "/common-portal/control/getDedupPartyDetails",
			        	async: false,
			         	data: { "firstName": firstName,"customerName": contractorName,"address1": address1,
			         		"zipCode": generalPostalCode, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
			        	success: function(data) {
			        		var status = data.status;
			        		var message = data.message;
			        		if(status && status === "success") {
			        			if(message && message === "PARTY_EXISTS"){
				        			valid = false;
							     	$('#submitModal').modal('show');
							        $("#message").html("Contractor already exists with the name of <b>"+data.name+".</b> Do you want to create a new Contractor?");
							        $("#createSubmitModal").attr("onclick","createNewContractor()");
						  		} else if(message && message === "PARTY_NOT_EXISTS"){
				        			valid = false;
							     	$('#submitModal').modal('show');
							        $("#message").html("This Contractor does not exist in the system. Do you want to create a new Contractor?");
							        $("#createSubmitModal").attr("onclick","createNewContractor()");
							  	}
						  	}
			        	}
			      	});	
				}
			}
			
			if (isUspsVarified) {
	  			return true;
	  		}
	  		
			<#if isActUspsAddrVal ? has_content && isActUspsAddrVal == "Y" >
                if (valid && $('#generalCountryGeoId').val() == "USA") {
                    var data = {
                        "Address1": "generalAddress1",
                        "Address2": "generalAddress2",
                        "Zip5": "generalPostalCode",
                        "Zip4": "generalPostalCodeExt",
                        "City": "generalCity",
                        "State": "generalStateProvinceGeoId",
                        "Business": "isBusiness",
                        "Vacant": "isVacant"
                    };
                    valid = USPSUTIL.performUspsAddrValidation(data, '${requestAttributes.externalLoginKey!}');
                } 
				</#if>
            return valid;
        } else{
        	var contactID = $('#ContactID').val();
	        if (contactID) {} else {
	            $("#ContactID_error").append('<ul class="list-unstyled text-danger"><li id="ContactID_err">Please select an item in the list. </li></ul>');
	            $('html, body').animate({
		            scrollTop: $("#primary_0").offset().top-100
		        }, 2000);
		        
	            return false;
	        }
        }
    });
    
    var homeOwnAddress = $("#homeOwnAddress").val();
    if (homeOwnAddress != "" && homeOwnAddress != undefined) {
        $("#homeOwnerAddress").val(homeOwnAddress);
        $('#homeOwnerAddress').dropdown('set selected', homeOwnAddress);
        $("#homeOwnerAddress").dropdown('refresh');
        $("#homeOwnerAddress").dropdown('change');
    }
    
    $("#homeOwnerAddress").on("change", function() {
    	console.log('trigger homeOwnerAddress');
		isChangingHomeownerAddress = true;
		$("#stateIdVal").val("");
		$("#countyVal").val("");
		$("#cityIdVal").val("");
		$("#generalAttnName").val('');
		$("#generalAddress1").val('');
		$("#generalAddress2").val('');
		$("#generalPostalCode").val('');
		$("#generalPostalCodeExt").val('');
		$("#generalCity").dropdown('clear');
	    $("#countyGeoId").dropdown('clear');
	    $("#generalStateProvinceGeoId").dropdown('clear');
	
	    var customerId = $("#customerId_val").val();
	    var contractorId = $("#contractorId_val").val();
	    var contactMech = $("#homeOwnerAddress").val();
	    var homeOwnAddress = $("#homeOwnAddress").val();
	    
	    var cNoId = $("#cNo_val").val();
	    
	    var selectedValue = $( "#homeOwnerAddress option:selected" ).text();
	    if(contactMech == "" || contactMech == null || contactMech == "undefined")
	    	$('#homeOwnerAddress').dropdown('set selected', '');
	    	
	     
	    
	    if (contactMech != "" && selectedValue.includes('[Homeowner]')) {		
			getHomeOwnerAddress(customerId,contactMech);			
			//getHomeOwnerPhoneNumbers(customerId);
			var stateValue=$('#stateIdVal').val();	
			getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
			loadZipCodeAssoc();
			//getLocation();
		} else if (contactMech != "" && selectedValue.includes('[Contractor]') ) {					
			getHomeOwnerAddress(contractorId,contactMech);			
			//getHomeOwnerPhoneNumbers(contractorId);
			var stateValue=$('#stateIdVal').val();			
			getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
			loadZipCodeAssoc();
			//getLocation();
		} else if (contactMech != "" && customerId != "" && contractorId != "" ) {					
			getHomeOwnerAddress(contractorId,contactMech);			
			//getHomeOwnerPhoneNumbers(contractorId);
			var stateValue=$('#stateIdVal').val();			
			getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
			loadZipCodeAssoc();
			//getLocation();
		} else if (contactMech != "" && contractorId != "") {
            getHomeOwnerAddress(contractorId, contactMech);
            //getHomeOwnerPhoneNumbers(contractorId);
            var stateValue = $('#stateIdVal').val();
            getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '');
            loadZipCodeAssoc();
            //getLocation();
        } else if (contactMech != "" && customerId) {
	        getHomeOwnerAddress(customerId, contactMech);
	        var stateValue = $('#stateIdVal').val();
	        getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
	        loadZipCodeAssoc();
	        //getLocation();
	    } else if (contactMech != "" && cNoId) {
            getHomeOwnerAddress(cNoId, contactMech);
            var stateValue = $('#stateIdVal').val();
            getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
            loadZipCodeAssoc();
            //getLocation();
        } else if (contactMech == "") {
	    	$("#homeOwnerAddress").val("");
	        $("#generalStateProvinceGeoId").dropdown('clear');
			$('#stateIdVal').val('');
			var stateValue=$('#stateIdVal').val();			
			getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
			$('#cityIdVal').val('');	
			$("#generalCity").dropdown('clear');
			$("#countyGeoId").dropdown('clear');
			$("#location").dropdown('clear');
			$("div.ui.dropdown.search.form-control.fluid.show-tick.location.selection > i").removeClass("clear");
			$("#countyVal").val('');
			$('#generalAddress1').val('');
			$('#generalAddress2').val('');			
			$('#generalPostalCode').val('');
			$('#generalPostalCodeExt').val('');
	    }
	    isChangingHomeownerAddress = false;
	    
	    loadCoordinator();
	});
	
    loadZipCodeAssoc();
    $("#generalStateProvinceGeoId").change(function() {
        if (!isChangingHomeownerAddress) {
			loadZipCodeAssoc();
		}
		loadCoordinator();
    });
    
    $('#generalPostalCode').change(function(e) {
        if (!isChangingHomeownerAddress) {
			loadZipCodeAssoc();
		}
    });

    $("#tsm-desc-btn").click(function(event) {
        $("#tsm-desc-modal").modal('show');
    });
    
    loadCustomFieldValue('SERVICE_GROUP','Service for a Fee','serviceFee');
    loadCustomFieldValue('VENDOR_GROUP','Vendor Code','vendorCode');
    $("#vendorCode").val('${vendorCode!}');
    $("#serviceFee").val('${serviceFee!}');
    console.log('vendorCode: ${vendorCode!}');
    console.log('serviceFee: ${serviceFee!}');
	    
	$(".finishType-input").one( "click",function(){
		loadSegmentCodeData('FSR_FINISH_TYPE', 'finishType');
	});
	
	$(".materialCategory-input").one( "click",function(){
		loadSegmentCodeData('FSR_MATERIAL_CATEGORY', 'materialCategory');
	});
	
	$("#materialCategory").change(function() {
		$("#materialSubCategory").dropdown('clear');
		loadSegmentValueData(this.value, "materialSubCategory");
	});
	
	$("#finishType").change(function() {
		$("#finishColor").prop('required',false);
		$("#finishColor").dropdown('clear');
		$("#finishColor_error").html('');
		var finishTypeVal = this.value;
		if (finishTypeVal != null && finishTypeVal != "" && finishTypeVal != "undefined" && (finishTypeVal == "UNFINISHED" || finishTypeVal == "Unfinished")) {
            $('<input>').attr({
			    type: 'hidden',
			    id: 'finishColorId',
			    name: 'finishColorId',
			    value: 'NA'
			}).appendTo('form');
        } else if(finishTypeVal != null && finishTypeVal != "" && finishTypeVal != "undefined") {
        	$("#finishColor").prop('required', true);
        }
		loadSegmentValueData(this.value, "finishColor")
	});
	$("#finishColor").change(function() {
		if(this.value){
			$("#finishColor_error").html('');	
		}
	});
	
	loadProgramTemplate("programTemplateId", "${inputContext.programTemplateId!}", "${requestAttributes.externalLoginKey!}");
	
});

function loadZipCodeAssoc() {
    var stateCode = $("#stateIdVal").val();
    if (stateCode != "") {
        $("#generalStateProvinceGeoId").val(stateCode);
    }
	
    if (!$("#generalStateProvinceGeoId").val()) {
        return;
    }
    var cityOptions = '<option value="" selected="">Select City</option>';
    var countyOptions = '<option value="" selected="">Select County</option>';
    var zipOptions = '<option value="" selected="">Select Zip Code</option>';

    let cityList = new Map();
    let countyList = new Map();
    let zipList = new Map();
    
    var countyCode = $("#countyVal").val();
    var cityIdCode = $("#cityIdVal").val();
    if (countyCode == "") {
        countyCode = '${inputContext.countyGeoId!}';
        $("#countyGeoId").val(countyCode);
    }
    if (cityIdCode == "") {
        cityIdCode = '${inputContext.generalCity!}';
        $("#generalCity").val(cityIdCode);
    }
    
    $.ajax({
        type: "POST",
        url: "/uiadv-portal/control/searchZipCodeAssocs",
        data: {
            "state": $("#generalStateProvinceGeoId").val(),
            "zip": $("#generalPostalCode").val(),
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function(result) {
            if (result.code == 200) {
                for (var i = 0; i < result.data.length; i++) {
                    var data = result.data[i];
                    cityList.set(data.city, data.city);
                    countyList.set(data.county, data.county);
                    //zipList.set(data.zip, data.zip); 
                }
            }
        }
    });

    for (let key of cityList.keys()) {
        if (cityIdCode && cityIdCode == cityList.get(key) || (cityList.size===1)) {
            cityOptions += '<option value="' + key + '" selected>' + cityList.get(key) + '</option>';
        } else {
            cityOptions += '<option value="' + key + '">' + cityList.get(key) + '</option>';
        }
    }
    for (let key of countyList.keys()) {
        if (countyCode && countyCode == key || (countyList.size===1)) {
            countyOptions += '<option value="' + key + '" selected>' + countyList.get(key) + '</option>';
        } else {
            countyOptions += '<option value="' + key + '">' + countyList.get(key) + '</option>';
        }
    }
    
    $("#generalCity").html(cityOptions).change();
    $("#countyGeoId").html(countyOptions);
    
    $("#generalPostalCode").dropdown('refresh');
    
    getLocation();
    loadCoordinator();
    
    $("#stateIdVal").val("");
	$("#countyVal").val("");
	$("#cityIdVal").val("");
}

function formSubmission() {
    var valid = true;
    var srStatusId = document.getElementById('srStatusId').value;
    var allowToCloseSR = document.getElementById('allowToCloseSR').value;
    var resolutionInstance = CKEDITOR.instances.resolution;
    var resolutionVal = CKEDITOR.instances.resolution.document.getBody().getText();
    var descriptionVal = CKEDITOR.instances.description.document.getBody().getText();
    var causeCategory = $("#causeCategory").val(); 
	var customerDispute = $('select[name="causeCategory"] > option:contains("Customer Dispute")').val();
	
	if (allowToCloseSR != "" && allowToCloseSR === "N") {
        showAlert("error", "You are not allowed to close FSRs!");
        return false;
    }
	if ($.inArray(customerDispute, causeCategory) != -1 && srStatusId === "SR_CLOSED") {
	 	showAlert("error", "An SR with Cause Category of Customer Dispute cannot be closed.");
		return false;
	}
	var errorMsg = "<b>The Below Fields are Mandatory for FSR Closure:</b>";
	var isError = false;
    if (descriptionVal == "") {
    	if(errorMsg)
        	errorMsg = errorMsg + "<br>Description";
        else
        	errorMsg = errorMsg + "Description";
        
        isError = true;
    }
    if (resolutionVal == "") {
    	if(errorMsg)
        	errorMsg = errorMsg + "<br>Requested Resolution";
        else
        	errorMsg = errorMsg + "Requested Resolution";
        
        isError = true;
    } else if(srStatusId === "SR_CLOSED" && isEmptyCKEd(resolutionInstance)) {
    	if(errorMsg)
        	errorMsg = errorMsg + "<br>Requested Resolution";
        else
        	errorMsg = errorMsg + "Requested Resolution";
        
        isError = true;
    }
    
    if (srStatusId === "SR_CLOSED") {
    	var typeId = "${srTypeId!}";
        if(!("REEB_REC_INS_OLY" === typeId)){
			var ownerVal = $('#owner').val();
	        if (ownerVal == "") {
	        	if(errorMsg)
		        	errorMsg = errorMsg + "<br>Owner";
		        else
		        	errorMsg = errorMsg + "Owner";
		        	
		        isError = true;
	        }
	        var startusIdVal = $("#srStatusId").val();
	        if (startusIdVal == "") {
	        	if(errorMsg)
		        	errorMsg = errorMsg + "<br>Status";
		        else
		        	errorMsg = errorMsg + "Status";
		        
		        isError = true;
	        }
	        var zipCodeVal = $("#postalCode").val();
	        if (zipCodeVal == "" || zipCodeVal == undefined) {
	        	if(errorMsg)
		        	errorMsg = errorMsg + "<br>Zip";
		        else
		        	errorMsg = errorMsg + "Zip";
		        
		        isError = true;
	        }
	        var soMaterialVal = $("#soMaterial").val();
	        if (soMaterialVal == "" || soMaterialVal == undefined) {
	        	if(errorMsg)
		        	errorMsg = errorMsg + "<br>Special Order Material";
		        else
		        	errorMsg = errorMsg + "Special Order Material";
		        
		        isError = true;
	        }
	        var materialTypeVal = $("#materialType").val();
	        if (materialTypeVal == "" || materialTypeVal == undefined) {
	        	if(errorMsg)
		        	errorMsg = errorMsg + "<br>Material Type";
		        else
		        	errorMsg = errorMsg + "Material Type";
		        
		        isError = true;
	        }
	        var majorMaterialVal = $("#majorMaterial").val();
	        if (majorMaterialVal == "" || majorMaterialVal == undefined) {
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
       		var currentSrStatusId1 = $('#srCurrentStatusId').val();
       		
       		if(errorMsg)
	        	errorMsg = errorMsg + "<br>There are Open Activities tagged to this " + $('#custRequestId').val() + ". Please close the Open Activities before closing/cancelling the SR!";
	        else
	        	errorMsg = errorMsg + "There are Open Activities tagged to this " + $('#custRequestId').val() + ". Please close the Open Activities before closing/cancelling the SR!";
       		$('#srStatusId').dropdown("clear");
            $('#srStatusId').dropdown("set selected", currentSrStatusId1);
            $('#srStatusId').dropdown('refresh');
            isError = true;
        }
        
        
        var reasonCode = $("#reasonCode").val();
        if (reasonCode == "") {
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Reason Code";
        	else
        		errorMsg = errorMsg + "Reason Code";
        	
        	isError = true;
        }
        var causeCategory = $("#causeCategory").val();
        if (causeCategory == "") {
        	if(errorMsg)
        		errorMsg = errorMsg + "<br>Cause Category";
        	else
        		errorMsg = errorMsg + "Cause Category";
        	
        	isError = true;
        }
        
        if(isError){
        	showAlert("error", errorMsg);
            return false;
        }
        
        var isAllowToCloseSRVal=$("#isAllowToCloseSR").val();
        if(isAllowToCloseSRVal == "" || isAllowToCloseSRVal == "N") {
        	valid = false;
        	$('#confirmationModel').modal("show");	
        } else{
        	valid = true;
        }
	
    }
    return valid;
}

$('#confirmYes').click(function(){
	$("#isAllowToCloseSR").val("Y");
	$('#confirmationModel').modal("hide");
	$("#mainFrom").submit();
});




$('#confirmNo').click(function(){
	$('#confirmationModel').modal("hide");
	var currentSrStatusId1 = $('#srCurrentStatusId').val();
	$('#srStatusId').dropdown("clear");
    $('#srStatusId').dropdown("set selected", currentSrStatusId1);
    $('#srStatusId').dropdown('refresh');
});

function getHomeOwnerPhoneNumbers(partyId, customerOrContractor) {

    $.ajax({
        type: "POST",
        url: "/common-portal/control/getPartyTelecomNumbers",
        data: {
            "partyId": partyId,
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function(data) {
            var homePhoneNumber;
            var offPhoneNumber;
            var mobileNumber;

            for (var i = 0; i < data.length; i++) {
                var entry = data[i];
                var purposeTypeId = entry.purposeTypeId;
                var phoneNumber = entry.contactNumber;

                if (purposeTypeId && "PRIMARY_PHONE" == purposeTypeId) {
                    homePhoneNumber = phoneNumber;
                } else if (purposeTypeId && "PHONE_MOBILE" == purposeTypeId) {
                    mobileNumber = phoneNumber;
                } else if (purposeTypeId && "PHONE_WORK" == purposeTypeId) {
                    offPhoneNumber = phoneNumber;
                }
            }

            if("Y" == customerOrContractor){
	            $('#homePhoneNumber').val(homePhoneNumber).change();
	            $('#offPhoneNumber').val(offPhoneNumber).change();
	            $('#mobilePhoneNumber').val(mobileNumber).change();
            }
            
            if("N" == customerOrContractor){
	            $('#contractorHomeNumber').val(homePhoneNumber).change();
	            $('#contractorOffNumber').val(offPhoneNumber).change();
	            $('#contractorMobileNumber').val(mobileNumber).change();
            }

        }
    });
	
    $.ajax({
        type: "POST",
        url: "/common-portal/control/getPartyAddress",
        async: false,
        data: {
            "partyId": partyId
        },
        success: function(data) {
            result = data;
            if ("Y" == customerOrContractor) {
            	$('#customerPrimaryEmail').val("");
            	if (result.primaryContactInformation) {
            		var primaryEmailId = result.primaryContactInformation.EmailAddress;
	            	if(primaryEmailId)
	                	$('#customerPrimaryEmail').val(primaryEmailId);
            	}
            }
            
	        if ("N" == customerOrContractor) {
            	$('#contractorPrimaryEmail').val("");
            	if (result.primaryContactInformation) {
            		var primaryEmailId = result.primaryContactInformation.EmailAddress;
	            	if(primaryEmailId)
	                	$('#contractorPrimaryEmail').val(primaryEmailId);
            	}
            }
        },
        error: function(data) {
            result = data;
            //showAlert("error", "Error occured while fetching homeowner address");
        }
    });
}
$("#srTypeId").change(function(){
	const typeId = this.value;
	if("REEB_REC_INS_OLY" === typeId){
		//$("#onceAndDone").prop("checked", true);
		$("input[name=onceAndDone][value='N']").attr('checked', false);
		$("input[name=onceAndDone][value='Y']").attr('checked', true);
		getAllSrStatuses("Y");
	} else{
		$("input[name=onceAndDone][value='Y']").attr('checked', false);
		$("input[name=onceAndDone][value='N']").attr('checked', true);
		var sr_status = $("#srStatusId").val();
		if(sr_status === "SR_CLOSED")
			getAllSrStatuses("N");
	}
});

function createNewCustomer(){
	//creating new customer
	$('#submitModal').modal('hide');
	var customerName = $('#customerId_desc').val();
	var valid = false;
	
	<#if isActUspsAddrVal ? has_content && isActUspsAddrVal == "Y" >
	if ($('#generalCountryGeoId').val() == "USA") {
		var data = {
			"Address1": "generalAddress1",
			"Address2": "generalAddress2",
			"Zip5": "generalPostalCode",
			"Zip4": "generalPostalCodeExt",
			"City": "generalCity",
			"State": "generalStateProvinceGeoId",
			"Business": "isBusiness",
			"Vacant": "isVacant"
		};
		valid = USPSUTIL.performUspsAddrValidation(data, '${requestAttributes.externalLoginKey!}');
	} 
	</#if>
	
	
	if (valid) {
		
		$.ajax({
	    	type: "POST",
	    	url : "/common-portal/control/createNewCustomer",
	    	async: false,
	     	data: {"customerName": customerName, "isContractor": "N", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
	    	success: function(data) {
	    		var status = data.status;
	    		if(status && status === "success"){
					var partyId = data.partyId;
					$("#customerId_val").val(partyId);    
					valid = true;
					isUspsVarified = true;
	    		} else{
	    			showAlert("error", data.message);
	    			valid = false;
	    			isUspsVarified = false;
	    		}
	    	}
	  	});
	  	
	  	if(valid)
			$('#mainFrom').submit();
	}
	
	
}

function createNewContractor(){
	//creating new customer
	$('#submitModal').modal('hide');
	var contractorName = $('#contractorId_desc').val();
	var contractorId_val = $("#contractorId_val").val();
	var valid = false;
	
	<#if isActUspsAddrVal ? has_content && isActUspsAddrVal == "Y" >
	if ($('#generalCountryGeoId').val() == "USA") {
		var data = {
			"Address1": "generalAddress1",
			"Address2": "generalAddress2",
			"Zip5": "generalPostalCode",
			"Zip4": "generalPostalCodeExt",
			"City": "generalCity",
			"State": "generalStateProvinceGeoId",
			"Business": "isBusiness",
			"Vacant": "isVacant"
		};
		valid = USPSUTIL.performUspsAddrValidation(data, '${requestAttributes.externalLoginKey!}');
	} 
	</#if>
	
	
	if (valid) {
		
		$.ajax({
	    	type: "POST",
	    	url : "/common-portal/control/createNewCustomer",
	    	async: false,
	     	data: {"customerName": contractorName, "isContractor": "Y", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
	    	success: function(data) {
	    		var status = data.status;
	    		if(status && status === "success"){
					var partyId = data.partyId;
					$("#contractorId_val").val(partyId);    
					valid = true;
					isUspsVarified = true;
	    		} else{
	    			showAlert("error", data.message);
	    			valid = false;
	    			isUspsVarified = false;
	    		}
	    	}
	  	});
	  	
	  	if(valid)
			$('#mainFrom').submit();
	}
	
	
}

</script>