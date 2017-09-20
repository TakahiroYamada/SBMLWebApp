package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

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
		BioModelsWSClient client = new BioModelsWSClient();
	    /* uncomment when a proxy is needed
	    client.setProperty("http.proxyHost", "your.http.proxy.host");
	    client.setProperty("http.proxyPort", "yourHttpProxyPort");
	    client.setProperty("socks.proxyHost", "your.socks.proxy.host");
	    client.setProperty("socks.proxyPort", "yourSocksProxyPort");
	    */
		bioModelsInfo_Beans = new BioModelsInfo_Beans();
		try {
			bioModelsInfo_Beans.setBiomodels_id( getAllCuratedModelId( client ) );
		} catch (BioModelsWSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			bioModelsInfo_Beans.setBiomodels_name(getAllCuratedModelName( client ));
		} catch (BioModelsWSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String jsonBioModels = JSON.encode( bioModelsInfo_Beans , true);
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print( jsonBioModels );
	}
	private String[] getAllCuratedModelId(BioModelsWSClient client) throws BioModelsWSException {
		this.allModelId = client.getAllCuratedModelsId();
		return allModelId;
	}
	private String[] getAllCuratedModelName(BioModelsWSClient client) throws BioModelsWSException {
		String[] allModelName = new String[ allModelId.length ];
		List<SimpleModel> resultSet = client.getSimpleModelsByIds( allModelId );
		for( int i = 0 ; i < resultSet.size() ; i ++){
			allModelName[ i ] = resultSet.get( i ).getName();
		}
		return allModelName;
	}
}
