package analyze.simulation;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.COPASI.CCopasiDataModel;
import org.COPASI.CCopasiRootContainer;
import org.COPASI.CModelEntity;
import org.apache.commons.math.ode.DerivativeException;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.validator.ModelOverdeterminedException;
import org.simulator.math.odes.MultiTable;
import org.simulator.math.odes.RosenbrockSolver;
import org.simulator.sbml.SBMLinterpreter;

import beans.simulation.Simulation_AllBeans;
import beans.simulation.Simulation_DatasetsBeans;
import beans.simulation.Simulation_XYDataBeans;
import coloring.Coloring;
import exception.NoDynamicSpeciesException;
import parameter.Simulation_Parameter;

public class Simulation_SBSCL {
	private Simulation_Parameter simParam;
	private Model model;
	private MultiTable solution;
	private String sbmlFile;
	public Simulation_SBSCL( String sbmlFile , Simulation_Parameter simParam){
		this.simParam = simParam;
		this.sbmlFile = sbmlFile;
		try {
			this.model = SBMLReader.read( new File( sbmlFile )).getModel();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		simulation();
	}
	public void simulation(){
		double stepSize = (double) simParam.getEndTime() / (double) simParam.getNumTime();
		double timeEnd = simParam.getEndTime();
		//DESSolver solver = new RosenbrockSolver();
		RosenbrockSolver solver = new RosenbrockSolver();
		solver.setStepSize( stepSize);
		if( simParam.getTolerance() != null ){
			solver.setAbsTol( simParam.getTolerance() );
		}
		else{
			solver.setAbsTol( 1.0e-12 );
		}
		try {
			SBMLinterpreter interpreter = new SBMLinterpreter( model );
			solution = solver.solve( interpreter, interpreter.getInitialValues() ,0d, timeEnd);
		} catch (SBMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelOverdeterminedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DerivativeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public MultiTable getTimeSeries(){
		return( this.solution );
	}
	public Simulation_AllBeans configureSimulationBeans(Coloring colorOfVis) throws NoDynamicSpeciesException{
		int numOfTimePoints = solution.getTimePoints().length;
		double maxCnadidate = 0.0;
		double minCandidate = Double.MAX_VALUE;
		Simulation_AllBeans simAllBeans = new Simulation_AllBeans();
		// The spicies information is contained in listOfSpecies
		if( model.getNumSpecies() != 0 ){ 
			ArrayList<String> orderNotFixedSpecies = getSBMLIDNotFixedSpecies();
			//int numOfSpecies = model.getListOfSpecies().size();
			int numOfSpecies = orderNotFixedSpecies.size();
			Simulation_DatasetsBeans allDataSets[] = new Simulation_DatasetsBeans[ numOfSpecies ];
			int speciesCount = 0;
			for( int i = 0 ; i < numOfSpecies ; i ++){
				for( int j = 0 ; j < solution.getColumnCount() ; j ++){
					//if( model.getListOfSpecies().get( i ).getId().equals( solution.getColumnName( j ))){
					if( orderNotFixedSpecies.get( i ).equals( solution.getColumnName( j ))){
						allDataSets[ speciesCount ] = new Simulation_DatasetsBeans();
						//if( !model.getListOfSpecies().get( i ).getName().equals("")){
						if( !model.getListOfSpecies().get( orderNotFixedSpecies.get( i ) ).getName().equals("")){
							allDataSets[ speciesCount ].setLabel( model.getListOfSpecies().get( orderNotFixedSpecies.get( i ) ).getName());
						}
						else{
							allDataSets[ speciesCount ].setLabel( solution.getColumnName( j ));
						}
						allDataSets[ speciesCount ].setSBMLId( solution.getColumnName( j ));
						Simulation_XYDataBeans allXYDataBeans[] = new Simulation_XYDataBeans[ numOfTimePoints ];
						for( int k = 0 ; k < numOfTimePoints ; k ++){
							allXYDataBeans[ k ] = new Simulation_XYDataBeans();
							allXYDataBeans[ k ].setX( solution.getTimePoint( k ));
							allXYDataBeans[ k ].setY( solution.getValueAt( k , j ));
							if( maxCnadidate < solution.getValueAt( k , j )){
								maxCnadidate = solution.getValueAt( k , j );
							}
							if( minCandidate > solution.getValueAt( k , j ) && solution.getValueAt( k , j ) > 0.0){
								minCandidate = solution.getValueAt( k , j );
							}
						}
						allDataSets[ speciesCount ].setData( allXYDataBeans );
						allDataSets[ speciesCount ].setBorderColor( colorOfVis.getColor( speciesCount ));
						allDataSets[ speciesCount ].setPointBorderColor( colorOfVis.getColor( speciesCount ));
						allDataSets[ speciesCount ].setBackgroundColor( colorOfVis.getColor( speciesCount ));
						allDataSets[ speciesCount ].setPointRadius( 1 );
						speciesCount += 1;
					}
				}
			}
			simAllBeans.setData( allDataSets );
		}
		// The species information is contained in listOfParameters( ordinal fomat of SBML for FBA)
		else{
			ArrayList< String > sbmlIDOfODESpecies = getSBMLIDODESpecies();
			int numOfSpecies = sbmlIDOfODESpecies.size();
			int speciesCount = 0;
			Simulation_DatasetsBeans allDataSets[] = new Simulation_DatasetsBeans[ numOfSpecies ];
			for( int i = 0 ; i < numOfSpecies ; i ++){
				for( int j = 0 ; j < solution.getColumnCount() ; j ++){
					if( model.getListOfParameters().get( sbmlIDOfODESpecies.get( i ) ).getId().equals( solution.getColumnName( j ))){
						allDataSets[ speciesCount ] = new Simulation_DatasetsBeans();
						
						if( !model.getListOfParameters().get(sbmlIDOfODESpecies.get( i )).getName().equals("")){
							allDataSets[ speciesCount ].setLabel( model.getListOfParameters().get( sbmlIDOfODESpecies.get( i )).getName());
						}
						else{
							allDataSets[ speciesCount ].setLabel( solution.getColumnName( j ));
						}
						allDataSets[ speciesCount ].setSBMLId( solution.getColumnName( j ));
						
						Simulation_XYDataBeans allXYDataBeans[] = new Simulation_XYDataBeans[ numOfTimePoints ];
						for( int k = 0 ; k < numOfTimePoints ; k ++){
							allXYDataBeans[ k ] = new Simulation_XYDataBeans();
							allXYDataBeans[ k ].setX( solution.getTimePoint( k ));
							allXYDataBeans[ k ].setY( solution.getValueAt( k , j ));
							if( maxCnadidate < solution.getValueAt( k , j )){
								maxCnadidate = solution.getValueAt( k , j );
							}
							if( minCandidate > solution.getValueAt( k , j ) && solution.getValueAt( k , j ) > 0.0){
								minCandidate = solution.getValueAt( k , j );
							}
						}
						allDataSets[ speciesCount ].setData( allXYDataBeans );
						allDataSets[ speciesCount ].setBorderColor( colorOfVis.getColor( speciesCount ));
						allDataSets[ speciesCount ].setPointBorderColor( colorOfVis.getColor( speciesCount ));
						allDataSets[ speciesCount ].setBackgroundColor( colorOfVis.getColor( speciesCount ));
						allDataSets[ speciesCount ].setPointRadius( 1 );
						speciesCount += 1;
					}
				}
			}
			simAllBeans.setData( allDataSets );
		}
		simAllBeans.setXmax( solution.getTimePoint( numOfTimePoints - 1));
		simAllBeans.setYmax( maxCnadidate );
		simAllBeans.setYmin( minCandidate );
		if( simAllBeans.getData().length == 0 ){
			throw new NoDynamicSpeciesException();
		}
		return simAllBeans;
	}
	private ArrayList<String> getSBMLIDODESpecies(){
		ArrayList< String > orderODESpecies = new ArrayList<>();
		CCopasiDataModel cdataModel = CCopasiRootContainer.addDatamodel();
		try {
			cdataModel.importSBML( this.sbmlFile );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for( int i = 0 ; i < cdataModel.getModel().getNumModelValues() ; i ++ ){
			if( cdataModel.getModel().getModelValue( i ).getStatus() == CModelEntity.ODE || cdataModel.getModel().getModelValue( i ).getStatus() == CModelEntity.ASSIGNMENT  ){
				orderODESpecies.add( cdataModel.getModel().getModelValue( i ).getSBMLId() );
			}
		}
		
		return orderODESpecies;
	}
	private ArrayList<String> getSBMLIDNotFixedSpecies(){
		ArrayList< String > orderNotFixedSpecies = new ArrayList<>();
		CCopasiDataModel cdataModel = CCopasiRootContainer.addDatamodel();
		try {
			cdataModel.importSBML( this.sbmlFile );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for( int i = 0 ; i < cdataModel.getModel().getNumMetabs() ; i ++){
			if( cdataModel.getModel().getMetabolite( i ).getStatus() != 0 ){
				orderNotFixedSpecies.add( cdataModel.getModel().getMetabolite( i ).getSBMLId() );
			}
		}
		return orderNotFixedSpecies;
	}
}
