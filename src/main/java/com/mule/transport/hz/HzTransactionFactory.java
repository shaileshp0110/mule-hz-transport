
package com.mule.transport.hz;

import org.mule.api.MuleContext;
import org.mule.api.transaction.Transaction;
import org.mule.api.transaction.TransactionException;
import org.mule.api.transaction.UniversalTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created with IntelliJ IDEA.
 * User: shailesh
 * Date: 11/03/2013
 * <code>HzTransactionFactory</code> Creates a
 * HazelcastTransaction
 *
 * @see HzTransaction
 */
public class HzTransactionFactory implements UniversalTransactionFactory
{

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    /* For general guidelines on writing transports see
       http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */

    public HzTransactionFactory() {
        logger.debug( "Created" );
    }

    public Transaction beginTransaction(MuleContext muleContext) throws TransactionException {
        logger.debug( "beginTransaction" );
        HzTransaction tx = new HzTransaction( muleContext );
        tx.begin();
        return tx;
    }

    public boolean isTransacted() {
        return true;
    }

    public Transaction createUnboundTransaction(final MuleContext muleContext) throws TransactionException {
        return  new HzTransaction( muleContext );
    }
}
