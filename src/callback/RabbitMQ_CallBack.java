package callback;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import net.sourceforge.sizeof.SizeOf;

public class RabbitMQ_CallBack {
	private Connection connection;
    private Channel channel;
    private String requestQueueName = "SWA_TASK";
    private String replyQueueName;
    public RabbitMQ_CallBack() throws IOException, TimeoutException{
    	ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        replyQueueName = channel.queueDeclare().getQueue();
    }
    public String call( String message) throws IOException, InterruptedException{
    	String corrId = UUID.randomUUID().toString();
		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId( corrId ).replyTo( replyQueueName ).build();
		
		System.out.println(" [x] Sent '" + message + "'");
		SizeOf.skipStaticField(true);
	    SizeOf.setMinSizeToLog(10);
		System.out.println(" [x] Size " + SizeOf.deepSizeOf( message ));
		channel.basicPublish("", requestQueueName, props, message.getBytes());
		final BlockingQueue< String > response_RPC = new ArrayBlockingQueue<>( 1 );
		
		channel.basicConsume( replyQueueName , true , new DefaultConsumer( channel){
			@Override
			public void handleDelivery( String consumerTag , Envelope envelope , AMQP.BasicProperties properties , byte[] body) throws UnsupportedEncodingException{
				if( properties.getCorrelationId().equals( corrId )){
					response_RPC.offer( new String(body));
				}
			}
		});
		return response_RPC.take();
    }
    public void close() throws IOException{
    	connection.close();
    }
}
