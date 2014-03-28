/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.mule.transport.hz.config;

import com.mule.transport.hz.HzConnector;
import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * TODO
 */

public class HzNamespaceHandlerTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigResources()
    {
        //TODO You'll need to edit this file to configure the properties specific to your transport
        return "hz-namespace-config.xml";
       // return "test-config.xml";
    }
    @Test
    public void testHzConfig() throws Exception
    {
        HzConnector c = (HzConnector) muleContext.getRegistry().lookupConnector("hzConnector");
        assertNotNull(c);
        assertTrue(c.isConnected());
        assertTrue(c.isStarted());

        //TODO Assert specific properties are configured correctly
    }
}
