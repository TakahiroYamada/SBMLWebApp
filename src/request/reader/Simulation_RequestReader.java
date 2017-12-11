package request.reader;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import beans.modelparameter.ModelParameter_Beans;
import net.arnx.jsonic.JSON;
import parameter.Simulation_Parameter;

public class Simulation_RequestReader {
	private Simulation_Parameter simParam;
	public Simulation_RequestReader( List<FileItem> fields){
		this.simParam = new Simulation_Parameter();
		Iterator< FileItem > it = fields.iterator();
		while( it.hasNext()){
			FileItem item = it.next();
			// SBML model file is got.
			if(item.getFieldName().equals("file")){
				this.simParam.setFileString( item.getString() );
			}
			// this.simParameter to analyze is set
			else if( item.getFieldName().equals("endpoint")){
				this.simParam.setEndTime( new Double( Integer.parseInt( item.getString() )));
			}
			else if( item.getFieldName().equals("numpoint")){
				this.simParam.setNumTime( new Integer( Integer.parseInt( item.getString() )));;
			}
			else if( item.getFieldName().equals( "tolerance")){
				this.simParam.setTolerance( new Double( Double.parseDouble( item.getString() )));
			}
			else if( item.getFieldName().equals("library")){
				this.simParam.setLibrary( item.getString() );
			}
			else if( item.getFieldName().equals("parameter")){
				this.simParam.setSbmlParam( JSON.decode( item.getString() , ModelParameter_Beans.class));
			}
		}
	}
	public String getSimParamAsJSON() {
		return JSON.encode( simParam );
	}
}
