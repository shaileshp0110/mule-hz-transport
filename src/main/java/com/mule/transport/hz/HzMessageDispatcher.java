/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.mule.transport.hz;

import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.mule.transport.hz.i18n.HzMessages;
import org.mule.DefaultMuleEvent;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.lifecycle.CreateException;
import org.mule.api.transport.DispatchException;
import org.mule.config.i18n.CoreMessages;
import org.mule.transport.AbstractMessageDispatcher;
import org.mule.util.StringMessageUtils;

import java.util.concurrent.TimeUnit;

/**
 *
 * updated with IntelliJ IDEA.
 * User: shailesh
 * Date: 11/03/2013
 * <code>HzMessageDispatcher</code> TODO document
 */
public class HzMessageDispatcher extends AbstractMessageDispatcher
{

    public static final long DEFAULT_POLL_FREQUENCY = 1000;
    public static final TimeUnit DEFAULT_POLL_TIMEUNIT = TimeUnit.MILLISECONDS;
    public static final long DEFAULT_TIMEOUT = 60000;

    private long frequency = DEFAULT_POLL_FREQUENCY;
    private TimeUnit timeUnit = DEFAULT_POLL_TIMEUNIT;
    private long timeout =  DEFAULT_TIMEOUT;
    private final HzConnector connector;
    private final HzEndpoint hzEndpoint;

    /* For general guidelines on writing transports see
       http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */

    public HzMessageDispatcher(OutboundEndpoint endpoint) throws CreateException {
        super(endpoint);
        this.connector = (HzConnector) endpoint.getConnector();
        this.hzEndpoint = HzEndpointFactory.newEndpoint(this ,endpoint , connector.getHzInstance());

        /* IMPLEMENTATION NOTE: If you need a reference to the specific
           connector for this dispatcher use:

           HzConnector cnn = (HzConnector)endpoint.getConnector(); */
    }



    @Override
    public void doDispatch(MuleEvent event) throws Exception
    {
        /* IMPLEMENTATION NOTE: This is invoked when the endpoint is
           asynchronous.  It should invoke the transport but not return any
           result.  If a result is returned it should be ignorred, but if the
           underlying transport does have a notion of asynchronous processing,
           that should be invoked.  This method is executed in a different
           thread to the request thread. */


        /* IMPLEMENTATION NOTE: The event message needs to be transformed for the outbound transformers to take effect. This
           isn't done automatically in case the dispatcher needs to modify the message before apllying transformers.  To
           get the transformed outbound message call -
           event.transformMessage(); */
        if (logger.isDebugEnabled()){
            logger.debug(StringMessageUtils.getBoilerPlate("EndPoint=" + endpoint.getAddress() + " Message =" + event.getMessage().getPayload()));
        }
        hzEndpoint.dispatch(event ,getEndpoint() ,connector);
        //throw new UnsupportedOperationException("doDispatch");
    }

    @Override
    public MuleMessage doSend(MuleEvent event) throws Exception
    {
        /* IMPLEMENTATION NOTE: Should send the event payload over the
           transport. If there is a response from the transport it shuold be
           returned from this method. The sendEvent method is called when the
           endpoint is running synchronously and any response returned will
           ultimately be passed back to the callee. This method is executed in
           the same thread as the request thread. */

        /* IMPLEMENTATION NOTE: The event message needs to be transformed for the outbound transformers to take effect. This
           isn't done automatically in case the dispatcher needs to modify the message before apllying transformers.  To
           get the transformed outbound message call -
           event.transformMessage(); */


        // wrapped in a MuleMessage object
        if (logger.isDebugEnabled()){
            logger.debug(StringMessageUtils.getBoilerPlate("EndPoint=" + endpoint.getAddress() + "Message =" + event.getMessage().getPayload()));
        }
        hzEndpoint.dispatch(event ,getEndpoint() ,connector);
        //Return original Message  and use the HZ Map inbound endpoint to return the processes message to original request
        //this will take care of the flow parallel processing
        return event.getMessage();

    }

    /**
     *
     * @param event
     * @return
     * @throws Exception   not using move to Individual Endpoint
     */
    private MuleMessage dispatchMessage(MuleEvent event) throws Exception{

        final EndpointURI endpointUri = endpoint.getEndpointURI();

        if (endpointUri == null)
        {
            throw new DispatchException(CoreMessages.objectIsNull("Endpoint"), event, getEndpoint());
        }
        if (logger.isDebugEnabled()){
            logger.debug(StringMessageUtils.getBoilerPlate("EndPoint=" + endpointUri + "Message =" + event.getMessage().getPayload()));
        }

        IQueue queue = connector.getHzInstance().getQueue(endpointUri.getAddress());
        final Object message = event.getMessage().getPayload();
        if  (!(event.getMessage().getPayload() instanceof IMessage))  {
            throw new DispatchException(
                    HzMessages.checkTransformer("Hz message", message.getClass(), connector.getName()),
                    event, getEndpoint());
        }
        final MuleEvent eventToDispatch = DefaultMuleEvent.copy(event);
        eventToDispatch.clearFlowVariables();
        eventToDispatch.setMessage(eventToDispatch.getMessage().createInboundMessage());
        IMessage msg =(IMessage)message;
        if (msg.getUniqueId() != null){
            if (!queue.offer(eventToDispatch.getMessage().getPayload())){
                // queue is full
                throw new DispatchException(HzMessages.queueIsFull(queue.getName(), queue.size()),
                        eventToDispatch, getEndpoint());
            }

        }else{
            throw new UnsupportedOperationException("doDispatch Unique Id cannot be  null " + msg.getUniqueId());
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("dispatched MuleEvent on endpointUri: " + endpointUri);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("Exchange Pattern  set on outbound endpoint : " + eventToDispatch.getExchangePattern());
        }
        if (eventToDispatch.getExchangePattern().hasResponse())  {
            final IMap responseMap =  connector.getHzInstance().getMap("TODO GET THE MAP"); //TODO Shailesh

            final IMessage responseMessage = required(responseMap, msg.getUniqueId()) ;
            if (responseMessage != null){
                final MuleMessage muleMessage =  createMuleMessage(responseMessage);
                return muleMessage;
            }
            logger.info("Giving up Timeout  For Response to get Processed  " + endpointUri);
        }

        return null;
    }


    private IMessage required(IMap map , String key) {
        long elapsed = 0;
        IMessage responseMsg = (IMessage) map.get(key); // try cheap first

        if (responseMsg == null) {
            while(map.get(key) == null && elapsed < timeout) {
                try{
                    Thread.sleep(frequency);
                    elapsed = elapsed + frequency;

                }catch (InterruptedException e)
                {
                    logger.warn(
                            "Interrupted while waiting for poll() to complete as part of message receiver stop.",
                            e);
                    break;
                }
            }
        }
        return responseMsg;
    }


}

