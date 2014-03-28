package com.mule.transport.hz;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.mule.transport.hz.i18n.HzMessages;
import org.mule.DefaultMuleEvent;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.transport.DispatchException;
import org.mule.config.i18n.CoreMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * /**
 *
 * created with IntelliJ IDEA.
 * User: shailesh
 * Date: 11/03/2013
 */
public class HzQueueEndpoint implements HzEndpoint {

    private final Logger logger;

    private final IQueue queue;

    public HzQueueEndpoint(String queueName , HazelcastInstance hazelcastInstance) {
        this.queue = hazelcastInstance.getQueue( queueName );
        this.logger = LoggerFactory.getLogger( this.getClass().getName() + "[" + queueName + "]" );

        this.logger.debug( "Created endpoint for Hazelcast queue '{}'", queueName );
    }

    /**
     *
     * @param factory
     * @return
     * @throws Exception
     */
    public MuleMessage poll(MessageFactory factory) throws Exception {
        logger.debug( "Polling" );
        MuleMessage result = null;
        Object entry = this.queue.poll();
        if ( entry != null ) {
            if (logger.isDebugEnabled()) logger.info( "Entry found" );
            result = factory.createMuleMessage( entry );
        }
        else {

            if (logger.isDebugEnabled()) logger.debug( "No entry found");
        }

        return result;
    }

    /**
     *
     * @param event
     * @param endpoint
     * @param connector
     * @throws Exception
     */
    public void dispatch(MuleEvent event , OutboundEndpoint endpoint , HzConnector connector) throws Exception {
        final EndpointURI endpointUri = endpoint.getEndpointURI();

        if (endpointUri == null)
        {
            throw new DispatchException(CoreMessages.objectIsNull("Endpoint"), event, endpoint);
        }
        final Object message = event.getMessage().getPayload();

        if  (!(event.getMessage().getPayload() instanceof IMessage))  {
            throw new DispatchException(
                    HzMessages.checkTransformer("Hz message", message.getClass(), connector.getName()),
                    event, endpoint);
        }
        final MuleEvent eventToDispatch = DefaultMuleEvent.copy(event);
        eventToDispatch.clearFlowVariables();
        eventToDispatch.setMessage(eventToDispatch.getMessage().createInboundMessage());
        IMessage msg =(IMessage)message;
        if (msg.getUniqueId() != null){
            if (!queue.offer(eventToDispatch.getMessage().getPayload())){
                // queue is full
                throw new DispatchException(HzMessages.queueIsFull(queue.getName(), queue.size()),
                        eventToDispatch, endpoint);
            }

        }else{
            throw new UnsupportedOperationException("doDispatch Unique Id cannot be  null " + msg.getUniqueId());
        }


    }

    public MuleMessage doRequest(final MessageFactory messageFactory) throws Exception {
        logger.debug( "Requesting" );
        MuleMessage result = null;
        Object entry = this.queue.poll();
        if ( entry != null ) {
            if (logger.isDebugEnabled()) logger.info( "Requested Entry found" );
            result = messageFactory.createMuleMessage( entry );
        }
        else {

            if (logger.isDebugEnabled()) logger.debug( "Requested  entry not found");
        }

        return result;
    }


}
