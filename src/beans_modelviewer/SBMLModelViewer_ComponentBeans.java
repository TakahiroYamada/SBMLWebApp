package beans_modelviewer;

import java.util.HashMap;

public class SBMLModelViewer_ComponentBeans {
	protected HashMap< String, String> data;
	protected String group;
	public HashMap<String, String> getData() {
		return data;
	}
	public void setData(HashMap<String, String> data) {
		this.data = data;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
}
