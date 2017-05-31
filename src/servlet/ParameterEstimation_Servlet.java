package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.omg.CORBA.PUBLIC_MEMBER;

import analize.parameter.ParameterEstimation_COPASI;
import analyze.simulation.Simulation_COPASI;
import beans.parameter.ParameterEstimation_AllBeans;
import net.arnx.jsonic.JSON;

/**
 * Servlet implementation class ParameterEstimation_Servlet
 */
public class ParameterEstimation_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private File SBMLFile;
	private File ExperimentFile;
	private ParameterEstimation_AllBeans paramBeans;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String path = getServletContext().getRealPath("/tmp");
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload( factory );
		paramBeans = new ParameterEstimation_AllBeans();
		
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
			// The simulation in order to visualize in Client side using parameters before parameter fitting
			Simulation_COPASI beforeFitting = new Simulation_COPASI( SBMLFile.getPath() );
			paramBeans.setBeforeFitting( beforeFitting.configureSimulationBeans() );
			
			//COPASI ParameterEstimation Execution
			ParameterEstimation_COPASI paramEstCopasi = new ParameterEstimation_COPASI( SBMLFile, ExperimentFile);
			paramEstCopasi.estimateParameter();
			
			// The simulation in order to visualize in Client side using parameters after parameter fitting
			
			//Following code can change the model parameter!!!!!! Should be Bug!!
			Simulation_COPASI afterFitting = new Simulation_COPASI( paramEstCopasi.getDataModel() );
			paramBeans.setAfterFitting( afterFitting.configureSimulationBeans());
			
			//Experiment data is set to beans
			paramBeans.setExpDataSets( paramEstCopasi.configureParamEstBeans() );
			
			// response to client side sending JSON format data
			String jsonParamEst = JSON.encode( paramBeans , true);
			response.setContentType("application/json;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print( jsonParamEst );
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
