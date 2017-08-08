package servlet;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import java.util.logging.Logger;

import javax.naming.ConfigurationException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.COPASI.CTimeSeries;
import org.COPASI.SWIGTYPE_p_CMath__SimulationType;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.sbml.jsbml.validator.SyntaxChecker;

import analyze.simulation.Simulation_COPASI;
import analyze.simulation.Simulation_SBSCL;
import beans.modelparameter.ModelParameter_Beans;
import beans.simulation.Simulation_AllBeans;
import beans.simulation.Simulation_DatasetsBeans;
import beans.simulation.Simulation_XYDataBeans;
import coloring.Coloring;
import general.unique_id.UniqueId;
import manipulator.SBML_Manipulator;
import net.arnx.jsonic.JSON;
import parameter.Simulation_Parameter;

/**
 * Servlet implementation class simulation_servlet
 */

public class Simulation_Servlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(Simulation_Servlet.class.getName());
	private static final long serialVersionUID = 1L;
	private String sessionId;
	private String path;
    private String filename;
    private File newFile;
    private Simulation_AllBeans simulationBeans;
    private Simulation_Parameter param;
    private ModelParameter_Beans sbmlParam;
    private Coloring colorOfVis;
	/**
	 * Method called on post.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Simulation_Servlet.doPost()");
		sessionId = UniqueId.getUniqueId();
		path = getServletContext().getRealPath("/tmp/" + sessionId);
		//path = getServletContext().getRealPath("/tmp");
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload( factory );
		// Save the SBML file in server side directory
		configureAnalysisEmviroment( request , upload );
		// Get and edit parameters value in SBML model
		SBML_Manipulator sbml_Manipulator = new SBML_Manipulator( newFile );
		sbml_Manipulator.editModelParameter( this.sbmlParam );
		if( param.getLibrary().equals("copasi")){
			try{
				Simulation_COPASI simCOPASI = new Simulation_COPASI( newFile.getPath() , param);
				colorOfVis = new Coloring( (int) (simCOPASI.getTimeSeries().getNumVariables() - 1) , 1.0);
				
				//TimeSeries data contains the following data structure:
				//Title : the ID of each species
				//Data : the amount of each species and this is indicated by intended number of time and variables
				simCOPASI.getTimeSeries().save( path + "/result.csv" , false , ",");
				this.simulationBeans = simCOPASI.configureSimulationBeans( colorOfVis );
				this.simulationBeans.setSessionId( this.sessionId);
				
			} catch( NullPointerException e){
				e.printStackTrace();
			}
		}
		else if( param.getLibrary().equals("simulationcore")){
			// TODO: implement
			Simulation_SBSCL simSBSCL = new Simulation_SBSCL( newFile.getPath(), param );
			colorOfVis = new Coloring( (int) simSBSCL.getTimeSeries().getColumnCount() , 1.0 );
			this.simulationBeans = simSBSCL.configureSimulationBeans( colorOfVis );
			this.simulationBeans.setSessionId( this.sessionId );
		}
		
		
		// add the units of each species
		sbml_Manipulator.addUnitForEachSpecies( this.simulationBeans );
		
		this.simulationBeans.setModelParameters( sbml_Manipulator.getModelParameter() );
		String jsonSimulation = JSON.encode( this.simulationBeans , true  );
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print( jsonSimulation );
		
		//Following code is future deleted
		
		//for( int i = 0 ; i < simCOPASI.getTimeSeries().getNumVariables() ; i ++){
		//	System.out.print( simCOPASI.getTimeSeries().getTitle( i ) +"\t");
		//}
		//System.out.println();
		//for( int i = 0 ; i < simCOPASI.getTimeSeries().getRecordedSteps() ; i ++){
		//	for( int j = 0 ; j < simCOPASI.getTimeSeries().getNumVariables() ; j ++){
		//		System.out.print( simCOPASI.getTimeSeries().getConcentrationData( i , j ) + "\t");
		//	}
		//	System.out.println();
		//}
		
		//response.setHeader("Content-Disposition", "attachment; filename=result.csv");
		//ServletContext ctx = getServletContext();
		//InputStream is = ctx.getResourceAsStream("/tmp/result.csv");
		//int read = 0;
		//byte[] bytes = new byte[ 1024 ];
		//OutputStream os = response.getOutputStream();
		//while(( read = is.read(bytes)) != -1 ){
		//	os.write( bytes , 0 , read);
		//}
		//os.flush();
		//os.close();
	}
	private void configureAnalysisEmviroment( HttpServletRequest request , ServletFileUpload upload  ) {
		// TODO Auto-generated method stub
		this.newFile = null;
		this.param = new Simulation_Parameter();
		
		try {
			List<FileItem> fields = upload.parseRequest( request );
			Iterator< FileItem > it = fields.iterator();
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
				// Parameter to analyze is set
				else if( item.getFieldName().equals("endpoint")){
					param.setEndTime( new Integer( Integer.parseInt( item.getString() )));
				}
				else if( item.getFieldName().equals("numpoint")){
					param.setNumTime( new Integer( Integer.parseInt( item.getString() )));;
				}
				else if( item.getFieldName().equals( "tolerance")){
					param.setTolerance( new Double( Double.parseDouble( item.getString() )));
				}
				else if( item.getFieldName().equals("library")){
					param.setLibrary( item.getString() );
				}
				else if( item.getFieldName().equals("parameter")){
					this.sbmlParam = JSON.decode( item.getString() , ModelParameter_Beans.class);
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}
}
