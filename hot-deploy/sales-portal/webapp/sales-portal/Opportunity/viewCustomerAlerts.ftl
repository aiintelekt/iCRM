<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign salesOpportunityId = "">
<#assign srNumber = "">
<#assign alertTrackingId = '${requestParameters.alertTrackingId?if_exists}'>
<#assign alertTrackingList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("alertEntityName","alertEntityReferenceId").from("AlertTrackingHistory").where("alertTrackingId",alertTrackingId).queryFirst())?if_exists />
 <#if alertTrackingList?has_content>
 	<#assign entityName = "${alertTrackingList.alertEntityName?if_exists}"> 
 	<#assign refId = "${alertTrackingList.alertEntityReferenceId?if_exists}"> 
 	<#if entityName?has_content && entityName=="SERVICE_REQUEST" && refId?has_content>
 		<#assign srNumber = refId>
 	<#elseif entityName?has_content && entityName=="SALES_OPPORTUNITY" && refId?has_content>	
 		<#assign salesOpportunityId = refId>
 	</#if>
 </#if>
 
<div class="row">
	<div id="main" role="main">
    	<div class="top-band bg-light">
        	<div class="col-lg-12 col-md-12 col-sm-12">
            	<div class="row">
                	<div class="col-lg-12 col-md-12 col-sm-12">
                  		<div class="text-right">  
                  			<#if salesOpportunityId?has_content>
				    			<a href="/sales-portal/control/viewOpportunity?salesOpportunityId=${salesOpportunityId?if_exists}" class="btn btn-primary btn-xs mt-1"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>
                  			<#elseif  srNumber?has_content>
                  				<a href="/sr-portal/control/viewServiceRequest?srNumber=${srNumber?if_exists}" class="btn btn-primary btn-xs mt-1"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>
                  			</#if>
                  		</div>
                	</div>
                </div>
            </div>
        </div>
        <div class="col-lg-12 col-md-12 col-sm-12 border-bottom mid">
        <@inputHidden name="alertTrackingId" id="alertTrackingId" value="${requestParameters.alertTrackingId?if_exists}"/>
        <@inputHidden name="alertEntityReferenceId" id="alertEntityReferenceId" />
        	<div class="row">
            	<div class="col-lg-6 col-md-12 col-sm-12">
                	<small> Alert : Information</small> 
                	<@headerH2
        			title="${alertTrackingId}"
       				/>
              	</div>
           		<div class="col-lg-6 col-md-12 col-sm-12">
			  		<div class="bd-callout">
                  		<small>Status</small>
                  		 <@headerH5
          					id="alertStatusId"
           					title=""
           				 />
                	</div>
			  		<div class="bd-callout">
                		<small>Expiry Date <span class="text-danger">*</span> </small>
                  		<@headerH5
        					title=""
        					id="alertEndDt"
       					/>
                	</div>
            	</div>
        	</div>
        </div>
        <div class="col-lg-12 col-md-12 col-sm-12">
        <div class="clearfix"></div>
            <div class="page-header pt-2">
            	<@headerH2
        			title="Customer Summary"
       			/>
            </div>
            <div class="row">
            	<div class="col-md-12 col-lg-6 col-sm-12 ">
                	<@displayCell
     					id="customerName"
     					label="Customer"
     					value=""
   					/>
   					<@displayCell
     					id="customerId"
     					label="CIN"
     					value=""
   					/>
              	</div>
              	<div class="col-md-12 col-lg-6 col-sm-12">
                	<@displayCell
     					id="customerIdSuffix"
     					label="CIN Suffix"
     					value=""
   					/>
              	</div>
            </div>
            <div class="clearfix"></div>
            <div class="page-header pt-2">
            	<@headerH2
        			title="General"
       			/>
            </div>
           	<div class="row">
            	<div class="col-md-12 col-lg-6 col-sm-12 ">
                	<@displayCell
     					id="alertCategory"
     					label="Alert Category"
     					value=""
   					/>
                	<@displayCell
     					id="alertDescription"
     					label="Alert Description"
     					value=""
   					/>
                	<@displayCell
     					id="action"
     					label="Recommended Action"
     					value=""
   					/>
   					<@displayCell
     					id="alertStatus"
     					label="Alert Status"
     					value=""
   					/>
   					<@displayCell
     					id="remarks"
     					label="Remarks"
     					value=""
   					/>
              	</div>
              	<div class="col-md-12 col-lg-6 col-sm-12">
                	<@displayCell
     					id="accountId"
     					label="Account ID"
     					value=""
   					/>
   					<@displayCell
     					id="priorityDesc"
     					label="Alert Priority"
     					value=""
   					/>
   					<@displayCell
     					id="alertStartDate"
     					label="Tigger Date"
     					value=""
   					/>
   					<@displayCell
     					id="alertEndDate"
     					label="Expiry Date"
     					value=""
   					/>
   					<@displayCell
     					id="owner"
     					label="Owner"
     					value=""
   					/>
                </div>	   
            </div>
			<div class="clearfix"></div>
            <div class="page-header pt-2">
            	<@headerH2
        			title="Opportunities"
       			/>
            </div>
			<div class="table-responsive">
                <div id="alertGrid" style="width: 100%;" class="ag-theme-balham"></div>
                <script type="text/javascript" src="/sales-portal-resource/js/ag-grid/alertGrid.js"></script>
            </div>
            <div class="page-header pt-2">
            	<@headerH2
        			title="Administration"
       			/>
            </div>
            <div class="row">
            	<div class="col-md-12 col-lg-6 col-sm-12 ">
                	<@displayCell
     					id="alertCreatedOn"
     					label="Created On"
     					value=""
   					/>
                	<@displayCell
     					id="alertModifiedOn"
     					label="Modified On"
     					value=""
   					/>
              	</div>
              	<div class="col-md-12 col-lg-6 col-sm-12">
                	<@displayCell
     					id="alertCreatedBy"
     					label="Created By"
     					value=""
   					/>
                	<@displayCell
     					id="alertModifiedBy"
     					label="Modified By"
     					value=""
   					/>
              	</div>
            </div>
    	</div>
    </div>
</div>
<script>

var alertTrackingId  = $("#alertTrackingId").val();

 $.ajax({
            type: "POST",
            url: "getCustomerAlertDetails",
            data: { "alertTrackingId": alertTrackingId},
            async: false,
            success: function(data) {
                 for (var i = 0; i < data.length; i++) {
                  	var alertData = data[i];
                 	var alertCategoryDesc = alertData.alertCategoryDesc;
                 	var alertDescription = alertData.alertInfo;
                 	var priorityDesc = alertData.priorityDesc;
                 	var alertStartDate = alertData.alertStartDate;
                 	var alertEndDate = alertData.alertEndDate;
                 	var isActiveDesc = alertData.isActiveDesc;
                 	var alertCreatedOn = alertData.alertCreatedOn;
                 	var alertCreatedBy = alertData.alertCreatedBy;
                 	var alertModifiedOn = alertData.alertModifiedOn;
                 	var alertModifiedBy = alertData.alertModifiedBy;
                 	var customerId = alertData.customerId;
                 	var customerName = alertData.customerName;
                 	var alertEntityReferenceId = alertData.alertEntityReferenceId;
                 	var remarks = alertData.remarks;
                 	
                 	$("#alertEntityReferenceId").val(alertEntityReferenceId);
                 	$("#remarks").html(remarks);
                 	$("#alertCategory").html(alertCategoryDesc);
                 	$("#alertDescription").html(alertDescription);
                 	$("#priorityDesc").html(priorityDesc);
                 	$("#alertStartDate").html(alertStartDate);
                 	$("#alertEndDate").html(alertEndDate);
                 	$("#alertEndDt").html(alertEndDate);
                 	$("#alertStatus").html(isActiveDesc);
                 	$("#alertStatusId").html(isActiveDesc);
                 	$("#alertCreatedOn").html(alertCreatedOn);
                 	$("#alertCreatedBy").html(alertCreatedBy);
                 	$("#alertModifiedOn").html(alertModifiedOn);
                 	$("#alertModifiedBy").html(alertModifiedBy);
                 	$("#customerId").html(customerId);
                 	$("#customerName").html(customerName);
                 	
                 }
            }
        });



</script>







