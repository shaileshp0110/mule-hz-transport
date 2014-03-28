mule-hz-transport
=================

mule hazelcast transport

The Mule (Hazelcast) Hz  Transport  queues and maps to work in clustered environment.

The transport hides the low level concepts what hazelcast queue/map 
Here is a quick review of the main configuration elements you'll deal with when using the transport:

The connector element take care of establishing the connection to Hazelcast queue/maps 
The inbound endpoint elements are in charge of consuming messages from Hazelcast queues and route them to your components, transformers, routers or other outbound endpoints as defined in your Mule configuration.
The outbound endpoint elements are in charge of publishing messages to Hazelcast Queues/Map from your Mule configuration.
Message payload and properties

The payload get converted to type HzMessage  , which gives  message some properties like to keep track of delivery counts etc

Test Use case  , which test the cluster and failover handling of the message which can give more insight of the transport
