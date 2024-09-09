<div class="small-box border rounded">
	<div class="inner float-left mr-4">
		<h4>Tasks Created/Closed/Outstanding by Week</h4>
		<select name="tbw_timePeriod" id="tbw_timePeriod">
         	<option value="THIS_MONTH">This Month</option>
         	<option value="THIS_QUARTER">This Quarter</option>
			<option value="LAST_MONTH">Last Month</option>
			<option value="LAST_QUARTER">Last Quarter</option>
		</select>
	</div>
	<div class="icon float-left">
		<h4 class="created-date">${presentDate?if_exists}</h4>
	</div>
	<div class="clearfix"></div>
	<div  id="task_by_week_chart" style="width: 100%; height: 300px;display: inline-block;"></div> 
</div>

<script type="text/javascript">
$(document).ready(function() {
loadTaskByWeekChart();

$("#tbw_timePeriod").change(function() {
	loadTaskByWeekChart();
});

});

function loadTaskByWeekChart() {

	var dom = document.getElementById("task_by_week_chart");
	var myChart = echarts.init(dom);
	var app = {};
	$.ajax({
		async : false,
		url : '/activity-portal/control/getTaskByWeekChartData',
		type : "POST",
		data : {
			"timePeriod" : $('#tbw_timePeriod').val()
		},
		success : function(data) {
		
			var chartData = data.task_by_week_chart_data;

			var xAxisData = chartData.xAxisData;
			var completedActivityData = chartData.completedActivityData;
			var createdActivityData = chartData.createdActivityData;
			var outstandingActivityData = chartData.outstandingActivityData;

			if (createdActivityData || completedActivityData) {

				var app = {};

				var colors = [ '#5793f3', '#d14a61', '#675bba' ];

				option = {
					color : colors,

					tooltip : {
						trigger : 'none',
						axisPointer : {
							type : 'cross'
						}
					},
					legend : {
						data : [ 'Created', 'Completed', 'OutStanding' ]
					},
					grid : {
						top : 70,
						bottom : 50
					},
					xAxis : [ {
						type : 'category',
						axisTick : {
							alignWithLabel : true
						},
						axisLine : {
							onZero : false,
							lineStyle : {
								color : colors[1]
							}
						},

						data : xAxisData
					}, {
						type : 'category',
						axisTick : {
							alignWithLabel : true
						}

					} ],
					yAxis : [ {
						type : 'value'
					} ],
					series : [ {
						name : 'Created',
						type : 'line',
						xAxisIndex : 1,
						smooth : true,
						data : createdActivityData
					}, {
						name : 'Completed',
						xAxisIndex : 1,
						type : 'line',
						smooth : true,
						data : completedActivityData
					}, {
						name : 'OutStanding',
						type : 'line',
						xAxisIndex : 1,
						smooth : true,
						data : outstandingActivityData
					} ]
				};

				if (option && typeof option === "object") {
					myChart.setOption(option, true);
				}
			}

		}
	});
}
</script>

