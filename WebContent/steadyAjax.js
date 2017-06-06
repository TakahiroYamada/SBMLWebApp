var req;
var link;
function getSteadyResult(){
	var form_file = document.getElementById("stedFile");
	var progressBar = document.getElementById("progress");
	link = document.createElement('a');
	var file = form_file.files[ 0 ];
	var filedata = new FormData();
	filedata.append("file" , file )
	
	var libSelect = document.getElementById("libselect");
	libSelect.style.display = "block";
	configureStedParameter( filedata );
	
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
	
	req.open("POST" , "/GSOC_WebMavenProject/SteadyState_Servlet" , true);
	req.onreadystatechange = callback;
	req.send( filedata )
	window.onload = callback;
}


function callback(){
	if( req.readyState == 4 ){
		if( req.status == 200 ){
			// If the result file is downloaded, following code is useful and uncomment it
			link.download = "result_steadystate.txt";
			link.href = "/GSOC_WebMavenProject/tmp/result_steadystate.txt";
			link.click()
			
		}
	}
}
function configureStedParameter( formdata ){
	var library = document.getElementById("library");
	if( library.value == "copasi"){
		document.getElementById("copasipara").style.display = "block";
		document.getElementById("libpara").style.display = "none";
		formdata.append("library" , library.value);
		formdata.append("resolution" , document.getElementById("resolution").value);
		formdata.append("derivation" , document.getElementById("derivation").value);
		formdata.append("itelimit" , document.getElementById("itelimit").value )
	}
	else if( library.value == "libroad"){
		document.getElementById("copasipara").style.display = "none";
		document.getElementById("libpara").style.display = "block";
		formdata.append("library",library.value)
	}
}