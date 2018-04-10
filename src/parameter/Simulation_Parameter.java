package parameter;

public class Simulation_Parameter extends Abstract_Parameter{
	private Double endTime;
	private Integer numTime;
	private Double tolerance;
	public Simulation_Parameter(){
		this.endTime = 100.0;
		this.numTime = 100;
		this.tolerance = 1.0e-12;
		super.setLibrary("copasi");
	}
	public Double getEndTime() {
		return endTime;
	}
	public void setEndTime(Double endTime) {
		this.endTime = endTime;
	}
	public void setNumTime(Integer numTime) {
		this.numTime = numTime;
	}
	public Integer getNumTime() {
		return numTime;
	}	
	public Double getTolerance() {
		return tolerance;
	}
	public void setTolerance(Double tolerance) {
		this.tolerance = tolerance;
	}
	public void setMethod(String methodName) {
		// if the other method to analyze is added. You can add following		
	}
}
