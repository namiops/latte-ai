# kafka-websocket-proxy

## What is this

Agora provides Kafka as the message bus system and this is primarily for services that are running inside Agora cluster.
However many service teams are running external applications that need to interface with Kafka.

This proxy provides a way for those external services to write messages to a Kafka topic.
In short, the client (data producer) running outside of Agora establishes a WebSocket connection with this proxy and sends messages through it following the specified message scheme, then the proxy feeds the messages to the designated Kafka topic on behalf of the client.

## Deploying kafka-websocket-proxy

TBA
