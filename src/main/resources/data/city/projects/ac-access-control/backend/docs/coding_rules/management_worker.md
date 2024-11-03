# AC Management Service implementation policy

---

## Overview

This document describes the implementation policy of Worker for AC management services.

## Implementation policy

### Sequence

![Sequence](image/management_worker.png)

### Initialization

Call the worker initialization function from the main logic of the AC Management Service.  
Initialization function connects to IoTA's RabbitMQ, declares queues, and registers Consumers.  
Consumer registration is performed separately for each MQTT topic that you wish to receive for the reception process described below.  
After Consumer registration, the message reception process is started.
The message reception process is divided into functions for each Consumer-registered MQTT topic, unless there is a special reason not to do so.
Functions should be able to be processed in parallel using goroutines, etc.

See [goRabbitMQClient's startWorker()](https://github.tri-ad.tech/R-D-WCM/ps-ac-implementation-examples/blob/3fb096447702c456a1f1505da6d36a35c290a311/goRabbitMQClient/worker/worker.go#L14) for a sample implementation.

### Message Receiving

Waiting to receive a message that matches the Topic registered in Consumer.  
When a message is received, the topic is parsed and the service internal processing is called accordingly.

See [goRabbitMQClient's receiveXXX()](https://github.tri-ad.tech/R-D-WCM/ps-ac-implementation-examples/blob/3fb096447702c456a1f1505da6d36a35c290a311/goRabbitMQClient/worker/worker.go#L116) for a sample implementation.

### Service internal processing

Execute the process according to the received message as appropriate.  
Logging, DB updates, external service notifications, AMQP response message sending, etc.

## Directory to store

The source of each process should be implemented in the following directory.

- Initialization (Connect to RabbitMQ)：backend/internal/infra/amqp
- Initialization (Register Consumer): backend/management/worker/internal/cmd/main.go
- Message Receiving: backend/management/worker/internal/handler
- Service internal processing: backend/management/worker/internal/application

See [coding_rules.md](coding_rules.md) for overall directory structure
