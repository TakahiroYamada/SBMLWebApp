package servlet.rabbitmq;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServlet;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import exception.COPASI_ExportException;
import exception.JSBML_ReadException;
import exception.NoDynamicSpeciesException;
import general.error.Error_Message;
import general.task_manager.Task_Manager;
import net.arnx.jsonic.JSON;
import uk.ac.ebi.biomodels.ws.BioModelsWSException;

/**
 * Servlet implementation class Consumer
 */
public class AnalysisConsumer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String RPC_QUEUE_NAME = "SWA_TASK";
	public void init(){
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = null;
		try {
			connection = factory.newConnection();
			final Channel channel = connection.createChannel();
			channel.basicQos( 1 );
			channel.queueDeclare( RPC_QUEUE_NAME , false , false , false , null );
			
			System.out.println(" [x] Awaiting RPC requests");
			
			// This is the method to do the analysis just after adding the task into Queue named by "SWA_TASK"
			Consumer consumer = new DefaultConsumer( channel ){
				@Override
				public void handleDelivery( String consumerTag , Envelope envelope , AMQP.BasicProperties properties , byte[] body) throws UnsupportedEncodingException, IOException{
					AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId( properties.getCorrelationId()).build();
					String response = "";
					try{
						String message = new String( body );
						Task_Manager manager;
						manager = new Task_Manager( message );
						response = manager.getReponseData();
					} catch (SBMLException e) {
						Error_Message error = new Error_Message();
						error.setErrorMessage( e.getMessage());
						error.setSolveText("Please check your input file which is really SBML.");
						response = JSON.encode( error );
						e.printStackTrace();
					}  catch (NoDynamicSpeciesException e) {
						Error_Message error = new Error_Message();
						error.setErrorMessage( e.getMessage() );
						error.setSolveText("Please check species in your model whether attribute of fixed is true or false.");
						response = JSON.encode( error );
						e.printStackTrace();
					} catch (COPASI_ExportException e) {
						Error_Message error = new Error_Message();
						error.setErrorMessage( e.getMessage() );
						error.setSolveText("Please check your model's extention. Current COPASI version does not support the export of SBML including Global Parameters.");
						response = JSON.encode( error );
						e.printStackTrace();
					} catch (IllegalArgumentException e){
						Error_Message error = new Error_Message();
						error.setErrorMessage("Unable to write SBML output for documents with undefined SBML Level and Version flag.");
						error.setSolveText("Please check Level and Version of SBML.");
						response = JSON.encode( error );
						e.printStackTrace();
					} catch (XMLStreamException e) {
						Error_Message error = new Error_Message();
						error.setErrorMessage( e.getMessage() );
						error.setSolveText("Please check your input file which is really xml format.");
						response = JSON.encode( error );
						e.printStackTrace();
					} catch (BioModelsWSException e) {
						e.printStackTrace();
					} catch (JSBML_ReadException e) {
						Error_Message error = new Error_Message();
						error.setErrorMessage( e.getMessage() );
						error.setSolveText("Please check your input file which is readable by JSBML (Your SBML file may include the unsupported packages in SBML).");
						response = JSON.encode( error );
						e.printStackTrace();
						e.printStackTrace();
					}finally{
						channel.basicPublish( "", properties.getReplyTo(), replyProps , response.getBytes("UTF-8"));
						channel.basicAck( envelope.getDeliveryTag() , false);
					}
				}
			};
			channel.basicConsume( RPC_QUEUE_NAME ,  false , consumer );
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
}
