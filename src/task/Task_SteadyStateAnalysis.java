package task;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLException;
import analyze.steadystate.SteadyState_COPASI;
import beans.steadystate.SteadyState_AllBeans;
import exception.JSBML_ReadException;
import net.arnx.jsonic.JSON;
import parameter.SteadyStateAnalysis_Parameter;

public class Task_SteadyStateAnalysis extends Super_Task{
	private SteadyStateAnalysis_Parameter stedParam;
	private SteadyState_AllBeans stedAllBeans;
	public Task_SteadyStateAnalysis( String message ) throws IOException, SBMLException , IllegalArgumentException, XMLStreamException, JSBML_ReadException{
		this.stedParam = JSON.decode( message , SteadyStateAnalysis_Parameter.class);		
		super.saveFile( this.stedParam.getPathToFile() , this.stedParam.getFileName() , stedParam.getFileString());
		super.manipulateSBMLModel( this.stedParam.getSbmlParam() );
		
		SteadyState_COPASI analyzeSteadyState = new SteadyState_COPASI( this.stedParam , this.stedParam.getPathToFile() + "/result_steadystate.txt", newFile.getPath() );
		analyzeSteadyState.executeSteadyStateAnalysis();
		
		this.stedAllBeans = analyzeSteadyState.configureSteadyBeans();
		stedAllBeans.setSessionId( stedParam.getSessionInfo() );
		
		this.postProcess( stedAllBeans );
	}
	public SteadyState_AllBeans getSteadyStateAnalysisResult(){
		return this.stedAllBeans;
	}
}