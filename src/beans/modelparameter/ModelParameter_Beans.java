package beans.modelparameter;

import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;

public class ModelParameter_Beans {
	private InitialValue_Beans initValue[];
	private Parameters_Beans paramValue[];
	private LocalParameters_Beans localParamValue[];
	private Compartment_Beans compartmentValue[];
	public InitialValue_Beans[] getInitValue() {
		return initValue;
	}
	public void setInitValue(InitialValue_Beans[] initValue) {
		this.initValue = initValue;
	}
	public Parameters_Beans[] getParamValue() {
		return paramValue;
	}
	public void setParamValue(Parameters_Beans[] paramValue) {
		this.paramValue = paramValue;
	}
	public LocalParameters_Beans[] getLocalParamValue() {
		return localParamValue;
	}
	public void setLocalParamValue(LocalParameters_Beans[] localParamValue) {
		this.localParamValue = localParamValue;
	}
	public Compartment_Beans[] getCompartmentValue() {
		return compartmentValue;
	}
	public void setCompartmentValue(Compartment_Beans[] compartmentValue) {
		this.compartmentValue = compartmentValue;
	}
	public LocalParameters_Beans getLocalParametersById( String reId , String paraId){
		for( int i = 0 ; i < this.localParamValue.length ; i ++){
			if( this.localParamValue[ i ].getReactionID().equals( reId) && this.localParamValue[ i ].getSbmlID().equals( paraId )){
				return localParamValue[ i ];
			}
		}
		return null;
	}
	public Parameters_Beans getGlobalParameterById( String paraId){
		for( int i = 0 ; i < this.paramValue.length ; i ++){
			if( this.paramValue[ i ].getSbmlID().equals( paraId )){
				return this.paramValue[ i ];
			}
		}
		return null;
	}
}
