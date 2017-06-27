package manipulator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.SBMLWriter;

import beans.modelparameter.InitialValue_Beans;
import beans.modelparameter.LocalParameters_Beans;
import beans.modelparameter.ModelParameter_Beans;
import beans.modelparameter.Parameters_Beans;

public class SBML_Manipulator {
	private File sbmlFile;
	private SBMLDocument document;
	public SBML_Manipulator( File file ){
		this.sbmlFile = file;
		try {
			this.document = SBMLReader.read( sbmlFile );
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ModelParameter_Beans getModelParameter(){
		ModelParameter_Beans modelParam = new ModelParameter_Beans();
		modelParam.setParamValue( this.getParameters() );
		modelParam.setLocalParamValue( this.getLocalParameters());
		modelParam.setInitValue( this.getInitValue());
		return modelParam;
	}
	public void editModelParameter(ModelParameter_Beans sbmlParam){
		// initial value is changed
		for( int i = 0 ; i < sbmlParam.getInitValue().length ; i ++){
			InitialValue_Beans initValue = sbmlParam.getInitValue()[ i ];
			document.getModel().getListOfSpecies().get( initValue.getSbmlID() ).setInitialAmount( initValue.getInitialValue() );
		}
		// local parameter value is changed
		for( int i = 0 ; i < sbmlParam.getLocalParamValue().length ; i ++){
			LocalParameters_Beans localParam = sbmlParam.getLocalParamValue()[ i ];
			document.getModel().getReaction( localParam.getReactionID() ).getKineticLaw().getLocalParameter( localParam.getSbmlID()).setValue( localParam.getParameterValue() );
		}
		// global parameter value is changed
		for( int i = 0 ; i < sbmlParam.getParamValue().length ; i ++){
			Parameters_Beans globalParam = sbmlParam.getParamValue()[ i ];
			document.getModel().getParameter( globalParam.getSbmlID() ).setValue( globalParam.getParameterValue() );
		}
		// the result is save in  sbmlFile path
		SBMLWriter writer = new SBMLWriter();
		try {
			writer.write( document , sbmlFile );
		} catch (SBMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private Parameters_Beans[] getParameters(){
		Parameters_Beans[] param_Beans = new Parameters_Beans[ document.getModel().getNumParameters() ];
		for( int i = 0 ; i < document.getModel().getNumParameters() ; i ++){
			param_Beans[ i ] = new Parameters_Beans();
			param_Beans[ i ].setSbmlID( document.getModel().getParameter( i ).getId());
			param_Beans[ i ].setParameterValue( document.getModel().getParameter( i ).getValue() );
		}
		return param_Beans;
	}
	private InitialValue_Beans[] getInitValue(){
		InitialValue_Beans[] init_Beans = new InitialValue_Beans[ document.getModel().getNumSpecies() ];
		for( int i = 0 ; i < document.getModel().getNumSpecies() ; i ++){
			init_Beans[ i ] = new InitialValue_Beans();
			init_Beans[ i ].setSbmlID( document.getModel().getSpecies( i ).getId());
			init_Beans[ i ].setInitialValue( document.getModel().getSpecies( i ).getInitialAmount());
		}
		return init_Beans;
	}
	private LocalParameters_Beans[] getLocalParameters(){
		int localParamCount = 0;
		LocalParameters_Beans[] localParam_Beans = new LocalParameters_Beans[ this.countLocalParameters() ];
		for( int i = 0 ; i < document.getModel().getNumReactions() ; i ++){
			Reaction reaction = document.getModel().getReaction( i );
			for( int j = 0 ; j < reaction.getKineticLaw().getLocalParameterCount() ; j ++){
				localParam_Beans[ localParamCount ] = new LocalParameters_Beans();
				localParam_Beans[ localParamCount ].setSbmlID( reaction.getKineticLaw().getLocalParameter( j ).getId());
				localParam_Beans[ localParamCount ].setReactionID( reaction.getId() );
				localParam_Beans[ localParamCount ].setParameterValue( reaction.getKineticLaw().getLocalParameter( j ).getValue() );
				localParamCount ++;
			}
		}
		return localParam_Beans;
	}
	private int countLocalParameters(){
		int count = 0;
		for( int i = 0 ; i < document.getModel().getNumReactions() ; i ++){
			count += document.getModel().getReaction( i ).getKineticLaw().getLocalParameterCount();
		}
		return count;
	}
}
