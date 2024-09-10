<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<style>
	<#--  #customerBoughtGrid h2.float-left.sub-txt,
	#customerOrderedGrid h2.float-left.sub-txt{
		float: none!important;
		padding-left: 0!important;
	}-->
	#customerBoughtGrid h2.float-left.sub-txt, #customerOrderedGrid h2.float-left.sub-txt , #trans-recent h2.float-left.sub-txt {
	    float: none!important;
	    padding-left: 10px !important;
	    background-color: #02829d;
	    color: #ffffff;
	    padding: 10px 10px !important;
	    margin-bottom: -16px;
	    margin-top: 10px;
	}
	.float-left.sub-txt {
	    margin-top: 10px;
	}
	.page-header {
    margin: 10px 0px;
	}
	.pt-2, .py-2 {
    padding-top: 0rem!important;
	}
	#customerBoughtGrid .ag-theme-balham .ag-root,
	#customerOrderedGrid .ag-theme-balham .ag-root,
	#trans-recent-grid1 .ag-theme-balham .ag-root {
		height:236px;
	}	
	.card-head {
	    margin-top: 5px!important;
	}
	#nav-tab-focus{
	    margin-top: 5px!important;
	}
	.bootstrap-datetimepicker-widget table td.day {
     width: 40px;
	 }
	#callBackdate_label{
		text-align: end;
		font-size: 15px;
    font-weight: 400;
    color: #000000;
	}
	#callStatusId_label{
	font-size: 15px;
    font-weight: 400;
    color: #000000;

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
	.bottom-shadow2{
		box-shadow: 0px 15px 10px -15px #a5a5a5;
	    padding: 0px 0px 10px 0px;
	    margin-bottom: 20px;
	}
	.h1 {
	    font-size: 18px;
	}
	.h2 {
	    font-size: 14px;
	}
	.small, small {
	    font-size: 14px;
	    font-weight: 600;
	    color: #000000;
	}
	#personResponsible_label{
	<#--color:  #0091b0;-->
    font-weight: 400;
    font-size: 14px;
	}
	#personResponsible{
	color: #0091b0;
    font-weight: 900;
    font-size: 17px;
	}
	.mt-2, .my-2 {
    margin-top: 1.5rem!important;
	}
<#if isCustomerSummaryView?has_content && isCustomerSummaryView=="Y">
.custom-bottom-shadow1{
	margin-bottom: 20px;
    padding: 0px;
    box-shadow: 0px 15px 10px -15px #a5a5a5;
}
</#if>
.viewOrdersListButton {
    display: inline-block;
    padding: 0px 12px !important;
    background: #FFFFFF!important;
    border: 1px solid #dedddd !important;
    color: #000 !important;
    line-height: 22px !important;
    font-size: 14px !important;
    font-weight: 400;
    border-radius: 4px;
    margin-bottom: 3px;
    vertical-align: middle;
    background-image: none !important;
    cursor: pointer;
    margin-right: 2px;
}
</style>
<#assign viewAll='<a title="View All" id="viewOrdersList" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-eye" aria-hidden="true"></i> View All </a> ' />
<#assign customerBoughtInstanceId ="CUSTOMER_BOUGHT_PRODUCT" />
<#assign customerOrderedInstanceId ="CUSTOMER_ORDERED_PRODUCT" />
<#assign recentTransactionsInstanceId ="RECENT_INVOICE_TRANSACTIONS" />
<#assign orderByColumn ="orderDate" />
<#if entityName!="RmsTransactionMaster">
	<#assign customerBoughtInstanceId ="CUSTOMER_BOUGHT_PRODUCT_INVOICE" />
	<#assign customerOrderedInstanceId ="CUSTOMER_ORDERED_PRODUCT_INVOICE" />
	<#assign recentTransactionsInstanceId ="RECENT_INVOICE_TRANSACTIONS" />
	<#assign orderByColumn ="invoiceDate" />
</#if>
<div class="row pt-2">
  <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "Profile") />    
</div>
<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey?if_exists}" />
<input type="hidden" name="userId" id="userId" value="${userLogin.userLoginId?if_exists}" />

<form name="updateDate" id="updateDate" action="<@ofbizUrl>updateCallStatus</@ofbizUrl>">
	<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
	<input type="hidden" name="noteId" id="partyNoteId" value="${partyNoteId?if_exists}" />
	<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey?if_exists}" />
	<input type="hidden" name="callStatus" id="callStatus" value="" />
	<input type="hidden" name="callBackDate" id="callBackDate" value="" />
	<input type="hidden" name="marketingCampaignId" id="marketingCampaignId" value="${marketingCampaignId!}" />
	<input type="hidden" name="contactListId" id="contactListId" value="${contactListId!}" />
</form>
<div class="page-header border-b pt-2" syle="margin-bottom: -15px;">
	<h2 class="d-inline-block">General Details</h2>
	<ul class="flot-icone">
		<li class="mt-0">
			<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
			<#if marketingCampaignList?has_content && marketingCampaignList?exists>
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
			</#if>
			</#if>
		</li>
		<li class="mt-0"  style="margin-left: 30px;">
			<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
			<#if marketingCampaignList?has_content && marketingCampaignList?exists>
			<@inputDate 
				id="callBackdate"
				label="Call Back Date"
				value="${callBackDate!}"
				placeholder="Call Back Date"
				dateFormat="MM-dd-yyyy"
				labelColSize="col-sm-5" inputColSize="col-sm-7"
				/>
			</#if>
			</#if>
		</li>
		<li class="mt-0">
		<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
			<#if marketingCampaignList?has_content && marketingCampaignList?exists>
			<submit id="oneClick" style="font-size: 14px; margin-left: -7px; margin-right: -7px; " class="btn btn-xs btn-primary"><i class="fa fa-save" aria-hidden="true"></i> Save </submit>
			</#if></#if>
		</li>
		<li class="mt-0">
		<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
			<a id="summary-screen" name="summary-screen" style="font-size: 14px; margin-right: -7px;" href="/customer-portal/control/viewCallListCustomer?partyId=${inputContext.partyId!}&marketingCampaignId=${marketingCampaignId?if_exists}&contactListId=${contactListId?if_exists}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-xs btn-primary">View Summary</a>
			</#if>
		</li>
		<#if isEnableSinglePageEdit?exists && isEnableSinglePageEdit?has_content && isEnableSinglePageEdit=="Y">
			<li class="mt-0">
				<a href="<@ofbizUrl>updateCustomerSingle?partyId=</@ofbizUrl>${inputContext.partyId!}" title="Single Page Edit" style="font-size: 14px; margin-right: -7px;" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> </a>
			</li>
		</#if>
		<#if partySummary.statusId?has_content && partySummary.statusId == "PARTY_ENABLED">
		<#if hasReassignPermission?default(true)> 
		<#if isUserLoginExists?exists && isUserLoginExists?has_content && isUserLoginExists == "Y">
		<#if isEnableCustReports?has_content && isEnableCustReports?if_exists=="Y">
		<li class="mt-0">
			<a id="food-journal" name="food-journal" style="font-size: 14px; margin-right: -7px;" href="/customer-portal/control/foodJournal?partyId=${inputContext.partyId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i>Food Journal</a>
		</li>
		<li class="mt-0">
			<a id="water-tracker" name="water-tracker" style="font-size: 14px; margin-right: -7px;"  href="/customer-portal/control/waterTracker?partyId=${inputContext.partyId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i>Water Tracker</a>
		</li>
		<li class="mt-0">
			<a id="daily-checklist" name="daily-checklist" style="font-size: 14px; margin-right: -7px;"href="/customer-portal/control/checklist?partyId=${inputContext.partyId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i>Daily Checklist</a>
		</li>
		<li class="mt-0">
			<a id="weight-report" name="weight-report" style="font-size: 14px; margin-right: -7px;"href="/customer-portal/control/weightReports?partyId=${inputContext.partyId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i>Weight Goal</a>
		</li>
		</#if>
		<li class="mt-0">
			<a id="reset-customer-pwd" style="font-size: 14px; margin-right: -7px;" name="reset-customer-pwd" href="#" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i>Reset Password </a>
		</li>
		<li class="mt-0">
			<a id="user-status" style="font-size: 14px; margin-right: -7px;" name="user-status" href="#" value="${isUserEnabled!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> <#if isUserEnabled?exists && isUserEnabled?has_content && isUserEnabled =="Y">Disable Login<#else>Enable Login</#if></a>
		</li>
		<#else>
			<#if isEnableInviteUser?has_content && isEnableInviteUser?if_exists=="Y">
				<li class="mt-0">
					<a id="invite-customer-user" style="font-size: 14px; margin-right: -7px;" name="invite-customer-user" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i>Invite User </a>
				</li>
			</#if>
		</#if>
		<#if isEnableReassign?exists && isEnableReassign?has_content && isEnableReassign=="Y">
			<li class="mt-0">
				<span data-toggle="modal" style="font-size: 14px; margin-right: -7px;" data-target="#partyResponsible" title="Reassign" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Reassign</span>
			</li>
		</#if>
		<li class="mt-0">
			<a href="<@ofbizUrl>updateCustomer?partyId=</@ofbizUrl>${inputContext.partyId!}" title="Update" style="font-size: 14px; margin-right: -7px;" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> </a>
		</li>
		<#if isEnableDeactivate?exists && isEnableDeactivate?has_content && isEnableDeactivate=="Y">
			<li class="mt-0">
				<span id="cust-deactivate" class="btn btn-xs btn-danger m5" style="font-size: 14px; margin-right: -7px;" data-toggle="confirmation" title="Are you sure?	Do you want to deactivate"><i class="fa fa-times" aria-hidden="true"></i> </span>
			</li>
		</#if>
		</#if>
		<#else>
		<li class="mt-0">
			<span id="cust-activate" class="btn btn-xs btn-primary" style="font-size: 14px; margin-right: -7px;" data-toggle="confirmation" title="Are you sure?	Do you want to activate"><i class="fa fa-check" aria-hidden="true"></i> </span>
		</li>
		</#if>
	</ul>
</div>

<div style="padding: 12px;">
<div class="row custom-bottom-shadow1">
<div class="col-md-12 col-lg-12 col-sm-12" style="margin-left: -16px;">
			
	<@dynaScreen 
	instanceId="VIEW_CUST_BASE"
	modeOfAction="VIEW"
	/>
	
</div>
</div>
</div>
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
<#assign viewAll = '<a href="#" title="View All" class="viewOrdersListButton" id="viewOrdersList"><i class="fa fa-eye" aria-hidden="true"></i> View All</a>' />
<style>
 .mt-2, .my-2 {
    margin-top: 0.5rem!important;
}
#viewOrdersList{
	margin-top: -40px;
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
		<#--  
		<@AgGrid
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
		-->
		<#-- 
		<div class="popup-agtitle">
			<@headerH2 title="Customer Bought" id=""! class="float-left sub-txt"/>
		</div>
	    <div id="customer-bought-product" style="width: 100%;margin-top: 20px;" class="ag-theme-balham"></div>
		<script type="text/javascript" src="/common-portal-resource/js/ag-grid/order/customer-bought-product.js"></script>
		-->
		<@fioGrid 
			id="customer-bought-product"
			instanceId="${customerBoughtInstanceId}"
			jsLoc="/common-portal-resource/js/ag-grid/order/customer-bought-product.js"
			headerLabel="Customer Bought"
			headerId="customer_bought_tle"
			savePrefBtnId="cust-bought-save-pref"
			clearFilterBtnId="cust-bought-clear-pref"
			subFltrClearId="cust-bought-clear-sub-ftr"
			serversidepaginate=false
			headerBarClass="grid-header-no-bar"
			exportBtnId="shipped-export-btn"
			exportBtn=exportBtn
			statusBar=false
			savePrefBtn=true
			clearFilterBtn=true
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
		<#-- 
		<@AgGrid
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
		debug="true"
		/> -->
		<@fioGrid 
			id="customer-ordered-product"
			instanceId="${customerOrderedInstanceId!}"
			jsLoc="/common-portal-resource/js/ag-grid/order/customer-ordered-product.js"
			headerLabel="Products Bought Waiting To Be Shipped"
			headerId="customer_ordered_tle"
			savePrefBtnId="cust-ordered-save-pref"
			clearFilterBtnId="cust-ordered-clear-pref"
			exportBtn=exportBtn
			headerBarClass="grid-header-no-bar"
			exportBtnId="ordered-export-btn"
			savePrefBtn=false
			clearFilterBtn=false
			subFltrClearBtn=false
			subFltrClearId="cust-ordered-clear-sub-ftr"
			/>
	</div>
	</div>
           
<div class="row">
<div class="col-md-12 col-lg-12 col-sm-12" id="trans-recent-grid">
		<form method="post" id="recent-transactions-form" novalidate="novalidate" data-toggle="validator">
			<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
			<input type="hidden" name="partyId" value="${inputContext.partyId?if_exists}"/>
			<input type="hidden" name="isRecentTransaction" value="Y"/>
			<input type="hidden" name="orderByColumn" value="${orderByColumn!} DESC"/>
			<input type="hidden" id="recentTransactionsInstanceId" value="${recentTransactionsInstanceId!}"/>
		</form>
		<#-- 
		<@AgGrid
		gridheadertitle="Recent 5 Transactions as on "
		gridheaderid="recent-5-transaction"
		savePrefBtn=savePrefBtn
		clearFilterBtn=clearFilterBtn
		exportBtn=exportBtn
		refreshPrefBtn=refreshPrefBtn
		subFltrClearBtn = subFltrClearBtn
		insertBtn=false
		updateBtn=false
		removeBtn=false
		headerextra=viewAll!
		refreshPrefBtnId="transactions-refresh-pref-btn"
		savePrefBtnId="transactions-save-pref-btn"
		subFltrClearId="transactions-sub-filter-clear-btn"
		clearFilterBtnId="transactions-clear-filter-btn"
		exportBtnId="transactions-export-btn"
		removeBtnId="transactions-remove-btn"
		userid="${userLogin.userLoginId}"
		shownotifications="true"
		instanceid="${recentTransactionsInstanceId}"
		autosizeallcol="true"
		debug="true"
		/> -->
		<#-- 
		<div id="trans-recent1" >
			<@headerH2 title="Recent 5 Transactions as on" id="recent-5-transaction" class="float-left sub-txt"/>
		</div>
		-->
		<@fioGrid 
			id="recent-transactions"
			instanceId="${recentTransactionsInstanceId}"
			jsLoc="/common-portal-resource/js/ag-grid/order/recent-transactions.js"
			headerLabel="Recent 5 Transactions as on"
			headerId="recent_trans_title"
			savePrefBtnId="recent-trans-save-pref"
			clearFilterBtnId="recent-trans-clear-pref"
			subFltrClearId="recent-trans-clear-sub-ftr"
			headerBarClass="grid-header-no-bar"
			exportBtn=exportBtn
			exportBtnId="transactions-export-btn"
			savePrefBtn=false
			clearFilterBtn=false
			subFltrClearBtn=false
			/>
			<#-- 
		<div id="trans-recent1" class="popup-agtitle">
			<@headerH2 title="Recent 5 Transactions as on" id="recent-5-transaction"! class="float-left sub-txt"/>
		</div>
		<div id="recent-transactions" style="width: 100%;margin-top: 20px;" class="ag-theme-balham"></div>
		<script type="text/javascript" src="/common-portal-resource/js/ag-grid/order/recent-transactions.js"></script>
		 -->
</div>
</div>
</#if>

<style>
span#cust_help_btn>a{
	margin-top: auto !important;
}
</style>

<form name="actDeactPartyForm" id="actDeactPartyForm" action="activateDeactivateParty" method="post">
	<input type="hidden" name="partyId" id="partyId" value="${parameters.partyId!}">
	<input type="hidden" name="enabled" id="enabled" value="${parameters.enabled!}">
</form>

<@reassignPicker 
    instanceId="partyResponsible"
    />

<#assign emailTemplateId = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "CUSTOMER_EMAIL_INVITE")?if_exists>
<#assign resetPasswordTemplateId = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "CUSTOMER_PASSWORD_RESET")?if_exists>
<div id="confirmationModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <span id="message">Do you want to replace the loyalty number?</span>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            </div>
            <div>
            </div>
            <div class="modal-footer">
                <a type="button" class="btn btn-sm btn-primary navbar-dark" href="/customer-portal/control/replaceLoyalty?partyId=${partyId!}&externalLoginKey=${externalLoginKey!}">Replace</a>
                <a type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" onclick="return false;">Cancel</a>
            </div>
        </div>
    </div>
</div>
<div>
<#include "component://customer-portal/webapp/customer-portal/call-list-customer/modal_window.ftl"/>
<div>
<form id="removeBirthdayDate" action="removeBirthDate">
		<input type="hidden" id="partyId" name="partyId" value="${inputContext.partyId?if_exists}"/>
	</form>
</div>
<@editBirthDayDate 
	instanceId="birthdayDatePopup"
	/>
</div>
<script>
$(document).ready(function() {

	$("#recent_trans_title").text($("#recent_trans_title").text() + " " +
			new Date().toLocaleString('en-us', {
				month: 'long',
				year: 'numeric',
				day: 'numeric'
			}));
});

$(document).ready(function() {
	$('#cust-activate').on('click', function(e) {
		$("#actDeactPartyForm #enabled").val('Y');
		$("#actDeactPartyForm").submit();
	});

	$('#cust-deactivate').on('click', function(e) {
		$("#actDeactPartyForm #enabled").val('N');
		$("#actDeactPartyForm").submit();
	});

	$('#invite-customer-user').on('click', function(e) {
		var partyId = "${inputContext.partyId!requestParameters.partyId!}";
		var emailTemplateId = "${emailTemplateId!''}";
		$.ajax({
			type: "POST",
			url: "createInviteUser",
			data: {
				"partyId": partyId,
				"emailTemplateId": emailTemplateId,
			},
			sync: true,
			success: function(data) {
				var response = data.response;
				var message = data.responseMessage;
				showAlert(response, message);
				location.reload();
			},
			error: function(data) {
				var message = data.responseMessage;
				showAlert("error", message);
				location.reload();
			}
		});
		e.preventDefault();
	});

	$('#reset-customer-pwd').on('click', function(e) {
		var partyId = "${inputContext.partyId!requestParameters.partyId!}";
		var emailTemplateId = "${resetPasswordTemplateId!''}";
		$.ajax({
			type: "POST",
			url: "resetPassword",
			data: {
				"partyId": partyId,
				"emailTemplateId": emailTemplateId,
			},
			sync: true,
			success: function(data) {
				var response = data.response;
				var message = data.responseMessage;
				showAlert(response, message);
				location.reload();
			},
			error: function(data) {
				var message = data.responseMessage;
				showAlert("error", message);
				location.reload();
			}
		});
		e.preventDefault();
	});

	$('#user-status').on('click', function(e) {
		var userStatus = "${isUserEnabled!}";
		var isEnabled = "Y";
		if (userStatus === "Y")
			isEnabled = "N"
		var partyId = "${inputContext.partyId!requestParameters.partyId!}";
		$.ajax({
			type: "POST",
			url: "resetUserLoginStatus",
			data: {
				"partyId": partyId,
				"userLoginStatus": isEnabled
			},
			sync: true,
			success: function(data) {
				var response = data.response;
				var message = data.responseMessage;
				showAlert(response, message);
				location.reload();
			},
			error: function(data) {
				var message = data.responseMessage;
				showAlert("error", message);
				location.reload();
			}
		});
		e.preventDefault();
	});

	$('#viewOrdersList').click(function() {
		$('.nav-tabs a[href="#c-orders"]').tab('show');
	});
	$("#oneClick").click(function() {
		let callStatus = $("#callStatusId").val();
		let callBackDate = $("#callBackdate").val();
		console.log("callStatus"+callStatus);
		console.log("callBackDate"+callBackDate);
		if(callStatus !="" && callStatus !=null|| callBackDate!="" && callBackDate!=null){
		
			$("#updateDate #callStatus").val(callStatus);
			$("#updateDate #callBackDate").val(callBackDate);
		console.log("callBackDate"+$("#updateDate #callBackDate").val());
		console.log("callStatus"+$("#updateDate #callStatus").val());

			$("#updateDate").submit();
		}else{
			event.preventDefault();
			showAlert("error","Please select any status or call back date");
		}
	});
});

<#if isEreceiptEnabled?has_content && isEreceiptEnabled?if_exists=="Y">
	ereceiptEnable();
</#if>
<#if isLoyaltyEnable?has_content && isLoyaltyEnable='Y'>
if ($('#isLoyaltyEnabled')) {
	if ($('#isLoyaltyEnabled').text() && $('#isLoyaltyEnabled').text().trim() == "Y") {
		$('#isLoyaltyEnabled').text("");
		$('#isLoyaltyEnabled').prepend('<input onchange="toggleLoyalty()" type="checkbox" checked id="isLoyaltyEnabledCheck" name="isLoyaltyEnabledCheck" value="">')
		$('#isLoyaltyEnabled').append("Yes");
	} else {
		$('#isLoyaltyEnabled').text("");
		$('#isLoyaltyEnabled').prepend('<input onchange="toggleLoyalty()" type="checkbox" id="isLoyaltyEnabledCheck" name="isLoyaltyEnabledCheck" value="">')
		$('#isLoyaltyEnabled').append("No");
	}
}
<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists!="Y" || !Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)??>
if ($('#loyaltyId')) {
	if ($('#loyaltyId').text() && $('#loyaltyId').text().trim() != "") {
		$('#loyaltyId').append('<a onclick="replaceLoyaltyNumber()" href="#">(click here to replace loyalty)</a>')
	} else {
		$('#loyaltyId').append('<a onclick="return createLoyaltyNumber()" href="/customer-portal/control/getAssignedOrNewLoyaltyNumber?partyId=${partyId!}&updateToPartyAttribute=N&externalLoginKey=${externalLoginKey!}">Add Loyalty Number</a>')
	}
}
</#if>

function replaceLoyaltyNumber() {
	$('#confirmationModal').modal('show');
}

function toggleLoyalty() {
	var isLoyaltyEnabled = "";
	if ($('#isLoyaltyEnabledCheck').prop('checked') == true) {
		isLoyaltyEnabled = "Y";
	} else {
		isLoyaltyEnabled = "N";
	}
	window.location.href = "/customer-portal/control/updateloyalid?partyId=${partyId!}&isLoyaltyEnabled=" + isLoyaltyEnabled + "&externalLoginKey=${externalLoginKey!}";
}

function createLoyaltyNumber() {
	if (!$('#isLoyaltyEnabledCheck').prop('checked') == true) {
		showAlert("error", "Please Enable Loyalty Flag as Yes");
		return false;
	}
	return true;
}
</#if>
function ereceiptEnable(){
if ($('#isEreceiptEnabled')) {
	if ($('#isEreceiptEnabled').text() && $('#isEreceiptEnabled').text().trim() == "Y") {
		$('#isEreceiptEnabled').text("");
		$('#isEreceiptEnabled').prepend('<input onchange="toggleEreceipt()" type="checkbox" checked id="isEreceiptEnabledCheck" name="isEreceiptEnabledChecked" value="">')
		$('#isEreceiptEnabled').append("Yes");
	} else {
		$('#isEreceiptEnabled').text("");
		$('#isEreceiptEnabled').prepend('<input onchange="toggleEreceipt()" type="checkbox" id="isEreceiptEnabledCheck" name="isEreceiptEnabledChecked" value="">')
		$('#isEreceiptEnabled').append("No");
	}
}
}
function toggleEreceipt() {
let partyId = "${inputContext.partyId!requestParameters.partyId!}";
let isEreceiptEnabled = "";
	if ($('#isEreceiptEnabledCheck').prop('checked') == true) {
		isEreceiptEnabled = "Y";
	} else {
		isEreceiptEnabled = "N";
	}
	$.ajax({
			type: "POST",
			url: "updateEreceiptEnabled",
			data: {
				"partyId": partyId,
				"isEreceiptEnabled": isEreceiptEnabled,
				"attrValue": isEreceiptEnabled,
				"attrName": "IS_ERCPT_ENABLED",
				"externalLoginKey":'${requestAttributes.externalLoginKey!}'
			},
			sync: true,
			success: function(data) {
				 if (isEreceiptEnabled == "Y") {
					$('#isEreceiptEnabled').text("");
					$('#isEreceiptEnabled').prepend('<input onchange="toggleEreceipt()" type="checkbox" checked id="isEreceiptEnabledCheck" name="isEreceiptEnabledChecked" value="">')
					$('#isEreceiptEnabled').append("Yes");
					showAlert("success", "Ereceipt enabled successfully");
				} else {
					$('#isEreceiptEnabled').text("");
					$('#isEreceiptEnabled').prepend('<input onchange="toggleEreceipt()" type="checkbox" id="isEreceiptEnabledCheck" name="isEreceiptEnabledChecked" value="">')
					$('#isEreceiptEnabled').append("No");
					showAlert("success", "Ereceipt disabled successfully");
				}
			},
			error: function(data) {
				showAlert("error", "Error occur in ereceipt enabled");
				location.reload();
			}
		});
}
	if ($('#clv_VF')) {
		if ($('#clv_VF').text()!="-") {
		$('#clv_VF').prepend('<span><i class="fa fa-usd" aria-hidden="true"></i></span>').attr('id', ' ' + $('#clv_VF').attr('id'));
		}
	}
	if ($('#ytd_VF')) {
		if ($('#ytd_VF').text()!="-") {
		$('#ytd_VF').prepend('<span><i class="fa fa-usd" aria-hidden="true"></i></span>').attr('id', ' ' + $('#ytd_VF').attr('id'));
		}
	}
	if ($('#lytd_VF')) {
		if ($('#lytd_VF').text()!="-") {
		$('#lytd_VF').prepend('<span><i class="fa fa-usd" aria-hidden="true"></i></span>').attr('id', ' ' + $('#lytd_VF').attr('id'));
		}
	}
	$('#cust-deactivate').on('click', function(e) {
		$("#actDeactPartyForm #enabled").val('N');
		$("#actDeactPartyForm").submit();
	});
	<#if partyId?has_content>
		birthDatePopup();
	</#if>
	function birthDatePopup(){
	if ($('#birthdayFormat')) {
		if ($('#birthdayFormat').text()) {
			$('#birthdayFormat').append('<span data-toggle="modal" data-target="#birthdayDatePopup" title="Edit" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> </span>');
			<#if birthDate?has_content>
				$('#birthdayFormat').append('<span id="removeBirthDate" class="btn btn-primary btn-xs ml-2" data-toggle="confirmation" title="Are you sure?	Do you want to remove"><i class="fa fa-trash" aria-hidden="true"></i> </span>');
			</#if>
		}
	}
	}
	$("#removeBirthDate").click(function() {
		let partyId = $("#removeBirthdayDate #partyId").val();
		if(partyId !=null && partyId !=""){
			event.preventDefault();
			$("#removeBirthdayDate").submit();
		}else{
			showAlert("error","PartyId is empty");
		}
	});
	<#-- <#if openCouponAssigned?has_content>
   		<#list openCouponAssigned as openCoupon>
   		if ($('#couponCount')) {
		if ($('#couponCount').text()) {
			$('#couponCount').append('${openCoupon.coupon?if_exists}<#if openCoupon.thruDate?has_content> - ${openCoupon.thruDate?if_exists}</#if><br/>');
		}
		}
   		</#list>
	</#if>-->
</script>