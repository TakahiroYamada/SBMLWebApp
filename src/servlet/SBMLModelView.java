package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
import general.unique_id.UniqueId;
import net.arnx.jsonic.JSON;

/**
 * Servlet implementation class SBMLModelView
 */
public class SBMLModelView extends HttpServlet {
	private static final Logger logger = Logger.getLogger( SBMLModelView.class.getName() );
	private static final long serialVersionUID = 1L;
	private SBMLModelViewer_AllBeans allData;
	private String sessionId;
	private List<FileItem> fields;
	private String path;
	private String filename;
	private File newFile;
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		logger.info("SBMLModelView.doPost()");
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload( factory );
		
		// Checking session has been already opend or not.
		try {
			sessionCheck( request , upload );
		} catch (FileUploadException e) {
			response.setStatus( 400 );
			PrintWriter out = response.getWriter();
			out.print( e.getMessage() );
			out.flush();
			e.printStackTrace();
			return;
		}
		if( sessionId.equals("")){
			sessionId = UniqueId.getUniqueId();
		}
		path = getServletContext().getRealPath("/tmp/" + sessionId);
		// Save the SBML file in server side directory
		configureAnalysisEmviroment( request , upload );
		try {
			addSBMLObjects( this.newFile.getPath() );
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		this.allData.setSessionId( sessionId );
		String jsonModelViewer = JSON.encode( this.allData , true );
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print( jsonModelViewer );
	}
	
	private void addSBMLObjects(String filepath) throws XMLStreamException, IOException {
		this.allData = new SBMLModelViewer_AllBeans();
		SBMLDocument d = SBMLReader.read( new File( filepath ) );
		Model m = d.getModel();
		addSpecies( m );
		addReactions( m );
	}

	private void addReactions(Model m) {
		for( Reaction r : m.getListOfReactions() ){
			SBMLModelViewer_ReactionNodeBeans tmpRN = new SBMLModelViewer_ReactionNodeBeans( r );
			this.allData.getAll().add( tmpRN );
			
			for( SpeciesReference s : r.getListOfReactants() ){
				SBMLModelViewer_ReactionBeans tmpReaction = new SBMLModelViewer_ReactionBeans("reactant");
				tmpReaction.getData().put("id", s.getSpecies() + "_" + r.getId() );
				tmpReaction.getData().put("source", s.getSpecies() );
				tmpReaction.getData().put("target", r.getId() );
				this.allData.getAll().add( tmpReaction );
			}
			
			for( SpeciesReference s : r.getListOfProducts() ){
				SBMLModelViewer_ReactionBeans tmpReaction = new SBMLModelViewer_ReactionBeans("product");
				tmpReaction.getData().put("id", r.getId() + "_" + s.getSpecies() );
				tmpReaction.getData().put("source", r.getId() );
				tmpReaction.getData().put("target", s.getSpecies() );
				this.allData.getAll().add( tmpReaction );
			}
			
			for( ModifierSpeciesReference s : r.getListOfModifiers() ){
				SBMLModelViewer_ReactionBeans tmpReaction = new SBMLModelViewer_ReactionBeans("activation");
				tmpReaction.getData().put("id", s.getSpecies() + "_" + r.getId() );
				tmpReaction.getData().put("source", s.getSpecies() );
				tmpReaction.getData().put("target", r.getId() );
				this.allData.getAll().add( tmpReaction );
			}
		}
	}

	private void addSpecies(Model m) {
		for( Species s : m.getListOfSpecies() ){
			SBMLModelViewer_SpeciesBeans tmpS = new SBMLModelViewer_SpeciesBeans( s );
			this.allData.getAll().add( tmpS );
		}
	}

	private void sessionCheck(HttpServletRequest request, ServletFileUpload upload) throws FileUploadException {

		this.fields = upload.parseRequest(request);
		Iterator<FileItem> it = fields.iterator();
		while (it.hasNext()) {
			FileItem item = it.next();
			if (item.getFieldName().equals("SessionId")) {
				sessionId = item.getString();
			}
		}

	}
	private void configureAnalysisEmviroment( HttpServletRequest request , ServletFileUpload upload  ) {
		// TODO Auto-generated method stub
		this.newFile = null;
		Iterator< FileItem > it = this.fields.iterator();
		while( it.hasNext()){
			FileItem item = it.next();

			// SBML model file is got.
			if(item.getFieldName().equals("file")){
				filename = item.getName();
				newFile = new File( path + "/" + filename);
				File tmpDir = new File( path );
				tmpDir.mkdirs();
				try {
					item.write( newFile );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
