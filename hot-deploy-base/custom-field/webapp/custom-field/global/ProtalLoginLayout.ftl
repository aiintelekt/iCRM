<#-- This file has been written by @author Sharif (sislam131@gmail.com) -->
<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<#include StringUtil.wrapString(iconTemplateLocation!)!>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
	<!--<meta content="width=device-width" name="viewport">-->
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	
	<title><#if title?has_content>${title}<#elseif titleProperty?has_content>${uiLabelMap.get(titleProperty)}</#if>: </title>
	
	<meta name="description" content="${pageDescriptionLabel!}" />
	<meta name="keywords" content="${keywordsLabel!}" />
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
  	<#if layoutSettings.rtlStyleSheets?has_content && langDir == "rtl">
    	<#--layoutSettings.rtlStyleSheets is a list of rtl style sheets.-->
    	<#list layoutSettings.rtlStyleSheets as styleSheet>
      		<link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    	</#list>
  	</#if>
  	<#if layoutSettings.VT_RTL_STYLESHEET?has_content && langDir == "rtl">
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
	<link href="/metronic/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/simple-line-icons.min.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/uniform.default.css" rel="stylesheet" type="text/css"/>
	<!-- END GLOBAL MANDATORY STYLES -->
	
	<!-- BEGIN PAGE LEVEL STYLES -->
	<link href="/metronic/css/select2.css" rel="stylesheet" type="text/css"/>
	<link href="/cf-resource/css/login-soft.css" rel="stylesheet" type="text/css"/>
	<link rel="stylesheet" type="text/css" href="/metronic/css/jquery.notific8.min.css"/>
	<!-- END PAGE LEVEL SCRIPTS -->
	
	<!-- BEGIN PAGE STYLES -->
	<!--<link href="/cf-resource/css/tasks.css" rel="stylesheet" type="text/css"/>-->
	<link rel="stylesheet" type="text/css" href="/metronic/css/fancybox/jquery.fancybox.css"/>
	<link href="/cf-resource/css/portfolio.css" rel="stylesheet" type="text/css"/>
	<!-- END PAGE STYLES -->
	
	<!-- BEGIN THEME STYLES -->
	<link href="/metronic/css/components-md.css" id="style_components" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/plugins-md.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/layout.css" rel="stylesheet" type="text/css"/>
	<link id="style_color" href="/metronic/css/default.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/custom.css" rel="stylesheet" type="text/css"/>
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
	<script src="/metronic/js/jquery/jquery.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery-migrate.min.js" type="text/javascript"></script>
	<script src="/metronic/js/bootstrap/bootstrap.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery.blockui.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery.uniform.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery.cokie.min.js" type="text/javascript"></script>
	<script src="/metronic/js/bootstrap/validator.js" type="text/javascript"></script>
	<!-- END CORE PLUGINS -->
	
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<script src="/metronic/js/jquery/jquery.validate.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery.backstretch.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="/metronic/js/select2/select2.min.js"></script>
	
	<script src="/metronic/js/amcharts/amcharts.js" type="text/javascript"></script>
	<script type="text/javascript" src="/metronic/js/jquery/jquery.mixitup.min.js"></script>
	<script type="text/javascript" src="/metronic/js/jquery/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="/metronic/js/jquery/jquery.notific8.min.js"></script>
	<script type="text/javascript" src="/metronic/js/jquery/jquery.pulsate.min.js"></script>
	
	<!-- END PAGE LEVEL PLUGINS -->
	
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<script src="/metronic/js/metronic/metronic.js" type="text/javascript"></script>
	<script src="/metronic/js/layout.js" type="text/javascript"></script>
	<script src="/metronic/js/demo.js" type="text/javascript"></script>
	<script src="/cf-resource/js/login-soft.js" type="text/javascript"></script>
	<!-- END PAGE LEVEL SCRIPTS -->
	
	<script src="/metronic/js/custom.js" type="text/javascript"></script>
	
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
	
	<#--<link rel="apple-touch-icon-precomposed" sizes="114x114" href="/ttt_portal_images/apple-touch-icon.png" />
	<link rel="apple-touch-icon-precomposed" sizes="144x144" href="/ttt_portal_images/apple-touch-icon-144x144.png" />-->
	
	<#--><@includeAnalytics/>-->

</head>

<#assign uri=request.getRequestURI() />

<body class="page-md login">

${sections.render("theme-header-content")}
	
${sections.render("header-main-content")}	

<!-- BEGIN SIDEBAR TOGGLER BUTTON -->
<div class="menu-toggler sidebar-toggler">
</div>
<!-- END SIDEBAR TOGGLER BUTTON -->
	
<!-- BEGIN CONTAINER -->
<div class="content">
		
	${sections.render("top-content")}

	${sections.render("bottom-content")}

	${sections.render("extra-bottom-content")}

	${sections.render("body-end")}
		
</div>
<!-- END CONTAINER -->

${sections.render("footer-content")}

${sections.render("theme-footer-content")}

<script type="text/javascript">
jQuery(document).ready(function() {     
  Metronic.init(); // init metronic core components
  Layout.init(); // init current layout
  Login.init();
  Demo.init();
       // init background slide images
       $.backstretch([
        "/metronic/images/bg/1.jpg",
        "/metronic/images/bg/2.jpg",
        "/metronic/images/bg/3.jpg",
        "/metronic/images/bg/4.jpg"
        ], {
          fade: 1000,
          duration: 8000
    }
    );
    
});

<#if parameters._ERROR_MESSAGE_?exists>
	showAlert("error", "${parameters._ERROR_MESSAGE_}");
</#if>

<#if parameters._SUCCESS_MESSAGE_?exists>
	showAlert("success", "${parameters._SUCCESS_MESSAGE_}");
</#if>

</script>
<!-- END JAVASCRIPTS -->

</body>
</html>
