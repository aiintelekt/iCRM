<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<script type="text/javascript" src="/bootstrap/js/charts/echarts.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/china.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/world.js"></script> 
<script type="text/javascript" src="/bootstrap/js/charts/bmap.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/dataTool.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/ecStat.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/echarts-gl.min.js"></script>
<#--<script type="text/javascript" src="https://api.map.baidu.com/api?v=2.0&ak=xfhhaTThl11qYVrqLZii6w8qE5ggnhrY&__ec_v__=20190126"></script>-->


 <#--<#assign extraRight='<span id="refresh-SrLoad-btn" class="text-dark btn class="float-right"" title="Refresh"> 
		<i class="fa fa-refresh fa-0" aria-hidden="true"></i> Refresh
	</span>'> -->
<div class="row">
    <div id="main" role="main">
	<@sectionFrameHeader title="SR Dashboard" />
	
   <#--      <div class="col-md-12 col-lg-12 col-sm-12 ">
     <#--        <div class="border rounded bg-light margin-adj-accordian pad-top">
	<div class="row">  
	
 --	<div class="col-md-12 col-lg-12 col-sm-12">
				<@dynaScreen 
					instanceId="SR_CHART_SOURCE"
					modeOfAction="CREATE"
					/>	
		</div>	
 	</div>-->	
 </div>
</div>


<#include "component://ticket-portal/webapp/ticket-portal/charts/sr_dash_pie_chart_new.ftl"/>


<script type="text/javascript">
$("#refresh-SrLoad-btn").click(function() {
		
	$.ajax({
		 url:'/ticket-portal/control/srDashboardChart',
 		 type:"POST",
 		 data: {"loadAllSp":"Y"},
		 success: function(data) {
			
		}
	});
});
</script>
