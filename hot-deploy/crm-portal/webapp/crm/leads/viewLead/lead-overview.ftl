<#include "component://lms-mobile/webapp/lms-mobile/lib/mobileMacros.ftl"/>
<script src="/lms-mobile-resource/js/leads.js"></script>
<style>
.form-group {border-bottom: 1px solid #e4e4e4 !important;}
.accordion-option{float: left;clear: both;}
.accordion-option .title {font-size: 20px;font-weight: bold;float: left; padding: 0; margin: 0;}
.accordion-option .toggle-accordion {}
.accordion-option .toggle-accordion:before {content: "Expand All";}
.accordion-option .toggle-accordion.active:before {content: "Collapse All";}
</style>
<script>
$(document).ready(function(){
	initAccordion();
});
</script>
<div class="leadbg-w">
    <div class="tab-content col-form-label" id="nav-tabContent">
        <div class="tab-pane fade show active mx-3" id="Overview-tab" role="tabpanel" aria-labelledby="nav-home-tab">
        <#if primaryPhone?has_content && primaryPhoneMechId?has_content>
            <div class="form-group row">
            <div class="col-10 text-dark pt-2 pb-2"><span class="text-secondary">Main line 1 </span><br/> ${primaryPhone!}</div>
            <div class="col-2 text-dark text-right">
            <i class="fa fa-phone display-2 text-success" aria-hidden="true"></i>
            <#-- <a href="#" data-toggle="modal" data-target="#myModal" onclick="initiateMainCall(`${primaryLeadAsContact!}`)"> 
            <i class="fa fa-phone display-2 text-success" aria-hidden="true"></i>
            </a> -->
            </div>
            </div>
        </#if>
		<#if primaryContact?has_content>
            <div class="form-group row">
            <div class="col-10 text-dark py-2">
				<strong><@nullChecked value=primaryContact?if_exists.firstName?if_exists/>  </strong><@nullChecked value=primaryContact?if_exists.lastName?if_exists/><br/>
				<span class="text-secondary"> <@nullChecked value=primaryContact?if_exists.designation?if_exists/></span><br/> <@nullChecked value=primaryContact?if_exists.phoneNumber?if_exists/> </div>
		    <#if !primaryContact.phoneNumber?has_content || primaryContact.dnd?if_exists == 'Y'>
		        <div class="col-2 text-secondary text-right"><a href="#"> <i class="fa fa-phone display-2 text-secondary" aria-hidden="true"></i></a></div>
		    <#else> 
		        <div class="col-2 text-dark text-right">
		        <i class="fa fa-phone display-2 text-success" aria-hidden="true"></i>
		        <#-- <a href="#" data-toggle="modal" data-target="#myModal" onclick="initiateCall(`${primaryContact!}`)"> 
		        <i class="fa fa-phone display-2 text-success" aria-hidden="true"></i>
		        </a> -->
		        </div> 
            </#if>
            </div>
		</#if>
	    <div class="text-center my-2">
	    	<a href="#contacts" id="viewAllContacts" onclick="viewTab('contacts')">View all contacts (${leadContacts.size()!}) </a>
	    </div>
        <div class="bg-light1 border-0 row">
        	<@pageSectionHeader title ="Product interest" class="col-12 px-0 border-b mb-2" />
        </div>					
            <#list products as product>
	        <div class="form-group row">
	            <div class="col-1"><i class="fa fa-dot-circle-o" aria-hidden="true"></i></div>
	            <div class="col-8">
	                <div class="text-secondary">Customer interested in</div>
	            <div class="mb-1"><@nullChecked value=productMap.get(product?if_exists.productId?if_exists)/> </div>
	            <div class="text-secondary">INR</div>
	                <div class="mb-1">&#x20b9;<@nullChecked value=product?if_exists.productValue?if_exists/></div>
	            </div>
	            <div class="col-3 text-right">${product?if_exists.lastUpdatedStamp?if_exists?date?string["d MMM yyyy hh:mm a"]}</div>         
	        </div>
            </#list> 
        <div class="bg-light1 border-0 row">
        	<@pageSectionHeader title ="Existing bank details" class="col-12 px-0 border-b mb-2" />
        </div>
        <#if banksLists?size == 0>
        <div class="form-group row">
            <div class="col-9 px-0">
                <div class="mb-1">No Banks </div>
            </div>	
        </div>
        </#if>
        <#assign count = 0>
        <#list banksLists as bank>           
            <#assign count = count + 1>
            <div class="form-group row">
            <div class="col-1"><i class="fa fa-university" aria-hidden="true"></i></div>
            <div class="col-9">
                <div class="mb-1">Bank number ${count!} </div>
            <div class="text-secondary">Bank name </div>
                <div class="mb-1 ">${bank?if_exists.bankName?if_exists}</div>
                <div class="text-secondary">Facilities with other banks</div>
                <#list bank.banksProductsList as facility> 				  
                    <div class="mb-1 "> ${facility?if_exists.productName?if_exists}</div>
                <div class="text-secondary">Amount with other banks</div>
                    <div class="mb-1 ">${facility?if_exists.productValue?if_exists}</div>
                </#list>
                </div>
            </div>
        </#list>
        <div class="row my-2 border-bottom">
            <div class="row col-12 py-3">
            <div class="col-4 text-dark text-center"><div class=""> Days in queue</div> <div class="display-5 pt-1"> ${daysInQueue!}</div></div>
            <div class="col-4 text-dark text-center"><div class="">No. of attempts</div> <div class="display-5 pt-1"> ${callAttempts!}</div></div>
                <#assign scoreClass = 'text-danger'>
                <#if leadScore.enumId = 'LEAD_SCORE_COLD'>
                    <#assign scoreClass = 'text-info'>
                </#if> 
                <#assign description = ''>
                <#if leadScore.description != ''>
                    <#assign description = leadScore.description + " Lead">
                </#if>
                <div class="col-4 text-dark text-center"><div class="">Classification</div> <div class="display-5 pt-1 ${scoreClass!}"> ${description!}</div></div>
            </div>
        </div>		
        <div class="bg-light1 border-0 row">
        	<@pageSectionHeader title ="Engagement logs" class="col-12 px-0 border-b mb-2" />
        </div>		
		<#if meetingLog.logsCount gt 0 >			
			<div class="form-group row py-2">
	          	<div class="col-1">
		          <#if meetingLog.workEffortType == "LMS_CALL">
		          	<i class="fa fa-phone" aria-hidden="true"></i>
		          <#elseif meetingLog.workEffortType == "LMS_MEETING">
		          	<i class="fa fa-handshake-o" aria-hidden="true"></i>
		          </#if>
				</div>
	        <div class="col-3 text-center">${meetingLog?if_exists.updateDate?string.medium_short}</div>
	          <div class="col-6 text-center">${meetingLog?if_exists.outcome?if_exists}</div>
	          <#if meetingLog.workEffortType?exists && meetingLog.workEffortType == "LMS_MEETING">
			      <div class="col-2 text-left"><a href="viewMeetingLog?workEffortLogId=${meetingLog?if_exists.workEffortLogId?if_exists}" class="">View</a></div>
			  <#elseif meetingLog.workEffortType?exists && meetingLog.workEffortType == "LMS_CALL">
			      <div class="col-2 text-left"><a href="callLogDetails?callLogId=${meetingLog?if_exists.workEffortId?if_exists}" class="">View</a></div>
			  </#if>
	        </div>
			<div class="text-center mt-2 mb-2">
			  <a href="viewLogs?leadId=${leadId!}" class="">View all</a>
			</div>
		<#else>
			<div class="form-group row">
				<div class="col-9">
					<div class="mb-1">No engagements made yet </div>
				</div>	
	        </div>
		</#if>			          
		</div>
	</div>
	</div>