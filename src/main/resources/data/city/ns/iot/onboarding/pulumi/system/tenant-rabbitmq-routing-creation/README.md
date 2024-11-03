# RabbitMQ routing creation

## What?

This is a Pulumi program to create a RabbitMQ exchanges, queues, bindings and policies.

## Why?

This program allows to create a RabbitMQ resources in a user-friendly manner.

## How?

This program will be located and executed by the onboarding service.

Add your resources to a file named `Pulumi.<ENV>_<TENANT-NAME>.yaml`.

File contents reference (`<VHOST>` in most cases should be equal to your tenant name):
```yaml
config:
  # sample tenant exchanges, key format: <VHOST>:exchanges
  sample:exchanges:
    - name: sample.exchange
      type: fanout
      durable: false
      auto-delete: true

  # sample tenant queues, key format: <VHOST>:queues
  sample:queues:
    - name: sample-queue
      durable: false
      auto-delete: true

  # sample tenant bindings, key format: <VHOST>:bindings
  sample:bindings:
    - source: sample.exchange
      destination: sample-queue
      destination-type: queue
      routing-key: "#"

  # sample tenant bindings, key format: <VHOST>:policies
  sample:policies:
    - source: sample-policy
      priority: 1
      pattern: sample-queue
      apply-to: queues
      definition: |
        { 
          "message-ttl": 60000, 
          "max-length": 10000 
        }
```

Optionally you may request a code ownership for your file to add additional resources without the Services Team approval later.
