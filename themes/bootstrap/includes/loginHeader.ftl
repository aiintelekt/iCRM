		<#include StringUtil.wrapString(iconTemplateLocation!)!>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta name="description" content="">
		<meta name="author" content="">
		<@icon/>
	  	<title><#if (page.titleProperty)?has_content>${uiLabelMap[page.titleProperty]}<#else>${(page.title)!}</#if></title>
	  	
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
	    <!-- AG Grid  CSS -->  	
		<script src="/bootstrap/js/ag-grid-community.min.js"></script>
	    <link rel="stylesheet" href="/bootstrap/css/ag-grid.css">
	    <link rel="stylesheet" href="/bootstrap/css/ag-theme-balham.css">
	    <!-- Custom  CSS -->   
	    <link href="/bootstrap/css/fio-custom.css" rel="stylesheet">
	    <!-- <link href="/bootstrap/css/red.css" rel="stylesheet"> -->
        <link href="/bootstrap/css/blue.css" rel="stylesheet">
	    <!--link href="/bootstrap/css/fio-new-custom.css" rel="stylesheet">
	    <link href="themes/css/style.css" rel="stylesheet"!-->
	    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	    <!--[if lt IE 9]>
	    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
	    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	    <![endif]-->  	
		<link href="/bootstrap/css/login.css" rel="stylesheet">