# Remote Pulumi

## What?

This is a Pulumi program to create a RabbitMQ vhosts.

## Why?

Vhosts are created with system user privileges, hence should reside in a system stack.

## How?

Add your vhosts following the reference:
```yaml
config:
  vhosts:
    # vhost name.
    - foo
```

This will be located and executed by the onboarding service.
