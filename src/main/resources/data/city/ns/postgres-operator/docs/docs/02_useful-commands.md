# Troubleshooting and Useful commands

## Finding the leader pod

```
kubectl exec -n <namespace> -it <one of the pods name> -- patronictl list
```

Example
```
kubectl exec -n postgresql-sample -it database-instance1-c6bq-0 -- patronictl list
+---------------------------+-----------------------------------------+---------+---------+----+-----------+
| Member                    | Host                                    | Role    | State   | TL | Lag in MB |
+ Cluster: database-ha (7205892414347587751) -------------------------+---------+---------+----+-----------+
| database-instance1-c6bq-0 | database-instance1-c6bq-0.database-pods | Leader  | running |  9 |           |
| database-instance1-psnx-0 | database-instance1-psnx-0.database-pods | Replica | running |  9 |         0 |
+---------------------------+-----------------------------------------+---------+---------+----+-----------+
```

## Migrating a db to a different db

You can migrate your data from one db to another by using the Postgres operator [Clone a Postgres Cluster](https://access.crunchydata.com/documentation/postgres-operator/5.3.0/tutorial/disaster-recovery/#clone-a-postgres-cluster) feature. We recommend manually taking a backup before this operation.

You can also [manually take a backup](#manually-taking-a-backup) and then [apply](#manually-applying-a-backup) it to your new db.

## Manually taking a backup

You can do so by executing this command:

```
kubectl exec -n <namespace> -it <pod-name> -- pg_dumpall --no-role-passwords > db-backup-$(date "+%F-%T").sql
```

Example

```
kubectl exec -n postgresql-sample -it database-instance1-c6bq-0 -- pg_dumpall --no-role-passwords > db-backup-$(date "+%F-%T").sql
```

## Manually applying a backup

```
kubectl exec -n <namespace> -it <new database's leader instance pod> -- psql < <dump file path>
```

Example

```
kubectl exec -n postgresql-sample -it database-instance1-c6bq-0 -- psql < db-backup-2023-07-27-00:27:32.sql
```

## Gettting auth information from the secret

You can view your credentials like so

```
kubectl get secret <cluster-name>-pguser-<user-name> -n <namespace> -o jsonpath='{.data.password}' | base64 -d
```

Example

```
kubectl get secret database-pguser-database -n postgresql-sample -o jsonpath='{.data.password}' | base64 -d
```

More info on the [official documentation website](https://access.crunchydata.com/documentation/postgres-operator/5.3.0/quickstart/#connect-to-the-postgres-cluster).
