package servlet.rabbitmq;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.print.attribute.standard.Media;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.catalina.ha.session.SessionIDMessage;
import org.apache.commons.math.optimization.fitting.HarmonicCoefficientsGuesser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.junit.internal.runners.ErrorReportingRunner;

import beans.modelparameter.Compartment_Beans;
import beans.modelparameter.InitialValue_Beans;
import beans.modelparameter.LocalParameters_Beans;
import beans.modelparameter.ModelParameter_Beans;
import beans.modelparameter.Parameters_Beans;
import callback.RabbitMQ_CallBack;
import general.task_type.Task_Type;
import general.unique_id.UniqueId;
import jp.ac.keio.bio.fun.libsbmlsim.libsbmlsim;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;
import parameter.Abstract_Parameter;
import parameter.Simulation_Parameter;
import rest.output.REST_OutputExtraction_Simulation;
@Path("produce")
public class RESTProducer {
	private String responseData;
	private static String WARNTEXT = "WARNING : Following parameter is ignored : \n";
	@POST
	@Path("model")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public String saveModel(@FormDataParam("file") InputStream file ,  @FormDataParam("file") FormDataContentDisposition fileDisposition , @FormDataParam("send") String send){
		String modelString;
		String sessionId = UniqueId.getUniqueId();
		String fileName = fileDisposition.getFileName();
		String pathToFile = new File(".").getAbsoluteFile().getParent() + "/" + sessionId;
		Abstract_Parameter modelParam = new Abstract_Parameter();
		
		// Set required information in order to send the message to RabbitMQ server and Daemon servlet to execute saving the model
		modelParam.setSessionInfo( sessionId );
		modelParam.setFileName( fileName );
		modelParam.setPathToFile( pathToFile );
		modelParam.setType(Task_Type.REST_MODEL_SAVE);
		
		// File String is extracted from inputstream
		BufferedReader br = new BufferedReader( new InputStreamReader( file ) );
		StringBuilder sb = new StringBuilder();
		String line;
		
		try {
			while( ( line = br.readLine() ) != null ){
				sb.append( line );
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		modelParam.setFileString( sb.toString() );
		
		responseData = this.getCallBackReturnStatement( JSON.encode( modelParam ));
		return responseData;
	}
	
	@GET
	@Path("simulation/sessionId={sessionId}/file={file}/condition/{condition}")
	@Produces( MediaType.TEXT_PLAIN)
	public String executeSimulation(@PathParam("sessionId") String sessionId , @PathParam("file") String fileName , @PathParam("condition") String condition){
		Simulation_Parameter simParam = new Simulation_Parameter();
		String pathToFile = new File(".").getAbsoluteFile().getParent() + "/" + sessionId;
		
		// Setting the meta date for executing simulation
		simParam.setPathToFile( pathToFile );
		simParam.setType( Task_Type.SIMULATION );
		simParam.setSessionInfo( sessionId );
		simParam.setFileName( fileName );
		
		// Setting the condition of simulation
		String[] conditionList = condition.split("-");
		String warnList = this.WARNTEXT;
		for( int i = 0 ; i < conditionList.length ; i ++){
			String paramName = conditionList[ i ].split("=")[ 0 ];
			String paramValue = conditionList[ i ].split("=")[ 1 ];
			
			if( paramName.equals("end")){
				simParam.setEndTime( Double.parseDouble( paramValue ));
			}
			else if( paramName.equals("num")){
				simParam.setNumTime( Integer.parseInt( paramValue ));
			}
			else if( paramName.equals("tol")){
				simParam.setTolerance( Double.parseDouble( paramValue));
			}
			else if( paramName.equals("lib")){
				simParam.setLibrary( paramValue );
			}
			else{
				warnList = warnList + paramName + "\n\n";
			}
		}
		
		// Setting the empty ModelParameter_Beans in order to send the correct message to RabbitMQ
		ModelParameter_Beans tmpParameter = new ModelParameter_Beans();
		tmpParameter.setInitValue( new InitialValue_Beans[0]);
		tmpParameter.setLocalParamValue( new LocalParameters_Beans[0]);
		tmpParameter.setParamValue( new Parameters_Beans[0]);
		tmpParameter.setCompartmentValue( new Compartment_Beans[0]);
		
		simParam.setSbmlParam( tmpParameter );
		
		// Send the message to RabbitMQ
		responseData = this.getCallBackReturnStatement( JSON.encode( simParam ));
		
		// Output for REST endpoint user is extracted
		REST_OutputExtraction_Simulation outputExtractor = new REST_OutputExtraction_Simulation( responseData );
		String result = outputExtractor.getCSVFormat();
		
		if( !warnList.equals( this.WARNTEXT )){
			return warnList + result;
		}
		else{
			return result;
		}
		
	}
	
	@DELETE
	@Path("delete/sessionId={sessionId}/file={file}")
	@Produces( MediaType.TEXT_PLAIN)
	public String deleteFile( @PathParam("sessionId") String sessionId , @PathParam("file") String fileName){
		Abstract_Parameter deleteModel = new Abstract_Parameter();
		String pathToFile = new File(".").getAbsoluteFile().getParent() + "/" + sessionId;
		
		deleteModel.setSessionInfo( sessionId );
		deleteModel.setType( Task_Type.REST_MODEL_DELETE );
		deleteModel.setPathToFile( pathToFile );
		deleteModel.setFileName( fileName );
		
		responseData = this.getCallBackReturnStatement( JSON.encode( deleteModel ));
		return responseData;
	}
	private String getCallBackReturnStatement( String message){
		String tmpResponse = null;
		RabbitMQ_CallBack callBack;
		try{
			callBack = new RabbitMQ_CallBack();
			tmpResponse = callBack.call( message );
			callBack.close();
		}catch(IOException e){
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tmpResponse;
	}
}
