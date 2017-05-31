package analyze.simulation;
import java.io.File;
import java.nio.file.Path;

import org.COPASI.*;

import beans.simulation.Simulation_AllBeans;
import beans.simulation.Simulation_DatasetsBeans;
import beans.simulation.Simulation_XYDataBeans;


public class Simulation_COPASI {
	private CTimeSeries simTimeSeries;
	private CCopasiDataModel dataModel;
	public Simulation_COPASI( String sbmlFile ){
		dataModel = CCopasiRootContainer.addDatamodel();
		try {
			dataModel.importSBML( sbmlFile );
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		simulation();
	}
	public Simulation_COPASI( CCopasiDataModel dataModel ){
		this.dataModel = dataModel;
		simulation();
	}
	private void simulation(){
		CModel simModel = dataModel.getModel();
		CReportDefinitionVector simReports = dataModel.getReportDefinitionList();
		
		CReportDefinition simrepDefinition = simReports.createReportDefinition( "Report" , "Output for timecource");
		simrepDefinition.setTaskType( CTaskEnum.timeCourse );
		simrepDefinition.setIsTable( false );
		simrepDefinition.setSeparator( "," );
		
		ReportItemVector header = simrepDefinition.getHeaderAddr();
		ReportItemVector body = simrepDefinition.getBodyAddr();
		
		body.add(new CRegisteredObjectName(simModel.getObject(new CCopasiObjectName("Reference=Time")).getCN().getString()));
        body.add(new CRegisteredObjectName(simrepDefinition.getSeparator().getCN().getString()));
        header.add(new CRegisteredObjectName(new CCopasiStaticString("time").getCN().getString()));
        header.add(new CRegisteredObjectName(simrepDefinition.getSeparator().getCN().getString()));
        
        int i , iMax = ( int ) simModel.getMetabolites().size();
        for( i = 0 ; i < iMax ; i ++){
        		CMetab metab = simModel.getMetabolite( i );
        		body.add(new CRegisteredObjectName(metab.getObject(new CCopasiObjectName("Reference=Concentration")).getCN().getString()));
        		header.add(new CRegisteredObjectName(new CCopasiStaticString(metab.getSBMLId()).getCN().getString()));
            
            if(i!=iMax-1){
              body.add(new CRegisteredObjectName(simrepDefinition.getSeparator().getCN().getString()));
              header.add(new CRegisteredObjectName(simrepDefinition.getSeparator().getCN().getString()));
            }
        }
        
        CTrajectoryTask simTrajekTask = ( CTrajectoryTask ) dataModel.getTask( "Time-Course");
        simTrajekTask.setMethodType( CTaskEnum.deterministic );
        simTrajekTask.getProblem().setModel( dataModel.getModel() );
        simTrajekTask.setScheduled( true );
        simTrajekTask.getReport().setTarget( "SimulationResult.txt");
        simTrajekTask.getReport().setAppend( false );
        
        
        CTrajectoryProblem simProblem = ( CTrajectoryProblem )simTrajekTask.getProblem();
        simProblem.setStepNumber( 100 );
        dataModel.getModel().setInitialTime( 0.0 );
        simProblem.setDuration( 100 );
        simProblem.setTimeSeriesRequested( true );
        
        CTrajectoryMethod simMethod = ( CTrajectoryMethod )simTrajekTask.getMethod();
        
        CCopasiParameter simParameter = simMethod.getParameter("Absolute Tolerance");
        simParameter.setDblValue( 1.0e-12 );
        
        boolean result=true;
        try
        {
            result = simTrajekTask.processWithOutputFlags( true, (int)CCopasiTask.ONLY_TIME_SERIES);
        }
        catch ( Exception e)
        {
        		e.printStackTrace();	
        }
        
        simTimeSeries = simTrajekTask.getTimeSeries();
        
	}
	public CTimeSeries getTimeSeries(){
		return( this.simTimeSeries );
	}
	//Following code sum up with the Beans of JSONIC and the return value will be encoded as JSON format and responsed to Client side.
	public Simulation_AllBeans configureSimulationBeans() {
		long numOfSpecies = simTimeSeries.getNumVariables();
		long numOfTimePoints = simTimeSeries.getRecordedSteps();
		double maxCandidate = 0.0;
		Simulation_AllBeans simAllBeans = new Simulation_AllBeans();
		Simulation_DatasetsBeans allDataSets[] = new Simulation_DatasetsBeans[ (int) (numOfSpecies - 1)];
		//i == 0 means the value of time point! this is considered as the value of x axis!
		for( int i = 1 ; i < numOfSpecies ; i ++ ){
			allDataSets[ i - 1 ] = new Simulation_DatasetsBeans();
			allDataSets[ i - 1 ].setLabel( simTimeSeries.getTitle( i ));
			
			Simulation_XYDataBeans allXYDataBeans[] = new Simulation_XYDataBeans[ (int) numOfTimePoints ];
			for( int j = 0 ; j < numOfTimePoints ; j ++){
				allXYDataBeans[ j ] = new Simulation_XYDataBeans();
				allXYDataBeans[ j ].setX( simTimeSeries.getConcentrationData( j , 0));
				allXYDataBeans[ j ].setY( simTimeSeries.getConcentrationData( j, i ));
				if( maxCandidate < simTimeSeries.getConcentrationData( j , i)){
					maxCandidate = simTimeSeries.getConcentrationData( j , i );
				}
			}
			allDataSets[ i - 1 ].setData( allXYDataBeans );
		}
			
		simAllBeans.setData( allDataSets );
		simAllBeans.setXmax( simTimeSeries.getData( numOfTimePoints - 1 , 0));
		simAllBeans.setYmax( maxCandidate );
		return simAllBeans;
	}
}
