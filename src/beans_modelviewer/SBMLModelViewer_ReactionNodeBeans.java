package beans_modelviewer;

import java.util.HashMap;

import org.sbml.jsbml.Reaction;

public class SBMLModelViewer_ReactionNodeBeans extends SBMLModelViewer_ComponentBeans {
	private String classes;
	public SBMLModelViewer_ReactionNodeBeans( Reaction r){
		this.data = new HashMap<>();
		this.group = "nodes";
		this.classes = "rxn";
		
		data.put( "id", r.getId() );
		if( !r.getName().equals("")){
			data.put("name", r.getName() );
		}
		else{
			data.put( "name", r.getId());
		}
	}
	public String getClasses() {
		return classes;
	}
	public void setClasses(String classes) {
		this.classes = classes;
	}
}
