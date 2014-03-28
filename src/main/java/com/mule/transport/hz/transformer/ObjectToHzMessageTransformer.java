/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.mule.transport.hz.transformer;

import com.mule.transport.hz.DefaultHzMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.util.StringMessageUtils;

/**
 * <code>ObjectToHzMessageTransformer</code>,
 *
 */
public class ObjectToHzMessageTransformer extends AbstractMessageTransformer
{
    /**
     * logger used by this class
     */
    private final Log logger = LogFactory.getLog(getClass());


    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException
    {
        DefaultHzMessage hzMessage = new DefaultHzMessage();
        final long timeStamp = System.nanoTime();
        hzMessage.setTimeStamp(timeStamp);
        hzMessage.setUniqueId(message.getUniqueId());
        hzMessage.setPayLoad(message.getPayload());

        if (logger.isDebugEnabled()){
            logger.debug(StringMessageUtils.getBoilerPlate("Transformed HZ Message : " + hzMessage ));
        }
        return hzMessage;
    }





  
}
