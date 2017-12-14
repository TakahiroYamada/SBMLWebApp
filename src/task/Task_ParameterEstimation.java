package task;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import analyze.parameter.ParameterEstimation_COPASI;
import analyze.simulation.Simulation_COPASI;
import beans.parameter.ParameterEstimation_AllBeans;
import coloring.Coloring;
import exception.COPASI_ExportException;
import exception.NoDynamicSpeciesException;
import net.arnx.jsonic.JSON;
import parameter.ParameterEstimation_Parameter;
import parameter.Simulation_Parameter;

public class Task_ParameterEstimation extends Super_Task{
	private double TRANSPARENCY = 1.0;
	private ParameterEstimation_Parameter paramestParam;
	private ParameterEstimation_AllBeans paramestAllBeans;
	private Coloring colorOfVis;
	public Task_ParameterEstimation( String message) throws IOException , XMLStreamException, NoDynamicSpeciesException, COPASI_ExportException , IllegalArgumentException{
		this.paramestParam = JSON.decode( message , ParameterEstimation_Parameter.class);
		
		super.saveFile( paramestParam.getPathToFile() , paramestParam.getFileName() , paramestParam.getFileString());
		super.saveExperimentData( paramestParam.getPathToFile() , paramestParam.getAnsFileName() , paramestParam.getAnsData());
		
		super.manipulateSBMLModel( this.paramestParam.getSbmlParam() );
		
		ParameterEstimation_COPASI paramestCOPASI = new ParameterEstimation_COPASI( this.paramestParam , this.newFile , this.experimentDataFile , this.paramestParam.getSbmlParam() );
		paramestCOPASI.estimateParameter();
		
		simulateFittedResult( paramestCOPASI );
		
		paramestAllBeans.setExpDataSets( paramestCOPASI.configureParamEstBeans( colorOfVis));
		paramestAllBeans.setUpdateParam( paramestCOPASI.configureParameterUpdateInformationBeans());
		paramestAllBeans.setSessionId( paramestParam.getSessionInfo());
		
		super.getManipulator().addAmountConcentration( this.paramestAllBeans.getBeforeFitting() );
		super.getManipulator().addAmountConcentration( this.paramestAllBeans.getAfterFitting() );
		
		paramestAllBeans.setModelParameters( super.getManipulator().getModelParameter() );
	}
	private void simulateFittedResult(ParameterEstimation_COPASI paramestCOPASI) throws NoDynamicSpeciesException, COPASI_ExportException {
		Simulation_Parameter paramSim = new Simulation_Parameter();
		this.paramestAllBeans = new ParameterEstimation_AllBeans();
		// Simulation condition is set
		int endTime = (int) Math.ceil(paramestCOPASI.getTimeData().get(paramestCOPASI.getTimeData().size() - 1));
		paramSim.setLibrary("copasi");
		paramSim.setNumTime(endTime * (int) paramestCOPASI.getTimeData().size());
		paramSim.setEndTime(new Double( endTime));

		this.colorOfVis = new Coloring((int) paramestCOPASI.getDependentData().size(), this.TRANSPARENCY);
		// Simulation execution using parameters before and after fitting
		Simulation_COPASI beforeFitting = new Simulation_COPASI(newFile.getPath(), paramSim);
		this.paramestAllBeans.setBeforeFitting(beforeFitting.configureSimulationBeans(colorOfVis));
		Simulation_COPASI afterFitting = new Simulation_COPASI(paramestCOPASI.getDataModel(), paramSim);
		this.paramestAllBeans.setAfterFitting(afterFitting.configureSimulationBeans(colorOfVis));
		try {
			paramestCOPASI.getDataModel().exportSBML( paramestParam.getPathToFile() + "/Updated_" + paramestParam.getFileName() );
		} catch (Exception e) {
			COPASI_ExportException ce = new COPASI_ExportException();
			throw ce;
		}
	}
	public ParameterEstimation_AllBeans getParamestAllBeans() {
		return paramestAllBeans;
	}
}
