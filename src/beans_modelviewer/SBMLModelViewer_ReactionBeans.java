package beans_modelviewer;

import java.util.HashMap;

public class SBMLModelViewer_ReactionBeans extends SBMLModelViewer_ComponentBeans{
	private String classes;

	public SBMLModelViewer_ReactionBeans( String classes) {
		this.data = new HashMap<>();
		this.group = "edges";
		this.classes = classes;	
	} 
	public String getClasses() {
		return classes;
	}
	public void setClasses(String classes) {
		this.classes = classes;
	}
	
}
