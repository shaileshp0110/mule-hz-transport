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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mule.transport.hz.IMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.util.StringMessageUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

/**
 * <code>ResponseMessageTransformer</code> will used to return the response to incoming request,
 *
 */
public class HzMapResponseMessageTransformer extends AbstractMessageTransformer
{
    /**
     * logger used by this class
     */
    private final Log logger = LogFactory.getLog(getClass());

    private static final String ENDPOINT_TYPE_MAP = "map";
    private final Pattern trailingSlash = Pattern.compile( "^/" );

    private HazelcastInstance hzInstance;
    private String mapName;
    private int timeout = 60;
    private int frequency = 10;
    private int MILLIS_FACTOR = 1000;


    private boolean isRemoveKey;


    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException
    {

        IMessage msg = (IMessage) message.getPayload();
        if (StringUtils.isBlank(mapName) ) {
            logger.error(StringMessageUtils.getBoilerPlate("ResponseMessageTransformer Required attribute mapName not set" ));
            throw new TransformerException(this , null);
        }
        IMap map =  hzInstance.getMap(mapName) ;

        IMessage processedMsg = requiredResponse(map , msg.getUniqueId());
        return processedMsg;
    }



    private IMessage requiredResponse(IMap map , String key) {
        long elapsed = 0;
        IMessage processedMsg = null; // try cheap first

        while (!map.containsKey(key) &&  elapsed < timeout)   {
            try{
                Thread.sleep(frequency * MILLIS_FACTOR);
                elapsed = elapsed + frequency;
                if (logger.isDebugEnabled()){
                    logger.debug("Time Elapsed = " + elapsed);
                }

            }catch (InterruptedException e)
            {
                logger.warn(
                        "Interrupted while waiting for poll() to complete as part of message receiver stop.",
                        e);
                break;
            }
        }
        if (isRemoveKey){

            try {
                processedMsg = (IMessage) map.tryRemove(key , 10 , TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                logger.error("Ouch Cannot acquire the Lock " + e) ;
            }

        } else {
            processedMsg = (IMessage) map.get(key);
        }
        return processedMsg;
    }


    public String getMapName() {
        return mapName;
    }

    public void setMapName(final String mapName) {
        this.mapName = mapName;
    }

    public HazelcastInstance getHzInstance() {
        return hzInstance;
    }

    public void setHzInstance(final HazelcastInstance hzInstance) {
        this.hzInstance = hzInstance;
    }

    public boolean isRemoveKey() {
        return isRemoveKey;
    }

    public void setRemoveKey(final boolean removeKey) {
        isRemoveKey = removeKey;
    }


    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(final int frequency) {
        this.frequency = frequency;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }
}
