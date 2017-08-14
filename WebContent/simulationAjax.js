var Ymin;
var sessionId= "";
var currentTab = "graph";
var currentFile = null;
var myChart;
var parameter_jsondata ={
		initValue : [],
		compartmentValue : [],
		localParamValue : [],
		paramValue : []
};
var canvas_jsondata = {
	      type: 'line',
	      data: {
	    	  datasets : []
	      },
	      options: {
	    	  maintainAspectRatio : false,
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
	    },
	    animation : true,
	    multiTooltipTemplate: "<%= datasetLabel %> - <%= value %>"
};

function getSimulationResult( loadingObject ){
	var tmpLegend = [];
	var form_file = document.getElementById("simFile");
	
	if( myChart != undefined ){
		for( var i = 0 ; i < myChart.data.datasets.length ; i ++){
			tmpLegend.push( myChart.getDatasetMeta( i ).hidden);
		}
	}
	//Check file change , if file is changed , JSON data and parameter contents are initialized.
	if( currentFile != form_file.files[ 0 ].name){
		parameter_jsondata ={
				initValue : [],
				compartmentValue : [],
				localParamValue : [],
				paramValue : []
		};
		$("#initialValue-slider").empty();
		$("#compartmentValue-slider").empty();
		$("#globalParam-slider").empty();
		$("#localParam-slider").empty();
		currentFile = form_file.files[ 0 ].name;
	}
	
	var file = form_file.files[ 0 ];
	var filedata = new FormData();
	filedata.append("file" , file )
	configureFormData( filedata );
	$.ajax("./Simulation_Servlet" , {
		async : true,
		type : "post",
		dataType : "text",
		data : filedata,
		processData : false,
		contentType : false,
		xhr : function(){
			XHR = $.ajaxSettings.xhr();
			if( XHR.upload){
				XHR.upload.addEventListener("progress" , function( e ){
					per_progress = parseInt( e.loaded/e.total*10000)/100;
					$("#progress").val( per_progress);
				});
			}
			return XHR;
		}
	}).done( function( result ){
		sessionId = result.sessionId;
		responseData = JSON.parse( result )
		callback( responseData  ,  tmpLegend);
		loadingObject.LoadingOverlay("hide");
	});
}

function callback( responseData , tmpLegend ){
	//window.location = "/GSOC_WebMavenProject/tmp/result.csv"
	configureCanvas( responseData  , tmpLegend);
	configureTable( responseData );
	addInitialValueSlider( responseData );
	addCompartmentSlider( responseData );
	addLocalParameterValueSlider( responseData );
	addGlobalParameterValueSlider( responseData );
	$("#download").removeClass("disabled");
}

function configureCanvas( responseData  , tmpLegend){
	var canvas = document.getElementById("simulationCanvas");
	var tmpData = responseData;
	canvas_jsondata.data.datasets = tmpData.data;
	canvas_jsondata.options.scales.xAxes[0].ticks.max = tmpData.xmax;
	canvas_jsondata.options.scales.yAxes[0].ticks.max = tmpData.ymax;
	configureMyChartLegend( canvas , canvas_jsondata  , tmpLegend );
	$("#graph-contents").show();
	$("#tabParameter").show();
	Ymin = responseData.ymin;
}

function configureMyChartLegend( canvas , jsondata  , tmpLegend){
	var tmp = [];
	if( myChart != undefined ){
		if( tmpLegend == null ){
			for( var i = 0 ; i < myChart.data.datasets.length ; i ++){
				tmp.push( myChart.getDatasetMeta( i ).hidden );
			}
		}
		else{
			tmp = tmpLegend;
		}
		
		myChart.destroy();
		myChart = new Chart( canvas , jsondata );
		if( tmp.length == myChart.data.datasets.length ){
			for( var i = 0 ; i < tmp.length ; i ++){
				myChart.getDatasetMeta( i ).hidden = tmp[ i ];
			}
			myChart.update();
		}
	}
	else{
		myChart = new Chart( canvas , canvas_jsondata);
	}
}
function configureTable( responseData ){
	var jsonResponse = responseData;
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
				title : simData[ i ].label + "(" + simData[ i ].units + ")"
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
	document.getElementById("numTable").style.display = "";
}
function configureFormData( formdata ){
	formdata.append("endpoint" , document.getElementById("endtime").value);
	formdata.append("numpoint" , document.getElementById("numpoint").value);
	formdata.append("tolerance" , document.getElementById("tolerance").value)
	formdata.append("library", document.getElementById("library").value);
	formdata.append("parameter" , JSON.stringify( parameter_jsondata));
	formdata.append("SessionId" , sessionId );
}
function addInitialValueSlider( responseData){
	var JSONResponse = responseData;
	var initialValue = JSONResponse.modelParameters.initValue;
	var initValueSlider = document.getElementById("initialValue-slider");
	$("#initialValue-slider").empty();
	parameter_jsondata.initValue = [];
	if( initialValue.length == 0){
		$("#init-item").addClass("disabled");
		$("#initialValue").removeClass("active")
	}
	else{
		$("#init-item").removeClass("disabled");
	}
	for( var i = 0 ; i < initialValue.length ; i ++){
		// html dynamical setting
		var stepSize = 0;
		var newDiv = document.createElement("div");
		var newParam = document.createElement("p");
		if( initialValue[ i ].sbmlName ){
			newParam.appendChild( document.createTextNode( initialValue[ i ].sbmlName));
		}
		else{
			newParam.appendChild( document.createTextNode( initialValue[ i ].sbmlID));
		}
		var newParamSlider = document.createElement("div");
		newParamSlider.setAttribute("id", initialValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", initialValue[ i ].sbmlID + "_input");
		newInputText.setAttribute("type","number")
		
		newParam.setAttribute("style","display:inline-block;width:20%;text-align:center");
		newParamSlider.setAttribute("style","display:inline-block;width:50%;text-align:center");
		newInputText.setAttribute("style","display:inline-block;width:20%;text-align:center");
		
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
				$("#simulationCanvas").LoadingOverlay("show");
				getSimulationResult( $("#simulationCanvas") );
			},
			create : function( e , ui){
				$( "#" + this.id + "_input").val($(this).slider('option','value'));
			}
		});
		// text box edition
		$("#" + initialValue[ i ].sbmlID + "_input").on("keypress" , function(e){
			if( e.which == 13 ){
				if( !errorCheck()){
					$("#" + this.id.replace("_input","")).slider("option","step" , Math.pow( 10 , (Math.floor( Math.log10( $(this).val())) - 1)));
					$("#" + this.id.replace("_input","")).slider("option","max",$(this).val() * 2);
					$("#" + this.id.replace("_input","")).slider("option","value",$(this).val())
				}
			}
		});
	}
}

function addGlobalParameterValueSlider(){
	var JSONResponse = responseData;
	var parameterValue = JSONResponse.modelParameters.paramValue;
	var globalParamSlider = document.getElementById("globalParam-slider");
	$("#globalParam-slider").empty();
	parameter_jsondata.paramValue = [];
	if( parameterValue.length == 0){
		$("#global-item").addClass("disabled");
		$("#globalParam").removeClass("active");
	}
	else{
		$("#global-item").removeClass("disabled");
	}
	for( var i = 0 ; i < parameterValue.length ; i ++){
		var stepSize = 0;
		var newDiv = document.createElement("div");
		
		var newParam = document.createElement("p");
		if( parameterValue[ i ].sbmlName ){
			newParam.appendChild( document.createTextNode( parameterValue[ i ].sbmlName));
		}
		else{
			newParam.appendChild( document.createTextNode( parameterValue[ i ].sbmlID));
		}
		var newParamSlider = document.createElement("div");
		newParamSlider.setAttribute("id", parameterValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", parameterValue[ i ].sbmlID + "_input");
		newInputText.setAttribute("type","number")
		
		newParam.setAttribute("style","display:inline-block;width:20%;text-align:center");
		newParamSlider.setAttribute("style","display:inline-block;width:50%;text-align:center");
		newInputText.setAttribute("style","display:inline-block;width:20%;text-align:center");
		
		newDiv.appendChild( newParam );
		newDiv.appendChild( newParamSlider );
		newDiv.appendChild( newInputText );
		globalParamSlider.appendChild( newDiv );
		
		if( parameterValue[ i ].parameterValue != 0.0 ){
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
				$("#simulationCanvas").LoadingOverlay("show")
				getSimulationResult($("#simulationCanvas"));
			},
			create : function( e , ui){
				$( "#" + this.id + "_input").val($(this).slider('option','value'));
			}
		});
		$("#" + parameterValue[ i ].sbmlID + "_input").on( "keypress", function( e ){
			if( e.which == 13 ){
				if( !errorCheck()){
					$("#" + this.id.replace("_input","")).slider("option","step" , Math.pow( 10 , (Math.floor( Math.log10( $(this).val())) - 1)));
					$("#" + this.id.replace("_input","")).slider("option","max",$(this).val() * 2);
					$("#" + this.id.replace("_input","")).slider("option","value",$(this).val());
				}
			}
		});
	}
}
function addCompartmentSlider(){
	var JSONResponse = responseData;
	var compartmentValue = JSONResponse.modelParameters.compartmentValue;
	var compartmentSlider = document.getElementById("compartmentValue-slider");
	if( compartmentValue.length == 0){
		$("#comp-item").addClass("disabled");
		$("#compartmentValue").removeClass("active");
	}
	else{
		$("#comp-item").removeClass("disabled");
	}
	$("#compartmentValue-slider").empty();
	parameter_jsondata.compartmentValue = [];

	for( var i = 0 ; i < compartmentValue.length ; i ++){
		var stepSize = 0;
		var newDiv = document.createElement("div");
		
		var newParam = document.createElement("p");
		if( compartmentValue[ i ].sbmlName ){
			newParam.appendChild( document.createTextNode( compartmentValue[ i ].sbmlName));
		}
		else{
			newParam.appendChild( document.createTextNode( compartmentValue[ i ].sbmlID));
		}
		var newParamSlider = document.createElement("div");
		newParamSlider.setAttribute("id", compartmentValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", compartmentValue[ i ].sbmlID + "_input");
		newInputText.setAttribute("type","number")
		
		newParam.setAttribute("style","display:inline-block;width:20%;text-align:center");
		newParamSlider.setAttribute("style","display:inline-block;width:50%;text-align:center");
		newInputText.setAttribute("style","display:inline-block;width:20%;text-align:center");
		
		newDiv.appendChild( newParam );
		newDiv.appendChild( newParamSlider );
		newDiv.appendChild( newInputText );
		compartmentSlider.appendChild( newDiv );
		
		if( compartmentValue[ i ].size != 0.0 ){
			stepSize = Math.pow( 10 , (Math.floor( Math.log10( compartmentValue[ i ].size )) - 1));
		}
		
		parameter_jsondata.compartmentValue.push({sbmlID : compartmentValue[ i ].sbmlID , size : compartmentValue[ i ].size});
		
		$("#" + compartmentValue[ i ].sbmlID).slider({
			min : 0,
			max : compartmentValue[ i ].size * 2,
			step : stepSize,
			value : compartmentValue[ i ].size ,
			change : function( e , ui ){
				$( "#" + this.id + "_input").val( ui.value);
				var sbmlId = this.id;
				var filtered = $.grep( parameter_jsondata.compartmentValue , function( elem , index){
					return( elem.sbmlID == sbmlId);
				});
				filtered[ 0 ].size = ui.value;
				$("#simulationCanvas").LoadingOverlay("show");
				getSimulationResult($("#simulationCanvas"));
			},
			create : function( e , ui){
				$( "#" + this.id + "_input").val($(this).slider('option','value'));
			}
		});
		$("#" + compartmentValue[ i ].sbmlID + "_input").on("keypress" ,  function( e ){
			if( e.which == 13 ){
				if( !errorCheck()){
					$("#" + this.id.replace("_input","")).slider("option","step" , Math.pow( 10 , (Math.floor( Math.log10( $(this).val())) - 1)));
					$("#" + this.id.replace("_input","")).slider("option","max",$(this).val() * 2);
					$("#" + this.id.replace("_input","")).slider("option","value",$(this).val())
				}
			}
		});
	}
}

function addLocalParameterValueSlider(){
	var JSONResponse = responseData;
	var parameterValue = JSONResponse.modelParameters.localParamValue;
	var localParamSlider = document.getElementById("localParam-slider");
	$("#localParam-slider").empty();
	parameter_jsondata.localParamValue = [];
	if( parameterValue.length == 0){
		$("#local-item").addClass("disabled");
		$("#localParam").removeClass("active");
	}
	else{
		$("#local-item").removeClass("disabled");
	}
	for( var i = 0 ; i < parameterValue.length ; i ++){
		var stepSize = 0;
		var newDiv = document.createElement("div");
		
		var newParam = document.createElement("p");
		
		if(  parameterValue[ i ].reactionName && parameterValue[ i ].sbmlName){
			newParam.appendChild( document.createTextNode( parameterValue[ i ].reactionName + " : " +parameterValue[ i ].sbmlName));
		}
		else if( !(parameterValue[ i ].reactionName) && parameterValue[ i ].sbmlName){
			newParam.appendChild( document.createTextNode( parameterValue[ i ].reactionID + " : " +parameterValue[ i ].sbmlName));
		}
		else if ( parameterValue[ i ].reactionName && !(parameterValue[ i ].sbmlName)){
			newParam.appendChild( document.createTextNode( parameterValue[ i ].reactionName + " : " +parameterValue[ i ].sbmlID));
		}
		else{
			newParam.appendChild( document.createTextNode( parameterValue[ i ].reactionID + " : " +parameterValue[ i ].sbmlID));
		}
		var newParamSlider = document.createElement("div");
		newParamSlider.setAttribute("id", parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID + "_input");
		newInputText.setAttribute("type","number")
		
		newParam.setAttribute("style","display:inline-block;width:20%;text-align:center");
		newParamSlider.setAttribute("style","display:inline-block;width:50%;text-align:center");
		newInputText.setAttribute("style","display:inline-block;width:20%;text-align:center");
		
		newDiv.appendChild( newParam );
		newDiv.appendChild( newParamSlider );
		newDiv.appendChild( newInputText );
		localParamSlider.appendChild( newDiv );
		
		if( parameterValue[ i ].parameterValue != 0.0 ){
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
				$("#simulationCanvas").LoadingOverlay("show")
				getSimulationResult($("#simulationCanvas"));
			},
			create : function( e , ui){
				$( "#" + this.id + "_input").val($(this).slider('option','value'));
			}
		});
		$("#" + parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID + "_input").on("keypress" , function( e ){
			if( e.which == 13 ){
				if( !errorCheck()){
					$("#" + this.id.replace("_input","")).slider("option","step" , Math.pow( 10 , (Math.floor( Math.log10( $(this).val())) - 1)));
					$("#" + this.id.replace("_input","")).slider("option","max",$(this).val() * 2);
					$("#" + this.id.replace("_input","")).slider("option","value",$(this).val());
				}
			}
		});
	}
}
function logarithmicFigure( axis ){
	var checkBox = document.getElementById( axis );
	if( axis == 'logarithmicY'){
		if( checkBox.checked ){
			var canvas = document.getElementById("simulationCanvas");
			canvas_jsondata.options.scales.yAxes[0].type = "logarithmic";
			// Chart.js is crushed when the minimum of scale is less than 1.0e-10
			// I should ask it to developer!
			if( Math.pow( 10 , (Math.floor( Math.log10( Ymin )))) < 1.0e-10){
				canvas_jsondata.options.scales.yAxes[0].ticks.min = 1.0e-10;
			}
			else{
				canvas_jsondata.options.scales.yAxes[0].ticks.min = Math.pow( 10 , (Math.floor( Math.log10( Ymin ))));
			}
			configureMyChartLegend( canvas , canvas_jsondata  , null);
		}
		else{
			var canvas = document.getElementById("simulationCanvas");
			canvas_jsondata.options.scales.yAxes[0].type = "linear";
			canvas_jsondata.options.scales.yAxes[0].ticks.min = 0;
			configureMyChartLegend( canvas , canvas_jsondata  , null);
		}
	}
	else if( axis == 'logarithmicX'){
		if( checkBox.checked ){
			var canvas = document.getElementById("simulationCanvas");
			canvas_jsondata.options.scales.xAxes[0].type = "logarithmic";
			canvas_jsondata.options.scales.xAxes[0].ticks.min = document.getElementById("endtime").value / document.getElementById("numpoint").value ;
			configureMyChartLegend( canvas , canvas_jsondata  , null);
		}
		else{
			var canvas = document.getElementById("simulationCanvas");
			canvas_jsondata.options.scales.xAxes[0].type = "linear";
			canvas_jsondata.options.scales.xAxes[0].ticks.min = 0;
			configureMyChartLegend( canvas , canvas_jsondata , null );
		}
	}
}
function checkActivePanel(){
	var initActive = $("#initialValue").hasClass("active");
	var compActive = $("#compartmentValue").hasClass("active");
	var localActive = $("#localParam").hasClass("active");
	var globalActive = $("#globalParam").hasClass("active");
	
	if( !(initActive || compActive || localActive || globalActive)){
		if( ! $("#init-item").hasClass("disabled")){
			$("#initialValue").addClass("active");
		}
		else if( ! $("#comp-item").hasClass("disabled")){
			$("#compartmentValue").addClass("active");
		}
		else if( ! $("#local-item").hasClass("disabled")){
			$("#localParam").addClass("active");
		}
		else if( ! $("#global-item").hasClass("disabled")){
			$("#globalParam").addClass("active");
		}
	}
}
function downloadData(){
	if( !$("#download").hasClass("disabled")){
		var model_name = $("#simFile")[0].files[0].name.replace(".xml" , "");
		// Canvas
		var canvas = document.getElementById("simulationCanvas");
		var url = canvas.toDataURL();
		var zip = new JSZip();
		var savable = new Image();
		savable.src = url;
		zip.file(model_name + "_result.png" , savable.src.substr(savable.src.indexOf(',')+1) , {base64 : true});
	
		// csv Data from tabulator
		
		var csvContent = tabulatorToCsv("#num-table");
		var csv_blob = new Blob([csvContent ] , {type : "text/csv;charset=utf-8"})
		zip.file( model_name + "_result.csv" , csv_blob);
		zip.generateAsync({type:"blob"}).then( function( content){
			saveAs( content , model_name + "_result.zip");
		});
	}
}
