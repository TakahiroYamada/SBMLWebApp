package task;

import java.io.IOException;

import org.sbml.jsbml.SBMLException;
import analyze.steadystate.SteadyState_COPASI;
import beans.steadystate.SteadyState_AllBeans;
import net.arnx.jsonic.JSON;
import parameter.SteadyStateAnalysis_Parameter;

public class Task_SteadyStateAnalysis extends Super_Task{
	private SteadyStateAnalysis_Parameter stedParam;
	private SteadyState_AllBeans stedAllBeans;
	public Task_SteadyStateAnalysis( String message ) throws IOException, SBMLException , IllegalArgumentException{
		this.stedParam = JSON.decode( message , SteadyStateAnalysis_Parameter.class);		
		super.saveFile( this.stedParam.getPathToFile() , this.stedParam.getFileName() , stedParam.getFileString());
		
		SteadyState_COPASI analyzeSteadyState = new SteadyState_COPASI( this.stedParam , this.stedParam.getPathToFile() + "/result_steadystate.txt", newFile.getPath() );
		analyzeSteadyState.executeSteadyStateAnalysis();
		
		this.stedAllBeans = analyzeSteadyState.configureSteadyBeans();
		stedAllBeans.setSessionId( stedParam.getSessionInfo() );
	}
	public SteadyState_AllBeans getSteadyStateAnalysisResult(){
		return this.stedAllBeans;
	}
}