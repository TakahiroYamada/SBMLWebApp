$.ajax("./BioModels_ModelExtraction" , {
		async : true ,
		type : "post" ,
		processData : false ,
		contentType : false,
	}).done( function( result ){
		var select = $("#select-biomodels");
		for( var i = 0 ; i < result.biomodels_id.length ; i ++){
			var newOption = document.createElement("option");
			newOption.setAttribute("value" , result.biomodels_id[ i ]);
			newOption.setAttribute("label", result.biomodels_name[ i ]);
			select.append( newOption);
			$("#div-biomodels").LoadingOverlay("hide");
		}
	});
