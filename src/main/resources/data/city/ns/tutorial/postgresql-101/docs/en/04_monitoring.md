# Monitoring

Now that we have successfully backed up our cluster lets get in to
understanding how we can monitor our clusters.

## Spec Changes

First lets add two changes to the postgres cluster specifications so that our
observability stack (prometheus and grafana) will be able to pick up metrics
from our postgres cluster.

```yaml
metadata:
        annotations:
          prometheus.io/port: "9187"
          prometheus.io/scrape: "true"
```
and
```yaml
monitoring:
        pgmonitor:
          exporter:
            # To inject the Prometheus exporter sidecar container,
            # we need to specify the "image" attribute.
            # The actual image URL is fulfilled with the default one
            # specified in the operator deployment.
            # See: https://access.crunchydata.com/documentation/postgres-operator/v5/tutorial/monitoring/
            image: ""
```

The first snippet is attached to the instance specification, providing two
annotations that let prometheus know which port to connect to, and if it
should be scraping metrics from these instances. 

The second snippet is attached to the spec, and is used to define that we
need an exporter sidecar pod to send metrics to prometheus. As you can see
from the comments we require the image to be defined, but the actual image
itself will be populated by the operator.

## Deploy Cluster

So let's deploy these changes.

```shell
$ kubectl apply -k deploy/03_monitoring
namespace/postgresql-101 created
service/postgresql-101 created
deployment.apps/postgresql-101 created
serviceentry.networking.istio.io/hippo created
postgrescluster.postgres-operator.crunchydata.com/hippo created
```

Lets continue to verify until all our pods are running successfully:

```shell
$ kubectl get po -n postgresql-101
NAME                              READY   STATUS      RESTARTS   AGE
hippo-backup-4ngj-5lv55           0/2     Completed   0          51s
hippo-instance1-5tnw-0            5/5     Running     0          74s
hippo-repo-host-0                 2/2     Running     0          74s
postgresql-101-678cdfd8cc-jp5rh   2/2     Running     2          75s
```

You can see above that we now have 5/5 containers running in our
hippo-instance1-5tnw-0 pod. Let's be a bit more specific and verify that the
exporter is running.

```shell
$ kubectl get po -n postgresql-101 -o jsonpath='{range .items[*]}{"\n"}{.metadata.name}{"\t"}{.metadata.namespace}{"\t"}{range .spec.containers[*]}{.name}{"=>"}{.image}{","}{end}{end}'|sort|column -t | grep instance
```

We can see that there is an exporter image among the containers in the pod. We
could alternatively use k9s, which would drill in a bit prettier.

Additionally we can check that our exporter container is actually serving data
so that postgres can reach our metrics.

```shell
$ kubectl exec -n postgresql-101 hippo-instance1-5tnw-0 -it -- curl localhost:9187/metrics | head -10
# HELP ccp_archive_command_status_archived_count Number of WAL files that have been successfully archived
# TYPE ccp_archive_command_status_archived_count gauge
ccp_archive_command_status_archived_count{server="localhost:5432"} 1169
# HELP ccp_archive_command_status_failed_count Number of failed attempts for archiving WAL files
# TYPE ccp_archive_command_status_failed_count gauge
ccp_archive_command_status_failed_count{server="localhost:5432"} 0
# HELP ccp_archive_command_status_seconds_since_last_archive Seconds since the last successful archive operation
# TYPE ccp_archive_command_status_seconds_since_last_archive gauge
ccp_archive_command_status_seconds_since_last_archive{server="localhost:5432"} 44.358154
# HELP ccp_archive_command_status_seconds_since_last_fail Seconds since the last recorded failure of the archive_command
```

## Cleanup

And finally we teardown the cluster for the last time

```Shell
$ kubectl delete -k deploy/03_monitoring
namespace "postgresql-101" deleted
service "postgresql-101" deleted
deployment.apps "postgresql-101" deleted
serviceentry.networking.istio.io "hippo" deleted
postgrescluster.postgres-operator.crunchydata.com "hippo" deleted
```