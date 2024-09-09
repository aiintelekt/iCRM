<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#macro addStoreReceipt instanceId>

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Add Store Receipts</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<form method="post" id="findAddStoreReceipt" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
            		
            		<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
    				<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
    				<input type="hidden" name="isStoreReceipt" value="Y">
    				<input type="hidden" name="isFullLoad" value="Y">
    			</form>	
    			<#-- 	
            	<div class="card-header">
            		
    				
					
					<div class="row">
						<div class="col-md-4 col-md-4 form-horizontal">
						
							<@dropdownCell
				            	id="marketingCampaignId"
				            	options=campaignList!     
				            	required=false
				            	allowEmpty=false
				            	placeholder="Select Campaign Name"
							/>  
						
							<@inputRow 
							id="salesOpportunityId"
							placeholder=uiLabelMap.opportunityNumber
							inputColSize="col-sm-12"
							iconClass="fa fa-user"
							required=false
							/> 
							
					  </div>
				</div>
				<div class="row p-2">
			        <div class="col-lg-12 col-md-12 col-sm-12">
						<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
			                <@button 
			            		label="${uiLabelMap.Find}"
			            		id="add-relate-opp-search-btn"
			            	/>
			           		<@reset
			        			label="${uiLabelMap.Reset}"
			        		/>
			        	</div>
			    	</div>
				</div>
				 -->	
			
			
			<#assign rightContent='
				<button id="refresh-btn-add-storereceipt" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
				<span id="add-btn-storereceipt" title="Add" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Add </span>
				' />   
						
			<#-- <@AgGrid
				gridheadertitle=""
				gridheaderid="${instanceId!}_storereceipt-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				headerextra=rightContent
				refreshPrefBtnId="add-storereceipt-refresh-pref-btn"
				savePrefBtnId="add-storereceipt-save-pref-btn"
				clearFilterBtnId="add-storereceipt-clear-filter-btn"
				exportBtnId="add-storereceipt-export-btn"					
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="STORE_RECEIPTS_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/store/find-add-store-receipt.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="add-storeReceiptGrid"
						instanceId="ADD_STORE_RECEIPTS_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/store/find-add-store-receipt.js"
						headerLabel="Store Receipts"
						headerId="add-storeReceipt-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="add-storeReceipt-clear-pref-btn"
						subFltrClearId="add-storeReceipt-sub-filter-clear-btn"
						savePrefBtnId="add-storeReceipt-save-filter-btn"
						headerExtra=rightContent!
						exportBtn=true
						exportBtnId="add-storereceipt-export-btn"
						/>
      	</div>
	      	<div class="modal-footer" style="border-top: 0px;padding-top: 0px;">
	             
	        </div>
    	</div>
  	</div>
</div>

<script>

jQuery(document).ready(function() {



});

</script>

</#macro>
