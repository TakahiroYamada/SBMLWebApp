$("#nav-graphview").on("click" , function(){
	$("#modelgraph-Modal").modal("show");
})
$("#graphmodalButton").on("click" , function(){
	$("#modelgraph-Modal").modal("hide");
});

$("#sbml-file").on("change" , function(){
	var input = $("#sbml-file").get(0).files[ 0 ];
	var reader = new FileReader();
	$(reader).on("error",function( e ){
		if( e.target.readyState == 2){
			errorSetting( e.target.error.message , "Change your file permission.")
			$("#warningModal").modal("show");
			$("#modalButton").off("click");
			$("#modalButton").on("click" , function(){
				$("#warningModal").modal("hide");
				var el = $("#sbml-file");
				el.after( "<input id = 'sbml-file' type = 'file' size = '50' accept = '.xml'>" );
				el.remove();
			});
		}
	})
	$(reader).on("load" , function(){
		showExpFile();
		var SBML_file;
		var form_file = document.getElementById("sbml-file");
		if( ! $("#check-biomodels")[ 0 ].checked ){
			SBML_file = form_file.files[ 0 ];
		}
		else{
			SBML_file = new Blob( [ModelSBML.SBML] , {type : "text/csv;charset=utf-8"});	
			SBML_file.name =  ModelSBML.SBMLId + ".xml";
		}
		var filedata = new FormData();
		filedata.append("file" , SBML_file );
		getGraphViewFromServer( filedata );
	})
	reader.readAsText( input );
})	

function getGraphViewFromServer( form_file ){
	form_file.append("SessionId" , sessionId);
	 $.ajax({
	      url: "./SBMLModelView",
	      type: "post",
	      data: form_file,
	      processData: false,
	      contentType: false,
	      timeout: 10000
	    }).done(function (json_str) {
	      console.log("done");	      
			var data = json_str.all;
			sessionId = json_str.sessionId;
			$("#modelgraph-Modal").modal("show");
			var cy = cytoscape({
				container: document.getElementById("graph-cy"),
				boxSelectionEnabled: false,
				autounselectify: true,
				elements: data,
				layout: {
					name: 'cose',
					directed: true,
					padding: 10,
					animate: true
				},
				style: [{
						 selector: 'node',
						 style: {
							 	'shape': 'roundrectangle',
								'background-color': '#d5fcd7',
								'border-color': 'black',
								'border-width': 1,
								'width': 80,
								'height': 40,
								'text-valign': 'center',
								'font-size': 12,
								'label': 'data(name)'
						 }
						},
						{
							selector: 'node.rxn',
							style: {
								'shape': 'rectangle',
								'background-color': 'white',
								'width': 10,
								'height': 10,
								'label': ''
							}
						},

						{
							selector: 'edge',
							style: {
								'curve-style': 'bezier',
								'width': 1,
								'target-arrow-shape': 'triangle',
								'arrow-scale': 1.3,
								'line-color': 'black',
								'target-arrow-color': 'black'
							}
						},

						{
							selector: 'edge.activation',
							style: {
								'target-arrow-shape': 'circle',
								'arrow-scale': 0.8,
								'target-arrow-fill': 'hollow',
								'line-style': 'dotted'
							}
						},

						{
							selector: 'edge.inhibition',
							style: {
								'target-arrow-shape': 'tee',
								'arrow-scale': 0.8,
								'line-style': 'dashed'
							}
						},

						{
							selector: 'edge.reactant',
							style: {
								'target-arrow-shape': 'none',
							}
						}
					],
				});
			}).fail(function () {
				console.log('fail');
			});
}