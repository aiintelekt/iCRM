<style>
			
</style>
<!--ETL Page level css-->

<#--Apps Style purpose-->	
<link href="/omstheme/css/dataTables.bootstrap.css" rel="stylesheet" type="text/css"/>
<link href="/omstheme/css/custom/style.css" rel="stylesheet" type="text/css"/>
<link href="/omstheme/css/custom/uiCustom.css" rel="stylesheet" type="text/css"/>

<#--from header.ftl file css and js-->
<link href="/mocatheme/css/themes/typeahead.css" rel="stylesheet" type="text/css"/>

<link href="/mocatheme/css/plugins-md.css" rel="stylesheet" type="text/css"/>
<link href="/mocatheme/css/custom.css" rel="stylesheet" type="text/css"/>
<link href="/mocatheme/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css"/>
<link href="/mocatheme/css/themes/typeahead.css" rel="stylesheet" type="text/css"/>
<link href="/mocatheme/css/pages/pricing.min.css" rel="stylesheet" type="text/css"/>
<#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists>
<link href="/cf-resource/css/etl.custom.css" rel="stylesheet" type="text/css"/>

<script src="/mocatheme/js/bootstrap/jquery.bootstrap.wizard.min.js" type="text/javascript"></script>

	<!-- BEGIN SIDEBAR TOGGLER BUTTON -->
	<div class="menu-toggler sidebar-toggler" style="margin-bottom: 40px;">
		<nav class="navbar navbar-default navbar-fixed-top">
		    <div class="col-md-12 text-center">
		        
		    </div>
		    <div class="">
		        <div class="navbar-header">
		            <a class="navbar-brand logo" href="/webtools/control/main${externalKeyParam}">
		                <img src="/images/ofbiz_logo.png" alt="myFiO Lockbox">
		                <!-- <span class="m">m</span>
		                <span class="o">o</span>
		                <span class="c">c</span>
		                <span class="a">a</span>
		                <span class="version">MOCA</span> -->
		            </a>
		            <a data-target=".navbar-collapse" data-toggle="collapse" class="menu-toggler responsive-toggler navbar-brand1" href="javascript:;"><img src="/mocatheme/img/theme/sidebar_toggler_icon_darkblue.png"/></a>
		        </div>
		        <div class="collapse navbar-collapse" style="cursor:none;">
		            <ul class="nav navbar-nav navbar-right" style="font-size:14px !important;">
		             
						<!--etl drop down feature-->
						 <li class="dropdown <#if request.getRequestURI().contains("custom-field")>active</#if>">
						   <a class="dropdown-toggle" data-toggle="dropdown" href="" aria-expanded="false"><i class="fa fa-users"></i> ${uiLabelMap.CustomField!}<i class="fa fa-caret-down username-margin"></i></a>
						  
						   <ul class="dropdown-menu dropdown-user">
						   
						   	<#-- <li><a href="#"><i class="fa fa-building icon-margin"></i>Manage Lockbox Batches</a> </li>
						   	<li><a href="#"><i class="fa fa-users icon-margin"></i>Apply Later Payments</a> </li> -->
						   	<#-- <li><a href="<@ofbizUrl>appRegistry</@ofbizUrl>"><i class="fa fa-building icon-margin"></i>${uiLabelMap.CreateAppRegistry!}</a> </li> -->
						   	<li><a href="<@ofbizUrl>customFieldGroup</@ofbizUrl>"><i class="fa fa-building icon-margin"></i>${uiLabelMap.CustomGroups!}</a> </li>
						   	<li><a href="<@ofbizUrl>customField</@ofbizUrl>"><i class="fa fa-building icon-margin"></i>${uiLabelMap.CustomFields!}</a> </li>
						   	
						   </ul>
						</li>
						<!--end of etl drop down feature-->
						<li class="dropdown">
						<#if locale?exists?has_content && locale=="en_US">
						 <a class="dropdown-toggle" data-toggle="dropdown" href="" aria-expanded="false"><i class="fa fa-language"></i> ${uiLabelMap.language}<i class="fa fa-caret-down username-margin"></i></a>						  
						   <ul class="dropdown-menu dropdown-user">
						   <li>
							<a <#if locale?exists?has_content && locale=="en_US">class="selected"<#else>class="account"</#if> <#if locale?exists?has_content && locale!="en_US">onclick="javascript:setStoreLocale('en_US');"</#if> rel="nofollow">                                
								English</a>
							</li>
							<li>
							<a <#if locale?exists?has_content && locale=="zh_CN">class="selected"<#else>class="account"</#if> <#if locale?exists?has_content && locale!="zh_CN">onclick="javascript:setStoreLocale('zh_CN');"</#if> rel="nofollow">                                
								${uiLabelMap.LocaleChinese}</a>
							</li>
						    </ul>
						<#else>
						<a class="dropdown-toggle" data-toggle="dropdown" href="" aria-expanded="false"><i class="fa fa-language"></i> ${uiLabelMap.language}<i class="fa fa-caret-down username-margin"></i></a>						  
						   <ul class="dropdown-menu dropdown-user">
						   <li>
							<a <#if locale?exists?has_content && locale=="en_US">class="selected"<#else>class="account"</#if> <#if locale?exists?has_content && locale!="en_US">onclick="javascript:setStoreLocale('en_US');"</#if> rel="nofollow">                                
									English</a>
							</li>
							<li>
							<a <#if locale?exists?has_content && locale=="zh_CN">class="selected"<#else>class="account"</#if> <#if locale?exists?has_content && locale!="zh_CN">onclick="javascript:setStoreLocale('zh_CN');"</#if> rel="nofollow">                                
							${uiLabelMap.LocaleChinese}</a>
							</li>
						   </ul>
						</#if>
					</li>
							<script type="text/javascript">
								function setStoreLocale(localeValue){
									var requestUrl=document.location.href;
									var res = requestUrl.split("?locale=");
									requestUrl=res[0];
									var url="<@ofbizUrl>setStoreLocale</@ofbizUrl>"; 
									$.post(url,{locale:localeValue},function(data,status){    
											window.location.href=requestUrl;
											location.reload();
										
									});
								}
							</script>
					<#-- <li><a href="/webtools/control/main?externalLoginKey=${externalLoginKey}"><i class="fa fa-plus-square"></i> ${uiLabelMap.moreApps}</a> -->
						
		                <!-- /.dropdown -->
		                <li class="dropdown <#if request.getRequestURI().contains("marketAndAccountSettings")>active</#if>">
		                    <a class="dropdown-toggle" data-toggle="dropdown" href="#"><#if requestAttributes.userLogin?exists && requestAttributes.userLogin?has_content>${userLogin.userLoginId?if_exists}</#if><i class="fa fa-caret-down username-margin"></i></a>
		                    <ul class="dropdown-menu dropdown-user">
		                       <#--<li><a href="/oms-management/control/marketAndAccountSettings"><i class="fa fa-gear icon-margin"></i>${uiLabelMap.settings}</a> </li>
		                        <li><a href="/oms-management/control/shipmentAccountList"><i class="fa fa-cogs icon-margin"></i>${uiLabelMap.shippingCarrierSetup} </a> </li>-->
		                        <li><a href="/oms-management/control/helpMain"><i class="fa fa-question-circle icon-margin"></i>${uiLabelMap.help}</a> </li>
		                        <li class="divider"></li>
		                        <li><a href="/custom-field/control/logout"><i class="fa fa-sign-out icon-margin"></i> ${uiLabelMap.logout}</a> </li>
		                    </ul>
		                    <!-- /.dropdown-user -->
		                </li>
		                <!-- /.dropdown -->
		            </ul>
		        </div>
		    </div>
		</nav>	
	</div>
	<!-- END SIDEBAR TOGGLER BUTTON -->

<#-- ${screens.render("component://custom-field/webapp/widget/custom-field/screens/common/CommonScreens.xml#messages")} -->		

		
						