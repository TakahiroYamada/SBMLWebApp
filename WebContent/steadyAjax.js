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
	
	req.open("POST" , "./SteadyState_Servlet" , true);
	req.onreadystatechange = callback;
	req.send( filedata )
	window.onload = callback;
}


function callback(){
	if( req.readyState == 4 ){
		if( req.status == 200 ){
			// If the result file is downloaded, following code is useful and uncomment it
			//link.download = "result_steadystate.txt";
			//link.href = "/GSOC_WebMavenProject/tmp/result_steadystate.txt";
			//link.click()
			var jsonResponse = JSON.parse( req.response)
			//Clear only the data in table not header
			$("#sted-table").tabulator("clearData")
			$("#sted-table").tabulator("setData", jsonResponse.steadyAmount)
			//var columnTxt = '{"fitColumns":true , "columns":' + JSON.stringify( jsonResponse.steadyJacobian.columns) + '}';
			//var columnJSON = JSON.parse( columnTxt );
			
			// Setting Column data is ignored when style.display is set as "none"
			document.getElementById("jacobian").style.display = "block";
			// Clear all data of jacobian
			$("#jacobian-table").tabulator( "setColumns" , jsonResponse.steadyJacobian.columns );
			$("#jacobian-table").tabulator("clearData");
			$("#jacobian-table").tabulator("setData" , jsonResponse.steadyJacobian.jacob_Amount);
			document.getElementById("jacobian").style.display = "";
		}
	}
}
function configureStedParameter( formdata ){
	var library = document.getElementById("library");
	if( library.value == "copasi"){
		document.getElementById("copasipara").style.display = "block";
		formdata.append("library" , library.value);
		formdata.append("resolution" , document.getElementById("resolution").value);
		formdata.append("derivation" , document.getElementById("derivation").value);
		formdata.append("itelimit" , document.getElementById("itelimit").value )
	}
}
