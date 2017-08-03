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