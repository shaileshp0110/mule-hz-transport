
package com.mule.transport.hz;

import com.hazelcast.core.HazelcastInstance;
import com.mule.transport.hz.i18n.HzMessages;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.lifecycle.CreateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: shailesh
 * Date: 11/03/2013
 * Knows what concrete HzEndpoint to instantiate according to endpoint URI. <br/>
 * Expected endpoint URI format is : "hz://<collection type>/<collection name>". <br/>
 */
public class HzEndpointFactory {

    private static HzEndpointFactory hzEndpointFactory = new HzEndpointFactory();

    /**
     * Creates an HzResourceEndpoint matching the given Mule endpoint URI. <br/>
     * Expected URI format is "hz://<Endpoint type>/<Collection/Queue/Map name>"
     * @param component
     * @param endpoint
     * @return
     * @throws org.mule.api.lifecycle.CreateException
     */
    public static HzEndpoint newEndpoint( Object component, ImmutableEndpoint endpoint , HazelcastInstance hazelcastInstance) throws CreateException {
        return hzEndpointFactory.createEndPoint( component, endpoint , hazelcastInstance);
    }

    private final Logger logger = LoggerFactory.getLogger( this.getClass() );

    public static final String ENDPOINT_TYPE_MAP = "map";

    public static final String ENDPOINT_TYPE_QUEUE = "queue";

    private static interface CollectionEndpointFactory {
        HzEndpoint createEndpoint(String endpointName , HazelcastInstance hazelcastInstance);
    }

    private final Map<String, CollectionEndpointFactory> factories = new HashMap<String, CollectionEndpointFactory>() {
        {
            this.put(ENDPOINT_TYPE_MAP, new CollectionEndpointFactory() {
                public HzEndpoint createEndpoint(String endpointName , HazelcastInstance hazelcastInstance) {
                    return new HzMapEndpoint( endpointName , hazelcastInstance );
                }
            } );

            this.put(ENDPOINT_TYPE_QUEUE, new CollectionEndpointFactory() {
                public HzEndpoint createEndpoint(String endpointName , HazelcastInstance hazelcastInstance) {
                    return new HzQueueEndpoint( endpointName , hazelcastInstance);
                }
            } );
        }

    };

    private final Pattern trailingSlash = Pattern.compile( "^/" );

    private HzEndpoint createEndPoint(Object component, ImmutableEndpoint endpoint , HazelcastInstance hazelcastInstance) throws CreateException {

        HzEndpoint hzResourceEndpoint;

        EndpointURI uri = endpoint.getEndpointURI();

        // The "authority" of the URI determines the type of endpoint
        String resourceType = uri.getAuthority();

        // Retrieve factory for that type
        CollectionEndpointFactory factory = this.factories.get( resourceType );

        if ( factory == null ) {
            throw new CreateException( HzMessages.invalidEndpointType(resourceType), component );
        }

        // The "path" of the URI determines the type of the collection
        String path = uri.getPath();
        String resourceName = "";
        if ( path != null ) {
            // Strip trailing slash from path
            resourceName = trailingSlash.matcher( path ).replaceFirst( "" );
        }

        // A Endpoint  name must be provided
        if ( resourceName.isEmpty() ) {
            throw new CreateException( HzMessages.endpointTypeRequired(resourceName), component );
        }

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Creating endpoint for type '{}' and name '{}'", resourceType, resourceName );
        }

        return factory.createEndpoint( resourceName , hazelcastInstance);
    }
}
