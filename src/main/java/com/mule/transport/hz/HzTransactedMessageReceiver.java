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

import org.mule.api.MuleMessage;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.CreateException;
import org.mule.api.transport.Connector;
import org.mule.transport.TransactedPollingMessageReceiver;
import org.mule.util.StringMessageUtils;

import java.util.Collections;
import java.util.List;

/**
 *
 * updated with IntelliJ IDEA.
 * User: shailesh
 * Date: 11/03/2013
 * <code>HzTransactedMessageReceiver</code>
 * Is Transacted Message Receiver When the Endpoint is defined and as transactional
 *
 */
public class HzTransactedMessageReceiver extends TransactedPollingMessageReceiver
{
    private final HzConnector hzConnector;
    private final HzEndpoint hzEndpoint;
    private HzEndpoint.MessageFactory factory = new HzEndpointMessageFactoryAdapter( this );

    /* For general guidelines on writing transports see
       http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */

    public HzTransactedMessageReceiver(Connector connector,
                                          FlowConstruct flowConstruct,
                                          final InboundEndpoint endpoint) throws CreateException
    {
        super(connector, flowConstruct, endpoint);
        this.hzConnector = (HzConnector)connector;
        this.hzEndpoint = HzEndpointFactory.newEndpoint(this , endpoint , hzConnector.getHzInstance());

    }

    @Override
    protected List<MuleMessage> getMessages() throws Exception
    {
        /* IMPLEMENTATION NOTE: You can just return a single message in a list
           or return a batch of messages to be processed. It's always
           recommend to process single messages to start with and then
           experiment with batching */

        // This method is executed within a transaction template so all
        // operations will be part of the transation

        //throw new UnsupportedOperationException("getMessages");

        MuleMessage message = this.hzEndpoint.poll( this.factory );
        if ( message != null ) {
            if (logger.isDebugEnabled()) {
                logger.debug(StringMessageUtils.getBoilerPlate("Endpoint=" + endpoint.getAddress() + " Message = " + message.getPayload().toString()));
            }
            return Collections.singletonList(message);
        }
        else {
            if (logger.isDebugEnabled()) logger.debug( "No message" );
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    protected void processMessage(Object message) throws Exception
    {
        // TODO Process the current message.  The object will be of the same
        // type returned in the List from getMessages()

        // Here is how to pass the message to Mule.  Most MessageReceivers
        // will need to do nothing more than this
        MuleMessage muleMessage = createMuleMessage(message);
        routeMessage(muleMessage);
    }


}
