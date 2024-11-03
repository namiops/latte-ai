# Guideline to test RabbitMQ Source

## 1. Get RabbitMQ Cluster username and password
```
$ export BROKER_USERNAME=`kubectl get secret -n lambda rabbitmq-default-user -o jsonpath='{.data.username}' | base64 -d`
$ export BROKER_TOKEN=`kubectl get secret -n lambda rabbitmq-default-user -o jsonpath='{.data.password}' | base64 -d`
```

## 2. Port forward to RabbitMQ pod 
```
$ kubectl port-forward -n lambda svc/rabbitmq "1883:1883"
```

## 3. Generate an event by sending data to RabbitMQ Cluster
**NOTE:** A routing key `testtopic` should match with a config in Binding `amq-topic-binding`.
```
$ mosquitto_pub -u "$BROKER_USERNAME" -P "$BROKER_TOKEN" -t "testtopic" -m '{"message":"hello eventing"}'
```

## 4. Verify that the subscriber received an event
**NOTE:** This pod might be scaled down to zero, if it doesn't receive any traffic for a period of time.
```
$ kubectl logs -f --tail 10 -n lambda -l serving.knative.dev/service=sockeye

2023/08/21 03:42:51 Broadasting to 0 clients: {"data":{"message":"hello eventing"},"id":"7f3c0f81-3cba-477d-952a-3c18ee8a4783","source":"/apis/v1/namespaces/lambda/rabbitmqsources/default-source#rabbit-source-queue","specversion":"1.0","subject":"7f3c0f81-3cba-477d-952a-3c18ee8a4783","type":"dev.knative.rabbitmq.event"}
got Validation: valid
Context Attributes,
  specversion: 1.0
  type: dev.knative.rabbitmq.event
  source: /apis/v1/namespaces/lambda/rabbitmqsources/default-source#rabbit-source-queue
  subject: 7f3c0f81-3cba-477d-952a-3c18ee8a4783
  id: 7f3c0f81-3cba-477d-952a-3c18ee8a4783
Data,
  {"message":"hello eventing"}
```
