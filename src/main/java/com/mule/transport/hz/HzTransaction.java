
package com.mule.transport.hz;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Transaction;
import org.mule.api.MuleContext;
import org.mule.api.registry.RegistrationException;
import org.mule.api.transaction.TransactionException;
import org.mule.transaction.AbstractSingleResourceTransaction;
import org.mule.util.StringMessageUtils;

/**
 *
 * Creataed with IntelliJ IDEA.
 * User: shailesh
 * Date: 11/03/2013
 * <code>HazelcastTransaction</code> is a wrapper for a
 * Hazelcast local transaction. This object holds the tx resource and
 * controls the when the transaction committed or rolled back.
 *
 */
public class HzTransaction extends AbstractSingleResourceTransaction
{
    /* For general guidelines on writing transports see
       http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */

    private HazelcastInstance hazelcastInstance;
    public HzTransaction(MuleContext muleContext)
    {
        super(muleContext);
        try {
         this.hazelcastInstance =  muleContext.getRegistry().lookupObject(HazelcastInstance.class);

        }catch (RegistrationException e){

          logger.error(StringMessageUtils.getBoilerPlate("Hazel Cast Instance Not found " + e ));
          throw new RuntimeException(e);
        }

    }

    @Override
    public void bindResource(Object key, Object resource) throws TransactionException
    {
        logger.info( String.format( "bindResource( '%s', '%s')", key, resource ) );
        super.bindResource( key, resource );
    }

    @Override
    protected void doBegin() throws TransactionException
    {
        logger.trace( "doBegin" );
        Transaction transaction = hazelcastInstance.getTransaction();
        transaction.begin();
    }

    @Override
    protected void doCommit() throws TransactionException
    {
        logger.trace( "doCommit" );
        Transaction transaction = hazelcastInstance.getTransaction();
        transaction.commit();
    }

    @Override
    protected void doRollback() throws TransactionException
    {
        logger.trace( "doRollback" );
        Transaction transaction = hazelcastInstance.getTransaction();
        transaction.rollback();
    }
}
