package beans.modelparameter;

public class Parameters_Beans {
	private String sbmlID;
	private String sbmlName;
	private double parameterValue;
	private Double lower;
	private Double upper;
	public String getSbmlName() {
		return sbmlName;
	}
	public void setSbmlName(String sbmlName) {
		this.sbmlName = sbmlName;
	}
	public String getSbmlID() {
		return sbmlID;
	}
	public void setSbmlID(String sbmlID) {
		this.sbmlID = sbmlID;
	}
	public double getParameterValue() {
		return parameterValue;
	}
	public void setParameterValue(double parameterValue) {
		this.parameterValue = parameterValue;
	}
	public Double getLower() {
		return lower;
	}
	public void setLower(Double lower) {
		this.lower = lower;
	}
	public Double getUpper() {
		return upper;
	}
	public void setUpper(Double upper) {
		this.upper = upper;
	}
}
