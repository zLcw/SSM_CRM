<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String basePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>
<head>
    <base href="<%=basePath%>">

    <%-- 引入插件 --%>
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/echars/echarts.min.js"></script>
    <title>Echarts演示</title>
    <script type="text/javascript">
        // 当容器加载完之后调用，对容器调用工具函数
        $(function () {
            // 基于准备好的dom，初始化echarts实例
            var myChart = echarts.init(document.getElementById('main'));

            // 指定图表的配置项和数据
            var option = {
                title: {
                    text: 'ECharts 入门示例',
                    subtext: "副标题",
                    textStyle:{
                        color: 'skyblue',
                        fontStyle:'italic'
                    }
                },
                tooltip: {// 提示框

                },
                legend: { // 图例
                    data: ['销量','利润']
                },
                xAxis: { // x轴，数据项轴
                    data: ['衬衫', '羊毛衫', '雪纺衫', '裤子', '高跟鞋', '袜子','领带']
                },
                yAxis: { // y轴，数据量轴

                },
                series: [
                    { // 系列
                        name: '销量', // 系列名
                        type: 'bar', // 系列的类型bar--柱状图
                        data: [5, 20, 55, 10, 10, 20, 15] // 系列的数据
                    },
                    {
                        name: '利润',
                        type: 'line', // line--折线图
                        data: [15, 40, 83, 15, 20, 30, 30]
                    }
                ]
            };

            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        })
    </script>
</head>
<body>
    <!-- 为 ECharts 准备一个定义了宽高的 DOM -->
    <div id="main" style="width: 600px;height:400px;"></div>
</body>
</html>
