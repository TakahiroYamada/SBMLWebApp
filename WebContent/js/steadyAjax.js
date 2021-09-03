var link;
//var sessionId = "";
var currentFile = null;
function getSteadyResult( loadingObject ){
	var SBML_file;
	var form_file = document.getElementById("sbml-file");
	var progressBar = document.getElementById("progress");
	link = document.createElement('a');
	var filedata = new FormData();
	if( ! ($("#check-biomodels")[ 0 ].checked || exampleFrag) ){
		SBML_file = form_file.files[ 0 ];
		filedata.append("file" , SBML_file );
	}
	else{
		SBML_file = new Blob( [ModelSBML.SBML] , {type : "text/csv;charset=utf-8"});	
		filedata.append("file" , SBML_file , ModelSBML.SBMLId + ".xml");
	}

	filedata.append("SessionId" , sessionId);
	configureStedParameter( filedata );
	$.ajax("./Producer" , {
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
		sessionId = result.sessionId;
		responseData = result;
		callback_Steady( SBML_file.name , responseData );
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


function callback_Steady( fileName , responseData ){
	var form_file = document.getElementById("sbml-file");
	if( currentFile != fileName){
		//addWarningText( responseData );
		currentFile = fileName;
	}
	
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
	$("#download-steady").removeClass("disabled")
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
function configureStedParameter( formdata ){
	formdata.append("resolution" , document.getElementById("resolution").value);
	formdata.append("derivation" , document.getElementById("derivation").value);
	formdata.append("itelimit" , document.getElementById("itelimit").value )
	formdata.append("Type" , "steady");
}
function downloadData_Steady(){
	if( !$("#download-steady").hasClass("disabled")){
		if( !($("#check-biomodels")[0].checked || exampleFrag)){
			var model_name = $("#sbml-file")[0].files[0].name.replace(".xml" , "");
		}
		else{
			var model_name = ModelSBML.SBMLId
		}
		var zip = new JSZip();
		
		// csv data of steady state
		var sted_csvContent = tabulatorToCsv("#sted-table");
		var sted_blob = new Blob([sted_csvContent]  , {type : "text/csv;charset=utf-8"})
		zip.file(model_name + "_result_SteadyState.csv" , sted_blob);
		
		//csv data of jacobian
		var jacob_csvContent = tabulatorToCsv("#jacobian-table")
		var jacob_blob = new Blob([jacob_csvContent] , {type : "text/csv;charset=utf-8"});
		zip.file(model_name + "_result_JacobianMatrix.csv" , jacob_blob);
		zip.generateAsync({type:"blob"}).then( function( content){
			saveAs( content , model_name + "_result.zip");
		});
	}
}
