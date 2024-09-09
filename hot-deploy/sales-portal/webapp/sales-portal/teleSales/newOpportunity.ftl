<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sr-portal/webapp/sr-portal/services/findCustomerModal.ftl"/>
<div class="row">
   <div id="main" role="main">
     <div class="col-lg-12 col-md-12 col-sm-12">
        <input type="hidden" id="salesOppId" name="salesOppId" value="${requestParameters.salesOpportunityId?if_exists}"/>
        <#assign salesOpportunityId = '${requestParameters.salesOpportunityId?if_exists}'>
        <#assign srNumber = '${requestParameters.srNumber?if_exists}'>
        <#assign customerId = "">
        <@inputHidden name="srNumber" id="srNumber" value="${requestParameters.srNumber?if_exists}"/>
        <@inputHidden name="salesOpportunityId" id="salesOpportunityId" value="${requestParameters.salesOpportunityId?if_exists}"/>
        <#if salesOpportunityId?has_content>
        	<#assign partyIdList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("partyId","roleTypeId").from("SalesOpportunityRole").where("salesOpportunityId",salesOpportunityId).queryFirst())?if_exists />
            <#assign partyId = "">
            <#if partyIdList?has_content>
            	<#assign partyId = "${partyIdList.partyId?if_exists}"> 
            </#if>
            <#assign customerIdList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue","partyId").from("PartyIdentification").where("partyId",partyId).queryFirst())?if_exists />
			<#if customerIdList?has_content>
            	<#assign customerId = "${customerIdList.idValue?if_exists}"> 
            </#if>
            <#assign extra='
                       	 	<a href="/sales-portal/control/viewOpportunity?salesOpportunityId=${salesOpportunityId?if_exists}" class="btn btn-primary btn-xs mt-1"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>
                       	   ' />
        <#elseif srNumber?has_content>
            <#assign custRequestDetails = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("fromPartyId","custRequestId").from("CustRequest").where("custRequestId",srNumber).queryFirst())?if_exists />
            <#assign partyId = "">
            <#if custRequestDetails?has_content>
            	<#assign partyId = "${custRequestDetails.fromPartyId?if_exists}"> 
        	</#if>
        	<#assign customerIdList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue","partyId").from("PartyIdentification").where("partyId",partyId).queryFirst())?if_exists />
			<#if customerIdList?has_content>
            	<#assign customerId = "${customerIdList.idValue?if_exists}"> 
            </#if>
            <#assign extra='
                       	 	<a href="/sr-portal/control/viewServiceRequest?srNumber=${srNumber?if_exists}" class="btn btn-primary btn-xs mt-1"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>
                       	   ' />
		<#else>
        <#assign extra='
                       	 <a href="findOpportunity" class="btn btn-primary btn-xs mt-1"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>
                       ' /> 
		</#if>
		<#assign opportunityState ="">
		<#assign oppStateData = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("enumId","description").from("Enumeration").where("enumId","SELF_CREATE").queryOne())?if_exists />
        <#if oppStateData?has_content>
        	<#assign opportunityState = "${oppStateData.enumId?if_exists}"> 
        </#if>
        <@inputHidden name="customerId" id="customerId" value="${customerId?if_exists}"/>
        <@inputHidden name="flag" id="flag" value="Y"/>
        <@inputHidden name="flag" id="isRequestFromAddSalesOpportunity" value="Y"/>
        <#if salesOpportunityId?has_content || srNumber?has_content>
        	<#assign extraLeft='
                            <a id="createProspect" title="Create Prospect" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-plus"></i> Create Prospect</a>
                            <a id="createNonCrm" title="Create Non CRM" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-times"></i>  Create Non CRM</a>
                           ' />
        <#else>
        	<#assign extraLeft='
                           	<a id="findcustomerSr" title="Find Customer" href="#" class="btn btn-primary btn-xs" data-toggle="modal" data-target="#findcustomer" ><i class="fa fa-search"></i> Find Customer</a>
                            <a id="createProspect" title="Create Prospect" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-plus"></i> Create Prospect</a>
                            <a id="createNonCrm" title="Create Non CRM" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-times"></i>  Create Non CRM</a>
                           ' />
        </#if>
   			 <@pageSectionHeader title="${uiLabelMap.addOpportunity!}" extraLeft=extraLeft extra=extra/>
   			 <#if salesOpportunityId?has_content || srNumber?has_content>
   			 	<div class="card-head margin-adj mt-2" id="cp">
                  ${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#cusNameCommon")}
             	</div> 
             	<div class="card-head margin-adj mt-2">
                    ${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#customerRowAddOpportunity")}
             	</div>
   			 <#else>
   			 	<div class="card-head margin-adj mt-0 d-none" id="cp">
                  ${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#cusNameCommon")}
             	</div> 
             	<div class="card-head margin-adj mt-0 d-none">
                    ${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#customerRowAddOpportunity")}
             	</div>
   			 </#if>
             <form method="post" action="#" name="createSalesOpportunity" id="createSalesOpportunity" data-toggle="validator">
             	<@pageSectionHeader title="${uiLabelMap.opportunityDetails}" />
             	
             	<#if requestParameters.srNumber?has_content>
             		<@inputHidden name="linkedFrom" id="linkedFrom" value="${requestParameters.srNumber?if_exists}"/>
             	</#if>
             	<@inputHidden name="cNo" id="cNo"/>
             	<@inputHidden name="dataSourceDataId" id="dataSourceDataId" value=""/>
             	<@inputHidden name="partyId" id="partyId" value="${partyId?if_exists}"/>
				<@inputHidden name="opportunityState" id="opportunityState" value="${opportunityState?if_exists}"/>
                <div class="row p-2">
                	<div class="col-md-12 col-lg-6 col-sm-12 ">
                    	<#assign channelEnum = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","SALES_CHANNEL_ID").orderBy("sequenceId").queryList()?if_exists />    
           				<#assign channelEnumList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(channelEnum,"enumId","description",false)?if_exists /> 
                        <@dropdownCell 
                        	id="typeEnumId" 
                            label=uiLabelMap.channel
                            required=true
                            allowEmpty = false
                            value=typeEnumId?if_exists
                            options=channelEnumList
                        />
                        <#assign prodCatalog = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("prodCatalogId","catalogName").from("ProdCatalog").queryList()?if_exists />    
           				<#assign prodCatalogList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(prodCatalog,"prodCatalogId","catalogName",false)?if_exists /> 
                        <@dropdownCell 
                        	id="prodCatalogId" 
                            label=uiLabelMap.productLevel1
                            placeholder="Please Select"
                            allowEmpty = true
                            options=prodCatalogList
                        />
                        <@dropdownCell 
                        	id="productCategoryId" 
                            label=uiLabelMap.productLevel2
                            placeholder="Please Select"
                            allowEmpty = true
                        />
                        <@dropdownCell 
                        	id="productId" 
                            label=uiLabelMap.productName
                            placeholder="Please Select"
                            allowEmpty = true
                        />
                        <@inputRow id="estimatedAmount"  label=uiLabelMap.totalsalesAmount placeholder="Total Sales Amount"/>
                        <@inputArea
                    		id="remarks"
                    		label="${uiLabelMap.remarks!}"
                    		placeholder="${uiLabelMap.remarks!}"
                    		required=false
                    		maxlength=100
                    	/>
                    </div>
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                    
                    	<#assign dataSource = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("dataSourceId","description").from("DataSource").queryList()?if_exists />    
           				<#assign dataSourceList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(dataSource,"dataSourceId","description",false)?if_exists /> 
                    	<@dropdownCell 
                        	id="dataSourceId" 
                            label=uiLabelMap.referralSource
                            allowEmpty = true
                            placeholder="Please Select"
                            options=dataSourceList
                            
                         />
                        <@inputRow id="dataSourceDesc"  label=uiLabelMap.referralDescription placeholder="Referral Description" readonly=true />
                        <@dropdownCell
	                    	id="marketingCampaignId"
	                  		label=uiLabelMap.originatingCampaign
	                    	required=false
	                    	value="${requestParameters.marketingCampaignId?if_exists}"
	                    	allowEmpty=false
	                    	placeholder="Please Select"
	                    /> 
	                    <@dropdownCell
	                    	id="originatingSR"
	                  		label=uiLabelMap.originatingSR
	                    	required=false
	                    	value='${requestParameters.srNumber?if_exists}'
	                    	allowEmpty=false
	                    	placeholder="Please Select"
	                    />
	                    <@dropdownCell
	                    	id="originatingAlert"
	                  		label=uiLabelMap.originatingAlert
	                    	required=false
	                    	value='${requestParameters.alertNumber?if_exists}'
	                    	allowEmpty=false
	                    	placeholder="Please Select"
	                    />
	                    <#if requestParameters.srNumber?has_content>
		                    <@displayCell
				              label=uiLabelMap.linkedFrom
				              value='${requestParameters.srNumber?if_exists}'
				            />
			            </#if>
                    </div>         
                </div>
                <div class="row">
                	<div class="form-group offset-2">
                    	<div class="text-left ml-3">
                        	<@formButton
                            	btn1type="button"
                                btn1label="${uiLabelMap.Save}"
                                btn1onclick="return formSubmission();"
                                btn2=true
                                btn2onclick = "resetForm()"
                                btn2type="reset"
                                btn2label="${uiLabelMap.Clear}"
                            />
                        </div>
                    </div>
                </div>
             </form>
     </div>
   </div><#-- End main-->
</div><#-- End row-->

<script>
	jQuery(document).ready( function() {
		loadOriginatingCampaigns();
		loadOriginatingAlerts();
		loadOriginatingSRs();
	});
	
	function formSubmission(){
		var originatingAlert  = $("#originatingAlert").val();
		var originatingSR  = $("#originatingSR").val();
		var linkedFrom = $("#linkedFrom").val();
		var cNo = $("#cNo").val();
		var partyId = $("#partyId").val();
		var valid = "true";
		if(originatingAlert != "" && originatingAlert != null && originatingSR != "" && originatingSR != null){
			valid = "false";
		}else{
			if(linkedFrom != "" && linkedFrom != null) {
				$("#dataSourceDataId").val(linkedFrom);
			}else if(originatingSR != "" && originatingSR != null) {
				$("#dataSourceDataId").val(originatingSR);
			}else if(originatingAlert != "" && originatingAlert != null){
				$("#dataSourceDataId").val(originatingAlert);
			}
		}
		if((partyId == "" || partyId == undefined) && (cNo == "" || cNo == undefined)){
			showAlert('error','Please select Customer');
	 		valid = "false";
		}
		if(valid == "true"){
			$('#createSalesOpportunity').attr('action', "createSalesOpportunityDetails");
            $("#createSalesOpportunity").submit();
		}
		
	}
	
	function loadOriginatingCampaigns() {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var campaignOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        $.ajax({
            type: "POST",
            url: "findOppCampaignListsAjax",
            data: "data",
            async: false,
            success: function(data) {
            	var campaignDetails = data.data;
                for (var i = 0; i < campaignDetails.length; i++) {
                	var eachCampaign = campaignDetails[i];
                    campaignOptions += '<option value="'+eachCampaign.campaignId+'">' + eachCampaign.campaignName + '(' +eachCampaign.campaignId + ')</option>';
                }
            }
        });
        $("#marketingCampaignId").html(campaignOptions);
	}
	
	function loadOriginatingAlerts() {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var alertOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        var customerId  = $("#customerId").val();
        if(customerId != null && customerId != undefined && customerId != ""){
        	 $.ajax({
            	type: "POST",
            	url: "getCustomerAlertDetails",
            	data: {"customerCin" : customerId, "alertPriority" : "HIGH"},
            	async: false,
            	success: function(data) {
            		for (var k = 0; k < data.length; k++) {
                		var eachAlert = data[k];
                    	alertOptions += '<option value="'+eachAlert.alertTrackingId+'">' + eachAlert.alertTrackingId + '</option>';
                	}
            	},error: function(data) {
		        	result=data;
					showAlert("error", "Error occured while fetching service Data!");
				}
        	});
        	$("#originatingAlert").html(alertOptions);
        }
	}
	
	function loadOriginatingSRs() {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var srOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        var customerId  = $("#customerId").val();
        if(customerId != null && customerId != undefined && customerId != ""){
        	$.ajax({
            	type: "POST",
            	url: "findOppServiceRequest",
            	data: {"cinNumber":customerId, "open":"Y"},
            	async: false,
            	success: function(data) {
            		for (var j = 0; j < data.length; j++) {
           	    		var eachSR = data[j];
                    	srOptions += '<option value="'+eachSR.custRequestId+'">' + eachSR.custRequestId + '</option>';
                	}
            	}
        	});
        	$("#originatingSR").html(srOptions);
        }
	}
	
   	$("#dataSourceId").change(function() {
   		var dataSourceId  = $("#dataSourceId").val();
   		if(dataSourceId != null && dataSourceId != undefined && dataSourceId != ""){
   			$.ajax({
	            type: "POST",
	            url: "getDataSourceDetails",
	            data: { "dataSourceId": dataSourceId},
	            async: false,
	            success: function(data) {
	            	if(data != null && data != undefined && data != ""){
	            		for (var i = 0; i < data.length; i++) {
	                   		var details = data[i];
	                   		$("#dataSourceDesc").val(details.description);
	             		}
	             	}else{
	             		$("#dataSourceDesc").val('');
	             	}
	            }
        	});
   		}else{
        	$("#dataSourceDesc").val('');
        }
   	});
   	
   	$("#prodCatalogId").change(function() {
   		var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var catalogOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        var prodCatalogId  = $("#prodCatalogId").val();
        if(prodCatalogId != null && prodCatalogId != undefined && prodCatalogId != ""){
        	$.ajax({
            	type: "POST",
            	url: "getProductDetails",
            	data: {"prodCatalogId":prodCatalogId},
            	async: false,
            	success: function(data) {
            		if(data != null && data != undefined && data != ""){
            			for (var j = 0; j < data.length; j++) {
           	    			var eachCategory = data[j];
                    		catalogOptions += '<option value="'+eachCategory.productCategoryId+'">' + eachCategory.categoryName + '(' +eachCategory.productCategoryId + ')</option>';
                		}
                		$("#productCategoryId").html(catalogOptions);
                	}else{
        				$("#productCategoryId").html("");
        			}
            	}
        	});
        }else{
        	$("#productCategoryId").html("");
        }
   	});
   	
   	$("#productCategoryId").change(function() {
   		var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var productOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        var productCategoryId  = $("#productCategoryId").val();
        if(productCategoryId != null && productCategoryId != undefined && productCategoryId != ""){
        	$.ajax({
            	type: "POST",
            	url: "getProductDetails",
            	data: {"productCategoryId":productCategoryId},
            	async: false,
            	success: function(data) {
            		if(data != null && data != undefined && data != ""){
            			for (var j = 0; j < data.length; j++) {
           	    			var eachCategory = data[j];
                    		productOptions += '<option value="'+eachCategory.productId+'">' + eachCategory.productName + '(' +eachCategory.productId + ')</option>';
                		}
                		$("#productId").html(productOptions);
                	}else{
                		$("#productId").html("");
                	}
            	}
        	});
        	
        }else{
        	$("#productId").html('');
        }
   	});
   	
   	$(function() {
   		$("#findcustomerSr").click(function() {
			loadAgGrid();
		});
	});
   	
</script>   	

