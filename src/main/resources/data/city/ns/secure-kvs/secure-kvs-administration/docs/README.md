# Scripts for Secure KVS administration

This folder includes several small scripts for the Secure KVS administration.

## Database creation ([create\_database.py](../create_database/create_database.py))

This script does the following to support the onboarding of users.

- Create a database following the naming convention of `<namespace>-<database name>`.
- Remove a default role for the created database to disable CouchDB's authentication.

About the authentication, we handle it based on a Kubernetes service account by utilizing Istio's AuthorizationPolicy.

The script is used by the Helm chart for user onboarding as a Kubernetes job triggered by Helm's `pre-install` hook.

Here are the environment variables for the script.

| Environment variable | Notes |
| -------------------- | ----- |
| DATABASE\_NAME | The database name |
| COUCHDB\_ENDPOINT | The endpoint of the CouchDB cluster |
| DB\_USER | The root user name of the CouchDB cluster |
| DB\_PASSWORD | The root password of the CouchDB cluster |
| RUN\_IN\_ISTIO\_SERVICE\_MESH | A flag to control if the script calls the Istio sidecar proxy's API to finish the sidecar after the database creation  |
| ENVOY\_PORT | A port number for the Istio sidecar proxy's API |
