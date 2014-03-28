
package com.mule.transport.hz;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.OutboundEndpoint;

/**
 *
 * Created with IntelliJ IDEA.
 * User: shailesh
 * Date: 11/03/2013
 * This interface is abstraction to to manage both map & queue stores
 */
public interface HzEndpoint {

    public interface MessageFactory {
        MuleMessage createMuleMessage(Object payload) throws MuleException;
    }

    MuleMessage poll(MessageFactory messageFactory) throws Exception;

    void dispatch(MuleEvent event , OutboundEndpoint endpoint , HzConnector connector) throws Exception;

    MuleMessage doRequest(MessageFactory messageFactory) throws Exception;
}
