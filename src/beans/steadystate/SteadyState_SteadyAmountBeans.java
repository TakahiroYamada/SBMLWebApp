package beans.steadystate;

import java.util.jar.Attributes.Name;

public class SteadyState_SteadyAmountBeans {
	private int id;
	private String name;
	private String type;
	private double concentration;
	private double rate;
	private double transition;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getConcentration() {
		return concentration;
	}
	public void setConcentration(double concentration) {
		this.concentration = concentration;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public double getTransition() {
		return transition;
	}
	public void setTransition(double transition) {
		this.transition = transition;
	}
}
