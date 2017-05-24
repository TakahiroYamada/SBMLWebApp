package beans.simulation;

public class Simulation_AllBeans {
	private double Xmax;
	private double Ymax;
	private Simulation_DatasetsBeans data[];
	public double getXmax() {
		return Xmax;
	}
	public void setXmax(double xmax) {
		Xmax = xmax;
	}
	public double getYmax() {
		return Ymax;
	}
	public void setYmax(double ymax) {
		Ymax = ymax;
	}
	public Simulation_DatasetsBeans[] getData() {
		return data;
	}
	public void setData(Simulation_DatasetsBeans[] data) {
		this.data = data;
	}
	
}
