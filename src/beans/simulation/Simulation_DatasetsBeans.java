package beans.simulation;

public class Simulation_DatasetsBeans {
	//Following name of variable will be used as JSON key data
	private String label;
	private Simulation_XYDataBeans data[];
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Simulation_XYDataBeans[] getData() {
		return data;
	}
	public void setData(Simulation_XYDataBeans[] data) {
		this.data = data;
	}
}
