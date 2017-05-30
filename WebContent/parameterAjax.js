var req;
function showExpFile(){
	var exp_file = document.getElementById("expFile");
	exp_file.style.display = "block"
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
	req.open("POST" , "/GSOC_WebMavenProject/ParameterEstimation_Servlet" , true);
	req.onreadystatechange = callback;
	req.send( filedata );
}
function callback(){
	if( req.readyState == 4 ){
		if( req.status == 200 ){
			
		}
	}
}