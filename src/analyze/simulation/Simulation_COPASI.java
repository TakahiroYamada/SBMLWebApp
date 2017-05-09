package analyze.simulation;
import java.io.File;
import org.COPASI.*;


public class Simulation_COPASI {
	private CTimeSeries simTimeSeries;
	public Simulation_COPASI( String sbmlFile ){
		CCopasiDataModel dataModel = CCopasiRootContainer.addDatamodel();
		try {
			dataModel.importSBML( sbmlFile );
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
        simTrajekTask.getReport().setTarget("SimulationResult.txt");
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
            result = simTrajekTask.processWithOutputFlags(true, (int)CCopasiTask.ONLY_TIME_SERIES);
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
}
