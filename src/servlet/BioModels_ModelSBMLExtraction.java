package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ebi.biomodels.ws.BioModelsWSClient;
import uk.ac.ebi.biomodels.ws.BioModelsWSException;

/**
 * Servlet implementation class BioModels_ModelSBMLExtraction
 */
public class BioModels_ModelSBMLExtraction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String modelId;
	private String modelSBML;
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BioModelsWSClient client = new BioModelsWSClient();
		this.modelId = request.getParameter("bioModelsId");
		try {
			this.modelSBML = client.getModelSBMLById( modelId );
		} catch (BioModelsWSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print( this.modelSBML );
	}

}
