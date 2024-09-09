<#-- This file has been written by @author Prasnath.k -->
<!DOCTYPE html>
<html>
<head>

<title>${titleProperty?if_exists}</title>
<style>
@media only screen and (max-width: 900px) {
		.alert {
		    left: 0px !important;
		}
}

		.alert {
		    position: fixed !important;
		    left: 310px !important;
		    z-index:1;
		    font-family:'Open Sans', Arial, Helvetica, sans-serif;
		}
		
		.alert-success {
		    color: #fff !important;
		    background-color: #51a351 !important;
		    border-color: #d6e9c6 !important;
		}
		.alert-danger
		{
		
		    color: #fff !important;
		    background-color: #F42A29 !important;
		    border-color: #d6e9c6 !important;
		}
		ul
		{
			list-style-type: none;
		}
		
		li.list-group-item.active
		{
			    background-color: white !important;
	   			color: black !important;
		}
		
		.panel-group .panel {
		    overflow: visible;
		    height: auto !important;
		}
		
		a:focus, a:hover {
		    color: black !important;
		    text-decoration: none;
		}
		
		#accordions .panel-heading {
		    background-color:#00AFF0 !important;
		    border-top-left-radius: 0px !important;
		    border-top-right-radius: 0px !important; 
		    border-bottom-left-radius: 0px !important;
		    border-bottom-right-radius: 0px !important;
		}
		
		fieldset.scheduler-border {
		    border: 1px groove #ddd !important;
		    padding: 0 1.4em 1.4em 1.4em !important;
		    margin: 0 0 1.5em 0 !important;
		    -webkit-box-shadow:  0px 0px 0px 0px #000;
		            box-shadow:  0px 0px 0px 0px #000;
		}
		
		    legend.scheduler-border {
		        font-size: 1.2em !important;
		        font-weight: bold !important;
		        text-align: left !important;
		        width:auto;
		        padding:0 10px;
		        border-bottom:none;
		    }
		    
		  
		</style>
<style>

		button.btn.btn-xs.green-turquoise
			{
				overflow:initial !important;
			}
			.btn.btn-xs {
    width: auto !important;
}
			
			ul>li.list-group-item
			{
				color:white;
			}
				/*span.input-group-addon.btn.default.btn-file {
					background-color:#FF7300 !important;	
				}*/

				span.fa.fa-question.pull-right.btn-info.btn-circle {
				    height: auto !important;
				}
				
				
				
				.tab-content > .active{
					background-color:white !important;
				}
				.nav-tabs {
				    background-color: white !important;
				    border-bottom: 1px solid #00AFF0 !important;
				
				}
				
				
				span.fa.fa-question.pull-right.btn-info.btn-circle {
    			font-size: 10px !important;
   				 width: 26px !important;
				}
				
				.panel-group .panel+.panel	
				{
					margin-top:0px !important;
				}
				.uppercase
				{
					    padding-left: 2px !important;
				}
				div.radio span, div.radio input {
				    width: 15px !important;
				}
				
				span.make-circle {
				    border-style: inset;
				    /*border-radius: 10px 10px 10px 10px;*/
				    background-color: #FF5722;
				    border-color: #FF5722;
				}
				span.filter-option.pull-left
				{
					color:#666 !important;
				}
				.caption
				{
					    font-weight: bold !important;
				}
				.portlet > .portlet-title > .caption > i {
					    color: #fff !important;
					}
					.dropdown
					{
						font-family :'Open Sans', Arial, Helvetica, sans-serif
					
					}
					.h1, .h2, .h3, .h4, .h5, .h6, h1, h2, h3, h4, h5, h6 {
						    font-family: inherit !important;
						    font-weight: 500 !important;
						    line-height: 1.1 !important;
						    color: inherit !important;
						}
						.badge1 {
						    display: inline-block !important;
						    min-width: 10px !important;
						    padding: 3px 7px !important;
						    font-size: 12px !important;
						    font-weight: 700 !important;
						    line-height: 1 !important;
						    text-align: center !important;
						    white-space: nowrap !important;
						    vertical-align: baseline !important;
						    color: #082935 !important;
						    background-color: #fff !important;
						    border-radius: 10px !important;
						}
						tr {
						    border-bottom: 1px solid black !important;
						    border-top: 1px solid black  !important;
						    border-collapse: collapse !important;
						}
						.page-header
						{
							
    						margin: 0px 0px 15px !important;
						}
						
						thead {
						    background-color: #00AFF0 !important;
						    color: white  !important;
						}

</style>
<!--ETL Page level css-->


<#--main css DO NOT REMOVE-->

	<!-- BEGIN GLOBAL MANDATORY STYLES -->
	<link href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/simple-line-icons.min.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/uniform.default.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css"/>
	<!-- END GLOBAL MANDATORY STYLES -->
	
	<!-- BEGIN PAGE LEVEL PLUGIN STYLES -->
	<link href="/metronic/css/daterangepicker-bs3.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/fullcalendar.min.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/jqvmap.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/morris.css" rel="stylesheet" type="text/css">
	
	<link rel="stylesheet" type="text/css" href="/metronic/css/bootstrap-select.min.css"/>
	<link rel="stylesheet" type="text/css" href="/metronic/css/select2.css"/>
	<link rel="stylesheet" type="text/css" href="/metronic/css/dataTables.scroller.min.css"/>
	<link rel="stylesheet" type="text/css" href="/metronic/css/dataTables.colReorder.min.css"/>
	<link rel="stylesheet" type="text/css" href="/metronic/css/dataTables.bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="/metronic/css/multi-select.css"/>
	
	<!-- END PAGE LEVEL PLUGIN STYLES -->
	<#--<script src="/metronic/js/bootstrap-confirmation.min.js" type="text/javascript"></script>
		   <script src="/metronic/js/ui-confirmations.min.js" type="text/javascript"></script>-->
	<!-- BEGIN PAGE STYLES -->
	<!--<link href="/frt_css/tasks.css" rel="stylesheet" type="text/css"/>-->
	<link rel="stylesheet" type="text/css" href="/metronic/css/bootstrap-datepicker3.min.css"/>
	<link rel="stylesheet" type="text/css" href="/metronic/css/bootstrap-timepicker.min.css"/>
	<link rel="stylesheet" type="text/css" href="/metronic/css/bootstrap-datetimepicker.min.css"/>
	<link rel="stylesheet" type="text/css" href="/metronic/css/jquery.notific8.min.css"/>
	<!-- END PAGE STYLES -->
	
	<!-- BEGIN THEME STYLES -->
	<!-- DOC: To use 'rounded corners' style just load 'components-rounded.css' stylesheet instead of 'components.css' in the below style tag -->
	<#--<link href="/metronic/css/components-md.css" id="style_components" rel="stylesheet" type="text/css"/>-->
	<#--<link href="/metronic/css/components.min.css" id="style_components" rel="stylesheet" type="text/css"/>-->
	<link href="/metronic/css/plugins-md.css" rel="stylesheet" type="text/css"/>
	<#--<link href="/metronic/css/layout.css" rel="stylesheet" type="text/css"/>-->
	<#--<link href="/metronic/css/light.css" rel="stylesheet" type="text/css" id="style_color"/>-->
	<link href="/metronic/css/error.css" rel="stylesheet" type="text/css"/>
	<link href="/metronic/css/custom.css" rel="stylesheet" type="text/css"/>
	<!-- END THEME STYLES -->
	
	<link rel="stylesheet" type="text/css" href="/metronic/css/fancybox/jquery.fancybox.css"/>
	<link href="/frt_css/portfolio.css" rel="stylesheet" type="text/css"/>
	
	
	
	<script src="/metronic/js/jquery/jquery.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery-migrate.min.js" type="text/javascript"></script>
	<!-- IMPORTANT! Load jquery-ui.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->
	<script src="/metronic/js/jquery/jquery-ui.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery.ui.touch-punch.min.js" type="text/javascript"></script>
	<script src="/metronic/js/bootstrap/bootstrap.min.js" type="text/javascript"></script>
	<script src="/metronic/js/bootstrap/bootstrap-hover-dropdown.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery.slimscroll.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery.blockui.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery.cokie.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery.uniform.min.js" type="text/javascript"></script>
	<script src="/metronic/js/bootstrap/bootstrap-switch.min.js" type="text/javascript"></script>
	<script src="/metronic/js/bootstrap/validator.js" type="text/javascript"></script>
	<link href="/etl_toolTip_js/css/gips.css" rel="stylesheet" type="text/css" />
	<script src="/etl_toolTip_js/js/gips.js" type="text/javascript"/>
	<!-- END CORE PLUGINS -->
	
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<script type="text/javascript" src="/metronic/js/bootstrap/bootstrap-datepicker.min.js"></script>
	<script type="text/javascript" src="/metronic/js/bootstrap/bootstrap-timepicker.min.js"></script>
	<script type="text/javascript" src="/metronic/js/clockface/clockface.js"></script>
	<script type="text/javascript" src="/metronic/js/bootstrap/moment.min.js"></script>
	<script type="text/javascript" src="/metronic/js/bootstrap/daterangepicker.js"></script>
	<script type="text/javascript" src="/metronic/js/bootstrap/bootstrap-datetimepicker.min.js"></script>	
	
	<script src="/metronic/js/jquery/jqvmap/jquery.vmap.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jqvmap/maps/jquery.vmap.russia.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jqvmap/maps/jquery.vmap.world.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jqvmap/maps/jquery.vmap.europe.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jqvmap/maps/jquery.vmap.germany.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jqvmap/maps/jquery.vmap.usa.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jqvmap/data/jquery.vmap.sampledata.js" type="text/javascript"></script>
	<!-- IMPORTANT! fullcalendar depends on jquery-ui.min.js for drag & drop support -->
	<script src="/metronic/js/morris/raphael-min.js" type="text/javascript"></script>
	<script src="/metronic/js/morris/morris.min.js" type="text/javascript"></script>
	<script src="/metronic/js/jquery/jquery.sparkline.min.js" type="text/javascript"></script>
	
	<script type="text/javascript" src="/metronic/js/bootstrap/bootstrap-select.min.js"></script>
	<script type="text/javascript" src="/metronic/js/select2/select2.min.js"></script>
	<script type="text/javascript" src="/metronic/js/jquery/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="/metronic/js/dataTables.tableTools.min.js"></script>
	<script type="text/javascript" src="/metronic/js/dataTables.colReorder.min.js"></script>
	<script type="text/javascript" src="/metronic/js/dataTables.scroller.min.js"></script>
	<script type="text/javascript" src="/metronic/js/bootstrap/dataTables.bootstrap.js"></script>
	<script type="text/javascript" src="/metronic/js/jquery/jquery.multi-select.js"></script>
	<script type="text/javascript" src="/metronic/js/jquery/jquery.notific8.min.js"></script>
	<script type="text/javascript" src="/metronic/js/jquery/jquery.pulsate.min.js"></script>
	
	<script type="text/javascript" src="/metronic/js/jquery/jquery.mixitup.min.js"></script>
	<script type="text/javascript" src="/metronic/js/jquery/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="/metronic/js/pace.min.js"></script>
	
	<#--
	-->
	
	<!-- END PAGE LEVEL PLUGINS -->
	
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<script src="/metronic/js/metronic/datatable.js" type="text/javascript"></script>
	<script src="/metronic/js/metronic/metronic.js" type="text/javascript"></script>
	<script src="/metronic/js/layout.js" type="text/javascript"></script>
	<script src="/metronic/js/quick-sidebar.js" type="text/javascript"></script>
	<script src="/metronic/js/demo.js" type="text/javascript"></script>
	
	<script src="/frt_js/components-pickers.js"></script>
	
	<!--<script src="/frt_js/index3.js" type="text/javascript"></script>
	<script src="/frt_js/tasks.js" type="text/javascript"></script>-->
	
	<!--<script src="/frt_js/charts-amcharts.js"></script>-->
	<!-- END PAGE LEVEL SCRIPTS -->
	
	<script src="/metronic/js/custom.js" type="text/javascript"></script>
	
	
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
	
	<script src="/mocatheme/js/bootstrap/jquery.bootstrap.wizard.min.js" type="text/javascript"></script>
	<!-- END THEME STYLES -->
	
	<#--end from header.ftl>
	
<#assign uri=request.getRequestURI() />


<body class="page-md page-header-fixed page-sidebar-closed-hide-logo page-sidebar-closed-hide-logo">

<!-- BEGIN SIDEBAR TOGGLER BUTTON -->
<div class="menu-toggler sidebar-toggler" style="margin-bottom: 40px;">
	<nav class="navbar navbar-default navbar-fixed-top">
	    <div class="col-md-12 text-center">
	        
	    </div>
	    <div class="">
	        <div class="navbar-header">
	            <a class="navbar-brand logo" href="/oms-management/control/main">
	                <img src="/omstheme/img/myfiooms.jpg" alt="myFiO OMS">
	                <!-- <span class="m">m</span>
	                <span class="o">o</span>
	                <span class="c">c</span>
	                <span class="a">a</span>
	                <span class="version">MOCA</span> -->
	            </a>
	            <a data-target=".navbar-collapse" data-toggle="collapse" class="menu-toggler responsive-toggler navbar-brand1" href="javascript:;"><img src="/mocatheme/img/theme/sidebar_toggler_icon_darkblue.png"/></a>
	        </div>
	        <div class="collapse navbar-collapse" style="cursor:none;">
	            <ul class="nav navbar-nav navbar-right">
	              <#--<li <#if request.getRequestURI().contains("ordersListing")>class="active"</#if>><a href="/oms-management/control/ordersListing"><i class="fa fa-shopping-cart"></i> Orders</a></li>
	               <li <#if request.getRequestURI().contains("listing")>class="active"</#if>><a href="/oms-management/control/listing"><i class="fa fa-list"></i> Listings</a></li>
	                <li <#if request.getRequestURI().contains("productListing")>class="active"</#if>><a href="/oms-management/control/productListing"><i class="fa fa-glass"></i> Products</a></li>
	                <li class="dropdown <#if request.getRequestURI().contains("createCustomer") || request.getRequestURI().contains("createSupplier") || request.getRequestURI().contains("customerListing") || request.getRequestURI().contains("supplierListing")>active</#if>">
					   <a class="dropdown-toggle" data-toggle="dropdown" href="" aria-expanded="false"><i class="fa fa-users"></i> People Management<i class="fa fa-caret-down username-margin"></i></a>
					  
					   <ul class="dropdown-menu dropdown-user">
					      <li><a href="/oms-management/control/customerListing"><i class="fa fa-user icon-margin"></i> Customer</a> </li>
					      <li><a href="/oms-management/control/supplierListing"><i class="fa fa-building icon-margin"></i> Supplier</a> </li>
					   </ul>
					</li>
					<li><a href="/oms-management/control/main" class="dropdown-toggle" data-toggle="dropdown" href="" aria-expanded="false"><i class="fa fa-bar-chart fa-fw"></i>Reports<i class="fa fa-caret-down report-margin"></i></a>
						<ul class="dropdown-menu dropdown-report">
					      <li><a href="/mocadashboards/control/main?externalLoginKey=${externalLoginKey}" target="_blank"><i class="fa fa-bar-chart fa-fw"></i> Analysis By Store</a> </li>
					      <li><a href="/mocadashboards/control/main?externalLoginKey=${externalLoginKey}" target="_blank"><i class="fa fa-bar-chart fa-fw"></i>Sales Order Amount Chart</a> </li>
					      <li><a href="/mocadashboards/control/main?externalLoginKey=${externalLoginKey}" target="_blank"><i class="fa fa-bar-chart fa-fw"></i>Sales By Product Store</a> </li>
					      <li><a href="/mocadashboards/control/main?externalLoginKey=${externalLoginKey}" target="_blank"><i class="fa fa-bar-chart fa-fw"></i>Open Back Orders</a> </li>
					   </ul>
					</li>-->
					 <li class="dropdown">
					    <a class="dropdown-toggle"  href="pandaConfiguration"><i class="fa fa-glass"></i>&nbsp;Panda Tenant Configuration<i class="fa fa-caret-down username-margin"></i></a>
	                   
					   </li>
					
					   <li class="dropdown">
					    <a class="dropdown-toggle"  href="main"><i class="fa fa-glass"></i>&nbsp;Panda Sub Accounts<i class="fa fa-caret-down username-margin"></i></a>
	                   
					   </li>
					
				
	                <!-- /.dropdown -->
	                <li class="dropdown <#if request.getRequestURI().contains("marketAndAccountSettings")>active</#if>">
	                    <a class="dropdown-toggle" data-toggle="dropdown" href="#"><#if requestAttributes.userLogin?exists && requestAttributes.userLogin?has_content>${userLogin.userLoginId?if_exists}</#if><i class="fa fa-caret-down username-margin"></i></a>
	                    <ul class="dropdown-menu dropdown-user">
	                     
	                        <li><a href="/panda/control/logout"><i class="fa fa-sign-out icon-margin"></i> Logout</a> </li>
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

	
	<#--Error header-->
		
		<div>&nbsp;</div>
		
		
		<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
		
			<#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>
		
			
		
			<#-- display the error messages -->
	
			<#if errorMessageList?has_content >
		
			
			<#if opentapsErrors?has_content && opentapsErrors.toplevel?size != 0>
			  <#list opentapsErrors.toplevel as errorMsg>
		
			  <div class="alert alert-danger hideWhen">
		
			  <button class="close" data-close="alert"></button>
		
				<strong><i class="fa fa-remove" style="font-size: 15px;"></i>&nbsp;&nbsp;</strong>${StringUtil.wrapString(errorMsg)}
			   </div> 
			  </#list>
		</#if>
			  <#list errorMessageList?if_exists as errorMsg>
		
			   <div class="alert alert-danger hideWhen">
		
			   <button class="close" data-close="alert"></button>
		
				<strong><i class="fa fa-remove" style="font-size: 15px;"></i>&nbsp;&nbsp;</strong>${StringUtil.wrapString(errorMsg)}
		
			   </div>
			  </#list>
			</#if>
		
			
		
			<#--display event message-->
		
			<#if eventMessageList?has_content>
		
			  <#list eventMessageList as eventMsg>
		
				<div class="alert alert-success hideWhen">
		
				<button class="close" data-close="alert"></button>
		
					<strong> <i class="fa fa-check" style="color:#fff!important;    font-size: 20px;"></i>&nbsp;&nbsp;</strong>${StringUtil.wrapString(eventMsg)}
		
				 </div>	
			  </#list>
		
			</#if>
		</div><!--end of Error header-->
		
		<#--end of Error header-->
			<!--end of error handler-->
			<!--for complete header bar-->
			<div>&nbsp;</div>
<div class="page-container">
<!-- BEGIN CONTAINER -->
	<div class="page-content-wrapper">
		<!-- BEGIN CONTENT -->
		<div class="page-content">
		
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
jQuery(document).ready(function() {    
	
	//disable tooltip for the select option 	
	$(".bs-select button").attr("title","");
				
	// initiate layout and plugins
   	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	Demo.init(); // init demo features
	   	
   	QuickSidebar.init(); // init quick sidebar
   	ComponentsPickers.init();
	 $('#myTable').dataTable();	
	   
	   	//to hide error/success message after particular time period
		$(".hideWhen").delay(2000).slideUp(500);
	
});
</script>
<!-- END JAVASCRIPTS -->


		