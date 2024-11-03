# Deploying the Cache App

Now that we've tried out some simple tire-kicking with the cluster let's try
something a bit more involved. In this section we will deploy a redis cluster,
a postgres cluster, and an application that uses both of them, before finally
adding in some faults using istio so we can see the benefits of adding caching
to our app.

## Cache App

This application is a Rust API with both a Postgres storage layer and a Redis
caching layer. Everytime a message is created it is also saved in Redis. This
caching only saves the LAST message that was created and will overwrite the
saved message with a new one when the new message is created. The cached
message can also be removed by deleting that same message id, which will remove
it from both postgres and redis.

Messages have the form:

```rust
pub struct Message {
    pub id: Option<i32>,
    pub content: String,
    pub from: Option<String>,
    pub createdAt: Option<DateTime<Utc>>,
    pub updatedAt: Option<DateTime<Utc>>,
}
```

Ids are the primary key and index of our messages, content is the actual text
content of the message, from is a field that is populated with the source of
the data we are fetching so we can tell if the data is coming from postgres or
redis, and then the two timestamp fields.

The application supports the endpoints below:

```text
GET /messages
GET /message/<id>
POST /message
DELETE /message/<id>
```

as well as two utility endpoints

```text
POST /setup
POST /teardown
```

## Deploying

Let's actually deploy our application now, from the tutorial project root

```shell
/ns/tutorial/redis-101/
```

we will deploy the second group.

```shell
$ kubectl apply -k 02_cache_app/
namespace/redis-test created
storageclass.storage.k8s.io/redis-test-test-postgresql-helm created
serviceaccount/sidecar-instance created
serviceaccount/sidecar-pgbackrest created
role.rbac.authorization.k8s.io/sidecar-instance-role created
role.rbac.authorization.k8s.io/sidecar-pgbackrest-role created
rolebinding.rbac.authorization.k8s.io/sidecar-instance created
rolebinding.rbac.authorization.k8s.io/sidecar-pgbackrest created
service/cache-app created
service/newapp-redis-headless created
service/newapp-sentinel-headless created
service/test-postgresql-helm-headless created
deployment.apps/cache-app created
redisfailover.databases.spotahome.com/newapp-redis created
postgrescluster.postgres-operator.crunchydata.com/test-postgresql-helm created
```

We can check the status of the pods as before with

```shell
$ kubectl get pods -n redis-test
NAME                                    READY   STATUS            RESTARTS      AGE
cache-app-8b96fd889-mmtqb               1/2     Error             1 (27s ago)   72s
rfr-newapp-redis-0                      1/2     Running           0             72s
rfr-newapp-redis-1                      1/2     Running           0             71s
rfr-newapp-redis-2                      1/2     Running           0             70s
rfs-newapp-redis-7f49b58564-ghk7g       1/2     Running           0             72s
rfs-newapp-redis-7f49b58564-k6h67       1/2     Running           0             71s
rfs-newapp-redis-7f49b58564-l2b8z       1/2     Running           0             71s
test-postgresql-helm-instance1-dnch-0   0/6     PodInitializing   0             70s
test-postgresql-helm-instance1-w2t8-0   0/6     PodInitializing   0             70s
test-postgresql-helm-repo-host-0        0/3     PodInitializing   0             71s
```

Once all our pods are running we are ready to try out our application. This may
take a minute or more because we are additionally starting up a postgres
cluster.

## Try out Cache App

First we need to port-forward so we can connect to our service from outside the
cluster

```shell
$ kubectl port-forward service/cache-app 8000:8000 -n redis-test
Forwarding from 127.0.0.1:8000 -> 8000
Forwarding from [::1]:8000 -> 8000
```

If you ran the last command in the foreground make sure to have another shell
open to the same location so we can run other commands.

Now we can actually connect to our service, so let's first run the setup
command.

```shell
$ curl -X POST http://127.0.0.1:8000/setup
Postgres Tables Set Up
```

Feel free to try out any combination of commands to see how the application
works, or follow along below for one possible set.

```shell
$ curl -X GET http://127.0.0.1:8000/messages
{"status":"success","results":0,"messages":[]}
```

When we start with a completely new application and cluster no messages are
saved. We add a new message and note that the "from" field that is returned is
"both" as the message has been saved in both Redis and Postgres.

```shell
$ curl -H 'Content-Type: application/json' --data '{"content":"This is a test message"}' http://127.0.0.1:8000/message
{"status":"success","data":{"message":{"id":1,"content":"This is a test message","from":"both" "createdAt":"2023-10-26T03:59:46.598327Z" "updatedAt":"2023-10-26T03:59:46.598327Z"}}}
```

We add a second message and this message is also stored in both places.

```shell
$ curl -H 'Content-Type: application/json' --data '{"content":"This is a SECOND test message"}' http://127.0.0.1:8000/message
{"status":"success","data":{"message":{"id":2,"content":"This is a SECOND test message","from":"both","createdAt":"2023-10-26T04:03:53.377958Z","updatedAt":"2023-10-26T04:03:53.377958Z"}}}
```

When we fetch all messages though, both values state that they come from
postgres. This is a design choice of the application as fetching all messages
will require accessing the database anyways and there is no benefit to
fetching from Redis also.

```shell
$ curl -X GET http://127.0.0.1:8000/messages
{"status":"success","results":2,"messages":[{"id":1,"content":"This is a test message","from":"postgres","createdAt":"2023-10-26T03:59:46.598327Z","updatedAt":"2023-10-26T03:59:46.598327Z"},{"id":2,"content":"This is a SECOND test message","from":"postgres","createdAt":"2023-10-26T04:03:53.377958Z","updatedAt":"2023-10-26T04:03:53.377958Z"}]}
```

If instead we query each value.

```shell
$ curl -X GET http://127.0.0.1:8000/message/1
{"status":"success","data":{"message":{"id":1,"content":"This is a test message","from":"postgres","createdAt":"2023-10-26T03:59:46.598327Z","updatedAt":"2023-10-26T03:59:46.598327Z"}}}

$ curl -X GET http://127.0.0.1:8000/message/2
{"status":"success","data":{"message":{"id":2,"content":"This is a SECOND test message","from":"redis","createdAt":"2023-10-26T04:03:53.377958Z","updatedAt":"2023-10-26T04:03:53.377958Z"}}}
```

You can see that the first value is fetched from Postgres as the cache no
longer has saved it, but the second value is fetched from Redis.

Just to round out our endpoints let's delete our second message and then add
a new, third, message.

```shell
$ curl -X DELETE http://127.0.0.1:8000/message/2
{"status":"succeeded","message":"Row deleted"}

$ curl -H 'Content-Type: application/json' --data '{"content":"This is a ==THIRD== message"}' http://127.0.0.1:8000/message
{"status":"success","data":{"message":{"id":3,"content":"This is a ==THIRD== message","from":"both","createdAt":"2023-10-26T04:19:00.491707Z","updatedAt":"2023-10-26T04:19:00.491707Z"}}}

$ curl -X GET http://127.0.0.1:8000/messages
{"status":"success","results":2,"messages":[{"id":1,"content":"This is a test message","from":"postgres","createdAt":"2023-10-26T03:59:46.598327Z","updatedAt":"2023-10-26T03:59:46.598327Z"},{"id":3,"content":"This is a ==THIRD== message","from":"postgres","createdAt":"2023-10-26T04:19:00.491707Z","updatedAt":"2023-10-26T04:19:00.491707Z"}]}
```

## Fault Injection

Now that we've explored the application let's explore a scenario where this
caching could actually make a difference. Our service mesh, Istio, allows us to
inject faults between services to test their interactions under less than ideal
conditions. For our service we will create a virtual service and a destination
rule targeting the postgres service and introducing a 3s delay.

With this fault added we will expect our app to now take 3s longer to respond
to all endpoints except for GET /message/<id> requests, because it can return
without ever touching the database.

Let's add those faults now

```shell
$ kubectl apply -f 02_cache_app/virtual-service.yaml
virtualservice.networking.istio.io/cache-vs created
$ kubectl apply -f 02_cache_app/destination-rule.yaml
destinationrule.networking.istio.io/cache-dr created
```

To verify that our messages are slow now let's try

```shell
$ curl -X GET http://127.0.0.1:8000/messages
{"status":"success","results":2,"messages":[{"id":1,"content":"This is a test message","from":"postgres","createdAt":"2023-10-26T03:59:46.598327Z","updatedAt":"2023-10-26T03:59:46.598327Z"},{"id":3,"content":"This is a ==THIRD== message","from":"postgres","createdAt":"2023-10-26T04:19:00.491707Z","updatedAt":"2023-10-26T04:19:00.491707Z"}]}
```

The same result as last time, but a whole lot slower!

But if we try our third message which should still be cached

```shell
$ curl -X GET http://127.0.0.1:8000/message/3
{"status":"success","data":{"message":{"id":3,"content":"This is a ==THIRD== message","from":"redis","createdAt":"2023-10-26T04:19:00.491707Z","updatedAt":"2023-10-26T04:19:00.491707Z"}}}
```

Much faster!

Of course a real application would cache much more data than just your most
recently created message, but we can see how to improve reliability and speed
of access by adding caching to your application.

Feel free to continue experimenting with the application and when you're done
we can run

```shell
$ curl -X POST http://127.0.0.1:8000/teardown
Tearing Down
```
