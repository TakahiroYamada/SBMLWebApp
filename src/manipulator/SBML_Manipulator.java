package manipulator;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.Species;
import beans.modelparameter.Compartment_Beans;
import beans.modelparameter.InitialValue_Beans;
import beans.modelparameter.LocalParameters_Beans;
import beans.modelparameter.ModelParameter_Beans;
import beans.modelparameter.Parameters_Beans;
import beans.simulation.Simulation_AllBeans;
import exception.JSBML_ReadException;

public class SBML_Manipulator {
	private File sbmlFile;
	private SBMLDocument document;
	public SBML_Manipulator( File file ) throws JSBML_ReadException{
		this.sbmlFile = file;
		try {
			this.document = SBMLReader.read( sbmlFile );
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch( NullPointerException e){
			e.printStackTrace();
			throw new JSBML_ReadException( e );
		}
	}
	public ModelParameter_Beans getModelParameter(){
		ModelParameter_Beans modelParam = new ModelParameter_Beans();
		modelParam.setParamValue( this.getParameters() );
		modelParam.setCompartmentValue( this.getCompartment());
		modelParam.setLocalParamValue( this.getLocalParameters());
		modelParam.setInitValue( this.getInitValue());
		return modelParam;
	}
	public void editModelParameter(ModelParameter_Beans sbmlParam) throws SBMLException, XMLStreamException, IOException, IllegalArgumentException{
		// initial value is changed
		for( int i = 0 ; i < sbmlParam.getInitValue().length ; i ++){
			InitialValue_Beans initValue = sbmlParam.getInitValue()[ i ];
			if( initValue.getStatus() == InitialValue_Beans.INIT_AMOUNT ){
				document.getModel().getListOfSpecies().get( initValue.getSbmlID() ).setInitialAmount( initValue.getInitialValue() );
			}
			else if( initValue.getStatus() == InitialValue_Beans.INIT_CONCENTRATION ){
				document.getModel().getListOfSpecies().get( initValue.getSbmlID() ).setInitialConcentration( initValue.getInitialValue() );
			}
		}
		// compartmet size is changed
		for( int i = 0 ; i < sbmlParam.getCompartmentValue().length ; i ++){
			Compartment_Beans compartment = sbmlParam.getCompartmentValue()[ i ];
			document.getModel().getCompartment( compartment.getSbmlID()).setSize( compartment.getSize() );
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
		// the result is saved in  sbmlFile path
		writeSBML();
	}
	public void writeSBML() throws SBMLException, XMLStreamException, IOException {
		SBMLWriter writer = new SBMLWriter();
		writer.write( document, sbmlFile );
	}
	public void addUnitForEachSpecies( Simulation_AllBeans allBeans ){
		for( int i = 0 ; i < allBeans.getData().length ; i ++){
			if( document.getModel().getSpecies( allBeans.getData()[ i ].getSBMLId()) != null ){
				Species tmpSpecies = document.getModel().getSpecies( allBeans.getData()[ i ].getSBMLId());
				if( !tmpSpecies.getUnits().isEmpty() ){
					allBeans.getData()[ i ].setUnits( tmpSpecies.getUnits() );
				}
				else if( !tmpSpecies.getSubstanceUnits().isEmpty()){
					allBeans.getData()[ i ].setUnits( tmpSpecies.getSubstanceUnits() );
				}
				else{
					allBeans.getData()[ i ].setUnits("UnitLess");
				}
			}
			else if( document.getModel().getParameter( allBeans.getData()[ i ].getSBMLId()) != null ){
				Parameter tmpParaeter = document.getModel().getParameter( allBeans.getData()[ i ].getSBMLId() );
				if( !tmpParaeter.getUnits().isEmpty()){
					allBeans.getData()[ i ].setUnits( tmpParaeter.getUnits() );
				}
				else{
					allBeans.getData()[ i ].setUnits( "UnitLess");
				}
			}
		}
	}
	public void addAmountConcentration( Simulation_AllBeans allBeans ){
		if( document.getModel().getNumSpecies() != 0 ){
			for( int i = 0 ; i < document.getModel().getNumSpecies() ; i ++){
				if( document.getModel().getSpecies( i ).isSetInitialAmount() ){
					allBeans.setYaxisLabel("Amount");
				}
				else if( document.getModel().getSpecies( i ).isSetInitialConcentration() ){
					allBeans.setYaxisLabel("Concentration");
				}
			}
		}
		else{
			allBeans.setYaxisLabel("Value");
		}
	}
	private Parameters_Beans[] getParameters(){
		Parameters_Beans[] param_Beans = new Parameters_Beans[ document.getModel().getNumParameters() ];
		for( int i = 0 ; i < document.getModel().getNumParameters() ; i ++){
			Double checker = new Double( document.getModel().getParameter( i ).getValue() );
			param_Beans[ i ] = new Parameters_Beans();
			if( !checker.isNaN() ){
				param_Beans[ i ].setParameterValue( document.getModel().getParameter( i ).getValue() );
			}
			else{
				param_Beans[ i ].setParameterValue( 0.0 );
			}
			param_Beans[ i ].setSbmlName( document.getModel().getParameter( i ).getName() );
			param_Beans[ i ].setSbmlID( document.getModel().getParameter( i ).getId());
		}
		return param_Beans;
	}
	private Compartment_Beans[] getCompartment() {
		// TODO Auto-generated method stub
		Compartment_Beans[] comp_Beans = new Compartment_Beans[ document.getModel().getNumCompartments() ];
		for( int i = 0 ; i < document.getModel().getNumCompartments() ; i ++){
			Double checker = new Double( document.getModel().getCompartment( i ).getSize() );
			comp_Beans[ i ] = new Compartment_Beans();
			comp_Beans[ i ].setSbmlName( document.getModel().getCompartment( i ).getName());
			comp_Beans[ i ].setSbmlID( document.getModel().getCompartment( i ).getId());
			if( !checker.isNaN() ){
				comp_Beans[ i ].setSize( document.getModel().getCompartment( i ).getSize() );
			}
			else{
				comp_Beans[ i ].setSize( 0.0 );
			}
		}
		return comp_Beans;
 	}
	private InitialValue_Beans[] getInitValue(){
		InitialValue_Beans[] init_Beans = new InitialValue_Beans[ document.getModel().getNumSpecies() ];
		int unitStatus = 0;
		for( int i = 0 ; i < document.getModel().getNumSpecies() ; i ++){
			init_Beans[ i ] = new InitialValue_Beans();
			Double checker_Amount = new Double( document.getModel().getSpecies( i ).getInitialAmount() );
			Double checker_Concentration = new Double( document.getModel().getSpecies( i ).getInitialConcentration() );
			init_Beans[ i ].setSbmlName( document.getModel().getSpecies( i ).getName() );
			init_Beans[ i ].setSbmlID( document.getModel().getSpecies( i ).getId());
			
			// If the value of species is defined as Initial Amount
			if( !checker_Amount.isNaN() ){
				init_Beans[ i ].setInitialValue( document.getModel().getSpecies( i ).getInitialAmount());
				init_Beans[ i ].setStatus( InitialValue_Beans.INIT_AMOUNT );
				unitStatus = InitialValue_Beans.INIT_AMOUNT;
			}
			else if( !checker_Concentration.isNaN() ){
				init_Beans[ i ].setInitialValue( document.getModel().getSpecies( i ).getInitialConcentration() );
				init_Beans[ i ].setStatus( InitialValue_Beans.INIT_CONCENTRATION );
				unitStatus = InitialValue_Beans.INIT_CONCENTRATION;
			}
			else{
				init_Beans[ i ].setInitialValue( 0.0 );
				if( unitStatus == InitialValue_Beans.INIT_AMOUNT ){
					init_Beans[ i ].setStatus( InitialValue_Beans.INIT_AMOUNT );
				}
				else if( unitStatus == InitialValue_Beans.INIT_CONCENTRATION ){
					init_Beans[ i ].setStatus( InitialValue_Beans.INIT_CONCENTRATION );
				}
			}
		}
		return init_Beans;
	}
	private LocalParameters_Beans[] getLocalParameters(){
		int localParamCount = 0;
		LocalParameters_Beans[] localParam_Beans = new LocalParameters_Beans[ this.countLocalParameters() ];
		for( int i = 0 ; i < document.getModel().getNumReactions() ; i ++){
			Reaction reaction = document.getModel().getReaction( i );
			for( int j = 0 ; j < reaction.getKineticLaw().getLocalParameterCount() ; j ++){
				Double checker = new Double( reaction.getKineticLaw().getLocalParameter( j ).getValue() );
				localParam_Beans[ localParamCount ] = new LocalParameters_Beans();
				
				if(!checker.isNaN() ){
					localParam_Beans[ localParamCount ].setParameterValue( reaction.getKineticLaw().getLocalParameter( j ).getValue() );
				}
				else{
					localParam_Beans[ localParamCount ].setParameterValue( 0.0  );
				}
				
				localParam_Beans[ localParamCount ].setReactionName( reaction.getName() );
				localParam_Beans[ localParamCount ].setReactionID( reaction.getId() );
				
				localParam_Beans[ localParamCount ].setSbmlName( reaction.getKineticLaw().getLocalParameter( j ).getName() );
				localParam_Beans[ localParamCount ].setSbmlID( reaction.getKineticLaw().getLocalParameter( j ).getId());
				
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
