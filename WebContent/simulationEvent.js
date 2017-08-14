$("#runbottun").on("click" , function(){
	// If file is not selected the error is visualized
	if( $("#simFile").val().length == 0){
		errorSetting("SBML model is not selected" , "Selecting SBML file in input form")
		$("#warningModal").modal("show");
		$("#modalButton").off("click");
		$("#modalButton").on("click" , function(){
			$("#warningModal").modal("hide");
			var el = $("#simFile");
			newOne = el.clone( true);
			el.before( newOne );
			el.remove();
			newOne.addClass("animated flash");
		})
	}
	else{
		$(this).LoadingOverlay("show");
		getSimulationResult( $(this) );
	}
})

$(".sim-param").on("keypress" , function( e ){
	if( e.which == 13 ){
		$("#runbottun").LoadingOverlay("show");
		getSimulationResult($("#runbottun"));
	}
})

// Warning setting
function warningSetting( warningText  , solveText){
	//Warning text is cleaned firstly
	$(".modal-body").empty();
	$("#modal-content").removeClass();
	$("#modal-content").addClass("modal-content alert alert-warning")
	
	$("#warningModalLabel").text("Warning!")
	
	var newWarningContents = $("<div>");
	
	var newWarningDetail = $("<h5>");
	newWarningDetail.text("Warning Detail : ")
	var newWarning = $("<p>");
	newWarning.append( document.createTextNode( warningText));
	
	var solveWarning = $("<h5>");
	solveWarning.text("Solve this warning : ");
	var solveWarningText = $("<p>");
	solveWarningText.append( document.createTextNode( solveText ));
	
	
	newWarningContents.append( newWarningDetail );
	newWarningContents.append( newWarning );
	newWarningContents.append( solveWarning );
	newWarningContents.append( solveWarningText );
	$(".modal-body").append( newWarningContents);
}
// Error Setting
function errorSetting( errorText , solveText){
	//Error text is cleaned firstly
	$(".modal-body").empty();
	$("#modal-content").removeClass();
	$("#modal-content").addClass("modal-content alert alert-danger")
	
	$("#warningModalLabel").text("Error!")
	
	var newErrorContents = $("<div>");
	
	var newErrorDetail = $("<h5>");
	newErrorDetail.text("Error Detail : ")
	var newError = $("<p>");
	newError.append( document.createTextNode( errorText));
	
	var solveError = $("<h5>");
	solveError.text("Solve this error : ");
	var solveErrorText = $("<p>");
	solveErrorText.append( document.createTextNode( solveText ));
	
	newErrorContents.append( newErrorDetail );
	newErrorContents.append( newError );
	newErrorContents.append( solveError );
	newErrorContents.append( solveErrorText );
	$(".modal-body").append( newErrorContents);
}