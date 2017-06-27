package beans.modelparameter;

public class InitialValue_Beans {
	public static int INIT_CONCENTRATION = 0;
	public static int INIT_AMOUNT = 0;
	
	private String sbmlID;
	private double initialValue;
	private int status;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getSbmlID() {
		return sbmlID;
	}
	public void setSbmlID(String sbmlID) {
		this.sbmlID = sbmlID;
	}
	public double getInitialValue() {
		return initialValue;
	}
	public void setInitialValue(double initialValue) {
		this.initialValue = initialValue;
	}
	
}
