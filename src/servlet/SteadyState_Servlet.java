package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
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
import org.sbml.jsbml.SBMLException;

import analyze.steadystate.SteadyState_COPASI;
import beans.steadystate.SteadyState_AllBeans;
import errorcheck.SBML_ErrorCheck;
import general.unique_id.UniqueId;
import manipulator.SBML_Manipulator;
import net.arnx.jsonic.JSON;
import parameter.SteadyStateAnalysis_Parameter;

/**
 * Servlet implementation class SteadyState_Servlet
 */
public class SteadyState_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String sessionId;
	private String path;
	private String filename;
	private List<FileItem> fields;
	private String saveFileName;
	private File analyzeFile;
	private SteadyStateAnalysis_Parameter stedParam;
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload( factory );
		
		sessionCheck( request , upload );
		if( sessionId.equals("")){
			sessionId = UniqueId.getUniqueId();
		}
		path = getServletContext().getRealPath("/tmp/" + sessionId );
		saveFileName =  path + "/result_steadystate.txt";
		configureAnalysisEmvironment( request , upload );
		// check the validity of given SBML model
		//SBML_ErrorCheck errorCheck = new SBML_ErrorCheck( this.analyzeFile.getPath());
		//errorCheck.checkError();
		
		SBML_Manipulator sbml_Manipulator = new SBML_Manipulator( analyzeFile );
		try {
			sbml_Manipulator.writeSBML();
		} catch (SBMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			response.setStatus( 400 );
			PrintWriter out = response.getWriter();
			out.print( "Unable to write SBML output for documents with undefined SBML Level and Version flag.");
			out.flush();
			e.printStackTrace();
			return;
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Execute steady state analysis with COPASI
		if( stedParam.getLibrary().equals("copasi") ){
			SteadyState_COPASI analyzeSteadyState = new SteadyState_COPASI( stedParam , saveFileName , analyzeFile.getPath());
			analyzeSteadyState.executeSteadyStateAnalysis();
			SteadyState_AllBeans stedBeans = analyzeSteadyState.configureSteadyBeans();
			stedBeans.setSessionId( this.sessionId );
			//stedBeans.setWarningText( errorCheck.getErrorMessage() );
			String jsonSteadyState = JSON.encode( stedBeans , true );
			response.setContentType("application/json;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print( jsonSteadyState);
		}
		
		// Output the result. In future these code should be changed to output the result file when the "download button" is pushed in client side.
		// Moreover the visualization of the result is significant. In order to do that, class of beans will be prepared and it will send the result as JSON configuring to client side JavaScript objects.
		
		//response.setHeader("Content-Disposition", "attachment; filename=result_steadystate.txt");
		//ServletContext ctx = getServletContext();
		//InputStream is = ctx.getResourceAsStream( "/tmp/result_steadystate.txt");
		//int read = 0;
		//byte[] bytes = new byte[ 1024 ];
		//OutputStream os = response.getOutputStream();
		//while(( read = is.read(bytes)) != -1 ){
		//	os.write( bytes , 0 , read);
		//}
		//os.flush();
		//os.close();
	}
	private void sessionCheck(HttpServletRequest request, ServletFileUpload upload) {
		try {
			this.fields = upload.parseRequest(request);
			Iterator<FileItem> it = fields.iterator();
			while (it.hasNext()) {
				FileItem item = it.next();
				if (item.getFieldName().equals("SessionId")) {
					sessionId = item.getString();
				}
			}
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void configureAnalysisEmvironment(HttpServletRequest request, ServletFileUpload upload) {

		stedParam = new SteadyStateAnalysis_Parameter();
		Iterator< FileItem > it = this.fields.iterator();
		// FormData from client side is checked.
		while( it.hasNext() ){
			FileItem item = it.next();

			// The SBML model is got and saved as tmp file in order to analyze based on importing this file
			if( item.getFieldName().equals("file")){
				filename = item.getName();
				File tmpDir = new File( path );
				analyzeFile = new File( path + "/" + filename );
				tmpDir.mkdirs();
				try {
					item.write( analyzeFile );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if( item.getFieldName().equals("library")){
				stedParam.setLibrary( item.getString() );
			}
			else if( item.getFieldName().equals("resolution")){
				stedParam.setResolution( Double.parseDouble( item.getString()));
			}
			else if( item.getFieldName().equals("derivation")){
				stedParam.setDerivation_factor( Double.parseDouble( item.getString() ));
			}
			else if( item.getFieldName().equals("itelimit")){
				stedParam.setIterationLimit( Integer.parseInt( item.getString()));
			}
		}
	}

}
