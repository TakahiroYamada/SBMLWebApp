function tabulatorToCsv( table ){
	var data = $(table).tabulator("getData");
	var tmpColumn = $(table).tabulator("getColumns");
	var tmpCSVColumn = [];
	for( var i = 0 ; i < tmpColumn.length ; i ++){
		tmpCSVColumn.push( tmpColumn[ i ].getDefinition().title);
	}
	var csvContent = [ tmpCSVColumn.join(",")]
	
	// sort the data to the order of column in visualuzed table
	var order = [];
	var columnField = Object.keys( data[ 0 ]);
	for( var i = 0 ; i < tmpColumn.length ; i ++){
		for( var j = 0 ; j < columnField.length ; j ++){
			if( tmpColumn[ i ].getDefinition().field == columnField[ j ]){
				order.push( j );
			}
		}
	}
	
	//generate each row of the table
	data.forEach(function(row){
		var rowString = [];
		for( var i = 0 ; i < order.length ; i ++){
			rowString.push( Object.values( row )[ order[ i ] ])
		}
		csvContent.push([rowString.join(",")]);
	});
	return csvContent.join("\n");
}

function checkNegativeValueinInput( inputs){
	var info = {
			isNegative : false,
			contents : []
	}
	for( var i = 0 ; i < inputs.length ; i ++){
		var tmp = $(inputs[ i ]);
		if( tmp.attr("type") == "number" && tmp.val() < 0 ){
			info.isNegative = true;
			info.contents.push( tmp );
		}
	}
	return info;
}
//Warning setting
function warningSetting( warningText  , solveText){
	//Warning text is cleaned firstly
	$(".modal-body").empty();
	$("#modal-content").removeClass();
	$("#modal-content").addClass("modal-content alert alert-warning")
	
	$("#warningModalLabel").text("Warning!")
	
	var newWarningContents = $("<div>");
	
	var newWarningDetail = $("<h5>");
	newWarningDetail.text("Warning Detail : ")
	var newWarning = $("<p>");
	newWarning.append( document.createTextNode( warningText));
	
	var solveWarning = $("<h5>");
	solveWarning.text("Solve this warning : ");
	var solveWarningText = $("<p>");
	solveWarningText.append( document.createTextNode( solveText ));
	
	
	newWarningContents.append( newWarningDetail );
	newWarningContents.append( newWarning );
	newWarningContents.append( solveWarning );
	newWarningContents.append( solveWarningText );
	$(".modal-body").append( newWarningContents);
}
// Error Setting
function errorSetting( errorText , solveText){
	//Error text is cleaned firstly
	$(".modal-body").empty();
	$("#modal-content").removeClass();
	$("#modal-content").addClass("modal-content alert alert-danger")
	
	$("#warningModalLabel").text("Error!")
	
	var newErrorContents = $("<div>");
	
	var newErrorDetail = $("<h5>");
	newErrorDetail.text("Error Detail : ")
	var newError = $("<p>");
	newError.append( document.createTextNode( errorText));
	
	var solveError = $("<h5>");
	solveError.text("Solve this error : ");
	var solveErrorText = $("<p>");
	solveErrorText.append( document.createTextNode( solveText ));
	
	newErrorContents.append( newErrorDetail );
	newErrorContents.append( newError );
	newErrorContents.append( solveError );
	newErrorContents.append( solveErrorText );
	$(".modal-body").append( newErrorContents);
}
