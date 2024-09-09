   <div class="small-box border rounded">
      <div class="inner float-left mr-4">
        <h4>FSRs Created/Closed/Outstanding(EOW)</h4>
        <div class="dropdown"></br>
         
        </div>
      </div>
      <div class="icon float-left mt-3">
        <h4 class="created-date">${presentDate}</h4>
      </div>

      <div class="clearfix"></div>
	<div class="chart" id="container-7" style="width: 100%; height: 500px;display: inline-block;"></div>  
    </div>
  </div>	
 

<script type="text/javascript">

$(document).ready(function() {

loadLineChartEow();

});
				
										
function loadLineChartEow() {

		var dom = document.getElementById("container-7");
		var myChart = echarts.init(dom);
		var app = {};
		$.ajax({
		  async: false,
		  url:'/sr-portal/control/getSrEowLineChartData',
		  type:"POST",
		  data: {"segmentCode":"LINE_EOW"},
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
				}else{
				
					$('#container-7').prepend('<img id="noDataImg" src="/bootstrap/images/nodata-found.jpg" class="nodata-img" />');
				
				}	
		
		  }
		});
}			
					
							
</script>