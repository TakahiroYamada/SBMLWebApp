var req;
var canvas_jsondata = {
	      type: 'line',
	      data: {
	    	  datasets : []
	      },
	      options: {
	          scales: {
	            xAxes: [{
	              type: 'linear',
	              position: 'bottom',
	              ticks: {
	            	callback : function( value ){ return (( value % 10 ) == 0)? value : ''},
	                min: 0,
	                max: 100,
	                stepSize: 1
	              }
	            }],
	            yAxes: [{
	              ticks: {
	            	callback : function( value ){ return (( value % 10 ) == 0)? value : ''},
	                min: 0,
	                max: 200,
	                stepSize: 1
	              }
	            }]
	          }
	    }
}

function getSimulationResult(){
	var form_file = document.getElementById("simFile");
	var progressBar = document.getElementById("progress");
	
	var file = form_file.files[ 0 ];
	var filedata = new FormData();
	filedata.append("file" , file )
	
	if( window.XMLHttpRequest ){
		req = new XMLHttpRequest();
	}
	
	req.onprogress = function( e ){
		progressBar.max = e.total;
		progressBar.value = e.loaded;
	}
	req.onloadstart = function( e ){
		progressBar.value = 0;
	}
	req.onloadend = function( e ){
		progressBar.value = e.loaded;
	}
	
	configureFormData( filedata );
	req.open("POST" , "/GSOC_WebMavenProject/Simulation_Servlet" , true);
	req.onreadystatechange = callback;
	req.send( filedata );
}


function callback(){
	if( req.readyState == 4 ){
		if( req.status == 200 ){
			//window.location = "/GSOC_WebMavenProject/tmp/result.csv"
			configureCanvas();
		}
	}
}

function configureCanvas(){
	var canvas = document.getElementById("simulationCanvas");
	var tmpData = JSON.parse( req.response || "null");
	
	canvas_jsondata.data.datasets = tmpData.data;
	
	canvas_jsondata.options.scales.xAxes[0].ticks.max = tmpData.xmax;
	canvas_jsondata.options.scales.yAxes[0].ticks.max = tmpData.ymax;
	var myChart = new Chart(canvas , canvas_jsondata );
}
function configureFormData( formdata ){
	formdata.append("endpoint" , document.getElementById("endtime").value)
	formdata.append("numpoint" , document.getElementById("numpoint").value)
	formdata.append("library", document.getElementById("library").value)
}
