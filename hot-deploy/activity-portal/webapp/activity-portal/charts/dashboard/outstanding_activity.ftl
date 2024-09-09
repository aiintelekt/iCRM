<div class="small-box border rounded">
	<div class="inner float-left mr-4">
		<h4>Outstanding Activities by Type, by User</h4>
		<#-- <select name="tbd_timePeriod" id="tbd_timePeriod">
		  	<option value="THIS_WEEK">This Week</option>
		  	<option value="THIS_MONTH">This Month</option>
		  	<option value="THIS_QUARTER">This Quarter</option>
		  	<option value="LAST_WEEK">Last Week</option>
		  	<option value="LAST_MONTH">Last Month</option>
		  	<option value="LAST_QUARTER">Last Quarter</option>
		</select> -->
	</div>
	<div class="icon float-left">
		<h4 class="created-date">${presentDate?if_exists}</h4>
	</div>
	<div class="clearfix"></div>
	<div  id="outstanding_activity_chart" style="width: 100%; height: 330px;display: inline-block;"></div> 
</div>

<script type="text/javascript">
$(document).ready(function() {

loadOutstandingActivityChart();

});

function loadOutstandingActivityChart() {

	var dom = document.getElementById("outstanding_activity_chart");
	var myChart = echarts.init(dom);
	var app = {};
	$.ajax({
		async : false,
		url : '/activity-portal/control/getOutstandingActivityChartData',
		type : "POST",
		data : {
			//"timePeriod" : $("#tbd_timePeriod").val()
		},
		success : function(data) {

			var chartData = data.outstanding_activity_chart_data;
			
			var xAxisData = chartData.xAxisData;
			var taskTypeData = chartData.taskTypeData;
			var appointmentTypeData = chartData.appointmentTypeData;
			
			var app = {};
			option = null;
			var posList = [ 'left', 'right', 'top', 'bottom', 'inside', 'insideTop',
					'insideLeft', 'insideRight', 'insideBottom', 'insideTopLeft',
					'insideTopRight', 'insideBottomLeft', 'insideBottomRight' ];
		
			app.configParameters = {
				rotate : {
					min : -90,
					max : 90
				},
				align : {
					options : {
						left : 'left',
						center : 'center',
						right : 'right'
					}
				},
				verticalAlign : {
					options : {
						top : 'top',
						middle : 'middle',
						bottom : 'bottom'
					}
				},
				position : {
					options : echarts.util.reduce(posList, function(map, pos) {
						map[pos] = pos;
						return map;
					}, {})
				},
				distance : {
					min : 10,
					max : 100
				}
			};
		
			app.config = {
				rotate : 90,
				align : 'left',
				verticalAlign : 'middle',
				position : 'insideBottom',
				distance : 15,
				onChange : function() {
					var labelOption = {
						normal : {
							rotate : app.config.rotate,
							align : app.config.align,
							verticalAlign : app.config.verticalAlign,
							position : app.config.position,
							distance : app.config.distance
						}
					};
					myChart.setOption({
						series : [ {
							label : labelOption
						}, {
							label : labelOption
						}, {
							label : labelOption
						}, {
							label : labelOption
						} ]
					});
				}
			};
		
			var labelOption = {
				show : true,
				position : 'top',
				distance : app.config.distance,
				align : 'center',
				verticalAlign : 'middle',
				rotate : app.config.rotate,
				formatter : '{c}  {name|}',
				fontSize : 12,
				rich : {
					name : {
						textBorderColor : 'white'
					}
				}
			};
		
			option = {
				color : [ '#003366', '#006699', '#4cabce', '#e5323e' ],
				tooltip : {
					trigger : 'axis',
					axisPointer : {
						type : 'shadow'
					}
				},
				legend : {
					data : [ 'Task', 'Appointment' ]
				},
				toolbox : {
					show : true,
					orient : 'vertical',
					left : 'right',
					top : 'center',
					feature : {
						mark : {
							show : true
						},
					// dataView: {show: true, readOnly: false},
					// magicType: {show: true, type: ['line', 'bar', 'stack', 'tiled']},
					// restore: {show: true},
					// saveAsImage: {show: true}
					}
				},
				xAxis : [ {
					type : 'category',
					axisTick : {
						show : false
					},
					data : xAxisData
				} ],
				yAxis : [ {
					type : 'value'
				} ],
				series : [ {
					name : 'Task',
					type : 'bar',
					barGap : 0,
					label : labelOption,
					data : taskTypeData
				}, {
					name : 'Appointment',
					type : 'bar',
					label : labelOption,
					data : appointmentTypeData
				} ]
			};
		
			if (option && typeof option === "object") {
				myChart.setOption(option, true);
			}
		}
	});
}
</script>

