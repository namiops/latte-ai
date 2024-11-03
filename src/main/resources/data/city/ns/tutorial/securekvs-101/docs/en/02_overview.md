# Overview
Now that the installation was a success we can have a look at all the things that were created.

## Services
In total, we should have deployed 4 services: 3 from the Helm Chart, and one from the API deployment

```shell
$ kubectl get services -n securekvs-101
NAME                                    TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
db-agora-kvs-test-secure-kvs            ClusterIP      10.109.183.35   <none>        5984/TCP                     3h35m
headless-db-agora-kvs-test-secure-kvs   ClusterIP      None            <none>        5984/TCP,4369/TCP,9100/TCP   3h35m
securekvs-101                           LoadBalancer   10.96.36.187    <pending>     8080:32445/TCP               3h34m
steelcouch-agora-kvs-test-secure-kvs    ClusterIP      10.98.150.35    <none>        5984/TCP                     3h35m
```

So what are all these services? Let's look at them one by one

* `db-agora-kvs-test-secure-kvs` - is an endpoint for the CouchDB.
* `headless-db-agora-kvs-test-secure-kvs` - is a service for internal CouchDB pod communication.
* `securekvs-101` - and endpoint for the API.
* `steelcouch-agora-kvs-test-secure-kvs` - and endpoint for the Steelcouch.

If you peek at the deployment files for the API you can see that we use the `steelcouch-agora-kvs-test-secure-kvs` service to inject the entry point into the API through the enviroment variable.

## Secrets
There are multiple secrets being created but we only need to care about the secret named `db-agora-kvs-test-secure-kvs`. This secret holds the user name and the password of the database. Steelcouch forwards the credentials to CouchDB for this reason they are the same.

Once again if you peek at the API deployment file you will be able to see that we use it to pass the database credentials to the API through environment variables.