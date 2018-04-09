package servlet.rabbitmq;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import callback.RabbitMQ_CallBack;
import general.task_type.Task_Type;
import general.unique_id.UniqueId;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;
import parameter.Abstract_Parameter;

@Path("producer")
public class RESTProducer {
	
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
		
		RabbitMQ_CallBack callBack;
		String responseData;
		try{
			callBack = new RabbitMQ_CallBack();
			responseData = callBack.call( JSON.encode( modelParam ));
			callBack.close();
		} catch( TimeoutException | InterruptedException | JSONException | IOException e){
			e.printStackTrace(); 
		}
		return "sessionId=" + sessionId + "/" + "file=" + fileName;
	}
	
	@DELETE
	@Path("delete/sessionId-{sessionId}/file-{file}")
	@Produces( MediaType.TEXT_PLAIN)
	//public String deleteFile( @FormDataParam("sessionId") String sessionID ,@FormDataParam("file") String file ){
	public String deleteFile( @PathParam("sessionId") String sessionId , @PathParam("file") String file){
		Abstract_Parameter deleteModel = new Abstract_Parameter();
		String pathToFile = new File(".").getAbsoluteFile().getParent() + "/" + sessionId;
		
		deleteModel.setSessionInfo( sessionId );
		deleteModel.setFileName( file );
		deleteModel.setType( Task_Type.REST_MODEL_DELETE );
		deleteModel.setPathToFile( pathToFile );
		RabbitMQ_CallBack callBack;
		try{
			callBack = new RabbitMQ_CallBack();
			callBack.call( JSON.encode( deleteModel ));
			callBack.close();
		} catch( IOException | TimeoutException | JSONException | InterruptedException e){
			e.printStackTrace();
		}
		return "Correctly Deleted!";
	}
}
