/**
* @author Akira Funahashi <funa@symbio.jst.go.jp>
* @author Takahiro G. Yamada <yamada@fun.bio.keio.ac.jp>
*/


package servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.biomodels.BioModelsInfo_Beans;
import database.BioModelsConnectionService;
import database.biomodels.CuratedModelsResponse;
import database.biomodels.ModelSummary;
import net.arnx.jsonic.JSON;
import uk.ac.ebi.biomodels.ws.BioModelsWSClient;

/**
 * Servlet implementation class BioModels_ModelRefresh
 */
public class BioModels_ModelRefresh extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BioModelsInfo_Beans bioModelsInfo_Beans;
	private String path;
	private List<String> allModelId;
	private List<String> allModelName;
    //private String[] allModelId;
    //private String[] allModelName;
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
		
		try (BioModelsConnectionService bioModelsService = new BioModelsConnectionService()){
			CuratedModelsResponse modelResponse = bioModelsService.getCuratedModelSet();
			Set<ModelSummary> modelSet = modelResponse.getModels();
			this.allModelId = new ArrayList<String>();
			this.allModelName = new ArrayList<String>();
			for( Iterator<ModelSummary> it = modelSet.iterator() ; it.hasNext();){
				ModelSummary modelSummary = it.next();
				System.out.println(modelSummary.getId() + " : " + modelSummary.getName());
				this.allModelId.add( modelSummary.getId() );
				this.allModelName.add( modelSummary.getName());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			saveAllCuratedModel();
		} catch( FileNotFoundException e){
			e.printStackTrace();
		}
		bioModelsInfo_Beans.setBiomodels_id( this.allModelId.toArray( new String[this.allModelId.size()]) );
		bioModelsInfo_Beans.setBiomodels_name( this.allModelName.toArray( new String[ this.allModelName.size()]) );
		String jsonBioModels = JSON.encode( bioModelsInfo_Beans , true);
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print( jsonBioModels );
	}
	private void saveAllCuratedModel() throws FileNotFoundException{
		File file = new File(this.path + "/BioModels_AllModelIDName.txt");
		PrintWriter writer = new PrintWriter( file );
		for( int i = 0 ; i < allModelId.size() ; i ++){
			writer.println( allModelId.get( i ) + "," + allModelName.get( i ));
		}
		writer.close();
	}
}
