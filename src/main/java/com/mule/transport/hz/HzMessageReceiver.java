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
import org.mule.transport.AbstractPollingMessageReceiver;
import org.mule.util.StringMessageUtils;

/**
 *
 * updated with IntelliJ IDEA.
 * User: shailesh
 * Date: 11/03/2013
 * <code>HzMessageReceiver</code>  Is the Receiver which is used to poll the HazelCast Queue/Map
 */
public class HzMessageReceiver extends  AbstractPollingMessageReceiver 
{
    /* For general guidelines on writing transports see
       http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */
    private final HzConnector hzConnector;
    private final HzEndpoint hzEndpoint;
    private HzEndpoint.MessageFactory factory = new HzEndpointMessageFactoryAdapter( this );

    public HzMessageReceiver(Connector connector, FlowConstruct flowConstruct,
                              InboundEndpoint endpoint)
            throws CreateException
    {
        super(connector, flowConstruct, endpoint);
        this.hzConnector = (HzConnector)connector;
        this.hzEndpoint = HzEndpointFactory.newEndpoint(this , endpoint , hzConnector.getHzInstance());
    }


    public void poll() throws Exception
    {
        /* IMPLEMENTATION NOTE: Once you have read the object it can be passed
           into Mule by first creating a new MuleMesage with the object and 
           calling routeMessage i.e.

			MuleMessage message = createMuleMessage(object);
            routeMessage(message));
        */


        MuleMessage muleMessage = this.hzEndpoint.poll( this.factory );

        if ( muleMessage != null ) {
            if (logger.isDebugEnabled()){
                logger.debug(StringMessageUtils.getBoilerPlate("EndPoint=" + endpoint.getAddress() + " Message =" + muleMessage.getPayload()));
            }
            this.routeMessage( muleMessage );
        }

    }
}
