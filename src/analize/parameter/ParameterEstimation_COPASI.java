package analize.parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


import org.COPASI.CCopasiDataModel;
import org.COPASI.CCopasiObject;
import org.COPASI.CCopasiObjectName;
import org.COPASI.CCopasiParameter;
import org.COPASI.CCopasiParameterGroup;
import org.COPASI.CCopasiRootContainer;
import org.COPASI.CCopasiTask;
import org.COPASI.CExperiment;
import org.COPASI.CExperimentObjectMap;
import org.COPASI.CExperimentSet;
import org.COPASI.CFitItem;
import org.COPASI.CFitProblem;
import org.COPASI.CFitTask;
import org.COPASI.CKeyFactory;
import org.COPASI.CMetab;
import org.COPASI.COptItem;
import org.COPASI.CReaction;
import org.COPASI.CTaskEnum;
import org.COPASI.FloatMatrix;
import org.COPASI.FloatVector;

import beans.simulation.Simulation_DatasetsBeans;
import beans.simulation.Simulation_XYDataBeans;
import parameter.ParameterEstimation_Parameter;




public class ParameterEstimation_COPASI {
	private File SBMLFile;
	private File ExperimentFile;
	private int ExpRow;
	private int ExpCol;
	private FloatMatrix dependentData;
	private FloatVector timeData;
	private HashMap<String, Double> optimizedParam;
	private CCopasiDataModel dataModel;
	private CExperimentSet experimentSet;
	private ParameterEstimation_Parameter paramestParam;
	public ParameterEstimation_COPASI( ParameterEstimation_Parameter paramestParam , File SBML , File Exp){
		this.paramestParam = paramestParam;
		this.SBMLFile = SBML;
		this.ExperimentFile = Exp;
	}
	
	public CCopasiDataModel getDataModel() {
		return dataModel;
	}
	// Public Method :
	//Main function to estimate parameter
	public void estimateParameter(){
		this.dataModel = CCopasiRootContainer.addDatamodel();
		try{
			dataModel.importSBML( SBMLFile.getPath() );
		}
		catch( java.lang.Exception ex){
			System.err.println("Error while importing the model");
			ex.printStackTrace();
		}
		
		//Preprocess : Preparing the metabolite data
		CKeyFactory keyFactory = CCopasiRootContainer.getKeyFactory();
		Vector<CMetab> metabVector = new Vector<CMetab>();
		
		for( int i = 0 ; i < dataModel.getModel().getNumMetabs() ; i ++){
			String key  = dataModel.getModel().getMetabolite( i ).getKey();
			CMetab m = (CMetab) keyFactory.get( key );
			metabVector.add( m );
		}
		//Preprocess : Preparing and Configuring Task for parameter estimation 
		
		// CFitTask is selected to fit the parameter
		CFitTask fitTask = (CFitTask) dataModel.addTask( CTaskEnum.parameterFitting);
		configureTaskParameter( fitTask );
				
		// The problem for task is set. Detail configuration is continued
		CFitProblem fitProblem = (CFitProblem) fitTask.getProblem();
		experimentSet = (CExperimentSet) fitProblem.getParameter("Experiment Set");
		
		CExperiment experiment = new CExperiment( dataModel );
		experiment.setFileName( ExperimentFile.getPath().toString());
		experiment.setSeparator(",");
		experiment.setFirstRow( 1 );
		experiment.setHeaderRow( 1 );
		experiment.setExperimentType( CTaskEnum.timeCourse);
		
		// Number of time( Row of Experiment File) and variables( Column of Experiment File) will be analyzed
		analyzeRowColumn();
		experiment.setNumColumns( this.ExpCol );
		experiment.setLastRow( this.ExpRow);
		//Following code let experiment get ColumnNames which contains the Time and Species ID
		experiment.readColumnNames();
		// CExperimentObjectMap is configured
		CExperimentObjectMap objectMap = experiment.getObjectMap();
		objectMap.setNumCols( this.ExpCol);
		//Time ObjectMap is set
		objectMap.setRole( 0 ,  CExperiment.time);
		objectMap.setObjectCN( 0 , dataModel.getModel().getValueReference().getCN().getString());
		
		//ObjectMap for each Species is set( if the ID of matrix and SBML is same, following code correctly work, it was already cofirmed.
		for( int i = 1 ; i < experiment.getColumnNames().size() ; i ++){
			for( int j = 0 ; j < metabVector.size() ; j ++){
				if( metabVector.elementAt( j ).getSBMLId().equals( experiment.getColumnNames().get( i ))){
					objectMap.setRole( i , CExperiment.dependent);
					CMetab tmpMetab = metabVector.elementAt( j );
					objectMap.setObjectCN( i , tmpMetab.getConcentrationReference().getCN().getString());
				}
			}
		}
		experimentSet.addExperiment( experiment );
		
		//Preprocess : Preparing the optimized parameter set
		CCopasiParameterGroup optimizationItemGroup = (CCopasiParameterGroup) fitProblem.getParameter("OptimizationItemList");
		List<CCopasiParameter> paramList = new ArrayList<>();
		
		for( int i = 0 ; i < dataModel.getModel().getNumReactions() ; i ++){
			CReaction reaction = dataModel.getModel().getReaction( i );
			for( int j = 0 ; j < reaction.getParameters().size() ; j ++){
				CCopasiParameter parameter = reaction.getParameters().getParameter( j );
				CCopasiObject parameterReference = parameter.getValueReference();
				CFitItem fitItem = new CFitItem( dataModel );
				fitItem.setObjectCN( parameterReference.getCN());
				fitItem.setStartValue( parameter.getDblValue() );
				//Following Lower and Upper bound is selected by User in future
				fitItem.setLowerBound( new CCopasiObjectName( new Double( parameter.getDblValue() / 1000).toString()));
				fitItem.setUpperBound( new CCopasiObjectName( new Double( parameter.getDblValue() * 10 ).toString() ));
				optimizationItemGroup.addParameter( fitItem );
				
				// paramList contains the parameter for each order of parameter in COptItemList got by CFitProblem
				paramList.add( parameter );
			}
		}
		
		//Execution of parameter estimation
		try{
			boolean result = fitTask.processWithOutputFlags( true , ( int ) CCopasiTask.ONLY_TIME_SERIES );
		}
		catch(Exception ex)
        {
          System.err.println("Error. Parameter fitting failed.");
			String lastError = fitTask.getProcessError();
            // check if there are additional error messages
            if (lastError.length() > 0)
            {
                // print the messages in chronological order
                System.err.println(lastError);
            }
          System.exit(1);
        }
		
		//Get the optimized Parameter value
		optimizedParam = new HashMap<>();
		for( int i = 0 ; i < fitProblem.getOptItemList().size() ; i ++){
			COptItem tmpOptItem = fitProblem.getOptItemList().get( i );
			optimizedParam.put( tmpOptItem.getObjectDisplayName() , fitProblem.getSolutionVariables().get( i ));
			
			// The parameter value in current model is changed by following line
			paramList.get( i ).setDblValue( fitProblem.getSolutionVariables().get( i ));
			dataModel.getModel().updateInitialValues( paramList.get( i ));
		}
		
		// Get the information of experiment
		this.dependentData = experimentSet.getExperiment( 0 ).getDependentData();
		this.timeData = experimentSet.getExperiment( 0 ).getTimeData();
	}
	
	// If the result should be sent to cliend side as JSON format, following code can be useful and the "expDataBeans" contains it.
	public Simulation_DatasetsBeans[] configureParamEstBeans(){
		CExperiment experiment = this.experimentSet.getExperiment( 0 );
		Simulation_DatasetsBeans expDataBeans[] = new Simulation_DatasetsBeans[ (int ) experiment.getDependentData().numCols()];
		for( int i = 0 ; i < experiment.getDependentData().numCols() ; i ++){
			expDataBeans[ i ] = new Simulation_DatasetsBeans();
			Simulation_XYDataBeans tmpXYDataBeans[] = new Simulation_XYDataBeans[ (int ) experiment.getDependentData().numRows() ];
			for( int j = 0 ; j < experiment.getDependentData().numRows() ; j ++){
				tmpXYDataBeans[ j ] = new Simulation_XYDataBeans();
				tmpXYDataBeans[ j ].setX( experiment.getTimeData().get( j ));
				tmpXYDataBeans[ j ].setY( experiment.getDependentData().get( j , i ));
			}
			expDataBeans[ i ].setData( tmpXYDataBeans );
			expDataBeans[ i ].setLabel( experiment.getColumnNames().get( i + 1 ) + " Experiment Data");
			expDataBeans[ i ].setShowLine( false );
			expDataBeans[ i ].setPointStyle("cross");
			expDataBeans[ i ].setPointRadius( 5 );
		}
		return expDataBeans;
	}
	public HashMap<String, Double> getOptimizedParam() {
		return optimizedParam;
	}
	public FloatMatrix getDependentData() {
		return dependentData;
	}
	public FloatVector getTimeData() {
		return timeData;
	}
	
	// Private Method : 	
	// Configure the parameter of task( algorithm to execute parameter estimation , iteration and tolerance)
	private void configureTaskParameter(CFitTask fitTask) {
		// Algorithm Setting
		if( this.paramestParam.getMethod().equals("lv")){
			fitTask.setMethodType( CTaskEnum.LevenbergMarquardt );
			// Iteration to analyze is set
			CCopasiParameter iteMax = fitTask.getMethod().getParameter("Iteration Limit");
			iteMax.setIntValue( paramestParam.getIteLimit() );
			// Tolerance to analyze is set
			CCopasiParameter tolerance = fitTask.getMethod().getParameter("Tolerance");
			tolerance.setDblValue( paramestParam.getTolerance() );
		}
		else if( this.paramestParam.getMethod().equals("ga")){
			fitTask.setMethodType( CTaskEnum.GeneticAlgorithm );
			
			CCopasiParameter generations = fitTask.getMethod().getParameter("Number of Generations");
			generations.setIntValue( paramestParam.getNumGenerations());
			
			CCopasiParameter populations = fitTask.getMethod().getParameter("Population Size");
			populations.setIntValue( paramestParam.getPopSize());
		}
		else if( this.paramestParam.getMethod().equals("nelder")){
			fitTask.setMethodType( CTaskEnum.NelderMead );
			
			// Iteration to analyze is set
			CCopasiParameter iteMax = fitTask.getMethod().getParameter("Iteration Limit");
			iteMax.setIntValue( paramestParam.getIteLimit() );
			
			// Tolerance to analyze is set
			CCopasiParameter tolerance = fitTask.getMethod().getParameter("Tolerance");
			tolerance.setDblValue( paramestParam.getTolerance() );
		}
	}
	
	private void analyzeRowColumn(){
		int RowCount = 0;
		try {
			String line;
			FileReader fr = new FileReader( this.ExperimentFile );
			BufferedReader br = new BufferedReader( fr );
			while( (line = br.readLine()) != null ){
				this.ExpCol = line.split(",").length;
				RowCount += 1;
			}
			this.ExpRow = RowCount ;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
