package beans.steadystate;

public class SteadyState_AllBeans {
	private SteadyState_SteadyAmountBeans steadyAmount[];
	private SteadyState_JacobianBeans steadyJacobian;
	private String concentrationUnit;
	private String rateUnit;
	private String transitiontimeUnit;
	public SteadyState_SteadyAmountBeans[] getSteadyAmount() {
		return steadyAmount;
	}
	public void setSteadyAmount(SteadyState_SteadyAmountBeans[] steadyAmount) {
		this.steadyAmount = steadyAmount;
	}
	public SteadyState_JacobianBeans getSteadyJacobian() {
		return steadyJacobian;
	}
	public void setSteadyJacobian(SteadyState_JacobianBeans steadyJacobian) {
		this.steadyJacobian = steadyJacobian;
	}
	public String getConcentrationUnit() {
		return concentrationUnit;
	}
	public void setConcentrationUnit(String concentrationUnit) {
		this.concentrationUnit = concentrationUnit;
	}
	public String getRateUnit() {
		return rateUnit;
	}
	public void setRateUnit(String rateUnit) {
		this.rateUnit = rateUnit;
	}
	public String getTransitiontimeUnit() {
		return transitiontimeUnit;
	}
	public void setTransitiontimeUnit(String transitiontimeUnit) {
		this.transitiontimeUnit = transitiontimeUnit;
	}
	
}
