var req;
var canvas_jsondata = {
	      type: 'line',
	      data: {
	        datasets: [{
	          label: null,
	          data: [],
	          fill : false,
	          backgroundColor: "rgba(179,181,198,0.2)",
	          borderColor: "rgba(179,181,198,1)",
	          pointBorderColor: "rgba(179,181,198,1)",
	          pointBackgroundColor: "#fff"
	        }]
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
	req.open("POST" , "/GSOC_WebMavenProject/Simulation_Servlet" , true);
	req.onreadystatechange = callback;
	req.send( filedata );
}


function callback(){
	if( req.readyState == 4 ){
		if( req.status == 200 ){
			//window.location = "/GSOC_WebMavenProject/tmp/result.csv"
			console.log(req.response)
		}
	}
}
