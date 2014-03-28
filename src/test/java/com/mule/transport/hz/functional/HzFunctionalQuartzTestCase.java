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

public class HzFunctionalQuartzTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigResources() {
        return "test-quartz-config.xml";
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
    public void testEndToEndFlowForFailureDQLToFinalNPQueue1() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;

        IQueue inputQueue = hazelcastInstance.getQueue("submitOrder_DLQ2");
        IQueue outputQueue = hazelcastInstance.getQueue( "submitOrder_np_Q2" );
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



        while ( ! inputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }
        while ( outputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }



        assertTrue(inputQueue.isEmpty() );
    }



    @Test
    public void testEndToEndFlowForFailureDQLToFinalNPQueue() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;
        IQueue inputQueue = hazelcastInstance.getQueue("submitOrder_RD_Q");
        IQueue outputQueue = hazelcastInstance.getQueue( "submitOrder_np_Q2" );
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



        while ( ! inputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }
        while ( outputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }



        assertTrue(inputQueue.isEmpty() );
    }



    @Test
    public void testTransactionWithQuartz() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;

        IQueue inputQueue = hazelcastInstance.getQueue("submitOrder_Q1");
        IQueue outputQueue = hazelcastInstance.getQueue( "submitOrder_Q2" );
        final String input = "This is a test";

        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("UniqueId-1");
        message.setPayLoad("orderId=1111111");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message );
        hazelcastInstance.getTransaction().commit();
        hazelcastInstance.getTransaction().begin();
        for (int i = 0; i < 100 ; i++) {

            TestMessage message1 = new TestMessage();
            message.setId("1" + i);
            message.setExternalId("External Id");
            message.setUniqueId("UniqueId-1" + i);
            message.setPayLoad("orderId=1111111" + i);

            inputQueue.offer( message );

        }
        hazelcastInstance.getTransaction().commit();

        //MuleClient muleClient = new DefaultLocalMuleClient(muleContext);

        // muleClient.dispatch("hz://queue/submitOrder_Q1?connector=hzConnectorIN", message , null);


        TestMessage message2 = new TestMessage();
        message2.setId("2");
        message2.setExternalId("External Id");
        message2.setUniqueId("UniqueId-2");
        message2.setPayLoad("orderId=2222222");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message2 );
        hazelcastInstance.getTransaction().commit();

        TestMessage message3 = new TestMessage();
        message3.setId("3");
        message3.setExternalId("External Id");
        message3.setUniqueId("UniqueId-3");
        message3.setPayLoad("orderId=333333");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message3 );
        hazelcastInstance.getTransaction().commit();

        TestMessage message4 = new TestMessage();
        message4.setId("4");
        message4.setExternalId("External Id");
        message4.setUniqueId("UniqueId-4");
        message4.setPayLoad("orderId=4444444");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message4 );
        hazelcastInstance.getTransaction().commit();

        while ( ! inputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }


        assertTrue(inputQueue.isEmpty() );
    }


}


