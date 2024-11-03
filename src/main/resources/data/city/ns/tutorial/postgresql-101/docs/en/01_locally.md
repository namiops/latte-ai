# Getting Started on Kubernetes
In this section we will get the app running on Kubernetes using Minikube
(or equivalent) so you can play around with it.

## Prerequisites
You will need a few things to run this code. But the good news is that if you
can get your application running, you are already more than halfway done with
running the app on the Agora platform.

1. Minikube. Please refer to the [Minikube-101](/docs/default/component/minikube-tutorial) to learn how to set it up.
2. PostgreSQL operator. To install, follow this [guide](https://access.crunchydata.com/documentation/postgres-operator/5.0.4/quickstart/).
3. The code. You can find the code used in this tutorial [here](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/tutorial/postgresql-101). You don't necessarily need this code. Feel free to substitute it with your app.

## Run
Once everything is installed and ready, navigate to the folder where the code
is.

!!!Note
    If you have more than one Kubernetes context, switch to the correct one by
    using `kubectl use-context <context name>` command.

To run, execute:

```Shell
$ kubectl apply -k deploy/01_quickstart
namespace/postgresql-101 created
service/postgresql-101 created
deployment.apps/postgresql-101 created
postgrescluster.postgres-operator.crunchydata.com/hippo created
```

You can view the created pods by running:

```Shell
$ kubectl get po -n postgresql-101
NAME                             READY   STATUS              RESTARTS   AGE
hippo-instance1-fkfx-0           0/4     Init:0/2            0          23s
hippo-repo-host-0                0/2     Init:0/2            0          23s
postgresql-101-97f59c5c7-hm26w   0/1     ContainerCreating   0          24s
```

!!!Warning
    If the database pods (marked with `hippo`) fail to initialize, it's most
    likely because it times out when pulling the image from the remote
    repository. One workaround for this is to pull the image locally. You can
    do this by running `minikube ssh docker pull {image}`. The images in the
    `postgres.yaml` are most likely to timeout.

Eventually you should see all three pods running successfully like so:

```Shell
$ kubectl get po -n postgresql-101
NAME                             READY   STATUS      RESTARTS        AGE
hippo-backup-bq64-79m8f          0/1     Completed   0               107s
hippo-instance1-9rqm-0           4/4     Running     0               6m44s
hippo-repo-host-0                2/2     Running     0               6m44s
postgresql-101-97f59c5c7-hm26w   1/1     Running     6 (3m41s ago)   6m44s
```

Don't worry too much about the restarts. The restarts happen because everything
gets initialized all at once. The API pod fails to connect to the database
initially because it takes a while for the database instance to initialize.

## View
Finally, that we have the pods running, we can access the API and see what's up.

To do that, let's port-forward a local port to the port in the cluster.

```Shell
$ kubectl port-forward service/postgresql-101 -n postgresql-101 8888:80
Forwarding from 127.0.0.1:8888 -> 80
Forwarding from [::1]:8888 -> 80
```

Once we forwarded the port, we can access the API using `localhost:8888`.

The API also comes pre-equipped with swagger, which lets you see the API
definition and even test it out. Access swagger here (in a browser):
`http://localhost:8888/swagger`. Feel free to play around with it.

You can also access the API by using `curl`. You can add todo items by running
this:

```Shell
$ curl -X 'POST' 'http://localhost:8888/TodoItem/Add?item=yourTodoItem'
```

You can view the items you added like so:

```Shell
$ curl -X 'GET' 'http://localhost:8888/TodoItem/GetAll'
```

All these calls work with the actual database instance that the operator
created.

## Cleanup

Lets get rid of the cluster we made so we have a clean slate to deploy in other
steps.

```Shell
$ kubectl delete -k deploy/01_quickstart
namespace "postgresql-101" deleted
service "postgresql-101" deleted
deployment.apps "postgresql-101" deleted
postgrescluster.postgres-operator.crunchydata.com "hippo" deleted
```