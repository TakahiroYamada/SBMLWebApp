package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import com.sun.mail.handlers.message_rfc822;

import beans.biomodels.BioModelsInfo_Beans;
import beans.biomodels_sbmlextraction.BioModels_SBMLInfo_Beans;
import general.unique_id.UniqueId;
import net.arnx.jsonic.JSON;
import request.reader.Biomodels_SBMLExtraction_RequestReader;

/**
 * Servlet implementation class ExampleModel_ModelExtraction
 */
public class ExampleModel_ModelExtraction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BioModels_SBMLInfo_Beans exampleModelBeans;
	private List<FileItem> fields;
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = getServletContext().getRealPath("/example");
		
		this.exampleModelBeans = new BioModels_SBMLInfo_Beans();
		this.exampleModelBeans.setModelFileName("SBMLModel_Sample.xml");
		
		byte[] encoded = Files.readAllBytes( Paths.get( path + "/SBMLModel/SBMLModel_Sample.xml"));
		this.exampleModelBeans.setModelString( new String( encoded , StandardCharsets.US_ASCII));
		
		try {
			this.exampleModelBeans.setSessionId( this.sessionCheck( request ));
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		
		String jsonExampleModels = JSON.encode( this.exampleModelBeans , true );
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print( jsonExampleModels );
	}
	private String sessionCheck(HttpServletRequest request) throws FileUploadException {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload( factory);
		this.fields = upload.parseRequest( request );
		Iterator<FileItem> it = fields.iterator();
		while (it.hasNext()) {
			FileItem item = it.next();
			if (item.getFieldName().equals("SessionId")) {
				if( item.getString().equals("")){
					return UniqueId.getUniqueId();
				}
				else{
					return item.getString();
				}
			}
		}		
		return UniqueId.getUniqueId();
	}
	
}
