package beans_modelviewer;

import java.util.HashMap;

import org.sbml.jsbml.Species;

public class SBMLModelViewer_SpeciesBeans extends SBMLModelViewer_ComponentBeans {
	public SBMLModelViewer_SpeciesBeans( Species s) {
		this.data = new HashMap<>();
		this.group = "nodes";
		
		data.put( "id" , s.getId() );
		if( ! s.getName().equals("")){
			data.put("name", s.getName() );
		}
		else{
			data.put("name", s.getId());
		}
	} 
}
