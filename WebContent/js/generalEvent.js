$(".start-analysis").on("click", function(){
	$("#name-app").hide(1000);
	$("#explain-doc").hide(1000);
	$("#tool-box").hide(1000);
	
	var analysis = $(this).attr("title");
	if( analysis == "Simulation"){
		$("#simulation-widget").tab("show");
		$("#simulation-tab").addClass("active");
	}
	else if( analysis == "Steady"){
		$("#steady-widget").tab("show");
		$("#steady-tab").addClass("active");
	}
	else if( analysis == "ParameterEstimation"){
		$("#parameter-widget").tab("show")
		$("#parameter-tab").addClass("active");
	}
	
	$("#detail_analysis").show(1000);
})