
package com.mule.transport.hz;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.transport.AbstractTransportMessageHandler;

/**
 * Created with IntelliJ IDEA.
 * User: shailesh
 * Date: 11/03/2013
 *
 * To change this template use File | Settings | File Templates.
   Adapter to for Wrapping Mule MessageFactory we are intrested in creating mule messaage  , Extending the class will be overkill
 */
public class HzEndpointMessageFactoryAdapter implements HzEndpoint.MessageFactory {

    private AbstractTransportMessageHandler adaptee;


    public HzEndpointMessageFactoryAdapter(AbstractTransportMessageHandler adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public MuleMessage createMuleMessage(Object payload) throws MuleException {
        return adaptee.createMuleMessage( payload );
    }


}
