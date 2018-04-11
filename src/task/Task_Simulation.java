package task;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import org.sbml.jsbml.SBMLException;

import analyze.simulation.Simulation_COPASI;
import analyze.simulation.Simulation_SBSCL;
import analyze.simulation.Simulation_libSBMLsim;
import beans.simulation.Simulation_AllBeans;
import coloring.Coloring;
import exception.NoDynamicSpeciesException;
import net.arnx.jsonic.JSON;
import parameter.Simulation_Parameter;

public class Task_Simulation extends Super_Task{
	private double TRANSPARENCY = 1.0;
	private Simulation_Parameter simParam;
	private Simulation_AllBeans simAllBeans;
	public Task_Simulation( String message ) throws IOException, SBMLException,XMLStreamException, NoDynamicSpeciesException, IllegalArgumentException{
		this.simParam = JSON.decode( message , Simulation_Parameter.class );
		
		super.saveFile( this.simParam.getPathToFile() , this.simParam.getFileName() , this.simParam.getFileString() );
		super.manipulateSBMLModel( this.simParam.getSbmlParam() );
		
		if( this.simParam.getLibrary().equals("copasi")){
			this.executeSimulationWithCOPASI();
		}
		else if( this.simParam.getLibrary().equals("simulationcore")){
			this.executeSimulationWithSimulationCore();
		}
		else if( this.simParam.getLibrary().equals("libsbmlsim")){
			this.executeSimulationWithLibSBMLSim();
		}
		
		this.postProcess( this.simAllBeans );
		
	}
	public Simulation_AllBeans getSimulationResult( ){
		return this.simAllBeans;
	}
	private void executeSimulationWithCOPASI() throws NoDynamicSpeciesException{
		Simulation_COPASI simCOPASI = new Simulation_COPASI( newFile.getPath() , this.simParam );
		Coloring colorOfVis = new Coloring( ( int ) simCOPASI.getTimeSeries().getNumVariables() - 1 , this.TRANSPARENCY );
		
		simCOPASI.getTimeSeries().save( this.simParam.getPathToFile() + "/result.csv" , false , ",");
		this.simAllBeans = simCOPASI.configureSimulationBeans( colorOfVis );
		this.simAllBeans.setSessionId( this.simParam.getSessionInfo() );
		
	}
	private void executeSimulationWithSimulationCore() throws NoDynamicSpeciesException {
		Simulation_SBSCL simSBSCL = new Simulation_SBSCL( newFile.getPath() , this.simParam);
		Coloring colorOfVis = new Coloring( simSBSCL.getTimeSeries().getColumnCount() , this.TRANSPARENCY );
		this.simAllBeans = simSBSCL.configureSimulationBeans( colorOfVis);
		this.simAllBeans.setSessionId( this.simParam.getSessionInfo() );
	}
	private void executeSimulationWithLibSBMLSim() throws NoDynamicSpeciesException{
		Simulation_libSBMLsim simLibsbmlsim = new Simulation_libSBMLsim( newFile.getPath() , this.simParam );
		Coloring colorOfVis = new Coloring( simLibsbmlsim.getNumberOfVisualizedObject() , this.TRANSPARENCY );
		
		this.simAllBeans = simLibsbmlsim.configureSimulationBeans( colorOfVis );
		this.simAllBeans.setSessionId( this.simParam.getSessionInfo() );
	}
}
