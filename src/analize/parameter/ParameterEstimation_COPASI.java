package analize.parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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




public class ParameterEstimation_COPASI {
	private File SBMLFile;
	private File ExperimentFile;
	private int ExpRow;
	private int ExpCol;
	private HashMap<String, Double> optimizedParam;
	public ParameterEstimation_COPASI( File SBML , File Exp){
		this.SBMLFile = SBML;
		this.ExperimentFile = Exp;
	}
	
	public void estimateParameter(){
		CCopasiDataModel dataModel = CCopasiRootContainer.addDatamodel();
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
		
		// CAUTION : Following code select the algorithm to optimize the parameter.
		// In Future following code should be bifurcated in order to let user select the algorithm
		fitTask.setMethodType( CTaskEnum.LevenbergMarquardt);
		CCopasiParameter iteMax = fitTask.getMethod().getParameter("Iteration Limit");
		iteMax.setIntValue( 2000 );
		CCopasiParameter tolerance = fitTask.getMethod().getParameter("Tolerance");
		tolerance.setDblValue( 1.0e-6 );
		
		// The problem for task is set. Detail configuration is continued
		CFitProblem fitProblem = (CFitProblem) fitTask.getProblem();
		CExperimentSet experimentSet = (CExperimentSet) fitProblem.getParameter("Experiment Set");
		
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
			System.out.println( tmpOptItem.getObjectDisplayName() + "->" + fitProblem.getSolutionVariables().get( i ));
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
