/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.mule.transport.hz.functional;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.mule.transport.hz.HzConnector;
import com.mule.transport.hz.IMessage;
import org.junit.Assert;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.client.DefaultLocalMuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import java.util.*;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO
 */

public class HzFunctionalObjectToHzMessageTransformerTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigResources() {
        return "test-config-object-to-HzMessageTransformer.xml";
    }

    @Test
    public void testPassThrough() throws Exception
    {
        org.mule.module.client.MuleClient client = new org.mule.module.client.MuleClient(muleContext);
        Set polos = new HashSet(Arrays.asList(new String[]{"Marco", "Niccolo", "Maffeo"}));
        Iterator people = polos.iterator();
        while (people.hasNext())
        {
            client.dispatch("vm://entry", people.next(), null);
        }

        for (int i = 0; i < 3; ++i)
        {
            MuleMessage response = client.request("queue", 3000L);
            Assert.assertNotNull("Response is null", response);
            String person = (String) response.getPayload();
            String name = new StringTokenizer(person).nextToken();
            assertTrue(name, polos.contains(name));
            polos.remove(name);
        }
    }

    @Test
    public void testSimple() throws Exception {
        MuleClient muleClient = new DefaultLocalMuleClient(muleContext);


        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;
        String payload = "orderId=34 ," + "locale=en_US";

        muleClient.dispatch("vm://test", payload, null);


        IQueue submitOrder_Q =  hazelcastInstance.getQueue("submitOrder_Q");

        while (submitOrder_Q.isEmpty() ) {
            logger.info( "Waiting for message to end up in output queue" );
            Thread.sleep( 1000 );
        }

        IMessage receivedMessage =(IMessage) submitOrder_Q.poll();

        assertNotNull(receivedMessage);
        assertTrue( submitOrder_Q.isEmpty() ); // Ensure that polling removes message from map

        muleContext.stop();

    }


}


