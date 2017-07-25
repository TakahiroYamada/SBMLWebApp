package analyze.steadystate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.naming.ConfigurationException;

import org.COPASI.CArrayAnnotation;
import org.COPASI.CCopasiDataModel;
import org.COPASI.CCopasiParameter;
import org.COPASI.CCopasiRootContainer;
import org.COPASI.CCopasiTask;
import org.COPASI.CModelEntity;
import org.COPASI.CModelValue;
import org.COPASI.CSteadyStateTask;
import org.COPASI.SizeTStdVector;
import org.COPASI.StringStdVector;
import org.omg.CORBA.PUBLIC_MEMBER;

import com.ctc.wstx.sw.EncodingXmlWriter;
import com.thoughtworks.xstream.core.util.Fields;

import beans.steadystate.SteadyState_AllBeans;
import beans.steadystate.SteadyState_JacobianBeans;
import beans.steadystate.SteadyState_JacobianColumnBeans;
import beans.steadystate.SteadyState_SteadyAmountBeans;
import parameter.SteadyStateAnalysis_Parameter;

public class SteadyState_COPASI {
	private String saveFilePath;
	private String sbmlModelName;
	private CCopasiDataModel dataModel;
	private SteadyStateAnalysis_Parameter stedParam;
	private CSteadyStateTask task;
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
		
		task = ( CSteadyStateTask ) dataModel.getTask("Steady-State");
		configureTaskParameter( task );
		
		// Execute Steady state analysis
		try{
			task.processWithOutputFlags( true , ( int ) CCopasiTask.ONLY_TIME_SERIES );
		} catch( Exception e ){
			e.printStackTrace();
		}
		
		// Save the result of steady state analysis which contains steady state expression of each species and Jacobian of steady state point.
		this.saveSteadyStateAnalysisResult();
	}
	private void configureTaskParameter(CSteadyStateTask task) {
		CCopasiParameter resolution = task.getMethod().getParameter("Resolution");
		resolution.setDblValue( stedParam.getResolution() );
		
		CCopasiParameter derivation_factor = task.getMethod().getParameter("Derivation Factor");
		derivation_factor.setDblValue( stedParam.getDerivation_factor() );
		
		CCopasiParameter iterationLimit = task.getMethod().getParameter("Iteration Limit");
		iterationLimit.setIntValue( stedParam.getIterationLimit() );
	}
	public void saveSteadyStateAnalysisResult(){
		
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
	public SteadyState_AllBeans configureSteadyBeans(){
		SteadyState_AllBeans allBeans = new SteadyState_AllBeans();
		SizeTStdVector index = new SizeTStdVector( 2 );
		Field[] typeFields = CModelEntity.class.getDeclaredFields();
		// if species information is contained in listOfSpecies
		if( dataModel.getModel().getNumMetabs() != 0 ){
			// Species Amount tab data is included in SteadyAmountBeans
			SteadyState_SteadyAmountBeans[] stedAmount = new SteadyState_SteadyAmountBeans[ (int) dataModel.getModel().getNumMetabs()];
			for( int i = 0 ; i < dataModel.getModel().getNumMetabs() ; i++ ){
				stedAmount[ i ] = new SteadyState_SteadyAmountBeans();
				stedAmount[ i ].setId( i );
				stedAmount[ i ].setName( dataModel.getModel().getMetabolite( i ).getObjectDisplayName() );
				// typeFields contains following data
				// 1:swigCPtr , 2:expressionReference , 3:FIXED , 4:ASSIGNMENT, 5:REACTIONS,6:ODE,7:TIME  
				stedAmount[ i ].setType( typeFields[ dataModel.getModel().getMetabolite( i ).getStatus() + 2].getName());
				stedAmount[ i ].setConcentration( dataModel.getModel().getMetabolite( i ).getConcentration());
				stedAmount[ i ].setRate( dataModel.getModel().getMetabolite( i ).getConcentrationRate());
				stedAmount[ i ].setTransition( dataModel.getModel().getMetabolite( i ).getTransitionTime() );
			}
			allBeans.setSteadyAmount( stedAmount );
			
			// Jacobian is contained JacobianBeans
			SteadyState_JacobianBeans jacobBeans = new SteadyState_JacobianBeans();
			SteadyState_JacobianColumnBeans[] jacobColumnBeans = new SteadyState_JacobianColumnBeans[ (int) dataModel.getModel().getNumMetabs() + 1];
			
			jacobColumnBeans[ 0 ] = new SteadyState_JacobianColumnBeans();
			jacobColumnBeans[ 0 ].setTitle("");
			jacobColumnBeans[ 0 ].setField("speciesid");
			jacobColumnBeans[ 0 ].setSortable( false );
			for( int i = 0 ; i < dataModel.getModel().getNumMetabs() ; i ++){
				jacobColumnBeans[ i + 1 ] = new SteadyState_JacobianColumnBeans();
				jacobColumnBeans[ i + 1 ].setTitle( dataModel.getModel().getMetabolite( i ).getObjectDisplayName() );
				jacobColumnBeans[ i + 1 ].setTitle( dataModel.getModel().getMetabolite( i ).getObjectDisplayName() );
				jacobColumnBeans[ i + 1 ].setField( dataModel.getModel().getMetabolite( i ).getObjectDisplayName() );
				jacobColumnBeans[ i + 1 ].setSortable( false );
			}
			jacobBeans.setColumns( jacobColumnBeans );
			HashMap<String, String>[] jacobValueBeans = new HashMap[ (int) dataModel.getModel().getNumMetabs()];
			CArrayAnnotation jacobAnnotation = task.getJacobianAnnotated();
			StringStdVector jacobianAnnotatedID = jacobAnnotation.getAnnotationsString( 0 );
			for( int i = 0 ; i < dataModel.getModel().getNumMetabs(); i ++){
				jacobValueBeans[ i ] = new HashMap<>();
				for( int j = 0 ; j < jacobianAnnotatedID.size(); j ++){
					if( dataModel.getModel().getMetabolite( i ).getObjectDisplayName().equals( jacobianAnnotatedID.get( j ))){
						jacobValueBeans[ i ].put( "speciesid", jacobianAnnotatedID.get( j ));
						index.set( 0 , j );
						for( int k = 0 ; k < jacobianAnnotatedID.size() ; k ++){
							index.set( 1 , k );
							jacobValueBeans[ i ].put( jacobianAnnotatedID.get( k ), String.valueOf( jacobAnnotation.array().get( index )));
						}
					}
				}
			}
			jacobBeans.setJacob_Amount( jacobValueBeans);
			allBeans.setSteadyJacobian( jacobBeans );
			allBeans.setConcentrationUnit( dataModel.getModel().getMetabolite( 0 ).getConcentrationReference().getUnits() );
			allBeans.setRateUnit( dataModel.getModel().getMetabolite( 0 ).getRateReference().getUnits().replaceFirst("#", allBeans.getConcentrationUnit() ));
			allBeans.setTransitiontimeUnit( dataModel.getModel().getMetabolite( 0 ).getTransitionTimeReference().getUnits() );
		}
		//if species information is contained in listOfParameters( The ordinal format of SBML for FBA)
		else{
			
		}
		return allBeans;
	}
}

