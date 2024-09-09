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
	
	<title>Custom Field: <#if title?has_content>${title}<#elseif titleProperty?has_content>${uiLabelMap.get(titleProperty)}</#if></title>
	
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
  	
  	<#--<link href="/metronic/css/components-md.css" id="style_components" rel="stylesheet" type="text/css"/>-->
	
	<#-- <link href="/cf-resource/css/portfolio.css" rel="stylesheet" type="text/css"/> -->
	
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
  	
  	
  	
	<!-- END PAGE LEVEL SCRIPTS -->
		
	<meta property="og:description" content="${pageDescriptionLabel!}">
	<meta property="og:title" content="<#if title?has_content>${title}<#elseif titleProperty?has_content>${uiLabelMap.get(titleProperty)}</#if>: ">
	<meta property="og:site_name" content="">
	<meta property="og:type" content="website">
	<#if og_image??>
		<meta property="og:image" content="<@fullUrlPath url=og_image />">
	</#if>

</head>

<#assign uri=request.getRequestURI() />

<body >

${sections.render("theme-header-content")}

${sections.render("header-common-content")}
	
${sections.render("header-main-content")}	
	
<!-- BEGIN CONTAINER -->
<div class="jumbotron">

	${sections.render("left-sidebar-content")}
	
	<!-- BEGIN CONTENT -->
	<div class="container-fluid" >
				
		${sections.render("top-content")}

		${sections.render("bottom-content")}
	
		${sections.render("extra-bottom-content")}
	
		${sections.render("body-end")}
				
		${sections.render("quick-sidebar-content")}
	
	</div>
	<!-- END CONTENT -->

</div>
<!-- END CONTAINER -->

${sections.render("footer-content")}

${sections.render("theme-footer-content")}

<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">

</script>
<!-- END JAVASCRIPTS -->

<script type="text/javascript" src="/metronic/js/jquery/jquery.pulsate.min.js"></script>
<script type="text/javascript" src="/metronic/js/bootbox/bootbox.min.js"></script>

<script src="/bootstrap/js/components-pickers.js"></script>
<script src="/cf-resource/js/custom.js" type="text/javascript"></script>

<#include "message.ftl"/>

<script type="text/javascript">
jQuery(document).ready(function() {    
	
   	ComponentsPickers.init();
		
});
</script>

</body>
</html>
