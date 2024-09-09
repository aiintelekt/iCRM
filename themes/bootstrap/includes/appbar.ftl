<#if userLogin?has_content>
	<#assign userLoginSecurityGroupDetails=Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("UserLoginSecurityGroup", {"userLoginId" : userLogin.userLoginId, "groupId" , "CLIENT_PORTAL_OWNER" }, [], false))?if_exists />
</#if>
<#include StringUtil.wrapString(logoTemplateLocation!)!>
<style type="text/css">
 .notify-desc {
    color: #4f4f4f;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
 }
 .notify-title {
    color: #17a2b8;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
 }
 .notify-date{
    float: right;
    font-size: 12px;
    color: #4f4f4f;
 }
 .notify-card{
    padding: 10px 0px 0px 10px !important;
    border-left: 5px solid #03a9f491;
    border-top: 1px solid #e5e5e5;
    border-bottom: 1px solid #e5e5e5;
    border-right: 1px solid #e5e5e5;
    margin-bottom: 10px;
    cursor: pointer !important;
 }
 .notify-card-read{
    padding: 10px 0px 0px 10px !important;
     border-left: 5px solid #e5e5e5;
     border-top: 1px solid #e5e5e5;
     border-bottom: 1px solid #e5e5e5;
     border-right: 1px solid #e5e5e5;
     margin-bottom: 10px;
 }
 .btm{
    padding: 10px 0px 0px 10px !important;
    border-left: 5px solid #03a9f491;
    border-top: 1px solid #e5e5e5;
    border-bottom: 1px solid #e5e5e5;
    border-right: 1px solid #e5e5e5;
 }
 .text-info-notify {
     line-height: 40px;
     color: #17a2b8!important;
 }
 
 [badgecustom]:after {
    background: red;
  	border-radius: 50px;
  	color: #fff;
  	content: attr(badgecustom);
  	margin-left: -6px;
  	font-size: 9px;
  	margin-top: -4px;
  	min-width: 20px;
  	padding: 0px;
  	position: absolute;
  	text-align: center;
  	width: 21px;
  	height: 21px;
  	line-height: 2.5;
  	z-index: 1;
  	font-weight: 800;
}
.linebot{
   border-left: 1px solid #bdc3c7;
    height: 30px;
    margin-top: 5px;
    margin-left: 15px;
}

<#if hideNotification?has_content && hideNotification=="N">
.bell-ringing {
  font-size: 15px;
  margin: 0px auto 0;
  -webkit-animation: ring 4s .7s ease-in-out infinite;
  -webkit-transform-origin: 50% 4px;
  -moz-animation: ring 4s .7s ease-in-out infinite;
  -moz-transform-origin: 50% 4px;
  animation: ring 4s .7s ease-in-out infinite;
  transform-origin: 50% 4px;
}

@-webkit-keyframes ring {
  0% { -webkit-transform: rotateZ(0); }
  1% { -webkit-transform: rotateZ(30deg); }
  3% { -webkit-transform: rotateZ(-28deg); }
  5% { -webkit-transform: rotateZ(34deg); }
  7% { -webkit-transform: rotateZ(-32deg); }
  9% { -webkit-transform: rotateZ(30deg); }
  11% { -webkit-transform: rotateZ(-28deg); }
  13% { -webkit-transform: rotateZ(26deg); }
  15% { -webkit-transform: rotateZ(-24deg); }
  17% { -webkit-transform: rotateZ(22deg); }
  19% { -webkit-transform: rotateZ(-20deg); }
  21% { -webkit-transform: rotateZ(18deg); }
  23% { -webkit-transform: rotateZ(-16deg); }
  25% { -webkit-transform: rotateZ(14deg); }
  27% { -webkit-transform: rotateZ(-12deg); }
  29% { -webkit-transform: rotateZ(10deg); }
  31% { -webkit-transform: rotateZ(-8deg); }
  33% { -webkit-transform: rotateZ(6deg); }
  35% { -webkit-transform: rotateZ(-4deg); }
  37% { -webkit-transform: rotateZ(2deg); }
  39% { -webkit-transform: rotateZ(-1deg); }
  41% { -webkit-transform: rotateZ(1deg); }

  43% { -webkit-transform: rotateZ(0); }
  100% { -webkit-transform: rotateZ(0); }
}

@-moz-keyframes ring {
  0% { -moz-transform: rotate(0); }
  1% { -moz-transform: rotate(30deg); }
  3% { -moz-transform: rotate(-28deg); }
  5% { -moz-transform: rotate(34deg); }
  7% { -moz-transform: rotate(-32deg); }
  9% { -moz-transform: rotate(30deg); }
  11% { -moz-transform: rotate(-28deg); }
  13% { -moz-transform: rotate(26deg); }
  15% { -moz-transform: rotate(-24deg); }
  17% { -moz-transform: rotate(22deg); }
  19% { -moz-transform: rotate(-20deg); }
  21% { -moz-transform: rotate(18deg); }
  23% { -moz-transform: rotate(-16deg); }
  25% { -moz-transform: rotate(14deg); }
  27% { -moz-transform: rotate(-12deg); }
  29% { -moz-transform: rotate(10deg); }
  31% { -moz-transform: rotate(-8deg); }
  33% { -moz-transform: rotate(6deg); }
  35% { -moz-transform: rotate(-4deg); }
  37% { -moz-transform: rotate(2deg); }
  39% { -moz-transform: rotate(-1deg); }
  41% { -moz-transform: rotate(1deg); }

  43% { -moz-transform: rotate(0); }
  100% { -moz-transform: rotate(0); }
}

@keyframes ring {
  0% { transform: rotate(0); }
  1% { transform: rotate(30deg); }
  3% { transform: rotate(-28deg); }
  5% { transform: rotate(34deg); }
  7% { transform: rotate(-32deg); }
  9% { transform: rotate(30deg); }
  11% { transform: rotate(-28deg); }
  13% { transform: rotate(26deg); }
  15% { transform: rotate(-24deg); }
  17% { transform: rotate(22deg); }
  19% { transform: rotate(-20deg); }
  21% { transform: rotate(18deg); }
  23% { transform: rotate(-16deg); }
  25% { transform: rotate(14deg); }
  27% { transform: rotate(-12deg); }
  29% { transform: rotate(10deg); }
  31% { transform: rotate(-8deg); }
  33% { transform: rotate(6deg); }
  35% { transform: rotate(-4deg); }
  37% { transform: rotate(2deg); }
  39% { transform: rotate(-1deg); }
  41% { transform: rotate(1deg); }

  43% { transform: rotate(0); }
  100% { transform: rotate(0); }
}
</#if>

</style>
<nav class="navbar navbar-expand-lg navbar-dark fixed-top1 nav1">
	<a class="navbar-brand navbar-brand-tit mr-auto" href="#"> <@logo class="img-responsive header-logo"/></a>
	<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#mainnav" aria-controls="mainnav" aria-expanded="false" aria-label="Toggle navigation" style="background: #666;padding: 5px;font-size: 15px;margin-right: 5px;">
		<span class="navbar-toggler-icon"></span>
	</button>
	<div class="collapse navbar-collapse" id="mainnav">
		<ul class="navbar-nav mr-auto">
		</ul>
		<ul class="navbar-nav ">
			<li class="nav-item">
				<div class="s003">

				</div>
				</a>
			</li>
			<#--<li class="nav-item">
				<input type="hidden" id="refreshMailUserLogin" name="refreshMailUserLogin" value="<#if userLogin ?exists && userLogin?has_content>${userLogin!''}</#if>" />
				<input type="hidden" id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
				<a class="nav-link dropdown-toggle icon-line" style="font-size: 14px;color: #606060 !important;line-height: 24px !important;background: #ffffff !important;" title="Refresh Email Download" id="email-refresh-btn" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fa fa-refresh" aria-hidden="true"></i>
				</a>
				</li> -->
				<#-- <li class="nav-item"><a class="nav-link text-light icon-line icon-hover" href="" id="faicon" title="Chat">
						<img src="/bootstrap/images/chat-icon.png" class="chaticon"></a>
					</li>
					-->
					<#if isEnableSupportLabel?has_content && isEnableSupportLabel=="Y">
						<li class="nav-item dropdown">
							<#if clientPortalUrl?exists && clientPortalUrl?has_content>
								<a class="btn btn-xs btn-primary support-portal" href="${clientPortalUrl!}" target="_blank" style=" font-size: 14px; padding-top: 2px !important; margin-right: 10px;">Ticket Portal</a>
							</#if>
						</li>
					</#if>
					<li class="nav-item dropdown">
						<#if isEnableIUCInt?has_content && isEnableIUCInt=="Y">
							<a class="nav-link " title="Back to IUC" href="${iucUrl}opentaps/control/main<#if token?has_content>?token=${token!}</#if>" style="padding-top: 3px !important"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> IUC</a>
						</#if>
					</li>
					<li class="nav-item dropdown">
						<#if enableHelpUrl?has_content && enableHelpUrl=="Y">
							<a class="nav-link icon-line faicon" title="Help" id="faicon" href="${helpUrl!}" target="_blank"><i class="fa fa-question-circle" aria-hidden="true"></i></a>
							<#else>
								<a class="nav-link dropdown-toggle icon-line faicon" title="Help" id="faicon" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fa fa-question-circle" aria-hidden="true"></i></a>
								<div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdown05" style="width: 400px !important; font-size: 16px;">
									<a class="dropdown-item"><strong style="color: #02829d;font-weight: 700;font-size: 18px;"><i class="fa fa-question-circle" aria-hidden="true"></i>
											Help </strong></a>
									<div class="dropdown-divider"></div>

									<a class="dropdown-item" style="padding: 0px 10px 0px 10px !important;">
										<div class="form-group row">
											<div class="col-lg-12">
												<div class="input-field second-wrap">
													<input type="text" class="form-control" name="username" placeholder="Search">
												</div>
											</div>
										</div>
									</a>

									<div class="dropdown-divider"></div>
									<a class="dropdown-item" style="padding: 0px 0px 0px 10px !important;">
										<div class="form-group row">
											<div class="col-lg-6" style="width: 100px !important">
												<span style="color: #02829d;font-weight: 600;"><i class="fa fa-question" aria-hidden="true"></i>
													Helpful information while you're setting up</span><br>
												<span style="font-size: 14px;" length="">About your CRM</span><br>
												<span style="font-size: 14px;" length="">Tour: See where everything lives</span><br>
												<span style="font-size: 14px;" length="">Find & connect your favourite tools to iCRM</span>
											</div>
										</div>
									</a>

									<div class="dropdown-divider"></div>
									<a class="dropdown-item" style="padding: 0px 0px 0px 10px !important;">
										<div class="form-group row">
											<div class="col-sm-6" style="width: 100px !important">
												<span style="color: #02829d;"><i class="fa fa-link" aria-hidden="true"></i>
													Quick links to your sales tools</span> <br>
												<span style="font-size: 14px;" length="">Contacts</span><br>
												<span style="font-size: 14px;" length="">Meetings</span><br>
												<span style="font-size: 14px;" length="">Deals Pipeline</span>
											</div>
										</div>
									</a>

									<div class="dropdown-divider"></div>
									<a class="dropdown-item" style="padding: 0px 0px 0px 10px !important;">
										<div class="form-group row">
											<div class="col-sm-6" style="width: 100px !important">
												<span style="color: #02829d;"><i class="fa fa-search" aria-hidden="true" style="font-size: 13px;"></i>
													Top searches for this page</span> <br>
												<span style="font-size: 14px;" length="">Determine likelihood to close with predictive lead scoring</span><br>
												<span style="font-size: 14px;" length="">Automatically sync lifecycle stages between certain records</span><br>
												<span style="font-size: 14px;" length="">Get started with analytics tools</span><br>
												<span style="font-size: 14px;" length="">Create and use lists</span>
											</div>
										</div>
									</a>

									<div class="dropdown-divider"></div>
									<a class="dropdown-item" style="padding: 0px 0px 0px 10px !important;">
										<div class="form-group row">
											<div class="col-sm-6" style="width: 100px !important">
												<span style="color: #02829d;"><i class="fa fa-user" aria-hidden="true" style="font-size: 13px;"></i>
													Support</span> <br>
												<span style="font-size: 14px;" length=""> Ask the community</span><br>
												<span style="font-size: 14px;" length="">Chat with iCRM</span>
											</div>
										</div>
									</a>

								</div>
						</#if>
					</li>
					<#if hideNotification?has_content && hideNotification=="N">
						<li class="nav-item dropdown">
							<a class="nav-link dropdown-toggle icon-line faicon" href="" id="notify-bill" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fa fa-bell"></i></a>
							<div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdown05" style="width: 45rem !important;padding: 5px;height: 30rem;max-height: 30rem;overflow-x: hidden;overflow-y: scroll;">
								<a class="notify-heading" style="cursor: pointer;">
								    <strong class="text-info-notify"><i class="fa fa-bell" aria-hidden="true"></i>
								       Notification 
									</strong>
								</a>
								<#if notifType?has_content && notifType=="READ">
								<a class="notify-heading" id="clear-all" style="cursor: pointer;">
									<strong class="text-info-notify float-right">
								       Clear All 
									</strong>
								</a>
		                    	</#if>
		                    	<span id="event-list">
		                    		<#-- 
		                    		<a class="dropdown-item notify-card" href ="">
				                        <div class="form-group row">
				                           <div class="col-lg-12 col-md-12 col-xs-12 col-sm-12 notify-title">
				                              Email Opened <span class="notify-date">Aug 30, 2:31</span>   
				                           </div>
				                           <div class="col-lg-12 col-md-12 col-xs-12 col-sm-12 notify-desc">
				                              Get Notified here, on new activites and updates. Get Notified here, on new activites and updates.                
				                           </div>
				                        </div>
				                     </a>
				                     -->
		                    	</span>
										                     
		                  	</div>
						</li>
						<li class="nav-item dropdown">
							<a class="nav-link icon-line faicon" href="/uiadv-portal/control/findActCal?activityType=APPOINTMENT&externalLoginKey=${externalLoginKey!}" id="notify-cal" target="_blank"><i class="fa fa-calendar"></i></a>
						</li>
					</#if>
					<li class="linebot"></li>
					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle icon-line" href="" id="faicon" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <span style="font-weight: bold;"> ${userName!} </span> <i class="fa fa-user-circle" aria-hidden="true"></i> <i class="fa fa-angle-down" aria-hidden="true"></i></a>
						<div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdown05">
							<#if userLoginSecurityGroupDetails?exists && userLoginSecurityGroupDetails?has_content>
								<a class="dropdown-item" href="<@ofbizUrl>profile</@ofbizUrl>">Profile</a>
								<a class="dropdown-item" href="<@ofbizUrl>myProfile</@ofbizUrl>">My Profile</a>
								<#else>
									<a class="dropdown-item" href="${helpUrl!}" target="_blank">Help</a>
							</#if>

							<a class="dropdown-item" href="#" onclick="document.getElementById('passwordResetForm').submit();">Reset Password</a>
							<a class="dropdown-item" href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a>
						</div>
					</li>
		</ul>
	</div>
</nav>
<form id="passwordResetForm" action="changeUserPassword" method="POST">
	<input type="hidden" id="requestUrl" name="requestUrl" value="${request.getRequestURL()}<#if request.getQueryString()?has_content>?${request.getQueryString()!}</#if>" />
</form>

<nav class="navbar navbar-expand-lg navbar-dark fixed-top nav2">

	<a class="navbar-brand-tit mr-auto header-nav" href="#" style="
    padding: 0px 0px 0 15px  !important;
">
	</a>

	<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#mainnav1" aria-controls="mainnav1" aria-expanded="false" aria-label="Toggle navigation">
		<span class="navbar-toggler-icon"></span>
	</button>
	<#if userLogin?has_content>
		<#assign session=request.getSession() />
		<#assign webAppMenus=session.getAttribute("webAppMenus")?if_exists />
		
		<#if webAppMenus?exists && webAppMenus?has_content && webAppMenus.webApps?exists && webAppMenus.webApps?has_content>
			<#assign getMenuList=webAppMenus! />
			<#else>
				<#assign getMenuList=dispatcher.runSync("getComponents", Static["org.ofbiz.base.util.UtilMisc"].toMap("activeApp", activeApp, "session" , session, "request" , request,"userLogin", userLogin)) />
		</#if>

		<div class="collapse navbar-collapse" id="mainnav1">
			<ul class="navbar-nav mr-auto">
				<#if getMenuList?has_content>
					<#list getMenuList.webApps as webApp>
						<#assign apps=webApp.webApp?if_exists />
						<#assign tabs=webApp.tabs?if_exists />
						<li class="nav-item dropdown <#if activeApp?has_content && activeApp == " ${apps.componentName?if_exists}">active
				</#if>">
				<a class="nav-link dropdown-toggle" href="#" id="crmdd" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${apps.uiLabels?if_exists} <i class="fa fa-angle-down" aria-hidden="true"></i></a>
				<ul class="dropdown-menu drop-list" aria-labelledby="dropdown1">
					<#list tabs as tab>
						<#assign webAppTab=tab.webAppTab?if_exists />
						<#assign webAppShortCutList=tab.webAppShortCutList?if_exists />
						<#if webAppTab?has_content>
							<li class="dropdown-item dropdown dropright">
								<#assign tabURI=webAppTab.requestURI?if_exists>
									<#if externalKeyParam?exists && externalKeyParam?has_content>
										<#assign queryString="?${externalKeyParam}">
											<#if tabURI?exists && tabURI?contains("?")>
												<#assign queryString="&amp;${externalKeyParam}">
											</#if>
									</#if>
									<a onclick="<#if tabURI?has_content>window.location.href='${tabURI!}${queryString!}'</#if>" href="<#if tabURI?has_content>${tabURI!}${queryString!}<#else>#</#if>" class="<#if webAppShortCutList?has_content>dropdown-toggle</#if>" id="dropdown1-2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="${webAppTab.favIcon!}" aria-hidden="true"></i> ${webAppTab.uiLabels?if_exists}</a>
									<#if webAppShortCutList?has_content>
										<ul class="dropdown-menu drop-list" aria-labelledby="dropdown1-2">
											<#list webAppShortCutList as webAppShortcut>
												<#assign webAppSubShortCutList=webAppShortcut.webAppSubShortCutList?if_exists>
													<#-- <#if webAppShortcut?if_exists.requestURI?has_content>-->
														<#assign shortcutURI=webAppShortcut.requestURI?if_exists>
															<#if externalKeyParam?exists && externalKeyParam?has_content>
																<#assign queryString="?${externalKeyParam}">
																	<#if shortcutURI?has_content && shortcutURI.contains("?")>
																		<#assign queryString="&amp;${externalKeyParam}">
																	</#if>
															</#if>
															<li class="dropdown-item dropdown dropright">
																<a onclick="<#if shortcutURI?has_content>window.location.href='${shortcutURI?if_exists}${queryString!}'</#if>" href="<#if shortcutURI?has_content>${shortcutURI?if_exists}${queryString!}<#else>#</#if>" class="<#if webAppSubShortCutList?has_content>dropdown-toggle</#if>" id="dropdown1-2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="${webAppShortcut.favIcon!}" aria-hidden="true"></i> ${webAppShortcut.uiLabels?if_exists}</a>
																<#if webAppSubShortCutList?has_content>
																	<ul class="dropdown-menu drop-list" aria-labelledby="dropdown1-2">
																		<#list webAppSubShortCutList as webAppSubShortcut>
																			<#if webAppSubShortcut?if_exists.requestURI?has_content>
																				<#if externalKeyParam?exists && externalKeyParam?has_content>
																					<#assign queryString="?${externalKeyParam}">
																						<#if webAppSubShortcut.requestURI.contains("?")>
																							<#assign queryString="&amp;${externalKeyParam}">
																						</#if>
																				</#if>
																				<li class="dropdown-item"><a onclick="window.location.href='${webAppSubShortcut.requestURI?if_exists}${queryString!}'" href="${webAppSubShortcut.requestURI?if_exists}${queryString!}"><i class="${webAppSubShortcut.favIcon!}" aria-hidden="true"></i> ${webAppSubShortcut.uiLabels?if_exists}</a>
																				</li>
																			</#if>
																		</#list>
																	</ul>
																</#if>
															</li>
															<#-- </#if>-->
											</#list>
										</ul>
									</#if>
							</li>
						</#if>
					</#list>
				</ul>
				</li>
				</#list>
	</#if>

	<#assign reportUiLabel=Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "REPORT_UI_LABEL" , "Reports" )?if_exists />
	<#assign intelektUrl=Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "INTELEKT_URL" )?if_exists />
	<#assign hasReportsPermission=Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("UserLoginSecurityGroup", {"userLoginId" : userLogin.userLoginId, "groupId" , "REPORT_PORTAL_MENU" }, [], false))?if_exists />
	<#if hasReportsPermission?exists && hasReportsPermission?has_content>
		<li class="nav-item"><a class="nav-link dropdown-toggle" href="<#if intelektUrl?has_content>${intelektUrl!}?rdReport=MainMenu&rdUsername=${userLogin.userLoginId?if_exists}<#else>#</#if>" id="dropdown05" title=""><span>
				</span>${reportUiLabel!'Reports'}</a>
		</li>
	</#if>
	</ul>
	</div>

	</#if>

</nav>

<#assign isJustLogin=session.getAttribute("isJustLogin")?if_exists />
<#if !isJustLogin?has_content>
	${session.setAttribute("isJustLogin", "Y")}
<#else>	
	${session.setAttribute("isJustLogin", "N")}
</#if>
<#assign isJustLogin=session.getAttribute("isJustLogin")?if_exists />

<audio id="notify-audio" >
  <source src="/notification-portal-resource/audio/bellring.mp3" type="audio/mpeg">
  Your browser does not support the audio element.
</audio>

<style type="text/css">
	.fa-mobile,
	.fa-phone {
		font-size: 1.3em !important;
	}
</style>

<script>

var notifyAudio;

$(document).ready(function() {
	
	notifyAudio = $('#notify-audio')[0];

	$('.dropdown-menu').on('click', function(e) {
	    e.stopPropagation(); // Stop the click event propagation
	});
	notificationEventCount();
	
	//notificationEvents();
	
	$("#notify-bill").on('click', function(){
		notificationEvents();
	});
	
	$("#clear-all").on('click', function(){
		clearAllNotify();
	});
		
});

var isFirstVisitedNotification = false;
console.log('isJustLogin - ${isJustLogin!}');
<#if isJustLogin?has_content && isJustLogin=='N'>
	isFirstVisitedNotification = true;
</#if>
function notificationEventCount(){
	$.ajax({
	    async: true,
	    type: "POST",
	    url: "/notification-portal/control/getUserNotificationCount?externalLoginKey=${requestAttributes.externalLoginKey!}",
	    data: {},
	    success: function (data) {
	    	if (data) {
	    		var eventCount = data.eventCount;
	    		if("0" !== eventCount && eventCount !== "undefined" && eventCount !== null){
	    			var eventIcon = '<span class="custom-badge" badgecustom="'+eventCount+'" style=""><i class="fa fa-bell" aria-hidden="true"></i></span>';  
	    			$("#notify-bill").html(eventIcon);
	    			if (eventCount > 0 && !isFirstVisitedNotification) {
	    				notifyAudio.play();
	    				isFirstVisitedNotification = true;
	    			}
	    		} else if("0" === eventCount || eventCount === "undefined" || eventCount === null){
	    			var eventIcon = '<span class="" style=""><i class="fa fa-bell" aria-hidden="true"></i></span>';  
	    			$("#notify-bill").html(eventIcon);
	    		}
	    	}
		}
	});
}

function notificationEvents(){
	$.ajax({
		async: false,
	    type: "POST",
	    url: "/notification-portal/control/getUserNotification?externalLoginKey=${requestAttributes.externalLoginKey!}",
	    data: {},
	    success: function (data) {
	    	if (data) {
	    		var eventList = data.eventList;
	    		if(eventList){
	    			var innerHtml = "";
	    			for(var i=0;i<eventList.length; i++){
	    				var event = eventList[i];
	    				var eventUrl = event.eventUrl;
	    				var tabId = event.tabId;
	    				if(!eventUrl)
	    					//innerHtml += '<a class="dropdown-item notify-card" href ="#" >';
	    					innerHtml += '<span class="dropdown-item notify-card">';
	    				else{
	    					if(eventUrl.indexOf("?") != -1)
	    						eventUrl = eventUrl+"&externalLoginKey=${requestAttributes.externalLoginKey!}";
	    					else
	    						eventUrl = eventUrl+"?externalLoginKey=${requestAttributes.externalLoginKey!}";
	    					
	    					if(tabId)
	    						eventUrl = eventUrl +"#"+tabId;
	    						
	    					//innerHtml += '<span class="dropdown-item notify-card" href ="'+eventUrl+'" target="_blank">';
	    					innerHtml += '<span class="dropdown-item notify-card" onclick="javascript: markAsReadEvent(\''+event.eventId+'\',\''+eventUrl+'\')">';
	    					innerHtml += '';
	    				}
	    				innerHtml += '<div class="form-group row">';
	                    innerHtml += '<div class="col-lg-12 col-md-12 col-xs-12 col-sm-12 notify-title">';
	                    innerHtml += event.eventName+ '<span class="notify-date">'+event.eventDateStr+'</span></div>';
						innerHtml += '<div class="col-lg-12 col-md-12 col-xs-12 col-sm-12 notify-desc" title="'+event.eventDescription+'">';
	                    innerHtml += event.eventDescription+'</div>';
	                    innerHtml += '</div></span>';
	    			}
	    			$('#event-list').html(innerHtml);
	    			if (eventList.length > 0 && !isFirstVisitedNotification) {
	    				notifyAudio.play();
	    				isFirstVisitedNotification = true;
	    			}
	    		}
	    	}
		}
	});
}

function markAsReadEvent(eventId, eventURL){
	//alert("eventId------>"+eventId);
	//alert("eventURL------>"+eventURL);
	$.ajax({
		async: false,
	    type: "POST",
	    url: "/notification-portal/control/markEventAsRead?externalLoginKey=${requestAttributes.externalLoginKey!}",
	    data: {"eventId":eventId},
	    success: function (data) {
	    	
	    	notificationEventCount();
	    	
	    	var redirectWindow = window.open(eventURL, '_blank');
    		redirectWindow.location;
	    }
	});
}

function clearAllNotify(){
	$.ajax({
		async: false,
	    type: "POST",
	    url: "/notification-portal/control/clearAllNotification?externalLoginKey=${requestAttributes.externalLoginKey!}",
	    data: {},
	    success: function (data) {
	    	//write success response logic here
	    	notificationEventCount();
	    	
	        var elementToRemove = $('.notify-card');
	        elementToRemove.animate({
	            height: 0,
	            opacity: 0
	        }, 1000, function() {
	            elementToRemove.remove();
	        });
	        
	    }
	});
}

</script>

<link href="/bootstrap/css/main.css" rel="stylesheet" />