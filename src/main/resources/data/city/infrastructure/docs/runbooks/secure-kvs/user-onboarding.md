# How to proceed with user onboarding for Secure KVS

This runbook explains how to handle the user onboarding process for Secure KVS.

**Note**: In [this ticket](https://jira.tri-ad.tech/browse/CITYPF-470), we're considering developing a Kubernetes operator to enable a service team
to create a database in a self-service manner .
If it becomes available, the process in this runbook might be abolished.

## What the onboarding process does

As the output of the onboarding process, a service team gets a database on the shared CouchDB cluster.

Secure KVS is a shared KVS database service, and it is constructed with
Steelcouch, which is an encryption proxy, and CouchDB, which is an open-source KVS.

A service team needs to share Kubernetes service accounts that will be used by Kubernetes pods accessing the database.
The authentication for the database access is handled based on them,
and the service team doesn't need to pass credentials to access it.

Here is the information that each requester will get after the onboarding process.

*   The endpoint of the database
*   Allocated database name

## Input for the onboarding process

We need to get the following information for the process.

*   Kubernetes service account names
*   Namespace
*   Database name
*   Necessity of encryption

The actual database name will become `<namespace>_<database name>`.

We offer the Steelcouch endpoint to protect stored data with encryption.
But, if a service will store only nonsensitive information
and prefer to use some specific CouchDB features that Steelcouch hasn't supported,
we can offer the CouchDB cluster endpoint.

## Onboarding process

Here is a diagram depicting the overview of what the Helm chart does.
![Overview of what the Helm chart for the onboarding process handles](./skvs_onboarding_process.svg)

The manifests for the onboarding are packaged as a Helm chart,
and we can handle the onboarding by putting a FluxCD HelmRelease manifest into the `infrastructure/k8s/*/secure-kvs/secure-kvs-onboarding` folder.

Here is a sample HelmRelease manifest.

```yaml
---
apiVersion: helm.toolkit.fluxcd.io/v2beta1
kind: HelmRelease
metadata:
  name: skvs-onboarding-demo-secure-kvs-demo
  namespace: flux-system
  labels:
    app.kubernetes.io/part-of: secure-kvs-onboarding
spec:
  interval: 5m
  chart:
    spec:
      chart: secure-kvs-onboarding
      version: "<0.2.0"
      sourceRef:
        kind: HelmRepository
        name: wit-artifactory
        namespace: flux-system
      interval: 1m
  serviceAccountName: helm-controller
  targetNamespace: core
  values:
    namespace: demo
    database: secure-kvs-demo
    serviceAccounts:
      - secure-kvs-demo
    encryption: true
```

What we need to change for each onboarding is the four attributes in `spec.values`. 
We can see the explanations for each attribute [here](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/helm/secure-kvs-onboarding#deploy-configuration).

### Naming conventions

As the naming convention for the name of the manifest, we **SHOULD** use the database name (`<namespace>_<database name>`) for the manifest file name.

And the name of the HelmRelease **SHOULD** follow the following convention to make listing the onboarding deployments easy.

```text
skvs-onboarding-<name>_<database>
```

### Database information

After the onboarding release is successfully deployed, we can get the information for accessing the database from the release note of the Helm chart.

```bash
helm get notes -n flux-system secure-kvs-skvs-onboarding-pds--database
NOTES:
A database was created!

  Database name: pds_database
  Endpoint: http://steelcouch-agora-kvs-secure-kvs-agora-couchdb.secure-kvs.svc:5984
  Service accounts:
    - pds
```

If you handle onboarding process for some service team, 
please share the information with the team after confirming the release has been successfully deployed.

### List onboarding releases

We can list the onboarding releases using the label we put to the HelmRelease, like the following.

```bash
kubectl get helmreleases.helm.toolkit.fluxcd.io -n flux-system --selector app.kubernetes.io/part-of=secure-kvs-onboarding
NAME                             AGE     READY   STATUS
skvs-onboarding-pds--database    3d20h   True    Release reconciliation succeeded
skvs-onboarding-iota--database   60m     True    Release reconciliation succeeded
```

### Update operation

We can update only `spec.values.serviceAccounts` to modify the service accounts that can access the database.

`spec.values.namespace` and `spec.values.database` **MUST NOT** be updated
because the calls to CouchDB API to create a database happens 
only when the Helm chart is installed,
and the update won't prepare a new database.

To get another name database, please delete the old one and create the new one with the desired configuration.
At this moment, we haven't implemented the backup and restore solution,
but we're currently working on it.
After implementing it, we'll be able to update the configuring, keeping stored data.

`spec.values.encryption` **SHOULD NOT** be updated.
We can technically offer both endpoints of the encryption proxy Steelcouch and CouchDB,
but we decided to offer only either one 
to avoid possible confusion caused by storing both encrypted and unencrypted data in a database.
To enforce the intention, we should avoid updating it.

### Delete operation

We can remove a deployment when the corresponding database becomes unnecessary.

The removal of the deployment will delete the corresponding authorization policy.

The database itself won't be deleted even though it becomes unable to access
except for the accesses from the namespace that Secure KVS is running on.
If the deletion of the database is essential, 
we need to manually directly call [the CouchDB API](https://docs.couchdb.org/en/3.2.2-docs/api/database/common.html#delete--db) from one of the CouchDB instance pods.

## Troubleshooting

We can get the status of the deployment from the status of the HelmRelease resource. 

When we list the HelmRelease resources for the onboarding, a failed release shows the latest error message like this.
```bash
kubectl get helmreleases.helm.toolkit.fluxcd.io -n flux-system --selector app.kubernetes.io/part-of=secure-kvs-onboarding
NAME                            AGE   READY   STATUS
skvs-onboarding-pds--database   23h   False   install retries exhausted
```

And we can see more messages from `status` of the HelmRelease.
```bash
kubectl get helmreleases.helm.toolkit.fluxcd.io -n flux-system skvs-onboarding-pds--database -o json | jq '.status'
{
  "conditions": [
    {
      "lastTransitionTime": "2022-09-08T06:29:37Z",
      "message": "install retries exhausted",
      "reason": "InstallFailed",
      "status": "False",
      "type": "Ready"
    },
    {
      "lastTransitionTime": "2022-09-08T06:29:37Z",
      "message": "Helm install failed: failed pre-install: warning: Hook pre-install secure-kvs-onboarding/templates/database-creation-job.yaml failed: namespaces \"core\" not found\n\nLast Helm logs:\n\nStarting delete for \"securekvs-onboarding-pds_databa
se-database-creation\" Job\njobs.batch \"securekvs-onboarding-pds_database-database-creation\" not found\ncreating 1 resource(s)",
      "reason": "InstallFailed",
      "status": "False",
      "type": "Released"
    }
  ],
  "failures": 105,
  "helmChart": "flux-system/flux-system-skvs-onboarding-pds--database",
  "installFailures": 1,
  "lastAttemptedRevision": "0.1.2+b066f00a",
  "lastAttemptedValuesChecksum": "deb5ba25f95fe963ede895da967172e5b5ada98a",
  "lastReleaseRevision": 1,
  "observedGeneration": 1
}
```
