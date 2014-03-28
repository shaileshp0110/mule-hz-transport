package com.mule.transport.hz;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: shailesh
 * Date: 30/03/2013
 * Time: 12:31
 * To change this template use File | Settings | File Templates.
 */
public interface IMessage<T extends Serializable>  extends  Serializable{
    String getId();
    void setId(String id);
    String getUniqueId();
    void setUniqueId(String uniqueId);
    String getExternalId();
    void setExternalId(String externalId);
    int getDLQRedeliveryCount();
    void setDLQRedeliveryCount(int count);
    String getQueueIdentifier();
    void setQueueIdentifier(String queueIdentifier);
    long getTimeStamp();
    void setTimeStamp(long timeStamp);
    T getPayLoad();
    void setPayLoad(Object payload );


}
