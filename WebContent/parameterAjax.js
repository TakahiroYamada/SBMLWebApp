var sessionId = "";
var beforeChart;
var afterChart;
var currentFile = null;
var parameter_jsondata ={
		initValue : [],
		compartmentValue : [],
		localParamValue : [],
		paramValue : []
}
var canvas_jsondata_Before = {
	      type: 'line',
	      data: {
	    	  datasets : []
	      },
	      options: {
	    	  maintainAspectRatio : false ,
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
	                max: 200
	              }
	            }]
	          }
	    }
}

var canvas_jsondata_After = {
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
	                max: 100,
	              }
	            }],
	            yAxes: [{
	              ticks: {
	            	callback : function( value ){ return (( value % 10 ) == 0)? value : ''},
	                min: 0,
	                max: 200,
	              }
	            }]
	          }
	    }
}


function showExpFile(){
	var exp_file = document.getElementById("expFile");
	var algorithm = document.getElementById("lvparam");
	exp_file.style.display = "block";
	algorithm.style.display ="block";
}

function analyzeData( loadingObject ){
	//Each form data is got as JavaScript variable
	var model_file = document.getElementById("paraFile");
	var exp_file = document.getElementById("expData");
	var progressBar = document.getElementById("progress");
	
	var SBML_file = model_file.files[ 0 ];
	var Exp_file = exp_file.files[ 0 ];
	
	// filedata contains all data transfered to Server side Servlet
	var filedata = new FormData();
	
	//Transfered data is added in filedata
	filedata.append("SBMLFile" , SBML_file );
	filedata.append("ExpFile" , Exp_file);
	filedata.append("SessionId" , sessionId);

	// Parameter data is set to filedata(FormData)
	configureFormData( filedata );
	
	$.ajax("./ParameterEstimation_Servlet" , {
		async : true ,
		type : "post" ,
		data : filedata , 
		processData : false ,
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
		responseData = result;
		callback( responseData );
		loadingObject.LoadingOverlay("hide");
	}).fail( function( result ){
		errorSetting( result.responseText , "Please check your input file which is really SBML.")
		$("#warningModal").modal("show");
		$("#modalButton").off("click");
		$("#modalButton").on("click" , function(){
			$("#warningModal").modal("hide");
			loadingObject.LoadingOverlay("hide");
		});
	});;
}

function callback( responseData ){
	addWarningText( responseData );
	configureCanvas( responseData );
	configureTable( responseData );
	if( currentFile != $("#paraFile")[ 0 ].files[ 0 ].name){
		parameter_jsondata ={
				initValue : [],
				compartmentValue : [],
				localParamValue : [],
				paramValue : []
		};
		$("#globalParam-slider").empty();
		$("#localParam-slider").empty();
		addLocalParamSlider( responseData );
		addGlobalParamSlider( responseData );
		currentFile = $("#paraFile")[ 0 ].files[ 0 ].name;
	}
	$("#download").removeClass("disabled")
}
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
function configureCanvas( responseData ){
	$("#graph-contents").show();
	$("#tabParameter").show();
	var canvas_before = document.getElementById("beforeCanvas");
	var canvas_after = document.getElementById("afterCanvas");
	
	var tmpData = responseData;
	
	canvas_jsondata_Before.data.datasets = tmpData.beforeFitting.data;
	canvas_jsondata_After.data.datasets = tmpData.afterFitting.data;
	
	//Experiment data is added
	for( var i = 0 ; i < tmpData.expDataSets.length ; i ++){
		canvas_jsondata_Before.data.datasets.push( tmpData.expDataSets[ i ]);
		canvas_jsondata_After.data.datasets.push( tmpData.expDataSets[ i ]);
	}
	
	canvas_jsondata_Before.options.scales.xAxes[0].ticks.max = tmpData.beforeFitting.xmax;
	canvas_jsondata_Before.options.scales.yAxes[0].ticks.max = tmpData.beforeFitting.ymax;
	
	canvas_jsondata_After.options.scales.xAxes[0].ticks.max = tmpData.afterFitting.xmax;
	canvas_jsondata_After.options.scales.yAxes[0].ticks.max = tmpData.afterFitting.ymax;
	if( beforeChart != undefined ){
		beforeChart.destroy();
		afterChart.destroy();
	}
	beforeChart = new Chart(canvas_before , canvas_jsondata_Before );
	afterChart = new Chart(canvas_after , canvas_jsondata_After );
}
function configureTable( responseData ){
	var jsonData = responseData;
	var parameterTransitData = jsonData.updateParam;
	
	
	var column = [
		{ field : "Reaction" , sortable : false , title : "Reaction ID"},
		{ field : "Parameter" , sortable : false , title : "Parameter ID"},
		{ field : "Lower" , sortable : false , title : "Lower bound"},
		{ field : "Start" , sortable : false , title : "Start value"},
		{ field : "Upper" , sortable : false , title : "Upper bound"},
		{ field : "Update" , sortable : false , title : "Updated Value"},
		{ field : "Unit" , sortable : false , title : "Unit"}
		];
	
	document.getElementById("numTable").style.display = "block";
	$("#num-table").tabulator("setColumns" , column);
	$("#num-table").tabulator("clearData");
	
	var transitData = [];
	for( var i = 0 ; i < parameterTransitData.length ; i ++){
		var tmpData = {};
		paramData = parameterTransitData[ i ].parameterId;
		if( parameterTransitData[ i ].global ){
			tmpData["Reaction"] = "";
			tmpData["Parameter"] = paramData.substr( paramData.indexOf("[") + 1 , paramData.indexOf("]") - paramData.indexOf("[") - 1);
		}
		else{
			tmpData["Reaction"] = paramData.match(/\((.+)\)/)[1];
			tmpData["Parameter"] = paramData.substr( paramData.indexOf(".") + 1);
		}
		tmpData["Lower"] = parameterTransitData[ i ].lower;
		tmpData["Start"] = parameterTransitData[ i ].startValue;
		tmpData["Upper"] = parameterTransitData[ i ].upper;
		tmpData["Update"] = parameterTransitData[ i ].updatedValue;
		tmpData["Unit"] = parameterTransitData[ i ].unit;
		transitData.push( tmpData );
	}
	$("#num-table").tabulator("setData" , transitData );
	document.getElementById("numTable").style.display = "";
}
function configureFormData( formdata ){
	
	formdata.append("algorithm" , document.getElementById("algorithm").value);
	var algorithm = document.getElementById("algorithm");
	if( algorithm.value == "lv"){
		formdata.append("itermax" , document.getElementById("lvite").value);
		formdata.append("tolerance" , document.getElementById("lvtol").value);
	}
	else if( algorithm.value == "nelder"){
		formdata.append("itermax" , document.getElementById("nelite").value);
		formdata.append("tolerance" , document.getElementById("neltol").value);
	}
	else if( algorithm.value == "particleSwarm" ){
		formdata.append("itermax" , document.getElementById("partite").value);
		formdata.append("swarmsize" , document.getElementById("partsize").value);
		formdata.append("stdDeviation" , document.getElementById("partstd").value);
		formdata.append("randomNumGenerator" , document.getElementById("partran").value);
		formdata.append("seed" , document.getElementById("partseed").value);
	}
	else if( algorithm.value == "diffEvol"){
		formdata.append("population" , document.getElementById("difevolpopu").value);
		formdata.append("randomNumGenerator" , document.getElementById("difevolran").value);
		formdata.append("seed" , document.getElementById("difevolseed").value);
	}
	formdata.append("parameter" , JSON.stringify( parameter_jsondata ));
		
}
function configureAlgorithmForm(){
	var algorithm = document.getElementById("algorithm");
	if( algorithm.value == "lv"){
		configureAlgorithmVisualization("lvparam");
	}
	else if( algorithm.value == "nelder"){
		configureAlgorithmVisualization("nelparam");
	}
	else if( algorithm.value == "particleSwarm"){
		configureAlgorithmVisualization("partparam");
	}
	else if( algorithm.value == "diffEvol"){
		configureAlgorithmVisualization("difevolparam");
	}
}
function configureAlgorithmVisualization( algorithm ){
	document.getElementById("lvparam").style = "display:none";
	document.getElementById("nelparam").style = "display:none";
	document.getElementById("partparam").style = "display:none";
	document.getElementById("difevolparam").style = "display:none";
	
	document.getElementById( algorithm ).style = "diplay:block";
}
function addLocalParamSlider( responseData ){
	var JSONResponse = responseData;
	var parameterValue = JSONResponse.modelParameters.localParamValue;
	var localParamSlider = document.getElementById("localParam-slider");
	
	$("#localParam-slider").empty();
	parameter_jsondata.localParamValue = [];
	
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
		
		var newLowerInputText = document.createElement("input");
		newLowerInputText.setAttribute("id", parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID + "_lower_input");
		newLowerInputText.setAttribute("type","number")
		
		var newUpperInputText = document.createElement("input");
		newUpperInputText.setAttribute("id", parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID + "_upper_input");
		newUpperInputText.setAttribute("type","number")
		
		newLowerInputText.setAttribute("style" , "display:inline-block ;width : 20%; text-align:center")
		newParamSlider.setAttribute("style" , "display:inline-block ; width : 50% ;text-align:center")
		newUpperInputText.setAttribute("style" , "display:inline-block ;width : 20% ; text-align:center")
		
		newDiv.appendChild( newParam );
		newDiv.appendChild( newLowerInputText );
		newDiv.appendChild( newParamSlider );
		newDiv.appendChild( newUpperInputText );
		localParamSlider.appendChild( newDiv );
		if( parameterValue[ i ].parameterValue != 0.0 ){
			stepSize = Math.pow( 10 , (Math.floor( Math.log10( parameterValue[ i ].parameterValue )) - 1));
		}
		
		parameter_jsondata.localParamValue.push({sbmlID : parameterValue[ i ].sbmlID , parameterValue : parameterValue[ i ].parameterValue , upper : parameterValue[ i ].parameterValue * 100 , lower : parameterValue[ i ].parameterValue * 0.01 , reactionID : parameterValue[ i ].reactionID , jsID : (parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID)});
		$("#" + parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID).slider( {
			min : 0 ,
			max : parameterValue[ i ].parameterValue * 200,
			step : stepSize,
			range : true,
			values : [ parameterValue[ i ].parameterValue * 0.01 , parameterValue[ i ].parameterValue * 100 ],
			change : function( e , ui ){
				$("#" + this.id + "_lower_input").val( ui.values[0]);
				$("#" + this.id + "_upper_input").val( ui.values[1]);
				var sbmlId = this.id;
				var filtered = $.grep( parameter_jsondata.localParamValue , function( elem , index){
					return( elem.jsID == sbmlId);
				})
				filtered[ 0 ].lower = ui.values[ 0 ];
				filtered[ 0 ].upper = ui.values[ 1 ];
				filtered[ 0 ].parameterValue =  (ui.values[ 1 ] + ui.values[ 0 ])  / 2 ;
			},
			create : function( e , ui){
				$("#" + this.id + "_lower_input").val($(this).slider("option","values")[0])
				$("#" + this.id + "_upper_input").val($(this).slider("option","values")[1])
			}
		});
		
		$("#" + parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID + "_lower_input").change( function(){
			if( !errorCheck()){
				$("#" + this.id.replace("_lower_input","")).slider("option","min",$(this).val() * 0.01);
				$("#" + this.id.replace("_lower_input","")).slider("option","values",[$(this).val(), $("#" + this.id.replace("_lower_input","") + "_upper_input").val()]);
			}
		});
		
		$("#" + parameterValue[ i ].reactionID + parameterValue[ i ].sbmlID + "_upper_input").change( function(){
			if( !errorCheck()){
				$("#" + this.id.replace("_upper_input","")).slider("option","max",$(this).val() * 100);
				$("#" + this.id.replace("_upper_input","")).slider("option","values",[$("#" + this.id.replace("_upper_input","") + "_lower_input").val() , $(this).val()])
			}
		});
		
		
	}
}
function addGlobalParamSlider( responseData ){
	var JSONResponse = responseData;
	var parameterValue = JSONResponse.modelParameters.paramValue;
	var globalParamSlider = document.getElementById("globalParam-slider");
	
	$("#globalParam-slider").empty();
	parameter_jsondata.paramValue = [];
	
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
		
		var newLowerInputText = document.createElement("input");
		newLowerInputText.setAttribute("id", parameterValue[ i ].sbmlID + "_lower_input");
		newLowerInputText.setAttribute("type","number")
		
		var newUpperInputText = document.createElement("input");
		newUpperInputText.setAttribute("id", parameterValue[ i ].sbmlID + "_upper_input");
		newUpperInputText.setAttribute("type","number")
		
		newLowerInputText.setAttribute("style" , "display:inline-block ;width : 20%; text-align:center")
		newParamSlider.setAttribute("style" , "display:inline-block ; width : 50% ;text-align:center")
		newUpperInputText.setAttribute("style" , "display:inline-block ;width : 20% ; text-align:center")
		
		newDiv.appendChild( newParam );
		newDiv.appendChild( newLowerInputText );
		newDiv.appendChild( newParamSlider );
		newDiv.appendChild( newUpperInputText );
		globalParamSlider.appendChild( newDiv );
		if( parameterValue[ i ].parameterValue != 0.0 ){
			stepSize = Math.pow( 10 , (Math.floor( Math.log10( parameterValue[ i ].parameterValue )) - 1));
		}
		
		parameter_jsondata.paramValue.push({sbmlID : parameterValue[ i ].sbmlID , parameterValue : parameterValue[ i ].parameterValue , upper : parameterValue[ i ].parameterValue * 100 , lower : parameterValue[ i ].parameterValue * 0.01 });
		$("#" + parameterValue[ i ].sbmlID).slider( {
			min : 0 ,
			max : parameterValue[ i ].parameterValue * 200,
			step : stepSize,
			range : true,
			values : [ parameterValue[ i ].parameterValue * 0.01 , parameterValue[ i ].parameterValue * 100 ],
			change : function( e , ui ){
				$("#" + this.id + "_lower_input").val( ui.values[0]);
				$("#" + this.id + "_upper_input").val( ui.values[1]);
				var sbmlId = this.id;
				var filtered = $.grep( parameter_jsondata.paramValue , function( elem , index){
					return( elem.sbmlID == sbmlId);
				})
				filtered[ 0 ].lower = ui.values[ 0 ];
				filtered[ 0 ].upper = ui.values[ 1 ];
				filtered[ 0 ].parameterValue =  (ui.values[ 1 ] + ui.values[ 0 ])  / 2 ;
			},
			create : function( e , ui){
				$("#" + this.id + "_lower_input").val($(this).slider("option","values")[0])
				$("#" + this.id + "_upper_input").val($(this).slider("option","values")[1])
			}
		});
		
		$("#" + parameterValue[ i ].sbmlID + "_lower_input").change( function(){
			if( !errorCheck()){
				$("#" + this.id.replace("_lower_input","")).slider("option","min",$(this).val() * 0.01);
				$("#" + this.id.replace("_lower_input","")).slider("option","values",[$(this).val(), $("#" + this.id.replace("_lower_input","") + "_upper_input").val()]);
			}
		});
		
		$("#" + parameterValue[ i ].sbmlID + "_upper_input").change( function(){
			if( !errorCheck()){
				$("#" + this.id.replace("_upper_input","")).slider("option","max",$(this).val() * 100);
				$("#" + this.id.replace("_upper_input","")).slider("option","values",[$("#" + this.id.replace("_upper_input","") + "_lower_input").val() , $(this).val()])
			}
		});
	}
}
function showBeforeFitting(){
	var checkBox = document.getElementById("before-fitting");
	if( checkBox.checked ){
		$("#before-graph").show();
		$("#after-graph").hide();
	}
	else{
		$("#before-graph").hide();
		$("#after-graph").show();
	}
}
function downloadData(){
	if(!$("#download").hasClass("disabled")){
		var zip = new JSZip();
		var model_name = $("#paraFile")[ 0 ].files[ 0 ].name.replace(".xml" , "");
		//Before Canvas
		var before_canvas = document.getElementById("beforeCanvas");
		var before_url = before_canvas.toDataURL();
		var before_savable = new Image();
		before_savable.src = before_url;
		zip.file(model_name + "_result_beforeFitting.png" , before_savable.src.substr( before_savable.src.indexOf(',')+1) , {base64 : true})
		
		// After Canvas
		var after_canvas = document.getElementById("afterCanvas");
		var after_url = after_canvas.toDataURL();
		var after_savable = new Image();
		after_savable.src = after_url;
		zip.file(model_name + "_result_afterFitting.png" , after_savable.src.substr(after_savable.src.indexOf(',')+1) , {base64 : true});
		
		//csv data from tabulator
		
		var csvContent = tabulatorToCsv("#num-table");
		var csv_blob = new Blob( [csvContent] , {type : "text/csv;charset=utf-8"})
		zip.file( model_name + "_result.csv" , csv_blob);
		
		//Updated Model
		$.ajax("./tmp/" + sessionId + "/Updated_" + $("#paraFile").prop('files')[0].name , {
			async : true,
			dataType:"xml"
		}).done( function( result){
			var xs = new XMLSerializer();
			result_text = xs.serializeToString( result );
			zip.file(model_name + "_updated.xml" , result_text);
			zip.generateAsync({type:"blob"}).then( function( content){
				saveAs( content , model_name + "_result.zip");
			});
		});
	}
}