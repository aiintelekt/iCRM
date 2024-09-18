<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="small-box border rounded">
	<div class="inner float-left mr-4">
		<h4>Outstanding Task by User</h4>
		<@inputDate 
		id="ot_timePeriod"
		disablePastDate="N"
		disableFutureDate="N"
		dateFormat="YYYY-MM-DD"
		/>
		<#-- <div class="dropdown">
			<select name="activity" id="activity">
				<option value="1">1 Day</option>
				<option value="7">7 Days</option>
				<option value="30">30 Days</option>
			</select>
		</div> -->
	</div>
	<div class="icon float-left">
		<h4 class="created-date">${presentDate?if_exists}</h4>
	</div>
	<div class="clearfix"></div>
	<div  id="outstanding_task_chart" style="width: 100%; height: 290px;display: inline-block;"></div> 
</div>

<script type="text/javascript">
$(document).ready(function() {
	loadOutstandingTaskChart();

	$("#ot_timePeriod_picker").on("dp.change", function (e) {
     	loadOutstandingTaskChart();
   	});   	
	
});

function loadOutstandingTaskChart() {

	var dom = document.getElementById("outstanding_task_chart");
	var myChart = echarts.init(dom);
	var app = {};
	$.ajax({
		async : false,
		url : '/activity-portal/control/getOutstandingTaskChartData',
		type : "POST",
		data : {
			"timePeriod" : $('#ot_timePeriod').val()
		},
		success : function(data) {

			var chartData = data.outstanding_task_chart_data;
			
			//if (chartData) {

				option = null;
				option = {
					title : {
						text : "",
						left : 'center',
						top : 20,
						textStyle : {
							color : 'black'
						}
					},
					tooltip : {
						trigger : 'item',
						formatter : '{a} <br/>{b} : {c} ({d}%)'
					},
					legend : {
						type : 'scroll',
						orient : 'horizontal',
						right : 1,
						top : 2,
						bottom : 50,
					// data: data.legendData,

					// selected: data.selected
					},
					series : [ {
						name : '',
						type : 'pie',
						radius : '60%%',
						top : 40,
						center : [ '50%', '50%' ],
						data : chartData,
						emphasis : {
							itemStyle : {
								shadowBlur : 10,
								shadowOffsetX : 0,
								shadowColor : 'rgba(0, 0, 0, 0.5)'
							}
						}
					} ]
				};

				if (option && typeof option === "object") {
					myChart.setOption(option, true);
				}

			//}

		}
	});
}
</script>

