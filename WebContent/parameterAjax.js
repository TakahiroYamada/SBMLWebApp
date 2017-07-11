var req;
var currentTab = "graph";
var canvas_jsondata_Before = {
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

var canvas_jsondata_After = {
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


function showExpFile(){
	var exp_file = document.getElementById("expFile");
	var algorithm = document.getElementById("lvparam");
	exp_file.style.display = "block";
	algorithm.style.display ="block";
}

function analyzeData(){
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
	
	// Algorithm form is changed
	configureAlgorithmForm();

	// Parameter data is set to filedata(FormData)
	configureFormData( filedata );
	
	// Progress bar's configuration
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
	req.open("POST" , "./ParameterEstimation_Servlet" , true);
	req.onreadystatechange = callback;
	req.send( filedata );
}

function callback(){
	if( req.readyState == 4 ){
		if( req.status == 200 ){
			configureCanvas();
			configureTable();
		}
	}
}

function configureCanvas(){
	var canvas_before = document.getElementById("beforeCanvas");
	var canvas_after = document.getElementById("afterCanvas");
		
	
	var tmpData = JSON.parse( req.response || "null");
	
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
	var beforeChart = new Chart(canvas_before , canvas_jsondata_Before );
	var afterChart = new Chart(canvas_after , canvas_jsondata_After );
	var canvas = document.getElementById("canvas_item");
	canvas.style.display = "block";
}
function configureTable(){
	var jsonData = JSON.parse( req.response );
	var parameterTransitData = jsonData.updateParam;
	
	var column = [
		{ field : "Reaction" , sortable : false , title : "Reaction ID"},
		{ field : "Parameter" , sortable : false , title : "Parameter ID"},
		{ field : "Start" , sortable : false , title : "Start value"},
		{ field : "Update" , sortable : false , title : "Updated Value"}
		];
	
	document.getElementById("numTable").style.display = "block";
	$("#num-table").tabulator("setColumns" , column);
	$("#num-table").tabulator("clearData");
	
	var transitData = [];
	for( var i = 0 ; i < parameterTransitData.length ; i ++){
		var tmpData = {};
		paramData = parameterTransitData[ i ].parameterId;
		tmpData["Reaction"] = paramData.match(/\((.+)\)/)[1];
		tmpData["Parameter"] = paramData.substr( paramData.indexOf(".") + 1);
		tmpData["Start"] = parameterTransitData[ i ].startValue;
		tmpData["Update"] = parameterTransitData[ i ].updatedValue;
		transitData.push( tmpData );
	}
	$("#num-table").tabulator("setData" , transitData );
	changeTab( currentTab );
}
function configureFormData( formdata ){
	
	formdata.append("algorithm" , document.getElementById("algorithm").value);
	
	if( document.getElementById("lvparam").style.display == "block"){
		formdata.append("itermax" , document.getElementById("lvite").value);
		formdata.append("tolerance" , document.getElementById("lvtol").value);
	}
	else if( document.getElementById("gaparam").style.display == "block" ){
		formdata.append("generation" , document.getElementById("gagene").value);
		formdata.append("population" , document.getElementById("gapopu").value);
	}
	else if( document.getElementById("nelparam").style.display == "block"){
		formdata.append("itermax" , document.getElementById("nelite").value);
		formdata.append("tolerance" , document.getElementById("neltol").value);
	}
}
function configureAlgorithmForm(){
	var algorithm = document.getElementById("algorithm");
	if( algorithm.value == "lv"){
		document.getElementById("lvparam").style = "display:block";
		document.getElementById("gaparam").style = "display:none";
		document.getElementById("nelparam").style = "display:none";		
	}
	else if( algorithm.value == "ga"){
		document.getElementById("lvparam").style = "display:none";
		document.getElementById("gaparam").style = "display:block";
		document.getElementById("nelparam").style = "display:none";		
	}
	if( algorithm.value == "nelder"){
		document.getElementById("lvparam").style = "display:none";
		document.getElementById("gaparam").style = "display:none";
		document.getElementById("nelparam").style = "display:block";		
	}
}
function changeTab( tabname){
	document.getElementById("graph").style.display = "none";
	document.getElementById("numTable").style.display = "none";
	
	document.getElementById( tabname ).style.display = "block";
	currentTab = tabname;
}