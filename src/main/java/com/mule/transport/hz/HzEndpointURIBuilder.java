package com.mule.transport.hz;import org.mule.api.endpoint.MalformedEndpointException;import org.mule.endpoint.ResourceNameEndpointURIBuilder;import java.net.URI;import java.util.Properties;/** * * * Created with IntelliJ IDEA. * User: shailesh * Date: 11/03/2013 */public class HzEndpointURIBuilder extends ResourceNameEndpointURIBuilder{    @Override    protected void setEndpoint(URI uri, Properties props) throws MalformedEndpointException    {        super.setEndpoint(uri, props);    }}