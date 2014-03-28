package com.mule.transport.hz;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: shailesh
 * Date: 30/03/2013
 * Time: 13:08
 * To change this template use File | Settings | File Templates.
 */
public class TestMessage<T extends Serializable> implements IMessage{
    private String id;
    private String uniqueId;
    private String externalId;
    private T payLoad;
    private int DLQRedeliveryCount;
    private long timeStamp;
    private String queueIdentifier;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    public int getDLQRedeliveryCount() {
        return DLQRedeliveryCount;
    }

    public void setDLQRedeliveryCount(final int DLQRedeliveryCount) {
        this.DLQRedeliveryCount = DLQRedeliveryCount;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(final long timeStamp) {
        this.timeStamp = timeStamp;
    }



    public String getQueueIdentifier() {
        return queueIdentifier;
    }

    public void setQueueIdentifier(final String queueIdentifier) {
        this.queueIdentifier = queueIdentifier;
    }




    public  T getPayLoad() {

        return (T) payLoad;
    }

    public void setPayLoad(final Object payload) {
        this.payLoad = (T) payload ;
    }

    @Override
    public String toString() {
        return "DefaultHzMessage{" +
                "id='" + id + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                ", externalId='" + externalId + '\'' +
                ", payLoad=" + payLoad +
                ", DLQRedeliveryCount=" + DLQRedeliveryCount +
                ", timeStamp=" + timeStamp +
                ", queueIdentifier='" + queueIdentifier + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TestMessage)) return false;

        final TestMessage hzMessage = (TestMessage) o;

        if (DLQRedeliveryCount != hzMessage.DLQRedeliveryCount) return false;
        if (timeStamp != hzMessage.timeStamp) return false;
        if (externalId != null ? !externalId.equals(hzMessage.externalId) : hzMessage.externalId != null) return false;
        if (id != null ? !id.equals(hzMessage.id) : hzMessage.id != null) return false;
        if (payLoad != null ? !payLoad.equals(hzMessage.payLoad) : hzMessage.payLoad != null) return false;
        if (queueIdentifier != null ? !queueIdentifier.equals(hzMessage.queueIdentifier) : hzMessage.queueIdentifier != null) return false;
        if (uniqueId != null ? !uniqueId.equals(hzMessage.uniqueId) : hzMessage.uniqueId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uniqueId != null ? uniqueId.hashCode() : 0);
        result = 31 * result + (externalId != null ? externalId.hashCode() : 0);
        result = 31 * result + (payLoad != null ? payLoad.hashCode() : 0);
        result = 31 * result + DLQRedeliveryCount;
        result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        result = 31 * result + (queueIdentifier != null ? queueIdentifier.hashCode() : 0);
        return result;
    }
}
