package analyze.steadystate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.COPASI.CArrayAnnotation;
import org.COPASI.CCopasiDataModel;
import org.COPASI.CCopasiRootContainer;
import org.COPASI.CCopasiTask;
import org.COPASI.CSteadyStateTask;
import org.COPASI.SizeTStdVector;
import org.COPASI.StringStdVector;

public class SteadyState_COPASI {
	private CCopasiDataModel dataModel;
	private CSteadyStateTask task;
	private String saveFilePath;
	public SteadyState_COPASI( String saveFilePath , String filename){
		this.saveFilePath = saveFilePath;
		dataModel = CCopasiRootContainer.addDatamodel();
		boolean result = true;
		
		try {
			result = dataModel.importSBML( filename );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		task = ( CSteadyStateTask ) dataModel.getTask("Steady-State");
		try{
			task.processWithOutputFlags( true , ( int ) CCopasiTask.ONLY_TIME_SERIES );
		} catch( Exception e ){
			e.printStackTrace();
		}
		
		this.saveSteadyStateAnalysisResult();
		
	}
	private void saveSteadyStateAnalysisResult( ){
		
		File file = new File( this.saveFilePath);
		try {
			PrintWriter pw = new PrintWriter( new BufferedWriter( new FileWriter( file )));
			CArrayAnnotation aj = this.task.getJacobianAnnotated();
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
