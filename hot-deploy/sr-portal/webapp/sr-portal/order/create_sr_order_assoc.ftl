<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#include "component://common-portal/webapp/common-portal/order/modal_window.ftl"/>
<input type="hidden" id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
<div class="row">
    <div id="main" role="main">
    
    	<#assign extra = ""/>
    	<#if isEnableSrorderSync?has_content && isEnableSrorderSync == "Y">
    		<#assign extra = '<span id="sync-order-assoc" class="btn btn-xs btn-primary" data-toggle="confirmation" title="${uiLabelMap.SyncConfirmation!}"><i class="fa fa-refresh" aria-hidden="true"></i> Sync</span>'/>
    	</#if>
    
        <#assign extra=extra+'
        <span id="create-order-assoc" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Apply to FSR</span>
        ' />
        <#assign isDisable = true />
		<#assign disableCloseOpTypes = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "DISABLE_CLOSE_OP_TYPES", "") />
		<#if disableCloseOpTypes?has_content && srTypeId?has_content && disableCloseOpTypes.contains(srTypeId) >
			<#assign isDisable = false />
		</#if>
        <#if srStatusId?has_content && (srStatusId == "SR_CLOSED" || srStatusId == "SR_CANCELLED") && isDisable>
			<#assign extra = ""/>
		</#if>
		
		<#assign extra = extra + '<a href="/sr-portal/control/viewServiceRequest?srNumber=${srNumber!}#sr-orders" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
        
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" data-toggle="validator">    
        	<input type="hidden" name="domainEntityType" value="${domainEntityType!}"/>
        	<input type="hidden" name="domainEntityId" value="${domainEntityId!}"/>
        	<input type="hidden" id="isSrOrderAssoc" value="Y"/>
        	<div id="order-assoc-fields"></div>
        	
            <div class="col-lg-12 col-md-12 col-sm-12">
            	<@sectionFrameHeader title="${uiLabelMap.SrOrderAssoc!}" extra=extra />
            	<@dynaScreen 
					instanceId="SR_ORDER_ASSOC"
					modeOfAction="CREATE"
					/>
            </div>
            
        </form>
    </div>
</div>

<style>
#justificationModel .modal-content {
    height: auto !important;
    overflow-x: hidden !important;
    overflow-y: auto !important;
}
</style>

<div id="justificationModel" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
        	<div class="modal-header">
		        <h5 class="modal-title" id=""></h5>
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		    </div>
		    <form name="justificationForm" id="justificationForm" action="" method="POST">
            <div class="modal-body" style="padding-bottom:100px !important;">
                	
        		<@inputHidden name="justificationOld" id="justificationOld" value="${justificationOldProd!}"/>
        		<@inputHidden name="srNumber" id="srNumber" value="${srNumber!}"/>
            	<div class="form-group justificationOldProd" style="">
				    <label for="justificationOldProd" id="justificationOldProd_label" class="col-form-label">Justification for FSR on Old Product <span class="text-danger"> *</span></label>
		           	<div class="justificationOldProd-input">
		               <select class="ui dropdown search form-control fluid show-tick form-control" id="justificationOldProd" name="justificationOldProd" required="" data-live-search="true" autocomplete="off">
		               	<option value="">Please select</option>
		               	<#if justificationList?exists && justificationList?has_content>
		               		<#list justificationList as justification >
            					<option value="${justification.fieldValue!}" <#if justificationOldProd?exists && justificationOldProd?has_content && justificationOldProd==justification.fieldValue!>selected</#if> >${justification.description!}</option>
            				</#list>
            			</#if>
		               </select>
		                <div class="help-block with-errors" id="justificationOldProd_error"></div>
		            </div>
		            
		        </div>
			        
            </div>
            <div class="modal-footer">
		        <input type="button" id="justification-btn" value="Save" class="btn btn-primary m5">
		    </div>
		    </form>
        </div>
    </div>
</div>

<#-- <@dealerPicker 
instanceId="dealerPicker"
/>-->
<@findOrderDealerPicker 
instanceId="findOrderDealerPicker"
/>
<@orderPicker 
instanceId="orderPicker"
/>

<@showDescription 
instanceId="show-des-modal"
/>

<div class="row" style="width:100%">
  	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
  	<div class="border-b pt-2">
        <@headerH2 title="${uiLabelMap.orderLines!} - <span class='qty-note'>(Please enter Applied QTY on the grid not in Search box)</span>" class="float-left"/>
        <div class="float-right">
        
        <#-- <span id="remove-screen-field-btn" title="Remove" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-times" aria-hidden="true"></i> Remove </span> -->
        
        </div>
        <div class="clearfix"></div>
    </div>  
  <#--   <script src="/bootstrap/js/ag-grid-community.min.js"></script>
    <link rel="stylesheet" href="/bootstrap/css/ag-grid.css">
    <link rel="stylesheet" href="/bootstrap/css/ag-theme-balham.css">-->
    	
  	<div id="order-line-grid" style="width: 100%;" class="ag-theme-balham"></div>
  	<script type="text/javascript" src="/sr-portal-resource/js/ag-grid/order/order_line.js"></script>
           
  	</div>
</div>

<div class="row" style="width:100%">
  	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
  	<div class="border-b pt-2">
        <@headerH2 title="${uiLabelMap.assignedLines!}" class="float-left"/>
        <div class="float-right">
        	<form method="post" id="order-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">	
				<input type="hidden" name="domainEntityType" value="${domainEntityType!}"/>
	        	<input type="hidden" name="domainEntityId" value="${domainEntityId!}"/>
				<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>	
				<input type="hidden" name="srNumber" value="${srNumber?if_exists}"/>
			</form>
        </div>
        <div class="clearfix"></div>
    </div>  
    	
  	<#-- <@AgGrid
	gridheadertitle=""
	gridheaderid="order-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	refreshPrefBtnId="order-refresh-pref-btn"
	savePrefBtnId="order-save-pref-btn"
	clearFilterBtnId="order-clear-filter-btn"
	subFltrClearId="order-sub-filter-clear-btn"
	exportBtnId="order-export-btn"
	removeBtnId="order-remove-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="LIST_SR_ORDERS_ASSOC" 
    autosizeallcol="true"
    debug="false"
    />    
	
  	<script type="text/javascript" src="/sr-portal-resource/js/ag-grid/order/find-order-assoc.js"></script>-->
  	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

<#assign rightContent='
		<button id="refresh-order-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
		<@fioGrid 
			id="order-assoc-grid"
			instanceId="LIST_SR_ORDERS_ASSOC"
			jsLoc="/sr-portal-resource/js/ag-grid/order/find-order-assoc.js"
			headerLabel="Issued Materials"
			headerId="order-assoc-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			subFltrClearBtn = true
			savePrefBtnId ="order-assoc-save-pref-btn"
			clearFilterBtnId ="order-assoc-clear-filter-btn"
			exportBtnId="order-assoc-list-export-btn"
			subFltrClearId="order-assoc-sub-filter-clear-btn"
			headerExtra=rightContent!
			/>
           
  	</div>
</div>

<script>

$(document).ready(function() {

$("#orderDate_picker").on("dp.change", function (e) {
 	console.log('orderDate changed: '+($("#mainFrom input[name=orderDate]").val()));
 	let isExists = isOrderAssocExists($("#mainFrom input[name=orderId]").val(), $("#mainFrom input[id=orderId_alter]").val(), $("#mainFrom input[name='srNumber']").val(), "${requestAttributes.externalLoginKey!}");
	if (!isExists) {
		getOrderLineRowData();
	}
});

$("#locationId").change(function() {
	console.log('location changed: '+$(this).val());
	let isExists = isOrderAssocExists($("#mainFrom input[name=orderId]").val(), $("#mainFrom input[id=orderId_alter]").val(), $("#mainFrom input[name='srNumber']").val(), "${requestAttributes.externalLoginKey!}");
	if (!isExists) {
		getOrderLineRowData();
	}
});

$("#orderId_desc").change(function() {
	console.log('orderId changed: '+$(this).val());
		
	let data = getOrderDetail($("#mainFrom input[name=orderId]").val(), $("#mainFrom input[id=orderId_alter]").val(), $("#mainFrom input[name='srNumber']").val(), "${requestAttributes.externalLoginKey!}");
	if (data.code == 200) {
    	if (data.orderDate) {
    		$("#orderDate").val(data.orderDate);
    	}
    	
    	if (data.locationId) {
    		$('#locationId').val(data.locationId);
			$('#locationId').trigger('change');
			$("div.ui.dropdown.search.form-control.fluid.show-tick.locationId.selection > i").addClass("clear");
    	}
    
    	//$("#actualQty").html(data.totalActualQty);
    	//$("#appliedQty").html(data.totalAppliedQty);
	    //$("#availableQty").html(data.totalAvailQty);
	}	
	
});

$("#sync-order-assoc").click(function() {
	
	var externalId = $("#mainFrom input[id=orderId_alter]").val();
	if(!externalId){
		externalId = $("#mainFrom input[id=orderId_desc]").val();
	}
	
	$.ajax({
		type: "POST",
     	url: "/sr-portal/control/syncReebOrder",
        data: {"orderId": $("#mainFrom input[name=orderId]").val(), "orderDate": $("#mainFrom input[name=orderDate]").val(), "location": $("#locationId").val(), "externalId": externalId, "srNumber": $("#mainFrom input[name='srNumber']").val(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (data) {   
            if (data.code == 200) {
            	showAlert("success", "Successfully sync order");
            	//$("#mainFrom input[id=orderId_desc]").trigger('change');
				getOrderLineRowData();
            } else {
            	showAlert("error", data.message);
            }
        }
        
	});
	
});

});

</script>