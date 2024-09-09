<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>                    
<@pageSectionHeader   		
	title=uiLabelMap.PwebRelated	
/>
<@headerH4
	title=uiLabelMap.wealthRelatedFields
/> 
<div class="row">
	<div class="col-md-12 col-lg-6 col-sm-12"> 
  		<div class="row">
  			<@labels
  				label=uiLabelMap.prefferedStartDate
  				required=false
   			/>
   			<b>
   				<div id="startDate">
   					<@displayCell
   						label=""
   						value="${(data.CustRequest.openDateTime)!}"
   					/>	
   				</div>
   			</b>
  		</div>
  		<div class="row">
  			<@labels
  				label=uiLabelMap.opportunityId
  				required=false
   			/>
   			<b>
   				<div id="opportunityId">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestSupplementory.opportunityId)!}"
   					/>	
   				</div>
   			</b>
  		</div>  	
  		<div class="row">
  			<@labels
  				label=uiLabelMap.prefferedBranch
  				required=false
   			/>
   			<b>
   				<div id="pwebPrefferedBranch">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebPrefBranch)!}"
   					/>	
   				</div>
   			</b>
  		</div>
    
  		<div class="row">
  			<@labels
  				label=uiLabelMap.channel
  				required=false
   			/>   		
   			<b>
   				<div id="channel">
   					<@displayCell
   						label=""
   						value="${(data.CustRequest.salesChannelEnumId)!}"
   					/>	
   				</div>
   			</b>   		
  		</div>
  	
  		<div class="row">
   			<@labels
  				label=uiLabelMap.priority
  				required=false
   			/>
   			<b>  
  				<div id="Priority">  		
   					<@displayCell
   						label=""
   						value="${(data.CustRequest.priority)!}"  			
   					/>
   				</div>
   			</b>
   		</div> 	
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.customerWantTo
  				required=false
   			/>
   			<b>
   				<div id="custWantTo">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebWantTo)!}"
   					/>	
   				</div>
   			</b>
  		</div>
  	
  		<div class="row">
  			<@labels
  				label=uiLabelMap.customerWantToDetails
  				required=false
   			/>
   			<b>
   				<div id="custWantToDetails">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebWantToDetails)!}"
   					/>	
   				</div>
   			</b>
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.accountTypeToOpen
  				required=false
   			/>
   			<b>
   				<div id="accountTypeToOpen">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebAccountType)!}"
   					/>	
   				</div>
   			</b>
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.investmentRange
  				required=false
   			/>
   			<b>
   				<div id="investRange">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebInvstRange)!}"
   					/>	
   				</div>
   			</b>
  		</div>
  	
  		<div class="row">
  			<@labels
  				label=uiLabelMap.formName
  				required=false
   			/>
   			<b>
   				<div id="formName">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebFormName)!}"
   					/>	
   				</div>
   			</b>
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.segment
  				required=false
   			/>
   			<b>
   				<div id="segmentName">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custReqSegment)!}"
   					/>	
   				</div>
   			</b>
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.campaign
  				required=false
   			/>
   			<b>
   				<div id="campaign">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebCampaign)!}"
   					/>	
   				</div>
   			</b>
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.caseOrigin
  				required=false
   			/>
   			<b>
   				<div id="caseOrigin">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custReqSegmentPweb)!}"
   					/>	
   				</div>
   			</b>
  		</div>  	
	</div>
  	<div class="col-lg-6 col-md-12 col-sm-12">
  		<div class="row">
  			<@labels
  				label=uiLabelMap.endDate
  				required=false
   			/>
   			<b>
   				<div id="endDate">
   					<@displayCell
   						label=""
   						value="${(data.CustRequest.closedDateTime)!}"
   					/>	
   				</div>
   			</b>	
  		</div> 
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.topic
  				required=false
   			/>  				
   			<b>
   				<div id="topic">
   					<@displayCell
   						label=""
   						value="${(data.CustRequest.topic)!}"
   					/>	
   				</div>
   			</b>	
  		</div> 	 		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.pwebContactMethod
  				required=false
   			/> 		
   			<b>
   				<div id="pwebContactMethod">
   					<@displayCell
   						label=""
   			        	value="${(data.CustRequestChannelPweb.custPwebPrefContactMethod)!}"
   					/>	
   				</div>
   			</b>  		
  		</div>  
  		<div class="row">
  			<@labels
  				label=uiLabelMap.activityType
  				required=false
   			/>
   			<b>
   				<div id="activityType">
   					<@displayCell
   						label=""
   			        	value="${(data.CustRequestChannelPweb.custPwebFormName)!}"
   					/>	
   				</div>
   			</b>  
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.pwebRefNumber
  				required=false
   			/>
   			<b>
   				<div id=pwebRefNumber>
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebRefNo)!}"
   					/>	
   				</div>
   			</b>   			
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.pwebMktId
  				required=false
   			/>   		
   			<b>
   				<div id=pwebMarketingId>
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebMktId)!}"
   					/>	
   				</div>
   			</b>  		
  		</div>
  		<div class="row">
  			<@labels
  				label=uiLabelMap.submitDate
  				required=false
   			/>
   			<b>
   				<div id="submitDate">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebSubmDate)!}"
   					/>	
   				</div>
   			</b> 	
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.callOutCome
  				required=false
   			/>
   			<b>
   				<div id="callOutCome">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestSupplementory.callOutcome)!}"
   					/>	
   				</div>
   			</b> 
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.campaignQueue
  				required=false
   			/>
   			<b>
   				<div id="camapignQueue">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custCampaignQueue)!}"
   					/>	
   				</div>
   			</b> 	
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.discliamerCheck 
  				required=false
   			/>
   			<b>
   				<div id="discChecked">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebDisclChked)!}"
   					/>	
   				</div>
   			</b> 	
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.consent
  				required=false
   			/>
   			<b>
   				<div id="consent">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebConsent)!}"
   					/>	
   				</div>
   			</b> 	
  		</div>
  	
  		<div class="row">
  			<@labels
  				label=uiLabelMap.customerLocalAddress 
  				required=false
   			/>
   			<b>
   				<div id="isAddLoc">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custPwebAddrLocal)!}"
   					/>	
   				</div>
   			</b> 
  		</div>
  		
  		<div class="row">
  			<@labels
  				label=uiLabelMap.existCustomer
  				required=false
   			/>  		
   			<b>
   				<div id="isExistCust">
   					<@displayCell
   						label=""
   						value="${(data.CustRequestChannelPweb.custIsExisting)!}"
   					/>	
   				</div>
   			</b> 	
  		</div> 
  	</div>
</div> 
  
<script>
	function getPwebDetails(){
  		var srNumber=null;
  		srNumber='${requestParameters.srNumber}';  
  		$.ajax({
        	type: "POST",
            url: "pwebRelatedDetailsResult",
            async: false,
            data: "srNumber="+srNumber,
            success: function(data) {
	            if(data.CustRequestChannelPweb!=null && data.CustRequest!=null && data.CustRequestSupplementory!=null){      
	            	if(data.CustRequestChannelPweb.custPwebFormName!=null){
	               		$("#activityType").text(data.CustRequestChannelPweb.custPwebFormName);
	              	}else{
	               		$("#activityType").text("-");
	              	}
	               	if(data.CustRequestSupplementory.opportunityId!=null){
	               		$("#opportunityId").text(data.CustRequestSupplementory.opportunityId);
	               	}else{
	               		$("#opportunityId").text("-");
	              	} 
	               	  
	               	if(data.CustRequestChannelPweb.custPwebPrefBranch!=null){
	               		$("#pwebPrefferedBranch").text(data.CustRequestChannelPweb.custPwebPrefBranch);
	              	}else{
	               		$("#pwebPrefferedBranch").text("-");
	               	}
	               	               
	                if(data.CustRequestChannelPweb.custPwebDisclChked!=null){
	               		$("#discChecked").text(data.CustRequestChannelPweb.custPwebDisclChked);
	               	}else{
	               		$("#discChecked").text("-");
	               	}
	               	                 
	                if(data.CustRequestChannelPweb.custIsExisting!=null && data!=null){
	               		$("#isExistCust").text(data.CustRequestChannelPweb.custIsExisting);
	               	}else{
	               		console.log("else block is executing");
	               		$("#isExistCust").text("-");
	               	}
	                 
	               	if(data.CustRequestChannelPweb.custPwebAddrLocal!=null){
	               		$("#isAddLoc").text(data.CustRequestChannelPweb.custPwebAddrLocal);
	               	}else{
	               		$("#isAddLoc").text("-");
	               	}
	               	
	               	if(data.CustRequestChannelPweb.custCampaignQueue!=null){
	               		$("#camapignQueue").text(data.CustRequestChannelPweb.custCampaignQueue);
	               	}else{
	               		$("#camapignQueue").text("-");
	               	}
	                 
	                if(data.CustRequestSupplementory.callOutcome!=null && data.CustRequestSupplementory.callOutcome!="undefined"){
	               		$("#callOutCome").text(data.CustRequestSupplementory.callOutcome);
	               	}else{
	               		$("#callOutCome").text("-");
	              	}
	                 
	                if(data.CustRequestChannelPweb.custPwebSubmDate!=null){
	               		$("#submitDate").text(data.CustRequestChannelPweb.custPwebSubmDate);
	               	}else{
	               		$("#submitDate").text("-");
	               	}               
	                  
	               	if(data.CustRequestChannelPweb.custPwebPrefContactMethod!=null){
	               		$("#pwebContactMethod").text(data.CustRequestChannelPweb.custPwebPrefContactMethod);
	               	}else{
	               		$("#pwebContactMethod").text("-");
	              	}
	                  
	              	if(data.CustRequest.closedDateTime!=null){
	               		$("#endDate").text(data.CustRequest.closedDateTime);
	               	}else{
	               		$("#endDate").text("-");
	                }
	                
	                if(data.CustRequestChannelPweb.custPwebConsent!=null){
	               		$("#consent").text(data.CustRequestChannelPweb.custPwebConsent);
	               	}else{
	               		$("#consent").text("-");
	                }
	                
	               	if(data.CustRequestChannelPweb.custPwebCampaign!=null){
	               		$("#campaign").text(data.CustRequestChannelPweb.custPwebCampaign);
	               	}else{
	               		$("#campaign").text("-");
	               	}
	               	
	               	if(data.CustRequestChannelPweb.custReqSegment!=null){
	               		$("#segmentName").text(data.CustRequestChannelPweb.custReqSegment);
	               	}else{
	              		$("#segmentName").text("-");
	                }
	                  
	               	if(data.CustRequestChannelPweb.custPwebFormName!=null){
	               		$("#formName").text(data.CustRequestChannelPweb.custPwebFormName);
	               	}else{
	               		$("#formName").text("-");
	                } 
	                
	               	if(data.CustRequestChannelPweb.custPwebInvstRange!=null){
	               		$("#investRange").text(data.CustRequestChannelPweb.custPwebInvstRange);
	               	}else{
	               		$("#investRange").text("-");
	               	} 
	                 
	              	if(data.CustRequestChannelPweb.custReqSegmentPweb!=null){
	                	$("#caseOrigin").text(data.CustRequestChannelPweb.custReqSegmentPweb);
	               	}else{
	                	$("#caseOrigin").text("-");
	                } 
	                 
	               	if(data.CustRequestChannelPweb.custPwebAccountType!=null){
	                	$("#accountTypeToOpen").text(data.CustRequestChannelPweb.custPwebAccountType);
	               	}else{
	                	$("#accountTypeToOpen").text("-");
	                } 
	                
	               	if(data.CustRequest.priority!=null){
	                	$("#Priority").text(data.CustRequest.priority);
	               	}else{
	                	$("#Priority").text("-");
	                }
	                 
	                if(data.CustRequest.openDateTime!=null){
	              		$("#startDate").text(data.CustRequest.openDateTime);
	               	}else{
	               		$("#startDate").text("-");
	                } 
	                
	                if(data.CustRequest.salesChannelEnumId!=null){
	              		$("#channel").text(data.CustRequest.salesChannelEnumId);
	               	}else{
	               		$("#channel").text("-");
	                } 
	                
	               	if(data.CustRequestChannelPweb.custPwebWantTo!=null){
	              		$("#custWantTo").text(data.CustRequestChannelPweb.custPwebWantTo);
	               	}else{
	               		$("#custWantTo").text("-");
	                } 
	                
	              	if(data.CustRequestChannelPweb.custPwebWantToDetails!=null){
	              		$("#custWantToDetails").text(data.CustRequestChannelPweb.custPwebWantToDetails);
	               	}else{
	               		$("#custWantToDetails").text("-");
	                } 
	               
	                if(data.CustRequestChannelPweb.custPwebMktId!=null){
	               		$("#pwebMarketingId").text(data.CustRequestChannelPweb.custPwebMktId);
	               	}else{
	               		$("#pwebMarketingId").text("-");
	               	}
	               	
	                if(data.CustRequest.topic!=null){
	               		$("#topic").text(data.CustRequest.topic);
	               	}else{
	               		$("#topic").text("-");
	               	}
	               	
	                if(data.CustRequestChannelPweb.custPwebRefNo!=null){
	               		$("#pwebRefNumber").text(data.CustRequestChannelPweb.custPwebRefNo);
	               	}else{
	               		$("#pwebRefNumber").text("-");
	               	}
				}else{
					$("#activityType").text("-");
	               	$("#opportunityId").text("-");
	               	$("#pwebPrefferedBranch").text("-");
	               	$("#discChecked").text("-");       		 
	               	$("#isExistCust").text("-");
	               	$("#isAddLoc").text("-");
	               	$("#camapignQueue").text("-");
	               	$("#callOutCome").text("-");
	               	$("#submitDate").text("-");
	               	$("#pwebContactMethod").text("-");
	               	$("#endDate").text("-");
	               	$("#consent").text("-");
	               	$("#campaign").text("-");
	              	$("#segmentName").text("-");
	               	$("#formName").text("-");
	               	$("#investRange").text("-");
	                $("#caseOrigin").text("-");
	                $("#accountTypeToOpen").text("-");
	                $("#Priority").text("-");
	               	$("#startDate").text("-");
	                $("#channel").text("-");
	               	$("#custWantTo").text("-");
	               	$("#custWantToDetails").text("-");
	               	$("#pwebMarketingId").text("-");
	               	$("#topic").text("-");
	               	$("#pwebRefNumber").text("-");
				}   
			},
            error: function(data) {                            
            }
        });
  }
  
</script>
  