$("#stedButton").on("click" , function(){
	$(this).LoadingOverlay("show");
	getSteadyResult( $(this));
})
$(".sted-param").on("keypress" , function( e ){
	if( e.which == 13 ){
		$("#stedButton").LoadingOverlay("show");
		getSteadyResult( $("#stedButton"));
	}
})