var req;
var link;
function getSteadyResult(){
	var form_file = document.getElementById("stedFile");
	var progressBar = document.getElementById("progress");
	link = document.createElement('a');
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
	
	req.open("POST" , "/GSOC_WebMavenProject/SteadyState_Servlet" , true);
	req.onreadystatechange = callback;
	req.send( filedata )
	window.onload = callback;
}


function callback(){
	if( req.readyState == 4 ){
		if( req.status == 200 ){
			link.download = "result_steadystate.txt";
			link.href = "/GSOC_WebMavenProject/tmp/result_steadystate.txt";
			link.click()
		}
	}
}
