$("#paraFile").on("change" , function(){
	var input = $(this).get(0).files[ 0 ];
	var reader = new FileReader();
	$(reader).on("error",function( e ){
		if( e.target.readyState == 2){
			errorSetting( e.target.error.message , "Change your file permission.")
			$("#warningModal").modal("show");
			$("#modalButton").off("click");
			$("#modalButton").on("click" , function(){
				$("#warningModal").modal("hide");
				var el = $("#paraFile");
				el.after( "<input id = 'paraFile' type = 'file' size = '50' accept = '.xml'>" );
				el.remove();
			});
			
		}
	})
	reader.readAsText( input );
})	
$("#paramButton").on("click" , function(){
	if( !errorCheck()){
		$(this).LoadingOverlay("show");
		analyzeData( $(this));
	}
})
$(".param-param").on("keypress" , function( e ){
	if( e.which == 13 ){
		if( !errorCheck()){
			$("#afterCanvas").LoadingOverlay("show");
			analyzeData( $("#afterCanvas"));
		}
	}
})
$("#algorithm").change( function(){
	configureAlgorithmForm();
})
$("#paraFile").on("change" , function(){
	showExpFile();
})
function errorCheck(){
	var info = checkNegativeValueinInput( $("input").each(function(index){
	}));
	// If file is not selected the error is visualized
	if( $("#paraFile").val().length == 0){
		errorSetting("SBML model is not selected" , "Selecting SBML file in input form")
		$("#warningModal").modal("show");
		$("#modalButton").off("click");
		$("#modalButton").on("click" , function(){
			$("#warningModal").modal("hide");
			var el = $("#paraFile");
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