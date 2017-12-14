package request.reader;

import org.apache.commons.fileupload.FileItem;

import general.task_type.Task_Type;
import parameter.BiomodelsSBMLModelExtraction_Parameter;

import java.util.Iterator;
import java.util.List;
public class Biomodels_SBMLExtraction_RequestReader {
	private BiomodelsSBMLModelExtraction_Parameter bmsbml_param;
	public Biomodels_SBMLExtraction_RequestReader( List<FileItem> fields , String path){
		this.bmsbml_param = new BiomodelsSBMLModelExtraction_Parameter();
		this.bmsbml_param.setPathToFile( path );
		this.bmsbml_param.setType( Task_Type.BIOMODELS_SBMLEXTRACTION );
		
		Iterator< FileItem> it = fields.iterator();
		while( it.hasNext() ){
			FileItem item = it.next();
			if( item.getFieldName().equals("bioModelsId")){
				this.bmsbml_param.setModelId( item.getString() );
			}
		}
	}
}
