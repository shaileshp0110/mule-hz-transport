/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.mule.transport.hz.i18n;

import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.util.ClassUtils;

public class HzMessages extends MessageFactory
{
    private static final HzMessages factory = new HzMessages();

    private static final String BUNDLE_PATH = getBundlePath("hz");

    public static Message hazelcastInstanceNotDefined()
    {
        return factory.createMessage(BUNDLE_PATH, 1);
    }
    public static Message queueIsFull(String queueName, int maxCapacity)
    {
        return factory.createMessage(BUNDLE_PATH, 2, queueName, maxCapacity);
    }
    public static Message noReceiverForEndpoint(String name, Object uri)
    {
        return factory.createMessage(BUNDLE_PATH, 3, name, uri);
    }
    public static Message checkTransformer(String string, Class<?> class1, String name)
    {
        return factory.createMessage(BUNDLE_PATH, 4, string, ClassUtils.getSimpleName(class1.getClass()), name);
    }

    public static Message invalidEndpointType(String authority) {
        return factory.createMessage( BUNDLE_PATH, 5, authority );
    }
    public static Message endpointTypeRequired(String resourceName) {
        return factory.createMessage( BUNDLE_PATH, 6, resourceName );
    }

}