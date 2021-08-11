package task;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;

import beans_modelviewer.SBMLModelViewer_AllBeans;
import beans_modelviewer.SBMLModelViewer_ReactionBeans;
import beans_modelviewer.SBMLModelViewer_ReactionNodeBeans;
import beans_modelviewer.SBMLModelViewer_SpeciesBeans;
import net.arnx.jsonic.JSON;
import parameter.Abstract_Parameter;

public class Task_ModelView extends Super_Task {
	private Abstract_Parameter modelviewParam;
	private SBMLModelViewer_AllBeans modelviewAllBeans;
	public Task_ModelView( String message) throws IOException, XMLStreamException{
		this.modelviewParam = JSON.decode( message , Abstract_Parameter.class );
		super.saveFile( this.modelviewParam.getPathToFile() , this.modelviewParam.getFileName() , this.modelviewParam.getFileString() );
		this.addSBMLObjects( super.newFile.getPath());
		this.modelviewAllBeans.setSessionId( this.modelviewParam.getSessionInfo() );
	}
	private void addSBMLObjects(String filepath) throws XMLStreamException, IOException {
		this.modelviewAllBeans = new SBMLModelViewer_AllBeans();
		SBMLDocument d = SBMLReader.read( new File( filepath ) );
		Model m = d.getModel();
		addSpecies( m );
		addReactions( m );
	}

	private void addReactions(Model m) {
		for( Reaction r : m.getListOfReactions() ){
			SBMLModelViewer_ReactionNodeBeans tmpRN = new SBMLModelViewer_ReactionNodeBeans( r );
			this.modelviewAllBeans.getAll().add( tmpRN );
			
			for( SpeciesReference s : r.getListOfReactants() ){
				SBMLModelViewer_ReactionBeans tmpReaction = new SBMLModelViewer_ReactionBeans("reactant");
				tmpReaction.getData().put("id", s.getSpecies() + "_" + r.getId() );
				tmpReaction.getData().put("source", s.getSpecies() );
				tmpReaction.getData().put("target", r.getId() );
				this.modelviewAllBeans.getAll().add( tmpReaction );
			}
			
			for( SpeciesReference s : r.getListOfProducts() ){
				SBMLModelViewer_ReactionBeans tmpReaction = new SBMLModelViewer_ReactionBeans("product");
				tmpReaction.getData().put("id", r.getId() + "_" + s.getSpecies() );
				tmpReaction.getData().put("source", r.getId() );
				tmpReaction.getData().put("target", s.getSpecies() );
				this.modelviewAllBeans.getAll().add( tmpReaction );
			}
			
			for( ModifierSpeciesReference s : r.getListOfModifiers() ){
				SBMLModelViewer_ReactionBeans tmpReaction = new SBMLModelViewer_ReactionBeans("activation");
				tmpReaction.getData().put("id", s.getSpecies() + "_" + r.getId() );
				tmpReaction.getData().put("source", s.getSpecies() );
				tmpReaction.getData().put("target", r.getId() );
				this.modelviewAllBeans.getAll().add( tmpReaction );
			}
		}
	}

	private void addSpecies(Model m) {
		for( Species s : m.getListOfSpecies() ){
			SBMLModelViewer_SpeciesBeans tmpS = new SBMLModelViewer_SpeciesBeans( s );
			this.modelviewAllBeans.getAll().add( tmpS );
		}
	}
	public SBMLModelViewer_AllBeans getModelviewAllBeans() {
		return modelviewAllBeans;
	}
}
