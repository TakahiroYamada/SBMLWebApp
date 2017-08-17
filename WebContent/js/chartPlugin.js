Chart.plugins.register({
	 beforeDraw: function(chartInstance) {
	   var ctx = chartInstance.chart.ctx;
	   ctx.fillStyle = "white";
	   ctx.fillRect(0, 0, chartInstance.chart.width, chartInstance.chart.height);
	 }
});