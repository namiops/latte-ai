# Namespaces 101

This tutorial is meant to convey a basic example of how Namespaces work in a
Kubernetes cluster. This tutorial will try to better explain namespaces, how to
effectively use them, and how they help to organize your kubernetes resources.

## Pre-requisites for This Tutorial

This tutorial uses an instance of Minikube to demonstrate the code.

* You can find installation instructions [here](https://minikube.sigs.k8s.io/docs/start/)

This tutorial also presumes you have an understanding of how to use Kubernetes

* The Agora team has a small tutorial on the basics of Kubernetes and Minikube
  available at [Minikube-101](https://developer.woven-city.toyota/docs/default/component/minikube-tutorial)

## What a Namespace Is

From the [Kubernetes Documentation](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/)

> In Kubernetes, namespaces provides a mechanism for isolating groups of
> resources within a single cluster. Names of resources need to be unique
> within a namespace, but not across namespaces. Namespace-based scoping is
> applicable only for namespaced objects (e.g. Deployments, Services, etc) and
> not for cluster-wide objects (e.g. StorageClass, Nodes, PersistentVolumes,
> etc).

Namespaces are primarily used for environments with multiple users, across
multiple teams and/or projects. They are a way to provide scope for names. In
Kubernetes, resources need to be named uniquely within a namespace, but not
**across** them. This means that, multiple teams could have similarly named
resources across namespaces.

In Agora, namespaces are used primarily to carve out spaces for our service
teams, allowing them to have a space for their applications. This provides each
team space to determine the best way to deploy their service.

## How to Read This Tutorial

This tutorial presumes you are working from the [tutorial source directory](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/tutorial/namespaces-101)
of the tutorial. All commands presumed should be followed as if you are running
from the source root.

```
cd ns/tutorial/namespaces-101
```
