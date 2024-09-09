<#-- This file has been written by @author Prasnath.k -->
<#include StringUtil.wrapString(iconTemplateLocation!)!>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
	<!--<meta content="width=device-width" name="viewport">-->
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	
	<title><#if headerItem?has_content>${headerItem}<#elseif titleProperty?has_content>${uiLabelMap.get(titleProperty)}</#if></title>
	
	<meta name="description" content="${pageDescriptionLabel!}" />
	<meta name="keywords" content="${keywordsLabel!}" />
	
	<link href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all" rel="stylesheet" type="text/css"/>
	<@icon/>
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
	<link href="/omstheme/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/simple-line-icons.min.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/uniform.default.css" rel="stylesheet" type="text/css"/>
	<link rel="stylesheet" type="text/css" href="/omstheme/css/toastr.min.css"/>
	<!-- END GLOBAL MANDATORY STYLES -->
	
	<!-- BEGIN PAGE LEVEL STYLES -->
	<link href="/omstheme/css/select2.css" rel="stylesheet" type="text/css"/>
	<link rel="stylesheet" type="text/css" href="/omstheme/css/jquery.notific8.min.css"/>
	<!-- END PAGE LEVEL SCRIPTS -->
	
	<!-- BEGIN PAGE STYLES -->
	<!--<link href="/fd_css/tasks.css" rel="stylesheet" type="text/css"/>-->
	<link rel="stylesheet" type="text/css" href="/omstheme/css/fancybox/jquery.fancybox.css"/>
	<!-- END PAGE STYLES -->
	
	<!-- BEGIN THEME STYLES -->
	<link id="style_color" href="/omstheme/css/default.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/custom.css" rel="stylesheet" type="text/css"/>
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
	<script src="/omstheme/js/jquery/jquery.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery-migrate.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/bootstrap/bootstrap.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery.blockui.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery.uniform.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery.cokie.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/bootstrap/validator.js" type="text/javascript"></script>
	<!-- END CORE PLUGINS -->
	
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<script src="/omstheme/js/jquery/jquery.validate.min.js" type="text/javascript"></script>
	<script src="/omstheme/js/jquery/jquery.backstretch.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="/omstheme/js/select2/select2.min.js"></script>
	
	<script src="/omstheme/js/amcharts/amcharts.js" type="text/javascript"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.mixitup.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.notific8.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/toastr.min.js"></script>
	<script type="text/javascript" src="/omstheme/js/jquery/jquery.pulsate.min.js"></script>
	
	<!-- END PAGE LEVEL PLUGINS -->
	
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<script src="/omstheme/js/metronic/metronic.js" type="text/javascript"></script>
	<script src="/omstheme/js/layout.js" type="text/javascript"></script>
	<script src="/omstheme/js/demo.js" type="text/javascript"></script>
	<!-- END PAGE LEVEL SCRIPTS -->
	
	<script src="/omstheme/js/custom.js" type="text/javascript"></script>
	
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
	<link href="/omstheme/css/pages/login.min.css" rel="stylesheet" type="text/css" />
	<link href="/omstheme/css/custom/style.css" rel="stylesheet" type="text/css"/>
	<link href="/omstheme/css/custom/uiCustom.css" rel="stylesheet" type="text/css"/>
	
</head>

<!--Fio Template common lib-->
<#include "component://fio-responsive-template/webapp/fio-responsive-template/lib/portalContentMacros.ftl"/>
<!--Fio Template common lib-->

<#assign uri=request.getRequestURI() />


<body>

<!-- BEGIN SIDEBAR TOGGLER BUTTON -->
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
                <a data-target=".navbar-collapse" data-toggle="collapse" class="menu-toggler responsive-toggler navbar-brand1" href="javascript:;"><img src="/omstheme/img/theme/sidebar_toggler_icon_darkblue.png"/></a>
           		</a>
        </div>
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav navbar-right" >
                <li><a href="http://www.myfiooms.com/pricing/">Sign Up</a></li>
            </ul>
        </div>
    </div>
</nav>
<!-- END SIDEBAR TOGGLER BUTTON -->

<!-- Message Section-->
	<#--<#include "component://fio-responsive-template/webapp/fio-responsive-template/common/messages.ftl"/>-->
<!-- End Message Section-->

<div id="wrapper">

    <div id="page-wrapper">
    
           <div class="container">