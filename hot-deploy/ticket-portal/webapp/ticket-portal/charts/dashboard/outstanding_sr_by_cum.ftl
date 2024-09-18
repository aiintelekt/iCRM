   <div class="small-box border rounded">
          <div class="inner float-left mr-4">
            <h4>SRs Created/Closed/Outstanding</h4>
            <div class="dropdown">
             
            <select name="srLineTimePeriod" id="srLineTimePeriod">
              <option value="LINE_CM_EOW_THISMONTH">This Month</option>
			  <option value="LINE_CM_EOW_LASTMONTH">Last Month</option>
			  <option value="LINE_CM_EOW_LASTQTR">Last Quarter</option>
			  <option value="LINE_CM_EOW_THISQTR">This Quarter</option>
  
			</select>
          
            </div>
          </div>
          <div class="icon float-left">
            <h4 class="created-date">${presentDate}</h4>
      </div>

      <div class="clearfix"></div>
	<div class="chart" id="container-4" style="width: 100%; height: 300px;display: inline-block;"></div>  
  </div>

<script type="text/javascript">


$(document).ready(function() {

		loadLineChart("LINE_CM_EOW_THISMONTH");

		$("#srLineTimePeriod").change(function() {
		loadLineChart($("#srLineTimePeriod").val());
		});
});

		
function loadLineChart(segVal) {

		var dom = document.getElementById("container-4");
		var myChart = echarts.init(dom);
		var app = {};
		$.ajax({
		  async: false,
		  url:'/ticket-portal/control/getSrCumLineChartData',
		  type:"POST",
		  data: {"segmentCode":segVal},
		  success: function(data){	
					
				  	var xAxisData = data.xAxisData;
					var closedLineSrData = data.closedLineSrData;
					var createdLineSrData = data.createdLineSrData;
					var outStandLineSrData  = data.outStandLineSrData;
					
					if(createdLineSrData  || closedLineSrData ){
					
		
					
						var app = {};
					
						var colors = ['#5793f3', '#d14a61', '#675bba'];
		
		
						option = {
					   		 color: colors,
					
					   		 tooltip: {
					          trigger: 'none',
					          axisPointer: {
					            type: 'cross'
					        }
					    },
					    legend: {
					        data:['Created', 'Closed', 'Outstanding']
					    },
					    grid: {
					        top: 70,
					        bottom: 50
					    },
					    xAxis: [
					        {
					            type: 'category',
					            axisTick: {
					                alignWithLabel: true
					            },
					            axisLine: {
					                onZero: false,
					                lineStyle: {
					                    color: colors[1]
					                }
					            },
					            
					            data: xAxisData
					        },
					        {
					            type: 'category',
					            axisTick: {
					                alignWithLabel: true
					            }
					            
					            
					        }
					    ],
					    yAxis: [
					        {
					            type: 'value'
					        }
					    ],
					    series: [
					        {
					            name: 'Created',
					            type: 'line',
					            xAxisIndex: 1,
					            smooth: true,
					            data: createdLineSrData
					        },
					        {
					            name: 'Closed',
					            xAxisIndex: 1,
					            type: 'line',
					            smooth: true,
					            data: closedLineSrData
					        },
					        {
					            name: 'Outstanding',
					            type: 'line',
					            xAxisIndex: 1,
					            smooth: true,
					            data:  outStandLineSrData
					        }
					    ]
					};
		
					
					if (option && typeof option === "object") {
					    myChart.setOption(option, true);
					}
				}	
		
		  }
		});
}			
	
				
							
</script>