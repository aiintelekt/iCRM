<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif request.getRequestURI().contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
</#if>

<#macro addRelateOpportunity instanceId fromAction="">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.relateOppportunity!}</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="card-header">
            		<form method="post" id="add-relate-opportunity-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
            		
            		<input type="hidden" name="domainEntityType" value="ADD_RELATED_OPPORTUNITY">
					<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
						
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
								
								<@inputRow 
								id="salesEmailAddress"
								placeholder=uiLabelMap.email
								inputColSize="col-sm-12"
								iconClass="fa fa-envelope"
								required=false
								/> 
								
								<@dropdownCell
					                id="assignedUserLoginId"  
					                placeholder="Owner"      
					                allowEmpty=true  
					                options=usersList!        
					                value="${requestParameters.assignedUserLoginId?if_exists}"     
								/> 
						  </div>
						 
						  <div class="col-md-4 col-md-4 form-horizontal">
						  
						  		<@dropdownCell
					            	id="callOutCome"
					              	name="callOutCome"
					              	options=callOutcomeList!
					              	label=""
					              	value=""
					              	allowEmpty=true
					              	placeholder="Select CallOutcome"
					            />
					            
					            <@inputRow 
								id="opportunityName"
								placeholder=uiLabelMap.opportunityName
								inputColSize="col-sm-12"
								iconClass="fa fa-user-circle-o"
								required=false
								/> 
								
								<@inputRow 
								id="salesPhone"
								placeholder=uiLabelMap.salesPhone
								inputColSize="col-sm-12"
								iconClass="fa fa-phone"
								required=false
								/>

						  </div>
						  
						  <div class="col-md-4 col-md-4 form-horizontal text-right">
						      <@inputRow 
								id="opportunitychname"
								placeholder=uiLabelMap.opportunityChineseName
								inputColSize="col-sm-12"
								iconClass="fa fa-user-circle-o"
								required=false
								/> 
								
								<@dropdownCell
					            	id="salesChannelId"
					                placeholder="Select Channel"
					                allowEmpty=true
					                options=salesChannelList!
					                value="${requestParameters.description?if_exists}" 
					            />
                              
                    	 </div>
					</div>
					
					<div class="row p-2">
				        <div class="col-lg-12 col-md-12 col-sm-12">
							<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
							 	<div class="form-check form-check-inline">
				      				<input class="form-check-input" name="statusOpen" id="statusOpen" value="SOSTG_OPEN" checked="" type="checkbox">
				      				<label class="form-check-label">Open</label>
				    			</div>
				                <div class="form-check form-check-inline">
				                	<input class="form-check-input" name="statusClosed" id="statusClosed" value="SOSTG_CLOSED" type="checkbox">
				                  	<label class="form-check-label" for="inlineRadio1">Closed</label>
				                </div>
								<div class="form-check form-check-inline">
				          			<input class="form-check-input" name="statusWon" id="statusWon" value="SOSTG_WON" type="checkbox">
				          			<label class="form-check-label">Won</label>
				        		</div>
								<div class="form-check form-check-inline">
				                	<input class="form-check-input" name="statusNew" id="statusNew" value="SOSTG_NEW" type="checkbox">
				                  	<label class="form-check-label">New</label>
				                </div>
								<div class="form-check form-check-inline">
				                	<input class="form-check-input" name="statusLost" id="statusLost" value="SOSTG_LOST" type="checkbox">
				                  	<label class="form-check-label">Lost</label>
				                </div>						
								<div class="form-check form-check-inline">
				                	<input class="form-check-input" name="statusProgress" id="statusProgress" value="IN_PROGRESS" type="checkbox">
				                  	<label class="form-check-label">In Progress</label>
				                </div>
				                <div class="form-check form-check-inline">
				                	<input class="form-check-input" name="statusContact" id="statusContact" value="SOSTG_CONTACT" type="checkbox">
				                  	<label class="form-check-label">Contacted</label>
				                </div>
				                 <div class="form-check form-check-inline">
				                 	<input class="form-check-input" name="statusNotContact" id="statusNotContact" value="SOSTG_NOT_CONTACT" type="checkbox">
				                  	<label class="form-check-label">Not Contacted</label>
				                </div>
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
					
				</form>
			</div>
			
			<#assign rightContent='<span id="add-relate-to-opportunity-btn" title="Relate to Opportunity" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Relate </a>' />   
						
			<@AgGrid
				gridheadertitle=""
				gridheaderid="${instanceId!}_party-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				headerextra=rightContent
									
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="OPPORTUNITYS" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/opportunity/find-add-relate-opportunity.js"></script>
			
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
