package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.xstream.alias.ClassMapper.Null;

import beans.biomodels.BioModelsInfo_Beans;
import ij.gui.Line;
import net.arnx.jsonic.JSON;
import uk.ac.ebi.biomodels.ws.BioModelsWSClient;
import uk.ac.ebi.biomodels.ws.BioModelsWSException;
import uk.ac.ebi.biomodels.ws.SimpleModel;

/**
 * Servlet implementation class BioModels_ModelExtraction
 */
public class BioModels_ModelExtraction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BioModelsInfo_Beans bioModelsInfo_Beans;
	private String[] allModelId;
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String path = getServletContext().getRealPath("/tmp");
		bioModelsInfo_Beans = new BioModelsInfo_Beans();
		File biomodelsFile = new File( path + "/BioModels_AllModelIDName.txt");
		ArrayList<String> listModelId = new ArrayList<>();
		ArrayList<String> listModelName = new ArrayList<>();
		
		String line;
		try{
			BufferedReader br = new BufferedReader( new FileReader( biomodelsFile ));
			while((line = br.readLine() ) != null ){
				String data[] = line.split(",", 0);
				listModelId.add( data[ 0 ]);
				listModelName.add( data[ 1 ]);
			}	
		}catch( FileNotFoundException e ){
			response.setStatus( 400 );
			return;
		}
		bioModelsInfo_Beans.setBiomodels_id( listModelId.toArray( new String[ 0 ]));
		bioModelsInfo_Beans.setBiomodels_name( listModelName.toArray( new String[ 0 ]));
		
		String jsonBioModels = JSON.encode( bioModelsInfo_Beans , true);
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print( jsonBioModels );
	}
}
