<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<@sectionHeader title="${uiLabelMap.Create} ${uiLabelMap.Lead}" />
<#assign partyId= request.getParameter("partyId")!>
<div class="d-none d-md-block d-lg-block">
   <form method="post" id="singleFormDesk" action="<@ofbizUrl>leadShortFromDesktop</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
   <input type="hidden" name="leadId" value="${leadId!}">
   <input type="hidden" name="actionType" value="${actionType!}">
   <input type="hidden" name="backUrl" value="${backUrl!}">
   <input type="hidden" name="leadShortForm" value="Y">
   <input type="hidden" name="leadShortFromDesktop" value="Y">
   <input type="hidden" id="isNotDuplicate" name="isNotDuplicate" value="">
<div class="page-header" id="eitherOrFields" >
    <#-- <h2 class="float-left">Basic Company Details</h2> -->
    <div class="float-right"> 
        <div class="help-block with-errors">
            <ul class="list-unstyled">
                <li>Mobile No OR Office No OR Email Address OR <br/> Registered Address 1/State/City/PIN Code are mandatory</li>
                
            </ul>
        </div>
    </div>
</div>
   <div class="row padding-r">
     <div class="col-md-6 col-sm-6">
         <@generalInput
             id="companyName"
             onkeyup = "loadConstitute(this);"
             label=uiLabelMap.companyName
             placeholder=uiLabelMap.companyName
             value=dataImportLead.companyName
             required=true
             maxlength=255
             pattern="^[ A-Za-z0-9'@.!&:*()+-]*$"
             dataError="Please enter valid name"
         />
         <#assign constitutionParameters = { "type-id": "constitution","name":"Constitution"}>
        <@singleFormDropdownInput
            id="constitution"
            label=uiLabelMap.constitution
            required=false
            options=constitutionList
            colDivClass=""
             searchSelectClass="search form-control input-sm"
            value=dataImportLead.constitution
            allowEmpty=true
            dataLiveSearch=true
            lookup = Y
            lookupTarget = "enumerationLookup"
            hasPermission=hasEnumPermission
            lookupParams=constitutionParameters
        />
        <#assign salutation=""/>
         <#-- <#if dataImportLead?if_exists.title?if_exists?contains(".")>
             <#assign salutation="${dataImportLead?if_exists.title?if_exists}"/>
         <#else> -->
             <#assign enumeration = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("Enumeration", {"enumCode" : "${dataImportLead?if_exists.title?if_exists}"}, [], false))?if_exists/>
             <#assign salutation="${enumeration.enumId!}"/>
         <#--</#if> -->
         
         <#assign salutationParameters = { "type-id": "salutation","name":"Salutation"}>
         <@singleFormDropdownInput
             id="title"
             label=uiLabelMap.salutation
             options=titleList
             required=false
             value="${salutation!}"
             colDivClass=""
             searchSelectClass="search form-control input-sm"
             allowEmpty=true
             dataLiveSearch=true
             lookup =Y
             lookupTarget = "enumerationLookup"
             hasPermission=hasEnumPermission
             lookupParams=salutationParameters
        />
         <@generalInput
             id="firstName"
             label=uiLabelMap.firstName
             placeholder=uiLabelMap.firstName
             value=dataImportLead.firstName
             required=true
             maxlength=100
             pattern="^[ A-Za-z'@.!&:*()+-]*$"
             dataError="Please enter valid name"
         />
         <@generalInput
             id="lastName"
             label=uiLabelMap.lastName
             placeholder=uiLabelMap.lastName
             value=dataImportLead.lastName
             required=false
             maxlength=100
             pattern="^[ A-Za-z'@.!&:*()+-]*$"
             dataError="Please enter valid name"
         />
         
        <#assign designationParameters = { "type-id": "leadDesignation","name":"Designation"}>
        <@singleFormDropdownInput
            id="designation"
            label=uiLabelMap.designation
            options=designationList
            required=false
            value=dataImportLead.designation
            colDivClass=""
             searchSelectClass="search form-control input-sm"
            allowEmpty=true
            dataLiveSearch=true
            lookup = Y
            lookupTarget = "enumerationLookup"
            hasPermission=hasEnumPermission
            lookupParams=designationParameters
        />
        <@generalInputSplitCol
            colId1="primaryPhoneCountryCode"
            colId2="primaryPhoneNumber"
            label="Mobile No"
            colPlaceholder1 = "+91"
            colPlaceholder2 = "4442147838"
            value1=dataImportLead.primaryPhoneCountryCode
            value2=dataImportLead.primaryPhoneNumber
            required=false
            mandatory = true
            maxlength1=3
            maxlength2=10
            minlength2=10
            pattern1="^[+]?[0-9]{2}$"
            pattern2="^[0-9]{0,}$"
            dataError2="Enter valid phone number"
            dataError1="Enter valid country code"
            errorId = "primaryPhone"
        />
        <@generalInputSplitCol
            colId1="secondaryPhoneCountryCode"
            colId2="secondaryPhoneNumber"
            label="Office No"
            colPlaceholder1 = "+91"
            colPlaceholder2 = "4442147838"
            value1=dataImportLead.secondaryPhoneCountryCode
            value2=dataImportLead.secondaryPhoneNumber
            required=false
            mandatory = true
            maxlength1=3
            maxlength2=10
            minlength2=10
            pattern1="^[+]?[0-9]{2}$"
            pattern2="^[0-9]{0,}$"
            dataError2="Enter valid phone number"
            dataError1="Enter valid country code"
            errorId = "secondaryPhone"
        />
        <@generalInput
            id="emailAddress"
            label=uiLabelMap.emailAddress
            placeholder="example@company.com"
            value=dataImportLead.emailAddress
            pattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
            required=false
            mandatory = true
            maxlength=255
            inputType="email"
            dataError="Please enter valid email address"
        />
        <@generalInput
            id="address1"
            label=uiLabelMap.registeredAddress1
            mandatory = true
            maxlength=255
            placeholder=uiLabelMap.registeredAddress1
            value=dataImportLead.address1
            required=false
        />
        <@generalInput
            id="address2"
            label=uiLabelMap.address2
            maxlength=255
            placeholder=uiLabelMap.address2
            value=dataImportLead.address2
            required=false
        />
        
        <@generalInput
            id="postalCode"
            label=uiLabelMap.pinCode
            placeholder=uiLabelMap.pinCode
            value=dataImportLead.postalCode
            minlength =6
            maxlength=6
            required=false
            mandatory = true
            dataError="Should accept 6 digits and numbers only"
            pattern="^\\d{6}$"/>
        <@singleFormDropdownInput
            id="city"
            label=uiLabelMap.city
            required=false
            mandatory = true
            options=cityList
            value=dataImportLead.city
            colDivClass=""
            searchSelectClass="search form-control input-sm"
            allowEmpty=true
            dataLiveSearch=true
        />
        <@singleFormDropdownInput
            id="stateProvinceGeoId"
            label=uiLabelMap.state
            required=false
            mandatory = true
            value=dataImportLead.stateProvinceGeoId
            colDivClass=""
            searchSelectClass="search form-control input-sm"
            allowEmpty=true
            dataLiveSearch=true
        />
        
        <@singleFormDropdownInput 
            id="source"
            label=uiLabelMap.leadSource
            options=leadSourceList
            required=true
            value=dataImportLead.source
            colDivClass=""
            searchSelectClass="search form-control input-sm"
            allowEmpty=true
            dataLiveSearch=true
        />
        <#assign tcpNameParameters = { "type-id": "tcpName","name":"TCP Name"}>
        <@singleFormDropdownInput
            id="tcpName"
            label=uiLabelMap.tcpName
            options=tcpNameList
            required=false
            value=dataImportLead.tcpName
            allowEmpty=true
            dataLiveSearch=true
            colDivClass=""
             searchSelectClass="search form-control input-sm"
            lookup= Y
            lookupTarget = "enumerationLookup"
            hasPermission=hasEnumPermission
            lookupParams=tcpNameParameters
        />
     </div>
   </div>
   
   <div class="clearfix"></div>
   
   <div class="col-md-12 col-sm-12">
   <#-- <@fromActions showClearBtn=true /> -->
      <#if actionType?has_content && (actionType == "UPDATE" || actionType == "STAGING")>
              <@fromCommonAction showCancelBtn=true showClearBtn=false submitLabel="Update" cancelUrl="${backUrl!}" cancelLabel="Back" onclick="return onSubmitValidate(this);"/>
          <#else>
              <@fromCommonAction clearId="resetForm"  showCancelBtn=false showClearBtn=true submitLabel="Submit" onclick="javascript:return onSubmitValidate(this);"/>
      </#if>
   </div>
   
</form>
</div>
<div class="d-sm-block d-md-none d-lg-none d-xl-none">
<div class="row">
    <div class="col-md-12 col-sm-12">
        <form method="post" id="msform" action="<@ofbizUrl>leadShortFromMobile</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            <input type="hidden" name="leadId" value="${leadId!}">
            <input type="hidden" name="actionType" value="${actionType!}">
            <input type="hidden" name="backUrl" value="${backUrl!}">
            <input type="hidden" name="leadShortForm" value="Y">
            <input type="hidden" name="leadShortFromMobile" value="Y">
            <input type="hidden" id="isNotDuplicate" name="isNotDuplicate" value="">
            <div class="page-header" id="eitherOrFields1" style="display:none";>
    <#-- <h2 class="float-left">Basic Company Details</h2> -->
    <div class="float-right"> 
        <div class="help-block with-errors">
            <ul class="list-unstyled">
                <li>Mobile No OR Email Address OR <br/> City/State are mandatory.</li>
                
            </ul>
        </div>
    </div>
</div>

             <ul id="progressbar">
                <li <#if !partyId?has_content> class="active" </#if> >Company</li>
                <li>Contact</li>
                <li <#if partyId?has_content> class="active" </#if> >Lead Source</li>
            </ul>
            
           <#if !partyId?has_content> 
          <fieldSet> 
          <@mobileInput
             id="companyNames"
             name="companyName"
             label=uiLabelMap.companyName
             placeholder=uiLabelMap.companyName
             value=dataImportLead.companyName
             onkeyup = "loadConstituteM(this);"
             required=true
             maxlength=255
             pattern="^[ A-Za-z0-9'@.!&:*()+-]*$"
             dataError="Please enter valid name"
         />
         <#assign constitutionParameters = { "type-id": "constitution","name":"Constitution"}>
        <@mobileDropdownInput
            id="constitutions"
            name="constitution"
            label=uiLabelMap.Constitution
            options=constitutionList
            required=false
            value=dataImportLead.Constitution
            allowEmpty=true
            dataLiveSearch=true
            lookup = Y
            lookupTarget = "enumerationLookup"
            hasPermission=hasEnumPermission
            lookupParams=constitutionParameters
        />
        <@mobileDropdownInput
             id="citys"
             name="city"
             label=uiLabelMap.city
             required=false
             options=cityList
             value=dataImportLead.city
             allowEmpty=true
             mandatory = true
             dataLiveSearch=true
         />
         <@mobileDropdownInput
            id="stateProvinceGeoIds"
            name="stateProvinceGeoId"
            label=uiLabelMap.state
            required=false
            mandatory = true
            value=dataImportLead.stateProvinceGeoId
            allowEmpty=true
            dataLiveSearch=true
        />
        <button type="submit" id="nextCompanyName" class="btn btn-sm btn-primary float-right" value="button" onclick="return companyNameValidation();">Next</button>
        <button type="button" name="next" id="isCompanyName" class="next action-button btn btn-sm btn-primary float-right" value="button" style="display:none";>Next</button>
        </fieldset>
        <fieldset>
        <@mobileInput
             id="firstNames"
             name="firstName"
             onkeyup = "nextFirstName(this);"
             label=uiLabelMap.firstName
             placeholder=uiLabelMap.firstName
             value=dataImportLead.firstName
             required=true
             maxlength=100
             pattern="^[ A-Za-z0-9'@.!&:*()+-]*$"
             dataError="Please enter valid name"
         />
         <@mobileInput
             id="lastName"
             name="lastName"
             label=uiLabelMap.lastName
             placeholder=uiLabelMap.lastName
             value=dataImportLead.lastName
             required=false
             maxlength=100
             pattern="^[ A-Za-z0-9'@.!&:*()+-]*$"
             dataError="Please enter valid name"
         />
         <#assign designationParameters = { "type-id": "leadDesignation","name":"Designation"}>
        <@mobileDropdownInput
            id="designation2"
            name="designation"
            label=uiLabelMap.designation
            options=designationList
            required=false
            value=dataImportLead.designation
            allowEmpty=true
            dataLiveSearch=true
            lookup = Y
            lookupTarget = "enumerationLookup"
            hasPermission=hasEnumPermission
            lookupParams=designationParameters
        />
        <@mobileInputSplitCol
            colId1="primaryPhoneCountryCodes"
            colId2="primaryPhoneNumbers"
            colName1="primaryPhoneCountryCode"
            colName2="primaryPhoneNumber"
            label="Mobile No"
            colPlaceholder1 = "+91"
            colPlaceholder2 = "4442147838"
            value1=dataImportLead.primaryPhoneCountryCode
            value2=dataImportLead.primaryPhoneNumber
            required=false
            mandatory = true
            maxlength1=3
            maxlength2=10
            minlength2=10
            pattern1="^[+]?[0-9]{2}$"
            pattern2="^[0-9]{0,}$"
            dataError1="Enter valid country code"
            dataError2="Enter valid phone number"
            errorId = "primaryPhones"
        />
        <@mobileInput
            id="emailAddresss"
            name="emailAddress"
            label=uiLabelMap.emailAddress
            placeholder=uiLabelMap.emailAddress
            value=dataImportLead.emailAddress
            pattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
            required=false
            mandatory = true
            maxlength=255
            dataError="Enter valid email address"
            inputType="email"
            onkeyup="checkMail(this);"
        />
        <button type="button" name="previous" class="previous action-button-previous btn btn-sm btn-primary float-left" value="button">Back</button>
        <button type="submit" id="nextfirstName" class="btn btn-sm btn-primary float-right" value="button" onclick="return firstNameValidation();">Next</button>
        <button type="button" name="next" id="isfirstName" class="next action-button btn btn-sm btn-primary float-right" value="button" >Next</button>
         </fieldset>
        <fieldset>
        <@mobileDropdownInput 
            id="sources"
            name="source"
            label=uiLabelMap.leadSource
            options=leadSourceList
            required=true
            value=dataImportLead.source
            allowEmpty=true
            dataLiveSearch=true
        />
        <#assign tcpNameParameters = { "type-id": "tcpName","name":"TCP Name"}>
        <@mobileDropdownInput
            id="tcpNames"
            name="tcpName"
            label=uiLabelMap.tcpName
            options=tcpNameList
            required=false
            value=dataImportLead.tcpName
            allowEmpty=true
            dataLiveSearch=true
            lookup= Y
            lookupTarget = "enumerationLookup"
            hasPermission=hasEnumPermission
            lookupParams=tcpNameParameters
        />
        <button type="button" name="previous" class="previous action-button-previous btn btn-sm btn-primary float-left" value="button">Back</button>
        <button type="submit" id="nextSource" class="btn btn-sm btn-primary float-right" value="button" onclick="javascript:return nextToSubmit()";>Submit</button>
        <button type="submit" name="next" id="submitMobile" class="next action-button btn btn-sm btn-primary float-right" value="button" onclick="javascript:return onSubmitValidate1(this)";>Submit</button>
                
        </fieldset>
        </#if>
        <#if partyId?has_content>
        <fieldset>
          <h4 class="text-center"> Success! Your Lead ID <a href="<@ofbizUrl>viewLead?partyId=${partyId}</@ofbizUrl>">${partyId!}</a> is Created. </h4>
          <input type="button" name="submit" onclick="location.href='<@ofbizUrl>viewLead?partyId=${partyId}</@ofbizUrl>'"  class="submit action-button btn btn-sm btn-primary float-right" value="Ok"/>
       </fieldset>
       </#if>
        </form>
       
    </div>
</div>
</div>

<#if partyId?exists && partyId?has_content>
<div class="d-none d-md-block d-lg-block">
<div id="desktopPopup" class="modal fade mt-5" role="dialog" style="display: block; padding-right: 15px;" data-keyboard="false" data-backdrop="static">
   <div class="modal-dialog modal-sm">
      <!-- Modal content-->
      <div class="modal-content rounded-0 border-0">
         <div class="modal-body">
            <div class="text-center font-weight-bold  pt-0 pb-5"> Your lead is successfully created,<br> The lead ID is <span class="text-success">${partyId!}</span></div>
            <div class="row">
               <div class="col-md-3 offset-md-3 col-6"><a href="#" class="btn btn-sm btn-primary float-right">Create Task</a></div>
               <div class="col-6"><a href="/crm/control/updateLeadForm?partyId=${partyId!}" class="btn btn-sm btn-secondary">Edit Lead</a></div>
            </div>
            <div class="clearfix"> </div>
         </div>
      </div>
   </div>
</div>
</div>
</#if>

<script>

<#if partyId?exists && partyId?has_content>
   if($(window).width() > 770) {
      $('#desktopPopup').modal('show');
   }
</#if>

$(window).resize(function(){
   if ($( window ).width() <= 700){
        $("#msform").show();
       $("#singleFormDesk").hide();
       $("#nextCompanyName").show();
       $("#isCompanyName").hide();
       $("#nextfirstName").show();
       $("#isfirstName").hide();
       $("#submitMobile").hide();
    }
    else {
       $("#msform").hide();
       $("#singleFormDesk").show();
    }
});

var selectedCity;

$(document).ready(function(){

$("#msform").show();
$("#singleFormDesk").show();
if ($( window ).width() <= 700){
   $("#msform").show();
   $("#singleFormDesk").hide();
   $("#nextCompanyName").show();
   $("#isCompanyName").hide();
   $("#nextfirstName").show();
   $("#isfirstName").hide();
   $("#submitMobile").hide();
  var companyName = $("#companyNames").val();
  
}
else {
   $("#msform").hide();
   $("#singleFormDesk").show();
}
    
$("#city").change(function() {
	checkPostalCodeValidaty();
});

$('#postalCode').bind( "blur keyup", function( event ) {
	
	if ($('#postalCode').val().length == 0) {
		$("#postalCode_error").html('');
	}
	
	if ($('#postalCode').val().length == 6) {
		$.ajax({
			      
			type: "POST",
	     	url: "getPostalCodeDetail",
	        data:  {"postalCode": $("#postalCode").val()},
	        async: false,
	        success: function (data) {   
	            if (data.code == 200) {
	            	if (data.postalCodeDetail.postalCode) {
	            		$("#postalCode_error").html('');
	            		selectedCity = data.postalCodeDetail.city;
	            		$("#city").val(data.postalCodeDetail.city).change();
	            	} else {
	            	
	            		$("#postalCode_error").html('<ul class="list-unstyled"><li>Invalid PIN Code</li></ul>'); 
	            		$("#postalCode_error").closest('.form-group').addClass("has-error has-danger");
	            	
						$('#city').dropdown("clear");	 
						$("#stateProvinceGeoId").html( "" );    
						$('#stateProvinceGeoId').dropdown('clear');  
						selectedCity = null;	
	            	}
	            }
	        }
	        
		});    
	}
	
});    
    
});

function checkPostalCodeValidaty() {
	if ($('#postalCode').val() && $('#city').val()) {
		$.ajax({
			      
			type: "POST",
	     	url: "getPostalCodeDetail",
	        data:  {"postalCode": $("#postalCode").val(), "geoId": $("#city").val()},
	        async: false,
	        success: function (data) {   
	            if (data.code == 200) {
	            	if (data.postalCodeDetail.postalCode) {
	            		
	            	} else {
						$('#postalCode').val("");
						$('#postalCode_error').html("");
	            	}
	            }
	        }
	        
		});    
	}
}

$("#source").change(function() {
    var source = $("#source").val();
    var tcpName = $("#tcpName").val();
    $("#source_error").html("");
    if(source ==""  && tcpName !=""){
      $("#source_error").html("");
      $("#tcpName").dropdown('clear'); 
    }
   if(source !=''){
      $("#errorsource").html("");
      $("#source_error").html("");
      $("#source_error").css('display','none');
      $("#errorsource").empty();
      if ( "TCP" == source || "TEV" == source ) {
        console.log("----source---"+$("#source").val());
        if(tcpName == ''){
          $("#tcpName_error").html("");
          $("#tcpName_error").append('<ul class="list-unstyled text-danger"><li id="errorTcpName">TCP Name should not be empty</li></ul>');
        }else{
          $("#tcpName_error").html("");
        }
      }else{
        $("#tcpName_error").html("");
        $("#source_error").html("");
        $("#tcpName").dropdown('clear'); 
        $("#source_error").css('display','none');
      }
    }else{
      $("#tcpName_error").html("");
      $(".ui.search.dropdown.active > input.search, .ui.search.dropdown.visible > input.search").val('')
      $("#errorsource").html("");
      $("#source_error").append('<ul class="list-unstyled text-danger"><li id="errorsource">Please select an item in the list.</li></ul>');
    }
    if(source ==''){
    $("#source_error").html("");
    $("#source_error").append('<ul class="list-unstyled text-danger"><li id="errorsource">Please select an item in the list.</li></ul>');
    $("#source_error").css('display','block');
    }
    if(source !=''){
      $("#errorsource").html("");
      $("#source_error").html("");
      $("#source_error").css('display','none');
    }
});
$("#city").change(function() {
    loadState();
});
$("#tcpName").change(function() {
    var source = $("#source").val();
    var tcpName = $("#tcpName").val();
    if(source !='' && ( "TCP" == source || "TEV" == source )){
      if(tcpName == ''){
          $("#tcpName_error").html("");
          $("#tcpName_error").append('<ul class="list-unstyled text-danger"><li id="errorTcpName">TCP Name should not be empty</li></ul>');
      }else{
        $("#tcpName_error").html("");
      }
    }else{
        $("#tcpName_error").html("");
      }
});
function loadState() {
    if ( $("#city").val() ) {
        $.ajax({
        
            type: "POST",
            url: "getGeoAssocState",
            data:  {"geoIdTo": $("#city").val(), "geoAssocTypeId": "COUNTY_CITY"},
            async: false,
            success: function (data) {   
              if (data.code == 200) {
                  for (var i = 0; i < data.results.length; i++) {
                      $('.search').val('');
                      var result = data.results[i]; 
                      var groupNameOptions = '<option value="'+result.geoId+'" selected="selected">'+result.geoName+'</option>';
                      $("#stateProvinceGeoId").html( groupNameOptions );
                  }
              }
            }
            
        });
        //$('#stateProvinceGeoId').dropdown('refresh');
    }else{
        var groupNameOptions = '<option value="">Please Select</option>';
        $("#stateProvinceGeoId").html( groupNameOptions );
        $('#stateProvinceGeoId').dropdown('clear');
    }
}

$("#sources").change(function() {
    var sources = $("#sources").val();
    if(sources !=''){
    if ( "TCP" == $("#sources").val() || "TEV" == $("#sources").val() ) {
        console.log("----source---"+$("#sources").val());
        $("#errorTcpName1").html("");
        $("#tcpNames_error").html("");
        $("#tcpNames_error").append('<ul class="list-unstyled text-danger"><li id="errorTcpName1">TCP Name should not be empty</li></ul>');
    }else{
      $("#tcpNames_error").html("");
      $("#nextSource").hide();
      $("#submitMobile").show();
    }
      
    }else{
     $("#submitMobile").hide();
     $("#nextSource").show();
     return false;
      
    }
    if ( "TCP" == $("#sources").val() || "TEV" == $("#sources").val() ) {
        console.log("----source---"+$("#sources").val());
        $("#errorTcpName1").html("");
        $("#tcpNames_error").append('<ul class="list-unstyled text-danger"><li id="errorTcpName1">TCP Name should not be empty</li></ul>');
    }else{
      $("#tcpNames_error").html("");
    }
    var primaryPhoneCountryCode =  $("#primaryPhoneCountryCodes").val();
    var primaryPhoneNumber =  $("#primaryPhoneNumbers").val();
    var emailAddress = $("#emailAddresss").val();
    var stateProvinceGeoId = $("#stateProvinceGeoIds").val();
    var city = $("#citys").val();
   /*
   if(primaryPhoneNumber !='' || emailAddress !='' || (address1 !='' && city !='' && postalCode !='') ){
         $("#eitherOrFields1").css("display","none");
         $("#submitMobile").show();
     }else{
         $("#submitMobile").hide();
         $("#nextSource").show();
         return false;
     }
    */
});
$("#citys").change(function() {
    loadState1();
    var firstName =  $("#firstNames").val();
  var phoneNumbers = $("#primaryPhoneNumbers").val();
  var emailAddress = $("#emailAddresss").val();
  var city = $("#citys").val();
  var state = $("#stateProvinceGeoIds").val();
  if(firstName !=''){
   if(phoneNumbers != '' || emailAddress != '' || city != '' || state != ''){
        $("#eitherOrFields1").css("display","none");
        $("#nextfirstName").hide();
        $("#isfirstName").show();
   }
  }
});

$("#tcpNames").change(function() {
    $("#tcpNames_error").html("");
});
function loadState1() {
    if ( $("#citys").val() ) {
        $.ajax({
        
            type: "POST",
            url: "getGeoAssocState",
            data:  {"geoIdTo": $("#citys").val(), "geoAssocTypeId": "COUNTY_CITY"},
            async: false,
            success: function (data) {   
              if (data.code == 200) {
                  for (var i = 0; i < data.results.length; i++) {
                      var result = data.results[i];
                      $('.search').val('');
                      var groupNameOptions = '<option value="'+result.geoId+'" selected="selected">'+result.geoName+'</option>';
                      $("#stateProvinceGeoIds").html( groupNameOptions );
                  }
              }
            }
            
        });
        //$('#stateProvinceGeoIds').dropdown('refresh');
    }
}

$("#resetForm").click(function(){
   $("#source_error").empty();
   $("#source_error").html("");
   $("#primaryPhone_error").empty();
   $("#secondaryPhone_error").empty();
   $('#singleFormDesk').trigger("reset");
   $("#primaryPhone_error").empty();
   $("#secondaryPhone_error").empty();
   $("#source_error").html("");
   $("#tcpName_error").empty();
});
$( "#primaryPhoneCountryCode" ).blur(function() {
    var primaryPhoneCountryCode =  $("#primaryPhoneCountryCode").val();
    var primaryPhoneNumber =  $("#primaryPhoneNumber").val();
    if(primaryPhoneCountryCode !='' && primaryPhoneNumber == ''){
       $("#primaryPhone_error").html('');
       $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="primary_error">Please enter phone number</li></ul>');
    }
    if(primaryPhoneCountryCode =='' && primaryPhoneNumber != ''){
       $("#primaryPhone_error").html('');
       $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="primary_error">Please enter country code</li></ul>');
    }
    if(primaryPhoneCountryCode =='' && primaryPhoneNumber == ''){
       $("#primaryPhone_error").html('');
    }
});
$( "#primaryPhoneNumber" ).blur(function() {
    var primaryPhoneCountryCode =  $("#primaryPhoneCountryCode").val();
    var primaryPhoneNumber =  $("#primaryPhoneNumber").val();
    if(primaryPhoneCountryCode =='' && primaryPhoneNumber != ''){
       $("#primaryPhone_error").html('');
       $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="primary_error">Please enter country code</li></ul>');
    }
    if(primaryPhoneCountryCode !='' && primaryPhoneNumber == ''){
       $("#primaryPhone_error").html('');
       $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="primary_error">Please enter phone number</li></ul>');
    }
    if(primaryPhoneCountryCode =='' && primaryPhoneNumber == ''){
       $("#primaryPhone_error").html('');
    }
});
$( "#secondaryPhoneCountryCode" ).blur(function() {
    var secondaryPhoneCountryCode =  $("#secondaryPhoneCountryCode").val();
    var secondaryPhoneNumber =  $("#secondaryPhoneNumber").val();
    if(secondaryPhoneCountryCode =='' && secondaryPhoneNumber != ''){
       $("#secondaryPhone_error").empty();
       $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Please enter country code</li></ul>');
    }
    if(secondaryPhoneCountryCode !='' && secondaryPhoneNumber == ''){
       $("#secondaryPhone_error").empty();
       $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');
    }
    if(secondaryPhoneCountryCode =='' && secondaryPhoneNumber == ''){
       $("#secondaryPhone_error").empty();
    }
});
$( "#secondaryPhoneNumber" ).blur(function() {
    var secondaryPhoneCountryCode =  $("#secondaryPhoneCountryCode").val();
    var secondaryPhoneNumber =  $("#secondaryPhoneNumber").val();
    if(secondaryPhoneCountryCode !='' && secondaryPhoneNumber == ''){
       $("#secondaryPhone_error").empty();
       $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');
    }
    if(secondaryPhoneCountryCode =='' && secondaryPhoneNumber != ''){
       $("#secondaryPhone_error").empty();
       $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Please enter country code</li></ul>');
    }
    if(secondaryPhoneCountryCode =='' && secondaryPhoneNumber == ''){
       $("#secondaryPhone_error").empty();
    }
});
function onSubmitValidate(){
   var primaryPhoneCountryCode =  $("#primaryPhoneCountryCode").val();
   var secondaryPhoneCountryCode =  $("#secondaryPhoneCountryCode").val();
   var primaryPhoneNumber =  $("#primaryPhoneNumber").val();
   var secondaryPhoneNumber =  $("#secondaryPhoneNumber").val();
   var emailAddress = $("#emailAddress").val();
   var address1 = $("#address1").val();
   var stateProvinceGeoId = $("#stateProvinceGeoId").val();
   var city = $("#city").val();
   var postalCode = $("#postalCode").val();
   var countryCodeErr = $("#primaryPhone_error").html();
   var countryCodeErr1 =$("#secondaryPhone_error").html();
   var companyName =  $("#companyName").val();
   var firstName =  $("#firstName").val();
   var source =  $("#source").val();
   var tcpName =  $("#tcpName").val();
   if(primaryPhoneCountryCode !='' || secondaryPhoneCountryCode !=''){
     if(primaryPhoneCountryCode !='' && primaryPhoneNumber ==""){
       $("#primaryPhone_error").html('');
       $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="primary_error">Please enter phone number</li></ul>');
       primaryFlag="Y";
     }else{
       if(countryCodeErr !=''){
        $("#primary_error").html("");
        $("#primaryPhone_error").html("");
        $("#primaryPhone_error").append(countryCodeErr);
        primaryFlag="Y";
        return false;
      }else{
       $("#primaryPhone_error").html('');
       $("#primaryPhone_error").attr("data-error", "");
       primaryFlag="N";
       }
     }
     if(secondaryPhoneCountryCode !='' && secondaryPhoneNumber ==""){
       $("#secondaryPhone_error").html('');
       $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="secondary_error">Please enter phone number</li></ul>');
       secondaryFlag="Y";
     }else{
     if(countryCodeErr1 !=''){
       $("#secondary_error").html("");
        $("#secondaryPhone_error").html("");
        $("#secondaryPhone_error").append(countryCodeErr1);
        secondaryFlag="Y";
        return false;
     }else{
       $("#secondaryPhone_error").html('');
       $("#primaryPhone_error").attr("data-error", "");
       secondaryFlag="N";
     }
     }
     if( primaryFlag=="Y" || secondaryFlag=="Y"){ 
       return false;
     }else{
      if(countryCodeErr !=''){
        $("#primaryPhone_error").append(countryCodeErr);
        return false;
      }else{
       $("#primaryPhone_error").html('');
       $("#secondaryPhone_error").html('');
       //return true;
      }
     } 
  }
  if(primaryPhoneNumber !='' || secondaryPhoneNumber !=''){
     if(primaryPhoneCountryCode =="" && primaryPhoneNumber !=''){
       $("#primaryPhone_error").html('');
       $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="primary_error">Please enter country code</li></ul>');
       primaryFlag="Y";
     }else{
       $("#primaryPhone_error").html('');
       $("#primaryPhone_error").attr("data-error", "");
       primaryFlag="N";
     }
     if(secondaryPhoneCountryCode =="" && secondaryPhoneNumber !=''){
       $("#secondaryPhone_error").html('');
       $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="secondary_error">Please enter country code</li></ul>');
       secondaryFlag="Y";
     }else{
       $("#secondaryPhone_error").html('');
       $("#primaryPhone_error").attr("data-error", "");
       secondaryFlag="N";
     }
     if( primaryFlag=="Y" || secondaryFlag=="Y"){ 
       return false;
     }else{
       $("#primaryPhone_error").html('');
       $("#secondaryPhone_error").html('');
       //return true;
     } 
  }
   if(tcpName ==''){
     if ( "TCP" == source || "TEV" == source ) {
        console.log("----source---"+$("#source").val());
        $("#errorTcpName").html("");
        $("#tcpName_error").html("");
        $("#tcpName_error").append('<ul class="list-unstyled text-danger"><li id="errorTcpName">TCP Name should not be empty</li></ul>');
        return false;
    }
   }
   
   if(companyName !='' && firstName !='' && source !='' ){
    if( primaryPhoneNumber !='' || secondaryPhoneNumber !='' || emailAddress !='' ) {
        if (address1 !='' && city !='' && postalCode !='') {
          $("#eitherOrFields").css("display","none");
        }else{
          $("#eitherOrFields").css("display","block");
       }
       //DND Validation
       var result = true;
       $.ajax({
          type: "POST",
          url: "dndPhoneNumberValidation",
          data:  {"primaryPhoneNumber": primaryPhoneNumber, "secondaryPhoneNumber": secondaryPhoneNumber},
          async: false,
          success: function (data) {
              var dndPrimaryPhoneStatus = data.dndPrimaryPhoneStatus;
              var dndSecondaryPhoneStatus = data.dndSecondaryPhoneStatus;
              if(dndPrimaryPhoneStatus != null && dndPrimaryPhoneStatus == "Y" && dndSecondaryPhoneStatus != null && dndSecondaryPhoneStatus == "Y") {
                 result = confirm("Both Mobile Number and Office Number in the DND List");
              } else if(dndPrimaryPhoneStatus != null && dndPrimaryPhoneStatus == "Y") {
                 result = confirm("Mobile Number in the DND List");
              }  else if(dndSecondaryPhoneStatus != null && dndSecondaryPhoneStatus == "Y") {
                 result = confirm("Office Number in the DND List");
              }
          }
        });
        if(!result) {
           return false;
        }
     }else{
      if ((address1 !='' && city !='' && postalCode !='') ) {
         $("#eitherOrFields").css("display","none");
      }else{
        $("#eitherOrFields").css("display","block");
        return false;
       }
     }
   }else{
     if(source =='' ){
       $("#errorsource").html("");
       $("#source_error").html("");
       $("#source_error").append('<ul class="list-unstyled text-danger"><li id="errorsource">Please select an item in the list.</li></ul>');
       $("#source_error").css("display","block");
     }
   }
   
  
}
$("#primaryPhoneCountryCodes").keyup(function (e){  
  var countryCode = $("#primaryPhoneCountryCodes").val();
  var phoneNumber = $("#primaryPhoneNumbers").val();
  var firstName = $("#firstNames").val();
  if(countryCode !='' ) {
  var re = /^[+]?[0-9]{2}$/;
            if (countryCode.match(re)) {
                $("#primaryPhones_error").html("");
            }else{
                $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li>Enter valid country code.</li></ul>');
            }
 
       //$("#primaryPhones_error").html("");
  }
  if(phoneNumber == '' || phoneNumber == null) {
     $("#primaryPhones_error").empty();
     $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');
     $("#primaryPhones_error").show();
    }else{
       var countryCodeErr = $("#primaryPhones_error").html();
       if (countryCode.match(re)) {
          $("#primaryPhones_error").html("");
          $("#nextfirstName").hide();
          $("#isfirstName").show();
       } else {
           $("#primaryPhones_error").html("");
         //  $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="primaryPhones_error">Enter valid country code.</li></ul>');
           $("#nextfirstName").show();
           $("#isfirstName").hide();
       }
   }
   if(countryCode == '') {
      $("#primaryPhones_error").empty();
      $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Please enter country code</li></ul>');
      $("#nextfirstName").show();
      $("#isfirstName").hide();
   }
   if(phoneNumber == '') {
       $("#primaryPhones_error").empty();
       $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');
       $("#nextfirstName").show();
       $("#isfirstName").hide();
  }
  if(phoneNumber.length == 10 && countryCode != '' && firstNames !='') {
      $("#primaryPhones_error").empty();
      $("#nextfirstName").hide();
      $("#isfirstName").show();
  }
  
});
$("#primaryPhoneCountryCode").keyup(function (e){  
  var countryCode = $("#primaryPhoneCountryCode").val();
  var phoneNumber = $("#primaryPhoneNumber").val();
  var re = /^[+]?[0-9]{2}$/;
  if(countryCode !='' ) {
    if (countryCode.match(re)) {
       $("#primaryPhone_error").empty();
      if(phoneNumber == '') {
        $("#primaryPhone_error").empty();
        $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');
      }
    } /*else {
       $("#primaryPhone_error").empty();
       alert('sdfsf'+countryCode)
       $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Enter valid country code</li></ul>');
     }*/
  }
  if(countryCode =='' && phoneNumber != '') {
       $("#primaryPhone_error").empty();
       $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Please enter country code</li></ul>');
  }
  if(countryCode =='' && phoneNumber == '') {
  $("#primaryPhone_error").empty();
  }
  if (re.test(countryCode) && phoneNumber !='') {
      $("#primaryPhone_error").empty();
  }

  if(phoneNumber.length > 10 && countryCode !='') {
        $("#primaryPhone_error").empty();
  }
  
});
$("#primaryPhoneNumber").keyup(function (e){  
  var countryCode = $("#primaryPhoneCountryCode").val();
  var phoneNumber = $("#primaryPhoneNumber").val();
  
  if(phoneNumber !=''){
      var re = new RegExp("^[0-9]{0,}$");
      if (re.test(phoneNumber)) {
           $("#primaryPhone_error").empty();
           if(countryCode =='' && phoneNumber.length ==10){
              $("#primaryPhone_error").html("");
              $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Please enter country code.</li></ul>');
           }else{
             $("#primaryPhone_error").html("");
           }
      } else {
           $("#primaryPhone_error").empty();
           $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Enter valid phone number.</li></ul>');
      }
  }
  if(phoneNumber == '' && countryCode !='') {
       $("#primaryPhone_error").empty();
       $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');
  }
  if(phoneNumber != '' && countryCode !='') {
    $("#primaryPhone_error").html("");
  }
  if(phoneNumber == '' && countryCode =='') {
    $("#primaryPhone_error").html("");
  }
  if(phoneNumber.length > 10) {
        $("#primaryPhone_error").html("");
  }
});
$("#secondaryPhoneCountryCode").keyup(function (e){  
  var countryCode = $("#secondaryPhoneCountryCode").val();
  var phoneNumber = $("#secondaryPhoneNumber").val();
  var re = /^[+]?[0-9]{2}$/;
  if(countryCode !='' ) {
    if (countryCode.match(re)) {
      $("#secondaryPhone_error").empty();
      if(phoneNumber == '') {
          $("#secondaryPhone_error").empty();
          $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');
      }else{
        $("#secondaryPhone_error").empty();
      }
    } /*else {
       $("#secondaryPhone_error").empty();
       $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Please enter country code.</li></ul>');
     }*/
  }
  if(countryCode =='' && phoneNumber != '') {
       $("#secondaryPhone_error").empty();
       $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Please enter country code</li></ul>');
  }
  if(countryCode =='' && phoneNumber == '') {
    $("#secondaryPhone_error").empty();
  }
  if (countryCode.match(re) && phoneNumber !='') {
      $("#secondaryPhone_error").empty();
  }
  if(phoneNumber.length > 10 && countryCode !='') {
        $("#secondaryPhone_error").empty();
  }
});
$("#secondaryPhoneNumber").keyup(function (e){  
  var countryCode = $("#secondaryPhoneCountryCode").val();
  var phoneNumber = $("#secondaryPhoneNumber").val();
  
  if(phoneNumber !=''){
      //$("#secondaryPhone_error").empty();
       var re = new RegExp("^[0-9]{0,}$");
      if (re.test(phoneNumber)) {
           $("#secondaryPhone_error").html("");
           if(countryCode =='' && phoneNumber.length ==10){
            $("#secondaryPhone_error").html("");
            $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Please enter country code.</li></ul>');
           }else{
             $("#secondaryPhone_error").html("");
           }
      } else {
           $("#secondaryPhone_error").empty();
           $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Enter valid phone number.</li></ul>');
      }
      
  }
  if(phoneNumber == '' && countryCode !='') {
       $("#secondaryPhone_error").empty();
       if(countryCode !=''){
           $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');  
       }else{
           $("#secondaryPhone_error").empty();
       }
  }
  if(phoneNumber != '' && countryCode !='') {
    $("#secondaryPhone_error").html("");
  }
  if(phoneNumber.length > 10) {
        $("#secondaryPhone_error").html("");
  }
});
function onSubmitValidate1(){
   var primaryPhoneCountryCode =  $("#primaryPhoneCountryCodes").val();
   var primaryPhoneNumber =  $("#primaryPhoneNumbers").val();
   var emailAddress = $("#emailAddresss").val();
   var address1 = $("#addresss1").val();
   var stateProvinceGeoId = $("#stateProvinceGeoIds").val();
   var city = $("#citys").val();
   var postalCode = $("#postalCodes").val();
   var source =  $("#sources").val();
   var tcpName =  $("#tcpNames").val();
   if(primaryPhoneCountryCode !=''){
     if(primaryPhoneCountryCode !='' && primaryPhoneNumber ==""){
       $("#primaryPhones_error").html('');
       $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="primary_error">Please enter phone number</li></ul>');
       primaryFlag="Y";
     }else{
       $("#primaryPhone_error").html('');
       $("#primaryPhone_error").attr("data-error", "");
       primaryFlag="N";
     }
     
     if( primaryFlag=="Y"){ 
       return false;
     }else{
       $("#primaryPhone_error").html('');
       $("#secondaryPhone_error").html('');
       //return true;
     } 
  }
  var source =  $("#sources").val();
  if(source == ''){
    $("#error_Sources").html("");
    $("#error_Sources").html("");
    $("#sources_error").html('');
    $("#sources_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Please select an item in the list.</li></ul>');
    return false;
  }else{
    $("#sources_error").html("");
  }
   if(tcpName ==''){
     if(tcpName ==''){
       if ( "TCP" == source || "TEV" == source ) {
        console.log("----source---"+$("#source").val());
        $("#errorTcpName1").html("");
        $("#tcpNames_error").html("");
        $("#tcpNames_error").append('<ul class="list-unstyled text-danger"><li id="errorTcpName1">TCP Name should not be empty</li></ul>');
        return false;
      }
     }
   }/*
   if(primaryPhoneNumber !='' || emailAddress !='' || (stateProvinceGeoId !='' && city !='') ){
     if( primaryPhoneNumber !='' || emailAddress !='' ) {
        if (stateProvinceGeoId !='' && city !='') {
          $("#eitherOrFields1").css("display","none");
        }else{
          $("#eitherOrFields1").css("display","block");
          //return false;
       }
     }else{
       if ((stateProvinceGeoId !='' && city !='' ) ) {
         $("#eitherOrFields1").css("display","none");
      }else{
         $("#eitherOrFields1").css("display","block");
         return false;
      }
    }
   }else{
         //alert('fill mandatory fields');
          $("#eitherOrFields1").css("display","block");
          return false;
       // return true;
     }*/
     if(primaryPhoneNumber !='' || emailAddress !='' || (stateProvinceGeoId !='' && city !='') ){
       return true;
     }
}
function loadConstitute(element){
  var companyName=element.value;
  var result = companyName.match(/Pte/i);
  var constitutionOption = '';
  if(companyName.match(/Pte/i) || companyName.match(/pvt/i) || companyName.match(/private/i) || companyName.match(/Private Limited/i) ||
   companyName.match(/Pvt. Ltd./i) ||  companyName.match(/Pte. LTd./i) || companyName.match(/PVT LTD/i) || companyName.match(/Pvtltd/i) || 
   companyName.match(/Pvt. Ltd/i) || companyName.match(/Pvt. Ltd/i) || companyName.match(/PVT  LTD/i) || companyName.match(/PVT LIMITED/i) ||
   companyName.match(/(Pvt) Ltd/i) || companyName.match(/Pvt/i) || companyName.match(/Pte Ltd/i) || companyName.match(/PRIVATE LTD/i) ||
   companyName.match(/P LTD/i) || companyName.match(/(P) LTD/i) || companyName.match(/limited/i) || companyName.match(/ltd/i) ||
   companyName.match(/Pte Ltd/i) || companyName.match(/Pvt. L/i) || companyName.match(/Pvt. Lt/i) || companyName.match(/Private Lim/i) ||
   companyName.match(/Pvt Lt/i) || companyName.match(/P. L/i) || companyName.match(/PVT.  LIMITED/i) || companyName.match(/ PL /i)
  ) {
      console.log('companyName is =='+companyName);
    //  var constitutionOption = '<option value="DBS_CONST_1006" selected="selected">Private Corporation</option>';
    // $("#constitution").html( constitutionOption );
      $.ajax({
        type: "POST",
        url: "getConstituteList",
	        data:  {"enumTypeId": "DBS_CONSTITUTION"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            	for (var i = 0; i < data.results.length; i++) {
	            		var result = data.results[i];
	            		var enumId = result.enumId ;
	            		console.log("==enumId===="+enumId);
	            		if("DBS_CONST_1006"==enumId){
	            		  constitutionOption += '<option value="'+result.enumId+'"  selected="selected"  >'+result.description+'</option>';
	            		}else{
	            		  constitutionOption += '<option value="'+result.enumId+'" >'+result.description+'</option>';
	            		}
	            	}
	            	console.log("==constitutionOption===="+constitutionOption);
	            	$("#constitution").html("");
	            	$("#constitution").html( constitutionOption );
	            }
				    	
	        }
	        
		});
  }
  if(companyName.match(/public/i) || companyName.match(/plc/i)) {
      console.log('companyName is =='+companyName);
    //  var constitutionOption = '<option value="DBS_CONST_1006" selected="selected">Private Corporation</option>';
    // $("#constitution").html( constitutionOption );
      $.ajax({
        type: "POST",
        url: "getConstituteList",
	        data:  {"enumTypeId": "DBS_CONSTITUTION"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            	for (var i = 0; i < data.results.length; i++) {
	            		var result = data.results[i];
	            		var enumId = result.enumId ;
	            		console.log("==enumId===="+enumId);
	            		if("DBS_CONST_1007"==enumId){
	            		  constitutionOption += '<option value="'+result.enumId+'"  selected="selected"  >'+result.description+'</option>';
	            		}else{
	            		  constitutionOption += '<option value="'+result.enumId+'" >'+result.description+'</option>';
	            		}
	            	}
	            	console.log("==constitutionOption===="+constitutionOption);
	            	$("#constitution").html("");
	            	$("#constitution").html( constitutionOption );
	            }
				    	
	        }
	        
		});
    }
    if(companyName.match(/LIMITED LIABILITY PARTNERSHIP/i) || companyName.match(/LLP/i) || companyName.match(/Limited LP/i) || companyName.match(/L.P./i) ||
       companyName.match(/L. P./i) ||  companyName.match(/L P/i) || companyName.match(/L L P/i) || companyName.match(/L. L P/i) || 
       companyName.match(/ LP /i) ) {
      console.log('companyName is =='+companyName);
    //  var constitutionOption = '<option value="DBS_CONST_1004" selected="selected">Private Corporation</option>';
    // $("#constitution").html( constitutionOption );
      $.ajax({
        type: "POST",
        url: "getConstituteList",
	        data:  {"enumTypeId": "DBS_CONSTITUTION"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            	for (var i = 0; i < data.results.length; i++) {
	            		var result = data.results[i];
	            		var enumId = result.enumId ;
	            		console.log("==enumId===="+enumId);
	            		if("DBS_CONST_1004"==enumId){
	            		  constitutionOption += '<option value="'+result.enumId+'"  selected="selected"  >'+result.description+'</option>';
	            		}else{
	            		  constitutionOption += '<option value="'+result.enumId+'" >'+result.description+'</option>';
	            		}
	            	}
	            	console.log("==constitutionOption===="+constitutionOption);
	            	$("#constitution").html("");
	            	$("#constitution").html( constitutionOption );
	            }
				    	
	        }
	        
		});
  }
  if(companyName.match(/EMBASSY/i) || companyName.match(/HIGH COMMISSION/i) || companyName.match(/CONSULATE/i) ) {
      console.log('companyName is =='+companyName);
    //  var constitutionOption = '<option value="DBS_CONST_1001" selected="selected">Private Corporation</option>';
    // $("#constitution").html( constitutionOption );
      $.ajax({
        type: "POST",
        url: "getConstituteList",
	        data:  {"enumTypeId": "DBS_CONSTITUTION"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            	for (var i = 0; i < data.results.length; i++) {
	            		var result = data.results[i];
	            		var enumId = result.enumId ;
	            		console.log("==enumId===="+enumId);
	            		if("DBS_CONST_1001"==enumId){
	            		  constitutionOption += '<option value="'+result.enumId+'"  selected="selected"  >'+result.description+'</option>';
	            		}else{
	            		  constitutionOption += '<option value="'+result.enumId+'" >'+result.description+'</option>';
	            		}
	            	}
	            	console.log("==constitutionOption===="+constitutionOption);
	            	$("#constitution").html("");
	            	$("#constitution").html( constitutionOption );
	            }
				    	
	        }
	        
		});
  }
  if(companyName.match(/HUF/i) ) {
      console.log('companyName is =='+companyName);
    //  var constitutionOption = '<option value="DBS_CONST_1002" selected="selected">Private Corporation</option>';
    // $("#constitution").html( constitutionOption );
      $.ajax({
        type: "POST",
        url: "getConstituteList",
	        data:  {"enumTypeId": "DBS_CONSTITUTION"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            	for (var i = 0; i < data.results.length; i++) {
	            		var result = data.results[i];
	            		var enumId = result.enumId ;
	            		console.log("==enumId===="+enumId);
	            		if("DBS_CONST_1002"==enumId){
	            		  constitutionOption += '<option value="'+result.enumId+'"  selected="selected"  >'+result.description+'</option>';
	            		}else{
	            		  constitutionOption += '<option value="'+result.enumId+'" >'+result.description+'</option>';
	            		}
	            	}
	            	console.log("==constitutionOption===="+constitutionOption);
	            	$("#constitution").html("");
	            	$("#constitution").html( constitutionOption );
	            }
				    	
	        }
	        
		});
  }
  if(companyName.match(/ENTERPRISES/i) || companyName.match(/ENTERPRISE/i) || companyName.match(/Traders/i) || companyName.match(/ASSOCIATES/i) ) {
      console.log('companyName is =='+companyName);
    //  var constitutionOption = '<option value="DBS_CONST_1012" selected="selected">Private Corporation</option>';
    // $("#constitution").html( constitutionOption );
      $.ajax({
        type: "POST",
        url: "getConstituteList",
	        data:  {"enumTypeId": "DBS_CONSTITUTION"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            	for (var i = 0; i < data.results.length; i++) {
	            		var result = data.results[i];
	            		var enumId = result.enumId ;
	            		console.log("==enumId===="+enumId);
	            		if("DBS_CONST_1012"==enumId){
	            		  constitutionOption += '<option value="'+result.enumId+'"  selected="selected"  >'+result.description+'</option>';
	            		}else{
	            		  constitutionOption += '<option value="'+result.enumId+'" >'+result.description+'</option>';
	            		}
	            	}
	            	console.log("==constitutionOption===="+constitutionOption);
	            	$("#constitution").html("");
	            	$("#constitution").html( constitutionOption );
	            }
				    	
	        }
	        
		});
  }
  if(companyName.match(/Pte/i) || companyName.match(/pvt/i) || companyName.match(/private/i) || companyName.match(/Private Limited/i) ||
   companyName.match(/Pvt. Ltd./i) ||  companyName.match(/Pte. LTd./i) || companyName.match(/PVT LTD/i) || companyName.match(/Pvtltd/i) || 
   companyName.match(/Pvt. Ltd/i) || companyName.match(/Pvt. Ltd/i) || companyName.match(/PVT  LTD/i) || companyName.match(/PVT LIMITED/i)  ||
   companyName.match(/(Pvt) Ltd/i) || companyName.match(/Pvt/i) || companyName.match(/Pte Ltd/i) || companyName.match(/PRIVATE LTD/i) ||
   companyName.match(/P LTD/i) || companyName.match(/(P) LTD/i) || companyName.match(/limited/i) || companyName.match(/ltd/i) ||
   companyName.match(/Pte Ltd/i) || companyName.match(/Pvt. L/i) || companyName.match(/Pvt. Lt/i) || companyName.match(/Private Lim/i) ||
   companyName.match(/Pvt Lt/i) || companyName.match(/P. L/i) || companyName.match(/PVT.  LIMITED/i) || companyName.match(/PL/i) ||
   companyName.match(/public/i) || companyName.match(/plc/i) || companyName.match(/LIMITED LIABILITY PARTNERSHIP/i) || companyName.match(/LLP/i) || 
   companyName.match(/Limited LP/i) || companyName.match(/L.P./i) ||
   companyName.match(/L. P./i) ||  companyName.match(/L P/i) || companyName.match(/L L P/i) || companyName.match(/L. L P/i) || 
   companyName.match(/LP/i) || companyName.match(/EMBASSY/i) || companyName.match(/HIGH COMMISSION/i) || companyName.match(/CONSULATE/i) ||
    companyName.match(/HUF/i) || companyName.match(/ENTERPRISES/i) || companyName.match(/ENTERPRISE/i) || companyName.match(/Traders/i) || companyName.match(/ASSOCIATES/i)
  ) {
  }else{
    $('#constitution').dropdown('clear'); 
  }
}
function companyNameValidation(){
  var companyNamesErr = $("#companyNames_error li").val();
  
  var companyName =  $("#companyNames").val();
  if(companyName == ''){
   if(companyNamesErr == "0"){
      $("#nextCompanyName").show();
      $("#isCompanyName").hide();
      return true;
    }else{
    $("#companyNames_error").html("");
    $("#companyNames_error").html('');
    $("#companyNames_error").append('<ul class="list-unstyled text-danger"><li id="error_companyNames">Please enter valid name</li></ul>');
    
      $("#nextCompanyName").show();
      $("#isCompanyName").hide();
      return false;
    }
  }
}
function firstNameValidation(){
  var firstNameErr = $("#firstNames_error li").val();
  
  var firstName =  $("#firstNames").val();
  var phoneNumbers = $("#primaryPhoneNumbers").val();
  var countryCode = $("#primaryPhoneCountryCodes").val();
  var emailAddress = $("#emailAddresss").val();
  var city = $("#citys").val();
  var state = $("#stateProvinceGeoIds").val();
  var countryCodeErr = $("#primaryPhones_error").html();
  if(firstName == ''){
    if(firstNameErr == "0"){
      if(phoneNumbers == '' || emailAddress == '' || city == '' || state == ''){
        $("#eitherOrFields1").css("display","none");
        $("#nextfirstName").hide();
        $("#isfirstName").show();
       // return false;
      }else{
        if(phoneNumbers != '' && countryCode ==""){
            $("#eitherOrFields1").css("display","none");
            $("#nextfirstName").hide();
            $("#isfirstName").show();
            if(countryCodeErr != ''){
            }else{
              $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="primary1_error">Please enter country code</li></ul>');
            }
        }else{
          if(countryCodeErr !=''){
            $("#primaryPhones_error").html("");
            $("#primaryPhones_error").append(countryCodeErr);
            $("#nextfirstName").show();
            $("#isfirstName").hide();
            return false;
          }else{
             $("#eitherOrFields1").css("display","block");
             $("#nextfirstName").show();
             $("#isfirstName").hide();
             return true;
          }
        }
      }
    }else{
       $("#firstNames_error").html("");
       $("#firstNames_error").html('');
        if(countryCodeErr != ''){
        }else{
        //$("#firstNames_error").append(countryCodeErr);
          $("#firstNames_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid name</li></ul>');
          $("#nextfirstName").show();
          $("#isfirstName").hide();
          return false;
       }
    }
    
  }else{
   if( firstName !=''&& (phoneNumbers != '' || emailAddress != '' || city != '' || emailAddress != '') ){
     if(phoneNumbers != '' && countryCode ==""){
          /*$("#eitherOrFields1").css("display","none");
          $("#nextfirstName").hide();
          $("#isfirstName").show(); 
          if(countryCodeErr != ''){
          }else{
          }*/
          $("#primaryPhones_error").html("");
          $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="primary1_error">Please enter country code</li></ul>');
      }else{
          if(countryCodeErr !=''){
            $("#primaryPhones_error").html("");
            $("#primaryPhones_error").append(countryCodeErr);
            $("#nextfirstName").show();
            $("#isfirstName").hide();
            return false;
          }else{
            $("#eitherOrFields1").css("display","none");
            $("#nextfirstName").hide();
            $("#isfirstName").show();
            //return false;
          }
        }
      }else{
        if(phoneNumbers == "" && countryCode !=""){
            $("#primaryPhones_error").html("");
            $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="primary1_error">Please enter phone number</li></ul>');
            $("#nextfirstName").show();
            $("#isfirstName").hide();
            return false;
        }
        if(phoneNumbers != "" && countryCode ==""){
            $("#primaryPhones_error").html("");
            $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="primary1_error">Please enter country code</li></ul>');
            $("#nextfirstName").show();
            $("#isfirstName").hide();
            return false;
        }
        if(countryCodeErr !=''){
            $("#primaryPhones_error").html("");
            $("#primaryPhones_error").append(countryCodeErr);
            $("#nextfirstName").show();
            $("#isfirstName").hide();
            return false;
          }else{
             $("#eitherOrFields1").css("display","block");
             $("#nextfirstName").show();
             $("#isfirstName").hide();
             return false;
         }
      }
  }
}

$("#isfirstName").click(function(){
    $("#eitherOrFields1").css("display","none");
})
function  nextFirstName(element){
  var firstNameErr = $("#firstNames_error li").val();
  var phoneNumbers = $("#primaryPhoneNumbers").val();
  var emailAddress = $("#emailAddresss").val();
  var city = $("#citys").val();
  var state = $("#stateProvinceGeoIds").val();
  var countryCode = $("#primaryPhoneCountryCodes").val();
  var firstName=element.value;
  if(firstName !=''){
    $("#firstNames_error").html("");
    $("#firstNames_error").html('');
    if(firstNameErr == "0"){
      $("#nextfirstName").show();
      $("#isfirstName").hide();
      return true;
    }else{
      if(phoneNumbers != '' || emailAddress != '' || city != '' || state != ''){
        if(phoneNumbers != '' && countryCode ==""){
          $("#eitherOrFields1").css("display","none");
           $("#nextfirstName").show();
           $("#isfirstName").hide();
           $("#primaryPhones_error").html("");
           $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="primary1_error">Please enter country code</li></ul>');
        }else{
          var countryCodeErr = $("#primaryPhones_error").html();
          if(countryCodeErr !=''){
            $("#nextfirstName").show();
            $("#isfirstName").hide();
            return false;
          }else{
            // $("#eitherOrFields1").css("display","none");
         //   $("#nextfirstName").hide();
         //   $("#isfirstName").show();
          //  return false;
          }
        }
        
      }else{
        // $("#eitherOrFields1").css("display","block");
        $("#nextfirstName").show();
        $("#isfirstName").hide();
        return false;
      }
      
    }
  }else{
    $("#nextfirstName").show();
    $("#isfirstName").hide();
  }
}

function nextToSubmit(){
  var source =  $("#sources").val();
  if(source == ''){
    $("#error_Sources").html("");
    $("#error_Sources").html("");
    $("#sources_error").html('');
    $("#sources_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Please select an item in the list.</li></ul>');
    $("#nextSource").show();
    $("#submitMobile").hide();
    return false;
  }else{
  if ( "TCP" == source || "TEV" == source ) {
        console.log("----tcpNames---"+$("#tcpNames").val());
        var tcpNames = $("#tcpNames").val();
        if( tcpNames ==''){
        $("#tcpNames_error").html("");
        $("#tcpNames_error").append('<ul class="list-unstyled text-danger"><li id="errorTcpName">TCP Name should not be empty</li></ul>');
        return false;
        }else{
        onSubmitValidate1();
        }
    }
    $("#sources_error").html("");
    //$("#eitherOrFields1").css("display","block");
    var primaryPhoneCountryCode =  $("#primaryPhoneCountryCodes").val();
    var primaryPhoneNumber =  $("#primaryPhoneNumbers").val();
    var emailAddress = $("#emailAddresss").val();
    var address1 = $("#addresss1").val();
    var stateProvinceGeoId = $("#stateProvinceGeoIds").val();
    var city = $("#citys").val();
    var postalCode = $("#postalCodes").val();
   
   if(primaryPhoneNumber !='' || emailAddress !='' || (address1 !='' && city !='' && postalCode !='') ){
         $("#eitherOrFields1").css("display","none");
     }else{
         //alert('fill mandatory fields');
         $("#eitherOrFields1").css("display","block");
         $("#submitMobile").hide();
         $("#nextSource").show();
         return false;
       // return true;
     }

    
  }
}
$("#primaryPhoneNumbers").keyup(function (e){  
  var phoneNumber = $("#primaryPhoneNumbers").val();
  var countryCode = $("#primaryPhoneCountryCodes").val();
  var phoneNumber = $("#primaryPhoneNumbers").val();
  var firstName = $("#firstNames").val();
  var countryCodeErr = $("#primaryPhones_error").html();
  if(phoneNumber !='' ) {
      $("#primaryPhones_error").html("");
      if(countryCode == '') {
         $("#primaryPhones_error").html("");
         $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Please enter country code</li></ul>');
         $("#nextfirstName").show();
         $("#isfirstName").hide();
      }else{
          if(phoneNumber.length != 10){
              $("#nextfirstName").show();
              $("#isfirstName").hide();
              $("#primaryPhones_error").html("");
              //$("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Please enter phone number</li></ul>');
          }else{
              $("#primaryPhones_error").html("");
              if(countryCode !='' && phoneNumber !=""){
                $("#nextfirstName").hide();
                $("#isfirstName").show();
              }else{
                $("#primaryPhones_error").html("");
                if(countryCode == '') {
                  $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Please enter country code</li></ul>');
                }
                if(phoneNumber == '') {
                  $("#primaryPhones_error").empty();
                  $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Please enter phone number</li></ul>');
                }
              }
          }
      }
  }
  if(countryCode == '') {
      $("#primaryPhones_error").empty();
      $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Please enter country code</li></ul>');
      $("#nextfirstName").show();
      $("#isfirstName").hide();
  }
  if(phoneNumber == '') {
      $("#primaryPhones_error").empty();
      $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Please enter phone number</li></ul>');
      $("#nextfirstName").show();
      $("#isfirstName").hide();
  }
  if(phoneNumber != '') {
    $("#primaryPhones_error").empty();
  }
  if(phoneNumber.length == 10 && countryCode != '' && firstName !='') {
      $("#primaryPhones_error").empty();
      $("#nextfirstName").hide();
      $("#isfirstName").show();
  }
});
function clearErrorMsg2(element){  
  
  var phoneNUmberSize = element.value.length
  //var countryCodes = $("#primaryPhoneCountryCodes").val();
    if(phoneNUmberSize == 10){
       var countryCode = $("#primaryPhoneCountryCodes").val();
       if(countryCode == "" ) {
         $("#primaryPhones_error").html("");
         $("#primaryPhones_error").html('');
         $("#primaryPhones_error").append('<ul class="list-unstyled text-danger"><li id="error_Sources">Enter valid country code.</li></ul>');
         return false;
      }
    }
}
function checkMail(element){
    var emailId = element.value;
    if(emailId == '') {
      var firstName =  $("#firstNames").val();
      var phoneNumbers = $("#primaryPhoneNumbers").val();
      var emailAddress = $("#emailAddresss").val();
      var city = $("#citys").val();
      var state = $("#stateProvinceGeoIds").val();
      if(phoneNumbers != '' || emailAddress != '' || city != '' || state != ''){
        
      }else{
        $("#eitherOrFields1").css("display","block");
        $("#nextfirstName").show();
        $("#isfirstName").hide();
        return;
      }
    }
    var emailErr = $("#emailAddresss_error li").val();
    if(emailErr == "0") {
      $("#nextfirstName").show();
      $("#isfirstName").hide();
    }else{
      $("#nextfirstName").hide();
      $("#isfirstName").show();
    }
}
function loadConstituteM(element){
  var companyNamesErr = $("#companyNames_error li").val();
  $("#companyNames_error").html("");
    $("#companyNames_error").html('');
  if(companyNamesErr == "0"){
    $("#nextCompanyName").show();
    $("#isCompanyName").hide();
  }else{
    $("#nextCompanyName").hide();
    $("#isCompanyName").show();
  }
  var companyName=element.value;
  var result = companyName.match(/Pte/i);
  var constitutionOption = '';
   
  if(companyName.match(/Pte/i) || companyName.match(/pvt/i) || companyName.match(/private/i)) {
      console.log('companyName is =='+companyName);
    //  var constitutionOption = '<option value="DBS_CONST_1006" selected="selected">Private Corporation</option>';
    // $("#constitution").html( constitutionOption );
      $.ajax({
        type: "POST",
        url: "getConstituteList",
	        data:  {"enumTypeId": "DBS_CONSTITUTION"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            	for (var i = 0; i < data.results.length; i++) {
	            		var result = data.results[i];
	            		var enumId = result.enumId ;
	            		console.log("==enumId===="+enumId);
	            		if("DBS_CONST_1006"==enumId){
	            		  constitutionOption += '<option value="'+result.enumId+'"  selected="selected"  >'+result.description+'</option>';
	            		}else{
	            		  constitutionOption += '<option value="'+result.enumId+'" >'+result.description+'</option>';
	            		}
	            	}
	            	console.log("==constitutionOption===="+constitutionOption);
	            	$("#constitutions").html( constitutionOption );
	            }
				    	
	        }
	  }); 
  }	
  
  if(companyName.match(/public/i) || companyName.match(/plc/i)) {
      console.log('companyName is =='+companyName);
      $.ajax({
        type: "POST",
        url: "getConstituteList",
	        data:  {"enumTypeId": "DBS_CONSTITUTION"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            	for (var i = 0; i < data.results.length; i++) {
	            		var result = data.results[i];
	            		var enumId = result.enumId ;
	            		console.log("==enumId===="+enumId);
	            		if("DBS_CONST_1007"==enumId){
	            		  constitutionOption += '<option value="'+result.enumId+'"  selected="selected"  >'+result.description+'</option>';
	            		}else{
	            		  constitutionOption += '<option value="'+result.enumId+'" >'+result.description+'</option>';
	            		}
	            	}
	            	console.log("==constitutionOption===="+constitutionOption);
	            	$("#constitutions").html( constitutionOption );
	            }
				    	
	        }
	        
		});
    }
  if(companyName.match(/Pte/i) || companyName.match(/pvt/i) || companyName.match(/private/i) || companyName.match(/public/i) || companyName.match(/plc/i) ) {
  }else{
    $('#constitutions').dropdown('clear'); 
  }
}
</script>