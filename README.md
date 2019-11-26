mule-hz-transport
=================

mule hazelcast transport

The Mule (Hazelcast) Hz  Transport  queues and maps to work in clustered environment.

The transport hides the low level concepts what Hazelcast queue/map
Here is a quick review of the main configuration elements you'll deal with when using the transport:

The connector element take care of establishing the connection to Hazelcast queue/maps 
The inbound endpoint elements are in charge of consuming messages from Hazelcast queues and route them to your components, transformers, routers or other outbound endpoints as defined in your Mule configuration.
The outbound endpoint elements are in charge of publishing messages to Hazelcast Queues/Map from your Mule configuration.
Message payload and properties

There is also transformer which is used to convert any payload to the IMessage type, so you the payload get converted
to HzMessage ,
Transformer to get the message from the map based on the name of the map

The payload get converted to type HzMessage  , which gives  message some properties like to keep track of delivery counts etc

Test Use case  , which test the cluster and fail-over handling of the message which can give more insight of the transport

Test 1