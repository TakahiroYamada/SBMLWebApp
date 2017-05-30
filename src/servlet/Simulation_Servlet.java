package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.COPASI.CTimeSeries;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import analyze.simulation.Simulation_COPASI;
import beans.simulation.Simulation_AllBeans;
import beans.simulation.Simulation_DatasetsBeans;
import beans.simulation.Simulation_XYDataBeans;
import net.arnx.jsonic.JSON;

/**
 * Servlet implementation class simulation_servlet
 */
public class Simulation_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String filename;   
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub 
		String path = getServletContext().getRealPath("/tmp");
		//response.setContentType("text/csv");
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload( factory );
		
		try {
			List<FileItem> fields = upload.parseRequest( request );
			Iterator< FileItem > it = fields.iterator();
			while( it.hasNext()){
				FileItem item = it.next();
				if(!item.isFormField()){
					filename = item.getName();
					File newFile = new File( path + "/" + filename);
					File tmpDir = new File( path );
					tmpDir.mkdir();
					try {
						item.write( newFile );
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Simulation_COPASI simCOPASI = new Simulation_COPASI( newFile.getPath());
					//TimeSeries data contais the following data structure:
					//Title : the ID of each species
					//Data : the amout of each species and this is indicated by intended number of time and variables
					simCOPASI.getTimeSeries().save( path + "/result.csv" , false , ",");
					
					Simulation_AllBeans simulationBeans = configureSimulationBeans( simCOPASI.getTimeSeries() );
					String jsonSimulation = JSON.encode( simulationBeans );
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
			}
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}
	//Following code sum up with the Beans of JSONIC and the return value will be encoded as JSON format and responsed to Client side.
	private Simulation_AllBeans configureSimulationBeans(CTimeSeries timeSeries) {
		long numOfSpecies = timeSeries.getNumVariables();
		long numOfTimePoints = timeSeries.getRecordedSteps();
		double maxCandidate = 0.0;
		Simulation_AllBeans simAllBeans = new Simulation_AllBeans();
		Simulation_DatasetsBeans allDataSets[] = new Simulation_DatasetsBeans[ (int) (numOfSpecies - 1)];
		//i == 0 means the value of time point! this is considered as the value of x axis!
		for( int i = 1 ; i < numOfSpecies ; i ++ ){
			allDataSets[ i - 1 ] = new Simulation_DatasetsBeans();
			allDataSets[ i - 1 ].setLabel( timeSeries.getTitle( i ));
			
			Simulation_XYDataBeans allXYDataBeans[] = new Simulation_XYDataBeans[ (int) numOfTimePoints ];
			for( int j = 0 ; j < numOfTimePoints ; j ++){
				allXYDataBeans[ j ] = new Simulation_XYDataBeans();
				allXYDataBeans[ j ].setX( timeSeries.getConcentrationData( j , 0));
				allXYDataBeans[ j ].setY( timeSeries.getConcentrationData( j, i ));
				if( maxCandidate < timeSeries.getConcentrationData( j , i)){
					maxCandidate = timeSeries.getConcentrationData( j , i );
				}
			}
			allDataSets[ i - 1 ].setData( allXYDataBeans );
		}
		
		simAllBeans.setData( allDataSets );
		simAllBeans.setXmax( timeSeries.getData( numOfTimePoints - 1 , 0));
		simAllBeans.setYmax( maxCandidate );
		return simAllBeans;
	}
}
