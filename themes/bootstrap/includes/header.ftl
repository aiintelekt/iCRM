		<#include StringUtil.wrapString(iconTemplateLocation!)!>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="">
        <meta name="author" content="">
        <@icon/>
        <title>${layoutSettings.appName?if_exists} | <#if (page.titleProperty)?has_content>${uiLabelMap[page.titleProperty]}<#else>${(page.title)!}</#if></title>
        
        <!-- Bootstrap core CSS --> 
        <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="/bootstrap/css/font-awesome.min.css" rel="stylesheet">
        
        <!-- DataTable  CSS -->     
        <link rel="stylesheet" type="text/css" href="/bootstrap/css/dataTables.bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="/bootstrap/css/fixedHeader.bootstrap.min.css">
        
        <link rel="stylesheet" type="text/css" href="/bootstrap/css/responsive.bootstrap.min.css">
        
        <!-- Select DropDown  CSS -->    
        <link href="/bootstrap/css/dropdown.css" rel="stylesheet">
        <link href="/bootstrap/css/transition.min.css" rel="stylesheet">
        <!-- Bootstrap Datetimepicker JavaScript-->
        <link href="/bootstrap/css/bootstrap-datetimepicker.min.css" rel="stylesheet"> 
        
        <!-- Custom  CSS -->   
        <link href="/bootstrap/css/fio-custom.css" rel="stylesheet">
        <!-- <link href="/bootstrap/css/red.css" rel="stylesheet"> -->
        <link href="/bootstrap/css/blue.css" rel="stylesheet">
        <link href="/bootstrap/css/animate.css" rel="stylesheet">
        
        <!--link href="/bootstrap/css/fio-new-custom.css" rel="stylesheet">
        <link href="themes/css/style.css" rel="stylesheet"!-->
        
        <script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.min.js" crossorigin="anonymous"> </script> 
        <script type="text/javascript" language="javascript" src="/bootstrap/js/jquery-ui.js"></script>
        
        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
        <script src="/bootstrap/js/html5shiv.min.js"></script>
        <script src="/bootstrap/js/respond.min.js"></script>
        <![endif]-->            
	<script src="/bootstrap/js/bootbox.min.js"></script>
	
<!-- include summernote css/js -->
<link href="/bootstrap/plugins/summernote/summernote.min.css" rel="stylesheet">
<script src="/bootstrap/plugins/summernote/summernote.min.js"></script>

<link href="/bootstrap/css/bootstrap-timepicker.min.css" rel="stylesheet">
<script type="text/javascript" language="javascript" src="/bootstrap/js/bootstrap-timepicker.min.js"></script>
      
<link href="/bootstrap/css/listbox_exchange.css" rel="stylesheet">
<script type="text/javascript" language="javascript" src="/bootstrap/js/listbox_exchange.js"></script>

<script type="text/javascript" language="javascript" src="/bootstrap/js/base64-utf8.module.js"></script>

<script type="text/javascript" language="javascript" src="/bootstrap/js/popper.min.js"></script> 
<script type="text/javascript" language="javascript" src="/bootstrap/js/bootstrap.min.js" ></script>
<script src="/bootstrap/js/bootstrap-confirmation.js"></script>

<script type="text/javascript" language="javascript" src="/bootstrap/js/pre-load.js"></script>
<script type="text/javascript" language="javascript" src="/bootstrap/js/fio-grid-pre-required.js"></script>

<script type="text/javascript" language="javascript" src="/bootstrap/js/DOMPurify/purify.min.js"></script>

<link rel="stylesheet" type="text/css" href="/bootstrap/css/custom.css">

