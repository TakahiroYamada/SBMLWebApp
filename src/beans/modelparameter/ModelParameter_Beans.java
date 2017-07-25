package beans.modelparameter;

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
}
