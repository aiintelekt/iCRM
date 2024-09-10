<script type="text/javascript" src="/bootstrap/js/echarts.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/dataTool.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/china.js"></script>
<script type="text/javascript" src="/bootstrap/js/world.js"></script> 
<script type="text/javascript" src="/bootstrap/js/bmap.min.js"></script>

<div id="container" style="height: 100%; -webkit-tap-highlight-color: transparent; margin: 0 auto; width: 500px; height: 508px; user-select: none;" >

</div>
<script type="text/javascript">
var dom = document.getElementById("container");
var myChart = echarts.init(dom);
var app = {};
var pieChartValData = ${StringUtil.wrapString(pieChartData)};

option = null;
option =  {
    backgroundColor: '#2c343c',

    title: {
        text: 'Pie Chart',
        left: 'center',
        top: 20,
        textStyle: {
            color: '#ccc'
        }
    },

    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    
    series: [
        {
            name: 'My Sample',
            type: 'pie',
            radius: '60%',
            center: ['50%', '50%'],
            data: pieChartValData.sort(function (a, b) { return a.value - b.value; }),
            roseType: 'radius',
            label: {
                color: 'rgba(255, 255, 255, 0.3)'
            },
            labelLine: {
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.3)'
                },
                smooth: 0.2,
                length: 10,
                length2: 20
            },	           

            animationType: 'scale',
            animationEasing: 'elasticOut',
            animationDelay: function (idx) {
                return Math.random() * 200;
            }
        }
    ]
};

if (option && typeof option === "object") {
myChart.setOption(option, true);
}
</script>