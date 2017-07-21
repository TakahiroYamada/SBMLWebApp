package servlet;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import analyze.parameter.ParameterEstimation_COPASI;
import analyze.simulation.Simulation_COPASI;
import beans.parameter.ParameterEstimation_AllBeans;
import coloring.Coloring;
import net.arnx.jsonic.JSON;
import parameter.ParameterEstimation_Parameter;
import parameter.Simulation_Parameter;

/**
 * Servlet implementation class ParameterEstimation_Servlet
 */
public class ParameterEstimation_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String path;
	private File SBMLFile;
	private File ExperimentFile;
	private ParameterEstimation_AllBeans paramBeans;
	private ParameterEstimation_Parameter paramestParam;
	private Simulation_Parameter paramSim;
	private Coloring colorOfVis;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//HttpSession session = request.getSession( true );
		//path = getServletContext().getRealPath("/tmp/" + session.getId() );
		path = getServletContext().getRealPath("/tmp");
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload( factory );
		paramBeans = new ParameterEstimation_AllBeans();
			
		configureAnalysisEmvironment(request , upload);
			
		//COPASI ParameterEstimation Execution
		ParameterEstimation_COPASI paramEstCopasi = new ParameterEstimation_COPASI( paramestParam , SBMLFile, ExperimentFile);
		paramEstCopasi.estimateParameter();
		
		// The simulation in order to visualize in Client side using parameters before parameter fitting
		simulateFittedResult( paramEstCopasi );
		
		//Experiment data is set to beans
		paramBeans.setExpDataSets( paramEstCopasi.configureParamEstBeans( colorOfVis ) );
		paramBeans.setUpdateParam( paramEstCopasi.configureParameterUpdateInformationBeans() );
		// response to client side sending JSON format data
		String jsonParamEst = JSON.encode( paramBeans , true);
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print( jsonParamEst );
	}
	private void simulateFittedResult( ParameterEstimation_COPASI paramEstCopasi) {
		//Simulation condition is set
		int endTime =  (int) Math.ceil( paramEstCopasi.getTimeData().get( paramEstCopasi.getTimeData().size() -1 ));
		this.paramSim = new Simulation_Parameter();
		this.paramSim.setLibrary("copasi");
		this.paramSim.setNumTime( endTime );
		this.paramSim.setEndTime( endTime );
		
		colorOfVis = new Coloring( (int) paramEstCopasi.getDependentData().size(), 1.0);
		// Simulation execution using parameters before and after fitting
		Simulation_COPASI beforeFitting = new Simulation_COPASI( SBMLFile.getPath() , paramSim);
		paramBeans.setBeforeFitting( beforeFitting.configureSimulationBeans( colorOfVis ) );
		Simulation_COPASI afterFitting = new Simulation_COPASI( paramEstCopasi.getDataModel() , paramSim);
		paramBeans.setAfterFitting( afterFitting.configureSimulationBeans( colorOfVis ));
	}
	private void configureAnalysisEmvironment(HttpServletRequest request, ServletFileUpload upload) {
		// TODO Auto-generated method stub
		paramestParam = new ParameterEstimation_Parameter();
		
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
				else if( item.getFieldName().equals("algorithm")){
					paramestParam.setMethod( item.getString() );
				}
				// If Leven-Berg , Nelder or Particle Swarm is selected
				else if( item.getFieldName().equals("itermax")){
					paramestParam.setIteLimit( Integer.parseInt( item.getString()));
				}
				else if( item.getFieldName().equals("tolerance")){
					paramestParam.setTolerance( Double.parseDouble(item.getString()));
				}
				// If GA is selected
				else if( item.getFieldName().equals("generation")){
					paramestParam.setNumGenerations( Integer.parseInt( item.getString()));
				}
				else if( item.getFieldName().equals("population")){
					paramestParam.setPopSize( Integer.parseInt( item.getString()));
				}
				// If Particle Swarm is selected
				else if( item.getFieldName().equals("swarmsize")){
					paramestParam.setSwarmSize( Integer.parseInt( item.getString()));
				}
				else if( item.getFieldName().equals("stdDeviation")){
					paramestParam.setStdDeviation( Double.parseDouble( item.getString() ));
				}
				else if( item.getFieldName().equals("randomNumGenerator")){
					paramestParam.setRandomNumGenerator( Integer.parseInt( item.getString() ));
				}
				else if( item.getFieldName().equals("seed")){
					paramestParam.setSeed( Integer.parseInt( item.getString() ));
				}
			}
		}catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
