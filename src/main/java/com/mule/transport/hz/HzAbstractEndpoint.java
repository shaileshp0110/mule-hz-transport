
package com.mule.transport.hz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class HzAbstractEndpoint implements HzEndpoint {

    protected final Log logger = LogFactory.getLog(getClass());

    protected HzAbstractEndpoint(String endPointName) {
        this.logger.debug( "Endpoint created '" + endPointName + "'" );
    }

}
