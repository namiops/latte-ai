# Backup
In this section we will deploy a new cluster so we can try backing up our data.

## Run
So let's apply the new cluster that has backup functionality enabled.

```shell
$ kubectl apply -k deploy/02_backup_and_restore
namespace/postgresql-101 created
service/postgresql-101 created
deployment.apps/postgresql-101 created
serviceentry.networking.istio.io/hippo created
postgrescluster.postgres-operator.crunchydata.com/hippo created
```

Similarly to the first section wait until pods are ready

```shell
$ kubectl get po -n postgresql-101
NAME                              READY   STATUS      RESTARTS   AGE
hippo-backup-875h-rpbkz           0/2     Completed   0          38s
hippo-instance1-mmfm-0            4/4     Running     0          61s
hippo-repo-host-0                 2/2     Running     0          63s
postgresql-101-678cdfd8cc-9qlpd   2/2     Running     2          65s
```

At this point we can port-forward and execute against the database identically
to how we did in our first section.

```shell
$ kubectl port-forward service/postgresql-101 -n postgresql-101 8888:80
Forwarding from 127.0.0.1:8888 -> 80
Forwarding from [::1]:8888 -> 80

$ curl -X 'POST' 'http://localhost:8888/TodoItem/Add?item=yourTodoItem'
```

## Scheduled backups

So how do we actually set up backups? Well, we already did!

```yaml
schedules:
    full: "0 1 * * *"
    incremental: "0 */4 * * *"
```

The above excerpt from the repos section of the yaml file is a [cron](https://en.wikipedia.org/wiki/Cron) format of
when we want to take backups of the repositories defined in the file. So we
will be taking full backups at 1:00am and incremental backups every 4 hours.
If you noticed the sample output that was displayed when we waited for the
pods, the backup pod had already executed successfully.

## Manual backups

Of course, sometimes we just want to take a one off backup. Like right now when
we are trying to have a backup we can test with the restore process. And this
cluster is also currently set up to allow for manual backups as we can see in
the below excerpt from the cluster definition.

```yaml
manual:
    repoName: repo1
    options:
        - --type=full
```

To actually execute the backup we also need to add an annotation
`postgres-operator.crunchydata.com/pgbackrest-backup` to the resource.
Crunchydata recommends setting the annotation with a timestamp such as $(date)
so you know when you initiated the backup. However we will be taking a
specific name to match commands given in later guides. So run:

```shell
$ kubectl annotate -n postgresql-101 postgrescluster hippo postgres-operator.crunchydata.com/pgbackrest-backup=backup1
ppo postgres-operator.crunchydata.com/pgbackrest-backup=backup1
postgrescluster.postgres-operator.crunchydata.com/hippo annotated
```

We can see that the new backup has executed successfully
```shell
kubectl get po -n postgresql-101
NAME                              READY   STATUS      RESTARTS   AGE
hippo-backup-4ww6-jrq45           0/2     Completed   0          24s
hippo-backup-875h-rpbkz           0/2     Completed   0          2m34s
hippo-instance1-mmfm-0            4/4     Running     0          2m57s
hippo-repo-host-0                 2/2     Running     0          2m59s
postgresql-101-678cdfd8cc-9qlpd   2/2     Running     2          3m1s
```

But maybe we aren't sure just from seeing a completed pod, so lets see what
actually happened in the logs
```shell
$ kubectl logs -n postgresql-101 hippo-backup-4ww6-jrq45 pgbackrest | tail
time="2022-12-15T06:34:35Z" level=info msg="backrest backup command requested"
time="2022-12-15T06:34:35Z" level=info msg="command to execute is [pgbackrest backup --stanza=db --repo=1 --type=full]"
time="2022-12-15T06:34:44Z" level=info msg="output=[]"
time="2022-12-15T06:34:44Z" level=info msg="stderr=[WARN: option 'repo1-retention-full' is not set for 'repo1-retention-full-type=count', the repository may run out of space\n      HINT: to retain full backups indefinitely (without warning), set option 'repo1-retention-full' to the maximum.\n]"
time="2022-12-15T06:34:44Z" level=info msg="crunchy-pgbackrest ends"
Stop the Istio sidecar proxy
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100     2  100     2    0     0   1000      0 --:--:-- --:--:-- --:--:--  1000
```
We can see the download executed successfully, that signifies that the backup worked!


## Cleanup

As before to teardown the cluster we run

```Shell
$ kubectl delete -k deploy/02_backup_and_restore
namespace "postgresql-101" deleted
service "postgresql-101" deleted
deployment.apps "postgresql-101" deleted
serviceentry.networking.istio.io "hippo" deleted
postgrescluster.postgres-operator.crunchydata.com "hippo" deleted
```