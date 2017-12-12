package analyze.simulation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.COPASI.CCopasiDataModel;
import org.COPASI.CCopasiRootContainer;
import org.COPASI.CModelEntity;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLReader;

import beans.simulation.Simulation_AllBeans;
import beans.simulation.Simulation_DatasetsBeans;
import beans.simulation.Simulation_XYDataBeans;
import coloring.Coloring;
import exception.NoDynamicSpeciesException;
import jp.ac.keio.bio.fun.libsbmlsim.*;
import parameter.Simulation_Parameter;


public class Simulation_libSBMLsim {
	private myResult r;
	private String sbmlFile;
	private Model model;
	static{
		System.loadLibrary("sbmlsimj");
	}
	public Simulation_libSBMLsim( String sbmlFile , Simulation_Parameter param){
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
		this.r = libsbmlsim.simulateSBMLFromFile( sbmlFile , param.getEndTime() , param.getEndTime() / param.getNumTime() , 1, 0, libsbmlsim.MTHD_RUNGE_KUTTA_FEHLBERG_5, 0);
	}
	public Simulation_AllBeans configureSimulationBeans( Coloring colorOfVis) throws NoDynamicSpeciesException{
		int numOfTimePoints = this.r.getNumOfRows();
		double maxCandidate = 0.0;
		double minCandidate = Double.MAX_VALUE;
		Simulation_AllBeans simAllBeans = new Simulation_AllBeans();
		
		if( r.getNumOfSpecies() != 0 ){
			ArrayList< String > orderNotFixedSpecies = getSBMLIDNotFixedSpecies();
			int numOfSpecies = orderNotFixedSpecies.size();
			Simulation_DatasetsBeans allDataSets[] = new Simulation_DatasetsBeans[ numOfSpecies ];
			int speciesCount = 0;
			for( int i = 0 ; i < numOfSpecies ; i ++){
				for( int j = 0 ; j < r.getNum_of_columns_sp() ; j ++){
					if( orderNotFixedSpecies.get( i ).equals( r.getSpeciesNameAtIndex( j ))){
						allDataSets[ speciesCount ] = new Simulation_DatasetsBeans();
						if( !model.getListOfSpecies().get( orderNotFixedSpecies.get( i )).getName().equals("")){
							allDataSets[ speciesCount ].setLabel( model.getListOfSpecies().get(orderNotFixedSpecies.get( i )).getName());
						}
						else{
							allDataSets[ speciesCount ].setLabel( model.getListOfSpecies().get(orderNotFixedSpecies.get( i )).getId());
						}
						allDataSets[ speciesCount ].setSBMLId( model.getListOfSpecies().get(orderNotFixedSpecies.get( i )).getId());
						Simulation_XYDataBeans allXYDataBeans[] = new Simulation_XYDataBeans[ numOfTimePoints ];
						for( int k = 0 ; k < numOfTimePoints ; k ++ ){
							allXYDataBeans[ k ] = new Simulation_XYDataBeans();
							allXYDataBeans[ k ].setX( r.getTimeValueAtIndex( k ));
							allXYDataBeans[ k ].setY( r.getSpeciesValueAtIndex( r.getSpeciesNameAtIndex( j ), k));
							if( maxCandidate < r.getSpeciesValueAtIndex( r.getSpeciesNameAtIndex( j ), k)){
								maxCandidate = r.getSpeciesValueAtIndex( r.getSpeciesNameAtIndex( j ), k);
							}
							if( minCandidate > r.getSpeciesValueAtIndex( r.getSpeciesNameAtIndex( j ), k)){
								minCandidate = r.getSpeciesValueAtIndex( r.getSpeciesNameAtIndex( j ), k);
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
		else{
			ArrayList< String > sbmlIDOfODESpecies = getSBMLIDODESpecies();
			int numOfSpecies = sbmlIDOfODESpecies.size();
			int speciesCount = 0;
			Simulation_DatasetsBeans allDataSets[] = new Simulation_DatasetsBeans[ numOfSpecies ];
			for( int i = 0 ; i < numOfSpecies ; i ++){
				for( int j = 0 ; j < r.getNum_of_columns_param() ; j ++){
					if( model.getListOfParameters().get( sbmlIDOfODESpecies.get( i ) ).getId().equals( r.getParameterNameAtIndex( j ))){
						allDataSets[ speciesCount ] = new Simulation_DatasetsBeans();
						
						if( !model.getListOfParameters().get(sbmlIDOfODESpecies.get( i )).getName().equals("")){
							allDataSets[ speciesCount ].setLabel( model.getListOfParameters().get( sbmlIDOfODESpecies.get( i )).getName());
						}
						else{
							allDataSets[ speciesCount ].setLabel( model.getListOfParameters().get( sbmlIDOfODESpecies.get( i )).getId());
						}
						allDataSets[ speciesCount ].setSBMLId( model.getListOfParameters().get( sbmlIDOfODESpecies.get( i )).getId());
						
						Simulation_XYDataBeans allXYDataBeans[] = new Simulation_XYDataBeans[ numOfTimePoints ];
						for( int k = 0 ; k < numOfTimePoints ; k ++){
							allXYDataBeans[ k ] = new Simulation_XYDataBeans();
							allXYDataBeans[ k ].setX( r.getTimeValueAtIndex( k ));
							allXYDataBeans[ k ].setY( r.getParameterValueAtIndex( r.getParameterNameAtIndex( j ), k));
							if( maxCandidate < r.getParameterValueAtIndex( r.getParameterNameAtIndex( j ), k)){
								maxCandidate = r.getParameterValueAtIndex( r.getParameterNameAtIndex( j ), k);
							}
							if( minCandidate > r.getParameterValueAtIndex( r.getParameterNameAtIndex( j ), k)){
								minCandidate = r.getParameterValueAtIndex( r.getParameterNameAtIndex( j ), k);
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
		simAllBeans.setXmax( r.getTimeValueAtIndex( numOfTimePoints - 1));
		simAllBeans.setYmax( maxCandidate );
		simAllBeans.setYmin( minCandidate );
		if( simAllBeans.getData().length == 0 ){
			throw new NoDynamicSpeciesException();
		}
		return simAllBeans;
	}
	private ArrayList<String> getSBMLIDNotFixedSpecies() {
		// TODO Auto-generated method stub
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
	private ArrayList getSBMLIDODESpecies(){
		ArrayList< String > orderODESpecies = new ArrayList<>();
		CCopasiDataModel cdataModel = CCopasiRootContainer.addDatamodel();
		try {
			cdataModel.importSBML( this.sbmlFile );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for( int i = 0 ; i < cdataModel.getModel().getNumModelValues() ; i ++ ){
			if( cdataModel.getModel().getModelValue( i ).getStatus() == CModelEntity.ODE ){
				orderODESpecies.add( cdataModel.getModel().getModelValue( i ).getSBMLId() );
			}
		}
		
		return orderODESpecies;
	}
	public int getNumberOfVisualizedObject(){
		if( r.getNumOfSpecies() != 0){
			return getSBMLIDNotFixedSpecies().size();
		}
		else{
			return getSBMLIDODESpecies().size();
		}
	}
	public myResult getMyResult() {
		return r;
	}
}
