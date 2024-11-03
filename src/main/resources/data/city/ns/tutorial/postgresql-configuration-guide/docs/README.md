# Postgres Configuration Guide
This document is intended as a baseline guide for service team developers to
generate a working postgres cluster to deploy on Agora. It is composed of,
first, an example working kubernetes yaml file defining a PostgresCluster
object as well as a table defining each of those keys. The second is a
collection of issues and important sections within the yaml definitions that
are necessary to ensure the database cluster runs without issue.

## Reference postgres.yaml
The below spec is a _reasonable_ spec that could be used by an application.
However service teams should take care to validate that values used in the file
are suitable for themselves. For example is the schedule of backups sufficient
for the team.

Links lead to anchors for the references below.

 <pre>
<a href="#postgrescluster-apiversion">apiVersion</a>: postgres-operator.crunchydata.com/v1beta1
<a href="#postgrescluster-kind">kind</a>: PostgresCluster
metadata:
  <a href="#postgrescluster-metadataname">name</a>: postgresql
  <a href="#postgrescluster-metadatanamespace">namespace</a>: test-namespace
<a href="#postgrescluster-spec">spec</a>:
  <a href="#postgrescluster-spec-pgversion">postgresVersion</a>: 14
  <a href="#postgrescluster-spec-users">users</a>:
    - <a href="#postgrescluster-spec-users-name">name</a>: dbuser
      <a href="#postgrescluster-spec-users-databases">databases</a>:
        - records
      <a href="#postgrescluster-spec-users-options">options</a>: "SUPERUSER"
  <a href="#postgrescluster-spec-patroni">patroni</a>:
    dynamicConfiguration:
      postgresql:
        <a href="#postgrescluster-spec-patroni-hba">pg_hba</a>:
          - "hostnossl all all all md5" # Allow ssl disabled
  <a href="#postgrescluster-spec-instances">instances</a>:
    - <a href="#postgrescluster-spec-instances-name">name</a>: instance1
      metadata:
        annotations:
          <a href="#postgrescluster-spec-instances-prometheus-port">prometheus.io/port</a>: "9187"
          <a href="#postgrescluster-spec-instances-prometheus-scrape">prometheus.io/scrape</a>: "true"
      <a href="#postgrescluster-spec-instances-replicas">replicas</a>: 3
      dataVolumeClaimSpec:
        <a href="#postgrescluster-spec-instances-dvc-access">accessModes</a>:
          - "ReadWriteOnce"
        resources:
          requests:
            <a href="#postgrescluster-spec-instances-dvc-storage">storage</a>: 200Gi
  backups:
    <a href="#postgrescluster-spec-pgbackrest">pgbackrest</a>:
      metadata:
        annotations:
          <a href="#postgrescluster-pgbackrest-annotations">proxy.istio.io/config</a>: '{ "holdApplicationUntilProxyStarts": true }'
      <a href="#postgrescluster-pgbackrest-command">command</a>:
        - /opt/crunchy/bin/custom_entrypoint.sh
        - /opt/crunchy/bin/pgbackrest
      global:
        <a href="#postgrescluster-pgbackrest-retention-full">repo1-retention-full</a>: "7"
        <a href="#postgrescluster-pgbackrest-retention-type">repo1-retention-full-type</a>: time
      repos:
        - <a href="#postgrescluster-pgbackrest-name">name</a>: repo1
          volume:
            volumeClaimSpec:
              <a href="#postgrescluster-pgbackrest-access">accessModes</a>:
                - "ReadWriteOnce"
              resources:
                requests:
                  <a href="#postgrescluster-pgbackrest-storage">storage</a>: 200Gi
          schedules:
            <a href="#postgrescluster-pgbackrest-full">full</a>: "0 1 * * *"
            <a href="#postgrescluster-pgbackrest-incremental">incremental</a>: "0 */4 * * *"
      manual:
        <a href="#postgrescluster-pgbackrest-reponame">repoName</a>: repo1
        <a href="#postgrescluster-pgbackrest-options">options</a>:
          - --type=full
  <a href="#postgrescluster-spec-openshift">openshift</a>: false
  <a href="#postgrescluster-spec-monitoring">monitoring</a>:
    pgmonitor:
      exporter:
        # To inject the Prometheus exporter sidecar container,
        # we need to specify the "image" attribute.
        # The actual image URL is fulfilled with the default one
        # specified in the operator deployment.
        # See: https://access.crunchydata.com/documentation/postgres-operator/v5/tutorial/monitoring/
        <a href="#postgrescluster-monitoring-image">image</a>: ""
---
<a href="#serviceentry-apiversion">apiVersion</a>: networking.istio.io/v1alpha3
<a href="#serviceentry-kind">kind</a>: ServiceEntry
metadata:
  <a href="#serviceentry-metadata-name">name</a>: postgresql
  <a href="#serviceentry-metadata-namespace">namespace</a>: test-namespace
<a href="#serviceentry-spec">spec</a>:
  <a href="#serviceentry-spec-hosts">hosts</a>:
    - postgresql-pods.test-namespace.svc.cluster.local
  <a href="#serviceentry-spec-location">location</a>: MESH_INTERNAL
  <a href="#serviceentry-spec-ports">ports</a>:
    - <a href="#serviceentry-port-5432">number</a>: 5432
      name: tcp-postgresql
      protocol: TCP
    - <a href="#serviceentry-port-2022">number</a>: 2022
      name: tcp-pgbackrest
      protocol: TCP
    - <a href="#serviceentry-port-8008">number</a>: 8008
      name: tcp-pgbackrest-patroni-rest-api
      protocol: TCP
    - <a href="#serviceentry-port-8432">number</a>: 8432
      name: tcp-pgbackrest-tls
      protocol: TCP
  <a href="#serviceentry-spec-resolution">resolution</a>: NONE
  workloadSelector:
    labels:
      <a href="#serviceentry-spec-cluster">postgres-operator.crunchydata.com/cluster</a>: postgresql
  <a href="#serviceentry-spec-exportto">exportTo</a>:
    - .
 </pre>

## Key Definitions

The following table details the keys used in the above specification. For
fields with recommended constant values the value used is listed in
'Fixed Value'. Some objects have been flattened to avoid high levels of
nesting. See [here](https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/) for the full CRD reference.

### postgres.yaml
<br>

#### PostgresCluster
Key | Explanation | Fixed Value
----| ----------- | -----------
<a id="postgrescluster-apiversion">apiVersion</a> | The api version of the postgres operator API used | postgres-operator.crunchydata.com/v1beta1
<a id="postgrescluster-kind">kind</a> | The type of resource to create | PostgresCluster
<a id="postgrescluster-metadataname">metadata.name</a>  | The name of the resource you are creating |
<a id="postgrescluster-metadatanamespace">metadata.namespace</a>  | The namespace you are creating the cluster in |
<a id="postgrescluster-spec">spec</a>  | PostgresClusterSpec defines the desired state of the PostgresCluster |

#### PostgresCluster.spec
Key | Explanation | Fixed Value
----| ----------- | -----------
<a id="postgrescluster-spec-pgversion">postgresVersion</a> | The major version of PostgreSQL installed in the image | 14
<a id="postgrescluster-spec-users">users</a> | Users to be created inside Postgres and the databases they should access. |
<a id="postgrescluster-spec-patroni">patroni</a> | Set patroni configuration, manages cluster customization especially as related to high availability | 
<a id="postgrescluster-spec-instances">instances</a> | Specifies one or more sets of PostgreSQL pods that replicate data for this cluster | 
<a id="postgrescluster-spec-pgbackrest">backups.pgbackrest</a> | Sets pgbackrest archive configuration | 
<a id="postgrescluster-spec-openshift">openshift</a> | Defines whether or not the Postgres cluster is being deployed to an openshift environment | false
<a id="postgrescluster-spec-monitoring">monitoring</a> | The specification of monitoring tools that connect to PostgreSQL | 

#### PostgresCluster.spec.users[index]
Key | Explanation | Fixed Value
----| ----------- | -----------
<a id="postgrescluster-spec-users-name">name</a> | The name of the PostgreSQL User. The value may contain only lowercase letters, numbers, and hyphen so that it fits into Kubernetes metadata. | 
<a id="postgrescluster-spec-users-databases">databases[index]</a> | Databases to which this user can connect and create objects. Removing a database from this list does NOT revoke access. This field is ignored for the "postgres" user. | 
<a id="postgrescluster-spec-users-options">options</a> | ALTER ROLE options except for PASSWORD. This field is ignored for the "postgres" user. More info: https://www.postgresql.org/docs/current/role-attributes.html | 

#### PostgresCluster.spec.patroni
Key | Explanation | Fixed Value
----| ----------- | -----------
<a id="postgrescluster-spec-patroni-hba">dynamicConfiguration.postgresql.pg_hba</a> | This is the setting of the pg_hba.conf file. Entries are recommended to use 'hostnossl' for the first field as istio already provides encryption through mTLS. | 

#### PostgresCluster.spec.instances[index]
Key | Explanation | Fixed Value
----| ----------- | -----------
<a id="postgrescluster-spec-instances-name">name</a> | Name that associates this set of Postgres pods. | 
<a id="postgrescluster-spec-instances-prometheus-port">metadata.annotations.prometheus.io/port</a> | Annotation for the port for prometheus to scan for metrics aggregation | 9187
<a id="postgrescluster-spec-instances-prometheus-scrape">metadata.annotations.prometheus.io/scrape</a> | Annotation asking should prometheus scrape this instance for metrics. Should be true so long as you want metrics. | 
<a id="postgrescluster-spec-instances-replicas">replicas</a> | Number of pods to create for the database. |
<a id="postgrescluster-spec-instances-dvc-access">dataVolumeClaimSpec.accessModes[index]</a> | AccessModes contains the desired access modes the volume should have. More info: https://kubernetes.io/docs/concepts/storage/persistent-volumes#access-modes-1 | 
<a id="postgrescluster-spec-instances-dvc-storage">dataVolumeClaimSpec.resources.requests.storage</a> | Total storage requested for the volume, data available for your cluster | 

#### PostgresCluster.spec.backups.pgbackrest
Key | Explanation | Fixed Value
----| ----------- | -----------
<a id="postgrescluster-pgbackrest-annotations">metadata.annotations.proxy.istio/config</a> | Specific annotation used to prevent the application from starting until istio proxy is started. Necessary to allow pods to communicate | '{ "holdApplicationUntilProxyStarts": true }'
<a id="postgrescluster-pgbackrest-command">command[index]</a> | Array of commands to run to explicitly terminate the istio sidecar when the backup or restore jobs finish. | - /opt/crunchy/bin/custom_entrypoint.sh, - /opt/crunchy/bin/pgbackrest
<a id="postgrescluster-pgbackrest-retention-full">global.\[repo name\]-retention-full</a> | Full backup retention count/time. https://pgbackrest.org/configuration.html |
<a id="postgrescluster-pgbackrest-retention-type">global.\[repo name\]-retention-full-type</a> | Full backup retention type. https://pgbackrest.org/configuration.html |
<a id="postgrescluster-pgbackrest-name">repos[index].name</a> | The name of the repository. Used for the repository settings in global retention types. | 
<a id="postgrescluster-pgbackrest-access">repos[index].volume.volumeClaimSpec.accessModes[index]</a> | AccessModes contains the desired access modes the volume should have. More info: https://kubernetes.io/docs/concepts/storage/persistent-volumes#access-modes-1. Used specifically for the backup volume.
<a id="postgrescluster-pgbackrest-storage">repos[index].volume.volumeClaomSpec.resources.requests.storage</a> | Total storage requested for the volume, data available for your cluster. Used specifically for the backup volume.
<a id="postgrescluster-pgbackrest-full">repos[index].schedules.full</a> | Cron format for how often to take full backups of the database | 
<a id="postgrescluster-pgbackrest-incremental">repos[index].schedules.incremental</a> | Cron format for how often to take incremental backups of the database | 
<a id="postgrescluster-pgbackrest-reponame">manual.reponame</a> | The name of the pgBackRest repo to run the backup command against | 
<a id="postgrescluster-pgbackrest-options">manual.options[index]</a> | Command line options to include when running the pgBackRest backup command | 

#### PostgresCluster.spec.monitoring
Key | Explanation | Fixed Value
----| ----------- | -----------
<a id="postgrescluster-monitoring-image">pgmonitor.exporter.image</a> | The image used for the sidecar metrics exporter. Explicitly should be set to "" because the operator will specify the correct image, but is required. | ""


### service-entry.yaml
<br>

#### ServiceEntry
Key | Explanation | Fixed Value
--- | ----------- | -----------
<a id="serviceentry-apiversion">apiVersion</a> | The api version of the istio API used | networking.istio.io/v1alpha3
<a id="serviceentry-kind">kind</a> | The type of Resource to create. | ServiceEntry
<a id="serviceentry-metadata-name">metadata.name</a> | The name of the Resource you are creating. | 
<a id="serviceentry-metadata-namespace">metadata.namespace</a> | The namespace where you are creating the Postgres cluster. Should match the PostgresCluster namespace | 
<a id="serviceentry-spec">spec</a> | Defines the desired spec of the ServiceEntry object | 

#### ServiceEntry.spec
Key | Explanation | Fixed Value
----| ----------- | -----------
<a id="serviceentry-spec-hosts">hosts[index]</a> | Cluster internal location of the postgresql pods | \[PostgresCluster metadata.name\]-pods.\[namespace\].svc.cluster.local
<a id="serviceentry-spec-location">location</a> | Specify whether the service should be considered external to the mesh or part of the mesh. | MESH_INTERNAL
<a id="serviceentry-spec-ports">ports[index]</a> | Port object(s) to expose in the Service Entry | 
<a id="serviceentry-spec-resolution">resolution</a> | Service discovery mode for the hosts | NONE
<a id="serviceentry-spec-cluster">workloadSelector.labels.postgres-operator.crunchydata.com/cluster</a> | |
<a id="serviceentry-spec-exportto">exportTo[index]</a> | The ’exportTo’ field allows for control over the visibility of a service declaration to other namespaces in the mesh. By default, a service is exported to all namespaces. | 

#### ServiceEntry.spec.ports Required
Number | Name | Protocol
------ | ---- | --------
<a id="serviceentry-port-5432">5432</a> | tcp-postgresql | TCP
<a id="serviceentry-port-2022">2022</a> | tcp-pgbackrest | TCP
<a id="serviceentry-port-8008">8008</a> | tcp-pgbackrest-patroni-rest-api | TCP
<a id="serviceentry-port-8432">8432</a> | tcp-pgbackrest-tls | TCP

## Important notes

* Generally avoid adding images to the kubernetes specification. The operator we are using deploys custom patched images that allows the postgres operator to work properly when used with istio and will do so automatically without you needing to specify an image.
* Be sure to set up a working backup configuration when defining your specification. If you do not have a correctly working backup configuration, the operator cannot create a working backup, which results in it not pruning WAL files which will eventually fill up your volume and crash your cluster.