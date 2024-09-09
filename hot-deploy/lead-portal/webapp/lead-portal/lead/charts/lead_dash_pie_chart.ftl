<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script type="text/javascript" src="/bootstrap/js/charts/echarts.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/china.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/world.js"></script> 
<script type="text/javascript" src="/bootstrap/js/charts/bmap.min.js"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/echarts-stat/dist/ecStat.min.js"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/echarts/dist/extension/dataTool.min.js"></script>
<div class="row">
<div id="main" role="main">
<#-- <@sectionFrameHeader title="Lead Dashboard"  />  -->
	<div class="dash-title-account">
		<@sectionFrameHeader title="Lead Dashboard"  />
		<h2>Lead Segment Statistics</h2>
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
  url:'/lead-portal/control/LeadDashboardChart',
  type:"POST",
  data: {"roleTypeId":"LEAD","segmentCode":segVal},
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
	            left :["50%","10%"],
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