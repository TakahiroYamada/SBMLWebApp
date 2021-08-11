/**
* @author Akira Funahashi <funa@bio.keio.ac.jp>
* @author Takahiro G. Yamada <yamada@fun.bio.keio.ac.jp>
*/

package task;

import beans.biomodels_sbmlextraction.BioModels_SBMLInfo_Beans;
import database.BioModelsConnectionService;
import database.biomodels.ModelFileResponse;
import net.arnx.jsonic.JSON;
import parameter.BiomodelsSBMLModelExtraction_Parameter;
import uk.ac.ebi.biomodels.ws.BioModelsWSClient;
import uk.ac.ebi.biomodels.ws.BioModelsWSException;

public class Task_BiomodelsSBMLExtraction extends Super_Task{
	private BiomodelsSBMLModelExtraction_Parameter bmsbmlParam;
	private BioModels_SBMLInfo_Beans bmsbmlAllBeans;
	public Task_BiomodelsSBMLExtraction( String message ) throws BioModelsWSException{
		this.bmsbmlParam = JSON.decode( message , BiomodelsSBMLModelExtraction_Parameter.class );
		this.bmsbmlAllBeans = new BioModels_SBMLInfo_Beans();
		
		try (BioModelsConnectionService bioModelsService = new BioModelsConnectionService()){
			ModelFileResponse mfr = bioModelsService.getModelFile( this.bmsbmlParam.getModelId());
			StringBuffer sbuf = new StringBuffer();
			sbuf.append( mfr.getFileContent());
			
			bmsbmlAllBeans.setModelString( sbuf.toString() );
			sbuf = null;
			
		} catch( Exception e){
			e.printStackTrace();
		}		
		bmsbmlAllBeans.setSessionId( bmsbmlParam.getSessionInfo() );
	}
	public BioModels_SBMLInfo_Beans getBmsbmlAllBeans() {
		return bmsbmlAllBeans;
	}
}
