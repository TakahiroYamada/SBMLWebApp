package parameter;

public class SteadyStateAnalysis_Parameter extends Abstract_Parameter{
	private double resolution;
	private double derivation_factor;
	private int iterationLimit;
	public double getResolution() {
		return resolution;
	}
	public void setResolution(double resolution) {
		this.resolution = resolution;
	}
	public double getDerivation_factor() {
		return derivation_factor;
	}
	public void setDerivation_factor(double derivation_factor) {
		this.derivation_factor = derivation_factor;
	}
	public int getIterationLimit() {
		return iterationLimit;
	}
	public void setIterationLimit(int iterationLimit) {
		this.iterationLimit = iterationLimit;
	}
	@Override
	public void setMethod(String methodName) {
		// if the other method to analyze is added. You can add following				
	}
	
}
