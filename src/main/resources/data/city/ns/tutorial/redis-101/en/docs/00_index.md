# Welcome

This tutorial is intended to introduce you to Redis and the Spotahome Redis
Operator implemented on Agora. While this tutorial will walk you through
getting a working Redis cluster on Agora, it is recommended that you know
some basic concepts around Kubernetes, Operators, and Redis. The tutorial will
cover this introduction, an exploration of an example deployable cluster, a
test app, and how we actually deploy these clusters on Agora.

## What is Redis?

[Redis](https://redis.io/) is an open source, in-memory data store. Redis can
be used as a database, message broker, or streaming engine, but is most
commonly used for its abilities as an in-memory cache.

## Why do we want redis?

Frequently when teams consider implementing Redis it is compared against
memcached for its ability to store user sessions, or other types of
relatively transient data, with a “real” database like PostgresSQL providing
persistence. Redis provides data structures such as strings, hashes, lists,
sets and more. In particular, several teams have requested that Agora
implement some level of transient storage for their specific use-cases, and
Redis is an excellent option for this.

## What is the Redis operator?

[Spotahome Redis Operator](https://github.com/spotahome/redis-operator) is
Spotahome's open source Redis Operator intended for deployment on kubernetes
clusters. This operator manages a resource defined by the operator known as a
redis-failover and is implemented in Agora.

The operator creates clusters deployed with [Sentinel](https://redis.io/docs/management/sentinel/).

![Redis Sentinel](./assets/redis-sentinel.png)

Refer to the [TN](https://docs.google.com/document/d/1GIBQZl1MqVa9SCtbPV5eg8qtbT3uj9d_08YjAyB-_DE/edit#heading=h.5kpaxzvyxcj) for more details on various Redis deployment patterns.
