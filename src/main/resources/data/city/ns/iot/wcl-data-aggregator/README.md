### FSS Device/WCL PoC

The FSS gate provisioned by Agora IoTA would send events to the IoTA's RabbitMQ. The event is parsed, enriched with meta information, and produced to Kafka by an AMQP watcher (Kafka Source Connector). This event would be consumed by a JDBC Sink connector, and the payload would be written to the Postgres Table. The Woven City Lite would poll the WCL data web server and retrieve the latest events. The WCL data server also serves as an endpoint to query incidents recorded in ServiceNow.

Here is a brief flow diagram.
![Alternative Text](./docs/device-wcl-poc.png)

[view lucid](https://lucid.app/lucidchart/invitations/accept/inv_7f83d716-bc3b-499a-b5d6-80d228ab4353)
