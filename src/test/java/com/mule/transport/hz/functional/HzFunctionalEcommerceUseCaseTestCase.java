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
import com.mule.transport.hz.IMessage;
import com.mule.transport.hz.TestMessage;
import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;

import java.util.concurrent.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * TODO
 */

public class HzFunctionalEcommerceUseCaseTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigResources() {
        return "test-ecommerce-usecase-config.xml";
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
    public void testMercatusUseCase() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;
        IQueue inputQueue = hazelcastInstance.getQueue("comergent_Q1");
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

        Thread.sleep( 1000 );

        assertTrue(inputQueue.isEmpty() );
    }


    @Test
    public void testMercatusFailureComergentUseCase() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;
        IQueue inputQueue = hazelcastInstance.getQueue("comergent_Q1");
        IQueue outputQueue = hazelcastInstance.getQueue( "DISCARD_Q" );
        final String input = "This is a test";

        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("comergent_failure_1");
        message.setPayLoad("orderId=1111111");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message );
        hazelcastInstance.getTransaction().commit();


        //MuleClient muleClient = new DefaultLocalMuleClient(muleContext);

        // muleClient.dispatch("hz://queue/submitOrder_Q1?connector=hzConnectorIN", message , null);



        while (!inputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }
        while (outputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }

        Object output = outputQueue.poll();
        IMessage messageOut = (IMessage)  output;
        assertNotNull( output );
        assertEquals( message.getUniqueId(), messageOut.getUniqueId());
        assertEquals(6, messageOut.getDLQRedeliveryCount());
        assertEquals("comergent_Q1", messageOut.getQueueIdentifier());

        assertTrue(inputQueue.isEmpty() );
    }

    @Test
    public void testMercatusFailureGetProductRumbaUseCase() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;
        IQueue inputQueue = hazelcastInstance.getQueue("rumba_getProduct_Q1");
        IQueue outputQueue = hazelcastInstance.getQueue( "DISCARD_Q" );
        final String input = "This is a test";

        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("Rumba_failure_getProduct_1");
        message.setPayLoad("orderId=1111111");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message );
        hazelcastInstance.getTransaction().commit();


        //MuleClient muleClient = new DefaultLocalMuleClient(muleContext);

        // muleClient.dispatch("hz://queue/submitOrder_Q1?connector=hzConnectorIN", message , null);



        while (!inputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }
        while (outputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }

        Object output = outputQueue.poll();
        IMessage messageOut = (IMessage)  output;
        assertNotNull( output );
        assertEquals( message.getUniqueId(), messageOut.getUniqueId());
        assertEquals(6, messageOut.getDLQRedeliveryCount());
        assertEquals("rumba_getProduct_Q1", messageOut.getQueueIdentifier());

        assertTrue(inputQueue.isEmpty() );
    }



    @Test
    public void testMercatusFailureCreateRumbaPaymentRecordUseCase() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;
        IQueue inputQueue = hazelcastInstance.getQueue("createRumbaPayment_Q1");
        IQueue outputQueue = hazelcastInstance.getQueue( "DISCARD_Q" );
        final String input = "This is a test";

        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("Rumba_failure_createPayment_1");
        message.setPayLoad("orderId=1111111");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message );
        hazelcastInstance.getTransaction().commit();


        //MuleClient muleClient = new DefaultLocalMuleClient(muleContext);

        // muleClient.dispatch("hz://queue/submitOrder_Q1?connector=hzConnectorIN", message , null);



        while (!inputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }
        while (outputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }

        Object output = outputQueue.poll();
        IMessage messageOut = (IMessage)  output;
        assertNotNull( output );
        assertEquals( message.getUniqueId(), messageOut.getUniqueId());
        assertEquals(6, messageOut.getDLQRedeliveryCount());
        assertEquals("createRumbaPayment_Q1", messageOut.getQueueIdentifier());

        assertTrue(inputQueue.isEmpty() );
    }


    @Test
    public void testMercatusFailureCreateRumbaSubscriptionUseCase() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;
        IQueue inputQueue = hazelcastInstance.getQueue("createRumbaSubscription_Q1");
        IQueue outputQueue = hazelcastInstance.getQueue( "DISCARD_Q" );

        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("Rumba_failure_createSubscription_1");
        message.setPayLoad("orderId=1111111");
        hazelcastInstance.getTransaction().begin();
        inputQueue.offer( message );
        hazelcastInstance.getTransaction().commit();


        //MuleClient muleClient = new DefaultLocalMuleClient(muleContext);

        // muleClient.dispatch("hz://queue/submitOrder_Q1?connector=hzConnectorIN", message , null);



        while (!inputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }
        while (outputQueue.isEmpty() ) {
            logger.info( "Waiting for input queue to be emptied" );
            Thread.sleep( 1000 );
        }

        Object output = outputQueue.poll();
        IMessage messageOut = (IMessage)  output;
        assertNotNull( output );
        assertEquals( message.getUniqueId(), messageOut.getUniqueId());
        assertEquals(6, messageOut.getDLQRedeliveryCount());
        assertEquals("createRumbaSubscription_Q1", messageOut.getQueueIdentifier());

        assertTrue(inputQueue.isEmpty() );
    }

}


