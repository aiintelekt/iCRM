<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sr-portal/webapp/sr-portal/services/findCustomerModal.ftl"/>
<#include "component://sales-portal/webapp/sales-portal/activities-home/modalForActivity.ftl">
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/service-request/service_request.js"></script>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<script>
    CKEDITOR.env.isCompatible = true;
</script>
<#assign cifNo = '${requestParameters.partyId!}' >
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
	<#assign extra='<a id="create-contact" href="/contact-portal/control/createContact?accountPartyId=${inputContext.cNo!}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary ${isActive!}"  target="_blank">
	    <i class="fa fa-plus create" aria-hidden="true"></i> Create Dealer Contact</a><a href="/customer-portal/control/createCustomer?externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary"  target="_blank">
	    <i class="fa fa-plus create" aria-hidden="true"></i> Create HO/Contractor</a>' />
		
		<#assign extra=extra+'<a href="/sr-portal/control/findServiceRequests" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <#-- <@sectionFrameHeader title="${uiLabelMap.AddServiceRequest!}" extra=extra /> -->
        <div class="clearfix"></div>
        <@inputHidden id="primContactIdForName" value=""/>
        <form id="mainFrom" method="post" action="<@ofbizUrl>addServiceRequestEvent</@ofbizUrl>" data-toggle="validator">    
            <div class="col-lg-12 col-md-12 col-sm-12">
	            <@inputHidden id="statusId" value="LEAD_ASSIGNED"/>	 
	             
	            <@inputHidden id="defaultLocationId" value="${defaultLocationId!}"/>	           
	            <@inputHidden id="leadOrAccountPartyId" value="${leadOrAccountPartyId!}"/>
	            <@inputHidden id="workEffortId" value="${requestParameters.workEffortId!}"/>   
	            <#-- 
	            <#assign partySummaryDetailsView = (delegator.findOne("PartyGroup", {"partyId" : "${parameters.partyId?if_exists}"}, false))?if_exists/>
	              <#if partySummaryDetailsView?has_content && partySummaryDetailsView.groupName?has_content>	                 
	                  <input type="hidden" id="partyName" value="${partySummaryDetailsView.groupName?if_exists}">
	              <#else>
	              	<#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "${parameters.partyId?if_exists}")>
		            <#assign personName = delegator.findOne("Person", findMap, true)!>
		            <#assign partyDetail = delegator.findOne("Party", findMap, true)!>
		            <#assign nameVal = (personName.firstName!) + " " + (personName.middleName!) + " " + personName.lastName!>
	              		<input type="hidden" id="partyName" value="${nameVal?if_exists}">
	              		<input type="hidden" id="role" value="${partyDetail.roleTypeId?if_exists}">
	             </#if>
	             -->
	            <input type="hidden" id="partyName" value="${partyName?if_exists}">
	            <input type="hidden" id="role" value="${partySecurityRole?if_exists}">
	            <@inputHidden id="primContactId" value="${loggedInContactPartyId!}" />
	            <#assign roleTypeId = "">
	            <#assign userName = "">
				<#-- <@inputHidden id="owner" value="${userLogin.userLoginId?if_exists}" />  -->
				<@inputHidden id="loggedInUserId" value="${userLogin.userLoginId?if_exists}" />
	            <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
	            <#assign person = delegator.findOne("Person", findMap, true)!>
	            <#if person?has_content>
	            	<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
	            	<@inputHidden id="userName" value="${userName!}"/>
	            </#if>
				<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				<input type="hidden" name="fromEmailId" id="fromEmailId" value="${fromEmailId!}" />
				<input type="hidden" name="appUrl" id="appUrl" value="${appUrl!}" />
				<input type="hidden" id="postalIdVal" />
				<input type="hidden" id="countryCodeVal" />
				<input type="hidden" id="countyVal" />
				<input type="hidden" id="cityIdVal" />
				<input type="hidden" id="stateIdVal" />
				<input type="hidden" name="loggedInContactPartyId" id="loggedInContactPartyId" value="${loggedInContactPartyId!}" />
				<@inputHidden id="selectedSalesPerson" value="${userLogin.partyId!}"/>
				<@sectionFrameHeader title="${uiLabelMap.AddServiceRequest!}" extra=extra />
				
            	<@dynaScreen 
					instanceId="ADD_SERVICE_REQUEST"
					modeOfAction="CREATE"
				/>
               
            </div>
            
        	<div class="col-lg-12 col-md-12 col-sm-12">
		 		<h4 class="bg-light pl-1 mt-2">FSR Address </h4>
                <@dynaScreen 
					instanceId="SR_ADDR"
					modeOfAction="CREATE"
					/>
		 	</div>
		 	
		 	<div class="col-lg-12 col-md-12 col-sm-12">
	 			<h4 class="bg-light pl-1 mt-2">FSR Contact Info </h4>
	            <@dynaScreen 
					instanceId="SR_CUSTOMER_CONTACT"
					modeOfAction="CREATE"
					/>
            </div>
            
            <div class="col-lg-12 col-md-12 col-sm-12">
		 		<#-- 
		 		<@inputArea
 					inputColSize="col-sm-12"
		         	id="description"
		         	label=uiLabelMap.Description
		         	rows="10"
		         	placeholder = uiLabelMap.Description
		        /> -->
		       
		 		<@textareaLarge
			    	id="description"
				   	label=uiLabelMap.Description
				   	rows="5"
				   	required = true
				   	txareaClass = "ckeditor"
				   	value=description!
				   	/>
				<script>          
				    CKEDITOR.replace( 'description',{
				    	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js'
				    });
				</script>
		 	</div>
		 	
		 	<div class="col-lg-12 col-md-12 col-sm-12">
		 		<#--
		 		<@inputArea
		        	inputColSize="col-sm-12"
		         	id="resolution"
		         	label=uiLabelMap.Resolution
		         	rows="10"
		         	placeholder = uiLabelMap.Resolution
		         	value=""   
				 /> -->
				 
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
			        	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js'
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
				    value=actualResolution!
				    />
			    <script>
			        CKEDITOR.replace( 'coordinatorDesc',{
			        	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js'
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
			        	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js'
			        });
			    </script>
			</div>
					 	
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
               	<@formButton
					btn1type="submit"
					btn1id="create-sr-btn"
                    btn1label="${uiLabelMap.Save}"
                    btn1onclick="return formSubmission();"
                    btn2=true
                    btn2onclick = "resetForm()"
                    btn2type="reset"
                    btn2label="${uiLabelMap.Clear}"
                 />
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

<script>
var isUspsVarified = false;
var isChangingHomeownerAddress = false;
$(document).ready(function() {
	dynamicRequiredField();

    $('.cNo .picker-window-erase').click(function() {
        $('#create-contact').click();
    });

    $("#cNo_desc").on("change", function() {
        var cinNo = $('#cNo_val').val();
        $("#create-contact").attr("href", "/contact-portal/control/createContact?accountPartyId=" + cinNo + "&externalLoginKey=${requestAttributes.externalLoginKey!}");

    });

    $('#create-contact').click(function() {
        var cinNo = $('#cNo_val').val();
        $("#create-contact").attr("href", "/contact-portal/control/createContact?accountPartyId=" + cinNo + "&externalLoginKey=${requestAttributes.externalLoginKey!}");
    });

    var accountCNo = $("#cNo_val").val();
    //if(cNo != null && cNo != "" && cNo !="undefined"){$("#create-contact").addClass("enabled");} else{$("#create-contact").addClass("disabled");}
    /*if(accountCNo != null && accountCNo != "" && accountCNo !="undefined"){
    	$(".ContactID-input").append('<span style="float:right;"><a id="contact-refresh"><img src="/sr-portal-resource/images/refresh-black.png" /></a></span>');
    } else{
    	$(".ContactID-input").append('<span style="float:right;"><a id="contact-refresh"><img src="/sr-portal-resource/images/refresh-black.png" /></a></span>');
    }*/

    $(".ContactID-input .ContactID .icon").after('<span id=""><i id="contact-refresh" class="icon fa fa-refresh" style="float: right; margin-right: -25px;"></i></span>');
    //$(".ContactID-input").append('<span style="float:right;"><a id="contact-refresh"><img src="/sr-portal-resource/images/refresh-black.png" /></a></span>');

    $("#contact-refresh").click(function() {
        accountCNo = $("#cNo_val").val();
        //$("#contact-refresh").html('<img src="/sr-portal-resource/images/input-spinner.gif" />');
        if (accountCNo != null && accountCNo != "" && accountCNo != "undefined") {
            getPrimaryContacts(accountCNo);
        }
        //$("#contact-refresh").html('<img src="/sr-portal-resource/images/refresh-black.png" />');

    });

    $("#contractorPrimaryEmail").prop('disabled', true);
    $("#contractorOffNumber").prop('disabled', true);
    $("#contractorMobileNumber").prop('disabled', true);
    $("#contractorHomeNumber").prop('disabled', true);

    $(".sourceComponent").hide();
    $(".sourceDocumentId").hide();

    $(".srCategoryId-input").one("click", function() {
        loadCategory();
    });

    //$(".serviceFee-input").one("click", function() {
        loadCustomFieldValue('SERVICE_GROUP', 'Service for a Fee', 'serviceFee',"N");
    //});

    /*
    $(".finishType-input").one( "click",function(){
    	loadCustomFieldValue('FINISH_GROUP','Finish Type', 'finishType');
    }); */

    $(".vendorCode-input").one("click", function() {
        loadCustomFieldValue('VENDOR_GROUP', 'Vendor Code', 'vendorCode');
    });

    var loggedInUserId = $("#loggedInUserId").val();
    var leadOrAccountPartyId = $("#leadOrAccountPartyId").val();
    if (leadOrAccountPartyId != null && leadOrAccountPartyId != '') {

        var partyRole = $("#role").val();
        if (partyRole != null && partyRole != "" && partyRole != "undefined" && partyRole == "CUSTOMER") {
            $("#customerId_row").find("span.picker-window-erase").css("display", "none");
            $("#customerId_row").find("span.picker-window").css("display", "none");
            $("#customerId").attr('readonly', 'readonly');
            $("#customerId_desc").autocomplete("disable");
            $("#customerId_desc").attr('readonly', 'readonly');

            $('#customerId_desc').val($("#partyName").val());
            $('#customerId_val').val(leadOrAccountPartyId);
            $("#primary_2").attr("checked", true);
            loadCustomerDetails();

        } else if (partyRole != null && partyRole != "" && partyRole != "undefined" && partyRole == "CONTRACTOR") {
            $("#contractorId_row").find("span.picker-window-erase").css("display", "none");
            $("#contractorId_row").find("span.picker-window").css("display", "none");
            $("#contractorId").attr('readonly', 'readonly');
			$("#contractorId_desc").autocomplete("disable");
            $("#contractorId_desc").attr('readonly', 'readonly');
            
            $("#contractorId_val").val(leadOrAccountPartyId);
            $("#contractorId_desc").val($("#partyName").val());
            $("#primary_0").attr("checked", true);
            loadContractorDetails();
        } else if (partyRole != null && partyRole != "" && partyRole != "undefined" && partyRole == "ACCOUNT") {
            $("#cNo_row").find("span.picker-window-erase").css("display", "none");
            $("#cNo_row").find("span.picker-window").css("display", "none");
            $("#cNo").attr('readonly', 'readonly');
            $("#cNo_desc").autocomplete("disable");
            $("#cNo_desc").attr('readonly', 'readonly');

            $("#cNo_val").val(leadOrAccountPartyId);
            $("#cNo_desc").val($("#partyName").val());
            $("#primary_1").attr("checked", true);
            loadDealerDetails();
        } else if (partyRole != null && partyRole != "" && partyRole != "undefined" && partyRole == "CONTACT") {
            //$("#cNo_row").find("span.picker-window-erase").css("display", "none");
            //$("#cNo_row").find("span.picker-window").css("display", "none");
            //$("#cNo").attr('readonly', 'readonly');
            //$("#cNo_desc").autocomplete("disable");
            //$("#cNo_desc").attr('readonly', 'readonly');

            $("#cNo_val").val(leadOrAccountPartyId);
            $("#primary_1").attr("checked", true);
            loadDealerDetails();
            //$('.ui.dropdown.ContactID').addClass("disabled");
        }


        getPartyRoleTypeId(leadOrAccountPartyId);
        getPrimaryContacts(leadOrAccountPartyId);

    }

    $("#customerId_desc").on("change", function() {
    	loadCustomerDetails();
    });	
	
    $("#contractorId_desc").on("change", function() {
        loadContractorDetails();
    });

    function loadContractorDetails() {
        var customerId = $("#customerId_val").val();
        var contractor = $("#contractorId_val").val();
        var contractorName = $("#contractorId_desc").val();
        
        if (customerId == "" && contractor != "") {
            $("#contractorPrimaryEmail").prop('disabled', false);
            $("#contractorOffNumber").prop('disabled', false);
            $("#contractorMobileNumber").prop('disabled', false);
            $("#contractorHomeNumber").prop('disabled', false);
        }
        if (customerId != "" && contractor != "") {
            $("#contractorPrimaryEmail").prop('disabled', false);
            $("#contractorOffNumber").prop('disabled', false);
            $("#contractorMobileNumber").prop('disabled', false);
            $("#contractorHomeNumber").prop('disabled', false);
        }
        if(contractorName){
        	$("#contractorPrimaryEmail").prop('disabled', false);
            $("#contractorOffNumber").prop('disabled', false);
            $("#contractorMobileNumber").prop('disabled', false);
            $("#contractorHomeNumber").prop('disabled', false);
        }
        
        getCustomersAddress(contractor);
        getHomeOwnerPhoneNumbers(contractor, "N");

        autoPopulateSrName();
    }

    $("#cNo_desc").on("change", function() {
        autoPopulateSrName();
    });

    $("#ContactID").on("change", function() {
        autoPopulateSrName();
    });

    $('input[type=radio][name=primary]').change(function() {
        autoPopulateSrName();
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
		
		var cNoId = $("#cNo_val").val();
	
        var selectedValue = $("#homeOwnerAddress option:selected").text();

        if (contactMech != "" && selectedValue.includes('[Homeowner]')) {
            getHomeOwnerAddress(customerId, contactMech);
            //getHomeOwnerPhoneNumbers(customerId);
            var stateValue = $('#stateIdVal').val();
            getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
            loadZipCodeAssoc();
            //getLocation();
        } else if (contactMech != "" && selectedValue.includes('[Contractor]')) {
            getHomeOwnerAddress(contractorId, contactMech);
            //getHomeOwnerPhoneNumbers(contractorId);
            var stateValue = $('#stateIdVal').val();
            getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
            loadZipCodeAssoc();
            //getLocation();
        } else if (contactMech != "" && contractorId != "") {
            getHomeOwnerAddress(contractorId, contactMech);
            //getHomeOwnerPhoneNumbers(contractorId);
            var stateValue = $('#stateIdVal').val();
            getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
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
            $("#generalStateProvinceGeoId").dropdown('clear');
            $('#stateIdVal').val('');
            var stateValue = $('#stateIdVal').val();
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
        loadPrimaryTechnician();
    });

    $("#srCategoryId").change(function() {
        $("#srSubCategoryId").dropdown('clear');
        var srCategoryId = $("#srCategoryId").val();
        if (srCategoryId == "" || srCategoryId == null) {
            $("#srCategoryId_error").show();
        } else {
            $("#srCategoryId_error").hide();
        }

    });
    $("#srSubCategoryId").change(function() {
        var srCategoryId = $("#srCategoryId").val();
        var srSubCategoryId = $("#srSubCategoryId").val();
        if (srSubCategoryId == "" || srSubCategoryId == null) {
            $("#srSubCategoryId_error").show();
        } else {
            $("#srSubCategoryId_error").hide();
        }

    });

    $('#ownerBu').attr('readonly', 'readonly');
    if (loggedInUserId != undefined && loggedInUserId != null) {
        getBusinessUnit(loggedInUserId);
        getUsers();
        getSalesPerson();
    }
    getPrimaryTechnician();

    $("#srName").on("keydown", function(event) {
        var keyCode = event.keyCode || event.which;
        if (keyCode == 188 || keyCode == 190) {
            return false;
        }
        return true;
    });

    var countryGeoId = $('#generalCountryGeoId').val();

    regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
    regex = regexJson.regex;
    if ($('#generalCountryGeoId').val()) {
        getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}',null, true);
    }

    $('#generalCountryGeoId').change(function(e, data) {

        $("#generalStateProvinceGeoId").dropdown('clear');
        $('#generalPostalCode').val('');
        $('#generalPostalCodeExt').val('');
        getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}',null, true);
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
    	$('#submitModal').modal('hide');
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
	  		
	  		isUspsVarified = false;
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
            if(valid)
            	$('#mainFrom').preventDoubleSubmission();
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
    //getCustomers();

    loadZipCodeAssoc();
    $("#generalStateProvinceGeoId").change(function() {
        if (!isChangingHomeownerAddress) {
            loadZipCodeAssoc();
        }
        loadCoordinator();
        loadPrimaryTechnician();
    });
    $('#generalPostalCode').change(function(e) {
        if (!isChangingHomeownerAddress) {
            loadZipCodeAssoc();
        }
    });

    $(".finishType-input").one("click", function() {
        loadSegmentCodeData('FSR_FINISH_TYPE', 'finishType');
    });

    $(".materialCategory-input").one("click", function() {
        loadSegmentCodeData('FSR_MATERIAL_CATEGORY', 'materialCategory');
    });

    $("#materialCategory").change(function() {
        $("#materialSubCategory").dropdown('clear');
        loadSegmentValueData(this.value, "materialSubCategory");
    });

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
    $("#finishColor").change(function() {
        if (this.value) {
            $("#finishColor_error").html('');
        }
    });

    <#--
    <#if defaultLocationId ? has_content >
        $("#location").val("${defaultLocationId!}");
    $("#location").trigger("change")
    $("div.ui.dropdown.search.form-control.fluid.show-tick.location.selection > i").addClass("clear"); 
    </#if>	
    -->
	
    $("#countyGeoId").on("change", function() {
        console.log('change countyGeoId');
        getLocation();
        loadCoordinator();
        loadPrimaryTechnician();
    });
    
    $(".programTemplateId-input").one("click", function() {
    	loadProgramTemplate("programTemplateId", null, "${requestAttributes.externalLoginKey!}");
    });

});

function loadCustomerDetails() {
	var customerId = $("#customerId_val").val();
    getCustomersAddress(customerId);
    
    if (customerId) {
    	getHomeOwnerPhoneNumbers(customerId, "Y");
    }
	
    autoPopulateSrName();
}

function loadZipCodeAssoc() {

    var postalCode = $("#postalIdVal").val();
    var stateCode = $("#stateIdVal").val();

    var cityIdCode = $("#cityIdVal").val();
    var countryCode = $("#countryCodeVal").val();
    var countyCode = $("#countyVal").val();

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
        if (cityIdCode && cityIdCode == key || (cityList.size === 1)) {
            cityOptions += '<option value="' + key + '" selected>' + cityList.get(key) + '</option>';
        } else {
            cityOptions += '<option value="' + key + '">' + cityList.get(key) + '</option>';
        }
    }
    for (let key of countyList.keys()) {
        if (countyCode && countyCode == key || (countyList.size === 1)) {
            countyOptions += '<option value="' + key + '" selected>' + countyList.get(key) + '</option>';
        } else {
            countyOptions += '<option value="' + key + '">' + countyList.get(key) + '</option>';
        }
    }

    $("#generalCity").html(cityOptions).change();
    $("#generalCity").dropdown('refresh');

    $("#countyGeoId").html(countyOptions);
    $("#countyGeoId").dropdown('refresh');

    getLocation();
    loadCoordinator();
    loadPrimaryTechnician();
}

function formSubmission() {
    var valid = true;
    var srStatusId = document.getElementById('srStatusId').value;
    var resolutionInstance = CKEDITOR.instances.resolution;
    var causeCategory = $("#causeCategory").val();
    var customerDispute = $('select[name="causeCategory"] > option:contains("Customer Dispute")').val();

    if ($.inArray(customerDispute, causeCategory) != -1 && srStatusId === "SR_CLOSED") {
        showAlert("error", "An SR with Cause Category of Customer Dispute cannot be closed.");
        return false;
    }

    if (srStatusId === "SR_CLOSED" && isEmptyCKEd(resolutionInstance)) {
        showAlert("error", "Resolution field is mandatory to resolve the SR!");
        return false;
    }
    var resolutionVal = CKEDITOR.instances.resolution.document.getBody().getText();
    var descriptionVal = CKEDITOR.instances.description.document.getBody().getText();
    if (descriptionVal == "") {
        showAlert("error", "Description field is mandatory to create the SR!");
        valid = false;
    }
    
    if (resolutionVal == "") {
        showAlert("error", "Resolution field is mandatory to create the SR!");
        valid = false;
    }

    var finishTypeVal = $('#finishType').val();
    if (finishTypeVal != null && finishTypeVal != "" && finishTypeVal != "undefined" && (finishTypeVal == "UNFINISHED" || finishTypeVal == "Unfinished")) {
    }
    else if(finishTypeVal != null && finishTypeVal != "" && finishTypeVal != "undefined") {
        var finishColor = $("#finishColor").val();
        $("#finishColor_error").html('');
        if (finishColor != '') {} else {
            $("#finishColor").focus();
            $("#finishColor_error").append('<ul class="list-unstyled text-danger"><li id="finishColor_err">Please select an item in the list. </li></ul>');
            valid = false;
        }
    }

    var srCategoryId = $("#srCategoryId").val();
    var srSubCategoryId = $("#srSubCategoryId").val();
    if (srCategoryId == "" || srCategoryId == null) {
        $("#srCategoryId_error").show();
    } else {
        $("#srCategoryId_error").hide();
    }
    if (srSubCategoryId == "" || srSubCategoryId == null) {
        $("#srSubCategoryId_error").show();
    } else {
        $("#srSubCategoryId_error").hide();
    }

    return valid;
}

function getHomeOwnerPhoneNumbers(partyId, customerOrContractor) {

	if (!partyId) {
		return;
	}
	console.log('partyId: '+partyId+', customerOrContractor: '+customerOrContractor);
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

            if ("Y" == customerOrContractor) {
                $('#homePhoneNumber').val(homePhoneNumber).change();
                $('#offPhoneNumber').val(offPhoneNumber).change();
                $('#mobilePhoneNumber').val(mobileNumber).change();
            }

            if ("N" == customerOrContractor) {
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
            if (result.primaryContactInformation) {
                var primaryEmailId = result.primaryContactInformation.EmailAddress;
                if ("Y" == customerOrContractor) {
                    $('#customerPrimaryEmail').val(primaryEmailId);
                }
                if ("N" == customerOrContractor) {
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

$('.contractorId .picker-window-erase').click(function() {
    $("#contractorPrimaryEmail").val("");
    $("#contractorOffNumber").val("");
    $("#contractorMobileNumber").val("");
    $("#contractorHomeNumber").val("");

    $("#contractorPrimaryEmail").prop('disabled', true);
    $("#contractorOffNumber").prop('disabled', true);
    $("#contractorMobileNumber").prop('disabled', true);
    $("#contractorHomeNumber").prop('disabled', true);
});

$("#srTypeId").change(function() {
    const typeId = this.value;
    if ("REEB_REC_INS_OLY" === typeId) {
        //$("#onceAndDone").prop("checked", true);
        $("input[name=onceAndDone][value='N']").attr('checked', false);
        $("input[name=onceAndDone][value='Y']").attr('checked', true);
        getAllSrStatuses("Y");
    } else {
        $("input[name=onceAndDone][value='Y']").attr('checked', false);
        $("input[name=onceAndDone][value='N']").attr('checked', true);
        var sr_status = $("#srStatusId").val();
        if (sr_status === "SR_CLOSED")
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

