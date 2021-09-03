package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.expdata.ExampleModel_ExpData_Beans;
import net.arnx.jsonic.JSON;

/**
 * Servlet implementation class ExampleModel_ExpDataExtraction
 */
public class ExampleModel_ExpDataExtraction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ExampleModel_ExpData_Beans expData_Beans;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = getServletContext().getRealPath("/example");
		this.expData_Beans = new ExampleModel_ExpData_Beans();
		this.expData_Beans.setExpFileName("SBMLSampleModel_ExperimentData");
		
		byte[] encoded = Files.readAllBytes( Paths.get( path + "/Experiment/SBMLSampleModel_ExperimentData.csv"));
		this.expData_Beans.setExpData( new String( encoded , StandardCharsets.US_ASCII));
		
		String jsonExampleData = JSON.encode( this.expData_Beans , true );
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print( jsonExampleData );
		
	}

}
