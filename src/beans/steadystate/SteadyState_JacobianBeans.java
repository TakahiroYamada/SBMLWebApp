package beans.steadystate;

import java.util.HashMap;

public class SteadyState_JacobianBeans {
	private SteadyState_JacobianColumnBeans[] columns;
	private HashMap< String, String>[] jacob_Amount;
	public HashMap<String, String>[] getJacob_Amount() {
		return jacob_Amount;
	}

	public void setJacob_Amount(HashMap<String, String>[] jacob_Amount) {
		this.jacob_Amount = jacob_Amount;
	}

	public SteadyState_JacobianColumnBeans[] getColumns() {
		return columns;
	}

	public void setColumns(SteadyState_JacobianColumnBeans[] columns) {
		this.columns = columns;
	}
}
