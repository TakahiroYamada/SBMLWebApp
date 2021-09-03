var ModelSBML = {
		SBMLId : null , 
		SBML : null
};

var ExpData = {
		ExpId : null,
		Data : null
};

$("#paramButton").on("click" , function(){
	if( !errorCheck_Parameter()){
		$(this).LoadingOverlay("show");
		analyzeData( $(this));
	}
})
$(".param-param").on("keypress" , function( e ){
	if( e.which == 13 ){
		if( !errorCheck_Parameter()){
			$("#afterCanvas").LoadingOverlay("show");
			analyzeData( $("#afterCanvas"));
		}
	}
})
$("#algorithm").change( function(){
	configureAlgorithmForm();
})
function errorCheck_Parameter(){
	var info = checkNegativeValueinInput( $(".param-param"));
	// If file is not selected the error is visualized
	if( $("#sbml-file").val().length == 0 && (!$("#check-biomodels")[0].checked) && !(exampleFrag)){
		errorSetting("SBML model is not selected" , "Selecting SBML file in input form")
		$("#warningModal").modal("show");
		$("#modalButton").off("click");
		$("#modalButton").on("click" , function(){
			$("#warningModal").modal("hide");
			var el = $("#sbml-file");
			newOne = el.clone( true);
			el.before( newOne );
			el.remove();
			newOne.addClass("animated flash");
			newOne.focus();
		})
		return true;
	}
	else if( $("#expData").val().length == 0 && !(exampleFrag)){
		errorSetting("Experiment data is not selected" , "Selecting experiment data in input form")
		$("#warningModal").modal("show");
		$("#modalButton").off("click");
		$("#modalButton").on("click" , function(){
			$("#warningModal").modal("hide");
			var el = $("#expData");
			newOne = el.clone( true);
			el.before( newOne );
			el.remove();
			newOne.addClass("animated flash");
			newOne.focus();
		})
		return true;
	}
	else if( info.isNegative ){
		errorSetting("Some value is negative" , "Set the value larger than 0")
		$("#warningModal").modal("show");
		$("#modalButton").off("click");
		$("#modalButton").on("click" , function(){
			$("#warningModal").modal("hide");
			for( var i = 0 ; i < info.contents.length ; i ++){
				var el = info.contents[ i ];
				newOne = el.clone( true );
				el.before( newOne);
				el.remove();
				newOne.addClass("animated flash");
				newOne.focus();
			}
		});
		return true
	}
	return false;
}