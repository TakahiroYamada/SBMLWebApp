package parameter;

public class ParameterEstimation_Parameter extends Abstract_Parameter{
	// The parameters for Leven berg and Nelder method
	private int iteLimit;
	private double tolerance;
	
	// The parameters for GA
	private int numGenerations;
	private int popSize;
	
	public int getIteLimit() {
		return iteLimit;
	}
	public void setIteLimit(int iteLimit) {
		this.iteLimit = iteLimit;
	}
	public double getTolerance() {
		return tolerance;
	}
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}
	public int getNumGenerations() {
		return numGenerations;
	}
	public void setNumGenerations(int numGenerations) {
		this.numGenerations = numGenerations;
	}
	public int getPopSize() {
		return popSize;
	}
	public void setPopSize(int popSize) {
		this.popSize = popSize;
	}
}
