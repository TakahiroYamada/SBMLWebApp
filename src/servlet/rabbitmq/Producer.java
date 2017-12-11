package servlet.rabbitmq;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import callback.RabbitMQ_CallBack;
import general.task_type.Task_Type;
import general.unique_id.UniqueId;
import parameter.Simulation_Parameter;
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
			e.printStackTrace();
		}
		// Check the analysis type from Client side
		checkAnalysis();
		
		// Summing up the analysis environment to json in order to be sent to RabbitMQ and consumer.
		transferData = getTransferedJSONData();
		
		//  Using RPC to get the call back from Consumer
		RabbitMQ_CallBack callBack = new RabbitMQ_CallBack();
		String responseData = callBack.call( transferData );
		
		// Returned back the result data to client side
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print( responseData );
		callBack.close();
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
	private String getTransferedJSONData( ) {
		String pathToDirectory = getServletContext().getRealPath("/tmp/" + this.sessionId);
		if( this.type == Task_Type.SIMULATION ){
			Simulation_RequestReader simReq = new Simulation_RequestReader( fields , pathToDirectory );
			this.transferData = simReq.getSimParamAsJSON();
		}
		else if( this.type == Task_Type.STEADY_STATE_ANALYSIS ){
			SteadyStateAnalysis_RequestReader stedReq = new SteadyStateAnalysis_RequestReader( fields , pathToDirectory);
			this.transferData = stedReq.getstedParamAsJSON();
		}
		else if( this.type == Task_Type.PARAMETER_ESTIMATION ){
			ParameterEstimation_RequestReader paramReq = new ParameterEstimation_RequestReader( fields , pathToDirectory );
			this.transferData = paramReq.getparamEstParamAsJSON();
		}		
		return null;
	}

}
