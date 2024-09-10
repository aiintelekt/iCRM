<#-- This file has been written by @author Prasnath.k -->
<#include StringUtil.wrapString(iconTemplateLocation!)!>
<!DOCTYPE html>
<html>
<head>
	<@icon/>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
	<!--<meta content="width=device-width" name="viewport">-->
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	
	<title><#if title?has_content>${title}<#elseif titleProperty?has_content>${uiLabelMap.get(titleProperty)}<#else>MyFio OMS</#if></title>
	
	<meta name="description" content="${pageDescriptionLabel!}" />
	<meta name="keywords" content="${keywordsLabel!}" />
	
	

	<#if layoutSettings.styleSheets?has_content>
    	<#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
    	<#list layoutSettings.styleSheets as styleSheet>
      		<link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    	</#list>
  	</#if>
  	<#if layoutSettings.VT_STYLESHEET?has_content>
    	<#list layoutSettings.VT_STYLESHEET as styleSheet>
      		<link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    	</#list>
  	</#if>
  	<#if layoutSettings.rtlStyleSheets?has_content && langDir?exists && langDir == "rtl">
    	<#--layoutSettings.rtlStyleSheets is a list of rtl style sheets.-->
    	<#list layoutSettings.rtlStyleSheets as styleSheet>
      		<link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    	</#list>
  	</#if>
  	<#if layoutSettings.VT_RTL_STYLESHEET?has_content && langDir?exists && langDir == "rtl">
    	<#list layoutSettings.VT_RTL_STYLESHEET as styleSheet>
      		<link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    	</#list>
  	</#if>

	${layoutSettings.extraHead?if_exists}
  	<#if layoutSettings.VT_EXTRA_HEAD?has_content>
    	<#list layoutSettings.VT_EXTRA_HEAD as extraHead>
      		${extraHead}
    	</#list>
  	</#if>

	<!--<link type="text/css" href="/floral/css/bootstrap-combined.css" 	rel="stylesheet" />-->	

	<!--<link href="http://netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.min.css" rel="stylesheet">-->
	
	<!-- BEGIN GLOBAL MANDATORY STYLES -->
	<link href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/simple-line-icons.min.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/uniform.default.css" rel="stylesheet" type="text/css"/>
	
	<#--added by m.vijayakumar for date picker-->
	<#--<link href="/omstheme/css/bootstrap-datetimepicker.css" rel="stylesheet" type="text/css"/>-->
	<link href="/omstheme/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css"/>
	<#--end @vijayakumar-->
	<link href="/omstheme/css/bootstrap-datepicker3.min.css" rel="stylesheet" type="text/css"/>
	<link href="/mocatheme/css/bootstrap-timepicker.min.css" rel="stylesheet" type="text/css"/>
	<!-- END GLOBAL MANDATORY STYLES -->
	<!-- BEGIN PAGE LEVEL STYLES -->
	<link href="/omstheme/css/select2.css" rel="stylesheet" type="text/css"/>
	<link rel="stylesheet" type="text/css" href="/omstheme/css/jquery.notific8.min.css"/>
	<link rel="stylesheet" type="text/css" href="/omstheme/css/toastr.min.css"/>
	<link href="/omstheme/css/themes/typeahead.css" rel="stylesheet" type="text/css"/>
	<!-- END PAGE LEVEL SCRIPTS -->
	
	<!-- BEGIN PAGE STYLES -->
	<!--<link href="/fd_css/tasks.css" rel="stylesheet" type="text/css"/>-->
	<link rel="stylesheet" type="text/css" href="/omstheme/css/fancybox/jquery.fancybox.css"/>
	<!-- END PAGE STYLES -->
	
	<!-- BEGIN THEME STYLES -->
	<#--<link href="/omstheme/css/components-md.css" id="style_components" rel="stylesheet" type="text/css"/>-->
	<link href="/omstheme/css/themes/darkblue.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/plugins-md.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/custom.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/themes/typeahead.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/bootstrap-datepicker3.min.css" rel="stylesheet" type="text/css"/>
	<!-- END THEME STYLES -->
	
	<#--<script language="JavaScript" type="text/javascript" src="/images/prototypejs/prototype.js"></script>-->
	
	<#--layoutSettings.javaScripts is a list of java scripts. -->
    <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
	<#if layoutSettings.javaScripts?has_content>
    	<#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
    	<#list layoutSettings.javaScripts as javaScript>
      		<#if javaScriptsSet.contains(javaScript)>
        		<#assign nothing = javaScriptsSet.remove(javaScript)/>
        		<script type="text/javascript" src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>"></script>
      		</#if>
    	</#list>
  	</#if>
	<#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
    	<#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
      		<script type="text/javascript" src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>"></script>
    	</#list>
  	</#if>
  	
  	<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
	<!-- BEGIN CORE PLUGINS -->
	<!--[if lt IE 9]>
	<script src="../../assets/global/plugins/respond.min.js"></script>
	<script src="../../assets/global/plugins/excanvas.min.js"></script> 
	<![endif]-->
	<script src="/opentaps_js/fieldlookup.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery-ui.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery.validate.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery-migrate.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/bootstrap/bootstrap.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery.blockui.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery.uniform.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery.cokie.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/bootstrap/validator.js" type="text/javascript"></script>
	<script src="/omstheme/js/handlebars.min.js"></script>
	<script src="/omstheme/js/typeahead.bundle.min.js"></script>
	<!-- END CORE PLUGINS -->
	
	
	<#--added by m.vijayakumr date time picker js-->
	<script src="/omstheme/js/bootstrap-datetimepicker.js"></script>
	<script src="/omstheme/js/bootstrap/bootstrap-datepicker.min.js"></script>
    <script src="/omstheme/js/bootstrap-datetimepicker.min.js"></script>
	<#--end @viijayakuamr-->
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	
	<script src="/omstheme/js/jquery/jquery.backstretch.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="/omstheme/js/select2/select2.min.js"></script>
	<#--added by m.vijayakumr date time picker js-->
	<script src="/omstheme/js/bootstrap-datetimepicker.js"></script>
	<script src="/omstheme/js/bootstrap-datetimepicker.min.js"></script>
	<#--end @viijayakuamr-->
	
	<script src="/omstheme/js/amcharts/amcharts.js" type="text/javascript"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.mixitup.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.notific8.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.pulsate.min.js"></script>
	<script src="/omstheme/js/bootstrap/bootstrap-switch.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/bootstrap/jquery.bootstrap.wizard.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="/omstheme/js/bootstrap/bootstrap-select.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/select2/select2.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/dataTables.tableTools.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/dataTables.colReorder.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/dataTables.scroller.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/bootstrap/dataTables.bootstrap.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.multi-select.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.notific8.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/bootstrap/bootbox.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/toastr.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.pulsate.min.js"></script>
	
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.mixitup.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="/omstheme/js/pace.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/fnReloadAjax.js"></script>
	<!-- END PAGE LEVEL PLUGINS -->
	
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<script src="/omstheme/js/metronic/metronic.js" type="text/javascript"></script>
	<script src="/omstheme/js/layout.js" type="text/javascript"></script>
	<script src="/omstheme/js/demo.js" type="text/javascript"></script>
	<!-- END PAGE LEVEL SCRIPTS -->
	
	<script src="/omstheme/js/custom.js" type="text/javascript"></script>
	<script src="/omstheme/js/rating.js" type="text/javascript"></script>
	
	
	<link href="/omstheme/css/categoryCssPlugin/aciTree.css" rel="stylesheet" type="text/css" media="all"/>
	<script src="/omstheme/js/jquery/categoryTreePlugin/jquery.aciPlugin.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/categoryTreePlugin/jquery.aciSortable.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/categoryTreePlugin/jquery.aciTree.dom.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/categoryTreePlugin/jquery.aciTree.core.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/categoryTreePlugin/jquery.aciTree.utils.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/categoryTreePlugin/jquery.aciTree.selectable.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/categoryTreePlugin/jquery.aciTree.sortable.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/categoryTreePlugin/jquery.aciTree.checkbox.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/categoryTreePlugin/jquery.aciTree.radio.js" type="text/javascript"></script>
	
	<script type="text/javascript" src="https://v1c.voicelivechat.com/?id=e702b5ca-6e83-4b67-8174-c061d2eb4481"></script>
	<script type="text/javascript">
	  var voicelivechat_options={
	    button_css_name: "button#exemple_id",
	    button_add_online_css: 'agent_online',
	    button_add_offline_css: 'agent_offline'
	  };
	</script>
	
	<meta property="og:description" content="${pageDescriptionLabel!}">
	<meta property="og:title" content="<#if title?has_content>${title}<#elseif titleProperty?has_content>${uiLabelMap.get(titleProperty)}</#if>: ">
	<meta property="og:site_name" content="">
	<meta property="og:type" content="website">
	<#if og_image??>
		<meta property="og:image" content="<@fullUrlPath url=og_image />">
	</#if>

	<script type="text/javascript">
		// This code set the timeout default value for opentaps.sendRequest
		var ajaxDefaultTimeOut = ${configProperties.get("opentaps.ajax.defaultTimeout")};
	</script>        
	<#if gwtScripts?exists>
		<meta name="gwt:property" content="locale=${locale}"/>
	</#if>

	<#--<link rel="apple-touch-icon-precomposed" sizes="114x114" href="/ttt_portal_images/apple-touch-icon.png" />
	<link rel="apple-touch-icon-precomposed" sizes="144x144" href="/ttt_portal_images/apple-touch-icon-144x144.png" />-->
	
	<#--><@includeAnalytics/>-->
	
	
	<#--Apps Style purpose-->	
	<link href="/omstheme/css/dataTables.bootstrap.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/custom/style.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/custom/uiCustom.css" rel="stylesheet" type="text/css"/>
	
	<style>
		.lchat {
		    background: #fff;
		    color: #333;
		    padding: 3px 10px;
		    font-size: 12px;
		    border-color: #fff;
		    text-align: center;
		    border: 1px solid transparent;
		    margin-top: 14px;
		}
		body .voicelivechat-chat-wrapper .online-offline-form select {
    		width: 309px !important;
    	}
    	body .voicelivechat-chat-wrapper .online-offline-form input[type="submit"] {
   			 width: calc( 92% - 60px ) !important;
   	    }
	</style>
	
</head>

<#assign uri=request.getRequestURI() />


<body class="page-md page-header-fixed page-sidebar-closed-hide-logo page-sidebar-closed-hide-logo">

<!-- BEGIN SIDEBAR TOGGLER BUTTON -->
<div class="menu-toggler sidebar-toggler">
	<nav class="navbar navbar-default navbar-fixed-top">
	    <div class="col-md-12 text-center">
	        
	    </div>
	    <div class="">
	        <div class="navbar-header">
		         <a class="navbar-brand logo" href="<@ofbizUrl>login</@ofbizUrl>"> 
	            	<img src="/omstheme/img/myfiooms.jpg" alt="myFiO OMS">
				 </a>
	                <!-- <span class="m">m</span>
	                <span class="o">o</span>
	                <span class="c">c</span>
	                <span class="a">a</span>
	                <span class="version">MOCA</span> -->
	            </a>
	            <a data-target=".navbar-collapse" data-toggle="collapse" class="menu-toggler responsive-toggler navbar-brand1" href="javascript:;"><img src="/omstheme/img/theme/sidebar_toggler_icon_darkblue.png"/></a>
	        </div>
	        <div class="collapse navbar-collapse" >
	            <ul class="nav navbar-nav navbar-right">
	            	<li><button id="exemple_id" class="lchat">Live Chat</button></li>
	                <li><a href="<@ofbizUrl>ordersListing</@ofbizUrl>"><i class="fa fa-shopping-cart"></i> Orders</a></li>
	                <li><a href="<@ofbizUrl>listing</@ofbizUrl>"><i class="fa fa-list"></i> Listings</a></li>
	                <li><a href="<@ofbizUrl>productListing</@ofbizUrl>"><i class="fa fa-glass"></i> Products</a></li>
	                <li class="dropdown">
					   <a class="dropdown-toggle" data-toggle="dropdown" href="" aria-expanded="false"><i class="fa fa-users"></i> People Management<i class="fa fa-caret-down username-margin"></i></a>
					  
					   <ul class="dropdown-menu dropdown-user">
					      <li><a href="<@ofbizUrl>customerListing</@ofbizUrl>"><i class="fa fa-user icon-margin"></i> Customer</a> </li>
					      <li><a href="<@ofbizUrl>supplierListing</@ofbizUrl>"><i class="fa fa-building icon-margin"></i> Supplier</a> </li>
					   </ul>
					</li>
					<!--<li><a href="<@ofbizUrl>main</@ofbizUrl>" class="dropdown-toggle" data-toggle="dropdown" href="" aria-expanded="false"><i class="fa fa-bar-chart fa-fw"></i>Reports<i class="fa fa-caret-down report-margin"></i></a>
					<ul class="dropdown-menu dropdown-report">
					  	  <li><a href="/dashboard/control/main?query=&screenLayoutId=10000&version=10000&externalLoginKey=${externalLoginKey}" target="_blank"><i class="fa fa-bar-chart fa-fw"></i> Analysis By Store</a> </li>
					      <li><a href="/dashboard/control/main?query=&screenLayoutId=10001&version=10001&externalLoginKey=${externalLoginKey}" target="_blank"><i class="fa fa-bar-chart fa-fw"></i>Sales Order Amount Chart</a> </li>
					      <li><a href="/dashboard/control/main?query=&screenLayoutId=10002&version=10002&externalLoginKey=${externalLoginKey}" target="_blank"><i class="fa fa-bar-chart fa-fw"></i>Sales By Product Store</a> </li>
					      <li><a href="/dashboard/control/main?query=&screenLayoutId=10003&version=10003&externalLoginKey=${externalLoginKey}" target="_blank"><i class="fa fa-bar-chart fa-fw"></i>Open Back Orders</a> </li>
					   </ul>
					</li>-->
					
					
					<!--etl drop down feature-->
					 <li class="dropdown <#if request.getRequestURI().contains("Etl-Process")>active</#if>">
					   <a class="dropdown-toggle" data-toggle="dropdown" href="" aria-expanded="false"><i class="fa fa-users"></i> ETL<i class="fa fa-caret-down username-margin"></i></a>
					  
					   <ul class="dropdown-menu dropdown-user">
					      <li><a href="/Etl-Process/control/myHome?title=1&externalLoginKey=${externalLoginKey}"><i class="fa fa-building icon-margin"></i> Create Model</a> </li>
					      <li><a href="/Etl-Process/control/etlUsers?externalLoginKey=${externalLoginKey}"><i class="fa fa-list icon-margin"></i> Models List</a> </li>
					      <li><a href="/Etl-Process/control/etlProcessConfiguration?externalLoginKey=${externalLoginKey}"><i class="fa fa-list icon-margin"></i> Process List</a> </li>
					      <li><a href="/Etl-Process/control/applyEtlModel?externalLoginKey=${externalLoginKey}"><i class="fa fa-users icon-margin"></i> Apply Model</a> </li>
					      <li><a href="/Etl-Process/control/etlConfiguration?externalLoginKey=${externalLoginKey}"><i class="fa fa-users icon-margin"></i>Configuration</a> </li>
					      <#--<li><a href="<@ofbizUrl>etlLogs</@ofbizUrl>"><i class="fa fa-building icon-margin"></i>Logs</a> </li>-->
					      <li><a href="/Etl-Process/control/expProduct?sessionTab=Export&externalLoginKey=${externalLoginKey}"><i class="fa fa-building icon-margin"></i>Export</a> </li>
					      <li><a href="/Etl-Process/control/logAccount?name=Account&sessionTab=Logs&externalLoginKey=${externalLoginKey}"><i class="fa fa-building icon-margin"></i>Error Logs</a> </li>
					      <li><a href="/Etl-Process/control/etlDataMapping?activeGroupId=Product&externalLoginKey=${externalLoginKey}"><i class="fa fa-building icon-margin"></i>Data Mapping</a> </li>
					      
					   </ul>
					</li>
					<!--end of etl drop down feature-->
					
					
					
					<#if applicationActiveList?exists && applicationActiveList?has_content>
					<li><a href="/home/control/main?externalLoginKey=${externalLoginKey}"><i class="fa fa-plus-square"></i> More Apps</a>
						
					<#--<li class="dropdown">
					   <a class="dropdown-toggle" data-toggle="dropdown" href="" aria-expanded="false"><i class="fa fa-plus-square"></i> More Apps<i class="fa fa-caret-down username-margin"></i></a>
					   
					   <ul class="dropdown-menu dropdown-user">
					   	  <#list applicationActiveList as applicationActive>
					   	  	  <#assign iconMap = "fa fa-share-square"/>
					   	  	  <#if applicationIconMap?exists && applicationIconMap?has_content && applicationIconMap.get(applicationActive.applicationId)?has_content>
					   	  	  	   <#assign iconMap = "${applicationIconMap.get(applicationActive.applicationId)?if_exists}"/>
					   	  	  </#if>
					   	  
						      <li><a href="${applicationActive.linkUrl?if_exists}?externalLoginKey=${externalLoginKey}" target="_BLANK">
						      	   <i class="${iconMap?if_exists} icon-margin"></i>
						      			${applicationActive.shortName?if_exists}
						      	   </a> 
						      </li>
						  </#list>
					   </ul>
					</li>-->
					</#if>
	                <!-- /.dropdown -->
	                <li class="dropdown">
	                    <a class="dropdown-toggle" data-toggle="dropdown" href="#"><#if requestAttributes.userLogin?exists && requestAttributes.userLogin?has_content>${userLogin.userLoginId?if_exists}</#if><i class="fa fa-caret-down username-margin"></i></a>
	                    <ul class="dropdown-menu dropdown-user">
	                        <li><a href="<@ofbizUrl>marketAndAccountSettings</@ofbizUrl>"><i class="fa fa-gear icon-margin"></i> Settings</a> </li>
	                        <li><a href="<@ofbizUrl>shipmentAccountList</@ofbizUrl>"><i class="fa fa-cogs icon-margin"></i> Shipping Carrier setup</a> </li>
	                        <!--li><a href="#"><i class="fa fa-question-circle icon-margin"></i>Help</a></li-->
	                        <li><a href="http://support.myfiooms.com/" target="_blank"><i class="fa fa-question-circle icon-margin"></i> Help</a> </li>
	                         <li><a href="<@ofbizUrl>tallyIntegration</@ofbizUrl>"><i class="fa fa-gear icon-margin"></i> Tally Integration</a> </li>
	                        <li class="divider"></li>
	                        <li><a href="<@ofbizUrl>logout</@ofbizUrl>"><i class="fa fa-sign-out icon-margin"></i> Logout</a> </li>
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

<!-- Message Section-->
	<#--<#include "component://fio-responsive-template/webapp/fio-responsive-template/common/messages.ftl"/>-->
<!-- End Message Section-->

<div class="page-container">
<!-- BEGIN CONTAINER -->
	<div class="page-content-wrapper">
		<!-- BEGIN CONTENT -->
		<div class="page-content">