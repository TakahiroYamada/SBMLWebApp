package parameter;

import org.COPASI.CCopasiDataModel;
import org.COPASI.CTaskEnum;
import org.apache.commons.math.stat.descriptive.StatisticalMultivariateSummary;

public class ParameterEstimation_Parameter extends Abstract_Parameter{
	
	
	// The parameters for Leven berg and Nelder method
	private int iteLimit;
	private double tolerance;
	
	// The parameters for GA and  differential evolution
	private int numGenerations;
	private int popSize;
	
	// The parameters for particle swarm optimization
	private int swarmSize;
	private double stdDeviation;
	
	// The parameters for particle swarm optimization and differential evolution
	private int randomNumGenerator;
	public int getSwarmSize() {
		return swarmSize;
	}
	public void setSwarmSize(int swarmSize) {
		this.swarmSize = swarmSize;
	}
	public double getStdDeviation() {
		return stdDeviation;
	}
	public void setStdDeviation(double stdDeviation) {
		this.stdDeviation = stdDeviation;
	}
	public int getRandomNumGenerator() {
		return randomNumGenerator;
	}
	public void setRandomNumGenerator(int randomNumGenerator) {
		this.randomNumGenerator = randomNumGenerator;
	}
	public int getSeed() {
		return Seed;
	}
	public void setSeed(int seed) {
		Seed = seed;
	}
	private int Seed;
	
	// The parameters for 
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
	@Override
	public void setMethod(String methodName) {
		// TODO Auto-generated method stub
		if( methodName.equals("lv")){
			this.Method = CTaskEnum.LevenbergMarquardt;
		}
		else if( methodName.equals("ga")){
			this.Method = CTaskEnum.GeneticAlgorithm;
		}
		else if( methodName.equals("nelder")){
			this.Method = CTaskEnum.NelderMead;
		}
		else if( methodName.equals("particleSwarm")){
			this.Method = CTaskEnum.ParticleSwarm;
		}
		else if( methodName.equals("diffEvol")){
			this.Method = CTaskEnum.DifferentialEvolution;
		}
	}
}
