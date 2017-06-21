package beans.steadystate;

public class SteadyState_AllBeans {
	private SteadyState_SteadyAmountBeans steadyAmount[];
	private SteadyState_JacobianBeans steadyJacobian;
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
	
}
