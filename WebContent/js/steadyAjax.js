var link;
sted_parameter_jsondata = {
		initValue : [],
		compartmentValue : [],
		localParamValue : [],
		paramValue : []
}
//var sessionId = "";
var currentFile_Steady = null;
function getSteadyResult( loadingObject ){
	var SBML_file;
	var form_file = document.getElementById("sbml-file");
	var progressBar = document.getElementById("sted-progress");
	link = document.createElement('a');
	var filedata = new FormData();
	if( ! ($("#check-biomodels")[ 0 ].checked || exampleFrag) ){
		SBML_file = form_file.files[ 0 ];
		filedata.append("file" , SBML_file );
	}
	else{
		SBML_file = new Blob( [ModelSBML.SBML] , {type : "text/csv;charset=utf-8"});	
		SBML_file.name = ModelSBML.SBMLId + ".xml";
		filedata.append("file" , SBML_file , ModelSBML.SBMLId + ".xml");
	}
	
	if( currentFile_Steady != SBML_file.name ){
		sted_parameter_jsondata ={
				initValue : [],
				compartmentValue : [],
				localParamValue : [],
				paramValue : []
		};
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
					$("#sted-progress").val( per_progress);
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
			$(".modal-body").empty();
			$("#modal-content").removeClass();
		});
	});
}


function callback_Steady( fileName , responseData ){
	var form_file = document.getElementById("sbml-file");
	if( currentFile_Steady != fileName){
		addInitialValueSlider( responseData , "sted-");
		addCompartmentSlider( responseData , "sted-");
		addLocalParameterValueSlider( responseData , "sted-");
		addGlobalParameterValueSlider( responseData , "sted-");
		currentFile_Steady = fileName;
	}
	
	var jsonResponse = responseData;
	//Clear only the data in table not header
	document.getElementById("stedAmount").style.display = "block";
	$("#sted-contents").show();
	$("#sted-tabs").show();
	
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
	
	// Set the result of stability information for this equiribrium point
	stabilityInfo = responseData.steadyStability
	
	$("#stability-content").empty();
	$("#stability-content").append("<h3> KINETIC STABILITY ANALYSIS </h3>");
	$("#stability-content").append("<p> The linear stability analysis based on the eigenvalues of the Jacobian matrix is only valid for steady states.</p>")
	
	$("#stability-content").append("<h4> Summary: </h4>");
	
	if( stabilityInfo.mMaxrealpart > stabilityInfo.mResolution){
		$("#stability-content").append("<p> This state is <span style='color: red; '>unstable. </span></p>");
	}
	else if( stabilityInfo.mMaxrealpart < - stabilityInfo.mResolution){
		$("#stability-content").append("<p> This state is <span style='color: blue; '>asymptotically stable.</span> </p>");
	}
	else{
		$("#stability-content").append("<p> This state's stability is <span style='color: green; '>undetermined.</span></p>");
	}
	
	if( stabilityInfo.mMaximagpart > stabilityInfo.mResolution ){
		$("#stability-content").append("<p> Transient states in its vicinity have <span style='color: navy; '>oscillatory components. </span></p>");
	}

	$("#stability-content").append("<br/>");
	
	$("#stability-content").append("<h4> Eigenvalue statistics: </h4>");
	$("#stability-content").append("<p> Largest real part:  " + stabilityInfo.mMaxrealpart + "</p>");
	
	$("#stability-content").append("<p> Largest absolute imaginary part:  " + stabilityInfo.mMaximagpart + "</p>");
	
	if( stabilityInfo.mImagOfMaxComplex > stabilityInfo.mResolution ){
		$("#stability-content").append("<p> The complex eigenvalues with the largest real part are:  " + stabilityInfo.mMaxRealOfComplex + "+|-" + stabilityInfo.mImagOfMaxComplex + " i</p>");
	}
	
	$("#stability-content").append( "<p>" + stabilityInfo.mNreal + " are purely real</p>");
	
	$("#stability-content").append( "<p>" + stabilityInfo.mNimag + " are purely imaginary</p>" );
	
	$("#stability-content").append( "<p>" + stabilityInfo.mNcplxconj + " are complex</p>");
	
	$("#stability-content").append( "<p>" + stabilityInfo.mNzero + " are equal to zero </p>" );
	
	$("#stability-content").append( "<p>" + stabilityInfo.mNposreal + " have positive real part </p>" );
	
	$("#stability-content").append( "<p>" + stabilityInfo.mNnegreal + " have negative real part </p>" );
	
	$("#stability-content").append("<p> stiffness = " + stabilityInfo.mStiffness + "</p>");
	
	$("#stability-content").append("<p> time hierarchy = " + stabilityInfo.mHierarchy + "</p>");
	document.getElementById("stability").style.display = "";
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
	formdata.append("parameter" , JSON.stringify( sted_parameter_jsondata));
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
		
		var stability_blob = new Blob( [document.getElementById("stability-content").innerHTML] , {type : "text/html;charset=utf-8"})
		zip.file(model_name + "_result_Stability.html", stability_blob );
		zip.generateAsync({type:"blob"}).then( function( content){
			saveAs( content , model_name + "_result.zip");
		});
	}
}
