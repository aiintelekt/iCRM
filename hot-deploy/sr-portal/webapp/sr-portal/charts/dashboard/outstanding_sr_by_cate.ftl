   <div class="small-box border rounded">
           <div class="inner float-left mr-4">
                <h4>Outstanding FSRs by FSR Category</h4>
                   <div class="dropdown">
                     <select name="srSubCategory" id="srSubCategory">
					 </select>
                   </div>
              </div>
            <div class="icon float-left mt-3">
                 <h4 class="created-date">${presentDate}</h4>
            </div>
	<div class="clearfix"></div>
         <div class="chart" id="container-6" style="width: 100%; height: 500px;display: inline-block;"></div> 
         
  </div>
 
<script type="text/javascript">

$(document).ready(function() {
    loadSrCategory("SR_CATEGORY");
    loadsrSubCategory();
	
	$("#srSubCategory").change(function() {

		loadSrCategory($("#srSubCategory").val(), $("#srSubCategory option:selected").text(), "Y");

	});
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
												
function loadSrCategory(segVal, categoryName, isSubCat) {

		var dom = document.getElementById("container-6");
		var myChart = echarts.init(dom);
		var app = {};
		$.ajax({
		  async: false,
		  url:'/sr-portal/control/getSrByCateChartData',
		  type:"POST",
		  data: {"segmentCode":segVal, "isSubCat": isSubCat, "categoryName": categoryName},
		  success: function(data){	
					$("#container-6").show()
					var pieChartValData = data.pieChartVal;
					
				if(pieChartValData && pieChartValData != ""){
					//$("#container-6").empty();
		
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
			
			$("#container-6").empty();
			$('#container-6').prepend('<img id="noDataImg" src="/bootstrap/images/nodata-found.jpg" class="nodata-img" />');

			}
		
		  }
		});
}			

							
</script>