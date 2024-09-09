 <div class="small-box border rounded">
      <div class="inner float-left mr-4">
        <h4>FSRs Created/Closed</h4>
        <div class="dropdown">
	         <select name="srBarTimePeriod" id="srBarTimePeriod">
				  <option value="BAR_LAST_WEEK">Last Week</option>
				  <option value="BAR_THIS_WEEK">This Week</option>
				  <option value="BAR_LAST_MONTH">Last Month</option>
				  <option value="BAR_THIS_MONTH">This Month</option>
				  <option value="BAR_LAST_QUARTER">Last Quarter</option>
				  <option value="BAR_THIS_QUARTER">This Quarter</option>
		
			</select>
        </div>
      </div>
      <div class="icon float-left mt-3">
        <h4 class="created-date">${presentDate}</h4>
      </div>
	<div class="clearfix"></div>
    <div class="chart" id="container-3" style="width: 100%; height: 500px;display: inline-block;"></div> 
</div>  

<script type="text/javascript">


$(document).ready(function() {
	loadBarChart("BAR_LAST_WEEK");
	
	$("#srBarTimePeriod").change(function() {
		loadBarChart($("#srBarTimePeriod").val());
	});
});
									
function loadBarChart(segVal) {

		var dom = document.getElementById("container-3");
		var myChart = echarts.init(dom);
		var app = {};
		$.ajax({
		  async: false,
		  url:'/sr-portal/control/getSrBarChartData',
		  type:"POST",
		  data: {"segmentCode":segVal},
		  success: function(data){	
			
				var xAxisData = data.xAxisData;
				var openSrData = data.openSrData;
				var closedSrData = data.closedSrData;
				  					
				if(openSrData  || closedSrData ){
		
					var app = {};
					option = null;
					var posList = [
					    'left', 'right', 'top', 'bottom',
					    'inside',
					    'insideTop', 'insideLeft', 'insideRight', 'insideBottom',
					    'insideTopLeft', 'insideTopRight', 'insideBottomLeft', 'insideBottomRight'
					];
					
					app.configParameters = {
					    rotate: {
					        min: -90,
					        max: 90
					    },
					    align: {
					        options: {
					            left: 'left',
					            center: 'center',
					            right: 'right'
					        }
					    },
					    verticalAlign: {
					        options: {
					            top: 'top',
					            middle: 'middle',
					            bottom: 'bottom'
					        }
					    },
					    position: {
					        options: echarts.util.reduce(posList, function (map, pos) {
					            map[pos] = pos;
					            return map;
					        }, {})
					    },
					    distance: {
					        min: 10,
					        max: 100
					    }
					};
					
					app.config = {
					    rotate: 90,
					    align: 'left',
					    verticalAlign: 'middle',
					    position: 'insideBottom',
					    distance: 15,
					    onChange: function () {
					        var labelOption = {
					            normal: {
					                rotate: app.config.rotate,
					                align: app.config.align,
					                verticalAlign: app.config.verticalAlign,
					                position: app.config.position,
					                distance: app.config.distance
					            }
					        };
					        myChart.setOption({
					            series: [{
					                label: labelOption
					            }, {
					                label: labelOption
					            }, {
					                label: labelOption
					            }, {
					                label: labelOption
					            }]
					        });
					    }
					};
					
					
					var labelOption = {
					    show: true,
					    position: 'top',
					    distance: app.config.distance,
					    align: 'center',
					    verticalAlign: 'middle',
					    rotate: app.config.rotate,
					    formatter: '{c}  {name|}',
					    fontSize: 12,
					    rich: {
					        name: {
					            textBorderColor: 'white'
					        }
					    }
					};
					
					option = {
					    color: ['#003366', '#006699', '#4cabce', '#e5323e'],
					    tooltip: {
					        trigger: 'axis',
					        axisPointer: {
					            type: 'shadow'
					        }
					    },
					    legend: {
					        data: ['Open', 'Closed']
					    },
					    toolbox: {
					        show: true,
					        orient: 'vertical',
					        left: 'right',
					        top: 'center',
					        feature: {
					            mark: {show: true},
					           // dataView: {show: true, readOnly: false},
					           // magicType: {show: true, type: ['line', 'bar', 'stack', 'tiled']},
					          //  restore: {show: true},
					           // saveAsImage: {show: true}
					        }
					    },
					    xAxis: [
					        {
					            type: 'category',
					            axisTick: {show: false},
					            data: xAxisData
					        }
					    ],
					    yAxis: [
					        {
					            type: 'value'
					        }
					    ],
					    series: [
					        {
					            name: 'Open',
					            type: 'bar',
					            barGap: 0,
					            label: labelOption,
					            data: openSrData
					        },
					        {
					            name: 'Closed',
					            type: 'bar',
					            label: labelOption,
					            data:  closedSrData
					        }
					    ]
					};
					
					
					if (option && typeof option === "object") {
					    myChart.setOption(option, true);
					}
					
			
				  }else{
				
					$('#container-3').prepend('<img id="noDataImg" src="/bootstrap/images/nodata-found.jpg" class="nodata-img" />');
				
				}
		
		  }
		});
}										
							
</script>