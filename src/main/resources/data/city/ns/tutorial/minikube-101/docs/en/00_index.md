# Introduction

## Overview

This tutorial contains files used for a very simple and barebones deployment of
a service to minikube. The purpose of this tutorial is to present one of the
many 'building blocks' that Agora uses to offer its services to other teams.

### Who this tutorial is for

This tutorial is to be used to help people who are completely new to Kubernetes
and Docker, or who have never really used either in a production environment.
This includes, for example:

* New developers
* Developers new to cloud development
* Project managers who wish to know the basics of Kubernetes

### Files for this tutorial

This tutorial assumes that you are working from the [source directory](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/tutorial/minikube-101/).

This directory contains a set of YAML files that deploy a very simple HTTP page via [Nginx](https://www.nginx.com/).  
Specifically, these are:

* a Namespace definition (`namespace.yaml`),
* a Service definition (`service.yaml`),
* a Deployment definition (`deployment.yaml`).

Feel free to use these files however you wish to get started.

### Pre-requisites for this tutorial

This tutorial assumes that you have set up minikube and a few tools:

* **Minikube**
  * You can find instructions on how to do this on the [minikube site](https://minikube.sigs.k8s.io/docs/start/)
    for Windows, Mac, and Linux systems.
  * Minikube requires a backing driver, and this tutorial is using **Docker**.
    You can find instructions [here](https://minikube.sigs.k8s.io/docs/drivers/docker/).
* **Kubectl**
  * You can find instructions on how to install
    [here](https://minikube.sigs.k8s.io/docs/start/) for Windows, Mac, and Linux.

### Note on Kubernetes terms

This tutorial introduces and uses several Kubernetes terms that are in common
usage in the Agora context, such as _namespace_, _pod_, or _services_. We try
to make it clear when we're using a Kubernetes term.

We will also add links to the relevant part of Kubernetes's documentation, but
these are considered "further reading" if you're interested. Reading the
Kubernetes docs is not required to move on with this tutorial.
