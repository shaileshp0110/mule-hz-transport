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
import org.mule.api.endpoint.EndpointURI;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.tck.AbstractMuleTestCase;

public class HzEndpointTestCase extends AbstractMuleTestCase
{
    /* For general guidelines on writing transports see
       http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */

    public void testValidEndpointURI() throws Exception
    {
        HzConnector connector = new HzConnector(muleContext);
        connector.setHzInstance(Hazelcast.newHazelcastInstance());
        muleContext.getRegistry().registerConnector(connector);

        EndpointURI url = new MuleEndpointURI("hz://queue/testQueue" ,muleContext);
        assertEquals("hz", url.getScheme());
        assertEquals("hz://queue/testQueue", url.getUri().toString());


    }
}
