
package com.mule.transport.hz;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mule.transport.hz.i18n.HzMessages;
import org.mule.DefaultMuleEvent;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.transport.DispatchException;
import org.mule.util.StringMessageUtils;

import java.util.Iterator;

/**
 * HzEndpoint wrapping an Hazelcast Map. <br/>
 */
public class HzMapEndpoint extends HzAbstractEndpoint {

    public static final String MESSAGE_IDENTIFIER_HEADER = "com.hazelcast.map.key";

    private final IMap<Object, Object> map;

    /**
     * Creates an endpoint to the Hazelcast map with the given name. <br/>
     * @param mapName
     */
    public HzMapEndpoint(String mapName , HazelcastInstance hazelcastInstance) {
        super( mapName );
        this.map = hazelcastInstance.getMap( mapName );
    }

    /**
     *
     * @param factory
     * @return
     * @throws Exception
     */
    public MuleMessage poll(MessageFactory factory) throws Exception {
        MuleMessage result = null;
        Iterator keys = this.map.keySet().iterator();
        if ( keys.hasNext() ) {
            // Remove the entry corresponding to the first key from the map
            final Object key = keys.next();
            final Object value = this.map.remove( key );

            if ( this.logger.isDebugEnabled() ) {
                this.logger.debug( String.format( "Removed [%s] (was '%s')", key, value ) );
            }

            if ( value != null ) {
                // Use the value as message payload, the key as message identifier property
                result = factory.createMuleMessage( value );
                result.setOutboundProperty( MESSAGE_IDENTIFIER_HEADER, key );
                if ( logger.isDebugEnabled() ) {
                    logger.debug( String.format( "Returning message w/ identifier '%s' and value '%s'", key, value ) );
                }
            }
            else {
                logger.warn( String.format( "Got <null> value for key '%s'", key ) );
            }
        }
        else {
            logger.debug( "Map is empty" );
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
        MuleMessage muleMessage =  event.getMessage();
        if (logger.isDebugEnabled()) logger.debug(StringMessageUtils.getBoilerPlate("Message" + muleMessage.getPayload().toString()));

        final Object message = muleMessage.getPayload();
        Object messageId = muleMessage.getInboundProperty( MESSAGE_IDENTIFIER_HEADER );
        if  (!(message instanceof IMessage))  {
            throw new DispatchException(
                    HzMessages.checkTransformer("Hz message", message.getClass(), connector.getName()),
                    event, endpoint);
        }
        final MuleEvent eventToDispatch = DefaultMuleEvent.copy(event);
        eventToDispatch.clearFlowVariables();
        eventToDispatch.setMessage(eventToDispatch.getMessage().createInboundMessage());
        IMessage msg =(IMessage)message;
        if ( msg.getUniqueId() == null ) {
            throw new IllegalArgumentException( "Message identifier not found as '" + MESSAGE_IDENTIFIER_HEADER + "' inbound or outbound property on the given message." );
        }
        //Transformer should set this values
        if ( messageId == null ) {
            messageId =  msg.getUniqueId() ;
            muleMessage.setOutboundProperty(MESSAGE_IDENTIFIER_HEADER  , messageId);
        }


        if ( logger.isDebugEnabled() ) {
            logger.debug( String.format( "Putting '%s'='%s'", messageId, message ) );
        }

        this.map.put(messageId, message);
    }


    public MuleMessage doRequest(MessageFactory factory) throws Exception {
        MuleMessage result = null;
        Iterator keys = this.map.keySet().iterator();
        if ( keys.hasNext() ) {
            // Remove the entry corresponding to the first key from the map
            final Object key = keys.next();
            final Object value = this.map.remove( key );

            if ( this.logger.isDebugEnabled() ) {
                this.logger.debug( String.format( "Removed [%s] (was '%s')", key, value ) );
            }

            if ( value != null ) {
                // Use the value as message payload, the key as message identifier property
                result = factory.createMuleMessage( value );
                result.setOutboundProperty( MESSAGE_IDENTIFIER_HEADER, key );
                if ( logger.isDebugEnabled() ) {
                    logger.debug( String.format( "Returning message w/ identifier '%s' and value '%s'", key, value ) );
                }
            }
            else {
                logger.warn( String.format( "Got <null> value for key '%s'", key ) );
            }
        }
        else {
            logger.debug( "Map is empty" );
        }

        return result;
    }
}
