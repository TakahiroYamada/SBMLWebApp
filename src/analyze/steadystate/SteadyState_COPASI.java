package analyze.steadystate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.ConfigurationException;

import org.COPASI.CArrayAnnotation;
import org.COPASI.CCopasiDataModel;
import org.COPASI.CCopasiParameter;
import org.COPASI.CCopasiRootContainer;
import org.COPASI.CCopasiTask;
import org.COPASI.CSteadyStateTask;
import org.COPASI.SizeTStdVector;
import org.COPASI.StringStdVector;

import parameter.SteadyStateAnalysis_Parameter;

public class SteadyState_COPASI {
	private String saveFilePath;
	private String sbmlModelName;
	private CCopasiDataModel dataModel;
	
	private SteadyStateAnalysis_Parameter stedParam;
	public SteadyState_COPASI( SteadyStateAnalysis_Parameter stedParam , String saveFilePath , String filename){
		this.stedParam = stedParam;
		this.saveFilePath = saveFilePath;
		this.sbmlModelName = filename;
		
		
	}
	public void executeSteadyStateAnalysis(){
		
		dataModel = CCopasiRootContainer.addDatamodel();
		boolean result = true;
		
		// Data import from SBML model file which user sends.
		try {
			result = dataModel.importSBML( sbmlModelName );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		CSteadyStateTask task = ( CSteadyStateTask ) dataModel.getTask("Steady-State");
		configureTaskParameter( task );
		
		// Execute Steady state analysis
		try{
			task.processWithOutputFlags( true , ( int ) CCopasiTask.ONLY_TIME_SERIES );
		} catch( Exception e ){
			e.printStackTrace();
		}
		
		// Save the result of steady state analysis which contains steady state expression of each species and Jacobian of steady state point.
		this.saveSteadyStateAnalysisResult( task );
	}
	private void configureTaskParameter(CSteadyStateTask task) {
		CCopasiParameter resolution = task.getMethod().getParameter("Resolution");
		resolution.setDblValue( stedParam.getResolution() );
		
		CCopasiParameter derivation_factor = task.getMethod().getParameter("Derivation Factor");
		derivation_factor.setDblValue( stedParam.getDerivation_factor() );
		
		CCopasiParameter iterationLimit = task.getMethod().getParameter("Iteration Limit");
		iterationLimit.setIntValue( stedParam.getIterationLimit() );
	}
	private void saveSteadyStateAnalysisResult( CSteadyStateTask task ){
		
		File file = new File( this.saveFilePath);
		try {
			PrintWriter pw = new PrintWriter( new BufferedWriter( new FileWriter( file )));
			CArrayAnnotation aj = task.getJacobianAnnotated();
			if( aj != null ){
				SizeTStdVector index = new SizeTStdVector( 2 );
				pw.println("Equilibrium Point");
				for( int i = 0 ; i < this.dataModel.getModel().getNumMetabs() ; i ++){
					pw.println( this.dataModel.getModel().getMetabolite( i ).getObjectDisplayName() + " = " + dataModel.getModel().getMetabolite( i ).getConcentration());
				}
				pw.println("");
				
				StringStdVector annotations = aj.getAnnotationsString( 1 );
				pw.println("Jacobian Matrix: ");
				pw.println("");
				pw.print("\t");
				for (int i = 0; i < annotations.size(); ++i){
			           pw.printf("%7s\t",annotations.get(i));
			    }
				pw.println("");
				for( int i = 0 ; i < annotations.size() ; i ++){
					pw.printf("%7s\t", annotations.get( i ));
					index.set( 0 , i);
					for( int j = 0 ; j < annotations.size() ; j++ ){
						index.set( 1 , j );
						pw.printf(("%7.3f"), aj.array().get( index ));
					}
					pw.println("");
				}
			}
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
