# How to solve PostgresCluster's WAL clean up issue

Author: Agora storage team (`@agora-storage`)  
Last updated date: Dec 5th, 2022

## Issue

[WAL logs](https://www.postgresql.org/docs/current/wal-intro.html) for the PostgreSQL cluster have never cleaned up and kept increasing the size until consuming every disk space.

## Cause

The backup feature ([pgBackrest](https://pgbackrest.org/)) of [CrunchyData's postgres-operator](https://access.crunchydata.com/documentation/postgres-operator/v5/) that we're currently using to support PostgreSQL usage on the Agora Kubernetes cluster has an issue when it's used inside the [Istio](https://istio.io/) service mesh.
That's the reason why there are some errored Kubernetes jobs for backup in the namespaces, including the PostgresCluster resources.

The error on the backup process also causes the issue of not cleaning up the WAL logs because pgBackrest might control the WAL log clean-up process.
(We used "might" because we haven't found the official documentation clearly mentioning about this.
But we confirmed it through testing on it.)

## Solution

We confirmed that we could trigger the clean-up process by making the backup process work correctly, keeping the existing data.
We have offered the patched container image for the backup process, and here are the instructions on how to apply it to your PostgresCluster resource.

### 1. Take a backup

We confirmed that the existing data is left when we update the backup configuration.
But, if you prefer to make sure to leave the existing data, please dump the data using [pg\_dump](https://www.postgresql.org/docs/current/app-pgdump.html) or other tools.

### 2. Update the backup configuration

There are two things that we need to update the deployment manifests.

The first one is an update on the manifest of the [PostgresCluster](https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/#postgrescluster) resource.
Here is the snippet of the manifest that we need to add.

```yaml
spec:
...
  backups:
    pgbackrest:
      metadata:
        # Annotation to make the job to wait for Istio sidecar initialization
        # See: https://istio.io/latest/docs/ops/common-problems/injection/#pod-or-containers-start-with-network-issues-if-istio-proxy-is-not-ready
        annotations:
          proxy.istio.io/config: '{ "holdApplicationUntilProxyStarts": true }'
      # Description to utilize the patched backup process
      command:
        - /opt/crunchy/bin/custom_entrypoint.sh
        - /opt/crunchy/bin/pgbackrest
...
```

The second thing we need to do is to deploy a [ServiceEntry](https://istio.io/latest/docs/reference/config/networking/service-entry/) to [enable pod-to-pod communication](https://istio.io/latest/docs/ops/configuration/traffic-management/traffic-routing/#serviceentry) between the database pod and the backup process.
Here is a sample manifest of the resource.

```yaml
---
apiVersion: networking.istio.io/v1alpha3
kind: ServiceEntry
metadata:
  name: {{ resource name }}
  namespace: {{ your namespace }}
spec:
  hosts:
    - {{ resource name }}-pods.{{ your namespace }}.svc.cluster.local
  location: MESH_INTERNAL
  ports:
    - number: 5432
      name: tcp-postgresql
      protocol: TCP
    - number: 2022
      name: tcp-pgbackrest
      protocol: TCP
    - number: 8008
      name: tcp-pgbackrest-patroni-rest-api
      protocol: TCP
    - number: 8432
      name: tcp-pgbackrest-tls
      protocol: TCP
  resolution: NONE
  workloadSelector:
    labels:
      postgres-operator.crunchydata.com/cluster: {{ your PostgresCluster resource name }}
  exportTo:
    - {{ your namespace }}
```

### 3. Delete the errored Kubernetes job

The job deployed by the postgres-operator and stopped by an error won't be automatically deleted. 
We need to delete it to allow the postgres-operator to launch a job with the updated configuration.

We can't basically manually delete any resources on the Agora Kubernetes cluster, so please post a message to the [`#wcm-org-agora-ama` Slack channel](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7) mentioning `@agora-storage` to let us handle the deletion operation.
Please include the following information in the message for the deletion operation.

- Your namespace the database is running in
- Name of the PostgresCluster resource
- Name of the target job for the deletion

### 4. Check and fix Stanza configuration

The pod-to-pod communication issue might cause pgBackrest Stanza misconfiguration.
We need to check the status in the configuration and fix it if it's necessary.

How to do it is described in the runbook [Stanza recreation](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/postgres-operator/docs/operations/recreate-stanza-3988097919).

## Inquiry

If the above operation doesn't solve the issue or you have some questions related to it, please feel free to ask the Agora storage team via Slack.

Slack channel: [`#wcm-org-agora-ama`](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7)
Agora storage team: `@agora-storage`
