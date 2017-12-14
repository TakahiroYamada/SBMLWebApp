package task;


import beans.biomodels_sbmlextraction.BioModels_SBMLInfo_Beans;
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
		
		BioModelsWSClient client = new BioModelsWSClient();
		bmsbmlAllBeans.setModelString( client.getModelSBMLById( this.bmsbmlParam.getModelId() ));
		bmsbmlAllBeans.setSessionId( bmsbmlParam.getSessionInfo() );
	}
	public BioModels_SBMLInfo_Beans getBmsbmlAllBeans() {
		return bmsbmlAllBeans;
	}
}
