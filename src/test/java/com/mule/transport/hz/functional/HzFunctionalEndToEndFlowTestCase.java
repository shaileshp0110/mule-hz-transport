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
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.mule.transport.hz.HzConnector;
import com.mule.transport.hz.TestMessage;
import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;

import java.util.concurrent.*;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * TODO
 */

public class HzFunctionalEndToEndFlowTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigResources() {
        return "test-EndToEndconfig.xml";
    }
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Object waitForMessage( final IMap map, final Object key) throws InterruptedException, ExecutionException {

        Future<Object> future = executorService.submit( new Callable<Object>() {
            public Object call() throws Exception {
                Object result = null;
                while ( result == null ) {
                    Thread.yield();
                    result = map.get( key );
                }

                return result;
            }
        } );

        try {
            return future.get(360, TimeUnit.SECONDS );
        }
        catch ( TimeoutException e ) {
            String message = "Timedout while waiting for message on map" + map.getName();
            logger.error( message, e );
            fail(message);
            return null;
        }
    }


    @Test
    public void testTransaction() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnectorIN");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;

        IQueue submitOrder_Q1 = hazelcastInstance.getQueue("submitOrder_Q1");
        IQueue submitOrder_Q2 = hazelcastInstance.getQueue("submitOrder_Q2");
        IQueue submitOrder_Q3 = hazelcastInstance.getQueue("submitOrder_Q3");
        IQueue submitOrder_Q4 = hazelcastInstance.getQueue("submitOrder_Q4");

        final String input = "This is a test";

        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("UniqueId-1");
        message.setPayLoad("orderId=1111111");
        hazelcastInstance.getTransaction().begin();
        submitOrder_Q1.offer( message );
        hazelcastInstance.getTransaction().commit();

        //MuleClient muleClient = new DefaultLocalMuleClient(muleContext);

        // muleClient.dispatch("hz://queue/submitOrder_Q1?connector=hzConnectorIN", message , null);


        while ( ! submitOrder_Q1.isEmpty() ) {
            logger.info("Waiting for input queue to be emptied");
            Thread.sleep( 1000 );
        }

        while ( submitOrder_Q4.isEmpty() ) {
            logger.info( "Waiting for message to end up in output queue" );
            Thread.sleep( 1000 );
        }

        Object output = submitOrder_Q4.poll();
        assertNotNull( output );
        assertEquals( message, output );



        assertTrue(submitOrder_Q1.isEmpty() );
        assertTrue(submitOrder_Q2.isEmpty() );

        assertTrue(submitOrder_Q3.isEmpty() );

    }
}


