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

import org.mule.api.transport.MuleMessageFactory;
import org.mule.transport.AbstractMuleMessageFactoryTestCase;

public class HzMuleMessageFactoryTestCase extends AbstractMuleMessageFactoryTestCase
{
    /* For general guidelines on writing transports see
       http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */

    @Override
    protected MuleMessageFactory doCreateMuleMessageFactory()
    {
        return new HzMuleMessageFactory(muleContext);
    }

    @Override
    protected Object getValidTransportMessage() throws Exception
    {

        return new TestMessage();
    }



    @Override
    protected Object getUnsupportedTransportMessage()
    {
        return "this is an invalid transport message for HzMuleMessageFactory";
    }
}
