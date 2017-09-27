package servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.biomodels.BioModelsInfo_Beans;
import net.arnx.jsonic.JSON;
import uk.ac.ebi.biomodels.ws.BioModelsWSClient;
import uk.ac.ebi.biomodels.ws.BioModelsWSException;
import uk.ac.ebi.biomodels.ws.SimpleModel;

/**
 * Servlet implementation class BioModels_ModelRefresh
 */
public class BioModels_ModelRefresh extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BioModelsInfo_Beans bioModelsInfo_Beans;
	private String path;
    private String[] allModelId;
    private String[] allModelName;
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		BioModelsWSClient client = new BioModelsWSClient();
		bioModelsInfo_Beans = new BioModelsInfo_Beans();
		this.path = getServletContext().getRealPath("/tmp");
		File tmpDir = new File( this.path );
		tmpDir.mkdir();
		
		try {
			this.allModelId = client.getAllCuratedModelsId();
			this.allModelName = getAllCuratedModelName( client );
		} catch (BioModelsWSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		try{
			saveAllCuratedModel();
		} catch( FileNotFoundException e){
			e.printStackTrace();
		}
		bioModelsInfo_Beans.setBiomodels_id( this.allModelId );
		bioModelsInfo_Beans.setBiomodels_name( this.allModelName );
		String jsonBioModels = JSON.encode( bioModelsInfo_Beans , true);
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print( jsonBioModels );
	}
	private String[] getAllCuratedModelName(BioModelsWSClient client) throws BioModelsWSException {
		String[] allModelName = new String[ allModelId.length ];
		List<SimpleModel> resultSet = client.getSimpleModelsByIds( allModelId );
		for( int i = 0 ; i < resultSet.size() ; i ++){
			allModelName[ i ] = resultSet.get( i ).getName();
		}
		return allModelName;
	}
	private void saveAllCuratedModel() throws FileNotFoundException{
		File file = new File(this.path + "/BioModels_AllModelIDName.txt");
		PrintWriter writer = new PrintWriter( file );
		for( int i = 0 ; i < allModelId.length ; i ++){
			writer.println( allModelId[ i ] + "," + allModelName[ i ]);
		}
		
	}
}
