# Step 1: Deploying our Application Without Vault

First, to better understand what Vault does for us and how we can make use of
it, we will make a deployment of an application that does **not** use it. We
will explain the sample application and then go over some potential issues with
not using Vault.

## Our Sample Application

Our sample application is a small piece of code that takes in a secret key and
then, ironically, displays it in the log.

```rust
/// This is the entire application
/// The file is read from the provided path, and simply prints out a message
/// After sixty seconds, the application exits
fn main() {
    let mut file = File::open(FILE_PATH).expect("Oops! Couldn't find the file");
    let mut secret_word = String::new();
    file.read_to_string(&mut secret_word)
        .expect("Oops! Something happened reading file");

    println!("Hey there, the word is {}", secret_word);

    let sixty_seconds = Duration::from_secs(60);
    sleep(sixty_seconds);

    println!("Oops! I let the secret word out, better run!")
}
```

The application per the code is reading data from a file path. This file can be
inserted into our deployment in a few ways, but there will be two ways we'll go
over in this tutorial. The first we'll go over is the use of Kubernetes Secrets

## What is a Kubernetes Secret

A [Secret](https://kubernetes.io/docs/concepts/configuration/secret/) is a
resource in Kubernetes that is meant to hold sensitive data such as passwords,
tokens, or keys. The idea behind a Secret is that data that is sensitive cannot
be readily read by any attackers.

Secrets attempt to solve this issue by making a resource that is independent of
the application's deployment, but can be used by deployments that need access
to the sensitive data. To show this off, we'll create a deployment in our
cluster that leverages a Secret.

## Deploying our application

### Setting up the Namespace

First we'll create a namespace for us to deploy our application to. This will
make sure all resources for the tutorial are organized in a logical manner. To
start, we'll move to our project's `kubernetes` folder.

```shell
$ cd kubernetes
```

Next we'll apply the following manifest via `kubectl`

```shell
$ kubectl apply -f _namespace.yaml
namespace/vault-101 created
```

### Setting up the Deployment's Secret

Next, we'll deploy the secret. Let's take a quick look at our `secret.yaml`file

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: my-secret-word
  namespace: vault-101
type: Opaque
data:
  secret-key: YmFuYW5h
```

Here we can see we have one key - `secret-key` - that will be deployed when we
apply this file to the cluster. Let's try that now

```shell
$ kubectl apply -f secret.yaml
secret/my-secret-word created
```

### Deploying the Application

Finally, we'll set up our application's deployment. Let's take a look at the
`secret-deployment.yaml`

```yaml
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
    spec:
      containers:
        - name: vault-101
          image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/tutorials/vault-101:main-2e325fc0-5337
          # Here we can mount our secret as a volume. This will essentially work as mounting a file
          # and a directory to the deployment that the application is allowed to read.
          # Our application is going to read a file from the path /vault/secrets
          # So we'll set our secret up to be read on that path
          volumeMounts:
            - mountPath: "/vault/secrets"
              name: my-secret
              readOnly: true
      volumes:
        # Here we can attach our secret to the deployment
        - name: my-secret
          secret:
            # This is the name we declared in our secret.yaml file
            secretName: my-secret-word
            # Here can tell Kubernetes how we want our secret to be presented
            # In our case, there's a key on our secret, and we would like it to be presented
            # as a file named 'config'. This will allow our application to read it.
            items:
              - key: secret-key
                path: "config"
```

Here, we are attaching the secret as a volume mount, that makes it appear like
a file path. The secret is then mounted like a file, which our application is
expecting, so this will work just fine for our application. The file will be
mounted per our deployment at the path `/vault/secrets/config`, which we have
set in our application.

```rust
const FILE_PATH: &str = "/vault/secrets/config";

/// This is the entire application
/// The file is read from the provided path, and simply prints out a message
/// After sixty seconds, the application exits
fn main() {
    let mut file = File::open(FILE_PATH).expect("Oops! Couldn't find the file");
    //code omitted
}
```

We'll deploy the application now

```shell
$ kubectl apply -f secret-deployment.yaml
deployment.apps/vault-101 created
```

### Verifying our Application Works

To check and see if things work, we'll go to see the logs of our deployment.
First we need the name of our pod, which we can find with `kubectl get all`

```shell
$ kubectl get all -n vault-101
NAME                             READY   STATUS    RESTARTS   AGE
pod/vault-101-7bc5f8b67b-9gtw7   1/1     Running   0          6s   <--- Our Application

NAME                        READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/vault-101   1/1     1            1           6s

NAME                                   DESIRED   CURRENT   READY   AGE
replicaset.apps/vault-101-7bc5f8b67b   1         1         1       6s
```

Once we have the name of our pod we can use `kubectl logs` with `-f` to follow
it. We'll see that the application works: the application prints the secret,
and then, realizing its mistake, exits out after a minute or so.

```shell
$ kubectl logs vault-101-7bc5f8b67b-9gtw7 -n vault-101 -f
Hey there, the word is banana
Oops! I let the secret word out, better run!
```

## Why Secrets Aren't so Secret

So our application works, but there are a few shortcomings with using just
Secrets in our deployments

### The Secret still needs to be deployed somewhere that can be handled by Agora and our infrastructure

The Agora Team works with several tools to handle automatic deployment. To have
a means to make our infrastructure reproducible and have ways to audit and
track changes, we are using GitOps and Infrastructure As Code (IAC) as
policies. This means two things for use of Secrets in our deployments

1) **We can't have Secrets inside our git repository**. This is a security
concern; if the secret is living inside a repository, that is something that
can be traced and found, and potentially exploited.

2) **We can't have Secrets living via manual deployments, or outside visible
places**. Part of GitOps and IAC is that we can see all infrastructure in the
code, so having a Secret that we don't know or cannot see makes it hard to
trace, or to reproduce, or to figure out what its intended use was.

Because of the above reasons, Secrets are untenable for our infrastructure's long-term future.

### Secrets can be found easily

Secrets per their [documentation](https://kubernetes.io/docs/concepts/configuration/secret/)
are stored unencrypted in the API server's underlying data store, which is
`/etcd`. In addition to that, anyone who can create a Pod in a Namespace
can see and read the secret, including any indirect methods like Deployments
(which is what we did). This means there are methods to get the Secret and just
read it.

Secrets in addition are only Base64 encoded, which makes it "essentially"
non-encrypted at all. If one has access to a Secret, they can easily decode any
secret keys inside the Secret.

### Cleaning up our Namespace

For the next step we'll be deploying something different, but for the sake of
making this process easier lets clean up what we did before. We just need to
remove the deployment and the secret for now. 

```shell
$ kubectl delete -f secret-deployment.yaml 
deployment.apps "vault-101" deleted
$ kubectl delete -f secret.yaml 
secret "my-secret-word" deleted
```

## What's Next
In the next step we'll see what we get with Vault, and why we recommend it as
an alternative to Secrets. We'll deploy the same application, but this time,
we'll let Vault handle the process of the secret and how to make it readable
for our application (which will just print it out anyways...blabbermouth).