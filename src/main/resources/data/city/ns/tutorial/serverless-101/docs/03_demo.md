# Demo

In this demo let's walkthrough creating a serverless architecture from ground up using agoractl-serverless. Make sure you have completed the [preparations](./01_quickstart.md#what-youll-need) before creating the serverless components.

For this demo I am using specific alias as shown below

```zsh
$ alias agoractl-serverless="bazel run //ns/agoractl -- serverless"
```

### Create a basic service

We will start small and create a simple service (slack-service) whose function would be to send a message to a slack channel. We will be using `agora-tenant` namespace and [`/infra/k8s/dev/agora-tenant/serverless-demo/`](https://github.com/wp-wcm/city/tree/main/infra/k8s/dev/agora-tenant/serverless-demo) folder.

![Alt text](./assets/demo-1.png)

#### command

```zsh
$ agoractl-serverless create service \
            --name=slack-service-001 \
            --namespace=agora-tenant \
            --application_at=/projects/serverless-demo/slack-service-001 \
            --application_language=go \
            --with_additional_configuration
```

> We have added [additional configuration](./02_references.md#integrating-vault-with-your-service) file to add vault annotations to use vault in our slack service
>
> For details on what the above command generates, you can have a look at [Creating a Service](./01_quickstart.md#2-create-your-service).

Now, we will update the business logic ([code](https://github.com/wp-wcm/city/blob/main/projects/serverless-demo/slack-service-001/main.go)) for our slack service and push the changes.

#### test

After merging, Flux will deploy our service and we can test our service by executing a curl command to our application url or use Postman.

```zsh
$ curl -X POST \
     -H "Content-Type: application/json" \
     -d '{ "Name": "robot-ce", "Message": "This is a CloudEvent Message from a curl command!"}' \
     https://slack-service-001-agora-tenant-lambda.cityos-dev.woven-planet.tech/
```

Our slack-service seems to be working fine. You can check the received message [here](https://woven-by-toyota.slack.com/archives/C061MHJRZ7U/p1698648143179839).

### Using Step Functions for event-driven architecture

An event-driven architecture uses events to trigger and communicate between decoupled services and is common in modern applications built with microservices.

Now let's say we have another service that executes some function say ordering a battery and then sends a message to slack channel about successful/unsuccessful order. This is an application of event-driven architecture where one function receives an event(notification of low battery), executes some action (order a battery), and then forwards the event to next function (send message to slack service).

Let's create our order service and connect it to our slack service

![Alt text](./assets/demo-2-1.png)


#### command

```zsh
$ agoractl-serverless create service \
            --name=order-svc-001 \
            --namespace=agora-tenant \
            --application_at=/projects/serverless-demo/order-svc-001 \
            --application_language=go \
            --sink=//infra/k8s/dev/agora-tenant/serverless-demo/slack-service-001:slack-service-001
```

Now, we will update the business logic ([code](https://github.com/wp-wcm/city/blob/main/projects/serverless-demo/order-svc-001/main.go)) for our order service and push the changes.

#### test

We can test our order service by sending a curl request


```zsh
$ curl -X POST \
     -H "Content-Type: application/json" \
     -d '{ "Name": "robot-ce", "Message": "Robot battery low. Please take action!"}' \
     https://order-svc-001-agora-tenant-lambda.cityos-dev.woven-planet.tech/
```

Our order service is working and you can check the delivered message [here](https://woven-by-toyota.slack.com/archives/C061MHJRZ7U/p1698649424977549).

### Using Broker to send events to multiple services

Now, suppose, we need to send events to multiple services at the same time or customize how we can send events to our services we can do that using a **Broker**. A broker can send events to multiple services simultaneously or filter the event to be sent to only 1 service. 

In our case we will create our broker and customize it in such a way that it should be able to filter events based on some event type.

![Alt text](./assets/demo-3-1.png)

#### command

```zsh
$ agoractl-serverless create broker \
            --name=serverless-svc-broker-001 \
            --namespace=agora-tenant \
            --sink_with_trigger=//infra/k8s/dev/agora-tenant/serverless-demo/slack-service-001:slack-service-001 \
            --sink_with_trigger=//infra/k8s/dev/agora-tenant/serverless-demo/order-svc-001:order-svc-001
```

> For details on what above command generates refer to [Creating a Broker](./01_quickstart.md#3-create-your-broker)

And then we will be configuring our broker for routing the events based on event Type.


1. [Filter](https://github.com/wp-wcm/city/blob/main/infra/k8s/dev/agora-tenant/serverless-demo/serverless-svc-broker-001/trigger-0-values.yaml) for the slack service

    ```yaml
    #@data/values
    #@overlay/match-child-defaults missing_ok=True
    ---
    filters:
    - suffix:
        type: .alert
    ```

2. [Filter](https://github.com/wp-wcm/city/blob/main/infra/k8s/dev/agora-tenant/serverless-demo/serverless-svc-broker-001/trigger-1-values.yaml) for the order service

    ```yaml
    #@data/values
    #@overlay/match-child-defaults missing_ok=True
    ---
    filters:
    - suffix:
        type: .update.alert
    ```

This effectively tells the broker that whenever event of type `"*.alert"` is received, route it to slack service. And whenever an event of type `"*.update.alert"` is received, route it to order service. You can see that all events that are routed to order service will also be routed to slack service.

#### test

To test this architecture we will create another service ktransform service which will set these event types and forward the events to broker.

![Alt text](./assets/demo-3-2.png)

Creating ktransform service

```zsh
$ agoractl-serverless create service \
            --name=ktransform-svc-001 \
            --namespace=agora-tenant \
            --application_at=/projects/serverless-demo/ktransform-svc-001 \
            --application_language=go \
            --sink=//infra/k8s/dev/agora-tenant/serverless-demo/serverless-svc-broker-001:serverless-svc-broker-001
```

Now, we will update the business logic ([code](https://github.com/wp-wcm/city/blob/main/projects/serverless-demo/ktransform-svc-001/main.go)) for our ktransform service and push the changes. You can see that we are specifically setting an event type [here](https://github.com/wp-wcm/city/blob/24781a294471b8f002d155ce8cf5aace71723de6/projects/serverless-demo/ktransform-svc-001/main.go#L41).

Now we can send request to our ktransform service and test our broker

```zsh
$ curl -X POST \
     -H "Content-Type: application/json" \
     -d '{ "Name": "robot-ce", "Message": "Battery: 70%", "Type": "1" }' \
     https://ktransform-svc-001-agora-tenant-lambda.cityos-dev.woven-planet.tech/
```
We can see that our broker is working correctly you can find the sent message [here](https://woven-by-toyota.slack.com/archives/C061MHJRZ7U/p1698653414977619)

We can also test `"Type": "2"` messages

```zsh
$ curl -X POST \
     -H "Content-Type: application/json" \
     -d '{ "Name": "robot-ce", "Message": "Battery: 5%, Please order a new battery", "Type": "2" }' \
     https://ktransform-svc-001-agora-tenant-lambda.cityos-dev.woven-planet.tech/
```

We can see that our broker is working correctly. You can find the sent message of type 2 [here](https://woven-by-toyota.slack.com/archives/C061MHJRZ7U/p1698653543349739)

> In order to customize the events that are sent to a broker we should use a service rather than directly sending events from sources like kafka topics. This is because events coming from other sources are less customizable. This is a limitation of sending events from sources.


### Using Different sources to bring events into serverless architecture

Source bridges between external and your serverless environment. Sources fetch or receive data from Agora's source (e.g. iota, or Kafka), convert these into events, and then forward these events to the appropriate broker or directly to services for processing.

here we will be using kafka source to bring kafka topic messages into our serverless architecture. Then we will forward these events to our ktransform service.


![Alt text](./assets/demo-4.png)

#### command

```zsh
$ agoractl-serverless create source kafka \
            --name=kafka-source-slack-001 \
            --namespace=agora-tenant \
            --consumer_group="dispatcher" \
            --topic="kafka-topic-slack" \
            --sink=//infra/k8s/dev/agora-tenant/serverless-demo/ktransform-svc-001:ktransform-svc-001
```

> For details on what the above command generates, you can have a look at [Creating a source](./01_quickstart.md#4-configure-your-source-and-deploy-your-service)
#### test

We can test this architecture by sending a message to the kafka topic that we have mentioned. 
In this case our architecture is working correctly and you can see the sent message [here](https://woven-by-toyota.slack.com/archives/C061MHJRZ7U/p1698654797926019).


### Adding other sources 

In a similar fashion we can use a number of sources to send events within our serverless architecture like:

- Deployment
- Pod
- CronJob
- Daemonset
- Knative source

![Alt text](./assets/demo-5.png)

Currently `agoractl-serverless` is in development and will, in future, support more types of sources other than [IoTASource](./01_quickstart.md#iota-source), [KafkaSource](./01_quickstart.md#kafka-source).



