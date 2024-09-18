  
 <#assign presentDate = "${presentDate?if_exists}">     

<div class="col-lg-12 col-md-12 col-sm-12 mt-3">
            <div class="row">
             <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
                <div class="small-box border rounded">
                  <div class="inner float-left mr-4">
                    <h4>Outstanding SRs by User</h4>
                     <div class="dropdown"></br>
                    </div>
                  </div>
                  <div class="icon float-left">
                    <h4 class="created-date">${presentDate}</h4>
                   
                  </div>

                  <div class="clearfix"></div>
					<div  id="container-2" style="width: 100%; height: 300px;display: inline-block;"></div> 
                </div>
              </div>

              <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
                <div class="small-box border rounded">
                  <div class="inner float-left mr-4">
                    <h4>Outstanding SRs by SR Type</h4>
                    <div class="dropdown"></br>
                    </div>
                  </div>
                  <div class="icon float-left">
                    <h4 class="created-date">${presentDate}</h4>
                  </div>

                  <div class="clearfix"></div>
					<div class="chart" id="container-5" style="width: 100%; height: 300px;display: inline-block;"></div> 
                </div>
              </div>

              <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
                <div class="small-box border rounded">
                  <div class="inner float-left mr-4">
                    <h4>Outstanding SRs by SR Category</h4>
                    <div class="dropdown">
                     <select name="srSubCategory" id="srSubCategory">
		 
		  
					</select>
                    </div>
                  </div>
                  <div class="icon float-left">
                    <h4 class="created-date">${presentDate}</h4>
                  </div>

                  <div class="clearfix"></div>
                     <div class="chart" id="container-6" style="width: 100%; height: 300px;display: inline-block;"></div> 

                </div>
              </div>

              <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
                <div class="small-box border rounded">
                  <div class="inner float-left mr-4">
                    <h4>SRs Created/Closed</h4>
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
                  <div class="icon float-left">
                    <h4 class="created-date">${presentDate}</h4>
                  </div>

                  <div class="clearfix"></div>
                    <div class="chart" id="container-3" style="width: 100%; height: 300px;display: inline-block;"></div> 


                </div>
              </div>

              <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
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
              </div>
              
              
              
               <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
                <div class="small-box border rounded">
                  <div class="inner float-left mr-4">
                    <h4>SRs Created/Closed/Outstanding(EOW)</h4>
                    <div class="dropdown"></br>
                     
                    </div>
                  </div>
                  <div class="icon float-left">
                    <h4 class="created-date">${presentDate}</h4>
                  </div>

                  <div class="clearfix"></div>
				<div class="chart" id="container-7" style="width: 100%; height: 300px;display: inline-block;"></div>  
                </div>
              </div>
            </div>
                    
              
            </div>
          </div>




<script type="text/javascript">

loadBarChart("BAR_LAST_WEEK");
loadLineChart("LINE_CM_EOW_THISMONTH");
loadSrByUser("SR_USER");
loadSrType("SR_TYPE");
loadSrCategory("SR_CATEGORY");
loadsrSubCategory();
loadLineChartEow();

	$("#srBarTimePeriod").change(function() {
		loadBarChart($("#srBarTimePeriod").val());
	});
	$("#srLineTimePeriod").change(function() {
		loadLineChart($("#srLineTimePeriod").val());
	});
	$("#srSubCategory").change(function() {

		loadSrCategory($("#srSubCategory").val(), $("#srSubCategory option:selected").text(), "Y");

	});

function loadsrSubCategory(){
	var srCategoryId  = $("#srCategoryId").val();
	var categoryOptions = "";
	categoryOptions += '<option value="ALL">'+"All"+'</option>';

	$.ajax({
			type: "GET",
			url: "getSrCategory",
			async: false,
			success: function(data) {
	
				var sourceDesc = data.results;
				for (var i = 0; i < data.length; i++) {
					var category = data[i];
	        			if(category){
						categoryOptions += '<option value="'+category.srCategoryId+'">'+category.srCategoryDesc+'</option>';
	        			}
				}
			}
		});
	$("#srSubCategory").html(categoryOptions);

}


function loadSrByUser(segVal) {

		var dom = document.getElementById("container-2");
		var myChart = echarts.init(dom);
		var app = {};
		$.ajax({
		  async: false,
		  url:'/ticket-portal/control/getSrByUserChartData',
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
					            name: 'Open SRs By',
					            type: 'pie',
					            radius: '60%%',
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
				
				
				
			}
		
		  }
		});
}

function loadSrType(segVal) {

		var dom = document.getElementById("container-5");
		var myChart = echarts.init(dom);
		var app = {};
		$.ajax({
		  async: false,
		  url:'/ticket-portal/control/getSrByTypeChartData',
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
					            radius: '60%%',
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
				
				
				
			}
		
		  }
		});
}
	
		
function loadSrCategory(segVal, categoryName, isSubCat) {

		var dom = document.getElementById("container-6");
		var myChart = echarts.init(dom);
		var app = {};
		$.ajax({
		  async: false,
		  url:'/ticket-portal/control/getSrByCateChartData',
		  type:"POST",
		  data: {"segmentCode":segVal, "isSubCat": isSubCat, "categoryName": categoryName},
		  success: function(data){	
					$("#container-6").show()
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
					        top: 5,
					        bottom: 50,
					       // data: data.legendData,
					
					       // selected: data.selected
					    },
					    series: [
					        {
					            name: '',
					            type: 'pie',
					            radius: '60%%',
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
			
			$("#container-6").hide()

			}
		
		  }
		});
}			
				
					
							
function loadBarChart(segVal) {

		var dom = document.getElementById("container-3");
		var myChart = echarts.init(dom);
		var app = {};
		$.ajax({
		  async: false,
		  url:'/ticket-portal/control/getSrBarChartData',
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
					
			
				  }
		
		  }
		});
}						

	
		
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
	
				
										
function loadLineChartEow() {

		var dom = document.getElementById("container-7");
		var myChart = echarts.init(dom);
		var app = {};
		$.ajax({
		  async: false,
		  url:'/ticket-portal/control/getSrEowLineChartData',
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
				}	
		
		  }
		});
}			
					
							
</script>