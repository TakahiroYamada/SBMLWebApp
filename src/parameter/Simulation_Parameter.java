package parameter;

public class Simulation_Parameter extends Abstract_Parameter{
	private Integer endTime;
	private Integer numTime;
	public Integer getEndTime() {
		return endTime;
	}
	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}
	public void setNumTime(Integer numTime) {
		this.numTime = numTime;
	}
	public Integer getNumTime() {
		return numTime;
	}	
}
