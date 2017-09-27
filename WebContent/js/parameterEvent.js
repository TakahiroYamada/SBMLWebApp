var ModelSBML = {
		SBMLId : null , 
		SBML : null
};

$("#check-biomodels").on("change" , function(){
	if( $(this)[ 0 ].checked ){
		$("#div-localfile").hide();
		$("#div-biomodels").show();
		if( $("#select-biomodels").children().length == 1 ){
			$("#div-biomodels").LoadingOverlay("show");
		}
	}
	else{
		$("#div-localfile").show();
		$("#div-biomodels").hide();
		if( $("#select-biomodels").children().length == 1 ){
			$("#div-biomodels").LoadingOverlay("hide");
		}
	}
})
$("#select-biomodels").on("change", function(){
	// Visualizing the setting form of experimental data
	var exp_file = document.getElementById("expFile");
	var algorithm = document.getElementById("lvparam");
	exp_file.style.display = "block";
	algorithm.style.display ="block";
	
	// Model data load
	var selectedModel = $("#select-biomodels option:selected");
	var modelId = $(this).val();
	var modelName = selectedModel.attr("label");
	
	$.ajax("./BioModels_ModelSBMLExtraction" , {
		async : true,
		type : "post",
		data : { bioModelsId : modelId } , 
	}).done( function( result ){
		ModelSBML.SBMLId = modelId;
		ModelSBML.SBML = result;
	});
})
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
$("#sbml-file").on("change" , function(){
	showExpFile();
})
function errorCheck_Parameter(){
	var info = checkNegativeValueinInput( $(".param-param"));
	// If file is not selected the error is visualized
	if( $("#sbml-file").val().length == 0 && (!$("#check-biomodels")[0].checked) ){
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
	else if( $("#expData").val().length == 0 ){
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