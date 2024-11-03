# Step 1: Deploying a Namespace

## What is a Namespace

A [_namespace_](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/)
in Kubernetes is a way to label and organize your resources. You
can group related resources in the same namespace, and resources in different
namespaces are isolated from each other. It's
similar to how you would organize your code into packages or modules with
names, and group your code files under the package that it should be under.

Namespaces are how Agora separates resources per Team, so it's useful to know
how to use them!

## How to deploy the namespace

From the working directory you can simply run `kubectl create` specifying the
namespace file:

```shell
kubectl create -f namespace.yaml
```

!!! Tip
    You could also use `kubectl apply -f namespace.yaml` to run the same file.
    Why `create` and not `apply`? Create is an _imperative command_ that we
    can use here because we're using a clean minikube system, whereas the use
    of `apply` presumes there is existing resources which could lead to
    potential issues if not used carefully. You're free to use both; you will
    get the same effect, but `create` works under the tutorial's assumptions.

## Verify the Namespace is there

Running another `kubectl` command will help us verify the namespace is there:
```shell
kubectl get namespaces
```

This should produce output like:
```shell
NAME              STATUS   AGE
default           Active   2d3h
kube-node-lease   Active   2d3h
kube-public       Active   2d3h
kube-system       Active   2d3h
landing-page      Active   5s
```

!!! Note
    You might notice you have some namespaces already in your minikube
    environment. Kubernetes has some namespaces for the resources minikube
    generates for you when you first start it up. You don't need to worry
    about these, but its good to know that there are some resources for you
    out of the box when you start Minikube.
