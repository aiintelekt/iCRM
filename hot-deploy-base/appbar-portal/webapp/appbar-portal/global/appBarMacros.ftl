
<#-- DashBoard AppBar macro 
*** id - any unique id to refer the macro it could be the appbar id also
*** barDataList -  needs to pass the appbar configuration data by invoking the generic util methods
*** barColSize - bootstrap class to define the size of the bar
*** barHight - to control the bar height explicitly.
*** barColor - by default all bar will filled with red color. if we want to display with different color then we need to configure
*** favIcon - For displaying fav icon for each bar section
  -->
<#macro Dashboard dashboardInstanceId id="" dashboardHeight="" dashboardWidth="" extra="" partyParameter="" isBoldBiDashboardEnabled="" >
	<#local dashboardDetails = Static["org.fio.homeapps.util.DataUtil"].getDashboardDetails(delegator, dashboardInstanceId, "N")!>
	<#if dashboardDetails?has_content>
		<#local error = dashboardDetails.get("error")!>
		<#local dashboardTypeId = dashboardDetails.get("dashboardTypeId")!>
		<#local isEnabled = dashboardDetails.get("isEnabled")!>
		<#local errorMessage = dashboardDetails.get("errorMessage")!>
		<#local defaultMessage = dashboardDetails.get("defaultMessage")!>
		<#if dashboardTypeId == "" || isEnabled =="">
			<div class="row justify-content-md-center">
					<div class="pt-2">
						<div class="alert alert-danger fade show">
							<span><strong>Warning!</strong> ${defaultMessage!'Please contact your administrator'}</span>
						</div>
					</div>
				</div>
		<#elseif isBoldBiDashboardEnabled?has_content && isBoldBiDashboardEnabled=="Y" && dashboardTypeId?has_content && dashboardTypeId =="POPUP">
			<#if error?has_content && errorMessage?has_content>
				<div class="row justify-content-md-center">
					<div class="pt-2">
						<div class="alert alert-danger fade show">
							<span><strong>Warning!</strong> Please contact your administrator - Dashboard Not Exists</span>
						</div>
					</div>
				</div>
			<#elseif isEnabled !="Y">
				<div class="row justify-content-md-center">
					<div class="pt-2">
						<div class="alert alert-danger fade show">
							<span><strong>Warning!</strong> Please contact your administrator - To enable the dashboard</span>
						</div>
					</div>
				</div>
			<#else>
				<#local rootUrl = dashboardDetails.get("rootUrl")!>
				<#local siteIdentifier = dashboardDetails.get("siteIdentifier")!>
				<#local environment = dashboardDetails.get("environment")!>
				<#local embedType = dashboardDetails.get("embedType")!>
				<#local getDashboardsUrl = dashboardDetails.get("getDashboardsUrl")!>
				<#local tokenKey = dashboardDetails.get("tokenKey")!>
				<#local dashboardId = dashboardDetails.get("dashboardId")!>
				<#local maxHeight = dashboardDetails.get("maxHeight")!>
				<#local maxWidth = dashboardDetails.get("maxWidth")!>
				<#if partyParameter?has_content>
					<#local partyId = partyParameter!>
				</#if>
				<#--<p>Dashboard Instance ID: ${dashboardInstanceId!}</p>
					<p>Unique ID: ${id!}</p>
					<p>Dashboard ID: ${dashboardId!}</p>
					<p>Party Parameter: ${partyId!}</p>-->
				<#include "component://dashboard-portal/webapp/dashboard-portal/dashboard/list_dashboard_details.ftl"/>
			</#if>
		<#elseif isBoldBiDashboardEnabled?has_content && isBoldBiDashboardEnabled=="Y" && dashboardTypeId?has_content && dashboardTypeId =="EMBEDDED">
			<div class="row justify-content-md-center">
					<div class="pt-2">
						<div class="alert alert-danger fade show">
							<span><strong>Warning!</strong> Please use the popup type to view the dashboard.</span>
						</div>
					</div>
				</div>
		<#else>
			<#if error?has_content && errorMessage?has_content>
				<div class="row justify-content-md-center">
					<div class="pt-2">
						<div class="alert alert-danger fade show">
							<span><strong>Warning!</strong> Please contact your administrator</span>
						</div>
					</div>
				</div>
			</#if>
		</#if>
	</#if>
</#macro>

<#macro AppBar appBarId appBarTypeId id="" colSize="col-lg-12 col-md-12 col-sm-12" barHeight="small-box" barColor="bg-o" isEnableUserPreference=false extra="" toggleDropDownData="" animateEffect="">
	<#assign checkAppBarId = "Y">
	<#local getDashboardDetails = Static["org.fio.homeapps.util.DataUtil"].getDashboardDetails(delegator, appBarId, checkAppBarId)!>
	<#if getDashboardDetails?has_content>
		<#local isAppBarId = getDashboardDetails.get("isAppBarId")!>
		<#local isBoldBiDashboard = getDashboardDetails.get("isBoldBiDashboard")!>
	</#if>
	<#if isBoldBiDashboard =="Y" && isAppBarId == "Y" >
		<@Dashboard dashboardInstanceId=appBarId! id=id! extra=extra! isBoldBiDashboardEnabled =isBoldBiDashboard />
	<#elseif "DASHBOARD" == appBarTypeId!>
		<@dashBoardAppBar appBarId=appBarId! appBarTypeId=appBarTypeId! id=id colSize=colSize! barHeight=barHeight! barColor=barColor! isEnableUserPreference=isEnableUserPreference! extra=extra! animateEffect=animateEffect!/>
	<#elseif "ACTION" == appBarTypeId!>
		<@actionAppBar appBarId=appBarId! appBarTypeId=appBarTypeId! id=id colSize=colSize! isEnableUserPreference=isEnableUserPreference! extra=extra! toggleDropDownData=toggleDropDownData!/>
	<#elseif "KPI" == appBarTypeId!>
		<@kpiAppBar appBarId=appBarId! appBarTypeId=appBarTypeId! id=id colSize=colSize! isEnableUserPreference=isEnableUserPreference! extra=extra! />
	</#if>
</#macro>
<#macro dashBoardAppBar appBarId appBarTypeId="" id="" colSize ="col-xl-3 col-lg-6 col-md-12 col-sm-12" barHeight="small-box small-box-db" barColor="bg-o" extra="" isEnableUserPreference=false animateEffect="">
	<#assign appBarTypeId = appBarTypeId!"DASHBOARD" />
	<#assign session = request.getSession() />
	<#assign appBar = dispatcher.runSync("ab.getAppBarConfiguration", Static["org.ofbiz.base.util.UtilMisc"].toMap("appBarId", appBarId!, "appBarTypeId", appBarTypeId!, "userLogin", userLogin!, "userPreference", "Y", "session", session)) />
	<#if appBar?has_content && appBar.appBarElementData?has_content>
		<#assign barDataList =  appBar.appBarElementData! />
		<#assign configuration = barDataList.configuration! />
		<#assign appBarAccessLevel = barDataList.appBarAccessLevel!'' />
		<#-- <#assign dataList = barDataList.dataList! /> -->
		<#if configuration?has_content>
			<div class="row" id="${id!}">
				<#list configuration.entrySet() as entry>
					<#assign elementId = entry.key! />
					<#assign elementData = entry.value! />
					<#assign appBarElementNolabel = elementData.appBarElementNolabel!'N' />
					<#assign favIconUrl = elementData.appBarElementFavIcon!'' />
					<#assign favImgUrl = elementData.appBarElementIconUrl!'' />
					<#assign barElementColor = elementData.appBarElementColor!barColor />
					<#assign elementKey = elementData.elementKey!'' />
					
					<#local dataMap = elementData.dataMap! />
					<#-- <#assign dataKey = dataList[elementId]! /> -->
					<#assign elementValue = "0" />
					
					<#if dataMap?exists && dataMap?has_content && dataMap.get(elementKey)?has_content>
						<#assign elementValue = dataMap.get(elementKey)! />
					<#elseif dashboardBarContext?exists && dashboardBarContext.get(elementKey)?has_content>
						<#assign elementValue = dashboardBarContext.get(elementKey)! />
					<#elseif appBarContext?exists && appBarContext.get(elementKey)?has_content>
						<#assign elementValue = appBarContext.get(elementKey)! />
					<#elseif context?exists && context.get(elementKey)?has_content>
						<#assign value = context.get(elementKey)! />
					</#if>
					
					<#assign elementLabel = "" />
					<#if appBarElementNolabel == "N">
						<#if elementData.appBarElementUilabel?has_content>
							<#assign elementLabel = "${uiLabelMap.get(elementData.appBarElementUilabel!)}" />
						<#else>
							<#assign elementLabel = elementData.appBarElementName!>
						</#if>
					</#if>
					<div class="${colSize!} smallcard-box">
					    <div class="${barHeight} border rounded ${barElementColor} box-animate" effettype="${animateEffect!}" id="${elementKey!}">
					    	<div class="inner float-left mr-4">
					        	<h3><span id="${elementKey!}_Id">${elementValue!}</span></h3>
					        	<p class="mb-0">${elementLabel!}</p>
					        </div>
							<#if favIconUrl?has_content>
					        	<div class="icon float-left">
					        		<i class="fa ${favIconUrl!} text-light"></i>
					        	</div>
					        <#elseif favImgUrl?has_content>
							    <div class="icon">
					        		<img src="${favImgUrl!}" width="21" height="22" class="cust-icon">
					        	</div>	
					    	</#if>
					    </div>
					</div>
				</#list>
				<span class="float-right">
					<#if isEnableUserPreference  && "USER_LEVEL" == appBarAccessLevel!>
						<a class="bar-settings" target="_blank" href="/appbar-portal/control/createUserPreference?appBarId=${appBarId!}&appBarTypeId=${appBarTypeId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" id="activityBarUserPref" name="activityBarUserPref"><i class="fa fa-cog" aria-hidden="true"></i></a>
					</#if>
				</span>
			</div>
		<#else>
			<div class="row justify-content-md-center">
				<div class="pt-2">
					<div class="alert alert-danger fade show">
						<#-- <span><strong>Warning!</strong> Dashboard ${appBar.successMessage!}! App Bar Id - ${appBarId!} </span> -->
						<span> ${StringUtil.wrapString(barDataList?if_exists.defaultMessage!'')}</span>
					</div>	
				</div>
	   		</div>
		</#if>
	<#else>
		<div class="row justify-content-md-center">
			<div class="pt-2">
				<div class="alert alert-danger fade show">
					<#-- <span><strong>Warning!</strong> Dashboard ${appBar.successMessage!}! App Bar Id - ${appBarId!} </span> -->
					<#if appBar?has_content>
						<span><strong>Warning!</strong> ${StringUtil.wrapString(appBar?if_exists.successMessage!'')}</span>
					<#else>
						<span><strong>Warning!</strong> Please contact your administrator</span>
					</#if>
				</div>	
			</div>
	   	</div>
    </#if>
    
</#macro>

<#macro kpiAppBar appBarId appBarTypeId="" id="" colSize="col-lg-12 col-md-12 col-sm-12" extra="" isEnableUserPreference=false styleClass="">
	<#assign appBarTypeId = appBarTypeId!"KPI" />
	<#assign session = request.getSession() />
	<#assign appBar = dispatcher.runSync("ab.getAppBarConfiguration", Static["org.ofbiz.base.util.UtilMisc"].toMap("appBarId", appBarId!, "appBarTypeId", appBarTypeId!, "userLogin", userLogin!, "userPreference", "Y", "session", session)) />
	<#if appBar?has_content && appBar.appBarElementData?has_content>
		<#assign barDataList =  appBar.appBarElementData! />
		<#assign configuration = barDataList.configuration! />
		<#assign appBarAccessLevel = barDataList.appBarAccessLevel!'' />
		<#--<#assign dataList = barDataList.dataList! /> -->
		<#if configuration?has_content>
			<#assign rightElement = "" />
			<#assign leftElement = "" />
			<#list configuration.entrySet() as entry>
				<#assign elementId = entry.key! />
				<#assign elementData = entry.value! />
				<#assign appBarElementNolabel = elementData.appBarElementNolabel!'N' />
				<#assign appBarElementPosition = elementData.appBarElementPosition!'L'>
				<#assign elementKey = elementData.elementKey!''>
				
				<#-- <#assign dataKey = dataList[elementId]! /> -->
				<#assign elementValue = "-" />
				<#if kpiBarContext?exists && kpiBarContext.get(elementKey)?has_content>
					<#assign elementValue = kpiBarContext.get(elementKey)! />
				<#elseif appBarContext?exists && appBarContext.get(elementKey)?has_content>
					<#assign elementValue = appBarContext.get(elementKey)! />
				<#elseif context?exists && context.get(elementKey)?has_content>
					<#assign value = context.get(elementKey)! />
				</#if>
				<#assign element ="" />
				<#-- 
				<div class="${elementData.appBarElementCustomClass!}<#if appBarElementPosition == "L"> float-left</#if> <#if styleClass?has_content> ${styleClass!}</#if>">
	                <small><#if appBarElementNolabel == "N">${elementData.appBarElementUilabel!}</#if></small>
	                <h5>${elementValue!}</h5>
	            </div> -->
	            <#-- <#assign uiLabel = "" />
	            <#if appBarElementNolabel == "N"><#assign uiLabel = elementData.appBarElementUilabel! /></#if> -->
	            <#assign elementLabel = "" />
				<#if appBarElementNolabel == "N">
					<#if elementData.appBarElementUilabel?has_content>
						<#assign elementLabel = "${uiLabelMap.get(elementData.appBarElementUilabel!)}" />
					<#else>
						<#assign elementLabel = elementData.appBarElementName!>
					</#if>
				</#if>
	            <#assign element= '<div class="${elementData.appBarElementCustomClass!}"><small id="${elementKey}_LF">${elementLabel!}</small><h5 id="${elementKey}_VF">${elementValue!}</h5></div>' />
	            		
	            
	            <#if element?has_content>
					<#if appBarElementPosition == "R">
						<#assign rightElement = rightElement+element+'\n' />
					<#else>
						<#assign leftElement=leftElement+element+'\n' />
					</#if>
				</#if>	
			</#list>
			<div class="row">
				<div class="${colSize!}" id="${id!}">
					<#if leftElement?has_content>
						${leftElement!}
					</#if>
					<span class="float-right">
						<#if rightElement?has_content>
							${rightElement!}
						</#if>
						${extra!}
						<#if isEnableUserPreference && "USER_LEVEL" == appBarAccessLevel!>
							<a class="bar-settings" target="_blank" href="/appbar-portal/control/createUserPreference?appBarId=${appBarId!}&appBarTypeId=${appBarTypeId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" id="activityBarUserPref" name="activityBarUserPref"><i class="fa fa-cog" aria-hidden="true"></i></a>
						</#if>
					</span>
				</div>
			</div>
		<#else>
			<div class="row justify-content-md-center">
				<div class="pt-2">
					<div class="alert alert-danger fade show">
						<#-- <span><strong>Warning!</strong> Dashboard ${appBar.successMessage!}! App Bar Id - ${appBarId!} </span> -->
						<span> ${StringUtil.wrapString(barDataList?if_exists.defaultMessage!'')}</span>
					</div>	
				</div>
	   		</div>
		</#if>
   	<#else>
		<div class="row justify-content-md-center">
			<div class="pt-2">
				<div class="alert alert-danger fade show">
					<#if appBar?has_content>
						<span><strong>Warning!</strong> ${StringUtil.wrapString(appBar?if_exists.successMessage!'')}</span>
					<#else>
						<span><strong>Warning!</strong> Please contact your administrator</span>
					</#if>
				</div>	
			</div>
	   	</div>
	</#if>
</#macro>

<#macro actionAppBar appBarId appBarTypeId ="" id="" colSize="col-lg-12 col-md-12 col-sm-12" extra="" isEnableUserPreference=false styleClass="" toggleDropDownData="" >
	<#assign appBarTypeId= appBarTypeId!"ACTION" />
	<#assign session = request.getSession() />
	<#assign appBar = dispatcher.runSync("ab.getAppBarConfiguration", Static["org.ofbiz.base.util.UtilMisc"].toMap("appBarId", appBarId!, "appBarTypeId", appBarTypeId!, "userLogin", userLogin!, "userPreference", "Y", "session", session)) />
    <#--
	<#assign barDataList = Static["org.fio.appbar.portal.util.AppBarConfigureUtil"].getAppBarConfiguration(delegator, userLogin!, appBarId!, "ACTION_APP_BAR")?if_exists /> 
	-->
	<#if appBar?has_content && appBar.appBarElementData?has_content>
		<#assign barDataList =  appBar.appBarElementData! />
		<#assign configuration = barDataList.configuration! />
		<#-- <#assign dataList = barDataList.dataList! /> -->
		<#assign appBarAccessLevel = barDataList.appBarAccessLevel!'' />
		<#if configuration?has_content>
				<#assign rightElement = "" />
				<#assign leftElement = "" />
			<#list configuration.entrySet() as entry>
				<#assign elementId = entry.key! />
				<#assign elementData = entry.value! />
				<#assign appBarElementNolabel = elementData.appBarElementNolabel!'N' />
				<#assign appBarElementPosition = elementData.appBarElementPosition!'L' />
				<#assign appBarElementType = elementData.appBarElementType!'' />
				<#assign favIconUrl = elementData.appBarElementFavIcon!'' />
				<#assign favImgUrl = elementData.appBarElementIconUrl!'' />
				<#assign isDisplayElementData = elementData.isDisplayElementData!'Y' />
				<#assign elementKey = elementData.elementKey!'' />
				
				<#assign icon="" />
				<#if favIconUrl?has_content>
	                <#assign icon = '<i class="fa ${favIconUrl!}" style="font-size:20px;" aria-hidden="true"></i>' />
	            <#elseif favImgUrl?has_content>
	                <#assign icon = '<img src="${favImgUrl!}" width="20" height="20" class="cust-icon" />' />
	            </#if>
	            <#--<#assign dataKey = dataList[elementId]! /> -->
	            
	            <#assign elementLabel = "" />
				<#if appBarElementNolabel == "N" || appBarElementNolabel == "">
					<#if elementData.appBarElementUilabel?has_content>
						<#assign elementLabel = "${uiLabelMap.get(elementData.appBarElementUilabel!)}" />
					<#else>
						<#assign elementLabel = elementData.appBarElementName!>
					</#if>
				</#if>
				
	            <#assign elementValue = "" />
	            <#assign value = "">
	            <#if actionBarContext?exists && actionBarContext.get(elementKey)?has_content>
	            	<#assign value = actionBarContext.get(elementKey)! />
	            <#elseif appBarContext?exists && appBarContext.get(elementKey)?has_content>
					<#assign value = appBarContext.get(elementKey)! />
				<#elseif context?exists && context.get(elementKey)?has_content>
					<#assign value = context.get(elementKey)! />
				</#if>
				
				<#if isDisplayElementData == "Y"><#assign elementValue = value! /></#if>
				
				<#assign target ="" />
				<#if "" == elementData.appBarElementTargetUrl! || elementData.appBarElementTargetUrl?contains("javascript")>
					<#assign target ="" />
				<#else>
					<#assign target ="_blank" />
				</#if>
				<#assign element ="" />
				<#if appBarElementType == "LINK">
					<#local requestURI = "" />
					<#assign requestCxt = Static["org.fio.appbar.portal.util.AppBarConfigureUtil"].getRequestContext(elementData.appBarElementTargetUrl!,context.requestParameterMap!,context!)?if_exists />
					<#if requestCxt?has_content>
						<#local requestURI = requestCxt.requestURI!>
					</#if>
					<#if elementValue?has_content  || isDisplayElementData == "N">
						<#assign element= "<a id='${elementId!}' href='${requestURI!}' class='${elementKey!} ${elementData.appBarElementCustomClass!}' target='${target!}' title='${elementLabel!}'>${icon} ${elementValue!}</a>" />
					<#else>
						<#assign element= '<span id="${elementId!}" class="${elementKey!}"></span>' />
					</#if>
				<#elseif appBarElementType == "LABEL">
					<#if value == "Y">
						<#assign icon = '<i class="fa fa-check fa-1 text-success" aria-hidden="true"></i>' />
					<#elseif value == "N">
						<#assign icon = '<i class="fa fa-times fa-1 text-danger" aria-hidden="true"></i>' />
					</#if>
					<#if elementValue?has_content  || isDisplayElementData == "N">
						<#assign element= '<label id="${elementId!}" class="${elementKey!} ${elementData.appBarElementCustomClass!}">${elementLabel!} ${icon!}</label>' />
					<#else>
						<#assign element= '<span id="${elementId!}" class="${elementKey!}"></span>' />
					</#if>
				<#elseif appBarElementType == "BADGE">
					<#if (elementValue?has_content || isDisplayElementData == "N")>
						<#if elementValue != 0>
							<#assign element= '<a id="${elementId!}" href="${elementData.appBarElementTargetUrl!"#"}" class="${elementKey!} ${elementData.appBarElementCustomClass!}" target="${target!}" title="${elementLabel!}"><span class="custom-badge" badge="${elementValue!0}" style="">${icon!}</span> </a>' />
						<#else>
							<#assign element= '<a id="${elementId!}" href="${elementData.appBarElementTargetUrl!"#"}" class="${elementKey!} ${elementData.appBarElementCustomClass!}" target="${target!}" title="${elementLabel!}"><span class="custom-badge" style="">${icon!}</span> </a>' />
						</#if>
					<#else>
						<#assign element= '<span id="${elementId!}"></span>' />
					</#if>
				<#elseif appBarElementType == "MODAL">
					<#if elementValue?has_content || isDisplayElementData == "N">
						<#assign element= '<a id="${elementId!}" href="#" class="${elementKey!} ${elementData.appBarElementCustomClass!}" data-toggle="modal" data-target="#${elementData.appBarElementTargetUrl!}" title="${elementLabel!}" data-element-data="${elementValue!}"> ${icon} ${elementValue!} </a>' />
					<#else>
						<#assign element= '<span id="${elementId!}" class="${elementKey!}"></span>' />
					</#if>
				<#elseif appBarElementType == "DROPDOWN">
					<#if elementValue?has_content || isDisplayElementData == "N">
						<#assign element= '<a id="${elementId!}" href="#" class="${elementKey!} ${elementData.appBarElementCustomClass!}" id="${elementData.appBarElementId!}" data-toggle="dropdown" title="${elementLabel!}"> ${icon} ${elementValue!} </a>' />
						<#if toggleDropDownData?exists && toggleDropDownData?has_content && toggleDropDownData[elementData.appBarElementId!]?has_content>
							<#assign element= element + toggleDropDownData[elementData.appBarElementId!]! />
						</#if>
					<#else>
						<#assign element= '<span id="${elementId!}" class="${elementKey!}"></span>' />
					</#if>
				<#else>
					<#if elementValue?has_content>
						<#assign element= '<span id="${elementId!}"  class="${elementKey!} ${elementData.appBarElementCustomClass!}" id="${elementData.appBarElementId!}" title="${elementLabel!}"> ${icon} ${elementValue!} </span>' />	
					<#else>
						<#assign element= '<span id="${elementId!}"  class="${elementKey!} ${elementData.appBarElementCustomClass!}" id="${elementData.appBarElementId!}" title="${elementLabel!}"> ${elementValue!} </span>' />
					</#if>
					
				</#if>
				<#if element?has_content>
					<#if appBarElementPosition == "R">
						<#assign rightElement = rightElement+element+'\n' />
					<#else>
						<#assign leftElement=leftElement+element+'\n' />
					</#if>
				</#if>
			</#list>
			<div class="row">
				<div class="${colSize!}" id="${id!}">
					<#if leftElement?has_content>
						${leftElement!}
					</#if>
					<span class="float-right">
						<#if rightElement?has_content>
							${rightElement!}
						</#if>
						${extra!}
						<#if isEnableUserPreference && "USER_LEVEL" == appBarAccessLevel!>
							<a class="bar-settings" target="_blank" href="/appbar-portal/control/createUserPreference?appBarId=${appBarId!}&appBarTypeId=${appBarTypeId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" id="actionBarUserPref" name="actionBarUserPref"><i class="fa fa-cog" aria-hidden="true"></i></a>
						</#if>
					</span>
				</div>
			</div>
		<#else>
			<div class="row justify-content-md-center">
				<div class="pt-2">
					<div class="alert alert-danger fade show">
						<#-- <span><strong>Warning!</strong> Dashboard ${appBar.successMessage!}! App Bar Id - ${appBarId!} </span> -->
						<span> ${StringUtil.wrapString(barDataList?if_exists.defaultMessage!'')}</span>
					</div>		
				</div>
	   		</div>
		</#if>
   	<#else>
		<div class="row justify-content-md-center">
			<div class="pt-2">
				<div class="alert alert-danger fade show">
					<#-- <span><strong>Warning!</strong> Dashboard ${appBar.successMessage!}! App Bar Id - ${appBarId!} </span> -->
					<#if appBar?has_content>
						<span> <strong>Warning!</strong> ${StringUtil.wrapString(appBar?if_exists.successMessage!'')}</span>
					<#else>
						<span><strong>Warning!</strong> Please contact your administrator</span>
					</#if>
				</div>	
			</div>
	   	</div>
	</#if>
</#macro>

