# Onboarding to Preprod

This document explains how to onboard Secure KVS on the Agora preprod cluster.

## Onboarding process

Secure KVS offers a CouchDB database on the shared CouchDB cluster administered by the Agora team.
Each user can use the database without actual deployments of Steelcouch and CouchDB by the user side.

### Prerequisite for the onboarding Namespace and ServiceAccount

Secure KVS applies access control based on a [Kubernetes service account](https://kubernetes.io/docs/tasks/configure-pod-container/configure-service-account/),
a namespaced resource tied to a Kubernetes namespace.
Pods that the registered service account is attached to can access the database without credentials such as a database user name and a password.

Please ensure you have the namespace and the service account before requesting the onboarding.

### Configuring access using the agoractl_securekvs plugin

You can learn more about the plugin on [agoractl securekvs plugin](https://developer.woven-city.toyota/docs/default/Component/agoractl-tutorial/plugins/08_agoractl_securekvs/) page.

Simply run the command below and push the changes. Once that's done open a PR and ask @agora-storage members to review it on the [Agora AMA slack channel](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7).

```
bazel run //ns/agoractl -- securekvs \
  --name {your db name suffix} \
  --namespace {your namespace} \
  --environment dev2 \
  --output {a subfolder in your project folder} \
  --serviceaccount {your service account}
```

A bit more about the available options:

```
bazel run //ns/agoractl -- securekvs --help
...
options:
  -h, --help            show this help message and exit
  -env {lab2,dev2}, --environment {lab2,dev2}
                        the desired environment
  -n NAME, --name NAME  the database name suffix. The final name will be 'namespace_suffix'
  -ns NAMESPACE, --namespace NAMESPACE
                        the namespace of the database
  -o OUTPUT, --output OUTPUT
                        output path to push files, this is from the root of the city repository. For best result use a sub directory eg. '<your-
                        project>/secure-kvs'
  -s SERVICEACCOUNT, --serviceaccount SERVICEACCOUNT
                        the name of the service account to give access to
```

A working example:

```
bazel run //ns/agoractl -- securekvs --name test2 --namespace secure-kvs-test --environment dev2 --output infrastructure/k8s/environments/lab2/clusters/worker1-east/secure-kvs-test/skvs --serviceaccount test2
```

### Example of using Secure KVS

Once the PR is merged you can check if the db has been created by calling a curl command from the pod that uses your service account. Assuming your pod has curl installed.

```
$ curl 'http://steelcouch-agora-kvs-secure-kvs-agora-couchdb.secure-kvs:5984/secure-kvs-test_test2/_all_docs'
{"total_rows":0,"offset":0,"rows":[

]}
```
