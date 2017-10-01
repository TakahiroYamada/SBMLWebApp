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
			newOption.innerText = result.biomodels_name[ i ];
			newOption.textContent = result.biomodels_name[ i ];
			select.append( newOption);
			
			$("#div-biomodels").LoadingOverlay("hide");
		}
	}).fail( function(){
		$.ajax("./BioModels_ModelRefresh", {
			async : true,
			type : "post",
			processData : false ,
			contentType : false
		}).done( function( result ){
			var select = $("#select-biomodels");
			for( var i = 0 ; i < result.biomodels_id.length ; i ++){
				var newOption = document.createElement("option");
				newOption.setAttribute("value" , result.biomodels_id[ i ]);
				newOption.innerText = result.biomodels_name[ i ];
				newOption.textContent = result.biomodels_name[ i ];
				select.append( newOption);
				$("#div-biomodels").LoadingOverlay("hide");
			}
		})
	});

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
	var customElement   = $("<div>", {
	    text : "SBML Model Loading"
	});
	$.LoadingOverlay("show" , {
		custom :  customElement
	});
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
		var SBML_file;
		var form_file = document.getElementById("sbml-file");
		SBML_file = new Blob( [ModelSBML.SBML] , {type : "text/csv;charset=utf-8"});	
		SBML_file.name =  ModelSBML.SBMLId + ".xml";
		var filedata = new FormData();
		filedata.append("file" , SBML_file );
		getGraphViewFromServer( filedata );
		$.LoadingOverlay("hide");
	});
})