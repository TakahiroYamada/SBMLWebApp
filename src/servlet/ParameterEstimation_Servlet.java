package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import analize.parameter.ParameterEstimation_COPASI;

/**
 * Servlet implementation class ParameterEstimation_Servlet
 */
public class ParameterEstimation_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private File SBMLFile;
	private File ExperimentFile;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String path = getServletContext().getRealPath("/tmp");
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload( factory );
		try {
			List<FileItem> fields = upload.parseRequest( request );
			Iterator< FileItem > it = fields.iterator();
			while( it.hasNext() ){
				FileItem item = it.next();
				// SBML Model file is inputed
				if( item.getFieldName().equals("SBMLFile")){
					String filename = item.getName();
					SBMLFile = new File( path + "/" + filename);
					File tmpDir = new File( path );
					tmpDir.mkdir();
					try {
						item.write( SBMLFile );
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				// Experiment Data file is inputed
				else if( item.getFieldName().equals( "ExpFile")){
					String filename = item.getName();
					this.ExperimentFile = new File( path + "/" + filename);
					try {
						item.write( ExperimentFile );
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			//COPASI ParameterEstimation Execution
			ParameterEstimation_COPASI paramEstCopasi = new ParameterEstimation_COPASI( SBMLFile, ExperimentFile);
			paramEstCopasi.estimateParameter();
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
