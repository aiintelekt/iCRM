<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
      <div id="main" role="main">
      <#if visit?exists && visit?has_content>
      
         <#assign extra='<a href="userLoginHistory" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/> 
         <@sectionFrameHeader 
         	title="${uiLabelMap.UserLoginHistory}"
         	extra=extra?if_exists
         	extraLeft=extraLeft
         	/>
         
         <div class="col-md-12 col-lg-12 col-sm-12 ">
            <#-- <@sectionHeader 
            	title="${uiLabelMap.VisitInformation}"
            	/>	-->
            <@viewSectionHeader
                title="${uiLabelMap.User!}:${uiLabelMap.Information!}"
                />
            <@displayCell
            	label="${uiLabelMap.VisitIdOrSessionId}"
            	value="${visit.visitId?if_exists} / ${visit.sessionId?if_exists}"
            	/>
            	
            <@displayCell
            	label="${uiLabelMap.PartyIdOrUserLoginId}"
            	value="${visit.partyId?if_exists} / ${visit.userLoginId?if_exists}"
            	/>

            <@displayCell
            	label="${uiLabelMap.UserCreated}"
            	value="${visit.userCreated?if_exists}"
            	/>
            
            <@displayCell
            	label="${uiLabelMap.WebApp}"
            	value="${visit.webappName?if_exists}"
            	/>
            
            <@displayCell
            	label="${uiLabelMap.Server}"
            	value="${visit.serverIpAddress?if_exists} / ${visit.serverHostName?if_exists}"
            	/>
            
            <@displayCell
            	label="${uiLabelMap.Cilent}"
            	value="${visit.clientIpAddress?if_exists} / ${visit.clientHostName?if_exists}"
            	/>
           
            <@displayCell
            	label="${uiLabelMap.ClientUser}"
            	value="${visit.clientUser?if_exists}"
            	/>
            
            <@displayCell
            	label="${uiLabelMap.InitialLocale}"
            	value="${visit.initialLocale?if_exists}"
            	/>
            
            <@displayCell
            	label="${uiLabelMap.InitialRequest}"
            	value="${visit.initialRequest?if_exists}"
            	/>

            <@displayCell
            	label="${uiLabelMap.InitialReferer}"
            	value="${visit.initialReferrer?if_exists}"
            	/>
           
            <@displayCell
            	label="${uiLabelMap.InitialUserAgent}"
            	value="${visit.initialUserAgent?if_exists}"
            	/>
           
            <@displayCell
            	label="${uiLabelMap.FromDateOrThruDate}"
            	value="${visit.fromDate!'Still active'} / ${visit.thruDate!'Still active'}"
            	/>

            <@sectionHeader 
            	title="${uiLabelMap.HitTracker}"
            	/>	
	         <div class="table-responsive">
	           <#--  <@AgGrid 
					userid="${userLogin.userLoginId}" 
					instanceid="0005" 
					styledimensions='{"width":"100%","height":"80vh"}'
					autosave="false"
					autosizeallcol="true" 
					debug="true"
					/> -->
				<table id="access_control_report_table" class="table table-hover table-bordered table-striped">
				   <thead>
				      <tr>
				         <th>Content ID</th>
				         <th>Type</th>
				         <th>Size</th>
				         <th>Start Date</th>
				         <th>URL</th>
				      </tr>
				   </thead>
				   <tbody>
				   	<#list serverHits as serverHit>
				      <tr>
				         <td>${serverHit.contentId!uiLabelMap.NotAvailable}</td>
				         <td>${serverHit.hitType!uiLabelMap.NotAvailable}</td>
				         <td>${serverHit.usedSize!uiLabelMap.NotAvailable}</td>
				         <td>${serverHit.startDate!uiLabelMap.NotAvailable}</td>
				         <td>${serverHit.url!uiLabelMap.NotAvailable}</td>
				      </tr>
				    </#list>
				   </tbody>
				</table>
	         </div>
	         <script type="text/javascript" src="/bootstrap/js/fio-agab-grid.js"></script>
         </div>
       <#else>
       	<div class="row">
		   <div class="col-lg-12 col-lg-offset-3 text-center text-danger">Data not available / Invalid visit id</div>
		</div>
       </#if>
      </div>
   </div>