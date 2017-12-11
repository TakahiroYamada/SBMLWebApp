package request.reader;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.fileupload.FileItem;

import net.arnx.jsonic.JSON;
import parameter.SteadyStateAnalysis_Parameter;

public class SteadyStateAnalysis_RequestReader {
	private SteadyStateAnalysis_Parameter stedParam;
	public SteadyStateAnalysis_RequestReader( List<FileItem> fields){
		this.stedParam = new SteadyStateAnalysis_Parameter();
		Iterator< FileItem > it = fields.iterator();
		// FormData from client side is checked.
		while( it.hasNext() ){
			FileItem item = it.next();
			// The SBML model is got and saved as tmp file in order to analyze based on importing this file
			if( item.getFieldName().equals("file")){
				this.stedParam.setFileString( item.getString());
			}

			else if( item.getFieldName().equals("library")){
				stedParam.setLibrary( item.getString() );
			}
			else if( item.getFieldName().equals("resolution")){
				stedParam.setResolution( Double.parseDouble( item.getString()));
			}
			else if( item.getFieldName().equals("derivation")){
				stedParam.setDerivation_factor( Double.parseDouble( item.getString() ));
			}
			else if( item.getFieldName().equals("itelimit")){
				stedParam.setIterationLimit( Integer.parseInt( item.getString()));
			}
		}
	}
	public String getstedParamAsJSON() {
		return JSON.encode( stedParam );
	}
}
