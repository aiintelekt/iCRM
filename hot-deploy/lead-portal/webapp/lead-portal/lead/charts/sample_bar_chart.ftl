<script type="text/javascript" src="/bootstrap/js/echarts.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/dataTool.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/china.js"></script>
<script type="text/javascript" src="/bootstrap/js/world.js"></script> 
<script type="text/javascript" src="/bootstrap/js/bmap.min.js"></script>

<div id="container" style="height: 100%; -webkit-tap-highlight-color: transparent;  float: center; width: 500px; margin: 0 auto;height: 500px; user-select: none;" >
</div>
<script type="text/javascript">
	var dom = document.getElementById("container");
	var myChart = echarts.init(dom);
	var app = {};
	var xAxisData = ${StringUtil.wrapString(xAxisData)};
	var yAxisData = ${StringUtil.wrapString(yAxisData)};

	option = null;
	option = {
	    xAxis: {
	        type: 'category',
	        data: xAxisData
	    },
	    yAxis: {
	        type: 'value'
	    },
	    tooltip: {	      
	        formatter: ' <br/>{b} : {c} '
	    },
	    series: [{
	        data: yAxisData,
	        type: 'bar',
	        animationType: 'scale',
            animationEasing: 'elasticOut',
            animationDelay: function (idx) {
                return Math.random() * 200;
            }
	    }]
	};
	
	if (option && typeof option === "object") {
	    myChart.setOption(option, true);
	}
</script>


