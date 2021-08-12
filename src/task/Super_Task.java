package task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLException;

import beans.modelparameter.ModelParameter_Beans;
import beans.simulation.Simulation_AllBeans;
import exception.JSBML_ReadException;
import manipulator.SBML_Manipulator;

public class Super_Task {
	protected File newFile;
	protected File experimentDataFile;
	private SBML_Manipulator manipulator;
	protected void saveFile( String filePath , String fileName , String fileString ) throws IOException{
		File tmpDir = new File( filePath );
		tmpDir.mkdirs();
		
		String fileNameWithPath = filePath + "/" + fileName;
		this.newFile = new File( fileNameWithPath );
		BufferedWriter writer = new BufferedWriter( new FileWriter( newFile ));
		writer.write( fileString );
		writer.close();
	}
	protected void saveExperimentData( String filePath , String fileName , String fileString) throws IOException{
		File tmpDir = new File( filePath );
		tmpDir.mkdirs();
		
		String fileNameWithPath = filePath + "/" + fileName;
		this.experimentDataFile = new File( fileNameWithPath );
		BufferedWriter writer = new BufferedWriter( new FileWriter( this.experimentDataFile ));
		writer.write( fileString );
		writer.close();
	}
	protected void manipulateSBMLModel( ModelParameter_Beans sbmlParam) throws SBMLException, IllegalArgumentException, XMLStreamException, IOException, JSBML_ReadException{
		this.manipulator = new SBML_Manipulator( this.newFile );
		this.manipulator.editModelParameter( sbmlParam );
	}
	protected void postProcess( Simulation_AllBeans allBeans ){
		this.manipulator.addUnitForEachSpecies( allBeans );
		this.manipulator.addAmountConcentration( allBeans );
		allBeans.setModelParameters( this.manipulator.getModelParameter() );
	}
	public SBML_Manipulator getManipulator() {
		return manipulator;
	}
	
}
