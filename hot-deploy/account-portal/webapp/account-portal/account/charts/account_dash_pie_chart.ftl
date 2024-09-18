<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<script type="text/javascript" src="/bootstrap/js/charts/echarts.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/china.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/world.js"></script> 
<script type="text/javascript" src="/bootstrap/js/charts/bmap.min.js"></script>
<div class="row">
<div id="main" role="main">
	<#-- <@sectionFrameHeader title="Account Dashboard" />  -->
	<div class="dash-title-account">
		<@sectionFrameHeader title="Account Dashboard" />
		<h2 class="dash-sub-title">Account Segment Statistics</h2>
		<select name="segmentCode" id="segmentCode">
			<option value="">Please select</option>
			<#list segmentDropDownList as type>
			  <option value="${type.code}">${type.value}</option>
			</#list>
		</select>
	</div>

	<div id="container-2" class="dash-port-account">

	</div>



</div>
</div>
<#-- pie chart -->
<script type="text/javascript">
$("#segmentCode").change(function() {
	var segText=$("#segmentCode option:selected" ).text();
	loadMainGrid($("#segmentCode").val(),segText);
});

function loadMainGrid(segVal,segText) {
$.ajax({
  async: false,
  url:'/account-portal/control/AccountDashboardChart',
  type:"POST",
  data: {"roleTypeId":"ACCOUNT","segmentCode":segVal},
  success: function(data){	 
		
		 var dom = document.getElementById("container-2");
			var myChart = echarts.init(dom);
			var app = {};
			var pieChartValData = $.parseJSON(data.pieChartVal);
			var names = $.parseJSON(data.namesList);
			//var count=data.namesSize;
			//var data=genData(count,names);
			option = null;
			option =  {title: {
		        text: segText,
		        subtext: '',
		        left: 'center'
		    },
		    tooltip: {
		        trigger: 'item',
		        formatter: '{b} : {c} ({d}%)',
		        extraCssText: "width:200px; white-space:pre-wrap;",
		        zlevel: 2,
	            z: 100
		    },
		    legend: {
		    	type: 'scroll',
		        orient: 'vertical',         
		       left :990,
		      
		        data: names
		    },
		    series: [
		        {
		        	 type: 'pie',
			         radius: '50%',
			         right: ['50%', '10%'],
		            selectedMode: 'single',
		            data:pieChartValData,
		            emphasis: {
		                itemStyle: {
		                    shadowBlur: 10,
		                    shadowOffsetX: 0,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            }
		        }
		    ]};
			
		

		if (option && typeof option === "object") {
		    myChart.setOption(option, true);
		}
	  }
});
}


	
</script>