$("#runbottun").on("click" , function(){
	$(this).LoadingOverlay("show");
	getSimulationResult( $(this) );
})

$(".sim-param").on("keypress" , function( e ){
	if( e.which == 13 ){
		$("#runbottun").LoadingOverlay("show");
		getSimulationResult($("#runbottun"));
	}
})