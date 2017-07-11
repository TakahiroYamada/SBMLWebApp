var req;
var currentTab = "graph";
var currentFile = null;
var parameter_jsondata ={
		initValue : [],
		localParamValue : [],
		paramValue : []
};
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
	                max: 100
	              }
	            }],
	            yAxes: [{
	              ticks: {
	            	callback : function( value ){ return (( value % 10 ) == 0)? value : ''},
	                min: 0,
	                max: 100
	              }
	            }]
	          }
	    }
};

function getSimulationResult(){
	var form_file = document.getElementById("simFile");
	var progressBar = document.getElementById("progress");
	//Check file change , if file is changed , JSON data and parameter contents are initialized.
	if( currentFile != form_file.files[ 0 ].name){
		parameter_jsondata ={
				initValue : [],
				localParamValue : [],
				paramValue : []
		};
		$("#initialValue-slider").empty();
		$("#globalParam-slider").empty();
		$("#localParam-slider").empty();
		currentFile = form_file.files[ 0 ].name;
	}
	
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
			configureTable();
			addInitialValueSlider();
			addLocalParameterValueSlider();
			addGlobalParameterValueSlider();
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
	var myChart = new Chart(canvas , canvas_jsondata );
}
function configureTable(){
	var jsonResponse = JSON.parse(req.response);
	var simData = jsonResponse.data;
	
	var column = [{
		field : "Time",
		sortable : false,
		title : "Time"
	}];
	for( var i = 0 ; i < simData.length ; i ++){
		var tmpColumn = {
				field : simData[ i ].label,
				sortable : false,
				title : simData[ i ].label
		};
		column.push( tmpColumn)
	}
	document.getElementById("numTable").style.display = "block";
	$("#num-table").tabulator("setColumns" , column );
	$("#num-table").tabulator("clearData");
	
	var jsonNumData = [];
	var timePoint = simData[ 0 ].data.length
	
	for( var i = 0 ; i < timePoint ; i ++){
		var keyValueData = {};
		keyValueData["Time"] = simData[ 0 ].data[ i ].x;
		for( var j = 0 ; j < simData.length ; j ++){
			keyValueData[ simData[ j ].label ] = simData[ j ].data[ i ].y;
		}
		jsonNumData.push( keyValueData);
	}
	$("#num-table").tabulator("setData" , jsonNumData );
	changeGNTab( currentTab);
}
function configureFormData( formdata ){
	formdata.append("endpoint" , document.getElementById("endtime").value);
	formdata.append("numpoint" , document.getElementById("numpoint").value);
	formdata.append("library", document.getElementById("library").value);
	formdata.append("parameter" , JSON.stringify( parameter_jsondata));
}
function addInitialValueSlider(){
	var JSONResponse = JSON.parse( req.response );
	var initialValue = JSONResponse.modelParameters.initValue;
	var initValueSlider = document.getElementById("initialValue-slider");
	$("#initialValue-slider").empty();
	parameter_jsondata.initValue = [];
	for( var i = 0 ; i < initialValue.length ; i ++){
		// html dynamical setting
		var stepSize = 0;
		var newDiv = document.createElement("div");
		
		var newParam = document.createElement("h5");
		newParam.appendChild( document.createTextNode( initialValue[ i ].sbmlID));
		
		var newParamSlider = document.createElement("div");
		newParamSlider.setAttribute("id", initialValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", initialValue[ i ].sbmlID + "_input");
		newInputText.setAttribute("type","text")
		
		newDiv.appendChild( newParam );
		newDiv.appendChild( newParamSlider );
		newDiv.appendChild( newInputText );
		initValueSlider.appendChild( newDiv );
		
		if( initialValue[ i ].initialValue != 0.0 ){
			stepSize = Math.pow( 10 , (Math.floor( Math.log10( initialValue[ i ].initialValue )) - 1));
		}
		// JSON format edition
		parameter_jsondata.initValue.push({sbmlID : initialValue[ i ].sbmlID , initialValue : initialValue[ i ].initialValue , status : initialValue[ i ].status});
		
		// slider edition
		$("#" + initialValue[ i ].sbmlID).slider({
			min : 0,
			max : initialValue[ i ].initialValue * 2,
			step : stepSize,
			value : initialValue[ i ].initialValue ,
			change : function( e , ui ){
				$( "#" + this.id + "_input").val( ui.value);
				var sbmlId = this.id;
				var filtered = $.grep( parameter_jsondata.initValue , function( elem , index){
					return( elem.sbmlID == sbmlId);
				});
				filtered[ 0 ].initialValue = ui.value;
				getSimulationResult();
			},
			create : function( e , ui){
				$( "#" + this.id + "_input").val($(this).slider('option','value'));
			}
		});
		// text box edition
		$("#" + initialValue[ i ].sbmlID + "_input").change( function(){
			$("#" + this.id.replace("_input","")).slider("option","step" , Math.pow( 10 , (Math.floor( Math.log10( $(this).val())) - 1)));
			$("#" + this.id.replace("_input","")).slider("option","max",$(this).val() * 2);
			$("#" + this.id.replace("_input","")).slider("option","value",$(this).val())
		});
	}
}

function addGlobalParameterValueSlider(){
	var JSONResponse = JSON.parse( req.response );
	var parameterValue = JSONResponse.modelParameters.paramValue;
	var globalParamSlider = document.getElementById("globalParam-slider");
	$("#globalParam-slider").empty();
	parameter_jsondata.paramValue = [];
	for( var i = 0 ; i < parameterValue.length ; i ++){
		var stepSize = 0;
		var newDiv = document.createElement("div");
		
		var newParam = document.createElement("h5");
		newParam.appendChild( document.createTextNode( parameterValue[ i ].sbmlID));
		
		var newParamSlider = document.createElement("div");
		newParamSlider.setAttribute("id", parameterValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", parameterValue[ i ].sbmlID + "_input");
		newInputText.setAttribute("type","text")
		
		newDiv.appendChild( newParam );
		newDiv.appendChild( newParamSlider );
		newDiv.appendChild( newInputText );
		globalParamSlider.appendChild( newDiv );
		
		if( parameterValue[ i ].initialValue != 0.0 ){
			stepSize = Math.pow( 10 , (Math.floor( Math.log10( parameterValue[ i ].parameterValue )) - 1));
		}
		
		parameter_jsondata.paramValue.push({sbmlID : parameterValue[ i ].sbmlID , parameterValue : parameterValue[ i ].parameterValue});
		
		$("#" + parameterValue[ i ].sbmlID).slider({
			min : 0,
			max : parameterValue[ i ].parameterValue * 2,
			step : stepSize,
			value : parameterValue[ i ].parameterValue ,
			change : function( e , ui ){
				$( "#" + this.id + "_input").val( ui.value);
				var sbmlId = this.id;
				var filtered = $.grep( parameter_jsondata.paramValue , function( elem , index){
					return( elem.sbmlID == sbmlId);
				});
				filtered[ 0 ].parameterValue = ui.value;
				getSimulationResult();
			},
			create : function( e , ui){
				$( "#" + this.id + "_input").val($(this).slider('option','value'));
			}
		});
		$("#" + parameterValue[ i ].sbmlID + "_input").change( function(){
			$("#" + this.id.replace("_input","")).slider("option","step" , Math.pow( 10 , (Math.floor( Math.log10( $(this).val())) - 1)));
			$("#" + this.id.replace("_input","")).slider("option","max",$(this).val() * 2);
			$("#" + this.id.replace("_input","")).slider("option","value",$(this).val())
		});
	}
}

function addLocalParameterValueSlider(){
	var JSONResponse = JSON.parse( req.response );
	var parameterValue = JSONResponse.modelParameters.localParamValue;
	var localParamSlider = document.getElementById("localParam-slider");
	$("#localParam-slider").empty();
	parameter_jsondata.localParamValue = [];
	for( var i = 0 ; i < parameterValue.length ; i ++){
		var stepSize = 0;
		var newDiv = document.createElement("div");
		
		var newParam = document.createElement("h5");
		newParam.appendChild( document.createTextNode( parameterValue[ i ].reactionID + " : " +parameterValue[ i ].sbmlID));
		
		var newParamSlider = document.createElement("div");
		newParamSlider.setAttribute("id", parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID + "_input");
		newInputText.setAttribute("type","text")
		
		newDiv.appendChild( newParam );
		newDiv.appendChild( newParamSlider );
		newDiv.appendChild( newInputText );
		localParamSlider.appendChild( newDiv );
		
		if( parameterValue[ i ].initialValue != 0.0 ){
			stepSize = Math.pow( 10 , (Math.floor( Math.log10( parameterValue[ i ].parameterValue )) - 1));
		}
		parameter_jsondata.localParamValue.push({sbmlID : parameterValue[ i ].sbmlID , parameterValue : parameterValue[ i ].parameterValue , reactionID : parameterValue[ i ].reactionID , jsID : (parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID)});
		
		$("#" + parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID).slider({
			min : 0,
			max : parameterValue[ i ].parameterValue * 2,
			step : stepSize,
			value : parameterValue[ i ].parameterValue ,
			change : function( e , ui ){
				$( "#" + this.id + "_input").val( ui.value);
				var sbmlId = this.id;
				var filtered = $.grep( parameter_jsondata.localParamValue , function( elem , index){
					return( elem.jsID == sbmlId);
				});
				filtered[ 0 ].parameterValue = ui.value;
				getSimulationResult();
			},
			create : function( e , ui){
				$( "#" + this.id + "_input").val($(this).slider('option','value'));
			}
		});
		$("#" + parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID + "_input").change( function(){
			$("#" + this.id.replace("_input","")).slider("option","step" , Math.pow( 10 , (Math.floor( Math.log10( $(this).val())) - 1)));
			$("#" + this.id.replace("_input","")).slider("option","max",$(this).val() * 2);
			$("#" + this.id.replace("_input","")).slider("option","value",$(this).val())
		});
	}
}
function changeTab( tabname ){
	document.getElementById("initialValue").style.display = "none";
	document.getElementById("localParam").style.display = "none";
	document.getElementById("globalParam").style.display= "none";
	
	document.getElementById( tabname).style.display = "block";
}
function changeGNTab( tabname){
	document.getElementById("graph").style.display = "none";
	document.getElementById("numTable").style.display = "none";
	
	document.getElementById( tabname ).style.display = "block";
	currentTab = tabname;
}
function logarithmicFigure(){
	var checkBox = document.getElementById("logarithmic");
	var tmpData = JSON.parse( req.response || "null");
	
	if( checkBox.checked ){
		var canvas = document.getElementById("simulationCanvas");
		canvas_jsondata.options.scales.yAxes[0].type = "logarithmic";
		var myChart = new Chart(canvas , canvas_jsondata );
	}
	else{
		var canvas = document.getElementById("simulationCanvas");
		canvas_jsondata.options.scales.yAxes[0].type = "linear";
		var myChart = new Chart(canvas , canvas_jsondata );
	}
}
