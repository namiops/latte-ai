## NOTES

This Helm chart is under draft.  
The chart must not be used for any purpose except for the current evaluation of the Zalando postgres-operator.

# agora-zalando-postgresql

![Version: 0.0.0](https://img.shields.io/badge/Version-0.0.0-informational?style=flat-square)

The chart for deploying a PostgreSQL cluster managed by Zalando's postgres-operator. The chart includes common configurations for the PostgreSQL cluster deployment to reduce the burden to write manifests for it from scratch.

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| City OS Platform | <wcm-city-os-platform@woven-planet.global> |  |

## Values

| Key | Type | Default | Description | Status |
|:-----|:------:|:---------|:-------------|:--------:|
| annotations | object | `nil` | Annotations for the PostgresCluster resource | usable |
| audit_trail | ojbect | `nil` | Audit Trail configuration | usable |
| audit_trail.engine | object | `nil` | Audit Trail engine choice | usable |
| audit_trail.engine.pgaudit.enable | bool | `false` | Activate pgAudit audit log method | usable |
| audit_trail.engine.log_statement.enable | bool | `false` | Activate log_statement audit log method | usable |
| audit_trail.engine.log_statement.type | string | `ddl` | Tell what kind of statement to logs.<br>See [5] for details | usable |
| backupConfiguration | object | `{"accessKeyIDName":"AWS_ACCESS_KEY_ID","bucketName":null,"bucketRegion":"ap-northeast-1","credentialSecretName":null,"secretAccessKeyName":"AWS_SECRET_ACCESS_KEY"}` | Backup configurations.<br>NOTE: incompatible value with the "agora-postgres-cluster" chart. | beta |
| backupConfiguration.bucketName | string | `nil` | Bucket name | usable |
| backupConfiguration.bucketRegion | string | `"ap-northeast-1"` | Regions for the bucket | beta |
| backupConfiguration.objectKeyPrefix | string | `"postgresql-wal"` | The object key prefix for the WAL backup on an S3 bucket | usable |
| backupConfiguration.objectKeyPostfix | string | `nil` | The object key postfix for the WAL backup on an S3 bucket - used a sub-path | usable |
| backupConfiguration.secretAccessKeyName | string | `"AWS_SECRET_ACCESS_KEY"` | The K8S secret key name for credential's secret access key | deprecated |
| backupConfiguration.accessKeyIDName | string | `"AWS_ACCESS_KEY_ID"` | The K8S secret key name for credential's access key ID | deprecated |
| backupConfiguration.credentialSecretName | string | `nil` | The name of K8S secret holding AWS IAM credential to access a S3 bucket for backup | deprecated |
| backupConfiguration.useEksIRSA | bool | `true` | Use EKS IRSA instead of AWS IAM TOKEN - set to `false` if you want to fallback to AWS IAM TOKEN | usable |
| backupConfiguration.physicalBackup | object | - | Configuration for physical backup of your cluster | usable |
| backupConfiguration.physicalBackup.numRetain | integer | 5 | Number days to keep backups before pruning | usable |
| backupConfiguration.physicalBackup.schedule | string | Cron-line string to schedule your backup | usable |
| ha | object | - | HA configuration | beta |
| ha.master.lb | bool | `false` | HA via LoadBalancer for leader | beta |
| ha.master.pool | bool | `false` | HA via PgBouncer for leader | beta |
| ha.master.pool_lb | bool | `false` | HA via LoadBalancing PgBouncer for leader | beta |
| ha.replicas.lb | bool | `false` | HA via LoadBalancer for replicas | beta |
| ha.replicas.pool | bool | `false` | HA via PgBouncer for replicas | beta |
| ha.replicas.pool_lb | bool | `false` | HA via LoadBalancing PgBouncer for replicas | beta |
| monitoring | object | - | Monitoring block | usable |
| monitoring.speedway.enable | bool | `false` | Manage to add extra PodAnnotations if set to `true` | usable |
| name | string | `nil` | Name of the PostgresCluster resource | usable |
| numberOfInstances | int | `2` | Number of database instances<br>NOTE: incompatible value with the "agora-postgres-cluster" chart. | usable |
| patroni | object | - | Patroni configuration | partially |
| patroni.agora.logLevel | string | `"INFO"` | Log level | usable |
| patroni.initdb | object | `{"encoding" : "UTF-8", "locale":"en_US.UTF-8", "data-checksums","true"}` | Patroni initdb parameters definiting encoding, locale and if daemon can detect hardware failure via CRC | usable |
| patroni._pg_hba | object | - | Placeholder for a future improvement [3] | usable as is |
| preparedDatabases | object | - | Section to provision roles ( reader / writer / owner ) for specific databases | usable [6] |
| preparedDatabases.[dbName] | object | - | Block to create [dbName] and let Operator manage roles for `[dbName]_[reader|writer|owner][_user]?` - See [6] for an example | usable [6] |
| postgresql | object | - | PostgreSQL specific parameters | usable |
| postgresql.databases | object | - | Parameters related to Databases / Users / Extensions - See [7] for an example | usable [7] |
| postgresql.databases.[dbName] | object | - | The key is the database you'd like to provision | usable |
| postgresql.databases.[dbName].userPrefix | string | - | The username prefix used by operator to create `_user_[reader|writer|owner]` | usable |
| postgresql.databases.[dbName].schema | object | `public` | Databases schema you are planning to use - Default to `public` - roles will have access exclusively to this schema | usable |
| postgresql.databases.[dbName].extensions | string | - | A comma separated list of extensions you'd like to create extension at startup | usable |
| postgresql.kubernetes | object | - | Parameters related to Kubernetes | usable |
| postgresql.kubernetes.resources | object | - | Kubernetes resources for cpu and memory split by limits / requests | usable |
| postgresql.kubernetes.resources.limits | object | `{ cpu: "" , memory: "" }` | Kubernetes resources for cpu and memory for limits | usable |
| postgresql.kubernetes.resources.requests | object | `{ cpu: "" , memory: "" }` | Kubernetes resources for cpu and memory for requests | usable |
| postgresql.parameters | object | `{shared_buffers: "32MB",max_connections: "10",log_statement: "all",log_destination: "csvlog",track_activity_query_size: "2048"}` | PostgreSQL parameters. | usable [1] |
| postgresql.version | string | `"16"` | Deployed PostgreSQL version. | usable |
| ses | object | `nil` | databases the operator will automatically create. The object should be construct like { <database name>: <database owner>, ... }<br>NOTE: incompatible value with the "agora-postgres-cluster" chart. | **UNSURE** |
| spilo | object | - | Parameters for Spilo container | usable as is |
| spilo.id | object | Spilo Unix IDs | | |
| spilo.id.user | integer | `101` | Unix user ID of Spilo process | |
| spilo.id.group | integer | `103` | Unix group ID of Spilo process | |
| spilo.callback_script| object | | Callback configuration for Spilo | | 
| spilo.callback_script.script | string | `databaseInitSQL.sh` | Callback script name that Spilo will run | |
| spilo.sql | object | `nil` | SQL scripts for various event on Cluster life cycle | |
| spilo.sql.on_start | string | `nil` | SQL script run *AFTER* PgSQL is up and running. Be careful that this can be run more than once - Make your script robust enough. | |
| spilo.sql.on_stop | string | `nil` | SQL script run *BEFORE* PgSQL is getting down. | |
| storage | object | - | Storage configuration | partially | 
| storage.class | object | `{"ebsVolumeType":"gp3","provisioner":"ebs.csi.aws.com","volumeBindingMode":"WaitForFirstConsumer","wovenLabels":{"app":"cityos","deployment":"other","env":"dev","orgCode":"AC810"}}` | Storage class configuration A storage class is created unless an existing storage class is explictly specified. | partially | 
| storage.class.create | bool | `true` | Create a StorageClass for this deployment is true. Else use the pgAgora default one | usable |
| storage.class.default_name | string | `nil` | Default name for StorageClass to use for PVC. Only in use if `storage.class.create` is set to `false` else unused<br>Three values are accepted :<br>- `smc-default` if your target SMC env<br>- `agora-default` if your target dev / dev2 / lab2 env<br>- any other existing StorageClassName you'd like to use<br><br>You have to provide a value else exception is raised ! | usable |
| storage.class.provisioner | string | `ebs.csi.aws.com` | Provisioner name | usable [2] |
| storage.class.wovenLabels | object | `{"app":"cityos","deployment":"other","env":"dev","orgCode":"AC810"}` | Woven tags and labels for resource management.<br>For now, the chart uses labels for the Agora team. See: https://security.woven-planet.tech/information-security-policy/asset-tagging-standard/#default-labelstags | usable |
| storage.class.wovenLabels.app | string | `"cityos"` | Application name | usable |
| storage.volume.size | string | `"2Gi"` | PGDATA volume size | usable |
| targetEnv | string | `nil` | Flag to target a specific environment.<br>- Speedway Dev ( `speedway-dev` )<br>- Speedway Prod ( `speedway-prod` )<br>- Speedway Dev ML ( `speedway-ml` )<br>- Agora Any ( `agora-any` )<br>- {empty} for legacy behavior | usable |
| users | object | - | Users and databases management - See [4] for full example | usable |
| users.`[user]` | object | - | Start the definition block for `[user]` - `[user]` can't contain `-` | usable |
| users.`[user]`.databases | object | - | List of databases which will be owned by `[user]` - database names can't contain `-` | usable |
| users.`[user]`.options | object | - | List of options attached to `[user]`. Currently only supported value by Operator is `superuser` | usable |

[1] This block is unserialized "as-is" so no syntax / semantic check is done. Details [here](https://github.com/postgres/postgres/blob/master/src/backend/utils/misc/postgresql.conf.sample)  
[2] Untested with any other storage provider  
[3] `pg_hba` is an ACL block telling to PostgreSQL from where, who and how an incoming connection is handled. More details [here](https://www.postgresql.org/docs/current/auth-pg-hba-conf.html)  
[4] Say we want to create a database `my_db` owned by user `my_user` and granting `my_user` superuser role, it would be this block :
```
users:
  my_user:
    databases:
      - my_db
  options:
    - superuser
```  
[5] List of available values [here](https://postgresqlco.nf/doc/en/param/log_statement/)
[6] Say we want to create a database `my_db` owned by user `my_user` and create roles reader / writer / owner who have access to `my_db` `public` schema,  
with bonus adding extension `pgcrypto`,  it will be this block :
```
postgresql:
  databases:
    my_db:
      userPrefix: my_user
      schema: public
      extensions: pgcrypto
```
[7] Say we want to create a database `my_db` owned by user `my_user` and have a strict separation between reader / writer / owner role on `public` schema, it would be this block :
```
databases:
  my_db: my_user_owner
preparedDatabases:
  my_db:
    strictRoles: true
    schemas:
      public:
        strictUsers: true
```
/!\ If you disable `strictRoles` ( ie set it to `false` ) then `strictUsers` can't be set to `true`. It is sementically prohited by Helm template.
[6] & [7] More details here :
 - [Prepared databases](https://github.com/zalando/postgres-operator/blob/master/docs/user.md#prepared-databases-with-roles-and-default-privileges)
 - [Default LOGIN roles](https://github.com/zalando/postgres-operator/blob/master/docs/user.md#default-login-roles)
 - [search-path](https://github.com/zalando/postgres-operator/blob/master/docs/user.md#schema-search_path-for-default-roles)

## Usage

The WAL backup is stored in object storage via `WAL-G`, which is one of the two backup solutions in the Zalando's postgres-operator with `WAL-E`.
We plan to utilize IRSA but this chart currently only supports using IAM credentials for the evaluation purposes.
The credentials must be stored in a K8S secret in advance to deploy a PostgreSQL resource,
and the secure name and data keys for credentials must be specified in the `values.yaml` file.

## Links

- AWS EKS IRSA :
  - https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html
  - https://docs.aws.amazon.com/eks/latest/userguide/associate-service-account-role.html
- https://github.com/zalando/postgres-operator/blob/master/docs/user.md
- https://patroni.readthedocs.io/en/latest/dynamic_configuration.html
- https://helm.sh/docs/chart_best_practices/templates/
- https://olof.tech/helm-values-inheritance-one-liner/
- https://github.com/zalando/postgres-operator/issues/264
- Helm Chart :
  - DNS issue we faced to import : https://github.com/zalando/postgres-operator/blob/master/charts/postgres-operator/templates/_helpers.tpl#L10
- mTLS side-effects :
  - https://github.com/CrunchyData/postgres-operator/issues/2534#issuecomment-1001435543
- https://www.percona.com/blog/monitoring-a-postgresql-patroni-cluster/
- https://patroni.readthedocs.io/en/latest/rest_api.html
- https://helm.sh/docs/chart_template_guide/function_list/#dictionaries-and-dict-functions
- Work done for PGO Crunchy we must use as well : https://github.com/CrunchyData/postgres-operator/issues/2341#issuecomment-1340369303
- https://github.com/le0pard/pgtune
- https://thedatabaseme.de/2023/02/19/a-pinch-of-salt-encrypt-wal-g-postgresql-backups/
- https://thedatabaseme.de/2022/03/26/backup-to-s3-configure-zalando-postgres-operator-backup-with-wal-g/-
- https://www.pgbouncer.org/features.html
- https://patroni.readthedocs.io/en/latest/ENVIRONMENT.html
- https://github.com/CrunchyData/pgmonitor/issues/353#issuecomment-1640989756
- In a browser with proper SSO to Azure :
  - https://observability.cityos-dev.woven-planet.tech/prometheus/api/v1/label/postgres_operator_crunchydata_com_cluster/values
- PostgreSQL
  - https://www.postgresql.org/docs/15/runtime-config-statistics.html
  - https://www.postgresql.org/docs/current/libpq-ssl.html#LIBPQ-SSL-SSLMODE-STATEMENTS
  - https://www.postgresql.org/docs/current/runtime-config-logging.html
  - https://www.timescale.com/blog/postgres-toast-vs-timescale-compression/
  - https://www.postgresql.org/docs/16/sql-cluster.html
  - https://pgxn.org/dist/postgresql_anonymizer/
  - https://www.cybertec-postgresql.com/en/transparent-data-encryption-installation-guide/
  - https://www.postgresql.org/docs/16/storage-toast.html
  - https://www.enterprisedb.com/blog/improving-postgresql-performance-without-making-changes-postgresql
  - https://github.com/jbranchaud/til/blob/master/postgres/insert-a-bunch-of-records-with-generate-series.md
- Helm :
  - https://helm.sh/docs/howto/charts_tips_and_tricks/#automatically-roll-deployments
- Misc :
  - https://github.com/zalando/postgres-operator/blob/master/manifests/complete-postgres-manifest.yaml

## Tips

To test locally :
```
$ helm template . -n dummy-ns --set name=dummy-name --debug
```

See `Makefile` for other options

To transfer data to a pod :
For example here copy `patroni_exporter` :
```
$ dd if=patroni_exporter  | kubectl exec -n postgresql-sample zalando-postgresql-sample-0 -it -- cp /proc/self/fd/0 patroni_exporter
```
If you want to keep permission you can pipe in a tar ball and use `tar -xf -` instead of the `cp` .
