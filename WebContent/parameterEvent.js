$("#paramButton").on("click" , function(){
	$(this).LoadingOverlay("show");
	analyzeData( $(this));
})
$(".param-param").on("keypress" , function( e ){
	if( e.which == 13 ){
		$("#afterCanvas").LoadingOverlay("show");
		analyzeData( $("#afterCanvas"));
	}
})
$("#algorithm").change( function(){
	configureAlgorithmForm();
})