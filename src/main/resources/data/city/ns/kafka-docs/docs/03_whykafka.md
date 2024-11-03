# Why use a message bus?

A message bus reduces the complexity of communication between many services. Services send messages (or events) to well known queues or channels, rather than directly to each other. A publish/subscribe model in particular makes it easy for large numbers of entities to cooperate in a loosely coupled fashion.

A message bus also allows rapid expansion of services in the city. As services are not required to know who is consuming the data they are publishing (for non-access-controlled data), new services may immediately take advantage of existing data streams and build on top of them or combine them in new ways. 

