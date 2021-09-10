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
	              scaleLabel : {
	            	  display : true,
	            	  labelString : "t"
	              },
	              ticks: {
	            	callback : function( value ){ return (( value % 10 ) == 0)? value : ''},
	                min: 0,
	                max: 100
	              }
	            }],
	            yAxes: [{
	              scaleLabel : {
	            	  display : true,
	            	  labelString : null
	              },
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
	var SBML_file;
	var tmpLegend = [];
	var form_file = document.getElementById("sbml-file");
	var filedata = new FormData();
	
	if( myChart != undefined ){
		for( var i = 0 ; i < myChart.data.datasets.length ; i ++){
			tmpLegend.push( myChart.getDatasetMeta( i ).hidden);
		}
	}
	
	if( ! ($("#check-biomodels")[ 0 ].checked || exampleFrag)){
		SBML_file = form_file.files[ 0 ];
		filedata.append("file" , SBML_file );
	}
	else{
		SBML_file = new Blob( [ModelSBML.SBML] , {type : "text/csv;charset=utf-8"});
		SBML_file.name = ModelSBML.SBMLId + ".xml";
		filedata.append("file" , SBML_file , ModelSBML.SBMLId + ".xml");
	}
	// Check file change , if file is changed , JSON data and parameter contents are initialized.
	if( currentFile != SBML_file.name){
		parameter_jsondata ={
				initValue : [],
				compartmentValue : [],
				localParamValue : [],
				paramValue : []
		};
		if( myChart != undefined ){
			myChart.destroy();
		}
		// currentFile = form_file.files[ 0 ].name;
	}
	configureFormData( filedata );
	//$.ajax("./Simulation_Servlet" , {
	$.ajax("./Producer" , {
		async : true,
		type : "post",
		data : filedata,
		processData : false,
		contentType : false,
		xhr : function(){
			XHR = $.ajaxSettings.xhr();
			if( XHR.upload){
				XHR.upload.addEventListener("progress" , function( e ){
					per_progress = parseInt( e.loaded/e.total*10000)/100;
					$("#sim-progress").val( per_progress);
				});
			}
			return XHR;
		}
	}).done( function( result ){
		sessionId = result.sessionId;
		responseData = result;
		callback_Simulation( SBML_file.name , responseData  ,  tmpLegend);
		loadingObject.LoadingOverlay("hide");
	}).fail( function( result ){
		errorSetting( result.responseJSON.errorMessage , result.responseJSON.solveText);
		$("#warningModal").modal("show");
		$("#modalButton").off("click");
		$("#modalButton").on("click" , function(){
			$("#warningModal").modal("hide");
			loadingObject.LoadingOverlay("hide");
		});
	});
}

function callback_Simulation( fileName,responseData , tmpLegend ){
	//window.location = "/GSOC_WebMavenProject/tmp/result.csv"
	var form_file = document.getElementById("sbml-file");
	configureCanvas( responseData  , tmpLegend);
	configureTable( responseData );

	if( currentFile != fileName ){
		addInitialValueSlider( responseData , "");
		addCompartmentSlider( responseData , "");
		addLocalParameterValueSlider( responseData , "");
		addGlobalParameterValueSlider( responseData , "");
		currentFile = fileName;
	}
	$("#download").removeClass("disabled");
}
// currently depricated
function addWarningText( responseData){
	if( responseData.warningText != null){
		warningSetting(  "Input SBML model is incorrect",responseData.warningText );
		$("#warningModal").modal("show");
		$("#modalButton").off("click");
		$("#modalButton").on("click" , function(){
			$("#warningModal").modal("hide");
		});
	}
}
function configureCanvas( responseData  , tmpLegend){
	var canvas = document.getElementById("simulationCanvas");
	var tmpData = responseData;
	canvas_jsondata.data.datasets = tmpData.data;
	canvas_jsondata.options.scales.xAxes[0].ticks.max = tmpData.xmax;
	canvas_jsondata.options.scales.yAxes[0].ticks.max = tmpData.ymax;
	canvas_jsondata.options.scales.yAxes[0].scaleLabel.labelString = tmpData.yaxisLabel; 
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
	var timePoint = simData[ 0 ].data.length;
	
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
	formdata.append("Type" , "simulation");
}
function addInitialValueSlider( responseData, analysis){
	var JSONResponse = responseData;
	var initialValue = JSONResponse.modelParameters.initValue;
	var initValueSlider = document.getElementById(analysis + "initialValue-slider");
	$("#" + analysis + "initialValue-slider").empty();
	parameter_jsondata.initValue = [];
	if( initialValue.length == 0){
		$("#" + analysis + "init-item").addClass("disabled");
		$("#" + analysis + "initialValue").removeClass("active")
	}
	else{
		$("#" + analysis + "init-item").removeClass("disabled");
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
		newParamSlider.setAttribute("id", analysis + initialValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", analysis + initialValue[ i ].sbmlID + "_input");
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
		if( analysis == "sted-"){
			sted_parameter_jsondata.initValue.push({sbmlID : initialValue[ i ].sbmlID , initialValue : initialValue[ i ].initialValue , status : initialValue[ i ].status});
		}
		else{
			parameter_jsondata.initValue.push({sbmlID : initialValue[ i ].sbmlID , initialValue : initialValue[ i ].initialValue , status : initialValue[ i ].status});
		}
		// slider edition
		$("#" + analysis + initialValue[ i ].sbmlID).slider({
			min : 0,
			max : initialValue[ i ].initialValue * 2,
			step : stepSize,
			value : initialValue[ i ].initialValue ,
			change : function( e , ui ){
				$( "#" + this.id + "_input").val( ui.value);
				var sbmlId = this.id;
				
				if( sbmlId.indexOf("sted-") >= 0){
					sbmlId = sbmlId.replace("sted-" , "")
					var filtered = $.grep( sted_parameter_jsondata.initValue , function( elem , index){
						return( elem.sbmlID == sbmlId);
					});
					filtered[ 0 ].initialValue = ui.value;
					$("#stedButton").LoadingOverlay("show");
					getSteadyResult( $("#stedButton") );
				}
				else{
					var filtered = $.grep( parameter_jsondata.initValue , function( elem , index){
					return( elem.sbmlID == sbmlId);
					});
					filtered[ 0 ].initialValue = ui.value;
					
					$("#simulationCanvas").LoadingOverlay("show");
					getSimulationResult( $("#simulationCanvas") );
				}
			},
			create : function( e , ui){
				$( "#" + this.id + "_input").val($(this).slider('option','value'));
			}
		});
		// text box edition
		$("#" + analysis + initialValue[ i ].sbmlID + "_input").on("keypress" , function(e){
			if( e.which == 13 ){
				if( !errorCheck_Simulation()){
					$("#" + this.id.replace("_input","")).slider("option","step" , Math.pow( 10 , (Math.floor( Math.log10( $(this).val())) - 1)));
					$("#" + this.id.replace("_input","")).slider("option","max",$(this).val() * 2);
					$("#" + this.id.replace("_input","")).slider("option","value",$(this).val())
				}
			}
		});
	}
}

function addGlobalParameterValueSlider( responseData , analysis ){
	var JSONResponse = responseData;
	var parameterValue = JSONResponse.modelParameters.paramValue;
	var globalParamSlider = document.getElementById(analysis + "globalParam-slider");
	$("#" + analysis + "globalParam-slider").empty();
	parameter_jsondata.paramValue = [];
	if( parameterValue.length == 0){
		$("#" + analysis + "global-item").addClass("disabled");
		$("#" + analysis + "globalParam").removeClass("active");
	}
	else{
		$("#" + analysis + "global-item").removeClass("disabled");
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
		newParamSlider.setAttribute("id", analysis + parameterValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", analysis + parameterValue[ i ].sbmlID + "_input");
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
		
		if( analysis == "sted-"){
			sted_parameter_jsondata.paramValue.push({sbmlID : parameterValue[ i ].sbmlID , parameterValue : parameterValue[ i ].parameterValue});
		}
		else{
			parameter_jsondata.paramValue.push({sbmlID : parameterValue[ i ].sbmlID , parameterValue : parameterValue[ i ].parameterValue});
		}
		$("#" + analysis + parameterValue[ i ].sbmlID).slider({
			min : 0,
			max : parameterValue[ i ].parameterValue * 2,
			step : stepSize,
			value : parameterValue[ i ].parameterValue ,
			change : function( e , ui ){
				$( "#" + this.id + "_input").val( ui.value);
				var sbmlId = this.id;
				
				if( sbmlId.indexOf("sted-") >= 0){
					sbmlId = sbmlId.replace("sted-" , "")
					var filtered = $.grep( sted_parameter_jsondata.paramValue , function( elem , index){
						return( elem.sbmlID == sbmlId);
					});
					filtered[ 0 ].parameterValue = ui.value;
					$("#stedButton").LoadingOverlay("show");
					getSteadyResult( $("#stedButton") );
				}
				else{
					var filtered = $.grep( parameter_jsondata.paramValue , function( elem , index){
						return( elem.sbmlID == sbmlId);
					});
					filtered[ 0 ].parameterValue = ui.value;
					$("#simulationCanvas").LoadingOverlay("show")
					getSimulationResult($("#simulationCanvas"));
				}
			},
			create : function( e , ui){
				$( "#" + this.id + "_input").val($(this).slider('option','value'));
			}
		});
		$("#" + analysis + parameterValue[ i ].sbmlID + "_input").on( "keypress", function( e ){
			if( e.which == 13 ){
				if( !errorCheck_Simulation()){
					$("#" + this.id.replace("_input","")).slider("option","step" , Math.pow( 10 , (Math.floor( Math.log10( $(this).val())) - 1)));
					$("#" + this.id.replace("_input","")).slider("option","max",$(this).val() * 2);
					$("#" + this.id.replace("_input","")).slider("option","value",$(this).val());
				}
			}
		});
	}
}
function addCompartmentSlider( responseData , analysis){
	var JSONResponse = responseData;
	var compartmentValue = JSONResponse.modelParameters.compartmentValue;
	var compartmentSlider = document.getElementById(analysis + "compartmentValue-slider");
	if( compartmentValue.length == 0){
		$("#" + analysis + "comp-item").addClass("disabled");
		$("#" + analysis + "compartmentValue").removeClass("active");
	}
	else{
		$("#" + analysis + "comp-item").removeClass("disabled");
	}
	$("#" + analysis + "compartmentValue-slider").empty();
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
		newParamSlider.setAttribute("id", analysis + compartmentValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", analysis + compartmentValue[ i ].sbmlID + "_input");
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
		
		if( analysis == "sted-"){
			sted_parameter_jsondata.compartmentValue.push({sbmlID : compartmentValue[ i ].sbmlID , size : compartmentValue[ i ].size});
		}
		else{
			parameter_jsondata.compartmentValue.push({sbmlID : compartmentValue[ i ].sbmlID , size : compartmentValue[ i ].size});
		}
		$("#" + analysis + compartmentValue[ i ].sbmlID).slider({
			min : 0,
			max : compartmentValue[ i ].size * 2,
			step : stepSize,
			value : compartmentValue[ i ].size ,
			change : function( e , ui ){
				$( "#" + this.id + "_input").val( ui.value);
				var sbmlId = this.id;
				if( sbmlId.indexOf("sted-") >= 0 ){
					sbmlId = sbmlId.replace("sted-" , "")
					var filtered = $.grep( sted_parameter_jsondata.compartmentValue , function( elem , index){
						return( elem.sbmlID == sbmlId);
					});
					filtered[ 0 ].size = ui.value;
					$("#stedButton").LoadingOverlay("show");
					getSteadyResult( $("#stedButton") );
				}
				else{
					var filtered = $.grep( parameter_jsondata.compartmentValue , function( elem , index){
						return( elem.sbmlID == sbmlId);
					});
					filtered[ 0 ].size = ui.value;
					$("#simulationCanvas").LoadingOverlay("show");
					getSimulationResult($("#simulationCanvas"));
				}
			},
			create : function( e , ui){
				$( "#" + this.id + "_input").val($(this).slider('option','value'));
			}
		});
		$("#" + analysis + compartmentValue[ i ].sbmlID + "_input").on("keypress" ,  function( e ){
			if( e.which == 13 ){
				if( !errorCheck_Simulation()){
					$("#" + this.id.replace("_input","")).slider("option","step" , Math.pow( 10 , (Math.floor( Math.log10( $(this).val())) - 1)));
					$("#" + this.id.replace("_input","")).slider("option","max",$(this).val() * 2);
					$("#" + this.id.replace("_input","")).slider("option","value",$(this).val())
				}
			}
		});
	}
}

function addLocalParameterValueSlider( responseData , analysis){
	var JSONResponse = responseData;
	var parameterValue = JSONResponse.modelParameters.localParamValue;
	var localParamSlider = document.getElementById(analysis + "localParam-slider");
	$("#" + analysis + "localParam-slider").empty();
	parameter_jsondata.localParamValue = [];
	if( parameterValue.length == 0){
		$("#" + analysis + "local-item").addClass("disabled");
		$("#" + analysis + "localParam").removeClass("active");
	}
	else{
		$("#" + analysis + "local-item").removeClass("disabled");
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
		newParamSlider.setAttribute("id", analysis + parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID);
		
		var newInputText = document.createElement("input");
		newInputText.setAttribute("id", analysis + parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID + "_input");
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
		
		if( analysis == "sted-"){
			sted_parameter_jsondata.localParamValue.push({sbmlID : parameterValue[ i ].sbmlID , parameterValue : parameterValue[ i ].parameterValue , reactionID : parameterValue[ i ].reactionID , jsID : (parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID)});
		}
		else{
			parameter_jsondata.localParamValue.push({sbmlID : parameterValue[ i ].sbmlID , parameterValue : parameterValue[ i ].parameterValue , reactionID : parameterValue[ i ].reactionID , jsID : (parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID)});
		}
		$("#" + analysis + parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID).slider({
			min : 0,
			max : parameterValue[ i ].parameterValue * 2,
			step : stepSize,
			value : parameterValue[ i ].parameterValue ,
			change : function( e , ui ){
				$( "#" + this.id + "_input").val( ui.value);
				var sbmlId = this.id;
				
				if( sbmlId.indexOf("sted-") >= 0){
					sbmlId = sbmlId.replace("sted-" , "")
					var filtered = $.grep( sted_parameter_jsondata.localParamValue , function( elem , index){
						return( elem.jsID == sbmlId);
					});
					filtered[ 0 ].parameterValue = ui.value;
					$("#stedButton").LoadingOverlay("show");
					getSteadyResult( $("#stedButton") );
				}
				else{
					var filtered = $.grep( parameter_jsondata.localParamValue , function( elem , index){
						return( elem.jsID == sbmlId);
					});
					filtered[ 0 ].parameterValue = ui.value;
					$("#simulationCanvas").LoadingOverlay("show")
					getSimulationResult($("#simulationCanvas"));
				}
			},
			create : function( e , ui){
				$( "#" + this.id + "_input").val($(this).slider('option','value'));
			}
		});
		$("#" + analysis + parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID + "_input").on("keypress" , function( e ){
			if( e.which == 13 ){
				if( !errorCheck_Simulation()){
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
		if( !($("#check-biomodels")[0].checked || exampleFrag) ){
			var model_name = $("#sbml-file")[0].files[0].name.replace(".xml" , "");
		}
		else{
			var model_name = ModelSBML.SBMLId
		}
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
