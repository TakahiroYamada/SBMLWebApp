package beans.simulation;

import javax.swing.Box.Filler;

public class Simulation_DatasetsBeans {
	//Following name of variable will be used as JSON key data
	private String label;
	private Simulation_XYDataBeans data[];
	private String type;
	private boolean showLine;
	private boolean fill;
	private String backgroundColor;
	private String borderColor;
	private String pointBorderColor;
	private String pointBackgroundColor;
	private int borderWidth;
	private String pointStyle;
	public Simulation_DatasetsBeans(){
		this.fill = false;
		this.backgroundColor = "rgba(179,181,198,0.2)";
		this.borderColor = "rgba(179,181,198,1)";
		this.pointBorderColor = "rgba(179,181,198,1)";
		this.pointBackgroundColor = "#fff";
		this.borderWidth = 3;
		this.type = "scatter";
		this.showLine = true;
		this.pointStyle = "circle";
	}
	public String getPointStyle() {
		return pointStyle;
	}
	public void setPointStyle(String pointStyle) {
		this.pointStyle = pointStyle;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public boolean getFill() {
		return fill;
	}
	public void setFill(boolean fill) {
		this.fill = fill;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public String getBorderColor() {
		return borderColor;
	}
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}
	public String getPointBorderColor() {
		return pointBorderColor;
	}
	public void setPointBorderColor(String pointBorderColor) {
		this.pointBorderColor = pointBorderColor;
	}
	public String getPointBackgroundColor() {
		return pointBackgroundColor;
	}
	public void setPointBackgroundColor(String pointBackgroundColor) {
		this.pointBackgroundColor = pointBackgroundColor;
	}
	public int getBorderWidth() {
		return borderWidth;
	}
	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}
	public Simulation_XYDataBeans[] getData() {
		return data;
	}
	public void setData(Simulation_XYDataBeans[] data) {
		this.data = data;
	}
	public String getType(){
		return type;
	}
	public void setType( String type){
		this.type = type;
	}
	public boolean getShowLine(){
		return showLine;
	}
	public void setShowLine( boolean showLine){
		this.showLine = showLine;
	}
}
