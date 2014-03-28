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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO
 */

public class HzFunctionalTransactionalTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigResources() {
        return "test-config-Transaction.xml";
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
    public void testRedeliveryTransaction() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnectorIN");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;

        IQueue inputQueue = hazelcastInstance.getQueue("submitOrder_RD_Q");
        IQueue outputQueue = hazelcastInstance.getQueue( "submitOrder_RD_Q2" );
        IQueue submitOrderDLQ2 = hazelcastInstance.getQueue( "submitOrder_DLQ2" );

        final String input = "This is a test";

        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("UniqueId-1");
        message.setPayLoad("orderId=1111111");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message );
        hazelcastInstance.getTransaction().commit();

        //MuleClient muleClient = new DefaultLocalMuleClient(muleContext);

        // muleClient.dispatch("hz://queue/submitOrder_Q1?connector=hzConnectorIN", message , null);





        while (submitOrderDLQ2.isEmpty() ) {
            logger.info("Waiting for input queue to be emptied");
            Thread.sleep( 1000 );
        }



        Object output = submitOrderDLQ2.poll();
        assertNotNull( output );
        assertEquals( message, output );




        assertTrue(submitOrderDLQ2.isEmpty() );
    }



    @Test
    public void testTransactionalFailAtEnd() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnectorIN");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;

        IQueue inputQueue = hazelcastInstance.getQueue("submitOrder_Q");
        IQueue outputQueue = hazelcastInstance.getQueue( "submitOrder_Q2" );


        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("UniqueId-0000");
        message.setPayLoad("orderId=1111111");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message );
        hazelcastInstance.getTransaction().commit();
        Thread.sleep( 20000 );

        Object output = outputQueue.poll();
        assertNull(output);
        assertTrue(outputQueue.isEmpty() );
        assertTrue(!inputQueue.isEmpty() );
    }

    @Test
    public void testTransactionalFailInTheMiddle() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnectorIN");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;

        IQueue inputQueue = hazelcastInstance.getQueue("submitOrder_Q3");
        IQueue outputQueue = hazelcastInstance.getQueue( "submitOrder_Q4" );

        final String input = "This is a test";

        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("UniqueId-111111");
        message.setPayLoad("orderId=1111111");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message );
        hazelcastInstance.getTransaction().commit();




        Thread.sleep( 20000 );

        Object output = outputQueue.poll();
        assertNull( output );
        assertTrue(outputQueue.isEmpty() );

        assertTrue(!inputQueue.isEmpty() );
    }



    @Test
    public void testTransactionalFailInTheMiddleWithCatchExceptionStrategy() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnectorIN");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;

        IQueue inputQueue = hazelcastInstance.getQueue("submitOrder_Q5");
        IQueue outputQueue = hazelcastInstance.getQueue( "submitOrder_Q6" );


        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("UniqueId-22222");
        message.setPayLoad("orderId=1111111");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message );
        hazelcastInstance.getTransaction().commit();




        Thread.sleep( 20000 );

        Object output = outputQueue.poll();
        assertNull( output );
        assertTrue(outputQueue.isEmpty() );
        assertTrue(inputQueue.isEmpty() );
    }

    @Test
    public void testTransactionalFailAtEndWithCatchExceptionStrategy() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnectorIN");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;

        IQueue inputQueue = hazelcastInstance.getQueue("submitOrder_Q7");
        IQueue outputQueue = hazelcastInstance.getQueue( "submitOrder_Q8" );


        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("UniqueId-33333");
        message.setPayLoad("orderId=1111111");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message );
        hazelcastInstance.getTransaction().commit();




        Thread.sleep( 10000 );

        Object output = outputQueue.poll();
        assertNotNull(output);
        assertTrue(outputQueue.isEmpty() );
        assertTrue(inputQueue.isEmpty() );
    }





}


