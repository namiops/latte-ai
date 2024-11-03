# Using The Vault Agent

!!! Warning
    **The use of Vault Agent is deprecated**
    This method is primarily for using the Kubernetes Engine.
    This method is only **RECOMMENDED** inside the legacy environment (`dev`)

## Overview

![vault-agent-overview](./assets/vault-agent-overview.jpg)

## Setting Up A Service Account For Vault Communication

Vault requires a Service Account to allow access.
For more information on this refer to the [Vault Documentation](https://developer.hashicorp.com/vault/docs/auth/kubernetes#kubernetes-auth-method)

To set up your Service Account to talk to Vault, add a YAML manifest to your `infrastructure/k8s/<environment>/flux-tenants/<tenant_name>/namespaces/<my_namespace>` directory:

```yaml title="vault-auth.yaml"
apiVersion: v1
kind: ServiceAccount
metadata:
  name: my-service-account
  namespace: my-namespace
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: my-auth-delegator
  namespace: my-namespace
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: system:auth-delegator
subjects:
  - kind: ServiceAccount
    name: my-service-account
    namespace: my-namespace
```

!!! Warning
    Service accounts are scoped to their namespace, if you have multiple services in a namespace, please be aware that **each service can see the same service accounts**.
    This can mean that a policy tied to one service account could be potentially used by another service.

## Setting Up Annotations For The Vault Agent

Vault Agent sidecars should be injected in containers in a way that makes the behavior predictable and reduces potential errors.
The Agora team recommends the following set of annotations.
You can learn more on the use of these annotations in Vault-101

```yaml
# This is our application deployment when we use Vault
# Vault can be automatically hooked into our deployment via the Vault Agent which is in our Agora Clusters
# We'll go over some finer details in this file
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-deployment
  namespace: my-namespace
  labels:
    app: my-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: my-deployment
  template:
    metadata:
      labels:
        app: my-deployment
      # To use Vault in Agora, we ask the Vault Agent to help us with our deployment
      # The Vault Agent can be requested to listen to a given deployment via annotations
      # For more information about all the various annotations you can provide to the Vault Agent
      # please refer here: https://www.vaultproject.io/docs/platform/k8s/injector/annotations
      annotations:
        # The first annotation is simply stating that we wish Vault to inject secrets into our deployment
        vault.hashicorp.com/agent-inject: 'true'
        # This tells our deployment that the Vault Agent needs to be allowed to start up and do its work first
        # This can be necessary when you have multiple containers or sidecars in your deployment. In Agora
        # these sidecars are done for you, automatically. This simply lets Vault start up first.
        vault.hashicorp.com/agent-init-first: 'true'
        # This annotation informs Vault which container or containers to mount the secret volume to
        # By default, Vault will try to mount a volume to all containers in a pod, but this is NOT recommended
        vault.hashicorp.com/agent-inject-containers: "my-container"
        # The Vault Agent sidecar has logs like a lot of other applications, and we can set the log level here
        # For this example we have it set to 'trace' 
        vault.hashicorp.com/log-level: 'trace'
        # Vault allows multi-tenancy in a system; this allows multiple teams to work in the same Vault without needing
        # to have multiple Vault instances. The Agora team has one such namespace, which is what this annotation is declaring
        # This allows the Vault Agent to look in the correct namespace for resources like secrets
        vault.hashicorp.com/namespace: 'my-namespace'
        # Vault requires that a Role is declared for the deployment. The Role is what allows Vault to determine what
        # secrets we are allowed to read. For this tutorial we have a Role set up for us, so we just need to declare it here.
        vault.hashicorp.com/role: 'my-vault-role'
        # Vault allows you to set the Vault Agent's UID. This is set to '1337' which is a reccomended default for 
        # allowing the Agent to connect to Vault.
        vault.hashicorp.com/agent-run-as-user: '1337'
        # Tells Vault Agent the authentication path for the Kuberenetes auth method
        # Set this to the same name as you set up for the auth engine
        vault.hashicorp.com/auth-path: 'auth/my-engine'
        # Vault allows you to inject secrets based on their 'path' which is the API path that you would like to read.
        # For us, our secrets are under the path 'my-kv/my-path' so that is what we declare here
        vault.hashicorp.com/agent-inject-secret-config: 'my-kv/my-path'
        # Vault allows you to format, or template, secrets to match what your code might expect.
        # For example, a Postgres database URL could be formatted and then set inside your application, where then you
        # can simply read the full URL without having to assemble it yourself.
        # For our application's sake we just need the secret to be in the file by itself.
        #
        # Vault uses the [Consul Templating Language](https://github.com/hashicorp/consul-template/blob/v0.28.1/docs/templating-language.md) 
        # which in turn uses the [Go Template Package](https://pkg.go.dev/text/template) package to figure and template data out. 
        # A limitation of this language is that **keys cannot be hypenated** as it violates the template specification. 
        # When you write out your Vault keys it is RECOMMENDED to use snake_case (in this example 'secret_key')
        vault.hashicorp.com/agent-inject-template-config: |-
          {{ with secret "my-kv/my-path" -}}
            {{ .Data.data.secret_key }}
          {{- end }}
    spec:
      serviceAccountName: my-service-account
      containers:
        - name: my-container
          # Fields omitted
```

### Using Vault Agent and IAM-Role for Service Account (IRSA) together

If you configure your service account with the IAM roles annotation, you may face this [bug](https://github.com/hashicorp/vault-k8s/issues/544).
To resolve, you need to add this annotation to your YAML file:

```yaml
vault.hashicorp.com/auth-config-token-path: "var/run/secrets/kubernetes.io/serviceaccount/token"
```

Example:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: my-service-account
  namespace: my-namespace
  annotations:
    eks.amazonaws.com/role-arn: <some-iam-role>

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-deployment
  namespace: my-namespace
  labels:
    app: my-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: my-deployment
  template:
    metadata:
      labels:
        app: my-deployment
      annotations:
        # Fields Omitted
        vault.hashicorp.com/agent-inject-template-config: |-
          {{ with secret "my-kv/my-path" -}}
            {{ .Data.data.secret_key }}
          {{- end }}
        # ADD THIS ANNOTATION
        vault.hashicorp.com/auth-config-token-path: "var/run/secrets/kubernetes.io/serviceaccount/token"
    spec:
      serviceAccountName: my-service-account
      containers:
        - name: my-container
          # Fields omitted
```
