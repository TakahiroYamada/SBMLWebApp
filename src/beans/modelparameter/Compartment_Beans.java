package beans.modelparameter;

public class Compartment_Beans {
	private String sbmlName;
	private String sbmlID;
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
	public double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}
	private double size;
}
