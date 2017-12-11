package servlet.rabbitmq;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServlet;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import general.task_manager.Task_Manager;
import general.task_type.Task_Type;
import net.arnx.jsonic.JSON;

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
					String response = null;
					try{
						String message = new String( body );
						Task_Manager manager = new Task_Manager( message );
						
						response = manager.getReponseData();
						
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
