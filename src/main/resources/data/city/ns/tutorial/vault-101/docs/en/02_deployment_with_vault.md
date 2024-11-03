# Step 2: Deploying our Application With Vault

In the first step we introduced our application and some potential pitfalls of
using Kubernetes Secrets for sensitive data. In this next step, we're going to
let Vault handle the sensitive data for us, and highlight some ways it helps
us.

## How Vault Works

The following is a very simple diagram of how Vault works with our application.
While this is not the entire process, any extraneous details have been omitted
for the sake of the tutorial.

![simple](./assets/vault-agent-simple.png)

What we do for this setup is that we have our secret stored in Vault. Vault now
is in charge of the secret and who gets to read it. For our application, we'll
supply the right credentials that tell Vault it's allowed to read the secret.
Vault will then put it into our deployment for us, in a way that works
similarly to the Kubernetes Secret.

## Deploying our application with Vault

### Setting up Our Secret Code in Vault

Much like how we set up our Kubernetes Secret, we need to tell Vault that we
have a secret to store. This has been done ahead of time for you by the Agora
Team, for the sake of the tutorial and to keep things simple.

There are slight differences between our Secret and what we stored in Vault,
such as:

* Our secret's name in Vault is not `my-secret-word` but `vault-tutorial`
* Our secret's key is named `secret-key`, but in Vault, its named `secret_key`

### Deploying our Application

Lets examine our `vault-deployment.yaml` file:

```yaml
# This is our application deployment when we use Vault
# Vault can be automatically hooked into our deployment via the Vault Agent which is in our Agora Clusters
# We'll go over some finer details in this file
apiVersion: apps/v1
kind: Deployment
metadata:
  name: vault-101
  namespace: vault-101
  labels:
    app: vault-101
spec:
  replicas: 1
  selector:
    matchLabels:
      app: vault-101
  template:
    metadata:
      labels:
        app: vault-101
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
        # The Vault Agent sidecar has logs like a lot of other applications, and we can set the log level here
        # For this example we have it set to 'trace'
        vault.hashicorp.com/log-level: 'trace'
        # Vault allows multi-tenancy in a system; this allows multiple teams to work in the same Vault without needing
        # to have multiple Vault instances. The Agora team has one such namespace, which is what this annotation is declaring
        # This allows the Vault Agent to look in the correct namespace for resources like secrets
        vault.hashicorp.com/namespace: 'ns_dev/ns_cityos_platform'
        # Vault requires that a Role is declared for the deployment. The Role is what allows Vault to determine what
        # secrets we are allowed to read. For this tutorial we have a Role set up for us, so we just need to declare it here.
        vault.hashicorp.com/role: 'vault-tutorial-dev'
        # Vault allows you to inject secrets based on their 'path' which is the API path that you would like to read.
        # For us, our secrets are under the path 'kv-dev/vault-tutorial' so that is what we declare here
        vault.hashicorp.com/agent-inject-secret-config: 'kv-dev/vault-tutorial'
        # Vault allows you to format, or template, secrets to match what your code might expect.
        # For example, a Postgres database URL could be formatted and then set inside your application, where then you
        # can simply read the full URL without having to assemble it yourself.
        # For our application's sake we just need the secret to be in the file by itself.
        vault.hashicorp.com/agent-inject-template-config: |-
          {{ with secret "kv-dev/vault-tutorial" -}}
            {{ .Data.data.secret_key }}
          {{- end }}
    spec:
      serviceAccountName: vault-tutorial
      containers:
        - name: vault-101
          image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/tutorials/vault-101:main-2e325fc0-5337
```

The `spec` portion of this file is similar to the one we have in our
`secret-deploymnet.yaml` file from Step 1. However, we do not have a volume
mount declared. The reason we don't is that Vault will do this for us. Vault
does this for us by way of the annotations we have added to our YAML.

!!! Note
    **So, where is the secret then?**

    The secret is handled by Vault, and the way Vault injects it for us is by
    making a file that is similar to the volume mount from our Secrets example.
    The difference being is that the secret is stored in a volume mount that
    only is there **at the time of deployment**, and when the deployment is
    removed or deleted, the secret goes away with it.

    As developers, we know where the secret is due to how Vault sets things up
    for us: either we can let Vault set it across a 'default' path - which is
    what our application is using - or, we can tell Vault where to put our
    secret, and how we'd like it to be formatted. There's a lot to unpack
    for just this tutorial, but just know that this stuff is out there for
    us to take advantage of

The information we provide to Vault to let it know we're allowed to read our
`vault-tutorial` secret is the **serviceAccountName**, which we have added
under our `spec`. This will tell Vault to use this account to determine if
we're allowed to read a secret we're requesting access to.

We need to first make sure that we have the account that Vault needs. To do
this, lets deploy our `service-account.yaml` file to the cluster

```shell
$ kubectl apply -f service-account.yaml  
serviceaccount/vault-tutorial created
clusterrolebinding.rbac.authorization.k8s.io/vault-tutorial-auth-delegator created
```

Let's deploy this application with Vault. We'll run the same command we did in Step 1:

```shell
$ kubectl apply -f vault-deployment.yaml 
deployment.apps/vault-101 created
```

Then we can run the same commands to make sure its working the same way

```shell
$ kubectl get all -n vault-101
NAME                             READY   STATUS    RESTARTS   AGE
pod/vault-101-7bc5f8b67b-9gtw7   1/1     Running   0          6s   <--- Our Application

NAME                        READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/vault-101   1/1     1            1           6s

NAME                                   DESIRED   CURRENT   READY   AGE
replicaset.apps/vault-101-7bc5f8b67b   1         1         1       6s

$ kubectl logs vault-101-7bc5f8b67b-9gtw7 -n vault-101 -f
Hey there, the word is banana
Oops! I let the secret word out, better run!
```

## What Vault Does Differently

With this we have successfully deployed our sample, blabbermouth application
twice, once using Kubernetes Secrets and once using Vault. So what exactly did
we gain from letting Vault handle things for us?

### There is no Secret deployed to the cluster

Because Vault handles the secret for us, we don't need to declare a Secret as
part of our deployment. This means that we fall in line with the goals of our
GitOps/Infrastructure As Code (IAC) approach, where we can safely declare all
our infrastructure in our repository.

### The Secret is in a single place that we can control access to

Vault needs to know who is asking for what: when we deployed we presented Vault
a service account token to tell it who we are, and what we're asking access to.
This access control can be extended or revoked as we need to. If we simply were
to remove the token from our `vault-deployment.yaml`, then Vault would say
we're not allowed to read the secret.


### Cleaning up our Namespace

To end this tutorial let's delete everything we created to avoid conflicts for future learners, as currently we are using a shared cluster.

```shell
$ kubectl delete -f vault-deployment.yaml
deployment.apps "vault-101" deleted
$ kubectl delete -f _namespace.yaml
secret "vault-101" deleted
```

## Congratulations

By getting to this point, you have a basic understanding of the following

* What is Vault and how it works to help us secure sensitive data
* How to deploy an application with Vault
* What does Vault do compared to using Kubernetes Secrets

Some next steps would be to try it out for yourself! Please refer to the
[Agora Developer Documentation]() for some best practices and further details
on how to use Vault in your applications. 
