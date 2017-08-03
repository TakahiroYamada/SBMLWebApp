function tabulatorToCsv( table ){
	var data = $(table).tabulator("getData");
	var csvContent = [Object.keys(data[0]).join(",")];
	//generate each row of the table
	data.forEach(function(row){
		var rowString = Object.values(row).join(",");
		csvContent.push(rowString);
	});
	return csvContent.join("\n");
}