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
import org.mule.api.client.MuleClient;
import org.mule.client.DefaultLocalMuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import java.util.HashSet;
import java.util.concurrent.*;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * TODO
 */

public class HzFunctionalTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigResources() {
        return "test-config.xml";
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
    public void testSimple() throws Exception {
        MuleClient muleClient = new DefaultLocalMuleClient(muleContext);

        TestMessage message = new TestMessage();
        message.setId("1");
        message.setExternalId("External Id");
        message.setUniqueId("UniqueId-1");
        message.setPayLoad("orderId=333333");
        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;

        muleClient.dispatch("hz://map/submitOrder_C?connector=hzConnector", message, null);

        IMap in =  hazelcastInstance.getMap("submitOrder_C");

        IMap out =  hazelcastInstance.getMap("submitOrder_B");

        Object receivedMessage = waitForMessage( out, "UniqueId-1" );
        assertNotNull( receivedMessage );
        assertEquals( message, receivedMessage );
        assertTrue( in.isEmpty() ); // Ensure that polling removes message from map

        muleContext.stop();

    }


    @Test
    public void testTransaction() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;

        IQueue inputQueue = hazelcastInstance.getQueue("submitOrder_Q2");
        IQueue outputQueue = hazelcastInstance.getQueue( "submitOrder_Q3" );

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

        while ( outputQueue.isEmpty() ) {
            logger.info( "Waiting for message to end up in output queue" );
            Thread.sleep( 1000 );
        }
        HashSet<IMessage> messages = new HashSet<IMessage>();
        messages.add(message) ;
        messages.add(message2) ;
        messages.add(message3) ;
        messages.add(message4) ;


        Object output = outputQueue.poll();
        assertNotNull( output );
        assertTrue(messages.contains(output));

        while ( outputQueue.isEmpty() ) {
            logger.info( "Waiting for message to end up in output queue" );
            Thread.sleep( 1000 );
        }
        Object output2 = outputQueue.poll();

        assertNotNull( output2 );
        assertTrue(messages.contains(output2));
        while ( outputQueue.isEmpty() ) {
            logger.info( "Waiting for message to end up in output queue" );
            Thread.sleep( 1000 );
        }
        Object output3 = outputQueue.poll();
        assertNotNull( output3 );

        assertTrue(messages.contains(output3));
        while ( outputQueue.isEmpty() ) {
            logger.info( "Waiting for message to end up in output queue" );
            Thread.sleep( 1000 );
        }
        Object output4 = outputQueue.poll();
        assertNotNull( output4 );
        assertTrue(messages.contains(output4));


        assertTrue(inputQueue.isEmpty() );
    }

    @Test
    public void testTransactionQueueMapTransformerSync() throws Exception {

        HzConnector hzConnector = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        HazelcastInstance hazelcastInstance =  hzConnector.getHzInstance() ;

        IQueue inputQueue = hazelcastInstance.getQueue("submitOrder_Q1");
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


