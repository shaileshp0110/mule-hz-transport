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

import com.hazelcast.core.Hazelcast;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.service.Service;
import org.mule.api.transport.MessageReceiver;
import org.mule.transport.AbstractMessageReceiverTestCase;

import com.mockobjects.dynamic.Mock;

public class HzMessageReceiverTestCase extends AbstractMessageReceiverTestCase
{
    /* For general guidelines on writing transports see
       http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */

    @Override
    public MessageReceiver getMessageReceiver() throws Exception
    {
        Mock mockService = new Mock(Service.class);
        mockService.expectAndReturn("getResponseTransformer", null);
        return new HzMessageReceiver(endpoint.getConnector(), (Service) mockService.proxy(), endpoint);
    }

    @Override
    public InboundEndpoint getEndpoint() throws Exception
    {
        HzConnector hzConnector = new HzConnector(muleContext);
        hzConnector.setHzInstance(Hazelcast.newHazelcastInstance());
        muleContext.getRegistry().registerConnector(hzConnector);
         return muleContext.getRegistry().lookupEndpointFactory().getInboundEndpoint("hz://queue/testQueue")  ;
        //throw new UnsupportedOperationException("getEndpoint");
    }
}
