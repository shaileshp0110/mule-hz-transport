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

import org.mule.api.MuleException;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.transport.MessageRequester;
import org.mule.transport.AbstractMessageRequesterFactory;

/**
 *
 * updated with IntelliJ IDEA.
 * User: shailesh
 * Date: 11/03/2013
 * <code>HzMessageRequester</code> TODO document
 */
public class HzMessageRequesterFactory extends AbstractMessageRequesterFactory
{

    public MessageRequester create(InboundEndpoint endpoint) throws MuleException
    {
        return new HzMessageRequester(endpoint);
    }

}
