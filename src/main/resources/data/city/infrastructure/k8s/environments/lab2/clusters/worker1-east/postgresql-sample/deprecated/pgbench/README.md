# pgbench configuration files

This folder contains configuration files to execute pgbench targeting the sample PostgreSQL clusters on `Lab2` for testing purposes.

The configuration files are value YAML files for [the Helm chart "pgbench"](/infrastructure/helm/pgbench),
and we can deploy pgbench pods with `helm install` using the value YAML files. 
The chart can take pgbench options through the value YAML file.
We can see the details of the pgbench options in [the PostgreSQL documentation](https://www.postgresql.org/docs/current/pgbench.html).

## pgbench targeting "zalando-postgresql-sample"

The value YAML file is [here](./zalando-postgresql-sample-pgbench-values.yaml).

```bash
$ kubectl config use-context lab2-worker1-east 
$ helm install pgbench-zalando-postgresql-sample -n postgresql-sample infrastructure/helm/pgbench --values infrastructure/k8s/environments/lab2/clusters/worker1-east/postgresql-sample/pgbench/zalando-postgresql-sample-pgbench-values.yaml [specify "--kube-as-user" and "--kube-as-group options"
```
