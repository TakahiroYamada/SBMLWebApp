package beans.parameter;

import beans.simulation.Simulation_AllBeans;
import beans.simulation.Simulation_DatasetsBeans;
import beans.superclass_beans.SuperClass_Beans;

public class ParameterEstimation_AllBeans extends SuperClass_Beans{
	private Simulation_AllBeans beforeFitting;
	private Simulation_AllBeans afterFitting;
	private Simulation_DatasetsBeans expDataSets[];
	private ParameterEstimation_UpdateInformationBeans updateParam[];
	public Simulation_AllBeans getBeforeFitting() {
		return beforeFitting;
	}
	public void setBeforeFitting(Simulation_AllBeans beforeFitting) {
		this.beforeFitting = beforeFitting;
	}
	public Simulation_AllBeans getAfterFitting() {
		return afterFitting;
	}
	public void setAfterFitting(Simulation_AllBeans afterFitting) {
		this.afterFitting = afterFitting;
	}
	public Simulation_DatasetsBeans[] getExpDataSets() {
		return expDataSets;
	}
	public void setExpDataSets(Simulation_DatasetsBeans[] expDataSets) {
		this.expDataSets = expDataSets;
	}
	public ParameterEstimation_UpdateInformationBeans[] getUpdateParam() {
		return updateParam;
	}
	public void setUpdateParam(ParameterEstimation_UpdateInformationBeans[] updateParam) {
		this.updateParam = updateParam;
	}
}
