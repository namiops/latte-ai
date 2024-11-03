# Manually take dump files for all database clusters

We need to manually take backups for all database clusters on an environment when we apply some cluster-wide postgres-operator-related updates.

To do it quickly in an automated way, we have a small Python script. Here is how to use it.

```bash
python ns/postgres-operator/docs/runbooks/dump_all_database_clusters/dump_all_database_clusters.py --help
usage: dump_all_database_clusters.py [-h] [--output OUTPUT] [--debug]

This small script takes dump files for each PostgresCluster resource running in the target K8S cluster. The dump files are taken by executing `pg_dumpall --no-role-passwords`. Each dump file will be stored with the name `<kubectl context
name>-<namespace>-<PostgresCluster resource name>.sql`. This script depends on `kubectl` and `jq` commands locally installed. Before running it, make sure "kubectl" uses the configuration of the target K8S cluster.

options:
  -h, --help       show this help message and exit
  --output OUTPUT  The folder to store the dump files. If the folder doesn't exist, it will be created.
  --debug          If it's specified, every executed commands will be outputted.
```
