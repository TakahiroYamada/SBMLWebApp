package general.task_manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLException;

import exception.COPASI_ExportException;
import exception.NoDynamicSpeciesException;
import general.task_type.Task_Type;
import net.arnx.jsonic.JSON;
import task.Task_ParameterEstimation;
import task.Task_Simulation;
import task.Task_SteadyStateAnalysis;

public class Task_Manager {
	private String message;
	private String responseData;
	private int type;
	public Task_Manager( String message) throws SBMLException, IOException,XMLStreamException, NoDynamicSpeciesException, COPASI_ExportException{
		this.message = message;
		Map map = ( Map ) JSON.decode( this.message );
		BigDecimal tmpType = (BigDecimal) map.get("type"); 
		this.type = tmpType.intValue();
		if( this.type == Task_Type.SIMULATION ){
			Task_Simulation simTask = new Task_Simulation( message );
			this.responseData = JSON.encode( simTask.getSimulationResult() );
		}
		else if( this.type == Task_Type.STEADY_STATE_ANALYSIS ){
			Task_SteadyStateAnalysis stedTask = new Task_SteadyStateAnalysis( message );
			this.responseData = JSON.encode( stedTask.getSteadyStateAnalysisResult() );
		}
		else if( this.type == Task_Type.PARAMETER_ESTIMATION ){
			Task_ParameterEstimation paramTask = new Task_ParameterEstimation( message );
			this.responseData = JSON.encode( paramTask.getParamestAllBeans() );
		}
	}
	public String getReponseData(){
		return this.responseData;
	}
}
