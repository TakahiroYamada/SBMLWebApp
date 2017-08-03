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
	$.ajax("./SteadyState_Servlet" , {
		async : true,
		type : "post" ,
		data : filedata ,
		processData : false,
		contentType : false,
		xhr : function(){
			XHR = $.ajaxSettings.xhr();
			if( XHR.upload){
				XHR.upload.addEventListener("progress" , function( e ){
					per_progress = parseInt( e.loaded/e.total*10000)/100;
					$("#progress").val( per_progress);
				})
			}
			return XHR;
		}
	}).done( function( result ){
		responseData = result;
		callback( responseData );
	})
}


function callback( responseData ){
	// If the result file is downloaded, following code is useful and uncomment it
	//link.download = "result_steadystate.txt";
	//link.href = "/GSOC_WebMavenProject/tmp/result_steadystate.txt";
	//link.click()
	var jsonResponse = responseData;
	//Clear only the data in table not header
	document.getElementById("stedAmount").style.display = "block";
	var columnStedTable = $("#sted-table").tabulator("getColumnDefinitions");
	columnStedTable[ 2 ].title = "Concentration (" + jsonResponse.concentrationUnit + ")";
	columnStedTable[ 3 ].title = "Rate (" + jsonResponse.rateUnit + ")";
	columnStedTable[ 4 ].title =  "Transition Time (" + jsonResponse.transitiontimeUnit + ")";;;
	$("#sted-table").tabulator("setColumns" , columnStedTable);
	$("#sted-table").tabulator("clearData");
	
	$("#sted-table").tabulator("setData", jsonResponse.steadyAmount);
	document.getElementById("stedAmount").style.display = "";
	
	//var columnTxt = '{"fitColumns":true , "columns":' + JSON.stringify( jsonResponse.steadyJacobian.columns) + '}';
	//var columnJSON = JSON.parse( columnTxt );
		
	// Setting Column data is ignored when style.display is set as "none"
	document.getElementById("jacobian").style.display = "block";
	// Clear all data of jacobian
	$("#jacobian-table").tabulator( "setColumns" , jsonResponse.steadyJacobian.columns );
	$("#jacobian-table").tabulator("clearData");
	$("#jacobian-table").tabulator("setData" , jsonResponse.steadyJacobian.jacob_Amount);
	document.getElementById("jacobian").style.display = "";
	$("#download").removeClass("disabled")
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
function downloadData(){
	if( !$("#download").hasClass("disabled")){
		var zip = new JSZip();
		
		// csv data of steady state
		var sted_csvContent = tabulatorToCsv("#sted-table");
		var sted_blob = new Blob([sted_csvContent]  , {type : "text/csv;charset=utf-8"})
		zip.file("result_SteadyState.csv" , sted_blob);
		
		//csv data of jacobian
		var jacob_csvContent = tabulatorToCsv("#jacobian-table")
		var jacob_blob = new Blob([jacob_csvContent] , {type : "text/csv;charset=utf-8"});
		zip.file("result_JacobianMatrix.csv" , jacob_blob);
		zip.generateAsync({type:"blob"}).then( function( content){
			saveAs( content , "result.zip");
		});
	}
}
