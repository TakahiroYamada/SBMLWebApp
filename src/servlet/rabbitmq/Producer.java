package servlet.rabbitmq;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import callback.RabbitMQ_CallBack;
import general.error.Error_Message;
import general.task_type.Task_Type;
import general.unique_id.UniqueId;
import net.arnx.jsonic.JSON;
import parameter.Simulation_Parameter;
import request.reader.Biomodels_SBMLExtraction_RequestReader;
import request.reader.ModelView_RequestReader;
import request.reader.ParameterEstimation_RequestReader;
import request.reader.Simulation_RequestReader;
import request.reader.SteadyStateAnalysis_RequestReader;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 * Servlet implementation class Producer
 */
public class Producer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String transferData;
	private String sessionId;
	private int type;
	private List<FileItem> fields;
	private String responseData;
	private static final Logger logger = Logger.getLogger( Producer.class.getName() );
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Producer.doPost()");
		
		// Session is checked. If this connection is first time, new session ID is added.
		try {
			this.sessionId = sessionCheck( request );
		} catch (FileUploadException e) {
			Error_Message error = new Error_Message();
			error.setErrorMessage( e.getMessage() );
			error.setSolveText("Please check your connection");
			
			response.setStatus( 400 );
			PrintWriter out = response.getWriter();
			out.print( JSON.encode( error ));
			out.flush();
			e.printStackTrace();
			return;
		}
		
		// Check the analysis type from Client side
		this.checkAnalysis();
		
		// Summing up the analysis environment to json in order to be sent to RabbitMQ and consumer.
		this.getTransferedJSONData();
		//  Using RPC to get the call back from Consumer
		RabbitMQ_CallBack callBack;
		try {
			callBack = new RabbitMQ_CallBack();
			responseData = callBack.call( transferData );
			callBack.close();			
		} catch (TimeoutException | InterruptedException e) {
			e.printStackTrace();
		}
		
		Map errorCheckMap = JSON.decode( responseData );
		if( errorCheckMap.get("errorMessage") != null ){
			response.setStatus(400);
			response.setContentType("application/json;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print( responseData );
		}
		// Returned back the result data to client side
		else{
			response.setContentType("application/json;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print( responseData );
		}
	}
	private void checkAnalysis() {
		// TODO Auto-generated method stub
		Iterator<FileItem> it = fields.iterator();
		while (it.hasNext()) {
			FileItem item = it.next();
			if (item.getFieldName().equals("Type")) {
				if( item.getString().equals("simulation")){
					this.type = Task_Type.SIMULATION;
				}
				else if( item.getString().equals("steady")){
					this.type = Task_Type.STEADY_STATE_ANALYSIS;
				}
				else if( item.getString().equals("parameter")){
					this.type = Task_Type.PARAMETER_ESTIMATION;
				}
				else if( item.getString().equals("model_sbmlextraction")){
					this.type = Task_Type.BIOMODELS_SBMLEXTRACTION;
				}
				else if ( item.getString().equals("modelview")){
					this.type = Task_Type.MODEL_VIEW;
				}
			}
		}
	}
	private String sessionCheck(HttpServletRequest request) throws FileUploadException {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload( factory);
		this.fields = upload.parseRequest( request );
		Iterator<FileItem> it = fields.iterator();
		while (it.hasNext()) {
			FileItem item = it.next();
			if (item.getFieldName().equals("SessionId")) {
				if( item.getString().equals("")){
					return UniqueId.getUniqueId();
				}
				else{
					return item.getString();
				}
			}
		}		
		return UniqueId.getUniqueId();
	}
	private void getTransferedJSONData( ) {
		String pathToDirectory = getServletContext().getRealPath("/tmp/" + this.sessionId);
		if( this.type == Task_Type.SIMULATION ){
			Simulation_RequestReader simReq = new Simulation_RequestReader( this.fields , pathToDirectory , this.sessionId );
			this.transferData = simReq.getSimParamAsJSON();
		}
		else if( this.type == Task_Type.STEADY_STATE_ANALYSIS ){
			SteadyStateAnalysis_RequestReader stedReq = new SteadyStateAnalysis_RequestReader( this.fields , pathToDirectory , this.sessionId);
			this.transferData = stedReq.getstedParamAsJSON();
		}
		else if( this.type == Task_Type.PARAMETER_ESTIMATION ){
			ParameterEstimation_RequestReader paramReq = new ParameterEstimation_RequestReader( this.fields , pathToDirectory , this.sessionId );
			this.transferData = paramReq.getparamEstParamAsJSON();
		}
		else if( this.type == Task_Type.BIOMODELS_SBMLEXTRACTION ){
			Biomodels_SBMLExtraction_RequestReader bmsbmlReq = new Biomodels_SBMLExtraction_RequestReader( this.fields , pathToDirectory , this.sessionId );
			this.transferData = bmsbmlReq.getBmsbmlParamAsJSON();
		}
		else if( this.type == Task_Type.MODEL_VIEW ){
			ModelView_RequestReader viewReq = new ModelView_RequestReader( this.fields , pathToDirectory , this.sessionId );
			this.transferData = viewReq.getModelviewParamAsJSON();
		}
	}

}
