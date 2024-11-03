# Getting a Secure KVS database on the Agora dev cluster

Secure KVS is a KVS database solution with encryption-at-rest managed by the Agora platform.
Secure KVS is constructed with Steelcouch (an encryption proxy) and CouchDB,
and each user can get the encryption feature without any additional code 
because the encryption is transparently applied through Steelcouch.

This document explains how to onboard Secure KVS on the Agora dev cluster.

## Onboarding process

Secure KVS offers a CouchDB database on the shared CouchDB cluster administered by the Agora team.
Each user can use the database without actual deployments of Steelcouch and CouchDB by the user side.

### Prerequisite for the onboarding

Secure KVS applies access control based on a [Kubernetes service account](https://kubernetes.io/docs/tasks/configure-pod-container/configure-service-account/),
a namespaced resource tied to a Kubernetes namespace.
Pods that the registered service account is attached to can access the database without credentials such as a database user name and a password.

Please ensure you have the namespace and the service account before requesting the onboarding.

### Make an onboarding request

Currently, the Agora team offers the database based on a user request.

If you want to get a database, please post a Slack message to the public [Slack channel `#wcm-org-agora-ama`](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7) with the mention `@agora-storage`.

Please include the following information in the Slack message.

*   Kubernetes service account names
*   Namespace
*   Database name

We use the namespace as the prefix for the CouchDB database name to avoid the database name conflict between namespaces.
So, the actual CouchDB database name will be `<namespace>_<database name>`.

