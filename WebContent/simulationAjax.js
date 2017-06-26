var req;
var canvas_jsondata = {
	      type: 'line',
	      data: {
	    	  datasets : []
	      },
	      options: {
	    	  legend : {
	    		  position : 'top',
	    		  labels : {
	    			  fontSize : 12,
	    			  boxWidth : 12,
	    			  usePointStyle : true,
	    		  }
	    	  },
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
	req.open("POST" , "./Simulation_Servlet" , true);
	req.onreadystatechange = callback;
	req.send( filedata );
}


function callback(){
	if( req.readyState == 4 ){
		if( req.status == 200 ){
			//window.location = "/GSOC_WebMavenProject/tmp/result.csv"
			configureCanvas();
			addInitialValueSlider();
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
function addInitialValueSlider(){
	var JSONResponse = JSON.parse( req.response );
	var initialValue = JSONResponse.modelParameters.initValue;
	var initValueSlider = document.getElementById("initialValue-slider");
	$("#initialValue-slider").empty();
	for( var i = 0 ; i < initialValue.length ; i ++){
		var newDiv = document.createElement("div");
		
		var newParam = document.createElement("h5");
		newParam.appendChild( document.createTextNode( initialValue[ i ].sbmlID));
		
		var newParamSlider = document.createElement("div");
		newParamSlider.setAttribute("id", initialValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", initialValue[ i ].sbmlID + "input");
		newInputText.setAttribute("type","text")
		newInputText.setAttribute("readonly","readonly")
		
		newDiv.appendChild( newParam );
		newDiv.appendChild( newParamSlider );
		newDiv.appendChild( newInputText );
		initValueSlider.appendChild( newDiv );
		$("#" + initialValue[ i ].sbmlID).slider({
			min : 0,
			max : initialValue[ i ].initialValue * 2,
			step : 0.01,
			value : initialValue[ i ].initialValue ,
			change : function( e , ui ){
				$( "#" + this.id + "input").val( ui.value);
			},
			create : function( e , ui){
				$( "#" + this.id + "input").val($(this).slider('option','value'));
			}
		});
	}
}
function changeTab( tabname ){
	document.getElementById("initialValue").style.display = "none";
	document.getElementById("localParam").style.display = "none";
	document.getElementById("globalParam").style.display= "none";
	
	document.getElementById( tabname).style.display = "block";
}
