<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#if isBirthdayRemainderEnabled?has_content && isBirthdayRemainderEnabled?if_exists=="Y">
<#include "component://campaign/webapp/campaign/birthdayRemainder/birthdayModal.ftl"/>
</#if>

<style>
	.fa-phone-square {
	    font-size: 14px !important;
	}
	.fa.fa.fa-envelope.fa-1 {
	    font-size: 14px !important;
	  }
	.fa.fa.fa.fa-sticky-note-o {
	    font-size: 14px !important;
	}
	.fa.fa.fa.fa-envelope-o {
	    font-size: 14px !important;
	    margin-left: -10px;
	}
	.fa.fa.fa.fa-file-o {
	    font-size: 14px !important;
	    margin-right: -10px;
	}
	.fa-mobile, .fa-phone {
	    font-size: 14px !important;
	}
	[badge]:after{
	    font-size: 10px;
	    min-width: 15px;
	}
	#customerBoughtGrid h2.float-left.sub-txt,
	#customerOrderedGrid h2.float-left.sub-txt{
	float: none!important;
	padding-left: 0!important;
	margin-bottom: -16px;
	margin-top: 10px;
	}
	#customerBoughtGrid .ag-theme-balham .ag-root,
	#customerOrderedGrid .ag-theme-balham .ag-root {
	height:236px;
	}
	h2, .h2 {
    font-size: 13px;
	}
	h5, .h5 {
    font-size: 13px;
    line-height: 17px;
	}
	.card-head {
    margin-top: 5px!important;
	}
	.mt-3, .my-3 {
    margin-top: 5px!important;
	}
	body {
    font-size: 14px;
	}
	#10230{
	font-size: 18px;
	}
	.page-header {
    padding-bottom: 2px;
	}
	.header-title {
    line-height: 11px;
	}
	h3, .h3 {
    font-size: 18px;
	}
	#callStatusId_label{
	font-size: 14px;
    font-weight: 400;
    color: #000000;
    line-height: 17px;
	}
	#callBackdate_label{
	text-align: end;
	font-size: 14px;
    font-weight: 400;
    color: #000000;
    line-height: 17px;
	}
	#callBackdate_picker{
	padding-left: inherit;
	}
	.col-sm-7.callStatusId-input {
    padding: inherit;
	}
	.ui.dropdown.search.form-control.fluid.show-tick.callStatusId {
    min-width: 9rem;
	}
    .bootstrap-datetimepicker-widget table td.day {
     width: 40px;
	 }
    .name.h2.text-info.disabled {
		padding-left: 3px;
     }
    .primaryPhone {
     padding-right: 3px;
     line-height: 17px;
     font-size: 13px;
     }
    .primaryEmail {
     line-height: 17px;
     }
    .phone {
    padding-left: 6px;
    margin-right: -15px;
    }
	.value-text {
    font-size: 13px;
    line-height:17px;
	}
	.field-text {
    font-size: 14px;
    line-height:17px;
	}
	.addNotes {
		padding-right: 9px;
	}
	.small, small {
    font-size: 14px;
    font-weight: 600;
    color: #000000;
    line-height: 17px;
	}
	#personResponsible_label{
	<#--color:  #0091b0;-->
    font-weight: 400;
    font-size: 14px;
    <#--padding-top: 6px;-->
	}
	#personResponsible{
	color:  #0091b0;
    font-weight: 900;
    font-size: 14px;
    padding: 0px 0px 0px 19px;
	}
	.left-icones {
    display: inline-flex;
    margin-left: 10px;
	}
	.page-header {
    margin-left: 0px;
	}
	.pt-2, .py-2 {
    padding-top: 0rem!important;
	}
	.float-left.sub-txt {
	    margin-bottom: -1px;
	    margin-top: 10px;
	}
	.mt-2, .my-2 {
    margin-top: 1.5rem!important;
	}
	<#if isCustomerSummaryView?has_content && isCustomerSummaryView=="Y">
	<#-- .bottom-shadow-cust-bot {
	margin-bottom: 20px;
	padding: 0px;
	box-shadow: 0px 15px 10px -15px #a5a5a5;
	}-->
	#customerBoughtGrid h2.float-left.sub-txt, #customerOrderedGrid h2.float-left.sub-txt {
	    float: none!important;
	    padding-left: 10px !important;
	    background-color: #02829d;
	    color: #ffffff;
	    padding: 10px 10px !important;
	}
	</#if>
</style>
<div>
	<form id="removeResponsiblePerson" action="removeResponsiblePerson">
		<input type="hidden" id="responsiblePerson" name="responsiblePerson" value="${inputContext.partyId?if_exists}"/>
		<input type="hidden" name="campaignListId" id="campaignListId" value="${campaignListId?if_exists}" />
		<input type="hidden" name="marketingCampaignId" id="marketingCampaignId" value="${marketingCampaignId?if_exists}" />
		<input type="hidden" name="contactListId" id="contactListId" value="${contactListId?if_exists}" />
	</form>
</div>
<#if marketingCampaignId?has_content && marketingCampaignId?exists && ( "${marketingCampaignId!}" == defalutMktCampaignId! )>
<form name="updateDate" id="updateDate" action="<@ofbizUrl>callStatusUpdate</@ofbizUrl>">
<#else>
<form name="updateDate" id="updateDate">
</#if>
	<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
	<input type="hidden" name="campaignListId" id="campaignListId" value="${campaignListId?if_exists}" />
	<input type="hidden" name="noteId" id="partyNoteId" value="${partyNoteId?if_exists}" />
	<input type="hidden" name="marketingCampaignId" id="marketingCampaignId" value="${marketingCampaignId?if_exists}" />
	<input type="hidden" name="contactListId" id="contactListId" value="${contactListId?if_exists}" />
	<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey?if_exists}" />
	<input type="hidden" name="callStatus" id="callStatus" value="" />
	<input type="hidden" name="callBackDate" id="callBackDate" value="" />
</form>
	<div class="page-header border-b pt-2">
		<h2 class="d-inline-block" style="margin-left: 3px;">Customer Profile</h2>
		<ul class="flot-icone">
			<li class="mt-0">
				<@dropdownCell
					id="callStatusId"
					label="Call Status"
					placeholder="Please Select"
					options=enumerationList!
					required=false
					value=callStatus?if_exists
					allowEmpty=true
					labelColSize="col-sm-5" inputColSize="col-sm-7"
					/>
			</li>
			<li class="mt-0" style="margin-left: 30px;">
				<@inputDate 
					id="callBackdate"
					label="Call Back Date"
					value="${callBackDate!}"
					placeholder="Call Back Date"
					dateFormat="MM-dd-yyyy"
					labelColSize="col-sm-5" inputColSize="col-sm-7"
					/>
			</li><li class="mt-0">
			<#if marketingCampaignId?has_content && marketingCampaignId?exists && ( "${marketingCampaignId!}" == defalutMktCampaignId! )>
            <submit id="updateStatusAndDate" style="font-size: 14px; margin-left: -7px;" class="btn btn-xs btn-primary"><i class="fa fa-save" aria-hidden="true"></i> Save </submit></li>
          	<#else>
            <submit  id="oneClick" style="font-size: 14px; margin-left: -7px;" class="btn btn-xs btn-primary"><i class="fa fa-save" aria-hidden="true"></i> Save </submit></li>
          	</#if>
			<li class="mt-0">
				<#if marketingCampaignId?exists && marketingCampaignId?has_content && contactListId?exists && contactListId?has_content>
				<form name="setCampaignListToSession" id="setCampaignListToSession" action="setCampaignListToSession" method="POST">
					<input type="hidden" name="campaignListId" id="campaignListId" value="${campaignListId?if_exists}"/>
					<input type="hidden" name="partyId" value="${inputContext.partyId?if_exists}"/>
					<input type="hidden" name="marketingCampaignId" id="marketingCampaignId" value="${marketingCampaignId?if_exists}" />
					<input type="hidden" name="contactListId" id="contactListId" value="${contactListId?if_exists}" />
					<button type="submit" class="btn btn-xs btn-primary" style="font-size: 14px; margin-left: -7px;" formtarget="_blank">View Details</button>
				</form>
				</#if>
			</li>
		</ul>
	</div>
<div class="bottom-shadow-cust-bot">
<div class="col-md-12 col-lg-12 col-sm-12" style="margin-left: -16px;">
	<@dynaScreen
		instanceId="VIEW_CALL_LIST_CUSTOMER" 
		modeOfAction="VIEW" 
		/>
</div>
</div>
<#assign customerBoughtInstanceId ="CUSTOMER_BOUGHT_PRODUCT" />
<#assign customerOrderedInstanceId ="CUSTOMER_ORDERED_PRODUCT" />
<#assign orderByColumn ="orderDate" />
<#assign clearFilterBtn=true>
<#assign savePrefBtn=true>
<#assign exportBtn=true>
<#assign refreshPrefBtn=true>
<#assign subFltrClearBtn=true>
<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
<#assign clearFilterBtn=false>
<#assign savePrefBtn=false>
<#assign exportBtn=false>
<#assign refreshPrefBtn=false>
<#assign subFltrClearBtn=false>
<style>
.mt-2, .my-2 {
    margin-top: 0.5rem!important;
}
</style>
</#if>
<#if isCustomerSummaryView?has_content && isCustomerSummaryView=="Y">
<div class="row bottom-shadow2">
	<div class="col-lg-6 col-mg-6 col-sm-6" id="customerBoughtGrid">
		<form method="post" id="shipped-product-form" novalidate="novalidate" data-toggle="validator">
			<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
			<input type="hidden" name="partyId" value="${inputContext.partyId?if_exists}"/>
			<input type="hidden" name="srNumber" value="${srNumber?if_exists}"/>
			<input type="hidden" name="isOrderCompleted" value="Y"/>
			<input type="hidden" name="isInvoiceApproved" value="Y"/>
			<input type="hidden" name="orderByColumn" value="${orderByColumn!} DESC"/>
			<input type="hidden" id="customerBoughtInstanceId" value="${customerBoughtInstanceId!}"/>
			<input type="hidden" name="limitRows" id="limitRows" value="N"/>
		</form>
		<#-- <@AgGrid
			gridheadertitle="Customer Bought"	
			gridheaderid="shipped-grid-action-container"
			savePrefBtn=savePrefBtn
			clearFilterBtn=clearFilterBtn
			exportBtn=exportBtn
			refreshPrefBtn=refreshPrefBtn
			subFltrClearBtn = subFltrClearBtn
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent!
			refreshPrefBtnId="shipped-refresh-pref-btn"
			savePrefBtnId="shipped-save-pref-btn"
			subFltrClearId="shipped-sub-filter-clear-btn"
			clearFilterBtnId="shipped-clear-filter-btn"
			exportBtnId="shipped-export-btn"
			removeBtnId="shipped-remove-btn"
			userid="${userLogin.userLoginId}"
			shownotifications="true"
			instanceid="${customerBoughtInstanceId}"
			autosizeallcol="true"
			debug="false"
			/>
		<script type="text/javascript" src="/common-portal-resource/js/ag-grid/order/customer-bought-product.js"></script>-->
			 <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
			<@fioGrid 
			id="customer-bought-product"
			instanceId="${customerBoughtInstanceId}"
			jsLoc="/common-portal-resource/js/ag-grid/order/customer-bought-product.js"
			headerLabel="Customer Bought"
			headerId="customer_bought_tle"
			exportBtn=exportBtn
			savePrefBtn=true
			clearFilterBtn=true
			subFltrClearBtn=true
			savePrefBtnId="cust-bought-save-pref"
			clearFilterBtnId="cust-bought-clear-pref"
			subFltrClearId="cust-bought-clear-sub-ftr"
			exportBtnId="shipped-export-btn"
			serversidepaginate=false
			statusBar=false
			subFltrClearBtn=true
			/>
	</div>
	<div class="col-lg-6 col-mg-6 col-sm-6 " id="customerOrderedGrid">
		<form method="post" id="ordered-product-form" novalidate="novalidate" data-toggle="validator">
			<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
			<input type="hidden" name="partyId" value="${inputContext.partyId?if_exists}"/>
			<input type="hidden" name="srNumber" value="${srNumber?if_exists}"/>
			<input type="hidden" name="isOrderCompleted" value="N"/>
			<input type="hidden" name="isInvoiceApproved" value="N"/>
			<input type="hidden" name="orderByColumn" value="${orderByColumn!}"/>
			<input type="hidden" id="customerOrderedInstanceId" value="${customerOrderedInstanceId!}"/>
			<input type="hidden" name="limitRows" id="limitRows" value="N"/>
		</form>
		<#-- <@AgGrid
			gridheadertitle="Products Bought Waiting To Be Shipped"
			gridheaderid="ordered-grid-action-container"
			savePrefBtn=savePrefBtn
			clearFilterBtn=clearFilterBtn
			exportBtn=exportBtn
			refreshPrefBtn=refreshPrefBtn
			subFltrClearBtn = subFltrClearBtn
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent!
			refreshPrefBtnId="ordered-refresh-pref-btn"
			savePrefBtnId="ordered-save-pref-btn"
			subFltrClearId="ordered-sub-filter-clear-btn"
			clearFilterBtnId="ordered-clear-filter-btn"
			exportBtnId="ordered-export-btn"
			removeBtnId="ordered-remove-btn"
			userid="${userLogin.userLoginId}"
			shownotifications="true"
			instanceid="${customerOrderedInstanceId}"
			autosizeallcol="true"
			debug="false"
			/>
		<script type="text/javascript" src="/common-portal-resource/js/ag-grid/order/customer-ordered-product.js"></script>-->
		<@fioGrid 
			id="customer-ordered-product"
			instanceId="${customerOrderedInstanceId!}"
			jsLoc="/common-portal-resource/js/ag-grid/order/customer-ordered-product.js"
			headerLabel="Products Bought Waiting To Be Shipped"
			headerId="customer_ordered_tle"
			savePrefBtnId="cust-ordered-save-pref"
			clearFilterBtnId="cust-ordered-clear-pref"
			subFltrClearId="cust-ordered-clear-sub-ftr"
			exportBtn=exportBtn
			savePrefBtn=false
			clearFilterBtn=false
			subFltrClearBtn=false
			exportBtnId="ordered-export-btn"
			/>
	</div>
</div>
</#if>
<div>
<form id="removeBirthdayDate" action="removeBirthdayDate">
		<input type="hidden" id="partyId" name="partyId" value="${inputContext.partyId?if_exists}"/>
		<input type="hidden" name="marketingCampaignId" id="marketingCampaignId" value="${marketingCampaignId?if_exists}" />
		<input type="hidden" name="contactListId" id="contactListId" value="${contactListId?if_exists}" />
		<input type="hidden" name="campaignListId" id="campaignListId" value="${campaignListId?if_exists}" />
	</form>
</div>
<#--Modal window for update postal customer-->
<div  id="updatePOSTALcustomerInfo" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
   <div class="modal-dialog modal-lg" style="width: 750px; max-width: 1200px;">
      <form method="post" action="<@ofbizUrl>updatePostalAddress</@ofbizUrl>" id="updatePostalAddress" class="form-horizontal" name="updatePostalAddress" novalidate="novalidate" data-toggle="validator">
         <input type="hidden" name="activeTab" value="c-profile" />
         <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
         <input type="hidden" name="contactMechId" value="${postalContactMechId?if_exists}">
         <input type="hidden" name="partyId" value="${partyId?if_exists}">
         <input type="hidden" name="campaignListId" value="${campaignListId?if_exists}">
         <input type="hidden" name="marketingCampaignId" id="marketingCampaignId" value="${marketingCampaignId?if_exists}" />
		<input type="hidden" name="contactListId" id="contactListId" value="${contactListId?if_exists}" />
	
         <div class="modal-content">
            <div class="modal-header">
               <h4 class="modal-title">Update Postal Address</h4>
               <button class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            <!-- Modal content-->
            <@inputHidden id="existingStateId" value="${stateGeoId?if_exists}"/>
               <@dynaScreen
                   instanceId="CALL_LIST_POSTAL_ADDRESS" 
                   modeOfAction="UPDATE" 
                   />
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                        <input type="submit" class="btn btn-sm btn-primary disabled" value="Update" id="updatePostal"/>
                        <button type="submit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Cancel</button>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </form>
   </div>
</div>
<script> 
	$(document).ready(function() {
	let regex='';
	var geoId = $('#countryGeoId').val();
	regexJson = getServiceResult("getZipCodeRegex",'countryGeoId', geoId);
	regex = regexJson.regex;
	$("#updatePostal").click(function(){
		if(regex != ''){
			var re = new RegExp(regex);
			console.log("re"+re);
			if (re.test($('#postalCode').val())) {
				$('#postalCode_error').html('');
				$("#updatePostalAddress").submit();
			}else{
				event.preventDefault();
				$('#postalCode_error').html('Please enter the valid zip code');
			}
		}
	});
	$("#removeRP").click(function() {
		let partyId = $("#removeResponsiblePerson #responsiblePerson").val();
		if(partyId !=null && partyId !=""){
			event.preventDefault();
			$("#removeResponsiblePerson").submit();
		}else{
			showAlert("error","PartyId is empty");
		}
	
	});
	$("#removeBirthDate").click(function() {
		let partyId = $("#removeBirthdayDate #partyId").val();
		if(partyId !=null && partyId !=""){
			event.preventDefault();
			$("#removeBirthdayDate").submit();
		}else{
			showAlert("error","PartyId is empty");
		}
	});
	$("#updateStatusAndDate").click(function(event) {
    let callStatus = $("#callStatusId").val();
    let callBackDate = $("#callBackdate").val();
    let marketingCampaignId = $("#marketingCampaignId").val();
    let defalutMktCampaignId = "${defalutMktCampaignId!}"
    if (callStatus !== "" && callStatus !== null || callBackDate !== "" && callBackDate !== null) {
        $("#updateDate #callStatus").val(callStatus);
        $("#updateDate #callBackDate").val(callBackDate);
        event.preventDefault();
        if(marketingCampaignId == defalutMktCampaignId ){
        	$("#updateDate").submit();
        }
    } else {
        event.preventDefault();
        showAlert("error", "Please select any status or call back date");
    }
	});
	$("#oneClick").click(function(event) {
    let callStatus = $("#callStatusId").val();
    let callBackDate = $("#callBackdate").val();
    if (callStatus !== "" && callStatus !== null || callBackDate !== "" && callBackDate !== null) {
        $("#updateDate #callStatus").val(callStatus);
        $("#updateDate #callBackDate").val(callBackDate);
        event.preventDefault();
        $.ajax({
            type: 'POST',
            url: '/customer-portal/control/callStatusAndCallBackDateUpdate', 
            data: $("#updateDate").serialize(),
            async: true,
            success: function(result) {
                if (result.responseMessage === "success") {
					window.location = "<@ofbizUrl>outBoundCallList</@ofbizUrl>";
                }
            },
            error: function(xhr, status, error) {
                console.log("Error: " + error);
            },
            complete: function() {}
        });
    } else {
        event.preventDefault();
        showAlert("error", "Please select any status or call back date");
    }
	});
	});
	<#if enableCSRReassignButton?has_content && enableCSRReassignButton?if_exists=="Y">
		enableAM();
	</#if>
	<#if CurrentCampaignList?has_content>
	<#list CurrentCampaignList as camp>
	if ($('#CurrentCampaignList')) {
		if ($('#CurrentCampaignList').text()) {
		<#if camp.callFinished?has_content && (camp.callFinished) != "Y">
			$('#CurrentCampaignList').append('<a href="<@ofbizUrl>viewCallListCustomer?partyId=${partyId!}&contactListId=${camp.contactListId?if_exists}&marketingCampaignId=${camp.marketingCampaignId?if_exists}</@ofbizUrl>">${camp.campaignName?if_exists}<#if camp.marketingCampaignId?has_content>(${camp.marketingCampaignId?if_exists})</#if><br/></a>');
			<#else>
			$('#CurrentCampaignList').append('${camp.campaignName?if_exists}<#if camp.marketingCampaignId?has_content>(${camp.marketingCampaignId?if_exists})</#if><br/>');
		</#if>
		}
		}
	</#list>
	<#else>
	if ($('#CurrentCampaignList').text()) {
		$('#CurrentCampaignList').append('N/A');
	}
	</#if>
	<#if openCouponAssigned?has_content>
   		<#list openCouponAssigned as openCoupon>
   		if ($('#couponCount')) {
		if ($('#couponCount').text()) {
			$('#couponCount').append('${openCoupon.coupon?if_exists}<#if openCoupon.thruDate?has_content> - ${openCoupon.thruDate?if_exists}</#if><br/>');
		}
		}
   		</#list>
	<#else>
	if ($('#couponCount').text()) {
			$('#couponCount').append('N/A ');
		}
	</#if>
	function enableAM(){
	if ($('#personResponsible')) {
		if ($('#personResponsible').text()) {
			$('#personResponsible').append('<span data-toggle="modal" data-target="#partyResponsible" title="Reassign" class=""><i class="fa fa-edit" aria-hidden="true" style="background-color: #fff; color: #0091b0; padding: 5px;"></i></span>');
			<#if responsibleName?has_content>
			    $('#personResponsible').append('<span id="removeRP" class="" data-toggle="confirmation" title="Are you sure? Do you want to remove the account manager"><i class="fa fa-trash" aria-hidden="true" style="background-color: #fff; color: #0091b0;"></i> </span>');
			</#if>
		}
	}
	}
	if ($('#clv_VF')) {
		if ($('#clv_VF').text()!="-") {
		$('#clv_VF').prepend('<span><i class="fa fa-usd" aria-hidden="true" style="line-height: 17px;"></i></span>').attr('id', ' ' + $('#clv_VF').attr('id'));
		}
	}
	if ($('#ytd_VF')) {
		if ($('#ytd_VF').text()!="-") {
		$('#ytd_VF').prepend('<span><i class="fa fa-usd" aria-hidden="true" style="line-height: 17px;"></i></span>').attr('id', ' ' + $('#ytd_VF').attr('id'));
		}
	}
	if ($('#lytd_VF')) {
		if ($('#lytd_VF').text()!="-") {
		$('#lytd_VF').prepend('<span><i class="fa fa-usd" aria-hidden="true" style="line-height: 17px;"></i></span>').attr('id', ' ' + $('#lytd_VF').attr('id'));
		}
	}
	<#if partyId?has_content>
		birthDatePopup();
	</#if>
	function birthDatePopup(){
	if ($('#birthdayFormat')) {
		if ($('#birthdayFormat').text()) {
			$('#birthdayFormat').append('<span data-toggle="modal" data-target="#birthdayDatePopup" title="Edit" class=""><i class="fa fa-edit" aria-hidden="true" style="padding-left: 2px; background-color: #fff; color: #0091b0;"></i> </span>');
			<#if birthDate?has_content>
				$('#birthdayFormat').append('<span id="removeBirthDate" class="" data-toggle="confirmation" title="Are you sure?	Do you want to remove"><i class="fa fa-trash" aria-hidden="true" style="background-color: #fff; color: #0091b0;"></i> </span>');
			</#if>
		}
	}
	}
	let phoneNumber = "${inputContext.partyPrimaryPhone!}";

	$("#10310").click(function() {
		if(phoneNumber !="" && phoneNumber != null ){
			$('#update-phone-number').modal("show");
		}else{
		
		}
	});
	if ($('#10310')) {
		if ($('#10310').text()) {
		if(phoneNumber !="" && phoneNumber != null ){
			$('#10310').append('<span data-toggle="modal" data-target="#update-phone-number" title="Edit" class=""><i class="fa fa-edit" style="font-weight: bold; background-color: #fff; color: #0091b0;" aria-hidden="true"></i> </span>');
		}
	}
	}
	if ($('#10320')) {
		if ($('#10320').text()) {
			$('#10320').append('<span style="color: #000000; font-size: 13px; font-weight: 400; line-height: 17px;">&nbsp;Client Time : ${inputContext.clientTime!}</span>');
	}
	}
	if ($('#10330')) {
		if ($('#10330').text()) {
			$('#10330').append('<span style="color: #000000; font-size: 13px; font-weight: 400; line-height: 17px;">&nbsp;My Time : ${inputContext.myTime!} </span>');
	}
	}
	updateAddressButton ();
	function updateAddressButton (){
		if ($('#generalAddress')) {
			if ($('#generalAddress').text()) {
				$('#generalAddress').append('<span title="Edit" id="editAddressButton" class=""><i class="fa fa-edit" aria-hidden="true" style="background-color: #fff; color: #0091b0;"></i></span>');
			}
		}
	}
	$("#editAddressButton").click(function() {
		$('#updatePOSTALcustomerInfo').modal('show');
	});
	var countryGeoId = $('#countryGeoId').val();
	$(function() {
		var stateGeoId = $('#existingStateId').val();
		var list = "";
		if (countryGeoId != null && countryGeoId != "") {
			var urlString = "/common-portal/control/getStateDataJSON?countryGeoId=" + countryGeoId + "&externalLoginKey=${requestAttributes.externalLoginKey!}";
			$.ajax({
				type: 'POST',
				async: true,
				url: urlString,
				success: function(states) {
					$('#stateProvinceGeoId').empty();
					list = $('#stateProvinceGeoId');
					list.append("<option value=''>Select State</option>");
					if (states.length == 0) {
						list.append("<option value = ''>N/A</option>");
					} else {
						for (var i = 0; i < states.length; i++) {
							if (stateGeoId != null && stateGeoId != "" && states[i].geoId == stateGeoId) {
								list.append("<option  value =" + states[i].geoId + " selected>" + states[i].geoName + " </option>");
							} else {
								list.append("<option  value =" + states[i].geoId + ">" + states[i].geoName + "</option>");
							}
						}
					}
				}
			});
			$('#stateProvinceGeoId').append(list);
			$('#stateProvinceGeoId').dropdown('refresh');
		}
	});
	var regex = '';
	var countryGeoId = $('#countryGeoId').val();
	console.log('countryGeoId', countryGeoId);
	$('#countryGeoId').change(function(e, data) {
		$("#stateProvinceGeoId").dropdown('clear');
		$('#postalCode').val('');
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoId', 'stateProvinceGeoId', 'stateList', 'geoId', 'geoName','${stateValue!}');
		var countryGeoId = $('#countryGeoId').val();
		if(countryGeoId != ''){
		
			regexJson = getServiceResult("getZipCodeRegex",'countryGeoId', countryGeoId);
			regex = regexJson.regex;
		} else {
			$('#stateProvinceGeoId').html('<option value="">Please Select</option>');
		}
	});
function validatePostalCode(regex){
	var isInvalid = false;
	console.log("regex"+regex);
	if(regex != ''){
		var re = new RegExp(regex);
		console.log("re"+re);
		if (re.test($('#postalCode').val())) {
		console.log(regex, $('#postalCode').val(), 'regex1');
		$('#postalCode_error').html('');
		}else{
			console.log(regex, $('#postalCode').val(), 'regex2');
			$('#postalCode_error').html('Please enter the valid zip code');
			isInvalid = true;
		}
	}
	console.log("isInvalid"+isInvalid);
	return isInvalid;
}
removeBadgeIfZero('phone');
removeBadgeIfZero('10300');
function removeBadgeIfZero(elementId) {
    var element = document.getElementById(elementId);
    if (element) {
        var spanElement = element.querySelector('span.custom-badge');
        if (spanElement && spanElement.hasAttribute('badge')) {
            var badgeValue = spanElement.getAttribute('badge');
            if (badgeValue === '0') {
                spanElement.removeAttribute('badge');
            } else {
            }
        }
    }
}
</script>