  
 <#assign presentDate = "${presentDate?if_exists}">     

	 <div class="small-box border rounded">
	      <div class="inner float-left mr-4">
	        <h4>Outstanding FSRs by FSR Type</h4>
	        <div class="dropdown"></br>
	        </div>
	      </div>
	      <div class="icon float-left mt-3">
	        <h4 class="created-date">${presentDate}</h4>
	      </div>
	
	      <div class="clearfix"></div>
			<div class="chart" id="container-5" style="width: 100%; height: 500px;display: inline-block;"></div> 
	 </div>


<script type="text/javascript">

$(document).ready(function() {

loadSrType("SR_TYPE");

});
function loadSrType(segVal) {

		var dom = document.getElementById("container-5");
		var myChart = echarts.init(dom);
		var app = {};
		$.ajax({
		  async: false,
		  url:'/sr-portal/control/getSrByTypeChartData',
		  type:"POST",
		  data: {"segmentCode":segVal},
		  success: function(data){	
					
					var pieChartValData = data.pieChartVal;
					
				if(pieChartValData && pieChartValData != ""){
				
		
					option = null;
					option = {
			   			 title: {
					         text: "",
							        left: 'center',
							        top: 20,
							        textStyle: {
							            color: 'black'
							        } 
					    },
					    tooltip: {
					        trigger: 'item',
					        formatter: '{a} <br/>{b} : {c} ({d}%)'
					    },
					    legend: {
					        type: 'scroll',
					        orient: 'horizontal',
					        right: 1,
					        top: 2,
					        bottom: 50,
					       // data: data.legendData,
					
					       // selected: data.selected
					    },
					    series: [
					        {
					            name: '',
					            type: 'pie',
					            radius: '50%',
					             top: 40,
					            center: ['50%', '50%'],
					            data: pieChartValData,
					            emphasis: {
					                itemStyle: {
					                    shadowBlur: 10,
					                    shadowOffsetX: 0,
					                    shadowColor: 'rgba(0, 0, 0, 0.5)'
					                }
					            }
					        }
					    ]
					};
			
				if (option && typeof option === "object") {
				    myChart.setOption(option, true);
				}
				
				
				
			}else{
				
				$('#container-5').prepend('<img id="noDataImg" src="/bootstrap/images/nodata-found.jpg" class="nodata-img" />');
				
			}
		
		  }
		});
}
	
		
							
</script>