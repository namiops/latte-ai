# Helm chart for creating a database on Secure KVS

This Helm chart handles the following as the user onboarding support for Secure KVS.

1.  Create a database in the CouchDB cluster and deleting the default role for it. If the database is already existed, the deployment is failed and the later steps won't be executed.
2.  Deploy an Istio authorization policy to allow the requester's service accounts to access the database.

# Deploy configuration

What we need to configure for each request for the onboarding is basically the following 4 values.

| Value | Type | Description |
| ----- | ---- | ----------- |
| namespace | string | The namespace that the client for the database is running. |
| database | string | A CouchDB database name. The actual database name will become `<namespace>_<database>`. The database creation is handled at first by being triggered as a Helm `pre-install` hook, and, if the database is already existed, the chart installation is failed and nothing will be deployed. |
| serviceAccounts | list of string | A service account name that the client of the database will use. |
| encryption | boolean | If it's true, an endpoint for Steelcouch will be available. If it's not, an endpoint for CouchDB will be offered. |

Other values are ones for specifying the target Secure KVS deployment, and ones for the configuration of the job to set up the database. You can see them in [values.yaml](../values.yaml).

# Uninstall the chart

Uninstalling the chart doesn't include the deletion of the CouchDB database to avoid unintentionally data loss. If acutually deleting the data is necessary, it should be manually handled.
